package client;
public final class SpotAnimationDefinition {
   public static SpotAnimationDefinition[] definitions;
   private int id;
   private int modelId;
   private int animationId = -1;
   public AnimationSequence animationSequence;
   private final int[] recolorToFind = new int[6];
   private final int[] recolorToReplace = new int[6];
   public int resizeX = 128;
   public int resizeY = 128;
   public int rotation;
   public int ambient;
   public int contrast;
   public static LruCache modelCache = new LruCache(30);
   public static void unpackConfig(Archive archive) {
      boolean flag = false;
      if (Client.extendedRevisionEnabled && !Client.hdModels) {
         if (Client.use2007Models) {
            flag = true;
         }

         Buffer buffer;
         int length = (buffer = new Buffer(archive.getFile("spotanim07.dat"))).readUnsignedShort();
         if (definitions == null) {
            definitions = new SpotAnimationDefinition[length];
         }

         for (int sourceId = 0; sourceId < length; sourceId++) {
            if (definitions[sourceId] == null) {
               definitions[sourceId] = new SpotAnimationDefinition();
            }

            definitions[sourceId].id = sourceId;
            SpotAnimationDefinition spotAnimationDefinition = definitions[sourceId];
            Buffer sourceBuffer = buffer;
            SpotAnimationDefinition sourceSpotAnimationDefinition = spotAnimationDefinition;

            int decodedUnsignedByte;
            while ((decodedUnsignedByte = sourceBuffer.readUnsignedByte()) != 0) {
               if (decodedUnsignedByte == 1) {
                  sourceSpotAnimationDefinition.modelId = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 2) {
                  sourceSpotAnimationDefinition.animationId = sourceBuffer.readUnsignedShort();
                  if (AnimationSequence.sequences != null) {
                     sourceSpotAnimationDefinition.animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(sourceSpotAnimationDefinition.animationId)];
                  }
               } else if (decodedUnsignedByte == 4) {
                  sourceSpotAnimationDefinition.resizeX = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 5) {
                  sourceSpotAnimationDefinition.resizeY = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 6) {
                  sourceSpotAnimationDefinition.rotation = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 7) {
                  sourceSpotAnimationDefinition.ambient = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte == 8) {
                  sourceSpotAnimationDefinition.contrast = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte == 40) {
                  decodedUnsignedByte = sourceBuffer.readUnsignedByte();

                  for (int recolorToFindIndex = 0; recolorToFindIndex < decodedUnsignedByte; recolorToFindIndex++) {
                     sourceSpotAnimationDefinition.recolorToFind[recolorToFindIndex] = sourceBuffer.readUnsignedShort();
                     sourceSpotAnimationDefinition.recolorToReplace[recolorToFindIndex] = sourceBuffer.readUnsignedShort();
                  }
               } else if (decodedUnsignedByte == 41) {
                  decodedUnsignedByte = sourceBuffer.readUnsignedByte();

                  for (int loopIndex = 0; loopIndex < decodedUnsignedByte; loopIndex++) {
                     sourceBuffer.readUnsignedShort();
                     sourceBuffer.readUnsignedShort();
                  }
               } else {
                  System.out.println("Error unrecognised spotanim config code: " + decodedUnsignedByte);
               }
            }
         }
      }

      if (!flag) {
         Buffer buffer2 = new Buffer(archive.getFile("spotanim.dat"));
         if (Client.hdModels) {
            buffer2 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/spotanim.dat"));
         }

         int readUnsignedShortOrLength = buffer2.readUnsignedShort();
         if (definitions == null) {
            definitions = new SpotAnimationDefinition[readUnsignedShortOrLength];
         }

         for (int definitionIndex = 0; definitionIndex < readUnsignedShortOrLength; definitionIndex++) {
            if (definitions[definitionIndex] == null || Client.extendedRevisionEnabled) {
               definitions[definitionIndex] = new SpotAnimationDefinition();
            }

            definitions[definitionIndex].id = definitionIndex;
            if (Client.hdModels) {
               definitions[definitionIndex].readHdValues(buffer2);
            } else {
               definitions[definitionIndex].readLegacyValues(buffer2);
            }
         }
      }
   }
   private void readHdValues(Buffer buffer) {
      int decodedUnsignedByte;
      while ((decodedUnsignedByte = buffer.readUnsignedByte()) != 0) {
         if (decodedUnsignedByte == 1) {
            this.modelId = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 2) {
            this.animationId = buffer.readUnsignedShort();
            if (AnimationSequence.sequences != null) {
               this.animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(this.animationId)];
            }
         } else if (decodedUnsignedByte == 4) {
            this.resizeX = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 5) {
            this.resizeY = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 6) {
            this.rotation = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 7) {
            this.ambient = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte == 8) {
            this.contrast = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte == 41) {
            decodedUnsignedByte = buffer.readUnsignedByte();

            for (int loopIndex = 0; loopIndex < decodedUnsignedByte; loopIndex++) {
               buffer.readUnsignedShort();
               buffer.readUnsignedShort();
            }
         } else if (decodedUnsignedByte == 40) {
            decodedUnsignedByte = buffer.readUnsignedByte();

            for (int recolorToFindIndex = 0; recolorToFindIndex < decodedUnsignedByte; recolorToFindIndex++) {
               this.recolorToFind[recolorToFindIndex] = buffer.readUnsignedShort();
               this.recolorToReplace[recolorToFindIndex] = buffer.readUnsignedShort();
            }
         } else {
            System.out.println("Error unrecognised spotanim config code: " + decodedUnsignedByte);
         }
      }
   }
   private void readLegacyValues(Buffer buffer) {
      int decodedUnsignedByte;
      while ((decodedUnsignedByte = buffer.readUnsignedByte()) != 0) {
         if (decodedUnsignedByte == 1) {
            this.modelId = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 2) {
            this.animationId = buffer.readUnsignedShort();
            if (AnimationSequence.sequences != null) {
               this.animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(this.animationId)];
            }
         } else if (decodedUnsignedByte == 4) {
            this.resizeX = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 5) {
            this.resizeY = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 6) {
            this.rotation = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte == 7) {
            this.ambient = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte == 8) {
            this.contrast = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte >= 40 && decodedUnsignedByte < 50) {
            this.recolorToFind[decodedUnsignedByte - 40] = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte >= 50 && decodedUnsignedByte < 60) {
            this.recolorToReplace[decodedUnsignedByte - 50] = buffer.readUnsignedShort();
         } else {
            System.out.println("Error unrecognised spotanim config code: " + decodedUnsignedByte);
         }
      }
   }
   public final Model getModel() {
      Model model;
      if ((model = (Model)modelCache.get(this.id)) != null) {
         return model;
      }

      if ((model = Model.getModel(this.modelId)) == null) {
         return null;
      }

      for (int recolorToFindIndex = 0; recolorToFindIndex < 6; recolorToFindIndex++) {
         if (this.recolorToFind[0] != 0) {
            model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
         }
      }

      modelCache.put(model, this.id);
      return model;
   }
}
