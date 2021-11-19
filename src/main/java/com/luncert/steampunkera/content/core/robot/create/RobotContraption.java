package com.luncert.steampunkera.content.core.robot.create;

import com.luncert.steampunkera.index.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.*;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class RobotContraption extends Contraption {

  private static final Logger LOGGER = LogManager.getLogger();

  private static final String NBT_ROTATION_MODE = "RotationMode";

  private static final ContraptionType ROBOT = ContraptionType.register("robot", RobotContraption::new);

  private RobotMovementMode rotationMode;

  public RobotContraption() {
    this(RobotMovementMode.ROTATE);
  }

  public RobotContraption(RobotMovementMode rotationMode) {
    this.rotationMode = rotationMode;
  }

  @Override
  protected ContraptionType getType() {
    return ROBOT;
  }

  @Override
  public boolean assemble(World world, BlockPos pos) throws AssemblyException {
    // pos will be anchor position
    if (!searchMovedStructure(world, pos, null)) {
      return false;
    }

    // add anchor block
    BlockInfo blockInfo = new BlockInfo(pos, ModBlocks.ROBOT_ANCHOR.getDefaultState(), null);
    addBlock(pos, Pair.of(blockInfo, null));

    LOGGER.info("{}", blocks.size());
    return blocks.size() != 1;
  }

  @Override
  protected boolean addToInitialFrontier(World world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
    // let contraption search above blocks
    frontier.clear();
    frontier.add(pos.above());
    return true;
  }

  @Override
  public CompoundNBT writeNBT(boolean spawnPacket) {
    CompoundNBT tag = super.writeNBT(spawnPacket);
    NBTHelper.writeEnum(tag, NBT_ROTATION_MODE, rotationMode);
    return tag;
  }

  @Override
  public void readNBT(World world, CompoundNBT nbt, boolean spawnData) {
    rotationMode = NBTHelper.readEnum(nbt, NBT_ROTATION_MODE, RobotMovementMode.class);
    super.readNBT(world, nbt, spawnData);
  }


  @Override
  protected boolean customBlockPlacement(IWorld world, BlockPos pos, BlockState state) {
    return ModBlocks.ROBOT_ANCHOR.has(state);
  }

  @Override
  protected boolean customBlockRemoval(IWorld world, BlockPos pos, BlockState state) {
    return ModBlocks.ROBOT_ANCHOR.has(state);
  }

  @Override
  public boolean canBeStabilized(Direction facing, BlockPos localPos) {
    // refers to MountedContraption
    return true;
  }

  @Override
  public void addExtraInventories(Entity entity) {
    if (!(entity instanceof IInventory)) {
      return;
    }

    IItemHandlerModifiable handlerFromInv = new ContraptionInvWrapper(true,
        new InvWrapper((IInventory) entity));
    inventory = new ContraptionInvWrapper(handlerFromInv, inventory);
  }

  @Override
  public ContraptionLighter<?> makeLighter() {
    return new NonStationaryLighter<>(this);
  }
}
