package com.luncert.steampunkera.content.core.robotchargestation;

import com.simibubi.create.foundation.gui.IInteractionChecker;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.utility.Lang;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotChargeStationTileEntity extends SyncedTileEntity
    implements ITickableTileEntity, INamedContainerProvider, IInteractionChecker {

  public RobotChargeStationInventory inventory = new RobotChargeStationInventory();

  public class RobotChargeStationInventory extends ItemStackHandler {
    public RobotChargeStationInventory() {
      super(2);
    }

    @Override
    protected void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      setChanged();
    }
  }

  public RobotChargeStationTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  public void sendToContainer(PacketBuffer buffer) {
    buffer.writeBlockPos(getBlockPos());
    buffer.writeNbt(getUpdateTag());
  }

  @Override
  public void load(BlockState state, CompoundNBT compound) {
    inventory.deserializeNBT(compound.getCompound("Inventory"));
    super.load(state, compound);
  }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
    compound.put("Inventory", inventory.serializeNBT());
    return super.save(compound);
  }

  // container

  @Override
  public ITextComponent getDisplayName() {
    return Lang.translate("gui.robot_charge_station.title");
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    return RobotChargeStationContainer.create(id, inv, this);
  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    if (level == null || level.getBlockEntity(worldPosition) != this) {
      return false;
    }
    return player.distanceToSqr(
        worldPosition.getX() + 0.5D,
        worldPosition.getY() + 0.5D,
        worldPosition.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  public void tick() {

  }
}
