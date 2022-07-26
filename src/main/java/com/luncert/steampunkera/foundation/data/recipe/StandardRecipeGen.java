package com.luncert.steampunkera.foundation.data.recipe;

import com.google.common.base.Supplier;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.luncert.steampunkera.index.ModBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class StandardRecipeGen extends ModRecipeProvider {

  GeneratedRecipe
    DEPOT_HOPPER = create(ModBlocks.DEPOT_HOPPER)
      .returns(2)
      .unlockedBy(AllItems.ANDESITE_ALLOY::get)
      .viaShaped(b -> b.define('A', AllTags.forgeItemTag("ingots/brass"))
          .define('K', Items.DRIED_KELP)
          .define('E', AllItems.ELECTRON_TUBE.get())
          .pattern("AEA")
          .pattern(" K "));

  /*
   * End of recipe list
   */

  String currentFolder = "";

  Marker enterSection(AllSections section) {
    currentFolder = Lang.asId(section.name());
    return new Marker();
  }

  Marker enterFolder(String folder) {
    currentFolder = folder;
    return new Marker();
  }

  GeneratedRecipeBuilder create(Supplier<IItemProvider> result) {
    return new GeneratedRecipeBuilder(currentFolder, result);
  }

  GeneratedRecipeBuilder create(ResourceLocation result) {
    return new GeneratedRecipeBuilder(currentFolder, result);
  }

  GeneratedRecipeBuilder create(ItemProviderEntry<? extends IItemProvider> result) {
    return create(result::get);
  }

  GeneratedRecipe createSpecial(Supplier<? extends SpecialRecipeSerializer<?>> serializer, String recipeType, String path) {
    ResourceLocation location = Create.asResource(recipeType + "/" + currentFolder + "/" + path);
    return register(consumer -> {
      CustomRecipeBuilder b = CustomRecipeBuilder.special(serializer.get());
      b.save(consumer, location.toString());
    });
  }

  GeneratedRecipe blastCrushedMetal(Supplier<? extends IItemProvider> result,
                                    Supplier<? extends IItemProvider> ingredient) {
    return create(result::get).withSuffix("_from_crushed")
        .viaCooking(ingredient::get)
        .rewardXP(.1f)
        .inBlastFurnace();
  }

  GeneratedRecipe blastModdedCrushedMetal(ItemEntry<? extends Item> ingredient, String metalName, Mods... mods) {
    for (Mods mod : mods) {
      ResourceLocation ingot = mod.ingotOf(metalName);
      String modId = mod.getId();
      create(ingot).withSuffix("_compat_" + modId)
          .whenModLoaded(modId)
          .viaCooking(ingredient::get)
          .rewardXP(.1f)
          .inBlastFurnace();
    }
    return null;
  }

  GeneratedRecipe blastMetalOre(Supplier<? extends IItemProvider> result, ITag.INamedTag<Item> ore) {
    return create(result::get).withSuffix("_from_ore")
        .viaCookingTag(() -> ore)
        .rewardXP(.1f)
        .inBlastFurnace();
  }

  GeneratedRecipe recycleGlass(BlockEntry<? extends Block> ingredient) {
    return create(() -> Blocks.GLASS).withSuffix("_from_" + ingredient.getId()
        .getPath())
        .viaCooking(ingredient::get)
        .forDuration(50)
        .inFurnace();
  }

  GeneratedRecipe recycleGlassPane(BlockEntry<? extends Block> ingredient) {
    return create(() -> Blocks.GLASS_PANE).withSuffix("_from_" + ingredient.getId()
        .getPath())
        .viaCooking(ingredient::get)
        .forDuration(50)
        .inFurnace();
  }

  GeneratedRecipe metalCompacting(List<ItemProviderEntry<? extends IItemProvider>> variants,
                                  List<Supplier<ITag<Item>>> ingredients) {
    GeneratedRecipe result = null;
    for (int i = 0; i + 1 < variants.size(); i++) {
      ItemProviderEntry<? extends IItemProvider> currentEntry = variants.get(i);
      ItemProviderEntry<? extends IItemProvider> nextEntry = variants.get(i + 1);
      Supplier<ITag<Item>> currentIngredient = ingredients.get(i);
      Supplier<ITag<Item>> nextIngredient = ingredients.get(i + 1);

      result = create(nextEntry).withSuffix("_from_compacting")
          .unlockedBy(currentEntry::get)
          .viaShaped(b -> b.pattern("###")
              .pattern("###")
              .pattern("###")
              .define('#', currentIngredient.get()));

      result = create(currentEntry).returns(9)
          .withSuffix("_from_decompacting")
          .unlockedBy(nextEntry::get)
          .viaShapeless(b -> b.requires(nextIngredient.get()));
    }
    return result;
  }

  GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends IItemProvider>> cycle) {
    GeneratedRecipe result = null;
    for (int i = 0; i < cycle.size(); i++) {
      ItemProviderEntry<? extends IItemProvider> currentEntry = cycle.get(i);
      ItemProviderEntry<? extends IItemProvider> nextEntry = cycle.get((i + 1) % cycle.size());
      result = create(nextEntry).withSuffix("from_conversion")
          .unlockedBy(currentEntry::get)
          .viaShapeless(b -> b.requires(currentEntry.get()));
    }
    return result;
  }

  class GeneratedRecipeBuilder {

    private String path;
    private String suffix;
    private Supplier<? extends IItemProvider> result;
    private ResourceLocation compatDatagenOutput;
    List<ICondition> recipeConditions;

    private Supplier<ItemPredicate> unlockedBy;
    private int amount;

    private GeneratedRecipeBuilder(String path) {
      this.path = path;
      this.recipeConditions = new ArrayList<>();
      this.suffix = "";
      this.amount = 1;
    }

    public GeneratedRecipeBuilder(String path, Supplier<? extends IItemProvider> result) {
      this(path);
      this.result = result;
    }

    public GeneratedRecipeBuilder(String path, ResourceLocation result) {
      this(path);
      this.compatDatagenOutput = result;
    }

    GeneratedRecipeBuilder returns(int amount) {
      this.amount = amount;
      return this;
    }

    GeneratedRecipeBuilder unlockedBy(Supplier<? extends IItemProvider> item) {
      this.unlockedBy = () -> ItemPredicate.Builder.item()
          .of(item.get())
          .build();
      return this;
    }

    GeneratedRecipeBuilder unlockedByTag(Supplier<ITag<Item>> tag) {
      this.unlockedBy = () -> ItemPredicate.Builder.item()
          .of(tag.get())
          .build();
      return this;
    }

    GeneratedRecipeBuilder whenModLoaded(String modid) {
      return withCondition(new ModLoadedCondition(modid));
    }

    GeneratedRecipeBuilder whenModMissing(String modid) {
      return withCondition(new NotCondition(new ModLoadedCondition(modid)));
    }

    GeneratedRecipeBuilder withCondition(ICondition condition) {
      recipeConditions.add(condition);
      return this;
    }

    GeneratedRecipeBuilder withSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
      return register(consumer -> {
        ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(result.get(), amount));
        if (unlockedBy != null)
          b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
        b.save(consumer, createLocation("crafting"));
      });
    }

    GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
      return register(consumer -> {
        ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(result.get(), amount));
        if (unlockedBy != null)
          b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
        b.save(consumer, createLocation("crafting"));
      });
    }

    private ResourceLocation createSimpleLocation(String recipeType) {
      return Create.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
    }

    private ResourceLocation createLocation(String recipeType) {
      return Create.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
    }

    private ResourceLocation getRegistryName() {
      return compatDatagenOutput == null ? result.get()
          .asItem()
          .getRegistryName() : compatDatagenOutput;
    }

    GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends IItemProvider> item) {
      return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
    }

    GeneratedCookingRecipeBuilder viaCookingTag(Supplier<ITag<Item>> tag) {
      return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
    }

    GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
      return new GeneratedCookingRecipeBuilder(ingredient);
    }

    class GeneratedCookingRecipeBuilder {

      private Supplier<Ingredient> ingredient;
      private float exp;
      private int cookingTime;

      private final CookingRecipeSerializer<?> FURNACE = IRecipeSerializer.SMELTING_RECIPE,
          SMOKER = IRecipeSerializer.SMOKING_RECIPE, BLAST = IRecipeSerializer.BLASTING_RECIPE,
          CAMPFIRE = IRecipeSerializer.CAMPFIRE_COOKING_RECIPE;

      GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
        this.ingredient = ingredient;
        cookingTime = 200;
        exp = 0;
      }

      GeneratedCookingRecipeBuilder forDuration(int duration) {
        cookingTime = duration;
        return this;
      }

      GeneratedCookingRecipeBuilder rewardXP(float xp) {
        exp = xp;
        return this;
      }

      GeneratedRecipe inFurnace() {
        return inFurnace(b -> b);
      }

      GeneratedRecipe inFurnace(UnaryOperator<CookingRecipeBuilder> builder) {
        return create(FURNACE, builder, 1);
      }

      GeneratedRecipe inSmoker() {
        return inSmoker(b -> b);
      }

      GeneratedRecipe inSmoker(UnaryOperator<CookingRecipeBuilder> builder) {
        create(FURNACE, builder, 1);
        create(CAMPFIRE, builder, 3);
        return create(SMOKER, builder, .5f);
      }

      GeneratedRecipe inBlastFurnace() {
        return inBlastFurnace(b -> b);
      }

      GeneratedRecipe inBlastFurnace(UnaryOperator<CookingRecipeBuilder> builder) {
        create(FURNACE, builder, 1);
        return create(BLAST, builder, .5f);
      }

      private GeneratedRecipe create(CookingRecipeSerializer<?> serializer,
                                     UnaryOperator<CookingRecipeBuilder> builder, float cookingTimeModifier) {
        return register(consumer -> {
          boolean isOtherMod = compatDatagenOutput != null;

          CookingRecipeBuilder b = builder
              .apply(CookingRecipeBuilder.cooking(ingredient.get(), isOtherMod ? Items.DIRT : result.get(),
                  exp, (int) (cookingTime * cookingTimeModifier), serializer));
          if (unlockedBy != null)
            b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
          b.save(result -> {
            consumer.accept(
                isOtherMod ? new ModdedCookingRecipeResult(result, compatDatagenOutput, recipeConditions)
                    : result);
          }, createSimpleLocation(serializer.getRegistryName()
              .getPath()));
        });
      }
    }
  }

  @Override
  public String getName() {
    return "Create's Standard Recipes";
  }

  public StandardRecipeGen(DataGenerator generator) {
    super(generator);
  }

  private static class ModdedCookingRecipeResult implements IFinishedRecipe {

    private IFinishedRecipe wrapped;
    private ResourceLocation outputOverride;
    private List<ICondition> conditions;

    public ModdedCookingRecipeResult(IFinishedRecipe wrapped, ResourceLocation outputOverride,
                                     List<ICondition> conditions) {
      this.wrapped = wrapped;
      this.outputOverride = outputOverride;
      this.conditions = conditions;
    }

    @Override
    public ResourceLocation getId() {
      return wrapped.getId();
    }

    @Override
    public IRecipeSerializer<?> getType() {
      return wrapped.getType();
    }

    @Override
    public JsonObject serializeAdvancement() {
      return wrapped.serializeAdvancement();
    }

    @Override
    public ResourceLocation getAdvancementId() {
      return wrapped.getAdvancementId();
    }

    @Override
    public void serializeRecipeData(JsonObject object) {
      wrapped.serializeRecipeData(object);
      object.addProperty("result", outputOverride.toString());

      JsonArray conds = new JsonArray();
      conditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
      object.add("conditions", conds);
    }

  }
}
