package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.robot.RobotControllerItem;
import com.luncert.steampunkera.content.weapon.SteamPistol;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;

public class AllItems {

    private static final CreateRegistrate REGISTRATE = SteampunkEra.registrate()
            .itemGroup(() -> SteampunkEra.MAIN);

    public static void register() {}

    public static final ItemEntry<RobotControllerItem> ROBOT_CONTROLLER =
        REGISTRATE.item("robot_controller", RobotControllerItem::new)
            .register();

    public static final ItemEntry<SteamPistol> STEAM_PISTOL =
        REGISTRATE.item("steam_pistol", SteamPistol::new)
            .register();
}
