package com.luncert.ccenhanced.content;

import com.luncert.ccenhanced.foundation.CCEItemGroupBase;
import com.luncert.ccenhanced.index.AllBlocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;

public class CCEItemGroup extends CCEItemGroupBase {

    public CCEItemGroup() {
        super("base");
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack makeIcon() {
        return AllBlocks.ROBOT.asStack();
    }
}
