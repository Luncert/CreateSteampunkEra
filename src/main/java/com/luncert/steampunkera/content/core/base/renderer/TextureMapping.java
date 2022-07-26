// package com.luncert.steampunkera.content.core.base.renderer;
//
// import net.minecraft.util.Direction;
//
// import java.util.Iterator;
//
// public class TextureMapping implements Iterable<TextureMapping.Entry> {
//
//   private final FaceUV[] mappings = new FaceUV[6];
//
//   @Override
//   public Iterator<Entry> iterator() {
//     return new Itr();
//   }
//
//   public static class FaceUV {
//
//     // uOffset, vOffset, uScale, vScale
//     public final Direction cullForDirection;
//     public final int tintIndex;
//     public float[] uvs;
//     public final int rotation;
//     public final String texture;
//
//     public FaceUV(Direction cullForDirection, int tintIndex,
//                   float[] uvs, int rotation, String textureId) {
//       this.cullForDirection = cullForDirection;
//       this.tintIndex = tintIndex;
//       this.uvs = uvs;
//       this.rotation = rotation;
//       this.texture = textureId;
//     }
//   }
//
//   public static class Entry {
//
//     final Direction face;
//     final FaceUV mapping;
//
//     public Entry(Direction face, FaceUV mapping) {
//       this.face = face;
//       this.mapping = mapping;
//     }
//   }
//
//   private class Itr implements Iterator<Entry> {
//
//     private int c = 0;
//
//     @Override
//     public boolean hasNext() {
//       return c < 6;
//     }
//
//     @Override
//     public Entry next() {
//       return new Entry(Direction.values()[c], mappings[c++]);
//     }
//   }
//
//   public static Builder builder() {
//     return new Builder();
//   }
//
//   public static class Builder {
//
//     private final TextureMapping textureMapping = new TextureMapping();
//
//     public Builder mapping(Direction face, FaceUV mapping) {
//       textureMapping.mappings[face.ordinal()] = mapping;
//       return this;
//     }
//
//     public TextureMapping build() {
//       return textureMapping;
//     }
//   }
// }
