package client;
public final class VarbitDefinition {
   public static VarbitDefinition[] definitions;
   public int index;
   public int leastSignificantBit;
   public int mostSignificantBit;
   private boolean tracksVarp = false;
   public static void load(Archive archive) {
      Buffer buffer = new Buffer(archive.getFile("varbit.dat"));
      if (Client.extendedRevisionEnabled) {
         buffer = new Buffer(archive.getFile("varbit07.dat"));
      }

      if (Client.hdModels) {
         buffer = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/varbit.dat"));
      }

      int readUnsignedShortOrLength = buffer.readUnsignedShort();
      if (definitions == null) {
         definitions = new VarbitDefinition[readUnsignedShortOrLength];
      }

      for (int definitionIndex = 0; definitionIndex < readUnsignedShortOrLength; definitionIndex++) {
         if (definitions[definitionIndex] == null) {
            definitions[definitionIndex] = new VarbitDefinition();
         }

         definitions[definitionIndex].decode(buffer);
         if (definitions[definitionIndex].tracksVarp) {
            ;
         }
      }

      if (buffer.currentPosition != buffer.buffer.length) {
         System.out.println("varbit load mismatch");
      }
   }
   private void decode(Buffer buffer) {
      int decodedUnsignedByte;
      while ((decodedUnsignedByte = buffer.readUnsignedByte()) != 0) {
         if (decodedUnsignedByte == 1) {
            this.index = buffer.readUnsignedShort();
            this.leastSignificantBit = buffer.readUnsignedByte();
            this.mostSignificantBit = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte == 10) {
            buffer.readString();
         } else if (decodedUnsignedByte == 2) {
            this.tracksVarp = true;
         } else if (decodedUnsignedByte == 3) {
            buffer.readInt();
         } else if (decodedUnsignedByte == 4) {
            buffer.readInt();
         } else {
            System.out.println("Error unrecognised config code: " + decodedUnsignedByte);
         }
      }
   }
}
