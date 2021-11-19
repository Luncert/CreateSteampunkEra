package com.luncert.steampunkera.content.core.robot.cc;

import dan200.computercraft.shared.computer.core.ComputerFamily;

public class ComputerData {

  static final String NBT_ID = "ComputerId";
  static final String NBT_LABEL = "Label";
  static final String NBT_ON = "On";

  final ComputerFamily family;

  int instanceID = -1;
  int computerID = -1;
  public String label = null;

  boolean on = false;
  boolean startOn = false;
  boolean fresh = false;

  ComputerData(ComputerFamily family) {
    this.family = family;
  }

  public static ComputerData of(ComputerFamily family) {
    return new ComputerData(family);
  }
}
