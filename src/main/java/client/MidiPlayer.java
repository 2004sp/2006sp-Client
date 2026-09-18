package client;
abstract class MidiPlayer {
   abstract void play(int volume, byte[] midiData, boolean loop);
   abstract void closeResources();
   abstract void setVolume(int volume, int attenuation);
   abstract void resetVolume(int volume);
   abstract void stopIfRequested(int sentinel);
   abstract void stop();
}
