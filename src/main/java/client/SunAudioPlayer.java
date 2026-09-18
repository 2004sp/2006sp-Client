package client;

import sun.audio.AudioPlayer;
final class SunAudioPlayer extends AudioPlayerBase {
   private AudioInputStream audioStream = new AudioInputStream();

   SunAudioPlayer() {
      super(8000);
      AudioPlayer.player.start(this.audioStream);
   }
}
