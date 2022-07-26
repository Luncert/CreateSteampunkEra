package com.luncert.steampunkera.content.core.depothopper;

import com.simibubi.create.content.logistics.block.depot.DepotTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class DepotHopperBehaviour extends TileEntityBehaviour {

  private static final String NBT_PREV_ITEM = "PrevItem";

  public static BehaviourType<DepotHopperBehaviour> TYPE = new BehaviourType<>();

  private ItemStack prevItem;

  public DepotHopperBehaviour(SmartTileEntity te) {
    super(te);
  }

  @Override
  public BehaviourType<?> getType() {
    return TYPE;
  }

  @Override
  public void tick() {
    super.tick();

    World world = getWorld();
    if (world.isClientSide) {
      return;
    }

    BlockPos pos = getPos();
    BlockState state = world.getBlockState(pos);

    Optional<Direction> optionalValue = state.getOptionalValue(BlockStateProperties.FACING);
    if (optionalValue.isPresent()) {
      Direction facing = optionalValue.get();

      TileEntity depotEntity = getWorld().getBlockEntity(pos.relative(facing.getOpposite()));
      if (depotEntity instanceof DepotTileEntity) {
        Optional<IItemHandler> capability = depotEntity
            .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).resolve();
        if (capability.isPresent()) {
          IItemHandler inventory = capability.get();
          ItemStack processedItem = extractOutputBuffer(inventory);
          if (processedItem == null) {
            processedItem = extractHeldItem(inventory);
          }

          if (processedItem != null) {
            dropItemStack(world,
                pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f,
                processedItem);
          }
        }

        return;
      }
    }

    world.removeBlock(pos, false);
  }

  private ItemStack extractOutputBuffer(IItemHandler inventory) {
    for (int slot = 1; slot < inventory.getSlots(); slot++) {
      ItemStack stack = inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        return inventory.extractItem(slot, stack.getCount(), false);
      }
    }

    return null;
  }
  
  private ItemStack extractHeldItem(IItemHandler inventory) {
    // search help item
    ItemStack heldItem = inventory.getStackInSlot(0);
    boolean depotUpdated = false;
    World world = getWorld();

    if (!heldItem.isEmpty()) {
      if (prevItem == null || prevItem.equals(heldItem, false)) {
        prevItem = heldItem;
        return null;
      } else {
        depotUpdated = world.getRecipeManager().getRecipes()
            .stream()
            .anyMatch(recipe -> {
              for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.test(prevItem)) {
                  return recipe.getResultItem().equals(heldItem, false);
                }
              }

              return false;
            });
      }
    }

    prevItem = null;
    return depotUpdated ? inventory.extractItem(0, heldItem.getCount(), false) : null;
  }

  private void dropItemStack(World world, double x, double y, double z, ItemStack item) {
    ItemEntity itementity = new ItemEntity(world, x, y, z, item);
    world.addFreshEntity(itementity);
  }

  public void write(CompoundNBT compound, boolean clientPacket) {
    if (prevItem != null) {
      compound.put(NBT_PREV_ITEM, prevItem.serializeNBT());
    }
  }

  public void read(CompoundNBT compound, boolean clientPacket) {
    prevItem = null;
    if (compound.contains(NBT_PREV_ITEM)) {
      prevItem = ItemStack.of(compound.getCompound(NBT_PREV_ITEM));
    }
  }
}
