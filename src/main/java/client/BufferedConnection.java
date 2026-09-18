package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
final class BufferedConnection implements Runnable {
   private InputStream inputStream;
   private OutputStream outputStream;
   private final Socket socket;
   private boolean closed = false;
   private final GameShell gameShell;
   private byte[] buffer;
   private int writeIndex;
   private int bufferIndex;
   private boolean writerRunning = false;
   private boolean ioError = false;

   public BufferedConnection(GameShell newGameShell, Socket newSocket) throws IOException {
      this.gameShell = newGameShell;
      this.socket = newSocket;
      this.socket.setSoTimeout(30000);
      this.socket.setTcpNoDelay(true);
      this.inputStream = this.socket.getInputStream();
      this.outputStream = this.socket.getOutputStream();
   }

   public final void close() {
      this.closed = true;

      try {
         if (this.inputStream != null) {
            this.inputStream.close();
         }

         if (this.outputStream != null) {
            this.outputStream.close();
         }

         if (this.socket != null) {
            this.socket.close();
         }
      } catch (IOException exception) {
         System.out.println("Error closing stream");
      }

      this.writerRunning = false;
      synchronized (this) {
         this.notify();
      }

      this.buffer = null;
   }

   public final int read() throws IOException {
      return this.closed ? 0 : this.inputStream.read();
   }

   public final int available() throws IOException {
      return this.closed ? 0 : this.inputStream.available();
   }
   public final void flushInputStream(byte[] buffer, int pktSize) throws IOException {
      int scalar = 0;
      if (!this.closed) {
         while (pktSize > 0) {
            int localInputStream;
            if ((localInputStream = this.inputStream.read(buffer, scalar, pktSize)) <= 0) {
               throw new IOException("EOF");
            }

            scalar += localInputStream;
            pktSize -= localInputStream;
         }
      }
   }
   public final void queueBytes(int currentPosition, byte[] newBuffer) throws IOException {
      if (!this.closed) {
         if (this.ioError) {
            this.ioError = false;
            throw new IOException("Error in writer thread");
         }

         if (this.buffer == null) {
            this.buffer = new byte[5000];
         }

         synchronized (this) {
            for (int loopIndex = 0; loopIndex < currentPosition; loopIndex++) {
               this.buffer[this.bufferIndex] = newBuffer[loopIndex];
               this.bufferIndex = (this.bufferIndex + 1) % 5000;
               if (this.bufferIndex == (this.writeIndex + 4900) % 5000) {
                  throw new IOException("buffer overflow");
               }
            }

            if (!this.writerRunning) {
               this.writerRunning = true;
               this.gameShell.startRunnable(this, 3);
            }

            this.notify();
         }
      }
   }
   @Override
   public final void run() {
      while (this.writerRunning) {
         int localBufferIndex;
         int writeIndexSucceeded;
         synchronized (this) {
            if (this.bufferIndex == this.writeIndex) {
               try {
                  this.wait();
               } catch (InterruptedException exception) {
               }
            }

            if (!this.writerRunning) {
               return;
            }

            writeIndexSucceeded = this.writeIndex;
            if (this.bufferIndex >= this.writeIndex) {
               localBufferIndex = this.bufferIndex - this.writeIndex;
            } else {
               localBufferIndex = 5000 - this.writeIndex;
            }
         }

         if (localBufferIndex > 0) {
            try {
               this.outputStream.write(this.buffer, writeIndexSucceeded, localBufferIndex);
            } catch (IOException exception3) {
               this.ioError = true;
            }

            this.writeIndex = (this.writeIndex + localBufferIndex) % 5000;

            try {
               if (this.bufferIndex == this.writeIndex) {
                  this.outputStream.flush();
               }
            } catch (IOException exception2) {
               this.ioError = true;
            }
         }
      }
   }
}
