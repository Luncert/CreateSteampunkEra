package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.core.base.DirectionalBlock;
import com.luncert.steampunkera.content.core.robot.cc.*;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RobotTileEntity extends ComputerTileBase implements IComputerContainer {

    private RobotBrain brain;

    public RobotTileEntity(TileEntityType<? extends TileGeneric> type) {
        super(type, ComputerData.of(ComputerFamily.NORMAL));
        brain = new RobotBrain(this);
    }

    void setComputerData(ComputerData other) {
        this.data = other;
        // updateBlock();
    }

    void setRobotBrain(RobotBrain other) {
        other.setOwner(this);
        this.brain = other;
    }

    void setOwningPlayer(GameProfile player) {
        brain.setOwningPlayer(player);
        setChanged();
    }

    @Override
    protected void updateBlockState(ComputerState computerState) {
    }

    @Override
    public Direction getDirection() {
        return getBlockState().getValue(DirectionalBlock.FACING);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return isUsable(player, false);
    }

    @Override
    protected ServerComputer createComputer(int instanceID, int computerID) {
        ServerComputer computer = new ServerComputer(getLevel(), computerID, data.label, instanceID, getFamily(),
            ComputerCraft.computerTermWidth, ComputerCraft.computerTermHeight);
        computer.setPosition(getBlockPos());
        computer.addAPI(new RobotAPI(computer.getAPIEnvironment(), getAccess()));
        brain.setupComputer(computer);
        return computer;
    }

    public IRobotAccess getAccess() {
        return brain;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return RobotContainer.create(id, brain);
    }

    @Override
    public void tick() {
        super.tick();

        brain.update();
    }

    // robot api

    @Override
    public boolean isAssembled() {
        // always false for tile entity
        return false;
    }

    @Override
    public void assemble(boolean assembleStructure) {
        World world = getLevel();
        BlockPos blockPos = getBlockPos();
        RobotEntity robotEntity = new RobotEntity(world, blockPos, getBlockState(), brain, data);
        world.addFreshEntity(robotEntity);
        world.removeBlock(blockPos, false);
    }
}
