package client;

import java.math.BigInteger;
public final class Buffer extends CacheableNode {
   private static final BigInteger rsaModulus = new BigInteger(
      "99230230517506100343270390684629394795913620229432543723785481725005189723892315646743300425699689384683734468617772678896581998104545267673279110607909977634789859432192334898265883447571272505528888376440271910447965214104345590932205834335509109763618697905660605290589366657894976382397138346698399253337"
   );
   private static final BigInteger rsaExponent = new BigInteger("65537");
   public byte[] buffer;
   public int currentPosition;
   public int bitPosition;
   private static final int[] bitMasks = new int[]{
      0,
      1,
      3,
      7,
      15,
      31,
      63,
      127,
      255,
      511,
      1023,
      2047,
      4095,
      8191,
      16383,
      32767,
      65535,
      131071,
      262143,
      524287,
      1048575,
      2097151,
      4194303,
      8388607,
      16777215,
      33554431,
      67108863,
      134217727,
      268435455,
      536870911,
      1073741823,
      Integer.MAX_VALUE,
      -1
   };
   public IsaacCipher isaacCipher;
   private static int pooledBufferCount;
   private static final NodeDeque bufferPool = new NodeDeque();
   public static Buffer acquire() {
      synchronized (bufferPool) {
         Buffer buffer = null;
         if (pooledBufferCount > 0) {
            pooledBufferCount--;
            buffer = (Buffer)bufferPool.removeFirst();
         }

         if (buffer != null) {
            buffer.currentPosition = 0;
            return buffer;
         }
      }

      Buffer buffer2;
      (buffer2 = new Buffer()).currentPosition = 0;
      buffer2.buffer = new byte[5000];
      return buffer2;
   }

   private Buffer() {
   }

   public Buffer(byte[] newBuffer) {
      this.buffer = newBuffer;
      this.currentPosition = 0;
   }
   public final void writeOpcode(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(scalarArgument + this.isaacCipher.nextInt());
   }
   public final void writeByte(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)scalarArgument;
   }
   public final void writeShort(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 8);
      this.buffer[this.currentPosition++] = (byte)scalarArgument;
   }
   public final void writeMedium(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 16);
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 8);
      this.buffer[this.currentPosition++] = (byte)scalarArgument;
   }
   public final void writeInt(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 24);
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 16);
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 8);
      this.buffer[this.currentPosition++] = (byte)scalarArgument;
   }
   public final int readUnsignedMediumLegacy() {
      this.currentPosition += 3;
      return ((this.buffer[this.currentPosition - 3] & 0xFF) << 16) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] & 0xFF);
   }
   public final int readAdjustedUnsignedShort() {
      this.currentPosition += 2;
      int scalar;
      if ((scalar = ((this.buffer[this.currentPosition - 2] & 255) << 8) + (this.buffer[this.currentPosition - 1] & 255)) > 60000) {
         scalar += -65535;
      }

      return scalar;
   }
   public final void writeLong(long encodedName) {
      try {
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 56);
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 48);
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 40);
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 32);
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 24);
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 16);
         this.buffer[this.currentPosition++] = (byte)(encodedName >> 8);
         this.buffer[this.currentPosition++] = (byte)encodedName;
      } catch (RuntimeException exception) {
         SignLink.reporterror("14395, 5, " + encodedName + ", " + exception.toString());
         throw new RuntimeException();
      }
   }

   public final void writeString(String text) {
      System.arraycopy(text.getBytes(), 0, this.buffer, this.currentPosition, text.length());
      this.currentPosition = this.currentPosition + text.length();
      this.buffer[this.currentPosition++] = 10;
   }
   public final void writeBytes(byte[] newBuffer, int scalarArgument, int scalarArgument2) {
      for (int loopIndex = 0; loopIndex < scalarArgument + 0; loopIndex++) {
         this.buffer[this.currentPosition++] = newBuffer[loopIndex];
      }
   }
   public final void writeLengthByte(int scalarArgument) {
      this.buffer[this.currentPosition - scalarArgument - 1] = (byte)scalarArgument;
   }

   public final int readUnsignedByte() {
      return this.buffer[this.currentPosition++] & 0xFF;
   }

   public final void writeUnsignedByte(int hoveredMenuActionIndex) {
      this.buffer[this.currentPosition++] = (byte)hoveredMenuActionIndex;
   }
   public final byte readByte() {
      return this.buffer[this.currentPosition++];
   }
   public final int readUnsignedShort() {
      this.currentPosition += 2;
      return ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] & 0xFF);
   }
   public final int readShort() {
      this.currentPosition += 2;
      int scalar;
      if ((scalar = ((this.buffer[this.currentPosition - 2] & 255) << 8) + (this.buffer[this.currentPosition - 1] & 255)) > 32767) {
         scalar -= 65536;
      }

      return scalar;
   }
   public final int readUnsignedMedium() {
      this.currentPosition += 3;
      return ((this.buffer[this.currentPosition - 3] & 0xFF) << 16) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] & 0xFF);
   }
   public final int readInt() {
      this.currentPosition += 4;
      return ((this.buffer[this.currentPosition - 4] & 0xFF) << 24)
         + ((this.buffer[this.currentPosition - 3] & 0xFF) << 16)
         + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8)
         + (this.buffer[this.currentPosition - 1] & 0xFF);
   }
   public final long readLong() {
      long decodedInt = this.readInt() & 4294967295L;
      long readInt2 = this.readInt() & 4294967295L;
      return (decodedInt << 32) + readInt2;
   }

   public final String readString() {
      int currentPosition = this.currentPosition;

      while (this.buffer[this.currentPosition++] != 10) {
      }

      return new String(this.buffer, currentPosition, this.currentPosition - currentPosition - 1);
   }

   public final byte[] readBytes() {
      int currentPosition = this.currentPosition;

      while (this.buffer[this.currentPosition++] != 10) {
      }

      byte[] byteBuffer = new byte[this.currentPosition - currentPosition - 1];
      System.arraycopy(this.buffer, currentPosition, byteBuffer, currentPosition - currentPosition, this.currentPosition - 1 - currentPosition);
      return byteBuffer;
   }
   public final void readBytes(int scalarArgument, int scalarArgument2, byte[] byteBufferArgument) {
      for (int loopIndex = 0; loopIndex < scalarArgument + 0; loopIndex++) {
         byteBufferArgument[loopIndex] = this.buffer[this.currentPosition++];
      }
   }
   public final void startBitAccess() {
      this.bitPosition = this.currentPosition << 3;
   }
   public final int readBits(int bitMaskIndex) {
      int bufferIndex = this.bitPosition >> 3;
      int bitMaskIndex2 = 8 - (this.bitPosition & 7);
      int scalar = 0;

      for (this.bitPosition += bitMaskIndex; bitMaskIndex > bitMaskIndex2; bitMaskIndex2 = 8) {
         scalar += (this.buffer[bufferIndex++] & bitMasks[bitMaskIndex2]) << bitMaskIndex - bitMaskIndex2;
         bitMaskIndex -= bitMaskIndex2;
      }

      if (bitMaskIndex == bitMaskIndex2) {
         scalar += this.buffer[bufferIndex] & bitMasks[bitMaskIndex2];
      } else {
         scalar += this.buffer[bufferIndex] >> bitMaskIndex2 - bitMaskIndex & bitMasks[bitMaskIndex];
      }

      return scalar;
   }
   public final void finishBitAccess() {
      this.currentPosition = (this.bitPosition + 7) / 8;
   }
   final int readUnsignedMediumAlternative() {
      this.currentPosition += 3;
      return (0xFF & this.buffer[this.currentPosition - 3] << 16) + (0xFF & this.buffer[this.currentPosition - 2] << 8) + (0xFF & this.buffer[this.currentPosition - 1]);
   }
   public final int readSignedSmart() {
      return (this.buffer[this.currentPosition] & 0xFF) < 128 ? this.readUnsignedByte() - 64 : this.readUnsignedShort() - 49152;
   }
   public final int readUnsignedSmart() {
      return (this.buffer[this.currentPosition] & 0xFF) < 128 ? this.readUnsignedByte() : this.readUnsignedShort() - 32768;
   }
   public final void encryptRsa() {
      int currentPositionOrLength = this.currentPosition;
      this.currentPosition = 0;
      byte[] byteBuffer = new byte[currentPositionOrLength];
      this.readBytes(currentPositionOrLength, 0, byteBuffer);
      byte[] bytes = new BigInteger(byteBuffer).modPow(rsaExponent, rsaModulus).toByteArray();
      this.currentPosition = 0;
      this.writeByte(bytes.length);
      this.writeBytes(bytes, bytes.length, 0);
   }
   public final void writeByteNegated(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(-scalarArgument);
   }
   public final void writeByteSubtracted(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(128 - scalarArgument);
   }
   public final int readUnsignedByteAdded() {
      return this.buffer[this.currentPosition++] - 128 & 0xFF;
   }
   public final int readUnsignedByteNegated() {
      return -this.buffer[this.currentPosition++] & 0xFF;
   }
   public final int readUnsignedByteSubtracted() {
      return 128 - this.buffer[this.currentPosition++] & 0xFF;
   }
   public final byte readByteNegated() {
      return (byte)(-this.buffer[this.currentPosition++]);
   }
   public final void writeShortLittleEndian(int baseY) {
      this.buffer[this.currentPosition++] = (byte)baseY;
      this.buffer[this.currentPosition++] = (byte)(baseY >> 8);
   }
   public final void writeShortAdded(int scalarArgument) {
      this.buffer[this.currentPosition++] = (byte)(scalarArgument >> 8);
      this.buffer[this.currentPosition++] = (byte)(scalarArgument + 128);
   }
   public final void writeShortLittleEndianAdded(int baseX) {
      this.buffer[this.currentPosition++] = (byte)(baseX + 128);
      this.buffer[this.currentPosition++] = (byte)(baseX >> 8);
   }
   public final int readUnsignedShortLittleEndian() {
      this.currentPosition += 2;
      return ((this.buffer[this.currentPosition - 1] & 0xFF) << 8) + (this.buffer[this.currentPosition - 2] & 0xFF);
   }
   public final int readUnsignedShortAdded() {
      this.currentPosition += 2;
      return ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] - 128 & 0xFF);
   }
   public final int readUnsignedShortLittleEndianAdded() {
      this.currentPosition += 2;
      return ((this.buffer[this.currentPosition - 1] & 0xFF) << 8) + (this.buffer[this.currentPosition - 2] - 128 & 0xFF);
   }
   public final int readShortLittleEndian() {
      this.currentPosition += 2;
      int scalar;
      if ((scalar = ((this.buffer[this.currentPosition - 1] & 255) << 8) + (this.buffer[this.currentPosition - 2] & 255)) > 32767) {
         scalar -= 65536;
      }

      return scalar;
   }
   public final int readShortLittleEndianAdded() {
      this.currentPosition += 2;
      int scalar;
      if ((scalar = ((this.buffer[this.currentPosition - 1] & 255) << 8) + (this.buffer[this.currentPosition - 2] - 128 & 0xFF)) > 32767) {
         scalar -= 65536;
      }

      return scalar;
   }
   public final int readIntMiddleEndian() {
      this.currentPosition += 4;
      return ((this.buffer[this.currentPosition - 2] & 0xFF) << 24)
         + ((this.buffer[this.currentPosition - 1] & 0xFF) << 16)
         + ((this.buffer[this.currentPosition - 4] & 0xFF) << 8)
         + (this.buffer[this.currentPosition - 3] & 0xFF);
   }
   public final int readIntInverseMiddleEndian() {
      this.currentPosition += 4;
      return ((this.buffer[this.currentPosition - 3] & 0xFF) << 24)
         + ((this.buffer[this.currentPosition - 4] & 0xFF) << 16)
         + ((this.buffer[this.currentPosition - 1] & 0xFF) << 8)
         + (this.buffer[this.currentPosition - 2] & 0xFF);
   }
   public final void writeBytesReversedAdded(int scalarArgument, byte[] buffer, int currentPosition) {
      for (int bufferIndex = currentPosition + 0 - 1; bufferIndex >= 0; bufferIndex--) {
         this.buffer[this.currentPosition++] = (byte)(buffer[bufferIndex] + 128);
      }
   }
}
