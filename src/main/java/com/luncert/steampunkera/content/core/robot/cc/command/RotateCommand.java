package com.luncert.steampunkera.content.core.robot.cc.command;

import com.luncert.steampunkera.content.core.robot.cc.IRobotAccess;
import com.luncert.steampunkera.content.core.robot.cc.IRobotCommand;
import com.luncert.steampunkera.content.core.robot.cc.RobotCommandResult;

import javax.annotation.Nonnull;

public class RotateCommand implements IRobotCommand {

  public enum TargetSide {
    Left(-90),
    Right(90),
    Back(180);

    private final int degree;

    TargetSide(int degree) {
      this.degree = degree;
    }
  }

  private final TargetSide targetSide;
  private int executionTime;

  public RotateCommand(TargetSide targetSide) {
    this.targetSide = targetSide;
  }

  @Override
  public RobotCommandResult execute(@Nonnull IRobotAccess access) {
    if (!access.isAssembled()) {
      return RobotCommandResult.failure("assemble robot before rotation");
    }

    if (executionTime++ == 0) {
      access.rotate(targetSide.degree);
    }

    if (access.isRotating()) {
      return RobotCommandResult.executing();
    } else {
      return RobotCommandResult.success();
    }
  }
}
