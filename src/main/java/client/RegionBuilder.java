package client;
final class RegionBuilder {
   private static int hueOffset = (int)(Math.random() * 17.0) - 8;
   private final int[] hueSums;
   private final int[] saturationSums;
   private final int[] lightnessSums;
   private final int[] hueMultiplierSums;
   private final int[] underlayCounts;
   private final int[][][] tileHeights;
   private final byte[][][] overlayIds;
   static int buildPlane;
   private static int lightnessOffset = (int)(Math.random() * 33.0) - 16;
   private final byte[][][] tileShadows;
   private final int[][][] occlusionFlags;
   private final byte[][][] overlayShapes;
   private static final int[] wallDecorationXOffsets = new int[]{1, 0, -1, 0};
   private final int[][] tileLighting;
   private static final int[] diagonalWallFlags = new int[]{16, 32, 64, 128};
   private final byte[][][] underlayIds;
   private static final int[] wallDecorationYOffsets = new int[]{0, -1, 0, 1};
   static int minimumPlane = 99;
   private final int regionWidth;
   private final int regionHeight;
   private final byte[][][] overlayOrientations;
   private final byte[][][] tileSettings;
   static boolean lowMemory = true;
   private static final int[] wallFlags = new int[]{1, 2, 4, 8};

   public RegionBuilder(byte[][][] newTileSettings, int[][][] newTileHeights) {
      minimumPlane = 99;
      this.regionWidth = 104;
      this.regionHeight = 104;
      this.tileHeights = newTileHeights;
      this.tileSettings = newTileSettings;
      this.underlayIds = new byte[4][104][104];
      this.overlayIds = new byte[4][104][104];
      this.overlayShapes = new byte[4][104][104];
      this.overlayOrientations = new byte[4][104][104];
      this.occlusionFlags = new int[4][105][105];
      this.tileShadows = new byte[4][105][105];
      this.tileLighting = new int[105][105];
      this.hueSums = new int[104];
      this.saturationSums = new int[104];
      this.lightnessSums = new int[104];
      this.hueMultiplierSums = new int[104];
      this.underlayCounts = new int[104];
   }
   private static int pseudoRandomNoise(int scalarArgument, int scalarArgument2) {
      int scalar;
      int scalar2;
      return ((scalar2 = (scalar = scalarArgument + scalarArgument2 * 57) ^ scalar << 13) * (scalar2 * scalar2 * 15731 + 789221) + 1376312589 & 2147483647) >> 19 & 0xFF;
   }
   public final void buildScene(CollisionMap[] values, SceneGraph sceneGraph) {
      for (int tileSettingIndex = 0; tileSettingIndex < 4; tileSettingIndex++) {
         for (int clippingDataIndex = 0; clippingDataIndex < 104; clippingDataIndex++) {
            for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
               if ((this.tileSettings[tileSettingIndex][clippingDataIndex][loopIndex] & 1) == 1) {
                  int sourceTileSettingIndex = tileSettingIndex;
                  if ((this.tileSettings[1][clippingDataIndex][loopIndex] & 2) == 2) {
                     sourceTileSettingIndex = tileSettingIndex - 1;
                  }

                  if (sourceTileSettingIndex >= 0) {
                     values[sourceTileSettingIndex].setBlocked(loopIndex, clippingDataIndex);
                  }
               }
            }
         }
      }

      if ((hueOffset = hueOffset + ((int)(Math.random() * 5.0) - 2)) < -8) {
         hueOffset = -8;
      }

      if (hueOffset > 8) {
         hueOffset = 8;
      }

      if ((lightnessOffset = lightnessOffset + ((int)(Math.random() * 5.0) - 2)) < -16) {
         lightnessOffset = -16;
      }

      if (lightnessOffset > 16) {
         lightnessOffset = 16;
      }

      for (int tileShadowIndex = 0; tileShadowIndex < 4; tileShadowIndex++) {
         byte[][] tileShadow = this.tileShadows[tileShadowIndex];
         int sqrtResult = (int)Math.sqrt(5100.0);
         int scalar = sqrtResult * 768 >> 8;

         for (int loopIndex2 = 1; loopIndex2 < 103; loopIndex2++) {
            for (int tileLightingIndex = 1; tileLightingIndex < 103; tileLightingIndex++) {
               int localTileHeights = this.tileHeights[tileShadowIndex][tileLightingIndex + 1][loopIndex2] - this.tileHeights[tileShadowIndex][tileLightingIndex - 1][loopIndex2];
               int tileHeights2 = this.tileHeights[tileShadowIndex][tileLightingIndex][loopIndex2 + 1] - this.tileHeights[tileShadowIndex][tileLightingIndex][loopIndex2 - 1];
               int sqrtResult2 = (int)Math.sqrt(localTileHeights * localTileHeights + 65536 + tileHeights2 * tileHeights2);
               int scalar2 = (localTileHeights << 8) / sqrtResult2;
               int scalar3 = 65536 / sqrtResult2;
               int scalar4 = (tileHeights2 << 8) / sqrtResult2;
               int scalar5 = 96 + (scalar2 * -50 + scalar3 * -10 + scalar4 * -50) / scalar;
               int scalar6 = (tileShadow[tileLightingIndex - 1][loopIndex2] >> 2)
                  + (tileShadow[tileLightingIndex + 1][loopIndex2] >> 3)
                  + (tileShadow[tileLightingIndex][loopIndex2 - 1] >> 2)
                  + (tileShadow[tileLightingIndex][loopIndex2 + 1] >> 3)
                  + (tileShadow[tileLightingIndex][loopIndex2] >> 1);
               this.tileLighting[tileLightingIndex][loopIndex2] = scalar5 - scalar6;
            }
         }

         for (int hueSumIndex = 0; hueSumIndex < 104; hueSumIndex++) {
            this.hueSums[hueSumIndex] = 0;
            this.saturationSums[hueSumIndex] = 0;
            this.lightnessSums[hueSumIndex] = 0;
            this.hueMultiplierSums[hueSumIndex] = 0;
            this.underlayCounts[hueSumIndex] = 0;
         }

         for (int tileLightingIndex2 = -5; tileLightingIndex2 < 109; tileLightingIndex2++) {
            for (int hueSumIndex2 = 0; hueSumIndex2 < 104; hueSumIndex2++) {
               int localUnderlayIds;
               int scalar7;
               if ((scalar7 = tileLightingIndex2 + 5) >= 0 && scalar7 < 104 && (localUnderlayIds = this.underlayIds[tileShadowIndex][scalar7][hueSumIndex2] & 255) > 0) {
                  FloorDefinition floorDefinition = FloorDefinition.underlayDefinitions[localUnderlayIds - 1];
                  this.hueSums[hueSumIndex2] = this.hueSums[hueSumIndex2] + floorDefinition.weightedHue;
                  this.saturationSums[hueSumIndex2] = this.saturationSums[hueSumIndex2] + floorDefinition.saturation;
                  this.lightnessSums[hueSumIndex2] = this.lightnessSums[hueSumIndex2] + floorDefinition.lightness;
                  this.hueMultiplierSums[hueSumIndex2] = this.hueMultiplierSums[hueSumIndex2] + floorDefinition.hueMultiplier;
                  this.underlayCounts[hueSumIndex2]++;
               }

               int scalar8;
               int underlayIds2;
               if ((scalar8 = tileLightingIndex2 - 5) >= 0 && scalar8 < 104 && (underlayIds2 = this.underlayIds[tileShadowIndex][scalar8][hueSumIndex2] & 255) > 0) {
                  FloorDefinition definition = FloorDefinition.underlayDefinitions[underlayIds2 - 1];
                  this.hueSums[hueSumIndex2] = this.hueSums[hueSumIndex2] - definition.weightedHue;
                  this.saturationSums[hueSumIndex2] = this.saturationSums[hueSumIndex2] - definition.saturation;
                  this.lightnessSums[hueSumIndex2] = this.lightnessSums[hueSumIndex2] - definition.lightness;
                  this.hueMultiplierSums[hueSumIndex2] = this.hueMultiplierSums[hueSumIndex2] - definition.hueMultiplier;
                  this.underlayCounts[hueSumIndex2]--;
               }
            }

            if (tileLightingIndex2 > 0 && tileLightingIndex2 < 103) {
               int scalar9 = 0;
               int scalar10 = 0;
               int scalar11 = 0;
               int scalar12 = 0;
               int scalar13 = 0;

               for (int loopIndex3 = -5; loopIndex3 < 109; loopIndex3++) {
                  int hueSumIndex3;
                  if ((hueSumIndex3 = loopIndex3 + 5) >= 0 && hueSumIndex3 < 104) {
                     scalar9 += this.hueSums[hueSumIndex3];
                     scalar10 += this.saturationSums[hueSumIndex3];
                     scalar11 += this.lightnessSums[hueSumIndex3];
                     scalar12 += this.hueMultiplierSums[hueSumIndex3];
                     scalar13 += this.underlayCounts[hueSumIndex3];
                  }

                  int averageTextureColorIndex;
                  if ((averageTextureColorIndex = loopIndex3 - 5) >= 0 && averageTextureColorIndex < 104) {
                     scalar9 -= this.hueSums[averageTextureColorIndex];
                     scalar10 -= this.saturationSums[averageTextureColorIndex];
                     scalar11 -= this.lightnessSums[averageTextureColorIndex];
                     scalar12 -= this.hueMultiplierSums[averageTextureColorIndex];
                     scalar13 -= this.underlayCounts[averageTextureColorIndex];
                  }

                  if (loopIndex3 > 0
                     && loopIndex3 < 103
                     && (
                        !lowMemory
                           || (this.tileSettings[0][tileLightingIndex2][loopIndex3] & 2) != 0
                           || (this.tileSettings[tileShadowIndex][tileLightingIndex2][loopIndex3] & 16) == 0 && this.getTileHeight(loopIndex3, tileShadowIndex, tileLightingIndex2) == buildPlane
                     )) {
                     if (tileShadowIndex < minimumPlane) {
                        minimumPlane = tileShadowIndex;
                     }

                     int underlayIds3 = this.underlayIds[tileShadowIndex][tileLightingIndex2][loopIndex3] & 255;
                     averageTextureColorIndex = this.overlayIds[tileShadowIndex][tileLightingIndex2][loopIndex3] & 255;
                     if (underlayIds3 > 0 || averageTextureColorIndex > 0) {
                        int tileHeights3 = this.tileHeights[tileShadowIndex][tileLightingIndex2][loopIndex3];
                        scalar = this.tileHeights[tileShadowIndex][tileLightingIndex2 + 1][loopIndex3];
                        hueSumIndex3 = this.tileHeights[tileShadowIndex][tileLightingIndex2 + 1][loopIndex3 + 1];
                        int tileHeights4 = this.tileHeights[tileShadowIndex][tileLightingIndex2][loopIndex3 + 1];
                        int localTileLighting = this.tileLighting[tileLightingIndex2][loopIndex3];
                        int tileLighting2 = this.tileLighting[tileLightingIndex2 + 1][loopIndex3];
                        int tileLighting3 = this.tileLighting[tileLightingIndex2 + 1][loopIndex3 + 1];
                        int tileLighting4 = this.tileLighting[tileLightingIndex2][loopIndex3 + 1];
                        int baseUnderlayHsl = -1;
                        int generateHslBitsetResult = -1;
                        if (underlayIds3 > 0) {
                           int hue = (scalar9 << 8) / scalar12;
                           generateHslBitsetResult = scalar10 / scalar13;
                           int lightness = scalar11 / scalar13;
                           baseUnderlayHsl = generateHslBitset(hue, generateHslBitsetResult, lightness);
                           hue = hue + hueOffset & 0xFF;
                           if ((lightness = lightness + lightnessOffset) < 0) {
                              lightness = 0;
                           } else if (lightness > 255) {
                              lightness = 255;
                           }

                           generateHslBitsetResult = generateHslBitset(hue, generateHslBitsetResult, lightness);
                        }

                        if (tileShadowIndex > 0) {
                           boolean flag = true;
                           if (underlayIds3 == 0 && this.overlayShapes[tileShadowIndex][tileLightingIndex2][loopIndex3] != 0) {
                              flag = false;
                           }

                           if (averageTextureColorIndex > 0 && !FloorDefinition.overlayDefinitions[averageTextureColorIndex - 1].occlude) {
                              flag = false;
                           }

                           if (flag && tileHeights3 == scalar && tileHeights3 == hueSumIndex3 && tileHeights3 == tileHeights4) {
                              this.occlusionFlags[tileShadowIndex][tileLightingIndex2][loopIndex3] = this.occlusionFlags[tileShadowIndex][tileLightingIndex2][loopIndex3] | 2340;
                           }
                        }

                        int localHSL_TO_RGB = 0;
                        if (baseUnderlayHsl != -1) {
                           localHSL_TO_RGB = Rasterizer3D.HSL_TO_RGB[mixUnderlayLightness(generateHslBitsetResult, 96)];
                        }

                        if (averageTextureColorIndex == 0) {
                           sceneGraph.addTile(
                              tileShadowIndex,
                              tileLightingIndex2,
                              loopIndex3,
                              0,
                              0,
                              -1,
                              tileHeights3,
                              scalar,
                              hueSumIndex3,
                              tileHeights4,
                              mixUnderlayLightness(baseUnderlayHsl, localTileLighting),
                              mixUnderlayLightness(baseUnderlayHsl, tileLighting2),
                              mixUnderlayLightness(baseUnderlayHsl, tileLighting3),
                              mixUnderlayLightness(baseUnderlayHsl, tileLighting4),
                              0,
                              0,
                              0,
                              0,
                              localHSL_TO_RGB,
                              0
                           );
                        } else {
                           generateHslBitsetResult = this.overlayShapes[tileShadowIndex][tileLightingIndex2][loopIndex3] + 1;
                           byte byteCode = this.overlayOrientations[tileShadowIndex][tileLightingIndex2][loopIndex3];
                           int overlayTextureColor;
                           FloorDefinition floorDefinition2;
                           if ((averageTextureColorIndex = (floorDefinition2 = FloorDefinition.overlayDefinitions[averageTextureColorIndex - 1]).textureId) >= 0) {
                              overlayTextureColor = Rasterizer3D.getAverageTextureColor(averageTextureColorIndex);
                              underlayIds3 = -1;
                           } else if (floorDefinition2.rgbColor == 16711935) {
                              overlayTextureColor = 0;
                              if (floorDefinition2.secondaryRgbColor != 0) {
                                 overlayTextureColor = Rasterizer3D.HSL_TO_RGB[mixOverlayLightness(floorDefinition2.packedHsl, 96)];
                              }

                              underlayIds3 = -2;
                              averageTextureColorIndex = -1;
                           } else {
                              underlayIds3 = generateHslBitset(floorDefinition2.hue, floorDefinition2.saturation, floorDefinition2.lightness);
                              overlayTextureColor = Rasterizer3D.HSL_TO_RGB[mixOverlayLightness(underlayIds3, 96)];
                           }

                           sceneGraph.addTile(
                              tileShadowIndex,
                              tileLightingIndex2,
                              loopIndex3,
                              generateHslBitsetResult,
                              byteCode,
                              averageTextureColorIndex,
                              tileHeights3,
                              scalar,
                              hueSumIndex3,
                              tileHeights4,
                              mixUnderlayLightness(baseUnderlayHsl, localTileLighting),
                              mixUnderlayLightness(baseUnderlayHsl, tileLighting2),
                              mixUnderlayLightness(baseUnderlayHsl, tileLighting3),
                              mixUnderlayLightness(baseUnderlayHsl, tileLighting4),
                              mixOverlayLightness(underlayIds3, localTileLighting),
                              mixOverlayLightness(underlayIds3, tileLighting2),
                              mixOverlayLightness(underlayIds3, tileLighting3),
                              mixOverlayLightness(underlayIds3, tileLighting4),
                              localHSL_TO_RGB,
                              overlayTextureColor
                           );
                        }
                     }
                  }
               }
            }
         }

         for (int loopIndex4 = 1; loopIndex4 < 103; loopIndex4++) {
            for (int clippingDataIndex2 = 1; clippingDataIndex2 < 103; clippingDataIndex2++) {
               sceneGraph.setDrawLevel(tileShadowIndex, clippingDataIndex2, loopIndex4, this.getTileHeight(loopIndex4, tileShadowIndex, clippingDataIndex2));
            }
         }
      }

      sceneGraph.buildModels(-10, -50, -50, this.tileSettings, values);

      for (int loopIndex5 = 0; loopIndex5 < 104; loopIndex5++) {
         for (int loopIndex6 = 0; loopIndex6 < 104; loopIndex6++) {
            if ((this.tileSettings[1][loopIndex5][loopIndex6] & 2) == 2) {
               sceneGraph.setTileBridge(loopIndex6, loopIndex5);
            }
         }
      }

      byte byteCode2 = 1;
      byte byteCode3 = 2;
      byte byteCode4 = 4;

      for (int occluderIndex = 0; occluderIndex < 4; occluderIndex++) {
         if (occluderIndex > 0) {
            byteCode2 <<= 3;
            byteCode3 <<= 3;
            byteCode4 <<= 3;
         }

         for (int occlusionFlagIndex = 0; occlusionFlagIndex <= occluderIndex; occlusionFlagIndex++) {
            for (int loopIndex7 = 0; loopIndex7 <= 104; loopIndex7++) {
               for (int loopIndex8 = 0; loopIndex8 <= 104; loopIndex8++) {
                  if ((this.occlusionFlags[occlusionFlagIndex][loopIndex8][loopIndex7] & byteCode2) != 0) {
                     int position = loopIndex7;
                     int loopIndex9 = loopIndex7;
                     int tileHeightIndex = occlusionFlagIndex;
                     int sourceOcclusionFlagIndex = occlusionFlagIndex;

                     while (position > 0 && (this.occlusionFlags[occlusionFlagIndex][loopIndex8][position - 1] & byteCode2) != 0) {
                        position--;
                     }

                     while (loopIndex9 < 104 && (this.occlusionFlags[occlusionFlagIndex][loopIndex8][loopIndex9 + 1] & byteCode2) != 0) {
                        loopIndex9++;
                     }

                     expandFirstLowerPlane:
                     while (tileHeightIndex > 0) {
                        for (int loopIndex10 = position; loopIndex10 <= loopIndex9; loopIndex10++) {
                           if ((this.occlusionFlags[tileHeightIndex - 1][loopIndex8][loopIndex10] & byteCode2) == 0) {
                              break expandFirstLowerPlane;
                           }
                        }

                        tileHeightIndex--;
                     }

                     expandFirstUpperPlane:
                     while (sourceOcclusionFlagIndex < occluderIndex) {
                        for (int loopIndex11 = position; loopIndex11 <= loopIndex9; loopIndex11++) {
                           if ((this.occlusionFlags[sourceOcclusionFlagIndex + 1][loopIndex8][loopIndex11] & byteCode2) == 0) {
                              break expandFirstUpperPlane;
                           }
                        }

                        sourceOcclusionFlagIndex++;
                     }

                     if ((sourceOcclusionFlagIndex + 1 - tileHeightIndex) * (loopIndex9 - position + 1) >= 8) {
                        int tileHeights5 = this.tileHeights[sourceOcclusionFlagIndex][loopIndex8][position] - 240;
                        int tileHeights6 = this.tileHeights[tileHeightIndex][loopIndex8][position];
                        SceneGraph.createOccluder(occluderIndex, loopIndex8 << 7, tileHeights6, loopIndex8 << 7, (loopIndex9 << 7) + 128, tileHeights5, position << 7, 1);

                        for (int sourceTileHeightIndex = tileHeightIndex; sourceTileHeightIndex <= sourceOcclusionFlagIndex; sourceTileHeightIndex++) {
                           for (int loopIndex12 = position; loopIndex12 <= loopIndex9; loopIndex12++) {
                              this.occlusionFlags[sourceTileHeightIndex][loopIndex8][loopIndex12] = this.occlusionFlags[sourceTileHeightIndex][loopIndex8][loopIndex12] & ~byteCode2;
                           }
                        }
                     }
                  }

                  if ((this.occlusionFlags[occlusionFlagIndex][loopIndex8][loopIndex7] & byteCode3) != 0) {
                     int position2 = loopIndex8;
                     int loopIndex13 = loopIndex8;
                     int sourceOcclusionFlagIndex2 = occlusionFlagIndex;
                     int sourceOcclusionFlagIndex3 = occlusionFlagIndex;

                     while (position2 > 0 && (this.occlusionFlags[occlusionFlagIndex][position2 - 1][loopIndex7] & byteCode3) != 0) {
                        position2--;
                     }

                     while (loopIndex13 < 104 && (this.occlusionFlags[occlusionFlagIndex][loopIndex13 + 1][loopIndex7] & byteCode3) != 0) {
                        loopIndex13++;
                     }

                     expandSecondLowerPlane:
                     while (sourceOcclusionFlagIndex2 > 0) {
                        for (int loopIndex14 = position2; loopIndex14 <= loopIndex13; loopIndex14++) {
                           if ((this.occlusionFlags[sourceOcclusionFlagIndex2 - 1][loopIndex14][loopIndex7] & byteCode3) == 0) {
                              break expandSecondLowerPlane;
                           }
                        }

                        sourceOcclusionFlagIndex2--;
                     }

                     expandSecondUpperPlane:
                     while (sourceOcclusionFlagIndex3 < occluderIndex) {
                        for (int loopIndex15 = position2; loopIndex15 <= loopIndex13; loopIndex15++) {
                           if ((this.occlusionFlags[sourceOcclusionFlagIndex3 + 1][loopIndex15][loopIndex7] & byteCode3) == 0) {
                              break expandSecondUpperPlane;
                           }
                        }

                        sourceOcclusionFlagIndex3++;
                     }

                     if ((sourceOcclusionFlagIndex3 + 1 - sourceOcclusionFlagIndex2) * (loopIndex13 - position2 + 1) >= 8) {
                        int tileHeights7 = this.tileHeights[sourceOcclusionFlagIndex3][position2][loopIndex7] - 240;
                        int tileHeights8 = this.tileHeights[sourceOcclusionFlagIndex2][position2][loopIndex7];
                        SceneGraph.createOccluder(occluderIndex, position2 << 7, tileHeights8, (loopIndex13 << 7) + 128, loopIndex7 << 7, tileHeights7, loopIndex7 << 7, 2);

                        for (int occlusionFlagIndex2 = sourceOcclusionFlagIndex2; occlusionFlagIndex2 <= sourceOcclusionFlagIndex3; occlusionFlagIndex2++) {
                           for (int loopIndex16 = position2; loopIndex16 <= loopIndex13; loopIndex16++) {
                              this.occlusionFlags[occlusionFlagIndex2][loopIndex16][loopIndex7] = this.occlusionFlags[occlusionFlagIndex2][loopIndex16][loopIndex7] & ~byteCode3;
                           }
                        }
                     }
                  }

                  if ((this.occlusionFlags[occlusionFlagIndex][loopIndex8][loopIndex7] & byteCode4) != 0) {
                     int position3 = loopIndex8;
                     int loopIndex17 = loopIndex8;
                     int position4 = loopIndex7;
                     int loopIndex18 = loopIndex7;

                     while (position4 > 0 && (this.occlusionFlags[occlusionFlagIndex][loopIndex8][position4 - 1] & byteCode4) != 0) {
                        position4--;
                     }

                     while (loopIndex18 < 104 && (this.occlusionFlags[occlusionFlagIndex][loopIndex8][loopIndex18 + 1] & byteCode4) != 0) {
                        loopIndex18++;
                     }

                     expandOccluderWest:
                     while (position3 > 0) {
                        for (int loopIndex19 = position4; loopIndex19 <= loopIndex18; loopIndex19++) {
                           if ((this.occlusionFlags[occlusionFlagIndex][position3 - 1][loopIndex19] & byteCode4) == 0) {
                              break expandOccluderWest;
                           }
                        }

                        position3--;
                     }

                     expandOccluderEast:
                     while (loopIndex17 < 104) {
                        for (int loopIndex20 = position4; loopIndex20 <= loopIndex18; loopIndex20++) {
                           if ((this.occlusionFlags[occlusionFlagIndex][loopIndex17 + 1][loopIndex20] & byteCode4) == 0) {
                              break expandOccluderEast;
                           }
                        }

                        loopIndex17++;
                     }

                     if ((loopIndex17 - position3 + 1) * (loopIndex18 - position4 + 1) >= 4) {
                        int tileHeights9 = this.tileHeights[occlusionFlagIndex][position3][position4];
                        SceneGraph.createOccluder(occluderIndex, position3 << 7, tileHeights9, (loopIndex17 << 7) + 128, (loopIndex18 << 7) + 128, tileHeights9, position4 << 7, 4);

                        for (int loopIndex21 = position3; loopIndex21 <= loopIndex17; loopIndex21++) {
                           for (int loopIndex22 = position4; loopIndex22 <= loopIndex18; loopIndex22++) {
                              this.occlusionFlags[occlusionFlagIndex][loopIndex21][loopIndex22] = this.occlusionFlags[occlusionFlagIndex][loopIndex21][loopIndex22] & ~byteCode4;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   public static void requestObjectModels(Buffer buffer, OnDemandFetcher onDemandFetcher) {
      int scalar = -1;

      int decodedUnsignedSmart;
      while ((decodedUnsignedSmart = buffer.readUnsignedSmart()) != 0) {
         ObjectDefinition.lookup(scalar += decodedUnsignedSmart).loadModels(onDemandFetcher);

         while (buffer.readUnsignedSmart() != 0) {
            buffer.readUnsignedByte();
         }
      }
   }
   public final void clearChunk(int positionArgument, int scalarArgument, int scalarArgument2, int newIndex) {
      for (int loopIndex = positionArgument; loopIndex <= positionArgument + scalarArgument; loopIndex++) {
         for (int loopIndex2 = newIndex; loopIndex2 <= newIndex + scalarArgument2; loopIndex2++) {
            if (loopIndex2 >= 0 && loopIndex2 < 104 && loopIndex >= 0 && loopIndex < 104) {
               this.tileShadows[0][loopIndex2][loopIndex] = 127;
               if (loopIndex2 == newIndex && loopIndex2 > 0) {
                  this.tileHeights[0][loopIndex2][loopIndex] = this.tileHeights[0][loopIndex2 - 1][loopIndex];
               }

               if (loopIndex2 == newIndex + scalarArgument2 && loopIndex2 < 103) {
                  this.tileHeights[0][loopIndex2][loopIndex] = this.tileHeights[0][loopIndex2 + 1][loopIndex];
               }

               if (loopIndex == positionArgument && loopIndex > 0) {
                  this.tileHeights[0][loopIndex2][loopIndex] = this.tileHeights[0][loopIndex2][loopIndex - 1];
               }

               if (loopIndex == positionArgument + scalarArgument && loopIndex < 103) {
                  this.tileHeights[0][loopIndex2][loopIndex] = this.tileHeights[0][loopIndex2][loopIndex + 1];
               }
            }
         }
      }
   }
   private void addObject(int sourceLocalSizeY, SceneGraph sceneGraph, CollisionMap collisionMap, int objectType, int newIndex, int clippingDataIndex, int newDiagonal2DAboveOrigin, int wallFlagIndex) {
      if (lowMemory && (this.tileSettings[0][clippingDataIndex][sourceLocalSizeY] & 2) == 0) {
         if ((this.tileSettings[newIndex][clippingDataIndex][sourceLocalSizeY] & 16) != 0) {
            return;
         }

         if (this.getTileHeight(sourceLocalSizeY, newIndex, clippingDataIndex) != buildPlane) {
            return;
         }
      }

      if (newIndex < minimumPlane) {
         minimumPlane = newIndex;
      }

      ObjectDefinition objectDefinition;
      if ((objectDefinition = ObjectDefinition.lookup(newDiagonal2DAboveOrigin)).animationId != -1) {
         int animationFrameArchiveId = Client.getAnimationFrameArchiveId(objectDefinition.animationId);
         if ((Client.hdModels || !Client.hdModels && Client.extendedRevisionEnabled && (animationFrameArchiveId > 1043 || Client.use2007Models || Client.extendedModelIds.contains(animationFrameArchiveId)))
            && animationFrameArchiveId != -1) {
            try {
               if (AnimationFrame.frameCache.get(animationFrameArchiveId) == null) {
                  if (Client.getClient().onDemandFetcher != null) {
                     Client.getClient().onDemandFetcher.provide(1, animationFrameArchiveId);
                  }
               }
            } catch (Exception exception) {
            }
         }
      }

      int localSizeY;
      int localSizeX;
      if (wallFlagIndex != 1 && wallFlagIndex != 3) {
         localSizeX = objectDefinition.sizeX;
         localSizeY = objectDefinition.sizeY;
      } else {
         localSizeX = objectDefinition.sizeY;
         localSizeY = objectDefinition.sizeX;
      }

      int sourceLocalSizeY2;
      if (104 >= localSizeX + clippingDataIndex) {
         sourceLocalSizeY2 = clippingDataIndex + (localSizeX + 1 >> 1);
         localSizeX = clippingDataIndex + (localSizeX >> 1);
      } else {
         localSizeX = clippingDataIndex;
         sourceLocalSizeY2 = clippingDataIndex + 1;
      }

      int scalar;
      if (104 >= localSizeY + sourceLocalSizeY) {
         scalar = sourceLocalSizeY + (localSizeY + 1 >> 1);
         localSizeY = (localSizeY >> 1) + sourceLocalSizeY;
      } else {
         localSizeY = sourceLocalSizeY;
         scalar = sourceLocalSizeY + 1;
      }

      int localTileHeights = this.tileHeights[newIndex][localSizeX][localSizeY];
      localSizeY = this.tileHeights[newIndex][sourceLocalSizeY2][localSizeY];
      sourceLocalSizeY2 = this.tileHeights[newIndex][sourceLocalSizeY2][scalar];
      localSizeX = this.tileHeights[newIndex][localSizeX][scalar];
      scalar = localTileHeights + localSizeY + sourceLocalSizeY2 + localSizeX >> 2;
      int scalar2 = clippingDataIndex + (sourceLocalSizeY << 7) + (newDiagonal2DAboveOrigin << 14) + 1073741824;
      if (!objectDefinition.hasActions) {
         scalar2 -= Integer.MIN_VALUE;
      }

      byte byteCode = (byte)((wallFlagIndex << 6) + objectType);
      if (objectType == 22) {
         if (!lowMemory || objectDefinition.hasActions || objectDefinition.obstructsGround) {
            Renderable renderable;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable = objectDefinition.modelAt(22, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, 22, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addGroundDecoration(newIndex, scalar, sourceLocalSizeY, renderable, byteCode, scalar2, clippingDataIndex);
            if (objectDefinition.solid && objectDefinition.hasActions && collisionMap != null) {
               collisionMap.setBlocked(sourceLocalSizeY, clippingDataIndex);
               return;
            }
         }
      } else if (objectType != 10 && objectType != 11) {
         if (objectType >= 12) {
            Renderable renderable14;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable14 = objectDefinition.modelAt(objectType, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable14 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, objectType, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addEntity(scalar2, byteCode, scalar, 1, renderable14, 1, newIndex, 0, sourceLocalSizeY, clippingDataIndex);
            if (objectType >= 12 && objectType <= 17 && objectType != 13 && newIndex > 0) {
               this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 2340;
            }

            if (objectDefinition.solid && collisionMap != null) {
               collisionMap.addObject(objectDefinition.walkable, objectDefinition.sizeX, objectDefinition.sizeY, clippingDataIndex, sourceLocalSizeY, wallFlagIndex);
               return;
            }
         } else if (objectType == 0) {
            Renderable renderable2;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable2 = objectDefinition.modelAt(0, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable2 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, 0, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(wallFlags[wallFlagIndex], renderable2, scalar2, sourceLocalSizeY, byteCode, clippingDataIndex, null, scalar, 0, newIndex);
            if (wallFlagIndex == 0) {
               if (objectDefinition.castsShadow) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY] = 50;
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = 50;
               }

               if (objectDefinition.wall) {
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 585;
               }
            } else if (wallFlagIndex == 1) {
               if (objectDefinition.castsShadow) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = 50;
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY + 1] = 50;
               }

               if (objectDefinition.wall) {
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY + 1] | 1170;
               }
            } else if (wallFlagIndex == 2) {
               if (objectDefinition.castsShadow) {
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = 50;
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY + 1] = 50;
               }

               if (objectDefinition.wall) {
                  this.occlusionFlags[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex + 1][sourceLocalSizeY] | 585;
               }
            } else if (wallFlagIndex == 3) {
               if (objectDefinition.castsShadow) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY] = 50;
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = 50;
               }

               if (objectDefinition.wall) {
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 1170;
               }
            }

            if (objectDefinition.solid && collisionMap != null) {
               collisionMap.addWall(sourceLocalSizeY, wallFlagIndex, clippingDataIndex, objectType, objectDefinition.walkable);
            }

            if (objectDefinition.decorDisplacement != 16) {
               sceneGraph.setWallDecorationOffset(sourceLocalSizeY, objectDefinition.decorDisplacement, clippingDataIndex, newIndex);
               return;
            }
         } else if (objectType == 1) {
            Renderable renderable3;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable3 = objectDefinition.modelAt(1, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable3 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, 1, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(diagonalWallFlags[wallFlagIndex], renderable3, scalar2, sourceLocalSizeY, byteCode, clippingDataIndex, null, scalar, 0, newIndex);
            if (objectDefinition.castsShadow) {
               if (wallFlagIndex == 0) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = 50;
               } else if (wallFlagIndex == 1) {
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY + 1] = 50;
               } else if (wallFlagIndex == 2) {
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = 50;
               } else if (wallFlagIndex == 3) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY] = 50;
               }
            }

            if (objectDefinition.solid && collisionMap != null) {
               collisionMap.addWall(sourceLocalSizeY, wallFlagIndex, clippingDataIndex, objectType, objectDefinition.walkable);
               return;
            }
         } else if (objectType == 2) {
            int wallFlagIndex2 = wallFlagIndex + 1 & 3;
            Renderable renderable4;
            Renderable renderable5;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable4 = objectDefinition.modelAt(2, wallFlagIndex + 4, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               renderable5 = objectDefinition.modelAt(2, wallFlagIndex2, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable4 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex + 4, 2, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
               renderable5 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex2, 2, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(wallFlags[wallFlagIndex], renderable4, scalar2, sourceLocalSizeY, byteCode, clippingDataIndex, renderable5, scalar, wallFlags[wallFlagIndex2], newIndex);
            if (objectDefinition.wall) {
               if (wallFlagIndex == 0) {
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 585;
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY + 1] | 1170;
               } else if (wallFlagIndex == 1) {
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY + 1] | 1170;
                  this.occlusionFlags[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex + 1][sourceLocalSizeY] | 585;
               } else if (wallFlagIndex == 2) {
                  this.occlusionFlags[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex + 1][sourceLocalSizeY] | 585;
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 1170;
               } else if (wallFlagIndex == 3) {
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 1170;
                  this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] = this.occlusionFlags[newIndex][clippingDataIndex][sourceLocalSizeY] | 585;
               }
            }

            if (objectDefinition.solid && collisionMap != null) {
               collisionMap.addWall(sourceLocalSizeY, wallFlagIndex, clippingDataIndex, objectType, objectDefinition.walkable);
            }

            if (objectDefinition.decorDisplacement != 16) {
               sceneGraph.setWallDecorationOffset(sourceLocalSizeY, objectDefinition.decorDisplacement, clippingDataIndex, newIndex);
               return;
            }
         } else if (objectType == 3) {
            Renderable renderable6;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable6 = objectDefinition.modelAt(3, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable6 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, 3, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(diagonalWallFlags[wallFlagIndex], renderable6, scalar2, sourceLocalSizeY, byteCode, clippingDataIndex, null, scalar, 0, newIndex);
            if (objectDefinition.castsShadow) {
               if (wallFlagIndex == 0) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY + 1] = 50;
               } else if (wallFlagIndex == 1) {
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY + 1] = 50;
               } else if (wallFlagIndex == 2) {
                  this.tileShadows[newIndex][clippingDataIndex + 1][sourceLocalSizeY] = 50;
               } else if (wallFlagIndex == 3) {
                  this.tileShadows[newIndex][clippingDataIndex][sourceLocalSizeY] = 50;
               }
            }

            if (objectDefinition.solid && collisionMap != null) {
               collisionMap.addWall(sourceLocalSizeY, wallFlagIndex, clippingDataIndex, objectType, objectDefinition.walkable);
               return;
            }
         } else if (objectType == 9) {
            Renderable renderable7;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable7 = objectDefinition.modelAt(objectType, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
            } else {
               renderable7 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, objectType, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
            }

            sceneGraph.addEntity(scalar2, byteCode, scalar, 1, renderable7, 1, newIndex, 0, sourceLocalSizeY, clippingDataIndex);
            if (objectDefinition.solid && collisionMap != null) {
               collisionMap.addObject(objectDefinition.walkable, objectDefinition.sizeX, objectDefinition.sizeY, clippingDataIndex, sourceLocalSizeY, wallFlagIndex);
               return;
            }
         } else {
            if (objectDefinition.adjustToTerrain) {
               if (wallFlagIndex == 1) {
                  int sourceLocalSizeX = localSizeX;
                  localSizeX = sourceLocalSizeY2;
                  sourceLocalSizeY2 = localSizeY;
                  localSizeY = localTileHeights;
                  localTileHeights = sourceLocalSizeX;
               } else if (wallFlagIndex == 2) {
                  int sourceLocalSizeX2 = localSizeX;
                  localSizeX = localSizeY;
                  localSizeY = sourceLocalSizeX2;
                  sourceLocalSizeX2 = sourceLocalSizeY2;
                  sourceLocalSizeY2 = localTileHeights;
                  localTileHeights = sourceLocalSizeX2;
               } else if (wallFlagIndex == 3) {
                  int sourceLocalSizeX3 = localSizeX;
                  localSizeX = localTileHeights;
                  localTileHeights = localSizeY;
                  localSizeY = sourceLocalSizeY2;
                  sourceLocalSizeY2 = sourceLocalSizeX3;
               }
            }

            if (objectType == 4) {
               Renderable renderable8;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable8 = objectDefinition.modelAt(4, 0, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               } else {
                  renderable8 = new DynamicObject(newDiagonal2DAboveOrigin, 0, 4, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar2, sourceLocalSizeY, wallFlagIndex << 9, newIndex, 0, scalar, renderable8, clippingDataIndex, byteCode, 0, wallFlags[wallFlagIndex]);
               return;
            }

            if (objectType == 5) {
               int localLookup = 16;
               int wallHash;
               if ((wallHash = sceneGraph.getWallHash(newIndex, clippingDataIndex, sourceLocalSizeY)) > 0) {
                  localLookup = ObjectDefinition.lookup(wallHash >> 14 & 32767).decorDisplacement;
               }

               Renderable renderable9;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable9 = objectDefinition.modelAt(4, 0, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               } else {
                  renderable9 = new DynamicObject(newDiagonal2DAboveOrigin, 0, 4, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar2, sourceLocalSizeY, wallFlagIndex << 9, newIndex, wallDecorationXOffsets[wallFlagIndex] * localLookup, scalar, renderable9, clippingDataIndex, byteCode, wallDecorationYOffsets[wallFlagIndex] * localLookup, wallFlags[wallFlagIndex]);
               return;
            }

            if (objectType == 6) {
               Renderable renderable10;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable10 = objectDefinition.modelAt(4, 0, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               } else {
                  renderable10 = new DynamicObject(newDiagonal2DAboveOrigin, 0, 4, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar2, sourceLocalSizeY, wallFlagIndex, newIndex, 0, scalar, renderable10, clippingDataIndex, byteCode, 0, 256);
               return;
            }

            if (objectType == 7) {
               Renderable renderable11;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable11 = objectDefinition.modelAt(4, 0, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               } else {
                  renderable11 = new DynamicObject(newDiagonal2DAboveOrigin, 0, 4, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar2, sourceLocalSizeY, wallFlagIndex, newIndex, 0, scalar, renderable11, clippingDataIndex, byteCode, 0, 512);
               return;
            }

            if (objectType == 8) {
               Renderable renderable12;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable12 = objectDefinition.modelAt(4, 0, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               } else {
                  renderable12 = new DynamicObject(newDiagonal2DAboveOrigin, 0, 4, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar2, sourceLocalSizeY, wallFlagIndex, newIndex, 0, scalar, renderable12, clippingDataIndex, byteCode, 0, 768);
               return;
            }
         }
      } else {
         Renderable renderable13;
         if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
            renderable13 = objectDefinition.modelAt(10, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
         } else {
            renderable13 = new DynamicObject(newDiagonal2DAboveOrigin, wallFlagIndex, 10, localSizeY, sourceLocalSizeY2, localTileHeights, localSizeX, objectDefinition.animationId, true);
         }

         if (renderable13 != null) {
            short shortCode = 0;
            if (objectType == 11) {
               shortCode += 256;
            }

            int sizeX2;
            int sizeY2;
            if (wallFlagIndex != 1 && wallFlagIndex != 3) {
               sizeX2 = objectDefinition.sizeX;
               sizeY2 = objectDefinition.sizeY;
            } else {
               sizeX2 = objectDefinition.sizeY;
               sizeY2 = objectDefinition.sizeX;
            }

            if (sceneGraph.addEntity(scalar2, byteCode, scalar, sizeY2, renderable13, sizeX2, newIndex, shortCode, sourceLocalSizeY, clippingDataIndex) && objectDefinition.castsShadow) {
               Model model;
               if (renderable13 instanceof Model) {
                  model = (Model)renderable13;
               } else {
                  model = objectDefinition.modelAt(10, wallFlagIndex, localTileHeights, localSizeY, sourceLocalSizeY2, localSizeX, -1, -1, -1, -1);
               }

               if (model != null) {
                  for (int loopIndex = 0; loopIndex <= sizeX2; loopIndex++) {
                     for (int loopIndex2 = 0; loopIndex2 <= sizeY2; loopIndex2++) {
                        if ((newDiagonal2DAboveOrigin = model.diagonal2DAboveOrigin / 4) > 30) {
                           newDiagonal2DAboveOrigin = 30;
                        }

                        if (newDiagonal2DAboveOrigin > this.tileShadows[newIndex][clippingDataIndex + loopIndex][sourceLocalSizeY + loopIndex2]) {
                           this.tileShadows[newIndex][clippingDataIndex + loopIndex][sourceLocalSizeY + loopIndex2] = (byte)newDiagonal2DAboveOrigin;
                        }
                     }
                  }
               }
            }
         }

         if (objectDefinition.solid && collisionMap != null) {
            collisionMap.addObject(objectDefinition.walkable, objectDefinition.sizeX, objectDefinition.sizeY, clippingDataIndex, sourceLocalSizeY, wallFlagIndex);
         }
      }
   }
   private static int interpolatedNoise(int newRotateObjectCoordinate, int heightY, int scalarArgument) {
      int smoothNoiseResult = newRotateObjectCoordinate / scalarArgument;
      newRotateObjectCoordinate &= scalarArgument - 1;
      int cosineInterpolateResult = heightY / scalarArgument;
      heightY &= scalarArgument - 1;
      int southWestNoise = smoothNoise(smoothNoiseResult, cosineInterpolateResult);
      int southEastNoise = smoothNoise(smoothNoiseResult + 1, cosineInterpolateResult);
      int northWestNoise = smoothNoise(smoothNoiseResult, cosineInterpolateResult + 1);
      smoothNoiseResult = smoothNoise(smoothNoiseResult + 1, cosineInterpolateResult + 1);
      cosineInterpolateResult = cosineInterpolate(southWestNoise, southEastNoise, newRotateObjectCoordinate, scalarArgument);
      newRotateObjectCoordinate = cosineInterpolate(northWestNoise, smoothNoiseResult, newRotateObjectCoordinate, scalarArgument);
      return cosineInterpolate(cosineInterpolateResult, newRotateObjectCoordinate, heightY, scalarArgument);
   }
   private static int generateHslBitset(int hue, int saturation, int lightness) {
      if (lightness > 179) {
         saturation /= 2;
      }

      if (lightness > 192) {
         saturation /= 2;
      }

      if (lightness > 217) {
         saturation /= 2;
      }

      if (lightness > 243) {
         saturation /= 2;
      }

      return (hue / 4 << 10) + (saturation / 32 << 7) + lightness / 2;
   }
   public static boolean isObjectTypeReady(int scalarArgument, int scalarArgument2) {
      ObjectDefinition objectDefinition = ObjectDefinition.lookup(scalarArgument);
      if (scalarArgument2 == 11) {
         scalarArgument2 = 10;
      }

      if (scalarArgument2 >= 5 && scalarArgument2 <= 8) {
         scalarArgument2 = 4;
      }

      return objectDefinition.areModelsReadyForType(scalarArgument2);
   }
   public final void loadTerrainChunk(int scalarArgument, int scalarArgument2, CollisionMap[] values, int scalarArgument3, int scalarArgument4, byte[] terrainRegionDataEntry, int scalarArgument5, int tileSettingIndex, int scalarArgument6) {
      for (int loopIndex = 0; loopIndex < 8; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < 8; loopIndex2++) {
            if (scalarArgument3 + loopIndex > 0 && scalarArgument3 + loopIndex < 103 && scalarArgument6 + loopIndex2 > 0 && scalarArgument6 + loopIndex2 < 103) {
               values[tileSettingIndex].clippingData[scalarArgument3 + loopIndex][scalarArgument6 + loopIndex2] = values[tileSettingIndex].clippingData[scalarArgument3 + loopIndex][scalarArgument6 + loopIndex2] & -16777217;
            }
         }
      }

      Buffer buffer = new Buffer(terrainRegionDataEntry);

      for (int loopIndex3 = 0; loopIndex3 < 4; loopIndex3++) {
         for (int loopIndex4 = 0; loopIndex4 < 64; loopIndex4++) {
            for (int loopIndex5 = 0; loopIndex5 < 64; loopIndex5++) {
               if (loopIndex3 == scalarArgument && loopIndex4 >= scalarArgument4 && loopIndex4 < scalarArgument4 + 8 && loopIndex5 >= scalarArgument5 && loopIndex5 < scalarArgument5 + 8) {
                  int scalar = loopIndex5 & 7;
                  int scalar2 = loopIndex4 & 7;
                  int scalar3 = scalarArgument2;
                  int scalar4 = scalar;
                  int scalar5;
                  int scalar6 = scalarArgument6 + ((scalar5 = scalar3 & 3) == 0 ? scalar4 : (scalar5 == 1 ? 7 - scalar2 : (scalar5 == 2 ? 7 - scalar4 : scalar2)));
                  int scalar7 = loopIndex5 & 7;
                  scalar2 = loopIndex4 & 7;
                  scalar3 = scalar7;
                  this.decodeTile(
                     scalar6, 0, buffer, scalarArgument3 + ((scalar4 = scalarArgument2 & 3) == 0 ? scalar2 : (scalar4 == 1 ? scalar3 : (scalar4 == 2 ? 7 - scalar2 : 7 - scalar3))), tileSettingIndex, scalarArgument2, 0
                  );
               } else {
                  this.decodeTile(-1, 0, buffer, -1, 0, 0, 0);
               }
            }
         }
      }
   }
   public final void loadTerrainRegion(byte[] byteBufferArgument, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4, CollisionMap[] values) {
      for (int loopIndex = 0; loopIndex < 4; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < 64; loopIndex2++) {
            for (int loopIndex3 = 0; loopIndex3 < 64; loopIndex3++) {
               if (scalarArgument2 + loopIndex2 > 0 && scalarArgument2 + loopIndex2 < 103 && scalarArgument + loopIndex3 > 0 && scalarArgument + loopIndex3 < 103) {
                  values[loopIndex].clippingData[scalarArgument2 + loopIndex2][scalarArgument + loopIndex3] = values[loopIndex].clippingData[scalarArgument2 + loopIndex2][scalarArgument + loopIndex3] & -16777217;
               }
            }
         }
      }

      Buffer buffer = new Buffer(byteBufferArgument);

      for (int tileSettingIndex = 0; tileSettingIndex < 4; tileSettingIndex++) {
         for (int loopIndex4 = 0; loopIndex4 < 64; loopIndex4++) {
            for (int loopIndex5 = 0; loopIndex5 < 64; loopIndex5++) {
               this.decodeTile(loopIndex5 + scalarArgument, scalarArgument4, buffer, loopIndex4 + scalarArgument2, tileSettingIndex, 0, scalarArgument3);
            }
         }
      }
   }
   private void decodeTile(int positionArgument, int newReadUnsignedByte, Buffer buffer, int newIndex, int tileSettingIndex, int scalarArgument, int scalarArgument2) {
      if (newIndex >= 0 && newIndex < 104 && positionArgument >= 0 && positionArgument < 104) {
         this.tileSettings[tileSettingIndex][newIndex][positionArgument] = 0;

         int decodedUnsignedByte;
         while ((decodedUnsignedByte = buffer.readUnsignedByte()) != 0) {
            if (decodedUnsignedByte == 1) {
               if ((newReadUnsignedByte = buffer.readUnsignedByte()) == 1) {
                  newReadUnsignedByte = 0;
               }

               if (tileSettingIndex == 0) {
                  this.tileHeights[0][newIndex][positionArgument] = -newReadUnsignedByte << 3;
                  return;
               }

               this.tileHeights[tileSettingIndex][newIndex][positionArgument] = this.tileHeights[tileSettingIndex - 1][newIndex][positionArgument] - (newReadUnsignedByte << 3);
               return;
            }

            if (decodedUnsignedByte <= 49) {
               this.overlayIds[tileSettingIndex][newIndex][positionArgument] = buffer.readByte();
               this.overlayShapes[tileSettingIndex][newIndex][positionArgument] = (byte)((decodedUnsignedByte - 2) / 4);
               this.overlayOrientations[tileSettingIndex][newIndex][positionArgument] = (byte)(decodedUnsignedByte - 2 + scalarArgument & 3);
            } else if (decodedUnsignedByte <= 81) {
               this.tileSettings[tileSettingIndex][newIndex][positionArgument] = (byte)(decodedUnsignedByte - 49);
            } else {
               this.underlayIds[tileSettingIndex][newIndex][positionArgument] = (byte)(decodedUnsignedByte - 81);
            }
         }

         if (tileSettingIndex == 0) {
            int heightX = newIndex + 932731 + scalarArgument2;
            int heightY = positionArgument + 556238 + newReadUnsignedByte;
            int height = (int)(
                  (
                        interpolatedNoise(heightX + 45365, heightY + 91923, 4)
                           - 128
                           + (interpolatedNoise(heightX + 10294, heightY + 37821, 2) - 128 >> 1)
                           + (interpolatedNoise(heightX, heightY, 1) - 128 >> 2)
                     )
                     * 0.3
               )
               + 35;
            if (height < 10) {
               height = 10;
            } else if (height > 60) {
               height = 60;
            }

            this.tileHeights[0][newIndex][positionArgument] = -height << 3;
         } else {
            this.tileHeights[tileSettingIndex][newIndex][positionArgument] = this.tileHeights[tileSettingIndex - 1][newIndex][positionArgument] - 240;
         }
      } else {
         int readUnsignedByte2;
         while ((readUnsignedByte2 = buffer.readUnsignedByte()) != 0) {
            if (readUnsignedByte2 == 1) {
               buffer.readUnsignedByte();
               return;
            }

            if (readUnsignedByte2 <= 49) {
               buffer.readUnsignedByte();
            }
         }
      }
   }
   private int getTileHeight(int scalarArgument, int tileSettingIndex, int clippingDataIndex) {
      if ((this.tileSettings[tileSettingIndex][clippingDataIndex][scalarArgument] & 8) != 0) {
         return 0;
      } else {
         return tileSettingIndex > 0 && (this.tileSettings[1][clippingDataIndex][scalarArgument] & 2) != 0 ? tileSettingIndex - 1 : tileSettingIndex;
      }
   }
   public final void loadObjectChunk(CollisionMap[] values, SceneGraph sceneGraph, int scalarArgument, int scalarArgument2, int scalarArgument3, int scalarArgument4, byte[] objectRegionDataEntry, int scalarArgument5, int scalarArgument6, int scalarArgument7) {
      Buffer buffer = new Buffer(objectRegionDataEntry);
      int scalar = -1;

      int decodedUnsignedSmart;
      while ((decodedUnsignedSmart = buffer.readUnsignedSmart()) != 0) {
         scalar += decodedUnsignedSmart;
         decodedUnsignedSmart = 0;

         int readUnsignedSmart2;
         while ((readUnsignedSmart2 = buffer.readUnsignedSmart()) != 0) {
            readUnsignedSmart2 = (decodedUnsignedSmart += readUnsignedSmart2 - 1) & 63;
            int scalar2 = decodedUnsignedSmart >> 6 & 63;
            int scalar3 = decodedUnsignedSmart >> 12;
            int decodedUnsignedByte;
            int objectType = (decodedUnsignedByte = buffer.readUnsignedByte()) >> 2;
            decodedUnsignedByte &= 3;
            if (scalar3 == scalarArgument && scalar2 >= scalarArgument5 && scalar2 < scalarArgument5 + 8 && readUnsignedSmart2 >= scalarArgument3 && readUnsignedSmart2 < scalarArgument3 + 8) {
               ObjectDefinition objectDefinition = ObjectDefinition.lookup(scalar);
               int scalar4 = scalar2 & 7;
               int scalar5 = readUnsignedSmart2 & 7;
               int sizeX = objectDefinition.sizeX;
               int sizeX2 = scalar5;
               int scalar6 = scalar4;
               int sizeY = objectDefinition.sizeY;
               int scalar7;
               int clippingDataIndex = scalarArgument2 + ((scalar7 = scalarArgument6 & 3) == 0 ? scalar6 : (scalar7 == 1 ? sizeX2 : (scalar7 == 2 ? 7 - scalar6 - (sizeX - 1) : 7 - sizeX2 - (sizeY - 1))));
               int scalar8 = readUnsignedSmart2 & 7;
               sizeX = scalar2 & 7;
               sizeX2 = objectDefinition.sizeX;
               scalar6 = scalarArgument6;
               sizeY = objectDefinition.sizeY;
               scalar7 = scalar8;
               int scalar9;
               readUnsignedSmart2 = scalarArgument7 + ((scalar9 = scalar6 & 3) == 0 ? scalar7 : (scalar9 == 1 ? 7 - sizeX - (sizeX2 - 1) : (scalar9 == 2 ? 7 - scalar7 - (sizeY - 1) : sizeX)));
               if (clippingDataIndex > 0 && readUnsignedSmart2 > 0 && clippingDataIndex < 103 && readUnsignedSmart2 < 103) {
                  scalar2 = scalar3;
                  if ((this.tileSettings[1][clippingDataIndex][readUnsignedSmart2] & 2) == 2) {
                     scalar2 = scalar3 - 1;
                  }

                  CollisionMap collisionMap = null;
                  if (scalar2 >= 0) {
                     collisionMap = values[scalar2];
                  }

                  this.addObject(readUnsignedSmart2, sceneGraph, collisionMap, objectType, scalarArgument4, clippingDataIndex, scalar, decodedUnsignedByte + scalarArgument6 & 3);
               }
            }
         }
      }
   }
   private static int cosineInterpolate(int scalarArgument, int southEastNoise, int heightY, int scalarArgument2) {
      heightY = 65536 - Rasterizer3D.COSINE[(heightY << 10) / scalarArgument2] >> 1;
      return (scalarArgument * (65536 - heightY) >> 16) + (southEastNoise * heightY >> 16);
   }
   private static int mixOverlayLightness(int packedHsl, int scalarArgument) {
      if (packedHsl == -2) {
         return 12345678;
      }

      if (packedHsl == -1) {
         if (scalarArgument < 0) {
            scalarArgument = 0;
         } else if (scalarArgument > 127) {
            scalarArgument = 127;
         }

         int scalar;
         return scalar = 127 - scalarArgument;
      } else {
         if ((scalarArgument = scalarArgument * (packedHsl & 127) / 128) < 2) {
            scalarArgument = 2;
         } else if (scalarArgument > 126) {
            scalarArgument = 126;
         }

         return (packedHsl & 65408) + scalarArgument;
      }
   }
   private static int smoothNoise(int newAdjustLightness, int scalarArgument) {
      int localAdjustLightness = pseudoRandomNoise(newAdjustLightness - 1, scalarArgument - 1) + pseudoRandomNoise(newAdjustLightness + 1, scalarArgument - 1) + pseudoRandomNoise(newAdjustLightness - 1, scalarArgument + 1) + pseudoRandomNoise(newAdjustLightness + 1, scalarArgument + 1);
      int adjustLightness2 = pseudoRandomNoise(newAdjustLightness - 1, scalarArgument) + pseudoRandomNoise(newAdjustLightness + 1, scalarArgument) + pseudoRandomNoise(newAdjustLightness, scalarArgument - 1) + pseudoRandomNoise(newAdjustLightness, scalarArgument + 1);
      newAdjustLightness = pseudoRandomNoise(newAdjustLightness, scalarArgument);
      return localAdjustLightness / 16 + adjustLightness2 / 8 + newAdjustLightness / 4;
   }
   private static int mixUnderlayLightness(int baseUnderlayHsl, int scalarArgument) {
      if (baseUnderlayHsl == -1) {
         return 12345678;
      }

      if ((scalarArgument = scalarArgument * (baseUnderlayHsl & 127) / 128) < 2) {
         scalarArgument = 2;
      } else if (scalarArgument > 126) {
         scalarArgument = 126;
      }

      return (baseUnderlayHsl & 65408) + scalarArgument;
   }
   public static void addObjectStatic(SceneGraph sceneGraph, int wallFlagIndex2, int newIndex, int objectType, int positionArgument, CollisionMap collisionMap, int[][][] values, int clippingDataIndex, int newSizeX, int tileIndex) {
      int scalar = values[positionArgument][clippingDataIndex][newIndex];
      int scalar2 = values[positionArgument][clippingDataIndex + 1][newIndex];
      int scalar3 = values[positionArgument][clippingDataIndex + 1][newIndex + 1];
      positionArgument = values[positionArgument][clippingDataIndex][newIndex + 1];
      int scalar4 = scalar + scalar2 + scalar3 + positionArgument >> 2;
      ObjectDefinition objectDefinition = ObjectDefinition.lookup(newSizeX);
      int scalar5 = clippingDataIndex + (newIndex << 7) + (newSizeX << 14) + 1073741824;
      if (!objectDefinition.hasActions) {
         scalar5 -= Integer.MIN_VALUE;
      }

      byte byteCode = (byte)((wallFlagIndex2 << 6) + objectType);
      if (objectType == 22) {
         Renderable renderable;
         if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
            renderable = objectDefinition.modelAt(22, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
         } else {
            renderable = new DynamicObject(newSizeX, wallFlagIndex2, 22, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
         }

         sceneGraph.addGroundDecoration(tileIndex, scalar4, newIndex, renderable, byteCode, scalar5, clippingDataIndex);
         if (objectDefinition.solid && objectDefinition.hasActions) {
            collisionMap.setBlocked(newIndex, clippingDataIndex);
            return;
         }
      } else if (objectType != 10 && objectType != 11) {
         if (objectType >= 12) {
            Renderable renderable14;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable14 = objectDefinition.modelAt(objectType, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
            } else {
               renderable14 = new DynamicObject(newSizeX, wallFlagIndex2, objectType, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
            }

            sceneGraph.addEntity(scalar5, byteCode, scalar4, 1, renderable14, 1, tileIndex, 0, newIndex, clippingDataIndex);
            if (objectDefinition.solid) {
               collisionMap.addObject(objectDefinition.walkable, objectDefinition.sizeX, objectDefinition.sizeY, clippingDataIndex, newIndex, wallFlagIndex2);
               return;
            }
         } else if (objectType == 0) {
            Renderable renderable2;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable2 = objectDefinition.modelAt(0, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
            } else {
               renderable2 = new DynamicObject(newSizeX, wallFlagIndex2, 0, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(wallFlags[wallFlagIndex2], renderable2, scalar5, newIndex, byteCode, clippingDataIndex, null, scalar4, 0, tileIndex);
            if (objectDefinition.solid) {
               collisionMap.addWall(newIndex, wallFlagIndex2, clippingDataIndex, objectType, objectDefinition.walkable);
               return;
            }
         } else if (objectType == 1) {
            Renderable renderable3;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable3 = objectDefinition.modelAt(1, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
            } else {
               renderable3 = new DynamicObject(newSizeX, wallFlagIndex2, 1, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(diagonalWallFlags[wallFlagIndex2], renderable3, scalar5, newIndex, byteCode, clippingDataIndex, null, scalar4, 0, tileIndex);
            if (objectDefinition.solid) {
               collisionMap.addWall(newIndex, wallFlagIndex2, clippingDataIndex, objectType, objectDefinition.walkable);
               return;
            }
         } else if (objectType == 2) {
            int wallFlagIndex = wallFlagIndex2 + 1 & 3;
            Renderable renderable4;
            Renderable renderable5;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable5 = objectDefinition.modelAt(2, wallFlagIndex2 + 4, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
               renderable4 = objectDefinition.modelAt(2, wallFlagIndex, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
            } else {
               renderable5 = new DynamicObject(newSizeX, wallFlagIndex2 + 4, 2, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
               renderable4 = new DynamicObject(newSizeX, wallFlagIndex, 2, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(wallFlags[wallFlagIndex2], renderable5, scalar5, newIndex, byteCode, clippingDataIndex, renderable4, scalar4, wallFlags[wallFlagIndex], tileIndex);
            if (objectDefinition.solid) {
               collisionMap.addWall(newIndex, wallFlagIndex2, clippingDataIndex, objectType, objectDefinition.walkable);
               return;
            }
         } else if (objectType == 3) {
            Renderable renderable6;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable6 = objectDefinition.modelAt(3, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
            } else {
               renderable6 = new DynamicObject(newSizeX, wallFlagIndex2, 3, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
            }

            sceneGraph.addWall(diagonalWallFlags[wallFlagIndex2], renderable6, scalar5, newIndex, byteCode, clippingDataIndex, null, scalar4, 0, tileIndex);
            if (objectDefinition.solid) {
               collisionMap.addWall(newIndex, wallFlagIndex2, clippingDataIndex, objectType, objectDefinition.walkable);
               return;
            }
         } else if (objectType == 9) {
            Renderable renderable7;
            if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
               renderable7 = objectDefinition.modelAt(objectType, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
            } else {
               renderable7 = new DynamicObject(newSizeX, wallFlagIndex2, objectType, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
            }

            sceneGraph.addEntity(scalar5, byteCode, scalar4, 1, renderable7, 1, tileIndex, 0, newIndex, clippingDataIndex);
            if (objectDefinition.solid) {
               collisionMap.addObject(objectDefinition.walkable, objectDefinition.sizeX, objectDefinition.sizeY, clippingDataIndex, newIndex, wallFlagIndex2);
               return;
            }
         } else {
            if (objectDefinition.adjustToTerrain) {
               if (wallFlagIndex2 == 1) {
                  int scalar6 = positionArgument;
                  positionArgument = scalar3;
                  scalar3 = scalar2;
                  scalar2 = scalar;
                  scalar = scalar6;
               } else if (wallFlagIndex2 == 2) {
                  int scalar7 = positionArgument;
                  positionArgument = scalar2;
                  scalar2 = scalar7;
                  scalar7 = scalar3;
                  scalar3 = scalar;
                  scalar = scalar7;
               } else if (wallFlagIndex2 == 3) {
                  int scalar8 = positionArgument;
                  positionArgument = scalar;
                  scalar = scalar2;
                  scalar2 = scalar3;
                  scalar3 = scalar8;
               }
            }

            if (objectType == 4) {
               Renderable renderable8;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable8 = objectDefinition.modelAt(4, 0, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
               } else {
                  renderable8 = new DynamicObject(newSizeX, 0, 4, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar5, newIndex, wallFlagIndex2 << 9, tileIndex, 0, scalar4, renderable8, clippingDataIndex, byteCode, 0, wallFlags[wallFlagIndex2]);
               return;
            }

            if (objectType == 5) {
               int localLookup = 16;
               int wallHash;
               if ((wallHash = sceneGraph.getWallHash(tileIndex, clippingDataIndex, newIndex)) > 0) {
                  localLookup = ObjectDefinition.lookup(wallHash >> 14 & 32767).decorDisplacement;
               }

               Renderable renderable9;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable9 = objectDefinition.modelAt(4, 0, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
               } else {
                  renderable9 = new DynamicObject(newSizeX, 0, 4, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar5, newIndex, wallFlagIndex2 << 9, tileIndex, wallDecorationXOffsets[wallFlagIndex2] * localLookup, scalar4, renderable9, clippingDataIndex, byteCode, wallDecorationYOffsets[wallFlagIndex2] * localLookup, wallFlags[wallFlagIndex2]);
               return;
            }

            if (objectType == 6) {
               Renderable renderable10;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable10 = objectDefinition.modelAt(4, 0, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
               } else {
                  renderable10 = new DynamicObject(newSizeX, 0, 4, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar5, newIndex, wallFlagIndex2, tileIndex, 0, scalar4, renderable10, clippingDataIndex, byteCode, 0, 256);
               return;
            }

            if (objectType == 7) {
               Renderable renderable11;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable11 = objectDefinition.modelAt(4, 0, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
               } else {
                  renderable11 = new DynamicObject(newSizeX, 0, 4, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar5, newIndex, wallFlagIndex2, tileIndex, 0, scalar4, renderable11, clippingDataIndex, byteCode, 0, 512);
               return;
            }

            if (objectType == 8) {
               Renderable renderable12;
               if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
                  renderable12 = objectDefinition.modelAt(4, 0, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
               } else {
                  renderable12 = new DynamicObject(newSizeX, 0, 4, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
               }

               sceneGraph.addWallDecoration(scalar5, newIndex, wallFlagIndex2, tileIndex, 0, scalar4, renderable12, clippingDataIndex, byteCode, 0, 768);
               return;
            }
         }
      } else {
         Renderable renderable13;
         if (objectDefinition.animationId == -1 && objectDefinition.childIds == null) {
            renderable13 = objectDefinition.modelAt(10, wallFlagIndex2, scalar, scalar2, scalar3, positionArgument, -1, -1, -1, -1);
         } else {
            renderable13 = new DynamicObject(newSizeX, wallFlagIndex2, 10, scalar2, scalar3, scalar, positionArgument, objectDefinition.animationId, true);
         }

         if (renderable13 != null) {
            short shortCode = 0;
            if (objectType == 11) {
               shortCode += 256;
            }

            int localSizeY;
            if (wallFlagIndex2 != 1 && wallFlagIndex2 != 3) {
               newSizeX = objectDefinition.sizeX;
               localSizeY = objectDefinition.sizeY;
            } else {
               newSizeX = objectDefinition.sizeY;
               localSizeY = objectDefinition.sizeX;
            }

            sceneGraph.addEntity(scalar5, byteCode, scalar4, localSizeY, renderable13, newSizeX, tileIndex, shortCode, newIndex, clippingDataIndex);
         }

         if (objectDefinition.solid) {
            collisionMap.addObject(objectDefinition.walkable, objectDefinition.sizeX, objectDefinition.sizeY, clippingDataIndex, newIndex, wallFlagIndex2);
         }
      }
   }
   public static boolean areObjectModelsReady(int scalarArgument, byte[] byteBufferArgument, int scalarArgument2) {
      boolean flag = true;
      Buffer buffer = new Buffer(byteBufferArgument);
      int scalar = -1;

      int decodedUnsignedSmart;
      scanObjectDefinitions:
      while ((decodedUnsignedSmart = buffer.readUnsignedSmart()) != 0) {
         scalar += decodedUnsignedSmart;
         decodedUnsignedSmart = 0;
         boolean localFlag = false;

         while (true) {
            while (!localFlag) {
               int readUnsignedSmart2;
               if ((readUnsignedSmart2 = buffer.readUnsignedSmart()) == 0) {
                  continue scanObjectDefinitions;
               }

               readUnsignedSmart2 = (decodedUnsignedSmart += readUnsignedSmart2 - 1) & 63;
               int scalar2 = decodedUnsignedSmart >> 6 & 63;
               int decodedUnsignedByte = buffer.readUnsignedByte() >> 2;
               scalar2 += scalarArgument;
               readUnsignedSmart2 += scalarArgument2;
               if (scalar2 > 0 && readUnsignedSmart2 > 0 && scalar2 < 103 && readUnsignedSmart2 < 103) {
                  ObjectDefinition objectDefinition = ObjectDefinition.lookup(scalar);
                  if (decodedUnsignedByte != 22 || !lowMemory || objectDefinition.hasActions || objectDefinition.obstructsGround) {
                     flag &= objectDefinition.areModelsReady();
                     localFlag = true;
                  }
               }
            }

            if (buffer.readUnsignedSmart() == 0) {
               break;
            }

            buffer.readUnsignedByte();
         }
      }

      return flag;
   }
   public final void loadObjectsRegion(int scalarArgument, CollisionMap[] values, int scalarArgument2, SceneGraph sceneGraph, byte[] byteBufferArgument) {
      Buffer buffer = new Buffer(byteBufferArgument);
      int scalar = -1;

      int decodedUnsignedSmart;
      while ((decodedUnsignedSmart = buffer.readUnsignedSmart()) != 0) {
         scalar += decodedUnsignedSmart;
         decodedUnsignedSmart = 0;

         int readUnsignedSmart2;
         while ((readUnsignedSmart2 = buffer.readUnsignedSmart()) != 0) {
            readUnsignedSmart2 = (decodedUnsignedSmart += readUnsignedSmart2 - 1) & 63;
            int clippingDataIndex = decodedUnsignedSmart >> 6 & 63;
            int scalar2 = decodedUnsignedSmart >> 12;
            int wallFlagIndex;
            int objectType = (wallFlagIndex = buffer.readUnsignedByte()) >> 2;
            wallFlagIndex &= 3;
            clippingDataIndex += scalarArgument;
            readUnsignedSmart2 += scalarArgument2;
            if (clippingDataIndex > 0 && readUnsignedSmart2 > 0 && clippingDataIndex < 103 && readUnsignedSmart2 < 103) {
               int scalar3 = scalar2;
               if ((this.tileSettings[1][clippingDataIndex][readUnsignedSmart2] & 2) == 2) {
                  scalar3 = scalar2 - 1;
               }

               CollisionMap collisionMap = null;
               if (scalar3 >= 0) {
                  collisionMap = values[scalar3];
               }

               this.addObject(readUnsignedSmart2, sceneGraph, collisionMap, objectType, scalar2, clippingDataIndex, scalar, wallFlagIndex);
            }
         }
      }
   }
}
