package client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
public final class SpriteArchive {
   private static SpriteArchive[] spriteEntries;
   public static Sprite[] sprites = null;
   private int id = -1;
   private int xOffset = 0;
   private int yOffset = 0;
   private byte[] imageData = null;
   private static SpriteArchive[] interfaceSpriteEntries;
   public static Sprite[] interfaceSprites = null;
   public static void loadSprites() {
      try {
         Buffer buffer = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "sprites.idx"));
         Buffer buffer2 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "sprites.dat"));
         DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(buffer.buffer)));
         DataInputStream dataInputStream2 = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(buffer2.buffer)));
         int readIntOrLength = dataInputStream.readInt();
         if (spriteEntries == null) {
            spriteEntries = new SpriteArchive[readIntOrLength];
            sprites = new Sprite[readIntOrLength];
         }

         for (int loopIndex = 0; loopIndex < readIntOrLength; loopIndex++) {
            int spriteIndex = dataInputStream.readInt();
            if (spriteEntries[spriteIndex] == null) {
               spriteEntries[spriteIndex] = new SpriteArchive();
            }

            spriteEntries[spriteIndex].readEntry(dataInputStream, dataInputStream2);
            SpriteArchive spriteArchive = spriteEntries[spriteIndex];
            sprites[spriteArchive.id] = new Sprite(spriteArchive.imageData);
            sprites[spriteArchive.id].xOffset = spriteArchive.xOffset;
            sprites[spriteArchive.id].yOffset = spriteArchive.yOffset;
         }

         dataInputStream.close();
         dataInputStream2.close();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
   public static void loadInterfaceSprites() {
      try {
         Buffer buffer = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "interfaceSprites.idx"));
         Buffer buffer2 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "interfaceSprites.dat"));
         DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(buffer.buffer)));
         DataInputStream dataInputStream2 = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(buffer2.buffer)));
         int readIntOrLength = dataInputStream.readInt();
         if (interfaceSpriteEntries == null) {
            interfaceSpriteEntries = new SpriteArchive[readIntOrLength];
            interfaceSprites = new Sprite[readIntOrLength];
         }

         for (int loopIndex = 0; loopIndex < readIntOrLength; loopIndex++) {
            int interfaceSpriteIndex = dataInputStream.readInt();
            if (interfaceSpriteEntries[interfaceSpriteIndex] == null) {
               interfaceSpriteEntries[interfaceSpriteIndex] = new SpriteArchive();
            }

            interfaceSpriteEntries[interfaceSpriteIndex].readEntry(dataInputStream, dataInputStream2);
            SpriteArchive spriteArchive = interfaceSpriteEntries[interfaceSpriteIndex];
            interfaceSprites[spriteArchive.id] = new Sprite(spriteArchive.imageData);
            interfaceSprites[spriteArchive.id].xOffset = spriteArchive.xOffset;
            interfaceSprites[spriteArchive.id].yOffset = spriteArchive.yOffset;
         }

         dataInputStream.close();
         dataInputStream2.close();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
   private void readEntry(DataInputStream dataInputStream, DataInputStream newDataInputStream) throws IOException {
      byte decodedByte;
      while ((decodedByte = newDataInputStream.readByte()) != 0) {
         if (decodedByte == 1) {
            this.id = newDataInputStream.readShort();
         } else if (decodedByte == 2) {
            newDataInputStream.readUTF();
         } else if (decodedByte == 3) {
            this.xOffset = newDataInputStream.readShort();
         } else if (decodedByte == 4) {
            this.yOffset = newDataInputStream.readShort();
         } else if (decodedByte == 5) {
            byte[] sourceImageData = new byte[dataInputStream.readInt()];
            newDataInputStream.readFully(sourceImageData);
            this.imageData = sourceImageData;
         }
      }
   }
}
