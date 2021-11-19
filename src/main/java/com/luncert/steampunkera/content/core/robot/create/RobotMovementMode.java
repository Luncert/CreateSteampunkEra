package com.luncert.steampunkera.content.core.robot.create;

import com.luncert.steampunkera.content.util.Lang;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.INamedIconOptions;

public enum RobotMovementMode implements INamedIconOptions {

  ROTATE(AllIcons.I_CART_ROTATE),
  ROTATE_PAUSED(AllIcons.I_CART_ROTATE_PAUSED),
  ROTATION_LOCKED(AllIcons.I_CART_ROTATE_LOCKED),
  ;

  private String translationKey;
  private AllIcons icon;

  RobotMovementMode(AllIcons icon) {
    this.icon = icon;
    translationKey = "contraptions.robot_movement_mode." + Lang.asId(name());
  }

  @Override
  public AllIcons getIcon() {
    return null;
  }

  @Override
  public String getTranslationKey() {
    return null;
  }
}
