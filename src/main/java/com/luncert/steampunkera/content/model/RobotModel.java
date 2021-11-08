package com.luncert.steampunkera.content.model;

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
        renderer.setTexSize(64, 64);
        renderer.texOffs(0, 0);
        renderer.addBox(-8, 0, -8, 16, 16, 16, 0.0F, false);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
                               float red, float green, float blue, float alpha) {
        renderer.translateAndRotate(matrixStackIn);
        renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    // private static class RobotModelRenderer extends ModelRenderer {
    //
    //     private BlockPart blockPart;
    //
    //     public RobotModelRenderer(Model model, BlockPart blockPart) {
    //         super(model);
    //         this.blockPart = blockPart;
    //     }
    //
    //     public void addBox(float xOffset, float yOffset,
    //                        float zOffset, float width, float height, float depth) {
    //
    //     }
    //
    //     private static class RobotModelBox extends ModelRenderer.ModelBox {
    //
    //         public RobotModelBox(int xTexOffs, int yTexOffs,
    //                              float offsetX, float offsetY, float offsetZ,
    //                              float width, float height, float depth,
    //                              float xScale, float yScale, float zScale,
    //                              boolean mirror,
    //                              float xTexSize, float yTexSize) {
    //             super(xTexOffs, yTexOffs, offsetX, offsetY, offsetZ,
    //                     width, height, depth, xScale, yScale, zScale,
    //                     mirror, xTexSize, yTexSize);
    //         }
    //     }
    // }
}
