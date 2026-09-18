package client;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
class GraphicsBuffer implements ImageObserver, ImageProducer {
   protected int[] pixels;
   private int width;
   private int height;
   protected ColorModel colorModel;
   private ImageConsumer imageConsumer;
   public Image image;
   public float[] depthBuffer;

   protected GraphicsBuffer() {
   }
   public void initDrawingArea() {
      Rasterizer2D.setRasterBuffer(this.height, this.width, this.pixels, this.depthBuffer);
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

   public void setHeight(int newHeight) {
      this.height = newHeight;
   }

   public void setWidth(int newWidth) {
      this.width = newWidth;
   }

   @Override
   public boolean imageUpdate(Image image, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4, int scalarArgument5) {
      return true;
   }
}
