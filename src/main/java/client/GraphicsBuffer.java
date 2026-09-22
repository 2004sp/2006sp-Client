package client;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Arrays;

class GraphicsBuffer implements ImageObserver, ImageProducer {
   static final int GPU_DIRTY_TILE_SIZE = 64;

   protected int[] pixels;
   private int width;
   private int height;
   protected ColorModel colorModel;
   private ImageConsumer imageConsumer;
   public Image image;
   public float[] depthBuffer;

   private boolean[] gpuDirtyTiles;
   private boolean[] gpuCoverageTiles;
   private int gpuTileColumns;
   private int gpuTileRows;
   private int gpuDirtyTileCount;

   protected GraphicsBuffer() {
   }

   public void initDrawingArea() {
      Rasterizer2D.setRasterBuffer(this);
   }

   public void drawGraphics(int scalarArgument, Graphics graphics, int scalarArgument2) {
      this.pushPixels();
      graphics.drawImage(this.image, scalarArgument2, scalarArgument, this);
   }

   @Override
   public synchronized void addConsumer(ImageConsumer newImageConsumer) {
      this.imageConsumer = newImageConsumer;
      newImageConsumer.setDimensions(this.width, this.height);
      newImageConsumer.setProperties(null);
      newImageConsumer.setColorModel(this.colorModel);
      newImageConsumer.setHints(14);
   }

   @Override
   public synchronized boolean isConsumer(ImageConsumer newImageConsumer) {
      return this.imageConsumer == newImageConsumer;
   }

   @Override
   public synchronized void removeConsumer(ImageConsumer newImageConsumer) {
      if (this.imageConsumer == newImageConsumer) {
         this.imageConsumer = null;
      }
   }

   @Override
   public void startProduction(ImageConsumer imageConsumer) {
      this.addConsumer(imageConsumer);
   }

   @Override
   public void requestTopDownLeftRightResend(ImageConsumer imageConsumer) {
      System.out.println("TDLR");
   }

   private synchronized void pushPixels() {
      if (this.imageConsumer != null) {
         this.imageConsumer.setPixels(0, 0, this.width, this.height, this.colorModel, this.pixels, 0, this.width);
         this.imageConsumer.imageComplete(2);
      }
   }

   final void markGpuDirty(int x, int y, int dirtyWidth, int dirtyHeight) {
      if (dirtyWidth <= 0 || dirtyHeight <= 0 || this.width <= 0 || this.height <= 0) {
         return;
      }

      int minX = Math.max(0, x);
      int minY = Math.max(0, y);
      int maxX = Math.min(this.width, x + dirtyWidth);
      int maxY = Math.min(this.height, y + dirtyHeight);
      if (minX >= maxX || minY >= maxY) {
         return;
      }

      ensureGpuTracking();
      int minTileX = minX / GPU_DIRTY_TILE_SIZE;
      int minTileY = minY / GPU_DIRTY_TILE_SIZE;
      int maxTileX = (maxX - 1) / GPU_DIRTY_TILE_SIZE;
      int maxTileY = (maxY - 1) / GPU_DIRTY_TILE_SIZE;
      for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
         int row = tileY * this.gpuTileColumns;
         for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            int index = row + tileX;
            if (!this.gpuDirtyTiles[index]) {
               this.gpuDirtyTiles[index] = true;
               this.gpuDirtyTileCount++;
            }
            this.gpuCoverageTiles[index] = true;
         }
      }
   }

   final void markGpuDirtyFromIndex(int pixelIndex, int dirtyWidth, int dirtyHeight) {
      if (pixelIndex < 0 || this.width <= 0) {
         return;
      }
      markGpuDirty(pixelIndex % this.width, pixelIndex / this.width, dirtyWidth, dirtyHeight);
   }

   /**
    * The direct GPU scene path clears the software scene buffer back to the
    * transparent overlay key before redrawing legacy UI. Only tiles that held
    * UI during the previous software pass need to be dirtied by that clear.
    */
   final void markGpuCleared() {
      ensureGpuTracking();
      for (int i = 0; i < this.gpuCoverageTiles.length; i++) {
         if (!this.gpuCoverageTiles[i]) {
            continue;
         }
         this.gpuCoverageTiles[i] = false;
         if (!this.gpuDirtyTiles[i]) {
            this.gpuDirtyTiles[i] = true;
            this.gpuDirtyTileCount++;
         }
      }
   }

   final void markGpuAllDirty() {
      ensureGpuTracking();
      Arrays.fill(this.gpuDirtyTiles, true);
      this.gpuDirtyTileCount = this.gpuDirtyTiles.length;
   }

   final boolean hasGpuDirtyTiles() {
      ensureGpuTracking();
      return this.gpuDirtyTileCount != 0;
   }

   final boolean copyGpuDirtyTiles(boolean[] destination, int tileColumns, int tileRows) {
      ensureGpuTracking();
      if (destination == null
         || tileColumns != this.gpuTileColumns
         || tileRows != this.gpuTileRows
         || destination.length < this.gpuDirtyTiles.length) {
         return false;
      }

      for (int i = 0; i < this.gpuDirtyTiles.length; i++) {
         if (this.gpuDirtyTiles[i]) {
            destination[i] = true;
         }
      }
      return true;
   }

   final void clearGpuDirtyTiles() {
      ensureGpuTracking();
      if (this.gpuDirtyTileCount == 0) {
         return;
      }
      Arrays.fill(this.gpuDirtyTiles, false);
      this.gpuDirtyTileCount = 0;
   }

   final void consumeGpuDirtyTo(GraphicsBuffer destination, int destinationX, int destinationY) {
      if (destination == null) {
         return;
      }

      ensureGpuTracking();
      if (this.gpuDirtyTileCount == 0) {
         return;
      }

      for (int tileY = 0; tileY < this.gpuTileRows; tileY++) {
         int row = tileY * this.gpuTileColumns;
         int y = tileY * GPU_DIRTY_TILE_SIZE;
         int tileHeight = Math.min(GPU_DIRTY_TILE_SIZE, this.height - y);
         for (int tileX = 0; tileX < this.gpuTileColumns; tileX++) {
            int index = row + tileX;
            if (!this.gpuDirtyTiles[index]) {
               continue;
            }

            int x = tileX * GPU_DIRTY_TILE_SIZE;
            int tileWidth = Math.min(GPU_DIRTY_TILE_SIZE, this.width - x);
            destination.markGpuDirty(destinationX + x, destinationY + y, tileWidth, tileHeight);
         }
      }

      Arrays.fill(this.gpuDirtyTiles, false);
      this.gpuDirtyTileCount = 0;
   }

   private void ensureGpuTracking() {
      int tileColumns = Math.max(1, (this.width + GPU_DIRTY_TILE_SIZE - 1) / GPU_DIRTY_TILE_SIZE);
      int tileRows = Math.max(1, (this.height + GPU_DIRTY_TILE_SIZE - 1) / GPU_DIRTY_TILE_SIZE);
      int tileCount = tileColumns * tileRows;
      if (this.gpuDirtyTiles != null
         && this.gpuDirtyTiles.length == tileCount
         && this.gpuTileColumns == tileColumns
         && this.gpuTileRows == tileRows) {
         return;
      }

      this.gpuDirtyTiles = new boolean[tileCount];
      this.gpuCoverageTiles = new boolean[tileCount];
      this.gpuTileColumns = tileColumns;
      this.gpuTileRows = tileRows;
      this.gpuDirtyTileCount = 0;
   }

   private void resetGpuTracking() {
      this.gpuDirtyTiles = null;
      this.gpuCoverageTiles = null;
      this.gpuTileColumns = 0;
      this.gpuTileRows = 0;
      this.gpuDirtyTileCount = 0;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int newHeight) {
      this.height = newHeight;
      resetGpuTracking();
   }

   public void setWidth(int newWidth) {
      this.width = newWidth;
      resetGpuTracking();
   }

   @Override
   public boolean imageUpdate(Image image, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4, int scalarArgument5) {
      return true;
   }
}
