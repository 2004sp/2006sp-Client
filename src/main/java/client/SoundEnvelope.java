package client;
final class SoundEnvelope {
   private int[] peaks = new int[2];
   private int[] durations = new int[2];
   int form;
   int end;
   private int segments = 2;
   int start;
   private int threshold;
   private int segmentIndex;
   private int step;
   private int amplitude;
   private int ticks;
   final int step(int scalarArgument) {
      if (this.ticks >= this.threshold) {
         this.amplitude = this.peaks[this.segmentIndex++] << 15;
         if (this.segmentIndex >= this.segments) {
            this.segmentIndex = this.segments - 1;
         }

         this.threshold = (int)(this.durations[this.segmentIndex] / 65536.0 * scalarArgument);
         if (this.threshold > this.ticks) {
            this.step = ((this.peaks[this.segmentIndex] << 15) - this.amplitude) / (this.threshold - this.ticks);
         }
      }

      this.amplitude = this.amplitude + this.step;
      this.ticks++;
      return this.amplitude - this.step >> 15;
   }
   final void reset() {
      this.threshold = 0;
      this.segmentIndex = 0;
      this.step = 0;
      this.amplitude = 0;
      this.ticks = 0;
   }
   final void decodeSegments(Buffer buffer) {
      this.segments = buffer.readUnsignedByte();
      this.durations = new int[this.segments];
      this.peaks = new int[this.segments];

      for (int durationIndex = 0; durationIndex < this.segments; durationIndex++) {
         this.durations[durationIndex] = buffer.readUnsignedShort();
         this.peaks[durationIndex] = buffer.readUnsignedShort();
      }
   }
   final void decode(Buffer buffer) {
      this.form = buffer.readUnsignedByte();
      this.start = buffer.readInt();
      this.end = buffer.readInt();
      this.decodeSegments(buffer);
   }

   public SoundEnvelope() {
      this.durations[0] = 0;
      this.durations[1] = 65535;
      this.peaks[0] = 0;
      this.peaks[1] = 65535;
   }
}
