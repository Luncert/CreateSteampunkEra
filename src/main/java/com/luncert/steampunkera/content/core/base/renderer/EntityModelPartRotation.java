package com.luncert.steampunkera.content.core.base.renderer;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class EntityModelPartRotation {

  public final Vector3f origin;
  public final Direction.Axis axis;
  public final float angle;
  public final boolean rescale;

  public EntityModelPartRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
    this.origin = origin;
    this.axis = axis;
    this.angle = angle;
    this.rescale = rescale;
  }
}
