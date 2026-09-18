package client;
final class AudioResampler {
   private int[][] filterTable;
   private int inputRate;
   private int outputRate;
   final int scaleRate(int sampleRate) {
      if (this.filterTable != null) {
         sampleRate = sampleRate * this.outputRate / this.inputRate;
      }

      return sampleRate;
   }
   final byte[] resample(byte[] samples) {
      if (this.filterTable != null) {
         int localLength = samples.length * this.outputRate / this.inputRate + 14;
         int scalar = 0;
         int[] values = new int[localLength];
         int filterTableIndex = 0;

         for (int sampleIndex = 0; samples.length > sampleIndex; sampleIndex++) {
            byte sample = samples[sampleIndex];
            int[] filterTableEntry = this.filterTable[filterTableIndex];

            for (int loopIndex = 0; loopIndex < 14; loopIndex++) {
               values[loopIndex + scalar] = values[loopIndex + scalar] + sample * filterTableEntry[loopIndex];
            }

            int scalar2;
            int scalar3 = (scalar2 = filterTableIndex + this.outputRate) / this.inputRate;
            filterTableIndex = scalar2 - scalar3 * this.inputRate;
            scalar += scalar3;
         }

         samples = new byte[localLength];

         for (int localIndex = 0; localLength > localIndex; localIndex++) {
            int scalar4;
            if ((scalar4 = values[localIndex] + 32768 >> 16) >= -128) {
               if (scalar4 <= 127) {
                  samples[localIndex] = (byte)scalar4;
               } else {
                  samples[localIndex] = 127;
               }
            } else {
               samples[localIndex] = -128;
            }
         }
      }

      return samples;
   }
   final int scalePosition(int start) {
      if (this.filterTable != null) {
         start = 7 + this.outputRate * start / this.inputRate;
      }

      return start;
   }

   AudioResampler(int sourceInputRate, int newOutputRate) {
      sourceInputRate = Client.greatestCommonDivisor(newOutputRate, 22050);
      newOutputRate /= sourceInputRate;
      sourceInputRate = 22050 / sourceInputRate;
      this.inputRate = sourceInputRate;
      this.outputRate = newOutputRate;
      if (newOutputRate != sourceInputRate) {
         this.filterTable = new int[sourceInputRate][14];

         for (int filterTableIndex = 0; sourceInputRate > filterTableIndex; filterTableIndex++) {
            int[] filterTableEntry = this.filterTable[filterTableIndex];
            int floorResult;
            double calculation;
            if ((floorResult = (int)Math.floor((calculation = (double)filterTableIndex / sourceInputRate + 6.0) + -7.0 + 1.0)) < 0) {
               floorResult = 0;
            }

            double calculation2 = (double)newOutputRate / sourceInputRate;
            int ceilResult;
            if ((ceilResult = (int)Math.ceil(calculation + 7.0)) > 14) {
               ceilResult = 14;
            }

            while (ceilResult > floorResult) {
               double calculation3 = (-calculation + floorResult) * Math.PI;
               double calculation4 = calculation2;
               if (calculation3 < -1.0E-4 || calculation3 > 1.0E-4) {
                  calculation4 = calculation2 * (Math.sin(calculation3) / calculation3);
               }

               calculation4 *= Math.cos((-calculation + floorResult) * (Math.PI / 14)) * 0.46 + 0.54;
               filterTableEntry[floorResult] = (int)Math.floor(calculation4 * 65536.0 + 0.5);
               floorResult++;
            }
         }
      }
   }
}
