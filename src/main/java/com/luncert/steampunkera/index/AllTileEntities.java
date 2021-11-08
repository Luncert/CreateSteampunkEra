package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.robot.RobotTileEntity;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class AllTileEntities {

    public static void register() {}

    public static final TileEntityEntry<RobotTileEntity> ROBOT = SteampunkEra.registrate()
            .tileEntity("robot", RobotTileEntity::new)
            .validBlocks(AllBlocks.ROBOT)
            .register();
}
