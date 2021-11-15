package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.core.base.DirectionalBlock;
import com.luncert.steampunkera.index.ModTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RobotBlockBase extends DirectionalBlock implements ITE<RobotTileEntity> {

  public RobotBlockBase(Properties props) {
    super(props);
  }

  @Override
  public void onRemove(@Nonnull BlockState block, @Nonnull World world, @Nonnull BlockPos pos, BlockState replace, boolean bool) {
    if (block.getBlock() != replace.getBlock()) {
      withTileEntityDo(world, pos, te -> {
        super.onRemove(block, world, pos, replace, bool);
        world.removeBlockEntity(pos);
        te.destroy();
      });
    }
  }

  @Nonnull
  @Override
  public ActionResultType use(@Nonnull BlockState state,
                              World world,
                              @Nonnull BlockPos pos,
                              PlayerEntity player,
                              @Nonnull Hand hand,
                              @Nonnull BlockRayTraceResult hit) {
    // activate computer
    return getTileEntityOptional(world, pos)
        .map(te -> te.onActivate(player, hand, hit))
        .orElse(ActionResultType.PASS);
  }

  public void neighborChanged(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull Block neighbourBlock, @Nonnull BlockPos neighbourPos, boolean isMoving) {
    withTileEntityDo(world, pos, te -> te.onNeighbourChange(neighbourPos));
  }

  public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbour) {
    withTileEntityDo(world, pos, te -> te.onNeighbourTileEntityChange(neighbour));
  }

  /** @deprecated */
  @Deprecated
  public void tick(@Nonnull BlockState state, ServerWorld world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // withTileEntityDo(world, pos, te -> te.blockTick());
  }

  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
    return ModTileEntities.ROBOT.create();
  }

  @Override
  public Class<RobotTileEntity> getTileEntityClass() {
    return RobotTileEntity.class;
  }
}
