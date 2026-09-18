package client;

import java.util.Random;
final class SoundTone {
   int offset = 0;
   private SoundEnvelope pitchModifier;
   private SoundEnvelope pitchModifierAmplitude;
   private SoundEnvelope attack;
   private int[] oscillatorVolume = new int[5];
   private SoundEnvelope volumeMultiplierAmplitude;
   private int delayTime = 0;
   private int[] oscillatorPitch = new int[5];
   int duration = 500;
   private SoundEnvelope volume;
   private SoundFilter filter;
   private SoundEnvelope volumeMultiplier;
   private static int[] sine;
   private SoundEnvelope release;
   private int[] oscillatorDelay = new int[5];
   private SoundEnvelope pitch;
   private static int[] noise = new int[32768];
   private SoundEnvelope filterEnvelope;
   private int delayDecay = 100;
   private static int[] sampleBuffer;
   private static int[] oscillatorPitchStep;
   private static int[] oscillatorVolumeStep;
   private static int[] oscillatorPhases;
   private static int[] oscillatorDelays;
   private static int[] oscillatorPitchBaseSteps;

   static {
      Random random = new Random(0L);

      for (int sampleIndex = 0; sampleIndex < 32768; sampleIndex++) {
         noise[sampleIndex] = (random.nextInt() & 2) - 1;
      }

      sine = new int[32768];

      for (int phaseIndex = 0; phaseIndex < 32768; phaseIndex++) {
         sine[phaseIndex] = (int)(Math.sin(phaseIndex / 5215.1903) * 16384.0);
      }

      sampleBuffer = new int[220500];
      oscillatorVolumeStep = new int[5];
      oscillatorPitchStep = new int[5];
      oscillatorPhases = new int[5];
      oscillatorPitchBaseSteps = new int[5];
      oscillatorDelays = new int[5];
   }
   final void decode(Buffer buffer) {
      this.pitch = new SoundEnvelope();
      this.pitch.decode(buffer);
      this.volume = new SoundEnvelope();
      this.volume.decode(buffer);
      if (buffer.readUnsignedByte() != 0) {
         buffer.currentPosition--;
         this.pitchModifier = new SoundEnvelope();
         this.pitchModifier.decode(buffer);
         this.pitchModifierAmplitude = new SoundEnvelope();
         this.pitchModifierAmplitude.decode(buffer);
      }

      if (buffer.readUnsignedByte() != 0) {
         buffer.currentPosition--;
         this.volumeMultiplier = new SoundEnvelope();
         this.volumeMultiplier.decode(buffer);
         this.volumeMultiplierAmplitude = new SoundEnvelope();
         this.volumeMultiplierAmplitude.decode(buffer);
      }

      if (buffer.readUnsignedByte() != 0) {
         buffer.currentPosition--;
         this.release = new SoundEnvelope();
         this.release.decode(buffer);
         this.attack = new SoundEnvelope();
         this.attack.decode(buffer);
      }

      int oscillatorVolumeOrReadUnsignedSmart;
      for (int oscillatorVolumeIndex = 0; oscillatorVolumeIndex < 10 && (oscillatorVolumeOrReadUnsignedSmart = buffer.readUnsignedSmart()) != 0; oscillatorVolumeIndex++) {
         this.oscillatorVolume[oscillatorVolumeIndex] = oscillatorVolumeOrReadUnsignedSmart;
         this.oscillatorPitch[oscillatorVolumeIndex] = buffer.readSignedSmart();
         this.oscillatorDelay[oscillatorVolumeIndex] = buffer.readUnsignedSmart();
      }

      this.delayTime = buffer.readUnsignedSmart();
      this.delayDecay = buffer.readUnsignedSmart();
      this.duration = buffer.readUnsignedShort();
      this.offset = buffer.readUnsignedShort();
      this.filter = new SoundFilter();
      this.filterEnvelope = new SoundEnvelope();
      this.filter.decode(buffer, this.filterEnvelope);
   }
   private static int evaluateWave(int oscillatorPhase, int scalarArgument, int form) {
      if (form == 1) {
         return (oscillatorPhase & 32767) < 16384 ? scalarArgument : -scalarArgument;
      } else if (form == 2) {
         return sine[oscillatorPhase & 32767] * scalarArgument >> 14;
      } else if (form == 3) {
         return ((oscillatorPhase & 32767) * scalarArgument >> 14) - scalarArgument;
      } else {
         return form == 4 ? noise[oscillatorPhase / 2607 & 32767] * scalarArgument : 0;
      }
   }
   final int[] synthesize(int loopIndex, int sampleBufferOrRelease) {
      for (int sampleBufferIndex = 0; sampleBufferIndex < loopIndex; sampleBufferIndex++) {
         sampleBuffer[sampleBufferIndex] = 0;
      }

      if (sampleBufferOrRelease < 10) {
         return sampleBuffer;
      }

      double position = loopIndex / (sampleBufferOrRelease + 0.0);
      this.pitch.reset();
      this.volume.reset();
      sampleBufferOrRelease = 0;
      int scalar = 0;
      int oscillatorPhase = 0;
      if (this.pitchModifier != null) {
         this.pitchModifier.reset();
         this.pitchModifierAmplitude.reset();
         sampleBufferOrRelease = (int)((this.pitchModifier.end - this.pitchModifier.start) * 32.768 / position);
         scalar = (int)(this.pitchModifier.start * 32.768 / position);
      }

      int scalar2 = 0;
      int scalar3 = 0;
      int oscillatorPhase2 = 0;
      if (this.volumeMultiplier != null) {
         this.volumeMultiplier.reset();
         this.volumeMultiplierAmplitude.reset();
         scalar2 = (int)((this.volumeMultiplier.end - this.volumeMultiplier.start) * 32.768 / position);
         scalar3 = (int)(this.volumeMultiplier.start * 32.768 / position);
      }

      for (int oscillatorVolumeIndex = 0; oscillatorVolumeIndex < 5; oscillatorVolumeIndex++) {
         if (this.oscillatorVolume[oscillatorVolumeIndex] != 0) {
            oscillatorPhases[oscillatorVolumeIndex] = 0;
            oscillatorDelays[oscillatorVolumeIndex] = (int)(this.oscillatorDelay[oscillatorVolumeIndex] * position);
            oscillatorVolumeStep[oscillatorVolumeIndex] = (this.oscillatorVolume[oscillatorVolumeIndex] << 14) / 100;
            oscillatorPitchStep[oscillatorVolumeIndex] = (int)(
               (this.pitch.end - this.pitch.start) * 32.768 * Math.pow(1.0057929410678534, this.oscillatorPitch[oscillatorVolumeIndex]) / position
            );
            oscillatorPitchBaseSteps[oscillatorVolumeIndex] = (int)(this.pitch.start * 32.768 / position);
         }
      }

      for (int loopIndex2 = 0; loopIndex2 < loopIndex; loopIndex2++) {
         int localPitch = this.pitch.step(loopIndex);
         int localVolume = this.volume.step(loopIndex);
         if (this.pitchModifier != null) {
            int localPitchModifier = this.pitchModifier.step(loopIndex);
            int localPitchModifierAmplitude = this.pitchModifierAmplitude.step(loopIndex);
            localPitch += evaluateWave(oscillatorPhase, localPitchModifierAmplitude, this.pitchModifier.form) >> 1;
            oscillatorPhase += (localPitchModifier * sampleBufferOrRelease >> 16) + scalar;
         }

         if (this.volumeMultiplier != null) {
            int localVolumeMultiplier = this.volumeMultiplier.step(loopIndex);
            int localVolumeMultiplierAmplitude = this.volumeMultiplierAmplitude.step(loopIndex);
            localVolume = localVolume * ((evaluateWave(oscillatorPhase2, localVolumeMultiplierAmplitude, this.volumeMultiplier.form) >> 1) + 32768) >> 15;
            oscillatorPhase2 += (localVolumeMultiplier * scalar2 >> 16) + scalar3;
         }

         for (int oscillatorVolumeIndex2 = 0; oscillatorVolumeIndex2 < 5; oscillatorVolumeIndex2++) {
            int sampleBufferIndex2;
            if (this.oscillatorVolume[oscillatorVolumeIndex2] != 0 && (sampleBufferIndex2 = loopIndex2 + oscillatorDelays[oscillatorVolumeIndex2]) < loopIndex) {
               sampleBuffer[sampleBufferIndex2] = sampleBuffer[sampleBufferIndex2] + evaluateWave(oscillatorPhases[oscillatorVolumeIndex2], localVolume * oscillatorVolumeStep[oscillatorVolumeIndex2] >> 15, this.pitch.form);
               oscillatorPhases[oscillatorVolumeIndex2] = oscillatorPhases[oscillatorVolumeIndex2] + (localPitch * oscillatorPitchStep[oscillatorVolumeIndex2] >> 16) + oscillatorPitchBaseSteps[oscillatorVolumeIndex2];
            }
         }
      }

      if (this.release != null) {
         this.release.reset();
         this.attack.reset();
         short shortCode = 0;
         boolean flag = true;

         for (int sampleBufferIndex3 = 0; sampleBufferIndex3 < loopIndex; sampleBufferIndex3++) {
            int localRelease = this.release.step(loopIndex);
            int localAttack = this.attack.step(loopIndex);
            if (flag) {
               sampleBufferOrRelease = this.release.start + ((this.release.end - this.release.start) * localRelease >> 8);
            } else {
               sampleBufferOrRelease = this.release.start + ((this.release.end - this.release.start) * localAttack >> 8);
            }

            shortCode += 256;
            if (shortCode >= sampleBufferOrRelease) {
               shortCode = 0;
               flag = !flag;
            }

            if (flag) {
               sampleBuffer[sampleBufferIndex3] = 0;
            }
         }
      }

      if (this.delayTime > 0 && this.delayDecay > 0) {
         int position2;
         for (int sampleBufferIndex4 = position2 = (int)(this.delayTime * position); sampleBufferIndex4 < loopIndex; sampleBufferIndex4++) {
            sampleBuffer[sampleBufferIndex4] = sampleBuffer[sampleBufferIndex4] + sampleBuffer[sampleBufferIndex4 - position2] * this.delayDecay / 100;
         }
      }

      if (this.filter.pairs[0] > 0 || this.filter.pairs[1] > 0) {
         this.filterEnvelope.reset();
         int localFilterEnvelope = this.filterEnvelope.step(loopIndex + 1);
         int computeResult = this.filter.compute(0, localFilterEnvelope / 65536.0F);
         int localFilter = this.filter.compute(1, localFilterEnvelope / 65536.0F);
         if (loopIndex >= computeResult + localFilter) {
            int sampleBufferIndex5 = 0;
            int sourceLocalFilter = localFilter;
            if (localFilter > loopIndex - computeResult) {
               sourceLocalFilter = loopIndex - computeResult;
            }

            while (sampleBufferIndex5 < sourceLocalFilter) {
               sampleBufferOrRelease = (int)((long)sampleBuffer[sampleBufferIndex5 + computeResult] * SoundFilter.forwardMultiplier >> 16);

               for (int loopIndex3 = 0; loopIndex3 < computeResult; loopIndex3++) {
                  sampleBufferOrRelease += (int)((long)sampleBuffer[sampleBufferIndex5 + computeResult - 1 - loopIndex3] * SoundFilter.coefficients[0][loopIndex3] >> 16);
               }

               for (int loopIndex4 = 0; loopIndex4 < sampleBufferIndex5; loopIndex4++) {
                  sampleBufferOrRelease -= (int)((long)sampleBuffer[sampleBufferIndex5 - 1 - loopIndex4] * SoundFilter.coefficients[1][loopIndex4] >> 16);
               }

               sampleBuffer[sampleBufferIndex5] = sampleBufferOrRelease;
               localFilterEnvelope = this.filterEnvelope.step(loopIndex + 1);
               sampleBufferIndex5++;
            }

            sourceLocalFilter = 128;

            while (true) {
               if (sourceLocalFilter > loopIndex - computeResult) {
                  sourceLocalFilter = loopIndex - computeResult;
               }

               while (sampleBufferIndex5 < sourceLocalFilter) {
                  sampleBufferOrRelease = (int)((long)sampleBuffer[sampleBufferIndex5 + computeResult] * SoundFilter.forwardMultiplier >> 16);

                  for (int loopIndex5 = 0; loopIndex5 < computeResult; loopIndex5++) {
                     sampleBufferOrRelease += (int)((long)sampleBuffer[sampleBufferIndex5 + computeResult - 1 - loopIndex5] * SoundFilter.coefficients[0][loopIndex5] >> 16);
                  }

                  for (int loopIndex6 = 0; loopIndex6 < localFilter; loopIndex6++) {
                     sampleBufferOrRelease -= (int)((long)sampleBuffer[sampleBufferIndex5 - 1 - loopIndex6] * SoundFilter.coefficients[1][loopIndex6] >> 16);
                  }

                  sampleBuffer[sampleBufferIndex5] = sampleBufferOrRelease;
                  localFilterEnvelope = this.filterEnvelope.step(loopIndex + 1);
                  sampleBufferIndex5++;
               }

               if (sampleBufferIndex5 >= loopIndex - computeResult) {
                  while (sampleBufferIndex5 < loopIndex) {
                     sampleBufferOrRelease = 0;

                     for (int loopIndex7 = sampleBufferIndex5 + computeResult - loopIndex; loopIndex7 < computeResult; loopIndex7++) {
                        sampleBufferOrRelease += (int)((long)sampleBuffer[sampleBufferIndex5 + computeResult - 1 - loopIndex7] * SoundFilter.coefficients[0][loopIndex7] >> 16);
                     }

                     for (int loopIndex8 = 0; loopIndex8 < localFilter; loopIndex8++) {
                        sampleBufferOrRelease -= (int)((long)sampleBuffer[sampleBufferIndex5 - 1 - loopIndex8] * SoundFilter.coefficients[1][loopIndex8] >> 16);
                     }

                     sampleBuffer[sampleBufferIndex5] = sampleBufferOrRelease;
                     this.filterEnvelope.step(loopIndex + 1);
                     sampleBufferIndex5++;
                  }
                  break;
               }

               computeResult = this.filter.compute(0, localFilterEnvelope / 65536.0F);
               localFilter = this.filter.compute(1, localFilterEnvelope / 65536.0F);
               sourceLocalFilter += 128;
            }
         }
      }

      for (int sampleBufferIndex6 = 0; sampleBufferIndex6 < loopIndex; sampleBufferIndex6++) {
         if (sampleBuffer[sampleBufferIndex6] < -32768) {
            sampleBuffer[sampleBufferIndex6] = -32768;
         }

         if (sampleBuffer[sampleBufferIndex6] > 32767) {
            sampleBuffer[sampleBufferIndex6] = 32767;
         }
      }

      return sampleBuffer;
   }
}
