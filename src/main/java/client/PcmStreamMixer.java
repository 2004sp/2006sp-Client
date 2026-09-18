package client;
final class PcmStreamMixer extends PcmStream {
   private NodeList[] subStreamsByPriority = new NodeList[8];
   private NodeList listeners = new NodeList();
   private int mixLimit = 16;
   private int nextListenerTime = -1;
   private int processedSamples = 0;
   private int priorityRefreshCountdown = 0;
   @Override
   final synchronized void skip(int sampleCount) {
      while (this.nextListenerTime >= 0) {
         if (this.processedSamples + sampleCount < this.nextListenerTime) {
            this.processedSamples += sampleCount;
            this.skipSubStreams(sampleCount);
            return;
         }

         int sampleCount2 = this.nextListenerTime - this.processedSamples;
         this.skipSubStreams(sampleCount2);
         sampleCount -= sampleCount2;
         this.processedSamples += sampleCount2;
         this.advanceListenerTimes();
         PcmStreamMixerListener pcmStreamMixerListener;
         synchronized (pcmStreamMixerListener = (PcmStreamMixerListener)this.listeners.last()) {
            int sourceRemainingSamples;
            if ((sourceRemainingSamples = pcmStreamMixerListener.update()) < 0) {
               pcmStreamMixerListener.remainingSamples = 0;
               this.removeListener(pcmStreamMixerListener);
            } else {
               pcmStreamMixerListener.remainingSamples = sourceRemainingSamples;
               this.insertListener(pcmStreamMixerListener.previous, pcmStreamMixerListener);
            }
         }

         if (sampleCount == 0) {
            return;
         }
      }

      this.skipSubStreams(sampleCount);
   }
   private final void skipSubStreams(int sampleCount) {
      this.priorityRefreshCountdown -= sampleCount;
      if (this.priorityRefreshCountdown < 0) {
         this.priorityRefreshCountdown = 0;
      }

      NodeList nodeList;
      for (int subStreamsByPriorityIndex = 0; subStreamsByPriorityIndex < 8; subStreamsByPriorityIndex++) {
         for (PcmStream pcmStream = (PcmStream)(nodeList = this.subStreamsByPriority[subStreamsByPriorityIndex]).last(); pcmStream != null; pcmStream = (PcmStream)nodeList.previous()) {
            pcmStream.skip(sampleCount);
         }
      }
   }
   private final void advanceListenerTimes() {
      if (this.processedSamples > 0) {
         for (PcmStreamMixerListener pcmStreamMixerListener = (PcmStreamMixerListener)this.listeners.last(); pcmStreamMixerListener != null; pcmStreamMixerListener = (PcmStreamMixerListener)this.listeners.previous()) {
            pcmStreamMixerListener.remainingSamples = pcmStreamMixerListener.remainingSamples - this.processedSamples;
         }

         this.nextListenerTime = this.nextListenerTime - this.processedSamples;
         this.processedSamples = 0;
      }
   }
   @Override
   final synchronized int fill(int[] samples, int offset, int length) {
      while (this.nextListenerTime >= 0) {
         if (this.processedSamples + length < this.nextListenerTime) {
            this.processedSamples += length;
            return this.fillSubStreams(samples, offset, length);
         }

         int localNextListenerTime = this.nextListenerTime - this.processedSamples;
         int mixedSamples = this.fillSubStreams(samples, offset, localNextListenerTime);
         offset += localNextListenerTime;
         length -= localNextListenerTime;
         this.processedSamples += localNextListenerTime;
         this.advanceListenerTimes();
         PcmStreamMixerListener pcmStreamMixerListener;
         synchronized (pcmStreamMixerListener = (PcmStreamMixerListener)this.listeners.last()) {
            int sourceRemainingSamples;
            if ((sourceRemainingSamples = pcmStreamMixerListener.update()) < 0) {
               pcmStreamMixerListener.remainingSamples = 0;
               this.removeListener(pcmStreamMixerListener);
            } else {
               pcmStreamMixerListener.remainingSamples = sourceRemainingSamples;
               this.insertListener(pcmStreamMixerListener.previous, pcmStreamMixerListener);
            }
         }

         if (length == 0) {
            return mixedSamples;
         }
      }

      return this.fillSubStreams(samples, offset, length);
   }
   private final void insertListener(Node node, PcmStreamMixerListener pcmStreamMixerListener) {
      while (node != this.listeners.head && ((PcmStreamMixerListener)node).remainingSamples <= pcmStreamMixerListener.remainingSamples) {
         node = node.previous;
      }

      Node sourceNode = node;
      node = pcmStreamMixerListener;
      if (pcmStreamMixerListener.next != null) {
         node.unlink();
      }

      node.previous = sourceNode;
      node.next = sourceNode.next;
      node.next.previous = node;
      node.previous.next = node;
      this.nextListenerTime = ((PcmStreamMixerListener)this.listeners.head.previous).remainingSamples;
   }
   final synchronized void addSubStream(PcmStream pcmStream) {
      this.subStreamsByPriority[pcmStream.getPriority() >> 5].addLast(pcmStream);
   }
   private final void removeListener(PcmStreamMixerListener pcmStreamMixerListener) {
      pcmStreamMixerListener.unlink();
      pcmStreamMixerListener = (PcmStreamMixerListener)this.listeners.head.previous;
      if (this.listeners.head.previous == this.listeners.head) {
         this.nextListenerTime = -1;
      } else {
         this.nextListenerTime = pcmStreamMixerListener.remainingSamples;
      }
   }
   private final int fillSubStreams(int[] values, int offset, int length) {
      this.priorityRefreshCountdown -= length;
      if (this.priorityRefreshCountdown <= 0) {
         this.priorityRefreshCountdown = this.priorityRefreshCountdown + (Client.audioSampleRate >> 4);

         NodeList nodeList;
         for (int subStreamsByPriorityIndex2 = 0; subStreamsByPriorityIndex2 < 8; subStreamsByPriorityIndex2++) {
            for (PcmStream pcmStream = (PcmStream)(nodeList = this.subStreamsByPriority[subStreamsByPriorityIndex2]).last(); pcmStream != null; pcmStream = (PcmStream)nodeList.previous()) {
               int priorityIndex;
               if ((priorityIndex = pcmStream.getPriority() >> 5) != subStreamsByPriorityIndex2) {
                  this.subStreamsByPriority[priorityIndex].addLast(pcmStream);
               }
            }
         }
      }

      NodeList nodeList3;
      for (int subStreamsByPriorityIndex = 0; subStreamsByPriorityIndex < 8; subStreamsByPriorityIndex++) {
         for (PcmStream pcmStream3 = (PcmStream)(nodeList3 = this.subStreamsByPriority[subStreamsByPriorityIndex]).last(); pcmStream3 != null; pcmStream3 = (PcmStream)nodeList3.previous()) {
            pcmStream3.active = false;
            if (pcmStream3.sound != null) {
               pcmStream3.sound.position = 0;
            }
         }
      }

      int scalar = 0;
      int loopIndex = 255;

      for (int position = 7; loopIndex != 0; position--) {
         int scalar2;
         int subStreamsByPriorityIndex3;
         if (position < 0) {
            subStreamsByPriorityIndex3 = position & 3;
            scalar2 = -(position >> 2);
         } else {
            subStreamsByPriorityIndex3 = position;
            scalar2 = 0;
         }

         for (int loopIndex2 = loopIndex >>> subStreamsByPriorityIndex3 & 286331153; loopIndex2 != 0; loopIndex2 >>>= 4) {
            if ((loopIndex2 & 1) != 0) {
               loopIndex &= ~(1 << subStreamsByPriorityIndex3);

               NodeList nodeList2;
               for (PcmStream pcmStream2 = (PcmStream)(nodeList2 = this.subStreamsByPriority[subStreamsByPriorityIndex3]).last(); pcmStream2 != null; pcmStream2 = (PcmStream)nodeList2.previous()) {
                  if (!pcmStream2.active) {
                     AbstractSound abstractSound = pcmStream2.sound;
                     if (pcmStream2.sound != null && abstractSound.position > scalar2) {
                        loopIndex |= 1 << subStreamsByPriorityIndex3;
                     } else {
                        if (scalar < this.mixLimit) {
                           int mixedSamples = pcmStream2.fill(values, offset, length);
                           scalar += mixedSamples;
                           if (abstractSound != null) {
                              abstractSound.position += mixedSamples;
                           }
                        } else {
                           pcmStream2.skip(length);
                        }

                        pcmStream2.active = true;
                     }
                  }
               }
            }

            subStreamsByPriorityIndex3 += 4;
            scalar2++;
         }
      }

      return scalar;
   }

   public PcmStreamMixer() {
      for (int subStreamsByPriorityIndex = 0; subStreamsByPriorityIndex < 8; subStreamsByPriorityIndex++) {
         this.subStreamsByPriority[subStreamsByPriorityIndex] = new NodeList();
      }
   }
}
