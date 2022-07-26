package com.luncert.steampunkera.content.core.observerfunnel;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.item.ItemStack;

public class ObserverFunnelBehaviour extends TileEntityBehaviour {

  public static BehaviourType<FilteringBehaviour> TYPE = new BehaviourType<>();

  private ItemStack prevItem;

  public ObserverFunnelBehaviour(SmartTileEntity te) {
    super(te);
    prevItem = ItemStack.EMPTY;
  }

  @Override
  public void tick() {
    super.tick();

    if (getWorld().isClientSide) {

    } else {
    }
  }

  @Override
  public BehaviourType<?> getType() {
    return TYPE;
  }
}
