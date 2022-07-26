package com.luncert.steampunkera.content.core.base.renderer;

import net.minecraft.util.math.vector.Vector3f;

public class EntityModelPart {

  public final Vector3f from;
  public final Vector3f to;
  public final EntityModelPartRotation rotation;
  public final EntityModelPartUV uv;

  public EntityModelPart(Vector3f from, Vector3f to, EntityModelPartRotation rotation, EntityModelPartUV uv) {
    this.from = from;
    this.to = to;
    this.rotation = rotation;
    this.uv = uv;
  }
}
