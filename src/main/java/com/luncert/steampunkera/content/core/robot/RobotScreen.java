package com.luncert.steampunkera.content.core.robot;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.gui.ComputerScreenBase;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.client.render.ComputerBorderRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RobotScreen extends ComputerScreenBase<RobotContainer> {

  private final int termWidth;
  private final int termHeight;

  private RobotScreen(RobotContainer container, PlayerInventory player, ITextComponent title, int termWidth, int termHeight) {
    super(container, player, title, 12);
    this.termWidth = termWidth;
    this.termHeight = termHeight;
    this.imageWidth = WidgetTerminal.getWidth(termWidth) + 24 + 17;
    this.imageHeight = WidgetTerminal.getHeight(termHeight) + 24;
  }

  public static RobotScreen create(RobotContainer container, PlayerInventory inventory, ITextComponent component) {
    return new RobotScreen(container, inventory, component, ComputerCraft.computerTermWidth, ComputerCraft.computerTermHeight);
  }

  @Override
  protected WidgetTerminal createTerminal() {
    return new WidgetTerminal(this.computer, this.leftPos + 17 + 12, this.topPos + 12, this.termWidth, this.termHeight);
  }

  @Override
  protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    minecraft.getTextureManager().bind(ComputerBorderRenderer.getTexture(this.family));
    ComputerBorderRenderer.render(this.terminal.x, this.terminal.y, this.getBlitOffset(), this.terminal.getWidth(), this.terminal.getHeight());
    ComputerSidebar.renderBackground(stack, this.leftPos, this.topPos + this.sidebarYOffset);
  }
}

