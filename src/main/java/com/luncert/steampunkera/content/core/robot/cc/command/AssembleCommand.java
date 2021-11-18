package com.luncert.steampunkera.content.core.robot.cc.command;

import com.luncert.steampunkera.content.core.robot.cc.IRobotAccess;
import com.luncert.steampunkera.content.core.robot.cc.IRobotCommand;
import com.luncert.steampunkera.content.core.robot.cc.RobotCommandResult;

import javax.annotation.Nonnull;

public class AssembleCommand implements IRobotCommand {

  private int executionTime;
  private final boolean assembleStructure;

  public AssembleCommand(boolean assembleStructure) {
    this.assembleStructure = assembleStructure;
  }

  @Override
  public RobotCommandResult execute(@Nonnull IRobotAccess robotAccess) {
    if (executionTime++ == 0) {
      robotAccess.assemble(assembleStructure);
    } else if (executionTime > 20) {
      return RobotCommandResult.failure("timeout while assembling robot");
    }

    if (robotAccess.isAssembled()) {
      return RobotCommandResult.success();
    }

    return RobotCommandResult.executing();
  }
}
