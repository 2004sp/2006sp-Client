package client;
final class SceneGraph {
   private boolean unusedFlag;
   public static boolean lowMemory = true;
   private final int planeCount;
   private final int width;
   private final int height;
   private final int[][][] tileHeights;
   private final SceneTile[][][] tiles;
   private int basePlane;
   private int temporaryObjectCount;
   private final InteractiveObject[] temporaryObjects;
   private final int[][][] tileOcclusionCycles;
   private static int visibleTileCount;
   private static int renderPlane;
   private static int renderCycle;
   private static int minTileX;
   private static int maxTileX;
   private static int minTileY;
   private static int maxTileY;
   private static int cameraTileX;
   private static int cameraTileY;
   private static int cameraX;
   private static int cameraZ;
   private static int cameraY;
   private static int pitchSin;
   private static int pitchCos;
   private static int yawSin;
   private static int yawCos;
   private static InteractiveObject[] entityBuffer = new InteractiveObject[100];
   private static final int[] decorXOffsetsA = new int[]{53, -53, -53, 53};
   private static final int[] decorYOffsetsA = new int[]{-53, -53, 53, 53};
   private static final int[] decorXOffsetsB = new int[]{-45, 45, 45, -45};
   private static final int[] decorYOffsetsB = new int[]{45, 45, -45, -45};
   private static boolean checkClick;
   private static int clickX;
   private static int clickY;
   public static int clickedTileX = -1;
   public static int clickedTileY = -1;
   private static int[] occluderCounts = new int[4];
   private static Occluder[][] occluders = new Occluder[4][500];
   private static int activeOccluderCount;
   private static final Occluder[] activeOccluders = new Occluder[500];
   private static NodeDeque tileQueue = new NodeDeque();
   private static final int[] wallVisibilityFlags = new int[]{19, 55, 38, 155, 255, 110, 137, 205, 76};
   private static final int[] wallCullFlags = new int[]{160, 192, 80, 96, 0, 144, 80, 48, 160};
   private static final int[] wallDeferredFlags = new int[]{76, 8, 137, 4, 0, 1, 38, 2, 19};
   private static final int[] wallCullMap16 = new int[]{0, 0, 2, 0, 0, 2, 1, 1, 0};
   private static final int[] wallCullMap32 = new int[]{2, 0, 0, 2, 0, 0, 0, 4, 4};
   private static final int[] wallCullMap64 = new int[]{0, 4, 4, 8, 0, 0, 8, 0, 0};
   private static final int[] wallCullMap128 = new int[]{1, 1, 0, 0, 0, 8, 0, 0, 8};
   private static final int[] textureBaseColors = new int[]{
      41,
      39248,
      41,
      4643,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      43086,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      8602,
      41,
      28992,
      41,
      41,
      41,
      41,
      41,
      5056,
      41,
      41,
      41,
      7079,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      41,
      3131,
      41,
      41,
      41
   };
   private final int[] mergeStampA;
   private final int[] mergeStampB;
   private int mergeCycle;
   private final int[][] tileShapeMasks = new int[][]{
      new int[16],
      {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
      {1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1},
      {1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
      {0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1},
      {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
      {1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
      {1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0},
      {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1},
      {1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1}
   };
   private final int[][] tileRotationMaps = new int[][]{
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
      {12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3},
      {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
      {3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12}
   };
   private static boolean[][][][] visibilityMaps = new boolean[8][32][51][51];
   private static boolean[][] visibilityMap;
   private static int viewportCenterX;
   private static int viewportCenterY;
   private static int viewportMinX;
   private static int viewportMinY;
   private static int viewportMaxX;
   private static int viewportMaxY;

   public SceneGraph(int[][][] newTileHeights) {
      this.unusedFlag = true;
      this.temporaryObjects = new InteractiveObject[5000];
      this.mergeStampA = new int[10000];
      this.mergeStampB = new int[10000];
      this.planeCount = 4;
      this.width = 104;
      this.height = 104;
      this.tiles = new SceneTile[4][104][104];
      this.tileOcclusionCycles = new int[4][105][105];
      this.tileHeights = newTileHeights;
      this.initToNull();
   }
   public static void nullLoader() {
      entityBuffer = null;
      occluderCounts = null;
      occluders = null;
      tileQueue = null;
      visibilityMaps = null;
      visibilityMap = null;
   }
   public final void initToNull() {
      for (int tileIndex = 0; tileIndex < 4; tileIndex++) {
         for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < 104; loopIndex2++) {
               this.tiles[tileIndex][loopIndex][loopIndex2] = null;
            }
         }
      }

      for (int occluderCountIndex = 0; occluderCountIndex < 4; occluderCountIndex++) {
         for (int loopIndex3 = 0; loopIndex3 < occluderCounts[occluderCountIndex]; loopIndex3++) {
            occluders[occluderCountIndex][loopIndex3] = null;
         }

         occluderCounts[occluderCountIndex] = 0;
      }

      for (int temporaryObjectIndex = 0; temporaryObjectIndex < this.temporaryObjectCount; temporaryObjectIndex++) {
         this.temporaryObjects[temporaryObjectIndex] = null;
      }

      this.temporaryObjectCount = 0;

      for (int entityBufferIndex = 0; entityBufferIndex < entityBuffer.length; entityBufferIndex++) {
         entityBuffer[entityBufferIndex] = null;
      }
   }
   public final void setPlane(int newBasePlane) {
      this.basePlane = newBasePlane;

      for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < 104; loopIndex2++) {
            if (this.tiles[newBasePlane][loopIndex][loopIndex2] == null) {
               this.tiles[newBasePlane][loopIndex][loopIndex2] = new SceneTile(newBasePlane, loopIndex, loopIndex2);
            }
         }
      }
   }
   public final void setTileBridge(int positionArgument, int newIndex) {
      SceneTile sceneTile = this.tiles[0][newIndex][positionArgument];

      for (int tileIndex = 0; tileIndex < 3; tileIndex++) {
         SceneTile sceneTile2;
         if ((sceneTile2 = this.tiles[tileIndex][newIndex][positionArgument] = this.tiles[tileIndex + 1][newIndex][positionArgument]) != null) {
            sceneTile2.plane--;

            for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile2.interactiveObjectCount; interactiveObjectIndex++) {
               InteractiveObject interactiveObject;
               if (((interactiveObject = sceneTile2.interactiveObjects[interactiveObjectIndex]).hash >> 29 & 3) == 2 && interactiveObject.tileLeft == newIndex && interactiveObject.tileTop == positionArgument) {
                  interactiveObject.z--;
               }
            }
         }
      }

      if (this.tiles[0][newIndex][positionArgument] == null) {
         this.tiles[0][newIndex][positionArgument] = new SceneTile(0, newIndex, positionArgument);
      }

      this.tiles[0][newIndex][positionArgument].linkedBelowTile = sceneTile;
      this.tiles[3][newIndex][positionArgument] = null;
   }
   public static void createOccluder(int occluderIndex, int newMinTileX, int newMaxY, int newMaxTileX, int newMaxTileY, int newMinY, int newMinTileY, int newType) {
      Occluder occluder;
      (occluder = new Occluder()).minTileX = newMinTileX / 128;
      occluder.maxTileX = newMaxTileX / 128;
      occluder.minTileY = newMinTileY / 128;
      occluder.maxTileY = newMaxTileY / 128;
      occluder.type = newType;
      occluder.minX = newMinTileX;
      occluder.maxX = newMaxTileX;
      occluder.minZ = newMinTileY;
      occluder.maxZ = newMaxTileY;
      occluder.minY = newMinY;
      occluder.maxY = newMaxY;
      occluders[occluderIndex][occluderCounts[occluderIndex]++] = occluder;
   }
   public final void setDrawLevel(int tileIndex, int scalarArgument, int scalarArgument2, int newDrawLevel) {
      if (this.tiles[tileIndex][scalarArgument][scalarArgument2] != null) {
         this.tiles[tileIndex][scalarArgument][scalarArgument2].drawLevel = newDrawLevel;
      }
   }
   public final void addTile(
      int tileIndex2,
      int newIndex,
      int positionArgument,
      int scalarArgument,
      int scalarArgument2,
      int averageTextureColorIndex,
      int scalarArgument3,
      int scalarArgument4,
      int scalarArgument5,
      int scalarArgument6,
      int scalarArgument7,
      int scalarArgument8,
      int scalarArgument9,
      int scalarArgument10,
      int scalarArgument11,
      int scalarArgument12,
      int scalarArgument13,
      int scalarArgument14,
      int scalarArgument15,
      int overlayTextureColor
   ) {
      if (scalarArgument == 0) {
         PlainTile plainTile = new PlainTile(scalarArgument7, scalarArgument8, scalarArgument9, scalarArgument10, -1, scalarArgument15, false);

         for (int tileIndex = tileIndex2; tileIndex >= 0; tileIndex--) {
            if (this.tiles[tileIndex][newIndex][positionArgument] == null) {
               this.tiles[tileIndex][newIndex][positionArgument] = new SceneTile(tileIndex, newIndex, positionArgument);
            }
         }

         this.tiles[tileIndex2][newIndex][positionArgument].plainTile = plainTile;
      } else if (scalarArgument != 1) {
         ShapedTile shapedTile = new ShapedTile(
            positionArgument, scalarArgument11, scalarArgument10, scalarArgument5, averageTextureColorIndex, scalarArgument13, scalarArgument2, scalarArgument7, scalarArgument15, scalarArgument9, scalarArgument6, scalarArgument4, scalarArgument3, scalarArgument, scalarArgument14, scalarArgument12, scalarArgument8, newIndex, overlayTextureColor
         );

         for (int tileIndex3 = tileIndex2; tileIndex3 >= 0; tileIndex3--) {
            if (this.tiles[tileIndex3][newIndex][positionArgument] == null) {
               this.tiles[tileIndex3][newIndex][positionArgument] = new SceneTile(tileIndex3, newIndex, positionArgument);
            }
         }

         this.tiles[tileIndex2][newIndex][positionArgument].shapedTile = shapedTile;
      } else {
         PlainTile plainTile2 = new PlainTile(scalarArgument11, scalarArgument12, scalarArgument13, scalarArgument14, averageTextureColorIndex, overlayTextureColor, scalarArgument3 == scalarArgument4 && scalarArgument3 == scalarArgument5 && scalarArgument3 == scalarArgument6);

         for (int tileIndex4 = tileIndex2; tileIndex4 >= 0; tileIndex4--) {
            if (this.tiles[tileIndex4][newIndex][positionArgument] == null) {
               this.tiles[tileIndex4][newIndex][positionArgument] = new SceneTile(tileIndex4, newIndex, positionArgument);
            }
         }

         this.tiles[tileIndex2][newIndex][positionArgument].plainTile = plainTile2;
      }
   }
   public final void addGroundDecoration(int tileIndex, int newZ, int newIndex, Renderable newRenderable, byte newConfig, int newHash, int clippingDataIndex) {
      if (newRenderable != null) {
         WallDecoration wallDecoration;
         (wallDecoration = new WallDecoration()).renderable = newRenderable;
         wallDecoration.x = (clippingDataIndex << 7) + 64;
         wallDecoration.y = (newIndex << 7) + 64;
         wallDecoration.z = newZ;
         wallDecoration.hash = newHash;
         wallDecoration.config = newConfig;
         if (this.tiles[tileIndex][clippingDataIndex][newIndex] == null) {
            this.tiles[tileIndex][clippingDataIndex][newIndex] = new SceneTile(tileIndex, clippingDataIndex, newIndex);
         }

         this.tiles[tileIndex][clippingDataIndex][newIndex].floorDecoration = wallDecoration;
      }
   }
   public final void addGroundItemTile(int positionArgument, int newHash, Renderable newRenderable, int newZ, Renderable renderable2, Renderable renderable3, int newIndex, int positionArgument2) {
      GroundItemPile groundItemPile;
      (groundItemPile = new GroundItemPile()).firstGroundItem = renderable3;
      groundItemPile.x = (positionArgument << 7) + 64;
      groundItemPile.y = (positionArgument2 << 7) + 64;
      groundItemPile.z = newZ;
      groundItemPile.hash = newHash;
      groundItemPile.secondGroundItem = newRenderable;
      groundItemPile.thirdGroundItem = renderable2;
      newHash = 0;
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[newIndex][positionArgument][positionArgument2]) != null) {
         for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
            if (sceneTile.interactiveObjects[interactiveObjectIndex].renderable instanceof Model) {
               int sceneHeightOffset = ((Model)sceneTile.interactiveObjects[interactiveObjectIndex].renderable).sceneHeightOffset;
               if (((Model)sceneTile.interactiveObjects[interactiveObjectIndex].renderable).sceneHeightOffset > newHash) {
                  newHash = sceneHeightOffset;
               }
            }
         }
      }

      groundItemPile.heightOffset = newHash;
      if (this.tiles[newIndex][positionArgument][positionArgument2] == null) {
         this.tiles[newIndex][positionArgument][positionArgument2] = new SceneTile(newIndex, positionArgument, positionArgument2);
      }

      this.tiles[newIndex][positionArgument][positionArgument2].groundItemPile = groundItemPile;
   }
   public final void addWall(int newOrientation, Renderable renderable, int newHash, int positionArgument, byte newConfig, int newIndex, Renderable newRenderable, int newZ, int newOrientationB, int tileIndex) {
      if (renderable != null || newRenderable != null) {
         WallObject wallObject;
         (wallObject = new WallObject()).hash = newHash;
         wallObject.config = newConfig;
         wallObject.x = (newIndex << 7) + 64;
         wallObject.y = (positionArgument << 7) + 64;
         wallObject.z = newZ;
         wallObject.primary = renderable;
         wallObject.secondary = newRenderable;
         wallObject.orientation = newOrientation;
         wallObject.orientationB = newOrientationB;

         for (int sourceTileIndex = tileIndex; sourceTileIndex >= 0; sourceTileIndex--) {
            if (this.tiles[sourceTileIndex][newIndex][positionArgument] == null) {
               this.tiles[sourceTileIndex][newIndex][positionArgument] = new SceneTile(sourceTileIndex, newIndex, positionArgument);
            }
         }

         this.tiles[tileIndex][newIndex][positionArgument].wall = wallObject;
      }
   }
   public final void addWallDecoration(int newHash, int positionArgument, int newFace, int newIndex, int scalarArgument, int newZ, Renderable newRenderable, int clippingDataIndex, byte newConfig, int scalarArgument2, int newConfigBits) {
      if (newRenderable != null) {
         GroundDecoration groundDecoration;
         (groundDecoration = new GroundDecoration()).hash = newHash;
         groundDecoration.config = newConfig;
         groundDecoration.x = (clippingDataIndex << 7) + 64 + scalarArgument;
         groundDecoration.y = (positionArgument << 7) + 64 + scalarArgument2;
         groundDecoration.z = newZ;
         groundDecoration.renderable = newRenderable;
         groundDecoration.configBits = newConfigBits;
         groundDecoration.face = newFace;

         for (int tileIndex = newIndex; tileIndex >= 0; tileIndex--) {
            if (this.tiles[tileIndex][clippingDataIndex][positionArgument] == null) {
               this.tiles[tileIndex][clippingDataIndex][positionArgument] = new SceneTile(tileIndex, clippingDataIndex, positionArgument);
            }
         }

         this.tiles[newIndex][clippingDataIndex][positionArgument].wallDecoration = groundDecoration;
      }
   }
   public final boolean addEntity(int scalarArgument, byte byteCodeArgument, int scalarArgument2, int scalarArgument3, Renderable renderable, int scalarArgument4, int tileIndex, int scalarArgument5, int scalarArgument6, int clippingDataIndex) {
      if (renderable == null) {
         return true;
      }

      int scalar = (clippingDataIndex << 7) + scalarArgument4 * 64;
      int scalar2 = (scalarArgument6 << 7) + scalarArgument3 * 64;
      return this.addRenderable(tileIndex, clippingDataIndex, scalarArgument6, scalarArgument4, scalarArgument3, scalar, scalar2, scalarArgument2, renderable, scalarArgument5, false, scalarArgument, byteCodeArgument);
   }
   public final boolean addEntityWithRadius(int plane, int orientation, int scalarArgument, int scalarArgument2, int worldY, int scalarArgument3, int worldX, Renderable renderable, boolean flag) {
      if (renderable == null) {
         return true;
      }

      int scalar = worldX - scalarArgument3;
      int scalar2 = worldY - scalarArgument3;
      int scalar3 = worldX + scalarArgument3;
      scalarArgument3 = worldY + scalarArgument3;
      if (flag) {
         if (orientation > 640 && orientation < 1408) {
            scalarArgument3 += 128;
         }

         if (orientation > 1152 && orientation < 1920) {
            scalar3 += 128;
         }

         if (orientation > 1664 || orientation < 384) {
            scalar2 -= 128;
         }

         if (orientation > 128 && orientation < 896) {
            scalar -= 128;
         }
      }

      scalar /= 128;
      scalar2 /= 128;
      scalar3 /= 128;
      scalarArgument3 /= 128;
      return this.addRenderable(plane, scalar, scalar2, scalar3 - scalar + 1, scalarArgument3 - scalar2 + 1, worldX, worldY, scalarArgument, renderable, orientation, true, scalarArgument2, (byte)0);
   }
   public final boolean addEntityBounds(int plane, int worldY, Renderable renderable, int orientation, int attachedModelMaxY, int worldX, int attachedModelBaseHeight, int attachedModelMinX, int attachedModelMaxX, int scalarArgument, int attachedModelMinY) {
      return renderable == null || this.addRenderable(plane, attachedModelMinX, attachedModelMinY, attachedModelMaxX - attachedModelMinX + 1, attachedModelMaxY - attachedModelMinY + 1, worldX, worldY, attachedModelBaseHeight, renderable, orientation, true, scalarArgument, (byte)0);
   }
   private boolean addRenderable(
      int sourceZ, int newIndex, int sourceTileTop, int scalarArgument, int scalarArgument2, int interactiveObjectFlagOrWorldX, int newWorldY, int newWorldZ, Renderable newRenderable, int newRotation, boolean flag, int newHash, byte newConfig
   ) {
      for (int loopIndex = newIndex; loopIndex < newIndex + scalarArgument; loopIndex++) {
         for (int loopIndex2 = sourceTileTop; loopIndex2 < sourceTileTop + scalarArgument2; loopIndex2++) {
            if (loopIndex < 0 || loopIndex2 < 0 || loopIndex >= 104 || loopIndex2 >= 104) {
               return false;
            }

            SceneTile sceneTile;
            if ((sceneTile = this.tiles[sourceZ][loopIndex][loopIndex2]) != null && sceneTile.interactiveObjectCount >= 5) {
               return false;
            }
         }
      }

      InteractiveObject interactiveObject;
      (interactiveObject = new InteractiveObject()).hash = newHash;
      interactiveObject.config = newConfig;
      interactiveObject.z = sourceZ;
      interactiveObject.worldX = interactiveObjectFlagOrWorldX;
      interactiveObject.worldY = newWorldY;
      interactiveObject.worldZ = newWorldZ;
      interactiveObject.renderable = newRenderable;
      interactiveObject.rotation = newRotation;
      interactiveObject.tileLeft = newIndex;
      interactiveObject.tileTop = sourceTileTop;
      interactiveObject.tileRight = newIndex + scalarArgument - 1;
      interactiveObject.tileBottom = sourceTileTop + scalarArgument2 - 1;

      for (int loopIndex3 = newIndex; loopIndex3 < newIndex + scalarArgument; loopIndex3++) {
         for (int loopIndex4 = sourceTileTop; loopIndex4 < sourceTileTop + scalarArgument2; loopIndex4++) {
            interactiveObjectFlagOrWorldX = 0;
            if (loopIndex3 > newIndex) {
               interactiveObjectFlagOrWorldX++;
            }

            if (loopIndex3 < newIndex + scalarArgument - 1) {
               interactiveObjectFlagOrWorldX += 4;
            }

            if (loopIndex4 > sourceTileTop) {
               interactiveObjectFlagOrWorldX += 8;
            }

            if (loopIndex4 < sourceTileTop + scalarArgument2 - 1) {
               interactiveObjectFlagOrWorldX += 2;
            }

            for (int tileIndex = sourceZ; tileIndex >= 0; tileIndex--) {
               if (this.tiles[tileIndex][loopIndex3][loopIndex4] == null) {
                  this.tiles[tileIndex][loopIndex3][loopIndex4] = new SceneTile(tileIndex, loopIndex3, loopIndex4);
               }
            }

            SceneTile sceneTile2;
            (sceneTile2 = this.tiles[sourceZ][loopIndex3][loopIndex4]).interactiveObjects[sceneTile2.interactiveObjectCount] = interactiveObject;
            sceneTile2.interactiveObjectFlags[sceneTile2.interactiveObjectCount] = interactiveObjectFlagOrWorldX;
            sceneTile2.interactiveObjectFlagsOr |= interactiveObjectFlagOrWorldX;
            sceneTile2.interactiveObjectCount++;
         }
      }

      if (flag) {
         this.temporaryObjects[this.temporaryObjectCount++] = interactiveObject;
      }

      return true;
   }
   public final void clearInteractiveObjectCache() {
      for (int temporaryObjectIndex = 0; temporaryObjectIndex < this.temporaryObjectCount; temporaryObjectIndex++) {
         InteractiveObject interactiveObject = this.temporaryObjects[temporaryObjectIndex];
         this.removeInteractiveObjectInstance(interactiveObject);
         this.temporaryObjects[temporaryObjectIndex] = null;
      }

      this.temporaryObjectCount = 0;
   }
   private void removeInteractiveObjectInstance(InteractiveObject interactiveObject) {
      for (int tileLeft = interactiveObject.tileLeft; tileLeft <= interactiveObject.tileRight; tileLeft++) {
         for (int tileTop = interactiveObject.tileTop; tileTop <= interactiveObject.tileBottom; tileTop++) {
            SceneTile sceneTile;
            if ((sceneTile = this.tiles[interactiveObject.z][tileLeft][tileTop]) != null) {
               for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
                  if (sceneTile.interactiveObjects[interactiveObjectIndex] == interactiveObject) {
                     sceneTile.interactiveObjectCount--;

                     for (int sourceInteractiveObjectIndex = interactiveObjectIndex; sourceInteractiveObjectIndex < sceneTile.interactiveObjectCount; sourceInteractiveObjectIndex++) {
                        sceneTile.interactiveObjects[sourceInteractiveObjectIndex] = sceneTile.interactiveObjects[sourceInteractiveObjectIndex + 1];
                        sceneTile.interactiveObjectFlags[sourceInteractiveObjectIndex] = sceneTile.interactiveObjectFlags[sourceInteractiveObjectIndex + 1];
                     }

                     sceneTile.interactiveObjects[sceneTile.interactiveObjectCount] = null;
                     break;
                  }
               }

               sceneTile.interactiveObjectFlagsOr = 0;

               for (int interactiveObjectFlagIndex = 0; interactiveObjectFlagIndex < sceneTile.interactiveObjectCount; interactiveObjectFlagIndex++) {
                  sceneTile.interactiveObjectFlagsOr = sceneTile.interactiveObjectFlagsOr | sceneTile.interactiveObjectFlags[interactiveObjectFlagIndex];
               }
            }
         }
      }
   }
   public final void setWallDecorationOffset(int newY, int decorDisplacement, int newX, int tileIndex) {
      SceneTile sceneTile;
      GroundDecoration groundDecoration;
      if ((sceneTile = this.tiles[tileIndex][newX][newY]) != null && (groundDecoration = sceneTile.wallDecoration) != null) {
         newX = (newX << 7) + 64;
         newY = (newY << 7) + 64;
         groundDecoration.x = newX + (groundDecoration.x - newX) * decorDisplacement / 16;
         groundDecoration.y = newY + (groundDecoration.y - newY) * decorDisplacement / 16;
      }
   }
   public final void removeWall(int tileX, int plane, int tileY, byte unused) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[plane][tileX][tileY]) != null) {
         sceneTile.wall = null;
      }
   }
   public final void removeWallDecoration(int tileY, int plane, int tileX) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[plane][tileX][tileY]) != null) {
         sceneTile.wallDecoration = null;
      }
   }
   public final void removeInteractiveObject(int plane, int tileX, int tileY) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[plane][tileX][tileY]) != null) {
         for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
            InteractiveObject interactiveObject;
            if (((interactiveObject = sceneTile.interactiveObjects[interactiveObjectIndex]).hash >> 29 & 3) == 2 && interactiveObject.tileLeft == tileX && interactiveObject.tileTop == tileY) {
               this.removeInteractiveObjectInstance(interactiveObject);
               return;
            }
         }
      }
   }
   public final void removeFloorDecoration(int plane, int tileY, int tileX) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[plane][tileX][tileY]) != null) {
         sceneTile.floorDecoration = null;
      }
   }
   public final void removeGroundItems(int tileIndex, int scalarArgument, int scalarArgument2) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[tileIndex][scalarArgument][scalarArgument2]) != null) {
         sceneTile.groundItemPile = null;
      }
   }
   public final WallObject getWall(int tileIndex, int scalarArgument, int scalarArgument2) {
      SceneTile sceneTile;
      return (sceneTile = this.tiles[tileIndex][scalarArgument][scalarArgument2]) == null ? null : sceneTile.wall;
   }
   public final GroundDecoration getWallDecoration(int scalarArgument, int scalarArgument2, int tileIndex) {
      SceneTile sceneTile;
      return (sceneTile = this.tiles[tileIndex][scalarArgument][scalarArgument2]) == null ? null : sceneTile.wallDecoration;
   }
   public final InteractiveObject getInteractiveObject(int scalarArgument, int scalarArgument2, int tileIndex) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[tileIndex][scalarArgument][scalarArgument2]) == null) {
         return null;
      }

      for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
         InteractiveObject interactiveObject;
         if (((interactiveObject = sceneTile.interactiveObjects[interactiveObjectIndex]).hash >> 29 & 3) == 2 && interactiveObject.tileLeft == scalarArgument && interactiveObject.tileTop == scalarArgument2) {
            return interactiveObject;
         }
      }

      return null;
   }
   public final WallDecoration getFloorDecoration(int scalarArgument, int scalarArgument2, int tileIndex) {
      SceneTile sceneTile;
      return (sceneTile = this.tiles[tileIndex][scalarArgument2][scalarArgument]) != null && sceneTile.floorDecoration != null ? sceneTile.floorDecoration : null;
   }
   public final int getWallHash(int plane, int tileX, int tileY) {
      SceneTile sceneTile;
      return (sceneTile = this.tiles[plane][tileX][tileY]) != null && sceneTile.wall != null ? sceneTile.wall.hash : 0;
   }
   public final int getWallDecorationHash(int plane, int tileX, int tileY) {
      SceneTile sceneTile;
      return (sceneTile = this.tiles[plane][tileX][tileY]) != null && sceneTile.wallDecoration != null ? sceneTile.wallDecoration.hash : 0;
   }
   public final int getInteractiveObjectHash(int plane, int tileX, int tileY) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[plane][tileX][tileY]) == null) {
         return 0;
      }

      for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
         InteractiveObject interactiveObject;
         if (((interactiveObject = sceneTile.interactiveObjects[interactiveObjectIndex]).hash >> 29 & 3) == 2 && interactiveObject.tileLeft == tileX && interactiveObject.tileTop == tileY) {
            return interactiveObject.hash;
         }
      }

      return 0;
   }
   public final int getFloorDecorationHash(int plane, int tileX, int tileY) {
      SceneTile sceneTile;
      return (sceneTile = this.tiles[plane][tileX][tileY]) != null && sceneTile.floorDecoration != null ? sceneTile.floorDecoration.hash : 0;
   }
   public final int getArrangement(int plane, int tileX, int tileY, int objectHash) {
      SceneTile sceneTile;
      if ((sceneTile = this.tiles[plane][tileX][tileY]) == null) {
         return -1;
      }

      if (sceneTile.wall != null && sceneTile.wall.hash == objectHash) {
         return sceneTile.wall.config & 0xFF;
      }

      if (sceneTile.wallDecoration != null && sceneTile.wallDecoration.hash == objectHash) {
         return sceneTile.wallDecoration.config & 0xFF;
      }

      if (sceneTile.floorDecoration != null && sceneTile.floorDecoration.hash == objectHash) {
         return sceneTile.floorDecoration.config & 0xFF;
      }

      for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
         if (sceneTile.interactiveObjects[interactiveObjectIndex].hash == objectHash) {
            return sceneTile.interactiveObjects[interactiveObjectIndex].config & 0xFF;
         }
      }

      return -1;
   }
   public final void buildModels(int contrast, int scalarArgument, int scalarArgument2) {
      contrast = (int)Math.ceil(1269.0) + 2750 >> 4;

      for (int tileHeightIndex = 0; tileHeightIndex < 4; tileHeightIndex++) {
         for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < 104; loopIndex2++) {
               SceneTile sceneTile;
               if ((sceneTile = this.tiles[tileHeightIndex][loopIndex][loopIndex2]) != null) {
                  WallObject wallObject = sceneTile.wall;
                  if (sceneTile.wall != null && wallObject.primary != null && wallObject.primary.vertexNormals != null) {
                     this.mergeObjectNormals(tileHeightIndex, 1, 1, loopIndex, loopIndex2, (Model)wallObject.primary);
                     if (wallObject.secondary != null && wallObject.secondary.vertexNormals != null) {
                        this.mergeObjectNormals(tileHeightIndex, 1, 1, loopIndex, loopIndex2, (Model)wallObject.secondary);
                        this.mergeNormals((Model)wallObject.primary, (Model)wallObject.secondary, 0, 0, 0, false);
                        ((Model)wallObject.secondary).applyLighting(100, contrast, -50, -10, -50);
                     }

                     ((Model)wallObject.primary).applyLighting(100, contrast, -50, -10, -50);
                  }

                  for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
                     InteractiveObject interactiveObject;
                     if ((interactiveObject = sceneTile.interactiveObjects[interactiveObjectIndex]) != null && interactiveObject.renderable != null && interactiveObject.renderable.vertexNormals != null) {
                        this.mergeObjectNormals(
                           tileHeightIndex, interactiveObject.tileRight - interactiveObject.tileLeft + 1, interactiveObject.tileBottom - interactiveObject.tileTop + 1, loopIndex, loopIndex2, (Model)interactiveObject.renderable
                        );
                        ((Model)interactiveObject.renderable).applyLighting(100, contrast, -50, -10, -50);
                     }
                  }

                  WallDecoration wallDecoration = sceneTile.floorDecoration;
                  if (sceneTile.floorDecoration != null && wallDecoration.renderable.vertexNormals != null) {
                     Model model = (Model)wallDecoration.renderable;
                     int position = loopIndex2;
                     Model sourceModel = model;
                     int tileIndex = tileHeightIndex;
                     int position2 = loopIndex;
                     SceneGraph sceneGraph = this;
                     SceneTile sceneTile2;
                     if (position2 < 104
                        && (sceneTile2 = sceneGraph.tiles[tileIndex][position2 + 1][position]) != null
                        && sceneTile2.floorDecoration != null
                        && sceneTile2.floorDecoration.renderable.vertexNormals != null) {
                        sceneGraph.mergeNormals(sourceModel, (Model)sceneTile2.floorDecoration.renderable, 128, 0, 0, true);
                     }

                     if (position < 104
                        && (sceneTile2 = sceneGraph.tiles[tileIndex][position2][position + 1]) != null
                        && sceneTile2.floorDecoration != null
                        && sceneTile2.floorDecoration.renderable.vertexNormals != null) {
                        sceneGraph.mergeNormals(sourceModel, (Model)sceneTile2.floorDecoration.renderable, 0, 0, 128, true);
                     }

                     if (position2 < 104
                        && position < 104
                        && (sceneTile2 = sceneGraph.tiles[tileIndex][position2 + 1][position + 1]) != null
                        && sceneTile2.floorDecoration != null
                        && sceneTile2.floorDecoration.renderable.vertexNormals != null) {
                        sceneGraph.mergeNormals(sourceModel, (Model)sceneTile2.floorDecoration.renderable, 128, 0, 128, true);
                     }

                     if (position2 < 104
                        && position > 0
                        && (sceneTile2 = sceneGraph.tiles[tileIndex][position2 + 1][position - 1]) != null
                        && sceneTile2.floorDecoration != null
                        && sceneTile2.floorDecoration.renderable.vertexNormals != null) {
                        sceneGraph.mergeNormals(sourceModel, (Model)sceneTile2.floorDecoration.renderable, 128, 0, -128, true);
                     }

                     ((Model)wallDecoration.renderable).applyLighting(100, contrast, -50, -10, -50);
                  }
               }
            }
         }
      }
   }
   private void mergeObjectNormals(int tileHeightIndex, int scalarArgument, int scalarArgument2, int newIndex, int positionArgument, Model model) {
      boolean flag = true;
      int position = newIndex;
      int loopIndex = newIndex + scalarArgument;
      int position2 = positionArgument - 1;
      int loopIndex2 = positionArgument + scalarArgument2;

      for (int tileIndex = tileHeightIndex; tileIndex <= tileHeightIndex + 1; tileIndex++) {
         if (tileIndex != 4) {
            for (int loopIndex3 = position; loopIndex3 <= loopIndex; loopIndex3++) {
               if (loopIndex3 >= 0 && loopIndex3 < 104) {
                  for (int loopIndex4 = position2; loopIndex4 <= loopIndex2; loopIndex4++) {
                     SceneTile sceneTile;
                     if (loopIndex4 >= 0
                        && loopIndex4 < 104
                        && (!flag || loopIndex3 >= loopIndex || loopIndex4 >= loopIndex2 || loopIndex4 < positionArgument && loopIndex3 != newIndex)
                        && (sceneTile = this.tiles[tileIndex][loopIndex3][loopIndex4]) != null) {
                        int scalar = (
                                 this.tileHeights[tileIndex][loopIndex3][loopIndex4]
                                    + this.tileHeights[tileIndex][loopIndex3 + 1][loopIndex4]
                                    + this.tileHeights[tileIndex][loopIndex3][loopIndex4 + 1]
                                    + this.tileHeights[tileIndex][loopIndex3 + 1][loopIndex4 + 1]
                              )
                              / 4
                           - (
                                 this.tileHeights[tileHeightIndex][newIndex][positionArgument]
                                    + this.tileHeights[tileHeightIndex][newIndex + 1][positionArgument]
                                    + this.tileHeights[tileHeightIndex][newIndex][positionArgument + 1]
                                    + this.tileHeights[tileHeightIndex][newIndex + 1][positionArgument + 1]
                              )
                              / 4;
                        WallObject wallObject = sceneTile.wall;
                        if (sceneTile.wall != null && wallObject.primary != null && wallObject.primary.vertexNormals != null) {
                           this.mergeNormals(
                              model, (Model)wallObject.primary, (loopIndex3 - newIndex << 7) + (1 - scalarArgument << 6), scalar, (loopIndex4 - positionArgument << 7) + (1 - scalarArgument2 << 6), flag
                           );
                        }

                        if (wallObject != null && wallObject.secondary != null && wallObject.secondary.vertexNormals != null) {
                           this.mergeNormals(
                              model, (Model)wallObject.secondary, (loopIndex3 - newIndex << 7) + (1 - scalarArgument << 6), scalar, (loopIndex4 - positionArgument << 7) + (1 - scalarArgument2 << 6), flag
                           );
                        }

                        for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile.interactiveObjectCount; interactiveObjectIndex++) {
                           InteractiveObject interactiveObject;
                           if ((interactiveObject = sceneTile.interactiveObjects[interactiveObjectIndex]) != null && interactiveObject.renderable != null && interactiveObject.renderable.vertexNormals != null) {
                              int localTileRight = interactiveObject.tileRight - interactiveObject.tileLeft + 1;
                              int localTileBottom = interactiveObject.tileBottom - interactiveObject.tileTop + 1;
                              this.mergeNormals(
                                 model,
                                 (Model)interactiveObject.renderable,
                                 (interactiveObject.tileLeft - newIndex << 7) + (localTileRight - scalarArgument << 6),
                                 scalar,
                                 (interactiveObject.tileTop - positionArgument << 7) + (localTileBottom - scalarArgument2 << 6),
                                 flag
                              );
                           }
                        }
                     }
                  }
               }
            }

            position--;
            flag = false;
         }
      }
   }
   private void mergeNormals(Model model, Model newModel, int scalarArgument, int scalarArgument2, int scalarArgument3, boolean flag) {
      this.mergeCycle++;
      int scalar = 0;
      int[] verticesX = newModel.verticesX;
      int vertexCount = newModel.vertexCount;

      for (int vertexIndex = 0; vertexIndex < model.vertexCount; vertexIndex++) {
         VertexNormal vertexNormal = model.vertexNormals[vertexIndex];
         VertexNormal vertexNormal4;
         int localVerticesY;
         int verticesX2;
         int localVerticesZ;
         if ((vertexNormal4 = model.vertexNormalOffset[vertexIndex]).magnitude != 0
            && (localVerticesY = model.verticesY[vertexIndex] - scalarArgument2) <= newModel.maxY
            && (verticesX2 = model.verticesX[vertexIndex] - scalarArgument) >= newModel.minX
            && verticesX2 <= newModel.maxX
            && (localVerticesZ = model.verticesZ[vertexIndex] - scalarArgument3) >= newModel.minZ
            && localVerticesZ <= newModel.maxZ) {
            for (int vertexNormalIndex = 0; vertexNormalIndex < vertexCount; vertexNormalIndex++) {
               VertexNormal vertexNormal2 = newModel.vertexNormals[vertexNormalIndex];
               VertexNormal vertexNormal3 = newModel.vertexNormalOffset[vertexNormalIndex];
               if (verticesX2 == verticesX[vertexNormalIndex] && localVerticesZ == newModel.verticesZ[vertexNormalIndex] && localVerticesY == newModel.verticesY[vertexNormalIndex] && vertexNormal3.magnitude != 0) {
                  vertexNormal.x = vertexNormal.x + vertexNormal3.x;
                  vertexNormal.y = vertexNormal.y + vertexNormal3.y;
                  vertexNormal.z = vertexNormal.z + vertexNormal3.z;
                  vertexNormal.magnitude = vertexNormal.magnitude + vertexNormal3.magnitude;
                  vertexNormal2.x = vertexNormal2.x + vertexNormal4.x;
                  vertexNormal2.y = vertexNormal2.y + vertexNormal4.y;
                  vertexNormal2.z = vertexNormal2.z + vertexNormal4.z;
                  vertexNormal2.magnitude = vertexNormal2.magnitude + vertexNormal4.magnitude;
                  scalar++;
                  this.mergeStampA[vertexIndex] = this.mergeCycle;
                  this.mergeStampB[vertexNormalIndex] = this.mergeCycle;
               }
            }
         }
      }

      if (scalar >= 3 && flag) {
         for (int trianglePointsXIndex = 0; trianglePointsXIndex < model.triangleCount; trianglePointsXIndex++) {
            if (this.mergeStampA[model.trianglePointsX[trianglePointsXIndex]] == this.mergeCycle
               && this.mergeStampA[model.trianglePointsY[trianglePointsXIndex]] == this.mergeCycle
               && this.mergeStampA[model.trianglePointsZ[trianglePointsXIndex]] == this.mergeCycle) {
               model.triangleDrawType[trianglePointsXIndex] = -1;
            }
         }

         for (int triangleIndex = 0; triangleIndex < newModel.triangleCount; triangleIndex++) {
            if (this.mergeStampB[newModel.trianglePointsX[triangleIndex]] == this.mergeCycle
               && this.mergeStampB[newModel.trianglePointsY[triangleIndex]] == this.mergeCycle
               && this.mergeStampB[newModel.trianglePointsZ[triangleIndex]] == this.mergeCycle) {
               newModel.triangleDrawType[triangleIndex] = -1;
            }
         }
      }
   }
   public final void drawMinimapTile(int[] newTileRotationMaps, int pixelsLength, int newIndex, int tileIndex, int tileRotationMapIndex) {
      if (Client.hdMinimap) {
         int sourceTileRotationMapIndex = tileRotationMapIndex;
         tileRotationMapIndex = tileIndex;
         tileIndex = newIndex;
         newIndex = pixelsLength;
         int[] values = newTileRotationMaps;
         SceneGraph sceneGraph = this;
         SceneTile sceneTile;
         if ((sceneTile = this.tiles[tileIndex][tileRotationMapIndex][sourceTileRotationMapIndex]) != null) {
            PlainTile plainTile = sceneTile.plainTile;
            if (sceneTile.plainTile != null) {
               if (plainTile.cornerColor0 != 12345678) {
                  if (plainTile.minimapColor != 0) {
                     tileIndex = plainTile.cornerColor0 & -128;
                     sourceTileRotationMapIndex = plainTile.cornerColor3 & 127;
                     int scalar = plainTile.cornerColor2 & 127;
                     int scalar2 = (plainTile.cornerColor0 & 127) - sourceTileRotationMapIndex;
                     int scalar3 = (plainTile.cornerColor1 & 127) - scalar;
                     sourceTileRotationMapIndex <<= 2;
                     scalar <<= 2;

                     for (int loopIndex = 0; loopIndex < 4; loopIndex++) {
                        if (!plainTile.flat) {
                           values[newIndex] = Rasterizer3D.HSL_TO_RGB[tileIndex | sourceTileRotationMapIndex >> 2];
                           values[newIndex + 1] = Rasterizer3D.HSL_TO_RGB[tileIndex | sourceTileRotationMapIndex * 3 + scalar >> 4];
                           values[newIndex + 2] = Rasterizer3D.HSL_TO_RGB[tileIndex | sourceTileRotationMapIndex + scalar >> 3];
                           values[newIndex + 3] = Rasterizer3D.HSL_TO_RGB[tileIndex | sourceTileRotationMapIndex + scalar * 3 >> 4];
                        } else {
                           int minimapColor = plainTile.minimapColor;
                           int scalar4 = 255 - ((sourceTileRotationMapIndex >> 1) * (sourceTileRotationMapIndex >> 1) >> 8);
                           values[newIndex] = ((minimapColor & 16711935) * scalar4 & -16711936) + ((minimapColor & 0xFF00) * scalar4 & 0xFF0000) >> 8;
                           scalar4 = 255 - ((sourceTileRotationMapIndex * 3 + scalar >> 3) * (sourceTileRotationMapIndex * 3 + scalar >> 3) >> 8);
                           values[newIndex + 1] = ((minimapColor & 16711935) * scalar4 & -16711936) + ((minimapColor & 0xFF00) * scalar4 & 0xFF0000) >> 8;
                           scalar4 = 255 - ((sourceTileRotationMapIndex + scalar >> 2) * (sourceTileRotationMapIndex + scalar >> 2) >> 8);
                           values[newIndex + 2] = ((minimapColor & 16711935) * scalar4 & -16711936) + ((minimapColor & 0xFF00) * scalar4 & 0xFF0000) >> 8;
                           scalar4 = 255 - ((sourceTileRotationMapIndex + scalar * 3 >> 3) * (sourceTileRotationMapIndex + scalar * 3 >> 3) >> 8);
                           values[newIndex + 3] = ((minimapColor & 16711935) * scalar4 & -16711936) + ((minimapColor & 0xFF00) * scalar4 & 0xFF0000) >> 8;
                        }

                        sourceTileRotationMapIndex += scalar2;
                        scalar += scalar3;
                        newIndex += 512;
                     }

                     return;
                  }
               } else {
                  tileIndex = plainTile.minimapColor;
                  if (plainTile.minimapColor != 0) {
                     for (int loopIndex2 = 0; loopIndex2 < 4; loopIndex2++) {
                        values[newIndex] = tileIndex;
                        values[newIndex + 1] = tileIndex;
                        values[newIndex + 2] = tileIndex;
                        values[newIndex + 3] = tileIndex;
                        newIndex += 512;
                     }

                     return;
                  }
               }
            } else {
               ShapedTile shapedTile;
               if ((shapedTile = sceneTile.shapedTile) != null) {
                  sourceTileRotationMapIndex = shapedTile.shape;
                  int rotation = shapedTile.rotation;
                  int underlayRgb = shapedTile.underlayRgb;
                  int overlayRgb = shapedTile.overlayRgb;
                  int[] tileShapeMask = sceneGraph.tileShapeMasks[sourceTileRotationMapIndex];
                  newTileRotationMaps = sceneGraph.tileRotationMaps[rotation];
                  int position = 0;
                  if (shapedTile.overlayCornerColor0 != 12345678) {
                     tileRotationMapIndex = shapedTile.overlayCornerColor0 & -128;
                     sourceTileRotationMapIndex = shapedTile.overlayCornerColor3 & 127;
                     rotation = shapedTile.overlayCornerColor2 & 127;
                     int scalar5 = (shapedTile.overlayCornerColor0 & 127) - sourceTileRotationMapIndex;
                     int scalar6 = (shapedTile.overlayCornerColor1 & 127) - rotation;
                     sourceTileRotationMapIndex <<= 2;
                     rotation <<= 2;

                     for (int loopIndex3 = 0; loopIndex3 < 4; loopIndex3++) {
                        if (!shapedTile.hasTexture) {
                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              values[newIndex] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex >> 2];
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              values[newIndex + 1] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex * 3 + rotation >> 4];
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              values[newIndex + 2] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex + rotation >> 3];
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              values[newIndex + 3] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex + rotation * 3 >> 4];
                           }
                        } else {
                           int sourceOverlayRgb = overlayRgb;
                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              int scalar7 = 255 - ((sourceTileRotationMapIndex >> 1) * (sourceTileRotationMapIndex >> 1) >> 8);
                              values[newIndex] = ((sourceOverlayRgb & 16711935) * scalar7 & -16711936) + ((sourceOverlayRgb & 0xFF00) * scalar7 & 0xFF0000) >> 8;
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              int scalar8 = 255 - ((sourceTileRotationMapIndex * 3 + rotation >> 3) * (sourceTileRotationMapIndex * 3 + rotation >> 3) >> 8);
                              values[newIndex + 1] = ((sourceOverlayRgb & 16711935) * scalar8 & -16711936) + ((sourceOverlayRgb & 0xFF00) * scalar8 & 0xFF0000) >> 8;
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              int scalar9 = 255 - ((sourceTileRotationMapIndex + rotation >> 2) * (sourceTileRotationMapIndex + rotation >> 2) >> 8);
                              values[newIndex + 2] = ((sourceOverlayRgb & 16711935) * scalar9 & -16711936) + ((sourceOverlayRgb & 0xFF00) * scalar9 & 0xFF0000) >> 8;
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                              int scalar10 = 255 - ((sourceTileRotationMapIndex + rotation * 3 >> 3) * (sourceTileRotationMapIndex + rotation * 3 >> 3) >> 8);
                              values[newIndex + 3] = ((sourceOverlayRgb & 16711935) * scalar10 & -16711936) + ((sourceOverlayRgb & 0xFF00) * scalar10 & 0xFF0000) >> 8;
                           }
                        }

                        sourceTileRotationMapIndex += scalar5;
                        rotation += scalar6;
                        newIndex += 512;
                     }

                     if (underlayRgb != 0 && shapedTile.underlayCornerColor0 != 12345678) {
                        newIndex -= 2048;
                        position -= 16;
                        tileRotationMapIndex = shapedTile.underlayCornerColor0 & -128;
                        sourceTileRotationMapIndex = shapedTile.underlayCornerColor3 & 127;
                        rotation = shapedTile.underlayCornerColor2 & 127;
                        scalar5 = (shapedTile.underlayCornerColor0 & 127) - sourceTileRotationMapIndex;
                        scalar6 = (shapedTile.underlayCornerColor1 & 127) - rotation;
                        sourceTileRotationMapIndex <<= 2;
                        rotation <<= 2;

                        for (int loopIndex4 = 0; loopIndex4 < 4; loopIndex4++) {
                           if (tileShapeMask[newTileRotationMaps[position++]] == 0) {
                              values[newIndex] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex >> 2];
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] == 0) {
                              values[newIndex + 1] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex * 3 + rotation >> 4];
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] == 0) {
                              values[newIndex + 2] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex + rotation >> 3];
                           }

                           if (tileShapeMask[newTileRotationMaps[position++]] == 0) {
                              values[newIndex + 3] = Rasterizer3D.HSL_TO_RGB[tileRotationMapIndex | sourceTileRotationMapIndex + rotation * 3 >> 4];
                           }

                           sourceTileRotationMapIndex += scalar5;
                           rotation += scalar6;
                           newIndex += 512;
                        }
                     }

                     return;
                  }

                  if (underlayRgb != 0) {
                     for (int loopIndex5 = 0; loopIndex5 < 4; loopIndex5++) {
                        values[newIndex] = tileShapeMask[newTileRotationMaps[position++]] != 0 ? overlayRgb : underlayRgb;
                        values[newIndex + 1] = tileShapeMask[newTileRotationMaps[position++]] != 0 ? overlayRgb : underlayRgb;
                        values[newIndex + 2] = tileShapeMask[newTileRotationMaps[position++]] != 0 ? overlayRgb : underlayRgb;
                        values[newIndex + 3] = tileShapeMask[newTileRotationMaps[position++]] != 0 ? overlayRgb : underlayRgb;
                        newIndex += 512;
                     }

                     return;
                  }

                  for (int loopIndex6 = 0; loopIndex6 < 4; loopIndex6++) {
                     if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                        values[newIndex] = overlayRgb;
                     }

                     if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                        values[newIndex + 1] = overlayRgb;
                     }

                     if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                        values[newIndex + 2] = overlayRgb;
                     }

                     if (tileShapeMask[newTileRotationMaps[position++]] != 0) {
                        values[newIndex + 3] = overlayRgb;
                     }

                     newIndex += 512;
                  }
               }
            }
         }
      } else {
         SceneTile sceneTile2;
         if ((sceneTile2 = this.tiles[newIndex][tileIndex][tileRotationMapIndex]) != null) {
            PlainTile plainTile2 = sceneTile2.plainTile;
            if (sceneTile2.plainTile == null) {
               ShapedTile shapedTile2;
               if ((shapedTile2 = sceneTile2.shapedTile) != null) {
                  tileIndex = shapedTile2.shape;
                  tileRotationMapIndex = shapedTile2.rotation;
                  int underlayRgb2 = shapedTile2.underlayRgb;
                  newIndex = shapedTile2.overlayRgb;
                  int[] tileShapeMasks2 = this.tileShapeMasks[tileIndex];
                  int[] tileRotationMap = this.tileRotationMaps[tileRotationMapIndex];
                  int position2 = 0;
                  if (underlayRgb2 != 0) {
                     for (int loopIndex7 = 0; loopIndex7 < 4; loopIndex7++) {
                        newTileRotationMaps[pixelsLength] = tileShapeMasks2[tileRotationMap[position2++]] != 0 ? newIndex : underlayRgb2;
                        newTileRotationMaps[pixelsLength + 1] = tileShapeMasks2[tileRotationMap[position2++]] != 0 ? newIndex : underlayRgb2;
                        newTileRotationMaps[pixelsLength + 2] = tileShapeMasks2[tileRotationMap[position2++]] != 0 ? newIndex : underlayRgb2;
                        newTileRotationMaps[pixelsLength + 3] = tileShapeMasks2[tileRotationMap[position2++]] != 0 ? newIndex : underlayRgb2;
                        pixelsLength += 512;
                     }

                     return;
                  }

                  for (int loopIndex8 = 0; loopIndex8 < 4; loopIndex8++) {
                     if (tileShapeMasks2[tileRotationMap[position2++]] != 0) {
                        newTileRotationMaps[pixelsLength] = newIndex;
                     }

                     if (tileShapeMasks2[tileRotationMap[position2++]] != 0) {
                        newTileRotationMaps[pixelsLength + 1] = newIndex;
                     }

                     if (tileShapeMasks2[tileRotationMap[position2++]] != 0) {
                        newTileRotationMaps[pixelsLength + 2] = newIndex;
                     }

                     if (tileShapeMasks2[tileRotationMap[position2++]] != 0) {
                        newTileRotationMaps[pixelsLength + 3] = newIndex;
                     }

                     pixelsLength += 512;
                  }

                  return;
               }
            } else {
               newIndex = plainTile2.minimapColor;
               if (plainTile2.minimapColor != 0) {
                  for (int loopIndex9 = 0; loopIndex9 < 4; loopIndex9++) {
                     newTileRotationMaps[pixelsLength] = newIndex;
                     newTileRotationMaps[pixelsLength + 1] = newIndex;
                     newTileRotationMaps[pixelsLength + 2] = newIndex;
                     newTileRotationMaps[pixelsLength + 3] = newIndex;
                     pixelsLength += 512;
                  }
               }
            }
         }
      }
   }
   public static void precalculateTileVisibility(int scalarArgument, int scalarArgument2, int newViewportMaxX, int newViewportMaxY, int[] values) {
      viewportMinX = 0;
      viewportMinY = 0;
      viewportMaxX = newViewportMaxX;
      viewportMaxY = newViewportMaxY;
      viewportCenterX = newViewportMaxX / 2;
      viewportCenterY = newViewportMaxY / 2;
      boolean[][][][] flag = new boolean[9][32][53][53];

      for (short sINEIndex2 = 128; sINEIndex2 <= 384; sINEIndex2 += 32) {
         for (int sINEIndex = 0; sINEIndex < 2048; sINEIndex += 64) {
            pitchSin = Model.SINE[sINEIndex2];
            pitchCos = Model.COSINE[sINEIndex2];
            yawSin = Model.SINE[sINEIndex];
            yawCos = Model.COSINE[sINEIndex];
            newViewportMaxY = (sINEIndex2 - 128) / 32;
            int scalar = sINEIndex / 64;

            for (int loopIndex = -26; loopIndex <= 26; loopIndex++) {
               for (int loopIndex2 = -26; loopIndex2 <= 26; loopIndex2++) {
                  int scalar2 = loopIndex << 7;
                  int scalar3 = loopIndex2 << 7;
                  boolean localFlag = false;

                  for (short loopIndex3 = -500; loopIndex3 <= 800; loopIndex3 += 128) {
                     int scalar4 = values[newViewportMaxY] + loopIndex3;
                     int scalar5 = scalar2;
                     int scalar6 = scalar3;
                     int scalar7 = scalar4;
                     int scalar8 = scalar6 * yawSin + scalar5 * yawCos >> 16;
                     scalar6 = scalar6 * yawCos - scalar5 * yawSin >> 16;
                     scalar5 = scalar7 * pitchSin + scalar6 * pitchCos >> 16;
                     scalar7 = scalar7 * pitchCos - scalar6 * pitchSin >> 16;
                     boolean flag2;
                     if (scalar5 >= 50 && scalar5 <= 3500) {
                        scalar6 = viewportCenterX + (scalar8 << 9) / scalar5;
                        scalar7 = viewportCenterY + (scalar7 << 9) / scalar5;
                        flag2 = scalar6 >= 0 && scalar6 <= viewportMaxX && scalar7 >= 0 && scalar7 <= viewportMaxY;
                     } else {
                        flag2 = false;
                     }

                     if (flag2) {
                        localFlag = true;
                        break;
                     }
                  }

                  flag[newViewportMaxY][scalar][loopIndex + 25 + 1][loopIndex2 + 25 + 1] = localFlag;
               }
            }
         }
      }

      for (int flagIndex = 0; flagIndex < 8; flagIndex++) {
         for (int loopIndex4 = 0; loopIndex4 < 32; loopIndex4++) {
            for (int loopIndex5 = -25; loopIndex5 < 25; loopIndex5++) {
               for (int loopIndex6 = -25; loopIndex6 < 25; loopIndex6++) {
                  boolean flag3 = false;

                  scanVisibilityNeighbors:
                  for (int loopIndex7 = -1; loopIndex7 <= 1; loopIndex7++) {
                     for (int loopIndex8 = -1; loopIndex8 <= 1; loopIndex8++) {
                        if (flag[flagIndex][loopIndex4][loopIndex5 + loopIndex7 + 25 + 1][loopIndex6 + loopIndex8 + 25 + 1]) {
                           flag3 = true;
                           break scanVisibilityNeighbors;
                        }

                        if (flag[flagIndex][(loopIndex4 + 1) & 31][loopIndex5 + loopIndex7 + 25 + 1][loopIndex6 + loopIndex8 + 25 + 1]) {
                           flag3 = true;
                           break scanVisibilityNeighbors;
                        }

                        if (flag[flagIndex + 1][loopIndex4][loopIndex5 + loopIndex7 + 25 + 1][loopIndex6 + loopIndex8 + 25 + 1]) {
                           flag3 = true;
                           break scanVisibilityNeighbors;
                        }

                        if (flag[flagIndex + 1][(loopIndex4 + 1) & 31][loopIndex5 + loopIndex7 + 25 + 1][loopIndex6 + loopIndex8 + 25 + 1]) {
                           flag3 = true;
                           break scanVisibilityNeighbors;
                        }
                     }
                  }

                  visibilityMaps[flagIndex][loopIndex4][loopIndex5 + 25][loopIndex6 + 25] = flag3;
               }
            }
         }
      }
   }
   public static void click(int newClickY, int newClickX) {
      checkClick = true;
      clickX = newClickX;
      clickY = newClickY;
      clickedTileX = -1;
      clickedTileY = -1;
   }
   public final void renderScene(int cameraXOrOccluderCounts, int newCameraY, int sINEIndex2, int newIndex, int sourceRenderPlane, int sINEIndex) {
      if (cameraXOrOccluderCounts < 0) {
         cameraXOrOccluderCounts = 0;
      } else if (cameraXOrOccluderCounts >= 13312) {
         cameraXOrOccluderCounts = 13311;
      }

      if (newCameraY < 0) {
         newCameraY = 0;
      } else if (newCameraY >= 13312) {
         newCameraY = 13311;
      }

      renderCycle++;
      pitchSin = Model.SINE[sINEIndex];
      pitchCos = Model.COSINE[sINEIndex];
      yawSin = Model.SINE[sINEIndex2];
      yawCos = Model.COSINE[sINEIndex2];
      visibilityMap = visibilityMaps[(sINEIndex - 128) / 32][sINEIndex2 / 64];
      cameraX = cameraXOrOccluderCounts;
      cameraZ = newIndex;
      cameraY = newCameraY;
      cameraTileX = cameraXOrOccluderCounts / 128;
      cameraTileY = newCameraY / 128;
      renderPlane = sourceRenderPlane;
      if ((minTileX = cameraTileX - 25) < 0) {
         minTileX = 0;
      }

      if ((minTileY = cameraTileY - 25) < 0) {
         minTileY = 0;
      }

      if ((maxTileX = cameraTileX + 25) > 104) {
         maxTileX = 104;
      }

      if ((maxTileY = cameraTileY + 25) > 104) {
         maxTileY = 104;
      }

      cameraXOrOccluderCounts = occluderCounts[renderPlane];
      Occluder[] occluder2 = occluders[renderPlane];
      activeOccluderCount = 0;

      for (int loopIndex = 0; loopIndex < cameraXOrOccluderCounts; loopIndex++) {
         Occluder occluder;
         if ((occluder = occluder2[loopIndex]).type == 1) {
            int visibilityMapIndex;
            if ((visibilityMapIndex = occluder.minTileX - cameraTileX + 25) >= 0 && visibilityMapIndex <= 50) {
               int localMinTileY;
               if ((localMinTileY = occluder.minTileY - cameraTileY + 25) < 0) {
                  localMinTileY = 0;
               }

               int localMaxTileY;
               if ((localMaxTileY = occluder.maxTileY - cameraTileY + 25) > 50) {
                  localMaxTileY = 50;
               }

               int localCameraX = 0;

               while (localMinTileY <= localMaxTileY) {
                  if (visibilityMap[visibilityMapIndex][localMinTileY++]) {
                     localCameraX = 1;
                     break;
                  }
               }

               if (localCameraX != 0) {
                  if ((localCameraX = cameraX - occluder.minX) > 32) {
                     occluder.mode = 1;
                  } else {
                     if (localCameraX >= -32) {
                        continue;
                     }

                     occluder.mode = 2;
                     localCameraX = -localCameraX;
                  }

                  occluder.minZDelta = (occluder.minZ - cameraY << 8) / localCameraX;
                  occluder.maxZDelta = (occluder.maxZ - cameraY << 8) / localCameraX;
                  occluder.minYDelta = (occluder.minY - cameraZ << 8) / localCameraX;
                  occluder.maxYDelta = (occluder.maxY - cameraZ << 8) / localCameraX;
                  activeOccluders[activeOccluderCount++] = occluder;
               }
            }
         } else if (occluder.type == 2) {
            int minTileY2;
            if ((minTileY2 = occluder.minTileY - cameraTileY + 25) >= 0 && minTileY2 <= 50) {
               int minTileX2;
               if ((minTileX2 = occluder.minTileX - cameraTileX + 25) < 0) {
                  minTileX2 = 0;
               }

               int localMaxTileX;
               if ((localMaxTileX = occluder.maxTileX - cameraTileX + 25) > 50) {
                  localMaxTileX = 50;
               }

               int localCameraY = 0;

               while (minTileX2 <= localMaxTileX) {
                  if (visibilityMap[minTileX2++][minTileY2]) {
                     localCameraY = 1;
                     break;
                  }
               }

               if (localCameraY != 0) {
                  if ((localCameraY = cameraY - occluder.minZ) > 32) {
                     occluder.mode = 3;
                  } else {
                     if (localCameraY >= -32) {
                        continue;
                     }

                     occluder.mode = 4;
                     localCameraY = -localCameraY;
                  }

                  occluder.minXDelta = (occluder.minX - cameraX << 8) / localCameraY;
                  occluder.maxXDelta = (occluder.maxX - cameraX << 8) / localCameraY;
                  occluder.minYDelta = (occluder.minY - cameraZ << 8) / localCameraY;
                  occluder.maxYDelta = (occluder.maxY - cameraZ << 8) / localCameraY;
                  activeOccluders[activeOccluderCount++] = occluder;
               }
            }
         } else {
            int localMinY;
            if (occluder.type == 4 && (localMinY = occluder.minY - cameraZ) > 128) {
               int position;
               if ((position = occluder.minTileY - cameraTileY + 25) < 0) {
                  position = 0;
               }

               int maxTileY2;
               if ((maxTileY2 = occluder.maxTileY - cameraTileY + 25) > 50) {
                  maxTileY2 = 50;
               }

               if (position <= maxTileY2) {
                  int position2;
                  if ((position2 = occluder.minTileX - cameraTileX + 25) < 0) {
                     position2 = 0;
                  }

                  int maxTileX2;
                  if ((maxTileX2 = occluder.maxTileX - cameraTileX + 25) > 50) {
                     maxTileX2 = 50;
                  }

                  boolean flag = false;

                  scanOccluderVisibility:
                  for (int visibilityMapIndex2 = position2; visibilityMapIndex2 <= maxTileX2; visibilityMapIndex2++) {
                     for (int loopIndex2 = position; loopIndex2 <= maxTileY2; loopIndex2++) {
                        if (visibilityMap[visibilityMapIndex2][loopIndex2]) {
                           flag = true;
                           break scanOccluderVisibility;
                        }
                     }
                  }

                  if (flag) {
                     occluder.mode = 5;
                     occluder.minXDelta = (occluder.minX - cameraX << 8) / localMinY;
                     occluder.maxXDelta = (occluder.maxX - cameraX << 8) / localMinY;
                     occluder.minZDelta = (occluder.minZ - cameraY << 8) / localMinY;
                     occluder.maxZDelta = (occluder.maxZ - cameraY << 8) / localMinY;
                     activeOccluders[activeOccluderCount++] = occluder;
                  }
               }
            }
         }
      }

      visibleTileCount = 0;

      for (int basePlane = this.basePlane; basePlane < 4; basePlane++) {
         SceneTile[][] tile = this.tiles[basePlane];

         for (int sourceMinTileX = minTileX; sourceMinTileX < maxTileX; sourceMinTileX++) {
            for (int sourceMinTileY = minTileY; sourceMinTileY < maxTileY; sourceMinTileY++) {
               SceneTile sceneTile;
               if ((sceneTile = tile[sourceMinTileX][sourceMinTileY]) != null) {
                  if (sceneTile.drawLevel <= sourceRenderPlane
                     && (visibilityMap[sourceMinTileX - cameraTileX + 25][sourceMinTileY - cameraTileY + 25] || this.tileHeights[basePlane][sourceMinTileX][sourceMinTileY] - newIndex >= 2000)) {
                     sceneTile.draw = true;
                     sceneTile.visible = true;
                     sceneTile.drawEntities = sceneTile.interactiveObjectCount > 0;
                     visibleTileCount++;
                  } else {
                     sceneTile.draw = false;
                     sceneTile.visible = false;
                     sceneTile.wallCullMask = 0;
                  }
               }
            }
         }
      }

      for (int basePlane2 = this.basePlane; basePlane2 < 4; basePlane2++) {
         SceneTile[][] tiles2 = this.tiles[basePlane2];

         for (int loopIndex3 = -25; loopIndex3 <= 0; loopIndex3++) {
            sINEIndex = cameraTileX + loopIndex3;
            int localCameraTileX = cameraTileX - loopIndex3;
            if (sINEIndex >= minTileX || localCameraTileX < maxTileX) {
               for (int loopIndex4 = -25; loopIndex4 <= 0; loopIndex4++) {
                  newIndex = cameraTileY + loopIndex4;
                  sourceRenderPlane = cameraTileY - loopIndex4;
                  if (sINEIndex >= minTileX) {
                     SceneTile sceneTile5;
                     if (newIndex >= minTileY && (sceneTile5 = tiles2[sINEIndex][newIndex]) != null && sceneTile5.draw) {
                        this.renderTile(sceneTile5, true);
                     }

                     if (sourceRenderPlane < maxTileY && (sceneTile5 = tiles2[sINEIndex][sourceRenderPlane]) != null && sceneTile5.draw) {
                        this.renderTile(sceneTile5, true);
                     }
                  }

                  if (localCameraTileX < maxTileX) {
                     SceneTile sceneTile2;
                     if (newIndex >= minTileY && (sceneTile2 = tiles2[localCameraTileX][newIndex]) != null && sceneTile2.draw) {
                        this.renderTile(sceneTile2, true);
                     }

                     if (sourceRenderPlane < maxTileY && (sceneTile2 = tiles2[localCameraTileX][sourceRenderPlane]) != null && sceneTile2.draw) {
                        this.renderTile(sceneTile2, true);
                     }
                  }

                  if (visibleTileCount == 0) {
                     checkClick = false;
                     return;
                  }
               }
            }
         }
      }

      for (int basePlane3 = this.basePlane; basePlane3 < 4; basePlane3++) {
         SceneTile[][] tiles3 = this.tiles[basePlane3];

         for (int loopIndex5 = -25; loopIndex5 <= 0; loopIndex5++) {
            sINEIndex = cameraTileX + loopIndex5;
            int cameraTileX2 = cameraTileX - loopIndex5;
            if (sINEIndex >= minTileX || cameraTileX2 < maxTileX) {
               for (int loopIndex6 = -25; loopIndex6 <= 0; loopIndex6++) {
                  newIndex = cameraTileY + loopIndex6;
                  sourceRenderPlane = cameraTileY - loopIndex6;
                  if (sINEIndex >= minTileX) {
                     SceneTile sceneTile3;
                     if (newIndex >= minTileY && (sceneTile3 = tiles3[sINEIndex][newIndex]) != null && sceneTile3.draw) {
                        this.renderTile(sceneTile3, false);
                     }

                     if (sourceRenderPlane < maxTileY && (sceneTile3 = tiles3[sINEIndex][sourceRenderPlane]) != null && sceneTile3.draw) {
                        this.renderTile(sceneTile3, false);
                     }
                  }

                  if (cameraTileX2 < maxTileX) {
                     SceneTile sceneTile4;
                     if (newIndex >= minTileY && (sceneTile4 = tiles3[cameraTileX2][newIndex]) != null && sceneTile4.draw) {
                        this.renderTile(sceneTile4, false);
                     }

                     if (sourceRenderPlane < maxTileY && (sceneTile4 = tiles3[cameraTileX2][sourceRenderPlane]) != null && sceneTile4.draw) {
                        this.renderTile(sceneTile4, false);
                     }
                  }

                  if (visibleTileCount == 0) {
                     checkClick = false;
                     return;
                  }
               }
            }
         }
      }

      checkClick = false;
   }
   private void renderTile(SceneTile sceneTile, boolean flag) {
      tileQueue.addLast(sceneTile);

      while (true) {
         int tileX;
         int tileY;
         int tileIndex;
         int sourceTileHeightIndex;
         SceneTile[][] sceneTile8;
         while (true) {
            if ((sceneTile = (SceneTile)tileQueue.removeFirst()) == null) {
               return;
            }

            if (sceneTile.visible) {
               tileX = sceneTile.x;
               tileY = sceneTile.y;
               tileIndex = sceneTile.plane;
               sourceTileHeightIndex = sceneTile.originalPlane;
               sceneTile8 = this.tiles[tileIndex];
               if (!sceneTile.draw) {
                  break;
               }

               if (flag) {
                  SceneTile sceneTile9;
                  if (tileIndex > 0 && (sceneTile9 = this.tiles[tileIndex - 1][tileX][tileY]) != null && sceneTile9.visible
                     || tileX <= cameraTileX
                        && tileX > minTileX
                        && (sceneTile9 = sceneTile8[tileX - 1][tileY]) != null
                        && sceneTile9.visible
                        && (sceneTile9.draw || (sceneTile.interactiveObjectFlagsOr & 1) == 0)
                     || tileX >= cameraTileX
                        && tileX < maxTileX - 1
                        && (sceneTile9 = sceneTile8[tileX + 1][tileY]) != null
                        && sceneTile9.visible
                        && (sceneTile9.draw || (sceneTile.interactiveObjectFlagsOr & 4) == 0)
                     || tileY <= cameraTileY
                        && tileY > minTileY
                        && (sceneTile9 = sceneTile8[tileX][tileY - 1]) != null
                        && sceneTile9.visible
                        && (sceneTile9.draw || (sceneTile.interactiveObjectFlagsOr & 8) == 0)
                     || tileY >= cameraTileY
                        && tileY < maxTileY - 1
                        && (sceneTile9 = sceneTile8[tileX][tileY + 1]) != null
                        && sceneTile9.visible
                        && (sceneTile9.draw || (sceneTile.interactiveObjectFlagsOr & 2) == 0)) {
                     continue;
                  }
               } else {
                  flag = true;
               }

               sceneTile.draw = false;
               if (sceneTile.linkedBelowTile != null) {
                  SceneTile sceneTile2 = sceneTile.linkedBelowTile;
                  if (sceneTile.linkedBelowTile.plainTile != null) {
                     if (!this.isTileOccluded(0, tileX, tileY)) {
                        this.renderPlainTile(sceneTile2.plainTile, 0, pitchSin, pitchCos, yawSin, yawCos, tileX, tileY);
                     }
                  } else if (sceneTile2.shapedTile != null && !this.isTileOccluded(0, tileX, tileY)) {
                     this.renderShapedTile(tileX, pitchSin, yawSin, sceneTile2.shapedTile, pitchCos, tileY, yawCos);
                  }

                  WallObject wallObject = sceneTile2.wall;
                  if (sceneTile2.wall != null) {
                     wallObject.primary
                        .renderAtPoint(
                           0,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           wallObject.x - cameraX,
                           wallObject.z - cameraZ,
                           wallObject.y - cameraY,
                           wallObject.hash
                        );
                  }

                  for (int interactiveObjectIndex = 0; interactiveObjectIndex < sceneTile2.interactiveObjectCount; interactiveObjectIndex++) {
                     InteractiveObject interactiveObject;
                     if ((interactiveObject = sceneTile2.interactiveObjects[interactiveObjectIndex]) != null) {
                        interactiveObject.renderable
                           .renderAtPoint(
                              interactiveObject.rotation,
                              pitchSin,
                              pitchCos,
                              yawSin,
                              yawCos,
                              interactiveObject.worldX - cameraX,
                              interactiveObject.worldZ - cameraZ,
                              interactiveObject.worldY - cameraY,
                              interactiveObject.hash
                           );
                     }
                  }
               }

               boolean localFlag = false;
               // The legacy occluder clusters are reliable for the ground plane but
               // can falsely reject upper-floor geometry as the camera rotates around
               // multi-level buildings. Keep the normal visibility-map, roof/draw
               // level and wall-facing checks, but only use cluster occlusion for
               // original plane 0. This also covers bridge-shifted upper-plane tiles
               // because sourceTileHeightIndex is the tile's original plane.
               boolean useLegacyOcclusion = sourceTileHeightIndex == 0;
               boolean floorOccluded = useLegacyOcclusion && this.isTileOccluded(sourceTileHeightIndex, tileX, tileY);
               if (sceneTile.plainTile != null) {
                  if (!floorOccluded) {
                     localFlag = true;
                     this.renderPlainTile(sceneTile.plainTile, sourceTileHeightIndex, pitchSin, pitchCos, yawSin, yawCos, tileX, tileY);
                  }
               } else if (sceneTile.shapedTile != null && !floorOccluded) {
                  localFlag = true;
                  this.renderShapedTile(tileX, pitchSin, yawSin, sceneTile.shapedTile, pitchCos, tileY, yawCos);
               }

               int wallVisibilityFlagIndex = 0;
               int localWallVisibilityFlags = 0;
               WallObject wall = sceneTile.wall;
               GroundDecoration groundDecoration = sceneTile.wallDecoration;
               if (wall != null || groundDecoration != null) {
                  if (cameraTileX == tileX) {
                     wallVisibilityFlagIndex++;
                  } else if (cameraTileX < tileX) {
                     wallVisibilityFlagIndex += 2;
                  }

                  if (cameraTileY == tileY) {
                     wallVisibilityFlagIndex += 3;
                  } else if (cameraTileY > tileY) {
                     wallVisibilityFlagIndex += 6;
                  }

                  localWallVisibilityFlags = wallVisibilityFlags[wallVisibilityFlagIndex];
                  sceneTile.delayedWallMask = wallDeferredFlags[wallVisibilityFlagIndex];
               }

               if (wall != null) {
                  if ((wall.orientation & wallCullFlags[wallVisibilityFlagIndex]) != 0) {
                     if (wall.orientation == 16) {
                        sceneTile.wallCullMask = 3;
                        sceneTile.wallCullDirection = wallCullMap16[wallVisibilityFlagIndex];
                        sceneTile.wallCullComplement = 3 - sceneTile.wallCullDirection;
                     } else if (wall.orientation == 32) {
                        sceneTile.wallCullMask = 6;
                        sceneTile.wallCullDirection = wallCullMap32[wallVisibilityFlagIndex];
                        sceneTile.wallCullComplement = 6 - sceneTile.wallCullDirection;
                     } else if (wall.orientation == 64) {
                        sceneTile.wallCullMask = 12;
                        sceneTile.wallCullDirection = wallCullMap64[wallVisibilityFlagIndex];
                        sceneTile.wallCullComplement = 12 - sceneTile.wallCullDirection;
                     } else {
                        sceneTile.wallCullMask = 9;
                        sceneTile.wallCullDirection = wallCullMap128[wallVisibilityFlagIndex];
                        sceneTile.wallCullComplement = 9 - sceneTile.wallCullDirection;
                     }
                  } else {
                     sceneTile.wallCullMask = 0;
                  }

                  if ((wall.orientation & localWallVisibilityFlags) != 0 && (!useLegacyOcclusion || !this.isWallOccluded(sourceTileHeightIndex, tileX, tileY, wall.orientation))) {
                     wall.primary
                        .renderAtPoint(
                           0,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           wall.x - cameraX,
                           wall.z - cameraZ,
                           wall.y - cameraY,
                           wall.hash
                        );
                  }

                  if ((wall.orientationB & localWallVisibilityFlags) != 0 && (!useLegacyOcclusion || !this.isWallOccluded(sourceTileHeightIndex, tileX, tileY, wall.orientationB))) {
                     wall.secondary
                        .renderAtPoint(
                           0,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           wall.x - cameraX,
                           wall.z - cameraZ,
                           wall.y - cameraY,
                           wall.hash
                        );
                  }
               }

               if (groundDecoration != null && (!useLegacyOcclusion || !this.isOccluded(sourceTileHeightIndex, tileX, tileY, groundDecoration.renderable.modelHeight))) {
                  if ((groundDecoration.configBits & localWallVisibilityFlags) != 0) {
                     groundDecoration.renderable
                        .renderAtPoint(
                           groundDecoration.face,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           groundDecoration.x - cameraX,
                           groundDecoration.z - cameraZ,
                           groundDecoration.y - cameraY,
                           groundDecoration.hash
                        );
                  } else if ((groundDecoration.configBits & 768) != 0) {
                     int localX = groundDecoration.x - cameraX;
                     int localZ = groundDecoration.z - cameraZ;
                     int localY = groundDecoration.y - cameraY;
                     int face = groundDecoration.face;
                     int sourceLocalX;
                     if (groundDecoration.face != 1 && face != 2) {
                        sourceLocalX = localX;
                     } else {
                        sourceLocalX = -localX;
                     }

                     int sourceLocalY;
                     if (face != 2 && face != 3) {
                        sourceLocalY = localY;
                     } else {
                        sourceLocalY = -localY;
                     }

                     if ((groundDecoration.configBits & 256) != 0 && sourceLocalY < sourceLocalX) {
                        localWallVisibilityFlags = localX + decorXOffsetsA[face];
                        int scalar = localY + decorYOffsetsA[face];
                        groundDecoration.renderable.renderAtPoint((face << 9) + 256, pitchSin, pitchCos, yawSin, yawCos, localWallVisibilityFlags, localZ, scalar, groundDecoration.hash);
                     }

                     if ((groundDecoration.configBits & 512) != 0 && sourceLocalY > sourceLocalX) {
                        localWallVisibilityFlags = localX + decorXOffsetsB[face];
                        int scalar2 = localY + decorYOffsetsB[face];
                        groundDecoration.renderable
                           .renderAtPoint((face << 9) + 1280 & 2047, pitchSin, pitchCos, yawSin, yawCos, localWallVisibilityFlags, localZ, scalar2, groundDecoration.hash);
                     }
                  }
               }

               if (localFlag) {
                  WallDecoration floorDecoration = sceneTile.floorDecoration;
                  if (sceneTile.floorDecoration != null) {
                     floorDecoration.renderable
                        .renderAtPoint(
                           0,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           floorDecoration.x - cameraX,
                           floorDecoration.z - cameraZ,
                           floorDecoration.y - cameraY,
                           floorDecoration.hash
                        );
                  }

                  GroundItemPile groundItemPile = sceneTile.groundItemPile;
                  if (sceneTile.groundItemPile != null && groundItemPile.heightOffset == 0) {
                     if (groundItemPile.secondGroundItem != null) {
                        groundItemPile.secondGroundItem
                           .renderAtPoint(
                              0,
                              pitchSin,
                              pitchCos,
                              yawSin,
                              yawCos,
                              groundItemPile.x - cameraX,
                              groundItemPile.z - cameraZ,
                              groundItemPile.y - cameraY,
                              groundItemPile.hash
                           );
                     }

                     if (groundItemPile.thirdGroundItem != null) {
                        groundItemPile.thirdGroundItem
                           .renderAtPoint(
                              0,
                              pitchSin,
                              pitchCos,
                              yawSin,
                              yawCos,
                              groundItemPile.x - cameraX,
                              groundItemPile.z - cameraZ,
                              groundItemPile.y - cameraY,
                              groundItemPile.hash
                           );
                     }

                     if (groundItemPile.firstGroundItem != null) {
                        groundItemPile.firstGroundItem
                           .renderAtPoint(
                              0,
                              pitchSin,
                              pitchCos,
                              yawSin,
                              yawCos,
                              groundItemPile.x - cameraX,
                              groundItemPile.z - cameraZ,
                              groundItemPile.y - cameraY,
                              groundItemPile.hash
                           );
                     }
                  }
               }

               int interactiveObjectFlagsOr = sceneTile.interactiveObjectFlagsOr;
               if (sceneTile.interactiveObjectFlagsOr != 0) {
                  SceneTile sceneTile3;
                  if (tileX < cameraTileX && (interactiveObjectFlagsOr & 4) != 0 && (sceneTile3 = sceneTile8[tileX + 1][tileY]) != null && sceneTile3.visible) {
                     tileQueue.addLast(sceneTile3);
                  }

                  if (tileY < cameraTileY && (interactiveObjectFlagsOr & 2) != 0 && (sceneTile3 = sceneTile8[tileX][tileY + 1]) != null && sceneTile3.visible) {
                     tileQueue.addLast(sceneTile3);
                  }

                  if (tileX > cameraTileX && (interactiveObjectFlagsOr & 1) != 0 && (sceneTile3 = sceneTile8[tileX - 1][tileY]) != null && sceneTile3.visible) {
                     tileQueue.addLast(sceneTile3);
                  }

                  if (tileY > cameraTileY && (interactiveObjectFlagsOr & 8) != 0 && (sceneTile3 = sceneTile8[tileX][tileY - 1]) != null && sceneTile3.visible) {
                     tileQueue.addLast(sceneTile3);
                  }
               }
               break;
            }
         }

         if (sceneTile.wallCullMask != 0) {
            boolean flag2 = true;

            for (int interactiveObjectIndex2 = 0; interactiveObjectIndex2 < sceneTile.interactiveObjectCount; interactiveObjectIndex2++) {
               if (sceneTile.interactiveObjects[interactiveObjectIndex2].cycle != renderCycle && (sceneTile.interactiveObjectFlags[interactiveObjectIndex2] & sceneTile.wallCullMask) == sceneTile.wallCullDirection) {
                  flag2 = false;
                  break;
               }
            }

            if (flag2) {
               WallObject wallObject2 = sceneTile.wall;
               if (!useLegacyOcclusion || !this.isWallOccluded(sourceTileHeightIndex, tileX, tileY, wallObject2.orientation)) {
                  wallObject2.primary
                     .renderAtPoint(
                        0,
                        pitchSin,
                        pitchCos,
                        yawSin,
                        yawCos,
                        wallObject2.x - cameraX,
                        wallObject2.z - cameraZ,
                        wallObject2.y - cameraY,
                        wallObject2.hash
                     );
               }

               sceneTile.wallCullMask = 0;
            }
         }

         if (sceneTile.drawEntities) {
            try {
               int interactiveObjectCount = sceneTile.interactiveObjectCount;
               sceneTile.drawEntities = false;
               int loopIndex = 0;

               interactiveObjectLoop:
               for (int interactiveObjectIndex3 = 0; interactiveObjectIndex3 < interactiveObjectCount; interactiveObjectIndex3++) {
                  InteractiveObject interactiveObject4;
                  if ((interactiveObject4 = sceneTile.interactiveObjects[interactiveObjectIndex3]).cycle != renderCycle) {
                     for (int tileLeft = interactiveObject4.tileLeft; tileLeft <= interactiveObject4.tileRight; tileLeft++) {
                        for (int tileTop = interactiveObject4.tileTop; tileTop <= interactiveObject4.tileBottom; tileTop++) {
                           SceneTile sceneTile4;
                           if ((sceneTile4 = sceneTile8[tileLeft][tileTop]).draw) {
                              sceneTile.drawEntities = true;
                              continue interactiveObjectLoop;
                           }

                           if (sceneTile4.wallCullMask != 0) {
                              int scalar3 = 0;
                              if (tileLeft > interactiveObject4.tileLeft) {
                                 scalar3++;
                              }

                              if (tileLeft < interactiveObject4.tileRight) {
                                 scalar3 += 4;
                              }

                              if (tileTop > interactiveObject4.tileTop) {
                                 scalar3 += 8;
                              }

                              if (tileTop < interactiveObject4.tileBottom) {
                                 scalar3 += 2;
                              }

                              if ((scalar3 & sceneTile4.wallCullMask) == sceneTile.wallCullComplement) {
                                 sceneTile.drawEntities = true;
                                 continue interactiveObjectLoop;
                              }
                           }
                        }
                     }

                     entityBuffer[loopIndex++] = interactiveObject4;
                     int renderPriorityOrCameraTileX = cameraTileX - interactiveObject4.tileLeft;
                     int localTileRight;
                     if ((localTileRight = interactiveObject4.tileRight - cameraTileX) > renderPriorityOrCameraTileX) {
                        renderPriorityOrCameraTileX = localTileRight;
                     }

                     int localCameraTileY = cameraTileY - interactiveObject4.tileTop;
                     int localTileBottom;
                     if ((localTileBottom = interactiveObject4.tileBottom - cameraTileY) > localCameraTileY) {
                        interactiveObject4.renderPriority = renderPriorityOrCameraTileX + localTileBottom;
                     } else {
                        interactiveObject4.renderPriority = renderPriorityOrCameraTileX + localCameraTileY;
                     }
                  }
               }

               while (loopIndex > 0) {
                  int tileHeightIndex = -50;
                  interactiveObjectCount = -1;

                  for (int entityBufferIndex = 0; entityBufferIndex < loopIndex; entityBufferIndex++) {
                     InteractiveObject interactiveObject2;
                     if ((interactiveObject2 = entityBuffer[entityBufferIndex]).cycle != renderCycle) {
                        if (interactiveObject2.renderPriority > tileHeightIndex) {
                           tileHeightIndex = interactiveObject2.renderPriority;
                           interactiveObjectCount = entityBufferIndex;
                        } else if (interactiveObject2.renderPriority == tileHeightIndex) {
                           int localWorldX = interactiveObject2.worldX - cameraX;
                           int localWorldY = interactiveObject2.worldY - cameraY;
                           int localEntityBuffer = entityBuffer[interactiveObjectCount].worldX - cameraX;
                           int entityBuffer2 = entityBuffer[interactiveObjectCount].worldY - cameraY;
                           if (localWorldX * localWorldX + localWorldY * localWorldY > localEntityBuffer * localEntityBuffer + entityBuffer2 * entityBuffer2) {
                              interactiveObjectCount = entityBufferIndex;
                           }
                        }
                     }
                  }

                  if (interactiveObjectCount == -1) {
                     break;
                  }

                  InteractiveObject interactiveObject3;
                  boolean objectOccluded;
                  testObjectOcclusion: {
                     (interactiveObject3 = entityBuffer[interactiveObjectCount]).cycle = renderCycle;
                     int modelHeight = interactiveObject3.renderable.modelHeight;
                     int tileBottom2 = interactiveObject3.tileBottom;
                     int tileTop2 = interactiveObject3.tileTop;
                     int tileRight2 = interactiveObject3.tileRight;
                     int tileLeft2 = interactiveObject3.tileLeft;
                     tileHeightIndex = sourceTileHeightIndex;
                     SceneGraph sceneGraph = this;
                     if (!useLegacyOcclusion) {
                        objectOccluded = false;
                        break testObjectOcclusion;
                     }

                     if (tileLeft2 == tileRight2 && tileTop2 == tileBottom2) {
                        if (sceneGraph.isTileOccluded(tileHeightIndex, tileLeft2, tileTop2)) {
                           interactiveObjectCount = tileLeft2 << 7;
                           int scalar4 = tileTop2 << 7;
                           objectOccluded = isPointOccluded(interactiveObjectCount + 1, sceneGraph.tileHeights[tileHeightIndex][tileLeft2][tileTop2] - modelHeight, scalar4 + 1)
                              && isPointOccluded(interactiveObjectCount + 128 - 1, sceneGraph.tileHeights[tileHeightIndex][tileLeft2 + 1][tileTop2] - modelHeight, scalar4 + 1)
                              && isPointOccluded(interactiveObjectCount + 128 - 1, sceneGraph.tileHeights[tileHeightIndex][tileLeft2 + 1][tileTop2 + 1] - modelHeight, scalar4 + 128 - 1)
                              && isPointOccluded(interactiveObjectCount + 1, sceneGraph.tileHeights[tileHeightIndex][tileLeft2][tileTop2 + 1] - modelHeight, scalar4 + 128 - 1);
                           break testObjectOcclusion;
                        }
                     } else {
                        interactiveObjectCount = tileLeft2;

                        scanObjectOcclusionTiles:
                        while (true) {
                           if (interactiveObjectCount > tileRight2) {
                              interactiveObjectCount = (tileLeft2 << 7) + 1;
                              int scalar5 = (tileTop2 << 7) + 2;
                              int localTileHeights = sceneGraph.tileHeights[tileHeightIndex][tileLeft2][tileTop2] - modelHeight;
                              if (isPointOccluded(interactiveObjectCount, localTileHeights, scalar5) && isPointOccluded(tileHeightIndex = (tileRight2 << 7) - 1, localTileHeights, scalar5)) {
                                 tileLeft2 = (tileBottom2 << 7) - 1;
                                 if (isPointOccluded(interactiveObjectCount, localTileHeights, tileLeft2) && isPointOccluded(tileHeightIndex, localTileHeights, tileLeft2)) {
                                    objectOccluded = true;
                                    break testObjectOcclusion;
                                 }
                              }
                              break;
                           }

                           for (int loopIndex2 = tileTop2; loopIndex2 <= tileBottom2; loopIndex2++) {
                              if (sceneGraph.tileOcclusionCycles[tileHeightIndex][interactiveObjectCount][loopIndex2] == -renderCycle) {
                                 break scanObjectOcclusionTiles;
                              }
                           }

                           interactiveObjectCount++;
                        }
                     }

                     objectOccluded = false;
                  }

                  if (!objectOccluded) {
                     interactiveObject3.renderable
                        .renderAtPoint(
                           interactiveObject3.rotation,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           interactiveObject3.worldX - cameraX,
                           interactiveObject3.worldZ - cameraZ,
                           interactiveObject3.worldY - cameraY,
                           interactiveObject3.hash
                        );
                  }

                  for (int tileLeft3 = interactiveObject3.tileLeft; tileLeft3 <= interactiveObject3.tileRight; tileLeft3++) {
                     for (int tileTop3 = interactiveObject3.tileTop; tileTop3 <= interactiveObject3.tileBottom; tileTop3++) {
                        SceneTile sceneTile5;
                        if ((sceneTile5 = sceneTile8[tileLeft3][tileTop3]).wallCullMask != 0) {
                           tileQueue.addLast(sceneTile5);
                        } else if ((tileLeft3 != tileX || tileTop3 != tileY) && sceneTile5.visible) {
                           tileQueue.addLast(sceneTile5);
                        }
                     }
                  }
               }

               if (sceneTile.drawEntities) {
                  continue;
               }
            } catch (Exception exception) {
               sceneTile.drawEntities = false;
            }
         }

         SceneTile sceneTile6;
         if (sceneTile.visible
            && sceneTile.wallCullMask == 0
            && (tileX > cameraTileX || tileX <= minTileX || (sceneTile6 = sceneTile8[tileX - 1][tileY]) == null || !sceneTile6.visible)
            && (tileX < cameraTileX || tileX >= maxTileX - 1 || (sceneTile6 = sceneTile8[tileX + 1][tileY]) == null || !sceneTile6.visible)
            && (tileY > cameraTileY || tileY <= minTileY || (sceneTile6 = sceneTile8[tileX][tileY - 1]) == null || !sceneTile6.visible)
            && (tileY < cameraTileY || tileY >= maxTileY - 1 || (sceneTile6 = sceneTile8[tileX][tileY + 1]) == null || !sceneTile6.visible)) {
            sceneTile.visible = false;
            visibleTileCount--;
            GroundItemPile groundItemPile2 = sceneTile.groundItemPile;
            if (sceneTile.groundItemPile != null && groundItemPile2.heightOffset != 0) {
               if (groundItemPile2.secondGroundItem != null) {
                  groundItemPile2.secondGroundItem
                     .renderAtPoint(
                        0,
                        pitchSin,
                        pitchCos,
                        yawSin,
                        yawCos,
                        groundItemPile2.x - cameraX,
                        groundItemPile2.z - cameraZ - groundItemPile2.heightOffset,
                        groundItemPile2.y - cameraY,
                        groundItemPile2.hash
                     );
               }

               if (groundItemPile2.thirdGroundItem != null) {
                  groundItemPile2.thirdGroundItem
                     .renderAtPoint(
                        0,
                        pitchSin,
                        pitchCos,
                        yawSin,
                        yawCos,
                        groundItemPile2.x - cameraX,
                        groundItemPile2.z - cameraZ - groundItemPile2.heightOffset,
                        groundItemPile2.y - cameraY,
                        groundItemPile2.hash
                     );
               }

               if (groundItemPile2.firstGroundItem != null) {
                  groundItemPile2.firstGroundItem
                     .renderAtPoint(
                        0,
                        pitchSin,
                        pitchCos,
                        yawSin,
                        yawCos,
                        groundItemPile2.x - cameraX,
                        groundItemPile2.z - cameraZ - groundItemPile2.heightOffset,
                        groundItemPile2.y - cameraY,
                        groundItemPile2.hash
                     );
               }
            }

            if (sceneTile.delayedWallMask != 0) {
               GroundDecoration wallDecoration = sceneTile.wallDecoration;
               if (sceneTile.wallDecoration != null && (!useLegacyOcclusion || !this.isOccluded(sourceTileHeightIndex, tileX, tileY, wallDecoration.renderable.modelHeight))) {
                  if ((wallDecoration.configBits & sceneTile.delayedWallMask) != 0) {
                     wallDecoration.renderable
                        .renderAtPoint(
                           wallDecoration.face,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           wallDecoration.x - cameraX,
                           wallDecoration.z - cameraZ,
                           wallDecoration.y - cameraY,
                           wallDecoration.hash
                        );
                  } else if ((wallDecoration.configBits & 768) != 0) {
                     int x2 = wallDecoration.x - cameraX;
                     int z2 = wallDecoration.z - cameraZ;
                     int y2 = wallDecoration.y - cameraY;
                     int face2 = wallDecoration.face;
                     int scalar6;
                     if (wallDecoration.face != 1 && face2 != 2) {
                        scalar6 = x2;
                     } else {
                        scalar6 = -x2;
                     }

                     int scalar7;
                     if (face2 != 2 && face2 != 3) {
                        scalar7 = y2;
                     } else {
                        scalar7 = -y2;
                     }

                     if ((wallDecoration.configBits & 256) != 0 && scalar7 >= scalar6) {
                        int scalar8 = x2 + decorXOffsetsA[face2];
                        int scalar9 = y2 + decorYOffsetsA[face2];
                        wallDecoration.renderable.renderAtPoint((face2 << 9) + 256, pitchSin, pitchCos, yawSin, yawCos, scalar8, z2, scalar9, wallDecoration.hash);
                     }

                     if ((wallDecoration.configBits & 512) != 0 && scalar7 <= scalar6) {
                        int scalar10 = x2 + decorXOffsetsB[face2];
                        int scalar11 = y2 + decorYOffsetsB[face2];
                        wallDecoration.renderable
                           .renderAtPoint((face2 << 9) + 1280 & 2047, pitchSin, pitchCos, yawSin, yawCos, scalar10, z2, scalar11, wallDecoration.hash);
                     }
                  }
               }

               WallObject wallObject3 = sceneTile.wall;
               if (sceneTile.wall != null) {
                  if ((wallObject3.orientationB & sceneTile.delayedWallMask) != 0 && (!useLegacyOcclusion || !this.isWallOccluded(sourceTileHeightIndex, tileX, tileY, wallObject3.orientationB))) {
                     wallObject3.secondary
                        .renderAtPoint(
                           0,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           wallObject3.x - cameraX,
                           wallObject3.z - cameraZ,
                           wallObject3.y - cameraY,
                           wallObject3.hash
                        );
                  }

                  if ((wallObject3.orientation & sceneTile.delayedWallMask) != 0 && (!useLegacyOcclusion || !this.isWallOccluded(sourceTileHeightIndex, tileX, tileY, wallObject3.orientation))) {
                     wallObject3.primary
                        .renderAtPoint(
                           0,
                           pitchSin,
                           pitchCos,
                           yawSin,
                           yawCos,
                           wallObject3.x - cameraX,
                           wallObject3.z - cameraZ,
                           wallObject3.y - cameraY,
                           wallObject3.hash
                        );
                  }
               }
            }

            SceneTile sceneTile7;
            if (tileIndex < 3 && (sceneTile7 = this.tiles[tileIndex + 1][tileX][tileY]) != null && sceneTile7.visible) {
               tileQueue.addLast(sceneTile7);
            }

            if (tileX < cameraTileX && (sceneTile7 = sceneTile8[tileX + 1][tileY]) != null && sceneTile7.visible) {
               tileQueue.addLast(sceneTile7);
            }

            if (tileY < cameraTileY && (sceneTile7 = sceneTile8[tileX][tileY + 1]) != null && sceneTile7.visible) {
               tileQueue.addLast(sceneTile7);
            }

            if (tileX > cameraTileX && (sceneTile7 = sceneTile8[tileX - 1][tileY]) != null && sceneTile7.visible) {
               tileQueue.addLast(sceneTile7);
            }

            if (tileY > cameraTileY && (sceneTile7 = sceneTile8[tileX][tileY - 1]) != null && sceneTile7.visible) {
               tileQueue.addLast(sceneTile7);
            }
         }
      }
   }
   private void renderPlainTile(PlainTile plainTile, int tileHeightIndex, int newViewportCenterY, int newViewportCenterX, int viewportCenterY2, int restrictEdgesOrViewportCenterX, int newIndex, int sourceClickedTileY) {
      int scalar;
      int scalar2 = scalar = (newIndex << 7) - cameraX;
      int scalar3;
      int scalar4 = scalar3 = (sourceClickedTileY << 7) - cameraY;
      int textureBaseColor;
      int scalar5 = textureBaseColor = scalar2 + 128;
      int scalar6;
      int scalar7 = scalar6 = scalar4 + 128;
      int localTileHeights = this.tileHeights[tileHeightIndex][newIndex][sourceClickedTileY] - cameraZ;
      int tileHeights2 = this.tileHeights[tileHeightIndex][newIndex + 1][sourceClickedTileY] - cameraZ;
      int tileHeights3 = this.tileHeights[tileHeightIndex][newIndex + 1][sourceClickedTileY + 1] - cameraZ;
      tileHeightIndex = this.tileHeights[tileHeightIndex][newIndex][sourceClickedTileY + 1] - cameraZ;
      int sourceLocalTileHeights = scalar4 * viewportCenterY2 + scalar2 * restrictEdgesOrViewportCenterX >> 16;
      scalar4 = scalar4 * restrictEdgesOrViewportCenterX - scalar2 * viewportCenterY2 >> 16;
      scalar2 = sourceLocalTileHeights;
      sourceLocalTileHeights = localTileHeights * newViewportCenterX - scalar4 * newViewportCenterY >> 16;
      scalar4 = localTileHeights * newViewportCenterY + scalar4 * newViewportCenterX >> 16;
      localTileHeights = sourceLocalTileHeights;
      if (scalar4 >= 50) {
         sourceLocalTileHeights = scalar3 * viewportCenterY2 + scalar5 * restrictEdgesOrViewportCenterX >> 16;
         scalar3 = scalar3 * restrictEdgesOrViewportCenterX - scalar5 * viewportCenterY2 >> 16;
         scalar5 = sourceLocalTileHeights;
         sourceLocalTileHeights = tileHeights2 * newViewportCenterX - scalar3 * newViewportCenterY >> 16;
         scalar3 = tileHeights2 * newViewportCenterY + scalar3 * newViewportCenterX >> 16;
         tileHeights2 = sourceLocalTileHeights;
         if (scalar3 >= 50) {
            sourceLocalTileHeights = scalar7 * viewportCenterY2 + textureBaseColor * restrictEdgesOrViewportCenterX >> 16;
            scalar7 = scalar7 * restrictEdgesOrViewportCenterX - textureBaseColor * viewportCenterY2 >> 16;
            textureBaseColor = sourceLocalTileHeights;
            sourceLocalTileHeights = tileHeights3 * newViewportCenterX - scalar7 * newViewportCenterY >> 16;
            scalar7 = tileHeights3 * newViewportCenterY + scalar7 * newViewportCenterX >> 16;
            tileHeights3 = sourceLocalTileHeights;
            if (scalar7 >= 50) {
               sourceLocalTileHeights = scalar6 * viewportCenterY2 + scalar * restrictEdgesOrViewportCenterX >> 16;
               scalar6 = scalar6 * restrictEdgesOrViewportCenterX - scalar * viewportCenterY2 >> 16;
               scalar = sourceLocalTileHeights;
               sourceLocalTileHeights = tileHeightIndex * newViewportCenterX - scalar6 * newViewportCenterY >> 16;
               if ((scalar6 = tileHeightIndex * newViewportCenterY + scalar6 * newViewportCenterX >> 16) >= 50) {
                  tileHeightIndex = Rasterizer3D.viewportCenterX + (scalar2 << Client.getProjectionScaleShift()) / scalar4;
                  newViewportCenterY = Rasterizer3D.viewportCenterY + (localTileHeights << Client.getProjectionScaleShift()) / scalar4;
                  newViewportCenterX = Rasterizer3D.viewportCenterX + (scalar5 << Client.getProjectionScaleShift()) / scalar3;
                  viewportCenterY2 = Rasterizer3D.viewportCenterY + (tileHeights2 << Client.getProjectionScaleShift()) / scalar3;
                  restrictEdgesOrViewportCenterX = Rasterizer3D.viewportCenterX + (textureBaseColor << Client.getProjectionScaleShift()) / scalar7;
                  int depthBufferIndex = Rasterizer3D.viewportCenterY + (tileHeights3 << Client.getProjectionScaleShift()) / scalar7;
                  int localViewportCenterX = Rasterizer3D.viewportCenterX + (scalar << Client.getProjectionScaleShift()) / scalar6;
                  int viewportCenterY3 = Rasterizer3D.viewportCenterY + (sourceLocalTileHeights << Client.getProjectionScaleShift()) / scalar6;
                  Rasterizer3D.alpha = 0;
                  if ((restrictEdgesOrViewportCenterX - localViewportCenterX) * (viewportCenterY2 - viewportCenterY3) - (depthBufferIndex - viewportCenterY3) * (newViewportCenterX - localViewportCenterX) > 0) {
                     Rasterizer3D.restrictEdges = restrictEdgesOrViewportCenterX < 0
                        || localViewportCenterX < 0
                        || newViewportCenterX < 0
                        || restrictEdgesOrViewportCenterX > Rasterizer2D.centerX
                        || localViewportCenterX > Rasterizer2D.centerX
                        || newViewportCenterX > Rasterizer2D.centerX;
                     if (checkClick && isPointInTriangle(clickX, clickY, depthBufferIndex, viewportCenterY3, viewportCenterY2, restrictEdgesOrViewportCenterX, localViewportCenterX, newViewportCenterX)) {
                        clickedTileX = newIndex;
                        clickedTileY = sourceClickedTileY;
                     }

                     if (plainTile.textureId == -1) {
                        if (plainTile.cornerColor2 != 12345678) {
                           Rasterizer3D.drawShadedTriangle(
                              false, depthBufferIndex, viewportCenterY3, viewportCenterY2, restrictEdgesOrViewportCenterX, localViewportCenterX, newViewportCenterX, plainTile.cornerColor2, plainTile.cornerColor3, plainTile.cornerColor1, scalar7, scalar6, scalar3
                           );
                        }
                     } else if (!lowMemory) {
                        if (plainTile.flat) {
                           Rasterizer3D.drawTexturedTriangle(
                              false,
                              depthBufferIndex,
                              viewportCenterY3,
                              viewportCenterY2,
                              restrictEdgesOrViewportCenterX,
                              localViewportCenterX,
                              newViewportCenterX,
                              plainTile.cornerColor2,
                              plainTile.cornerColor3,
                              plainTile.cornerColor1,
                              scalar2,
                              scalar5,
                              scalar,
                              localTileHeights,
                              tileHeights2,
                              sourceLocalTileHeights,
                              scalar4,
                              scalar3,
                              scalar6,
                              plainTile.textureId,
                              scalar7,
                              scalar6,
                              scalar3
                           );
                        } else {
                           Rasterizer3D.drawTexturedTriangle(
                              false,
                              depthBufferIndex,
                              viewportCenterY3,
                              viewportCenterY2,
                              restrictEdgesOrViewportCenterX,
                              localViewportCenterX,
                              newViewportCenterX,
                              plainTile.cornerColor2,
                              plainTile.cornerColor3,
                              plainTile.cornerColor1,
                              textureBaseColor,
                              scalar,
                              scalar5,
                              tileHeights3,
                              sourceLocalTileHeights,
                              tileHeights2,
                              scalar7,
                              scalar6,
                              scalar3,
                              plainTile.textureId,
                              scalar7,
                              scalar6,
                              scalar3
                           );
                        }
                     } else {
                        textureBaseColor = textureBaseColors[plainTile.textureId];
                        Rasterizer3D.drawShadedTriangle(
                           false,
                           depthBufferIndex,
                           viewportCenterY3,
                           viewportCenterY2,
                           restrictEdgesOrViewportCenterX,
                           localViewportCenterX,
                           newViewportCenterX,
                           adjustLightness(textureBaseColor, plainTile.cornerColor2),
                           adjustLightness(textureBaseColor, plainTile.cornerColor3),
                           adjustLightness(textureBaseColor, plainTile.cornerColor1),
                           scalar7,
                           scalar6,
                           scalar3
                        );
                     }
                  }

                  if ((tileHeightIndex - newViewportCenterX) * (viewportCenterY3 - viewportCenterY2) - (newViewportCenterY - viewportCenterY2) * (localViewportCenterX - newViewportCenterX) > 0) {
                     Rasterizer3D.restrictEdges = tileHeightIndex < 0
                        || newViewportCenterX < 0
                        || localViewportCenterX < 0
                        || tileHeightIndex > Rasterizer2D.centerX
                        || newViewportCenterX > Rasterizer2D.centerX
                        || localViewportCenterX > Rasterizer2D.centerX;
                     if (checkClick && isPointInTriangle(clickX, clickY, newViewportCenterY, viewportCenterY2, viewportCenterY3, tileHeightIndex, newViewportCenterX, localViewportCenterX)) {
                        clickedTileX = newIndex;
                        clickedTileY = sourceClickedTileY;
                     }

                     if (plainTile.textureId == -1) {
                        if (plainTile.cornerColor0 != 12345678) {
                           Rasterizer3D.drawShadedTriangle(
                              false, newViewportCenterY, viewportCenterY2, viewportCenterY3, tileHeightIndex, newViewportCenterX, localViewportCenterX, plainTile.cornerColor0, plainTile.cornerColor1, plainTile.cornerColor3, scalar4, scalar3, scalar6
                           );
                           return;
                        }
                     } else {
                        if (!lowMemory) {
                           Rasterizer3D.drawTexturedTriangle(
                              false,
                              newViewportCenterY,
                              viewportCenterY2,
                              viewportCenterY3,
                              tileHeightIndex,
                              newViewportCenterX,
                              localViewportCenterX,
                              plainTile.cornerColor0,
                              plainTile.cornerColor1,
                              plainTile.cornerColor3,
                              scalar2,
                              scalar5,
                              scalar,
                              localTileHeights,
                              tileHeights2,
                              sourceLocalTileHeights,
                              scalar4,
                              scalar3,
                              scalar6,
                              plainTile.textureId,
                              scalar4,
                              scalar3,
                              scalar6
                           );
                           return;
                        }

                        textureBaseColor = textureBaseColors[plainTile.textureId];
                        Rasterizer3D.drawShadedTriangle(
                           false,
                           newViewportCenterY,
                           viewportCenterY2,
                           viewportCenterY3,
                           tileHeightIndex,
                           newViewportCenterX,
                           localViewportCenterX,
                           adjustLightness(textureBaseColor, plainTile.cornerColor0),
                           adjustLightness(textureBaseColor, plainTile.cornerColor1),
                           adjustLightness(textureBaseColor, plainTile.cornerColor3),
                           scalar4,
                           scalar3,
                           scalar6
                        );
                     }
                  }
               }
            }
         }
      }
   }
   private void renderShapedTile(int newClickedTileX, int newProjectedX, int projectedX2, ShapedTile shapedTile, int newProjectedY, int newClickedTileY, int projectedY2) {
      int vertexXLengthOrVertexX = shapedTile.vertexX.length;

      for (int vertexXIndex = 0; vertexXIndex < vertexXLengthOrVertexX; vertexXIndex++) {
         int cameraXOrVertexX = shapedTile.vertexX[vertexXIndex] - cameraX;
         int localVertexY = shapedTile.vertexY[vertexXIndex] - cameraZ;
         int cameraZOrVertexZ;
         int localCameraY = (cameraZOrVertexZ = shapedTile.vertexZ[vertexXIndex] - cameraY) * projectedX2 + cameraXOrVertexX * projectedY2 >> 16;
         cameraZOrVertexZ = cameraZOrVertexZ * projectedY2 - cameraXOrVertexX * projectedX2 >> 16;
         cameraXOrVertexX = localCameraY;
         localCameraY = localVertexY * newProjectedY - cameraZOrVertexZ * newProjectedX >> 16;
         if ((cameraZOrVertexZ = localVertexY * newProjectedX + cameraZOrVertexZ * newProjectedY >> 16) < 50) {
            return;
         }

         if (shapedTile.triangleTextureIds != null) {
            ShapedTile.cameraX[vertexXIndex] = cameraXOrVertexX;
            ShapedTile.cameraY[vertexXIndex] = localCameraY;
            ShapedTile.cameraZ[vertexXIndex] = cameraZOrVertexZ;
         }

         ShapedTile.projectedX[vertexXIndex] = Rasterizer3D.viewportCenterX + (cameraXOrVertexX << Client.getProjectionScaleShift()) / cameraZOrVertexZ;
         ShapedTile.projectedY[vertexXIndex] = Rasterizer3D.viewportCenterY + (localCameraY << Client.getProjectionScaleShift()) / cameraZOrVertexZ;
         ShapedTile.projectedDepth[vertexXIndex] = cameraZOrVertexZ;
      }

      Rasterizer3D.alpha = 0;
      vertexXLengthOrVertexX = shapedTile.triangleVertexA.length;

      for (int triangleVertexAIndex = 0; triangleVertexAIndex < vertexXLengthOrVertexX; triangleVertexAIndex++) {
         int triangleVertexAEntry = shapedTile.triangleVertexA[triangleVertexAIndex];
         int triangleVertexBEntry = shapedTile.triangleVertexB[triangleVertexAIndex];
         int triangleVertexCEntry = shapedTile.triangleVertexC[triangleVertexAIndex];
         int restrictEdgesOrProjectedX = ShapedTile.projectedX[triangleVertexAEntry];
         newProjectedX = ShapedTile.projectedX[triangleVertexBEntry];
         projectedX2 = ShapedTile.projectedX[triangleVertexCEntry];
         newProjectedY = ShapedTile.projectedY[triangleVertexAEntry];
         projectedY2 = ShapedTile.projectedY[triangleVertexBEntry];
         int projectedYEntry = ShapedTile.projectedY[triangleVertexCEntry];
         if ((restrictEdgesOrProjectedX - newProjectedX) * (projectedYEntry - projectedY2) - (newProjectedY - projectedY2) * (projectedX2 - newProjectedX) > 0) {
            Rasterizer3D.restrictEdges = restrictEdgesOrProjectedX < 0
               || newProjectedX < 0
               || projectedX2 < 0
               || restrictEdgesOrProjectedX > Rasterizer2D.centerX
               || newProjectedX > Rasterizer2D.centerX
               || projectedX2 > Rasterizer2D.centerX;
            if (checkClick && isPointInTriangle(clickX, clickY, newProjectedY, projectedY2, projectedYEntry, restrictEdgesOrProjectedX, newProjectedX, projectedX2)) {
               clickedTileX = newClickedTileX;
               clickedTileY = newClickedTileY;
            }

            Rasterizer3D.drawDepthTriangle(restrictEdgesOrProjectedX, newProjectedX, projectedX2, newProjectedY, projectedY2, projectedYEntry, ShapedTile.projectedDepth[triangleVertexAEntry], ShapedTile.projectedDepth[triangleVertexBEntry], ShapedTile.projectedDepth[triangleVertexCEntry]);
            if (shapedTile.triangleTextureIds != null && shapedTile.triangleTextureIds[triangleVertexAIndex] != -1) {
               if (!lowMemory) {
                  if (shapedTile.flat) {
                     Rasterizer3D.drawTexturedTriangle(
                        false,
                        newProjectedY,
                        projectedY2,
                        projectedYEntry,
                        restrictEdgesOrProjectedX,
                        newProjectedX,
                        projectedX2,
                        shapedTile.triangleColorA[triangleVertexAIndex],
                        shapedTile.triangleColorB[triangleVertexAIndex],
                        shapedTile.triangleColorC[triangleVertexAIndex],
                        ShapedTile.cameraX[0],
                        ShapedTile.cameraX[1],
                        ShapedTile.cameraX[3],
                        ShapedTile.cameraY[0],
                        ShapedTile.cameraY[1],
                        ShapedTile.cameraY[3],
                        ShapedTile.cameraZ[0],
                        ShapedTile.cameraZ[1],
                        ShapedTile.cameraZ[3],
                        shapedTile.triangleTextureIds[triangleVertexAIndex],
                        ShapedTile.projectedDepth[triangleVertexAEntry],
                        ShapedTile.projectedDepth[triangleVertexBEntry],
                        ShapedTile.projectedDepth[triangleVertexCEntry]
                     );
                  } else {
                     Rasterizer3D.drawTexturedTriangle(
                        false,
                        newProjectedY,
                        projectedY2,
                        projectedYEntry,
                        restrictEdgesOrProjectedX,
                        newProjectedX,
                        projectedX2,
                        shapedTile.triangleColorA[triangleVertexAIndex],
                        shapedTile.triangleColorB[triangleVertexAIndex],
                        shapedTile.triangleColorC[triangleVertexAIndex],
                        ShapedTile.cameraX[triangleVertexAEntry],
                        ShapedTile.cameraX[triangleVertexBEntry],
                        ShapedTile.cameraX[triangleVertexCEntry],
                        ShapedTile.cameraY[triangleVertexAEntry],
                        ShapedTile.cameraY[triangleVertexBEntry],
                        ShapedTile.cameraY[triangleVertexCEntry],
                        ShapedTile.cameraZ[triangleVertexAEntry],
                        ShapedTile.cameraZ[triangleVertexBEntry],
                        ShapedTile.cameraZ[triangleVertexCEntry],
                        shapedTile.triangleTextureIds[triangleVertexAIndex],
                        ShapedTile.projectedDepth[triangleVertexAEntry],
                        ShapedTile.projectedDepth[triangleVertexBEntry],
                        ShapedTile.projectedDepth[triangleVertexCEntry]
                     );
                  }
               } else {
                  int textureBaseColor = textureBaseColors[shapedTile.triangleTextureIds[triangleVertexAIndex]];
                  Rasterizer3D.drawShadedTriangle(
                     false,
                     newProjectedY,
                     projectedY2,
                     projectedYEntry,
                     restrictEdgesOrProjectedX,
                     newProjectedX,
                     projectedX2,
                     adjustLightness(textureBaseColor, shapedTile.triangleColorA[triangleVertexAIndex]),
                     adjustLightness(textureBaseColor, shapedTile.triangleColorB[triangleVertexAIndex]),
                     adjustLightness(textureBaseColor, shapedTile.triangleColorC[triangleVertexAIndex]),
                     ShapedTile.projectedDepth[triangleVertexAEntry],
                     ShapedTile.projectedDepth[triangleVertexBEntry],
                     ShapedTile.projectedDepth[triangleVertexCEntry]
                  );
               }
            } else if (shapedTile.triangleColorA[triangleVertexAIndex] != 12345678) {
               Rasterizer3D.drawShadedTriangle(
                  false,
                  newProjectedY,
                  projectedY2,
                  projectedYEntry,
                  restrictEdgesOrProjectedX,
                  newProjectedX,
                  projectedX2,
                  shapedTile.triangleColorA[triangleVertexAIndex],
                  shapedTile.triangleColorB[triangleVertexAIndex],
                  shapedTile.triangleColorC[triangleVertexAIndex],
                  ShapedTile.projectedDepth[triangleVertexAEntry],
                  ShapedTile.projectedDepth[triangleVertexBEntry],
                  ShapedTile.projectedDepth[triangleVertexCEntry]
               );
            }
         }
      }
   }
   private static int adjustLightness(int textureBaseColor, int scalarArgument) {
      if ((scalarArgument = (scalarArgument = 127 - scalarArgument) * (textureBaseColor & 127) / 160) < 2) {
         scalarArgument = 2;
      } else if (scalarArgument > 126) {
         scalarArgument = 126;
      }

      return (textureBaseColor & 65408) + scalarArgument;
   }
   private static boolean isPointInTriangle(int clickX, int clickY, int depthBufferIndex, int scalarArgument, int scalarArgument2, int tileHeightIndex, int scalarArgument3, int scalarArgument4) {
      if (clickY < depthBufferIndex && clickY < scalarArgument && clickY < scalarArgument2) {
         return false;
      }

      if (clickY > depthBufferIndex && clickY > scalarArgument && clickY > scalarArgument2) {
         return false;
      }

      if (clickX < tileHeightIndex && clickX < scalarArgument3 && clickX < scalarArgument4) {
         return false;
      }

      if (clickX > tileHeightIndex && clickX > scalarArgument3 && clickX > scalarArgument4) {
         return false;
      }

      int scalar = (clickY - depthBufferIndex) * (scalarArgument3 - tileHeightIndex) - (clickX - tileHeightIndex) * (scalarArgument - depthBufferIndex);
      depthBufferIndex = (clickY - scalarArgument2) * (tileHeightIndex - scalarArgument4) - (clickX - scalarArgument4) * (depthBufferIndex - scalarArgument2);
      clickX = (clickY - scalarArgument) * (scalarArgument4 - scalarArgument3) - (clickX - scalarArgument3) * (scalarArgument2 - scalarArgument);
      return scalar * clickX > 0 && clickX * depthBufferIndex > 0;
   }
   private boolean isTileOccluded(int tileOcclusionCycleIndex, int newIndex, int positionArgument) {
      int localTileOcclusionCycles;
      if ((localTileOcclusionCycles = this.tileOcclusionCycles[tileOcclusionCycleIndex][newIndex][positionArgument]) == -renderCycle) {
         return false;
      } else if (localTileOcclusionCycles == renderCycle) {
         return true;
      } else {
         localTileOcclusionCycles = newIndex << 7;
         int scalar = positionArgument << 7;
         if (isPointOccluded(localTileOcclusionCycles + 1, this.tileHeights[tileOcclusionCycleIndex][newIndex][positionArgument], scalar + 1)
            && isPointOccluded(localTileOcclusionCycles + 128 - 1, this.tileHeights[tileOcclusionCycleIndex][newIndex + 1][positionArgument], scalar + 1)
            && isPointOccluded(localTileOcclusionCycles + 128 - 1, this.tileHeights[tileOcclusionCycleIndex][newIndex + 1][positionArgument + 1], scalar + 128 - 1)
            && isPointOccluded(localTileOcclusionCycles + 1, this.tileHeights[tileOcclusionCycleIndex][newIndex][positionArgument + 1], scalar + 128 - 1)) {
            this.tileOcclusionCycles[tileOcclusionCycleIndex][newIndex][positionArgument] = renderCycle;
            return true;
         } else {
            this.tileOcclusionCycles[tileOcclusionCycleIndex][newIndex][positionArgument] = -renderCycle;
            return false;
         }
      }
   }
   private boolean isWallOccluded(int tileHeightIndex, int scalarArgument, int scalarArgument2, int orientation) {
      if (!this.isTileOccluded(tileHeightIndex, scalarArgument, scalarArgument2)) {
         return false;
      }

      int interactiveObjectCount = scalarArgument << 7;
      int scalar = scalarArgument2 << 7;
      int localTileHeights;
      scalarArgument2 = (localTileHeights = this.tileHeights[tileHeightIndex][scalarArgument][scalarArgument2] - 1) - 120;
      int scalar2 = localTileHeights - 230;
      int scalar3 = localTileHeights - 238;
      if (orientation < 16) {
         if (orientation == 1) {
            if (interactiveObjectCount > cameraX) {
               if (!isPointOccluded(interactiveObjectCount, localTileHeights, scalar)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount, localTileHeights, scalar + 128)) {
                  return false;
               }
            }

            if (tileHeightIndex > 0) {
               if (!isPointOccluded(interactiveObjectCount, scalarArgument2, scalar)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount, scalarArgument2, scalar + 128)) {
                  return false;
               }
            }

            if (isPointOccluded(interactiveObjectCount, scalar2, scalar) && isPointOccluded(interactiveObjectCount, scalar2, scalar + 128)) {
               return true;
            }

            return false;
         }

         if (orientation == 2) {
            if (scalar < cameraY) {
               if (!isPointOccluded(interactiveObjectCount, localTileHeights, scalar + 128)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount + 128, localTileHeights, scalar + 128)) {
                  return false;
               }
            }

            if (tileHeightIndex > 0) {
               if (!isPointOccluded(interactiveObjectCount, scalarArgument2, scalar + 128)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount + 128, scalarArgument2, scalar + 128)) {
                  return false;
               }
            }

            if (isPointOccluded(interactiveObjectCount, scalar2, scalar + 128) && isPointOccluded(interactiveObjectCount + 128, scalar2, scalar + 128)) {
               return true;
            }

            return false;
         }

         if (orientation == 4) {
            if (interactiveObjectCount < cameraX) {
               if (!isPointOccluded(interactiveObjectCount + 128, localTileHeights, scalar)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount + 128, localTileHeights, scalar + 128)) {
                  return false;
               }
            }

            if (tileHeightIndex > 0) {
               if (!isPointOccluded(interactiveObjectCount + 128, scalarArgument2, scalar)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount + 128, scalarArgument2, scalar + 128)) {
                  return false;
               }
            }

            if (isPointOccluded(interactiveObjectCount + 128, scalar2, scalar) && isPointOccluded(interactiveObjectCount + 128, scalar2, scalar + 128)) {
               return true;
            }

            return false;
         }

         if (orientation == 8) {
            if (scalar > cameraY) {
               if (!isPointOccluded(interactiveObjectCount, localTileHeights, scalar)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount + 128, localTileHeights, scalar)) {
                  return false;
               }
            }

            if (tileHeightIndex > 0) {
               if (!isPointOccluded(interactiveObjectCount, scalarArgument2, scalar)) {
                  return false;
               }

               if (!isPointOccluded(interactiveObjectCount + 128, scalarArgument2, scalar)) {
                  return false;
               }
            }

            if (isPointOccluded(interactiveObjectCount, scalar2, scalar) && isPointOccluded(interactiveObjectCount + 128, scalar2, scalar)) {
               return true;
            }

            return false;
         }
      }

      if (!isPointOccluded(interactiveObjectCount + 64, scalar3, scalar + 64)) {
         return false;
      }

      if (orientation == 16) {
         return isPointOccluded(interactiveObjectCount, scalar2, scalar + 128);
      }

      if (orientation == 32) {
         return isPointOccluded(interactiveObjectCount + 128, scalar2, scalar + 128);
      }

      if (orientation == 64) {
         return isPointOccluded(interactiveObjectCount + 128, scalar2, scalar);
      }

      if (orientation == 128) {
         return isPointOccluded(interactiveObjectCount, scalar2, scalar);
      }

      System.out.println("Warning unsupported wall type");
      return true;
   }
   private boolean isOccluded(int tileHeightIndex, int newIndex, int positionArgument, int modelHeight) {
      if (!this.isTileOccluded(tileHeightIndex, newIndex, positionArgument)) {
         return false;
      }

      int scalar = newIndex << 7;
      int scalar2 = positionArgument << 7;
      return isPointOccluded(scalar + 1, this.tileHeights[tileHeightIndex][newIndex][positionArgument] - modelHeight, scalar2 + 1)
         && isPointOccluded(scalar + 128 - 1, this.tileHeights[tileHeightIndex][newIndex + 1][positionArgument] - modelHeight, scalar2 + 1)
         && isPointOccluded(scalar + 128 - 1, this.tileHeights[tileHeightIndex][newIndex + 1][positionArgument + 1] - modelHeight, scalar2 + 128 - 1)
         && isPointOccluded(scalar + 1, this.tileHeights[tileHeightIndex][newIndex][positionArgument + 1] - modelHeight, scalar2 + 128 - 1);
   }
   private static boolean isPointOccluded(int interactiveObjectCount, int scalarArgument, int scalarArgument2) {
      for (int activeOccluderIndex = 0; activeOccluderIndex < activeOccluderCount; activeOccluderIndex++) {
         Occluder occluder;
         if ((occluder = activeOccluders[activeOccluderIndex]).mode == 1) {
            int localMinX;
            if ((localMinX = occluder.minX - interactiveObjectCount) > 0) {
               int localMinZ = occluder.minZ + (occluder.minZDelta * localMinX >> 8);
               int localMaxZ = occluder.maxZ + (occluder.maxZDelta * localMinX >> 8);
               int localMinY = occluder.minY + (occluder.minYDelta * localMinX >> 8);
               int localMaxY = occluder.maxY + (occluder.maxYDelta * localMinX >> 8);
               if (scalarArgument2 >= localMinZ && scalarArgument2 <= localMaxZ && scalarArgument >= localMinY && scalarArgument <= localMaxY) {
                  return true;
               }
            }
         } else if (occluder.mode == 2) {
            int scalar;
            if ((scalar = interactiveObjectCount - occluder.minX) > 0) {
               int minZ2 = occluder.minZ + (occluder.minZDelta * scalar >> 8);
               int maxZ2 = occluder.maxZ + (occluder.maxZDelta * scalar >> 8);
               int minY2 = occluder.minY + (occluder.minYDelta * scalar >> 8);
               int maxY2 = occluder.maxY + (occluder.maxYDelta * scalar >> 8);
               if (scalarArgument2 >= minZ2 && scalarArgument2 <= maxZ2 && scalarArgument >= minY2 && scalarArgument <= maxY2) {
                  return true;
               }
            }
         } else if (occluder.mode == 3) {
            int minZ3;
            if ((minZ3 = occluder.minZ - scalarArgument2) > 0) {
               int minX2 = occluder.minX + (occluder.minXDelta * minZ3 >> 8);
               int localMaxX = occluder.maxX + (occluder.maxXDelta * minZ3 >> 8);
               int minY3 = occluder.minY + (occluder.minYDelta * minZ3 >> 8);
               int maxY3 = occluder.maxY + (occluder.maxYDelta * minZ3 >> 8);
               if (interactiveObjectCount >= minX2 && interactiveObjectCount <= localMaxX && scalarArgument >= minY3 && scalarArgument <= maxY3) {
                  return true;
               }
            }
         } else if (occluder.mode == 4) {
            int scalar2;
            if ((scalar2 = scalarArgument2 - occluder.minZ) > 0) {
               int minX3 = occluder.minX + (occluder.minXDelta * scalar2 >> 8);
               int maxX2 = occluder.maxX + (occluder.maxXDelta * scalar2 >> 8);
               int minY4 = occluder.minY + (occluder.minYDelta * scalar2 >> 8);
               int maxY4 = occluder.maxY + (occluder.maxYDelta * scalar2 >> 8);
               if (interactiveObjectCount >= minX3 && interactiveObjectCount <= maxX2 && scalarArgument >= minY4 && scalarArgument <= maxY4) {
                  return true;
               }
            }
         } else {
            int scalar3;
            if (occluder.mode == 5 && (scalar3 = scalarArgument - occluder.minY) > 0) {
               int minX4 = occluder.minX + (occluder.minXDelta * scalar3 >> 8);
               int maxX3 = occluder.maxX + (occluder.maxXDelta * scalar3 >> 8);
               int minZ4 = occluder.minZ + (occluder.minZDelta * scalar3 >> 8);
               int maxZ3 = occluder.maxZ + (occluder.maxZDelta * scalar3 >> 8);
               if (interactiveObjectCount >= minX4 && interactiveObjectCount <= maxX3 && scalarArgument2 >= minZ4 && scalarArgument2 <= maxZ3) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
