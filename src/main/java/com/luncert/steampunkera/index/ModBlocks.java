package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.depothopper.DepotHopperBlock;
import com.luncert.steampunkera.content.core.depothopper.DepotHopperItem;
import com.luncert.steampunkera.content.core.observerfunnel.ObserverFunnelBlock;
import com.luncert.steampunkera.content.core.robot.RobotBlock;
import com.luncert.steampunkera.content.core.robot.RobotBlock.RobotAnchorBlock;
import com.luncert.steampunkera.content.core.robotchargestation.RobotChargeStationBlock;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.logistics.block.funnel.FunnelGenerator;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.builders.ItemBuilder;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullFunction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;

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
            .tag(AllBlockTags.SAFE_NBT.tag)
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
            .tag(AllBlockTags.SAFE_NBT.tag)
            .simpleItem()
            .register();

    public static final BlockEntry<ObserverFunnelBlock> OBSERVER_FUNNEL =
        REGISTRATE.block("observer_funnel", ObserverFunnelBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .tag(AllBlockTags.SAFE_NBT.tag)
            .blockstate(new FunnelGenerator("brass", true)::generate)
            // .item(FunnelItem::new)
            // .model(FunnelGenerator.itemModel("brass"))
            // .build()
            .simpleItem()
            .register();

    public static final BlockEntry<DepotHopperBlock> DEPOT_HOPPER =
        REGISTRATE.block("depot_hopper", DepotHopperBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(AbstractBlock.Properties::noOcclusion)
            .tag(AllBlockTags.SAFE_NBT.tag)
            .item(DepotHopperItem::new)
            .transform(customItemModel())
            .register();

    public static <I extends BlockItem, P> NonNullFunction<ItemBuilder<I, P>, P> customItemModel() {
        return b -> b.model(AssetLookup::customItemModel)
            .build();
    }
}
