package com.luncert.steampunkera.content.core.robotchargestation;

import com.google.common.collect.ImmutableList;
import com.luncert.steampunkera.content.util.Lang;
import com.luncert.steampunkera.index.ModBlocks;
import com.luncert.steampunkera.index.ModGuiTextures;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.gui.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.gui.widgets.IconButton;
import com.simibubi.create.foundation.gui.widgets.Label;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collections;
import java.util.List;

public class RobotChargeStationScreen extends AbstractSimiContainerScreen<RobotChargeStationContainer> {

  private final ITextComponent titleText = Lang.translate("gui.robot_charge_station.title");

  private List<Rectangle2d> extraAreas = Collections.emptyList();
  private final ItemStack renderedItem = ModBlocks.ROBOT_CHARGE_STATION.asStack();
  private final ModGuiTextures background;
  private IconButton button;
  private Label label;

  public RobotChargeStationScreen(RobotChargeStationContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
    background = ModGuiTextures.ROBOT_CHARGE_STATION;
  }

  protected void init() {
    setWindowSize(this.background.width, this.background.height + 4 + AllGuiTextures.PLAYER_INVENTORY.height);
    setWindowOffset(4, 8);
    super.init();
    widgets.clear();

    int x = leftPos;
    int y = topPos;

    label = new Label(x + 42, y + 24, StringTextComponent.EMPTY).withShadow();
    label.text = StringTextComponent.EMPTY;
    // widgets.add(label);

    button = new IconButton(x + 143, y + 21, AllIcons.I_CONFIRM);
    widgets.add(button);

    extraAreas = ImmutableList.of(
        new Rectangle2d(x + background.width, y + background.height - 40, 48, 48));
  }

  @Override
  protected void renderWindow(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.width);
    int invY = topPos + background.height + 4;
    renderPlayerInventory(ms, invX, invY);

    int x = leftPos;
    int y = topPos;

    background.draw(ms, this, x, y);

    drawCenteredString(ms, font, titleText, x + (background.width - 8) / 2, y + 3, 16777215);

    GuiGameElement.of(renderedItem)
        .<GuiGameElement.GuiRenderBuilder>at((float)(x + background.width), (float)(y + background.height - 40), -200.0F)
        .scale(3.0D)
        .render(ms);
  }

  @Override
  public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
    if (button.active) {
    }

    return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
  }

  @Override
  public List<Rectangle2d> getExtraAreas() {
    return extraAreas;
  }
}
