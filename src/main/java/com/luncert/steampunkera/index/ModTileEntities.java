package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.depothopper.DepotHopperTileEntity;
import com.luncert.steampunkera.content.core.robotchargestation.RobotChargeStationTileEntity;
import com.luncert.steampunkera.content.core.robot.RobotTileEntity;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class ModTileEntities {

    public static void register() {}

    public static final TileEntityEntry<RobotTileEntity> ROBOT =
        SteampunkEra.registrate()
            .tileEntity("robot", RobotTileEntity::new)
            .validBlocks(ModBlocks.ROBOT)
            .register();

    public static final TileEntityEntry<RobotChargeStationTileEntity> ROBOT_CHARGE_STATION =
        SteampunkEra.registrate()
            .tileEntity("robot_charge_station", RobotChargeStationTileEntity::new)
            .validBlocks(ModBlocks.ROBOT_CHARGE_STATION)
            // .renderer(() -> RobotChargeStationTileEntityRenderer::new)
            .register();

    public static final TileEntityEntry<DepotHopperTileEntity> DEPOT_HOPPER =
        SteampunkEra.registrate()
            .tileEntity("depot_hopper", DepotHopperTileEntity::new)
            .validBlocks(ModBlocks.DEPOT_HOPPER)
            .register();
}
