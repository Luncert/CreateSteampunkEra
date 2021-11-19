package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.robot.RobotBlock;
import com.luncert.steampunkera.content.core.robot.RobotBlock.RobotAnchorBlock;
import com.luncert.steampunkera.content.core.robotchargestation.RobotChargeStationBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class ModBlocks {

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

    public static final BlockEntry<RobotAnchorBlock> ROBOT_ANCHOR =
        REGISTRATE.block("robot_anchor", RobotAnchorBlock::new)
            .initialProperties(SharedProperties::stone)
            .blockstate((c, p) -> p.simpleBlock(c.get(), p.models().getExistingFile(
                p.modLoc("block/robot/" + c.getName()))))
            .register();

    public static final BlockEntry<RobotChargeStationBlock> ROBOT_CHARGE_STATION =
        REGISTRATE.block("robot_charge_station", RobotChargeStationBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(AbstractBlock.Properties::noOcclusion)
            .properties(b -> b.strength(1200, 1200))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .simpleItem()
            .register();
}
