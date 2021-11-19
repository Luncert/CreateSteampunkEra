package com.luncert.steampunkera.content.core.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class JsonModel extends Model {

  private static final Logger LOGGER = LogManager.getLogger();

  public static JsonModel load(ResourceLocation location) {
    try {
      InputStream is = Minecraft.getInstance().getResourceManager().getResource(location).getInputStream();
      BlockModel blockModel = BlockModel.fromString(IOUtils.toString(is, Charset.defaultCharset()));
      return new JsonModel(blockModel);
    } catch (IOException e) {
      LOGGER.error("missing resource " + location, e);
    }

    return new JsonModel();
  }

  private final ModelRenderer renderer;

  public JsonModel() {
    super(RenderType::entityCutoutNoCull);

    renderer = new ModelRenderer(this);
    renderer.addBox(-8, 0, -8, 16, 16, 16, 0.0F, false);
  }

  public JsonModel(BlockModel blockModel) {
    super(RenderType::entityCutoutNoCull);

    this.renderer = parseBlockModel(blockModel);
  }

  private static final Vector3f MODEL_OFFSET = new Vector3f(-8f, 0f, -8f);

  private ModelRenderer parseBlockModel(BlockModel blockModel) {
    ModelRenderer root = new ModelRenderer(this);
    ModelRenderer nonRotationCubes = new ModelRenderer(this);
    root.addChild(nonRotationCubes);

    for (BlockPart element : blockModel.getElements()) {
      element.to.sub(element.from);
      element.from.add(MODEL_OFFSET);
      Vector3f start = element.from;
      Vector3f box = element.to;

      ModelRenderer renderer = nonRotationCubes;

      if (element.rotation != null) {
        renderer = new ModelRenderer(this);
        root.addChild(renderer);
        switch (element.rotation.axis) {
          case X:
            renderer.xRot = element.rotation.angle;
            break;
          case Y:
            renderer.yRot = element.rotation.angle;
            break;
          case Z:
            renderer.zRot = element.rotation.angle;
            break;
        }
      }

      renderer.texOffs(0, 0)
          .addBox(start.x(), start.y(), start.z(), box.x(), box.y(), box.z(), 0F, false);
    }

    return root;
  }

  public void setRotationAngle(Direction.Axis axis, float value) {
    switch (axis) {
      case X:
        renderer.xRot = value;
        break;
      case Y:
        renderer.yRot = value;
        break;
      case Z:
        renderer.zRot = value;
        break;
    }
  }

  @Override
  public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
                             float red, float green, float blue, float alpha) {
    renderer.translateAndRotate(matrixStackIn);
    renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
