package com.luncert.steampunkera.content.core.robot;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;

import java.util.List;

public class RobotChargeStationTileEntity extends SmartTileEntity {

  private ItemStack robotController = ItemStack.EMPTY;

  public RobotChargeStationTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  @Override
  public void addBehaviours(List<TileEntityBehaviour> behaviours) {
  }

  public void setRobotController(ItemStack robotController) {
    if (robotController.getItem() instanceof RobotControllerItem) {
      this.robotController = robotController;
    }
  }

  public ItemStack getRobotController() {
    return robotController;
  }

  public ItemStack removeRobotController() {
    ItemStack t = robotController;
    robotController = ItemStack.EMPTY;
    return t;
  }
}
