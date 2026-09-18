package client;

import java.io.InputStream;
public final class AudioInputStream extends InputStream {
   private boolean failed;
   private int[] muLawExponentTable = new int[]{
      0,
      0,
      1,
      1,
      2,
      2,
      2,
      2,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7
   };
   private byte[] muLawTable = new byte[65536];
   private int[] mixBuffer = new int[256];

   @Override
   public final synchronized int read(byte[] newMuLawTable, int scalarArgument, int length) {
      try {
         if (this.failed) {
            return -1;
         }

         if (length > 256) {
            this.read(newMuLawTable, scalarArgument, 256);
            this.read(newMuLawTable, scalarArgument + 256, length - 256);
            return length;
         }

         Client.mixPcmSamples(this.mixBuffer, length);

         for (int mixBufferIndex = 0; mixBufferIndex < length; mixBufferIndex++) {
            int localMixBuffer;
            if (((localMixBuffer = this.mixBuffer[mixBufferIndex]) + 8388608 & 0xFF000000) != 0) {
               this.mixBuffer[mixBufferIndex] = 8388607 ^ localMixBuffer >> 31;
            }
         }

         int sourceLength = length;
         int scalar = scalarArgument;
         byte[] byteBuffer = newMuLawTable;
         int[] mixBuffer2 = this.mixBuffer;
         newMuLawTable = this.muLawTable;

         for (int loopIndex = 0; loopIndex < sourceLength; loopIndex++) {
            byteBuffer[scalar++] = newMuLawTable[(mixBuffer2[loopIndex] >> 8) + 32768];
         }

         return length;
      } catch (Exception exception) {
         this.failed = true;
         return -1;
      }
   }

   @Override
   public final int read() {
      byte[] byteBuffer = new byte[1];
      this.read(byteBuffer, 0, 1);
      return byteBuffer[0];
   }

   AudioInputStream() {
      for (int loopIndex = -32768; loopIndex < 32768; loopIndex++) {
         int muLawTableIndex = loopIndex + 32768;
         int scalar = loopIndex;
         AudioInputStream audioInputStream = this;
         int scalar2;
         if ((scalar2 = scalar >> 8 & 128) != 0) {
            scalar = -scalar;
         }

         if (scalar > 32635) {
            scalar = 32635;
         }

         scalar += 132;
         int muLawExponentTableEntry = audioInputStream.muLawExponentTable[scalar >> 7 & 0xFF];
         scalar = scalar >> muLawExponentTableEntry + 3 & 15;
         this.muLawTable[muLawTableIndex] = (byte)(~(scalar2 | muLawExponentTableEntry << 4 | scalar));
      }
   }
}
