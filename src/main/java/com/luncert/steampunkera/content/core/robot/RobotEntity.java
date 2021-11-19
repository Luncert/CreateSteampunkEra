package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.common.SimpleDirection;
import com.luncert.steampunkera.content.core.robot.cc.*;
import com.luncert.steampunkera.content.net.CCEPacketHandler;
import com.luncert.steampunkera.content.net.CRobotPacket;
import com.luncert.steampunkera.content.util.Common;
import com.luncert.steampunkera.index.ModBlocks;
import com.luncert.steampunkera.index.ModEntityTypes;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

import static com.luncert.steampunkera.content.core.robot.RobotMovement.MOVEMENT_SERIALIZER;

/*
 TODO:
   - 允许窒息伤害
   不能被活塞推动，也不能被机械动力变成移动结构
   实现robot收纳盒，用来拾起robot
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotEntity extends ComputerEntityBase implements IEntityAdditionalSpawnData, INamedContainerProvider {

    private static final double MIN_MOVE_LENGTH = 1.0E-7D;

    private static final DataParameter<Integer> SPEED =
        EntityDataManager.defineId(RobotEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> CLOCKWISE_ROTATION =
        EntityDataManager.defineId(RobotEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> WAITING_Y_ROT =
        EntityDataManager.defineId(RobotEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Optional<RobotMovement>> WAITING_MOVEMENT =
        EntityDataManager.defineId(RobotEntity.class, MOVEMENT_SERIALIZER);

    private RobotBrain brain;

    private BlockState blockState = ModBlocks.ROBOT.get().defaultBlockState();
    private float deltaRotation;
    public CompoundNBT blockData;

    // control
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    // for client
    public RobotEntity(EntityType<?> entity, World world) {
        super(entity, world, ComputerData.of(ComputerFamily.NORMAL));
        init(null);
    }

    // for server
    public RobotEntity(World world, BlockPos pos, BlockState blockState, RobotBrain brain, ComputerData data) {
        super(ModEntityTypes.ROBOT.get(), world, data);
        init(brain);

        this.data = data;

        this.blockState = blockState;
        // following data will be synced automatically
        setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
        setDeltaMovement(Vector3d.ZERO);
    }

    private void init(@Nullable RobotBrain brain) {
        this.blocksBuilding = true; // not allow building at entity's position
        this.setInvulnerable(true); // cannot be hurt
        if (brain == null) {
            this.brain = new RobotBrain(this);
        } else {
            brain.setOwner(this);
            this.brain = brain;
        }
    }

    // computer

    @Override
    protected ServerComputer createComputer(int instanceID, int computerID) {
        ServerComputer computer = new ServerComputer(this.level, computerID, data.label, instanceID, getFamily(),
            ComputerCraft.computerTermWidth, ComputerCraft.computerTermHeight);
        computer.setPosition(blockPosition());
        computer.addAPI(new RobotAPI(computer.getAPIEnvironment(), getAccess()));
        brain.setupComputer(computer);
        return computer;
    }

    public IRobotAccess getAccess() {
        return brain;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public World getLevel() {
        return level;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPosition();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return isUsable(player, false);
    }

    @Override
    public boolean isAssembled() {
        // always true for entity
        return true;
    }

    @Override
    public void assemble(boolean assembleStructure) {
        // ignore
    }

    @Override
    public void dissemble() {
        tryDissemble(ModBlocks.ROBOT.get(), getBlockPos());
    }

    @Override
    public boolean isMoving() {
        Optional<RobotMovement> opt = getWaitingMovement();
        if (opt.isPresent()) {
            RobotMovement movement = opt.get();
            double v = position().get(movement.axis);
            if (v != movement.expectedPos) {
                double absDist = Math.abs(movement.expectedPos - v);
                if (absDist != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void forward(int n) {
        SimpleDirection direction = getSimpleDirection();
        Direction.Axis axis = direction.getAxis();
        int posDelta = direction.isPositive() ? n : -n;
        setWaitingMovement(new RobotMovement(axis, direction.isPositive(),
            blockPosition().get(axis) + .5f + posDelta));
    }

    @Override
    public boolean isRotating() {
        return yRot != getWaitingYRot();
    }

    @Override
    public void rotate(int degree) {
        float waitingYRot = wrapDegrees(yRot + degree);
        setWaitingYRot(waitingYRot);
        if (degree > 0) {
            if (waitingYRot < yRot) {
                yRot = -180;
            }
        } else {
            if (waitingYRot > yRot) {
                yRot = 180;
            }
        }
    }

    // entity

    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double lerpX, double lerpY, double lerpZ, float lerpYRot, float lerpXRot,
                       int p_180426_9_, boolean p_180426_10_) {
        this.lerpX = lerpX;
        this.lerpY = lerpY;
        this.lerpZ = lerpZ;
        this.lerpYRot = lerpYRot;
        this.lerpXRot = lerpXRot;
        this.lerpSteps = 10;
    }

    @OnlyIn(Dist.CLIENT)
    public void setInput(MovementInput input) {
        this.inputLeft = input.left;
        this.inputRight = input.right;
        this.inputUp = input.up;
        this.inputDown = input.down;
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity robot, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !robot.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.remove();
            return;
        }

        super.tick();
        brain.update();
        tickLerp();

        // if server has passenger, this method will return false
        // when passenger stops riding, it will return true
        // for client side, the return value is in opposite of server side
        // -> if robot is controlled by player, all update will be processed by client
        // -> else server will do that
        // rotation and position update will be synced between server and client
        if (isControlledByLocalInstance()) {
            boolean acceptControl = !tryToRotate() && !tryToMove();

            if (level.isClientSide) {
                if (acceptControl && getSpeed() > 0) {
                    controlRobot();
                    // sync data to server
                    CCEPacketHandler.INSTANCE
                        .sendToServer(new CRobotPacket(getWaitingYRot(), getWaitingMovement().orElse(null)));
                }
            } else {
                // remove entity and drop item if entity is outside of the world
                // if (entityPos.getY() < 1 || entityPos.getY() > 256) {
                //   tryDropItemOf(robotBlock);
                //   this.remove();
                //   return;
                // }

                // tryDissemble(robotBlock, entityPos);
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vector3d.ZERO);
        }

        tickPush();

        tickCollide();
    }

    private void tickLerp() {
        // isControlledByLocalInstance always return true in server side
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
            return;
        }

        // client only
        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYRot - (double)this.yRot);
            this.yRot = (float)((double)this.yRot + d3 / (double)this.lerpSteps);
            this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.yRot, this.xRot);
        }
    }

    private void tickPush() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F),
            EntityPredicates.pushableBy(this));
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                push(entity);
            }
        }
    }

    private void tickCollide() {
        if (horizontalCollision) {
            getWaitingMovement().ifPresent(movement -> {
                SimpleDirection direction = getSimpleDirection();
                BlockPos targetPos = blockPosition().relative(direction.getAxis(), direction.getDirectionFactor());
                double dist = targetPos.get(direction.getAxis()) - position().get(direction.getAxis());
                if (!isFree(level.getBlockState(targetPos)) && dist < MIN_MOVE_LENGTH) {
                    Vector3d pos = Common.set(position(), movement.axis, movement.expectedPos);
                    setPos(pos.x, pos.y, pos.z);
                    setWaitingMovement(null);
                }
            });
        }
    }

    @Override
    public void push(Entity entity) {
        if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    // not allow robot to ride other entity
    @Override
    public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
        return false;
    }

    private boolean tryToRotate() {
        if (yRot != getWaitingYRot()) {
            updateYRot();
            return true;
        }

        deltaRotation = 0;
        return false;
    }

    private boolean tryToMove() {
        Optional<RobotMovement> opt = getWaitingMovement();
        if (opt.isPresent()) {
            RobotMovement movement = opt.get();
            double v = position().get(movement.axis);
            if (v != movement.expectedPos) {
                double absDist = Math.abs(movement.expectedPos - v);
                if (absDist != 0 && updateDeltaMovement(absDist)) {
                    return true;
                }
            }
            setWaitingMovement(null);
        }

        setDeltaMovement(Vector3d.ZERO);
        return false;
    }

    private void controlRobot() {
        if (inputRight != inputLeft) {
            updateYRot(inputRight ? 90 : -90);
            return;
        }

        if (inputUp != inputDown) {
            SimpleDirection direction = getSimpleDirection();

            // refuse moving if block or robot entity detected
            BlockPos targetPos = blockPosition().relative(direction.getAxis(),
                direction.getDirectionFactor() * (inputUp ? 1 : -1));
            List<RobotEntity> robots = level.getEntitiesOfClass(RobotEntity.class, new AxisAlignedBB(targetPos));

            if (isFree(level.getBlockState(targetPos)) && robots.isEmpty()) {
                Direction.Axis axis = direction.getAxis();
                boolean positive = direction.isPositive() == inputUp;
                int posDelta = positive ? 1 : -1;

                setWaitingMovement(new RobotMovement(axis, positive, blockPosition().get(axis) + .5f + posDelta));
                updateDeltaMovement(1);
                // Paddle?
            }
        }
    }

    private boolean isFree(BlockState blockState) {
        Material material = blockState.getMaterial();
        return blockState.isAir() || blockState.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    public SimpleDirection getSimpleDirection() {
        // yRot = [-180, 180]
        return SimpleDirection.values()[((int) yRot / 90 + 2) % 4];
    }

    private boolean updateDeltaMovement(double absDistance) {
        return getWaitingMovement().map(movement -> {
            if (absDistance < MIN_MOVE_LENGTH) {
                Vector3d pos = Common.set(position(), movement.axis, movement.expectedPos);
                setPos(pos.x, pos.y, pos.z);
                return false;
            }

            double x = 0, z = 0;

            double speed = Math.min(getMovementSpeed(), absDistance);
            if (!movement.positive) {
                speed = -speed;
            }

            // movement over y axis is not supported for now
            if (Direction.Axis.Z.equals(movement.axis)) {
                z = speed;
            } else {
                x = speed;
            }

            setDeltaMovement(x, 0, z);
            return true;
        }).orElse(false);
    }

    private void updateYRot() {
        if (getWaitingYRot() > yRot) {
            incYRot();
        } else {
            decYRot();
        }
    }

    private void updateYRot(float deltaYRot) {
        float waitingYRot = wrapDegrees(yRot + deltaYRot);
        setWaitingYRot(waitingYRot);
        if (deltaYRot > 0) {
            if (waitingYRot < yRot) {
                yRot = -180;
            }
            incYRot();
        } else {
            if (waitingYRot > yRot) {
                yRot = 180;
            }
            decYRot();
        }
    }

    private float wrapDegrees(float d) {
        d %= 360.0f;

        if (d > 180) {
            d -= 360f;
        } else if (d < -180) {
            d += 360f;
        }

        return d;
    }

    private void incYRot() {
        float rot = Math.min(yRot + getRotationSpeed(), getWaitingYRot());
        deltaRotation = rot - yRot;
        yRot = rot;
    }

    private void decYRot() {
        float rot = Math.max(yRot - getRotationSpeed(), getWaitingYRot());
        deltaRotation = rot - yRot;
        yRot = rot;
    }

    private void tryDropItemOf(Block robotBlock) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(robotBlock);
        }
    }

    private void tryDissemble(Block robotBlock, BlockPos entityPos) {
        remove();

        // get the current block where entity is
        BlockState target = level.getBlockState(entityPos);
        boolean canBeReplaced = target.canBeReplaced(
            new DirectionalPlaceContext(level, entityPos, net.minecraft.util.Direction.DOWN, ItemStack.EMPTY, net.minecraft.util.Direction.UP));
        boolean canSurvival = blockState.canSurvive(level, entityPos);

        if (canBeReplaced && canSurvival) {
            if (level.setBlock(entityPos, blockState, 3)) {
                if (blockData != null && blockState.hasTileEntity()) {
                    // create tile entity for block
                    RobotTileEntity placedTileEntity = (RobotTileEntity) level.getBlockEntity(entityPos);
                    if (placedTileEntity != null) {
                        placedTileEntity.setComputerData(data);
                        placedTileEntity.setRobotBrain(brain);

                        CompoundNBT nbt = placedTileEntity.save(new CompoundNBT());

                        // copy entity's block data to tile entity
                        for(String s : blockData.getAllKeys()) {
                            INBT inbt = blockData.get(s);
                            if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                nbt.put(s, inbt.copy());
                            }
                        }

                        placedTileEntity.load(blockState, nbt);
                        placedTileEntity.setChanged();
                    }
                }
            } else {
                tryDropItemOf(robotBlock);
            }
        }
    }

    private float getRotationSpeed() {
        // 18 = 5 tick / 90 angle
        return 18 * getLinearSpeed();
    }

    private float getMovementSpeed() {
        return 1.5f * getLinearSpeed();
    }

    private float getLinearSpeed() {
        return getSpeed() / 512f;
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (player.isSecondaryUseActive()) {
            // shift key is pressed
            return ActionResultType.PASS;
        } else {
            // open terminal
            if (!level.isClientSide && isUsable(player, false)) {
                ServerComputer computer = createServerComputer();
                computer.turnOn();
                (new ComputerContainerData(computer)).open(player, this);
            }

            return ActionResultType.SUCCESS;
        }
    }

    @Override
    protected boolean canRide(Entity entity) {
        return !(entity instanceof FakePlayer);
    }

    /**
     * Update rider position in ride tick.
     */
    @Override
    public void positionRider(Entity rider) {
        if (this.hasPassenger(rider)) {
            double d0 = this.getY() + this.getPassengersRidingOffset() + rider.getMyRidingOffset();
            rider.setPos(this.getX(), d0, this.getZ());

            // let rider rotate with robot
            // rider.yRot += this.deltaRotation;
            // rider.setYHeadRot(rider.getYHeadRot() + this.deltaRotation);
            // clampRotation(rider);

            if (rider instanceof ClientPlayerEntity) {
                setInput(((ClientPlayerEntity) rider).input);
            }

            // code for more than one passenger, let second passenger rotate 90
            // if (rider instanceof AnimalEntity && this.getPassengers().size() > 1) {
            //     int j = rider.getId() % 2 == 0 ? 90 : 270;
            //     rider.setYBodyRot(((AnimalEntity) rider).yBodyRot + (float)j);
            //     rider.setYHeadRot(rider.getYHeadRot() + (float)j);
            // }
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.8D;
    }

    @OnlyIn(Dist.CLIENT)
    public void onPassengerTurned(Entity p_184190_1_) {
        // this.clampRotation(p_184190_1_);
    }

    protected void clampRotation(Entity rider) {
        rider.setYBodyRot(this.yRot);
        // f = rider's head rotation offset related to robot
        // if f is out of [-105, 105], we should correct rider's rotation
        float f = MathHelper.wrapDegrees(rider.yRot - this.yRot);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        float correction = f1 - f;
        rider.yRotO += correction;
        rider.yRot += correction;
        rider.setYHeadRot(rider.yRot);
    }

    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    // Forge: Fix MC-119811 by instantly completing lerp on board
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }

        passenger.yRotO = yRot;
        passenger.yRot = yRot;
        passenger.setYHeadRot(yRot);
    }

    @Override
    public Vector3d getDismountLocationForPassenger(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            Vector3d pos = position();
            return pos.add(0f, 1f, 0f);
        }

        Vector3d vector3d = getCollisionHorizontalEscapeVector(
            this.getBbWidth() * MathHelper.SQRT_OF_TWO,
            entity.getBbWidth(), this.yRot);
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getZ() + vector3d.z;
        BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1);
        BlockPos blockpos1 = blockpos.below();
        if (!this.level.isWaterAt(blockpos1)) {
            double d2 = (double)blockpos.getY() + this.level.getBlockFloorHeight(blockpos);
            double d3 = (double)blockpos.getY() + this.level.getBlockFloorHeight(blockpos1);

            for(Pose pose : entity.getDismountPoses()) {
                Vector3d vector3d1 = TransportationHelper.findDismountLocation(this.level, d0, d2, d1, entity, pose);
                if (vector3d1 != null) {
                    entity.setPose(pose);
                    return vector3d1;
                }

                Vector3d vector3d2 = TransportationHelper.findDismountLocation(this.level, d0, d3, d1, entity, pose);
                if (vector3d2 != null) {
                    entity.setPose(pose);
                    return vector3d2;
                }
            }
        }

        return super.getDismountLocationForPassenger(entity);
    }

    @Override
    public boolean isPickable() {
        // impact whether player can re-ride robot
        return !removed;
    }

    @Override
    protected void defineSynchedData() {
        entityData.clearDirty();
        entityData.define(SPEED, 50);
        entityData.define(CLOCKWISE_ROTATION, true);
        entityData.define(WAITING_Y_ROT, 0f);
        entityData.define(WAITING_MOVEMENT, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> param) {
        super.onSyncedDataUpdated(param);
        // LOGGER.info("{} {}", level.isClientSide ? "C" : "S", param.getSerializer() == DataSerializers.FLOAT);
    }

    public void setSpeed(int speed) {
        if (speed < 0) {
            entityData.set(CLOCKWISE_ROTATION, false);
        } else {
            entityData.set(CLOCKWISE_ROTATION, true);
        }
        entityData.set(SPEED, MathHelper.clamp(Math.abs(speed), 0, 255));
    }

    private int getSpeed() {
        return entityData.get(SPEED);
    }

    public void setWaitingYRot(float v) {
        entityData.set(WAITING_Y_ROT, v);
    }

    private float getWaitingYRot() {
        return entityData.get(WAITING_Y_ROT);
    }

    public void setWaitingMovement(@Nullable RobotMovement movement) {
        entityData.set(WAITING_MOVEMENT, Optional.ofNullable(movement));
    }

    private Optional<RobotMovement> getWaitingMovement() {
        return entityData.get(WAITING_MOVEMENT);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.isEmpty())
            return;

        entityData.set(SPEED, compound.getInt("speed"));
        entityData.set(CLOCKWISE_ROTATION, compound.getBoolean("clockwiseRotation"));
        setWaitingYRot(compound.getFloat("waitingYRot"));
        if (compound.getBoolean("hasWaitingMovement")) {
            compound = compound.getCompound("waitingMovement");
            setWaitingMovement(new RobotMovement(Direction.Axis.values()[compound.getInt("axis")],
                compound.getBoolean("positive"), compound.getFloat("expectedPos")));
        }

        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.putInt("speed", getSpeed());
        compound.putBoolean("clockwiseRotation", entityData.get(CLOCKWISE_ROTATION));
        compound.putFloat("waitingYRot", getWaitingYRot());
        Optional<RobotMovement> opt = getWaitingMovement();
        compound.putBoolean("hasWaitingMovement", opt.isPresent());
        opt.ifPresent(movement -> {
            CompoundNBT n = new CompoundNBT();
            n.putInt("axis", movement.axis.ordinal());
            n.putBoolean("positive", movement.positive);
            n.putFloat("expectedPos", movement.expectedPos);
            compound.put("waitingMovement", n);
        });

        super.addAdditionalSaveData(compound);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {

    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {

    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return RobotContainer.create(id, brain);
    }
}
