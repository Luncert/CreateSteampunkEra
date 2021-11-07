package com.luncert.ccenhanced.index;

import com.luncert.ccenhanced.CCE;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.item.Item;

public class AllItems {

    private static final CreateRegistrate REGISTRATE = CCE.registrate()
            .itemGroup(() -> CCE.MAIN);

    public static void register() {}

    // public static final ItemEntry<Item> ROBOT =
    //     REGISTRATE.item("robot", Item::new)
    //         .register();
}
