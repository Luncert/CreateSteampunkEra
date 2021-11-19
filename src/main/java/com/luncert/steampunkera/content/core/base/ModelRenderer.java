package com.luncert.steampunkera.content.core.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Random;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRenderer {
  private float xTexSize = 64.0F;
  private float yTexSize = 32.0F;
  private int xTexOffs;
  private int yTexOffs;
  public float x;
  public float y;
  public float z;
  public float xRot;
  public float yRot;
  public float zRot;
  public boolean mirror;
  public boolean visible = true;
  private final ObjectList<ModelBox> cubes = new ObjectArrayList<>();
  private final ObjectList<ModelRenderer> children = new ObjectArrayList<>();

  public ModelRenderer(Model model) {
    model.accept(this);
    this.setTexSize(model.texWidth, model.texHeight);
  }

  public ModelRenderer(Model model, int xTexOffs, int yTexOffs) {
    this(model.texWidth, model.texHeight, xTexOffs, yTexOffs);
    model.accept(this);
  }

  public ModelRenderer(int texWidth, int texHeight, int xTexOffs, int yTexOffs) {
    this.setTexSize(texWidth, texHeight);
    this.texOffs(xTexOffs, yTexOffs);
  }

  private ModelRenderer() {
  }

  public ModelRenderer createShallowCopy() {
    ModelRenderer modelrenderer = new ModelRenderer();
    modelrenderer.copyFrom(this);
    return modelrenderer;
  }

  public void copyFrom(ModelRenderer renderer) {
    this.xRot = renderer.xRot;
    this.yRot = renderer.yRot;
    this.zRot = renderer.zRot;
    this.x = renderer.x;
    this.y = renderer.y;
    this.z = renderer.z;
  }

  public void addChild(ModelRenderer renderer) {
    this.children.add(renderer);
  }

  public ModelRenderer texOffs(int xTexOffs, int yTexOffs) {
    this.xTexOffs = xTexOffs;
    this.yTexOffs = yTexOffs;
    return this;
  }

  public ModelRenderer addBox(String p_217178_1_,
                              float x, float y, float z,
                              int xLen, int yLen, int zLen,
                              float v,
                              int xTexOffs, int yTexOffs) {
    texOffs(xTexOffs, yTexOffs);
    addBox(xTexOffs, yTexOffs,
        x, y, z,
        (float)xLen, (float)yLen, (float)zLen,
        v, v, v,
        mirror, false);
    return this;
  }

  public ModelRenderer addBox(float x, float y, float z,
                              float xLen, float yLen, float zLen) {
    addBox(
        xTexOffs, yTexOffs,
        x, y, z,
        xLen, yLen, zLen,
        0.0F, 0.0F, 0.0F,
        mirror, false);
    return this;
  }

  public ModelRenderer addBox(float x, float y, float z,
                              float xLen, float yLen, float zLen,
                              boolean mirror) {
    addBox(xTexOffs, yTexOffs,
        x, y, z,
        xLen, yLen, zLen,
        0.0F, 0.0F, 0.0F,
        mirror, false);
    return this;
  }

  public void addBox(float x, float y, float z,
                     float xLen, float yLen, float zLen,
                     float v) {
    addBox(xTexOffs, yTexOffs,
        x, y, z,
        xLen, yLen, zLen,
        v, v, v,
        mirror, false);
  }

  public void addBox(float x, float y, float z,
                     float xLen, float yLen, float zLen,
                     float v1, float v2, float v3) {
    addBox(xTexOffs, yTexOffs,
        x, y, z,
        xLen, yLen, zLen,
        v1, v2, v3,
        mirror, false);
  }

  public void addBox(float x, float y, float z,
                     float xLen, float yLen, float zLen,
                     float v, boolean mirror) {
    addBox(xTexOffs, yTexOffs,
        x, y, z,
        xLen, yLen, zLen,
        v, v, v,
        mirror, false);
  }

  private void addBox(int xTexOffs, int yTexOffs,
                      float x, float y, float z,
                      float xLen, float yLen, float zLen,
                      float v1, float v2, float v3,
                      boolean mirror, boolean p_228305_13_) {
    this.cubes.add(new ModelBox(xTexOffs, yTexOffs,
        x, y, z,
        xLen, yLen, zLen,
        v1, v2, v3,
        mirror, xTexSize, yTexSize));
  }

  public void setPos(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn) {
    this.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
  }

  public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder,
                     int packedLightIn, int packedOverlayIn,
                     float red, float green, float blue, float alpha) {
    if (this.visible) {
      if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
        matrixStack.pushPose();
        this.translateAndRotate(matrixStack);
        this.compile(matrixStack.last(), vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        for(ModelRenderer modelrenderer : this.children) {
          modelrenderer.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        matrixStack.popPose();
      }
    }
  }

  public void translateAndRotate(MatrixStack ms) {
    ms.translate(x / 16.0F, y / 16.0F, z / 16.0F);
    if (zRot != 0.0F) {
      ms.mulPose(Vector3f.ZP.rotation(zRot));
    }

    if (yRot != 0.0F) {
      ms.mulPose(Vector3f.YP.rotation(yRot));
    }

    if (xRot != 0.0F) {
      ms.mulPose(Vector3f.XP.rotation(xRot));
    }

  }

  private void compile(MatrixStack.Entry entry, IVertexBuilder vertexBuilder,
                       int packedLightIn, int packedOverlayIn,
                       float red, float green, float blue, float alpha) {
    Matrix4f pose = entry.pose();
    Matrix3f normal = entry.normal();

    for(ModelBox modelBox : this.cubes) {
      for(TexturedQuad texturedQuad : modelBox.polygons) {
        Vector3f normalCopy = texturedQuad.normal.copy();
        normalCopy.transform(normal);
        float vx = normalCopy.x();
        float vy = normalCopy.y();
        float vz = normalCopy.z();

        for(int i = 0; i < 4; ++i) {
          PositionTextureVertex textureVertex = texturedQuad.vertices[i];
          float px = textureVertex.pos.x() / 16.0F;
          float py = textureVertex.pos.y() / 16.0F;
          float pz = textureVertex.pos.z() / 16.0F;

          Vector4f v = new Vector4f(px, py, pz, 1.0F);
          v.transform(pose);
          vertexBuilder.vertex(v.x(), v.y(), v.z(),
              red, green, blue, alpha,
              textureVertex.u, textureVertex.v,
              packedOverlayIn, packedLightIn, vx, vy, vz);
        }
      }
    }

  }

  public ModelRenderer setTexSize(int xTexSize, int yTexSize) {
    this.xTexSize = (float)xTexSize;
    this.yTexSize = (float)yTexSize;
    return this;
  }

  public ModelBox getRandomCube(Random random) {
    return this.cubes.get(random.nextInt(this.cubes.size()));
  }

  @OnlyIn(Dist.CLIENT)
  public static class ModelBox {
    private final TexturedQuad[] polygons;
    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;

    public ModelBox(int xTexOffs, int yTexOffs,
                    float minX, float minY, float minZ,
                    float xLen, float yLen, float zLen,
                    float xOffset, float yOffset, float zOffset,
                    boolean mirror,
                    float xTexSize, float yTexSize) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = minX + xLen;
      this.maxY = minY + yLen;
      this.maxZ = minZ + zLen;

      this.polygons = new TexturedQuad[6];

      float f = minX + xLen;
      float f1 = minY + yLen;
      float f2 = minZ + zLen;
      minX = minX - xOffset;
      minY = minY - yOffset;
      minZ = minZ - zOffset;
      f = f + xOffset;
      f1 = f1 + yOffset;
      f2 = f2 + zOffset;

      if (mirror) {
        float f3 = f;
        f = minX;
        minX = f3;
      }

      PositionTextureVertex v7 = new PositionTextureVertex(minX, minY, minZ, 0.0F, 0.0F);
      PositionTextureVertex v = new PositionTextureVertex(f, minY, minZ, 0.0F, 8.0F);
      PositionTextureVertex v1 = new PositionTextureVertex(f, f1, minZ, 8.0F, 8.0F);
      PositionTextureVertex v2 = new PositionTextureVertex(minX, f1, minZ, 8.0F, 0.0F);
      PositionTextureVertex v3 = new PositionTextureVertex(minX, minY, f2, 0.0F, 0.0F);
      PositionTextureVertex v4 = new PositionTextureVertex(f, minY, f2, 0.0F, 8.0F);
      PositionTextureVertex v5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
      PositionTextureVertex v6 = new PositionTextureVertex(minX, f1, f2, 8.0F, 0.0F);

      float f4 = (float)xTexOffs;
      float f5 = (float)xTexOffs + zLen;
      float f6 = (float)xTexOffs + zLen + xLen;
      float f7 = (float)xTexOffs + zLen + xLen + xLen;
      float f8 = (float)xTexOffs + zLen + xLen + zLen;
      float f9 = (float)xTexOffs + zLen + xLen + zLen + xLen;
      float f10 = (float)yTexOffs;
      float f11 = (float)yTexOffs + zLen;
      float f12 = (float)yTexOffs + zLen + yLen;

      this.polygons[2] = new TexturedQuad(new PositionTextureVertex[]{v4, v3, v7, v}, f5, f10, f6, f11, xTexSize, yTexSize, mirror, Direction.DOWN);
      this.polygons[3] = new TexturedQuad(new PositionTextureVertex[]{v1, v2, v6, v5}, f6, f11, f7, f10, xTexSize, yTexSize, mirror, Direction.UP);
      this.polygons[1] = new TexturedQuad(new PositionTextureVertex[]{v7, v3, v6, v2}, f4, f11, f5, f12, xTexSize, yTexSize, mirror, Direction.WEST);
      this.polygons[4] = new TexturedQuad(new PositionTextureVertex[]{v, v7, v2, v1}, f5, f11, f6, f12, xTexSize, yTexSize, mirror, Direction.NORTH);
      this.polygons[0] = new TexturedQuad(new PositionTextureVertex[]{v4, v, v1, v5}, f6, f11, f8, f12, xTexSize, yTexSize, mirror, Direction.EAST);
      this.polygons[5] = new TexturedQuad(new PositionTextureVertex[]{v3, v4, v5, v6}, f8, f11, f9, f12, xTexSize, yTexSize, mirror, Direction.SOUTH);
    }
  }

  @OnlyIn(Dist.CLIENT)
  static class PositionTextureVertex {
    public final Vector3f pos;
    public final float u;
    public final float v;

    public PositionTextureVertex(float x, float y, float z, float u, float v) {
      this(new Vector3f(x, y, z), u, v);
    }

    public PositionTextureVertex remap(float u, float v) {
      return new PositionTextureVertex(pos, u, v);
    }

    public PositionTextureVertex(Vector3f pos, float u, float v) {
      this.pos = pos;
      this.u = u;
      this.v = v;
    }
  }

  @OnlyIn(Dist.CLIENT)
  static class TexturedQuad {
    public final PositionTextureVertex[] vertices;
    public final Vector3f normal;

    public TexturedQuad(PositionTextureVertex[] textureVertices,
                        float p_i225951_2_, float p_i225951_3_, float p_i225951_4_,
                        float p_i225951_5_, float p_i225951_6_, float p_i225951_7_,
                        boolean p_i225951_8_, Direction p_i225951_9_) {
      this.vertices = textureVertices;
      float f = 0.0F / p_i225951_6_;
      float f1 = 0.0F / p_i225951_7_;
      textureVertices[0] = textureVertices[0].remap(p_i225951_4_ / p_i225951_6_ - f, p_i225951_3_ / p_i225951_7_ + f1);
      textureVertices[1] = textureVertices[1].remap(p_i225951_2_ / p_i225951_6_ + f, p_i225951_3_ / p_i225951_7_ + f1);
      textureVertices[2] = textureVertices[2].remap(p_i225951_2_ / p_i225951_6_ + f, p_i225951_5_ / p_i225951_7_ - f1);
      textureVertices[3] = textureVertices[3].remap(p_i225951_4_ / p_i225951_6_ - f, p_i225951_5_ / p_i225951_7_ - f1);
      if (p_i225951_8_) {
        int i = textureVertices.length;

        for(int j = 0; j < i / 2; ++j) {
          PositionTextureVertex textureVertex = textureVertices[j];
          textureVertices[j] = textureVertices[i - 1 - j];
          textureVertices[i - 1 - j] = textureVertex;
        }
      }

      this.normal = p_i225951_9_.step();
      if (p_i225951_8_) {
        this.normal.mul(-1.0F, 1.0F, 1.0F);
      }

    }
  }
}