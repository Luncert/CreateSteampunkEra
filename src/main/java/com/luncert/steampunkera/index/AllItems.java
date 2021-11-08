package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.simibubi.create.foundation.data.CreateRegistrate;

public class AllItems {

    private static final CreateRegistrate REGISTRATE = SteampunkEra.registrate()
            .itemGroup(() -> SteampunkEra.MAIN);

    public static void register() {}

    // public static final ItemEntry<Item> ROBOT =
    //     REGISTRATE.item("robot", Item::new)
    //         .register();
}
