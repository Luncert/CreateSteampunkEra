// package com.luncert.steampunkera.content.core.itemattribute;
//
// import com.simibubi.create.content.logistics.item.filter.ItemAttribute;
// import net.minecraft.item.ItemStack;
// import net.minecraft.item.crafting.IRecipe;
// import net.minecraft.item.crafting.IRecipeType;
// import net.minecraft.item.crafting.Ingredient;
// import net.minecraft.nbt.CompoundNBT;
// import net.minecraft.world.World;
// import net.minecraftforge.fml.common.registry.GameRegistry;
//
// import java.util.List;
//
// public class IsCombustionProducts implements ItemAttribute {
//
//   @Override
//   public boolean appliesTo(ItemStack itemStack) {
//     return false;
//   }
//
//   @Override
//   public boolean appliesTo(ItemStack stack, World world) {
//     world.getRecipeManager().getAllRecipesFor(IRecipeType.SMELTING)
//       .stream().filter(r -> {
//       for (Ingredient ingredient : r.getIngredients()) {
//         if (ingredient.test(stack)) {
//           return true;
//         }
//       }
//       return false;
//     }).any;
//
//     return ItemAttribute.super.appliesTo(stack, world);
//   }
//
//   @Override
//   public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
//     return null;
//   }
//
//   @Override
//   public String getTranslationKey() {
//     return null;
//   }
//
//   @Override
//   public void writeNBT(CompoundNBT compoundNBT) {
//
//   }
//
//   @Override
//   public ItemAttribute readNBT(CompoundNBT compoundNBT) {
//     return null;
//   }
// }
