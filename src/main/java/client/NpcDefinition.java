package client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
public final class NpcDefinition {
   public int turnLeftAnimationId = -1;
   private static int cacheIndex;
   private int varbitId = -1;
   public int turnAroundAnimationId = -1;
   private int configId = -1;
   private static Buffer definitionData;
   private static Buffer hdDefinitionData;
   private static Buffer revisionDefinitionData;
   private static boolean revision443Definitions;
   private static Models revision443Models;
   public int combatLevel = -1;
   public String name;
   public String[] actions;
   public int walkAnimationId = -1;
   public byte size = 1;
   private int[] recolorToReplace;
   private static int[] definitionOffsets;
   private static int[] hdDefinitionOffsets;
   private static int[] revisionDefinitionOffsets;
   private int[] headModelIds;
   public int headIcon = -1;
   private int[] recolorToFind;
   public int idleAnimationId = -1;
   public long id = -1L;
   public int turnSpeed = 32;
   private static NpcDefinition[] recentDefinitions;
   public static Client clientInstance;
   public int turnRightAnimationId = -1;
   public boolean clickable = true;
   private int ambient;
   private int scaleY = 128;
   public boolean drawMinimapDot = true;
   public int[] childIds;
   public byte[] description;
   private int scaleXZ = 128;
   private int contrast;
   public boolean priorityRender = false;
   private int[] modelIds;
   public static LruCache modelCache = new LruCache(30);
   private boolean temporaryDefinition = false;
   public static int definitionCount;
   private static int hdDefinitionCount;
   public static int revisionDefinitionCount;
   public static NpcDefinition lookup(int sourceId) {
      if (revision443Definitions) {
         for (NpcDefinition cached : recentDefinitions) {
            if (cached != null && cached.id == sourceId) return cached;
         }
         cacheIndex = (cacheIndex + 1) % recentDefinitions.length;
         NpcDefinition definition = new NpcDefinition();
         definition.id = sourceId;
         recentDefinitions[cacheIndex] = definition;
         definitionData.currentPosition = definitionOffsets[sourceId];
         definition.readValues(definitionData);
         definition.namespaceRevision443Models();
         return definition;
      }
      if (Client.hdModels || Client.use2007Models) {
         if (sourceId == 748) {
            sourceId = 6088;
         } else if (sourceId == 749) {
            sourceId = 6094;
         } else if (sourceId == 751) {
            sourceId = 6099;
         } else if (sourceId == 752) {
            sourceId = 6101;
         } else if (sourceId == 139) {
            sourceId = 6108;
         }
      }

      for (int recentDefinitionIndex = 0; recentDefinitionIndex < 20; recentDefinitionIndex++) {
         if (recentDefinitions[recentDefinitionIndex].id == sourceId) {
            return recentDefinitions[recentDefinitionIndex];
         }
      }

      cacheIndex = (cacheIndex + 1) % 20;
      NpcDefinition npcDefinition;
      (npcDefinition = recentDefinitions[cacheIndex] = new NpcDefinition()).id = sourceId;
      if (Client.hdModels
         && (sourceId <= 3851 || sourceId > 3887)
         && (sourceId < 1024 || sourceId > 1035)
         && sourceId != 790
         && sourceId != 379
         && sourceId != 754
         && sourceId != 568
         && sourceId != 2661
         && sourceId != 641
         && sourceId != 882
         && sourceId != 1047
         && sourceId != 1596
         && sourceId != 296
         && sourceId != 297
         && sourceId != 70
         && sourceId != 246
         && sourceId != 1826
         && sourceId != 1622
         && sourceId != 375
         && sourceId != 2693
         && sourceId != 11
         && sourceId != 45
         && sourceId != 381
         && sourceId != 750
         && sourceId != 1048
         && sourceId != 664) {
         hdDefinitionData.currentPosition = hdDefinitionOffsets[sourceId];
         npcDefinition.readValues(hdDefinitionData);
      } else if ((!Client.extendedRevisionEnabled || sourceId <= 3887)
         && (
            !Client.use2007Models
               || Client.hdModels
               || sourceId > 3851
               || sourceId >= 1024 && sourceId <= 1035
               || sourceId == 641
               || sourceId == 379
               || sourceId == 754
               || sourceId == 568
               || sourceId == 882
               || sourceId == 296
               || sourceId == 297
               || sourceId == 375
               || sourceId == 2693
               || sourceId == 11
               || sourceId == 45
               || sourceId == 381
               || sourceId == 750
         )) {
         definitionData.currentPosition = definitionOffsets[sourceId];
         npcDefinition.readValues(definitionData);
      } else {
         revisionDefinitionData.currentPosition = revisionDefinitionOffsets[sourceId];
         npcDefinition.readValues(revisionDefinitionData);
      }

      if (Client.extendedRevisionEnabled && sourceId == 6222) {
         npcDefinition.scaleY = 121;
         npcDefinition.scaleXZ = 121;
      }

      if (!Client.hdModels && !Client.use2007Models) {
         if (sourceId == 6211) {
            npcDefinition.modelIds[0] = 2887;
         } else if (sourceId == 6204) {
            npcDefinition.modelIds = new int[1];
            npcDefinition.modelIds[0] = 2942;
         } else if (sourceId == 6206) {
            npcDefinition.modelIds = new int[1];
            npcDefinition.modelIds[0] = 2943;
            npcDefinition.scaleY = 160;
            npcDefinition.scaleXZ = 160;
         } else if (sourceId == 6208) {
            npcDefinition.modelIds = new int[1];
            npcDefinition.modelIds[0] = 2942;
            npcDefinition.recolorToFind = new int[3];
            npcDefinition.recolorToReplace = new int[3];
            npcDefinition.recolorToFind[0] = 918;
            npcDefinition.recolorToReplace[0] = 4;
            npcDefinition.recolorToFind[1] = 929;
            npcDefinition.recolorToReplace[1] = 4;
            npcDefinition.recolorToFind[2] = 0;
            npcDefinition.recolorToReplace[2] = 931;
         }
      }

      if (Client.hdModels || Client.use2007Models) {
         if (sourceId == 1616) {
            npcDefinition.walkAnimationId = 65000;
            npcDefinition.turnAroundAnimationId = 65000;
            npcDefinition.turnRightAnimationId = 65000;
            npcDefinition.turnLeftAnimationId = 65000;
         }

         if (sourceId == 3869) {
            npcDefinition.idleAnimationId = 171;
            npcDefinition.walkAnimationId = 168;
            npcDefinition.modelIds[0] = 19982;
            npcDefinition.recolorToFind = new int[16];
            npcDefinition.recolorToReplace = new int[16];
            npcDefinition.recolorToFind[0] = 668;
            npcDefinition.recolorToReplace[0] = 54670;
            npcDefinition.recolorToFind[1] = 673;
            npcDefinition.recolorToReplace[1] = 54670;
            npcDefinition.recolorToFind[2] = 790;
            npcDefinition.recolorToReplace[2] = 54660;
            npcDefinition.recolorToFind[3] = 792;
            npcDefinition.recolorToReplace[3] = 54670;
            npcDefinition.recolorToFind[4] = 1816;
            npcDefinition.recolorToReplace[4] = 54680;
            npcDefinition.recolorToFind[5] = 4905;
            npcDefinition.recolorToReplace[5] = 54660;
            npcDefinition.recolorToFind[6] = 540;
            npcDefinition.recolorToReplace[6] = 54670;
            npcDefinition.recolorToFind[7] = 10306;
            npcDefinition.recolorToReplace[7] = 54680;
            npcDefinition.recolorToFind[8] = 545;
            npcDefinition.recolorToReplace[8] = 54660;
            npcDefinition.recolorToFind[9] = 10297;
            npcDefinition.recolorToReplace[9] = 54670;
            npcDefinition.recolorToFind[10] = 549;
            npcDefinition.recolorToReplace[10] = 54680;
            npcDefinition.recolorToFind[11] = 796;
            npcDefinition.recolorToReplace[11] = 54660;
            npcDefinition.recolorToFind[12] = 51;
            npcDefinition.recolorToReplace[12] = 54670;
            npcDefinition.recolorToFind[13] = 7469;
            npcDefinition.recolorToReplace[13] = 54680;
            npcDefinition.recolorToFind[14] = 54319;
            npcDefinition.recolorToReplace[14] = 54660;
            npcDefinition.recolorToFind[15] = 54302;
            npcDefinition.recolorToReplace[15] = 54670;
         } else if (sourceId == 3883) {
            npcDefinition.idleAnimationId = 66;
            npcDefinition.walkAnimationId = 63;
            npcDefinition.modelIds = new int[4];
            npcDefinition.modelIds[0] = 17375;
            npcDefinition.modelIds[1] = 17391;
            npcDefinition.modelIds[2] = 17384;
            npcDefinition.modelIds[3] = 17388;
            npcDefinition.recolorToFind = new int[5];
            npcDefinition.recolorToReplace = new int[5];
            npcDefinition.recolorToFind[0] = 910;
            npcDefinition.recolorToReplace[0] = 54670;
            npcDefinition.recolorToFind[1] = 912;
            npcDefinition.recolorToReplace[1] = 54672;
            npcDefinition.recolorToFind[2] = 1814;
            npcDefinition.recolorToReplace[2] = 54668;
            npcDefinition.recolorToFind[3] = 1938;
            npcDefinition.recolorToReplace[3] = 54672;
            npcDefinition.recolorToFind[4] = 1690;
            npcDefinition.recolorToReplace[4] = 54668;
         } else if (sourceId == 3885) {
            npcDefinition.idleAnimationId = 90;
            npcDefinition.walkAnimationId = 79;
            npcDefinition.modelIds = new int[3];
            npcDefinition.modelIds[0] = 17407;
            npcDefinition.modelIds[1] = 17425;
            npcDefinition.modelIds[2] = 17418;
            npcDefinition.recolorToFind = new int[6];
            npcDefinition.recolorToReplace = new int[6];
            npcDefinition.recolorToFind[0] = 2469;
            npcDefinition.recolorToReplace[0] = 54670;
            npcDefinition.recolorToFind[1] = 912;
            npcDefinition.recolorToReplace[1] = 54672;
            npcDefinition.recolorToFind[2] = 1938;
            npcDefinition.recolorToReplace[2] = 54668;
            npcDefinition.recolorToFind[3] = 1814;
            npcDefinition.recolorToReplace[3] = 54668;
            npcDefinition.recolorToFind[4] = 2588;
            npcDefinition.recolorToReplace[4] = 54672;
            npcDefinition.recolorToFind[5] = 910;
            npcDefinition.recolorToReplace[5] = 54670;
         }
      }

      switch (sourceId) {
         case 945:
            npcDefinition.name = "RuneScape Guide";
            break;
         case 2258:
            npcDefinition.actions = new String[5];
            npcDefinition.actions[0] = "Talk-to";
            npcDefinition.actions[2] = "Trade";
            npcDefinition.actions[3] = "Teleport";
      }

      return npcDefinition;
   }
   public final Model model() {
      if (this.childIds != null) {
         NpcDefinition npcDefinition = this.morph();
         return npcDefinition == null ? null : npcDefinition.model();
      }

      if (this.headModelIds == null) {
         return null;
      }

      boolean flag = false;

      for (int headModelIdIndex = 0; headModelIdIndex < this.headModelIds.length; headModelIdIndex++) {
         if (!Model.isCached(this.headModelIds[headModelIdIndex])) {
            flag = true;
         }
      }

      if (flag) {
         return null;
      }

      Model[] headModelIdsLength = new Model[this.headModelIds.length];

      for (int headModelIdsLengthIndex = 0; headModelIdsLengthIndex < this.headModelIds.length; headModelIdsLengthIndex++) {
         headModelIdsLength[headModelIdsLengthIndex] = Model.getModel(this.headModelIds[headModelIdsLengthIndex]);
      }

      Model model;
      if (headModelIdsLength.length == 1) {
         model = headModelIdsLength[0];
      } else {
         model = new Model(headModelIdsLength.length, headModelIdsLength);
      }

      if (this.recolorToFind != null) {
         for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
            model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
         }
      }

      if (this.id == 3863) {
         model.scale(192, 192, 192);
      }

      return model;
   }
   public final NpcDefinition morph() {
      int childIdIndex = -1;
      if (this.varbitId != -1) {
         VarbitDefinition varbitDefinition;
         int varpIndex = (varbitDefinition = VarbitDefinition.definitions[this.varbitId]).index;
         int leastSignificantBit = varbitDefinition.leastSignificantBit;
         childIdIndex = varbitDefinition.mostSignificantBit;
         childIdIndex = Client.bitMasks[childIdIndex - leastSignificantBit];
         childIdIndex = clientInstance.varps[varpIndex] >> leastSignificantBit & childIdIndex;
      } else if (this.configId != -1) {
         childIdIndex = clientInstance.varps[this.configId];
      }

      return childIdIndex >= 0 && childIdIndex < this.childIds.length && this.childIds[childIdIndex] != -1 ? lookup(this.childIds[childIdIndex]) : null;
   }
   public static void unpackConfig(Archive archive) {
      revision443Definitions = false;
      revision443Models = null;
      definitionData = new Buffer(archive.getFile("npc.dat"));
      Buffer buffer = new Buffer(archive.getFile("npc.idx"));
      if (Client.hdModels) {
         hdDefinitionData = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/npc.dat"));
         Buffer buffer3;
         hdDefinitionOffsets = new int[hdDefinitionCount = (buffer3 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/npc.idx"))).readUnsignedShort()];
         int hdDefinitionOffset = 2;

         for (int hdDefinitionOffsetIndex = 0; hdDefinitionOffsetIndex < hdDefinitionCount; hdDefinitionOffsetIndex++) {
            hdDefinitionOffsets[hdDefinitionOffsetIndex] = hdDefinitionOffset;
            hdDefinitionOffset += buffer3.readUnsignedShort();
         }
      }

      if (Client.extendedRevisionEnabled) {
         revisionDefinitionData = new Buffer(archive.getFile("npc07.dat"));
         Buffer buffer2;
         revisionDefinitionOffsets = new int[revisionDefinitionCount = (buffer2 = new Buffer(archive.getFile("npc07.idx"))).readUnsignedShort()];
         int revisionDefinitionOffset = 2;

         for (int revisionDefinitionOffsetIndex = 0; revisionDefinitionOffsetIndex < revisionDefinitionCount; revisionDefinitionOffsetIndex++) {
            revisionDefinitionOffsets[revisionDefinitionOffsetIndex] = revisionDefinitionOffset;
            revisionDefinitionOffset += buffer2.readUnsignedShort();
         }
      }

      definitionOffsets = new int[definitionCount = buffer.readUnsignedShort()];
      int definitionOffset = 2;

      for (int definitionOffsetIndex = 0; definitionOffsetIndex < definitionCount; definitionOffsetIndex++) {
         definitionOffsets[definitionOffsetIndex] = definitionOffset;
         definitionOffset += buffer.readUnsignedShort();
      }

      recentDefinitions = new NpcDefinition[20];

      for (int recentDefinitionIndex = 0; recentDefinitionIndex < 20; recentDefinitionIndex++) {
         recentDefinitions[recentDefinitionIndex] = new NpcDefinition();
      }
   }
   public static void loadRevision443(Cache cache) throws IOException {
      Map<Integer, byte[]> files = cache.readFiles(2, 9);
      int maxId = -1;
      for (Integer id : files.keySet()) maxId = Math.max(maxId, id.intValue());
      int[] offsets = new int[maxId + 1];
      ByteArrayOutputStream data = new ByteArrayOutputStream();
      data.write(0);
      data.write(0);
      for (int id = 0; id <= maxId; id++) {
         offsets[id] = data.size();
         byte[] file = files.get(Integer.valueOf(id));
         if (file == null) {
            data.write(0);
            continue;
         }
         Buffer audit = new Buffer(file);
         audit.stringTerminator = 0;
         try {
            new NpcDefinition().readValues(audit);
         } catch (RuntimeException exception) {
            throw new IOException("Invalid 443 NPC " + id, exception);
         }
         if (audit.currentPosition != file.length) {
            throw new IOException("443 NPC parser did not consume NPC " + id);
         }
         data.write(file, 0, file.length);
      }
      definitionData = new Buffer(data.toByteArray());
      definitionData.stringTerminator = 0;
      definitionOffsets = offsets;
      definitionCount = offsets.length;
      recentDefinitions = new NpcDefinition[20];
      cacheIndex = 0;
      modelCache = new LruCache(30);
      revision443Models = Models.load(cache);
      revision443Models.initializeModelNamespace();
      revision443Definitions = true;
   }
   private void namespaceRevision443Models() {
      if (modelIds != null) {
         for (int i = 0; i < modelIds.length; i++) {
            modelIds[i] = revision443Models.getRegisteredModelId(modelIds[i]);
         }
      }
      if (headModelIds != null) {
         for (int i = 0; i < headModelIds.length; i++) {
            headModelIds[i] = revision443Models.getRegisteredModelId(headModelIds[i]);
         }
      }
   }
   public static void nullLoader() {
      modelCache = null;
      definitionOffsets = null;
      recentDefinitions = null;
      definitionData = null;
   }
   public final Model getAnimatedModelInterpolated(int frameIndex, int frameIndex2, int frameId, int frameLength, int emoteFrameCycle, int[] values) {
      if (this.childIds != null) {
         NpcDefinition npcDefinition;
         return (npcDefinition = this.morph()) == null ? null : npcDefinition.getAnimatedModel(frameIndex, frameIndex2, values);
      }

      Model model;
      if ((model = (Model)modelCache.get(this.id)) == null) {
         boolean flag = false;

         for (int modelIdIndex = 0; modelIdIndex < this.modelIds.length; modelIdIndex++) {
            if (!Model.isCached(this.modelIds[modelIdIndex])) {
               flag = true;
            }
         }

         if (flag) {
            return null;
         }

         Model[] modelIdsLength = new Model[this.modelIds.length];

         for (int modelIdsLengthIndex = 0; modelIdsLengthIndex < this.modelIds.length; modelIdsLengthIndex++) {
            modelIdsLength[modelIdsLengthIndex] = Model.getModel(this.modelIds[modelIdsLengthIndex]);
         }

         if (modelIdsLength.length == 1) {
            model = modelIdsLength[0];
         } else {
            model = new Model(modelIdsLength.length, modelIdsLength);
         }

         if (this.recolorToFind != null) {
            for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
               model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
            }
         }

         model.skin();
         model.light(64 + this.ambient, 850 + this.contrast, -30, -50, -30, true);
         modelCache.put(model, this.id);
      }

      Model model2;
      (model2 = Model.sharedModel).copyForAnimation(model, AnimationFrame.isNullFrame(frameIndex2) & AnimationFrame.isNullFrame(frameIndex));
      if (frameIndex2 != -1 && frameIndex != -1) {
         model2.applyAnimationFrames(values, frameIndex, frameIndex2);
      } else if (frameIndex2 != -1) {
         if (Client.smoothAnimations && frameId != -1) {
            model2.applyInterpolatedAnimation(frameIndex2, frameId, emoteFrameCycle, frameLength);
         } else {
            model2.applyAnimationFrame(frameIndex2);
         }
      }

      if (this.scaleXZ != 128 || this.scaleY != 128) {
         model2.scale(this.scaleXZ, this.scaleXZ, this.scaleY);
      }

      model2.calculateBoundsCylinder();
      model2.triangleSkin = null;
      model2.vectorSkin = null;
      if (this.size == 1) {
         model2.singleTile = true;
      }

      return model2;
   }
   private Model getAnimatedModel(int frameIndex, int frameIndex2, int[] values) {
      if (this.childIds != null) {
         NpcDefinition npcDefinition = this.morph();
         return npcDefinition == null ? null : npcDefinition.getAnimatedModel(frameIndex, frameIndex2, values);
      }

      Model model;
      if ((model = (Model)modelCache.get(this.id)) == null) {
         boolean flag = false;

         for (int modelIdIndex = 0; modelIdIndex < this.modelIds.length; modelIdIndex++) {
            if (!Model.isCached(this.modelIds[modelIdIndex])) {
               flag = true;
            }
         }

         if (flag) {
            return null;
         }

         Model[] modelIdsLength = new Model[this.modelIds.length];

         for (int modelIdsLengthIndex = 0; modelIdsLengthIndex < this.modelIds.length; modelIdsLengthIndex++) {
            modelIdsLength[modelIdsLengthIndex] = Model.getModel(this.modelIds[modelIdsLengthIndex]);
         }

         if (modelIdsLength.length == 1) {
            model = modelIdsLength[0];
         } else {
            model = new Model(modelIdsLength.length, modelIdsLength);
         }

         if (this.recolorToFind != null) {
            for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
               model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
            }
         }

         model.skin();
         model.light(64 + this.ambient, 850 + this.contrast, -30, -50, -30, true);
         modelCache.put(model, this.id);
      }

      Model model2;
      (model2 = Model.sharedModel).copyForAnimation(model, AnimationFrame.isNullFrame(frameIndex2) & AnimationFrame.isNullFrame(frameIndex));
      if (frameIndex2 != -1 && frameIndex != -1) {
         model2.applyAnimationFrames(values, frameIndex, frameIndex2);
      } else if (frameIndex2 != -1) {
         model2.applyAnimationFrame(frameIndex2);
      }

      if (this.scaleXZ != 128 || this.scaleY != 128) {
         model2.scale(this.scaleXZ, this.scaleXZ, this.scaleY);
      }

      model2.calculateBoundsCylinder();
      model2.triangleSkin = null;
      model2.vectorSkin = null;
      if (this.size == 1) {
         model2.singleTile = true;
      }

      return model2;
   }
   private void readValues(Buffer buffer) {
      int decodedUnsignedByte2;
      while ((decodedUnsignedByte2 = buffer.readUnsignedByte()) != 0) {
         if (decodedUnsignedByte2 == 1) {
            decodedUnsignedByte2 = buffer.readUnsignedByte();
            this.modelIds = new int[decodedUnsignedByte2];

            for (int modelIdIndex = 0; modelIdIndex < decodedUnsignedByte2; modelIdIndex++) {
               this.modelIds[modelIdIndex] = buffer.readUnsignedShort();
            }
         } else if (decodedUnsignedByte2 == 2) {
            this.name = buffer.readString();
         } else if (decodedUnsignedByte2 == 3) {
            this.description = buffer.readBytes();
         } else if (decodedUnsignedByte2 == 12) {
            this.size = buffer.readByte();
         } else if (decodedUnsignedByte2 == 13) {
            this.idleAnimationId = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 14) {
            this.walkAnimationId = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 17) {
            this.walkAnimationId = buffer.readUnsignedShort();
            this.turnAroundAnimationId = buffer.readUnsignedShort();
            this.turnRightAnimationId = buffer.readUnsignedShort();
            this.turnLeftAnimationId = buffer.readUnsignedShort();
            if (this.turnAroundAnimationId == 65535) {
               this.turnAroundAnimationId = this.walkAnimationId;
            }

            if (this.turnRightAnimationId == 65535) {
               this.turnRightAnimationId = this.walkAnimationId;
            }

            if (this.turnLeftAnimationId == 65535) {
               this.turnLeftAnimationId = this.walkAnimationId;
            }
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
            decodedUnsignedByte2 = buffer.readUnsignedByte();
            this.headModelIds = new int[decodedUnsignedByte2];

            for (int headModelIdIndex = 0; headModelIdIndex < decodedUnsignedByte2; headModelIdIndex++) {
               this.headModelIds[headModelIdIndex] = buffer.readUnsignedShort();
            }
         } else if (decodedUnsignedByte2 == 90) {
            buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 91) {
            buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 92) {
            buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 93) {
            this.drawMinimapDot = false;
         } else if (decodedUnsignedByte2 == 95) {
            this.combatLevel = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 97) {
            this.scaleXZ = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 98) {
            this.scaleY = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 99) {
            this.priorityRender = true;
         } else if (decodedUnsignedByte2 == 100) {
            this.ambient = buffer.readByte();
         } else if (decodedUnsignedByte2 == 101) {
            this.contrast = buffer.readByte() * 5;
         } else if (decodedUnsignedByte2 == 102) {
            this.headIcon = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 == 103) {
            this.turnSpeed = buffer.readUnsignedShort();
         } else if (decodedUnsignedByte2 != 107 && decodedUnsignedByte2 != 109 && decodedUnsignedByte2 != 111) {
            if (decodedUnsignedByte2 == 249) {
               int decodedUnsignedByte = buffer.readUnsignedByte();

               for (int loopIndex = 0; loopIndex < decodedUnsignedByte; loopIndex++) {
                  boolean readUnsignedByte2 = buffer.readUnsignedByte() == 1;
                  buffer.readUnsignedMediumLegacy();
                  if (readUnsignedByte2) {
                     buffer.readString();
                  } else {
                     buffer.readInt();
                  }
               }
            } else if (decodedUnsignedByte2 == 15) {
               buffer.readUnsignedShort();
            } else if (decodedUnsignedByte2 == 16) {
               buffer.readUnsignedShort();
            } else if (decodedUnsignedByte2 == 41) {
               int readUnsignedByte3 = buffer.readUnsignedByte();

               for (int loopIndex2 = 0; loopIndex2 < readUnsignedByte3; loopIndex2++) {
                  buffer.readUnsignedShort();
                  buffer.readUnsignedShort();
               }
            } else if (decodedUnsignedByte2 != 106 && decodedUnsignedByte2 != 118) {
               if (decodedUnsignedByte2 == 107) {
                  this.clickable = false;
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

               int childIdOrReadUnsignedShort = -1;
               if (decodedUnsignedByte2 == 118 && (childIdOrReadUnsignedShort = buffer.readUnsignedShort()) == 65535) {
                  childIdOrReadUnsignedShort = -1;
               }

               decodedUnsignedByte2 = buffer.readUnsignedByte();
               this.childIds = new int[decodedUnsignedByte2 + 2];

               for (int childIdIndex = 0; childIdIndex <= decodedUnsignedByte2; childIdIndex++) {
                  this.childIds[childIdIndex] = buffer.readUnsignedShort();
                  if (this.childIds[childIdIndex] == 65535) {
                     this.childIds[childIdIndex] = -1;
                  }
               }

               this.childIds[decodedUnsignedByte2 + 1] = childIdOrReadUnsignedShort;
            }
         }
      }
   }
}
