package client;
final class SoundFilter {
   private int[] unity = new int[2];
   private int[][][] phases = new int[2][2][4];
   private static float forwardMinimisedCoefficientMultiplier;
   int[] pairs = new int[2];
   private int[][][] magnitudes = new int[2][2][4];
   static int[][] coefficients = new int[2][8];
   private static float[][] minimisedCoefficients = new float[2][8];
   static int forwardMultiplier;
   final void decode(Buffer buffer, SoundEnvelope soundEnvelope) {
      int pairOrReadUnsignedByte = buffer.readUnsignedByte();
      this.pairs[0] = pairOrReadUnsignedByte >> 4;
      this.pairs[1] = pairOrReadUnsignedByte & 15;
      if (pairOrReadUnsignedByte == 0) {
         this.unity[0] = this.unity[1] = 0;
      } else {
         this.unity[0] = buffer.readUnsignedShort();
         this.unity[1] = buffer.readUnsignedShort();
         pairOrReadUnsignedByte = buffer.readUnsignedByte();

         for (int pairIndex2 = 0; pairIndex2 < 2; pairIndex2++) {
            for (int loopIndex = 0; loopIndex < this.pairs[pairIndex2]; loopIndex++) {
               this.phases[pairIndex2][0][loopIndex] = buffer.readUnsignedShort();
               this.magnitudes[pairIndex2][0][loopIndex] = buffer.readUnsignedShort();
            }
         }

         for (int pairIndex = 0; pairIndex < 2; pairIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < this.pairs[pairIndex]; loopIndex2++) {
               if ((pairOrReadUnsignedByte & 1 << (pairIndex << 2) << loopIndex2) != 0) {
                  this.phases[pairIndex][1][loopIndex2] = buffer.readUnsignedShort();
                  this.magnitudes[pairIndex][1][loopIndex2] = buffer.readUnsignedShort();
               } else {
                  this.phases[pairIndex][1][loopIndex2] = this.phases[pairIndex][0][loopIndex2];
                  this.magnitudes[pairIndex][1][loopIndex2] = this.magnitudes[pairIndex][0][loopIndex2];
               }
            }
         }

         if (pairOrReadUnsignedByte != 0 || this.unity[1] != this.unity[0]) {
            soundEnvelope.decodeSegments(buffer);
            return;
         }
      }
   }
   final int compute(int pairIndex, float interpolantArgument) {
      if (pairIndex == 0) {
         float interpolant = (this.unity[0] + (this.unity[1] - this.unity[0]) * interpolantArgument) * 0.0030517578F;
         forwardMultiplier = (int)((forwardMinimisedCoefficientMultiplier = (float)Math.pow(0.1, interpolant / 20.0F)) * 65536.0F);
      }

      if (this.pairs[pairIndex] == 0) {
         return 0;
      }

      float localInterpolateMagnitude = this.interpolateMagnitude(pairIndex, 0, interpolantArgument);
      minimisedCoefficients[pairIndex][0] = localInterpolateMagnitude * -2.0F * (float)Math.cos(this.interpolatePhase(pairIndex, 0, interpolantArgument));
      minimisedCoefficients[pairIndex][1] = localInterpolateMagnitude * localInterpolateMagnitude;

      for (int loopIndex = 1; loopIndex < this.pairs[pairIndex]; loopIndex++) {
         float interpolant2 = (localInterpolateMagnitude = this.interpolateMagnitude(pairIndex, loopIndex, interpolantArgument)) * -2.0F * (float)Math.cos(this.interpolatePhase(pairIndex, loopIndex, interpolantArgument));
         localInterpolateMagnitude *= localInterpolateMagnitude;
         minimisedCoefficients[pairIndex][(loopIndex << 1) + 1] = minimisedCoefficients[pairIndex][(loopIndex << 1) - 1] * localInterpolateMagnitude;
         minimisedCoefficients[pairIndex][loopIndex << 1] = minimisedCoefficients[pairIndex][(loopIndex << 1) - 1] * interpolant2 + minimisedCoefficients[pairIndex][(loopIndex << 1) - 2] * localInterpolateMagnitude;

         for (int loopIndex2 = (loopIndex << 1) - 1; loopIndex2 >= 2; loopIndex2--) {
            minimisedCoefficients[pairIndex][loopIndex2] = minimisedCoefficients[pairIndex][loopIndex2] + (minimisedCoefficients[pairIndex][loopIndex2 - 1] * interpolant2 + minimisedCoefficients[pairIndex][loopIndex2 - 2] * localInterpolateMagnitude);
         }

         minimisedCoefficients[pairIndex][1] = minimisedCoefficients[pairIndex][1] + (minimisedCoefficients[pairIndex][0] * interpolant2 + localInterpolateMagnitude);
         minimisedCoefficients[pairIndex][0] = minimisedCoefficients[pairIndex][0] + interpolant2;
      }

      if (pairIndex == 0) {
         for (int loopIndex3 = 0; loopIndex3 < this.pairs[0] << 1; loopIndex3++) {
            minimisedCoefficients[0][loopIndex3] = minimisedCoefficients[0][loopIndex3] * forwardMinimisedCoefficientMultiplier;
         }
      }

      for (int loopIndex4 = 0; loopIndex4 < this.pairs[pairIndex] << 1; loopIndex4++) {
         coefficients[pairIndex][loopIndex4] = (int)(minimisedCoefficients[pairIndex][loopIndex4] * 65536.0F);
      }

      return this.pairs[pairIndex] << 1;
   }
   private final float interpolateMagnitude(int magnitudeIndex, int newIndex, float interpolantArgument) {
      float interpolant = (this.magnitudes[magnitudeIndex][0][newIndex] + interpolantArgument * (this.magnitudes[magnitudeIndex][1][newIndex] - this.magnitudes[magnitudeIndex][0][newIndex])) * 0.0015258789F;
      return 1.0F - (float)Math.pow(10.0, -interpolant / 20.0F);
   }
   private final float interpolatePhase(int phaseIndex, int newIndex, float interpolantArgument) {
      float interpolant = (this.phases[phaseIndex][0][newIndex] + interpolantArgument * (this.phases[phaseIndex][1][newIndex] - this.phases[phaseIndex][0][newIndex])) * 1.2207031E-4F;
      return (float)Math.pow(2.0, interpolant) * 32.703197F * (float) Math.PI / 11025.0F;
   }
}
