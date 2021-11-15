package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.robot.RobotControllerItem;
import com.mrcrayfish.guns.item.GunItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;

public class ModItems {

    private static final CreateRegistrate REGISTRATE = SteampunkEra.registrate()
            .itemGroup(() -> SteampunkEra.MAIN);

    public static void register() {}

    public static final ItemEntry<RobotControllerItem> ROBOT_CONTROLLER =
        REGISTRATE.item("robot_controller", RobotControllerItem::new)
            .register();

    public static final ItemEntry<GunItem> PISTOL =
        REGISTRATE.item("pistol", GunItem::new)
            .properties(b -> b.stacksTo(1))
            .register();
}
