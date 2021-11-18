package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.index.ModContainerTypes;
import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputerBase;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

import java.util.function.Predicate;

/**
 * Used to display computer gui.
 */
public class RobotContainer extends ContainerComputerBase {

  private RobotContainer(ContainerType<RobotContainer> type, int id, Predicate<PlayerEntity> canUse, IComputer computer, ComputerFamily family) {
    super(type, id, canUse, computer, family);
  }

  public static RobotContainer create(int id, RobotBrain robot) {
    return new RobotContainer(ModContainerTypes.ROBOT.get(), id, p -> robot.getOwner().stillValid(p),
        robot.getOwner().createServerComputer(),
        robot.getFamily());
  }

  public static RobotContainer create(ContainerType<RobotContainer> type, int id, PlayerInventory inventory, PacketBuffer buffer) {
    ComputerContainerData data = new ComputerContainerData(buffer);
    return new RobotContainer(type, id, x -> true, getComputer(inventory, data), data.getFamily());
  }
}
