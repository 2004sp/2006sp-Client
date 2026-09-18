package worldmap;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
public final class WorldMapGraphicsBuffer implements ImageObserver, ImageProducer {
   private int[] pixels;
   private int width;
   private int height;
   private ColorModel colorModel;
   private ImageConsumer imageConsumer;
   public Image image;
   public final void initDrawingArea() {
      WorldMapRasterizer2D.setRasterBuffer(this.pixels, this.width, this.height);
   }

   public WorldMapGraphicsBuffer(int newWidth, int newHeight, Component component) {
      this.width = newWidth;
      this.height = newHeight;
      this.pixels = new int[newWidth * newHeight];
      this.colorModel = new DirectColorModel(32, 16711680, 65280, 255);
      this.image = component.createImage(this);
      this.pushPixels();
      component.prepareImage(this.image, this);
      this.pushPixels();
      component.prepareImage(this.image, this);
      this.pushPixels();
      component.prepareImage(this.image, this);
      this.initDrawingArea();
   }

   @Override
   public final boolean imageUpdate(Image image, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4, int scalarArgument5) {
      return true;
   }

   @Override
   public final void requestTopDownLeftRightResend(ImageConsumer imageConsumer) {
      System.out.println("TDLR");
   }

   @Override
   public final synchronized boolean isConsumer(ImageConsumer newImageConsumer) {
      return this.imageConsumer == newImageConsumer;
   }

   @Override
   public final synchronized void removeConsumer(ImageConsumer newImageConsumer) {
      if (this.imageConsumer == newImageConsumer) {
         this.imageConsumer = null;
      }
   }

   @Override
   public final void startProduction(ImageConsumer imageConsumer) {
      this.addConsumer(imageConsumer);
   }

   @Override
   public final synchronized void addConsumer(ImageConsumer newImageConsumer) {
      this.imageConsumer = newImageConsumer;
      newImageConsumer.setDimensions(this.width, this.height);
      newImageConsumer.setProperties(null);
      newImageConsumer.setColorModel(this.colorModel);
      newImageConsumer.setHints(14);
   }
   public final synchronized void pushPixels() {
      if (this.imageConsumer != null) {
         this.imageConsumer.setPixels(0, 0, this.width, this.height, this.colorModel, this.pixels, 0, this.width);
         this.imageConsumer.imageComplete(2);
      }
   }
}
