package com.luncert.steampunkera.content.core.robot.cc;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.apis.IAPIEnvironment;

import java.util.Optional;

public class RobotAPI implements ILuaAPI {

  private final IAPIEnvironment environment;
  private final IRobotAccess access;

  public RobotAPI(IAPIEnvironment environment, IRobotAccess access) {
    this.environment = environment;
    this.access = access;
  }
  @Override
  public String[] getNames() {
    return new String[]{"robot"};
  }

  @LuaFunction
  public final MethodResult forward(Optional<Integer> distance) {
    return trackCommand(access -> {
      return RobotCommandResult.failure("unimplemented " + distance);
    });
  }

  // @LuaFunction
  // public final MethodResult moveTo(int x, int y, int z) {
  //
  // }

  private MethodResult trackCommand(IRobotCommand command) {
    // environment.addTrackingChange(TrackingField.TURTLE_OPS);
    return access.executeCommand(command);
  }
}
