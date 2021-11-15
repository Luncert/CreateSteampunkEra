package com.luncert.steampunkera.content.core.robot;

import com.luncert.steampunkera.content.core.robot.cc.*;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class RobotTileEntity extends ComputerTileBase implements IComputerContainer {

    private final RobotBrain brain;
    private LazyOptional<IPeripheral> peripheral;

    public RobotTileEntity(TileEntityType<? extends TileGeneric> type) {
        super(type, ComputerFamily.NORMAL);
        brain = new RobotBrain(this);
    }

    @Override
    protected void updateBlockState(ComputerState computerState) {

    }

    @Override
    public Direction getDirection() {
        // TODO
        return Direction.NORTH;
    }

    @Override
    public INamedContainerProvider getContainerProvider(ServerComputer computer,
                                                        PlayerEntity player,
                                                        @Nonnull Hand hand,
                                                        RobotControllerItem controller) {
        return new RobotContainer.Factory(computer, player.getItemInHand(hand), controller, hand);
    }

    @Override
    protected ServerComputer createComputer(int instanceID, int computerID) {
        ServerComputer computer = new ServerComputer(this.getLevel(), computerID, this.label, instanceID, this.getFamily(), 39, 13);
        computer.setPosition(this.getBlockPos());
        computer.addAPI(new RobotAPI(computer.getAPIEnvironment(), this.getAccess()));
        this.brain.setupComputer(computer);
        return computer;
    }

    public IRobotAccess getAccess() {
        return this.brain;
    }

    public ComputerProxy createProxy() {
        return this.brain.getProxy();
    }

    public boolean isUsableByPlayer(PlayerEntity player) {
        return this.isUsable(player, false);
    }
}
