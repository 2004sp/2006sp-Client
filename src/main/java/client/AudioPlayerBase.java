package client;
class AudioPlayerBase {
   void process(long longScalarArgument) {
   }

   AudioPlayerBase(int newAudioSampleRate) {
      Client.audioSampleRate = newAudioSampleRate;
      Client.lastAudioUpdateMillis = System.currentTimeMillis();
   }
}
