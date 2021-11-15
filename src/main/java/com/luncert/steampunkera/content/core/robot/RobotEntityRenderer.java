package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.model.RobotModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.luncert.steampunkera.Reference.MOD_ID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotEntityRenderer extends EntityRenderer<RobotEntity> {

    private final RobotModel model;

    public RobotEntityRenderer(EntityRendererManager manager) {
        super(manager);
        model = new RobotModel();
    }

    @Override
    public ResourceLocation getTextureLocation(RobotEntity entity) {
        return new ResourceLocation(MOD_ID, "textures/entity/robot.png");
    }

    @Override
    public boolean shouldRender(RobotEntity entity, ClippingHelper clippingHelper,
                                double cameraX, double cameraY, double cameraZ) {
        if (!entity.isAlive())
            return false;

        return super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ);
    }

    @Override
    public void render(RobotEntity entity,
                       float yaw, float partialTicks,
                       MatrixStack ms, IRenderTypeBuffer buffers, int overlay) {
        super.render(entity, yaw, partialTicks, ms, buffers, overlay);

        ms.pushPose();

        IVertexBuilder ivertexbuilder = buffers.getBuffer(model.renderType(getTextureLocation(entity)));
        model.renderToBuffer(ms, ivertexbuilder, overlay, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        ms.popPose();
    }
}
