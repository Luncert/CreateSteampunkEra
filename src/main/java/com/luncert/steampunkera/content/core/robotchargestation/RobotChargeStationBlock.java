package com.luncert.steampunkera.content.core.robotchargestation;

import com.luncert.steampunkera.index.ModTileEntities;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

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
    if (world.isClientSide) {
      return ActionResultType.SUCCESS;
    }

    withTileEntityDo(world, pos, te -> NetworkHooks.openGui((ServerPlayerEntity) player, te, te::sendToContainer));
    return ActionResultType.SUCCESS;
  }

  @Override
  public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.hasTileEntity() || state.getBlock() == newState.getBlock())
      return;

    withTileEntityDo(worldIn, pos, te -> ItemHelper.dropContents(worldIn, pos, te.inventory));
    worldIn.removeBlockEntity(pos);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
    return ModTileEntities.ROBOT_CHARGE_STATION.create();
  }

  @Override
  public Class<RobotChargeStationTileEntity> getTileEntityClass() {
    return RobotChargeStationTileEntity.class;
  }
}
