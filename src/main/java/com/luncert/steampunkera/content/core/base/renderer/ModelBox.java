// package com.luncert.steampunkera.content.core.base.renderer;
//
// import com.luncert.steampunkera.content.core.base.renderer.ModelRenderer.TexturedQuad;
// import net.minecraft.util.Direction;
// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
//
// @OnlyIn(Dist.CLIENT)
// public class ModelBox {
//
//   final TexturedQuad[] polygons;
//   public final float minX;
//   public final float minY;
//   public final float minZ;
//   public final float maxX;
//   public final float maxY;
//   public final float maxZ;
//
//   public ModelBox(float minX, float minY, float minZ,
//                   float xLen, float yLen, float zLen,
//                   TextureMapping textureMapping) {
//     this.minX = minX;
//     this.minY = minY;
//     this.minZ = minZ;
//     this.maxX = minX + xLen;
//     this.maxY = minY + yLen;
//     this.maxZ = minZ + zLen;
//
//     this.polygons = new TexturedQuad[6];
//
//     for (TextureMapping.Entry entry : textureMapping) {
//       TextureMapping.FaceUV mapping = entry.mapping;
//       polygons[entry.face.ordinal()] = new TexturedQuad();
//     }
//
//     ModelRenderer.PositionTextureVertex v7 = new ModelRenderer.PositionTextureVertex(minX, minY, minZ, 0.0F, 0.0F);
//     ModelRenderer.PositionTextureVertex v = new ModelRenderer.PositionTextureVertex(maxX, minY, minZ, 0.0F, 8.0F);
//     ModelRenderer.PositionTextureVertex v1 = new ModelRenderer.PositionTextureVertex(maxX, maxY, minZ, 8.0F, 8.0F);
//     ModelRenderer.PositionTextureVertex v2 = new ModelRenderer.PositionTextureVertex(minX, maxY, minZ, 8.0F, 0.0F);
//     ModelRenderer.PositionTextureVertex v3 = new ModelRenderer.PositionTextureVertex(minX, minY, maxZ, 0.0F, 0.0F);
//     ModelRenderer.PositionTextureVertex v4 = new ModelRenderer.PositionTextureVertex(maxX, minY, maxZ, 0.0F, 8.0F);
//     ModelRenderer.PositionTextureVertex v5 = new ModelRenderer.PositionTextureVertex(maxX, maxY, maxZ, 8.0F, 8.0F);
//     ModelRenderer.PositionTextureVertex v6 = new ModelRenderer.PositionTextureVertex(minX, maxY, maxZ, 8.0F, 0.0F);
//
//     float f4 = (float) xTexOffs;
//     float f5 = (float) xTexOffs + zLen;
//     float f6 = (float) xTexOffs + zLen + xLen;
//     float f7 = (float) xTexOffs + zLen + xLen + xLen;
//     float f8 = (float) xTexOffs + zLen + xLen + zLen;
//     float f9 = (float) xTexOffs + zLen + xLen + zLen + xLen;
//     float f10 = (float) yTexOffs;
//     float f11 = (float) yTexOffs + zLen;
//     float f12 = (float) yTexOffs + zLen + yLen;
//
//     this.polygons[2] = new TexturedQuad(
//         new ModelRenderer.PositionTextureVertex[]{v4, v3, v7, v}, f5, f10, f6, f11, xTexSize, yTexSize, mirror, Direction.DOWN);
//     this.polygons[3] = new TexturedQuad(
//         new ModelRenderer.PositionTextureVertex[]{v1, v2, v6, v5}, f6, f11, f7, f10, xTexSize, yTexSize, mirror, Direction.UP);
//     this.polygons[1] = new TexturedQuad(
//         new ModelRenderer.PositionTextureVertex[]{v7, v3, v6, v2}, f4, f11, f5, f12, xTexSize, yTexSize, mirror, Direction.WEST);
//     this.polygons[4] = new TexturedQuad(
//         new ModelRenderer.PositionTextureVertex[]{v, v7, v2, v1}, f5, f11, f6, f12, xTexSize, yTexSize, mirror, Direction.NORTH);
//     this.polygons[0] = new TexturedQuad(
//         new ModelRenderer.PositionTextureVertex[]{v4, v, v1, v5}, f6, f11, f8, f12, xTexSize, yTexSize, mirror, Direction.EAST);
//     this.polygons[5] = new TexturedQuad(
//         new ModelRenderer.PositionTextureVertex[]{v3, v4, v5, v6}, f8, f11, f9, f12, xTexSize, yTexSize, mirror, Direction.SOUTH);
//   }
//
//   public static class Builder {
//
//     private float minX;
//     private float minY;
//     private float minZ;
//     private float maxX;
//     private float maxY;
//     private float maxZ;
//
//     public Builder minX(int minX) {
//       this.minX = minX;
//       return this;
//     }
//
//     public Builder minY(int minY) {
//       this.minY = minY;
//       return this;
//     }
//
//     public Builder minZ(int minZ) {
//       this.minZ = minZ;
//       return this;
//     }
//
//     public Builder maxX(int maxX) {
//       this.maxX = maxX;
//       return this;
//     }
//
//     public Builder maxY(int maxY) {
//       this.maxY = maxY;
//       return this;
//     }
//
//     public Builder maxZ(int maxZ) {
//       this.maxZ = maxZ;
//       return this;
//     }
//
//     public ModelBox build() {
//       return new ModelBox(minX, minY, minZ, maxX, maxY, maxZ);
//     }
//   }
// }
