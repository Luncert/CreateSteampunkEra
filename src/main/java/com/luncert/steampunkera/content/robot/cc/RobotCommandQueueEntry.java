package com.luncert.steampunkera.content.robot.cc;

import com.luncert.steampunkera.content.robot.cc.IRobotCommand;

public class RobotCommandQueueEntry {

  public final int callbackID;
  public final IRobotCommand command;

  public RobotCommandQueueEntry(int callbackID, IRobotCommand command) {
    this.callbackID = callbackID;
    this.command = command;
  }
}
