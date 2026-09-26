package client;
import java.io.IOException;
import java.util.Map;
public final class IdentityKit {
   public static int length;
   public static IdentityKit[] kits;
   private static int hdLength;
   private static IdentityKit[] hdKits;
   public int bodyPartId = -1;
   private int[] bodyModelIds;
   private static int[] recolorToFind = new int[6];
   private static int[] recolorToReplace = new int[6];
   private final int[] headModelIds = new int[]{-1, -1, -1, -1, -1};
   public boolean nonSelectable = false;
   public static void loadRevision443(Cache cache) throws IOException {
      Map<Integer, byte[]> files = cache.readFiles(2, 3);
      int maxId = -1;
      for (Integer id : files.keySet()) maxId = Math.max(maxId, id.intValue());
      IdentityKit[] loaded = new IdentityKit[maxId + 1];
      for (Map.Entry<Integer, byte[]> entry : files.entrySet()) {
         int id = entry.getKey().intValue();
         Buffer buffer = new Buffer(entry.getValue());
         IdentityKit kit = new IdentityKit();
         try {
            int opcode;
            while ((opcode = buffer.readUnsignedByte()) != 0) {
               if (opcode == 1) {
                  kit.bodyPartId = buffer.readUnsignedByte();
               } else if (opcode == 2) {
                  int count = buffer.readUnsignedByte();
                  kit.bodyModelIds = new int[count];
                  for (int i = 0; i < count; i++) kit.bodyModelIds[i] = buffer.readUnsignedShort();
               } else if (opcode == 3) {
                  kit.nonSelectable = true;
               } else if (opcode >= 40 && opcode < 50) {
                  recolorToFind[opcode - 40] = buffer.readUnsignedShort();
               } else if (opcode >= 50 && opcode < 60) {
                  recolorToReplace[opcode - 50] = buffer.readUnsignedShort();
               } else if (opcode >= 60 && opcode < 65) {
                  kit.headModelIds[opcode - 60] = buffer.readUnsignedShort();
               } else {
                  throw new IOException("Unsupported 443 identity kit opcode " + opcode + " for " + id);
               }
            }
         } catch (RuntimeException exception) {
            throw new IOException("Truncated 443 identity kit " + id, exception);
         }
         if (buffer.currentPosition != buffer.buffer.length) {
            throw new IOException("Trailing bytes in 443 identity kit " + id);
         }
         loaded[id] = kit;
      }
      Models models = Models.load(cache);
      models.initializeModelNamespace();
      for (IdentityKit kit : loaded) {
         if (kit == null) continue;
         if (kit.bodyModelIds != null) {
            for (int i = 0; i < kit.bodyModelIds.length; i++) {
               kit.bodyModelIds[i] = models.getRegisteredModelId(kit.bodyModelIds[i]);
            }
         }
         for (int i = 0; i < kit.headModelIds.length; i++) {
            if (kit.headModelIds[i] >= 0) {
               kit.headModelIds[i] = models.getRegisteredModelId(kit.headModelIds[i]);
            }
         }
      }
      kits = loaded;
      length = loaded.length;
   }
   public static void unpackConfig(Archive archive) {
      Buffer buffer;
      length = (buffer = new Buffer(archive.getFile("idk.dat"))).readUnsignedShort();
      if (kits == null) {
         kits = new IdentityKit[length];
      }

      for (int kitIndex = 0; kitIndex < length; kitIndex++) {
         if (kits[kitIndex] == null) {
            kits[kitIndex] = new IdentityKit();
         }

         kits[kitIndex].readValues(buffer);
      }

      if (Client.hdModels) {
         Buffer buffer2;
         hdLength = (buffer2 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/idk.dat"))).readUnsignedShort();
         if (hdKits == null) {
            hdKits = new IdentityKit[hdLength];
         }

         for (int hdKitIndex = 0; hdKitIndex < hdLength; hdKitIndex++) {
            if (hdKits[hdKitIndex] == null) {
               hdKits[hdKitIndex] = new IdentityKit();
            }

            if (hdKitIndex <= 83) {
               kits[hdKitIndex].readValues(buffer2);
            } else {
               hdKits[hdKitIndex].readValues(buffer2);
            }

            recolorToFind[0] = 55232;
            recolorToReplace[0] = 6798;
         }
      }
   }
   private void readValues(Buffer buffer) {
      int decodedUnsignedByte;
      while ((decodedUnsignedByte = buffer.readUnsignedByte()) != 0) {
         if (decodedUnsignedByte == 1) {
            this.bodyPartId = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte == 2) {
            decodedUnsignedByte = buffer.readUnsignedByte();
            this.bodyModelIds = new int[decodedUnsignedByte];

            for (int bodyModelIdIndex = 0; bodyModelIdIndex < decodedUnsignedByte; bodyModelIdIndex++) {
               this.bodyModelIds[bodyModelIdIndex] = buffer.readUnsignedShort();
            }
         } else if (decodedUnsignedByte == 3) {
            this.nonSelectable = true;
         } else if (decodedUnsignedByte >= 40 && decodedUnsignedByte < 50) {
            recolorToFind[decodedUnsignedByte - 40] = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte >= 50 && decodedUnsignedByte < 60) {
            recolorToReplace[decodedUnsignedByte - 50] = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte >= 60 && decodedUnsignedByte < 70) {
            this.headModelIds[decodedUnsignedByte - 60] = buffer.readUnsignedShort();
         } else {
            System.out.println("Error unrecognised config code: " + decodedUnsignedByte);
         }
      }
   }
   public final boolean bodyLoaded() {
      if (this.bodyModelIds == null) {
         return true;
      }

      boolean flag = true;

      for (int bodyModelIdIndex = 0; bodyModelIdIndex < this.bodyModelIds.length; bodyModelIdIndex++) {
         if (!Model.isCached(this.bodyModelIds[bodyModelIdIndex])) {
            flag = false;
         }
      }

      return flag;
   }
   public final Model bodyModel() {
      if (this.bodyModelIds == null) {
         return null;
      }

      Model[] bodyModelIdsLength = new Model[this.bodyModelIds.length];

      for (int bodyModelIdsLengthIndex = 0; bodyModelIdsLengthIndex < this.bodyModelIds.length; bodyModelIdsLengthIndex++) {
         bodyModelIdsLength[bodyModelIdsLengthIndex] = Model.getModel(this.bodyModelIds[bodyModelIdsLengthIndex]);
      }

      Model model;
      if (bodyModelIdsLength.length == 1) {
         model = bodyModelIdsLength[0];
      } else {
         model = new Model(bodyModelIdsLength.length, bodyModelIdsLength);
      }

      for (int recolorToFindIndex = 0; recolorToFindIndex < 6 && recolorToFind[recolorToFindIndex] != 0; recolorToFindIndex++) {
         model.recolor(recolorToFind[recolorToFindIndex], recolorToReplace[recolorToFindIndex]);
      }

      return model;
   }
   public final boolean headLoaded() {
      boolean flag = true;

      for (int headModelIdIndex = 0; headModelIdIndex < 5; headModelIdIndex++) {
         if (this.headModelIds[headModelIdIndex] != -1 && !Model.isCached(this.headModelIds[headModelIdIndex])) {
            flag = false;
         }
      }

      return flag;
   }
   public final Model headModel() {
      Model[] values = new Model[5];
      int scalar = 0;

      for (int headModelIdIndex = 0; headModelIdIndex < 5; headModelIdIndex++) {
         if (this.headModelIds[headModelIdIndex] != -1) {
            values[scalar++] = Model.getModel(this.headModelIds[headModelIdIndex]);
         }
      }

      Model model = new Model(scalar, values);

      for (int recolorToFindIndex = 0; recolorToFindIndex < 6 && recolorToFind[recolorToFindIndex] != 0; recolorToFindIndex++) {
         model.recolor(recolorToFind[recolorToFindIndex], recolorToReplace[recolorToFindIndex]);
      }

      return model;
   }
}
