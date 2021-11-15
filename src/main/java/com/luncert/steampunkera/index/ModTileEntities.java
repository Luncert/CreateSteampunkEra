package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.robot.RobotChargeStationTileEntity;
import com.luncert.steampunkera.content.core.robot.RobotChargeStationTileEntityRenderer;
import com.luncert.steampunkera.content.core.robot.RobotTileEntity;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class ModTileEntities {

    public static void register() {}

    public static final TileEntityEntry<RobotTileEntity> ROBOT =
        SteampunkEra.registrate()
            .tileEntity("robot", RobotTileEntity::new)
            .validBlocks(ModBlocks.ROBOT)
            .register();

    public static final TileEntityEntry<RobotChargeStationTileEntity> ROBOT_CONTAINER =
        SteampunkEra.registrate()
            .tileEntity("robot_container", RobotChargeStationTileEntity::new)
            .validBlocks(ModBlocks.ROBOT_CHARGE_STATION)
            .renderer(() -> RobotChargeStationTileEntityRenderer::new)
            .register();
}
