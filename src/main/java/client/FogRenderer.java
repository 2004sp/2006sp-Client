package client;
public final class FogRenderer {
   private float distanceOffset;
   private static int fogColor = 9803416;
   public final void renderFog(int depthBufferIndex, int pixel, int newPixels) {
      depthBufferIndex = fogColor;
      fogColor = fogColor;
      depthBufferIndex = Rasterizer3D.scanOffsets[0];
      int scalar = (int)(1430.0F + this.distanceOffset);
      int scalar2 = (int)(2100.0F + this.distanceOffset);

      for (int loopIndex = 0; loopIndex < Rasterizer2D.bottomY; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < Rasterizer2D.centerX; loopIndex2++) {
            if (Rasterizer3D.depthBuffer[depthBufferIndex] >= scalar2) {
               Rasterizer2D.pixels[depthBufferIndex] = fogColor;
            } else if (Rasterizer3D.depthBuffer[depthBufferIndex] >= scalar) {
               int scalar3 = (int)(Rasterizer3D.depthBuffer[depthBufferIndex] - scalar) / 3;
               pixel = ((fogColor & 16711935) * scalar3 >> 8 & 16711935) + ((fogColor & 0xFF00) * scalar3 >> 8 & 0xFF00);
               scalar3 = 256 - scalar3;
               newPixels = (((newPixels = Rasterizer2D.pixels[depthBufferIndex]) & 16711935) * scalar3 >> 8 & 16711935) + ((newPixels & 0xFF00) * scalar3 >> 8 & 0xFF00);
               Rasterizer2D.pixels[depthBufferIndex] = pixel + newPixels;
            }

            depthBufferIndex++;
         }

         depthBufferIndex += Rasterizer2D.width - Rasterizer2D.centerX;
      }
   }
   public final void setDistanceOffset(float newDistanceOffset) {
      this.distanceOffset = newDistanceOffset;
   }
}
