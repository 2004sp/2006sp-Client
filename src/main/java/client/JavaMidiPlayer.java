package client;

import java.io.ByteArrayInputStream;
import java.io.File;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
final class JavaMidiPlayer extends MidiController implements Receiver {
   private static Receiver receiver = null;
   private static Sequencer sequencer = null;
   private static Synthesizer synthesizer = null;
   private static File soundbankFile = null;
   private static boolean unusedFlag = false;
   @Override
   final void play(int volume, byte[] midiData, boolean loop) {
      if (sequencer != null) {
         try {
            Sequence sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiData));
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(!loop ? 0 : -1);
            this.setVolume(0, volume, -1L);
            sequencer.start();
            return;
         } catch (Exception exception) {
         }
      }
   }
   @Override
   final void stop() {
      if (sequencer != null) {
         sequencer.stop();
         this.reset(-1L);
      }
   }

   @Override
   public final synchronized void send(MidiMessage message, long timestamp) {
      byte[] localGetMessage;
      if ((localGetMessage = message.getMessage()).length < 3 || !this.handleControlChange(localGetMessage[0], localGetMessage[1], localGetMessage[2], timestamp)) {
         receiver.send(message, timestamp);
      }
   }

   JavaMidiPlayer() {
      try {
         Soundbank soundbank = null;
         File file = new File(SignLink.findcachedir() + Client.customSoundfontName);
         if (soundbankFile == null && file.exists()) {
            soundbankFile = file;
         }

         if (soundbankFile != null) {
            soundbank = MidiSystem.getSoundbank(soundbankFile);
         }

         receiver = MidiSystem.getReceiver();
         (sequencer = MidiSystem.getSequencer(false)).getTransmitter().setReceiver(this);
         if (Client.customSoundfontEnabled) {
            synthesizer = MidiSystem.getSynthesizer();
         }

         sequencer.open();
         if (Client.customSoundfontEnabled && soundbank != null) {
            synthesizer.open();
            synthesizer.loadAllInstruments(soundbank);
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            receiver = synthesizer.getReceiver();
         }

         this.reset(-1L);
      } catch (Exception exception) {
         Client.shutdownMidiPlayer();
      }
   }
   @Override
   final void closeResources() {
      if (sequencer != null) {
         sequencer.close();
         sequencer = null;
      }

      if (receiver != null) {
         receiver.close();
         receiver = null;
      }
   }

   @Override
   public final void close() {
   }
   @Override
   final void resetVolume(int volume) {
      if (sequencer != null) {
         this.resetVolume(volume, -1L);
      }
   }
   @Override
   final synchronized void setVolume(int volume, int attenuation) {
      if (sequencer != null) {
         this.setVolume(attenuation, volume, -1L);
      }
   }
   @Override
   final void sendMidiMessage(int status, int payloadArgument, int payloadArgument2, long timestamp) {
      try {
         ShortMessage shortMessage;
         (shortMessage = new ShortMessage()).setMessage(status, payloadArgument, payloadArgument2);
         receiver.send(shortMessage, timestamp);
      } catch (InvalidMidiDataException exception) {
      }
   }
   @Override
   final void stopIfRequested(int sentinel) {
      if (sentinel > -90) {
         this.stop();
      }
   }
}
