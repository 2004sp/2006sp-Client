package client;
import java.io.IOException;
import java.util.Map;
public final class VarpDefinition {
   public static VarpDefinition[] definitions;
   private static int trackedVarpCount;
   private static int[] trackedVarpIds;
   public int type;
   public static void loadRevision443(Cache cache) throws IOException {
      Map<Integer, byte[]> files = cache.readFiles(2, 16);
      int maxId = -1;
      for (Integer id : files.keySet()) maxId = Math.max(maxId, id.intValue());
      VarpDefinition[] loaded = new VarpDefinition[maxId + 1];
      for (Map.Entry<Integer, byte[]> entry : files.entrySet()) {
         int id = entry.getKey().intValue();
         Buffer buffer = new Buffer(entry.getValue());
         VarpDefinition definition = new VarpDefinition();
         try {
            int opcode;
            while ((opcode = buffer.readUnsignedByte()) != 0) {
               if (opcode == 5) {
                  definition.type = buffer.readUnsignedShort();
               } else {
                  throw new IOException("Unsupported 443 varp opcode " + opcode + " for " + id);
               }
            }
         } catch (RuntimeException exception) {
            throw new IOException("Truncated 443 varp " + id, exception);
         }
         if (buffer.currentPosition != buffer.buffer.length) {
            throw new IOException("Trailing bytes in 443 varp " + id);
         }
         loaded[id] = definition;
      }
      definitions = loaded;
   }
   public static void load(Archive archive) {
      Buffer buffer = new Buffer(archive.getFile("varp.dat"));
      if (Client.extendedRevisionEnabled) {
         buffer = new Buffer(archive.getFile("varp07.dat"));
      }

      trackedVarpCount = 0;
      int readUnsignedShortOrLength = buffer.readUnsignedShort();
      if (definitions == null) {
         definitions = new VarpDefinition[readUnsignedShortOrLength];
      }

      if (trackedVarpIds == null) {
         trackedVarpIds = new int[readUnsignedShortOrLength];
      }

      for (int definitionIndex = 0; definitionIndex < readUnsignedShortOrLength; definitionIndex++) {
         if (definitions[definitionIndex] == null) {
            definitions[definitionIndex] = new VarpDefinition();
         }

         VarpDefinition varpDefinition = definitions[definitionIndex];
         int trackedVarpId = definitionIndex;
         Buffer buffer2 = buffer;
         VarpDefinition sourceVarpDefinition = varpDefinition;

         int decodedUnsignedByte;
         while ((decodedUnsignedByte = buffer2.readUnsignedByte()) != 0) {
            if (decodedUnsignedByte == 1) {
               buffer2.readUnsignedByte();
            } else if (decodedUnsignedByte == 2) {
               buffer2.readUnsignedByte();
            } else if (decodedUnsignedByte == 3) {
               trackedVarpIds[trackedVarpCount++] = trackedVarpId;
            } else if (decodedUnsignedByte != 4) {
               if (decodedUnsignedByte == 5) {
                  sourceVarpDefinition.type = buffer2.readUnsignedShort();
               } else if (decodedUnsignedByte != 6) {
                  if (decodedUnsignedByte == 7) {
                     buffer2.readInt();
                  } else if (decodedUnsignedByte != 8) {
                     if (decodedUnsignedByte == 10) {
                        buffer2.readString();
                     } else if (decodedUnsignedByte != 11) {
                        if (decodedUnsignedByte == 12) {
                           buffer2.readInt();
                        } else if (decodedUnsignedByte != 13) {
                           System.out.println("Error unrecognised config code: " + decodedUnsignedByte);
                        }
                     }
                  }
               }
            }
         }
      }

      if (buffer.currentPosition != buffer.buffer.length) {
         System.out.println("varptype load mismatch");
      }
   }
}
