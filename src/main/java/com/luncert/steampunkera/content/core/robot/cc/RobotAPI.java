package com.luncert.steampunkera.content.core.robot.cc;

import com.luncert.steampunkera.content.core.robot.cc.command.DissembleCommand;
import com.luncert.steampunkera.content.core.robot.cc.command.AssembleCommand;
import com.luncert.steampunkera.content.core.robot.cc.command.ForwardCommand;
import com.luncert.steampunkera.content.core.robot.cc.command.RotateCommand;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.apis.IAPIEnvironment;

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
  public final MethodResult isAssembled() {
    return MethodResult.of(access.isAssembled());
  }

  @LuaFunction
  public final MethodResult assemble(boolean assembleStructure) {
    return access.executeCommand(new AssembleCommand(assembleStructure));
  }

  @LuaFunction
  public final MethodResult dissemble() {
    return access.executeCommand(new DissembleCommand());
  }

  @LuaFunction
  public final MethodResult isMoving() {
    return MethodResult.of(access.isMoving());
  }

  @LuaFunction
  public final MethodResult forward(int distance) {
    return access.executeCommand(new ForwardCommand(distance));
  }

  @LuaFunction
  public final MethodResult isRotating() {
    return MethodResult.of(access.isRotating());
  }

  @LuaFunction
  public final MethodResult turnLeft() {
    return access.executeCommand(new RotateCommand(RotateCommand.TargetSide.Left));
  }

  @LuaFunction
  public final MethodResult turnRight() {
    return access.executeCommand(new RotateCommand(RotateCommand.TargetSide.Right));
  }

  @LuaFunction
  public final MethodResult turnBack() {
    return access.executeCommand(new RotateCommand(RotateCommand.TargetSide.Back));
  }
}
