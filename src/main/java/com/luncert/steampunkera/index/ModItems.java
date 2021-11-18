package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.robotcontroller.RobotConnectorItem;
import com.mrcrayfish.guns.item.GunItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;

public class ModItems {

    private static final CreateRegistrate REGISTRATE = SteampunkEra.registrate()
            .itemGroup(() -> SteampunkEra.MAIN);

    public static void register() {}

    public static final ItemEntry<RobotConnectorItem> ROBOT_CONNECTOR =
        REGISTRATE.item("robot_connector", RobotConnectorItem::new)
            .properties(b -> b.stacksTo(1))
            .register();

    public static final ItemEntry<GunItem> PISTOL =
        REGISTRATE.item("pistol", GunItem::new)
            .properties(b -> b.stacksTo(1))
            .register();
}
