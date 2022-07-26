package com.luncert.steampunkera.index;

import com.simibubi.create.AllShapes.Builder;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class ModShapes {

  public static final VoxelShape DEFAULT =
      cuboid(0D, 0D, 0D, 16D, 16D, 16D);

  public static final VoxelShaper DEPOT_HOPPER =
      shape(5D, 2D, 11D, 10.5D, 9D, 16D).forHorizontal(Direction.NORTH);

  private static Builder shape(VoxelShape shape) {
    return new Builder(shape);
  }

  private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
    return shape(cuboid(x1, y1, z1, x2, y2, z2));
  }

  private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
    return Block.box(x1, y1, z1, x2, y2, z2);
  }
}
