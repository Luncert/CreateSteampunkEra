package com.luncert.ccenhanced.content.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonEvents {

  private static final Logger LOGGER = LogManager.getLogger();

  // @SubscribeEvent
  // public void onPlayerInteract(PlayerInteractEvent event) {
  //   LOGGER.info("{}", event.getEntity());
  //
  //   if(event.getHand() == Hand.OFF_HAND)
  //     return;
  //
  //
  //   World world = event.getWorld();
  //   if(world.isClientSide) {
  //     if(event instanceof PlayerInteractEvent.RightClickEmpty || event instanceof PlayerInteractEvent.RightClickItem) {
  //       Entity targetEntity = event.getEntity();
  //       if (targetEntity instanceof RobotEntity && !targetEntity.isVehicle() && targetEntity.isAlive()) {
  //         event.getPlayer().startRiding(targetEntity);
  //       }
  //     }
  //   }
  // }
}
