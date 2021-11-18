package com.luncert.steampunkera.content.core.robot.cc.command;

import com.luncert.steampunkera.content.core.robot.cc.IRobotAccess;
import com.luncert.steampunkera.content.core.robot.cc.IRobotCommand;
import com.luncert.steampunkera.content.core.robot.cc.RobotCommandResult;

import javax.annotation.Nonnull;

public class RotateCommand implements IRobotCommand {

  public enum TargetSide {
    Left,
    Right,
    Back
  }

  private final TargetSide targetSide;

  public RotateCommand(TargetSide targetSide) {
    this.targetSide = targetSide;
  }

  @Override
  public RobotCommandResult execute(@Nonnull IRobotAccess robotAccess) {
    return null;
  }
}
