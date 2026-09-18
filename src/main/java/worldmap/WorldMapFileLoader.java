package worldmap;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
public final class WorldMapFileLoader {
   private static int loadedFileCount = 0;
   public static final byte[] readFile(String text) {
      try {
         int lengthResult;
         byte[] byteBuffer = new byte[lengthResult = (int)new File(text).length()];
         DataInputStream dataInputStream;
         (dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(text)))).readFully(byteBuffer, 0, lengthResult);
         dataInputStream.close();
         loadedFileCount++;
         return byteBuffer;
      } catch (Exception exception) {
         System.out.println("Read Error: " + text);
         return null;
      }
   }
}
