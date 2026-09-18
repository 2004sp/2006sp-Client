package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;
final class JavaSoundPcmPlayer extends PcmPlayer {
   private AudioFormat audioFormat = new AudioFormat(22050.0F, 16, 1, true, false);
   private SourceDataLine outputLine;
   private byte[] outputBytes = new byte[512];
   private static Class sourceDataLineClass;
   @Override
   final void open(int capacity) {
      try {
         Info info = new Info(sourceDataLineClass == null ? (sourceDataLineClass = loadClass("javax.sound.sampled.SourceDataLine")) : sourceDataLineClass, this.audioFormat, capacity << 1);
         this.outputLine = (SourceDataLine)AudioSystem.getLine(info);
         this.outputLine.open();
         this.outputLine.start();
      } catch (LineUnavailableException exception) {
         this.outputLine = null;
         throw new RuntimeException(exception);
      }
   }
   @Override
   final int available() {
      return this.outputLine.available() >> 1;
   }

   JavaSoundPcmPlayer() {
      super(22050);
   }

   @Override
   final void close() {
      if (this.outputLine != null) {
         this.outputLine.close();
         this.outputLine = null;
      }
   }

   @Override
   final void write() {
      for (int sampleBufferIndex = 0; sampleBufferIndex < 256; sampleBufferIndex++) {
         int localSampleBuffer;
         if (((localSampleBuffer = sampleBuffer[sampleBufferIndex]) + 8388608 & 0xFF000000) != 0) {
            localSampleBuffer = 8388607 ^ localSampleBuffer >> 31;
         }

         this.outputBytes[sampleBufferIndex << 1] = (byte)(localSampleBuffer >> 8);
         this.outputBytes[(sampleBufferIndex << 1) + 1] = (byte)(localSampleBuffer >> 16);
      }

      this.outputLine.write(this.outputBytes, 0, 512);
   }
   private static Class loadClass(String text) {
      Class forNameResult = null;

      try {
         forNameResult = Class.forName(text);
      } catch (ClassNotFoundException exception) {
         ClassNotFoundException classNotFoundException = exception;

         try {
            throw new NoClassDefFoundError().initCause(classNotFoundException);
         } catch (Throwable throwable) {
            throwable.printStackTrace();
         }
      }

      return forNameResult;
   }
}
