package com.luncert.steampunkera.content.core.base.renderer;

import net.minecraft.util.math.vector.Vector3f;

import java.util.List;

public class EntityModelGroup {

  public final String name;
  public final Vector3f origin;
  public final List<EntityModelPart> children;

  public EntityModelGroup(String name, Vector3f origin, List<EntityModelPart> children) {
    this.name = name;
    this.origin = origin;
    this.children = children;
  }
}
