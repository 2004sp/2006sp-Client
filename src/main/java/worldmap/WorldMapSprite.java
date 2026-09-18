package worldmap;
public final class WorldMapSprite extends WorldMapRasterizer2D {
   public int[] pixels;
   public int spriteWidth;
   public int spriteHeight;
   public int xOffset;
   public int yOffset;
   public final void initDrawingArea() {
      WorldMapRasterizer2D.setRasterBuffer(this.pixels, this.spriteWidth, this.spriteHeight);
   }
   public final void drawSprite(int newTopX, int newTopY) {
      newTopX += this.xOffset;
      newTopY += this.yOffset;
      int position = newTopX + newTopY * WorldMapRasterizer2D.width;
      int scalar = 0;
      int spriteHeight = this.spriteHeight;
      int spriteWidth = this.spriteWidth;
      int localWidth = WorldMapRasterizer2D.width - spriteWidth;
      int scalar2 = 0;
      if (newTopY < WorldMapRasterizer2D.topY) {
         int localTopY = WorldMapRasterizer2D.topY - newTopY;
         spriteHeight -= localTopY;
         newTopY = WorldMapRasterizer2D.topY;
         scalar = 0 + localTopY * spriteWidth;
         position += localTopY * WorldMapRasterizer2D.width;
      }

      if (newTopY + spriteHeight > WorldMapRasterizer2D.bottomY) {
         spriteHeight -= newTopY + spriteHeight - WorldMapRasterizer2D.bottomY;
      }

      if (newTopX < WorldMapRasterizer2D.topX) {
         int localTopX = WorldMapRasterizer2D.topX - newTopX;
         spriteWidth -= localTopX;
         newTopX = WorldMapRasterizer2D.topX;
         scalar += localTopX;
         position += localTopX;
         scalar2 = localTopX + 0;
         localWidth += localTopX;
      }

      if (newTopX + spriteWidth > WorldMapRasterizer2D.bottomX) {
         int scalar3 = newTopX + spriteWidth - WorldMapRasterizer2D.bottomX;
         spriteWidth -= scalar3;
         scalar2 += scalar3;
         localWidth += scalar3;
      }

      if (spriteWidth > 0 && spriteHeight > 0) {
         int sourceSpriteHeight = spriteHeight;
         spriteHeight = spriteWidth;
         int position2 = position;
         position = scalar;
         int[] pixels = this.pixels;
         int[] pixels2 = WorldMapRasterizer2D.pixels;
         int position3 = -(spriteHeight >> 2);
         spriteHeight = -(spriteHeight & 3);

         for (int loopIndex = -sourceSpriteHeight; loopIndex < 0; loopIndex++) {
            for (int loopIndex2 = position3; loopIndex2 < 0; loopIndex2++) {
               if ((spriteWidth = pixels[position++]) != 0) {
                  pixels2[position2++] = spriteWidth;
               } else {
                  position2++;
               }

               if ((spriteWidth = pixels[position++]) != 0) {
                  pixels2[position2++] = spriteWidth;
               } else {
                  position2++;
               }

               if ((spriteWidth = pixels[position++]) != 0) {
                  pixels2[position2++] = spriteWidth;
               } else {
                  position2++;
               }

               if ((spriteWidth = pixels[position++]) != 0) {
                  pixels2[position2++] = spriteWidth;
               } else {
                  position2++;
               }
            }

            for (int sourceSpriteHeight2 = spriteHeight; sourceSpriteHeight2 < 0; sourceSpriteHeight2++) {
               if ((spriteWidth = pixels[position++]) != 0) {
                  pixels2[position2++] = spriteWidth;
               } else {
                  position2++;
               }
            }

            position2 += localWidth;
            position += scalar2;
         }
      }
   }

   public WorldMapSprite(int newSpriteWidth, int newSpriteHeight) {
      this.pixels = new int[newSpriteWidth * newSpriteHeight];
      this.spriteWidth = newSpriteWidth;
      this.spriteHeight = newSpriteHeight;
      this.xOffset = this.yOffset = 0;
   }

   public WorldMapSprite(WorldMapArchive worldMapArchive, String text, int loopIndex) {
      WorldMapBuffer worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile(text + ".dat", null));
      WorldMapBuffer worldMapBuffer2;
      (worldMapBuffer2 = new WorldMapBuffer(worldMapArchive.getFile("index.dat", null))).currentPosition = worldMapBuffer.readUnsignedShort();
      worldMapBuffer2.readUnsignedShort();
      worldMapBuffer2.readUnsignedShort();
      int paletteSize;
      int[] pixel = new int[paletteSize = worldMapBuffer2.readUnsignedByte()];

      for (int loopIndex2 = 0; loopIndex2 < paletteSize - 1; loopIndex2++) {
         pixel[loopIndex2 + 1] = worldMapBuffer2.readUnsignedMedium();
         if (pixel[loopIndex2 + 1] == 0) {
            pixel[loopIndex2 + 1] = 1;
         }
      }

      for (int loopIndex3 = 0; loopIndex3 < loopIndex; loopIndex3++) {
         worldMapBuffer2.currentPosition += 2;
         worldMapBuffer.currentPosition = worldMapBuffer.currentPosition + worldMapBuffer2.readUnsignedShort() * worldMapBuffer2.readUnsignedShort();
         worldMapBuffer2.currentPosition++;
      }

      this.xOffset = worldMapBuffer2.readUnsignedByte();
      this.yOffset = worldMapBuffer2.readUnsignedByte();
      this.spriteWidth = worldMapBuffer2.readUnsignedShort();
      this.spriteHeight = worldMapBuffer2.readUnsignedShort();
      int pixelOrder = worldMapBuffer2.readUnsignedByte();
      long pixelCount = (long)this.spriteWidth * (long)this.spriteHeight;
      if (this.spriteWidth <= 0 || this.spriteHeight <= 0 || pixelCount > 4194304L) {
         throw new IllegalArgumentException("Invalid sprite dimensions: " + this.spriteWidth + "x" + this.spriteHeight);
      }
      int spriteWidthOrLength = (int)pixelCount;
      this.pixels = new int[spriteWidthOrLength];
      if (pixelOrder == 0) {
         for (int pixelIndex = 0; pixelIndex < spriteWidthOrLength; pixelIndex++) {
            this.pixels[pixelIndex] = pixel[worldMapBuffer.readUnsignedByte()];
         }
      } else {
         if (pixelOrder == 1) {
            for (int loopIndex4 = 0; loopIndex4 < this.spriteWidth; loopIndex4++) {
               for (int loopIndex5 = 0; loopIndex5 < this.spriteHeight; loopIndex5++) {
                  this.pixels[loopIndex4 + loopIndex5 * this.spriteWidth] = pixel[worldMapBuffer.readUnsignedByte()];
               }
            }
         }
      }
   }
}
