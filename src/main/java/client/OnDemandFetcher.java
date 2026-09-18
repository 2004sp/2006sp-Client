package client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
public final class OnDemandFetcher extends OnDemandFetcherBase implements Runnable {
   private final NodeDeque requested = new NodeDeque();
   private int extraFilePriority;
   public String statusString = "";
   private int keepAliveCycles;
   private long openSocketTime;
   private int[] mapIndices3;
   private final byte[] ioBuffer;
   private int onDemandCycle;
   private Client clientInstance;
   private final NodeDeque extraRequests;
   private int completedSize;
   private int expectedSize;
   private int[] midiIndices;
   public int connectionErrors;
   private int[] mapIndices2;
   private boolean running;
   private OutputStream outputStream;
   private int[] mapIndices4;
   private boolean waiting;
   private final NodeDeque completedRequests;
   private final byte[] gzipInputBuffer;
   private int[] animationIndices;
   private final CacheableNodeDeque requestTracking;
   private InputStream inputStream;
   private Socket socket;
   private final int[][] crcs;
   private int highPriorityRequestCount;
   private int lowPriorityRequestCount;
   private final NodeDeque networkRequests;
   private OnDemandRequest current;
   private final NodeDeque cacheLookupRequests;
   private int[] mapIndices1;
   private byte[] modelIndices;
   private int socketIdleCycles;

   public OnDemandFetcher() {
      new CRC32();
      this.ioBuffer = new byte[500];
      this.extraRequests = new NodeDeque();
      this.running = true;
      this.waiting = false;
      this.completedRequests = new NodeDeque();
      this.gzipInputBuffer = new byte[999999];
      this.requestTracking = new CacheableNodeDeque();
      this.crcs = new int[Client.cacheStoreCount - 1][];
      this.networkRequests = new NodeDeque();
      this.cacheLookupRequests = new NodeDeque();
   }
   private void readData() {
      try {
         int localInputStream = this.inputStream.available();
         if (this.expectedSize == 0 && localInputStream >= 6) {
            this.waiting = true;
            int localIoBuffer = 0;

            while (localIoBuffer < 6) {
               localIoBuffer += this.inputStream.read(this.ioBuffer, localIoBuffer, 6 - localIoBuffer);
            }

            localIoBuffer = this.ioBuffer[0] & 255;
            int scalar = ((this.ioBuffer[1] & 255) << 8) + (this.ioBuffer[2] & 255);
            int expectedSizeOrLength = ((this.ioBuffer[3] & 255) << 8) + (this.ioBuffer[4] & 255);
            int completedSizeOrIoBuffer = this.ioBuffer[5] & 255;
            this.current = null;

            for (OnDemandRequest onDemandRequest = (OnDemandRequest)this.requested.first(); onDemandRequest != null; onDemandRequest = (OnDemandRequest)this.requested.next()) {
               if (onDemandRequest.dataType == localIoBuffer && onDemandRequest.id == scalar) {
                  this.current = onDemandRequest;
               }

               if (this.current != null) {
                  onDemandRequest.requestAge = 0;
               }
            }

            if (this.current != null) {
               this.socketIdleCycles = 0;
               if (expectedSizeOrLength == 0) {
                  SignLink.reporterror("Rej: " + localIoBuffer + "," + scalar);
                  this.current.buffer = null;
                  if (this.current.highPriority) {
                     synchronized (this.completedRequests) {
                        this.completedRequests.addLast(this.current);
                     }
                  } else {
                     this.current.unlink();
                  }

                  this.current = null;
               } else {
                  if (this.current.buffer == null && completedSizeOrIoBuffer == 0) {
                     this.current.buffer = new byte[expectedSizeOrLength];
                  }

                  if (this.current.buffer == null && completedSizeOrIoBuffer != 0) {
                     throw new IOException("missing start of file");
                  }
               }
            }

            this.completedSize = completedSizeOrIoBuffer * 500;
            this.expectedSize = 500;
            if (this.expectedSize > expectedSizeOrLength - completedSizeOrIoBuffer * 500) {
               this.expectedSize = expectedSizeOrLength - completedSizeOrIoBuffer * 500;
            }
         }

         if (this.expectedSize > 0 && localInputStream >= this.expectedSize) {
            this.waiting = true;
            byte[] ioBuffer2 = this.ioBuffer;
            int localCompletedSize = 0;
            if (this.current != null) {
               ioBuffer2 = this.current.buffer;
               localCompletedSize = this.completedSize;
            }

            int scalar2 = 0;

            while (scalar2 < this.expectedSize) {
               scalar2 += this.inputStream.read(ioBuffer2, scalar2 + localCompletedSize, this.expectedSize - scalar2);
            }

            if (this.expectedSize + this.completedSize >= ioBuffer2.length && this.current != null) {
               if (this.clientInstance.cacheStores[0] != null) {
                  this.clientInstance.cacheStores[this.current.dataType + 1].write(ioBuffer2.length, ioBuffer2, this.current.id);
               }

               if (!this.current.highPriority && this.current.dataType == 3) {
                  this.current.highPriority = true;
                  this.current.dataType = 93;
               }

               if (this.current.highPriority) {
                  synchronized (this.completedRequests) {
                     this.completedRequests.addLast(this.current);
                  }
               } else {
                  this.current.unlink();
               }
            }

            this.expectedSize = 0;
            return;
         }
      } catch (IOException exception) {
         try {
            this.socket.close();
         } catch (Exception exception2) {
         }

         this.socket = null;
         this.inputStream = null;
         this.outputStream = null;
         this.expectedSize = 0;
      }
   }
   public final void start(Archive archive, Client client) {
      String[] text = new String[]{"model_crc", "anim_crc", "midi_crc", "map_crc"};

      for (int textIndex = 0; textIndex < 4; textIndex++) {
         byte[] localGetFile;
         int localLength = (localGetFile = archive.getFile(text[textIndex])).length / 4;
         Buffer buffer = new Buffer(localGetFile);
         this.crcs[textIndex] = new int[localLength];

         for (int localIndex = 0; localIndex < localLength; localIndex++) {
            this.crcs[textIndex][localIndex] = buffer.readInt();
         }
      }

      byte[] modelIndiceOrGetFile = archive.getFile("model_index");
      int loopIndex = this.crcs[0].length;
      this.modelIndices = new byte[loopIndex];

      for (int modelIndex = 0; modelIndex < loopIndex; modelIndex++) {
         if (modelIndex < modelIndiceOrGetFile.length) {
            this.modelIndices[modelIndex] = modelIndiceOrGetFile[modelIndex];
         } else {
            this.modelIndices[modelIndex] = 0;
         }
      }

      modelIndiceOrGetFile = archive.getFile("map_index");
      Buffer buffer2 = new Buffer(modelIndiceOrGetFile);
      loopIndex = modelIndiceOrGetFile.length / 7;
      this.mapIndices1 = new int[loopIndex];
      this.mapIndices2 = new int[loopIndex];
      this.mapIndices3 = new int[loopIndex];
      this.mapIndices4 = new int[loopIndex];

      for (int loopIndex2 = 0; loopIndex2 < loopIndex; loopIndex2++) {
         this.mapIndices1[loopIndex2] = buffer2.readUnsignedShort();
         this.mapIndices2[loopIndex2] = buffer2.readUnsignedShort();
         this.mapIndices3[loopIndex2] = buffer2.readUnsignedShort();
         this.mapIndices4[loopIndex2] = buffer2.readUnsignedByte();
      }

      modelIndiceOrGetFile = archive.getFile("anim_index");
      buffer2 = new Buffer(modelIndiceOrGetFile);
      loopIndex = modelIndiceOrGetFile.length / 2;
      this.animationIndices = new int[loopIndex];

      for (int animationIndex = 0; animationIndex < loopIndex; animationIndex++) {
         this.animationIndices[animationIndex] = buffer2.readUnsignedShort();
      }

      modelIndiceOrGetFile = archive.getFile("midi_index");
      buffer2 = new Buffer(modelIndiceOrGetFile);
      this.midiIndices = new int[this.crcs[2].length];

      for (int midiIndex = 0; midiIndex < 647; midiIndex++) {
         this.midiIndices[midiIndex] = buffer2.readUnsignedByte();
      }

      this.clientInstance = client;
      this.running = true;
      this.clientInstance.startRunnable(this, 2);
   }
   public final int getNodeCount() {
      synchronized (this.requestTracking) {
         return this.requestTracking.size();
      }
   }

   public final void disable() {
      this.running = false;
   }
   public final void prefetchMaps(boolean flag) {
      int mapIndices1LengthOrMapIndices1 = this.mapIndices1.length;

      for (int loopIndex = 0; loopIndex < mapIndices1LengthOrMapIndices1; loopIndex++) {
         if (flag || this.mapIndices4[loopIndex] != 0) {
            this.readCacheFile(3, this.mapIndices3[loopIndex]);
            this.readCacheFile(3, this.mapIndices2[loopIndex]);
         }
      }
   }
   public final int getVersionCount(int crcIndex) {
      if (Client.hdModels && crcIndex == 0) {
         return 45468;
      } else {
         return Client.extendedRevisionEnabled && crcIndex == 0 ? 29192 : this.crcs[crcIndex].length;
      }
   }
   private void sendRequest(OnDemandRequest onDemandRequest) {
      try {
         if (this.socket == null) {
            long openSocketTimeOrCurrentTimeMillis;
            if ((openSocketTimeOrCurrentTimeMillis = System.currentTimeMillis()) - this.openSocketTime < 4000L) {
               return;
            }

            this.openSocketTime = openSocketTimeOrCurrentTimeMillis;
            this.socket = this.clientInstance.openSocket(43594);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
            this.outputStream.write(15);

            for (int loopIndex = 0; loopIndex < 8; loopIndex++) {
               this.inputStream.read();
            }

            this.socketIdleCycles = 0;
         }

         this.ioBuffer[0] = (byte)onDemandRequest.dataType;
         this.ioBuffer[1] = (byte)(onDemandRequest.id >> 8);
         this.ioBuffer[2] = (byte)onDemandRequest.id;
         if (onDemandRequest.highPriority) {
            this.ioBuffer[3] = 2;
         } else if (!Client.loggedIn) {
            this.ioBuffer[3] = 1;
         } else {
            this.ioBuffer[3] = 0;
         }

         this.outputStream.write(this.ioBuffer, 0, 4);
         this.keepAliveCycles = 0;
         this.connectionErrors = -10000;
      } catch (IOException exception) {
         try {
            this.socket.close();
         } catch (Exception exception2) {
         }

         this.socket = null;
         this.inputStream = null;
         this.outputStream = null;
         this.expectedSize = 0;
         this.connectionErrors++;
      }
   }
   public final int getAnimCount() {
      return this.animationIndices.length;
   }
   public final void provide(int newDataType, int newId) {
      synchronized (this.requestTracking) {
         for (OnDemandRequest onDemandRequest = (OnDemandRequest)this.requestTracking.first(); onDemandRequest != null; onDemandRequest = (OnDemandRequest)this.requestTracking.next()) {
            if (onDemandRequest.dataType == newDataType && onDemandRequest.id == newId) {
               return;
            }
         }

         OnDemandRequest node;
         (node = new OnDemandRequest()).dataType = newDataType;
         node.id = newId;
         node.highPriority = true;
         synchronized (this.cacheLookupRequests) {
            this.cacheLookupRequests.addLast(node);
         }

         this.requestTracking.addLast(node);
      }
   }
   public final int getModelIndex(int modelIndex) {
      if (Client.hdModels) {
         return 45468;
      } else {
         return Client.extendedRevisionEnabled ? 29192 : this.modelIndices[modelIndex] & 0xFF;
      }
   }
   @Override
   public final void run() {
      while (true) {
         try {
            while (this.running) {
               this.onDemandCycle++;
               byte byteCode = 20;
               if (this.extraFilePriority == 0 && this.clientInstance.cacheStores[0] != null) {
                  byteCode = 50;
               }

               try {
                  Thread.sleep(byteCode);
               } catch (Exception exception) {
               }

               this.waiting = true;

               for (int position = 0; position < 100 && this.waiting; position++) {
                  this.waiting = false;
                  OnDemandFetcher onDemandFetcher = this;
                  OnDemandRequest onDemandRequest;
                  synchronized (onDemandFetcher.cacheLookupRequests) {
                     onDemandRequest = (OnDemandRequest)onDemandFetcher.cacheLookupRequests.removeFirst();
                  }

                  while (onDemandRequest != null) {
                     onDemandFetcher.waiting = true;
                     byte[] sourceBuffer = null;
                     if (Client.hdModels) {
                        if (onDemandRequest.dataType == 0 && !Client.isCacheOnlyModelId(onDemandRequest.id)) {
                           sourceBuffer = GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Models/" + onDemandRequest.id + ".gz");
                        }

                        if (onDemandRequest.dataType == 1) {
                           sourceBuffer = GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Anims/" + onDemandRequest.id + ".gz");
                        }
                     }

                     int cacheStoreIndex = onDemandRequest.dataType + 1;
                     if (!Client.hdModels && Client.extendedRevisionEnabled) {
                        selectExtendedCacheStore:
                        if (onDemandRequest.dataType == 0) {
                           if (onDemandRequest.id <= 15027 && (!Client.use2007Models || onDemandRequest.id > 14926 || Client.isCacheOnlyModelId(onDemandRequest.id))) {
                              Client client = Client.getClient();
                              int id = onDemandRequest.id;
                              Client sourceClient = client;
                              int localBaseX = client.baseX >> 6;
                              int localBaseY;
                              int scalar;
                              if ((scalar = (localBaseY = sourceClient.baseY >> 6) + (localBaseX << 8)) != 11090 && scalar != 11346 && scalar != 11089 && scalar != 11345
                                 || id != 834
                                    && id != 1095
                                    && id != 1112
                                    && id != 1126
                                    && id != 2214
                                    && id != 4873
                                    && id != 2215
                                    && id != 1105
                                    && id != 144
                                    && id != 12736
                                    && id != 143
                                    && id != 5256
                                    && id != 12737
                                    && id != 14204
                                    && id != 1124
                                    && id != 1139
                                    && id != 1032
                                    && id != 2109
                                    && id != 1482
                                    && id != 12446
                                    && id != 8232
                                    && id != 14828
                                    && id != 14829
                                    && id != 13745
                                    && id != 13746
                                    && id != 145
                                    && id != 14447) {
                                 break selectExtendedCacheStore;
                              }
                           }

                           cacheStoreIndex = Client.extendedModelCacheStoreIndex;
                        }

                        if (onDemandRequest.dataType == 1 && (onDemandRequest.id > 1043 || Client.use2007Models || Client.extendedModelIds.contains(onDemandRequest.id))) {
                           cacheStoreIndex = Client.extendedAnimationCacheStoreIndex;
                        }
                     }

                     if (onDemandFetcher.clientInstance.cacheStores[0] != null && sourceBuffer == null) {
                        sourceBuffer = onDemandFetcher.clientInstance.cacheStores[cacheStoreIndex].read(onDemandRequest.id);
                     }

                     synchronized (onDemandFetcher.cacheLookupRequests) {
                        if (sourceBuffer == null) {
                           onDemandFetcher.networkRequests.addLast(onDemandRequest);
                        } else {
                           onDemandRequest.buffer = sourceBuffer;
                           synchronized (onDemandFetcher.completedRequests) {
                              onDemandFetcher.completedRequests.addLast(onDemandRequest);
                           }
                        }

                        onDemandRequest = (OnDemandRequest)onDemandFetcher.cacheLookupRequests.removeFirst();
                     }
                  }

                  this.processHighPriorityRequests();
                  if (this.highPriorityRequestCount == 0 && position >= 5) {
                     break;
                  }

                  this.processExtraRequests();
                  if (this.inputStream != null) {
                     this.readData();
                  }
               }

               boolean localRunning = false;

               for (OnDemandRequest onDemandRequest3 = (OnDemandRequest)this.requested.first(); onDemandRequest3 != null; onDemandRequest3 = (OnDemandRequest)this.requested.next()) {
                  if (onDemandRequest3.highPriority) {
                     localRunning = true;
                     onDemandRequest3.requestAge++;
                     if (onDemandRequest3.requestAge > 50) {
                        onDemandRequest3.requestAge = 0;
                        this.sendRequest(onDemandRequest3);
                     }
                  }
               }

               if (!localRunning) {
                  for (OnDemandRequest onDemandRequest2 = (OnDemandRequest)this.requested.first(); onDemandRequest2 != null; onDemandRequest2 = (OnDemandRequest)this.requested.next()) {
                     localRunning = true;
                     onDemandRequest2.requestAge++;
                     if (onDemandRequest2.requestAge > 50) {
                        onDemandRequest2.requestAge = 0;
                        this.sendRequest(onDemandRequest2);
                     }
                  }
               }

               if (localRunning) {
                  this.socketIdleCycles++;
                  if (this.socketIdleCycles > 750) {
                     try {
                        this.socket.close();
                     } catch (Exception exception4) {
                     }

                     this.socket = null;
                     this.inputStream = null;
                     this.outputStream = null;
                     this.expectedSize = 0;
                  }
               } else {
                  this.socketIdleCycles = 0;
                  this.statusString = "";
               }

               if (Client.loggedIn && this.socket != null && this.outputStream != null && (this.extraFilePriority > 0 || this.clientInstance.cacheStores[0] == null)) {
                  this.keepAliveCycles++;
                  if (this.keepAliveCycles > 500) {
                     this.keepAliveCycles = 0;
                     this.ioBuffer[0] = 0;
                     this.ioBuffer[1] = 0;
                     this.ioBuffer[2] = 0;
                     this.ioBuffer[3] = 10;

                     try {
                        this.outputStream.write(this.ioBuffer, 0, 4);
                     } catch (IOException exception2) {
                        this.socketIdleCycles = 5000;
                     }
                  }
               }
            }
         } catch (Exception exception3) {
            SignLink.reporterror("od_ex " + exception3.getMessage());
         }

         return;
      }
   }
   public final void queueExtraRequest(int newId, int newDataType) {
      if (this.clientInstance.cacheStores[0] != null) {
         if (this.extraFilePriority != 0) {
            OnDemandRequest onDemandRequest;
            (onDemandRequest = new OnDemandRequest()).dataType = newDataType;
            onDemandRequest.id = newId;
            onDemandRequest.highPriority = false;
            synchronized (this.extraRequests) {
               this.extraRequests.addLast(onDemandRequest);
            }
         }
      }
   }
   public final OnDemandRequest getNextCompletedRequest() {
      OnDemandRequest onDemandRequest;
      synchronized (this.completedRequests) {
         onDemandRequest = (OnDemandRequest)this.completedRequests.removeFirst();
      }

      if (onDemandRequest == null) {
         return null;
      }

      synchronized (this.requestTracking) {
         onDemandRequest.unlinkCacheable();
      }

      if (onDemandRequest.buffer == null) {
         return onDemandRequest;
      }

      int length = 0;

      try {
         GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(onDemandRequest.buffer));

         while (true) {
            if (length == 999999) {
               throw new RuntimeException("buffer overflow!");
            }

            int decodedEntry;
            if ((decodedEntry = gZIPInputStream.read(this.gzipInputBuffer, length, 999999 - length)) == -1) {
               break;
            }

            length += decodedEntry;
         }
      } catch (IOException exception) {
         throw new RuntimeException("error unzipping");
      }

      onDemandRequest.buffer = new byte[length];
      System.arraycopy(this.gzipInputBuffer, 0, onDemandRequest.buffer, 0, length);
      return onDemandRequest;
   }
   public final int getMapFileId(int scalarArgument, int scalarArgument2, int scalarArgument3) {
      scalarArgument2 = (scalarArgument3 << 8) + scalarArgument2;

      for (int loopIndex = 0; loopIndex < this.mapIndices1.length; loopIndex++) {
         if (this.mapIndices1[loopIndex] == scalarArgument2) {
            if (scalarArgument == 0) {
               return this.mapIndices2[loopIndex];
            }

            return this.mapIndices3[loopIndex];
         }
      }

      return -1;
   }
   @Override
   public final void provide(int scalarArgument) {
      this.provide(0, scalarArgument);
   }
   public final void readCacheFile(int scalarArgument, int midiIndex) {
      if (this.clientInstance.cacheStores[0] != null) {
         this.clientInstance.cacheStores[scalarArgument + 1].read(midiIndex);
      }
   }
   public final boolean isLandscapeFile(int id) {
      for (int loopIndex = 0; loopIndex < this.mapIndices1.length; loopIndex++) {
         if (this.mapIndices3[loopIndex] == id) {
            return true;
         }
      }

      return false;
   }
   private void processHighPriorityRequests() {
      this.highPriorityRequestCount = 0;
      this.lowPriorityRequestCount = 0;

      for (OnDemandRequest onDemandRequest = (OnDemandRequest)this.requested.first(); onDemandRequest != null; onDemandRequest = (OnDemandRequest)this.requested.next()) {
         if (onDemandRequest.highPriority) {
            this.highPriorityRequestCount++;
         } else {
            this.lowPriorityRequestCount++;
         }
      }

      OnDemandRequest onDemandRequest2;
      while (this.highPriorityRequestCount < 10 && (onDemandRequest2 = (OnDemandRequest)this.networkRequests.removeFirst()) != null) {
         this.requested.addLast(onDemandRequest2);
         this.highPriorityRequestCount++;
         this.sendRequest(onDemandRequest2);
         this.waiting = true;
      }
   }
   public final void clearExtraRequests() {
      synchronized (this.extraRequests) {
         this.extraRequests.removeAll();
      }
   }
   private void processExtraRequests() {
      while (this.highPriorityRequestCount == 0 && this.lowPriorityRequestCount < 10 && this.extraFilePriority != 0) {
         OnDemandRequest onDemandRequest;
         synchronized (this.extraRequests) {
            onDemandRequest = (OnDemandRequest)this.extraRequests.removeFirst();
         }

         while (onDemandRequest != null) {
            synchronized (this.extraRequests) {
               onDemandRequest = (OnDemandRequest)this.extraRequests.removeFirst();
            }
         }

         this.extraFilePriority--;
      }
   }
   public final boolean shouldPreloadMidi(int midiIndex) {
      return this.midiIndices[midiIndex] == 1;
   }
}
