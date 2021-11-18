package com.luncert.steampunkera.content.core.robot.cc;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public interface IComputerContainer {

  ComputerFamily getFamily();

  boolean isRemoved();

  World getLevel();

  BlockPos getBlockPos();

  Direction getDirection();

  int getComputerID();

  String getLabel();

  Optional<ServerComputer> getServerComputer();

  ServerComputer createServerComputer();

  void scheduleUpdateComputer(boolean startOn);

  boolean stillValid(PlayerEntity player);

  boolean isAssembled();

  default void assemble(boolean assembleStructure) {}

  default void dissemble() {}

  default boolean isMoving() {
    return false;
  }

  default void forward(int n) {}
}
