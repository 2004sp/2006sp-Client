package client;
public class Rasterizer2D extends CacheableNode {
   public static int[] pixels;
   public static int width;
   public static int height;
   public static int topY;
   public static int bottomY;
   public static int topX;
   public static int bottomX;
   public static int centerX;
   public static int centerY;
   public static int viewportCenterY;
   public static float[] depthBuffer;

   /**
    * Blend a legacy software-UI pixel while preserving real alpha for the
    * direct GPU compositor. The legacy framebuffer's high byte is unused by
    * its DirectColorModel, so direct presentation can safely use it as alpha
    * metadata without changing the software renderer's normal output.
    */
   static int blendUiPixel(int sourceRgb, int destination, int alpha256) {
      if (alpha256 <= 0) {
         return destination;
      }
      sourceRgb &= 0x00FFFFFF;
      if (alpha256 >= 256) {
         return sourceRgb;
      }

      if (GpuRasterizer3D.isDirectUiOverlayPrepared()) {
         int destinationRgb = destination & 0x00FFFFFF;
         int destinationAlpha = destination >>> 24;

         if (destinationAlpha == 0 && destinationRgb == GpuRasterizer3D.UI_TRANSPARENT_KEY) {
            int encodedAlpha = (alpha256 * 255 + 128) >> 8;
            if (encodedAlpha < 1) {
               encodedAlpha = 1;
            }
            return encodedAlpha << 24 | sourceRgb;
         }

         if (destinationAlpha != 0) {
            int inverse = 256 - alpha256;
            int outAlphaNumerator = alpha256 * 255 + destinationAlpha * inverse;
            if (outAlphaNumerator <= 0) {
               return GpuRasterizer3D.UI_TRANSPARENT_KEY;
            }

            int sourceRed = sourceRgb >> 16 & 255;
            int sourceGreen = sourceRgb >> 8 & 255;
            int sourceBlue = sourceRgb & 255;
            int destinationRed = destinationRgb >> 16 & 255;
            int destinationGreen = destinationRgb >> 8 & 255;
            int destinationBlue = destinationRgb & 255;

            int sourceWeight = alpha256 * 255;
            int destinationWeight = destinationAlpha * inverse;
            int red = (sourceRed * sourceWeight + destinationRed * destinationWeight + outAlphaNumerator / 2) / outAlphaNumerator;
            int green = (sourceGreen * sourceWeight + destinationGreen * destinationWeight + outAlphaNumerator / 2) / outAlphaNumerator;
            int blue = (sourceBlue * sourceWeight + destinationBlue * destinationWeight + outAlphaNumerator / 2) / outAlphaNumerator;
            int encodedAlpha = (outAlphaNumerator + 128) >> 8;
            if (encodedAlpha < 1) {
               encodedAlpha = 1;
            } else if (encodedAlpha > 255) {
               encodedAlpha = 255;
            }

            return encodedAlpha << 24 | red << 16 | green << 8 | blue;
         }
      }

      int inverse = 256 - alpha256;
      return ((sourceRgb & 16711935) * alpha256 + (destination & 16711935) * inverse & -16711936)
            + ((sourceRgb & 0xFF00) * alpha256 + (destination & 0xFF00) * inverse & 0xFF0000)
         >> 8;
   }
   public static void setRasterBuffer(int rasterHeight, int rasterWidth, int[] pixelBuffer, float[] depthBufferData) {
      pixels = pixelBuffer;
      width = rasterWidth;
      height = rasterHeight;
      depthBuffer = depthBufferData;
      setClip(rasterHeight, 0, rasterWidth, 0);
   }
   public static void drawHorizontalGradient(int pixelIndex, int scalarArgument, int newBottomX, int pixel, int scalarArgument2) {
      if (scalarArgument >= topY && scalarArgument < bottomY) {
         if (pixelIndex < topX) {
            newBottomX = 405 - (topX - pixelIndex);
            pixelIndex = topX;
         }

         if (pixelIndex + newBottomX > bottomX) {
            newBottomX = bottomX - pixelIndex;
         }

         pixelIndex += scalarArgument * width;

         for (int loopIndex = 0; loopIndex < newBottomX; loopIndex++) {
            int alpha = (newBottomX - loopIndex) / (newBottomX / 256);
            pixels[pixelIndex] = blendUiPixel(0x6D6A57, pixels[pixelIndex], alpha);
            pixelIndex++;
         }
      }
   }
   public static int toPixelIndex(int pixelIndex) {
      int sourcePixelIndex = 172;
      int scalar;
      int scalar2 = scalar = pixelIndex % 172;
      sourcePixelIndex = (short)172;
      pixelIndex = scalar = pixelIndex / 172;
      int scalar3 = scalar2 + 29;
      sourcePixelIndex = pixelIndex;
      return scalar3 + sourcePixelIndex * width;
   }
   public static void resetClip() {
      topX = 0;
      topY = 0;
      bottomX = width;
      bottomY = height;
      centerX = bottomX;
      centerY = bottomX / 2;
   }
   public static void setClip(int left, int top, int right, int bottom) {
      if (top < 0) {
         top = 0;
      }

      if (bottom < 0) {
         bottom = 0;
      }

      if (right > width) {
         right = width;
      }

      if (left > height) {
         left = height;
      }

      topX = top;
      topY = bottom;
      bottomX = right;
      bottomY = left;
      centerX = bottomX;
      centerY = bottomX / 2;
      viewportCenterY = bottomY / 2;
   }
   public static void clear() {
      int localWidth = width * height;

      for (int pixelIndex = 0; pixelIndex < localWidth; pixelIndex++) {
         pixels[pixelIndex] = 0;
         depthBuffer[pixelIndex] = Float.MAX_VALUE;
      }
   }
   public static void fillRectangleAlpha(int scalarArgument, int pixelIndex, int newIndex, int newBottomY, int newWidth, int newTopX) {
      if (newTopX < topX) {
         newIndex -= topX - newTopX;
         newTopX = topX;
      }

      if (pixelIndex < topY) {
         newBottomY -= topY - pixelIndex;
         pixelIndex = topY;
      }

      if (newTopX + newIndex > bottomX) {
         newIndex = bottomX - newTopX;
      }

      if (pixelIndex + newBottomY > bottomY) {
         newBottomY = bottomY - pixelIndex;
      }

      int alpha = newWidth;
      int rowSkip = width - newIndex;
      pixelIndex = newTopX + pixelIndex * width;

      for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
         for (int loopIndex2 = -newIndex; loopIndex2 < 0; loopIndex2++) {
            pixels[pixelIndex] = blendUiPixel(scalarArgument, pixels[pixelIndex], alpha);
            pixelIndex++;
         }

         pixelIndex += rowSkip;
      }
   }
   public static void fillRectangle(int positionArgument, int newTopY, int newTopX, int pixel, int newIndex) {
      if (newTopX < topX) {
         newIndex -= topX - newTopX;
         newTopX = topX;
      }

      if (newTopY < topY) {
         positionArgument -= topY - newTopY;
         newTopY = topY;
      }

      if (newTopX + newIndex > bottomX) {
         newIndex = bottomX - newTopX;
      }

      if (newTopY + positionArgument > bottomY) {
         positionArgument = bottomY - newTopY;
      }

      int localWidth = width - newIndex;
      newTopY = newTopX + newTopY * width;

      for (int loopIndex = -positionArgument; loopIndex < 0; loopIndex++) {
         for (int loopIndex2 = -newIndex; loopIndex2 < 0; loopIndex2++) {
            pixels[newTopY++] = pixel;
         }

         newTopY += localWidth;
      }
   }
   public static void fillRectangleAlternate(int newTopX, int newTopY, int newBottomX, int newBottomY, int pixel) {
      if (newTopX < topX) {
         newBottomX -= topX - newTopX;
         newTopX = topX;
      }

      if (newTopY < topY) {
         newBottomY -= topY - newTopY;
         newTopY = topY;
      }

      if (newTopX + newBottomX > bottomX) {
         newBottomX = bottomX - newTopX;
      }

      if (newTopY + newBottomY > bottomY) {
         newBottomY = bottomY - newTopY;
      }

      int localWidth = width - newBottomX;
      newTopX += newTopY * width;

      for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < newBottomX; loopIndex2++) {
            pixels[newTopX++] = pixel;
         }

         newTopX += localWidth;
      }
   }
   public static void darkenRectangle(int pixelIndex, int newTopY, int newBottomX, int newBottomY, int newWidth, int scalarArgument) {
      if (pixelIndex < topX) {
         newBottomX -= topX - pixelIndex;
         pixelIndex = topX;
      }

      if (newTopY < topY) {
         newBottomY -= topY - newTopY;
         newTopY = topY;
      }

      if (pixelIndex + newBottomX > bottomX) {
         newBottomX = bottomX - pixelIndex;
      }

      if (newTopY + newBottomY > bottomY) {
         newBottomY = bottomY - newTopY;
      }

      newWidth = width - newBottomX;
      pixelIndex += newTopY * width;

      for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < newBottomX; loopIndex2++) {
            int pixel = (pixels[pixelIndex] >> 16 & 0xFF) * 206;
            int scalar = (pixels[pixelIndex] >> 8 & 0xFF) * 206;
            int scalar2 = (pixels[pixelIndex] & 0xFF) * 206;
            pixel = (pixel + 0 >> 8 << 16) + (scalar + 0 >> 8 << 8) + (scalar2 + 0 >> 8);
            pixels[pixelIndex++] = pixel;
         }

         pixelIndex += newWidth;
      }
   }
   public static void drawRectangle(int scalarArgument, int width, int height, int pixel, int pixelIndex) {
      drawHorizontalLine(pixelIndex, pixel, width, scalarArgument);
      drawHorizontalLine(pixelIndex + height - 1, pixel, width, scalarArgument);
      drawVerticalLine(pixelIndex, pixel, height, scalarArgument);
      drawVerticalLine(pixelIndex, pixel, height, scalarArgument + width - 1);
   }
   public static void drawRectangleAlpha(int pixelIndex, int height, int scalarArgument, int scalarArgument2, int width, int pixelIndex2) {
      drawHorizontalLineAlpha(scalarArgument2, width, pixelIndex, scalarArgument, pixelIndex2);
      drawHorizontalLineAlpha(scalarArgument2, width, pixelIndex + height - 1, scalarArgument, pixelIndex2);
      if (height >= 3) {
         drawVerticalLineAlpha(scalarArgument2, pixelIndex2, scalarArgument, pixelIndex + 1, height - 2);
         drawVerticalLineAlpha(scalarArgument2, pixelIndex2 + width - 1, scalarArgument, pixelIndex + 1, height - 2);
      }
   }
   public static void drawHorizontalLine(int pixelIndex, int pixel, int newBottomX, int newTopX) {
      if (pixelIndex >= topY && pixelIndex < bottomY) {
         if (newTopX < topX) {
            newBottomX -= topX - newTopX;
            newTopX = topX;
         }

         if (newTopX + newBottomX > bottomX) {
            newBottomX = bottomX - newTopX;
         }

         pixelIndex = newTopX + pixelIndex * width;

         for (int loopIndex = 0; loopIndex < newBottomX; loopIndex++) {
            pixels[pixelIndex + loopIndex] = pixel;
         }
      }
   }
   private static void drawHorizontalLineAlpha(int scalarArgument, int newBottomX, int pixelIndex, int scalarArgument2, int pixelOrTopX) {
      if (pixelIndex >= topY && pixelIndex < bottomY) {
         if (pixelOrTopX < topX) {
            newBottomX -= topX - pixelOrTopX;
            pixelOrTopX = topX;
         }

         if (pixelOrTopX + newBottomX > bottomX) {
            newBottomX = bottomX - pixelOrTopX;
         }

         pixelIndex = pixelOrTopX + pixelIndex * width;

         for (int loopIndex = 0; loopIndex < newBottomX; loopIndex++) {
            pixels[pixelIndex] = blendUiPixel(scalarArgument, pixels[pixelIndex], scalarArgument2);
            pixelIndex++;
         }
      }
   }
   public static void drawVerticalLine(int newTopY, int pixel, int newBottomY, int scalarArgument) {
      if (scalarArgument >= topX && scalarArgument < bottomX) {
         if (newTopY < topY) {
            newBottomY -= topY - newTopY;
            newTopY = topY;
         }

         if (newTopY + newBottomY > bottomY) {
            newBottomY = bottomY - newTopY;
         }

         newTopY = scalarArgument + newTopY * width;

         for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
            pixels[newTopY + loopIndex * width] = pixel;
         }
      }
   }
   public static void drawHorizontalLineAlternate(int newTopX, int lineHeight, int newBottomX, int pixel) {
      if (lineHeight >= topY && lineHeight < bottomY) {
         if (newTopX < topX) {
            newBottomX -= topX - newTopX;
            newTopX = topX;
         }

         if (newTopX + newBottomX > bottomX) {
            newBottomX = bottomX - newTopX;
         }

         newTopX += lineHeight * width;

         for (int loopIndex = 0; loopIndex < newBottomX; loopIndex++) {
            pixels[newTopX + loopIndex] = pixel;
         }
      }
   }
   private static void drawVerticalLineAlpha(int scalarArgument, int pixelIndex, int scalarArgument2, int pixelOrTopY, int newBottomY) {
      if (pixelIndex >= topX && pixelIndex < bottomX) {
         if (pixelOrTopY < topY) {
            newBottomY -= topY - pixelOrTopY;
            pixelOrTopY = topY;
         }

         if (pixelOrTopY + newBottomY > bottomY) {
            newBottomY = bottomY - pixelOrTopY;
         }

         pixelIndex += pixelOrTopY * width;

         for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
            pixels[pixelIndex] = blendUiPixel(scalarArgument, pixels[pixelIndex], scalarArgument2);
            pixelIndex += width;
         }
      }
   }
}
