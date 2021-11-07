package com.luncert.ccenhanced.index;

import com.luncert.ccenhanced.CCE;
import com.luncert.ccenhanced.content.robot.RobotTileEntity;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class AllTileEntities {

    public static void register() {}

    public static final TileEntityEntry<RobotTileEntity> ROBOT = CCE.registrate()
            .tileEntity("robot", RobotTileEntity::new)
            .validBlocks(AllBlocks.ROBOT)
            .register();
}
