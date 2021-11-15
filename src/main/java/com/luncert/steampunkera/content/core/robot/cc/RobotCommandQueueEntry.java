package com.luncert.steampunkera.content.core.robot.cc;

public class RobotCommandQueueEntry {

  public final int callbackID;
  public final IRobotCommand command;

  public RobotCommandQueueEntry(int callbackID, IRobotCommand command) {
    this.callbackID = callbackID;
    this.command = command;
  }
}
