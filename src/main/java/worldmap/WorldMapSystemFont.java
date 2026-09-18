package worldmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;
public final class WorldMapSystemFont extends WorldMapRasterizer2D {
   private boolean capturingGlyph = false;
   private int glyphDataOffset = 0;
   public byte[] glyphData = new byte[100000];
   private static int[] glyphOffsets = new int[256];

   static {
      for (int characterIndex = 0; characterIndex < 256; characterIndex++) {
         int glyphOffset;
         if ((glyphOffset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ".indexOf(characterIndex)) == -1) {
            glyphOffset = 74;
         }

         glyphOffsets[characterIndex] = glyphOffset * 9;
      }
   }
   private void captureGlyph(Font font, FontMetrics fontMetrics, char character, int positionArgument, boolean flag, WorldMapGameShell worldMapGameShell) {
      int charWidthResult;
      int scalar = charWidthResult = fontMetrics.charWidth((char)character);
      if (flag) {
         if (character == 47) {
            flag = false;
         }

         if (character == 102 || character == 116 || character == 119 || character == 118 || character == 107 || character == 120 || character == 121 || character == 65 || character == 86 || character == 87) {
            charWidthResult++;
         }
      }

      int maxAscent = fontMetrics.getMaxAscent();
      int loopIndex = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
      int height = fontMetrics.getHeight();
      Graphics graphics;
      Image image;
      (graphics = (image = worldMapGameShell.getGameComponent().createImage(charWidthResult, loopIndex)).getGraphics()).setColor(Color.black);
      graphics.fillRect(0, 0, charWidthResult, loopIndex);
      graphics.setColor(Color.white);
      graphics.setFont(font);
      graphics.drawString(String.valueOf((char)character), 0, maxAscent);
      if (flag) {
         graphics.drawString(String.valueOf((char)character), 1, maxAscent);
      }

      int[] values = new int[charWidthResult * loopIndex];
      PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, charWidthResult, loopIndex, values, 0, charWidthResult);

      try {
         pixelGrabber.grabPixels();
      } catch (Exception exception) {
      }

      image.flush();
      int left = 0;
      int top = 0;
      int sourceCharWidthResult = charWidthResult;
      int loopIndex2 = loopIndex;

      findGlyphTop:
      for (int sourceTop = 0; sourceTop < loopIndex; sourceTop++) {
         for (int loopIndex3 = 0; loopIndex3 < charWidthResult; loopIndex3++) {
            int scalar2;
            if (((scalar2 = values[loopIndex3 + sourceTop * charWidthResult]) & 16777215) != 0) {
               top = sourceTop;
               break findGlyphTop;
            }
         }
      }

      findGlyphLeft:
      for (int sourceLeft = 0; sourceLeft < charWidthResult; sourceLeft++) {
         for (int loopIndex4 = 0; loopIndex4 < loopIndex; loopIndex4++) {
            int scalar3;
            if (((scalar3 = values[sourceLeft + loopIndex4 * charWidthResult]) & 16777215) != 0) {
               left = sourceLeft;
               break findGlyphLeft;
            }
         }
      }

      findGlyphBottom:
      for (int loopIndex5 = loopIndex - 1; loopIndex5 >= 0; loopIndex5--) {
         for (int loopIndex6 = 0; loopIndex6 < charWidthResult; loopIndex6++) {
            int scalar4;
            if (((scalar4 = values[loopIndex6 + loopIndex5 * charWidthResult]) & 16777215) != 0) {
               loopIndex2 = loopIndex5 + 1;
               break findGlyphBottom;
            }
         }
      }

      findGlyphRight:
      for (int loopIndex7 = charWidthResult - 1; loopIndex7 >= 0; loopIndex7--) {
         for (int loopIndex8 = 0; loopIndex8 < loopIndex; loopIndex8++) {
            int scalar5;
            if (((scalar5 = values[loopIndex7 + loopIndex8 * charWidthResult]) & 16777215) != 0) {
               sourceCharWidthResult = loopIndex7 + 1;
               break findGlyphRight;
            }
         }
      }

      this.glyphData[positionArgument * 9] = (byte)(this.glyphDataOffset / 16384);
      this.glyphData[positionArgument * 9 + 1] = (byte)(this.glyphDataOffset / 128 & 127);
      this.glyphData[positionArgument * 9 + 2] = (byte)(this.glyphDataOffset & 127);
      this.glyphData[positionArgument * 9 + 3] = (byte)(sourceCharWidthResult - left);
      this.glyphData[positionArgument * 9 + 4] = (byte)(loopIndex2 - top);
      this.glyphData[positionArgument * 9 + 5] = (byte)left;
      this.glyphData[positionArgument * 9 + 6] = (byte)(maxAscent - top);
      this.glyphData[positionArgument * 9 + 7] = (byte)scalar;
      this.glyphData[positionArgument * 9 + 8] = (byte)height;

      for (int sourceTop2 = top; sourceTop2 < loopIndex2; sourceTop2++) {
         for (int sourceLeft2 = left; sourceLeft2 < sourceCharWidthResult; sourceLeft2++) {
            int scalar6;
            if ((scalar6 = values[sourceLeft2 + sourceTop2 * charWidthResult] & 0xFF) > 30 && scalar6 < 230) {
               this.capturingGlyph = true;
            }

            this.glyphData[this.glyphDataOffset++] = (byte)scalar6;
         }
      }
   }

   public WorldMapSystemFont(int scalarArgument, boolean flag, WorldMapGameShell worldMapGameShell) {
      this.glyphDataOffset = 855;
      this.capturingGlyph = false;
      Font font = new Font("Helvetica", 1, scalarArgument);
      FontMetrics fontMetrics = worldMapGameShell.getFontMetrics(font);

      for (int loopIndex = 0; loopIndex < 95; loopIndex++) {
         this.captureGlyph(
            font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ".charAt(loopIndex), loopIndex, false, worldMapGameShell
         );
      }

      if (this.capturingGlyph) {
         this.glyphDataOffset = 855;
         this.capturingGlyph = false;
         Font font2 = new Font("Helvetica", 0, scalarArgument);
         FontMetrics fontMetrics2 = worldMapGameShell.getFontMetrics(font2);

         for (int loopIndex2 = 0; loopIndex2 < 95; loopIndex2++) {
            this.captureGlyph(
               font2, fontMetrics2, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ".charAt(loopIndex2), loopIndex2, false, worldMapGameShell
            );
         }

         if (!this.capturingGlyph) {
            this.glyphDataOffset = 855;
            this.capturingGlyph = false;

            for (int loopIndex3 = 0; loopIndex3 < 95; loopIndex3++) {
               this.captureGlyph(
                  font2,
                  fontMetrics2,
                  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ".charAt(loopIndex3),
                  loopIndex3,
                  true,
                  worldMapGameShell
               );
            }
         }
      }

      byte[] sourceGlyphData = new byte[this.glyphDataOffset];

      for (int glyphDataIndex = 0; glyphDataIndex < this.glyphDataOffset; glyphDataIndex++) {
         sourceGlyphData[glyphDataIndex] = this.glyphData[glyphDataIndex];
      }

      this.glyphData = sourceGlyphData;
   }
   public final void drawCenteredString(String text, int scalarArgument, int scalarArgument2, int newGlyphOffsets, boolean flag) {
      String sourceText = text;
      WorldMapSystemFont worldMapSystemFont = this;
      int localHalfWidth = 0;

      for (int loopIndex = 0; loopIndex < sourceText.length(); loopIndex++) {
         if (sourceText.charAt(loopIndex) == '@' && loopIndex + 4 < sourceText.length() && sourceText.charAt(loopIndex + 4) == '@') {
            loopIndex += 4;
         } else if (sourceText.charAt(loopIndex) == '~' && loopIndex + 4 < sourceText.length() && sourceText.charAt(loopIndex + 4) == '~') {
            loopIndex += 4;
         } else {
            localHalfWidth += worldMapSystemFont.glyphData[glyphOffsets[sourceText.charAt(loopIndex)] + 7];
         }
      }

      int halfWidth = localHalfWidth / 2;
      byte glyphDataEntry = this.glyphData[6];
      if (scalarArgument - halfWidth <= WorldMapRasterizer2D.bottomX) {
         if (scalarArgument + halfWidth >= WorldMapRasterizer2D.topX) {
            if (scalarArgument2 - glyphDataEntry <= WorldMapRasterizer2D.bottomY) {
               if (scalarArgument2 >= 0) {
                  int sourceLocalHalfWidth = scalarArgument - halfWidth;
                  boolean localFlag = true;
                  int scalar = newGlyphOffsets;
                  int scalar2 = scalarArgument2;
                  localHalfWidth = sourceLocalHalfWidth;
                  sourceText = text;
                  WorldMapSystemFont worldMapSystemFont2 = this;

                  try {
                     if (worldMapSystemFont2.capturingGlyph || scalar == 0) {
                        localFlag = false;
                     }

                     for (int loopIndex2 = 0; loopIndex2 < sourceText.length(); loopIndex2++) {
                        newGlyphOffsets = glyphOffsets[sourceText.charAt(loopIndex2)];
                        if (localFlag) {
                           worldMapSystemFont2.drawGlyph(newGlyphOffsets, localHalfWidth + 1, scalar2, 0, worldMapSystemFont2.glyphData, worldMapSystemFont2.capturingGlyph);
                           worldMapSystemFont2.drawGlyph(newGlyphOffsets, localHalfWidth, scalar2 + 1, 0, worldMapSystemFont2.glyphData, worldMapSystemFont2.capturingGlyph);
                        }

                        worldMapSystemFont2.drawGlyph(newGlyphOffsets, localHalfWidth, scalar2, scalar, worldMapSystemFont2.glyphData, worldMapSystemFont2.capturingGlyph);
                        localHalfWidth += worldMapSystemFont2.glyphData[newGlyphOffsets + 7];
                     }
                  } catch (Exception exception) {
                     System.out.println("drawstring: " + exception);
                     exception.printStackTrace();
                  }
               }
            }
         }
      }
   }
   public final int getLineHeight() {
      return this.glyphData[8] - 1;
   }
   private void drawGlyph(int glyphDataIndex, int newTopX, int newTopY, int scalarArgument, byte[] glyphData, boolean flag) {
      newTopX += glyphData[glyphDataIndex + 5];
      newTopY -= glyphData[glyphDataIndex + 6];
      int localGlyphWidth = glyphData[glyphDataIndex + 3];
      int glyphDataEntry = glyphData[glyphDataIndex + 4];
      glyphDataIndex = (glyphData[glyphDataIndex] << 14) + (glyphData[glyphDataIndex + 1] << 7) + glyphData[glyphDataIndex + 2];
      int scalar = newTopX + newTopY * WorldMapRasterizer2D.width;
      int localWidth = WorldMapRasterizer2D.width - localGlyphWidth;
      int scalar2 = 0;
      if (newTopY < WorldMapRasterizer2D.topY) {
         int localTopY = WorldMapRasterizer2D.topY - newTopY;
         glyphDataEntry -= localTopY;
         newTopY = WorldMapRasterizer2D.topY;
         glyphDataIndex += localTopY * localGlyphWidth;
         scalar += localTopY * WorldMapRasterizer2D.width;
      }

      if (newTopY + glyphDataEntry >= WorldMapRasterizer2D.bottomY) {
         glyphDataEntry -= newTopY + glyphDataEntry - WorldMapRasterizer2D.bottomY + 1;
      }

      if (newTopX < WorldMapRasterizer2D.topX) {
         int localTopX = WorldMapRasterizer2D.topX - newTopX;
         localGlyphWidth -= localTopX;
         newTopX = WorldMapRasterizer2D.topX;
         glyphDataIndex += localTopX;
         scalar += localTopX;
         scalar2 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + localGlyphWidth >= WorldMapRasterizer2D.bottomX) {
         int scalar3 = newTopX + localGlyphWidth - WorldMapRasterizer2D.bottomX + 1;
         localGlyphWidth -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (localGlyphWidth > 0 && glyphDataEntry > 0) {
         if (flag) {
            int scalar4 = scalar2;
            int sourceLocalWidth = localWidth;
            int position = glyphDataEntry;
            int glyphWidth = localGlyphWidth;
            int pixelIndex = scalar;
            int sourceGlyphDataIndex = glyphDataIndex;
            newTopY = scalarArgument;
            byte[] sourceGlyphData = glyphData;
            int[] pixels = WorldMapRasterizer2D.pixels;

            for (int loopIndex = -position; loopIndex < 0; loopIndex++) {
               for (int loopIndex2 = -glyphWidth; loopIndex2 < 0; loopIndex2++) {
                  if ((scalar2 = sourceGlyphData[sourceGlyphDataIndex++] & 255) > 30) {
                     if (scalar2 >= 230) {
                        pixels[pixelIndex++] = newTopY;
                     } else {
                        int pixel = pixels[pixelIndex];
                        pixels[pixelIndex++] = ((newTopY & 16711935) * scalar2 + (pixel & 16711935) * (256 - scalar2) & -16711936)
                              + ((newTopY & 0xFF00) * scalar2 + (pixel & 0xFF00) * (256 - scalar2) & 0xFF0000)
                           >> 8;
                     }
                  } else {
                     pixelIndex++;
                  }
               }

               pixelIndex += sourceLocalWidth;
               sourceGlyphDataIndex += scalar4;
            }
         } else {
            int scalar5 = scalar2;
            int sourceLocalWidth2 = localWidth;
            int position2 = glyphDataEntry;
            int glyphWidth = localGlyphWidth;
            int position3 = scalar;
            int sourceGlyphDataIndex2 = glyphDataIndex;
            newTopY = scalarArgument;
            byte[] sourceGlyphData2 = glyphData;
            int[] pixels2 = WorldMapRasterizer2D.pixels;

            try {
               int groups = -(glyphWidth >> 2);
               int remainder = -(glyphWidth & 3);

               for (int loopIndex3 = -position2; loopIndex3 < 0; loopIndex3++) {
                  for (int sourceGroups = groups; sourceGroups < 0; sourceGroups++) {
                     if (sourceGlyphData2[sourceGlyphDataIndex2++] != 0) {
                        pixels2[position3++] = newTopY;
                     } else {
                        position3++;
                     }

                     if (sourceGlyphData2[sourceGlyphDataIndex2++] != 0) {
                        pixels2[position3++] = newTopY;
                     } else {
                        position3++;
                     }

                     if (sourceGlyphData2[sourceGlyphDataIndex2++] != 0) {
                        pixels2[position3++] = newTopY;
                     } else {
                        position3++;
                     }

                     if (sourceGlyphData2[sourceGlyphDataIndex2++] != 0) {
                        pixels2[position3++] = newTopY;
                     } else {
                        position3++;
                     }
                  }

                  for (int sourceRemainder = remainder; sourceRemainder < 0; sourceRemainder++) {
                     if (sourceGlyphData2[sourceGlyphDataIndex2++] != 0) {
                        pixels2[position3++] = newTopY;
                     } else {
                        position3++;
                     }
                  }

                  position3 += sourceLocalWidth2;
                  sourceGlyphDataIndex2 += scalar5;
               }
            } catch (Exception exception) {
               System.out.println("plotletter: " + exception);
               exception.printStackTrace();
            }
         }
      }
   }
}
