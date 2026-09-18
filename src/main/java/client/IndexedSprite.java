package client;
public final class IndexedSprite extends Rasterizer2D {
   public byte[] pixelIndices;
   public final int[] palette;
   public int width;
   public int height;
   public int xOffset;
   public int yOffset;
   public int canvasWidth;
   private int canvasHeight;

   public IndexedSprite(Archive archive, String text, int loopIndex) {
      Buffer buffer = new Buffer(archive.getFile(text + ".dat"));
      Buffer buffer2;
      (buffer2 = new Buffer(archive.getFile("index.dat"))).currentPosition = buffer.readUnsignedShort();
      this.canvasWidth = buffer2.readUnsignedShort();
      this.canvasHeight = buffer2.readUnsignedShort();
      int readUnsignedByteOrLength = buffer2.readUnsignedByte();
      this.palette = new int[readUnsignedByteOrLength];

      for (int loopIndex2 = 0; loopIndex2 < readUnsignedByteOrLength - 1; loopIndex2++) {
         this.palette[loopIndex2 + 1] = buffer2.readUnsignedMedium();
      }

      for (int loopIndex3 = 0; loopIndex3 < loopIndex; loopIndex3++) {
         buffer2.currentPosition += 2;
         buffer.currentPosition = buffer.currentPosition + buffer2.readUnsignedShort() * buffer2.readUnsignedShort();
         buffer2.currentPosition++;
      }

      this.xOffset = buffer2.readUnsignedByte();
      this.yOffset = buffer2.readUnsignedByte();
      this.width = buffer2.readUnsignedShort();
      this.height = buffer2.readUnsignedShort();
      int decodedUnsignedByte = buffer2.readUnsignedByte();
      int widthOrLength = this.width * this.height;
      this.pixelIndices = new byte[widthOrLength];
      if (decodedUnsignedByte == 0) {
         for (int pixelIndex = 0; pixelIndex < widthOrLength; pixelIndex++) {
            this.pixelIndices[pixelIndex] = buffer.readByte();
         }
      } else {
         if (decodedUnsignedByte == 1) {
            for (int loopIndex4 = 0; loopIndex4 < this.width; loopIndex4++) {
               for (int loopIndex5 = 0; loopIndex5 < this.height; loopIndex5++) {
                  this.pixelIndices[loopIndex4 + loopIndex5 * this.width] = buffer.readByte();
               }
            }
         }
      }
   }
   public final void downscaleHalf() {
      this.canvasWidth /= 2;
      this.canvasHeight /= 2;
      byte[] sourcePixelIndices = new byte[this.canvasWidth * this.canvasHeight];
      int scalar = 0;

      for (int loopIndex = 0; loopIndex < this.height; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < this.width; loopIndex2++) {
            sourcePixelIndices[(loopIndex2 + this.xOffset >> 1) + (loopIndex + this.yOffset >> 1) * this.canvasWidth] = this.pixelIndices[scalar++];
         }
      }

      this.pixelIndices = sourcePixelIndices;
      this.width = this.canvasWidth;
      this.height = this.canvasHeight;
      this.xOffset = 0;
      this.yOffset = 0;
   }

   public final void resize() {
      if (this.width != this.canvasWidth || this.height != this.canvasHeight) {
         byte[] sourcePixelIndices = new byte[this.canvasWidth * this.canvasHeight];
         int scalar = 0;

         for (int loopIndex = 0; loopIndex < this.height; loopIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < this.width; loopIndex2++) {
               sourcePixelIndices[loopIndex2 + this.xOffset + (loopIndex + this.yOffset) * this.canvasWidth] = this.pixelIndices[scalar++];
            }
         }

         this.pixelIndices = sourcePixelIndices;
         this.width = this.canvasWidth;
         this.height = this.canvasHeight;
         this.xOffset = 0;
         this.yOffset = 0;
      }
   }
   public final void flipHorizontal() {
      byte[] sourcePixelIndices = new byte[this.width * this.height];
      int scalar = 0;

      for (int loopIndex = 0; loopIndex < this.height; loopIndex++) {
         for (int loopIndex2 = this.width - 1; loopIndex2 >= 0; loopIndex2--) {
            sourcePixelIndices[scalar++] = this.pixelIndices[loopIndex2 + loopIndex * this.width];
         }
      }

      this.pixelIndices = sourcePixelIndices;
      this.xOffset = this.canvasWidth - this.width - this.xOffset;
   }
   public final void flipVertical() {
      byte[] sourcePixelIndices = new byte[this.width * this.height];
      int scalar = 0;

      for (int loopIndex = this.height - 1; loopIndex >= 0; loopIndex--) {
         for (int loopIndex2 = 0; loopIndex2 < this.width; loopIndex2++) {
            sourcePixelIndices[scalar++] = this.pixelIndices[loopIndex2 + loopIndex * this.width];
         }
      }

      this.pixelIndices = sourcePixelIndices;
      this.yOffset = this.canvasHeight - this.height - this.yOffset;
   }
   public final void adjustPalette(int scalarArgument, int scalarArgument2, int scalarArgument3) {
      for (int paletteIndex = 0; paletteIndex < this.palette.length; paletteIndex++) {
         int scalar;
         if ((scalar = (this.palette[paletteIndex] >> 16 & 0xFF) + scalarArgument) < 0) {
            scalar = 0;
         } else if (scalar > 255) {
            scalar = 255;
         }

         int scalar2;
         if ((scalar2 = (this.palette[paletteIndex] >> 8 & 0xFF) + scalarArgument2) < 0) {
            scalar2 = 0;
         } else if (scalar2 > 255) {
            scalar2 = 255;
         }

         int scalar3;
         if ((scalar3 = (this.palette[paletteIndex] & 0xFF) + scalarArgument3) < 0) {
            scalar3 = 0;
         } else if (scalar3 > 255) {
            scalar3 = 255;
         }

         this.palette[paletteIndex] = (scalar << 16) + (scalar2 << 8) + scalar3;
      }
   }
   public final void drawBackground(int positionArgument, int newTopY) {
      positionArgument += this.xOffset;
      newTopY += this.yOffset;
      int scalar = positionArgument + newTopY * Rasterizer2D.width;
      int sourceLocalWidth = 0;
      int height = this.height;
      int width = this.width;
      int localWidth = Rasterizer2D.width - width;
      int scalar2 = 0;
      if (newTopY < Rasterizer2D.topY) {
         int localTopY = Rasterizer2D.topY - newTopY;
         height -= localTopY;
         newTopY = Rasterizer2D.topY;
         sourceLocalWidth = 0 + localTopY * width;
         scalar += localTopY * Rasterizer2D.width;
      }

      if (newTopY + height > Rasterizer2D.bottomY) {
         height -= newTopY + height - Rasterizer2D.bottomY;
      }

      if (positionArgument < Rasterizer2D.topX) {
         int localTopX = Rasterizer2D.topX - positionArgument;
         width -= localTopX;
         positionArgument = Rasterizer2D.topX;
         sourceLocalWidth += localTopX;
         scalar += localTopX;
         scalar2 = localTopX + 0;
         localWidth += localTopX;
      }

      if (positionArgument + width > Rasterizer2D.bottomX) {
         int scalar3 = positionArgument + width - Rasterizer2D.bottomX;
         width -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (width > 0 && height > 0) {
         int scalar4 = scalar2;
         int[] palette = this.palette;
         int position = sourceLocalWidth;
         int position2 = scalar;
         sourceLocalWidth = localWidth;
         byte[] pixelIndices = this.pixelIndices;
         int[] pixels = Rasterizer2D.pixels;
         positionArgument = height;
         int position3 = -(width >> 2);
         width = -(width & 3);

         for (int loopIndex = -positionArgument; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = position3; loopIndex2 < 0; loopIndex2++) {
               byte pixelIndex;
               if ((pixelIndex = pixelIndices[position++]) != 0) {
                  pixels[position2++] = palette[pixelIndex & 0xFF];
               } else {
                  position2++;
               }

               if ((pixelIndex = pixelIndices[position++]) != 0) {
                  pixels[position2++] = palette[pixelIndex & 0xFF];
               } else {
                  position2++;
               }

               if ((pixelIndex = pixelIndices[position++]) != 0) {
                  pixels[position2++] = palette[pixelIndex & 0xFF];
               } else {
                  position2++;
               }

               if ((pixelIndex = pixelIndices[position++]) != 0) {
                  pixels[position2++] = palette[pixelIndex & 0xFF];
               } else {
                  position2++;
               }
            }

            for (int sourceWidth = width; sourceWidth < 0; sourceWidth++) {
               byte pixelIndex2;
               if ((pixelIndex2 = pixelIndices[position++]) != 0) {
                  pixels[position2++] = palette[pixelIndex2 & 0xFF];
               } else {
                  position2++;
               }
            }

            position2 += sourceLocalWidth;
            position += scalar4;
         }
      }
   }
}
