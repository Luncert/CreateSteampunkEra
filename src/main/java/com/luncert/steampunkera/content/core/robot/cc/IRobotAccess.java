package com.luncert.steampunkera.content.core.robot.cc;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Proxy of RobotBrain.
 */
public interface IRobotAccess {

  World getWorld();

  BlockPos getPosition();

  Direction getDirection();

  GameProfile getOwningPlayer();

  MethodResult executeCommand(@Nonnull IRobotCommand command);

  boolean isAssembled();

  void assemble(boolean assembleStructure);

  void dissemble();

  boolean isMoving();

  void forward(int n);

  boolean isRotating();

  void rotate(int degree);

  // void moveTo(int x, int y, int z);
}
