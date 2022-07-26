package com.luncert.steampunkera.content.core.base.renderer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.luncert.steampunkera.Reference;
import com.luncert.steampunkera.SteampunkEra;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityModelManager extends JsonReloadListener {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  private static final Map<ResourceLocation, Function<JsonObject, Model>> PARSER = Maps.newHashMap();

  public EntityModelManager() {
    super(GSON, "models/entity");
  }

  static {
    // Default
    registerModelParser(new ResourceLocation(Reference.MOD_ID, "default"), new EntityModelParser());

    // Biped Model
    // registerModelParser(new ResourceLocation(Reference.MOD_ID, "biped"), new BipedModelParser());
  }

  @Override
  protected Map<ResourceLocation, JsonElement> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
    return super.prepare(resourceManagerIn, profilerIn);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> splashList,
                       IResourceManager resourceManagerIn, IProfiler profilerIn) {
    for (Map.Entry<ResourceLocation, JsonElement> entry : splashList.entrySet()) {
      try {
        Model model = parseModel((JsonObject) entry.getValue());
        ModelRegistry.registerModel(entry.getKey().toString(), model);
      } catch (Exception e) {
        SteampunkEra.LOGGER.error("Parsing error loading entity model {}", entry.getKey(), e);
      }
      SteampunkEra.LOGGER.info("Loaded entity model {}", entry.getKey());
    }
  }

  public static void registerModelParser(ResourceLocation resourceLocation, Function<JsonObject, Model> function) {
    Preconditions.checkNotNull(resourceLocation);
    Preconditions.checkNotNull(function);
    PARSER.put(resourceLocation, function);
  }

  public static Model parseModel(JsonObject json) {
    Function<JsonObject, Model> function = PARSER.get(new ResourceLocation(JSONUtils.getAsString(json, "type")));

    if (function == null)
      throw new JsonParseException("The entity model type '" + JSONUtils.getAsString(json, "type") + "' does not exist!");

    Model model = function.apply(json);
    return Objects.requireNonNull(model);
  }
}
