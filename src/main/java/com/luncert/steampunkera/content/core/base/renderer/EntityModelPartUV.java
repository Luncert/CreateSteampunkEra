package com.luncert.steampunkera.content.core.base.renderer;

public class EntityModelPartUV {

  public final float xTexOffs;
  public final float yTexOffs;
  public final float texWidth;
  public final float texHeight;
  public final String texture;

  public EntityModelPartUV(float xTexOffs, float yTexOffs, float texWidth, float texHeight, String texture) {
    this.xTexOffs = xTexOffs;
    this.yTexOffs = yTexOffs;
    this.texWidth = texWidth;
    this.texHeight = texHeight;
    this.texture = texture;
  }
}
