package client;
final class RawPcmStream extends PcmStream {
   private int loopCount;
   private int rampRemaining;
   private int position;
   private int volume;
   private int loopStart;
   private int loopEnd;
   private int step;
   @Override
   final int getPriority() {
      int localVolume;
      localVolume = ((localVolume = this.volume * 3) ^ localVolume >> 31) + (localVolume >>> 31);
      if (this.loopCount == 0) {
         localVolume -= localVolume * this.position / (((RawSound)this.sound).samples.length << 8);
      } else if (this.loopCount >= 0) {
         localVolume -= localVolume * this.loopStart / ((RawSound)this.sound).samples.length;
      }

      return localVolume > 255 ? 255 : localVolume;
   }
   @Override
   final synchronized int fill(int[] newSamples, int offset, int newLength) {
      if (this.volume == 0) {
         this.skip(newLength);
         return 0;
      }

      RawSound rawSound = (RawSound)this.sound;
      int positionOrLoopStart = this.loopStart << 8;
      int localLoopEnd = this.loopEnd << 8;
      int scalar = rawSound.samples.length << 8;
      int scalar2;
      if ((scalar2 = localLoopEnd - positionOrLoopStart) <= 0) {
         this.loopCount = 0;
      }

      int sourceOffset = offset;
      newLength += offset;
      if (this.loopCount < 0) {
         if (this.step < 0) {
            while (true) {
               sourceOffset = this.mixBackward(newSamples, sourceOffset, positionOrLoopStart, newLength, rawSound.samples[this.loopEnd - 1]);
               if (this.position >= positionOrLoopStart) {
                  return 1;
               }

               this.position = localLoopEnd - 1 - (localLoopEnd - 1 - this.position) % scalar2;
            }
         } else {
            while (true) {
               sourceOffset = this.mixForward(newSamples, sourceOffset, localLoopEnd, newLength, rawSound.samples[this.loopStart]);
               if (this.position < localLoopEnd) {
                  return 1;
               }

               this.position = positionOrLoopStart + (this.position - positionOrLoopStart) % scalar2;
            }
         }
      } else {
         if (this.loopCount > 0) {
            if (this.step < 0) {
               while (true) {
                  sourceOffset = this.mixBackward(newSamples, sourceOffset, positionOrLoopStart, newLength, rawSound.samples[this.loopEnd - 1]);
                  if (this.position >= positionOrLoopStart) {
                     return 1;
                  }

                  if ((offset = (localLoopEnd - 1 - this.position) / scalar2) >= this.loopCount) {
                     this.position = this.position + scalar2 * this.loopCount;
                     this.loopCount = 0;
                     break;
                  }

                  this.position += scalar2 * offset;
                  this.loopCount -= offset;
               }
            } else {
               while (true) {
                  sourceOffset = this.mixForward(newSamples, sourceOffset, localLoopEnd, newLength, rawSound.samples[this.loopStart]);
                  if (this.position < localLoopEnd) {
                     return 1;
                  }

                  if ((offset = (this.position - positionOrLoopStart) / scalar2) >= this.loopCount) {
                     this.position = this.position - scalar2 * this.loopCount;
                     this.loopCount = 0;
                     break;
                  }

                  this.position -= scalar2 * offset;
                  this.loopCount -= offset;
               }
            }
         }

         if (this.step < 0) {
            this.mixBackward(newSamples, sourceOffset, 0, newLength, 0);
            if (this.position < 0) {
               this.position = 0;
               this.unlink();
            }
         } else {
            this.mixForward(newSamples, sourceOffset, scalar, newLength, 0);
            if (this.position >= scalar) {
               this.position = scalar - 1;
               this.unlink();
            }
         }

         return 1;
      }
   }
   final synchronized void setNumLoops(int newLoopCount) {
      this.loopCount = newLoopCount;
   }
   private final int mixBackward(int[] values, int scalarArgument, int scalarArgument2, int scalarArgument3, int sample2) {
      if (this.rampRemaining > 0) {
         int scalar;
         if ((scalar = scalarArgument + this.rampRemaining) > scalarArgument3) {
            scalar = scalarArgument3;
         }

         this.rampRemaining += scalarArgument;
         if (this.step == -256 && (this.position & 0xFF) == 0) {
            RawPcmStream rawPcmStream = this;
            int scalar2 = scalarArgument2;
            int scalar3 = scalar;
            byte byteCode = 0;
            byte byteCode2 = 0;
            int volume = this.volume;
            int loopIndex = scalarArgument;
            int position3 = this.position;
            int[] integerBuffer = values;
            byte[] samples2 = ((RawSound)this.sound).samples;
            position3 >>= 8;
            scalar2 >>= 8;
            volume <<= 8;
            int position5;
            if ((position5 = loopIndex + position3 - (scalar2 - 1)) > scalar3) {
               position5 = scalar3;
            }

            position5 -= 3;

            while (loopIndex < position5) {
               int position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3--] * volume >> byteCode2;
               volume += byteCode;
               position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3--] * volume >> byteCode2;
               volume += byteCode;
               position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3--] * volume >> byteCode2;
               volume += byteCode;
               position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3--] * volume >> byteCode2;
               volume += byteCode;
            }

            for (int loopIndex2 = position5 + 3; loopIndex < loopIndex2; volume += byteCode) {
               int scalar4 = loopIndex++;
               integerBuffer[scalar4] += samples2[position3--] * volume >> byteCode2;
            }

            rawPcmStream.volume = volume >> 8;
            rawPcmStream.position = position3 << 8;
            scalarArgument = loopIndex;
         } else {
            int scalar5 = sample2;
            int step = this.step;
            RawPcmStream rawPcmStream4 = this;
            int scalar6 = scalarArgument2;
            int scalar7 = scalar;
            byte byteCode3 = 0;
            byte byteCode4 = 0;
            int volume2 = this.volume;
            int loopIndex3 = scalarArgument;
            int position = this.position;
            int[] values2 = values;
            byte[] samples = ((RawSound)this.sound).samples;
            int loopIndex4;
            if (step == 0 || (loopIndex4 = loopIndex3 + (scalar6 + 256 - position + step) / step) > scalar7) {
               loopIndex4 = scalar7;
            }

            while (loopIndex3 < loopIndex4) {
               int sampleIndex = position >> 8;
               byte sample = samples[sampleIndex - 1];
               int scalar8 = loopIndex3++;
               values2[scalar8] += ((sample << 8) + (samples[sampleIndex] - sample) * (position & 0xFF)) * volume2 >> byteCode4;
               volume2 += byteCode3;
               position += step;
            }

            if (step == 0 || (loopIndex4 = loopIndex3 + (scalar6 - position + step) / step) > scalar7) {
               loopIndex4 = scalar7;
            }

            int scalar9 = scalar5;

            for (int sourceStep = step; loopIndex3 < loopIndex4; position += sourceStep) {
               int scalar10 = loopIndex3++;
               values2[scalar10] += ((scalar9 << 8) + (samples[position >> 8] - scalar9) * (position & 0xFF)) * volume2 >> byteCode4;
               volume2 += byteCode3;
            }

            rawPcmStream4.volume = volume2;
            rawPcmStream4.position = position;
            scalarArgument = loopIndex3;
         }

         this.rampRemaining -= scalarArgument;
         if (this.rampRemaining != 0) {
            return scalarArgument;
         }

         this.volume = 0;
      }

      if (this.step == -256 && (this.position & 0xFF) == 0) {
         RawPcmStream rawPcmStream2 = this;
         int scalar11 = scalarArgument2;
         int scalar12 = scalarArgument3;
         int volume3 = this.volume;
         int scalar13 = scalarArgument;
         int position4 = this.position;
         int[] values3 = values;
         byte[] samples3 = ((RawSound)this.sound).samples;
         position4 >>= 8;
         scalar11 >>= 8;
         volume3 <<= 8;
         int scalar14;
         if ((scalar14 = scalar13 + position4 - (scalar11 - 1)) > scalar12) {
            scalar14 = scalar12;
         }

         scalar14 -= 3;

         while (scalar13 < scalar14) {
            int position7 = scalar13++;
            values3[position7] += samples3[position4--] * volume3;
            position7 = scalar13++;
            values3[position7] += samples3[position4--] * volume3;
            position7 = scalar13++;
            values3[position7] += samples3[position4--] * volume3;
            position7 = scalar13++;
            values3[position7] += samples3[position4--] * volume3;
         }

         scalar14 += 3;

         while (scalar13 < scalar14) {
            int scalar15 = scalar13++;
            values3[scalar15] += samples3[position4--] * volume3;
         }

         rawPcmStream2.position = position4 << 8;
         return scalar13;
      } else {
         int scalar16 = sample2;
         int step2 = this.step;
         RawPcmStream rawPcmStream3 = this;
         int scalar17 = scalarArgument2;
         int scalar18 = scalarArgument3;
         int volume4 = this.volume;
         int loopIndex5 = scalarArgument;
         int position2 = this.position;
         int[] values4 = values;
         byte[] samples4 = ((RawSound)this.sound).samples;
         int loopIndex6;
         if (step2 == 0 || (loopIndex6 = loopIndex5 + (scalar17 + 256 - position2 + step2) / step2) > scalar18) {
            loopIndex6 = scalar18;
         }

         while (loopIndex5 < loopIndex6) {
            int scalar19 = position2 >> 8;
            byte byteCode5 = samples4[scalar19 - 1];
            int scalar20 = loopIndex5++;
            values4[scalar20] += ((byteCode5 << 8) + (samples4[scalar19] - byteCode5) * (position2 & 0xFF)) * volume4;
            position2 += step2;
         }

         if (step2 == 0 || (loopIndex6 = loopIndex5 + (scalar17 - position2 + step2) / step2) > scalar18) {
            loopIndex6 = scalar18;
         }

         int scalar21 = scalar16;

         for (int position8 = step2; loopIndex5 < loopIndex6; position2 += position8) {
            int scalar22 = loopIndex5++;
            values4[scalar22] += ((scalar21 << 8) + (samples4[position2 >> 8] - scalar21) * (position2 & 0xFF)) * volume4;
         }

         rawPcmStream3.position = position2;
         return loopIndex5;
      }
   }
   static final RawPcmStream create(RawSound rawSound, int scalarArgument, int soundEffectVolume) {
      return rawSound.samples != null && rawSound.samples.length != 0
         ? new RawPcmStream(rawSound, (int)(((long)rawSound.sampleRate << 8) * 100L / (Client.audioSampleRate * 100)), soundEffectVolume)
         : null;
   }
   @Override
   final synchronized void skip(int sampleCount) {
      if (this.rampRemaining > 0) {
         if (sampleCount >= this.rampRemaining) {
            this.volume = 0;
            this.rampRemaining = 0;
         } else {
            this.volume += 0 * sampleCount;
            this.rampRemaining -= sampleCount;
         }
      }

      this.position = this.position + this.step * sampleCount;
      RawSound rawSound = (RawSound)this.sound;
      int positionOrLoopStart = this.loopStart << 8;
      int localLoopEnd = this.loopEnd << 8;
      sampleCount = rawSound.samples.length << 8;
      int scalar;
      if ((scalar = localLoopEnd - positionOrLoopStart) <= 0) {
         this.loopCount = 0;
      }

      if (this.loopCount < 0) {
         if (this.step < 0) {
            if (this.position < positionOrLoopStart) {
               this.position = localLoopEnd - 1 - (localLoopEnd - 1 - this.position) % scalar;
               return;
            }
         } else if (this.position >= localLoopEnd) {
            this.position = positionOrLoopStart + (this.position - positionOrLoopStart) % scalar;
            return;
         }
      } else {
         if (this.loopCount > 0) {
            if (this.step < 0) {
               if (this.position >= positionOrLoopStart) {
                  return;
               }

               if ((positionOrLoopStart = (localLoopEnd - 1 - this.position) / scalar) < this.loopCount) {
                  this.position += scalar * positionOrLoopStart;
                  this.loopCount -= positionOrLoopStart;
                  return;
               }

               this.position = this.position + scalar * this.loopCount;
               this.loopCount = 0;
            } else {
               if (this.position < localLoopEnd) {
                  return;
               }

               if ((positionOrLoopStart = (this.position - positionOrLoopStart) / scalar) < this.loopCount) {
                  this.position -= scalar * positionOrLoopStart;
                  this.loopCount -= positionOrLoopStart;
                  return;
               }

               this.position = this.position - scalar * this.loopCount;
               this.loopCount = 0;
            }
         }

         if (this.step < 0) {
            if (this.position < 0) {
               this.position = 0;
               this.unlink();
               return;
            }
         } else if (this.position >= sampleCount) {
            this.position = sampleCount - 1;
            this.unlink();
         }
      }
   }

   private RawPcmStream(RawSound rawSound, int newStep, int newVolume) {
      this.sound = rawSound;
      this.loopStart = rawSound.start;
      this.loopEnd = rawSound.end;
      this.step = newStep;
      this.volume = newVolume;
      this.position = 0;
   }
   private final int mixForward(int[] values, int scalarArgument, int scalarArgument2, int scalarArgument3, int sample3) {
      if (this.rampRemaining > 0) {
         int scalar;
         if ((scalar = scalarArgument + this.rampRemaining) > scalarArgument3) {
            scalar = scalarArgument3;
         }

         this.rampRemaining += scalarArgument;
         if (this.step == 256 && (this.position & 0xFF) == 0) {
            RawPcmStream rawPcmStream = this;
            int scalar2 = scalarArgument2;
            int scalar3 = scalar;
            byte byteCode = 0;
            byte byteCode2 = 0;
            int volume = this.volume;
            int loopIndex = scalarArgument;
            int position3 = this.position;
            int[] integerBuffer = values;
            byte[] samples2 = ((RawSound)this.sound).samples;
            position3 >>= 8;
            scalar2 >>= 8;
            volume <<= 8;
            int position5;
            if ((position5 = loopIndex + scalar2 - position3) > scalar3) {
               position5 = scalar3;
            }

            position5 -= 3;

            while (loopIndex < position5) {
               int position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3++] * volume >> byteCode2;
               volume += byteCode;
               position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3++] * volume >> byteCode2;
               volume += byteCode;
               position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3++] * volume >> byteCode2;
               volume += byteCode;
               position6 = loopIndex++;
               integerBuffer[position6] += samples2[position3++] * volume >> byteCode2;
               volume += byteCode;
            }

            for (int loopIndex2 = position5 + 3; loopIndex < loopIndex2; volume += byteCode) {
               int scalar4 = loopIndex++;
               integerBuffer[scalar4] += samples2[position3++] * volume >> byteCode2;
            }

            rawPcmStream.volume = volume >> 8;
            rawPcmStream.position = position3 << 8;
            scalarArgument = loopIndex;
         } else {
            int scalar5 = sample3;
            int step = this.step;
            RawPcmStream rawPcmStream4 = this;
            int scalar6 = scalarArgument2;
            int scalar7 = scalar;
            byte byteCode3 = 0;
            byte byteCode4 = 0;
            int volume2 = this.volume;
            int scalar8 = scalarArgument;
            int position = this.position;
            int[] values2 = values;
            byte[] samples = ((RawSound)this.sound).samples;
            int scalar9;
            if (step == 0 || (scalar9 = scalar8 + (scalar6 - position + step - 257) / step) > scalar7) {
               scalar9 = scalar7;
            }

            while (scalar8 < scalar9) {
               int sampleIndex = position >> 8;
               byte sample = samples[sampleIndex];
               int scalar10 = scalar8++;
               values2[scalar10] += ((sample << 8) + (samples[sampleIndex + 1] - sample) * (position & 0xFF)) * volume2 >> byteCode4;
               volume2 += byteCode3;
               position += step;
            }

            if (step == 0 || (scalar9 = scalar8 + (scalar6 - position + step - 1) / step) > scalar7) {
               scalar9 = scalar7;
            }

            int scalar11 = scalar5;

            while (scalar8 < scalar9) {
               byte sample2 = samples[position >> 8];
               int scalar12 = scalar8++;
               values2[scalar12] += ((sample2 << 8) + (scalar11 - sample2) * (position & 0xFF)) * volume2 >> byteCode4;
               volume2 += byteCode3;
               position += step;
            }

            rawPcmStream4.volume = volume2;
            rawPcmStream4.position = position;
            scalarArgument = scalar8;
         }

         this.rampRemaining -= scalarArgument;
         if (this.rampRemaining != 0) {
            return scalarArgument;
         }

         this.volume = 0;
      }

      if (this.step == 256 && (this.position & 0xFF) == 0) {
         RawPcmStream rawPcmStream2 = this;
         int scalar13 = scalarArgument2;
         int scalar14 = scalarArgument3;
         int volume3 = this.volume;
         int scalar15 = scalarArgument;
         int position4 = this.position;
         int[] values3 = values;
         byte[] samples3 = ((RawSound)this.sound).samples;
         position4 >>= 8;
         scalar13 >>= 8;
         volume3 <<= 8;
         int scalar16;
         if ((scalar16 = scalar15 + scalar13 - position4) > scalar14) {
            scalar16 = scalar14;
         }

         scalar16 -= 3;

         while (scalar15 < scalar16) {
            int position7 = scalar15++;
            values3[position7] += samples3[position4++] * volume3;
            position7 = scalar15++;
            values3[position7] += samples3[position4++] * volume3;
            position7 = scalar15++;
            values3[position7] += samples3[position4++] * volume3;
            position7 = scalar15++;
            values3[position7] += samples3[position4++] * volume3;
         }

         scalar16 += 3;

         while (scalar15 < scalar16) {
            int scalar17 = scalar15++;
            values3[scalar17] += samples3[position4++] * volume3;
         }

         rawPcmStream2.position = position4 << 8;
         return scalar15;
      } else {
         int scalar18 = sample3;
         int step2 = this.step;
         RawPcmStream rawPcmStream3 = this;
         int scalar19 = scalarArgument2;
         int scalar20 = scalarArgument3;
         int volume4 = this.volume;
         int scalar21 = scalarArgument;
         int position2 = this.position;
         int[] values4 = values;
         byte[] samples4 = ((RawSound)this.sound).samples;
         int scalar22;
         if (step2 == 0 || (scalar22 = scalar21 + (scalar19 - position2 + step2 - 257) / step2) > scalar20) {
            scalar22 = scalar20;
         }

         while (scalar21 < scalar22) {
            int scalar23 = position2 >> 8;
            byte byteCode5 = samples4[scalar23];
            int scalar24 = scalar21++;
            values4[scalar24] += ((byteCode5 << 8) + (samples4[scalar23 + 1] - byteCode5) * (position2 & 0xFF)) * volume4;
            position2 += step2;
         }

         int scalar25;
         if (step2 == 0 || (scalar25 = scalar21 + (scalar19 - position2 + step2 - 1) / step2) > scalar20) {
            scalar25 = scalar20;
         }

         int scalar26 = scalar18;

         while (scalar21 < scalar25) {
            byte byteCode6 = samples4[position2 >> 8];
            int scalar27 = scalar21++;
            values4[scalar27] += ((byteCode6 << 8) + (scalar26 - byteCode6) * (position2 & 0xFF)) * volume4;
            position2 += step2;
         }

         rawPcmStream3.position = position2;
         return scalar21;
      }
   }
}
