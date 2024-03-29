package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.robot.RobotEntity;
import com.luncert.steampunkera.content.core.robot.RobotEntityRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import net.minecraft.entity.EntityClassification;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ModEntityTypes {

    private static final CreateRegistrate REGISTRATE = SteampunkEra.registrate();

    public static void register() {}

    public static final EntityEntry<RobotEntity> ROBOT =
        REGISTRATE.<RobotEntity>entity("robot", RobotEntity::new, EntityClassification.MISC)
            .properties(b -> b
                .sized(1, 1))
            .register();

    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(ROBOT.get(), RobotEntityRenderer::new);
    }
}
