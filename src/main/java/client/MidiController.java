package client;
abstract class MidiController extends MidiPlayer {
   final void setVolume(int attenuation, int scaledVolume, long longScalarArgument) {
      if ((scaledVolume = (int)(scaledVolume * Math.pow(0.1, attenuation * 5.0E-4) + 0.5)) != Client.midiMasterVolume) {
         Client.midiMasterVolume = scaledVolume;

         for (int midiChannelVolumeIndex = 0; midiChannelVolumeIndex < 16; midiChannelVolumeIndex++) {
            scaledVolume = getChannelVolume(midiChannelVolumeIndex);
            this.sendMidiMessage(midiChannelVolumeIndex + 176, 7, scaledVolume >> 7, -1L);
            this.sendMidiMessage(midiChannelVolumeIndex + 176, 39, scaledVolume & 127, -1L);
         }
      }
   }
   abstract void sendMidiMessage(int status, int controller, int payloadArgument, long timestamp);
   final boolean handleControlChange(int status, int controller, int localGetMessageEntry, long timestamp) {
      if ((status & 240) == 176) {
         if (controller == 121) {
            this.sendMidiMessage(status, controller, localGetMessageEntry, timestamp);
            int midiChannelVolumeIndex = status & 15;
            Client.midiChannelVolumes[midiChannelVolumeIndex] = 12800;
            controller = getChannelVolume(midiChannelVolumeIndex);
            this.sendMidiMessage(status, 7, controller >> 7, timestamp);
            this.sendMidiMessage(status, 39, controller & 127, timestamp);
            return true;
         }

         if (controller == 7 || controller == 39) {
            int midiChannelVolumeIndex2 = status & 15;
            if (controller == 7) {
               Client.midiChannelVolumes[midiChannelVolumeIndex2] = (Client.midiChannelVolumes[midiChannelVolumeIndex2] & 127) + (localGetMessageEntry << 7);
            } else {
               Client.midiChannelVolumes[midiChannelVolumeIndex2] = (Client.midiChannelVolumes[midiChannelVolumeIndex2] & 16256) + localGetMessageEntry;
            }

            controller = getChannelVolume(midiChannelVolumeIndex2);
            this.sendMidiMessage(status, 7, controller >> 7, timestamp);
            this.sendMidiMessage(status, 39, controller & 127, timestamp);
            return true;
         }
      }

      return false;
   }
   final void reset(long longScalarArgument) {
      for (int loopIndex = 0; loopIndex < 16; loopIndex++) {
         this.sendMidiMessage(loopIndex + 176, 123, 0, -1L);
      }

      for (int loopIndex2 = 0; loopIndex2 < 16; loopIndex2++) {
         this.sendMidiMessage(loopIndex2 + 176, 120, 0, -1L);
      }

      for (int loopIndex3 = 0; loopIndex3 < 16; loopIndex3++) {
         this.sendMidiMessage(loopIndex3 + 176, 121, 0, -1L);
      }

      for (int loopIndex4 = 0; loopIndex4 < 16; loopIndex4++) {
         this.sendMidiMessage(loopIndex4 + 176, 0, 0, -1L);
      }

      for (int loopIndex5 = 0; loopIndex5 < 16; loopIndex5++) {
         this.sendMidiMessage(loopIndex5 + 176, 32, 0, -1L);
      }

      for (int loopIndex6 = 0; loopIndex6 < 16; loopIndex6++) {
         this.sendMidiMessage(loopIndex6 + 192, 0, 0, -1L);
      }
   }
   final void resetVolume(int newMidiMasterVolume, long longScalarArgument) {
      Client.midiMasterVolume = newMidiMasterVolume;

      for (int midiChannelVolumeIndex = 0; midiChannelVolumeIndex < 16; midiChannelVolumeIndex++) {
         Client.midiChannelVolumes[midiChannelVolumeIndex] = 12800;
      }

      for (int midiChannelVolumeIndex2 = 0; midiChannelVolumeIndex2 < 16; midiChannelVolumeIndex2++) {
         int channelVolume = getChannelVolume(midiChannelVolumeIndex2);
         this.sendMidiMessage(midiChannelVolumeIndex2 + 176, 7, channelVolume >> 7, -1L);
         this.sendMidiMessage(midiChannelVolumeIndex2 + 176, 39, channelVolume & 127, -1L);
      }
   }
   private static final int getChannelVolume(int midiChannelVolumeIndex) {
      int localMidiChannelVolumes;
      return (int)(Math.sqrt(((localMidiChannelVolumes = Client.midiChannelVolumes[midiChannelVolumeIndex]) * Client.midiMasterVolume >> 8) * localMidiChannelVolumes) + 0.5);
   }
}
