package client;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.util.Hashtable;
public final class BufferedImageGraphicsBuffer extends GraphicsBuffer {
   private BufferedImage bufferedImage;

   public BufferedImageGraphicsBuffer(int scalarArgument, int scalarArgument2) {
      this(scalarArgument, scalarArgument2, (byte)0);
   }

   public BufferedImageGraphicsBuffer(int scalarArgument, int scalarArgument2, byte byteCodeArgument) {
      super.pixels = new int[scalarArgument * scalarArgument2 + 1];
      super.depthBuffer = new float[scalarArgument * scalarArgument2];
      super.colorModel = new DirectColorModel(32, 16711680, 65280, 255, 0);
      super.image = new BufferedImage(
         this.colorModel,
         Raster.createWritableRaster(this.colorModel.createCompatibleSampleModel(scalarArgument, scalarArgument2), new DataBufferInt(this.pixels, this.pixels.length), null),
         false,
         new Hashtable()
      );
      this.bufferedImage = new BufferedImage(scalarArgument, scalarArgument2, 2);
      super.setWidth(scalarArgument);
      super.setHeight(scalarArgument2);
      super.markGpuAllDirty();
      super.initDrawingArea();
   }
   @Override
   public final void drawGraphics(int scalarArgument, Graphics graphics, int scalarArgument2) {
      graphics.drawImage(this.image, scalarArgument2, scalarArgument, this);
   }
   public final void drawToBuffer(int scalarArgument, BufferedImageGraphicsBuffer bufferedImageGraphicsBuffer, int scalarArgument2) {
      Graphics2D graphics = (Graphics2D)bufferedImageGraphicsBuffer.image.getGraphics();
      try {
         graphics.drawImage(this.image, scalarArgument2, scalarArgument, this);
      } finally {
         graphics.dispose();
      }
      this.consumeGpuDirtyTo(bufferedImageGraphicsBuffer, scalarArgument2, scalarArgument);
   }

   @Override
   public final void finalize() {
      this.bufferedImage.getGraphics().drawImage(this.image, 0, 0, this);
   }
}
