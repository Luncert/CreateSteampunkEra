package com.luncert.steampunkera.content.core.base.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.function.Function;

public class TexturedModelRenderer extends ModelRenderer {

  private BakedEntityModel model;
  private String texture;

  public TexturedModelRenderer(BakedEntityModel model) {
    super(model);
  }

  public String getTexture() {
    return texture;
  }

  public TexturedModelRenderer setTexture(String texture) {
    this.texture = texture;
    return this;
  }

  public void render(MatrixStack ms, IRenderTypeBuffer buffers,
                     Function<ResourceLocation, RenderType> renderType,
                     int overlay, int p_228308_4_) {
    Optional<RenderMaterial> textureEntry = model.findTexture(texture);
    // textureEntry.ifPresent();
    IVertexBuilder ivertexbuilder = buffers.getBuffer(renderType.apply(null));

    super.render(ms, ivertexbuilder, overlay, p_228308_4_);
  }
}
