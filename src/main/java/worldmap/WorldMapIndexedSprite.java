package worldmap;
public final class WorldMapIndexedSprite extends WorldMapRasterizer2D {
   public byte[] pixelIndices;
   public int[] palette;
   public int width;
   public int height;
   public int xOffset;
   public int yOffset;
   public int canvasWidth;
   public int canvasHeight;

   public WorldMapIndexedSprite(WorldMapArchive worldMapArchive, String text, int loopIndex) {
      WorldMapBuffer worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile(text + ".dat", null));
      WorldMapBuffer worldMapBuffer2;
      (worldMapBuffer2 = new WorldMapBuffer(worldMapArchive.getFile("index.dat", null))).currentPosition = worldMapBuffer.readUnsignedShort();
      this.canvasWidth = worldMapBuffer2.readUnsignedShort();
      this.canvasHeight = worldMapBuffer2.readUnsignedShort();
      int paletteSize = worldMapBuffer2.readUnsignedByte();
      this.palette = new int[paletteSize];

      for (int loopIndex2 = 0; loopIndex2 < paletteSize - 1; loopIndex2++) {
         this.palette[loopIndex2 + 1] = worldMapBuffer2.readUnsignedMedium();
      }

      for (int loopIndex3 = 0; loopIndex3 < loopIndex; loopIndex3++) {
         worldMapBuffer2.currentPosition += 2;
         worldMapBuffer.currentPosition = worldMapBuffer.currentPosition + worldMapBuffer2.readUnsignedShort() * worldMapBuffer2.readUnsignedShort();
         worldMapBuffer2.currentPosition++;
      }

      this.xOffset = worldMapBuffer2.readUnsignedByte();
      this.yOffset = worldMapBuffer2.readUnsignedByte();
      this.width = worldMapBuffer2.readUnsignedShort();
      this.height = worldMapBuffer2.readUnsignedShort();
      int pixelOrder = worldMapBuffer2.readUnsignedByte();
      long pixelCount = (long)this.width * (long)this.height;
      if (this.width <= 0 || this.height <= 0 || pixelCount > 4194304L) {
         throw new IllegalArgumentException("Invalid indexed sprite dimensions: " + this.width + "x" + this.height);
      }
      int widthOrLength = (int)pixelCount;
      this.pixelIndices = new byte[widthOrLength];
      if (pixelOrder == 0) {
         for (int pixelIndex = 0; pixelIndex < widthOrLength; pixelIndex++) {
            this.pixelIndices[pixelIndex] = worldMapBuffer.getByte();
         }
      } else {
         if (pixelOrder == 1) {
            for (int loopIndex4 = 0; loopIndex4 < this.width; loopIndex4++) {
               for (int loopIndex5 = 0; loopIndex5 < this.height; loopIndex5++) {
                  this.pixelIndices[loopIndex4 + loopIndex5 * this.width] = worldMapBuffer.getByte();
               }
            }
         }
      }
   }
}
