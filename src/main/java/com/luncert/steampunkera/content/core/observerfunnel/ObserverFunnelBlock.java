package com.luncert.steampunkera.content.core.observerfunnel;

import com.luncert.steampunkera.index.ModBlocks;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.FunnelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class ObserverFunnelBlock extends FunnelBlock {

  public ObserverFunnelBlock(Properties props) {
    super(props);
  }

  @Override
  public BlockState getEquivalentBeltFunnel(IBlockReader iBlockReader, BlockPos pos, BlockState state) {
    Direction facing = getFacing(state);
    return null;
    // return ModBlocks.BRASS_BELT_FUNNEL.getDefaultState()
    //     .setValue(BeltFunnelBlock.HORIZONTAL_FACING, facing)
    //     .setValue(POWERED, state.getValue(POWERED));
  }
}
