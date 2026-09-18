package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import javax.swing.ImageIcon;
public final class Sprite extends Rasterizer2D {
   public int[] pixels;
   public int spriteWidth;
   public int spriteHeight;
   public int xOffset;
   public int yOffset;
   public int canvasWidth;
   public int canvasHeight;

   public Sprite(int newCanvasWidth, int newCanvasHeight) {
      this.pixels = new int[newCanvasWidth * newCanvasHeight];
      this.spriteWidth = this.canvasWidth = newCanvasWidth;
      this.spriteHeight = this.canvasHeight = newCanvasHeight;
      this.xOffset = this.yOffset = 0;
   }

   public Sprite(int newSpriteWidth, int newSpriteHeight, int scalarArgument, int scalarArgument2, int[] newPixels) {
      this.spriteWidth = newSpriteWidth;
      this.spriteHeight = newSpriteHeight;
      this.xOffset = 0;
      this.yOffset = 0;
      this.pixels = newPixels;
      Color color = Color.MAGENTA;
      this.makeColorTransparent(color.getRed(), color.getGreen(), color.getBlue());
   }

   public Sprite(Sprite sprite, int newSpriteHeight, int scalarArgument, int scalarArgument2, int scalarArgument3) {
      try {
         BufferedImage bufferedImage;
         if ((sprite = sprite) == null) {
            bufferedImage = null;
         } else {
            int[] pixels = sprite.pixels;
            newSpriteHeight = sprite.spriteHeight;
            int spriteWidth = sprite.spriteWidth;
            BufferedImage sourceBufferedImage;
            (sourceBufferedImage = new BufferedImage(spriteWidth, newSpriteHeight, 1)).setRGB(0, 0, spriteWidth, newSpriteHeight, pixels, 0, spriteWidth);
            sourceBufferedImage.createGraphics().dispose();
            bufferedImage = sourceBufferedImage;
         }

         ImageIcon imageIcon = new ImageIcon(bufferedImage);
         BufferedImage bufferedImage2 = toBufferedImage(imageIcon.getImage()).getSubimage(0, 0, scalarArgument2, 7);
         Image image = new ImageIcon(bufferedImage2).getImage();
         ImageIcon imageIcon2 = new ImageIcon(image);
         this.spriteWidth = imageIcon2.getIconWidth();
         this.spriteHeight = imageIcon2.getIconHeight();
         this.canvasWidth = this.spriteWidth;
         this.canvasHeight = this.spriteHeight;
         this.xOffset = 0;
         this.yOffset = 0;
         this.pixels = new int[this.spriteWidth * this.spriteHeight];
         new PixelGrabber(image, 0, 0, this.spriteWidth, this.spriteHeight, this.pixels, 0, this.spriteWidth).grabPixels();
         this.makeColorTransparent(0, 0, 0);
      } catch (Exception exception) {
         System.out.println(exception);
      }
   }
   private static BufferedImage toBufferedImage(Image image) {
      if (image instanceof BufferedImage) {
         return (BufferedImage)image;
      }

      image = new ImageIcon(image).getImage();
      BufferedImage bufferedImage = null;
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

      try {
         bufferedImage = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(image.getWidth(null), image.getHeight(null), 1);
      } catch (HeadlessException exception) {
      }

      if (bufferedImage == null) {
         bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
      }

      Graphics2D graphics2D;
      (graphics2D = bufferedImage.createGraphics()).drawImage(image, 0, 0, null);
      graphics2D.dispose();
      return bufferedImage;
   }
   public final void drawArgbSprite(int newTopX, int newTopY) {
      newTopX += this.xOffset;
      newTopY = 0 + this.yOffset;
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int scalar2 = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar3 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         spriteHeight -= localTopY;
         newTopY = Rasterizer2D.topY;
         scalar2 = 0 + localTopY * spriteWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= newTopY + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar2 += localTopX;
         scalar += localTopX;
         scalar3 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar4;
         scalar3 += scalar4;
         localWidth += scalar4;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         int sourceLocalWidth = localWidth;
         int[] pixels = Rasterizer2D.pixels;
         int pixelIndex = scalar;
         int scalar5 = scalar3;
         int[] pixels2 = this.pixels;
         scalar = scalar2;
         newTopY = spriteHeight;
         newTopX = spriteWidth;

         for (int loopIndex = 0; loopIndex < newTopY; loopIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < newTopX; loopIndex2++) {
               int scalar6;
               if ((scalar6 = pixels2[scalar++]) != 0) {
                  int scalar7 = scalar6 >>> 24;
                  int scalar8 = 256 - scalar7;
                  int pixel = pixels[pixelIndex];
                  pixels[pixelIndex++] = ((scalar6 & 16711935) * scalar7 + (pixel & 16711935) * scalar8 & -16711936)
                        + ((scalar6 & 0xFF00) * scalar7 + (pixel & 0xFF00) * scalar8 & 0xFF0000)
                     >> 8;
               } else {
                  pixelIndex++;
               }
            }

            pixelIndex += sourceLocalWidth;
            scalar += scalar5;
         }
      }
   }
   private void makeColorTransparent(int scalarArgument, int scalarArgument2, int scalarArgument3) {
      for (int pixelIndex = 0; pixelIndex < this.pixels.length; pixelIndex++) {
         if ((this.pixels[pixelIndex] >> 16 & 0xFF) == scalarArgument && (this.pixels[pixelIndex] >> 8 & 0xFF) == scalarArgument2 && (this.pixels[pixelIndex] & 0xFF) == scalarArgument3) {
            this.pixels[pixelIndex] = 0;
         }
      }
   }
   public final void drawSpriteDefaultAlpha(int newTopX, int newTopY) {
      newTopX += this.xOffset;
      newTopY += this.yOffset;
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int scalar2 = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar3 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         spriteHeight -= localTopY;
         newTopY = Rasterizer2D.topY;
         scalar2 = 0 + localTopY * spriteWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= newTopY + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar2 += localTopX;
         scalar += localTopX;
         scalar3 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar4;
         scalar3 += scalar4;
         localWidth += scalar4;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         blitAlpha(scalar2, spriteWidth, Rasterizer2D.pixels, this.pixels, scalar3, spriteHeight, localWidth, 225, scalar);
      }
   }
   public final void drawSpriteAlpha(int newTopX, int newTopY, int transparency) {
      newTopX += this.xOffset;
      newTopY += this.yOffset;
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int scalar2 = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar3 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         spriteHeight -= localTopY;
         newTopY = Rasterizer2D.topY;
         scalar2 = 0 + localTopY * spriteWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= newTopY + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar2 += localTopX;
         scalar += localTopX;
         scalar3 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar4;
         scalar3 += scalar4;
         localWidth += scalar4;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         blitAlpha(scalar2, spriteWidth, Rasterizer2D.pixels, this.pixels, scalar3, spriteHeight, localWidth, transparency, scalar);
      }
   }

   public Sprite(byte[] byteBufferArgument) {
      try {
         Image image = Toolkit.getDefaultToolkit().createImage(byteBufferArgument);
         ImageIcon imageIcon = new ImageIcon(image);
         this.spriteWidth = imageIcon.getIconWidth();
         this.spriteHeight = imageIcon.getIconHeight();
         this.canvasWidth = this.spriteWidth;
         this.canvasHeight = this.spriteHeight;
         this.xOffset = 0;
         this.yOffset = 0;
         this.pixels = new int[this.spriteWidth * this.spriteHeight];
         new PixelGrabber(image, 0, 0, this.spriteWidth, this.spriteHeight, this.pixels, 0, this.spriteWidth).grabPixels();
         this.makeColorTransparent(255, 0, 255);
      } catch (Exception exception) {
         System.out.println(exception);
      }
   }

   public Sprite(byte[] byteBufferArgument, Component component) {
      try {
         Image image = Toolkit.getDefaultToolkit().createImage(byteBufferArgument);
         MediaTracker mediaTracker;
         (mediaTracker = new MediaTracker(component)).addImage(image, 0);
         mediaTracker.waitForAll();
         this.spriteWidth = image.getWidth(component);
         this.spriteHeight = image.getHeight(component);
         this.canvasWidth = this.spriteWidth;
         this.canvasHeight = this.spriteHeight;
         this.xOffset = 0;
         this.yOffset = 0;
         this.pixels = new int[this.spriteWidth * this.spriteHeight];
         new PixelGrabber(image, 0, 0, this.spriteWidth, this.spriteHeight, this.pixels, 0, this.spriteWidth).grabPixels();
      } catch (Exception exception) {
         System.out.println("Error converting jpg");
      }
   }
   public final void drawOutlinedSprite(int newTopX, int newTopY, int sourceLocalSpriteHeight) {
      int localSpriteWidth = this.spriteWidth + 2;
      int localSpriteHeight = this.spriteHeight + 2;
      int[] values = new int[localSpriteWidth * localSpriteHeight];

      for (int loopIndex = 0; loopIndex < this.spriteWidth; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < this.spriteHeight; loopIndex2++) {
            if (this.pixels[loopIndex + loopIndex2 * this.spriteWidth] != 0) {
               values[loopIndex + 1 + (loopIndex2 + 1) * localSpriteWidth] = this.pixels[loopIndex + loopIndex2 * this.spriteWidth];
            }
         }
      }

      for (int loopIndex3 = 0; loopIndex3 < localSpriteWidth; loopIndex3++) {
         for (int loopIndex4 = 0; loopIndex4 < localSpriteHeight; loopIndex4++) {
            if (values[loopIndex3 + loopIndex4 * localSpriteWidth] == 0) {
               if (loopIndex3 < localSpriteWidth - 1 && values[loopIndex3 + 1 + loopIndex4 * localSpriteWidth] > 0 && values[loopIndex3 + 1 + loopIndex4 * localSpriteWidth] != 16777215) {
                  values[loopIndex3 + loopIndex4 * localSpriteWidth] = sourceLocalSpriteHeight;
               }

               if (loopIndex3 > 0 && values[loopIndex3 - 1 + loopIndex4 * localSpriteWidth] > 0 && values[loopIndex3 - 1 + loopIndex4 * localSpriteWidth] != 16777215) {
                  values[loopIndex3 + loopIndex4 * localSpriteWidth] = sourceLocalSpriteHeight;
               }

               if (loopIndex4 < localSpriteHeight - 1 && values[loopIndex3 + (loopIndex4 + 1) * localSpriteWidth] > 0 && values[loopIndex3 + (loopIndex4 + 1) * localSpriteWidth] != 16777215) {
                  values[loopIndex3 + loopIndex4 * localSpriteWidth] = sourceLocalSpriteHeight;
               }

               if (loopIndex4 > 0 && values[loopIndex3 + (loopIndex4 - 1) * localSpriteWidth] > 0 && values[loopIndex3 + (loopIndex4 - 1) * localSpriteWidth] != 16777215) {
                  values[loopIndex3 + loopIndex4 * localSpriteWidth] = sourceLocalSpriteHeight;
               }
            }
         }
      }

      newTopX--;
      newTopY--;
      newTopX += this.xOffset;
      newTopY += this.yOffset;
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int scalar2 = 0;
      sourceLocalSpriteHeight = localSpriteHeight;
      localSpriteHeight = Rasterizer2D.width - localSpriteWidth;
      int scalar3 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         sourceLocalSpriteHeight -= localTopY;
         newTopY = Rasterizer2D.topY;
         scalar2 = 0 + localTopY * localSpriteWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + sourceLocalSpriteHeight > Rasterizer2D.bottomY) {
         sourceLocalSpriteHeight -= newTopY + sourceLocalSpriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         localSpriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar2 += localTopX;
         scalar += localTopX;
         scalar3 = localTopX + 0;
         localSpriteHeight += localTopX;
      }

      if (newTopX + localSpriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + localSpriteWidth - Rasterizer2D.bottomX;
         localSpriteWidth -= scalar4;
         scalar3 += scalar4;
         localSpriteHeight += scalar4;
      }

      if (localSpriteWidth > 0 && sourceLocalSpriteHeight > 0) {
         blitTransparent(Rasterizer2D.pixels, values, scalar2, scalar, localSpriteWidth, sourceLocalSpriteHeight, localSpriteHeight, scalar3);
      }
   }

   public Sprite(Archive archive, String text, int loopIndex) {
      Buffer buffer = new Buffer(archive.getFile(text + ".dat"));
      Buffer buffer2;
      (buffer2 = new Buffer(archive.getFile("index.dat"))).currentPosition = buffer.readUnsignedShort();
      this.canvasWidth = buffer2.readUnsignedShort();
      this.canvasHeight = buffer2.readUnsignedShort();
      int decodedUnsignedByte;
      int[] pixel = new int[decodedUnsignedByte = buffer2.readUnsignedByte()];

      for (int loopIndex2 = 0; loopIndex2 < decodedUnsignedByte - 1; loopIndex2++) {
         pixel[loopIndex2 + 1] = buffer2.readUnsignedMedium();
         if (pixel[loopIndex2 + 1] == 0) {
            pixel[loopIndex2 + 1] = 1;
         }
      }

      for (int loopIndex3 = 0; loopIndex3 < loopIndex; loopIndex3++) {
         buffer2.currentPosition += 2;
         buffer.currentPosition = buffer.currentPosition + buffer2.readUnsignedShort() * buffer2.readUnsignedShort();
         buffer2.currentPosition++;
      }

      this.xOffset = buffer2.readUnsignedByte();
      this.yOffset = buffer2.readUnsignedByte();
      this.spriteWidth = buffer2.readUnsignedShort();
      this.spriteHeight = buffer2.readUnsignedShort();
      int readUnsignedByte2 = buffer2.readUnsignedByte();
      int spriteWidthOrLength = this.spriteWidth * this.spriteHeight;
      this.pixels = new int[spriteWidthOrLength];
      if (readUnsignedByte2 == 0) {
         for (int pixelIndex = 0; pixelIndex < spriteWidthOrLength; pixelIndex++) {
            this.pixels[pixelIndex] = pixel[buffer.readUnsignedByte()];
         }
      } else {
         if (readUnsignedByte2 == 1) {
            for (int loopIndex4 = 0; loopIndex4 < this.spriteWidth; loopIndex4++) {
               for (int loopIndex5 = 0; loopIndex5 < this.spriteHeight; loopIndex5++) {
                  this.pixels[loopIndex4 + loopIndex5 * this.spriteWidth] = pixel[buffer.readUnsignedByte()];
               }
            }
         }
      }
   }
   public final void adjustRgb(int scalarArgument, int scalarArgument2, int scalarArgument3) {
      for (int pixelIndex = 0; pixelIndex < this.pixels.length; pixelIndex++) {
         int localPixels;
         if ((localPixels = this.pixels[pixelIndex]) != 0) {
            int scalar;
            if ((scalar = (localPixels >> 16 & 0xFF) + scalarArgument) <= 0) {
               scalar = 1;
            } else if (scalar > 255) {
               scalar = 255;
            }

            int scalar2;
            if ((scalar2 = (localPixels >> 8 & 0xFF) + scalarArgument2) <= 0) {
               scalar2 = 1;
            } else if (scalar2 > 255) {
               scalar2 = 255;
            }

            if ((localPixels = (localPixels = localPixels & 0xFF) + scalarArgument3) <= 0) {
               localPixels = 1;
            } else if (localPixels > 255) {
               localPixels = 255;
            }

            this.pixels[pixelIndex] = (scalar << 16) + (scalar2 << 8) + localPixels;
         }
      }
   }
   public final void expandToCanvas() {
      try {
         int[] sourcePixels = new int[this.canvasWidth * this.canvasHeight];

         for (int loopIndex = 0; loopIndex < this.spriteHeight; loopIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < this.spriteWidth; loopIndex2++) {
               sourcePixels[(loopIndex + this.yOffset) * this.canvasWidth + loopIndex2 + this.xOffset] = this.pixels[loopIndex * this.spriteWidth + loopIndex2];
            }
         }

         this.pixels = sourcePixels;
         this.spriteWidth = this.canvasWidth;
         this.spriteHeight = this.canvasHeight;
         this.xOffset = 0;
         this.yOffset = 0;
      } catch (RuntimeException exception) {
         SignLink.reporterror("26341, " + exception.toString());
         throw new RuntimeException();
      }
   }
   public final void drawOpaqueSprite(int topX, int newIndex) {
      topX += this.xOffset;
      newIndex += this.yOffset;
      int sourceTopX = topX + newIndex * Rasterizer2D.width;
      int scalar = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar2 = 0;
      if (newIndex < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newIndex;
         spriteHeight -= localTopY;
         newIndex = Rasterizer2D.topY;
         scalar = 0 + localTopY * spriteWidth;
         sourceTopX += localTopY * Rasterizer2D.width;
      }

      if (newIndex + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= newIndex + spriteHeight - Rasterizer2D.bottomY;
      }

      if (topX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - topX;
         spriteWidth -= localTopX;
         topX = Rasterizer2D.topX;
         scalar += localTopX;
         sourceTopX += localTopX;
         scalar2 = localTopX + 0;
         localWidth += localTopX;
      }

      if (topX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar3 = topX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         int[] pixels = Rasterizer2D.pixels;
         int[] pixels2 = this.pixels;
         int sourceLocalWidth = localWidth;
         int position = scalar;
         scalar = scalar2;
         int sourceSpriteHeight = spriteHeight;
         newIndex = spriteWidth;
         topX = sourceTopX;
         int position2 = -(newIndex >> 2);
         newIndex = -(newIndex & 3);

         for (int loopIndex = -sourceSpriteHeight; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = position2; loopIndex2 < 0; loopIndex2++) {
               pixels[topX++] = pixels2[position++];
               pixels[topX++] = pixels2[position++];
               pixels[topX++] = pixels2[position++];
               pixels[topX++] = pixels2[position++];
            }

            for (int loopIndex3 = newIndex; loopIndex3 < 0; loopIndex3++) {
               pixels[topX++] = pixels2[position++];
            }

            topX += sourceLocalWidth;
            position += scalar;
         }
      }
   }
   public final void drawSpriteHalfAlpha(int newTopX, int newTopY) {
      newTopX += this.xOffset;
      newTopY += this.yOffset;
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int scalar2 = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar3 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         spriteHeight -= localTopY;
         newTopY = Rasterizer2D.topY;
         scalar2 = 0 + localTopY * spriteWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= newTopY + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar2 += localTopX;
         scalar += localTopX;
         scalar3 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar4;
         scalar3 += scalar4;
         localWidth += scalar4;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         blitAlpha(scalar2, spriteWidth, Rasterizer2D.pixels, this.pixels, scalar3, spriteHeight, localWidth, 128, scalar);
      }
   }
   public final void drawSprite(int newTopX, int newTopY) {
      newTopX += this.xOffset;
      newTopY += this.yOffset;
      int scalar = newTopX + newTopY * Rasterizer2D.width;
      int scalar2 = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar3 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         spriteHeight -= localTopY;
         newTopY = Rasterizer2D.topY;
         scalar2 = 0 + localTopY * spriteWidth;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= newTopY + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar2 += localTopX;
         scalar += localTopX;
         scalar3 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar4;
         scalar3 += scalar4;
         localWidth += scalar4;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         blitTransparent(Rasterizer2D.pixels, this.pixels, scalar2, scalar, spriteWidth, spriteHeight, localWidth, scalar3);
      }
   }
   private static void blitTransparent(int[] values, int[] newValues, int positionArgument, int newIndex, int spriteWidth, int spriteHeight, int scalarArgument, int scalarArgument2) {
      int position = -(spriteWidth >> 2);
      spriteWidth = -(spriteWidth & 3);

      for (int loopIndex = -spriteHeight; loopIndex < 0; loopIndex++) {
         for (int loopIndex2 = position; loopIndex2 < 0; loopIndex2++) {
            int scalar;
            if ((scalar = newValues[positionArgument++]) != 0) {
               values[newIndex++] = scalar;
            } else {
               newIndex++;
            }

            if ((scalar = newValues[positionArgument++]) != 0) {
               values[newIndex++] = scalar;
            } else {
               newIndex++;
            }

            if ((scalar = newValues[positionArgument++]) != 0) {
               values[newIndex++] = scalar;
            } else {
               newIndex++;
            }

            if ((scalar = newValues[positionArgument++]) != 0) {
               values[newIndex++] = scalar;
            } else {
               newIndex++;
            }
         }

         for (int sourceSpriteWidth = spriteWidth; sourceSpriteWidth < 0; sourceSpriteWidth++) {
            int scalar2;
            if ((scalar2 = newValues[positionArgument++]) != 0) {
               values[newIndex++] = scalar2;
            } else {
               newIndex++;
            }
         }

         newIndex += scalarArgument;
         positionArgument += scalarArgument2;
      }
   }
   private static void blitAlpha(int scalarArgument, int spriteWidth, int[] values, int[] newValues, int scalarArgument2, int newIndex, int scalarArgument3, int transparency, int scalarArgument4) {
      int scalar = 256 - transparency;

      for (int loopIndex = -newIndex; loopIndex < 0; loopIndex++) {
         for (int loopIndex2 = -spriteWidth; loopIndex2 < 0; loopIndex2++) {
            int scalar2;
            if ((scalar2 = newValues[scalarArgument++]) != 0) {
               int scalar3 = values[scalarArgument4];
               values[scalarArgument4++] = ((scalar2 & 16711935) * transparency + (scalar3 & 16711935) * scalar & -16711936)
                     + ((scalar2 & 0xFF00) * transparency + (scalar3 & 0xFF00) * scalar & 0xFF0000)
                  >> 8;
            } else {
               scalarArgument4++;
            }
         }

         scalarArgument4 += scalarArgument3;
         scalarArgument += scalarArgument2;
      }
   }
   public final void drawRotatedMasked(int loopIndex, int scalarArgument, int[] compassMaskLineWidths, int scalarArgument2, int[] values, int scalarArgument3, int minimapDrawY, int minimapDrawX, int scalarArgument4, int scalarArgument5) {
      try {
         scalarArgument4 = -scalarArgument4 / 2;
         int scalar = -loopIndex / 2;
         int scalar2 = (int)(Math.sin(scalarArgument / 326.11) * 65536.0);
         scalarArgument = (int)(Math.cos(scalarArgument / 326.11) * 65536.0);
         scalar2 = scalar2 * scalarArgument2 >> 8;
         scalarArgument = scalarArgument * scalarArgument2 >> 8;
         scalarArgument2 = (scalarArgument5 << 16) + scalar * scalar2 + scalarArgument4 * scalarArgument;
         scalarArgument3 = (scalarArgument3 << 16) + scalar * scalarArgument - scalarArgument4 * scalar2;
         scalarArgument4 = minimapDrawX + minimapDrawY * Rasterizer2D.width;

         for (int compassMaskLineWidthIndex = 0; compassMaskLineWidthIndex < loopIndex; compassMaskLineWidthIndex++) {
            minimapDrawX = values[compassMaskLineWidthIndex];
            scalarArgument5 = scalarArgument4 + minimapDrawX;
            scalar = scalarArgument2 + scalarArgument * minimapDrawX;
            int scalar3 = scalarArgument3 - scalar2 * minimapDrawX;

            for (int loopIndex2 = -compassMaskLineWidths[compassMaskLineWidthIndex]; loopIndex2 < 0; loopIndex2++) {
               Rasterizer2D.pixels[scalarArgument5++] = this.pixels[(scalar >> 16) + (scalar3 >> 16) * this.spriteWidth];
               scalar += scalarArgument;
               scalar3 -= scalar2;
            }

            scalarArgument2 += scalar2;
            scalarArgument3 += scalarArgument;
            scalarArgument4 += Rasterizer2D.width;
         }
      } catch (Exception exception) {
      }
   }
   public final void drawRotated20x20(int scalarArgument, double calculationArgument, int scalarArgument2) {
      try {
         int scalar = (int)(Math.sin(calculationArgument) * 65536.0);
         int scalar2 = (int)(Math.cos(calculationArgument) * 65536.0);
         scalar = scalar << 8 >> 8;
         int scalar3 = scalar2 << 8 >> 8;
         int scalar4 = 983040 + scalar * -10 + scalar3 * -10;
         int scalar5 = 983040 + scalar3 * -10 - scalar * -10;
         int scalar6 = scalarArgument2 + scalarArgument * Rasterizer2D.width;

         for (int loopIndex = 0; loopIndex < 20; loopIndex++) {
            int scalar7 = scalar6;
            int scalar8 = scalar4;
            int scalar9 = scalar5;

            for (int loopIndex2 = -20; loopIndex2 < 0; loopIndex2++) {
               int pixelOrPixels;
               if ((pixelOrPixels = this.pixels[(scalar8 >> 16) + (scalar9 >> 16) * this.spriteWidth]) != 0) {
                  Rasterizer2D.pixels[scalar7++] = pixelOrPixels;
               } else {
                  scalar7++;
               }

               scalar8 += scalar3;
               scalar9 -= scalar;
            }

            scalar4 += scalar;
            scalar5 += scalar3;
            scalar6 += Rasterizer2D.width;
         }
      } catch (Exception exception) {
      }
   }
   public final void drawMaskedMinimapBufferSprite(IndexedSprite indexedSprite, int sourceSpriteWidth, int scalarArgument) {
      scalarArgument += this.xOffset;
      sourceSpriteWidth += this.yOffset;
      int sourceSpriteHeight = scalarArgument + sourceSpriteWidth * 172;
      int scalar = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int scalar2 = 172 - spriteWidth;
      int scalar3 = 0;
      if (sourceSpriteWidth < 0) {
         int scalar4 = 0 - sourceSpriteWidth;
         spriteHeight -= scalar4;
         sourceSpriteWidth = 0;
         scalar = 0 + scalar4 * spriteWidth;
         sourceSpriteHeight += scalar4 * 172;
      }

      if (sourceSpriteWidth + spriteHeight > 156) {
         spriteHeight -= sourceSpriteWidth + spriteHeight - 156;
      }

      if (scalarArgument < 0) {
         int scalar5 = 0 - scalarArgument;
         spriteWidth -= scalar5;
         scalarArgument = 0;
         scalar += scalar5;
         sourceSpriteHeight += scalar5;
         scalar3 = scalar5 + 0;
         scalar2 += scalar5;
      }

      if (scalarArgument + spriteWidth > 172) {
         int scalar6 = scalarArgument + spriteWidth - 172;
         spriteWidth -= scalar6;
         scalar3 += scalar6;
         scalar2 += scalar6;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         int position = scalar;
         int scalar7 = scalar3;
         int pixelIndex = sourceSpriteHeight;
         int scalar8 = scalar2;
         int[] pixels = Rasterizer2D.pixels;
         sourceSpriteHeight = spriteHeight;
         byte[] pixelIndices = indexedSprite.pixelIndices;
         sourceSpriteWidth = spriteWidth;
         int[] pixels2 = this.pixels;
         int position2 = -(sourceSpriteWidth >> 2);
         sourceSpriteWidth = -(sourceSpriteWidth & 3);

         for (int loopIndex = -sourceSpriteHeight; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = position2; loopIndex2 < 0; loopIndex2++) {
               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }
            }

            for (int loopIndex3 = sourceSpriteWidth; loopIndex3 < 0; loopIndex3++) {
               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }
            }

            pixelIndex += scalar8;
            position += scalar7;
         }
      }
   }
   public final void drawMaskedSprite(IndexedSprite indexedSprite, int positionArgument, int newTopX) {
      newTopX += this.xOffset;
      positionArgument += this.yOffset;
      int sourceSpriteHeight = newTopX + positionArgument * Rasterizer2D.width;
      int scalar = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = Rasterizer2D.width - spriteWidth;
      int scalar2 = 0;
      if (positionArgument < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - positionArgument;
         spriteHeight -= localTopY;
         positionArgument = Rasterizer2D.topY;
         scalar = 0 + localTopY * spriteWidth;
         sourceSpriteHeight += localTopY * Rasterizer2D.width;
      }

      if (positionArgument + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= positionArgument + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar += localTopX;
         sourceSpriteHeight += localTopX;
         scalar2 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar3 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         int position = scalar;
         int scalar4 = scalar2;
         int pixelIndex = sourceSpriteHeight;
         int sourceLocalWidth = localWidth;
         int[] pixels = Rasterizer2D.pixels;
         sourceSpriteHeight = spriteHeight;
         byte[] pixelIndices = indexedSprite.pixelIndices;
         positionArgument = spriteWidth;
         int[] pixels2 = this.pixels;
         int position2 = -(positionArgument >> 2);
         positionArgument = -(positionArgument & 3);

         for (int loopIndex = -sourceSpriteHeight; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = position2; loopIndex2 < 0; loopIndex2++) {
               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }
            }

            for (int loopIndex3 = positionArgument; loopIndex3 < 0; loopIndex3++) {
               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[pixelIndex++] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }
            }

            pixelIndex += sourceLocalWidth;
            position += scalar4;
         }
      }
   }
   public final void drawMappedMaskedMinimapSprite(IndexedSprite indexedSprite, int positionArgument, int newTopX) {
      newTopX += this.xOffset;
      positionArgument += this.yOffset;
      int sourceSpriteHeight = newTopX + positionArgument * 172;
      int scalar = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int scalar2 = 172 - spriteWidth;
      int scalar3 = 0;
      if (positionArgument < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - positionArgument;
         spriteHeight -= localTopY;
         positionArgument = Rasterizer2D.topY;
         scalar = 0 + localTopY * spriteWidth;
         sourceSpriteHeight += localTopY * 172;
      }

      if (positionArgument + spriteHeight > Rasterizer2D.bottomY) {
         spriteHeight -= positionArgument + spriteHeight - Rasterizer2D.bottomY;
      }

      if (newTopX < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = Rasterizer2D.topX;
         scalar += localTopX;
         sourceSpriteHeight += localTopX;
         scalar3 = localTopX + 0;
         scalar2 += localTopX;
      }

      if (newTopX + spriteWidth > Rasterizer2D.bottomX) {
         int scalar4 = newTopX + spriteWidth - Rasterizer2D.bottomX;
         spriteWidth -= scalar4;
         scalar3 += scalar4;
         scalar2 += scalar4;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         int position = scalar;
         int scalar5 = scalar3;
         int pixelIndex = sourceSpriteHeight;
         int scalar6 = scalar2;
         int[] pixels = Rasterizer2D.pixels;
         sourceSpriteHeight = spriteHeight;
         byte[] pixelIndices = indexedSprite.pixelIndices;
         positionArgument = spriteWidth;
         int[] pixels2 = this.pixels;
         int position2 = -(positionArgument >> 2);
         positionArgument = -(positionArgument & 3);

         for (int loopIndex = -sourceSpriteHeight; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = position2; loopIndex2 < 0; loopIndex2++) {
               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[toPixelIndex(pixelIndex++)] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[toPixelIndex(pixelIndex++)] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[toPixelIndex(pixelIndex++)] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }

               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[toPixelIndex(pixelIndex++)] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }
            }

            for (int loopIndex3 = positionArgument; loopIndex3 < 0; loopIndex3++) {
               if ((sourceSpriteHeight = pixels2[position++]) != 0 && pixelIndices[pixelIndex] == 0) {
                  pixels[toPixelIndex(pixelIndex++)] = sourceSpriteHeight;
               } else {
                  pixelIndex++;
               }
            }

            pixelIndex += scalar6;
            position += scalar5;
         }
      }
   }
}
