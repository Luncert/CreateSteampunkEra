package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.util.Lang;
import com.luncert.steampunkera.index.ModItems;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.items.IComputerItem;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotControllerItem extends Item implements IComputerItem, IMedia {
  private static final Logger LOGGER = LogManager.getLogger();

  private final ITextComponent noRobotBound = Lang.translate("msg.robot_controller_item.no_robot_bound");
  private final ComputerFamily family;

  public RobotControllerItem(Properties props) {
    super(props);
    this.family = ComputerFamily.NORMAL;
  }

  public static RobotControllerItemEntity wrap(ItemStack stack) {
    return new RobotControllerItemEntity(stack);
  }

  public static class RobotControllerItemEntity {

    private final ItemStack stack;
    private final RobotControllerItem item;

    RobotControllerItemEntity(ItemStack stack) {
      this.stack = stack;
      this.item = (RobotControllerItem) stack.getItem();
    }

    public void bindRobotServer(int computerID) {
      if (stack.getTag() != null && computerID != -1) {
        stack.getTag().putInt(NBT_ID, computerID);
      }
    }

    public int getRobotServer() {
      return item.getComputerID(stack);
    }

    public void unbindRobotServer() {
      if (stack.getTag() != null) {
        stack.getTag().remove(NBT_ID);
      }
    }
  }

  /**
   * Call computerContainer#openTerminal().
   */
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!world.isClientSide) {
      if (getComputerID(stack) == -1) {
        player.displayClientMessage(noRobotBound, true);
        return ActionResult.pass(stack);
      }
      openTerminal(stack, player, hand, this);
    }

    return new ActionResult<>(ActionResultType.SUCCESS, stack);
  }

  private void openTerminal(ItemStack stack, PlayerEntity player, @Nonnull Hand hand, RobotControllerItem controller) {
    ServerComputer computer = getServerComputer(stack);
    computer.turnOn();
    ComputerContainerData computerContainerData = new ComputerContainerData(computer);
    computerContainerData.open(player, getContainerProvider(computer, player, hand, controller));
  }

  public ServerComputer getServerComputer(ItemStack stack) {
    return ComputerCraft.serverComputerRegistry.get(getComputerID(stack));
  }

  public INamedContainerProvider getContainerProvider(ServerComputer computer,
                                                      PlayerEntity player,
                                                      @Nonnull Hand hand,
                                                      RobotControllerItem controller) {
    return new RobotContainer.Factory(computer, player.getItemInHand(hand), controller, hand);
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
