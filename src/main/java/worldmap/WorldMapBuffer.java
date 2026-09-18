package worldmap;
public final class WorldMapBuffer extends WorldMapCacheableNode {
   public byte[] buffer;
   public int currentPosition;
   private static int[] crcTable = new int[256];

   static {
      for (int crcIndex = 0; crcIndex < 256; crcIndex++) {
         int crc = crcIndex;

         for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            if ((crc & 1) == 1) {
               crc = crc >>> 1 ^ -306674912;
            } else {
               crc >>>= 1;
            }
         }

         crcTable[crcIndex] = crc;
      }
   }

   public final int getInt() {
      this.currentPosition += 4;
      return ((this.buffer[this.currentPosition - 4] & 0xFF) << 24)
         + ((this.buffer[this.currentPosition - 3] & 0xFF) << 16)
         + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8)
         + (this.buffer[this.currentPosition - 1] & 0xFF);
   }
   public final int readUnsignedByte() {
      return this.buffer[this.currentPosition++] & 0xFF;
   }

   public final byte getByte() {
      return this.buffer[this.currentPosition++];
   }
   public final int readUnsignedShort() {
      this.currentPosition += 2;
      return ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] & 0xFF);
   }
   public final int readUnsignedMedium() {
      this.currentPosition += 3;
      return ((this.buffer[this.currentPosition - 3] & 0xFF) << 16) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] & 0xFF);
   }

   public WorldMapBuffer() {
   }

   public WorldMapBuffer(byte[] newBuffer) {
      this.buffer = newBuffer;
      this.currentPosition = 0;
   }
}
