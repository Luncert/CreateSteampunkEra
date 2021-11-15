package com.luncert.steampunkera.content.core.robot.cc;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRobotAccess {

  @Nonnull
  World getWorld();

  @Nonnull
  BlockPos getPosition();

  @Nonnull
  Direction getDirection();

  @Nullable
  GameProfile getOwningPlayer();

  @Nonnull
  IInventory getInventory();

  MethodResult assemble();

  MethodResult dissemble();

  @Nonnull
  MethodResult executeCommand(@Nonnull IRobotCommand command);
}
