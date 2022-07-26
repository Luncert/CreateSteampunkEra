package com.luncert.steampunkera.content.core.robot;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class RobotModel extends Model {

  private final ModelRenderer renderer;

  public RobotModel() {
    super(RenderType::entityCutoutNoCull);

    renderer = new ModelRenderer(this);
    renderer.setTexSize(64, 64)
        .texOffs(0, 0)
        .addBox(-8, 0, -8,
            16, 16, 16,
            0, false);
  }

  @Override
  public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn,
                             int packedLightIn, int packedOverlayIn,
                             float red, float green, float blue, float alpha) {
    renderer.translateAndRotate(matrixStackIn);
    renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
