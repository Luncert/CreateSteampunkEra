package com.luncert.steampunkera.content.robot;

import com.luncert.steampunkera.index.AllItems;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotBlock extends RobotBlockBase {

    public RobotBlock(Properties props) {
        super(props);
    }

    // public static BlockState createAnchor(BlockState state) {
    //     return AllBlocks.MINECART_ANCHOR.getDefaultState();
    // }

    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float speed) {
        // reduce falling damage
        // super.fallOn(world, pos, entity, speed * 0.5F);
    }

    @Override
    public void updateEntityAfterFallOn(IBlockReader reader, Entity entity) {
        BlockPos pos = entity.blockPosition().below();
        // if entity is mob then robot can pick it up
        if (!(entity instanceof PlayerEntity) && entity instanceof LivingEntity) {
            BlockState state = reader.getBlockState(pos);
            if (state.getBlock() == this) {
                sitDown(entity.level, pos, state, entity);
            }
        } else {
            super.updateEntityAfterFallOn(reader, entity);
        }
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state,
                                World world,
                                @Nonnull BlockPos pos,
                                PlayerEntity player,
                                @Nonnull Hand hand,
                                @Nonnull BlockRayTraceResult traceResult) {
        if (player.isShiftKeyDown()) {
            return ActionResultType.PASS;
        }

        // return super.use(state, world, pos, player, hand, traceResult);

        // change robot color on use dye
        // ItemStack heldItem = player.getItemInHand(hand);
        // DyeColor color = DyeColor.getColor(heldItem);
        // if (color != null && color != this.color) {
        //     if (world.isClientSide)
        //         return ActionResultType.SUCCESS;
        //     BlockState newState = BlockHelper.copyProperties(state, AllBlocks.SEATS.get(color).getDefaultState());
        //     world.setBlockAndUpdate(pos, newState);
        //     return ActionResultType.sidedSuccess(world.isClientSide);
        // }

        // List<RobotEntity> robots = world.getEntitiesOfClass(RobotEntity.class, new AxisAlignedBB(pos));
        // if (!robots.isEmpty()) {
        //     RobotEntity robot = robots.get(0);
        //     List<Entity> passengers = robot.getPassengers();
        //     if (passengers.isEmpty() || !(passengers.get(0) instanceof PlayerEntity)) {
        //         // robot has no passenger or the passenger is not a player
        //         if (!world.isClientSide) {
        //             robot.ejectPassengers();
        //             player.startRiding(robot);
        //         }
        //         return ActionResultType.SUCCESS;
        //     } else {
        //         return ActionResultType.PASS;
        //     }
        // }

        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        // create robot entity and pick entity up
        sitDown(world, pos, state, player);
        return ActionResultType.SUCCESS;
    }

    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos,
                                          @Nullable MobEntity entity) {
        return PathNodeType.RAIL;
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
                               ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_,
                                        ISelectionContext p_220071_4_) {
        return SHAPE;
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader reader, BlockPos pos, PathType type) {
        return false;
    }

    public static void sitDown(World world, BlockPos pos, BlockState blockState, Entity rider) {
        if (world.isClientSide) {
            return;
        }

        // create new robot entity
        RobotEntity robot = new RobotEntity(world, pos, blockState);
        world.addFreshEntity(robot);
        rider.startRiding(robot, true);
    }
}
