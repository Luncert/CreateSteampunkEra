package com.luncert.steampunkera.content.core.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import javax.annotation.Nullable;

public class DirectionalBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public DirectionalBlock(Properties props) {
    super(props);
  }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }

  @Nullable
  public BlockState getStateForPlacement(BlockItemUseContext ctx) {
    Direction facing = Direction.NORTH;
    if (ctx.getPlayer() != null) {
      facing = ctx.getPlayer().getDirection();
    }
    facing = facing.getOpposite();
    return this.defaultBlockState().setValue(FACING, facing);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
  }
}
