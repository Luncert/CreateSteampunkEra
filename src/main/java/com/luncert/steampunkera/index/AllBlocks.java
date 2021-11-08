package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.robot.RobotBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;

public class AllBlocks {

    private static final CreateRegistrate REGISTRATE = SteampunkEra.registrate()
            .itemGroup(() -> SteampunkEra.MAIN);

    public static void register() {}

    public static final BlockEntry<RobotBlock> ROBOT =
        REGISTRATE.block("robot", RobotBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(AbstractBlock.Properties::noOcclusion)
            .properties(b -> b.strength(1200, 1200))
            // .addLayer(() -> RenderType::cutoutMipped)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .simpleItem()
            // .item()
            // .transform(customItemModel())
            .register();
}
