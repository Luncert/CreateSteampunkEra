package com.luncert.steampunkera.content.core.robot;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotBlock extends RobotBlockBase {

    public RobotBlock(Properties props) {
        super(props);
    }

    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float speed) {
        // reduce falling damage
        super.fallOn(world, pos, entity, speed * 0.5F);
    }

    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos,
                                          @Nullable MobEntity entity) {
        return PathNodeType.RAIL;
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader reader, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public void setPlacedBy(World world,
                            BlockPos pos,
                            BlockState state,
                            LivingEntity player,
                            ItemStack stack) {
        super.setPlacedBy(world, pos, state, player, stack);

        withTileEntityDo(world, pos, te -> {
            if (!world.isClientSide) {
                if (player instanceof PlayerEntity) {
                    te.setOwningPlayer(((PlayerEntity) player).getGameProfile());
                }
            }
        });
    }
}