package client;

import java.awt.Component;
final class ComponentPcmPlayer extends PcmPlayer {
   private static AudioDevice audioDevice;
   @Override
   final int available() {
      return audioDevice.available();
   }

   @Override
   final void close() {
   }
   @Override
   final void open(int capacity) {
   }

   @Override
   final void write() {
   }

   ComponentPcmPlayer(Component component) {
      super(22050);
      audioDevice = null;
      this.start(16384);
   }
}
