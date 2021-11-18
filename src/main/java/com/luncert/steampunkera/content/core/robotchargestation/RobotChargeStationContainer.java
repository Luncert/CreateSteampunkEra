package com.luncert.steampunkera.content.core.robotchargestation;

import com.luncert.steampunkera.index.ModContainerTypes;
import com.luncert.steampunkera.index.ModItems;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotChargeStationContainer extends Container {

  private RobotChargeStationTileEntity te;
  private final PlayerEntity player;

  public RobotChargeStationContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer extraData) {
    super(type, id);
    this.player = inv.player;
    ClientWorld world = Minecraft.getInstance().level;
    TileEntity tileEntity = world.getBlockEntity(extraData.readBlockPos());
    if (tileEntity instanceof RobotChargeStationTileEntity) {
      te = (RobotChargeStationTileEntity)tileEntity;
      te.handleUpdateTag(te.getBlockState(), extraData.readNbt());
      init();
    }
  }

  public RobotChargeStationContainer(ContainerType<?> type, int id, PlayerInventory inv, RobotChargeStationTileEntity te) {
    super(type, id);
    this.player = inv.player;
    this.te = te;

    init();
  }

  public static RobotChargeStationContainer create(int id, PlayerInventory inv, RobotChargeStationTileEntity te) {
    return new RobotChargeStationContainer(ModContainerTypes.ROBOT_CHARGE_STATION.get(), id, inv, te);
  }

  protected void init() {
    Slot inputSlot = new SlotItemHandler(te.inventory, 0, 123, 22) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return true;
      }
    };

    addSlot(inputSlot);

    // player Slots
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        addSlot(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 70 + row * 18));
      }
    }

    for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
      addSlot(new Slot(player.inventory, hotbarSlot, 8 + hotbarSlot * 18, 128));
    }

    broadcastChanges();
  }

  @Override
  public boolean stillValid(PlayerEntity player) {
    return this.te != null && this.te.canPlayerUse(player);
  }

  @Override
  public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
    Slot clickedSlot = getSlot(index);
    if (clickedSlot.hasItem()) {
      ItemStack stack = clickedSlot.getItem();
      if (index == 0) {
        // remove item from input slot to player slot, 0 -> all other slot
        moveItemStackTo(stack, 1, slots.size(), false);
      } else {
        // move player inventory item to input slot, -> 0
        moveItemStackTo(stack, 0, 1, false);
      }
    }

    return ItemStack.EMPTY;
  }

  public RobotChargeStationTileEntity getTileEntity() {
    return te;
  }
}
