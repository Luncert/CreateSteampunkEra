package com.luncert.steampunkera.content.core.robot.cc.command;

import com.luncert.steampunkera.content.core.robot.cc.IRobotAccess;
import com.luncert.steampunkera.content.core.robot.cc.IRobotCommand;
import com.luncert.steampunkera.content.core.robot.cc.RobotCommandResult;

import javax.annotation.Nonnull;

public class DissembleCommand implements IRobotCommand {

  private int executionTime;

  @Override
  public RobotCommandResult execute(@Nonnull IRobotAccess robotAccess) {
    if (executionTime++ == 0) {
      robotAccess.dissemble();
    } else if (executionTime > 20) {
      return RobotCommandResult.failure("timeout while assembling robot");
    }

    if (!robotAccess.isAssembled()) {
      return RobotCommandResult.success();
    }

    return RobotCommandResult.executing();
  }
}
