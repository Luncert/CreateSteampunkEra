package com.luncert.steampunkera.foundation;

import com.luncert.steampunkera.SteampunkEra;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CCEItemGroupBase extends ItemGroup {

    public CCEItemGroupBase(String id) {
        super(getGroupCountSafe(), SteampunkEra.MOD_ID + "." + id);
    }

    @Override
    public ItemStack makeIcon() {
        return null;
    }
}
