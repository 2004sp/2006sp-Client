package client;
final class Archive {
   private final byte[] data;
   private final int fileCount;
   private final int[] fileNameHashes;
   private final int[] uncompressedSizes;
   private final int[] compressedSizes;
   private final int[] fileOffsets;
   private final boolean wholeArchiveDecompressed;

   public Archive(byte[] newData) {
      Buffer buffer;
      int length = (buffer = new Buffer(newData)).readUnsignedMedium();
      int decodedUnsignedMedium;
      if ((decodedUnsignedMedium = buffer.readUnsignedMedium()) != length) {
         byte[] byteBuffer = new byte[length];
         int decompressed = BZip2Decompressor.decompress(byteBuffer, length, newData, decodedUnsignedMedium, 6);
         if (decompressed != length) {
            throw new IllegalStateException("Archive BZip2 size mismatch: decoded=" + decompressed + " expected=" + length + " packed=" + decodedUnsignedMedium);
         }
         this.data = byteBuffer;
         buffer = new Buffer(this.data);
         this.wholeArchiveDecompressed = true;
      } else {
         this.data = newData;
         this.wholeArchiveDecompressed = false;
      }

      this.fileCount = buffer.readUnsignedShort();
      this.fileNameHashes = new int[this.fileCount];
      this.uncompressedSizes = new int[this.fileCount];
      this.compressedSizes = new int[this.fileCount];
      this.fileOffsets = new int[this.fileCount];
      int fileOffsetOrCurrentPosition = buffer.currentPosition + this.fileCount * 10;

      for (int fileNameHashIndex = 0; fileNameHashIndex < this.fileCount; fileNameHashIndex++) {
         this.fileNameHashes[fileNameHashIndex] = buffer.readInt();
         this.uncompressedSizes[fileNameHashIndex] = buffer.readUnsignedMedium();
         this.compressedSizes[fileNameHashIndex] = buffer.readUnsignedMedium();
         this.fileOffsets[fileNameHashIndex] = fileOffsetOrCurrentPosition;
         fileOffsetOrCurrentPosition += this.compressedSizes[fileNameHashIndex];
      }
   }
   public final byte[] getFile(String text) {
      int scalar = 0;
      text = text.toUpperCase();

      for (int loopIndex = 0; loopIndex < text.length(); loopIndex++) {
         scalar = scalar * 61 + text.charAt(loopIndex) - 32;
      }

      for (int fileNameHashIndex = 0; fileNameHashIndex < this.fileCount; fileNameHashIndex++) {
         if (this.fileNameHashes[fileNameHashIndex] == scalar) {
            byte[] byteBuffer = new byte[this.uncompressedSizes[fileNameHashIndex]];
            if (!this.wholeArchiveDecompressed) {
               int decompressed = BZip2Decompressor.decompress(byteBuffer, this.uncompressedSizes[fileNameHashIndex], this.data, this.compressedSizes[fileNameHashIndex], this.fileOffsets[fileNameHashIndex]);
               if (decompressed != this.uncompressedSizes[fileNameHashIndex]) {
                  throw new IllegalStateException(
                     "Archive entry BZip2 size mismatch for " + text + ": decoded=" + decompressed
                        + " expected=" + this.uncompressedSizes[fileNameHashIndex] + " packed=" + this.compressedSizes[fileNameHashIndex]
                  );
               }
            } else {
               System.arraycopy(this.data, this.fileOffsets[fileNameHashIndex], byteBuffer, 0, this.uncompressedSizes[fileNameHashIndex]);
            }

            return byteBuffer;
         }
      }

      return null;
   }
}
