package com.luncert.steampunkera.content.core.robot.cc;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.BundledRedstone;
import dan200.computercraft.shared.Peripherals;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.computer.blocks.IComputerTile;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.RedstoneUtil;
import joptsimple.internal.Strings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public abstract class ComputerTileBase extends TileGeneric implements IComputerTile,
    ITickableTileEntity, INameable, IComputerContainer {

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
  public ComputerTileBase(TileEntityType<? extends TileGeneric> type, ComputerFamily family) {
    super(type);
    this.family = family;
    NonNullConsumer<LazyOptional<IPeripheral>>[] invalidate = this.invalidate = new NonNullConsumer[6];
    for (Direction direction : Direction.values()) {
      invalidate[direction.ordinal()] = (o) -> this.updateInput(direction);
    }
  }

  protected void unload() {
    if (this.instanceID >= 0) {
      if (!this.getLevel().isClientSide) {
        ComputerCraft.serverComputerRegistry.remove(this.instanceID);
      }

      this.instanceID = -1;
    }

  }

  @Override
  public void destroy() {
    this.unload();
    for (Direction facing : DirectionUtil.FACINGS) {
      RedstoneUtil.propagateRedstoneOutput(this.getLevel(), this.getBlockPos(), facing);
    }
  }

  @Override
  public void onChunkUnloaded() {
    this.unload();
  }

  @Override
  public void setRemoved() {
    this.unload();
    super.setRemoved();
  }

  protected boolean canNameWithTag(PlayerEntity player) {
    return false;
  }

  @Nonnull
  public ActionResultType onActivate(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    ItemStack currentItem = player.getItemInHand(hand);
    if (!currentItem.isEmpty()
        && currentItem.getItem() == Items.NAME_TAG
        && this.canNameWithTag(player)
        && currentItem.hasCustomHoverName()) {
      if (!this.getLevel().isClientSide) {
        this.setLabel(currentItem.getHoverName().getString());
        currentItem.shrink(1);
      }

      return ActionResultType.SUCCESS;
    } else {
      return ActionResultType.PASS;
    }
  }

  @Override
  public void onNeighbourChange(@Nonnull BlockPos neighbour) {
    this.updateInput(neighbour);
  }

  @Override
  public void onNeighbourTileEntityChange(@Nonnull BlockPos neighbour) {
    this.updateInput(neighbour);
  }

  @Override
  public void scheduleUpdateComputer(boolean startOn) {
    this.startOn = startOn;
  }

  @Override
  public void tick() {
    if (!this.getLevel().isClientSide) {
      ServerComputer computer = this.createServerComputer();
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
      this.updateBlockState(computer.getState());
      if (computer.hasOutputChanged()) {
        this.updateOutput();
      }
    }

  }

  protected abstract void updateBlockState(ComputerState var1);

  @Nonnull
  public CompoundNBT save(@Nonnull CompoundNBT nbt) {
    if (this.computerID >= 0) {
      nbt.putInt(NBT_ID, this.computerID);
    }

    if (this.label != null) {
      nbt.putString(NBT_LABEL, this.label);
    }

    nbt.putBoolean(NBT_ON, this.on);
    return super.save(nbt);
  }

  @Override
  public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
    super.load(state, nbt);
    this.computerID = nbt.contains(NBT_ID) ? nbt.getInt(NBT_ID) : -1;
    this.label = nbt.contains(NBT_LABEL) ? nbt.getString(NBT_LABEL) : null;
    this.on = this.startOn = nbt.getBoolean(NBT_ON);
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
    computer.setBundledRedstoneInput(localDir, BundledRedstone.getOutput(this.getLevel(), offset, offsetSide));
    if (!this.isPeripheralBlockedOnSide(localDir)) {
      IPeripheral peripheral = Peripherals.getPeripheral(this.getLevel(), offset, offsetSide, this.invalidate[dir.ordinal()]);
      computer.setPeripheral(localDir, peripheral);
    }

  }

  protected static int getRedstoneInput(World world, BlockPos pos, Direction side) {
    int power = world.getSignal(pos, side);
    if (power >= 15) {
      return power;
    } else {
      BlockState neighbour = world.getBlockState(pos);
      return neighbour.getBlock() == Blocks.REDSTONE_WIRE ? Math.max(power, (Integer)neighbour.getValue(RedstoneWireBlock.POWER)) : power;
    }
  }

  public void updateInput() {
    if (this.getLevel() != null && !this.getLevel().isClientSide) {
      getServerComputer().ifPresent(computer -> {
        BlockPos pos = computer.getPosition();
        for (Direction facing : DirectionUtil.FACINGS) {
          this.updateSideInput(computer, facing, pos.relative(facing));
        }
      });
    }
  }

  private void updateInput(BlockPos neighbour) {
    if (this.getLevel() != null && !this.getLevel().isClientSide) {
      getServerComputer().ifPresent(computer -> {
        for (Direction facing : DirectionUtil.FACINGS) {
          BlockPos offset = this.worldPosition.relative(facing);
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
    if (this.getLevel() != null && !this.getLevel().isClientSide) {
      getServerComputer().ifPresent(computer -> updateSideInput(computer, dir, this.worldPosition.relative(dir)));
    }
  }

  public void updateOutput() {
    this.updateBlock();
    for (Direction facing : DirectionUtil.FACINGS) {
      RedstoneUtil.propagateRedstoneOutput(this.getLevel(), this.getBlockPos(), facing);
    }
  }

  protected abstract ServerComputer createComputer(int instanceID, int computerID);

  @Override
  public final int getComputerID() {
    return this.computerID;
  }

  @Override
  public final String getLabel() {
    return this.label;
  }

  @Override
  public final void setComputerID(int id) {
    if (!this.getLevel().isClientSide && this.computerID != id) {
      this.computerID = id;
      getServerComputer().ifPresent(computer -> computer.setID(id));
      this.setChanged();
    }
  }

  @Override
  public final void setLabel(String label) {
    if (!this.getLevel().isClientSide && !Objects.equals(this.label, label)) {
      this.label = label;
      getServerComputer().ifPresent(computer -> computer.setLabel(label));
      this.setChanged();
    }
  }

  @Override
  public ComputerFamily getFamily() {
    return this.family;
  }

  public ServerComputer createServerComputer() {
    if (this.getLevel().isClientSide) {
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

  @Override
  public Optional<ServerComputer> getServerComputer() {
    return this.getLevel().isClientSide ? Optional.empty()
        : Optional.ofNullable(ComputerCraft.serverComputerRegistry.get(this.instanceID));
  }

  protected void writeDescription(@Nonnull CompoundNBT nbt) {
    super.writeDescription(nbt);
    if (this.label != null) {
      nbt.putString(NBT_LABEL, this.label);
    }

    if (this.computerID >= 0) {
      nbt.putInt(NBT_ID, this.computerID);
    }
  }

  protected void readDescription(@Nonnull CompoundNBT nbt) {
    super.readDescription(nbt);
    this.label = nbt.contains(NBT_LABEL) ? nbt.getString(NBT_LABEL) : null;
    this.computerID = nbt.contains(NBT_ID) ? nbt.getInt(NBT_ID) : -1;
  }

  protected void transferStateFrom(ComputerTileBase copy) {
    if (copy.computerID != this.computerID || copy.instanceID != this.instanceID) {
      this.unload();
      this.instanceID = copy.instanceID;
      this.computerID = copy.computerID;
      this.label = copy.label;
      this.on = copy.on;
      this.startOn = copy.startOn;
      this.updateBlock();
    }

    copy.instanceID = -1;
  }

  @Nonnull
  public ITextComponent getName() {
    return this.hasCustomName() ? new StringTextComponent(this.label) : new TranslationTextComponent(this.getBlockState().getBlock().getDescriptionId());
  }

  public boolean hasCustomName() {
    return !Strings.isNullOrEmpty(this.label);
  }

  @Nullable
  public ITextComponent getCustomName() {
    return this.hasCustomName() ? new StringTextComponent(this.label) : null;
  }

  @Nonnull
  public ITextComponent getDisplayName() {
    return this.getName();
  }
}
