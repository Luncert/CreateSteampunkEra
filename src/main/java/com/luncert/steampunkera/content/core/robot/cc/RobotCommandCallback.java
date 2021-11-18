package com.luncert.steampunkera.content.core.robot.cc;

import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.MethodResult;

import javax.annotation.Nonnull;
import java.util.Arrays;

public final class RobotCommandCallback implements ILuaCallback {

  private final int command;
  private final MethodResult callbackHook;

  public static MethodResult hook(int commandID, String respEventName) {
    return new RobotCommandCallback(commandID, respEventName).callbackHook;
  }

  private RobotCommandCallback(int command, String respEventName) {
    this.command = command;
    callbackHook = MethodResult.pullEvent(respEventName, this);
  }

  @Nonnull
  public MethodResult resume(Object[] response) {
    if (response.length >= 3 && response[1] instanceof Number && response[2] instanceof Boolean) {
      return ((Number) response[1]).intValue() != this.command ?
          callbackHook : MethodResult.of(Arrays.copyOfRange(response, 2, response.length));
    } else {
      return callbackHook;
    }
  }
}
