package com.luncert.steampunkera.content.core.robot.cc;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.BundledRedstone;
import dan200.computercraft.shared.Peripherals;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.RedstoneUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

import static com.luncert.steampunkera.content.core.robot.cc.ComputerData.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ComputerEntityBase extends Entity implements IComputerContainer {

  protected ComputerData data;

  private final NonNullConsumer<LazyOptional<IPeripheral>>[] invalidate;

  @SuppressWarnings("unchecked")
  public ComputerEntityBase(EntityType<?> entityType, World world, ComputerData data) {
    super(entityType, world);

    this.data = data;

    this.invalidate = new NonNullConsumer[6];

    for (Direction direction : Direction.values()) {
      invalidate[direction.ordinal()] = (o) -> this.updateInput(direction);
    }
  }

  // protected void unload() {
  //   if (data.instanceID >= 0) {
  //     if (!this.level.isClientSide) {
  //       ComputerCraft.serverComputerRegistry.remove(data.instanceID);
  //     }
  //
  //     data.instanceID = -1;
  //   }
  //
  // }
  //
  // public void destroy() {
  //   this.unload();
  //   BlockPos pos = this.blockPosition();
  //   for (Direction facing : DirectionUtil.FACINGS) {
  //     RedstoneUtil.propagateRedstoneOutput(level, pos, facing);
  //   }
  // }
  //
  // public void onChunkUnloaded() {
  //   this.unload();
  // }
  //
  // public void setRemoved() {
  //   this.unload();
  // }

  @Override
  public void scheduleUpdateComputer(boolean startOn) {
    data.startOn = startOn;
  }

  @Override
  public void tick() {
    if (!this.level.isClientSide) {
      ServerComputer computer = createServerComputer();
      if (computer == null) {
        return;
      }

      if (data.startOn || data.fresh && data.on) {
        computer.turnOn();
        data.startOn = false;
      }

      computer.keepAlive();
      data.fresh = false;
      data.computerID = computer.getID();
      data.label = computer.getLabel();
      data.on = computer.isOn();
      if (computer.hasOutputChanged()) {
        updateOutput();
      }
    }
  }

  protected boolean isPeripheralBlockedOnSide(ComputerSide localSide) {
    return false;
  }

  protected ComputerSide remapToLocalSide(Direction globalSide) {
    return this.remapLocalSide(DirectionUtil.toLocal(this.getDirection(), globalSide));
  }

  protected ComputerSide remapLocalSide(ComputerSide localSide) {
    return localSide;
  }

  private void updateSideInput(ServerComputer computer, Direction dir, BlockPos offset) {
    Direction offsetSide = dir.getOpposite();
    ComputerSide localDir = this.remapToLocalSide(dir);
    computer.setRedstoneInput(localDir, getRedstoneInput(this.level, offset, dir));
    computer.setBundledRedstoneInput(localDir, BundledRedstone.getOutput(this.level, offset, offsetSide));
    if (!this.isPeripheralBlockedOnSide(localDir)) {
      IPeripheral peripheral = Peripherals.getPeripheral(this.level, offset, offsetSide, this.invalidate[dir.ordinal()]);
      computer.setPeripheral(localDir, peripheral);
    }
  }

  protected static int getRedstoneInput(World world, BlockPos pos, Direction side) {
    int power = world.getSignal(pos, side);
    if (power >= 15) {
      return power;
    } else {
      BlockState neighbour = world.getBlockState(pos);
      return neighbour.getBlock() == Blocks.REDSTONE_WIRE ? Math.max(power, neighbour.getValue(RedstoneWireBlock.POWER)) : power;
    }
  }

  public void updateInput() {
    if (!this.level.isClientSide) {
      getServerComputer().ifPresent(computer -> {
        BlockPos pos = computer.getPosition();
        for (Direction facing : DirectionUtil.FACINGS) {
          this.updateSideInput(computer, facing, pos.relative(facing));
        }
      });
    }
  }

  private void updateInput(BlockPos neighbour) {
    if (!this.level.isClientSide) {
      getServerComputer().ifPresent(computer -> {
        for (Direction facing : DirectionUtil.FACINGS) {
          BlockPos offset = this.blockPosition().relative(facing);
          if (offset.equals(neighbour)) {
            this.updateSideInput(computer, facing, offset);
            return;
          }
        }

        this.updateInput();
      });
    }
  }

  private void updateInput(Direction dir) {
    if (!this.level.isClientSide) {
      getServerComputer().ifPresent(computer -> updateSideInput(computer, dir, blockPosition().relative(dir)));
    }
  }

  public void updateOutput() {
    for (Direction facing : DirectionUtil.FACINGS) {
      RedstoneUtil.propagateRedstoneOutput(this.level, this.blockPosition(), facing);
    }
  }

  protected abstract ServerComputer createComputer(int instanceID, int computerID);

  public final int getComputerID() {
    return data.computerID;
  }

  public final String getLabel() {
    return data.label;
  }

  public final void setComputerID(int id) {
    if (!this.level.isClientSide && data.computerID != id) {
      data.computerID = id;
      getServerComputer().ifPresent(computer -> computer.setID(id));
    }
  }

  public final void setLabel(String label) {
    if (!this.level.isClientSide && !Objects.equals(data.label, label)) {
      data.label = label;
      getServerComputer().ifPresent(computer -> computer.setLabel(label));
    }
  }

  public ComputerFamily getFamily() {
    return data.family;
  }

  @Nullable
  public ServerComputer createServerComputer() {
    if (level.isClientSide) {
      return null;
    }

    boolean changed = false;
    if (data.instanceID < 0) {
      data.instanceID = ComputerCraft.serverComputerRegistry.getUnusedInstanceID();
      changed = true;
    }

    if (!ComputerCraft.serverComputerRegistry.contains(data.instanceID)) {
      ServerComputer computer = this.createComputer(data.instanceID, data.computerID);
      ComputerCraft.serverComputerRegistry.add(data.instanceID, computer);
      data.fresh = true;
      changed = true;
    }

    if (changed) {
      this.updateInput();
    }

    return ComputerCraft.serverComputerRegistry.get(data.instanceID);
  }

  public Optional<ServerComputer> getServerComputer() {
    return this.level.isClientSide ? Optional.empty()
        : Optional.ofNullable(ComputerCraft.serverComputerRegistry.get(data.instanceID));
  }

  public double getInteractRange(PlayerEntity player) {
    return 8.0D;
  }

  public boolean isUsable(PlayerEntity player, boolean ignoreRange) {
    if (player.isAlive()) {
      if (ignoreRange) {
        return true;
      } else {
        double range = this.getInteractRange(player);
        BlockPos pos = this.blockPosition();
        return player.getCommandSenderWorld() == this.level
            && player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= range * range;
      }
    } else {
      return false;
    }
  }

  @Override
  protected void defineSynchedData() {
  }

  @Override
  protected void readAdditionalSaveData(CompoundNBT nbt) {
    data.computerID = nbt.contains(NBT_ID) ? nbt.getInt(NBT_ID) : -1;
    data.label = nbt.contains(NBT_LABEL) ? nbt.getString(NBT_LABEL) : null;
    data.on = data.startOn = nbt.getBoolean(NBT_ON);
  }

  @Override
  protected void addAdditionalSaveData(CompoundNBT nbt) {
    if (data.computerID >= 0) {
      nbt.putInt(NBT_ID, data.computerID);
    }

    if (data.label != null) {
      nbt.putString(NBT_LABEL, data.label);
    }

    nbt.putBoolean(NBT_ON, data.on);
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
