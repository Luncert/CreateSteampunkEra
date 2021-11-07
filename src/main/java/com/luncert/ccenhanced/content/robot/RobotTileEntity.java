package com.luncert.ccenhanced.content.robot;

import com.luncert.ccenhanced.index.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.components.structureMovement.*;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.INamedIconOptions;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class RobotTileEntity extends SmartTileEntity implements ITickableTileEntity, IControlContraption {

    public float speed = 1.0f;

    public boolean running;
    public boolean assembleNextTick;
    protected MovementMode movementMode;
    protected boolean waitingForSpeedChange;
    protected AssemblyException lastException;

    private AbstractContraptionEntity movedContraption;

    // Custom position sync
    protected float clientOffsetDiff;

    public RobotTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        setLazyTickRate(3);
        movementMode = MovementMode.MOVE_PLACE;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> list) {
        // 滚动选择运动模式
    }

    // @Override
    public void _tick() {
        super.tick();


        if (movedContraption != null && !movedContraption.isAlive()) {
            movedContraption = null;
        }


        if (level.isClientSide) {
            clientOffsetDiff *= .75f;
        }

        if (waitingForSpeedChange && movedContraption != null) {
            if (level.isClientSide) {
                float syncSpeed = clientOffsetDiff / 2f;
                movedContraption.setContraptionMotion(toMotionVector(syncSpeed));
                return;
            }
            movedContraption.setContraptionMotion(Vector3d.ZERO);
            return;
        }

        if (!level.isClientSide && assembleNextTick) {
            assembleNextTick = false;
            if (running) {
                if (getSpeed() == 0)
                    tryDisassemble();
                else
                    sendData();
                return;
            } else {
                if (getSpeed() != 0)
                    // try {
                    //     assemble();
                    //     lastException = null;
                    // } catch (AssemblyException e) {
                    //     lastException = e;
                    // }
                sendData();
            }
            return;
        }

        if (!running) {
            return;
        }

        boolean contraptionPresent = movedContraption != null;
        if (contraptionPresent) {
            applyContraptionMotion();
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (movedContraption != null && !level.isClientSide) {
            sendData();
        }
    }

    protected void tryDisassemble() {
        // 根据运动模式决定是disassemble还是等待update
        this.disassemble();
    }

    public void assemble(World world, BlockPos pos) throws AssemblyException {
        // check block type
        if (!(level.getBlockState(worldPosition).getBlock() instanceof RobotBlock))
            return;

        // Collect Construct
        RobotContraption contraption = new RobotContraption();
        try {
            if (!contraption.assemble(world, pos))
                return;

            lastException = null;
            sendData();
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return;
        }

        Direction direction = getBlockState().getValue(BlockStateProperties.FACING);
        if (ContraptionCollider.isCollidingWithWorld(level, contraption, contraption.anchor, direction)) {
            return;
        }

        // run
        running = true;
        sendData();
        clientOffsetDiff = 0;

        OrientedContraptionEntity entity = OrientedContraptionEntity.create(world, contraption, direction);
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
        world.addFreshEntity(entity);
        // entity.startRiding(robot);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);
    }

    public void disassemble() {
        if (!running && movedContraption == null) {
            return;
        }

        if (movedContraption != null) {
            applyContraptionPosition();
            movedContraption.disassemble();
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.playOnServer(level, worldPosition);
        }
        running = false;
        movedContraption = null;
        sendData();

        if (remove) {
            AllBlocks.ROBOT.get().playerWillDestroy(
                    level, worldPosition, getBlockState(), null);
        }
    }

    protected void applyContraptionPosition() {
        if (movedContraption == null)
            return;
        // Vector3d vec = toPosition(offset);
        // movedContraption.setPos(vec.x, vec.y, vec.z);
        if (getSpeed() == 0 || waitingForSpeedChange) {
            movedContraption.setContraptionMotion(Vector3d.ZERO);
        }
    }

    private Vector3d toMotionVector(float speed) {
        Direction direction = getBlockState().getValue(BlockStateProperties.FACING);
        return Vector3d.atLowerCornerOf(direction.getNormal()).scale(speed);
    }

    @Override
    public void onStall() {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void collided() {
        if (!running && getMovementSpeed() > 0) {
            assembleNextTick = true;
        }
    }

    public float getMovementSpeed() {
        float movementSpeed = MathHelper.clamp(getSpeed(), -.49f, .49f) + clientOffsetDiff / 2f;
        if (level.isClientSide) {
            movementSpeed *= ServerSpeedProvider.get();
        }
        return movementSpeed;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        this.movedContraption = contraption;
        if (!level.isClientSide) {
            this.running = true;
            sendData();
        }
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return movedContraption == contraption;
    }

    @Override
    public BlockPos getBlockPosition() {
        return worldPosition;
    }

    protected void applyContraptionMotion() {
        if (movedContraption == null) {
            return;
        }

        // stop contraption
        if (movedContraption.isStalled()) {
            movedContraption.setContraptionMotion(Vector3d.ZERO);
            return;
        }

        movedContraption.setContraptionMotion(getMotionVector());
    }

    public Vector3d getMotionVector() {
        return toMotionVector(getMovementSpeed());
    }

    public enum RobotMovementMode implements INamedIconOptions {

        ROTATE(AllIcons.I_CART_ROTATE),
        ROTATE_PAUSED(AllIcons.I_CART_ROTATE_PAUSED),
        ROTATION_LOCKED(AllIcons.I_CART_ROTATE_LOCKED),

        ;

        private String translationKey;
        private AllIcons icon;

        RobotMovementMode(AllIcons icon) {
            this.icon = icon;
            translationKey = "contraptions.cart_movement_mode." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }
}
