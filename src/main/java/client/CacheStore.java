package client;

import java.io.IOException;
import java.io.RandomAccessFile;
final class CacheStore {
   private static final byte[] buffer = new byte[520];
   private final RandomAccessFile dataFile;
   private final RandomAccessFile indexFile;
   private final int indexId;

   public CacheStore(RandomAccessFile randomAccessFile, RandomAccessFile newRandomAccessFile, int newIndexId) {
      this.indexId = newIndexId;
      this.dataFile = randomAccessFile;
      this.indexFile = newRandomAccessFile;
   }
   public final synchronized byte[] read(int param1) {
      try {
         this.seek(this.indexFile, param1 * 6);
         int bytesRead = 0;

         while (bytesRead < 6) {
            int count = this.indexFile.read(buffer, bytesRead, 6 - bytesRead);
            if (count == -1) {
               return null;
            }
            bytesRead += count;
         }

         int length = ((buffer[0] & 255) << 16) + ((buffer[1] & 255) << 8) + (buffer[2] & 255);
         int sector = ((buffer[3] & 255) << 16) + ((buffer[4] & 255) << 8) + (buffer[5] & 255);
         if (sector <= 0 || sector > this.dataFile.length() / 520L) {
            return null;
         }

         byte[] byteBuffer = new byte[length];
         int offset = 0;
         int chunk = 0;

         while (offset < length) {
            if (sector == 0) {
               return null;
            }

            this.seek(this.dataFile, sector * 520);
            int payloadLength = length - offset;
            if (payloadLength > 512) {
               payloadLength = 512;
            }

            int blockBytesRead = 0;
            while (blockBytesRead < payloadLength + 8) {
               int count = this.dataFile.read(buffer, blockBytesRead, payloadLength + 8 - blockBytesRead);
               if (count == -1) {
                  return null;
               }
               blockBytesRead += count;
            }

            int fileId = ((buffer[0] & 255) << 8) + (buffer[1] & 255);
            int chunkId = ((buffer[2] & 255) << 8) + (buffer[3] & 255);
            int nextSector = ((buffer[4] & 255) << 16) + ((buffer[5] & 255) << 8) + (buffer[6] & 255);
            int indexId = buffer[7] & 255;
            if (fileId != param1 || chunkId != chunk || indexId != this.indexId) {
               return null;
            }

            if (nextSector < 0 || nextSector > this.dataFile.length() / 520L) {
               return null;
            }

            for (int i = 0; i < payloadLength; i++) {
               byteBuffer[offset++] = buffer[i + 8];
            }

            sector = nextSector;
            chunk++;
         }

         return byteBuffer;
      } catch (IOException ignored) {
         return null;
      }

   }
   public final synchronized boolean write(int length, byte[] byteBufferArgument, int id) {
      boolean writeInternalSucceeded;
      if (!(writeInternalSucceeded = this.writeInternal(true, id, length, byteBufferArgument))) {
         writeInternalSucceeded = this.writeInternal(false, id, length, byteBufferArgument);
      }

      return writeInternalSucceeded;
   }
   private synchronized boolean writeInternal(boolean flag, int id, int length, byte[] byteBufferArgument) {
      try {
         int dataFileLength;
         if (!flag) {
            if ((dataFileLength = (int)((this.dataFile.length() + 519L) / 520L)) == 0) {
               dataFileLength = 1;
            }
         } else {
            this.seek(this.indexFile, id * 6);
            int scalar = 0;

            while (scalar < 6) {
               int position;
               if ((position = this.indexFile.read(buffer, scalar, 6 - scalar)) == -1) {
                  return false;
               }

               scalar += position;
            }

            if ((dataFileLength = ((buffer[3] & 255) << 16) + ((buffer[4] & 255) << 8) + (buffer[5] & 255)) <= 0 || dataFileLength > this.dataFile.length() / 520L) {
               return false;
            }
         }

         buffer[0] = (byte)(length >> 16);
         buffer[1] = (byte)(length >> 8);
         buffer[2] = (byte)length;
         buffer[3] = (byte)(dataFileLength >> 16);
         buffer[4] = (byte)(dataFileLength >> 8);
         buffer[5] = (byte)dataFileLength;
         this.seek(this.indexFile, id * 6);
         this.indexFile.write(buffer, 0, 6);
         int loopIndex = 0;

         for (int position2 = 0; loopIndex < length; position2++) {
            int payload = 0;
            if (flag) {
               this.seek(this.dataFile, dataFileLength * 520);
               int scalar2 = 0;

               int payload2;
               while (scalar2 < 8 && (payload2 = this.dataFile.read(buffer, scalar2, 8 - scalar2)) != -1) {
                  scalar2 += payload2;
               }

               if (scalar2 == 8) {
                  scalar2 = ((buffer[0] & 255) << 8) + (buffer[1] & 255);
                  payload2 = ((buffer[2] & 255) << 8) + (buffer[3] & 255);
                  payload = ((buffer[4] & 255) << 16) + ((buffer[5] & 255) << 8) + (buffer[6] & 255);
                  int localBuffer = buffer[7] & 255;
                  if (scalar2 != id || payload2 != position2 || localBuffer != this.indexId) {
                     return false;
                  }

                  if (payload < 0 || payload > this.dataFile.length() / 520L) {
                     return false;
                  }
               }
            }

            if (payload == 0) {
               flag = false;
               if ((payload = (int)((this.dataFile.length() + 519L) / 520L)) == 0) {
                  payload++;
               }

               if (payload == dataFileLength) {
                  payload++;
               }
            }

            if (length - loopIndex <= 512) {
               payload = 0;
            }

            buffer[0] = (byte)(id >> 8);
            buffer[1] = (byte)id;
            buffer[2] = (byte)(position2 >> 8);
            buffer[3] = (byte)position2;
            buffer[4] = (byte)(payload >> 16);
            buffer[5] = (byte)(payload >> 8);
            buffer[6] = (byte)payload;
            buffer[7] = (byte)this.indexId;
            this.seek(this.dataFile, dataFileLength * 520);
            this.dataFile.write(buffer, 0, 8);
            int scalar3;
            if ((scalar3 = length - loopIndex) > 512) {
               scalar3 = 512;
            }

            this.dataFile.write(byteBufferArgument, loopIndex, scalar3);
            loopIndex += scalar3;
            dataFileLength = payload;
         }

         return true;
      } catch (IOException exception) {
         return false;
      }
   }
   private synchronized void seek(RandomAccessFile randomAccessFile, int scalarArgument) throws IOException {
      randomAccessFile.seek(scalarArgument);
   }
}
