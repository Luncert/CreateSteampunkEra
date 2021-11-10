package com.luncert.steampunkera.content.robot;

import com.luncert.steampunkera.content.robot.cc.*;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Responses to execute commands parsed from lua scripts.
 */
public class RobotBrain implements IRobotAccess {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String EVENT_ROBOT_RESPONSE = "robot_response";

  private final Queue<RobotCommandQueueEntry> commandQueue;
  private final Map<TurtleSide, IPeripheral> peripherals;
  private IComputerContainer owner;
  private ComputerProxy proxy;
  private GameProfile owningPlayer;
  private int commandsIssued;

  public RobotBrain(IComputerContainer robot) {
    this.owner = robot;
    this.commandQueue = new ArrayDeque<>();
    this.peripherals = new EnumMap<>(TurtleSide.class);
  }

  public void setOwner(IComputerContainer owner) {
    this.owner = owner;
  }

  public IComputerContainer getOwner() {
    return this.owner;
  }

  public ComputerProxy getProxy() {
    if (this.proxy == null) {
      this.proxy = new ComputerProxy(this.owner);
    }

    return this.proxy;
  }

  public ComputerFamily getFamily() {
    return this.owner.getFamily();
  }

  public void setupComputer(ServerComputer computer) {
    this.updatePeripherals(computer);
  }

  public void update() {
    World world = this.getWorld();
    if (!world.isClientSide) {
      this.updateCommands();
      if (this.owner.isRemoved()) {
        return;
      }
    }
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
    return null;
  }

  @Nonnull
  @Override
  public IInventory getInventory() {
    return null;
  }

  @Override
  public MethodResult assemble() {
    return null;
  }

  @Override
  public MethodResult dissemble() {
    return null;
  }

  @Nonnull
  public MethodResult executeCommand(@Nonnull IRobotCommand command) {
    LOGGER.info(command);
    if (this.getWorld().isClientSide) {
      throw new UnsupportedOperationException("Cannot run commands on the client");
    } else if (this.commandQueue.size() > 16) {
      return MethodResult.of(false, "Too many ongoing robot commands");
    } else {
      this.commandQueue.offer(new RobotCommandQueueEntry(++this.commandsIssued, command));
      int commandID = this.commandsIssued;
      return (new CommandCallback(commandID)).pull;
    }
  }

  private static final class CommandCallback implements ILuaCallback {
    final MethodResult pull = MethodResult.pullEvent("turtle_response", this);
    private final int command;

    CommandCallback(int command) {
      this.command = command;
    }

    @Nonnull
    public MethodResult resume(Object[] response) {
      if (response.length >= 3 && response[1] instanceof Number && response[2] instanceof Boolean) {
        return ((Number)response[1]).intValue() != this.command ?
            this.pull : MethodResult.of(Arrays.copyOfRange(response, 2, response.length));
      } else {
        return this.pull;
      }
    }
  }

  // data

  private void readCommon(CompoundNBT nbt) {
  }

  private void writeCommon(CompoundNBT nbt) {
  }

  private void updateCommands() {
    if (!this.commandQueue.isEmpty()) {
      ServerComputer computer = this.owner.getServerComputer().orElse(null);

      if (computer == null || computer.getComputer().getMainThreadMonitor().canWork()) {
        RobotCommandQueueEntry task = this.commandQueue.poll();

        if (task != null) {
          long start = System.nanoTime();
          RobotCommandResult result = task.command.execute(this);
          long end = System.nanoTime();

          if (computer != null) {
            computer.getComputer().getMainThreadMonitor()
                .trackWork(end - start, TimeUnit.NANOSECONDS);

            int callbackID = task.callbackID;
            if (callbackID < 0) {
              return;
            }

            if (result != null && result.isSuccess()) {
              Object[] results = result.getResults();
              if (results != null) {
                Object[] arguments = new Object[results.length + 2];
                arguments[0] = callbackID;
                arguments[1] = true;
                System.arraycopy(results, 0, arguments, 2, results.length);
                computer.queueEvent(EVENT_ROBOT_RESPONSE, arguments);
              } else {
                computer.queueEvent(EVENT_ROBOT_RESPONSE, new Object[]{callbackID, true});
              }
            } else {
              computer.queueEvent(EVENT_ROBOT_RESPONSE,
                  new Object[]{callbackID, false, result != null ? result.getErrorMessage() : null});
            }
          }
        }
      }
    }
  }

  private void updatePeripherals(ServerComputer serverComputer) {
    // TODO
  }
}
