package client;
final class RawSound extends AbstractSound {
   byte[] samples;
   int sampleRate = 22050;
   int end;
   int start;

   RawSound(int scalarArgument, byte[] newSamples, int newStart, int newEnd) {
      this.samples = newSamples;
      this.start = newStart;
      this.end = newEnd;
   }
}
