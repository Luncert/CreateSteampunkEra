package com.luncert.steampunkera.content.core.robot;

import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputerBase;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Used to display computer gui.
 */
public class RobotContainer extends ContainerComputerBase {

  // public RobotContainer(int id, RobotTileEntity tile) {
  //   super(AllContainerTypes.ROBOT.get(), id, tile::isUsableByPlayer, tile.createServerComputer(), tile.getFamily());
  // }
  //
  // public RobotContainer(int id, PlayerInventory player, ComputerContainerData data) {
  //   super(AllContainerTypes.ROBOT.get(), id, player, data);
  // }

  private RobotContainer(int id, ServerComputer computer, RobotControllerItem item, Hand hand) {
    super(Registry.ModContainers.POCKET_COMPUTER.get(), id, (p) -> {
      ItemStack stack = p.getItemInHand(hand);
      return stack.getItem() == item && ItemPocketComputer.getServerComputer(stack) == computer;
    }, computer, item.getFamily());
  }

  public RobotContainer(int id, PlayerInventory player, ComputerContainerData data) {
    super(Registry.ModContainers.POCKET_COMPUTER.get(), id, player, data);
  }

  public static class Factory implements INamedContainerProvider {
    private final ServerComputer computer;
    private final ITextComponent name;
    private final RobotControllerItem item;
    private final Hand hand;

    public Factory(ServerComputer computer, ItemStack stack, RobotControllerItem item, Hand hand) {
      this.computer = computer;
      this.name = stack.getHoverName();
      this.item = item;
      this.hand = hand;
    }

    @Nonnull
    public ITextComponent getDisplayName() {
      return this.name;
    }

    @Nullable
    public Container createMenu(int id, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity entity) {
      return new RobotContainer(id, this.computer, this.item, this.hand);
    }
  }
}
