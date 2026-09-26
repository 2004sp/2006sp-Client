package client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
public final class ItemDefinition {
   private byte femaleYOffset;
   public int value;
   private int[] recolorToFind;
   private int[] recolorFrom;
   private int[] recolorTo;
   public int id = -1;
   static LruCache spriteCache = new LruCache(100);
   public static LruCache modelCache = new LruCache(50);
   private int[] recolorToReplace;
   public boolean members;
   private int femaleModel2;
   private int noteTemplateId;
   private int femaleModel1;
   private int maleModel0;
   private int secondaryMaleHeadPiece;
   private int groundScaleX;
   public String[] groundOptions;
   private int xOffset2d;
   public String name;
   private static ItemDefinition[] recentDefinitions;
   private int secondaryFemaleHeadPiece;
   private int inventoryModelId;
   private int primaryMaleHeadPiece;
   public boolean stackable;
   public byte[] description;
   private int notedId;
   private static int cacheIndex;
   public int zoom2d;
   public static boolean membersServer = true;
   private static Buffer definitionData;
   private static Buffer hdDefinitionData;
   private static Buffer revisionDefinitionData;
   private static boolean revision443Definitions;
   private static Models revision443Models;
   private int contrast;
   private int maleModel2;
   private int maleModel1;
   public String[] inventoryOptions;
   public int xan2d;
   private int groundScaleZ;
   private int groundScaleY;
   private int[] stackIds;
   private int yOffset2d;
   private static int[] definitionOffsets;
   private static int[] hdDefinitionOffsets;
   private static int[] revisionDefinitionOffsets;
   private int ambient;
   private int primaryFemaleHeadPiece;
   public int yan2d;
   private int femaleModel0;
   private int[] stackAmounts;
   public int teamIndex;
   private int maleXOffset;
   private int maleZOffset;
   private int femaleXOffset;
   private int femaleZOffset;
   public static int definitionCount;
   private static int hdDefinitionCount;
   private static int revisionDefinitionCount;
   private int zan2d;
   private byte maleYOffset;
   private boolean temporaryDefinition;
   public boolean searchable;
   public final int getLowAlchValue() {
      return (this.value << 1) / 5;
   }
   public final int getHighAlchValue() {
      return this.value * 3 / 5;
   }
   public static void clearCache() {
      modelCache = null;
      spriteCache = null;
      definitionOffsets = null;
      recentDefinitions = null;
      definitionData = null;
   }
   public final boolean isDialogueModelCached(int gender) {
      int primaryMaleHeadPiece = this.primaryMaleHeadPiece;
      int secondaryMaleHeadPiece = this.secondaryMaleHeadPiece;
      if (gender == 1) {
         primaryMaleHeadPiece = this.primaryFemaleHeadPiece;
         secondaryMaleHeadPiece = this.secondaryFemaleHeadPiece;
      }

      if (primaryMaleHeadPiece == -1) {
         return true;
      }

      boolean flag = true;
      if (!Model.isCached(primaryMaleHeadPiece)) {
         flag = false;
      }

      if (secondaryMaleHeadPiece != -1 && !Model.isCached(secondaryMaleHeadPiece)) {
         flag = false;
      }

      return flag;
   }
   public static int getDefinitionCount() {
      return revision443Definitions ? definitionCount
         : Client.extendedRevisionEnabled ? revisionDefinitionCount : definitionCount;
   }
   public static boolean isValidDefinitionId(int id) {
      if (id < 0) {
         return false;
      }
      if (revision443Definitions) {
         return definitionOffsets != null && id < definitionOffsets.length;
      }
      if (Client.hdModels && (id <= 7955 || id > 8118) && id != 552 && id != 553) {
         return hdDefinitionOffsets != null && id < hdDefinitionOffsets.length;
      }
      if (Client.extendedRevisionEnabled
            && (id > 8118 || Client.use2007Models && !Client.hdModels && id <= 7955)
            && id != 552 && id != 553) {
         return revisionDefinitionOffsets != null && id < revisionDefinitionOffsets.length;
      }
      return definitionOffsets != null && id < definitionOffsets.length;
   }
   public static void unpackConfig(Archive archive) {
      revision443Definitions = false;
      revision443Models = null;
      definitionData = new Buffer(archive.getFile("obj.dat"));
      Buffer buffer = new Buffer(archive.getFile("obj.idx"));
      if (Client.hdModels) {
         hdDefinitionData = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/obj.dat"));
         Buffer buffer3;
         hdDefinitionOffsets = new int[hdDefinitionCount = (buffer3 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/obj.idx"))).readUnsignedShort()];
         int hdDefinitionOffset = 2;

         for (int hdDefinitionOffsetIndex = 0; hdDefinitionOffsetIndex < hdDefinitionCount; hdDefinitionOffsetIndex++) {
            hdDefinitionOffsets[hdDefinitionOffsetIndex] = hdDefinitionOffset;
            hdDefinitionOffset += buffer3.readUnsignedShort();
         }
      }

      if (Client.extendedRevisionEnabled) {
         revisionDefinitionData = new Buffer(archive.getFile("obj07.dat"));
         Buffer buffer2;
         revisionDefinitionOffsets = new int[revisionDefinitionCount = (buffer2 = new Buffer(archive.getFile("obj07.idx"))).readUnsignedShort()];
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

      recentDefinitions = new ItemDefinition[10];

      for (int recentDefinitionIndex = 0; recentDefinitionIndex < 10; recentDefinitionIndex++) {
         recentDefinitions[recentDefinitionIndex] = new ItemDefinition();
      }
   }
   public static void loadRevision443(Cache cache) throws IOException {
      Map<Integer, byte[]> files = cache.readFiles(2, 10);
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
            new ItemDefinition().readValues(audit);
         } catch (RuntimeException exception) {
            throw new IOException("Invalid 443 item " + id, exception);
         }
         if (audit.currentPosition != file.length) {
            throw new IOException("443 item parser did not consume item " + id);
         }
         data.write(file, 0, file.length);
      }
      definitionData = new Buffer(data.toByteArray());
      definitionData.stringTerminator = 0;
      definitionOffsets = offsets;
      definitionCount = offsets.length;
      recentDefinitions = new ItemDefinition[10];
      for (int i = 0; i < recentDefinitions.length; i++) recentDefinitions[i] = new ItemDefinition();
      cacheIndex = 0;
      spriteCache = new LruCache(100);
      modelCache = new LruCache(50);
      revision443Models = Models.load(cache);
      revision443Models.initializeModelNamespace();
      revision443Definitions = true;
   }
   public final Model getChatEquipModel(int gender) {
      int primaryMaleHeadPiece = this.primaryMaleHeadPiece;
      int secondaryMaleHeadPiece = this.secondaryMaleHeadPiece;
      if (gender == 1) {
         primaryMaleHeadPiece = this.primaryFemaleHeadPiece;
         secondaryMaleHeadPiece = this.secondaryFemaleHeadPiece;
      }

      if (primaryMaleHeadPiece == -1) {
         return null;
      }

      Model model = Model.getModel(primaryMaleHeadPiece);
      if (secondaryMaleHeadPiece != -1) {
         Model model2 = Model.getModel(secondaryMaleHeadPiece);
         Model[] values = new Model[]{model, model2};
         model = new Model(2, values);
      }

      if (this.recolorToFind != null) {
         for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
            model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
         }
      }

      if (this.recolorFrom != null) {
         for (int recolorFromIndex = 0; recolorFromIndex < this.recolorFrom.length; recolorFromIndex++) {
            model.setTriangleAlphaForColor(this.recolorFrom[recolorFromIndex], this.recolorTo[recolorFromIndex]);
         }
      }

      return model;
   }
   public final boolean isEquippedModelCached(int gender) {
      int maleModel0 = this.maleModel0;
      int maleModel1 = this.maleModel1;
      int maleModel2 = this.maleModel2;
      if (gender == 1) {
         maleModel0 = this.femaleModel0;
         maleModel1 = this.femaleModel1;
         maleModel2 = this.femaleModel2;
      }

      if (maleModel0 == -1) {
         return true;
      }

      boolean flag = true;
      if (!Model.isCached(maleModel0)) {
         flag = false;
      }

      if (maleModel1 != -1 && !Model.isCached(maleModel1)) {
         flag = false;
      }

      if (maleModel2 != -1 && !Model.isCached(maleModel2)) {
         flag = false;
      }

      return flag;
   }
   public final Model getEquippedModel(int gender) {
      int maleModel0 = this.maleModel0;
      int maleModel1 = this.maleModel1;
      int maleModel2 = this.maleModel2;
      if (gender == 1) {
         maleModel0 = this.femaleModel0;
         maleModel1 = this.femaleModel1;
         maleModel2 = this.femaleModel2;
      }

      if (maleModel0 == -1) {
         return null;
      }

      Model model = Model.getModel(maleModel0);
      if (maleModel1 != -1) {
         if (maleModel2 != -1) {
            Model model4 = Model.getModel(maleModel1);
            Model model2 = Model.getModel(maleModel2);
            Model[] values = new Model[]{model, model4, model2};
            model = new Model(3, values);
         } else {
            Model model3 = Model.getModel(maleModel1);
            Model[] model5 = new Model[]{model, model3};
            model = new Model(2, model5);
         }
      }

      if (gender == 0 && this.maleYOffset != 0) {
         model.translate(this.maleXOffset, this.maleYOffset, this.maleZOffset);
      }

      if (gender == 1 && this.femaleYOffset != 0) {
         model.translate(this.femaleXOffset, this.femaleYOffset, this.femaleZOffset);
      }

      if (this.recolorToFind != null) {
         for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
            model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
         }
      }

      if (this.recolorFrom != null) {
         for (int recolorFromIndex = 0; recolorFromIndex < this.recolorFrom.length; recolorFromIndex++) {
            model.setTriangleAlphaForColor(this.recolorFrom[recolorFromIndex], this.recolorTo[recolorFromIndex]);
         }
      }

      return model;
   }
   public static ItemDefinition lookup(int sourceId) {
      if (!isValidDefinitionId(sourceId)) {
         return invalidDefinition(sourceId);
      }

      for (int recentDefinitionIndex = 0; recentDefinitionIndex < 10; recentDefinitionIndex++) {
         if (recentDefinitions[recentDefinitionIndex].id == sourceId) {
            return recentDefinitions[recentDefinitionIndex];
         }
      }

      cacheIndex = (cacheIndex + 1) % 10;
      ItemDefinition itemDefinition;
      (itemDefinition = recentDefinitions[cacheIndex]).id = sourceId;
      ItemDefinition sourceItemDefinition = itemDefinition;
      itemDefinition.inventoryModelId = 0;
      sourceItemDefinition.name = null;
      sourceItemDefinition.description = null;
      sourceItemDefinition.recolorToFind = null;
      sourceItemDefinition.recolorToReplace = null;
      sourceItemDefinition.zoom2d = 2000;
      sourceItemDefinition.xan2d = 0;
      sourceItemDefinition.yan2d = 0;
      sourceItemDefinition.zan2d = 0;
      sourceItemDefinition.xOffset2d = 0;
      sourceItemDefinition.yOffset2d = 0;
      sourceItemDefinition.stackable = false;
      sourceItemDefinition.searchable = false;
      sourceItemDefinition.value = 1;
      sourceItemDefinition.members = false;
      sourceItemDefinition.groundOptions = null;
      sourceItemDefinition.inventoryOptions = null;
      sourceItemDefinition.maleModel0 = -1;
      sourceItemDefinition.maleModel1 = -1;
      sourceItemDefinition.maleYOffset = 0;
      sourceItemDefinition.femaleModel0 = -1;
      sourceItemDefinition.femaleModel1 = -1;
      sourceItemDefinition.femaleYOffset = 0;
      sourceItemDefinition.maleModel2 = -1;
      sourceItemDefinition.femaleModel2 = -1;
      sourceItemDefinition.primaryMaleHeadPiece = -1;
      sourceItemDefinition.secondaryMaleHeadPiece = -1;
      sourceItemDefinition.primaryFemaleHeadPiece = -1;
      sourceItemDefinition.secondaryFemaleHeadPiece = -1;
      sourceItemDefinition.stackIds = null;
      sourceItemDefinition.stackAmounts = null;
      sourceItemDefinition.notedId = -1;
      sourceItemDefinition.noteTemplateId = -1;
      sourceItemDefinition.groundScaleX = 128;
      sourceItemDefinition.groundScaleY = 128;
      sourceItemDefinition.groundScaleZ = 128;
      sourceItemDefinition.ambient = 0;
      sourceItemDefinition.contrast = 0;
      sourceItemDefinition.teamIndex = 0;
      sourceItemDefinition.maleXOffset = 0;
      sourceItemDefinition.maleZOffset = 0;
      sourceItemDefinition.femaleXOffset = 0;
      sourceItemDefinition.femaleZOffset = 0;
      sourceItemDefinition.recolorTo = null;
      sourceItemDefinition.recolorFrom = null;
      sourceItemDefinition.temporaryDefinition = false;
      if (revision443Definitions) {
         definitionData.currentPosition = definitionOffsets[sourceId];
         itemDefinition.readValues(definitionData);
         itemDefinition.namespaceRevision443Models();
      } else if (Client.hdModels && (sourceId <= 7955 || sourceId > 8118) && sourceId != 552 && sourceId != 553) {
         hdDefinitionData.currentPosition = hdDefinitionOffsets[sourceId];
         itemDefinition.readValues(hdDefinitionData);
      } else if (Client.extendedRevisionEnabled && (sourceId > 8118 || Client.use2007Models && !Client.hdModels && sourceId <= 7955) && sourceId != 552 && sourceId != 553) {
         revisionDefinitionData.currentPosition = revisionDefinitionOffsets[sourceId];
         itemDefinition.readValues(revisionDefinitionData);
      } else {
         definitionData.currentPosition = definitionOffsets[sourceId];
         itemDefinition.readValues(definitionData);
      }

      if (itemDefinition.noteTemplateId != -1) {
         sourceItemDefinition = itemDefinition;
         ItemDefinition itemDefinition2 = lookup(itemDefinition.noteTemplateId);
         sourceItemDefinition.inventoryModelId = itemDefinition2.inventoryModelId;
         sourceItemDefinition.zoom2d = itemDefinition2.zoom2d;
         sourceItemDefinition.xan2d = itemDefinition2.xan2d;
         sourceItemDefinition.yan2d = itemDefinition2.yan2d;
         sourceItemDefinition.zan2d = itemDefinition2.zan2d;
         sourceItemDefinition.xOffset2d = itemDefinition2.xOffset2d;
         sourceItemDefinition.yOffset2d = itemDefinition2.yOffset2d;
         sourceItemDefinition.recolorToFind = itemDefinition2.recolorToFind;
         sourceItemDefinition.recolorToReplace = itemDefinition2.recolorToReplace;
         sourceItemDefinition.recolorTo = itemDefinition2.recolorTo;
         sourceItemDefinition.recolorFrom = itemDefinition2.recolorFrom;
         itemDefinition2 = lookup(sourceItemDefinition.notedId);
         sourceItemDefinition.name = itemDefinition2.name;
         sourceItemDefinition.members = itemDefinition2.members;
         sourceItemDefinition.value = itemDefinition2.value;
         String text = "a";
         char localName;
         if ((localName = itemDefinition2.name.charAt(0)) == 'A' || localName == 'E' || localName == 'I' || localName == 'O' || localName == 'U') {
            text = "an";
         }

         sourceItemDefinition.description = ("Swap this note at any bank for " + text + " " + itemDefinition2.name + ".").getBytes();
         sourceItemDefinition.stackable = true;
      }

      if (!membersServer && itemDefinition.members) {
         itemDefinition.name = "Members Object";
         itemDefinition.description = "Login to a members' server to use this object.".getBytes();
         itemDefinition.groundOptions = null;
         itemDefinition.inventoryOptions = null;
         itemDefinition.teamIndex = 0;
      }

      if (!revision443Definitions && sourceId == 7999) {
         itemDefinition.name = "Membership scroll";
         itemDefinition.description = "Can be claimed for membership.".getBytes();
      }

      return itemDefinition;
   }
   private static ItemDefinition invalidDefinition(int sourceId) {
      ItemDefinition itemDefinition = new ItemDefinition();
      itemDefinition.id = sourceId;
      itemDefinition.inventoryModelId = -1;
      itemDefinition.name = null;
      itemDefinition.zoom2d = 2000;
      itemDefinition.maleModel0 = -1;
      itemDefinition.maleModel1 = -1;
      itemDefinition.maleModel2 = -1;
      itemDefinition.femaleModel0 = -1;
      itemDefinition.femaleModel1 = -1;
      itemDefinition.femaleModel2 = -1;
      itemDefinition.primaryMaleHeadPiece = -1;
      itemDefinition.secondaryMaleHeadPiece = -1;
      itemDefinition.primaryFemaleHeadPiece = -1;
      itemDefinition.secondaryFemaleHeadPiece = -1;
      itemDefinition.notedId = -1;
      itemDefinition.noteTemplateId = -1;
      return itemDefinition;
   }
   private void namespaceRevision443Models() {
      inventoryModelId = revision443Models.getRegisteredModelId(inventoryModelId);
      if (maleModel0 >= 0) maleModel0 = revision443Models.getRegisteredModelId(maleModel0);
      if (maleModel1 >= 0) maleModel1 = revision443Models.getRegisteredModelId(maleModel1);
      if (maleModel2 >= 0) maleModel2 = revision443Models.getRegisteredModelId(maleModel2);
      if (femaleModel0 >= 0) femaleModel0 = revision443Models.getRegisteredModelId(femaleModel0);
      if (femaleModel1 >= 0) femaleModel1 = revision443Models.getRegisteredModelId(femaleModel1);
      if (femaleModel2 >= 0) femaleModel2 = revision443Models.getRegisteredModelId(femaleModel2);
      if (primaryMaleHeadPiece >= 0) primaryMaleHeadPiece = revision443Models.getRegisteredModelId(primaryMaleHeadPiece);
      if (secondaryMaleHeadPiece >= 0) secondaryMaleHeadPiece = revision443Models.getRegisteredModelId(secondaryMaleHeadPiece);
      if (primaryFemaleHeadPiece >= 0) primaryFemaleHeadPiece = revision443Models.getRegisteredModelId(primaryFemaleHeadPiece);
      if (secondaryFemaleHeadPiece >= 0) secondaryFemaleHeadPiece = revision443Models.getRegisteredModelId(secondaryFemaleHeadPiece);
   }
   public static Sprite getSprite(int key, int newCanvasHeight, int pixel) {
      if (!isValidDefinitionId(key)) {
         return null;
      }

      if (pixel == 0) {
         Sprite sprite;
         if ((sprite = (Sprite)spriteCache.get(key)) != null && sprite.canvasHeight != newCanvasHeight && sprite.canvasHeight != -1) {
            sprite.unlink();
            sprite = null;
         }

         if (sprite != null) {
            return sprite;
         }
      }

      ItemDefinition itemDefinition;
      if ((itemDefinition = lookup(key)).stackIds == null) {
         newCanvasHeight = -1;
      }

      if (newCanvasHeight > 1) {
         int localStackIds = -1;

         for (int stackAmountIndex = 0; stackAmountIndex < 10; stackAmountIndex++) {
            if (newCanvasHeight >= itemDefinition.stackAmounts[stackAmountIndex] && itemDefinition.stackAmounts[stackAmountIndex] != 0) {
               localStackIds = itemDefinition.stackIds[stackAmountIndex];
            }
         }

         if (localStackIds != -1) {
            if (!isValidDefinitionId(localStackIds)) {
               return null;
            }
            itemDefinition = lookup(localStackIds);
         }
      }

      Model model;
      if ((model = itemDefinition.getModel(1)) == null) {
         return null;
      }

      Sprite sprite3 = null;
      if (itemDefinition.noteTemplateId != -1 && (sprite3 = getSprite(itemDefinition.notedId, 10, -1)) == null) {
         return null;
      }

      Sprite sprite2 = new Sprite(32, 32);
      int viewportCenterX = Rasterizer3D.viewportCenterX;
      int viewportCenterY = Rasterizer3D.viewportCenterY;
      int[] scanOffsets = Rasterizer3D.scanOffsets;
      int[] pixels = Rasterizer2D.pixels;
      float[] depthBuffer = Rasterizer2D.depthBuffer;
      int width = Rasterizer2D.width;
      int height = Rasterizer2D.height;
      int topX = Rasterizer2D.topX;
      int bottomX = Rasterizer2D.bottomX;
      int topY = Rasterizer2D.topY;
      int bottomY = Rasterizer2D.bottomY;
      Rasterizer3D.renderModeFlag = false;
      Rasterizer2D.setRasterBuffer(32, 32, sprite2.pixels, new float[1024]);
      Rasterizer2D.fillRectangle(32, 0, 0, 0, 32);
      Rasterizer3D.setViewportFromRaster();
      int canvasWidthOrZoom2d = itemDefinition.zoom2d;
      if (pixel == -1) {
         canvasWidthOrZoom2d = (int)(canvasWidthOrZoom2d * 1.5);
      }

      if (pixel > 0) {
         canvasWidthOrZoom2d = (int)(canvasWidthOrZoom2d * 1.04);
      }

      int localSINE = Rasterizer3D.SINE[itemDefinition.xan2d] * canvasWidthOrZoom2d >> 16;
      canvasWidthOrZoom2d = Rasterizer3D.COSINE[itemDefinition.xan2d] * canvasWidthOrZoom2d >> 16;
      model.renderSimple(itemDefinition.yan2d, itemDefinition.zan2d, itemDefinition.xan2d, itemDefinition.xOffset2d, localSINE + model.modelHeight / 2 + itemDefinition.yOffset2d, canvasWidthOrZoom2d + itemDefinition.yOffset2d);

      for (int loopIndex = 31; loopIndex >= 0; loopIndex--) {
         for (int loopIndex2 = 31; loopIndex2 >= 0; loopIndex2--) {
            if (sprite2.pixels[loopIndex + (loopIndex2 << 5)] == 0) {
               if (loopIndex > 0 && sprite2.pixels[loopIndex - 1 + (loopIndex2 << 5)] > 1) {
                  sprite2.pixels[loopIndex + (loopIndex2 << 5)] = 1;
               } else if (loopIndex2 > 0 && sprite2.pixels[loopIndex + (loopIndex2 - 1 << 5)] > 1) {
                  sprite2.pixels[loopIndex + (loopIndex2 << 5)] = 1;
               } else if (loopIndex < 31 && sprite2.pixels[loopIndex + 1 + (loopIndex2 << 5)] > 1) {
                  sprite2.pixels[loopIndex + (loopIndex2 << 5)] = 1;
               } else if (loopIndex2 < 31 && sprite2.pixels[loopIndex + (loopIndex2 + 1 << 5)] > 1) {
                  sprite2.pixels[loopIndex + (loopIndex2 << 5)] = 1;
               }
            }
         }
      }

      if (pixel > 0) {
         for (int loopIndex3 = 31; loopIndex3 >= 0; loopIndex3--) {
            for (int loopIndex4 = 31; loopIndex4 >= 0; loopIndex4--) {
               if (sprite2.pixels[loopIndex3 + (loopIndex4 << 5)] == 0) {
                  if (loopIndex3 > 0 && sprite2.pixels[loopIndex3 - 1 + (loopIndex4 << 5)] == 1) {
                     sprite2.pixels[loopIndex3 + (loopIndex4 << 5)] = pixel;
                  } else if (loopIndex4 > 0 && sprite2.pixels[loopIndex3 + (loopIndex4 - 1 << 5)] == 1) {
                     sprite2.pixels[loopIndex3 + (loopIndex4 << 5)] = pixel;
                  } else if (loopIndex3 < 31 && sprite2.pixels[loopIndex3 + 1 + (loopIndex4 << 5)] == 1) {
                     sprite2.pixels[loopIndex3 + (loopIndex4 << 5)] = pixel;
                  } else if (loopIndex4 < 31 && sprite2.pixels[loopIndex3 + (loopIndex4 + 1 << 5)] == 1) {
                     sprite2.pixels[loopIndex3 + (loopIndex4 << 5)] = pixel;
                  }
               }
            }
         }
      } else if (pixel == 0) {
         for (int loopIndex5 = 31; loopIndex5 >= 0; loopIndex5--) {
            for (int loopIndex6 = 31; loopIndex6 >= 0; loopIndex6--) {
               if (sprite2.pixels[loopIndex5 + (loopIndex6 << 5)] == 0 && loopIndex5 > 0 && loopIndex6 > 0 && sprite2.pixels[loopIndex5 - 1 + (loopIndex6 - 1 << 5)] > 0) {
                  sprite2.pixels[loopIndex5 + (loopIndex6 << 5)] = 3153952;
               }
            }
         }
      }

      if (itemDefinition.noteTemplateId != -1) {
         canvasWidthOrZoom2d = sprite3.canvasWidth;
         int canvasHeight = sprite3.canvasHeight;
         sprite3.canvasWidth = 32;
         sprite3.canvasHeight = 32;
         sprite3.drawSprite(0, 0);
         sprite3.canvasWidth = canvasWidthOrZoom2d;
         sprite3.canvasHeight = canvasHeight;
      }

      if (pixel == 0) {
         spriteCache.put(sprite2, key);
      }

      Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
      Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
      Rasterizer3D.viewportCenterX = viewportCenterX;
      Rasterizer3D.viewportCenterY = viewportCenterY;
      Rasterizer3D.scanOffsets = scanOffsets;
      Rasterizer3D.renderModeFlag = true;
      if (itemDefinition.stackable) {
         sprite2.canvasWidth = 33;
      } else {
         sprite2.canvasWidth = 32;
      }

      sprite2.canvasHeight = newCanvasHeight;
      return sprite2;
   }
   public final Model getModel(int quantity) {
      if (this.stackIds != null && quantity > 1) {
         int localStackIds = -1;

         for (int stackAmountIndex = 0; stackAmountIndex < 10; stackAmountIndex++) {
            if (quantity >= this.stackAmounts[stackAmountIndex] && this.stackAmounts[stackAmountIndex] != 0) {
               localStackIds = this.stackIds[stackAmountIndex];
            }
         }

         if (localStackIds != -1) {
            return lookup(localStackIds).getModel(1);
         }
      }

      Model model;
      if ((model = (Model)modelCache.get(this.id)) != null) {
         return model;
      }

      if ((model = Model.getModel(this.inventoryModelId)) == null) {
         return null;
      }

      if (this.groundScaleX != 128 || this.groundScaleY != 128 || this.groundScaleZ != 128) {
         model.scale(this.groundScaleX, this.groundScaleZ, this.groundScaleY);
      }

      if (this.recolorToFind != null) {
         for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
            model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
         }
      }

      if (this.recolorFrom != null) {
         for (int recolorFromIndex = 0; recolorFromIndex < this.recolorFrom.length; recolorFromIndex++) {
            model.setTriangleAlphaForColor(this.recolorFrom[recolorFromIndex], this.recolorTo[recolorFromIndex]);
         }
      }

      model.light(64 + this.ambient, 768 + this.contrast, -50, -10, -50, true);
      model.singleTile = true;
      modelCache.put(model, this.id);
      return model;
   }
   public final Model getUnshadedModel(int scalarArgument) {
      if (this.stackIds != null && scalarArgument > 1) {
         int localStackIds = -1;

         for (int stackAmountIndex = 0; stackAmountIndex < 10; stackAmountIndex++) {
            if (scalarArgument >= this.stackAmounts[stackAmountIndex] && this.stackAmounts[stackAmountIndex] != 0) {
               localStackIds = this.stackIds[stackAmountIndex];
            }
         }

         if (localStackIds != -1) {
            return lookup(localStackIds).getUnshadedModel(1);
         }
      }

      Model model;
      if ((model = Model.getModel(this.inventoryModelId)) == null) {
         return null;
      }

      if (this.recolorToFind != null) {
         for (int recolorToFindIndex = 0; recolorToFindIndex < this.recolorToFind.length; recolorToFindIndex++) {
            model.recolor(this.recolorToFind[recolorToFindIndex], this.recolorToReplace[recolorToFindIndex]);
         }
      }

      if (this.recolorFrom != null) {
         for (int recolorFromIndex = 0; recolorFromIndex < this.recolorFrom.length; recolorFromIndex++) {
            model.setTriangleAlphaForColor(this.recolorFrom[recolorFromIndex], this.recolorTo[recolorFromIndex]);
         }
      }

      return model;
   }
   private void readValues(Buffer buffer) {
      int inventoryOptionIndex;
      while ((inventoryOptionIndex = buffer.readUnsignedByte()) != 0) {
         if (inventoryOptionIndex == 1) {
            this.inventoryModelId = buffer.readUnsignedShort();
         } else if (inventoryOptionIndex == 2) {
            this.name = buffer.readString();
         } else if (inventoryOptionIndex == 3) {
            this.description = buffer.readBytes();
         } else if (inventoryOptionIndex == 4) {
            this.zoom2d = buffer.readUnsignedShort();
         } else if (inventoryOptionIndex == 5) {
            this.xan2d = buffer.readUnsignedShort();
         } else if (inventoryOptionIndex == 6) {
            this.yan2d = buffer.readUnsignedShort();
         } else if (inventoryOptionIndex == 7) {
            this.xOffset2d = buffer.readUnsignedShort();
            if (this.xOffset2d > 32767) {
               this.xOffset2d -= 65536;
            }
         } else if (inventoryOptionIndex == 8) {
            this.yOffset2d = buffer.readUnsignedShort();
            if (this.yOffset2d > 32767) {
               this.yOffset2d -= 65536;
            }
         } else if (inventoryOptionIndex == 10) {
            buffer.readUnsignedShort();
         } else if (inventoryOptionIndex == 11) {
            this.stackable = true;
         } else if (inventoryOptionIndex == 12) {
            this.value = buffer.readInt();
         } else if (inventoryOptionIndex == 16) {
            this.members = true;
         } else if (inventoryOptionIndex == 23) {
            this.maleModel0 = buffer.readUnsignedShort();
            this.maleYOffset = buffer.readByte();
         } else if (inventoryOptionIndex == 24) {
            this.maleModel1 = buffer.readUnsignedShort();
         } else if (inventoryOptionIndex == 25) {
            this.femaleModel0 = buffer.readUnsignedShort();
            this.femaleYOffset = buffer.readByte();
         } else if (inventoryOptionIndex == 26) {
            this.femaleModel1 = buffer.readUnsignedShort();
         } else if (inventoryOptionIndex >= 30 && inventoryOptionIndex < 35) {
            if (this.groundOptions == null) {
               this.groundOptions = new String[5];
            }

            this.groundOptions[inventoryOptionIndex - 30] = buffer.readString();
            if (this.groundOptions[inventoryOptionIndex - 30].equalsIgnoreCase("hidden")) {
               this.groundOptions[inventoryOptionIndex - 30] = null;
            }
         } else if (inventoryOptionIndex >= 35 && inventoryOptionIndex < 40) {
            if (this.inventoryOptions == null) {
               this.inventoryOptions = new String[5];
            }

            inventoryOptionIndex -= 35;
            this.inventoryOptions[inventoryOptionIndex] = buffer.readString();
            if (inventoryOptionIndex == 4 && this.inventoryOptions[inventoryOptionIndex] != null && this.inventoryOptions[inventoryOptionIndex].toLowerCase().equals("drop")) {
               this.inventoryOptions[inventoryOptionIndex] = null;
            }
         } else if (inventoryOptionIndex == 40) {
            inventoryOptionIndex = buffer.readUnsignedByte();
            this.recolorToFind = new int[inventoryOptionIndex];
            this.recolorToReplace = new int[inventoryOptionIndex];

            for (int recolorToFindIndex = 0; recolorToFindIndex < inventoryOptionIndex; recolorToFindIndex++) {
               this.recolorToFind[recolorToFindIndex] = buffer.readUnsignedShort();
               this.recolorToReplace[recolorToFindIndex] = buffer.readUnsignedShort();
            }
         } else if (inventoryOptionIndex == 41) {
            inventoryOptionIndex = buffer.readUnsignedByte();

            for (int loopIndex = 0; loopIndex < inventoryOptionIndex; loopIndex++) {
               buffer.readUnsignedShort();
               buffer.readUnsignedShort();
            }
         } else if (inventoryOptionIndex == 42) {
            buffer.readUnsignedByte();
         } else if (inventoryOptionIndex != 65) {
            if (inventoryOptionIndex == 139) {
               buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 148) {
               buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 149) {
               buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 78) {
               this.maleModel2 = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 79) {
               this.femaleModel2 = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 90) {
               this.primaryMaleHeadPiece = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 91) {
               this.primaryFemaleHeadPiece = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 92) {
               this.secondaryMaleHeadPiece = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 93) {
               this.secondaryFemaleHeadPiece = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 95) {
               this.zan2d = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 97) {
               this.notedId = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 98) {
               this.noteTemplateId = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex >= 100 && inventoryOptionIndex < 110) {
               if (this.stackIds == null) {
                  this.stackIds = new int[10];
                  this.stackAmounts = new int[10];
               }

               this.stackIds[inventoryOptionIndex - 100] = buffer.readUnsignedShort();
               this.stackAmounts[inventoryOptionIndex - 100] = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 110) {
               this.groundScaleX = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 111) {
               this.groundScaleY = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 112) {
               this.groundScaleZ = buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 113) {
               this.ambient = buffer.readByte();
            } else if (inventoryOptionIndex == 114) {
               this.contrast = buffer.readByte() * 5;
            } else if (inventoryOptionIndex == 115) {
               this.teamIndex = buffer.readUnsignedByte();
            } else if (inventoryOptionIndex == 121) {
               buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 122) {
               buffer.readUnsignedShort();
            } else if (inventoryOptionIndex == 125) {
               this.maleXOffset = buffer.readByte();
               this.maleZOffset = buffer.readByte();
            } else if (inventoryOptionIndex == 126) {
               this.femaleXOffset = buffer.readByte();
               this.femaleZOffset = buffer.readByte();
            } else if (inventoryOptionIndex == 140) {
               inventoryOptionIndex = buffer.readUnsignedByte();
               this.recolorFrom = new int[inventoryOptionIndex];
               this.recolorTo = new int[inventoryOptionIndex];

               for (int recolorFromIndex = 0; recolorFromIndex < inventoryOptionIndex; recolorFromIndex++) {
                  this.recolorFrom[recolorFromIndex] = buffer.readUnsignedShort();
                  this.recolorTo[recolorFromIndex] = buffer.readUnsignedShort();
               }
            } else if (inventoryOptionIndex == 177) {
               this.searchable = true;
            } else {
               System.out.println("Error unrecognised item opcode: " + inventoryOptionIndex);
            }
         }
      }
   }
}
