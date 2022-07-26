package com.luncert.steampunkera.content.core.base.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class BakedEntityModel extends Model {

  private static final Logger LOGGER = LogManager.getLogger();

  public static BakedEntityModel load(ResourceLocation location) {
    try {
      InputStream is = Minecraft.getInstance().getResourceManager().getResource(location).getInputStream();
      EntityModel model = EntityModel.fromStream(new InputStreamReader(is));
      return new BakedEntityModel(model);
    } catch (IOException e) {
      LOGGER.error("missing resource " + location, e);
    }

    return new BakedEntityModel();
  }

  private final TexturedModelRenderer renderer;
  private final Map<String, Either<RenderMaterial, String>> textureMap;

  public BakedEntityModel() {
    super(RenderType::entityCutoutNoCull);

    textureMap = Collections.emptyMap();
    renderer = new TexturedModelRenderer(this);
    renderer.addBox(-8, 0, -8, 16, 16, 16, 0, false);
  }

  public BakedEntityModel(EntityModel model) {
    super(RenderType::entityCutoutNoCull);

    textureMap = model.textures;
    this.renderer = parseBlockModel(model);
  }

  // private static final Vector3f MODEL_OFFSET = new Vector3f(-8f, 0f, -8f);

  private TexturedModelRenderer parseBlockModel(EntityModel model) {
    TexturedModelRenderer root = new TexturedModelRenderer(this);

    for (EntityModelGroup group : model.groups) {
      TexturedModelRenderer g = new TexturedModelRenderer(this);
      root.addChild(g);
      for (EntityModelPart child : group.children) {
        TexturedModelRenderer c = new TexturedModelRenderer(this);
        g.addChild(c);

        EntityModelPartUV uv = child.uv;
        c.setTexture(uv.texture)
            .setTexSize((int) uv.texWidth, (int) uv.texHeight)
            .texOffs((int) uv.xTexOffs, (int) uv.yTexOffs);

        if (child.rotation != null) {
          c.x = child.rotation.origin.x();
          c.y = child.rotation.origin.y();
          c.z = child.rotation.origin.z();
          switch (child.rotation.axis) {
            case X:
              c.xRot = child.rotation.angle;
              break;
            case Y:
              c.yRot = child.rotation.angle;
              break;
            case Z:
              c.zRot = child.rotation.angle;
              break;
          }
        }

        Vector3f start = child.from;
        Vector3f box = child.to;
        c.addBox(start.x(), start.y(), start.z(), box.x(), box.y(), box.z());
      }
    }

    return root;
  }

  public Optional<RenderMaterial> findTexture(String textureReference) {
    if (StringUtils.isBlank(textureReference)) {
      return Optional.empty();
    }

    while (true) {
      Either<RenderMaterial, String> either = textureMap.get(textureReference);
      if (either != null) {
        Optional<String> opt = either.right();
        if (opt.isPresent()) {
          textureReference = opt.get();
        } else {
          return either.left();
        }
      } else {
        return Optional.of(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, MissingTextureSprite.getLocation()));
      }
    }
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
  public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn,
                             int packedLightIn, int packedOverlayIn,
                             float red, float green, float blue, float alpha) {
    renderer.translateAndRotate(matrixStackIn);
    renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
