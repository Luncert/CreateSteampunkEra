package com.luncert.steampunkera.index;

import com.luncert.steampunkera.SteampunkEra;
import com.luncert.steampunkera.content.core.robot.RobotContainer;
import com.luncert.steampunkera.content.core.robot.RobotScreen;
import com.luncert.steampunkera.content.core.robotchargestation.RobotChargeStationContainer;
import com.luncert.steampunkera.content.core.robotchargestation.RobotChargeStationScreen;
import com.simibubi.create.content.schematics.block.SchematicTableContainer;
import com.simibubi.create.content.schematics.block.SchematicTableScreen;
import com.simibubi.create.repack.registrate.builders.ContainerBuilder;
import com.simibubi.create.repack.registrate.util.entry.ContainerEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import dan200.computercraft.client.gui.GuiComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

  public static void register() {}

  public static final ContainerEntry<RobotContainer> ROBOT =
      register("robot", RobotContainer::create, () -> RobotScreen::create);

  public static final ContainerEntry<RobotChargeStationContainer> ROBOT_CHARGE_STATION =
      register("robot_charge_station", RobotChargeStationContainer::new, () -> RobotChargeStationScreen::new);

  private static <C extends Container, S extends Screen & IHasContainer<C>> ContainerEntry<C> register(
      String name,
      ContainerBuilder.ForgeContainerFactory<C> factory,
      NonNullSupplier<ContainerBuilder.ScreenFactory<C, S>> screenFactory) {
    return SteampunkEra.registrate().container(name, factory, screenFactory).register();
  }
}
