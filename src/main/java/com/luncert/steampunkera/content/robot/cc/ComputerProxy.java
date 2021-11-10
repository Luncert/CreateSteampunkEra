package com.luncert.steampunkera.content.robot.cc;

import dan200.computercraft.shared.computer.core.ServerComputer;

import java.util.Optional;

public class ComputerProxy {

  private final IComputerContainer robot;

  public ComputerProxy(IComputerContainer robot) {
    this.robot = robot;
  }

  public void turnOn() {
    Optional<ServerComputer> opt = robot.getServerComputer();
    if (opt.isPresent()) {
      opt.get().turnOn();
    } else {
      robot.scheduleUpdateComputer(true);
    }
  }

  public void shutdown() {
    Optional<ServerComputer> opt = robot.getServerComputer();
    if (opt.isPresent()) {
      opt.get().shutdown();
    } else {
      robot.scheduleUpdateComputer(false);
    }
  }

  public void reboot() {
    Optional<ServerComputer> opt = robot.getServerComputer();
    if (opt.isPresent()) {
      opt.get().reboot();
    } else {
      robot.scheduleUpdateComputer(true);
    }
  }

  public int assignID() {
    return robot.getServerComputer().map(ServerComputer::getID).orElse(robot.getComputerID());
  }

  public boolean isOn() {
    return robot.getServerComputer().map(ServerComputer::isOn).orElse(false);
  }

  public String getLabel() {
    return robot.getServerComputer().map(ServerComputer::getLabel).orElse(robot.getLabel());
  }
}
