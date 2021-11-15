package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.core.robot.cc.IComputerContainer;
import com.luncert.steampunkera.index.ModItems;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.items.IComputerItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotControllerItem extends Item implements IComputerItem, IMedia {

  private final ComputerFamily family;

  private IComputerContainer computerContainer;

  public RobotControllerItem(Properties props) {
    super(props);
    this.family = ComputerFamily.NORMAL;
  }

  public void bindComputerContainer(IComputerContainer computerContainer) {
    this.computerContainer = computerContainer;
  }

  /**
   * Call computerContainer#openTerminal().
   */
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!world.isClientSide) {
      if (computerContainer == null) {
        // player.displayClientMessage(
        //     ITextComponent.nullToEmpty("No robot connected to this controller"), true);
        return ActionResult.pass(stack);
      }
      computerContainer.openTerminal(player, hand, this);
    }

    return new ActionResult<>(ActionResultType.SUCCESS, stack);
  }

  @Override
  public String getLabel(@Nonnull ItemStack stack) {
    return IComputerItem.super.getLabel(stack);
  }

  @Override
  public ComputerFamily getFamily() {
    return family;
  }

  @Override
  public ItemStack withFamily(@Nonnull ItemStack stack, @Nonnull ComputerFamily computerFamily) {
    return ModItems.ROBOT_CONTROLLER.asStack();
  }
}
