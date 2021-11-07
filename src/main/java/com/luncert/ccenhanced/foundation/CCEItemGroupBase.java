package com.luncert.ccenhanced.foundation;

import com.luncert.ccenhanced.CCE;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CCEItemGroupBase extends ItemGroup {

    public CCEItemGroupBase(String id) {
        super(getGroupCountSafe(), CCE.MOD_ID + "." + id);
    }

    @Override
    public ItemStack makeIcon() {
        return null;
    }
}