package com.luncert.steampunkera.content.core.depothopper;

import com.luncert.steampunkera.index.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DepotHopperItem extends BlockItem {

  public DepotHopperItem(Block block, Properties props) {
    super(block, props);
  }

  @Nonnull
  public ActionResultType useOn(ItemUseContext context) {
    BlockPos pos = context.getClickedPos();
    World world = context.getLevel();
    BlockState state = world.getBlockState(pos);
    PlayerEntity player = context.getPlayer();
    Direction facing = context.getClickedFace();

    if (player == null || !AllBlocks.DEPOT.has(state) || facing.getAxis().equals(Direction.Axis.Y)) {
      return ActionResultType.FAIL;
    }

    BlockState newState = ModBlocks.DEPOT_HOPPER.getDefaultState()
        .setValue(BlockStateProperties.FACING, facing);
    ActionResultType resultType = super.useOn(context);
    if (resultType.consumesAction()) {
      pos = context.getClickedPos().relative(facing);
      world.setBlockAndUpdate(pos, newState);
      if (!player.isCreative()) {
        context.getItemInHand().shrink(1);
      }
    }

    return resultType;
  }
}
