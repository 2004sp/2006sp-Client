package client;
public final class ObjectDefinition {
   public boolean obstructsGround;
   private byte ambient;
   private int offsetX;
   public String name;
   private int modelSizeY;
   private static final Model[] modelPartsScratch = new Model[4];
   private byte contrast;
   public int sizeX;
   private int offsetHeight;
   public int icon;
   private int[] recolorToReplace;
   private int modelSizeX;
   public int configId;
   private boolean rotated;
   public static boolean lowMemory;
   private static Buffer definitionData;
   private static Buffer hdDefinitionData;
   private static Buffer revisionDefinitionData;
   public int type = -1;
   private static int[] definitionOffsets;
   private static int[] hdDefinitionOffsets;
   private static int[] revisionDefinitionOffsets;
   public boolean walkable;
   public int mapSceneId;
   public int[] childIds;
   private int supportsItems;
   public int sizeY;
   public boolean adjustToTerrain;
   public boolean wall;
   public static Client clientInstance;
   private boolean hollow;
   public boolean solid;
   public int blockingMask;
   private boolean nonFlatShading;
   private static int cacheIndex;
   private int modelSizeHeight;
   private int[] objectModels;
   public int varbitId;
   public int decorDisplacement;
   private int[] objectTypes;
   public byte[] description;
   public boolean hasActions;
   public boolean castsShadow;
   public static LruCache modelCache = new LruCache(30);
   public int animationId;
   private static ObjectDefinition[] recentDefinitions;
   private int offsetY;
   private int[] recolorToFind;
   public static LruCache rawModelCache = new LruCache(500);
   public String[] actions;
   public static ObjectDefinition lookup(int sourceType) {
      for (int recentDefinitionIndex = 0; recentDefinitionIndex < 20; recentDefinitionIndex++) {
         if (recentDefinitions[recentDefinitionIndex].type == sourceType) {
            return recentDefinitions[recentDefinitionIndex];
         }
      }

      cacheIndex = (cacheIndex + 1) % 20;
      ObjectDefinition objectDefinition;
      (objectDefinition = recentDefinitions[cacheIndex]).type = sourceType;
      ObjectDefinition sourceObjectDefinition = objectDefinition;
      objectDefinition.objectModels = null;
      sourceObjectDefinition.objectTypes = null;
      sourceObjectDefinition.name = null;
      sourceObjectDefinition.description = null;
      sourceObjectDefinition.recolorToFind = null;
      sourceObjectDefinition.recolorToReplace = null;
      sourceObjectDefinition.sizeX = 1;
      sourceObjectDefinition.sizeY = 1;
      sourceObjectDefinition.solid = true;
      sourceObjectDefinition.walkable = true;
      sourceObjectDefinition.hasActions = false;
      sourceObjectDefinition.adjustToTerrain = false;
      sourceObjectDefinition.nonFlatShading = false;
      sourceObjectDefinition.wall = false;
      sourceObjectDefinition.animationId = -1;
      sourceObjectDefinition.decorDisplacement = 16;
      sourceObjectDefinition.ambient = 0;
      sourceObjectDefinition.contrast = 0;
      sourceObjectDefinition.actions = null;
      sourceObjectDefinition.icon = -1;
      sourceObjectDefinition.mapSceneId = -1;
      sourceObjectDefinition.rotated = false;
      sourceObjectDefinition.castsShadow = true;
      sourceObjectDefinition.modelSizeX = 128;
      sourceObjectDefinition.modelSizeHeight = 128;
      sourceObjectDefinition.modelSizeY = 128;
      sourceObjectDefinition.blockingMask = 0;
      sourceObjectDefinition.offsetX = 0;
      sourceObjectDefinition.offsetHeight = 0;
      sourceObjectDefinition.offsetY = 0;
      sourceObjectDefinition.obstructsGround = false;
      sourceObjectDefinition.hollow = false;
      sourceObjectDefinition.supportsItems = -1;
      sourceObjectDefinition.varbitId = -1;
      sourceObjectDefinition.configId = -1;
      sourceObjectDefinition.childIds = null;
      if (Client.hdModels
         && sourceType != 1816
         && sourceType != 3480
         && sourceType != 3478
         && sourceType != 2066
         && sourceType != 2618
         && sourceType != 2585
         && sourceType != 2715
         && sourceType != 12765
         && sourceType != 3443
         && sourceType != 2607
         && sourceType != 2608
         && sourceType != 2609
         && sourceType != 2610
         && sourceType != 9358
         && sourceType != 2416
         && sourceType != 2417
         && sourceType != 2143
         && sourceType != 2144
         && sourceType != 9294
         && sourceType != 9293
         && sourceType != 2638
         && sourceType != 2781
         && sourceType != 2503
         && sourceType != 2504
         && sourceType != 2505
         && sourceType != 2506
         && sourceType != 2507
         && sourceType != 9739
         && sourceType != 9740
         && sourceType != 12137
         && sourceType != 12138
         && sourceType != 12139
         && sourceType != 24
         && sourceType != 1512
         && sourceType != 1533
         && sourceType != 1534
         && sourceType != 1535
         && sourceType != 2025
         && sourceType != 2586
         && sourceType != 3489
         && sourceType != 3490
         && sourceType != 3743
         && sourceType != 6916
         && sourceType != 2402
         && sourceType != 14879
         && sourceType != 2079
         && sourceType != 2080
         && sourceType != 6279
         && sourceType != 2311
         && sourceType != 15
         && sourceType != 16
         && sourceType != 17
         && sourceType != 18
         && sourceType != 19
         && sourceType != 20
         && sourceType != 9328
         && sourceType != 9329
         && sourceType != 9330) {
         hdDefinitionData.currentPosition = hdDefinitionOffsets[sourceType];
         objectDefinition.readValues(hdDefinitionData, sourceType);
      } else if (Client.extendedRevisionEnabled && sourceType > 14973) {
         revisionDefinitionData.currentPosition = revisionDefinitionOffsets[sourceType];
         objectDefinition.readValues(revisionDefinitionData, sourceType);
      } else {
         definitionData.currentPosition = definitionOffsets[sourceType];
         objectDefinition.readValues(definitionData, sourceType);
      }

      return objectDefinition;
   }
   public final void loadModels(OnDemandFetcher onDemandFetcher) {
      if (this.objectModels != null) {
         int[] objectModels = this.objectModels;
         int objectModelsLengthOrObjectModels = this.objectModels.length;

         for (int objectModelIndex = 0; objectModelIndex < objectModelsLengthOrObjectModels; objectModelIndex++) {
            int objectModel = objectModels[objectModelIndex];
            onDemandFetcher.queueExtraRequest(objectModel & 65535, 0);
         }
      }
   }
   public static void nullLoader() {
      rawModelCache = null;
      modelCache = null;
      definitionOffsets = null;
      hdDefinitionOffsets = null;
      recentDefinitions = null;
      definitionData = null;
      hdDefinitionData = null;
   }
   public static void unpackConfig(Archive archive) {
      definitionData = new Buffer(archive.getFile("loc.dat"));
      Buffer buffer = new Buffer(archive.getFile("loc.idx"));
      if (Client.hdModels) {
         hdDefinitionData = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/loc.dat"));
         Buffer buffer3;
         int decodedUnsignedShort2;
         hdDefinitionOffsets = new int[decodedUnsignedShort2 = (buffer3 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/loc.idx"))).readUnsignedShort()];
         int hdDefinitionOffset = 2;

         for (int hdDefinitionOffsetIndex = 0; hdDefinitionOffsetIndex < decodedUnsignedShort2; hdDefinitionOffsetIndex++) {
            hdDefinitionOffsets[hdDefinitionOffsetIndex] = hdDefinitionOffset;
            hdDefinitionOffset += buffer3.readUnsignedShort();
         }
      }

      if (Client.extendedRevisionEnabled) {
         revisionDefinitionData = new Buffer(archive.getFile("loc07.dat"));
         Buffer buffer2;
         int decodedUnsignedShort3;
         revisionDefinitionOffsets = new int[decodedUnsignedShort3 = (buffer2 = new Buffer(archive.getFile("loc07.idx"))).readUnsignedShort()];
         int revisionDefinitionOffset = 2;

         for (int revisionDefinitionOffsetIndex = 0; revisionDefinitionOffsetIndex < decodedUnsignedShort3; revisionDefinitionOffsetIndex++) {
            revisionDefinitionOffsets[revisionDefinitionOffsetIndex] = revisionDefinitionOffset;
            revisionDefinitionOffset += buffer2.readUnsignedShort();
         }
      }

      int decodedUnsignedShort;
      definitionOffsets = new int[decodedUnsignedShort = buffer.readUnsignedShort()];
      int definitionOffset = 2;

      for (int definitionOffsetIndex = 0; definitionOffsetIndex < decodedUnsignedShort; definitionOffsetIndex++) {
         definitionOffsets[definitionOffsetIndex] = definitionOffset;
         definitionOffset += buffer.readUnsignedShort();
      }

      recentDefinitions = new ObjectDefinition[20];

      for (int recentDefinitionIndex = 0; recentDefinitionIndex < 20; recentDefinitionIndex++) {
         recentDefinitions[recentDefinitionIndex] = new ObjectDefinition();
      }
   }
   public final boolean areModelsReadyForType(int scalarArgument) {
      if (this.objectTypes != null) {
         for (int objectTypeIndex = 0; objectTypeIndex < this.objectTypes.length; objectTypeIndex++) {
            if (this.objectTypes[objectTypeIndex] == scalarArgument) {
               return Model.isCached(this.objectModels[objectTypeIndex] & 65535);
            }
         }

         return true;
      } else {
         if (this.objectModels == null) {
            return true;
         }

         if (scalarArgument != 10) {
            return true;
         }

         boolean flag = true;
         int[] objectModels = this.objectModels;
         int objectModelsLengthOrObjectModels = this.objectModels.length;

         for (int objectModelIndex = 0; objectModelIndex < objectModelsLengthOrObjectModels; objectModelIndex++) {
            int objectModel = objectModels[objectModelIndex];
            flag &= Model.isCached(objectModel & 65535);
         }

         return flag;
      }
   }
   public final Model modelAt(int objectType, int wallFlagIndex, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4, int frameIndex, int verticesXEntry, int verticesZEntry, int frameLength) {
      Model model;
      buildObjectModel: {
         int graphicFrameCycle = frameLength;
         frameLength = verticesZEntry;
         verticesZEntry = verticesXEntry;
         verticesXEntry = wallFlagIndex;
         wallFlagIndex = objectType;
         ObjectDefinition objectDefinition = this;
         Model node = null;
         long key;
         if (objectDefinition.objectTypes == null) {
            if (wallFlagIndex != 10) {
               model = null;
               break buildObjectModel;
            }

            key = (objectDefinition.type << 6) + verticesXEntry + ((long)(frameIndex + 1) << 32);
            Model model2;
            if ((model2 = (Model)modelCache.get(key)) != null) {
               model = model2;
               break buildObjectModel;
            }

            if (objectDefinition.objectModels == null) {
               model = null;
               break buildObjectModel;
            }

            boolean localRotated = objectDefinition.rotated ^ verticesXEntry > 3;
            int objectModelsLengthOrObjectModels = objectDefinition.objectModels.length;

            for (int objectModelIndex2 = 0; objectModelIndex2 < objectModelsLengthOrObjectModels; objectModelIndex2++) {
               int objectModel = objectDefinition.objectModels[objectModelIndex2];
               if (localRotated) {
                  objectModel += 65536;
               }

               if ((node = (Model)rawModelCache.get(objectModel)) == null) {
                  if ((node = Model.getModel(objectModel & 65535)) == null) {
                     model = null;
                     break buildObjectModel;
                  }

                  if (localRotated) {
                     node.mirror();
                  }

                  rawModelCache.put(node, objectModel);
               }

               if (objectModelsLengthOrObjectModels > 1) {
                  modelPartsScratch[objectModelIndex2] = node;
               }
            }

            if (objectModelsLengthOrObjectModels > 1) {
               node = new Model(objectModelsLengthOrObjectModels, modelPartsScratch);
            }
         } else {
            int objectModelIndex = -1;

            for (int objectTypeIndex = 0; objectTypeIndex < objectDefinition.objectTypes.length; objectTypeIndex++) {
               if (objectDefinition.objectTypes[objectTypeIndex] == wallFlagIndex) {
                  objectModelIndex = objectTypeIndex;
                  break;
               }
            }

            if (objectModelIndex == -1) {
               model = null;
               break buildObjectModel;
            }

            key = (objectDefinition.type << 6) + (objectModelIndex << 3) + verticesXEntry + ((long)(frameIndex + 1) << 32);
            Model model3;
            if ((model3 = (Model)modelCache.get(key)) != null) {
               model = model3;
               break buildObjectModel;
            }

            objectModelIndex = objectDefinition.objectModels[objectModelIndex];
            boolean rotated2;
            if (rotated2 = objectDefinition.rotated ^ verticesXEntry > 3) {
               objectModelIndex += 65536;
            }

            if ((node = (Model)rawModelCache.get(objectModelIndex)) == null) {
               if ((node = Model.getModel(objectModelIndex & 65535)) == null) {
                  model = null;
                  break buildObjectModel;
               }

               if (rotated2) {
                  node.mirror();
               }

               rawModelCache.put(node, objectModelIndex);
            }
         }

         boolean localModelSizeX = objectDefinition.modelSizeX != 128 || objectDefinition.modelSizeHeight != 128 || objectDefinition.modelSizeY != 128;
         boolean localOffsetX = objectDefinition.offsetX != 0 || objectDefinition.offsetHeight != 0 || objectDefinition.offsetY != 0;
         Model model4 = new Model(objectDefinition.recolorToFind == null, AnimationFrame.isNullFrame(frameIndex), verticesXEntry == 0 && frameIndex == -1 && !localModelSizeX && !localOffsetX, node);
         if (frameIndex != -1) {
            model4.skin();
            if (Client.smoothAnimations && verticesZEntry != -1) {
               model4.applyInterpolatedAnimation(frameIndex, verticesZEntry, graphicFrameCycle, frameLength);
            } else {
               model4.applyAnimationFrame(frameIndex);
            }

            model4.triangleSkin = null;
            model4.vectorSkin = null;
         }

         while (verticesXEntry-- > 0) {
            model4.rotateY90();
         }

         if (objectDefinition.recolorToFind != null) {
            for (int recolorToFindIndex = 0; recolorToFindIndex < objectDefinition.recolorToFind.length; recolorToFindIndex++) {
               model4.recolor(objectDefinition.recolorToFind[recolorToFindIndex], objectDefinition.recolorToReplace[recolorToFindIndex]);
            }
         }

         if (localModelSizeX) {
            model4.scale(objectDefinition.modelSizeX, objectDefinition.modelSizeY, objectDefinition.modelSizeHeight);
         }

         if (localOffsetX) {
            model4.translate(objectDefinition.offsetX, objectDefinition.offsetHeight, objectDefinition.offsetY);
         }

         model4.light(64 + objectDefinition.ambient, 768 + objectDefinition.contrast * 5, -50, -10, -50, !objectDefinition.nonFlatShading);
         if (objectDefinition.supportsItems == 1) {
            model4.sceneHeightOffset = model4.modelHeight;
         }

         modelCache.put(model4, key);
         model = model4;
      }

      Model model5 = model;
      if (model == null) {
         return null;
      }

      if (this.adjustToTerrain || this.nonFlatShading) {
         model5 = new Model(this.adjustToTerrain, this.nonFlatShading, model5);
      }

      if (this.adjustToTerrain) {
         wallFlagIndex = (scalarArgument + scalarArgument2 + scalarArgument3 + scalarArgument4) / 4;

         for (int verticesXIndex = 0; verticesXIndex < model5.vertexCount; verticesXIndex++) {
            verticesXEntry = model5.verticesX[verticesXIndex];
            verticesZEntry = model5.verticesZ[verticesXIndex];
            frameLength = scalarArgument + (scalarArgument2 - scalarArgument) * (verticesXEntry + 64) / 128;
            verticesXEntry = scalarArgument4 + (scalarArgument3 - scalarArgument4) * (verticesXEntry + 64) / 128;
            verticesXEntry = frameLength + (verticesXEntry - frameLength) * (verticesZEntry + 64) / 128;
            model5.verticesY[verticesXIndex] = model5.verticesY[verticesXIndex] + (verticesXEntry - wallFlagIndex);
         }

         model5.computeSphericalBounds();
      }

      return model5;
   }
   public final boolean areModelsReady() {
      if (this.objectModels == null) {
         return true;
      }

      boolean flag = true;
      int[] objectModels = this.objectModels;
      int objectModelsLengthOrObjectModels = this.objectModels.length;

      for (int objectModelIndex = 0; objectModelIndex < objectModelsLengthOrObjectModels; objectModelIndex++) {
         int objectModel = objectModels[objectModelIndex];
         flag &= Model.isCached(objectModel & 65535);
      }

      return flag;
   }
   private void readValues(Buffer buffer, int scalarArgument) {
      int decodedUnsignedByte = -1;

      int decodedUnsignedByte2;
      while ((decodedUnsignedByte2 = buffer.readUnsignedByte()) != 0) {
         if (decodedUnsignedByte2 == 1) {
            if ((decodedUnsignedByte2 = buffer.readUnsignedByte()) > 0) {
               if (this.objectModels != null) {
                  buffer.currentPosition += decodedUnsignedByte2 * 3;
               } else {
                  this.objectTypes = new int[decodedUnsignedByte2];
                  this.objectModels = new int[decodedUnsignedByte2];

                  for (int objectModelIndex = 0; objectModelIndex < decodedUnsignedByte2; objectModelIndex++) {
                     this.objectModels[objectModelIndex] = buffer.readUnsignedShort();
                     this.objectTypes[objectModelIndex] = buffer.readUnsignedByte();
                  }
               }
            }
         } else if (decodedUnsignedByte2 == 2) {
            this.name = buffer.readString();
         } else if (decodedUnsignedByte2 == 3) {
            this.description = buffer.readBytes();
         } else if (decodedUnsignedByte2 == 5) {
            if ((decodedUnsignedByte2 = buffer.readUnsignedByte()) > 0) {
               if (this.objectModels != null) {
                  buffer.currentPosition += decodedUnsignedByte2 << 1;
               } else {
                  this.objectTypes = null;
                  this.objectModels = new int[decodedUnsignedByte2];

                  for (int objectModelIndex2 = 0; objectModelIndex2 < decodedUnsignedByte2; objectModelIndex2++) {
                     this.objectModels[objectModelIndex2] = buffer.readUnsignedShort();
                  }
               }
            }
         } else if (decodedUnsignedByte2 == 14) {
            this.sizeX = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte2 == 15) {
            this.sizeY = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte2 == 17) {
            this.solid = false;
         } else if (decodedUnsignedByte2 == 18) {
            this.walkable = false;
         } else if (decodedUnsignedByte2 == 19) {
            if ((decodedUnsignedByte = buffer.readUnsignedByte()) == 1) {
               this.hasActions = true;
            }
         } else if (decodedUnsignedByte2 == 21) {
            this.adjustToTerrain = true;
         } else if (decodedUnsignedByte2 == 22) {
            this.nonFlatShading = true;
            if (scalarArgument == 26340 || scalarArgument == 26341 || scalarArgument == 26420 || scalarArgument == 26421 || scalarArgument == 26422 || scalarArgument == 26302 || scalarArgument == 734) {
               this.nonFlatShading = false;
            }
         } else if (decodedUnsignedByte2 == 23) {
            this.wall = true;
         } else if (decodedUnsignedByte2 == 24) {
            this.animationId = buffer.readUnsignedShort();
            if (this.animationId == 65535) {
               this.animationId = -1;
            }
         } else if (decodedUnsignedByte2 == 28) {
            this.decorDisplacement = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte2 == 29) {
            this.ambient = buffer.readByte();
         } else if (decodedUnsignedByte2 == 39) {
            this.contrast = buffer.readByte();
         } else if (decodedUnsignedByte2 >= 30 && decodedUnsignedByte2 < 35) {
            if (this.actions == null) {
               this.actions = new String[5];
            }

            this.actions[decodedUnsignedByte2 - 30] = buffer.readString();
            if (this.actions[decodedUnsignedByte2 - 30].equalsIgnoreCase("hidden")) {
               this.actions[decodedUnsignedByte2 - 30] = null;
            }
         } else if (decodedUnsignedByte2 == 40) {
            decodedUnsignedByte2 = buffer.readUnsignedByte();
            this.recolorToFind = new int[decodedUnsignedByte2];
            this.recolorToReplace = new int[decodedUnsignedByte2];

            for (int recolorToFindIndex = 0; recolorToFindIndex < decodedUnsignedByte2; recolorToFindIndex++) {
               this.recolorToFind[recolorToFindIndex] = buffer.readUnsignedShort();
               this.recolorToReplace[recolorToFindIndex] = buffer.readUnsignedShort();
            }
         } else if (decodedUnsignedByte2 == 60) {
            this.icon = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 62) {
            this.rotated = true;
         } else if (decodedUnsignedByte2 == 64) {
            this.castsShadow = false;
         } else if (decodedUnsignedByte2 == 65) {
            this.modelSizeX = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 66) {
            this.modelSizeHeight = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 67) {
            this.modelSizeY = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 68) {
            this.mapSceneId = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 69) {
            this.blockingMask = buffer.readUnsignedByte();
         } else if (decodedUnsignedByte2 == 70) {
            this.offsetX = buffer.readShort();
         } else if (decodedUnsignedByte2 == 71) {
            this.offsetHeight = buffer.readShort();
         } else if (decodedUnsignedByte2 == 72) {
            this.offsetY = buffer.readShort();
         } else if (decodedUnsignedByte2 == 73) {
            this.obstructsGround = true;
         } else if (decodedUnsignedByte2 == 74) {
            this.hollow = true;
         } else if (decodedUnsignedByte2 != 27) {
            if (decodedUnsignedByte2 == 41) {
               short[] values = new short[decodedUnsignedByte2 = buffer.readUnsignedByte()];
               short[] workingArray = new short[decodedUnsignedByte2];

               for (int loopIndex = 0; loopIndex < decodedUnsignedByte2; loopIndex++) {
                  values[loopIndex] = (short)buffer.readUnsignedShort();
                  workingArray[loopIndex] = (short)buffer.readUnsignedShort();
               }
            } else if (decodedUnsignedByte2 == 78) {
               buffer.readShort();
               buffer.readUnsignedByte();
            } else if (decodedUnsignedByte2 == 79) {
               buffer.readShort();
               buffer.readShort();
               buffer.readUnsignedByte();
               decodedUnsignedByte2 = buffer.readUnsignedByte();

               for (int loopIndex2 = 0; loopIndex2 < decodedUnsignedByte2; loopIndex2++) {
                  buffer.readShort();
               }
            } else if (decodedUnsignedByte2 == 81) {
               this.adjustToTerrain = buffer.readUnsignedByte() == 0;
            } else if (decodedUnsignedByte2 == 82) {
               this.icon = buffer.readUnsignedShort();
            } else if (decodedUnsignedByte2 != 92) {
               if (decodedUnsignedByte2 == 249) {
                  decodedUnsignedByte2 = buffer.readUnsignedByte();

                  for (int loopIndex3 = 0; loopIndex3 < decodedUnsignedByte2; loopIndex3++) {
                     boolean readUnsignedByte2 = buffer.readUnsignedByte() == 1;
                     buffer.readUnsignedMediumLegacy();
                     if (readUnsignedByte2) {
                        buffer.readString();
                     } else {
                        buffer.readInt();
                     }
                  }
               } else if (decodedUnsignedByte2 != 75) {
                  if (decodedUnsignedByte2 == 77) {
                     this.varbitId = buffer.readUnsignedShort();
                     if (this.varbitId == 65535) {
                        this.varbitId = -1;
                     }

                     if (this.varbitId >= VarbitDefinition.definitions.length) {
                        System.out.println("ObjectId: " + scalarArgument + " too high config id: " + this.varbitId);
                        this.varbitId = -1;
                     }

                     this.configId = buffer.readUnsignedShort();
                     if (this.configId == 65535) {
                        this.configId = -1;
                     }

                     decodedUnsignedByte2 = buffer.readUnsignedByte();
                     this.childIds = new int[decodedUnsignedByte2 + 1];

                     for (int childIdIndex = 0; childIdIndex <= decodedUnsignedByte2; childIdIndex++) {
                        this.childIds[childIdIndex] = buffer.readUnsignedShort();
                        if (this.childIds[childIdIndex] == 65535) {
                           this.childIds[childIdIndex] = -1;
                        }
                     }
                  }
               } else {
                  this.supportsItems = buffer.readUnsignedByte();
               }
            } else {
               this.varbitId = buffer.readUnsignedShort();
               if (this.varbitId == 65535) {
                  this.varbitId = -1;
               }

               this.configId = buffer.readUnsignedShort();
               if (this.configId == 65535) {
                  this.configId = -1;
               }

               if ((decodedUnsignedByte2 = buffer.readUnsignedShort()) == 65535) {
                  decodedUnsignedByte2 = -1;
               }

               int readUnsignedByte3 = buffer.readUnsignedByte();
               this.childIds = new int[readUnsignedByte3 + 2];

               for (int childIdIndex2 = 0; childIdIndex2 <= readUnsignedByte3; childIdIndex2++) {
                  this.childIds[childIdIndex2] = buffer.readUnsignedShort();
                  if (65535 == this.childIds[childIdIndex2]) {
                     this.childIds[childIdIndex2] = -1;
                  }
               }

               this.childIds[readUnsignedByte3 + 1] = decodedUnsignedByte2;
            }
         }
      }

      if (scalarArgument == 3152 || scalarArgument == 3153 || scalarArgument == 3154 || scalarArgument == 3155 || scalarArgument == 3156 || scalarArgument == 3157 || scalarArgument == 3181 || scalarArgument == 3430) {
         this.nonFlatShading = true;
      }

      if (decodedUnsignedByte == -1) {
         this.hasActions = this.objectModels != null && (this.objectTypes == null || this.objectTypes[0] == 10);
         if (this.actions != null) {
            this.hasActions = true;
         }
      }

      if (this.hollow) {
         this.solid = false;
         this.walkable = false;
      }

      if (this.supportsItems == -1) {
         this.supportsItems = this.solid ? 1 : 0;
      }
   }
}
