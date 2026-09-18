package client;
final class SoundEffect {
   private int loopEnd;
   private int loopStart;
   private SoundTone[] tones = new SoundTone[10];
   public static SoundEffect[] effects = new SoundEffect[5000];
   private byte[] data;
   public static void load(Buffer buffer) {
      int effectIndex;
      while ((effectIndex = buffer.readUnsignedShort()) != 65535) {
         effects[effectIndex] = new SoundEffect(buffer);
      }
   }
   final RawSound toRawSound() {
      byte[] byteBuffer;
      if (this.data != null) {
         byteBuffer = this.data;
      } else {
         SoundEffect soundEffect = this;
         int localTones = 0;

         for (int toneIndex2 = 0; toneIndex2 < 10; toneIndex2++) {
            if (soundEffect.tones[toneIndex2] != null && soundEffect.tones[toneIndex2].duration + soundEffect.tones[toneIndex2].offset > localTones) {
               localTones = soundEffect.tones[toneIndex2].duration + soundEffect.tones[toneIndex2].offset;
            }
         }

         if (localTones == 0) {
            byteBuffer = new byte[0];
         } else {
            byte[] byteBuffer2 = new byte[localTones * 22050 / 1000];

            for (int toneIndex = 0; toneIndex < 10; toneIndex++) {
               if (soundEffect.tones[toneIndex] != null) {
                  int tones2 = soundEffect.tones[toneIndex].duration * 22050 / 1000;
                  int tones3 = soundEffect.tones[toneIndex].offset * 22050 / 1000;
                  int[] tones4 = soundEffect.tones[toneIndex].synthesize(tones2, soundEffect.tones[toneIndex].duration);

                  for (int loopIndex = 0; loopIndex < tones2; loopIndex++) {
                     int scalar;
                     if (((scalar = byteBuffer2[loopIndex + tones3] + (tones4[loopIndex] >> 8)) + 128 & -256) != 0) {
                        scalar = scalar >> 31 ^ 127;
                     }

                     byteBuffer2[loopIndex + tones3] = (byte)scalar;
                  }
               }
            }

            byteBuffer = byteBuffer2;
         }
      }

      byte[] byteBuffer3 = byteBuffer;
      return new RawSound(22050, byteBuffer3, this.loopStart * 22050 / 1000, this.loopEnd * 22050 / 1000);
   }

   public SoundEffect(byte[] newData) {
      this.loopStart = 0;
      this.loopEnd = 0;
      this.data = newData;
   }
   final int trim() {
      int localTones = 9999999;

      for (int toneIndex2 = 0; toneIndex2 < 10; toneIndex2++) {
         if (this.tones[toneIndex2] != null && this.tones[toneIndex2].offset / 20 < localTones) {
            localTones = this.tones[toneIndex2].offset / 20;
         }
      }

      if (this.loopStart < this.loopEnd && this.loopStart / 20 < localTones) {
         localTones = this.loopStart / 20;
      }

      if (localTones != 9999999 && localTones != 0) {
         for (int toneIndex = 0; toneIndex < 10; toneIndex++) {
            if (this.tones[toneIndex] != null) {
               this.tones[toneIndex].offset -= localTones * 20;
            }
         }

         if (this.loopStart < this.loopEnd) {
            this.loopStart -= localTones * 20;
            this.loopEnd -= localTones * 20;
         }

         return localTones;
      } else {
         return 0;
      }
   }

   private SoundEffect(Buffer buffer) {
      for (int toneIndex = 0; toneIndex < 10; toneIndex++) {
         if (buffer.readUnsignedByte() != 0) {
            buffer.currentPosition--;
            this.tones[toneIndex] = new SoundTone();
            this.tones[toneIndex].decode(buffer);
         }
      }

      this.loopStart = buffer.readUnsignedShort();
      this.loopEnd = buffer.readUnsignedShort();
   }

   private SoundEffect() {
   }
}
