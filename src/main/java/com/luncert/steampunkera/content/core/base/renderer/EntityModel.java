package com.luncert.steampunkera.content.core.base.renderer;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.luncert.steampunkera.content.util.ModJsonUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class EntityModel {

  public final Map<String, Either<RenderMaterial, String>> textures;
  public final List<EntityModelGroup> groups;

  public EntityModel(Map<String, Either<RenderMaterial, String>> textures, List<EntityModelGroup> groups) {
    this.textures = textures;
    this.groups = groups;
  }

  public static EntityModel fromStream(Reader in) {
    return JSONUtils.fromJson(Deserializer.INSTANCE, in, EntityModel.class);
  }

  @OnlyIn(Dist.CLIENT)
  public static class Deserializer implements JsonDeserializer<EntityModel> {

    public static final Gson INSTANCE = (new GsonBuilder())
        .registerTypeAdapter(EntityModel.class, new Deserializer())
        .create();

    @Override
    public EntityModel deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      Map<String, Either<RenderMaterial, String>> textures = getTextures(jsonObject);
      List<EntityModelGroup> groups = getGroups(jsonObject);
      return new EntityModel(textures, groups);
    }

    private Map<String, Either<RenderMaterial, String>> getTextures(JsonObject root) {
      ResourceLocation resourcelocation = AtlasTexture.LOCATION_BLOCKS;
      Map<String, Either<RenderMaterial, String>> map = Maps.newHashMap();
      if (root.has("textures")) {
        JsonObject jsonObject = JSONUtils.getAsJsonObject(root, "textures");

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
          map.put(entry.getKey(),
              parseTextureLocationOrReference(resourcelocation, entry.getValue().getAsString()));
        }
      }

      return map;
    }

    private Either<RenderMaterial, String> parseTextureLocationOrReference(
        ResourceLocation location, String texture) {
      if (isTextureReference(texture)) {
        return Either.right(texture.substring(1));
      } else {
        ResourceLocation resourcelocation = ResourceLocation.tryParse(texture);
        if (resourcelocation == null) {
          throw new JsonParseException(texture + " is not valid resource location");
        } else {
          return Either.left(new RenderMaterial(location, resourcelocation));
        }
      }
    }

    private boolean isTextureReference(String texture) {
      return texture.charAt(0) == '#';
    }

    private List<EntityModelGroup> getGroups(JsonObject root) {
      if (root.has("elements")) {
        if (root.has("groups")) {
          List<EntityModelGroup> groups = new ArrayList<>();

          JsonArray jsonArray = root.getAsJsonArray("groups");
          for (JsonElement item : jsonArray) {
            JsonObject object = item.getAsJsonObject();
            String name = JSONUtils.getAsString(object, "name", "unknown");
            Vector3f origin = ModJsonUtil.getVector3f(object, "origin");
            List<EntityModelPart> children = getChildren(root, ModJsonUtil.getIntArray(object, "children"));
            groups.add(new EntityModelGroup(name, origin, children));
          }

          return groups;
        } else {
          EntityModelGroup defaultGroup = new EntityModelGroup("default",
              new Vector3f(0, 0, 0), getChildren(root));
          return Collections.singletonList(defaultGroup);
        }
      }

      return Collections.emptyList();
    }

    private List<EntityModelPart> getChildren(JsonObject root, int[] partIds) {
      List<EntityModelPart> children = new ArrayList<>(partIds.length);
      JsonArray elements = JSONUtils.getAsJsonArray(root, "elements");
      for (int partId : partIds) {
        JsonObject element = elements.get(partId).getAsJsonObject();
        children.add(parsePart(element));
      }
      return children;
    }

    private List<EntityModelPart> getChildren(JsonObject root) {
      JsonArray elements = JSONUtils.getAsJsonArray(root, "elements");
      List<EntityModelPart> children = new ArrayList<>(elements.size());
      for (JsonElement element : elements) {
        children.add(parsePart((JsonObject) element));
      }
      return children;
    }

    private EntityModelPart parsePart(JsonObject object) {
      Vector3f from = ModJsonUtil.getVector3f(object, "from");
      Vector3f to = ModJsonUtil.getVector3f(object, "to");
      EntityModelPartRotation rotation = parseRotation(object);
      EntityModelPartUV uv = parseUV(object);
      return new EntityModelPart(from, to, rotation, uv);
    }

    private EntityModelPartRotation parseRotation(JsonObject object) {
      Vector3f origin = ModJsonUtil.getVector3f(object, "origin");
      Direction.Axis axis = Direction.Axis.valueOf(JSONUtils.getAsString(object, "axis"));
      float angle = JSONUtils.getAsFloat(object, "angle", 0);
      return new EntityModelPartRotation(origin, axis, angle, false);
    }

    private EntityModelPartUV parseUV(JsonObject object) {
      float[] texOffs = ModJsonUtil.getFloatArray(object, 2, "texOffs", 0, 0);
      float[] texSize = ModJsonUtil.getFloatArray(object, 2, "texSize", 0, 0);
      return new EntityModelPartUV(texOffs[0], texOffs[1], texSize[0], texSize[1],
          JSONUtils.getAsString(object, "texture"));
    }
  }
}
