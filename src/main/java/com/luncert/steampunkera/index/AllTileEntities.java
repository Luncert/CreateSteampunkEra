package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.robot.RobotContainerTileEntity;
import com.luncert.steampunkera.content.robot.RobotTileEntity;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class AllTileEntities {

    public static void register() {}

    public static final TileEntityEntry<RobotTileEntity> ROBOT =
        SteampunkEra.registrate()
            .tileEntity("robot", RobotTileEntity::new)
            .validBlocks(AllBlocks.ROBOT)
            .register();

    public static final TileEntityEntry<RobotContainerTileEntity> ROBOT_CONTAINER =
        SteampunkEra.registrate()
            .tileEntity("robot_container", RobotContainerTileEntity::new)
            .validBlocks(AllBlocks.ROBOT_CHARGE_STATION)
            .register();
}
