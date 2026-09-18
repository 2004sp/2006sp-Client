package worldmap;

import client.SignLink;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
public final class WorldMapViewer extends WorldMapGameShell {
   private static WorldMapViewer instance;
   private int buttonHighlightColor;
   private int buttonFillColor;
   private int buttonShadowColor;
   private int selectedButtonHighlightColor;
   private int selectedButtonFillColor;
   private int selectedButtonShadowColor;
   private boolean redrawNeeded;
   private int frameBlitCountdown;
   private static int mapOriginX;
   private static int mapOriginY;
   private static int mapWidth;
   private static int mapHeight;
   private int[] underlayHslData;
   private int[] overlayRgbColors;
   private int[][] underlayColorMap;
   private int[][] overlayColorMap;
   private byte[][] overlayShapeRotation;
   private byte[][] wallTypes;
   private byte[][] mapFunctionIds;
   private byte[][] mapSceneIds;
   private WorldMapIndexedSprite[] mapIcons;
   private WorldMapSprite[] mapSprites;
   private WorldMapBitmapFont bitmapFont;
   private WorldMapSystemFont labelFont11;
   private WorldMapSystemFont labelFont12;
   private WorldMapSystemFont labelFont14;
   private WorldMapSystemFont labelFont17;
   private WorldMapSystemFont labelFont19;
   private WorldMapSystemFont labelFont22;
   private WorldMapSystemFont labelFont26;
   private WorldMapSystemFont labelFont30;
   private int[] markerScreenX;
   private int[] markerScreenY;
   private int[] markerIconIds;
   private int mapFunctionCount;
   private int[] mapFunctionX;
   private int[] mapFunctionY;
   private int[] mapFunctionTypes;
   private int keyPanelX;
   private int keyPanelY;
   private int keyPanelWidth;
   private int keyPanelHeight;
   private int keyScrollOffset;
   private int targetKeyScrollOffset;
   private boolean keyVisible;
   private int hoveredKeyIndex;
   private int previousHoveredKeyIndex;
   private int selectedMapFunctionType;
   private int selectionFlashTicks;
   private int overviewHeight;
   private int overviewWidth;
   private int overviewX;
   private int overviewY;
   private boolean overviewVisible;
   private WorldMapSprite overviewSprite;
   private int dragStartMouseX;
   private int dragStartMouseY;
   private int dragStartCenterX;
   private int dragStartCenterY;
   private static boolean showMapLabels = true;
   private int labelCount;
   private int labelCapacity;
   private String[] labelTexts;
   private int[] labelX;
   private int[] labelY;
   private int[] labelStyle;
   private double currentZoom;
   private double targetZoom;
   private static int viewCenterX;
   private static int viewCenterY;
   private String[] mapIconLabels = new String[]{
      "General Store",
      "Sword Shop",
      "Magic Shop",
      "Axe Shop",
      "Helmet Shop",
      "Bank",
      "Quest Start",
      "Amulet Shop",
      "Mining Site",
      "Furnace",
      "Anvil",
      "Combat Training",
      "Dungeon",
      "Staff Shop",
      "Platebody Shop",
      "Platelegs Shop",
      "Scimitar Shop",
      "Archery Shop",
      "Shield Shop",
      "Altar",
      "Herbalist",
      "Jewelery",
      "Gem Shop",
      "Crafting Shop",
      "Candle Shop",
      "Fishing Shop",
      "Fishing Spot",
      "Clothes Shop",
      "Apothecary",
      "Silk Trader",
      "Kebab Seller",
      "Pub/Bar",
      "Mace Shop",
      "Tannery",
      "Rare Trees",
      "Spinning Wheel",
      "Food Shop",
      "Cookery Shop",
      "Mini-Game",
      "Water Source",
      "Cooking Range",
      "Skirt Shop",
      "Potters Wheel",
      "Windmill",
      "Mining Shop",
      "Chainmail Shop",
      "Silver Shop",
      "Fur Trader",
      "Spice Shop",
      "Agility Training",
      "Vegetable Store",
      "Slayer Master",
      "Hair Dressers",
      "Farming patch",
      "Makeover Mage",
      "Guide",
      "Transportation",
      "???",
      "Farming shop",
      "Loom",
      "Brewery"
   };
   private static final int CONTROL_PANEL_MAP_PIXELS_PER_TILE = 8;
   private static final int CONTROL_PANEL_MAP_TILE_GAME_SIZE = 128;
   private static final String CONTROL_PANEL_MAP_DIRECTORY = "world_map_control_panel_tiles";

   public static boolean exportControlPanelMapTilesNow(String cacheDirectory) {
      try {
         File worldMapFile = new File(cacheDirectory, "worldmap.dat");
         File tileDirectory = new File(cacheDirectory, CONTROL_PANEL_MAP_DIRECTORY);
         File manifestFile = new File(tileDirectory, "manifest.txt");
         if (!worldMapFile.exists()) {
            System.err.println("worldmap.dat not found: " + worldMapFile.getPath());
            return false;
         }
         if (manifestFile.exists() && manifestFile.lastModified() >= worldMapFile.lastModified()) {
            System.out.println("Lossless tiled control-panel map is already up to date.");
            return true;
         }
         return exportControlPanelMapTiles(worldMapFile, tileDirectory, manifestFile);
      } catch (Throwable throwable) {
         System.err.println("Unable to export tiled control-panel world map: " + throwable.getClass().getName() + ": " + throwable.getMessage());
         throwable.printStackTrace();
         return false;
      }
   }

   public static void exportControlPanelMapIfNeededAsync() {
      Thread exportThread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               String cacheDirectory = SignLink.findcachedir();
               File worldMapFile = new File(cacheDirectory, "worldmap.dat");
               File tileDirectory = new File(cacheDirectory, CONTROL_PANEL_MAP_DIRECTORY);
               File manifestFile = new File(tileDirectory, "manifest.txt");
               if (!worldMapFile.exists()) {
                  return;
               }
               if (manifestFile.exists() && manifestFile.lastModified() >= worldMapFile.lastModified()) {
                  return;
               }
               exportControlPanelMapTiles(worldMapFile, tileDirectory, manifestFile);
            } catch (Throwable throwable) {
               System.err.println("Unable to export tiled control-panel world map: " + throwable.getClass().getName() + ": " + throwable.getMessage());
         throwable.printStackTrace();
            }
         }
      }, "ControlPanelWorldMapExporter");
      exportThread.setDaemon(true);
      exportThread.start();
   }

   private static boolean exportControlPanelMapTiles(File worldMapFile, File tileDirectory, File manifestFile) {
      try {
         byte[] worldMapData = WorldMapFileLoader.readFile(worldMapFile.getPath());
         if (worldMapData == null) {
            return false;
         }
         WorldMapViewer renderer = new WorldMapViewer();
         renderer.prepareExportMapData(new WorldMapArchive(worldMapData));
         renderer.currentZoom = 4.0;
         renderer.targetZoom = 6.0;

         if (!tileDirectory.exists()) {
            tileDirectory.mkdirs();
         }
         manifestFile.delete();
         File[] oldFiles = tileDirectory.listFiles();
         if (oldFiles != null) {
            for (int index = 0; index < oldFiles.length; ++index) {
               String name = oldFiles[index].getName();
               if (name.endsWith(".png") || name.endsWith(".tmp")) {
                  oldFiles[index].delete();
               }
            }
         }

         int columnCount = (mapWidth + CONTROL_PANEL_MAP_TILE_GAME_SIZE - 1) / CONTROL_PANEL_MAP_TILE_GAME_SIZE;
         int rowCount = (mapHeight + CONTROL_PANEL_MAP_TILE_GAME_SIZE - 1) / CONTROL_PANEL_MAP_TILE_GAME_SIZE;
         System.out.println(
            "Rendering lossless tiled control-panel map: "
               + (mapWidth * CONTROL_PANEL_MAP_PIXELS_PER_TILE) + "x"
               + (mapHeight * CONTROL_PANEL_MAP_PIXELS_PER_TILE)
               + " in " + columnCount + "x" + rowCount + " tiles"
         );

         for (int tileY = 0; tileY < rowCount; ++tileY) {
            int startY = tileY * CONTROL_PANEL_MAP_TILE_GAME_SIZE;
            int endY = Math.min(mapHeight, startY + CONTROL_PANEL_MAP_TILE_GAME_SIZE);
            for (int tileX = 0; tileX < columnCount; ++tileX) {
               int startX = tileX * CONTROL_PANEL_MAP_TILE_GAME_SIZE;
               int endX = Math.min(mapWidth, startX + CONTROL_PANEL_MAP_TILE_GAME_SIZE);
               int outputWidth = (endX - startX) * CONTROL_PANEL_MAP_PIXELS_PER_TILE;
               int outputHeight = (endY - startY) * CONTROL_PANEL_MAP_PIXELS_PER_TILE;

               WorldMapSprite mapTile = new WorldMapSprite(outputWidth, outputHeight);
               mapTile.initDrawingArea();
               renderer.renderMapRegion(startX, startY, endX, endY, 0, 0, outputWidth, outputHeight);

               DataBufferInt dataBuffer = new DataBufferInt(mapTile.pixels, mapTile.pixels.length);
               int[] channelMasks = new int[]{16711680, 65280, 255};
               WritableRaster raster = Raster.createPackedRaster(dataBuffer, outputWidth, outputHeight, outputWidth, channelMasks, null);
               ColorModel colorModel = new DirectColorModel(24, 16711680, 65280, 255);
               BufferedImage image = new BufferedImage(colorModel, raster, false, null);
               File tileFile = new File(tileDirectory, "tile_" + tileX + "_" + tileY + ".png");
               File temporaryTileFile = new File(tileFile.getPath() + ".tmp");
               temporaryTileFile.delete();
               if (!ImageIO.write(image, "png", temporaryTileFile)) {
                  throw new IOException("PNG writer unavailable");
               }
               Files.move(temporaryTileFile.toPath(), tileFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
         }

         String manifest = mapOriginX + ";" + mapOriginY + ";" + mapWidth + ";" + mapHeight + ";"
            + CONTROL_PANEL_MAP_PIXELS_PER_TILE + ";" + CONTROL_PANEL_MAP_TILE_GAME_SIZE + ";"
            + columnCount + ";" + rowCount;
         File temporaryManifestFile = new File(manifestFile.getPath() + ".tmp");
         FileOutputStream manifestOutput = new FileOutputStream(temporaryManifestFile);
         try {
            manifestOutput.write(manifest.getBytes("UTF-8"));
         } finally {
            manifestOutput.close();
         }
         Files.move(temporaryManifestFile.toPath(), manifestFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         System.out.println("Saved lossless tiled control-panel map: " + tileDirectory.getPath());
         return true;
      } catch (Throwable throwable) {
         manifestFile.delete();
         System.err.println("Unable to render lossless tiled control-panel map: " + throwable.getClass().getName() + ": " + throwable.getMessage());
         throwable.printStackTrace();
         return false;
      }
   }

   private void prepareExportMapData(WorldMapArchive worldMapArchive) {
      WorldMapBuffer worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile("size.dat", null));
      mapOriginX = worldMapBuffer.readUnsignedShort();
      mapOriginY = worldMapBuffer.readUnsignedShort();
      mapWidth = worldMapBuffer.readUnsignedShort();
      mapHeight = worldMapBuffer.readUnsignedShort();

      int floorColorCount = (worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile("floorcol.dat", null))).readUnsignedShort();
      this.underlayHslData = new int[floorColorCount + 1];
      this.overlayRgbColors = new int[floorColorCount + 1];
      for (int index = 0; index < floorColorCount; index++) {
         this.underlayHslData[index + 1] = worldMapBuffer.getInt();
         this.overlayRgbColors[index + 1] = worldMapBuffer.getInt();
      }

      byte[][] underlayIds = new byte[mapWidth][mapHeight];
      decodeUnderlayData(worldMapArchive.getFile("underlay.dat", null), underlayIds);
      this.overlayColorMap = new int[mapWidth][mapHeight];
      this.overlayShapeRotation = new byte[mapWidth][mapHeight];
      this.decodeOverlayData(worldMapArchive.getFile("overlay.dat", null), this.overlayColorMap, this.overlayShapeRotation);
      this.wallTypes = new byte[mapWidth][mapHeight];
      this.mapSceneIds = new byte[mapWidth][mapHeight];
      this.mapFunctionIds = new byte[mapWidth][mapHeight];
      this.decodeLocationData(worldMapArchive.getFile("loc.dat", null), this.wallTypes, this.mapSceneIds, this.mapFunctionIds);

       boolean[] requiredMapScenes = new boolean[this.mapIcons.length];
       boolean[] requiredMapFunctions = new boolean[this.mapSprites.length];
       for (int x = 0; x < mapWidth; ++x) {
          for (int y = 0; y < mapHeight; ++y) {
             int mapSceneId = this.mapSceneIds[x][y] & 255;
             if (mapSceneId > 0 && mapSceneId <= requiredMapScenes.length) {
                requiredMapScenes[mapSceneId - 1] = true;
             }
             int mapFunctionId = this.mapFunctionIds[x][y] & 255;
             if (mapFunctionId > 0 && mapFunctionId <= requiredMapFunctions.length) {
                requiredMapFunctions[mapFunctionId - 1] = true;
             }
          }
       }

      for (int mapIconIndex = 0; mapIconIndex < this.mapIcons.length; mapIconIndex++) {
          if (!requiredMapScenes[mapIconIndex]) {
             continue;
          }
         try {
            this.mapIcons[mapIconIndex] = new WorldMapIndexedSprite(worldMapArchive, "mapscene", mapIconIndex);
         } catch (Exception ignored) {
         }
      }
      for (int mapSpriteIndex = 0; mapSpriteIndex < this.mapSprites.length; mapSpriteIndex++) {
          if (!requiredMapFunctions[mapSpriteIndex]) {
             continue;
          }
         try {
            this.mapSprites[mapSpriteIndex] = new WorldMapSprite(worldMapArchive, "mapfunction", mapSpriteIndex);
         } catch (Exception ignored) {
         }
      }
      this.underlayColorMap = new int[mapWidth][mapHeight];
      this.buildUnderlayColorMap(underlayIds, this.underlayColorMap);
   }

   public static void showWorldMap() {
      if (instance == null) {
         WorldMapViewer worldMapViewer = instance = new WorldMapViewer();
         short shortCode = 503;
         shortCode = 635;
         WorldMapViewer sourceWorldMapViewer = worldMapViewer;
         worldMapViewer.canvasWidth = 635;
         sourceWorldMapViewer.canvasHeight = 503;
         sourceWorldMapViewer.frame = new WorldMapFrame(sourceWorldMapViewer, sourceWorldMapViewer.canvasWidth, sourceWorldMapViewer.canvasHeight);
         sourceWorldMapViewer.graphics = sourceWorldMapViewer.getGameComponent().getGraphics();
         sourceWorldMapViewer.graphicsBuffer = new WorldMapGraphicsBuffer(sourceWorldMapViewer.canvasWidth, sourceWorldMapViewer.canvasHeight, sourceWorldMapViewer.getGameComponent());
         WorldMapGameShell.startRunnable(sourceWorldMapViewer, 1);
      } else {
         instance.frame.setVisible(true);
      }
   }

   @Override
   public final void init() {
      short shortCode = 503;
      shortCode = 635;
      WorldMapViewer worldMapViewer = this;
      super.canvasWidth = 635;
      worldMapViewer.canvasHeight = 503;
      worldMapViewer.graphics = worldMapViewer.getGameComponent().getGraphics();
      worldMapViewer.graphicsBuffer = new WorldMapGraphicsBuffer(worldMapViewer.canvasWidth, worldMapViewer.canvasHeight, worldMapViewer.getGameComponent());
      WorldMapGameShell.startRunnable(worldMapViewer, 1);
   }
   @Override
   public final void startUp() {
      WorldMapArchive worldMapArchive = this.loadMapArchive();
      this.drawLoadingText(100, "Please wait... Rendering Map");
      WorldMapBuffer worldMapBuffer;
      mapOriginX = (worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile("size.dat", null))).readUnsignedShort();
      mapOriginY = worldMapBuffer.readUnsignedShort();
      mapWidth = worldMapBuffer.readUnsignedShort();
      mapHeight = worldMapBuffer.readUnsignedShort();
      viewCenterX = 3200 - mapOriginX;
      viewCenterY = mapOriginY + mapHeight - 3200;
      this.overviewHeight = 180;
      this.overviewWidth = mapWidth * this.overviewHeight / mapHeight;
      this.overviewX = 635 - this.overviewWidth - 5;
      this.overviewY = 503 - this.overviewHeight - 20;
      worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile("labels.dat", null));
      this.labelCount = worldMapBuffer.readUnsignedShort();

      for (int labelTextArrayIndex = 0; labelTextArrayIndex < this.labelCount; labelTextArrayIndex++) {
         String[] labelTextArray = this.labelTexts;
         WorldMapBuffer sourceWorldMapBuffer = worldMapBuffer;
         int currentPosition = worldMapBuffer.currentPosition;

         while (sourceWorldMapBuffer.buffer[sourceWorldMapBuffer.currentPosition++] != 10) {
         }

         labelTextArray[labelTextArrayIndex] = new String(sourceWorldMapBuffer.buffer, currentPosition, sourceWorldMapBuffer.currentPosition - currentPosition - 1);
         this.labelX[labelTextArrayIndex] = worldMapBuffer.readUnsignedShort();
         this.labelY[labelTextArrayIndex] = worldMapBuffer.readUnsignedShort();
         this.labelStyle[labelTextArrayIndex] = worldMapBuffer.readUnsignedByte();
      }

      int decodedUnsignedShort = (worldMapBuffer = new WorldMapBuffer(worldMapArchive.getFile("floorcol.dat", null))).readUnsignedShort();
      this.underlayHslData = new int[decodedUnsignedShort + 1];
      this.overlayRgbColors = new int[decodedUnsignedShort + 1];

      for (int loopIndex = 0; loopIndex < decodedUnsignedShort; loopIndex++) {
         this.underlayHslData[loopIndex + 1] = worldMapBuffer.getInt();
         this.overlayRgbColors[loopIndex + 1] = worldMapBuffer.getInt();
      }

      byte[] underlayData = worldMapArchive.getFile("underlay.dat", null);
      byte[][] workingArray = new byte[mapWidth][mapHeight];
      decodeUnderlayData(underlayData, workingArray);
      byte[] overlayData = worldMapArchive.getFile("overlay.dat", null);
      this.overlayColorMap = new int[mapWidth][mapHeight];
      this.overlayShapeRotation = new byte[mapWidth][mapHeight];
      this.decodeOverlayData(overlayData, this.overlayColorMap, this.overlayShapeRotation);
      byte[] locationData = worldMapArchive.getFile("loc.dat", null);
      this.wallTypes = new byte[mapWidth][mapHeight];
      this.mapSceneIds = new byte[mapWidth][mapHeight];
      this.mapFunctionIds = new byte[mapWidth][mapHeight];
      this.decodeLocationData(locationData, this.wallTypes, this.mapSceneIds, this.mapFunctionIds);

      try {
         for (int mapIconIndex = 0; mapIconIndex < 100; mapIconIndex++) {
            this.mapIcons[mapIconIndex] = new WorldMapIndexedSprite(worldMapArchive, "mapscene", mapIconIndex);
         }
      } catch (Exception exception) {
      }

      try {
         for (int mapSpriteIndex = 0; mapSpriteIndex < 100; mapSpriteIndex++) {
            this.mapSprites[mapSpriteIndex] = new WorldMapSprite(worldMapArchive, "mapfunction", mapSpriteIndex);
         }
      } catch (Exception exception2) {
      }

      this.bitmapFont = new WorldMapBitmapFont(worldMapArchive, "b12_full", false);
      this.labelFont11 = new WorldMapSystemFont(11, true, this);
      this.labelFont12 = new WorldMapSystemFont(12, true, this);
      this.labelFont14 = new WorldMapSystemFont(14, true, this);
      this.labelFont17 = new WorldMapSystemFont(17, true, this);
      this.labelFont19 = new WorldMapSystemFont(19, true, this);
      this.labelFont22 = new WorldMapSystemFont(22, true, this);
      this.labelFont26 = new WorldMapSystemFont(26, true, this);
      this.labelFont30 = new WorldMapSystemFont(30, true, this);
      this.underlayColorMap = new int[mapWidth][mapHeight];
      this.buildUnderlayColorMap(workingArray, this.underlayColorMap);
      this.overviewSprite = new WorldMapSprite(this.overviewWidth, this.overviewHeight);
      this.overviewSprite.initDrawingArea();
      this.renderMapRegion(0, 0, mapWidth, mapHeight, 0, 0, this.overviewWidth, this.overviewHeight);
      WorldMapRasterizer2D.drawRectangle(0, 0, this.overviewWidth, this.overviewHeight, 0);
      WorldMapRasterizer2D.drawRectangle(1, 1, this.overviewWidth - 2, this.overviewHeight - 2, this.buttonHighlightColor);
      super.graphicsBuffer.initDrawingArea();
   }
   private void decodeLocationData(byte[] locationData, byte[][] newData, byte[][] mapSceneIds, byte[][] mapFunctionIds) {
      int position = 0;

      while (position < locationData.length) {
         int scalar = ((locationData[position++] & 255) << 6) - mapOriginX;
         int scalar2 = ((locationData[position++] & 255) << 6) - mapOriginY;
         if (scalar > 0 && scalar2 > 0 && scalar + 64 < mapWidth && scalar2 + 64 < mapHeight) {
            for (int loopIndex = 0; loopIndex < 64; loopIndex++) {
               byte[] newDataEntry = newData[loopIndex + scalar];
               byte[] mapSceneId = mapSceneIds[loopIndex + scalar];
               byte[] mapFunctionId = mapFunctionIds[loopIndex + scalar];
               int mapSceneIdIndex = mapHeight - scalar2 - 1;

               for (int loopIndex2 = -64; loopIndex2 < 0; loopIndex2++) {
                  int locationCode;
                  while ((locationCode = locationData[position++] & 255) != 0) {
                     if (locationCode < 29) {
                        newDataEntry[mapSceneIdIndex] = (byte)locationCode;
                     } else if (locationCode < 160) {
                        mapSceneId[mapSceneIdIndex] = (byte)(locationCode - 28);
                     } else {
                        mapFunctionId[mapSceneIdIndex] = (byte)(locationCode - 159);
                        this.mapFunctionX[this.mapFunctionCount] = loopIndex + scalar;
                        this.mapFunctionY[this.mapFunctionCount] = mapSceneIdIndex;
                        this.mapFunctionTypes[this.mapFunctionCount] = locationCode - 160;
                        this.mapFunctionCount++;
                     }
                  }

                  mapSceneIdIndex--;
               }
            }
         } else {
            for (int loopIndex3 = 0; loopIndex3 < 64; loopIndex3++) {
               byte locationDataEntry;
               for (int loopIndex4 = -64; loopIndex4 < 0; loopIndex4++) {
                  while ((locationDataEntry = locationData[position++]) != 0) {
                  }
               }
            }
         }
      }
   }
   private static void decodeUnderlayData(byte[] underlayData, byte[][] newData) {
      int position = 0;

      while (position < underlayData.length) {
         int scalar = ((underlayData[position++] & 255) << 6) - mapOriginX;
         int scalar2 = ((underlayData[position++] & 255) << 6) - mapOriginY;
         if (scalar > 0 && scalar2 > 0 && scalar + 64 < mapWidth && scalar2 + 64 < mapHeight) {
            for (int loopIndex = 0; loopIndex < 64; loopIndex++) {
               byte[] newDataEntry = newData[loopIndex + scalar];
               int localMapHeight = mapHeight - scalar2 - 1;

               for (int loopIndex2 = -64; loopIndex2 < 0; loopIndex2++) {
                  newDataEntry[localMapHeight--] = underlayData[position++];
               }
            }
         } else {
            position += 4096;
         }
      }
   }
   private void decodeOverlayData(byte[] overlayData, int[][] values, byte[][] newData) {
      int position = 0;

      while (position < overlayData.length) {
         int scalar = ((overlayData[position++] & 255) << 6) - mapOriginX;
         int scalar2 = ((overlayData[position++] & 255) << 6) - mapOriginY;
         if (scalar > 0 && scalar2 > 0 && scalar + 64 < mapWidth && scalar2 + 64 < mapHeight) {
            for (int loopIndex = 0; loopIndex < 64; loopIndex++) {
               int[] integerBuffer = values[loopIndex + scalar];
               byte[] newDataEntry = newData[loopIndex + scalar];
               int position2 = mapHeight - scalar2 - 1;

               for (int loopIndex2 = -64; loopIndex2 < 0; loopIndex2++) {
                  byte overlayRgbColorIndex;
                  if ((overlayRgbColorIndex = overlayData[position++]) != 0) {
                     newDataEntry[position2] = overlayData[position++];
                     int overlayColor = 0;
                     if (overlayRgbColorIndex > 0) {
                        overlayColor = this.overlayRgbColors[overlayRgbColorIndex];
                     }

                     integerBuffer[position2--] = overlayColor;
                  } else {
                     integerBuffer[position2--] = 0;
                  }
               }
            }
         } else {
            for (int loopIndex3 = -4096; loopIndex3 < 0; loopIndex3++) {
               byte overlayDataEntry;
               if ((overlayDataEntry = overlayData[position++]) != 0) {
                  position++;
               }
            }
         }
      }
   }
   private void buildUnderlayColorMap(byte[][] workingArrayArgument, int[][] values) {
      int sourceMapWidth = mapWidth;
      int sourceMapHeight = mapHeight;
      int[] integerBuffer = new int[mapHeight];

      for (int loopIndex = 0; loopIndex < sourceMapHeight; loopIndex++) {
         integerBuffer[loopIndex] = 0;
      }

      for (int loopIndex2 = 5; loopIndex2 < sourceMapWidth - 5; loopIndex2++) {
         byte[] byteBuffer = workingArrayArgument[loopIndex2 + 5];
         byte[] byteBuffer2 = workingArrayArgument[loopIndex2 - 5];

         for (int loopIndex3 = 0; loopIndex3 < sourceMapHeight; loopIndex3++) {
            integerBuffer[loopIndex3] += this.underlayHslData[byteBuffer[loopIndex3] & 0xFF] - this.underlayHslData[byteBuffer2[loopIndex3] & 0xFF];
         }

         if (loopIndex2 > 10 && loopIndex2 < sourceMapWidth - 10) {
            int scalar = 0;
            int scalar2 = 0;
            int scalar3 = 0;
            int[] values2 = values[loopIndex2];

            for (int loopIndex4 = 5; loopIndex4 < sourceMapHeight - 5; loopIndex4++) {
               int scalar4 = integerBuffer[loopIndex4 - 5];
               int scalar5 = integerBuffer[loopIndex4 + 5];
               scalar += (scalar5 >> 20) - (scalar4 >> 20);
               scalar2 += (scalar5 >> 10 & 1023) - (scalar4 >> 10 & 1023);
               if ((scalar3 += (scalar5 & 1023) - (scalar4 & 1023)) > 0) {
                  double calculation = scalar / 8533.0;
                  double calculation2 = scalar2 / 8533.0;
                  double calculation3 = scalar3 / 8533.0;
                  double calculation4 = calculation2;
                  double calculation5 = calculation;
                  double calculation6 = calculation3;
                  double calculation7 = calculation3;
                  double calculation8 = calculation3;
                  if (calculation4 != 0.0) {
                     double calculation9;
                     if (calculation3 < 0.5) {
                        calculation9 = calculation3 * (calculation4 + 1.0);
                     } else {
                        calculation9 = calculation3 + calculation4 - calculation3 * calculation4;
                     }

                     double calculation10 = calculation3 * 2.0 - calculation9;
                     double calculation11;
                     if ((calculation11 = calculation5 + 0.3333333333333333) > 1.0) {
                        calculation11--;
                     }

                     double calculation12 = calculation5;
                     double calculation13;
                     if ((calculation13 = calculation5 - 0.3333333333333333) < 0.0) {
                        calculation13++;
                     }

                     if (calculation11 * 6.0 < 1.0) {
                        calculation6 = calculation10 + (calculation9 - calculation10) * 6.0 * calculation11;
                     } else if (calculation11 * 2.0 < 1.0) {
                        calculation6 = calculation9;
                     } else if (calculation11 * 3.0 < 2.0) {
                        calculation6 = calculation10 + (calculation9 - calculation10) * (0.6666666666666666 - calculation11) * 6.0;
                     } else {
                        calculation6 = calculation10;
                     }

                     if (calculation12 * 6.0 < 1.0) {
                        calculation7 = calculation10 + (calculation9 - calculation10) * 6.0 * calculation12;
                     } else if (calculation12 * 2.0 < 1.0) {
                        calculation7 = calculation9;
                     } else if (calculation12 * 3.0 < 2.0) {
                        calculation7 = calculation10 + (calculation9 - calculation10) * (0.6666666666666666 - calculation12) * 6.0;
                     } else {
                        calculation7 = calculation10;
                     }

                     if (calculation13 * 6.0 < 1.0) {
                        calculation8 = calculation10 + (calculation9 - calculation10) * 6.0 * calculation13;
                     } else if (calculation13 * 2.0 < 1.0) {
                        calculation8 = calculation9;
                     } else if (calculation13 * 3.0 < 2.0) {
                        calculation8 = calculation10 + (calculation9 - calculation10) * (0.6666666666666666 - calculation13) * 6.0;
                     } else {
                        calculation8 = calculation10;
                     }
                  }

                  int scalar6 = (int)(calculation6 * 256.0);
                  int scalar7 = (int)(calculation7 * 256.0);
                  int scalar8 = (int)(calculation8 * 256.0);
                  values2[loopIndex4] = (scalar6 << 16) + (scalar7 << 8) + scalar8;
               }
            }
         }
      }
   }
   @Override
   public final void cleanUpForQuit() {
      try {
         this.underlayHslData = null;
         this.overlayRgbColors = null;
         this.underlayColorMap = null;
         this.overlayColorMap = null;
         this.overlayShapeRotation = null;
         this.wallTypes = null;
         this.mapFunctionIds = null;
         this.mapSceneIds = null;
         this.mapIcons = null;
         this.mapSprites = null;
         this.bitmapFont = null;
         this.markerScreenX = null;
         this.markerScreenY = null;
         this.markerIconIds = null;
         this.mapFunctionX = null;
         this.mapFunctionY = null;
         this.mapFunctionTypes = null;
         this.overviewSprite = null;
         this.labelTexts = null;
         this.labelX = null;
         this.labelY = null;
         this.labelStyle = null;
         this.mapIconLabels = null;
         System.gc();
      } catch (Throwable throwable) {
      }
   }
   @Override
   public final void processGameLoop() {
      if (super.keyStatus[1] == 1) {
         viewCenterX = (int)(viewCenterX - 16.0 / this.currentZoom);
         this.redrawNeeded = true;
      }

      if (super.keyStatus[2] == 1) {
         viewCenterX = (int)(viewCenterX + 16.0 / this.currentZoom);
         this.redrawNeeded = true;
      }

      if (super.keyStatus[3] == 1) {
         viewCenterY = (int)(viewCenterY - 16.0 / this.currentZoom);
         this.redrawNeeded = true;
      }

      if (super.keyStatus[4] == 1) {
         viewCenterY = (int)(viewCenterY + 16.0 / this.currentZoom);
         this.redrawNeeded = true;
      }

      int scalar = 1;

      while (scalar > 0) {
         WorldMapViewer worldMapViewer = this;
         int localKeyQueue = -1;
         if (worldMapViewer.middleMouseY != worldMapViewer.middleMouseX) {
            localKeyQueue = worldMapViewer.keyQueue[worldMapViewer.middleMouseX];
            worldMapViewer.middleMouseX = worldMapViewer.middleMouseX + 1 & 127;
         }

         scalar = localKeyQueue;
         if (localKeyQueue == 49) {
            this.targetZoom = 3.0;
            this.redrawNeeded = true;
         }

         if (scalar == 50) {
            this.targetZoom = 4.0;
            this.redrawNeeded = true;
         }

         if (scalar == 51) {
            this.targetZoom = 6.0;
            this.redrawNeeded = true;
         }

         if (scalar == 52) {
            this.targetZoom = 8.0;
            this.redrawNeeded = true;
         }

         if (scalar == 107 || scalar == 75) {
            this.keyVisible = !this.keyVisible;
            this.redrawNeeded = true;
         }

         if (scalar == 111 || scalar == 79) {
            this.overviewVisible = !this.overviewVisible;
            this.redrawNeeded = true;
         }

         if (super.frame != null && scalar == 101) {
            System.out.println("Starting export...");
            WorldMapSprite worldMapSprite;
            (worldMapSprite = new WorldMapSprite(mapWidth << 1, mapHeight << 1)).initDrawingArea();
            this.renderMapRegion(0, 0, mapWidth, mapHeight, 0, 0, mapWidth << 1, mapHeight << 1);
            super.graphicsBuffer.initDrawingArea();
            int pixelsLengthOrPixels;
            byte[] pixelsLength = new byte[(pixelsLengthOrPixels = worldMapSprite.pixels.length) * 3];
            int position = 0;

            for (int pixelIndex = 0; pixelIndex < pixelsLengthOrPixels; pixelIndex++) {
               int pixel = worldMapSprite.pixels[pixelIndex];
               pixelsLength[position++] = (byte)(pixel >> 16);
               pixelsLength[position++] = (byte)(pixel >> 8);
               pixelsLength[position++] = (byte)pixel;
            }

            System.out.println("Saving to disk");

            try {
               BufferedOutputStream bufferedOutputStream;
               (bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("map-" + (mapWidth << 1) + "-" + (mapHeight << 1) + "-rgb.raw"))).write(pixelsLength);
               bufferedOutputStream.close();
            } catch (Exception exception) {
               exception.printStackTrace();
            }

            System.out.println("Done export: " + (mapWidth << 1) + "," + (mapHeight << 1));
         }
      }

      if (super.clickButton == 1) {
         this.dragStartMouseX = super.clickX;
         this.dragStartMouseY = super.clickY;
         this.dragStartCenterX = viewCenterX;
         this.dragStartCenterY = viewCenterY;
         if (super.clickX > 170 && super.clickX < 220 && super.clickY > 471 && super.clickY < 503) {
            this.targetZoom = 3.0;
            this.dragStartMouseX = -1;
         }

         if (super.clickX > 230 && super.clickX < 280 && super.clickY > 471 && super.clickY < 503) {
            this.targetZoom = 4.0;
            this.dragStartMouseX = -1;
         }

         if (super.clickX > 290 && super.clickX < 340 && super.clickY > 471 && super.clickY < 503) {
            this.targetZoom = 6.0;
            this.dragStartMouseX = -1;
         }

         if (super.clickX > 350 && super.clickX < 400 && super.clickY > 471 && super.clickY < 503) {
            this.targetZoom = 8.0;
            this.dragStartMouseX = -1;
         }

         if (super.clickX > this.keyPanelX
            && super.clickY > this.keyPanelY + this.keyPanelHeight
            && super.clickX < this.keyPanelX + this.keyPanelWidth
            && super.clickY < 503) {
            this.keyVisible = !this.keyVisible;
            this.dragStartMouseX = -1;
         }

         if (super.clickX > this.overviewX
            && super.clickY > this.overviewY + this.overviewHeight
            && super.clickX < this.overviewX + this.overviewWidth
            && super.clickY < 503) {
            this.overviewVisible = !this.overviewVisible;
            this.dragStartMouseX = -1;
         }

         if (this.keyVisible) {
            if (super.clickX > this.keyPanelX
               && super.clickY > this.keyPanelY
               && super.clickX < this.keyPanelX + this.keyPanelWidth
               && super.clickY < this.keyPanelY + this.keyPanelHeight) {
               this.dragStartMouseX = -1;
            }

            if (super.clickX > this.keyPanelX
               && super.clickY > this.keyPanelY
               && super.clickX < this.keyPanelX + this.keyPanelWidth
               && super.clickY < this.keyPanelY + 18
               && this.targetKeyScrollOffset > 0) {
               this.targetKeyScrollOffset -= 25;
            }

            if (super.clickX > this.keyPanelX
               && super.clickY > this.keyPanelY + this.keyPanelHeight - 18
               && super.clickX < this.keyPanelX + this.keyPanelWidth
               && super.clickY < this.keyPanelY + this.keyPanelHeight
               && this.targetKeyScrollOffset < 50) {
               this.targetKeyScrollOffset += 25;
            }
         }

         this.redrawNeeded = true;
      }

      if (this.keyVisible) {
         this.hoveredKeyIndex = -1;
         if (super.mouseX > this.keyPanelX && super.mouseX < this.keyPanelX + this.keyPanelWidth) {
            scalar = this.keyPanelY + 21 + 5;

            for (int loopIndex = 0; loopIndex < 25; loopIndex++) {
               if (loopIndex + this.keyScrollOffset >= this.mapIconLabels.length || !this.mapIconLabels[loopIndex + this.keyScrollOffset].equals("???")) {
                  if (super.mouseY >= scalar && super.mouseY < scalar + 17) {
                     this.hoveredKeyIndex = loopIndex + this.keyScrollOffset;
                     if (super.clickButton == 1) {
                        this.selectedMapFunctionType = loopIndex + this.keyScrollOffset;
                        this.selectionFlashTicks = 50;
                     }
                  }

                  scalar += 17;
               }
            }
         }

         if (this.hoveredKeyIndex != this.previousHoveredKeyIndex) {
            this.previousHoveredKeyIndex = this.hoveredKeyIndex;
            this.redrawNeeded = true;
         }
      }

      if ((super.mouseButtonDown == 1 || super.clickButton == 1) && this.overviewVisible) {
         scalar = super.clickX;
         int clickY = super.clickY;
         if (super.mouseButtonDown == 1) {
            scalar = super.mouseX;
            clickY = super.mouseY;
         }

         if (scalar > this.overviewX && clickY > this.overviewY && scalar < this.overviewX + this.overviewWidth && clickY < this.overviewY + this.overviewHeight) {
            viewCenterX = (scalar - this.overviewX) * mapWidth / this.overviewWidth;
            viewCenterY = (clickY - this.overviewY) * mapHeight / this.overviewHeight;
            this.dragStartMouseX = -1;
            this.redrawNeeded = true;
         }
      }

      if (super.mouseButtonDown == 1 && this.dragStartMouseX != -1) {
         viewCenterX = this.dragStartCenterX + (int)((this.dragStartMouseX - super.mouseX) * 2.0 / this.targetZoom);
         viewCenterY = this.dragStartCenterY + (int)((this.dragStartMouseY - super.mouseY) * 2.0 / this.targetZoom);
         this.redrawNeeded = true;
      }

      if (this.currentZoom < this.targetZoom) {
         this.redrawNeeded = true;
         this.currentZoom = this.currentZoom + this.currentZoom / 30.0;
         if (this.currentZoom > this.targetZoom) {
            this.currentZoom = this.targetZoom;
         }
      }

      if (this.currentZoom > this.targetZoom) {
         this.redrawNeeded = true;
         this.currentZoom = this.currentZoom - this.currentZoom / 30.0;
         if (this.currentZoom < this.targetZoom) {
            this.currentZoom = this.targetZoom;
         }
      }

      if (this.keyScrollOffset < this.targetKeyScrollOffset) {
         this.redrawNeeded = true;
         this.keyScrollOffset++;
      }

      if (this.keyScrollOffset > this.targetKeyScrollOffset) {
         this.redrawNeeded = true;
         this.keyScrollOffset--;
      }

      if (this.selectionFlashTicks > 0) {
         this.redrawNeeded = true;
         this.selectionFlashTicks--;
      }

      scalar = viewCenterX - (int)(635.0 / this.currentZoom);
      int workingViewY = viewCenterY - (int)(503.0 / this.currentZoom);
      int workingViewX = viewCenterX + (int)(635.0 / this.currentZoom);
      int workingViewY2 = viewCenterY + (int)(503.0 / this.currentZoom);
      if (scalar < 48) {
         viewCenterX = 48 + (int)(635.0 / this.currentZoom);
      }

      if (workingViewY < 48) {
         viewCenterY = 48 + (int)(503.0 / this.currentZoom);
      }

      if (workingViewX > mapWidth - 48) {
         viewCenterX = mapWidth - 48 - (int)(635.0 / this.currentZoom);
      }

      if (workingViewY2 > mapHeight - 48) {
         viewCenterY = mapHeight - 48 - (int)(503.0 / this.currentZoom);
      }
   }
   @Override
   public final void showErrorScreen() {
      if (this.redrawNeeded) {
         this.redrawNeeded = false;
         this.frameBlitCountdown = 0;
         WorldMapRasterizer2D.clear();
         int workingViewX = viewCenterX - (int)(635.0 / this.currentZoom);
         int workingViewY = viewCenterY - (int)(503.0 / this.currentZoom);
         int workingViewX2 = viewCenterX + (int)(635.0 / this.currentZoom);
         int workingViewY2 = viewCenterY + (int)(503.0 / this.currentZoom);
         this.renderMapRegion(workingViewX, workingViewY, workingViewX2, workingViewY2, 0, 0, 635, 503);
         if (this.overviewVisible) {
            int overviewY = this.overviewY;
            int overviewDrawX = this.overviewX;
            WorldMapSprite worldMapSprite = this.overviewSprite;
            overviewDrawX += worldMapSprite.xOffset;
            overviewY += worldMapSprite.yOffset;
            int position = overviewDrawX + overviewY * WorldMapRasterizer2D.width;
            int sourceOverviewY = 0;
            int spriteHeight = worldMapSprite.spriteHeight;
            int spriteWidth = worldMapSprite.spriteWidth;
            int localWidth = WorldMapRasterizer2D.width - spriteWidth;
            int position2 = 0;
            if (overviewY < WorldMapRasterizer2D.topY) {
               int localTopY = WorldMapRasterizer2D.topY - overviewY;
               spriteHeight -= localTopY;
               overviewY = WorldMapRasterizer2D.topY;
               sourceOverviewY = 0 + localTopY * spriteWidth;
               position += localTopY * WorldMapRasterizer2D.width;
            }

            if (overviewY + spriteHeight > WorldMapRasterizer2D.bottomY) {
               spriteHeight -= overviewY + spriteHeight - WorldMapRasterizer2D.bottomY;
            }

            if (overviewDrawX < WorldMapRasterizer2D.topX) {
               int localTopX = WorldMapRasterizer2D.topX - overviewDrawX;
               spriteWidth -= localTopX;
               overviewDrawX = WorldMapRasterizer2D.topX;
               sourceOverviewY += localTopX;
               position += localTopX;
               position2 = localTopX + 0;
               localWidth += localTopX;
            }

            if (overviewDrawX + spriteWidth > WorldMapRasterizer2D.bottomX) {
               int scalar = overviewDrawX + spriteWidth - WorldMapRasterizer2D.bottomX;
               spriteWidth -= scalar;
               position2 += scalar;
               localWidth += scalar;
            }

            if (spriteWidth > 0 && spriteHeight > 0) {
               int scalar2 = position2;
               int sourceLocalWidth = localWidth;
               int sourceSpriteWidth = spriteWidth;
               overviewY = sourceOverviewY;
               int[] pixels = worldMapSprite.pixels;
               int[] pixels2 = WorldMapRasterizer2D.pixels;
               position2 = -(sourceSpriteWidth >> 2);
               sourceOverviewY = -(sourceSpriteWidth & 3);

               for (int loopIndex = -spriteHeight; loopIndex < 0; loopIndex++) {
                  for (int loopIndex2 = position2; loopIndex2 < 0; loopIndex2++) {
                     pixels2[position++] = pixels[overviewY++];
                     pixels2[position++] = pixels[overviewY++];
                     pixels2[position++] = pixels[overviewY++];
                     pixels2[position++] = pixels[overviewY++];
                  }

                  for (int loopIndex3 = sourceOverviewY; loopIndex3 < 0; loopIndex3++) {
                     pixels2[position++] = pixels[overviewY++];
                  }

                  position += sourceLocalWidth;
                  overviewY += scalar2;
               }
            }

            WorldMapRasterizer2D.fillRectangleAlpha(
               this.overviewX + this.overviewWidth * workingViewX / mapWidth,
               this.overviewY + this.overviewHeight * workingViewY / mapHeight,
               (workingViewX2 - workingViewX) * this.overviewWidth / mapWidth,
               (workingViewY2 - workingViewY) * this.overviewHeight / mapHeight,
               16711680,
               128
            );
            WorldMapRasterizer2D.drawRectangle(
               this.overviewX + this.overviewWidth * workingViewX / mapWidth,
               this.overviewY + this.overviewHeight * workingViewY / mapHeight,
               (workingViewX2 - workingViewX) * this.overviewWidth / mapWidth,
               (workingViewY2 - workingViewY) * this.overviewHeight / mapHeight,
               16711680
            );
            if (this.selectionFlashTicks > 0 && this.selectionFlashTicks % 10 < 5) {
               for (int mapFunctionTypeIndex = 0; mapFunctionTypeIndex < this.mapFunctionCount; mapFunctionTypeIndex++) {
                  if (this.mapFunctionTypes[mapFunctionTypeIndex] == this.selectedMapFunctionType) {
                     workingViewY = this.overviewX + this.overviewWidth * this.mapFunctionX[mapFunctionTypeIndex] / mapWidth;
                     workingViewX2 = this.overviewY + this.overviewHeight * this.mapFunctionY[mapFunctionTypeIndex] / mapHeight;
                     WorldMapRasterizer2D.drawCircleAlpha(workingViewY, workingViewX2, 2, 16776960, 256);
                  }
               }
            }
         }

         if (this.keyVisible) {
            this.drawBeveledPanel(this.keyPanelX, this.keyPanelY, this.keyPanelWidth, 18, 10066329, 7829367, 5592405, "Prev page");
            this.drawBeveledPanel(this.keyPanelX, this.keyPanelY + 18, this.keyPanelWidth, this.keyPanelHeight - 36, 10066329, 7829367, 5592405, "");
            this.drawBeveledPanel(this.keyPanelX, this.keyPanelY + this.keyPanelHeight - 18, this.keyPanelWidth, 18, 10066329, 7829367, 5592405, "Next page");
            workingViewX = this.keyPanelY + 3 + 18;

            for (int loopIndex4 = 0; loopIndex4 < 25; loopIndex4++) {
               if (loopIndex4 + this.keyScrollOffset < this.mapSprites.length && loopIndex4 + this.keyScrollOffset < this.mapIconLabels.length) {
                  if (this.mapIconLabels[loopIndex4 + this.keyScrollOffset].equals("???")) {
                     continue;
                  }

                  this.mapSprites[loopIndex4 + this.keyScrollOffset].drawSprite(this.keyPanelX + 3, workingViewX);
                  this.bitmapFont.textLeft(this.mapIconLabels[loopIndex4 + this.keyScrollOffset], this.keyPanelX + 21, workingViewX + 14, 0);
                  workingViewX2 = 16777215;
                  if (this.hoveredKeyIndex == loopIndex4 + this.keyScrollOffset) {
                     workingViewX2 = 12298922;
                  }

                  if (this.selectionFlashTicks > 0 && this.selectionFlashTicks % 10 < 5 && this.selectedMapFunctionType == loopIndex4 + this.keyScrollOffset) {
                     workingViewX2 = 16776960;
                  }

                  this.bitmapFont.textLeft(this.mapIconLabels[loopIndex4 + this.keyScrollOffset], this.keyPanelX + 20, workingViewX + 13, workingViewX2);
               }

               workingViewX += 17;
            }
         }

         this.drawBeveledPanel(this.overviewX, this.overviewY + this.overviewHeight, this.overviewWidth, 18, this.buttonHighlightColor, this.buttonFillColor, this.buttonShadowColor, "Overview");
         this.drawBeveledPanel(this.keyPanelX, this.keyPanelY + this.keyPanelHeight, this.keyPanelWidth, 18, this.buttonHighlightColor, this.buttonFillColor, this.buttonShadowColor, "Key");
         if (this.targetZoom == 3.0) {
            this.drawBeveledPanel(170, 471, 50, 30, this.selectedButtonHighlightColor, this.selectedButtonFillColor, this.selectedButtonShadowColor, "37%");
         } else {
            this.drawBeveledPanel(170, 471, 50, 30, this.buttonHighlightColor, this.buttonFillColor, this.buttonShadowColor, "37%");
         }

         if (this.targetZoom == 4.0) {
            this.drawBeveledPanel(230, 471, 50, 30, this.selectedButtonHighlightColor, this.selectedButtonFillColor, this.selectedButtonShadowColor, "50%");
         } else {
            this.drawBeveledPanel(230, 471, 50, 30, this.buttonHighlightColor, this.buttonFillColor, this.buttonShadowColor, "50%");
         }

         if (this.targetZoom == 6.0) {
            this.drawBeveledPanel(290, 471, 50, 30, this.selectedButtonHighlightColor, this.selectedButtonFillColor, this.selectedButtonShadowColor, "75%");
         } else {
            this.drawBeveledPanel(290, 471, 50, 30, this.buttonHighlightColor, this.buttonFillColor, this.buttonShadowColor, "75%");
         }

         if (this.targetZoom == 8.0) {
            this.drawBeveledPanel(350, 471, 50, 30, this.selectedButtonHighlightColor, this.selectedButtonFillColor, this.selectedButtonShadowColor, "100%");
         } else {
            this.drawBeveledPanel(350, 471, 50, 30, this.buttonHighlightColor, this.buttonFillColor, this.buttonShadowColor, "100%");
         }
      }

      this.frameBlitCountdown--;
      if (this.frameBlitCountdown <= 0) {
         boolean flag = false;
         boolean localFlag = false;
         Graphics graphics = super.graphics;
         WorldMapGraphicsBuffer worldMapGraphicsBuffer = super.graphicsBuffer;
         super.graphicsBuffer.pushPixels();
         graphics.drawImage(worldMapGraphicsBuffer.image, 0, 0, worldMapGraphicsBuffer);
         this.frameBlitCountdown = 50;
      }
   }
   @Override
   public final void raiseWelcomeScreen() {
      this.frameBlitCountdown = 0;
   }
   private void drawBeveledPanel(int keyPanelX, int tileScreenY, int keyPanelWidth, int overviewHeight, int pixel, int pixel2, int pixel3, String text) {
      WorldMapRasterizer2D.drawRectangle(keyPanelX, tileScreenY, keyPanelWidth, overviewHeight, 0);
      keyPanelX++;
      tileScreenY++;
      keyPanelWidth -= 2;
      overviewHeight -= 2;
      WorldMapRasterizer2D.fillRectangle(keyPanelX, tileScreenY, keyPanelWidth, overviewHeight, pixel2);
      WorldMapRasterizer2D.drawHorizontalLine(keyPanelX, tileScreenY, keyPanelWidth, pixel);
      WorldMapRasterizer2D.drawVerticalLine(keyPanelX, tileScreenY, overviewHeight, pixel);
      WorldMapRasterizer2D.drawHorizontalLine(keyPanelX, tileScreenY + overviewHeight - 1, keyPanelWidth, pixel3);
      WorldMapRasterizer2D.drawVerticalLine(keyPanelX + keyPanelWidth - 1, tileScreenY, overviewHeight, pixel3);
      this.bitmapFont.textCenter(text, keyPanelX + keyPanelWidth / 2 + 1, tileScreenY + overviewHeight / 2 + 1 + 4, 0);
      this.bitmapFont.textCenter(text, keyPanelX + keyPanelWidth / 2, tileScreenY + overviewHeight / 2 + 4, 16777215);
   }
   private void renderMapRegion(int workingViewX, int workingViewY, int mapWidth, int mapHeight, int loopIndex, int loopIndex2, int overviewWidth, int overviewHeight) {
      loopIndex = mapWidth - workingViewX;
      loopIndex2 = mapHeight - workingViewY;
      int scalar = (overviewWidth << 16) / loopIndex;
      int scalar2 = (overviewHeight << 16) / loopIndex2;

      for (int loopIndex3 = 0; loopIndex3 < loopIndex; loopIndex3++) {
         int scalar3 = scalar * loopIndex3 >> 16;
         int scalar4;
         int keyPanelWidth;
         if ((keyPanelWidth = (scalar4 = scalar * (loopIndex3 + 1) >> 16) - scalar3) > 0) {
            int[] underlayColorColumn = this.underlayColorMap[loopIndex3 + workingViewX];
            int[] overlayColorColumn = this.overlayColorMap[loopIndex3 + workingViewX];
            byte[] overlayShapeColumn = this.overlayShapeRotation[loopIndex3 + workingViewX];

            for (int loopIndex4 = 0; loopIndex4 < loopIndex2; loopIndex4++) {
               int scalar5 = scalar2 * loopIndex4 >> 16;
               int scalar6;
               int scalar7;
               if ((scalar7 = (scalar6 = scalar2 * (loopIndex4 + 1) >> 16) - scalar5) > 0) {
                  int pixel;
                  if ((pixel = overlayColorColumn[loopIndex4 + workingViewY]) == 0) {
                     WorldMapRasterizer2D.fillRectangle(scalar3, scalar5, scalar4 - scalar3, scalar6 - scalar5, underlayColorColumn[loopIndex4 + workingViewY]);
                  } else {
                     int overlayShapeColumnEntry;
                     int scalar8;
                     if ((scalar8 = (overlayShapeColumnEntry = overlayShapeColumn[loopIndex4 + workingViewY]) & 252) != 0 && keyPanelWidth > 1 && scalar7 > 1) {
                        int scalar9 = scalar5 * WorldMapRasterizer2D.width + scalar3;
                        int underlayColorColumnEntry = underlayColorColumn[loopIndex4 + workingViewY];
                        int scalar10 = scalar8 >> 2;
                        int scalar11 = overlayShapeColumnEntry & 3;
                        scalar5 = scalar10;
                        int loopIndex5 = scalar7;
                        int sourceKeyPanelWidth = keyPanelWidth;
                        int sourcePixel = pixel;
                        scalar8 = underlayColorColumnEntry;
                        overlayShapeColumnEntry = scalar9;
                        int[] pixels = WorldMapRasterizer2D.pixels;
                        int localWidth = WorldMapRasterizer2D.width - sourceKeyPanelWidth;
                        if (scalar5 == 9) {
                           scalar5 = 1;
                           scalar11 = scalar11 + 1 & 3;
                        }

                        if (scalar5 == 10) {
                           scalar5 = 1;
                           scalar11 = scalar11 + 3 & 3;
                        }

                        if (scalar5 == 11) {
                           scalar5 = 8;
                           scalar11 = scalar11 + 3 & 3;
                        }

                        if (scalar5 == 1) {
                           if (scalar11 == 0) {
                              for (int loopIndex6 = 0; loopIndex6 < loopIndex5; loopIndex6++) {
                                 for (int loopIndex7 = 0; loopIndex7 < sourceKeyPanelWidth; loopIndex7++) {
                                    if (loopIndex7 <= loopIndex6) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 1) {
                              for (int loopIndex8 = loopIndex5 - 1; loopIndex8 >= 0; loopIndex8--) {
                                 for (int loopIndex9 = 0; loopIndex9 < sourceKeyPanelWidth; loopIndex9++) {
                                    if (loopIndex9 <= loopIndex8) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 2) {
                              for (int loopIndex10 = 0; loopIndex10 < loopIndex5; loopIndex10++) {
                                 for (int loopIndex11 = 0; loopIndex11 < sourceKeyPanelWidth; loopIndex11++) {
                                    if (loopIndex11 >= loopIndex10) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 3) {
                              for (int loopIndex12 = loopIndex5 - 1; loopIndex12 >= 0; loopIndex12--) {
                                 for (int loopIndex13 = 0; loopIndex13 < sourceKeyPanelWidth; loopIndex13++) {
                                    if (loopIndex13 >= loopIndex12) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           }
                        } else if (scalar5 == 2) {
                           if (scalar11 == 0) {
                              for (int loopIndex14 = loopIndex5 - 1; loopIndex14 >= 0; loopIndex14--) {
                                 for (int loopIndex15 = 0; loopIndex15 < sourceKeyPanelWidth; loopIndex15++) {
                                    if (loopIndex15 <= loopIndex14 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 1) {
                              for (int loopIndex16 = 0; loopIndex16 < loopIndex5; loopIndex16++) {
                                 for (int loopIndex17 = 0; loopIndex17 < sourceKeyPanelWidth; loopIndex17++) {
                                    if (loopIndex17 >= loopIndex16 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 2) {
                              for (int loopIndex18 = 0; loopIndex18 < loopIndex5; loopIndex18++) {
                                 for (int loopIndex19 = sourceKeyPanelWidth - 1; loopIndex19 >= 0; loopIndex19--) {
                                    if (loopIndex19 <= loopIndex18 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 3) {
                              for (int loopIndex20 = loopIndex5 - 1; loopIndex20 >= 0; loopIndex20--) {
                                 for (int loopIndex21 = sourceKeyPanelWidth - 1; loopIndex21 >= 0; loopIndex21--) {
                                    if (loopIndex21 >= loopIndex20 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           }
                        } else if (scalar5 == 3) {
                           if (scalar11 == 0) {
                              for (int loopIndex22 = loopIndex5 - 1; loopIndex22 >= 0; loopIndex22--) {
                                 for (int loopIndex23 = sourceKeyPanelWidth - 1; loopIndex23 >= 0; loopIndex23--) {
                                    if (loopIndex23 <= loopIndex22 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 1) {
                              for (int loopIndex24 = loopIndex5 - 1; loopIndex24 >= 0; loopIndex24--) {
                                 for (int loopIndex25 = 0; loopIndex25 < sourceKeyPanelWidth; loopIndex25++) {
                                    if (loopIndex25 >= loopIndex24 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 2) {
                              for (int loopIndex26 = 0; loopIndex26 < loopIndex5; loopIndex26++) {
                                 for (int loopIndex27 = 0; loopIndex27 < sourceKeyPanelWidth; loopIndex27++) {
                                    if (loopIndex27 <= loopIndex26 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 3) {
                              for (int loopIndex28 = 0; loopIndex28 < loopIndex5; loopIndex28++) {
                                 for (int loopIndex29 = sourceKeyPanelWidth - 1; loopIndex29 >= 0; loopIndex29--) {
                                    if (loopIndex29 >= loopIndex28 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           }
                        } else if (scalar5 == 4) {
                           if (scalar11 == 0) {
                              for (int loopIndex30 = loopIndex5 - 1; loopIndex30 >= 0; loopIndex30--) {
                                 for (int loopIndex31 = 0; loopIndex31 < sourceKeyPanelWidth; loopIndex31++) {
                                    if (loopIndex31 >= loopIndex30 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 1) {
                              for (int loopIndex32 = 0; loopIndex32 < loopIndex5; loopIndex32++) {
                                 for (int loopIndex33 = 0; loopIndex33 < sourceKeyPanelWidth; loopIndex33++) {
                                    if (loopIndex33 <= loopIndex32 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 2) {
                              for (int loopIndex34 = 0; loopIndex34 < loopIndex5; loopIndex34++) {
                                 for (int loopIndex35 = sourceKeyPanelWidth - 1; loopIndex35 >= 0; loopIndex35--) {
                                    if (loopIndex35 >= loopIndex34 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 3) {
                              for (int loopIndex36 = loopIndex5 - 1; loopIndex36 >= 0; loopIndex36--) {
                                 for (int loopIndex37 = sourceKeyPanelWidth - 1; loopIndex37 >= 0; loopIndex37--) {
                                    if (loopIndex37 <= loopIndex36 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           }
                        } else if (scalar5 == 5) {
                           if (scalar11 == 0) {
                              for (int loopIndex38 = loopIndex5 - 1; loopIndex38 >= 0; loopIndex38--) {
                                 for (int loopIndex39 = sourceKeyPanelWidth - 1; loopIndex39 >= 0; loopIndex39--) {
                                    if (loopIndex39 >= loopIndex38 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 1) {
                              for (int loopIndex40 = loopIndex5 - 1; loopIndex40 >= 0; loopIndex40--) {
                                 for (int loopIndex41 = 0; loopIndex41 < sourceKeyPanelWidth; loopIndex41++) {
                                    if (loopIndex41 <= loopIndex40 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 2) {
                              for (int loopIndex42 = 0; loopIndex42 < loopIndex5; loopIndex42++) {
                                 for (int loopIndex43 = 0; loopIndex43 < sourceKeyPanelWidth; loopIndex43++) {
                                    if (loopIndex43 >= loopIndex42 >> 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           } else if (scalar11 == 3) {
                              for (int loopIndex44 = 0; loopIndex44 < loopIndex5; loopIndex44++) {
                                 for (int loopIndex45 = sourceKeyPanelWidth - 1; loopIndex45 >= 0; loopIndex45--) {
                                    if (loopIndex45 <= loopIndex44 << 1) {
                                       pixels[overlayShapeColumnEntry++] = sourcePixel;
                                    } else {
                                       pixels[overlayShapeColumnEntry++] = scalar8;
                                    }
                                 }

                                 overlayShapeColumnEntry += localWidth;
                              }
                           }
                        } else {
                           if (scalar5 == 6) {
                              if (scalar11 == 0) {
                                 for (int loopIndex46 = 0; loopIndex46 < loopIndex5; loopIndex46++) {
                                    for (int loopIndex47 = 0; loopIndex47 < sourceKeyPanelWidth; loopIndex47++) {
                                       if (loopIndex47 <= sourceKeyPanelWidth / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }

                              if (scalar11 == 1) {
                                 for (int loopIndex48 = 0; loopIndex48 < loopIndex5; loopIndex48++) {
                                    for (int loopIndex49 = 0; loopIndex49 < sourceKeyPanelWidth; loopIndex49++) {
                                       if (loopIndex48 <= loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }

                              if (scalar11 == 2) {
                                 for (int loopIndex50 = 0; loopIndex50 < loopIndex5; loopIndex50++) {
                                    for (int loopIndex51 = 0; loopIndex51 < sourceKeyPanelWidth; loopIndex51++) {
                                       if (loopIndex51 >= sourceKeyPanelWidth / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }

                              if (scalar11 == 3) {
                                 for (int loopIndex52 = 0; loopIndex52 < loopIndex5; loopIndex52++) {
                                    for (int loopIndex53 = 0; loopIndex53 < sourceKeyPanelWidth; loopIndex53++) {
                                       if (loopIndex52 >= loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }
                           }

                           if (scalar5 == 7) {
                              if (scalar11 == 0) {
                                 for (int loopIndex54 = 0; loopIndex54 < loopIndex5; loopIndex54++) {
                                    for (int loopIndex55 = 0; loopIndex55 < sourceKeyPanelWidth; loopIndex55++) {
                                       if (loopIndex55 <= loopIndex54 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }

                              if (scalar11 == 1) {
                                 for (int loopIndex56 = loopIndex5 - 1; loopIndex56 >= 0; loopIndex56--) {
                                    for (int loopIndex57 = 0; loopIndex57 < sourceKeyPanelWidth; loopIndex57++) {
                                       if (loopIndex57 <= loopIndex56 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }

                              if (scalar11 == 2) {
                                 for (int loopIndex58 = loopIndex5 - 1; loopIndex58 >= 0; loopIndex58--) {
                                    for (int loopIndex59 = sourceKeyPanelWidth - 1; loopIndex59 >= 0; loopIndex59--) {
                                       if (loopIndex59 <= loopIndex58 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }

                              if (scalar11 == 3) {
                                 for (int loopIndex60 = 0; loopIndex60 < loopIndex5; loopIndex60++) {
                                    for (int loopIndex61 = sourceKeyPanelWidth - 1; loopIndex61 >= 0; loopIndex61--) {
                                       if (loopIndex61 <= loopIndex60 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                                 continue;
                              }
                           }

                           if (scalar5 == 8) {
                              if (scalar11 == 0) {
                                 for (int loopIndex62 = 0; loopIndex62 < loopIndex5; loopIndex62++) {
                                    for (int loopIndex63 = 0; loopIndex63 < sourceKeyPanelWidth; loopIndex63++) {
                                       if (loopIndex63 >= loopIndex62 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                              } else if (scalar11 == 1) {
                                 for (int loopIndex64 = loopIndex5 - 1; loopIndex64 >= 0; loopIndex64--) {
                                    for (int loopIndex65 = 0; loopIndex65 < sourceKeyPanelWidth; loopIndex65++) {
                                       if (loopIndex65 >= loopIndex64 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                              } else if (scalar11 == 2) {
                                 for (int loopIndex66 = loopIndex5 - 1; loopIndex66 >= 0; loopIndex66--) {
                                    for (int loopIndex67 = sourceKeyPanelWidth - 1; loopIndex67 >= 0; loopIndex67--) {
                                       if (loopIndex67 >= loopIndex66 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                              } else if (scalar11 == 3) {
                                 for (int loopIndex68 = 0; loopIndex68 < loopIndex5; loopIndex68++) {
                                    for (int loopIndex69 = sourceKeyPanelWidth - 1; loopIndex69 >= 0; loopIndex69--) {
                                       if (loopIndex69 >= loopIndex68 - loopIndex5 / 2) {
                                          pixels[overlayShapeColumnEntry++] = sourcePixel;
                                       } else {
                                          pixels[overlayShapeColumnEntry++] = scalar8;
                                       }
                                    }

                                    overlayShapeColumnEntry += localWidth;
                                 }
                              }
                           }
                        }
                     } else {
                        WorldMapRasterizer2D.fillRectangle(scalar3, scalar5, keyPanelWidth, scalar7, pixel);
                     }
                  }
               }
            }
         }
      }

      if (mapWidth - workingViewX <= overviewWidth) {
         int markerIconIdIndex = 0;

         for (int loopIndex70 = 0; loopIndex70 < loopIndex; loopIndex70++) {
            int tileScreenX = scalar * loopIndex70 >> 16;
            int scalar12;
            int scalar13;
            if ((scalar13 = (scalar12 = scalar * (loopIndex70 + 1) >> 16) - tileScreenX) > 0) {
               byte[] wallTypeColumn = this.wallTypes[loopIndex70 + workingViewX];
               byte[] mapSceneColumn = this.mapSceneIds[loopIndex70 + workingViewX];
               byte[] mapFunctionColumn = this.mapFunctionIds[loopIndex70 + workingViewX];

               for (int loopIndex71 = 0; loopIndex71 < loopIndex2; loopIndex71++) {
                  int tileScreenY = scalar2 * loopIndex71 >> 16;
                  int tileBottomOrFunctionId;
                  int loopIndex72;
                  if ((loopIndex72 = (tileBottomOrFunctionId = scalar2 * (loopIndex71 + 1) >> 16) - tileScreenY) > 0) {
                     int scalar14;
                     if ((scalar14 = wallTypeColumn[loopIndex71 + workingViewY] & 255) != 0) {
                        int sourceTileScreenX;
                        if (scalar13 == 1) {
                           sourceTileScreenX = tileScreenX;
                        } else {
                           sourceTileScreenX = scalar12 - 1;
                        }

                        if (loopIndex72 == 1) {
                           tileBottomOrFunctionId = tileScreenY;
                        } else {
                           tileBottomOrFunctionId--;
                        }

                        int pixel2 = 13421772;
                        if (scalar14 >= 5 && scalar14 <= 8 || scalar14 >= 13 && scalar14 <= 16 || scalar14 >= 21 && scalar14 <= 24 || scalar14 == 27 || scalar14 == 28) {
                           pixel2 = 13369344;
                           scalar14 -= 4;
                        }

                        if (scalar14 == 1) {
                           WorldMapRasterizer2D.drawVerticalLine(tileScreenX, tileScreenY, loopIndex72, pixel2);
                        } else if (scalar14 == 2) {
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileScreenY, scalar13, pixel2);
                        } else if (scalar14 == 3) {
                           WorldMapRasterizer2D.drawVerticalLine(sourceTileScreenX, tileScreenY, loopIndex72, pixel2);
                        } else if (scalar14 == 4) {
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileBottomOrFunctionId, scalar13, pixel2);
                        } else if (scalar14 == 9) {
                           WorldMapRasterizer2D.drawVerticalLine(tileScreenX, tileScreenY, loopIndex72, 16777215);
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileScreenY, scalar13, pixel2);
                        } else if (scalar14 == 10) {
                           WorldMapRasterizer2D.drawVerticalLine(sourceTileScreenX, tileScreenY, loopIndex72, 16777215);
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileScreenY, scalar13, pixel2);
                        } else if (scalar14 == 11) {
                           WorldMapRasterizer2D.drawVerticalLine(sourceTileScreenX, tileScreenY, loopIndex72, 16777215);
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileBottomOrFunctionId, scalar13, pixel2);
                        } else if (scalar14 == 12) {
                           WorldMapRasterizer2D.drawVerticalLine(tileScreenX, tileScreenY, loopIndex72, 16777215);
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileBottomOrFunctionId, scalar13, pixel2);
                        } else if (scalar14 == 17) {
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileScreenY, 1, pixel2);
                        } else if (scalar14 == 18) {
                           WorldMapRasterizer2D.drawHorizontalLine(sourceTileScreenX, tileScreenY, 1, pixel2);
                        } else if (scalar14 == 19) {
                           WorldMapRasterizer2D.drawHorizontalLine(sourceTileScreenX, tileBottomOrFunctionId, 1, pixel2);
                        } else if (scalar14 == 20) {
                           WorldMapRasterizer2D.drawHorizontalLine(tileScreenX, tileBottomOrFunctionId, 1, pixel2);
                        } else if (scalar14 == 25) {
                           for (int loopIndex73 = 0; loopIndex73 < loopIndex72; loopIndex73++) {
                              WorldMapRasterizer2D.drawHorizontalLine(tileScreenX + loopIndex73, tileBottomOrFunctionId - loopIndex73, 1, pixel2);
                           }
                        } else if (scalar14 == 26) {
                           for (int loopIndex74 = 0; loopIndex74 < loopIndex72; loopIndex74++) {
                              WorldMapRasterizer2D.drawHorizontalLine(tileScreenX + loopIndex74, tileScreenY + loopIndex74, 1, pixel2);
                           }
                        }
                     }

                     int scalar15;
                     if ((scalar15 = mapSceneColumn[loopIndex71 + workingViewY] & 255) != 0) {
                        WorldMapIndexedSprite worldMapIndexedSprite = this.mapIcons[scalar15 - 1];
                        int scalar16 = tileScreenX - scalar13 / 2;
                        int scalar17 = tileScreenY - loopIndex72 / 2;
                        int scalar18 = scalar13 << 1;
                        int scalar19 = loopIndex72 << 1;
                        int scalar20 = scalar18;
                        scalar15 = scalar17;
                        scalar14 = scalar16;
                        WorldMapIndexedSprite sourceWorldMapIndexedSprite = worldMapIndexedSprite;

                        try {
                           int width2 = sourceWorldMapIndexedSprite.width;
                           int scalar21 = 0;
                           int scalar22 = 0;
                           int canvasWidth = sourceWorldMapIndexedSprite.canvasWidth;
                           int canvasHeight = sourceWorldMapIndexedSprite.canvasHeight;
                           int position2 = (canvasWidth << 16) / scalar20;
                           int scalar23 = (canvasHeight << 16) / scalar19;
                           scalar14 += (sourceWorldMapIndexedSprite.xOffset * scalar20 + canvasWidth - 1) / canvasWidth;
                           scalar15 += (sourceWorldMapIndexedSprite.yOffset * scalar19 + canvasHeight - 1) / canvasHeight;
                           if (sourceWorldMapIndexedSprite.xOffset * scalar20 % canvasWidth != 0) {
                              scalar21 = (canvasWidth - sourceWorldMapIndexedSprite.xOffset * scalar20 % canvasWidth << 16) / scalar20;
                           }

                           if (sourceWorldMapIndexedSprite.yOffset * scalar19 % canvasHeight != 0) {
                              scalar22 = (canvasHeight - sourceWorldMapIndexedSprite.yOffset * scalar19 % canvasHeight << 16) / scalar19;
                           }

                           scalar20 = scalar20 * (sourceWorldMapIndexedSprite.width - (scalar21 >> 16)) / canvasWidth;
                           scalar19 = scalar19 * (sourceWorldMapIndexedSprite.height - (scalar22 >> 16)) / canvasHeight;
                           canvasWidth = scalar14 + scalar15 * WorldMapRasterizer2D.width;
                           canvasHeight = WorldMapRasterizer2D.width - scalar20;
                           if (scalar15 < WorldMapRasterizer2D.topY) {
                              int localTopY = WorldMapRasterizer2D.topY - scalar15;
                              scalar19 -= localTopY;
                              scalar15 = 0;
                              canvasWidth += localTopY * WorldMapRasterizer2D.width;
                              scalar22 += scalar23 * localTopY;
                           }

                           if (scalar15 + scalar19 > WorldMapRasterizer2D.bottomY) {
                              scalar19 -= scalar15 + scalar19 - WorldMapRasterizer2D.bottomY;
                           }

                           if (scalar14 < WorldMapRasterizer2D.topX) {
                              int localTopX = WorldMapRasterizer2D.topX - scalar14;
                              scalar20 -= localTopX;
                              scalar14 = 0;
                              canvasWidth += localTopX;
                              scalar21 += position2 * localTopX;
                              canvasHeight += localTopX;
                           }

                           if (scalar14 + scalar20 > WorldMapRasterizer2D.bottomX) {
                              int scalar24 = scalar14 + scalar20 - WorldMapRasterizer2D.bottomX;
                              scalar20 -= scalar24;
                              canvasHeight += scalar24;
                           }

                           int scalar25 = width2;
                           int scalar26 = scalar23;
                           scalar23 = position2;
                           position2 = scalar19;
                           int position3 = scalar20;
                           int sourceCanvasHeight = canvasHeight;
                           width2 = canvasWidth;
                           scalar19 = scalar22;
                           scalar20 = scalar21;
                           int[] palette = sourceWorldMapIndexedSprite.palette;
                           byte[] pixelIndices = sourceWorldMapIndexedSprite.pixelIndices;
                           int[] pixels2 = WorldMapRasterizer2D.pixels;

                           try {
                              int scalar27 = scalar20;

                              for (int loopIndex75 = -position2; loopIndex75 < 0; loopIndex75++) {
                                 int scalar28 = (scalar19 >> 16) * scalar25;

                                 for (int loopIndex76 = -position3; loopIndex76 < 0; loopIndex76++) {
                                    byte pixelIndex;
                                    if ((pixelIndex = pixelIndices[(scalar20 >> 16) + scalar28]) != 0) {
                                       pixels2[width2++] = palette[pixelIndex & 0xFF];
                                    } else {
                                       width2++;
                                    }

                                    scalar20 += scalar23;
                                 }

                                 scalar19 += scalar26;
                                 scalar20 = scalar27;
                                 width2 += sourceCanvasHeight;
                              }
                           } catch (Exception exception) {
                              System.out.println("error in plot_scale");
                           }
                        } catch (Exception exception2) {
                           System.out.println("error in sprite clipping routine");
                        }
                     }

                     if ((tileBottomOrFunctionId = mapFunctionColumn[loopIndex71 + workingViewY] & 255) != 0) {
                        this.markerIconIds[markerIconIdIndex] = tileBottomOrFunctionId - 1;
                        this.markerScreenX[markerIconIdIndex] = tileScreenX + scalar13 / 2;
                        this.markerScreenY[markerIconIdIndex] = tileScreenY + loopIndex72 / 2;
                        markerIconIdIndex++;
                     }
                  }
               }
            }
         }

         for (int markerIconIdIndex2 = 0; markerIconIdIndex2 < markerIconIdIndex; markerIconIdIndex2++) {
            if (this.mapSprites[this.markerIconIds[markerIconIdIndex2]] != null) {
               this.mapSprites[this.markerIconIds[markerIconIdIndex2]].drawSprite(this.markerScreenX[markerIconIdIndex2] - 7, this.markerScreenY[markerIconIdIndex2] - 7);
            }
         }

         if (this.selectionFlashTicks > 0) {
            for (int markerIconIdIndex3 = 0; markerIconIdIndex3 < markerIconIdIndex; markerIconIdIndex3++) {
               if (this.markerIconIds[markerIconIdIndex3] == this.selectedMapFunctionType) {
                  this.mapSprites[this.markerIconIds[markerIconIdIndex3]].drawSprite(this.markerScreenX[markerIconIdIndex3] - 7, this.markerScreenY[markerIconIdIndex3] - 7);
                  if (this.selectionFlashTicks % 10 < 5) {
                     WorldMapRasterizer2D.drawCircleAlpha(this.markerScreenX[markerIconIdIndex3], this.markerScreenY[markerIconIdIndex3], 15, 16776960, 128);
                     WorldMapRasterizer2D.drawCircleAlpha(this.markerScreenX[markerIconIdIndex3], this.markerScreenY[markerIconIdIndex3], 7, 16777215, 256);
                  }
               }
            }
         }

         if (this.currentZoom == this.targetZoom && showMapLabels) {
            for (int labelXIndex = 0; labelXIndex < this.labelCount; labelXIndex++) {
               int labelMapX = this.labelX[labelXIndex];
               int labelMapY = this.labelY[labelXIndex];
               labelMapX -= mapOriginX;
               labelMapY = mapOriginY + mapHeight - labelMapY;
               int scalar29 = 0 + overviewWidth * (labelMapX - workingViewX) / (mapWidth - workingViewX);
               int scalar30 = 0 + overviewHeight * (labelMapY - workingViewY) / (mapHeight - workingViewY);
               int labelStyleEntry = this.labelStyle[labelXIndex];
               int scalar31 = 16777215;
               WorldMapSystemFont worldMapSystemFont = null;
               if (labelStyleEntry == 0) {
                  if (this.currentZoom == 3.0) {
                     worldMapSystemFont = this.labelFont11;
                  }

                  if (this.currentZoom == 4.0) {
                     worldMapSystemFont = this.labelFont12;
                  }

                  if (this.currentZoom == 6.0) {
                     worldMapSystemFont = this.labelFont14;
                  }

                  if (this.currentZoom == 8.0) {
                     worldMapSystemFont = this.labelFont17;
                  }
               }

               if (labelStyleEntry == 1) {
                  if (this.currentZoom == 3.0) {
                     worldMapSystemFont = this.labelFont14;
                  }

                  if (this.currentZoom == 4.0) {
                     worldMapSystemFont = this.labelFont17;
                  }

                  if (this.currentZoom == 6.0) {
                     worldMapSystemFont = this.labelFont19;
                  }

                  if (this.currentZoom == 8.0) {
                     worldMapSystemFont = this.labelFont22;
                  }
               }

               if (labelStyleEntry == 2) {
                  scalar31 = 16755200;
                  if (this.currentZoom == 3.0) {
                     worldMapSystemFont = this.labelFont19;
                  }

                  if (this.currentZoom == 4.0) {
                     worldMapSystemFont = this.labelFont22;
                  }

                  if (this.currentZoom == 6.0) {
                     worldMapSystemFont = this.labelFont26;
                  }

                  if (this.currentZoom == 8.0) {
                     worldMapSystemFont = this.labelFont30;
                  }
               }

               if (worldMapSystemFont != null) {
                  String text = this.labelTexts[labelXIndex];
                  int scalar32 = 1;

                  for (int loopIndex77 = 0; loopIndex77 < text.length(); loopIndex77++) {
                     if (text.charAt(loopIndex77) == '/') {
                        scalar32++;
                     }
                  }

                  int scalar33;
                  scalar30 = (scalar33 = scalar30 - worldMapSystemFont.getLineHeight() * (scalar32 - 1) / 2) + worldMapSystemFont.glyphData[6] / 2;

                  int position;
                  while ((position = text.indexOf("/")) != -1) {
                     String substring = text.substring(0, position);
                     worldMapSystemFont.drawCenteredString(substring, scalar29, scalar30, scalar31, true);
                     scalar30 += worldMapSystemFont.getLineHeight();
                     text = text.substring(position + 1);
                  }

                  worldMapSystemFont.drawCenteredString(text, scalar29, scalar30, scalar31, true);
               }
            }
         }
      }
   }
   private WorldMapArchive loadMapArchive() {
      String text = null;

      try {
         text = SignLink.findcachedir();
         byte[] worldMapData = WorldMapFileLoader.readFile(text + "worldmap.dat");
         return new WorldMapArchive(worldMapData);
      } catch (Throwable throwable) {
         byte[] mapArchiveBytes = this.loadMapArchiveBytes();
         if (text != null && mapArchiveBytes != null) {
            try {
               String sourceText = text + "/worldmap.dat";
               byte[] sourceMapArchiveBytes = mapArchiveBytes;
               text = sourceText;
               FileOutputStream fileOutputStream;
               (fileOutputStream = new FileOutputStream(text)).write(sourceMapArchiveBytes, 0, sourceMapArchiveBytes.length);
               fileOutputStream.close();
            } catch (Throwable exception) {
            }
         }

         return new WorldMapArchive(mapArchiveBytes);
      }
   }
   private byte[] loadMapArchiveBytes() {
      this.drawLoadingText(0, "Requesting map");

      try {
         String text = "";

         for (int componentValueIndex = 0; componentValueIndex < 10; componentValueIndex++) {
            text = text + WorldMapVersion.componentValues[componentValueIndex];
         }

         DataInputStream dataInputStream;
         if (super.frame != null) {
            dataInputStream = new DataInputStream(new FileInputStream("worldmap.dat"));
         } else {
            dataInputStream = new DataInputStream(new URL(this.getCodeBase(), "worldmap" + text + ".dat").openStream());
         }

         int scalar = 0;
         int scalar2 = 0;
         byte[] byteBuffer = new byte[342273];

         while (scalar2 < 342273) {
            int decodedEntry;
            if ((decodedEntry = 342273 - scalar2) > 1000) {
               decodedEntry = 1000;
            }

            if ((decodedEntry = dataInputStream.read(byteBuffer, scalar2, decodedEntry)) < 0) {
               throw new IOException("End of file");
            }

            if ((decodedEntry = (scalar2 += decodedEntry) * 100 / 342273) != scalar) {
               this.drawLoadingText(decodedEntry, "Loading map - " + decodedEntry + "%");
            }

            scalar = decodedEntry;
         }

         dataInputStream.close();
         return byteBuffer;
      } catch (IOException exception) {
         System.out.println("Error loading");
         exception.printStackTrace();
         return null;
      }
   }

   public WorldMapViewer() {
      this.buttonHighlightColor = 8943445;
      this.buttonFillColor = 7824964;
      this.buttonShadowColor = 6706483;
      this.selectedButtonHighlightColor = 11141120;
      this.selectedButtonFillColor = 10027008;
      this.selectedButtonShadowColor = 8912896;
      this.redrawNeeded = true;
      this.mapIcons = new WorldMapIndexedSprite[100];
      this.mapSprites = new WorldMapSprite[100];
      this.markerScreenX = new int[2000];
      this.markerScreenY = new int[2000];
      this.markerIconIds = new int[2000];
      this.mapFunctionX = new int[2000];
      this.mapFunctionY = new int[2000];
      this.mapFunctionTypes = new int[2000];
      this.keyPanelX = 5;
      this.keyPanelY = 13;
      this.keyPanelWidth = 140;
      this.keyPanelHeight = 470;
      this.keyVisible = false;
      this.hoveredKeyIndex = -1;
      this.previousHoveredKeyIndex = -1;
      this.selectedMapFunctionType = -1;
      this.overviewVisible = false;
      this.labelCapacity = 1000;
      this.labelTexts = new String[this.labelCapacity];
      this.labelX = new int[this.labelCapacity];
      this.labelY = new int[this.labelCapacity];
      this.labelStyle = new int[this.labelCapacity];
      this.currentZoom = 4.0;
      this.targetZoom = 4.0;
   }
}
