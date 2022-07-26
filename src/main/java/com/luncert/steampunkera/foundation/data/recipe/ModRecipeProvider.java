package com.luncert.steampunkera.foundation.data.recipe;

import com.luncert.steampunkera.SteampunkEra;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

  protected final List<GeneratedRecipe> all = new ArrayList<>();

  public ModRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  protected void buildShapelessRecipes(Consumer<IFinishedRecipe> p_200404_1_) {
    this.all.forEach((c) -> {
      c.register(p_200404_1_);
    });
    SteampunkEra.LOGGER.info(
        this.getName() + " registered " + this.all.size() + " recipe" + (this.all.size() == 1 ? "" : "s"));
  }

  protected GeneratedRecipe register(GeneratedRecipe recipe) {
    this.all.add(recipe);
    return recipe;
  }

  protected static class Marker {
    protected Marker() {
    }
  }

  @FunctionalInterface
  public interface GeneratedRecipe {
    void register(Consumer<IFinishedRecipe> var1);
  }
}
