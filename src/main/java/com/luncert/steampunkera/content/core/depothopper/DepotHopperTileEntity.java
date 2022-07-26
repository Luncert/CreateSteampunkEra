package com.luncert.steampunkera.content.core.depothopper;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.tileentity.TileEntityType;

import java.util.List;

public class DepotHopperTileEntity extends SmartTileEntity {

  public DepotHopperTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  @Override
  public void addBehaviours(List<TileEntityBehaviour> list) {
    list.add(new DepotHopperBehaviour(this));
  }
}
