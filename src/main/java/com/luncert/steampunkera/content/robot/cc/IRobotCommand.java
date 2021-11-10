package com.luncert.steampunkera.content.robot.cc;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IRobotCommand {

  RobotCommandResult execute(@Nonnull IRobotAccess robotAccess);
}
