package com.luncert.steampunkera.content.robot;

import com.luncert.steampunkera.index.AllBlocks;
import com.luncert.steampunkera.index.AllItems;
import com.luncert.steampunkera.index.AllTileEntities;
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
public class RobotChargeStationBlock extends Block implements ITE<RobotContainerTileEntity> {

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
    if (item == AllItems.ROBOT_CONTROLLER.get()
      || item == AllBlocks.ROBOT_CHARGE_STATION.get().asItem()) {
      withTileEntityDo(world, pos, te -> {

        // ((RobotControllerItem)heldItem.getItem()).bindComputerContainer(te);
        // player.displayClientMessage(
        //     ITextComponent.nullToEmpty("Robot connected"), true);
      });
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
    return AllTileEntities.ROBOT_CONTAINER.create();
  }

  @Override
  public Class<RobotContainerTileEntity> getTileEntityClass() {
    return RobotContainerTileEntity.class;
  }
}
