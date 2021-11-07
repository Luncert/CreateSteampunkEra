package com.luncert.ccenhanced.content.net;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CCEPacketHandler {

  private static final String PROTOCOL_VERSION = "1";

  private static int messageId;

  public static void register() {
  }

  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
      new ResourceLocation("ccenhanced", "main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals
  );

  static {
    INSTANCE.registerMessage(messageId++, CRobotPacket.class,
        CRobotPacket::write, CRobotPacket::read, CRobotPacket::handle);
  }
}
