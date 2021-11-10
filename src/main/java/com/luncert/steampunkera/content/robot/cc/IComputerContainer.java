package com.luncert.steampunkera.content.robot.cc;

import com.luncert.steampunkera.content.robot.RobotContainer;
import com.luncert.steampunkera.content.robot.RobotControllerItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface IComputerContainer {

  ComputerFamily getFamily();

  boolean isRemoved();

  World getLevel();

  BlockPos getBlockPos();

  Direction getDirection();

  Optional<ServerComputer> getServerComputer();

  default void openTerminal(PlayerEntity player, @Nonnull Hand hand, RobotControllerItem controller) {
    getServerComputer().ifPresent(computer -> {
      if (!getLevel().isClientSide) {
        computer.turnOn();
        ComputerContainerData computerContainerData = new ComputerContainerData(computer);
        computerContainerData.open(player, getContainerProvider(computer, player, hand, controller));
      }
    });
  }

  INamedContainerProvider getContainerProvider(ServerComputer computer,
                                               PlayerEntity player,
                                               @Nonnull Hand hand,
                                               RobotControllerItem controller);

  int getComputerID();

  String getLabel();

  void scheduleUpdateComputer(boolean startOn);
}
