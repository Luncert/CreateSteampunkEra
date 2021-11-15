package com.luncert.steampunkera.content;

import com.luncert.steampunkera.foundation.CCEItemGroupBase;
import com.luncert.steampunkera.index.ModBlocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;

public class CCEItemGroup extends CCEItemGroupBase {

    public CCEItemGroup() {
        super("base");
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack makeIcon() {
        return ModBlocks.ROBOT.asStack();
    }
}
