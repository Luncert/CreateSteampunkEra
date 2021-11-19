package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.core.robot.cc.*;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Responses to execute commands parsed from lua scripts.
 */
public class RobotBrain implements IRobotAccess {

  private static final String EVENT_ROBOT_RESPONSE = "robot_response";

  private final Queue<RobotCommandQueueEntry> commandQueue;
  private IComputerContainer owner;
  private ComputerProxy proxy;
  private GameProfile owningPlayer;
  private int commandsIssued;

  public RobotBrain(IComputerContainer robot) {
    this.owner = robot;
    this.commandQueue = new ArrayDeque<>();
  }

  public void setOwner(IComputerContainer owner) {
    this.owner = owner;
  }

  public IComputerContainer getOwner() {
    return owner;
  }

  public ComputerProxy getProxy() {
    if (proxy == null) {
      proxy = new ComputerProxy(this.owner);
    }

    return proxy;
  }

  public ComputerFamily getFamily() {
    return owner.getFamily();
  }

  public void setOwningPlayer(GameProfile profile) {
    owningPlayer = profile;
  }

  public void setupComputer(ServerComputer computer) {
    // update peripheral
  }

  public void readFromNBT(CompoundNBT nbt) {
    this.readCommon(nbt);
    if (nbt.contains("Owner", 10)) {
      CompoundNBT owner = nbt.getCompound("Owner");
      UUID id = new UUID(owner.getLong("UpperId"), owner.getLong("LowerId"));
      this.owningPlayer = new GameProfile(id, owner.getString("Name"));
    } else {
      this.owningPlayer = null;
    }
  }

  public CompoundNBT writeToNBT(CompoundNBT nbt) {
    this.writeCommon(nbt);
    if (this.owningPlayer != null) {
      CompoundNBT owner = new CompoundNBT();
      nbt.put("Owner", owner);
      owner.putLong("UpperId", this.owningPlayer.getId().getMostSignificantBits());
      owner.putLong("LowerId", this.owningPlayer.getId().getLeastSignificantBits());
      owner.putString("Name", this.owningPlayer.getName());
    }

    return nbt;
  }

  public void readDescription(CompoundNBT nbt) {
    this.readCommon(nbt);
  }

  public void writeDescription(CompoundNBT nbt) {
    this.writeCommon(nbt);
  }

  private void readCommon(CompoundNBT nbt) {
  }

  private void writeCommon(CompoundNBT nbt) {
  }

  // robot api

  @Nonnull
  public World getWorld() {
    return this.owner.getLevel();
  }

  @Nonnull
  public BlockPos getPosition() {
    return this.owner.getBlockPos();
  }

  @Nonnull
  public Direction getDirection() {
    return this.owner.getDirection();
  }

  @Nullable
  @Override
  public GameProfile getOwningPlayer() {
    return owningPlayer;
  }

  @Override
  public boolean isAssembled() {
    return owner.isAssembled();
  }

  @Override
  public void assemble(boolean assembleStructure) {
    owner.assemble(assembleStructure);
  }

  @Override
  public void dissemble() {
    owner.dissemble();
  }

  @Override
  public boolean isMoving() {
    return owner.isMoving();
  }

  @Override
  public void forward(int n) {
    owner.forward(n);
  }

  @Override
  public boolean isRotating() {
    return owner.isRotating();
  }

  @Override
  public void rotate(int degree) {
    owner.rotate(degree);
  }

  @Nonnull
  public MethodResult executeCommand(@Nonnull IRobotCommand command) {
    if (getWorld().isClientSide) {
      throw new UnsupportedOperationException("Cannot run commands on the client");
    } else if (commandQueue.size() > 16) {
      return MethodResult.of(false, "Too many ongoing robot commands");
    } else {
      commandQueue.offer(new RobotCommandQueueEntry(++commandsIssued, command));
      return RobotCommandCallback.hook(commandsIssued, EVENT_ROBOT_RESPONSE);
    }
  }

  // update

  public void update() {
    World world = this.getWorld();
    if (!world.isClientSide) {
      this.updateCommands();
      if (this.owner.isRemoved()) {
        return;
      }
    }
  }

  private void updateCommands() {
    if (commandQueue.isEmpty()) {
      return;
    }

    ServerComputer computer = owner.getServerComputer().orElse(null);

    if (computer == null || computer.getComputer().getMainThreadMonitor().canWork()) {
      RobotCommandQueueEntry task = commandQueue.peek();
      if (task == null) {
        commandQueue.remove();
        return;
      }

      long start = System.nanoTime();
      RobotCommandResult result = task.command.execute(this);
      long end = System.nanoTime();

      if (computer != null) {
        computer.getComputer().getMainThreadMonitor().trackWork(end - start, TimeUnit.NANOSECONDS);

        int callbackID = task.callbackID;
        if (callbackID >= 0) {
          Object[] evtArgs;
          if (RobotCommandResult.isSuccess(result)) {
            Object[] results = result.getResults();
            if (results != null) {
              evtArgs = new Object[results.length + 2];
              evtArgs[0] = callbackID;
              evtArgs[1] = true;
              System.arraycopy(results, 0, evtArgs, 2, results.length);
            } else {
              evtArgs = new Object[]{callbackID, true};
            }
          } else if (RobotCommandResult.isExecuting(result)) {
            return;
          } else {
            evtArgs = new Object[]{callbackID, false, result != null ? result.getErrorMessage() : null};
          }

          commandQueue.remove();
          computer.queueEvent(EVENT_ROBOT_RESPONSE, evtArgs);
        }
      }
    }
  }
}
