package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.index.ModBlocks;
import com.luncert.steampunkera.index.ModItems;
import com.luncert.steampunkera.index.ModTileEntities;
import com.simibubi.create.foundation.block.ITE;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotChargeStationBlock extends Block implements ITE<RobotChargeStationTileEntity> {

  public RobotChargeStationBlock(Properties props) {
    super(props);
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

    ItemStack heldItem = player.getItemInHand(hand);
    Item item = heldItem.getItem();
    if (item == ModItems.ROBOT_CONTROLLER.get()) {
      withTileEntityDo(world, pos, te -> te.setRobotController(heldItem));
      return ActionResultType.CONSUME;
    }

    return ActionResultType.PASS;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
    return ModTileEntities.ROBOT_CONTAINER.create();
  }

  @Override
  public Class<RobotChargeStationTileEntity> getTileEntityClass() {
    return RobotChargeStationTileEntity.class;
  }
}
