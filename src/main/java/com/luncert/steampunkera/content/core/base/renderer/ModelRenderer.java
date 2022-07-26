// package com.luncert.steampunkera.content.core.base.renderer;
//
// import com.mojang.blaze3d.matrix.MatrixStack;
// import com.mojang.blaze3d.vertex.IVertexBuilder;
// import it.unimi.dsi.fastutil.objects.ObjectArrayList;
// import it.unimi.dsi.fastutil.objects.ObjectList;
// import java.util.Random;
//
// import net.minecraft.client.Minecraft;
// import net.minecraft.util.Direction;
// import net.minecraft.util.math.vector.Matrix3f;
// import net.minecraft.util.math.vector.Matrix4f;
// import net.minecraft.util.math.vector.Vector3f;
// import net.minecraft.util.math.vector.Vector4f;
// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
//
// @OnlyIn(Dist.CLIENT)
// public class ModelRenderer {
//   public float x;
//   public float y;
//   public float z;
//   public float xRot;
//   public float yRot;
//   public float zRot;
//   public boolean visible = true;
//   private final ObjectList<ModelBox> cubes = new ObjectArrayList<>();
//   private final ObjectList<ModelRenderer> children = new ObjectArrayList<>();
//
//   public ModelRenderer(Model model) {
//     model.accept(this);
//   }
//
//   private ModelRenderer() {
//   }
//
//   public ModelRenderer createShallowCopy() {
//     ModelRenderer modelrenderer = new ModelRenderer();
//     modelrenderer.copyFrom(this);
//     return modelrenderer;
//   }
//
//   public void copyFrom(ModelRenderer renderer) {
//     this.xRot = renderer.xRot;
//     this.yRot = renderer.yRot;
//     this.zRot = renderer.zRot;
//     this.x = renderer.x;
//     this.y = renderer.y;
//     this.z = renderer.z;
//   }
//
//   public void addChild(ModelRenderer renderer) {
//     this.children.add(renderer);
//   }
//
//   public void addBox(float x, float y, float z,
//                      float xLen, float yLen, float zLen,
//                      TextureMapping textureMapping) {
//     this.cubes.add(new ModelBox(x, y, z, xLen, yLen, zLen, textureMapping));
//   }
//
//   public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn) {
//     this.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
//   }
//
//   public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder,
//                      int packedLightIn, int packedOverlayIn,
//                      float red, float green, float blue, float alpha) {
//     if (this.visible) {
//       if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
//         matrixStack.pushPose();
//         this.translateAndRotate(matrixStack);
//         this.compile(matrixStack.last(), vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//
//         for(ModelRenderer modelrenderer : this.children) {
//           modelrenderer.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//         }
//
//         matrixStack.popPose();
//       }
//     }
//   }
//
//   public void translateAndRotate(MatrixStack ms) {
//     ms.translate(x / 16.0F, y / 16.0F, z / 16.0F);
//     if (zRot != 0.0F) {
//       ms.mulPose(Vector3f.ZP.rotation(zRot));
//     }
//
//     if (yRot != 0.0F) {
//       ms.mulPose(Vector3f.YP.rotation(yRot));
//     }
//
//     if (xRot != 0.0F) {
//       ms.mulPose(Vector3f.XP.rotation(xRot));
//     }
//
//   }
//
//   private void compile(MatrixStack.Entry entry, IVertexBuilder vertexBuilder,
//                        int packedLightIn, int packedOverlayIn,
//                        float red, float green, float blue, float alpha) {
//     Matrix4f pose = entry.pose();
//     Matrix3f normal = entry.normal();
//
//     for(ModelBox modelBox : this.cubes) {
//       for(TexturedQuad texturedQuad : modelBox.polygons) {
//         Vector3f normalCopy = texturedQuad.normal.copy();
//         normalCopy.transform(normal);
//         float vx = normalCopy.x();
//         float vy = normalCopy.y();
//         float vz = normalCopy.z();
//
//         for(int i = 0; i < 4; ++i) {
//           PositionTextureVertex textureVertex = texturedQuad.vertices[i];
//           float px = textureVertex.pos.x() / 16.0F;
//           float py = textureVertex.pos.y() / 16.0F;
//           float pz = textureVertex.pos.z() / 16.0F;
//
//           Vector4f v = new Vector4f(px, py, pz, 1.0F);
//           v.transform(pose);
//           vertexBuilder.vertex(v.x(), v.y(), v.z(),
//               red, green, blue, alpha,
//               textureVertex.u, textureVertex.v,
//               packedOverlayIn, packedLightIn, vx, vy, vz);
//         }
//       }
//     }
//
//   }
//
//   @OnlyIn(Dist.CLIENT)
//   static class PositionTextureVertex {
//     public final Vector3f pos;
//     public final float u;
//     public final float v;
//
//     public PositionTextureVertex(float x, float y, float z, float u, float v) {
//       this(new Vector3f(x, y, z), u, v);
//     }
//
//     public PositionTextureVertex remap(float u, float v) {
//       return new PositionTextureVertex(pos, u, v);
//     }
//
//     public PositionTextureVertex(Vector3f pos, float u, float v) {
//       this.pos = pos;
//       this.u = u;
//       this.v = v;
//     }
//   }
//
//   @OnlyIn(Dist.CLIENT)
//   static class TexturedQuad {
//     public final PositionTextureVertex[] vertices;
//     public final Vector3f normal;
//
//     public TexturedQuad(PositionTextureVertex[] textureVertices,
//                         float p_i225951_2_, float p_i225951_3_, float p_i225951_4_,
//                         float p_i225951_5_, float p_i225951_6_, float p_i225951_7_,
//                         boolean p_i225951_8_, Direction p_i225951_9_) {
//       this.vertices = textureVertices;
//       float f = 0.0F / p_i225951_6_;
//       float f1 = 0.0F / p_i225951_7_;
//       textureVertices[0] = textureVertices[0].remap(p_i225951_4_ / p_i225951_6_ - f, p_i225951_3_ / p_i225951_7_ + f1);
//       textureVertices[1] = textureVertices[1].remap(p_i225951_2_ / p_i225951_6_ + f, p_i225951_3_ / p_i225951_7_ + f1);
//       textureVertices[2] = textureVertices[2].remap(p_i225951_2_ / p_i225951_6_ + f, p_i225951_5_ / p_i225951_7_ - f1);
//       textureVertices[3] = textureVertices[3].remap(p_i225951_4_ / p_i225951_6_ - f, p_i225951_5_ / p_i225951_7_ - f1);
//       if (p_i225951_8_) {
//         int i = textureVertices.length;
//
//         for(int j = 0; j < i / 2; ++j) {
//           PositionTextureVertex textureVertex = textureVertices[j];
//           textureVertices[j] = textureVertices[i - 1 - j];
//           textureVertices[i - 1 - j] = textureVertex;
//         }
//       }
//
//       this.normal = p_i225951_9_.step();
//       if (p_i225951_8_) {
//         this.normal.mul(-1.0F, 1.0F, 1.0F);
//       }
//
//     }
//   }
// }