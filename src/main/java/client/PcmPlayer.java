package client;
abstract class PcmPlayer extends AudioPlayerBase implements Runnable {
   private long reopenAtMillis = 0L;
   private int minimumBufferDelta = 0;
   private int maximumBufferDelta = 0;
   private int bufferDeltaTotal = 0;
   private boolean unusedFlag = false;
   private long lastWriteMillis;
   static int[] sampleBuffer = new int[256];
   private int previousBufferedSamples;
   private int[] bufferHistory = new int[512];
   private long streamTimeMillis;
   private int historyIndex = 0;
   private int capacityRetryCount;
   private int targetBufferedSamples = 2560;
   private int bufferCapacity;
   abstract void open(int capacity);

   abstract void close();
   private final void reopen(long newStreamTimeMillis) {
      this.open(this.bufferCapacity);

      while (this.available() >= this.targetBufferedSamples) {
         this.write();
      }

      this.capacityRetryCount = 0;
      this.previousBufferedSamples = 0;
      this.streamTimeMillis = newStreamTimeMillis;
      this.lastWriteMillis = newStreamTimeMillis;
   }
   @Override
   public final void run() {
      while (true) {
         try {
            synchronized (this) {
               this.process(System.currentTimeMillis());
            }

            Client.sleep(5L);
         } catch (Exception exception) {
            return;
         }
      }
   }

   abstract void write();

   PcmPlayer(int scalarArgument) {
      super(22050);
   }
   abstract int available();
   @Override
   final synchronized void process(long sourceStreamTimeMillis) {
      processAudioCycle: {
         long sourceReopenAtMillis = sourceStreamTimeMillis;
         PcmPlayer pcmPlayer = this;
         if (this.reopenAtMillis != 0L) {
            while (pcmPlayer.streamTimeMillis < sourceReopenAtMillis) {
               Client.skipPcmSamples(256);
               pcmPlayer.streamTimeMillis = pcmPlayer.streamTimeMillis + 256000 / Client.audioSampleRate;
            }

            if (sourceReopenAtMillis < pcmPlayer.reopenAtMillis) {
               break processAudioCycle;
            }

            try {
               pcmPlayer.reopen(sourceReopenAtMillis);
            } catch (Exception exception) {
               pcmPlayer.close();
               pcmPlayer.reopenAtMillis += 5000L;
               break processAudioCycle;
            }

            pcmPlayer.reopenAtMillis = 0L;
         }

         while (pcmPlayer.streamTimeMillis < sourceReopenAtMillis) {
            pcmPlayer.streamTimeMillis = pcmPlayer.streamTimeMillis + 250880 / Client.audioSampleRate;

            int availableSamples;
            try {
               availableSamples = pcmPlayer.available();
            } catch (Exception exception3) {
               pcmPlayer.close();
               pcmPlayer.reopenAtMillis = sourceReopenAtMillis;
               break processAudioCycle;
            }

            int bufferHistoryOrMaximumBufferDelta = availableSamples;
            PcmPlayer sourcePcmPlayer = pcmPlayer;
            bufferHistoryOrMaximumBufferDelta -= sourcePcmPlayer.targetBufferedSamples;
            int bufferHistoryEntry = sourcePcmPlayer.bufferHistory[sourcePcmPlayer.historyIndex];
            sourcePcmPlayer.bufferHistory[sourcePcmPlayer.historyIndex] = bufferHistoryOrMaximumBufferDelta;
            sourcePcmPlayer.bufferDeltaTotal += bufferHistoryOrMaximumBufferDelta - bufferHistoryEntry;
            int sourceHistoryIndex = sourcePcmPlayer.historyIndex + 1 & 511;
            if (bufferHistoryOrMaximumBufferDelta > sourcePcmPlayer.maximumBufferDelta) {
               sourcePcmPlayer.maximumBufferDelta = bufferHistoryOrMaximumBufferDelta;
            }

            if (bufferHistoryOrMaximumBufferDelta < sourcePcmPlayer.minimumBufferDelta) {
               sourcePcmPlayer.minimumBufferDelta = bufferHistoryOrMaximumBufferDelta;
            }

            if (bufferHistoryEntry == sourcePcmPlayer.maximumBufferDelta) {
               int sourceMaximumBufferDelta = bufferHistoryOrMaximumBufferDelta;

               for (int bufferHistoryIndex = sourceHistoryIndex; bufferHistoryIndex != sourcePcmPlayer.historyIndex && sourceMaximumBufferDelta < sourcePcmPlayer.maximumBufferDelta; bufferHistoryIndex = bufferHistoryIndex + 1 & 511) {
                  int bufferHistory2;
                  if ((bufferHistory2 = sourcePcmPlayer.bufferHistory[bufferHistoryIndex]) > sourceMaximumBufferDelta) {
                     sourceMaximumBufferDelta = bufferHistory2;
                  }
               }

               sourcePcmPlayer.maximumBufferDelta = sourceMaximumBufferDelta;
            }

            if (bufferHistoryEntry == sourcePcmPlayer.minimumBufferDelta) {
               int sourceMinimumBufferDelta = bufferHistoryOrMaximumBufferDelta;

               for (int bufferHistoryIndex2 = sourceHistoryIndex; bufferHistoryIndex2 != sourcePcmPlayer.historyIndex && sourceMinimumBufferDelta > sourcePcmPlayer.minimumBufferDelta; bufferHistoryIndex2 = bufferHistoryIndex2 + 1 & 511) {
                  int bufferHistory3;
                  if ((bufferHistory3 = sourcePcmPlayer.bufferHistory[bufferHistoryIndex2]) < sourceMinimumBufferDelta) {
                     sourceMinimumBufferDelta = bufferHistory3;
                  }
               }

               sourcePcmPlayer.minimumBufferDelta = sourceMinimumBufferDelta;
            }

            sourcePcmPlayer.historyIndex = sourceHistoryIndex;
            int localBufferDeltaTotal;
            if ((localBufferDeltaTotal = pcmPlayer.bufferDeltaTotal * 3 / 512 - (pcmPlayer.minimumBufferDelta << 1)) < 0) {
               localBufferDeltaTotal = 0;
            } else if (localBufferDeltaTotal > pcmPlayer.maximumBufferDelta) {
               localBufferDeltaTotal = pcmPlayer.maximumBufferDelta;
            }

            pcmPlayer.targetBufferedSamples = pcmPlayer.bufferCapacity - 256 - localBufferDeltaTotal;
            if (pcmPlayer.targetBufferedSamples < 256) {
               pcmPlayer.targetBufferedSamples = 256;
            }

            if (pcmPlayer.bufferCapacity < 16384) {
               if (availableSamples >= pcmPlayer.bufferCapacity) {
                  pcmPlayer.capacityRetryCount += 5;
                  if (pcmPlayer.capacityRetryCount >= 100) {
                     pcmPlayer.close();
                     pcmPlayer.bufferCapacity += 2048;
                     pcmPlayer.reopenAtMillis = sourceReopenAtMillis;
                     break processAudioCycle;
                  }
               } else if (availableSamples != pcmPlayer.previousBufferedSamples && pcmPlayer.capacityRetryCount > 0) {
                  pcmPlayer.capacityRetryCount--;
               }
            }

            pcmPlayer.previousBufferedSamples = availableSamples;
            if (availableSamples < pcmPlayer.targetBufferedSamples) {
               break;
            }

            Client.mixPcmSamples(sampleBuffer, 256);

            try {
               pcmPlayer.write();
            } catch (Exception exception2) {
               pcmPlayer.close();
               pcmPlayer.reopenAtMillis = sourceReopenAtMillis;
               break processAudioCycle;
            }

            pcmPlayer.lastWriteMillis = sourceReopenAtMillis;
            pcmPlayer.previousBufferedSamples -= 256;
         }

         if (sourceReopenAtMillis >= pcmPlayer.lastWriteMillis + 5000L) {
            pcmPlayer.close();
            pcmPlayer.reopenAtMillis = sourceReopenAtMillis;

            for (int bufferHistoryIndex3 = 0; bufferHistoryIndex3 < 512; bufferHistoryIndex3++) {
               pcmPlayer.bufferHistory[bufferHistoryIndex3] = 0;
            }

            pcmPlayer.minimumBufferDelta = pcmPlayer.maximumBufferDelta = pcmPlayer.bufferDeltaTotal = 0;
         }
      }

      if (this.streamTimeMillis < sourceStreamTimeMillis) {
         this.streamTimeMillis = sourceStreamTimeMillis;
      }
   }
   final void start(int newBufferCapacity) {
      this.bufferCapacity = newBufferCapacity;
      this.reopen(System.currentTimeMillis());
      Thread thread;
      (thread = new Thread(this)).setDaemon(true);
      thread.start();
      thread.setPriority(10);
   }
}
