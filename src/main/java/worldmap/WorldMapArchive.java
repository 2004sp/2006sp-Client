package worldmap;
public final class WorldMapArchive {
   private byte[] data;
   private int fileCount;
   private int[] fileNameHashes;
   private int[] uncompressedSizes;
   private int[] compressedSizes;
   private int[] fileOffsets;
   private boolean wholeArchiveDecompressed;

   public WorldMapArchive(byte[] byteBufferArgument) {
      this.decodeArchive(byteBufferArgument);
   }
   private void decodeArchive(byte[] newData) {
      WorldMapBuffer worldMapBuffer;
      int length = (worldMapBuffer = new WorldMapBuffer(newData)).readUnsignedMedium();
      int compressedLength;
      if ((compressedLength = worldMapBuffer.readUnsignedMedium()) != length) {
         byte[] byteBuffer;
         WorldMapBZip2Decompressor.decompress(byteBuffer = new byte[length], length, newData, compressedLength, 6);
         this.data = byteBuffer;
         worldMapBuffer = new WorldMapBuffer(this.data);
         this.wholeArchiveDecompressed = true;
      } else {
         this.data = newData;
         this.wholeArchiveDecompressed = false;
      }

      this.fileCount = worldMapBuffer.readUnsignedShort();
      this.fileNameHashes = new int[this.fileCount];
      this.uncompressedSizes = new int[this.fileCount];
      this.compressedSizes = new int[this.fileCount];
      this.fileOffsets = new int[this.fileCount];
      int fileOffsetOrCurrentPosition = worldMapBuffer.currentPosition + this.fileCount * 10;

      for (int fileNameHashIndex = 0; fileNameHashIndex < this.fileCount; fileNameHashIndex++) {
         this.fileNameHashes[fileNameHashIndex] = worldMapBuffer.getInt();
         this.uncompressedSizes[fileNameHashIndex] = worldMapBuffer.readUnsignedMedium();
         this.compressedSizes[fileNameHashIndex] = worldMapBuffer.readUnsignedMedium();
         this.fileOffsets[fileNameHashIndex] = fileOffsetOrCurrentPosition;
         fileOffsetOrCurrentPosition += this.compressedSizes[fileNameHashIndex];
      }
   }
   public final byte[] getFile(String text, byte[] newData) {
      int scalar = 0;
      text = text.toUpperCase();

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         scalar = scalar * 61 + text.charAt(loopIndex) - 32;
      }

      for (int fileNameHashIndex = 0; fileNameHashIndex < this.fileCount; fileNameHashIndex++) {
         if (this.fileNameHashes[fileNameHashIndex] == scalar) {
            newData = new byte[this.uncompressedSizes[fileNameHashIndex]];
            if (!this.wholeArchiveDecompressed) {
               WorldMapBZip2Decompressor.decompress(newData, this.uncompressedSizes[fileNameHashIndex], this.data, this.compressedSizes[fileNameHashIndex], this.fileOffsets[fileNameHashIndex]);
            } else {
               for (int newDataIndex = 0; newDataIndex < this.uncompressedSizes[fileNameHashIndex]; newDataIndex++) {
                  newData[newDataIndex] = this.data[this.fileOffsets[fileNameHashIndex] + newDataIndex];
               }
            }

            return newData;
         }
      }

      return null;
   }
}
