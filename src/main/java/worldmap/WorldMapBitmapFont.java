package worldmap;

import java.util.Random;
public final class WorldMapBitmapFont extends WorldMapRasterizer2D {
   private byte[][] glyphPixels = new byte[256][];
   private int[] glyphWidths = new int[256];
   private int[] glyphHeights = new int[256];
   private int[] xOffsets = new int[256];
   private int[] yOffsets = new int[256];
   private int[] glyphAdvances = new int[256];
   private int lineHeight = 0;
   private int getRawTextWidth(String text) {
      if (text == null) {
         return 0;
      }

      int scalar = 0;

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         scalar += this.glyphAdvances[text.charAt(loopIndex)];
      }

      return scalar;
   }
   public final void textLeft(String text, int scalarArgument, int scalarArgument2, int scalarArgument3) {
      if (text != null) {
         scalarArgument2 -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            char glyphPixelIndex;
            if ((glyphPixelIndex = text.charAt(loopIndex)) != ' ') {
               byte[] glyphPixel = this.glyphPixels[glyphPixelIndex];
               int scalar = scalarArgument + this.xOffsets[glyphPixelIndex];
               int scalar2 = scalarArgument2 + this.yOffsets[glyphPixelIndex];
               int glyphWidth = this.glyphWidths[glyphPixelIndex];
               int glyphHeight = this.glyphHeights[glyphPixelIndex];
               int sourceTopX = scalarArgument3;
               int sourceGlyphHeight = glyphHeight;
               int sourceGlyphWidth = glyphWidth;
               int topY = scalar2;
               int topX = scalar;
               byte[] sourceGlyphPixel = glyphPixel;
               int scalar3 = topX + topY * WorldMapRasterizer2D.width;
               int localWidth = WorldMapRasterizer2D.width - sourceGlyphWidth;
               int scalar4 = 0;
               int sourceTopY = 0;
               if (topY < WorldMapRasterizer2D.topY) {
                  int localTopY = WorldMapRasterizer2D.topY - topY;
                  sourceGlyphHeight -= localTopY;
                  topY = WorldMapRasterizer2D.topY;
                  sourceTopY = 0 + localTopY * sourceGlyphWidth;
                  scalar3 += localTopY * WorldMapRasterizer2D.width;
               }

               if (topY + sourceGlyphHeight >= WorldMapRasterizer2D.bottomY) {
                  sourceGlyphHeight -= topY + sourceGlyphHeight - WorldMapRasterizer2D.bottomY + 1;
               }

               if (topX < WorldMapRasterizer2D.topX) {
                  int localTopX = WorldMapRasterizer2D.topX - topX;
                  sourceGlyphWidth -= localTopX;
                  topX = WorldMapRasterizer2D.topX;
                  sourceTopY += localTopX;
                  scalar3 += localTopX;
                  scalar4 = localTopX + 0;
                  localWidth += localTopX;
               }

               if (topX + sourceGlyphWidth >= WorldMapRasterizer2D.bottomX) {
                  int scalar5 = topX + sourceGlyphWidth - WorldMapRasterizer2D.bottomX + 1;
                  sourceGlyphWidth -= scalar5;
                  scalar4 += scalar5;
                  localWidth += scalar5;
               }

               if (sourceGlyphWidth > 0 && sourceGlyphHeight > 0) {
                  int position = sourceGlyphHeight;
                  sourceGlyphHeight = sourceGlyphWidth;
                  sourceGlyphWidth = scalar3;
                  topY = sourceTopY;
                  topX = sourceTopX;
                  int[] pixels = WorldMapRasterizer2D.pixels;
                  sourceTopY = -(sourceGlyphHeight >> 2);
                  sourceGlyphHeight = -(sourceGlyphHeight & 3);

                  for (int loopIndex2 = -position; loopIndex2 < 0; loopIndex2++) {
                     for (int loopIndex3 = sourceTopY; loopIndex3 < 0; loopIndex3++) {
                        if (sourceGlyphPixel[topY++] != 0) {
                           pixels[sourceGlyphWidth++] = topX;
                        } else {
                           sourceGlyphWidth++;
                        }

                        if (sourceGlyphPixel[topY++] != 0) {
                           pixels[sourceGlyphWidth++] = topX;
                        } else {
                           sourceGlyphWidth++;
                        }

                        if (sourceGlyphPixel[topY++] != 0) {
                           pixels[sourceGlyphWidth++] = topX;
                        } else {
                           sourceGlyphWidth++;
                        }

                        if (sourceGlyphPixel[topY++] != 0) {
                           pixels[sourceGlyphWidth++] = topX;
                        } else {
                           sourceGlyphWidth++;
                        }
                     }

                     for (int loopIndex4 = sourceGlyphHeight; loopIndex4 < 0; loopIndex4++) {
                        if (sourceGlyphPixel[topY++] != 0) {
                           pixels[sourceGlyphWidth++] = topX;
                        } else {
                           sourceGlyphWidth++;
                        }
                     }

                     sourceGlyphWidth += localWidth;
                     topY += scalar4;
                  }
               }
            }

            scalarArgument += this.glyphAdvances[glyphPixelIndex];
         }
      }
   }

   public WorldMapBitmapFont(WorldMapArchive worldMapArchive, String text, boolean flag) {
      new Random();
      WorldMapBuffer worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile(text + ".dat", null));
      WorldMapBuffer worldMapBuffer2;
      (worldMapBuffer2 = new WorldMapBuffer(worldMapArchive.getFile("index.dat", null))).currentPosition = worldMapBuffer.readUnsignedShort() + 4;
      int indexVersion = worldMapBuffer2.readUnsignedByte();
      if (indexVersion > 0) {
         worldMapBuffer2.currentPosition += 3 * (indexVersion - 1);
      }

      for (int xOffsetIndex = 0; xOffsetIndex < 256; xOffsetIndex++) {
         this.xOffsets[xOffsetIndex] = worldMapBuffer2.readUnsignedByte();
         this.yOffsets[xOffsetIndex] = worldMapBuffer2.readUnsignedByte();
         int glyphAdvanceOrGlyphWidths = this.glyphWidths[xOffsetIndex] = worldMapBuffer2.readUnsignedShort();
         int sourceLineHeight = this.glyphHeights[xOffsetIndex] = worldMapBuffer2.readUnsignedShort();
         int pixelOrder = worldMapBuffer2.readUnsignedByte();
         int length = glyphAdvanceOrGlyphWidths * sourceLineHeight;
         this.glyphPixels[xOffsetIndex] = new byte[length];
         if (pixelOrder == 0) {
            for (int loopIndex = 0; loopIndex < length; loopIndex++) {
               this.glyphPixels[xOffsetIndex][loopIndex] = worldMapBuffer.getByte();
            }
         } else if (pixelOrder == 1) {
            for (int loopIndex2 = 0; loopIndex2 < glyphAdvanceOrGlyphWidths; loopIndex2++) {
               for (int loopIndex3 = 0; loopIndex3 < sourceLineHeight; loopIndex3++) {
                  this.glyphPixels[xOffsetIndex][loopIndex2 + loopIndex3 * glyphAdvanceOrGlyphWidths] = worldMapBuffer.getByte();
               }
            }
         }

         if (sourceLineHeight > this.lineHeight && xOffsetIndex < 128) {
            this.lineHeight = sourceLineHeight;
         }

         this.xOffsets[xOffsetIndex] = 1;
         this.glyphAdvances[xOffsetIndex] = glyphAdvanceOrGlyphWidths + 2;
         byte byteCode = 0;

         for (int loopIndex4 = sourceLineHeight / 7; loopIndex4 < sourceLineHeight; loopIndex4++) {
            byteCode += this.glyphPixels[xOffsetIndex][loopIndex4 * glyphAdvanceOrGlyphWidths];
         }

         if (byteCode <= sourceLineHeight / 7) {
            this.glyphAdvances[xOffsetIndex]--;
            this.xOffsets[xOffsetIndex] = 0;
         }

         byteCode = 0;

         for (int loopIndex5 = sourceLineHeight / 7; loopIndex5 < sourceLineHeight; loopIndex5++) {
            byteCode += this.glyphPixels[xOffsetIndex][glyphAdvanceOrGlyphWidths - 1 + loopIndex5 * glyphAdvanceOrGlyphWidths];
         }

         if (byteCode <= sourceLineHeight / 7) {
            this.glyphAdvances[xOffsetIndex]--;
         }
      }

      this.glyphAdvances[32] = this.glyphAdvances[105];
   }
   public final void textCenter(String text, int scalarArgument, int scalarArgument2, int scalarArgument3) {
      this.textLeft(text, scalarArgument - this.getRawTextWidth(text) / 2, scalarArgument2, scalarArgument3);
   }
}
