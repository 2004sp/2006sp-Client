package client;

import java.util.Arrays;
import java.util.Random;
import java.io.IOException;
public final class BitmapFont extends Rasterizer2D {
   private final byte[][] glyphPixels = new byte[256][];
   private final int[] glyphWidths = new int[256];
   private final int[] glyphHeights = new int[256];
   private final int[] xOffsets = new int[256];
   private final int[] yOffsets = new int[256];
   private final int[] glyphAdvances = new int[256];
   public int lineHeight;
   private boolean strikethroughEnabled;

   public BitmapFont(Cache cache, String name) throws IOException {
      int metricsGroup = cache.readReferenceTable(13).getGroupId(name);
      if (metricsGroup < 0) throw new IOException("Missing 443 font metrics " + name);
      byte[] metrics = cache.readFile(13, metricsGroup, 0);
      if (metrics.length != 256) {
         throw new IOException("Unsupported 443 font metrics for " + name + ": " + metrics.length);
      }
      Sprites.DecodedSprite[] glyphs = Sprites.load(cache, name);
      if (glyphs.length != 256) {
         throw new IOException("Expected 256 glyphs in 443 font " + name);
      }
      for (int i = 0; i < glyphs.length; i++) {
         Sprites.DecodedSprite glyph = glyphs[i];
         glyphPixels[i] = glyph.toFontMask();
         glyphWidths[i] = glyph.width;
         glyphHeights[i] = glyph.height;
         xOffsets[i] = glyph.xOffset;
         yOffsets[i] = glyph.yOffset;
         glyphAdvances[i] = metrics[i] & 255;
         if (i < 128) lineHeight = Math.max(lineHeight, glyph.yOffset + glyph.height);
      }
   }

   public BitmapFont(boolean flag, String text, Archive archive) {
      new Random();
      this.strikethroughEnabled = false;
      byte[] fontData = archive.getFile(text + ".dat");
      byte[] fontIndex = archive.getFile("index.dat");
      Buffer buffer = new Buffer(fontData);
      int fontIndexOffset = buffer.readUnsignedShort();
      Buffer buffer2;
      (buffer2 = new Buffer(fontIndex)).currentPosition = fontIndexOffset + 4;
      int indexHeaderPos = buffer2.currentPosition;
      int decodedUnsignedByte;
      if ((decodedUnsignedByte = buffer2.readUnsignedByte()) > 0) {
         buffer2.currentPosition += 3 * (decodedUnsignedByte - 1);
      }
      int glyphTablePos = buffer2.currentPosition;

      for (int xOffsetIndex = 0; xOffsetIndex < 256; xOffsetIndex++) {
         this.xOffsets[xOffsetIndex] = buffer2.readUnsignedByte();
         this.yOffsets[xOffsetIndex] = buffer2.readUnsignedByte();
         int glyphAdvanceOrGlyphWidths = this.glyphWidths[xOffsetIndex] = buffer2.readUnsignedShort();
         int sourceLineHeight = this.glyphHeights[xOffsetIndex] = buffer2.readUnsignedShort();
         int fontDataLengthOrReadUnsignedByte = buffer2.readUnsignedByte();
         int localLength = glyphAdvanceOrGlyphWidths * sourceLineHeight;
         if ((fontDataLengthOrReadUnsignedByte == 0 || fontDataLengthOrReadUnsignedByte == 1) && (localLength < 0 || buffer.currentPosition + localLength > fontData.length)) {
            throw new IllegalStateException(
               "BitmapFont " + text
                  + " glyph=" + xOffsetIndex
                  + " dataPos=" + buffer.currentPosition
                  + " dataLen=" + fontData.length
                  + " fontIndexOffset=" + fontIndexOffset
                  + " indexHeaderPos=" + indexHeaderPos
                  + " variantCount=" + decodedUnsignedByte
                  + " glyphTablePos=" + glyphTablePos
                  + " indexPos=" + buffer2.currentPosition
                  + " indexLen=" + fontIndex.length
                  + " xOffset=" + this.xOffsets[xOffsetIndex]
                  + " yOffset=" + this.yOffsets[xOffsetIndex]
                  + " width=" + glyphAdvanceOrGlyphWidths
                  + " height=" + sourceLineHeight
                  + " bytes=" + localLength
                  + " packing=" + fontDataLengthOrReadUnsignedByte
                  + " indexHead=" + Arrays.toString(Arrays.copyOfRange(fontIndex, 0, Math.min(24, fontIndex.length)))
            );
         }
         this.glyphPixels[xOffsetIndex] = new byte[localLength];
         if (fontDataLengthOrReadUnsignedByte == 0) {
            for (int localIndex = 0; localIndex < localLength; localIndex++) {
               this.glyphPixels[xOffsetIndex][localIndex] = buffer.readByte();
            }
         } else if (fontDataLengthOrReadUnsignedByte == 1) {
            for (int loopIndex = 0; loopIndex < glyphAdvanceOrGlyphWidths; loopIndex++) {
               for (int loopIndex2 = 0; loopIndex2 < sourceLineHeight; loopIndex2++) {
                  this.glyphPixels[xOffsetIndex][loopIndex + loopIndex2 * glyphAdvanceOrGlyphWidths] = buffer.readByte();
               }
            }
         }

         if (sourceLineHeight > this.lineHeight && xOffsetIndex < 128) {
            this.lineHeight = sourceLineHeight;
         }

         this.xOffsets[xOffsetIndex] = 1;
         this.glyphAdvances[xOffsetIndex] = glyphAdvanceOrGlyphWidths + 2;
         byte byteCode = 0;

         for (int loopIndex3 = sourceLineHeight / 7; loopIndex3 < sourceLineHeight; loopIndex3++) {
            byteCode += this.glyphPixels[xOffsetIndex][loopIndex3 * glyphAdvanceOrGlyphWidths];
         }

         if (byteCode <= sourceLineHeight / 7) {
            this.glyphAdvances[xOffsetIndex]--;
            this.xOffsets[xOffsetIndex] = 0;
         }

         byteCode = 0;

         for (int loopIndex4 = sourceLineHeight / 7; loopIndex4 < sourceLineHeight; loopIndex4++) {
            byteCode += this.glyphPixels[xOffsetIndex][glyphAdvanceOrGlyphWidths - 1 + loopIndex4 * glyphAdvanceOrGlyphWidths];
         }

         if (byteCode <= sourceLineHeight / 7) {
            this.glyphAdvances[xOffsetIndex]--;
         }
      }

      if (flag) {
         this.glyphAdvances[32] = this.glyphAdvances[73];
      } else {
         this.glyphAdvances[32] = this.glyphAdvances[105];
      }
   }
   public final void textRight(String text, int scalarArgument, int chatPrivilege, int spriteDrawY) {
      this.textLeft(chatPrivilege, text, spriteDrawY, scalarArgument - this.getRawTextWidth(text));
   }
   public final void textCenter(int chatPrivilege, String text, int spriteDrawY, int spriteDrawX) {
      this.textLeft(chatPrivilege, text, spriteDrawY, spriteDrawX - this.getRawTextWidth(text) / 2);
   }
   public final void textCenterShadow(int xpDropColor, int scalarArgument, String text, int scalarArgument2, boolean flag) {
      this.textLeftShadow(flag, scalarArgument - this.getTextWidth(text) / 2, xpDropColor, text, scalarArgument2);
   }
   public final int getTextWidth(String text) {
      if (text == null) {
         return 0;
      }

      int scalar = 0;

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         if (text.charAt(loopIndex) == '@' && loopIndex + 4 < text.length() && text.charAt(loopIndex + 4) == '@') {
            loopIndex += 4;
         } else {
            scalar += this.glyphAdvances[text.charAt(loopIndex)];
         }
      }

      return scalar;
   }
   public final int getRawTextWidth(String text) {
      if (text == null) {
         return 0;
      }

      int scalar = 0;

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         scalar += this.glyphAdvances[text.charAt(loopIndex)];
      }

      return scalar;
   }
   public final void textLeft(int chatPrivilege, String text, int spriteDrawY, int scalarArgument) {
      if (text != null) {
         spriteDrawY -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            char glyphPixelIndex;
            if ((glyphPixelIndex = text.charAt(loopIndex)) != ' ') {
               this.drawGlyph(
                  this.glyphPixels[glyphPixelIndex], scalarArgument + this.xOffsets[glyphPixelIndex], spriteDrawY + this.yOffsets[glyphPixelIndex], this.glyphWidths[glyphPixelIndex], this.glyphHeights[glyphPixelIndex], chatPrivilege
               );
            }

            scalarArgument += this.glyphAdvances[glyphPixelIndex];
         }
      }
   }
   public final void textLeftShadow(int scalarArgument, String text, int scalarArgument2, int scalarArgument3, boolean flag) {
      if (text != null) {
         scalarArgument2 -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            char character = text.charAt(loopIndex);
            if (character != ' ') {
               this.drawGlyph(
                  this.glyphPixels[character], scalarArgument3 + this.xOffsets[character] + 1, scalarArgument2 + this.yOffsets[character] + 1, this.glyphWidths[character], this.glyphHeights[character], 0
               );
               this.drawGlyph(
                  this.glyphPixels[character], scalarArgument3 + this.xOffsets[character], scalarArgument2 + this.yOffsets[character], this.glyphWidths[character], this.glyphHeights[character], 8366591
               );
            }

            scalarArgument3 += this.glyphAdvances[character];
         }
      }
   }
   public final void drawCenteredStringWaveY(int scalarArgument, String text, int spriteDrawX, int sceneCycle, int spriteDrawY) {
      if (text != null) {
         spriteDrawX -= this.getRawTextWidth(text) / 2;
         spriteDrawY -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            char glyphPixelIndex;
            if ((glyphPixelIndex = text.charAt(loopIndex)) != ' ') {
               this.drawGlyph(
                  this.glyphPixels[glyphPixelIndex],
                  spriteDrawX + this.xOffsets[glyphPixelIndex],
                  spriteDrawY + this.yOffsets[glyphPixelIndex] + (int)(Math.sin(loopIndex / 2.0 + sceneCycle / 5.0) * 5.0),
                  this.glyphWidths[glyphPixelIndex],
                  this.glyphHeights[glyphPixelIndex],
                  scalarArgument
               );
            }

            spriteDrawX += this.glyphAdvances[glyphPixelIndex];
         }
      }
   }
   public final void drawCenteredStringWaveXY(int spriteDrawX, String text, int sceneCycle, int spriteDrawY, int scalarArgument) {
      if (text != null) {
         spriteDrawX -= this.getRawTextWidth(text) / 2;
         spriteDrawY -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            char glyphPixelIndex;
            if ((glyphPixelIndex = text.charAt(loopIndex)) != ' ') {
               this.drawGlyph(
                  this.glyphPixels[glyphPixelIndex],
                  spriteDrawX + this.xOffsets[glyphPixelIndex] + (int)(Math.sin(loopIndex / 5.0 + sceneCycle / 5.0) * 5.0),
                  spriteDrawY + this.yOffsets[glyphPixelIndex] + (int)(Math.sin(loopIndex / 3.0 + sceneCycle / 5.0) * 5.0),
                  this.glyphWidths[glyphPixelIndex],
                  this.glyphHeights[glyphPixelIndex],
                  scalarArgument
               );
            }

            spriteDrawX += this.glyphAdvances[glyphPixelIndex];
         }
      }
   }
   public final void drawCenteredStringWaveXYMove(int scalarArgument, String text, int sceneCycle, int spriteDrawY, int spriteDrawX, int scalarArgument2) {
      if (text != null) {
         double calculation;
         if ((calculation = 7.0 - scalarArgument / 8.0) < 0.0) {
            calculation = 0.0;
         }

         spriteDrawX -= this.getRawTextWidth(text) / 2;
         spriteDrawY -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            char glyphPixelIndex;
            if ((glyphPixelIndex = text.charAt(loopIndex)) != ' ') {
               this.drawGlyph(
                  this.glyphPixels[glyphPixelIndex],
                  spriteDrawX + this.xOffsets[glyphPixelIndex],
                  spriteDrawY + this.yOffsets[glyphPixelIndex] + (int)(Math.sin(loopIndex / 1.5 + sceneCycle) * calculation),
                  this.glyphWidths[glyphPixelIndex],
                  this.glyphHeights[glyphPixelIndex],
                  scalarArgument2
               );
            }

            spriteDrawX += this.glyphAdvances[glyphPixelIndex];
         }
      }
   }
   public final void textLeftShadow(boolean flag, int scalarArgument, int xpDropColor, String text, int baselineY) {
      this.strikethroughEnabled = false;
      int scalar = scalarArgument;
      if (text != null) {
         baselineY -= this.lineHeight;

         for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
            if (text.charAt(loopIndex) == '@' && loopIndex + 4 < text.length() && text.charAt(loopIndex + 4) == '@') {
               String substring = text.substring(loopIndex + 1, loopIndex + 4);
               BitmapFont bitmapFont = this;
               int localGroundItemOtherMenuColor;
               if (substring.equals("red")) {
                  localGroundItemOtherMenuColor = 16711680;
               } else if (substring.equals("ud1")) {
                  localGroundItemOtherMenuColor = Client.groundItemOtherMenuColor;
               } else if (substring.equals("ud2")) {
                  localGroundItemOtherMenuColor = Client.groundItemRareMenuColor;
               } else if (substring.equals("gre")) {
                  localGroundItemOtherMenuColor = 65280;
               } else if (substring.equals("blu")) {
                  localGroundItemOtherMenuColor = 255;
               } else if (substring.equals("yel")) {
                  localGroundItemOtherMenuColor = 16776960;
               } else if (substring.equals("cya")) {
                  localGroundItemOtherMenuColor = 65535;
               } else if (substring.equals("mag")) {
                  localGroundItemOtherMenuColor = 16711935;
               } else if (substring.equals("whi")) {
                  localGroundItemOtherMenuColor = 16777215;
               } else if (substring.equals("bla")) {
                  localGroundItemOtherMenuColor = 0;
               } else if (substring.equals("lre")) {
                  localGroundItemOtherMenuColor = 16748608;
               } else if (substring.equals("dre")) {
                  localGroundItemOtherMenuColor = 8388608;
               } else if (substring.equals("dbl")) {
                  localGroundItemOtherMenuColor = 128;
               } else if (substring.equals("or1")) {
                  localGroundItemOtherMenuColor = 16756736;
               } else if (substring.equals("or2")) {
                  localGroundItemOtherMenuColor = 16740352;
               } else if (substring.equals("or3")) {
                  localGroundItemOtherMenuColor = 16723968;
               } else if (substring.equals("gr1")) {
                  localGroundItemOtherMenuColor = 12648192;
               } else if (substring.equals("gr2")) {
                  localGroundItemOtherMenuColor = 8453888;
               } else if (substring.equals("gr3")) {
                  localGroundItemOtherMenuColor = 4259584;
               } else {
                  if (substring.equals("str")) {
                     bitmapFont.strikethroughEnabled = true;
                  }

                  if (substring.equals("end")) {
                     bitmapFont.strikethroughEnabled = false;
                  }

                  localGroundItemOtherMenuColor = -1;
               }

               int sourceLocalGroundItemOtherMenuColor = localGroundItemOtherMenuColor;
               if (localGroundItemOtherMenuColor != -1) {
                  xpDropColor = sourceLocalGroundItemOtherMenuColor;
               }

               loopIndex += 4;
            } else {
               char glyphPixelIndex;
               if ((glyphPixelIndex = text.charAt(loopIndex)) != ' ') {
                  if (flag) {
                     this.drawGlyph(
                        this.glyphPixels[glyphPixelIndex],
                        scalarArgument + this.xOffsets[glyphPixelIndex] + 1,
                        baselineY + this.yOffsets[glyphPixelIndex] + 1,
                        this.glyphWidths[glyphPixelIndex],
                        this.glyphHeights[glyphPixelIndex],
                        0
                     );
                  }

                  this.drawGlyph(
                     this.glyphPixels[glyphPixelIndex], scalarArgument + this.xOffsets[glyphPixelIndex], baselineY + this.yOffsets[glyphPixelIndex], this.glyphWidths[glyphPixelIndex], this.glyphHeights[glyphPixelIndex], xpDropColor
                  );
               }

               scalarArgument += this.glyphAdvances[glyphPixelIndex];
            }
         }

         if (this.strikethroughEnabled) {
            Rasterizer2D.drawHorizontalLine(baselineY + (int)(this.lineHeight * 0.7), 8388608, scalarArgument - scalar, scalar);
         }
      }
   }
   private void drawGlyph(byte[] glyphPixel, int newTopX, int newTopY, int glyphWidth, int newIndex, int positionArgument) {
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int localWidth = Rasterizer2D.width - glyphWidth;
      int scalar2 = 0;
      int sourceGlyphWidth = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         newIndex -= localTopY;
         newTopY = Rasterizer2D.topY;
         sourceGlyphWidth = 0 + localTopY * glyphWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + newIndex >= Rasterizer2D.bottomY) {
         newIndex -= newTopY + newIndex - Rasterizer2D.bottomY + 1;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         glyphWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         sourceGlyphWidth += localTopX;
         scalar += localTopX;
         scalar2 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + glyphWidth >= Rasterizer2D.bottomX) {
         int scalar3 = newTopX + glyphWidth - Rasterizer2D.bottomX + 1;
         glyphWidth -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (glyphWidth > 0 && newIndex > 0) {
         Rasterizer2D.markGpuDirty(newTopX, newTopY, glyphWidth, newIndex);
         int position = newIndex;
         int sourceGlyphWidth2 = glyphWidth;
         newIndex = scalar;
         glyphWidth = sourceGlyphWidth;
         newTopY = positionArgument;
         byte[] sourceGlyphPixel = glyphPixel;
         int[] pixels = Rasterizer2D.pixels;
         sourceGlyphWidth = -(sourceGlyphWidth2 >> 2);
         positionArgument = -(sourceGlyphWidth2 & 3);

         for (int loopIndex = -position; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = sourceGlyphWidth; loopIndex2 < 0; loopIndex2++) {
               if (sourceGlyphPixel[glyphWidth++] != 0) {
                  pixels[newIndex++] = newTopY;
               } else {
                  newIndex++;
               }

               if (sourceGlyphPixel[glyphWidth++] != 0) {
                  pixels[newIndex++] = newTopY;
               } else {
                  newIndex++;
               }

               if (sourceGlyphPixel[glyphWidth++] != 0) {
                  pixels[newIndex++] = newTopY;
               } else {
                  newIndex++;
               }

               if (sourceGlyphPixel[glyphWidth++] != 0) {
                  pixels[newIndex++] = newTopY;
               } else {
                  newIndex++;
               }
            }

            for (int loopIndex3 = positionArgument; loopIndex3 < 0; loopIndex3++) {
               if (sourceGlyphPixel[glyphWidth++] != 0) {
                  pixels[newIndex++] = newTopY;
               } else {
                  newIndex++;
               }
            }

            newIndex += localWidth;
            glyphWidth += scalar2;
         }
      }
   }
}
