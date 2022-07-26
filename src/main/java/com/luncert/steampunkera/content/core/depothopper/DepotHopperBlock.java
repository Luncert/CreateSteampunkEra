package com.luncert.steampunkera.content.core.depothopper;

import com.luncert.steampunkera.index.ModShapes;
import com.luncert.steampunkera.index.ModTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

public class DepotHopperBlock extends Block implements ITE<DepotHopperTileEntity> {

  public DepotHopperBlock(Properties props) {
    super(props);
  }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos,
                             ISelectionContext context) {
    VoxelShape shape = ModShapes.DEPOT_HOPPER.get(state.getValue(FACING));
    return shape == null ? ModShapes.DEFAULT : shape;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos,
                                      ISelectionContext context) {
    return getShape(state, world, pos, context);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return ModTileEntities.DEPOT_HOPPER.create();
  }

  @Override
  public Class<DepotHopperTileEntity> getTileEntityClass() {
    return DepotHopperTileEntity.class;
  }
}
