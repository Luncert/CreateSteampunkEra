package com.luncert.steampunkera.content.net;

import com.luncert.steampunkera.content.core.robot.RobotEntity;
import com.luncert.steampunkera.content.core.robot.RobotMovement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

import static com.luncert.steampunkera.content.core.robot.RobotMovement.MOVEMENT_SERIALIZER;

// used to sync robot entity data from client side to server
public class CRobotPacket {

  private final float waitingYRot;
  private final RobotMovement waitingMovement;

  public CRobotPacket(float waitingYRot, @Nullable RobotMovement waitingMovement) {
    this.waitingYRot = waitingYRot;
    this.waitingMovement = waitingMovement;
  }

  public static CRobotPacket read(PacketBuffer buffer) {
    return new CRobotPacket(
        buffer.readFloat(),
        MOVEMENT_SERIALIZER.read(buffer).orElse(null)
    );
  }

  public static void write(CRobotPacket packet, PacketBuffer buffer) {
    buffer.writeFloat(packet.waitingYRot);
    MOVEMENT_SERIALIZER.write(buffer, Optional.ofNullable(packet.waitingMovement));
  }

  // handle client package
  public static void handle(CRobotPacket packet, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      // Work that needs to be thread-safe (most work)
      ServerPlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
      if (sender != null) {
        Entity vehicle = sender.getVehicle();
        if (vehicle instanceof RobotEntity) {
          RobotEntity robot = (RobotEntity) vehicle;
          robot.setWaitingYRot(packet.waitingYRot);
          robot.setWaitingMovement(packet.waitingMovement);
        }
      }
      // Do stuff
    });
    ctx.get().setPacketHandled(true);
  }
}
