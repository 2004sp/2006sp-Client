package client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
public final class GzipDecompressor {
   private static final byte[] decompressionBuffer = new byte[999999];
   private static int fileReadCount = 0;
   public static final byte[] readFile(String text) {
      try {
         int lengthResult;
         byte[] byteBuffer = new byte[lengthResult = (int)new File(text).length()];
         DataInputStream dataInputStream;
         (dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(text)))).readFully(byteBuffer, 0, lengthResult);
         dataInputStream.close();
         fileReadCount++;
         return byteBuffer;
      } catch (Exception exception) {
         return null;
      }
   }
   public static byte[] decompress(byte[] byteBufferArgument) {
      int length = 0;

      try {
         GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(byteBufferArgument));

         while (length != 999999) {
            int decodedEntry;
            if ((decodedEntry = gZIPInputStream.read(decompressionBuffer, length, 999999 - length)) == -1) {
               byte[] byteBuffer = new byte[length];
               System.arraycopy(decompressionBuffer, 0, byteBuffer, 0, length);
               return byteBuffer;
            }

            length += decodedEntry;
         }

         throw new RuntimeException("buffer overflow!");
      } catch (IOException exception) {
         exception.printStackTrace();
         return null;
      }
   }
}
