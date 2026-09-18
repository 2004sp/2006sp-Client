package worldmap;
public class WorldMapRasterizer2D extends WorldMapCacheableNode {
   public static int[] pixels;
   public static int width;
   private static int height;
   public static int topY = 0;
   public static int bottomY = 0;
   public static int topX = 0;
   public static int bottomX = 0;
   public static void drawHorizontalLine(int newTopX, int tileScreenY, int newBottomX, int pixel) {
      if (tileScreenY >= topY && tileScreenY < bottomY) {
         if (newTopX < topX) {
            newBottomX -= topX - newTopX;
            newTopX = topX;
         }

         if (newTopX + newBottomX > bottomX) {
            newBottomX = bottomX - newTopX;
         }

         newTopX += tileScreenY * width;

         for (int loopIndex = 0; loopIndex < newBottomX; loopIndex++) {
            pixels[newTopX + loopIndex] = pixel;
         }
      }
   }
   public static void drawVerticalLine(int tileScreenX, int newTopY, int newBottomY, int pixel) {
      if (tileScreenX >= topX && tileScreenX < bottomX) {
         if (newTopY < topY) {
            newBottomY -= topY - newTopY;
            newTopY = topY;
         }

         if (newTopY + newBottomY > bottomY) {
            newBottomY = bottomY - newTopY;
         }

         tileScreenX += newTopY * width;

         for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
            pixels[tileScreenX + loopIndex * width] = pixel;
         }
      }
   }
   public static void fillRectangleAlpha(int pixelIndex, int newTopY, int newIndex, int newBottomY, int newWidth, int scalarArgument) {
      if (pixelIndex < topX) {
         newIndex -= topX - pixelIndex;
         pixelIndex = topX;
      }

      if (newTopY < topY) {
         newBottomY -= topY - newTopY;
         newTopY = topY;
      }

      if (pixelIndex + newIndex > bottomX) {
         newIndex = bottomX - pixelIndex;
      }

      if (newTopY + newBottomY > bottomY) {
         newBottomY = bottomY - newTopY;
      }

      newWidth = width - newIndex;
      pixelIndex += newTopY * width;

      for (int loopIndex = 0; loopIndex < newBottomY; loopIndex++) {
         for (int loopIndex2 = -newIndex; loopIndex2 < 0; loopIndex2++) {
            int pixel = (pixels[pixelIndex] >> 16 & 0xFF) << 7;
            int scalar = (pixels[pixelIndex] >> 8 & 0xFF) << 7;
            int scalar2 = (pixels[pixelIndex] & 0xFF) << 7;
            pixel = (pixel + 32640 >> 8 << 16) + (scalar + 0 >> 8 << 8) + (scalar2 + 0 >> 8);
            pixels[pixelIndex++] = pixel;
         }

         pixelIndex += newWidth;
      }
   }
   public static void drawRectangle(int keyPanelX, int tileScreenY, int overviewWidth, int overviewHeight, int pixel) {
      drawHorizontalLine(keyPanelX, tileScreenY, overviewWidth, pixel);
      drawHorizontalLine(keyPanelX, tileScreenY + overviewHeight - 1, overviewWidth, pixel);
      drawVerticalLine(keyPanelX, tileScreenY, overviewHeight, pixel);
      drawVerticalLine(keyPanelX + overviewWidth - 1, tileScreenY, overviewHeight, pixel);
   }
   public static void setRasterBuffer(int[] newPixels, int newWidth, int newHeight) {
      pixels = newPixels;
      width = newWidth;
      height = newHeight;
      boolean flag = false;
      boolean localFlag = false;
      if (newWidth > width) {
         newWidth = width;
      }

      if (newHeight > height) {
         newHeight = height;
      }

      topX = 0;
      topY = 0;
      bottomX = newWidth;
      bottomY = newHeight;
   }
   public static void fillRectangle(int newTopX, int newTopY, int keyPanelWidth, int newIndex, int pixel) {
      if (newTopX < topX) {
         keyPanelWidth -= topX - newTopX;
         newTopX = topX;
      }

      if (newTopY < topY) {
         newIndex -= topY - newTopY;
         newTopY = topY;
      }

      if (newTopX + keyPanelWidth > bottomX) {
         keyPanelWidth = bottomX - newTopX;
      }

      if (newTopY + newIndex > bottomY) {
         newIndex = bottomY - newTopY;
      }

      int localWidth = width - keyPanelWidth;
      newTopX += newTopY * width;

      for (int loopIndex = -newIndex; loopIndex < 0; loopIndex++) {
         for (int loopIndex2 = -keyPanelWidth; loopIndex2 < 0; loopIndex2++) {
            pixels[newTopX++] = pixel;
         }

         newTopX += localWidth;
      }
   }
   public static void clear() {
      int localWidth = width * height;

      for (int pixelIndex = 0; pixelIndex < localWidth; pixelIndex++) {
         pixels[pixelIndex] = 0;
      }
   }
   public static void drawCircleAlpha(int markerScreenXEntry, int markerScreenYEntry, int scalarArgument, int scalarArgument2, int positionArgument) {
      int scalar = 256 - positionArgument;
      int scalar2 = (scalarArgument2 >> 16 & 0xFF) * positionArgument;
      int scalar3 = (scalarArgument2 >> 8 & 0xFF) * positionArgument;
      scalarArgument2 = (scalarArgument2 & 0xFF) * positionArgument;
      if ((positionArgument = markerScreenYEntry - scalarArgument) < 0) {
         positionArgument = 0;
      }

      int loopIndex;
      if ((loopIndex = markerScreenYEntry + scalarArgument) >= height) {
         loopIndex = height - 1;
      }

      for (int loopIndex2 = positionArgument; loopIndex2 <= loopIndex; loopIndex2++) {
         int sqrtResult = loopIndex2 - markerScreenYEntry;
         sqrtResult = (int)Math.sqrt(scalarArgument * scalarArgument - sqrtResult * sqrtResult);
         int position;
         if ((position = markerScreenXEntry - sqrtResult) < 0) {
            position = 0;
         }

         if ((sqrtResult = markerScreenXEntry + sqrtResult) >= width) {
            sqrtResult = width - 1;
         }

         int pixelIndex = position + loopIndex2 * width;

         for (int loopIndex3 = position; loopIndex3 <= sqrtResult; loopIndex3++) {
            int pixel = (pixels[pixelIndex] >> 16 & 0xFF) * scalar;
            int scalar4 = (pixels[pixelIndex] >> 8 & 0xFF) * scalar;
            int scalar5 = (pixels[pixelIndex] & 0xFF) * scalar;
            pixel = (scalar2 + pixel >> 8 << 16) + (scalar3 + scalar4 >> 8 << 8) + (scalarArgument2 + scalar5 >> 8);
            pixels[pixelIndex++] = pixel;
         }
      }
   }
}
