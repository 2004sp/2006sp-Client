package client;

import java.awt.Color;
import java.io.IOException;
public final class RichTextFont extends Rasterizer2D {
   public int lineHeight = 0;
   private int[] glyphYOffsets;
   private int[] glyphHeights;
   private int[] glyphXOffsets;
   private int[] glyphWidths;
   private byte[][] glyphPixels;
   private int[] glyphAdvances;
   public Sprite[] icons;
   private static String tagNbsp = "nbsp";
   private static String tagTransparencyPrefix = "trans=";
   private static String tagShadow = "shad";
   private static String tagShadowEnd = "/shad";
   private static String tagGreaterThan = "gt";
   private static String tagStrikethroughEnd = "/str";
   private static String tagEuro = "euro";
   private static String tagColorPrefix = "col=";
   private static String tagLineBreak = "br";
   private static String tagStrikethroughColorPrefix = "str=";
   private static String tagColorEnd = "/col";
   private static String tagImagePrefix = "img=";
   private static String tagUnderlineEnd = "/u";
   private static String tagStrikethrough = "str";
   private static String tagShadowColorPrefix = "shad=";
   private static String tagLessThan = "lt";
   private static String tagSoftHyphen = "shy";
   private static String tagCopyright = "copy";
   private static String tagTransparencyEnd = "/trans";
   private static String tagTimes = "times";
   private static String tagUnderlineColorPrefix = "u=";
   private static String tagUnderline = "u";
   private static String tagRegistered = "reg";
   private static int baseTextColor = 0;
   private static int currentShadowColor = -1;
   private static int strikethroughColor = -1;
   private static int baseTransparency = 256;
   private static int justificationCurrent = 0;
   private static int underlineColor = -1;
   private static int baseShadowColor = -1;
   private static int justificationTotal = 0;
   private static int transparency = 256;
   private static int currentTextColor = 0;
   private boolean oldSyntaxStrikethroughActive = false;

   public RichTextFont(Cache cache, String name) throws IOException {
      this(cache,
         cache.readReferenceTable(8).getGroupId(name),
         cache.readReferenceTable(13).getGroupId(name),
         name);
   }

   RichTextFont(Cache cache, int spriteGroup, int metricsGroup) throws IOException {
      this(cache, spriteGroup, metricsGroup,
         "sprite " + spriteGroup + "/metrics " + metricsGroup);
   }

   private RichTextFont(Cache cache, int spriteGroup, int metricsGroup, String name) throws IOException {
      if (spriteGroup < 0 || metricsGroup < 0) throw new IOException("Missing 443 font " + name);
      byte[] metrics = cache.readFile(13, metricsGroup, 0);
      Sprites.DecodedSprite[] glyphs = Sprites.load(cache, spriteGroup);
      if (metrics.length != 256 || glyphs.length != 256) {
         throw new IOException("Unsupported 443 font " + name);
      }
      this.glyphPixels = new byte[256][];
      this.glyphWidths = new int[256];
      this.glyphHeights = new int[256];
      this.glyphXOffsets = new int[256];
      this.glyphYOffsets = new int[256];
      this.glyphAdvances = new int[256];
      for (int i = 0; i < 256; i++) {
         Sprites.DecodedSprite glyph = glyphs[i];
         glyphPixels[i] = glyph.toFontMask();
         glyphWidths[i] = glyph.width;
         glyphHeights[i] = glyph.height;
         glyphXOffsets[i] = glyph.xOffset;
         glyphYOffsets[i] = glyph.yOffset;
         glyphAdvances[i] = metrics[i] & 255;
         if (i < 128) lineHeight = Math.max(lineHeight, glyph.yOffset + glyph.height);
      }
   }

   public RichTextFont(boolean flag, String text, Archive archive) {
      this.glyphPixels = new byte[256][];
      this.glyphWidths = new int[256];
      this.glyphHeights = new int[256];
      this.glyphXOffsets = new int[256];
      this.glyphYOffsets = new int[256];
      this.glyphAdvances = new int[256];
      Buffer buffer = new Buffer(archive.getFile(text + ".dat"));
      Buffer buffer2;
      (buffer2 = new Buffer(archive.getFile("index.dat"))).currentPosition = buffer.readUnsignedShort() + 4;
      int decodedUnsignedByte;
      if ((decodedUnsignedByte = buffer2.readUnsignedByte()) > 0) {
         buffer2.currentPosition += 3 * (decodedUnsignedByte - 1);
      }

      for (int glyphXOffsetIndex = 0; glyphXOffsetIndex < 256; glyphXOffsetIndex++) {
         this.glyphXOffsets[glyphXOffsetIndex] = buffer2.readUnsignedByte();
         this.glyphYOffsets[glyphXOffsetIndex] = buffer2.readUnsignedByte();
         int glyphAdvanceOrGlyphWidths = this.glyphWidths[glyphXOffsetIndex] = buffer2.readUnsignedShort();
         int sourceLineHeight = this.glyphHeights[glyphXOffsetIndex] = buffer2.readUnsignedShort();
         int readUnsignedByte2 = buffer2.readUnsignedByte();
         int length = glyphAdvanceOrGlyphWidths * sourceLineHeight;
         this.glyphPixels[glyphXOffsetIndex] = new byte[length];
         if (readUnsignedByte2 == 0) {
            for (int loopIndex = 0; loopIndex < length; loopIndex++) {
               this.glyphPixels[glyphXOffsetIndex][loopIndex] = buffer.readByte();
            }
         } else if (readUnsignedByte2 == 1) {
            for (int loopIndex2 = 0; loopIndex2 < glyphAdvanceOrGlyphWidths; loopIndex2++) {
               for (int loopIndex3 = 0; loopIndex3 < sourceLineHeight; loopIndex3++) {
                  this.glyphPixels[glyphXOffsetIndex][loopIndex2 + loopIndex3 * glyphAdvanceOrGlyphWidths] = buffer.readByte();
               }
            }
         }

         if (sourceLineHeight > this.lineHeight && glyphXOffsetIndex < 128) {
            this.lineHeight = sourceLineHeight;
         }

         this.glyphXOffsets[glyphXOffsetIndex] = 1;
         this.glyphAdvances[glyphXOffsetIndex] = glyphAdvanceOrGlyphWidths + 2;
         byte byteCode = 0;

         for (int loopIndex4 = sourceLineHeight / 7; loopIndex4 < sourceLineHeight; loopIndex4++) {
            byteCode += this.glyphPixels[glyphXOffsetIndex][loopIndex4 * glyphAdvanceOrGlyphWidths];
         }

         if (byteCode <= sourceLineHeight / 7) {
            this.glyphAdvances[glyphXOffsetIndex]--;
            this.glyphXOffsets[glyphXOffsetIndex] = 0;
         }

         byteCode = 0;

         for (int loopIndex5 = sourceLineHeight / 7; loopIndex5 < sourceLineHeight; loopIndex5++) {
            byteCode += this.glyphPixels[glyphXOffsetIndex][glyphAdvanceOrGlyphWidths - 1 + loopIndex5 * glyphAdvanceOrGlyphWidths];
         }

         if (byteCode <= sourceLineHeight / 7) {
            this.glyphAdvances[glyphXOffsetIndex]--;
         }
      }

      if (flag) {
         this.glyphAdvances[32] = this.glyphAdvances[73];
      } else {
         this.glyphAdvances[32] = this.glyphAdvances[105];
      }
   }
   public final void drawBasicString(String text, int scalarArgument, int scalarArgument2) {
      scalarArgument2 -= this.lineHeight;
      int scalar = -1;

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         if (text.charAt(loopIndex) == '@' && loopIndex + 4 < text.length() && text.charAt(loopIndex + 4) == '@') {
            int oldSyntaxColor = this.getColorByName(text.substring(loopIndex + 1, loopIndex + 4));
            this.setColorAndShadow(oldSyntaxColor, currentShadowColor);
            loopIndex += 4;
         } else {
            char glyphWidthIndex;
            if ((glyphWidthIndex = text.charAt(loopIndex)) > 255) {
               glyphWidthIndex = 32;
            }

            if (glyphWidthIndex == 60) {
               scalar = loopIndex;
            } else {
               if (glyphWidthIndex == 62 && scalar != -1) {
                  String substring = text.substring(scalar + 1, loopIndex);
                  scalar = -1;
                  if (substring.equals(tagLessThan)) {
                     glyphWidthIndex = 60;
                  } else if (substring.equals(tagGreaterThan)) {
                     glyphWidthIndex = 62;
                  } else if (substring.equals(tagNbsp)) {
                     glyphWidthIndex = 160;
                  } else if (substring.equals(tagSoftHyphen)) {
                     glyphWidthIndex = 173;
                  } else if (substring.equals(tagTimes)) {
                     glyphWidthIndex = 215;
                  } else if (substring.equals(tagEuro)) {
                     glyphWidthIndex = 128;
                  } else if (substring.equals(tagCopyright)) {
                     glyphWidthIndex = 169;
                  } else {
                     if (!substring.equals(tagRegistered)) {
                        if (substring.startsWith(tagImagePrefix)) {
                           try {
                              int iconIndex = Integer.valueOf(substring.substring(4));
                              Sprite sprite;
                              int canvasHeight = (sprite = this.icons[iconIndex]).canvasHeight;
                              if (transparency == 256) {
                                 sprite.drawSpriteDefaultAlpha(scalarArgument, scalarArgument2 + this.lineHeight - canvasHeight);
                              } else {
                                 sprite.drawSpriteAlpha(scalarArgument, scalarArgument2 + this.lineHeight - canvasHeight, transparency);
                              }

                              scalarArgument += sprite.canvasWidth;
                           } catch (Exception exception) {
                           }
                        } else {

                           try {
                              if (substring.startsWith(tagColorPrefix)) {
                                 String text2;
                                 currentTextColor = (text2 = substring.substring(4)).length() < 6 ? Color.decode(text2).getRGB() : Integer.parseInt(text2, 16);
                              } else if (substring.equals(tagColorEnd)) {
                                 currentTextColor = baseTextColor;
                              } else if (substring.startsWith(tagTransparencyPrefix)) {
                                 transparency = Integer.valueOf(substring.substring(6));
                              } else if (substring.equals(tagTransparencyEnd)) {
                                 transparency = baseTransparency;
                              } else if (substring.startsWith(tagStrikethroughColorPrefix)) {
                                 String text3;
                                 strikethroughColor = (text3 = substring.substring(4)).length() < 6 ? Color.decode(text3).getRGB() : Integer.parseInt(text3, 16);
                              } else if (substring.equals(tagStrikethrough)) {
                                 strikethroughColor = 8388608;
                              } else if (substring.equals(tagStrikethroughEnd)) {
                                 strikethroughColor = -1;
                              } else if (substring.startsWith(tagUnderlineColorPrefix)) {
                                 String text4;
                                 underlineColor = (text4 = substring.substring(2)).length() < 6 ? Color.decode(text4).getRGB() : Integer.parseInt(text4, 16);
                              } else if (substring.equals(tagUnderline)) {
                                 underlineColor = 0;
                              } else if (substring.equals(tagUnderlineEnd)) {
                                 underlineColor = -1;
                              } else if (substring.startsWith(tagShadowColorPrefix)) {
                                 String text5;
                                 currentShadowColor = (text5 = substring.substring(5)).length() < 6 ? Color.decode(text5).getRGB() : Integer.parseInt(text5, 16);
                              } else if (substring.equals(tagShadow)) {
                                 currentShadowColor = 0;
                              } else if (substring.equals(tagShadowEnd)) {
                                 currentShadowColor = baseShadowColor;
                              } else if (substring.equals(tagLineBreak)) {
                                 int sourceBaseTransparency = baseTransparency;
                                 int sourceBaseShadowColor = baseShadowColor;
                                 int baseColor = baseTextColor;
                                 strikethroughColor = -1;
                                 underlineColor = -1;
                                 baseShadowColor = sourceBaseShadowColor;
                                 currentShadowColor = sourceBaseShadowColor;
                                 baseTextColor = baseColor;
                                 currentTextColor = baseColor;
                                 baseTransparency = sourceBaseTransparency;
                                 transparency = sourceBaseTransparency;
                                 justificationTotal = 0;
                                 justificationCurrent = 0;
                              }
                           } catch (Exception exception2) {
                           }
                        }
                        continue;
                     }

                     glyphWidthIndex = 174;
                  }
               }

               if (scalar == -1) {
                  int glyphWidth = this.glyphWidths[glyphWidthIndex];
                  int glyphHeight = this.glyphHeights[glyphWidthIndex];
                  if (glyphWidthIndex != 32) {
                     if (transparency == 256) {
                        if (currentShadowColor != -1) {
                           this.drawCharacter(glyphWidthIndex, scalarArgument + this.glyphXOffsets[glyphWidthIndex] + 1, scalarArgument2 + this.glyphYOffsets[glyphWidthIndex] + 1, glyphWidth, glyphHeight, currentShadowColor);
                        }

                        this.drawCharacter(glyphWidthIndex, scalarArgument + this.glyphXOffsets[glyphWidthIndex], scalarArgument2 + this.glyphYOffsets[glyphWidthIndex], glyphWidth, glyphHeight, currentTextColor);
                     } else {
                        if (currentShadowColor != -1) {
                           this.drawTransparentCharacter(glyphWidthIndex, scalarArgument + this.glyphXOffsets[glyphWidthIndex] + 1, scalarArgument2 + this.glyphYOffsets[glyphWidthIndex] + 1, glyphWidth, glyphHeight, currentShadowColor, transparency);
                        }

                        this.drawTransparentCharacter(glyphWidthIndex, scalarArgument + this.glyphXOffsets[glyphWidthIndex], scalarArgument2 + this.glyphYOffsets[glyphWidthIndex], glyphWidth, glyphHeight, currentTextColor, transparency);
                     }
                  }

                  int advance = this.glyphAdvances[glyphWidthIndex];
                  if (strikethroughColor != -1) {
                     Rasterizer2D.drawHorizontalLineAlternate(scalarArgument, scalarArgument2 + (int)(this.lineHeight * 0.7), advance, strikethroughColor);
                  }

                  if (underlineColor != -1) {
                     Rasterizer2D.drawHorizontalLineAlternate(scalarArgument, scalarArgument2 + this.lineHeight, advance, underlineColor);
                  }

                  scalarArgument += advance;
               }
            }
         }
      }

      this.oldSyntaxStrikethroughActive = false;
   }
   public final void setColorAndShadow(int newBaseTextColor, int newBaseShadowColor) {
      if (!this.oldSyntaxStrikethroughActive) {
         strikethroughColor = -1;
      }

      underlineColor = -1;
      if (!this.oldSyntaxStrikethroughActive && newBaseTextColor != -1) {
         baseShadowColor = newBaseShadowColor;
         currentShadowColor = newBaseShadowColor;
         baseTextColor = newBaseTextColor;
         currentTextColor = newBaseTextColor;
         baseTransparency = 256;
         transparency = 256;
         justificationTotal = 0;
         justificationCurrent = 0;
      }
   }
   public final int getTextWidth(String text) {
      if (text == null) {
         return 0;
      }

      int scalar = -1;
      int scalar2 = 0;

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         if (text.charAt(loopIndex) == '@' && loopIndex + 4 < text.length() && text.charAt(loopIndex + 4) == '@') {
            int oldSyntaxColor = this.getColorByName(text.substring(loopIndex + 1, loopIndex + 4));
            this.setColorAndShadow(oldSyntaxColor, currentShadowColor);
            loopIndex += 4;
         } else {
            char glyphAdvanceIndex;
            if ((glyphAdvanceIndex = text.charAt(loopIndex)) > 255) {
               glyphAdvanceIndex = 32;
            }

            if (glyphAdvanceIndex == 60) {
               scalar = loopIndex;
            } else {
               if (glyphAdvanceIndex == 62 && scalar != -1) {
                  String substring = text.substring(scalar + 1, loopIndex);
                  scalar = -1;
                  if (substring.equals(tagLessThan)) {
                     glyphAdvanceIndex = 60;
                  } else if (substring.equals(tagGreaterThan)) {
                     glyphAdvanceIndex = 62;
                  } else if (substring.equals(tagNbsp)) {
                     glyphAdvanceIndex = 160;
                  } else if (substring.equals(tagSoftHyphen)) {
                     glyphAdvanceIndex = 173;
                  } else if (substring.equals(tagTimes)) {
                     glyphAdvanceIndex = 215;
                  } else if (substring.equals(tagEuro)) {
                     glyphAdvanceIndex = 128;
                  } else if (substring.equals(tagCopyright)) {
                     glyphAdvanceIndex = 169;
                  } else {
                     if (!substring.equals(tagRegistered)) {
                        if (substring.startsWith(tagImagePrefix)) {
                           try {
                              int imageIndex = Integer.valueOf(substring.substring(4));
                              scalar2 += this.icons[imageIndex].canvasWidth;
                           } catch (Exception exception) {
                           }
                        }
                        continue;
                     }

                     glyphAdvanceIndex = 174;
                  }
               }

               if (scalar == -1) {
                  scalar2 += this.glyphAdvances[glyphAdvanceIndex];
               }
            }
         }
      }

      this.oldSyntaxStrikethroughActive = false;
      return scalar2;
   }
   public final void drawBasicString(String text, int scalarArgument, int scalarArgument2, int textColor, int scalarArgument3) {
      if (text != null) {
         this.setColorAndShadow(textColor, scalarArgument3);
         this.drawBasicString(text, scalarArgument, scalarArgument2);
      }
   }
   public final void drawCenteredString(String text, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4) {
      if (text != null) {
         this.setColorAndShadow(scalarArgument3, scalarArgument4);
         this.drawBasicString(text, scalarArgument - this.getTextWidth(text) / 2, scalarArgument2);
      }
   }
   private void drawTransparentCharacter(int glyphPixelIndex, int newTopX, int newTopY, int glyphWidth, int pixelIndex, int scalarArgument, int sourcePixelIndex2) {
      int sourcePixelIndex = newTopX + newTopY * Rasterizer2D.width;
      int localWidth = Rasterizer2D.width - glyphWidth;
      int scalar = 0;
      int sourceGlyphWidth = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         pixelIndex -= localTopY;
         newTopY = Rasterizer2D.topY;
         sourceGlyphWidth = 0 + localTopY * glyphWidth;
         sourcePixelIndex += localTopY * Rasterizer2D.width;
      }

      if (newTopY + pixelIndex > Rasterizer2D.bottomY) {
         pixelIndex -= newTopY + pixelIndex - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         glyphWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         sourceGlyphWidth += localTopX;
         sourcePixelIndex += localTopX;
         scalar = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + glyphWidth > Rasterizer2D.bottomX) {
         int scalar2 = newTopX + glyphWidth - Rasterizer2D.bottomX;
         glyphWidth -= scalar2;
         scalar += scalar2;
         localWidth += scalar2;
      }

      if (glyphWidth > 0 && pixelIndex > 0) {
         Rasterizer2D.markGpuDirty(newTopX, newTopY, glyphWidth, pixelIndex);
         byte[] glyphPixel = this.glyphPixels[glyphPixelIndex];
         int scalar3 = sourcePixelIndex2;
         int scalar4 = scalar;
         int sourceLocalWidth = localWidth;
         sourcePixelIndex2 = pixelIndex;
         int sourceGlyphWidth2 = glyphWidth;
         pixelIndex = sourcePixelIndex;
         glyphWidth = sourceGlyphWidth;
         newTopY = scalarArgument;
         byte[] sourceGlyphPixel = glyphPixel;
         int[] pixels = Rasterizer2D.pixels;

         for (int loopIndex = -sourcePixelIndex2; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = -sourceGlyphWidth2; loopIndex2 < 0; loopIndex2++) {
               if (sourceGlyphPixel[glyphWidth++] != 0) {
                  pixels[pixelIndex] = Rasterizer2D.blendUiPixel(
                     newTopY,
                     pixels[pixelIndex],
                     scalar3
                  );
                  pixelIndex++;
               } else {
                  pixelIndex++;
               }
            }

            pixelIndex += sourceLocalWidth;
            glyphWidth += scalar4;
         }
      }
   }

   public static String capitalize(String text) {
      return text.length() == 0 ? text : text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
   }
   private void drawCharacter(int glyphPixelIndex, int newTopX, int newTopY, int glyphWidth, int newIndex, int positionArgument) {
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

      if (newTopY + newIndex > Rasterizer2D.bottomY) {
         newIndex -= newTopY + newIndex - Rasterizer2D.bottomY;
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

      if (newTopX + glyphWidth > Rasterizer2D.bottomX) {
         int scalar3 = newTopX + glyphWidth - Rasterizer2D.bottomX;
         glyphWidth -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (glyphWidth > 0 && newIndex > 0) {
         Rasterizer2D.markGpuDirty(newTopX, newTopY, glyphWidth, newIndex);
         byte[] glyphPixel = this.glyphPixels[glyphPixelIndex];
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
   private int getColorByName(String text) {
      if (text.equals("red")) {
         return 16711680;
      }

      if (text.equals("ud1")) {
         return Client.groundItemOtherMenuColor;
      }

      if (text.equals("ud2")) {
         return Client.groundItemRareMenuColor;
      }

      if (text.equals("gre")) {
         return 65280;
      }

      if (text.equals("blu")) {
         return 255;
      }

      if (text.equals("yel")) {
         return 16776960;
      }

      if (text.equals("cya")) {
         return 65535;
      }

      if (text.equals("mag")) {
         return 16711935;
      }

      if (text.equals("whi")) {
         return 16777215;
      }

      if (text.equals("bla")) {
         return 0;
      }

      if (text.equals("lre")) {
         return 16748608;
      }

      if (text.equals("dre")) {
         return 8388608;
      }

      if (text.equals("dbl")) {
         return 128;
      }

      if (text.equals("or1")) {
         return 16756736;
      }

      if (text.equals("or2")) {
         return 16740352;
      }

      if (text.equals("or3")) {
         return 16723968;
      }

      if (text.equals("gr1")) {
         return 12648192;
      }

      if (text.equals("gr2")) {
         return 8453888;
      }

      if (text.equals("gr3")) {
         return 4259584;
      }

      if (text.equals("str")) {
         strikethroughColor = 8388608;
         this.oldSyntaxStrikethroughActive = true;
      }

      if (text.equals("end")) {
         strikethroughColor = -1;
         this.oldSyntaxStrikethroughActive = false;
      }

      return -1;
   }
}
