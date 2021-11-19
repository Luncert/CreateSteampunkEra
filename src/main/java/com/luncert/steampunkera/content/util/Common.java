package com.luncert.steampunkera.content.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class Common {

  private Common() {
  }

  public static Vector3d relative(Vector3d v, Direction.Axis axis, double delta) {
    if (delta != 0) {
      switch (axis) {
        case X:
          return v.add(delta, 0, 0);
        case Y:
          return v.add(0, delta, 0);
        case Z:
          return v.add(0, 0, delta);
      }
    }

    return v;
  }

  public static Vector3d set(Vector3d v, Direction.Axis axis, double value) {
    switch (axis) {
      case X:
        return new Vector3d(value, v.y, v.z);
      case Y:
        return new Vector3d(v.x, value, v.z);
      case Z:
        return new Vector3d(v.x, v.y, value);
    }

    return v;
  }
}
