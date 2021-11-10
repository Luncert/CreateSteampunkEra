package com.luncert.steampunkera.index;

import com.luncert.steampunkera.content.robot.RobotContainer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllContainerTypes {

  public static void register() {}

  private static final DeferredRegister<ContainerType<?>> REGISTRATE = DeferredRegister.create(ForgeRegistries.CONTAINERS, "steampunkera");

  public static final RegistryObject<ContainerType<RobotContainer>> ROBOT =
      REGISTRATE.register("robot", () -> ContainerData.toType(ComputerContainerData::new, RobotContainer::new));
}
