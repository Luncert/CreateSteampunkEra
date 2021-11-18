package com.luncert.steampunkera.content.core.robot.cc.command;

import com.luncert.steampunkera.content.core.robot.cc.IRobotAccess;
import com.luncert.steampunkera.content.core.robot.cc.IRobotCommand;
import com.luncert.steampunkera.content.core.robot.cc.RobotCommandResult;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class ForwardCommand implements IRobotCommand {

  private final int distance;
  private int executionTime;
  private int startPos;

  public ForwardCommand(int distance) {
    this.distance = distance;
  }

  @Override
  public RobotCommandResult execute(@Nonnull IRobotAccess robotAccess) {
    if (!robotAccess.isAssembled()) {
      return RobotCommandResult.failure("assemble robot before movement");
    }

    if (distance <= 0) {
      return RobotCommandResult.success(0);
    }

    if (executionTime++ == 0) {
      BlockPos pos = robotAccess.getPosition();
      startPos = pos.get(robotAccess.getDirection().getAxis());
      robotAccess.forward(distance);
    }

    if (robotAccess.isMoving()) {
      return RobotCommandResult.executing();
    } else {
      BlockPos pos = robotAccess.getPosition();
      int endPos = pos.get(robotAccess.getDirection().getAxis());
      int movedDist = Math.abs(endPos - startPos);
      return RobotCommandResult.success(movedDist);
    }
  }
}
