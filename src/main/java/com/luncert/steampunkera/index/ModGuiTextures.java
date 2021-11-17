package com.luncert.steampunkera.index;

import com.luncert.steampunkera.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.gui.IScreenRenderable;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ModGuiTextures implements IScreenRenderable {
  ROBOT_CHARGE_STATION("robot_charge_station.png", 0, 0, 184, 48),
  ;

  public static final int FONT_COLOR = 5726074;
  public final ResourceLocation location;
  public int width;
  public int height;
  public int startX;
  public int startY;

  ModGuiTextures(String location, int width, int height) {
    this(location, 0, 0, width, height);
  }

  ModGuiTextures(int startX, int startY) {
    this("icons.png", startX * 16, startY * 16, 16, 16);
  }

  ModGuiTextures(String location, int startX, int startY, int width, int height) {
    this(Reference.MOD_ID, location, startX, startY, width, height);
  }

  ModGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
    this.location = new ResourceLocation(namespace, "textures/gui/" + location);
    this.width = width;
    this.height = height;
    this.startX = startX;
    this.startY = startY;
  }

  @OnlyIn(Dist.CLIENT)
  public void bind() {
    Minecraft.getInstance().getTextureManager().bind(this.location);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void draw(MatrixStack ms, AbstractGui screen, int x, int y) {
    bind();
    screen.blit(ms, x, y, this.startX, this.startY, this.width, this.height);
  }

  public void draw(MatrixStack ms, int x, int y, Color c) {
    bind();
    UIRenderHelper.drawColoredTexture(ms, c, x, y, this.startX, this.startY, this.width, this.height);
  }
}
