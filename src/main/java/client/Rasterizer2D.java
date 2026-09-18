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
            pixel = (newBottomX - loopIndex) / (newBottomX / 256);
            scalarArgument2 = 256 - pixel;
            int scalar = pixel * 109;
            int scalar2 = pixel * 106;
            pixel *= 87;
            int scalar3 = (pixels[pixelIndex] >> 16 & 0xFF) * scalarArgument2;
            int scalar4 = (pixels[pixelIndex] >> 8 & 0xFF) * scalarArgument2;
            scalarArgument2 = (pixels[pixelIndex] & 0xFF) * scalarArgument2;
            pixel = (scalar + scalar3 >> 8 << 16) + (scalar2 + scalar4 >> 8 << 8) + (pixel + scalarArgument2 >> 8);
            pixels[pixelIndex++] = pixel;
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

      int scalar = 256 - newWidth;
      int scalar2 = (scalarArgument >> 16 & 0xFF) * newWidth;
      int scalar3 = (scalarArgument >> 8 & 0xFF) * newWidth;
      scalarArgument = (scalarArgument & 0xFF) * newWidth;
      newWidth = width - newIndex;
      pixelIndex = newTopX + pixelIndex * width;

      for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
         for (int loopIndex2 = -newIndex; loopIndex2 < 0; loopIndex2++) {
            int pixel = (pixels[pixelIndex] >> 16 & 0xFF) * scalar;
            int scalar4 = (pixels[pixelIndex] >> 8 & 0xFF) * scalar;
            int scalar5 = (pixels[pixelIndex] & 0xFF) * scalar;
            pixel = (scalar2 + pixel >> 8 << 16) + (scalar3 + scalar4 >> 8 << 8) + (scalarArgument + scalar5 >> 8);
            pixels[pixelIndex++] = pixel;
         }

         pixelIndex += newWidth;
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

         int scalar = 256 - scalarArgument2;
         int scalar2 = (scalarArgument >> 16 & 0xFF) * scalarArgument2;
         int scalar3 = (scalarArgument >> 8 & 0xFF) * scalarArgument2;
         scalarArgument = (scalarArgument & 0xFF) * scalarArgument2;
         pixelIndex = pixelOrTopX + pixelIndex * width;

         for (int loopIndex = 0; loopIndex < newBottomX; loopIndex++) {
            pixelOrTopX = (pixels[pixelIndex] >> 16 & 0xFF) * scalar;
            int scalar4 = (pixels[pixelIndex] >> 8 & 0xFF) * scalar;
            int scalar5 = (pixels[pixelIndex] & 0xFF) * scalar;
            pixelOrTopX = (scalar2 + pixelOrTopX >> 8 << 16) + (scalar3 + scalar4 >> 8 << 8) + (scalarArgument + scalar5 >> 8);
            pixels[pixelIndex++] = pixelOrTopX;
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

         int scalar = 256 - scalarArgument2;
         int scalar2 = (scalarArgument >> 16 & 0xFF) * scalarArgument2;
         int scalar3 = (scalarArgument >> 8 & 0xFF) * scalarArgument2;
         scalarArgument = (scalarArgument & 0xFF) * scalarArgument2;
         pixelIndex += pixelOrTopY * width;

         for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
            pixelOrTopY = (pixels[pixelIndex] >> 16 & 0xFF) * scalar;
            int scalar4 = (pixels[pixelIndex] >> 8 & 0xFF) * scalar;
            int scalar5 = (pixels[pixelIndex] & 0xFF) * scalar;
            pixelOrTopY = (scalar2 + pixelOrTopY >> 8 << 16) + (scalar3 + scalar4 >> 8 << 8) + (scalarArgument + scalar5 >> 8);
            pixels[pixelIndex] = pixelOrTopY;
            pixelIndex += width;
         }
      }
   }
}
