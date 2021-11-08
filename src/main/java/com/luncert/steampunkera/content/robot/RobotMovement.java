package com.luncert.steampunkera.content.robot;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.Direction;

import java.util.Optional;

public class RobotMovement {

  Direction.Axis axis;
  boolean positive;
  float expectedPos;

  public RobotMovement(Direction.Axis axis, boolean positive, float expectedPos) {
    this.axis = axis;
    this.positive = positive;
    this.expectedPos = expectedPos;
  }

  public static final IDataSerializer<Optional<RobotMovement>> MOVEMENT_SERIALIZER = new MovementSerializer();

  static {
    DataSerializers.registerSerializer(MOVEMENT_SERIALIZER);
  }

  @MethodsReturnNonnullByDefault
  private static class MovementSerializer implements IDataSerializer<Optional<RobotMovement>> {

    @Override
    public void write(PacketBuffer buffer, Optional<RobotMovement> opt) {
      buffer.writeBoolean(opt.isPresent());
      opt.ifPresent(movement -> {
        buffer.writeInt(movement.axis.ordinal());
        buffer.writeBoolean(movement.positive);
        buffer.writeFloat(movement.expectedPos);
      });
    }

    @Override
    public Optional<RobotMovement> read(PacketBuffer buffer) {
      boolean isPresent = buffer.readBoolean();
      return isPresent ? Optional.of(new RobotMovement(Direction.Axis.values()[buffer.readInt()], buffer.readBoolean(), buffer.readFloat()))
          : Optional.empty();
    }

    @Override
    public Optional<RobotMovement> copy(Optional<RobotMovement> opt) {
      return opt.map(movement -> new RobotMovement(movement.axis, movement.positive, movement.expectedPos));
    }
  }
}
