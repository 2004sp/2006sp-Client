package client;
abstract class PcmStream extends Node {
   AbstractSound sound;
   boolean active;
   abstract int fill(int[] samples, int offset, int length);
   int getPriority() {
      return 255;
   }
   abstract void skip(int sampleCount);
}
