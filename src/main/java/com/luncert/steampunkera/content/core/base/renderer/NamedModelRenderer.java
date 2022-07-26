package com.luncert.steampunkera.content.core.base.renderer;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class NamedModelRenderer extends ModelRenderer {

  public final String name;

  public NamedModelRenderer(String name, Model model) {
    super(model);
    this.name = name;
  }

  public NamedModelRenderer(String name, Model model, int texOffX, int texOffY) {
    super(model, texOffX, texOffY);
    this.name = name;
  }

  public NamedModelRenderer(String name, int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn) {
    super(textureWidthIn, textureHeightIn, textureOffsetXIn, textureOffsetYIn);
    this.name = name;
  }

  public void setRotationPoint(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public String getName() {
    return name;
  }
}

