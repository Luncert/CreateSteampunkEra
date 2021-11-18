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

// TODO sync computer data from tile entity
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ComputerEntityBase extends Entity implements IComputerContainer {
  private static final String NBT_ID = "ComputerId";
  private static final String NBT_LABEL = "Label";
  private static final String NBT_ON = "On";
  private int instanceID = -1;
  private int computerID = -1;
  protected String label = null;
  private boolean on = false;
  boolean startOn = false;
  private boolean fresh = false;
  private final NonNullConsumer<LazyOptional<IPeripheral>>[] invalidate;
  private final ComputerFamily family;

  @SuppressWarnings("unchecked")
  public ComputerEntityBase(EntityType<?> entityType, World world, ComputerFamily family) {
    super(entityType, world);
    this.family = family;

    this.invalidate = new NonNullConsumer[6];

    for (Direction direction : Direction.values()) {
      invalidate[direction.ordinal()] = (o) -> this.updateInput(direction);
    }
  }

  protected void unload() {
    if (this.instanceID >= 0) {
      if (!this.level.isClientSide) {
        ComputerCraft.serverComputerRegistry.remove(this.instanceID);
      }

      this.instanceID = -1;
    }

  }

  public void destroy() {
    this.unload();
    BlockPos pos = this.blockPosition();
    for (Direction facing : DirectionUtil.FACINGS) {
      RedstoneUtil.propagateRedstoneOutput(level, pos, facing);
    }
  }

  public void onChunkUnloaded() {
    this.unload();
  }

  public void setRemoved() {
    this.unload();
  }

  @Override
  public void scheduleUpdateComputer(boolean startOn) {
    this.startOn = startOn;
  }

  @Override
  public void tick() {
    if (!this.level.isClientSide) {
      ServerComputer computer = createServerComputer();
      if (computer == null) {
        return;
      }

      if (this.startOn || this.fresh && this.on) {
        computer.turnOn();
        this.startOn = false;
      }

      computer.keepAlive();
      this.fresh = false;
      this.computerID = computer.getID();
      this.label = computer.getLabel();
      this.on = computer.isOn();
      if (computer.hasOutputChanged()) {
        this.updateOutput();
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
    return this.computerID;
  }

  public final String getLabel() {
    return this.label;
  }

  public final void setComputerID(int id) {
    if (!this.level.isClientSide && this.computerID != id) {
      this.computerID = id;
      getServerComputer().ifPresent(computer -> computer.setID(id));
    }
  }

  public final void setLabel(String label) {
    if (!this.level.isClientSide && !Objects.equals(this.label, label)) {
      this.label = label;
      getServerComputer().ifPresent(computer -> computer.setLabel(label));
    }
  }

  public ComputerFamily getFamily() {
    return family;
  }

  @Nullable
  public ServerComputer createServerComputer() {
    if (this.level.isClientSide) {
      return null;
    } else {
      boolean changed = false;
      if (this.instanceID < 0) {
        this.instanceID = ComputerCraft.serverComputerRegistry.getUnusedInstanceID();
        changed = true;
      }

      if (!ComputerCraft.serverComputerRegistry.contains(this.instanceID)) {
        ServerComputer computer = this.createComputer(this.instanceID, this.computerID);
        ComputerCraft.serverComputerRegistry.add(this.instanceID, computer);
        this.fresh = true;
        changed = true;
      }

      if (changed) {
        this.updateInput();
      }

      return ComputerCraft.serverComputerRegistry.get(this.instanceID);
    }
  }

  public Optional<ServerComputer> getServerComputer() {
    return this.level.isClientSide ? Optional.empty() : Optional.ofNullable(ComputerCraft.serverComputerRegistry.get(this.instanceID));
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
    this.computerID = nbt.contains(NBT_ID) ? nbt.getInt(NBT_ID) : -1;
    this.label = nbt.contains(NBT_LABEL) ? nbt.getString(NBT_LABEL) : null;
    this.on = this.startOn = nbt.getBoolean(NBT_ON);
  }

  @Override
  protected void addAdditionalSaveData(CompoundNBT nbt) {
    if (this.computerID >= 0) {
      nbt.putInt(NBT_ID, this.computerID);
    }

    if (this.label != null) {
      nbt.putString(NBT_LABEL, this.label);
    }

    nbt.putBoolean(NBT_ON, this.on);
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
