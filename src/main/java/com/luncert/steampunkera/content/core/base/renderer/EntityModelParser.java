package com.luncert.steampunkera.content.core.base.renderer;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.luncert.steampunkera.content.util.ModJsonUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class EntityModelParser implements Function<JsonObject, Model> {

  private static final EntityModelParser INSTANCE = new EntityModelParser();

  public static Model load(ResourceLocation location) {
    try {
      InputStream is = Minecraft.getInstance().getResourceManager().getResource(location).getInputStream();
      return INSTANCE.apply(JSONUtils.parse(new InputStreamReader(is)));
    } catch (IOException e) {
      throw new RuntimeException(e);
      // SteampunkEra.LOGGER.error("missing resource " + location, e);
    }
  }

  @Override
  public Model apply(JsonObject jsonObject) {
    ParsedModel model = new ParsedModel();
    model.texWidth = JSONUtils.getAsInt(jsonObject, "texture_width", 64);
    model.texHeight = JSONUtils.getAsInt(jsonObject, "texture_height", 32);

    if (JSONUtils.isValidNode(jsonObject, "cubes")) {
      JsonArray cubes = JSONUtils.getAsJsonArray(jsonObject, "cubes");

      for (int i = 0; i < cubes.size(); i++) {
        JsonObject cubeJson = cubes.get(i).getAsJsonObject();
        model.addCube(parseRendererModel(cubeJson, model));
      }
    }

    return model;
  }

  public static NamedModelRenderer parseRendererModel(JsonObject json, Model model) {
    int[] textureOffsets = ModJsonUtil.getIntArray(json, 2, "texture_offset", 0, 0);
    NamedModelRenderer rendererModel = new NamedModelRenderer(JSONUtils.getAsString(json, "name", ""), model, textureOffsets[0], textureOffsets[1]);
    float[] offsets = ModJsonUtil.getFloatArray(json, 3, "offset", 0, 0, 0);
    float[] rotationPoint = ModJsonUtil.getFloatArray(json, 3, "rotation_point", 0, 0, 0);
    float[] rotation = ModJsonUtil.getFloatArray(json, 3, "rotation", 0, 0, 0);
    int[] size = ModJsonUtil.getIntArray(json, 3, "size", 1, 1, 1);
    rendererModel.addBox(offsets[0], offsets[1], offsets[2], size[0], size[1], size[2], JSONUtils.getAsFloat(json, "scale", 0F));
    rendererModel.setRotationPoint(rotationPoint[0], rotationPoint[1], rotationPoint[2]);
    rendererModel.xRot = (float) Math.toRadians(rotation[0]);
    rendererModel.yRot = (float) Math.toRadians(rotation[1]);
    rendererModel.zRot = (float) Math.toRadians(rotation[2]);
    rendererModel.mirror = JSONUtils.getAsBoolean(json, "mirror", false);
    if (JSONUtils.isValidNode(json, "children")) {
      JsonArray children = JSONUtils.getAsJsonArray(json, "children");
      for (int i = 0; i < children.size(); i++) {
        rendererModel.addChild(parseRendererModel(children.get(i).getAsJsonObject(), model));
      }
    }

    return rendererModel;
  }

  public static class ParsedModel extends Model {

    public List<NamedModelRenderer> cubes = Lists.newLinkedList();

    public ParsedModel() {
      super(RenderType::entityCutoutNoCull);
    }

    public ParsedModel(List<NamedModelRenderer> cubes) {
      this();
      this.cubes = cubes;
    }

    public ParsedModel addCube(NamedModelRenderer rendererModel) {
      this.cubes.add(rendererModel);
      return this;
    }

    public NamedModelRenderer getNamedPart(String name) {
      for (NamedModelRenderer modelRenderer : this.cubes) {
        if (modelRenderer.getName().equals(name)) {
          return modelRenderer;
        }
      }
      return null;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder,
                               int packedLightIn, int packedOverlayIn,
                               float red, float green, float blue, float alpha) {
      RenderSystem.enableBlend();
      for (ModelRenderer cube : this.cubes) {
        cube.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      }
      RenderSystem.disableBlend();
    }
  }
}
