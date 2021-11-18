// package com.luncert.steampunkera.content.core.robotchargestation;
//
// import com.mojang.blaze3d.matrix.MatrixStack;
// import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.IRenderTypeBuffer;
// import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
// import net.minecraft.item.ItemStack;
//
// public class RobotChargeStationTileEntityRenderer extends SafeTileEntityRenderer<RobotChargeStationTileEntity> {
//
//   public RobotChargeStationTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
//     super(dispatcher);
//   }
//
//   @Override
//   protected void renderSafe(RobotChargeStationTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
//     ItemStack robotController = te.inventory.getStackInSlot(0);
//     if (robotController == ItemStack.EMPTY) {
//       return;
//     }
//
//     ms.pushPose();
//     ms.scale(1.5f, 1.5f, 1.5f);
//     ms.translate(0.5d, 0.5d, 0.5d);
//     Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(robotController, 0, 0);
//     ms.popPose();
//   }
// }
