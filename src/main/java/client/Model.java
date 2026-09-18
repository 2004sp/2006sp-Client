package client;
public final class Model extends Renderable {
   public static final Model sharedModel = new Model();
   private static int[] sharedVerticesX = new int[2000];
   private static int[] sharedVerticesY = new int[2000];
   private static int[] sharedVerticesZ = new int[2000];
   private static int[] sharedTriangleAlpha = new int[2000];
   public int vertexCount;
   public int[] verticesX;
   public int[] verticesY;
   public int[] verticesZ;
   public int triangleCount;
   public int[] trianglePointsX;
   public int[] trianglePointsY;
   public int[] trianglePointsZ;
   private int[] triangleHSLA;
   private int[] triangleHSLB;
   private int[] triangleHSLC;
   public int[] triangleDrawType;
   private int[] trianglePriorities;
   private int[] triangleAlphaValues;
   public int[] triangleColorValues;
   private int trianglePriority;
   private int texturedTriangleCount;
   private int[] texturedTrianglePointsX;
   private int[] texturedTrianglePointsY;
   private int[] texturedTrianglePointsZ;
   public int minX;
   public int maxX;
   public int maxZ;
   public int minZ;
   public int diagonal2DAboveOrigin;
   public int maxY;
   private int diagonal3D;
   private int diagonal3DAboveOrigin;
   public int sceneHeightOffset;
   private int[] vertexSkins;
   private int[] triangleSkinValues;
   public int[][] vectorSkin;
   public int[][] triangleSkin;
   public boolean singleTile;
   VertexNormal[] vertexNormalOffset;
   private static ModelHeader[] modelHeaders;
   private static OnDemandFetcherBase onDemandFetcher;
   private static boolean[] triangleEdgeRestricted = new boolean[4096];
   private static boolean[] triangleNearClipped = new boolean[4096];
   private static int[] projectedX = new int[4096];
   private static int[] projectedY = new int[4096];
   private static int[] vertexDepthOffset = new int[4096];
   private static int[] cameraDepth = new int[4096];
   private static int[] cameraX = new int[4096];
   private static int[] cameraY = new int[4096];
   private static int[] cameraZ = new int[4096];
   private static int[] depthBucketCounts = new int[1500];
   private static int[][] depthBuckets = new int[1500][512];
   private static int[] priorityCounts = new int[12];
   private static int[][] priorityTriangles = new int[12][2000];
   private static int[] priority10Depths = new int[2000];
   private static int[] priority11Depths = new int[2000];
   private static int[] priorityDepthSums = new int[12];
   private static final int[] clippedX = new int[10];
   private static final int[] clippedY = new int[10];
   private static final int[] clippedShading = new int[10];
   private static int transformOriginX;
   private static int transformOriginY;
   private static int transformOriginZ;
   public static boolean pickingEnabled;
   public static int mouseX;
   public static int mouseY;
   public static int pickedCount;
   public static final int[] pickedIds = new int[1000];
   public static int[] SINE = Rasterizer3D.SINE;
   public static int[] COSINE = Rasterizer3D.COSINE;
   private static int[] HSL_TO_RGB = Rasterizer3D.HSL_TO_RGB;
   private static int[] RECIPROCAL_2048 = Rasterizer3D.reciprocal2048;
   private boolean oldFormat;
   private static int oldFormatModelCount;
   public final void applyInterpolatedAnimation(int frameIndex, int frameIndex2, int graphicFrameCycle, int frameLength) {
      if (this.vectorSkin != null) {
         if (frameIndex != -1) {
            AnimationFrame animationFrame;
            if ((animationFrame = AnimationFrame.get(frameIndex)) != null) {
               AnimationSkeleton animationSkeleton = animationFrame.skeleton;
               AnimationFrame animationFrame2 = null;
               if (frameIndex2 != -1 && (animationFrame2 = AnimationFrame.get(frameIndex2)).skeleton != animationSkeleton) {
                  animationFrame2 = null;
               }

               transformOriginX = 0;
               transformOriginY = 0;
               transformOriginZ = 0;
               if (animationFrame2 == null) {
                  for (int transformIndex = 0; transformIndex < animationFrame.transformCount; transformIndex++) {
                     int transformIndex2 = animationFrame.transformIndices[transformIndex];
                     this.transformSkin(animationSkeleton.transformTypes[transformIndex2], animationSkeleton.labels[transformIndex2], animationFrame.transformX[transformIndex], animationFrame.transformY[transformIndex], animationFrame.transformZ[transformIndex]);
                  }
               } else {
                  frameIndex2 = 0;
                  int transformIndex3 = 0;

                  for (int transformTypeIndex = 0; transformTypeIndex < animationSkeleton.transformCount; transformTypeIndex++) {
                     int localTransformX = 0;
                     if (frameIndex2 < animationFrame.transformCount && animationFrame.transformIndices[frameIndex2] == transformTypeIndex) {
                        localTransformX = 1;
                     }

                     int transformX2 = 0;
                     if (transformIndex3 < animationFrame2.transformCount && animationFrame2.transformIndices[transformIndex3] == transformTypeIndex) {
                        transformX2 = 1;
                     }

                     if (localTransformX != 0 || transformX2 != 0) {
                        int localTransformZ = 0;
                        int localTransformTypes;
                        if ((localTransformTypes = animationSkeleton.transformTypes[transformTypeIndex]) == 3) {
                           localTransformZ = 128;
                        }

                        int localTransformY;
                        int transformZ2;
                        if (localTransformX != 0) {
                           localTransformX = animationFrame.transformX[frameIndex2];
                           localTransformY = animationFrame.transformY[frameIndex2];
                           transformZ2 = animationFrame.transformZ[frameIndex2];
                           frameIndex2++;
                        } else {
                           localTransformX = localTransformZ;
                           localTransformY = localTransformZ;
                           transformZ2 = localTransformZ;
                        }

                        int transformY2;
                        if (transformX2 != 0) {
                           transformX2 = animationFrame2.transformX[transformIndex3];
                           transformY2 = animationFrame2.transformY[transformIndex3];
                           localTransformZ = animationFrame2.transformZ[transformIndex3];
                           transformIndex3++;
                        } else {
                           transformX2 = localTransformZ;
                           transformY2 = localTransformZ;
                        }

                        if (localTransformTypes == 2) {
                           transformX2 = transformX2 - localTransformX & 2047;
                           transformY2 = transformY2 - localTransformY & 2047;
                           localTransformZ = localTransformZ - transformZ2 & 2047;
                           if (transformX2 >= 1024) {
                              transformX2 -= 2048;
                           }

                           if (transformY2 >= 1024) {
                              transformY2 -= 2048;
                           }

                           if (localTransformZ >= 1024) {
                              localTransformZ -= 2048;
                           }

                           localTransformX = localTransformX + transformX2 * graphicFrameCycle / frameLength & 2047;
                           transformX2 = localTransformY + transformY2 * graphicFrameCycle / frameLength & 2047;
                           localTransformZ = transformZ2 + localTransformZ * graphicFrameCycle / frameLength & 2047;
                        } else {
                           localTransformX += (transformX2 - localTransformX) * graphicFrameCycle / frameLength;
                           transformX2 = localTransformY + (transformY2 - localTransformY) * graphicFrameCycle / frameLength;
                           localTransformZ = transformZ2 + (localTransformZ - transformZ2) * graphicFrameCycle / frameLength;
                        }

                        this.transformSkin(localTransformTypes, animationSkeleton.labels[transformTypeIndex], localTransformX, transformX2, localTransformZ);
                     }
                  }
               }
            }
         }
      }
   }
   private void buildTextureMappings(short[] localTriangleColor, int[] values, int[] newValues, int[] values2, byte[] byteBufferArgument) {
      int sourceTexturedTriangleCount = 0;
      int localTriangleDrawType = 0;
      if (localTriangleColor != null) {
         this.texturedTrianglePointsX = new int[this.triangleCount];
         this.texturedTrianglePointsY = new int[this.triangleCount];
         this.texturedTrianglePointsZ = new int[this.triangleCount];

         for (int triangleDrawTypeIndex = 0; triangleDrawTypeIndex < this.triangleCount; triangleDrawTypeIndex++) {
            if (localTriangleColor[triangleDrawTypeIndex] == -1 && this.triangleDrawType[triangleDrawTypeIndex] == 2) {
               this.triangleColorValues[triangleDrawTypeIndex] = 65535;
               this.triangleDrawType[triangleDrawTypeIndex] = 0;
            }

            int[] localTexturedTrianglePointsZ;
            int localTexturedTrianglePointsZIndex;
            int scalar;
            if (localTriangleColor[triangleDrawTypeIndex] < 50 && localTriangleColor[triangleDrawTypeIndex] >= 0 && localTriangleColor[triangleDrawTypeIndex] != 39) {
               this.triangleDrawType[triangleDrawTypeIndex] = localTriangleDrawType + 2;
               localTriangleDrawType += 4;
               int trianglePointsXEntry = this.trianglePointsX[triangleDrawTypeIndex];
               int trianglePointsYEntry = this.trianglePointsY[triangleDrawTypeIndex];
               int trianglePointsZEntry = this.trianglePointsZ[triangleDrawTypeIndex];
               this.triangleColorValues[triangleDrawTypeIndex] = localTriangleColor[triangleDrawTypeIndex];
               int position = -1;
               if (byteBufferArgument != null
                  && (position = byteBufferArgument[triangleDrawTypeIndex] & 255) != 255
                  && (values[position] >= cameraY.length || newValues[position] >= cameraX.length || values2[position] >= cameraZ.length)) {
                  position = -1;
               }

               if (position == 255) {
                  position = -1;
               }

               this.texturedTrianglePointsX[sourceTexturedTriangleCount] = position == -1 ? trianglePointsXEntry : values[position];
               this.texturedTrianglePointsY[sourceTexturedTriangleCount] = position == -1 ? trianglePointsYEntry : newValues[position];
               localTexturedTrianglePointsZ = this.texturedTrianglePointsZ;
               localTexturedTrianglePointsZIndex = sourceTexturedTriangleCount++;
               scalar = position == -1 ? trianglePointsZEntry : values2[position];
            } else {
               localTexturedTrianglePointsZ = this.triangleDrawType;
               localTexturedTrianglePointsZIndex = triangleDrawTypeIndex;
               scalar = 0;
            }

            localTexturedTrianglePointsZ[localTexturedTrianglePointsZIndex] = scalar;
         }

         this.texturedTriangleCount = sourceTexturedTriangleCount;
      }
   }

   private Model(int modelHeaderIndex2) {
      byte[] currentPositionOrModelHeaders = modelHeaders[modelHeaderIndex2].data;
      if (modelHeaders[modelHeaderIndex2].data[currentPositionOrModelHeaders.length - 1] == -1 && currentPositionOrModelHeaders[currentPositionOrModelHeaders.length - 2] == -1) {
         int modelHeaderIndex = modelHeaderIndex2;
         Model model = this;
         Buffer buffer = new Buffer(currentPositionOrModelHeaders);
         Buffer buffer19 = new Buffer(currentPositionOrModelHeaders);
         Buffer buffer2 = new Buffer(currentPositionOrModelHeaders);
         Buffer buffer3 = new Buffer(currentPositionOrModelHeaders);
         Buffer buffer4 = new Buffer(currentPositionOrModelHeaders);
         Buffer buffer5 = new Buffer(currentPositionOrModelHeaders);
         Buffer buffer6 = new Buffer(currentPositionOrModelHeaders);
         buffer.currentPosition = currentPositionOrModelHeaders.length - 23;
         int sourceVertexCount = buffer.readUnsignedShort();
         int sourceTriangleCount = buffer.readUnsignedShort();
         int sourceTexturedTriangleCount = buffer.readUnsignedByte();
         ModelHeader modelHeader;
         (modelHeader = modelHeaders[modelHeaderIndex] = new ModelHeader()).data = currentPositionOrModelHeaders;
         modelHeader.vertexCount = sourceVertexCount;
         modelHeader.triangleCount = sourceTriangleCount;
         modelHeader.texturedTriangleCount = sourceTexturedTriangleCount;
         int decodedUnsignedByte = buffer.readUnsignedByte();
         boolean flag = ~(1 & decodedUnsignedByte) == -2;
         if ((8 & decodedUnsignedByte) == 8) {
            boolean localFlag = false;
            buffer.currentPosition -= 7;
            int readUnsignedByte2 = buffer.readUnsignedByte();
            buffer.currentPosition += 6;
            modelHeaderIndex = buffer.readUnsignedByte();
            int readUnsignedByte3 = buffer.readUnsignedByte();
            int readUnsignedByte4 = buffer.readUnsignedByte();
            int readUnsignedByte5 = buffer.readUnsignedByte();
            int readUnsignedByte6 = buffer.readUnsignedByte();
            int currentPositionOrReadUnsignedShort = buffer.readUnsignedShort();
            int sourceCurrentPosition = buffer.readUnsignedShort();
            int currentPositionOrReadUnsignedShort2 = buffer.readUnsignedShort();
            int currentPositionOrReadUnsignedShort3 = buffer.readUnsignedShort();
            int currentPositionOrReadUnsignedShort4 = buffer.readUnsignedShort();
            int sourceCurrentPosition2 = 0;
            int position = 0;
            int localLength = 0;
            byte[] byteBuffer = null;
            byte[] byteBuffer2 = null;
            byte[] byteBuffer3 = null;
            byte[] byteBuffer4 = null;
            byte[] byteBuffer5 = null;
            byte[] byteBuffer6 = null;
            byte[] byteBuffer7 = null;
            int[] values = null;
            int[] integerBuffer = null;
            int[] values2 = null;
            short[] values3 = null;
            if (sourceTexturedTriangleCount > 0) {
               byteBuffer2 = new byte[sourceTexturedTriangleCount];
               buffer.currentPosition = 0;

               for (int loopIndex = 0; loopIndex < sourceTexturedTriangleCount; loopIndex++) {
                  byte byteCode;
                  if ((byteCode = byteBuffer2[loopIndex] = buffer.readByte()) == 0) {
                     sourceCurrentPosition2++;
                  }

                  if (byteCode > 0 && byteCode <= 3) {
                     position++;
                  }

                  if (byteCode == 2) {
                     localLength++;
                  }
               }
            }

            int scalar = sourceTexturedTriangleCount;
            int currentPosition2 = sourceTexturedTriangleCount;
            int scalar2;
            int currentPosition3 = scalar2 = scalar + sourceVertexCount;
            if (flag) {
               scalar2 += sourceTriangleCount;
            }

            if (decodedUnsignedByte == 1) {
               scalar2 += sourceTriangleCount;
            }

            int currentPosition4 = scalar2;
            int currentPosition5 = scalar = scalar2 + sourceTriangleCount;
            if (modelHeaderIndex == 255) {
               scalar += sourceTriangleCount;
            }

            int currentPosition6 = scalar;
            if (readUnsignedByte4 == 1) {
               scalar += sourceTriangleCount;
            }

            int currentPosition7 = scalar;
            if (readUnsignedByte6 == 1) {
               scalar += sourceVertexCount;
            }

            int currentPosition8 = scalar;
            if (readUnsignedByte3 == 1) {
               scalar += sourceTriangleCount;
            }

            int currentPosition9 = scalar;
            int scalar3;
            currentPositionOrReadUnsignedShort3 = scalar3 = scalar + currentPositionOrReadUnsignedShort3;
            if (readUnsignedByte5 == 1) {
               scalar3 += sourceTriangleCount << 1;
            }

            int currentPosition10 = scalar3;
            currentPositionOrReadUnsignedShort4 = scalar = scalar3 + currentPositionOrReadUnsignedShort4;
            int scalar4;
            int currentPosition11 = scalar4 = scalar + (sourceTriangleCount << 1);
            currentPositionOrReadUnsignedShort = scalar = scalar4 + currentPositionOrReadUnsignedShort;
            int scalar5;
            sourceCurrentPosition = scalar5 = scalar + sourceCurrentPosition;
            currentPositionOrReadUnsignedShort2 = scalar = scalar5 + currentPositionOrReadUnsignedShort2;
            int scalar6;
            sourceCurrentPosition2 = scalar6 = scalar + sourceCurrentPosition2 * 6;
            scalar = scalar6 + position * 6;
            byte byteCode2 = 6;
            if (readUnsignedByte2 != 14) {
               if (readUnsignedByte2 >= 15) {
                  byteCode2 = 9;
               }
            } else {
               byteCode2 = 7;
            }

            int currentPosition12 = scalar;
            int scalar7;
            int currentPosition13 = scalar7 = scalar + byteCode2 * position;
            int currentPosition14 = scalar = scalar7 + position;
            int scalar8;
            int currentPosition15 = scalar8 = scalar + position;
            int[] values4 = new int[sourceVertexCount];
            int[] values5 = new int[sourceVertexCount];
            int[] values6 = new int[sourceVertexCount];
            int[] sourceTrianglePointsX = new int[sourceTriangleCount];
            int[] sourceTrianglePointsY = new int[sourceTriangleCount];
            int[] sourceTrianglePointsZ = new int[sourceTriangleCount];
            model.vertexSkins = new int[sourceVertexCount];
            model.triangleDrawType = new int[sourceTriangleCount];
            model.trianglePriorities = new int[sourceTriangleCount];
            model.triangleAlphaValues = new int[sourceTriangleCount];
            model.triangleSkinValues = new int[sourceTriangleCount];
            if (readUnsignedByte6 == 1) {
               model.vertexSkins = new int[sourceVertexCount];
            }

            if (flag) {
               model.triangleDrawType = new int[sourceTriangleCount];
            }

            if (modelHeaderIndex == 255) {
               model.trianglePriorities = new int[sourceTriangleCount];
            }

            if (readUnsignedByte3 == 1) {
               model.triangleAlphaValues = new int[sourceTriangleCount];
            }

            if (readUnsignedByte4 == 1) {
               model.triangleSkinValues = new int[sourceTriangleCount];
            }

            if (readUnsignedByte5 == 1) {
               values3 = new short[sourceTriangleCount];
            }

            if (readUnsignedByte5 == 1 && sourceTexturedTriangleCount > 0) {
               byteBuffer = new byte[sourceTriangleCount];
            }

            int[] sourceTriangleColorValues = new int[sourceTriangleCount];
            int[] values7 = null;
            int[] values8 = null;
            int[] values9 = null;
            if (sourceTexturedTriangleCount > 0) {
               values7 = new int[sourceTexturedTriangleCount];
               values8 = new int[sourceTexturedTriangleCount];
               values9 = new int[sourceTexturedTriangleCount];
               if (position > 0) {
                  values = new int[position];
                  values2 = new int[position];
                  integerBuffer = new int[position];
                  byteBuffer6 = new byte[position];
                  byteBuffer7 = new byte[position];
                  byteBuffer4 = new byte[position];
               }

               if (localLength > 0) {
                  byteBuffer5 = new byte[localLength];
                  byteBuffer3 = new byte[localLength];
               }
            }

            buffer.currentPosition = currentPosition2;
            buffer19.currentPosition = currentPosition11;
            buffer2.currentPosition = currentPositionOrReadUnsignedShort;
            buffer3.currentPosition = sourceCurrentPosition;
            buffer4.currentPosition = currentPosition7;
            int scalar9 = 0;
            int scalar10 = 0;
            int scalar11 = 0;

            for (int vertexSkinIndex = 0; vertexSkinIndex < sourceVertexCount; vertexSkinIndex++) {
               int readUnsignedByte7 = buffer.readUnsignedByte();
               int decodedSignedSmart = 0;
               if ((readUnsignedByte7 & 1) != 0) {
                  decodedSignedSmart = buffer19.readSignedSmart();
               }

               int readSignedSmart2 = 0;
               if ((readUnsignedByte7 & 2) != 0) {
                  readSignedSmart2 = buffer2.readSignedSmart();
               }

               int readSignedSmart3 = 0;
               if ((readUnsignedByte7 & 4) != 0) {
                  readSignedSmart3 = buffer3.readSignedSmart();
               }

               values4[vertexSkinIndex] = scalar9 + decodedSignedSmart;
               values5[vertexSkinIndex] = scalar10 + readSignedSmart2;
               values6[vertexSkinIndex] = scalar11 + readSignedSmart3;
               scalar9 = values4[vertexSkinIndex];
               scalar10 = values5[vertexSkinIndex];
               scalar11 = values6[vertexSkinIndex];
               if (model.vertexSkins != null) {
                  model.vertexSkins[vertexSkinIndex] = buffer4.readUnsignedByte();
               }
            }

            buffer.currentPosition = currentPositionOrReadUnsignedShort4;
            buffer19.currentPosition = currentPosition3;
            buffer2.currentPosition = currentPosition5;
            buffer3.currentPosition = currentPosition8;
            buffer4.currentPosition = currentPosition6;
            buffer5.currentPosition = currentPositionOrReadUnsignedShort3;
            buffer6.currentPosition = currentPosition10;

            for (int triangleDrawTypeIndex = 0; triangleDrawTypeIndex < sourceTriangleCount; triangleDrawTypeIndex++) {
               sourceTriangleColorValues[triangleDrawTypeIndex] = buffer.readUnsignedShort();
               if (decodedUnsignedByte == 1) {
                  model.triangleDrawType[triangleDrawTypeIndex] = buffer19.readByte();
                  if (model.triangleDrawType[triangleDrawTypeIndex] == 2) {
                     sourceTriangleColorValues[triangleDrawTypeIndex] = 65535;
                  }

                  model.triangleDrawType[triangleDrawTypeIndex] = 0;
               }

               if (modelHeaderIndex == 255) {
                  model.trianglePriorities[triangleDrawTypeIndex] = buffer2.readByte();
               }

               if (readUnsignedByte3 == 1) {
                  model.triangleAlphaValues[triangleDrawTypeIndex] = buffer3.readByte();
                  if (model.triangleAlphaValues[triangleDrawTypeIndex] < 0) {
                     model.triangleAlphaValues[triangleDrawTypeIndex] = 256 + model.triangleAlphaValues[triangleDrawTypeIndex];
                  }
               }

               if (readUnsignedByte4 == 1) {
                  model.triangleSkinValues[triangleDrawTypeIndex] = buffer4.readUnsignedByte();
               }

               if (readUnsignedByte5 == 1) {
                  values3[triangleDrawTypeIndex] = (short)(buffer5.readUnsignedShort() - 1);
               }

               if (byteBuffer != null) {
                  if (values3[triangleDrawTypeIndex] != -1) {
                     byteBuffer[triangleDrawTypeIndex] = (byte)(buffer6.readUnsignedByte() - 1);
                  } else {
                     byteBuffer[triangleDrawTypeIndex] = -1;
                  }
               }
            }

            buffer.currentPosition = currentPosition9;
            buffer19.currentPosition = currentPosition4;
            int readSignedSmart4 = 0;
            int readSignedSmart5 = 0;
            int readSignedSmart6 = 0;
            int scalar12 = 0;

            for (int loopIndex2 = 0; loopIndex2 < sourceTriangleCount; loopIndex2++) {
               int readUnsignedByte8;
               if ((readUnsignedByte8 = buffer19.readUnsignedByte()) == 1) {
                  scalar12 = readSignedSmart4 = buffer.readSignedSmart() + scalar12;
                  scalar12 = readSignedSmart5 = buffer.readSignedSmart() + scalar12;
                  scalar12 = readSignedSmart6 = buffer.readSignedSmart() + scalar12;
                  sourceTrianglePointsX[loopIndex2] = readSignedSmart4;
                  sourceTrianglePointsY[loopIndex2] = readSignedSmart5;
                  sourceTrianglePointsZ[loopIndex2] = readSignedSmart6;
               }

               if (readUnsignedByte8 == 2) {
                  readSignedSmart5 = readSignedSmart6;
                  scalar12 = readSignedSmart6 = buffer.readSignedSmart() + scalar12;
                  sourceTrianglePointsX[loopIndex2] = readSignedSmart4;
                  sourceTrianglePointsY[loopIndex2] = readSignedSmart5;
                  sourceTrianglePointsZ[loopIndex2] = readSignedSmart6;
               }

               if (readUnsignedByte8 == 3) {
                  readSignedSmart4 = readSignedSmart6;
                  scalar12 = readSignedSmart6 = buffer.readSignedSmart() + scalar12;
                  sourceTrianglePointsX[loopIndex2] = readSignedSmart4;
                  sourceTrianglePointsY[loopIndex2] = readSignedSmart5;
                  sourceTrianglePointsZ[loopIndex2] = readSignedSmart6;
               }

               if (readUnsignedByte8 == 4) {
                  int scalar13 = readSignedSmart4;
                  readSignedSmart4 = readSignedSmart5;
                  readSignedSmart5 = scalar13;
                  scalar12 = readSignedSmart6 = buffer.readSignedSmart() + scalar12;
                  sourceTrianglePointsX[loopIndex2] = readSignedSmart4;
                  sourceTrianglePointsY[loopIndex2] = readSignedSmart5;
                  sourceTrianglePointsZ[loopIndex2] = readSignedSmart6;
               }
            }

            buffer.currentPosition = currentPositionOrReadUnsignedShort2;
            buffer19.currentPosition = sourceCurrentPosition2;
            buffer2.currentPosition = currentPosition12;
            buffer3.currentPosition = currentPosition13;
            buffer4.currentPosition = currentPosition14;
            buffer5.currentPosition = currentPosition15;

            for (int loopIndex3 = 0; loopIndex3 < sourceTexturedTriangleCount; loopIndex3++) {
               int scalar14;
               if ((scalar14 = byteBuffer2[loopIndex3] & 255) == 0) {
                  values7[loopIndex3] = buffer.readUnsignedShort();
                  values8[loopIndex3] = buffer.readUnsignedShort();
                  values9[loopIndex3] = buffer.readUnsignedShort();
               }

               if (scalar14 == 1) {
                  values7[loopIndex3] = buffer19.readUnsignedShort();
                  values8[loopIndex3] = buffer19.readUnsignedShort();
                  values9[loopIndex3] = buffer19.readUnsignedShort();
                  if (readUnsignedByte2 < 15) {
                     values[loopIndex3] = buffer2.readUnsignedShort();
                     if (readUnsignedByte2 >= 14) {
                        values2[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     } else {
                        values2[loopIndex3] = buffer2.readUnsignedShort();
                     }

                     integerBuffer[loopIndex3] = buffer2.readUnsignedShort();
                  } else {
                     values[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     values2[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     integerBuffer[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                  }

                  byteBuffer6[loopIndex3] = buffer3.readByte();
                  byteBuffer7[loopIndex3] = buffer4.readByte();
                  byteBuffer4[loopIndex3] = buffer5.readByte();
               }

               if (scalar14 == 2) {
                  values7[loopIndex3] = buffer19.readUnsignedShort();
                  values8[loopIndex3] = buffer19.readUnsignedShort();
                  values9[loopIndex3] = buffer19.readUnsignedShort();
                  if (readUnsignedByte2 >= 15) {
                     values[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     values2[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     integerBuffer[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                  } else {
                     values[loopIndex3] = buffer2.readUnsignedShort();
                     if (readUnsignedByte2 < 14) {
                        values2[loopIndex3] = buffer2.readUnsignedShort();
                     } else {
                        values2[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     }

                     integerBuffer[loopIndex3] = buffer2.readUnsignedShort();
                  }

                  byteBuffer6[loopIndex3] = buffer3.readByte();
                  byteBuffer7[loopIndex3] = buffer4.readByte();
                  byteBuffer4[loopIndex3] = buffer5.readByte();
                  byteBuffer5[loopIndex3] = buffer5.readByte();
                  byteBuffer3[loopIndex3] = buffer5.readByte();
               }

               if (scalar14 == 3) {
                  values7[loopIndex3] = buffer19.readUnsignedShort();
                  values8[loopIndex3] = buffer19.readUnsignedShort();
                  values9[loopIndex3] = buffer19.readUnsignedShort();
                  if (readUnsignedByte2 < 15) {
                     values[loopIndex3] = buffer2.readUnsignedShort();
                     if (readUnsignedByte2 < 14) {
                        values2[loopIndex3] = buffer2.readUnsignedShort();
                     } else {
                        values2[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     }

                     integerBuffer[loopIndex3] = buffer2.readUnsignedShort();
                  } else {
                     values[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     values2[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                     integerBuffer[loopIndex3] = buffer2.readUnsignedMediumAlternative();
                  }

                  byteBuffer6[loopIndex3] = buffer3.readByte();
                  byteBuffer7[loopIndex3] = buffer4.readByte();
                  byteBuffer4[loopIndex3] = buffer5.readByte();
               }
            }

            if (modelHeaderIndex != 255) {
               for (int trianglePriorityIndex = 0; trianglePriorityIndex < sourceTriangleCount; trianglePriorityIndex++) {
                  model.trianglePriorities[trianglePriorityIndex] = modelHeaderIndex;
               }
            }

            model.triangleColorValues = sourceTriangleColorValues;
            model.vertexCount = sourceVertexCount;
            model.triangleCount = sourceTriangleCount;
            model.verticesX = values4;
            model.verticesY = values5;
            model.verticesZ = values6;
            model.trianglePointsX = sourceTrianglePointsX;
            model.trianglePointsY = sourceTrianglePointsY;
            model.trianglePointsZ = sourceTrianglePointsZ;
            model.buildTextureMappings(values3, values7, values8, values9, byteBuffer);
            return;
         }

         int sourceModelHeaderIndex = modelHeaderIndex;
         byte[] currentPosition16 = currentPositionOrModelHeaders;
         Model sourceModel = model;
         Buffer buffer7 = new Buffer(currentPosition16);
         Buffer buffer8 = new Buffer(currentPosition16);
         Buffer buffer9 = new Buffer(currentPosition16);
         Buffer buffer10 = new Buffer(currentPosition16);
         Buffer buffer11 = new Buffer(currentPosition16);
         Buffer buffer12 = new Buffer(currentPosition16);
         Buffer buffer13 = new Buffer(currentPosition16);
         buffer7.currentPosition = currentPosition16.length - 23;
         int decodedUnsignedShort2 = buffer7.readUnsignedShort();
         int decodedUnsignedShort3 = buffer7.readUnsignedShort();
         int decodedUnsignedByte2 = buffer7.readUnsignedByte();
         ModelHeader modelHeader3;
         (modelHeader3 = modelHeaders[sourceModelHeaderIndex] = new ModelHeader()).data = currentPosition16;
         modelHeader3.vertexCount = decodedUnsignedShort2;
         modelHeader3.triangleCount = decodedUnsignedShort3;
         modelHeader3.texturedTriangleCount = decodedUnsignedByte2;
         int readUnsignedByte9 = buffer7.readUnsignedByte();
         boolean flag2 = ~(1 & readUnsignedByte9) == -2;
         int trianglePriorityOrReadUnsignedByte = buffer7.readUnsignedByte();
         int readUnsignedByte10 = buffer7.readUnsignedByte();
         int readUnsignedByte11 = buffer7.readUnsignedByte();
         int readUnsignedByte12 = buffer7.readUnsignedByte();
         int readUnsignedByte13 = buffer7.readUnsignedByte();
         int decodedUnsignedShort = buffer7.readUnsignedShort();
         int readUnsignedShort2 = buffer7.readUnsignedShort();
         int readUnsignedShort3 = buffer7.readUnsignedShort();
         int readUnsignedShort4 = buffer7.readUnsignedShort();
         int readUnsignedShort5 = buffer7.readUnsignedShort();
         int scalar15 = 0;
         int position2 = 0;
         int length2 = 0;
         byte[] byteBuffer8 = null;
         byte[] byteBuffer9 = null;
         byte[] byteBuffer10 = null;
         byte[] byteBuffer11 = null;
         byte[] byteBuffer12 = null;
         byte[] byteBuffer13 = null;
         byte[] byteBuffer14 = null;
         int[] values10 = null;
         int[] values11 = null;
         int[] values12 = null;
         short[] values13 = null;
         if (decodedUnsignedByte2 > 0) {
            byteBuffer9 = new byte[decodedUnsignedByte2];
            buffer7.currentPosition = 0;

            for (int loopIndex4 = 0; loopIndex4 < decodedUnsignedByte2; loopIndex4 += 1) {
               byte byteCode3;
               if ((byteCode3 = byteBuffer9[loopIndex4] = buffer7.readByte()) == 0) {
                  scalar15++;
               }

               if (byteCode3 > 0 && byteCode3 <= 3) {
                  position2++;
               }

               if (byteCode3 == 2) {
                  length2++;
               }
            }
         }

         int scalar16 = decodedUnsignedByte2;
         int currentPosition17 = decodedUnsignedByte2;
         int scalar17;
         int currentPosition18 = scalar17 = scalar16 + decodedUnsignedShort2;
         if (readUnsignedByte9 == 1) {
            scalar17 += decodedUnsignedShort3;
         }

         int currentPosition19 = scalar17;
         int currentPosition20 = scalar16 = scalar17 + decodedUnsignedShort3;
         if (trianglePriorityOrReadUnsignedByte == 255) {
            scalar16 += decodedUnsignedShort3;
         }

         int currentPosition21 = scalar16;
         if (readUnsignedByte11 == 1) {
            scalar16 += decodedUnsignedShort3;
         }

         int currentPosition22 = scalar16;
         if (readUnsignedByte13 == 1) {
            scalar16 += decodedUnsignedShort2;
         }

         int currentPosition23 = scalar16;
         if (readUnsignedByte10 == 1) {
            scalar16 += decodedUnsignedShort3;
         }

         int currentPosition24 = scalar16;
         int scalar18;
         int currentPosition25 = scalar18 = scalar16 + readUnsignedShort4;
         if (readUnsignedByte12 == 1) {
            scalar18 += decodedUnsignedShort3 << 1;
         }

         int currentPosition26 = scalar18;
         int currentPosition27 = scalar16 = scalar18 + readUnsignedShort5;
         int scalar19;
         int currentPosition28 = scalar19 = scalar16 + (decodedUnsignedShort3 << 1);
         int currentPosition29 = scalar16 = scalar19 + decodedUnsignedShort;
         int scalar20;
         int currentPosition30 = scalar20 = scalar16 + readUnsignedShort2;
         int currentPosition31 = scalar16 = scalar20 + readUnsignedShort3;
         int scalar21;
         int currentPosition32 = scalar21 = scalar16 + scalar15 * 6;
         int currentPosition33 = scalar16 = scalar21 + position2 * 6;
         int scalar22;
         int currentPosition34 = scalar22 = scalar16 + position2 * 6;
         int currentPosition35 = scalar16 = scalar22 + position2;
         int scalar23;
         int currentPosition36 = scalar23 = scalar16 + position2;
         int[] values14 = new int[decodedUnsignedShort2];
         int[] values15 = new int[decodedUnsignedShort2];
         int[] values16 = new int[decodedUnsignedShort2];
         int[] trianglePointsX2 = new int[decodedUnsignedShort3];
         int[] trianglePointsY2 = new int[decodedUnsignedShort3];
         int[] trianglePointsZ2 = new int[decodedUnsignedShort3];
         sourceModel.vertexSkins = new int[decodedUnsignedShort2];
         sourceModel.triangleDrawType = new int[decodedUnsignedShort3];
         sourceModel.trianglePriorities = new int[decodedUnsignedShort3];
         sourceModel.triangleAlphaValues = new int[decodedUnsignedShort3];
         sourceModel.triangleSkinValues = new int[decodedUnsignedShort3];
         if (readUnsignedByte13 == 1) {
            sourceModel.vertexSkins = new int[decodedUnsignedShort2];
         }

         if (flag2) {
            sourceModel.triangleDrawType = new int[decodedUnsignedShort3];
         }

         if (trianglePriorityOrReadUnsignedByte == 255) {
            sourceModel.trianglePriorities = new int[decodedUnsignedShort3];
         }

         if (readUnsignedByte10 == 1) {
            sourceModel.triangleAlphaValues = new int[decodedUnsignedShort3];
         }

         if (readUnsignedByte11 == 1) {
            sourceModel.triangleSkinValues = new int[decodedUnsignedShort3];
         }

         if (readUnsignedByte12 == 1) {
            values13 = new short[decodedUnsignedShort3];
         }

         if (readUnsignedByte12 == 1 && decodedUnsignedByte2 > 0) {
            byteBuffer8 = new byte[decodedUnsignedShort3];
         }

         int[] triangleColorValues2 = new int[decodedUnsignedShort3];
         int[] values17 = null;
         int[] values18 = null;
         int[] values19 = null;
         if (decodedUnsignedByte2 > 0) {
            values17 = new int[decodedUnsignedByte2];
            values18 = new int[decodedUnsignedByte2];
            values19 = new int[decodedUnsignedByte2];
            if (position2 > 0) {
               values10 = new int[position2];
               values12 = new int[position2];
               values11 = new int[position2];
               byteBuffer13 = new byte[position2];
               byteBuffer14 = new byte[position2];
               byteBuffer11 = new byte[position2];
            }

            if (length2 > 0) {
               byteBuffer12 = new byte[length2];
               byteBuffer10 = new byte[length2];
            }
         }

         buffer7.currentPosition = currentPosition17;
         buffer8.currentPosition = currentPosition28;
         buffer9.currentPosition = currentPosition29;
         buffer10.currentPosition = currentPosition30;
         buffer11.currentPosition = currentPosition22;
         int scalar24 = 0;
         int scalar25 = 0;
         int scalar26 = 0;

         for (int vertexSkinIndex2 = 0; vertexSkinIndex2 < decodedUnsignedShort2; vertexSkinIndex2 += 1) {
            int readUnsignedByte14 = buffer7.readUnsignedByte();
            int readSignedSmart7 = 0;
            if ((readUnsignedByte14 & 1) != 0) {
               readSignedSmart7 = buffer8.readSignedSmart();
            }

            int readSignedSmart8 = 0;
            if ((readUnsignedByte14 & 2) != 0) {
               readSignedSmart8 = buffer9.readSignedSmart();
            }

            int readSignedSmart9 = 0;
            if ((readUnsignedByte14 & 4) != 0) {
               readSignedSmart9 = buffer10.readSignedSmart();
            }

            values14[vertexSkinIndex2] = scalar24 + readSignedSmart7;
            values15[vertexSkinIndex2] = scalar25 + readSignedSmart8;
            values16[vertexSkinIndex2] = scalar26 + readSignedSmart9;
            scalar24 = values14[vertexSkinIndex2];
            scalar25 = values15[vertexSkinIndex2];
            scalar26 = values16[vertexSkinIndex2];
            if (sourceModel.vertexSkins != null) {
               sourceModel.vertexSkins[vertexSkinIndex2] = buffer11.readUnsignedByte();
            }
         }

         buffer7.currentPosition = currentPosition27;
         buffer8.currentPosition = currentPosition18;
         buffer9.currentPosition = currentPosition20;
         buffer10.currentPosition = currentPosition23;
         buffer11.currentPosition = currentPosition21;
         buffer12.currentPosition = currentPosition25;
         buffer13.currentPosition = currentPosition26;

         for (int triangleDrawTypeIndex2 = 0; triangleDrawTypeIndex2 < decodedUnsignedShort3; triangleDrawTypeIndex2 += 1) {
            triangleColorValues2[triangleDrawTypeIndex2] = buffer7.readUnsignedShort();
            if (readUnsignedByte9 == 1) {
               sourceModel.triangleDrawType[triangleDrawTypeIndex2] = buffer8.readByte();
               if (sourceModel.triangleDrawType[triangleDrawTypeIndex2] == 2) {
                  triangleColorValues2[triangleDrawTypeIndex2] = 65535;
               }

               sourceModel.triangleDrawType[triangleDrawTypeIndex2] = 0;
            }

            if (trianglePriorityOrReadUnsignedByte == 255) {
               sourceModel.trianglePriorities[triangleDrawTypeIndex2] = buffer9.readByte();
            }

            if (readUnsignedByte10 == 1) {
               sourceModel.triangleAlphaValues[triangleDrawTypeIndex2] = buffer10.readByte();
               if (sourceModel.triangleAlphaValues[triangleDrawTypeIndex2] < 0) {
                  sourceModel.triangleAlphaValues[triangleDrawTypeIndex2] = 256 + sourceModel.triangleAlphaValues[triangleDrawTypeIndex2];
               }
            }

            if (readUnsignedByte11 == 1) {
               sourceModel.triangleSkinValues[triangleDrawTypeIndex2] = buffer11.readUnsignedByte();
            }

            if (readUnsignedByte12 == 1) {
               values13[triangleDrawTypeIndex2] = (short)(buffer12.readUnsignedShort() - 1);
            }

            if (byteBuffer8 != null) {
               if (values13[triangleDrawTypeIndex2] != -1) {
                  byteBuffer8[triangleDrawTypeIndex2] = (byte)(buffer13.readUnsignedByte() - 1);
               } else {
                  byteBuffer8[triangleDrawTypeIndex2] = -1;
               }
            }
         }

         buffer7.currentPosition = currentPosition24;
         buffer8.currentPosition = currentPosition19;
         int readSignedSmart10 = 0;
         int readSignedSmart11 = 0;
         int readSignedSmart12 = 0;
         int scalar27 = 0;

         for (int loopIndex5 = 0; loopIndex5 < decodedUnsignedShort3; loopIndex5 += 1) {
            int readUnsignedByte15;
            if ((readUnsignedByte15 = buffer8.readUnsignedByte()) == 1) {
               scalar27 = readSignedSmart10 = buffer7.readSignedSmart() + scalar27;
               scalar27 = readSignedSmart11 = buffer7.readSignedSmart() + scalar27;
               scalar27 = readSignedSmart12 = buffer7.readSignedSmart() + scalar27;
               trianglePointsX2[loopIndex5] = readSignedSmart10;
               trianglePointsY2[loopIndex5] = readSignedSmart11;
               trianglePointsZ2[loopIndex5] = readSignedSmart12;
            }

            if (readUnsignedByte15 == 2) {
               readSignedSmart11 = readSignedSmart12;
               scalar27 = readSignedSmart12 = buffer7.readSignedSmart() + scalar27;
               trianglePointsX2[loopIndex5] = readSignedSmart10;
               trianglePointsY2[loopIndex5] = readSignedSmart11;
               trianglePointsZ2[loopIndex5] = readSignedSmart12;
            }

            if (readUnsignedByte15 == 3) {
               readSignedSmart10 = readSignedSmart12;
               scalar27 = readSignedSmart12 = buffer7.readSignedSmart() + scalar27;
               trianglePointsX2[loopIndex5] = readSignedSmart10;
               trianglePointsY2[loopIndex5] = readSignedSmart11;
               trianglePointsZ2[loopIndex5] = readSignedSmart12;
            }

            if (readUnsignedByte15 == 4) {
               int scalar28 = readSignedSmart10;
               readSignedSmart10 = readSignedSmart11;
               readSignedSmart11 = scalar28;
               scalar27 = readSignedSmart12 = buffer7.readSignedSmart() + scalar27;
               trianglePointsX2[loopIndex5] = readSignedSmart10;
               trianglePointsY2[loopIndex5] = readSignedSmart11;
               trianglePointsZ2[loopIndex5] = readSignedSmart12;
            }
         }

         buffer7.currentPosition = currentPosition31;
         buffer8.currentPosition = currentPosition32;
         buffer9.currentPosition = currentPosition33;
         buffer10.currentPosition = currentPosition34;
         buffer11.currentPosition = currentPosition35;
         buffer12.currentPosition = currentPosition36;

         for (int loopIndex6 = 0; loopIndex6 < decodedUnsignedByte2; loopIndex6 += 1) {
            int scalar29;
            if ((scalar29 = byteBuffer9[loopIndex6] & 255) == 0) {
               values17[loopIndex6] = buffer7.readUnsignedShort();
               values18[loopIndex6] = buffer7.readUnsignedShort();
               values19[loopIndex6] = buffer7.readUnsignedShort();
            }

            if (scalar29 == 1) {
               values17[loopIndex6] = buffer8.readUnsignedShort();
               values18[loopIndex6] = buffer8.readUnsignedShort();
               values19[loopIndex6] = buffer8.readUnsignedShort();
               values10[loopIndex6] = buffer9.readUnsignedShort();
               values12[loopIndex6] = buffer9.readUnsignedShort();
               values11[loopIndex6] = buffer9.readUnsignedShort();
               byteBuffer13[loopIndex6] = buffer10.readByte();
               byteBuffer14[loopIndex6] = buffer11.readByte();
               byteBuffer11[loopIndex6] = buffer12.readByte();
            }

            if (scalar29 == 2) {
               values17[loopIndex6] = buffer8.readUnsignedShort();
               values18[loopIndex6] = buffer8.readUnsignedShort();
               values19[loopIndex6] = buffer8.readUnsignedShort();
               values10[loopIndex6] = buffer9.readUnsignedShort();
               values12[loopIndex6] = buffer9.readUnsignedShort();
               values11[loopIndex6] = buffer9.readUnsignedShort();
               byteBuffer13[loopIndex6] = buffer10.readByte();
               byteBuffer14[loopIndex6] = buffer11.readByte();
               byteBuffer11[loopIndex6] = buffer12.readByte();
               byteBuffer12[loopIndex6] = buffer12.readByte();
               byteBuffer10[loopIndex6] = buffer12.readByte();
            }

            if (scalar29 == 3) {
               values17[loopIndex6] = buffer8.readUnsignedShort();
               values18[loopIndex6] = buffer8.readUnsignedShort();
               values19[loopIndex6] = buffer8.readUnsignedShort();
               values10[loopIndex6] = buffer9.readUnsignedShort();
               values12[loopIndex6] = buffer9.readUnsignedShort();
               values11[loopIndex6] = buffer9.readUnsignedShort();
               byteBuffer13[loopIndex6] = buffer10.readByte();
               byteBuffer14[loopIndex6] = buffer11.readByte();
               byteBuffer11[loopIndex6] = buffer12.readByte();
            }
         }

         if (trianglePriorityOrReadUnsignedByte != 255) {
            for (int trianglePriorityIndex2 = 0; trianglePriorityIndex2 < decodedUnsignedShort3; trianglePriorityIndex2 += 1) {
               sourceModel.trianglePriorities[trianglePriorityIndex2] = trianglePriorityOrReadUnsignedByte;
            }
         }

         sourceModel.triangleColorValues = triangleColorValues2;
         sourceModel.vertexCount = decodedUnsignedShort2;
         sourceModel.triangleCount = decodedUnsignedShort3;
         sourceModel.verticesX = values14;
         sourceModel.verticesY = values15;
         sourceModel.verticesZ = values16;
         sourceModel.trianglePointsX = trianglePointsX2;
         sourceModel.trianglePointsY = trianglePointsY2;
         sourceModel.trianglePointsZ = trianglePointsZ2;
         sourceModel.buildTextureMappings(values13, values17, values18, values19, byteBuffer8);
      } else {
         int modelHeaderIndex3 = modelHeaderIndex2;
         Model model2 = this;
         this.oldFormat = true;
         model2.singleTile = false;
         oldFormatModelCount++;
         ModelHeader modelHeader2 = modelHeaders[modelHeaderIndex3];
         model2.vertexCount = modelHeader2.vertexCount;
         model2.triangleCount = modelHeader2.triangleCount;
         model2.texturedTriangleCount = modelHeader2.texturedTriangleCount;
         model2.verticesX = new int[model2.vertexCount];
         model2.verticesY = new int[model2.vertexCount];
         model2.verticesZ = new int[model2.vertexCount];
         model2.trianglePointsX = new int[model2.triangleCount];
         model2.trianglePointsY = new int[model2.triangleCount];
         model2.trianglePointsZ = new int[model2.triangleCount];
         model2.texturedTrianglePointsX = new int[model2.texturedTriangleCount];
         model2.texturedTrianglePointsY = new int[model2.texturedTriangleCount];
         model2.texturedTrianglePointsZ = new int[model2.texturedTriangleCount];
         if (modelHeader2.vertexSkinOffset >= 0) {
            model2.vertexSkins = new int[model2.vertexCount];
         }

         if (modelHeader2.triangleDrawTypeOffset >= 0) {
            model2.triangleDrawType = new int[model2.triangleCount];
         }

         if (modelHeader2.trianglePriorityOffset >= 0) {
            model2.trianglePriorities = new int[model2.triangleCount];
         } else {
            model2.trianglePriority = -modelHeader2.trianglePriorityOffset - 1;
         }

         if (modelHeader2.triangleAlphaOffset >= 0) {
            model2.triangleAlphaValues = new int[model2.triangleCount];
         }

         if (modelHeader2.triangleSkinOffset >= 0) {
            model2.triangleSkinValues = new int[model2.triangleCount];
         }

         model2.triangleColorValues = new int[model2.triangleCount];
         Buffer buffer14;
         (buffer14 = new Buffer(modelHeader2.data)).currentPosition = 0;
         Buffer buffer15;
         (buffer15 = new Buffer(modelHeader2.data)).currentPosition = modelHeader2.vertexXDataOffset;
         Buffer buffer16;
         (buffer16 = new Buffer(modelHeader2.data)).currentPosition = modelHeader2.vertexYDataOffset;
         Buffer buffer17;
         (buffer17 = new Buffer(modelHeader2.data)).currentPosition = modelHeader2.vertexZDataOffset;
         Buffer buffer18;
         (buffer18 = new Buffer(modelHeader2.data)).currentPosition = modelHeader2.vertexSkinOffset;
         int localVerticesX = 0;
         int localVerticesY = 0;
         int localVerticesZ = 0;

         for (int verticesXIndex = 0; verticesXIndex < model2.vertexCount; verticesXIndex++) {
            int readUnsignedByte16 = buffer14.readUnsignedByte();
            int readSignedSmart13 = 0;
            if ((readUnsignedByte16 & 1) != 0) {
               readSignedSmart13 = buffer15.readSignedSmart();
            }

            int readSignedSmart14 = 0;
            if ((readUnsignedByte16 & 2) != 0) {
               readSignedSmart14 = buffer16.readSignedSmart();
            }

            int readSignedSmart15 = 0;
            if ((readUnsignedByte16 & 4) != 0) {
               readSignedSmart15 = buffer17.readSignedSmart();
            }

            model2.verticesX[verticesXIndex] = localVerticesX + readSignedSmart13;
            model2.verticesY[verticesXIndex] = localVerticesY + readSignedSmart14;
            model2.verticesZ[verticesXIndex] = localVerticesZ + readSignedSmart15;
            localVerticesX = model2.verticesX[verticesXIndex];
            localVerticesY = model2.verticesY[verticesXIndex];
            localVerticesZ = model2.verticesZ[verticesXIndex];
            if (model2.vertexSkins != null) {
               model2.vertexSkins[verticesXIndex] = buffer18.readUnsignedByte();
            }
         }

         buffer14.currentPosition = modelHeader2.triangleColorOffset;
         buffer15.currentPosition = modelHeader2.triangleDrawTypeOffset;
         buffer16.currentPosition = modelHeader2.trianglePriorityOffset;
         buffer17.currentPosition = modelHeader2.triangleAlphaOffset;
         buffer18.currentPosition = modelHeader2.triangleSkinOffset;

         for (int triangleColorValueIndex = 0; triangleColorValueIndex < model2.triangleCount; triangleColorValueIndex++) {
            model2.triangleColorValues[triangleColorValueIndex] = buffer14.readUnsignedShort();
            if (model2.triangleDrawType != null) {
               model2.triangleDrawType[triangleColorValueIndex] = buffer15.readUnsignedByte();
            }

            if (model2.trianglePriorities != null) {
               model2.trianglePriorities[triangleColorValueIndex] = buffer16.readUnsignedByte();
            }

            if (model2.triangleAlphaValues != null) {
               model2.triangleAlphaValues[triangleColorValueIndex] = buffer17.readUnsignedByte();
            }

            if (model2.triangleSkinValues != null) {
               model2.triangleSkinValues[triangleColorValueIndex] = buffer18.readUnsignedByte();
            }
         }

         if (model2.triangleAlphaValues == null) {
            model2.triangleAlphaValues = new int[model2.triangleCount];
         }

         buffer14.currentPosition = modelHeader2.triangleIndexDataOffset;
         buffer15.currentPosition = modelHeader2.triangleIndexTypeOffset;
         int trianglePointsXOrReadSignedSmart = 0;
         int trianglePointsYOrReadSignedSmart = 0;
         int trianglePointsZOrReadSignedSmart = 0;
         int scalar30 = 0;

         for (int trianglePointsXIndex = 0; trianglePointsXIndex < model2.triangleCount; trianglePointsXIndex++) {
            int readUnsignedByte17;
            if ((readUnsignedByte17 = buffer15.readUnsignedByte()) == 1) {
               scalar30 = trianglePointsXOrReadSignedSmart = buffer14.readSignedSmart() + scalar30;
               scalar30 = trianglePointsYOrReadSignedSmart = buffer14.readSignedSmart() + scalar30;
               scalar30 = trianglePointsZOrReadSignedSmart = buffer14.readSignedSmart() + scalar30;
               model2.trianglePointsX[trianglePointsXIndex] = trianglePointsXOrReadSignedSmart;
               model2.trianglePointsY[trianglePointsXIndex] = trianglePointsYOrReadSignedSmart;
               model2.trianglePointsZ[trianglePointsXIndex] = trianglePointsZOrReadSignedSmart;
            }

            if (readUnsignedByte17 == 2) {
               trianglePointsYOrReadSignedSmart = trianglePointsZOrReadSignedSmart;
               scalar30 = trianglePointsZOrReadSignedSmart = buffer14.readSignedSmart() + scalar30;
               model2.trianglePointsX[trianglePointsXIndex] = trianglePointsXOrReadSignedSmart;
               model2.trianglePointsY[trianglePointsXIndex] = trianglePointsYOrReadSignedSmart;
               model2.trianglePointsZ[trianglePointsXIndex] = trianglePointsZOrReadSignedSmart;
            }

            if (readUnsignedByte17 == 3) {
               trianglePointsXOrReadSignedSmart = trianglePointsZOrReadSignedSmart;
               scalar30 = trianglePointsZOrReadSignedSmart = buffer14.readSignedSmart() + scalar30;
               model2.trianglePointsX[trianglePointsXIndex] = trianglePointsXOrReadSignedSmart;
               model2.trianglePointsY[trianglePointsXIndex] = trianglePointsYOrReadSignedSmart;
               model2.trianglePointsZ[trianglePointsXIndex] = trianglePointsZOrReadSignedSmart;
            }

            if (readUnsignedByte17 == 4) {
               int scalar31 = trianglePointsXOrReadSignedSmart;
               trianglePointsXOrReadSignedSmart = trianglePointsYOrReadSignedSmart;
               trianglePointsYOrReadSignedSmart = scalar31;
               scalar30 = trianglePointsZOrReadSignedSmart = buffer14.readSignedSmart() + scalar30;
               model2.trianglePointsX[trianglePointsXIndex] = trianglePointsXOrReadSignedSmart;
               model2.trianglePointsY[trianglePointsXIndex] = trianglePointsYOrReadSignedSmart;
               model2.trianglePointsZ[trianglePointsXIndex] = trianglePointsZOrReadSignedSmart;
            }
         }

         buffer14.currentPosition = modelHeader2.texturedTriangleDataOffset;

         for (int texturedTrianglePointsXIndex = 0; texturedTrianglePointsXIndex < model2.texturedTriangleCount; texturedTrianglePointsXIndex++) {
            model2.texturedTrianglePointsX[texturedTrianglePointsXIndex] = buffer14.readUnsignedShort();
            model2.texturedTrianglePointsY[texturedTrianglePointsXIndex] = buffer14.readUnsignedShort();
            model2.texturedTrianglePointsZ[texturedTrianglePointsXIndex] = buffer14.readUnsignedShort();
         }
      }
   }
   public static void clearModelLoader() {
      modelHeaders = null;
      triangleEdgeRestricted = null;
      triangleNearClipped = null;
      projectedX = null;
      projectedY = null;
      vertexDepthOffset = null;
      cameraX = null;
      cameraY = null;
      cameraZ = null;
      depthBucketCounts = null;
      depthBuckets = null;
      priorityCounts = null;
      priorityTriangles = null;
      priority10Depths = null;
      priority11Depths = null;
      priorityDepthSums = null;
      SINE = null;
      COSINE = null;
      HSL_TO_RGB = null;
      RECIPROCAL_2048 = null;
   }
   public static void initializeModelCache(int length, OnDemandFetcherBase onDemandFetcherBase) {
      modelHeaders = new ModelHeader[length];
      onDemandFetcher = onDemandFetcherBase;
   }
   public static void decodeModelHeader(byte[] newCurrentPosition, int modelHeaderIndex) {
      if (newCurrentPosition == null) {
         ModelHeader modelHeader;
         (modelHeader = modelHeaders[modelHeaderIndex] = new ModelHeader()).vertexCount = 0;
         modelHeader.triangleCount = 0;
         modelHeader.texturedTriangleCount = 0;
      } else {
         Buffer buffer;
         (buffer = new Buffer(newCurrentPosition)).currentPosition = newCurrentPosition.length - 18;
         ModelHeader modelHeader2;
         (modelHeader2 = modelHeaders[modelHeaderIndex] = new ModelHeader()).data = newCurrentPosition;
         modelHeader2.vertexCount = buffer.readUnsignedShort();
         modelHeader2.triangleCount = buffer.readUnsignedShort();
         modelHeader2.texturedTriangleCount = buffer.readUnsignedByte();
         int decodedUnsignedByte = buffer.readUnsignedByte();
         int readUnsignedByte2 = buffer.readUnsignedByte();
         int readUnsignedByte3 = buffer.readUnsignedByte();
         int readUnsignedByte4 = buffer.readUnsignedByte();
         int readUnsignedByte5 = buffer.readUnsignedByte();
         int decodedUnsignedShort = buffer.readUnsignedShort();
         int readUnsignedShort2 = buffer.readUnsignedShort();
         buffer.readUnsignedShort();
         int readUnsignedShort3 = buffer.readUnsignedShort();
         modelHeader2.vertexFlagsOffset = 0;
         int localTriangleIndexTypeOffset = 0 + modelHeader2.vertexCount;
         modelHeader2.triangleIndexTypeOffset = localTriangleIndexTypeOffset;
         localTriangleIndexTypeOffset += modelHeader2.triangleCount;
         modelHeader2.trianglePriorityOffset = localTriangleIndexTypeOffset;
         if (readUnsignedByte2 == 255) {
            localTriangleIndexTypeOffset += modelHeader2.triangleCount;
         } else {
            modelHeader2.trianglePriorityOffset = -readUnsignedByte2 - 1;
         }

         modelHeader2.triangleSkinOffset = localTriangleIndexTypeOffset;
         if (readUnsignedByte4 == 1) {
            localTriangleIndexTypeOffset += modelHeader2.triangleCount;
         } else {
            modelHeader2.triangleSkinOffset = -1;
         }

         modelHeader2.triangleDrawTypeOffset = localTriangleIndexTypeOffset;
         if (decodedUnsignedByte == 1) {
            localTriangleIndexTypeOffset += modelHeader2.triangleCount;
         } else {
            modelHeader2.triangleDrawTypeOffset = -1;
         }

         modelHeader2.vertexSkinOffset = localTriangleIndexTypeOffset;
         if (readUnsignedByte5 == 1) {
            localTriangleIndexTypeOffset += modelHeader2.vertexCount;
         } else {
            modelHeader2.vertexSkinOffset = -1;
         }

         modelHeader2.triangleAlphaOffset = localTriangleIndexTypeOffset;
         if (readUnsignedByte3 == 1) {
            localTriangleIndexTypeOffset += modelHeader2.triangleCount;
         } else {
            modelHeader2.triangleAlphaOffset = -1;
         }

         modelHeader2.triangleIndexDataOffset = localTriangleIndexTypeOffset;
         localTriangleIndexTypeOffset += readUnsignedShort3;
         modelHeader2.triangleColorOffset = localTriangleIndexTypeOffset;
         localTriangleIndexTypeOffset += modelHeader2.triangleCount << 1;
         modelHeader2.texturedTriangleDataOffset = localTriangleIndexTypeOffset;
         localTriangleIndexTypeOffset += modelHeader2.texturedTriangleCount * 6;
         modelHeader2.vertexXDataOffset = localTriangleIndexTypeOffset;
         localTriangleIndexTypeOffset += decodedUnsignedShort;
         modelHeader2.vertexYDataOffset = localTriangleIndexTypeOffset;
         localTriangleIndexTypeOffset += readUnsignedShort2;
         modelHeader2.vertexZDataOffset = localTriangleIndexTypeOffset;
      }
   }
   public static Model getModel(int modelHeaderIndex) {
      if (modelHeaders == null) {
         return null;
      } else {
         ModelHeader modelHeader;
         if ((modelHeader = modelHeaders[modelHeaderIndex]) == null) {
            onDemandFetcher.provide(modelHeaderIndex);
            return null;
         } else {
            return new Model(modelHeaderIndex);
         }
      }
   }
   public static boolean isCached(int modelHeaderIndex) {
      if (modelHeaders == null) {
         return false;
      } else {
         ModelHeader modelHeader;
         if ((modelHeader = modelHeaders[modelHeaderIndex]) == null) {
            onDemandFetcher.provide(modelHeaderIndex);
            return false;
         } else {
            return true;
         }
      }
   }

   private Model() {
      this.singleTile = false;
   }

   public Model(int loopIndex, Model[] values) {
      this.singleTile = false;
      boolean flag = false;
      boolean localFlag = false;
      boolean flag2 = false;
      boolean flag3 = false;
      this.vertexCount = 0;
      this.triangleCount = 0;
      this.texturedTriangleCount = 0;
      this.trianglePriority = -1;

      for (int loopIndex2 = 0; loopIndex2 < loopIndex; loopIndex2++) {
         Model model;
         if ((model = values[loopIndex2]) != null) {
            this.vertexCount = this.vertexCount + model.vertexCount;
            this.triangleCount = this.triangleCount + model.triangleCount;
            this.texturedTriangleCount = this.texturedTriangleCount + model.texturedTriangleCount;
            flag |= model.triangleDrawType != null;
            if (model.trianglePriorities != null) {
               localFlag = true;
            } else {
               if (this.trianglePriority == -1) {
                  this.trianglePriority = model.trianglePriority;
               }

               if (this.trianglePriority != model.trianglePriority) {
                  localFlag = true;
               }
            }

            flag2 |= model.triangleAlphaValues != null;
            flag3 |= model.triangleSkinValues != null;
         }
      }

      this.verticesX = new int[this.vertexCount];
      this.verticesY = new int[this.vertexCount];
      this.verticesZ = new int[this.vertexCount];
      this.vertexSkins = new int[this.vertexCount];
      this.trianglePointsX = new int[this.triangleCount];
      this.trianglePointsY = new int[this.triangleCount];
      this.trianglePointsZ = new int[this.triangleCount];
      this.texturedTrianglePointsX = new int[this.texturedTriangleCount];
      this.texturedTrianglePointsY = new int[this.texturedTriangleCount];
      this.texturedTrianglePointsZ = new int[this.texturedTriangleCount];
      if (flag) {
         this.triangleDrawType = new int[this.triangleCount];
      }

      if (localFlag) {
         this.trianglePriorities = new int[this.triangleCount];
      }

      if (flag2) {
         this.triangleAlphaValues = new int[this.triangleCount];
      }

      if (flag3) {
         this.triangleSkinValues = new int[this.triangleCount];
      }

      this.triangleColorValues = new int[this.triangleCount];
      this.vertexCount = 0;
      this.triangleCount = 0;
      this.texturedTriangleCount = 0;
      int scalar = 0;

      for (int loopIndex3 = 0; loopIndex3 < loopIndex; loopIndex3++) {
         Model model2;
         if ((model2 = values[loopIndex3]) != null) {
            for (int triangleDrawTypeIndex = 0; triangleDrawTypeIndex < model2.triangleCount; triangleDrawTypeIndex++) {
               if (flag) {
                  if (model2.triangleDrawType == null) {
                     this.triangleDrawType[this.triangleCount] = 0;
                  } else {
                     int localTriangleDrawType;
                     if (((localTriangleDrawType = model2.triangleDrawType[triangleDrawTypeIndex]) & 2) == 2) {
                        localTriangleDrawType += scalar << 2;
                     }

                     this.triangleDrawType[this.triangleCount] = localTriangleDrawType;
                  }
               }

               if (localFlag) {
                  if (model2.trianglePriorities == null) {
                     this.trianglePriorities[this.triangleCount] = model2.trianglePriority;
                  } else {
                     this.trianglePriorities[this.triangleCount] = model2.trianglePriorities[triangleDrawTypeIndex];
                  }
               }

               if (flag2) {
                  if (model2.triangleAlphaValues == null) {
                     this.triangleAlphaValues[this.triangleCount] = 0;
                  } else {
                     this.triangleAlphaValues[this.triangleCount] = model2.triangleAlphaValues[triangleDrawTypeIndex];
                  }
               }

               if (flag3 && model2.triangleSkinValues != null) {
                  this.triangleSkinValues[this.triangleCount] = model2.triangleSkinValues[triangleDrawTypeIndex];
               }

               this.triangleColorValues[this.triangleCount] = model2.triangleColorValues[triangleDrawTypeIndex];
               this.trianglePointsX[this.triangleCount] = this.getOrAddVertex(model2, model2.trianglePointsX[triangleDrawTypeIndex]);
               this.trianglePointsY[this.triangleCount] = this.getOrAddVertex(model2, model2.trianglePointsY[triangleDrawTypeIndex]);
               this.trianglePointsZ[this.triangleCount] = this.getOrAddVertex(model2, model2.trianglePointsZ[triangleDrawTypeIndex]);
               this.triangleCount++;
            }

            for (int texturedTrianglePointsXIndex = 0; texturedTrianglePointsXIndex < model2.texturedTriangleCount; texturedTrianglePointsXIndex++) {
               this.texturedTrianglePointsX[this.texturedTriangleCount] = this.getOrAddVertex(model2, model2.texturedTrianglePointsX[texturedTrianglePointsXIndex]);
               this.texturedTrianglePointsY[this.texturedTriangleCount] = this.getOrAddVertex(model2, model2.texturedTrianglePointsY[texturedTrianglePointsXIndex]);
               this.texturedTrianglePointsZ[this.texturedTriangleCount] = this.getOrAddVertex(model2, model2.texturedTrianglePointsZ[texturedTrianglePointsXIndex]);
               this.texturedTriangleCount++;
            }

            scalar += model2.texturedTriangleCount;
         }
      }
   }

   public Model(Model[] values) {
      this.singleTile = false;
      boolean flag = false;
      boolean localFlag = false;
      boolean flag2 = false;
      boolean flag3 = false;
      this.vertexCount = 0;
      this.triangleCount = 0;
      this.texturedTriangleCount = 0;
      this.trianglePriority = -1;

      for (int loopIndex = 0; loopIndex < 2; loopIndex++) {
         Model model;
         if ((model = values[loopIndex]) != null) {
            this.vertexCount = this.vertexCount + model.vertexCount;
            this.triangleCount = this.triangleCount + model.triangleCount;
            this.texturedTriangleCount = this.texturedTriangleCount + model.texturedTriangleCount;
            flag |= model.triangleDrawType != null;
            if (model.trianglePriorities != null) {
               localFlag = true;
            } else {
               if (this.trianglePriority == -1) {
                  this.trianglePriority = model.trianglePriority;
               }

               if (this.trianglePriority != model.trianglePriority) {
                  localFlag = true;
               }
            }

            flag2 |= model.triangleAlphaValues != null;
            flag3 |= model.triangleColorValues != null;
         }
      }

      this.verticesX = new int[this.vertexCount];
      this.verticesY = new int[this.vertexCount];
      this.verticesZ = new int[this.vertexCount];
      this.trianglePointsX = new int[this.triangleCount];
      this.trianglePointsY = new int[this.triangleCount];
      this.trianglePointsZ = new int[this.triangleCount];
      this.triangleHSLA = new int[this.triangleCount];
      this.triangleHSLB = new int[this.triangleCount];
      this.triangleHSLC = new int[this.triangleCount];
      this.texturedTrianglePointsX = new int[this.texturedTriangleCount];
      this.texturedTrianglePointsY = new int[this.texturedTriangleCount];
      this.texturedTrianglePointsZ = new int[this.texturedTriangleCount];
      if (flag) {
         this.triangleDrawType = new int[this.triangleCount];
      }

      if (localFlag) {
         this.trianglePriorities = new int[this.triangleCount];
      }

      if (flag2) {
         this.triangleAlphaValues = new int[this.triangleCount];
      }

      if (flag3) {
         this.triangleColorValues = new int[this.triangleCount];
      }

      this.vertexCount = 0;
      this.triangleCount = 0;
      this.texturedTriangleCount = 0;
      int scalar = 0;

      for (int loopIndex2 = 0; loopIndex2 < 2; loopIndex2++) {
         Model model2;
         if ((model2 = values[loopIndex2]) != null) {
            int vertexCount = this.vertexCount;

            for (int verticesXIndex = 0; verticesXIndex < model2.vertexCount; verticesXIndex++) {
               this.verticesX[this.vertexCount] = model2.verticesX[verticesXIndex];
               this.verticesY[this.vertexCount] = model2.verticesY[verticesXIndex];
               this.verticesZ[this.vertexCount] = model2.verticesZ[verticesXIndex];
               this.vertexCount++;
            }

            for (int trianglePointsXIndex = 0; trianglePointsXIndex < model2.triangleCount; trianglePointsXIndex++) {
               this.trianglePointsX[this.triangleCount] = model2.trianglePointsX[trianglePointsXIndex] + vertexCount;
               this.trianglePointsY[this.triangleCount] = model2.trianglePointsY[trianglePointsXIndex] + vertexCount;
               this.trianglePointsZ[this.triangleCount] = model2.trianglePointsZ[trianglePointsXIndex] + vertexCount;
               this.triangleHSLA[this.triangleCount] = model2.triangleHSLA[trianglePointsXIndex];
               this.triangleHSLB[this.triangleCount] = model2.triangleHSLB[trianglePointsXIndex];
               this.triangleHSLC[this.triangleCount] = model2.triangleHSLC[trianglePointsXIndex];
               if (flag) {
                  if (model2.triangleDrawType == null) {
                     this.triangleDrawType[this.triangleCount] = 0;
                  } else {
                     int localTriangleDrawType;
                     if (((localTriangleDrawType = model2.triangleDrawType[trianglePointsXIndex]) & 2) == 2) {
                        localTriangleDrawType += scalar << 2;
                     }

                     this.triangleDrawType[this.triangleCount] = localTriangleDrawType;
                  }
               }

               if (localFlag) {
                  if (model2.trianglePriorities == null) {
                     this.trianglePriorities[this.triangleCount] = model2.trianglePriority;
                  } else {
                     this.trianglePriorities[this.triangleCount] = model2.trianglePriorities[trianglePointsXIndex];
                  }
               }

               if (flag2) {
                  if (model2.triangleAlphaValues == null) {
                     this.triangleAlphaValues[this.triangleCount] = 0;
                  } else {
                     this.triangleAlphaValues[this.triangleCount] = model2.triangleAlphaValues[trianglePointsXIndex];
                  }
               }

               if (flag3 && model2.triangleColorValues != null) {
                  this.triangleColorValues[this.triangleCount] = model2.triangleColorValues[trianglePointsXIndex];
               }

               this.triangleCount++;
            }

            for (int texturedTrianglePointsXIndex = 0; texturedTrianglePointsXIndex < model2.texturedTriangleCount; texturedTrianglePointsXIndex++) {
               this.texturedTrianglePointsX[this.texturedTriangleCount] = model2.texturedTrianglePointsX[texturedTrianglePointsXIndex] + vertexCount;
               this.texturedTrianglePointsY[this.texturedTriangleCount] = model2.texturedTrianglePointsY[texturedTrianglePointsXIndex] + vertexCount;
               this.texturedTrianglePointsZ[this.texturedTriangleCount] = model2.texturedTrianglePointsZ[texturedTrianglePointsXIndex] + vertexCount;
               this.texturedTriangleCount++;
            }

            scalar += model2.texturedTriangleCount;
         }
      }

      this.calculateBoundsCylinder();
   }

   public Model(boolean flag, boolean newFlag, boolean flag2, Model model) {
      this.singleTile = false;
      this.vertexCount = model.vertexCount;
      this.triangleCount = model.triangleCount;
      this.texturedTriangleCount = model.texturedTriangleCount;
      if (flag2) {
         this.verticesX = model.verticesX;
         this.verticesY = model.verticesY;
         this.verticesZ = model.verticesZ;
      } else {
         this.verticesX = new int[this.vertexCount];
         this.verticesY = new int[this.vertexCount];
         this.verticesZ = new int[this.vertexCount];

         for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
            this.verticesX[verticesXIndex] = model.verticesX[verticesXIndex];
            this.verticesY[verticesXIndex] = model.verticesY[verticesXIndex];
            this.verticesZ[verticesXIndex] = model.verticesZ[verticesXIndex];
         }
      }

      if (flag) {
         this.triangleColorValues = model.triangleColorValues;
      } else {
         this.triangleColorValues = new int[this.triangleCount];
         System.arraycopy(model.triangleColorValues, 0, this.triangleColorValues, 0, this.triangleCount);
      }

      if (newFlag) {
         this.triangleAlphaValues = model.triangleAlphaValues;
      } else {
         this.triangleAlphaValues = new int[this.triangleCount];
         if (model.triangleAlphaValues == null) {
            for (int triangleAlphaValueIndex = 0; triangleAlphaValueIndex < this.triangleCount; triangleAlphaValueIndex++) {
               this.triangleAlphaValues[triangleAlphaValueIndex] = 0;
            }
         } else {
            System.arraycopy(model.triangleAlphaValues, 0, this.triangleAlphaValues, 0, this.triangleCount);
         }
      }

      this.vertexSkins = model.vertexSkins;
      this.triangleSkinValues = model.triangleSkinValues;
      this.triangleDrawType = model.triangleDrawType;
      this.trianglePointsX = model.trianglePointsX;
      this.trianglePointsY = model.trianglePointsY;
      this.trianglePointsZ = model.trianglePointsZ;
      this.trianglePriorities = model.trianglePriorities;
      this.trianglePriority = model.trianglePriority;
      this.texturedTrianglePointsX = model.texturedTrianglePointsX;
      this.texturedTrianglePointsY = model.texturedTrianglePointsY;
      this.texturedTrianglePointsZ = model.texturedTrianglePointsZ;
   }

   public Model(boolean flag, boolean newFlag, Model model) {
      this.singleTile = false;
      this.vertexCount = model.vertexCount;
      this.triangleCount = model.triangleCount;
      this.texturedTriangleCount = model.texturedTriangleCount;
      if (flag) {
         this.verticesY = new int[this.vertexCount];
         System.arraycopy(model.verticesY, 0, this.verticesY, 0, this.vertexCount);
      } else {
         this.verticesY = model.verticesY;
      }

      if (newFlag) {
         this.triangleHSLA = new int[this.triangleCount];
         this.triangleHSLB = new int[this.triangleCount];
         this.triangleHSLC = new int[this.triangleCount];

         for (int triangleHSLAIndex = 0; triangleHSLAIndex < this.triangleCount; triangleHSLAIndex++) {
            this.triangleHSLA[triangleHSLAIndex] = model.triangleHSLA[triangleHSLAIndex];
            this.triangleHSLB[triangleHSLAIndex] = model.triangleHSLB[triangleHSLAIndex];
            this.triangleHSLC[triangleHSLAIndex] = model.triangleHSLC[triangleHSLAIndex];
         }

         this.triangleDrawType = new int[this.triangleCount];
         if (model.triangleDrawType == null) {
            for (int triangleDrawTypeIndex = 0; triangleDrawTypeIndex < this.triangleCount; triangleDrawTypeIndex++) {
               this.triangleDrawType[triangleDrawTypeIndex] = 0;
            }
         } else {
            System.arraycopy(model.triangleDrawType, 0, this.triangleDrawType, 0, this.triangleCount);
         }

         super.vertexNormals = new VertexNormal[this.vertexCount];

         for (int vertexNormalIndex = 0; vertexNormalIndex < this.vertexCount; vertexNormalIndex++) {
            VertexNormal vertexNormal = super.vertexNormals[vertexNormalIndex] = new VertexNormal();
            VertexNormal vertexNormal2 = model.vertexNormals[vertexNormalIndex];
            vertexNormal.x = vertexNormal2.x;
            vertexNormal.y = vertexNormal2.y;
            vertexNormal.z = vertexNormal2.z;
            vertexNormal.magnitude = vertexNormal2.magnitude;
         }

         this.vertexNormalOffset = model.vertexNormalOffset;
      } else {
         this.triangleHSLA = model.triangleHSLA;
         this.triangleHSLB = model.triangleHSLB;
         this.triangleHSLC = model.triangleHSLC;
         this.triangleDrawType = model.triangleDrawType;
      }

      this.verticesX = model.verticesX;
      this.verticesZ = model.verticesZ;
      this.triangleColorValues = model.triangleColorValues;
      this.triangleAlphaValues = model.triangleAlphaValues;
      this.trianglePriorities = model.trianglePriorities;
      this.trianglePriority = model.trianglePriority;
      this.trianglePointsX = model.trianglePointsX;
      this.trianglePointsY = model.trianglePointsY;
      this.trianglePointsZ = model.trianglePointsZ;
      this.texturedTrianglePointsX = model.texturedTrianglePointsX;
      this.texturedTrianglePointsY = model.texturedTrianglePointsY;
      this.texturedTrianglePointsZ = model.texturedTrianglePointsZ;
      super.modelHeight = model.modelHeight;
      this.maxY = model.maxY;
      this.diagonal2DAboveOrigin = model.diagonal2DAboveOrigin;
      this.diagonal3DAboveOrigin = model.diagonal3DAboveOrigin;
      this.diagonal3D = model.diagonal3D;
      this.minX = model.minX;
      this.maxZ = model.maxZ;
      this.minZ = model.minZ;
      this.maxX = model.maxX;
   }
   public final void copyForAnimation(Model model, boolean flag) {
      this.vertexCount = model.vertexCount;
      this.triangleCount = model.triangleCount;
      this.texturedTriangleCount = model.texturedTriangleCount;
      if (sharedVerticesX.length < this.vertexCount) {
         sharedVerticesX = new int[this.vertexCount + 100];
         sharedVerticesY = new int[this.vertexCount + 100];
         sharedVerticesZ = new int[this.vertexCount + 100];
      }

      this.verticesX = sharedVerticesX;
      this.verticesY = sharedVerticesY;
      this.verticesZ = sharedVerticesZ;

      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         this.verticesX[verticesXIndex] = model.verticesX[verticesXIndex];
         this.verticesY[verticesXIndex] = model.verticesY[verticesXIndex];
         this.verticesZ[verticesXIndex] = model.verticesZ[verticesXIndex];
      }

      if (flag) {
         this.triangleAlphaValues = model.triangleAlphaValues;
      } else {
         if (sharedTriangleAlpha.length < this.triangleCount) {
            sharedTriangleAlpha = new int[this.triangleCount + 100];
         }

         this.triangleAlphaValues = sharedTriangleAlpha;
         if (model.triangleAlphaValues == null) {
            for (int triangleAlphaValueIndex = 0; triangleAlphaValueIndex < this.triangleCount; triangleAlphaValueIndex++) {
               this.triangleAlphaValues[triangleAlphaValueIndex] = 0;
            }
         } else {
            System.arraycopy(model.triangleAlphaValues, 0, this.triangleAlphaValues, 0, this.triangleCount);
         }
      }

      this.triangleDrawType = model.triangleDrawType;
      this.triangleColorValues = model.triangleColorValues;
      this.trianglePriorities = model.trianglePriorities;
      this.trianglePriority = model.trianglePriority;
      this.triangleSkin = model.triangleSkin;
      this.vectorSkin = model.vectorSkin;
      this.trianglePointsX = model.trianglePointsX;
      this.trianglePointsY = model.trianglePointsY;
      this.trianglePointsZ = model.trianglePointsZ;
      this.triangleHSLA = model.triangleHSLA;
      this.triangleHSLB = model.triangleHSLB;
      this.triangleHSLC = model.triangleHSLC;
      this.texturedTrianglePointsX = model.texturedTrianglePointsX;
      this.texturedTrianglePointsY = model.texturedTrianglePointsY;
      this.texturedTrianglePointsZ = model.texturedTrianglePointsZ;
   }
   private int getOrAddVertex(Model model, int verticesXIndex2) {
      int sourceVerticesXIndex = -1;
      int verticesXEntry = model.verticesX[verticesXIndex2];
      int verticesYEntry = model.verticesY[verticesXIndex2];
      int verticesZEntry = model.verticesZ[verticesXIndex2];

      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         if (verticesXEntry == this.verticesX[verticesXIndex] && verticesYEntry == this.verticesY[verticesXIndex] && verticesZEntry == this.verticesZ[verticesXIndex]) {
            sourceVerticesXIndex = verticesXIndex;
            break;
         }
      }

      if (sourceVerticesXIndex == -1) {
         this.verticesX[this.vertexCount] = verticesXEntry;
         this.verticesY[this.vertexCount] = verticesYEntry;
         this.verticesZ[this.vertexCount] = verticesZEntry;
         if (model.vertexSkins != null) {
            this.vertexSkins[this.vertexCount] = model.vertexSkins[verticesXIndex2];
         }

         sourceVerticesXIndex = this.vertexCount++;
      }

      return sourceVerticesXIndex;
   }
   public final void calculateBoundsCylinder() {
      super.modelHeight = 0;
      this.diagonal2DAboveOrigin = 0;
      this.maxY = 0;

      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         int verticesXEntry = this.verticesX[verticesXIndex];
         int maxYOrVerticesY = this.verticesY[verticesXIndex];
         int verticesZEntry = this.verticesZ[verticesXIndex];
         if (-maxYOrVerticesY > super.modelHeight) {
            super.modelHeight = -maxYOrVerticesY;
         }

         if (maxYOrVerticesY > this.maxY) {
            this.maxY = maxYOrVerticesY;
         }

         if ((verticesXEntry = verticesXEntry * verticesXEntry + verticesZEntry * verticesZEntry) > this.diagonal2DAboveOrigin) {
            this.diagonal2DAboveOrigin = verticesXEntry;
         }
      }

      this.diagonal2DAboveOrigin = (int)(Math.sqrt(this.diagonal2DAboveOrigin) + 0.99);
      this.diagonal3DAboveOrigin = (int)(Math.sqrt(this.diagonal2DAboveOrigin * this.diagonal2DAboveOrigin + super.modelHeight * super.modelHeight) + 0.99);
      this.diagonal3D = this.diagonal3DAboveOrigin + (int)(Math.sqrt(this.diagonal2DAboveOrigin * this.diagonal2DAboveOrigin + this.maxY * this.maxY) + 0.99);
   }
   public final void computeSphericalBounds() {
      super.modelHeight = 0;
      this.maxY = 0;

      for (int verticesYIndex = 0; verticesYIndex < this.vertexCount; verticesYIndex++) {
         int maxYOrVerticesY;
         if (-(maxYOrVerticesY = this.verticesY[verticesYIndex]) > super.modelHeight) {
            super.modelHeight = -maxYOrVerticesY;
         }

         if (maxYOrVerticesY > this.maxY) {
            this.maxY = maxYOrVerticesY;
         }
      }

      this.diagonal3DAboveOrigin = (int)(Math.sqrt(this.diagonal2DAboveOrigin * this.diagonal2DAboveOrigin + super.modelHeight * super.modelHeight) + 0.99);
      this.diagonal3D = this.diagonal3DAboveOrigin + (int)(Math.sqrt(this.diagonal2DAboveOrigin * this.diagonal2DAboveOrigin + this.maxY * this.maxY) + 0.99);
   }
   public final void skin() {
      if (this.vertexSkins != null) {
         int[] values = new int[256];
         int sourceVertexSkin = 0;

         for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
            int vertexSkin = this.vertexSkins[vertexIndex];
            values[vertexSkin]++;
            if (vertexSkin > sourceVertexSkin) {
               sourceVertexSkin = vertexSkin;
            }
         }

         this.vectorSkin = new int[sourceVertexSkin + 1][];

         for (int vectorSkinIndex = 0; vectorSkinIndex <= sourceVertexSkin; vectorSkinIndex++) {
            this.vectorSkin[vectorSkinIndex] = new int[values[vectorSkinIndex]];
            values[vectorSkinIndex] = 0;
         }

         int vertexSkinIndex = 0;

         while (vertexSkinIndex < this.vertexCount) {
            int vertexSkins2 = this.vertexSkins[vertexSkinIndex];
            this.vectorSkin[vertexSkins2][values[vertexSkins2]++] = vertexSkinIndex++;
         }

         this.vertexSkins = null;
      }

      if (this.triangleSkinValues != null) {
         int[] integerBuffer = new int[256];
         int loopIndex = 0;

         for (int triangleSkinValueIndex = 0; triangleSkinValueIndex < this.triangleCount; triangleSkinValueIndex++) {
            int triangleSkin = this.triangleSkinValues[triangleSkinValueIndex];
            integerBuffer[triangleSkin]++;
            if (triangleSkin > loopIndex) {
               loopIndex = triangleSkin;
            }
         }

         this.triangleSkin = new int[loopIndex + 1][];

         for (int triangleSkinIndex = 0; triangleSkinIndex <= loopIndex; triangleSkinIndex++) {
            this.triangleSkin[triangleSkinIndex] = new int[integerBuffer[triangleSkinIndex]];
            integerBuffer[triangleSkinIndex] = 0;
         }

         int triangleSkinValueIndex2 = 0;

         while (triangleSkinValueIndex2 < this.triangleCount) {
            int triangleSkinValues2 = this.triangleSkinValues[triangleSkinValueIndex2];
            this.triangleSkin[triangleSkinValues2][integerBuffer[triangleSkinValues2]++] = triangleSkinValueIndex2++;
         }

         this.triangleSkinValues = null;
      }
   }
   public final void applyAnimationFrame(int frameIndex) {
      AnimationFrame animationFrame;
      if (this.vectorSkin != null && frameIndex != -1 && (animationFrame = AnimationFrame.get(frameIndex)) != null) {
         AnimationSkeleton animationSkeleton = animationFrame.skeleton;
         transformOriginX = 0;
         transformOriginY = 0;
         transformOriginZ = 0;

         for (int transformIndex2 = 0; transformIndex2 < animationFrame.transformCount; transformIndex2++) {
            int transformIndex = animationFrame.transformIndices[transformIndex2];
            this.transformSkin(animationSkeleton.transformTypes[transformIndex], animationSkeleton.labels[transformIndex], animationFrame.transformX[transformIndex2], animationFrame.transformY[transformIndex2], animationFrame.transformZ[transformIndex2]);
         }
      }
   }
   public final void applyAnimationFrames(int[] values, int frameIndex, int frameIndex2) {
      if (frameIndex2 != -1) {
         if (values != null && frameIndex != -1) {
            AnimationFrame animationFrame;
            if ((animationFrame = AnimationFrame.get(frameIndex2)) == null) {
               return;
            }

            AnimationFrame animationFrame2;
            if ((animationFrame2 = AnimationFrame.get(frameIndex)) != null) {
               AnimationSkeleton animationSkeleton = animationFrame.skeleton;
               transformOriginX = 0;
               transformOriginY = 0;
               transformOriginZ = 0;
               int scalar = 1;
               int scalar2 = values[0];

               for (int transformIndex2 = 0; transformIndex2 < animationFrame.transformCount; transformIndex2++) {
                  int transformIndex = animationFrame.transformIndices[transformIndex2];

                  while (transformIndex > scalar2) {
                     scalar2 = values[scalar++];
                  }

                  if (transformIndex != scalar2 || animationSkeleton.transformTypes[transformIndex] == 0) {
                     this.transformSkin(animationSkeleton.transformTypes[transformIndex], animationSkeleton.labels[transformIndex], animationFrame.transformX[transformIndex2], animationFrame.transformY[transformIndex2], animationFrame.transformZ[transformIndex2]);
                  }
               }

               transformOriginX = 0;
               transformOriginY = 0;
               transformOriginZ = 0;
               scalar = 1;
               scalar2 = values[0];

               for (int transformIndex3 = 0; transformIndex3 < animationFrame2.transformCount; transformIndex3++) {
                  int transformIndex4 = animationFrame2.transformIndices[transformIndex3];

                  while (transformIndex4 > scalar2) {
                     scalar2 = values[scalar++];
                  }

                  if (transformIndex4 == scalar2 || animationSkeleton.transformTypes[transformIndex4] == 0) {
                     this.transformSkin(animationSkeleton.transformTypes[transformIndex4], animationSkeleton.labels[transformIndex4], animationFrame2.transformX[transformIndex3], animationFrame2.transformY[transformIndex3], animationFrame2.transformZ[transformIndex3]);
                  }
               }

               return;
            }
         }

         this.applyAnimationFrame(frameIndex2);
      }
   }
   private void transformSkin(int transformType, int[] values, int newTransformOriginX, int newTransformOriginY, int newTransformOriginZ) {
      int localLength = values.length;
      if (transformType == 0) {
         transformType = 0;
         transformOriginX = 0;
         transformOriginY = 0;
         transformOriginZ = 0;

         for (int localIndex = 0; localIndex < localLength; localIndex++) {
            int vectorSkinLength;
            if ((vectorSkinLength = values[localIndex]) < this.vectorSkin.length) {
               int[] localVectorSkin;
               int[] integerBuffer = localVectorSkin = this.vectorSkin[vectorSkinLength];
               vectorSkinLength = localVectorSkin.length;

               for (int vectorSkinIndex2 = 0; vectorSkinIndex2 < vectorSkinLength; vectorSkinIndex2++) {
                  int verticesXIndex = integerBuffer[vectorSkinIndex2];
                  transformOriginX = transformOriginX + this.verticesX[verticesXIndex];
                  transformOriginY = transformOriginY + this.verticesY[verticesXIndex];
                  transformOriginZ = transformOriginZ + this.verticesZ[verticesXIndex];
                  transformType++;
               }
            }
         }

         if (transformType > 0) {
            transformOriginX = transformOriginX / transformType + newTransformOriginX;
            transformOriginY = transformOriginY / transformType + newTransformOriginY;
            transformOriginZ = transformOriginZ / transformType + newTransformOriginZ;
         } else {
            transformOriginX = newTransformOriginX;
            transformOriginY = newTransformOriginY;
            transformOriginZ = newTransformOriginZ;
         }
      } else if (transformType == 1) {
         for (int localIndex2 = 0; localIndex2 < localLength; localIndex2++) {
            int vectorSkinIndex;
            if ((vectorSkinIndex = values[localIndex2]) < this.vectorSkin.length) {
               int[] vectorSkin2;
               int[] values2 = vectorSkin2 = this.vectorSkin[vectorSkinIndex];
               int length2 = vectorSkin2.length;

               for (int loopIndex = 0; loopIndex < length2; loopIndex++) {
                  int values2Entry = values2[loopIndex];
                  this.verticesX[values2Entry] = this.verticesX[values2Entry] + newTransformOriginX;
                  this.verticesY[values2Entry] = this.verticesY[values2Entry] + newTransformOriginY;
                  this.verticesZ[values2Entry] = this.verticesZ[values2Entry] + newTransformOriginZ;
               }
            }
         }
      } else if (transformType == 2) {
         for (int localIndex3 = 0; localIndex3 < localLength; localIndex3++) {
            int vectorSkinIndex3;
            if ((vectorSkinIndex3 = values[localIndex3]) < this.vectorSkin.length) {
               int[] vectorSkin3;
               int[] values3 = vectorSkin3 = this.vectorSkin[vectorSkinIndex3];
               int length3 = vectorSkin3.length;

               for (int loopIndex2 = 0; loopIndex2 < length3; loopIndex2++) {
                  int values3Entry = values3[loopIndex2];
                  this.verticesX[values3Entry] = this.verticesX[values3Entry] - transformOriginX;
                  this.verticesY[values3Entry] = this.verticesY[values3Entry] - transformOriginY;
                  this.verticesZ[values3Entry] = this.verticesZ[values3Entry] - transformOriginZ;
                  if (Client.smoothAnimations) {
                     if (newTransformOriginZ != 0) {
                        int sINEEntry = SINE[newTransformOriginZ];
                        vectorSkinIndex3 = COSINE[newTransformOriginZ];
                        int verticesXOrVerticesY = this.verticesY[values3Entry] * sINEEntry + this.verticesX[values3Entry] * vectorSkinIndex3 >> 16;
                        this.verticesY[values3Entry] = this.verticesY[values3Entry] * vectorSkinIndex3 - this.verticesX[values3Entry] * sINEEntry >> 16;
                        this.verticesX[values3Entry] = verticesXOrVerticesY;
                     }

                     if (newTransformOriginX != 0) {
                        int SINE2 = SINE[newTransformOriginX];
                        vectorSkinIndex3 = COSINE[newTransformOriginX];
                        int localVerticesY = this.verticesY[values3Entry] * vectorSkinIndex3 - this.verticesZ[values3Entry] * SINE2 >> 16;
                        this.verticesZ[values3Entry] = this.verticesY[values3Entry] * SINE2 + this.verticesZ[values3Entry] * vectorSkinIndex3 >> 16;
                        this.verticesY[values3Entry] = localVerticesY;
                     }

                     if (newTransformOriginY != 0) {
                        int SINE3 = SINE[newTransformOriginY];
                        vectorSkinIndex3 = COSINE[newTransformOriginY];
                        int verticesXOrVerticesZ = this.verticesZ[values3Entry] * SINE3 + this.verticesX[values3Entry] * vectorSkinIndex3 >> 16;
                        this.verticesZ[values3Entry] = this.verticesZ[values3Entry] * vectorSkinIndex3 - this.verticesX[values3Entry] * SINE3 >> 16;
                        this.verticesX[values3Entry] = verticesXOrVerticesZ;
                     }
                  } else {
                     int sINEIndex = (newTransformOriginX & 0xFF) << 3;
                     vectorSkinIndex3 = (newTransformOriginY & 0xFF) << 3;
                     int sINEIndex2;
                     if ((sINEIndex2 = (newTransformOriginZ & 0xFF) << 3) != 0) {
                        int SINE4 = SINE[sINEIndex2];
                        sINEIndex2 = COSINE[sINEIndex2];
                        int localVerticesX = this.verticesY[values3Entry] * SINE4 + this.verticesX[values3Entry] * sINEIndex2 >> 16;
                        this.verticesY[values3Entry] = this.verticesY[values3Entry] * sINEIndex2 - this.verticesX[values3Entry] * SINE4 >> 16;
                        this.verticesX[values3Entry] = localVerticesX;
                     }

                     if (sINEIndex != 0) {
                        int SINE5 = SINE[sINEIndex];
                        sINEIndex2 = COSINE[sINEIndex];
                        int verticesY2 = this.verticesY[values3Entry] * sINEIndex2 - this.verticesZ[values3Entry] * SINE5 >> 16;
                        this.verticesZ[values3Entry] = this.verticesY[values3Entry] * SINE5 + this.verticesZ[values3Entry] * sINEIndex2 >> 16;
                        this.verticesY[values3Entry] = verticesY2;
                     }

                     if (vectorSkinIndex3 != 0) {
                        int SINE6 = SINE[vectorSkinIndex3];
                        sINEIndex2 = COSINE[vectorSkinIndex3];
                        int localVerticesX2 = this.verticesZ[values3Entry] * SINE6 + this.verticesX[values3Entry] * sINEIndex2 >> 16;
                        this.verticesZ[values3Entry] = this.verticesZ[values3Entry] * sINEIndex2 - this.verticesX[values3Entry] * SINE6 >> 16;
                        this.verticesX[values3Entry] = localVerticesX2;
                     }
                  }

                  this.verticesX[values3Entry] = this.verticesX[values3Entry] + transformOriginX;
                  this.verticesY[values3Entry] = this.verticesY[values3Entry] + transformOriginY;
                  this.verticesZ[values3Entry] = this.verticesZ[values3Entry] + transformOriginZ;
               }
            }
         }
      } else if (transformType == 3) {
         for (int localIndex4 = 0; localIndex4 < localLength; localIndex4++) {
            int vectorSkinLength2;
            if ((vectorSkinLength2 = values[localIndex4]) < this.vectorSkin.length) {
               int[] vectorSkin4;
               int[] values4 = vectorSkin4 = this.vectorSkin[vectorSkinLength2];
               int length4 = vectorSkin4.length;

               for (int loopIndex3 = 0; loopIndex3 < length4; loopIndex3++) {
                  int values4Entry = values4[loopIndex3];
                  this.verticesX[values4Entry] = this.verticesX[values4Entry] - transformOriginX;
                  this.verticesY[values4Entry] = this.verticesY[values4Entry] - transformOriginY;
                  this.verticesZ[values4Entry] = this.verticesZ[values4Entry] - transformOriginZ;
                  this.verticesX[values4Entry] = this.verticesX[values4Entry] * newTransformOriginX / 128;
                  this.verticesY[values4Entry] = this.verticesY[values4Entry] * newTransformOriginY / 128;
                  this.verticesZ[values4Entry] = this.verticesZ[values4Entry] * newTransformOriginZ / 128;
                  this.verticesX[values4Entry] = this.verticesX[values4Entry] + transformOriginX;
                  this.verticesY[values4Entry] = this.verticesY[values4Entry] + transformOriginY;
                  this.verticesZ[values4Entry] = this.verticesZ[values4Entry] + transformOriginZ;
               }
            }
         }
      } else {
         if (transformType == 5 && this.triangleSkin != null && this.triangleAlphaValues != null) {
            for (int localIndex5 = 0; localIndex5 < localLength; localIndex5++) {
               int triangleSkinLength;
               if ((triangleSkinLength = values[localIndex5]) < this.triangleSkin.length) {
                  int[] localTriangleSkin;
                  int[] values5 = localTriangleSkin = this.triangleSkin[triangleSkinLength];
                  int length5 = localTriangleSkin.length;

                  for (int loopIndex4 = 0; loopIndex4 < length5; loopIndex4++) {
                     int values5Entry = values5[loopIndex4];
                     this.triangleAlphaValues[values5Entry] = this.triangleAlphaValues[values5Entry] + (newTransformOriginX << 3);
                     if (this.triangleAlphaValues[values5Entry] < 0) {
                        this.triangleAlphaValues[values5Entry] = 0;
                     }

                     if (this.triangleAlphaValues[values5Entry] > 255) {
                        this.triangleAlphaValues[values5Entry] = 255;
                     }
                  }
               }
            }
         }
      }
   }
   public final void rotateY90() {
      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         int verticesXEntry = this.verticesX[verticesXIndex];
         this.verticesX[verticesXIndex] = this.verticesZ[verticesXIndex];
         this.verticesZ[verticesXIndex] = -verticesXEntry;
      }
   }
   public final void translate(int scalarArgument, int graphicHeight, int scalarArgument2) {
      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         this.verticesX[verticesXIndex] = this.verticesX[verticesXIndex] + scalarArgument;
         this.verticesY[verticesXIndex] = this.verticesY[verticesXIndex] + graphicHeight;
         this.verticesZ[verticesXIndex] = this.verticesZ[verticesXIndex] + scalarArgument2;
      }
   }
   public final void setTriangleAlpha(int localTriangleAlpha) {
      for (int triangleAlphaValueIndex = 0; triangleAlphaValueIndex < this.triangleCount; triangleAlphaValueIndex++) {
         this.triangleAlphaValues[triangleAlphaValueIndex] = localTriangleAlpha;
      }
   }
   public final void setTriangleAlphaForColor(int recolorFromEntry, int localTriangleAlpha) {
      for (int triangleColorValueIndex = 0; triangleColorValueIndex < this.triangleCount; triangleColorValueIndex++) {
         if (this.triangleColorValues[triangleColorValueIndex] == recolorFromEntry) {
            this.triangleAlphaValues[triangleColorValueIndex] = localTriangleAlpha;
         }
      }
   }
   public final void recolor(int recolorToFindEntry, int localTriangleColor) {
      for (int triangleColorValueIndex = 0; triangleColorValueIndex < this.triangleCount; triangleColorValueIndex++) {
         if (this.triangleColorValues[triangleColorValueIndex] == recolorToFindEntry) {
            this.triangleColorValues[triangleColorValueIndex] = localTriangleColor;
         }
      }
   }
   public final void mirror() {
      for (int verticesZIndex = 0; verticesZIndex < this.vertexCount; verticesZIndex++) {
         this.verticesZ[verticesZIndex] = -this.verticesZ[verticesZIndex];
      }

      for (int trianglePointsXIndex = 0; trianglePointsXIndex < this.triangleCount; trianglePointsXIndex++) {
         int trianglePointsZOrTrianglePointsX = this.trianglePointsX[trianglePointsXIndex];
         this.trianglePointsX[trianglePointsXIndex] = this.trianglePointsZ[trianglePointsXIndex];
         this.trianglePointsZ[trianglePointsXIndex] = trianglePointsZOrTrianglePointsX;
      }
   }
   public final void scale(int resizeX, int resizeX2, int resizeY) {
      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         this.verticesX[verticesXIndex] = this.verticesX[verticesXIndex] * resizeX / 128;
         this.verticesY[verticesXIndex] = this.verticesY[verticesXIndex] * resizeY / 128;
         this.verticesZ[verticesXIndex] = this.verticesZ[verticesXIndex] * resizeX2 / 128;
      }
   }
   public final void light(int ambient, int contrast, int minXOrVerticesX, int maxYOrVerticesY, int minZOrVerticesZ, boolean flag) {
      int sqrtResult = (int)Math.sqrt(minXOrVerticesX * minXOrVerticesX + maxYOrVerticesY * maxYOrVerticesY + minZOrVerticesZ * minZOrVerticesZ);
      contrast = contrast * sqrtResult >> 8;
      if (this.triangleHSLA == null) {
         this.triangleHSLA = new int[this.triangleCount];
         this.triangleHSLB = new int[this.triangleCount];
         this.triangleHSLC = new int[this.triangleCount];
      }

      if (super.vertexNormals == null) {
         super.vertexNormals = new VertexNormal[this.vertexCount];

         for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
            super.vertexNormals[vertexIndex] = new VertexNormal();
         }
      }

      for (int triangleColorValueIndex = 0; triangleColorValueIndex < this.triangleCount; triangleColorValueIndex++) {
         if (Client.hdModels && this.triangleColorValues != null && this.triangleAlphaValues != null && this.triangleColorValues[triangleColorValueIndex] == 65535) {
            this.triangleAlphaValues[triangleColorValueIndex] = 255;
         }

         int trianglePointsXEntry = this.trianglePointsX[triangleColorValueIndex];
         int trianglePointsYEntry = this.trianglePointsY[triangleColorValueIndex];
         int trianglePointsZEntry = this.trianglePointsZ[triangleColorValueIndex];
         int position = this.verticesX[trianglePointsYEntry] - this.verticesX[trianglePointsXEntry];
         int sqrtResult2 = this.verticesY[trianglePointsYEntry] - this.verticesY[trianglePointsXEntry];
         int localVerticesZ = this.verticesZ[trianglePointsYEntry] - this.verticesZ[trianglePointsXEntry];
         int position2 = this.verticesX[trianglePointsZEntry] - this.verticesX[trianglePointsXEntry];
         int position3 = this.verticesY[trianglePointsZEntry] - this.verticesY[trianglePointsXEntry];
         int verticesZ2 = this.verticesZ[trianglePointsZEntry] - this.verticesZ[trianglePointsXEntry];
         int scalar = sqrtResult2 * verticesZ2 - position3 * localVerticesZ;
         localVerticesZ = localVerticesZ * position2 - verticesZ2 * position;

         for (position = position * position3 - position2 * sqrtResult2;
            scalar > 8192 || localVerticesZ > 8192 || position > 8192 || scalar < -8192 || localVerticesZ < -8192 || position < -8192;
            position >>= 1
         ) {
            scalar >>= 1;
            localVerticesZ >>= 1;
         }

         if ((sqrtResult2 = (int)Math.sqrt(scalar * scalar + localVerticesZ * localVerticesZ + position * position)) <= 0) {
            sqrtResult2 = 1;
         }

         scalar = (scalar << 8) / sqrtResult2;
         localVerticesZ = (localVerticesZ << 8) / sqrtResult2;
         position = (position << 8) / sqrtResult2;
         if (this.triangleDrawType != null && (this.triangleDrawType[triangleColorValueIndex] & 1) != 0) {
            trianglePointsXEntry = ambient + (minXOrVerticesX * scalar + maxYOrVerticesY * localVerticesZ + minZOrVerticesZ * position) / (contrast + contrast / 2);
            this.triangleHSLA[triangleColorValueIndex] = adjustLightness(this.triangleColorValues[triangleColorValueIndex], trianglePointsXEntry, this.triangleDrawType[triangleColorValueIndex]);
         } else {
            VertexNormal vertexNormal;
            (vertexNormal = super.vertexNormals[trianglePointsXEntry]).x += scalar;
            vertexNormal.y += localVerticesZ;
            vertexNormal.z += position;
            vertexNormal.magnitude++;
            VertexNormal vertexNormal5;
            (vertexNormal5 = super.vertexNormals[trianglePointsYEntry]).x += scalar;
            vertexNormal5.y += localVerticesZ;
            vertexNormal5.z += position;
            vertexNormal5.magnitude++;
            VertexNormal vertexNormal2;
            (vertexNormal2 = super.vertexNormals[trianglePointsZEntry]).x += scalar;
            vertexNormal2.y += localVerticesZ;
            vertexNormal2.z += position;
            vertexNormal2.magnitude++;
         }
      }

      if (flag) {
         this.applyLighting(ambient, contrast, minXOrVerticesX, maxYOrVerticesY, minZOrVerticesZ);
      } else {
         this.vertexNormalOffset = new VertexNormal[this.vertexCount];

         for (int vertexNormalIndex = 0; vertexNormalIndex < this.vertexCount; vertexNormalIndex++) {
            VertexNormal vertexNormal3 = super.vertexNormals[vertexNormalIndex];
            VertexNormal vertexNormal4;
            (vertexNormal4 = this.vertexNormalOffset[vertexNormalIndex] = new VertexNormal()).x = vertexNormal3.x;
            vertexNormal4.y = vertexNormal3.y;
            vertexNormal4.z = vertexNormal3.z;
            vertexNormal4.magnitude = vertexNormal3.magnitude;
         }
      }

      if (flag) {
         this.calculateBoundsCylinder();
      } else {
         Model model = this;
         super.modelHeight = 0;
         model.diagonal2DAboveOrigin = 0;
         model.maxY = 0;
         model.minX = 999999;
         model.maxX = -999999;
         model.maxZ = -99999;
         model.minZ = 99999;

         for (int verticesXIndex = 0; verticesXIndex < model.vertexCount; verticesXIndex++) {
            minXOrVerticesX = model.verticesX[verticesXIndex];
            maxYOrVerticesY = model.verticesY[verticesXIndex];
            minZOrVerticesZ = model.verticesZ[verticesXIndex];
            if (minXOrVerticesX < model.minX) {
               model.minX = minXOrVerticesX;
            }

            if (minXOrVerticesX > model.maxX) {
               model.maxX = minXOrVerticesX;
            }

            if (minZOrVerticesZ < model.minZ) {
               model.minZ = minZOrVerticesZ;
            }

            if (minZOrVerticesZ > model.maxZ) {
               model.maxZ = minZOrVerticesZ;
            }

            if (-maxYOrVerticesY > model.modelHeight) {
               model.modelHeight = -maxYOrVerticesY;
            }

            if (maxYOrVerticesY > model.maxY) {
               model.maxY = maxYOrVerticesY;
            }

            if ((minXOrVerticesX = minXOrVerticesX * minXOrVerticesX + minZOrVerticesZ * minZOrVerticesZ) > model.diagonal2DAboveOrigin) {
               model.diagonal2DAboveOrigin = minXOrVerticesX;
            }
         }

         model.diagonal2DAboveOrigin = (int)Math.sqrt(model.diagonal2DAboveOrigin);
         model.diagonal3DAboveOrigin = (int)Math.sqrt(model.diagonal2DAboveOrigin * model.diagonal2DAboveOrigin + model.modelHeight * model.modelHeight);
         model.diagonal3D = model.diagonal3DAboveOrigin + (int)Math.sqrt(model.diagonal2DAboveOrigin * model.diagonal2DAboveOrigin + model.maxY * model.maxY);
      }
   }
   public final void applyLighting(int ambient, int contrast, int scalarArgument, int scalarArgument2, int scalarArgument3) {
      for (int trianglePointsXIndex = 0; trianglePointsXIndex < this.triangleCount; trianglePointsXIndex++) {
         int trianglePointsXEntry = this.trianglePointsX[trianglePointsXIndex];
         int trianglePointsYEntry = this.trianglePointsY[trianglePointsXIndex];
         int trianglePointsZEntry = this.trianglePointsZ[trianglePointsXIndex];
         if (this.triangleDrawType == null) {
            int triangleColor = this.triangleColorValues[trianglePointsXIndex];
            VertexNormal vertexNormal = super.vertexNormals[trianglePointsXEntry];
            trianglePointsXEntry = ambient + (scalarArgument * vertexNormal.x + scalarArgument2 * vertexNormal.y + scalarArgument3 * vertexNormal.z) / (contrast * vertexNormal.magnitude);
            this.triangleHSLA[trianglePointsXIndex] = adjustLightness(triangleColor, trianglePointsXEntry, 0);
            vertexNormal = super.vertexNormals[trianglePointsYEntry];
            trianglePointsXEntry = ambient + (scalarArgument * vertexNormal.x + scalarArgument2 * vertexNormal.y + scalarArgument3 * vertexNormal.z) / (contrast * vertexNormal.magnitude);
            this.triangleHSLB[trianglePointsXIndex] = adjustLightness(triangleColor, trianglePointsXEntry, 0);
            vertexNormal = super.vertexNormals[trianglePointsZEntry];
            trianglePointsXEntry = ambient + (scalarArgument * vertexNormal.x + scalarArgument2 * vertexNormal.y + scalarArgument3 * vertexNormal.z) / (contrast * vertexNormal.magnitude);
            this.triangleHSLC[trianglePointsXIndex] = adjustLightness(triangleColor, trianglePointsXEntry, 0);
         } else if ((this.triangleDrawType[trianglePointsXIndex] & 1) == 0) {
            int triangleColorValues2 = this.triangleColorValues[trianglePointsXIndex];
            int triangleDrawTypeEntry = this.triangleDrawType[trianglePointsXIndex];
            VertexNormal vertexNormal4 = super.vertexNormals[trianglePointsXEntry];
            trianglePointsXEntry = ambient + (scalarArgument * vertexNormal4.x + scalarArgument2 * vertexNormal4.y + scalarArgument3 * vertexNormal4.z) / (contrast * vertexNormal4.magnitude);
            this.triangleHSLA[trianglePointsXIndex] = adjustLightness(triangleColorValues2, trianglePointsXEntry, triangleDrawTypeEntry);
            VertexNormal vertexNormal2 = super.vertexNormals[trianglePointsYEntry];
            trianglePointsXEntry = ambient + (scalarArgument * vertexNormal2.x + scalarArgument2 * vertexNormal2.y + scalarArgument3 * vertexNormal2.z) / (contrast * vertexNormal2.magnitude);
            this.triangleHSLB[trianglePointsXIndex] = adjustLightness(triangleColorValues2, trianglePointsXEntry, triangleDrawTypeEntry);
            VertexNormal vertexNormal3 = super.vertexNormals[trianglePointsZEntry];
            trianglePointsXEntry = ambient + (scalarArgument * vertexNormal3.x + scalarArgument2 * vertexNormal3.y + scalarArgument3 * vertexNormal3.z) / (contrast * vertexNormal3.magnitude);
            this.triangleHSLC[trianglePointsXIndex] = adjustLightness(triangleColorValues2, trianglePointsXEntry, triangleDrawTypeEntry);
         }
      }

      super.vertexNormals = null;
      this.vertexNormalOffset = null;
      this.vertexSkins = null;
      this.triangleSkinValues = null;
      if (this.triangleDrawType != null) {
         for (int triangleDrawTypeIndex = 0; triangleDrawTypeIndex < this.triangleCount; triangleDrawTypeIndex++) {
            if ((this.triangleDrawType[triangleDrawTypeIndex] & 2) == 2) {
               return;
            }
         }
      }

      this.triangleColorValues = null;
   }
   private static int adjustLightness(int color, int scalarArgument, int triangleDrawTypeEntry) {
      if ((triangleDrawTypeEntry & 2) == 2) {
         if (scalarArgument < 0) {
            scalarArgument = 0;
         } else if (scalarArgument > 127) {
            scalarArgument = 127;
         }

         int scalar;
         return scalar = 127 - scalarArgument;
      } else {
         if ((scalarArgument = scalarArgument * (color & 127) >> 7) < 2) {
            scalarArgument = 2;
         } else if (scalarArgument > 126) {
            scalarArgument = 126;
         }

         return (color & 65408) + scalarArgument;
      }
   }
   public final void renderSimple(int sINEIndex2, int sINEIndex, int newCOSINE, int xOffset2d, int yOffset2d, int yOffset2d2) {
      int projectedXOrViewportCenterX = Rasterizer3D.viewportCenterX;
      int projectedYOrViewportCenterY = Rasterizer3D.viewportCenterY;
      int sINEEntry = SINE[sINEIndex2];
      int cOSINEEntry = COSINE[sINEIndex2];
      int SINE2 = SINE[sINEIndex];
      int COSINE2 = COSINE[sINEIndex];
      int SINE3 = SINE[newCOSINE];
      newCOSINE = COSINE[newCOSINE];
      int scalar = yOffset2d * SINE3 + yOffset2d2 * newCOSINE >> 16;

      for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
         int cameraXOrVerticesX = this.verticesX[verticesXIndex];
         int verticesYEntry = this.verticesY[verticesXIndex];
         int vertexDepthOffsetOrVerticesZ = this.verticesZ[verticesXIndex];
         if (sINEIndex != 0) {
            int scalar2 = verticesYEntry * SINE2 + cameraXOrVerticesX * COSINE2 >> 16;
            verticesYEntry = verticesYEntry * COSINE2 - cameraXOrVerticesX * SINE2 >> 16;
            cameraXOrVerticesX = scalar2;
         }

         if (sINEIndex2 != 0) {
            int scalar3 = vertexDepthOffsetOrVerticesZ * sINEEntry + cameraXOrVerticesX * cOSINEEntry >> 16;
            vertexDepthOffsetOrVerticesZ = vertexDepthOffsetOrVerticesZ * cOSINEEntry - cameraXOrVerticesX * sINEEntry >> 16;
            cameraXOrVerticesX = scalar3;
         }

         cameraXOrVerticesX += xOffset2d;
         verticesYEntry += yOffset2d;
         vertexDepthOffsetOrVerticesZ += yOffset2d2;
         int localCameraY = verticesYEntry * newCOSINE - vertexDepthOffsetOrVerticesZ * SINE3 >> 16;
         vertexDepthOffsetOrVerticesZ = verticesYEntry * SINE3 + vertexDepthOffsetOrVerticesZ * newCOSINE >> 16;
         vertexDepthOffset[verticesXIndex] = vertexDepthOffsetOrVerticesZ - scalar;
         cameraDepth[verticesXIndex] = 0;
         projectedX[verticesXIndex] = projectedXOrViewportCenterX + (cameraXOrVerticesX << 9) / vertexDepthOffsetOrVerticesZ;
         projectedY[verticesXIndex] = projectedYOrViewportCenterY + (localCameraY << 9) / vertexDepthOffsetOrVerticesZ;
         if (this.texturedTriangleCount > 0) {
            cameraX[verticesXIndex] = cameraXOrVerticesX;
            cameraY[verticesXIndex] = localCameraY;
            cameraZ[verticesXIndex] = vertexDepthOffsetOrVerticesZ;
         }
      }

      try {
         this.drawTriangles(false, false, 0);
      } catch (Exception exception) {
      }
   }
   @Override
   public final void renderAtPoint(int sINEIndex, int pitchSin, int pitchCos, int yawSin, int yawCos, int scalarArgument, int heightOffset, int scalarArgument2, int pickedId) {
      int sINEEntry = scalarArgument2 * yawCos - scalarArgument * yawSin >> 16;
      int scalar = heightOffset * pitchSin + sINEEntry * pitchCos >> 16;
      int projectedXOrDiagonal2DAboveOrigin = this.diagonal2DAboveOrigin * pitchCos >> 16;
      int localProjectedY;
      int cOSINEEntry;
      int scalar2;
      int localCameraX;
      if ((localProjectedY = scalar + projectedXOrDiagonal2DAboveOrigin) > 50
         && scalar < 9500
         && (scalar2 = (cOSINEEntry = scalarArgument2 * yawSin + scalarArgument * yawCos >> 16) - this.diagonal2DAboveOrigin << Client.getProjectionScaleShift()) / localProjectedY < Rasterizer2D.centerY
         && (localCameraX = cOSINEEntry + this.diagonal2DAboveOrigin << Client.getProjectionScaleShift()) / localProjectedY > -Rasterizer2D.centerY) {
         sINEEntry = heightOffset * pitchCos - sINEEntry * pitchSin >> 16;
         int verticesYEntry = this.diagonal2DAboveOrigin * pitchSin >> 16;
         int localVertexDepthOffset;
         if ((localVertexDepthOffset = sINEEntry + verticesYEntry << Client.getProjectionScaleShift()) / localProjectedY > -Rasterizer2D.viewportCenterY) {
            verticesYEntry += super.modelHeight * pitchCos >> 16;
            if ((verticesYEntry = sINEEntry - verticesYEntry << Client.getProjectionScaleShift()) / localProjectedY < Rasterizer2D.viewportCenterY) {
               int scalar3 = projectedXOrDiagonal2DAboveOrigin + (super.modelHeight * pitchSin >> 16);
               boolean flag = false;
               if (scalar - scalar3 <= 50) {
                  flag = true;
               }

               boolean localFlag = false;
               if (pickedId > 0 && pickingEnabled) {
                  if ((projectedXOrDiagonal2DAboveOrigin = scalar - projectedXOrDiagonal2DAboveOrigin) <= 50) {
                     projectedXOrDiagonal2DAboveOrigin = 50;
                  }

                  if (cOSINEEntry > 0) {
                     scalar2 /= localProjectedY;
                     localCameraX /= projectedXOrDiagonal2DAboveOrigin;
                  } else {
                     localCameraX /= localProjectedY;
                     scalar2 /= projectedXOrDiagonal2DAboveOrigin;
                  }

                  if (sINEEntry > 0) {
                     verticesYEntry /= localProjectedY;
                     localVertexDepthOffset /= projectedXOrDiagonal2DAboveOrigin;
                  } else {
                     localVertexDepthOffset /= localProjectedY;
                     verticesYEntry /= projectedXOrDiagonal2DAboveOrigin;
                  }

                  localProjectedY = mouseX - Rasterizer3D.viewportCenterX;
                  sINEEntry = mouseY - Rasterizer3D.viewportCenterY;
                  if (localProjectedY > scalar2 && localProjectedY < localCameraX && sINEEntry > verticesYEntry && sINEEntry < localVertexDepthOffset) {
                     if (this.singleTile) {
                        pickedIds[pickedCount++] = pickedId;
                     } else {
                        localFlag = true;
                     }
                  }
               }

               projectedXOrDiagonal2DAboveOrigin = Rasterizer3D.viewportCenterX;
               localProjectedY = Rasterizer3D.viewportCenterY;
               sINEEntry = 0;
               cOSINEEntry = 0;
               if (sINEIndex != 0) {
                  sINEEntry = SINE[sINEIndex];
                  cOSINEEntry = COSINE[sINEIndex];
               }

               for (int verticesXIndex = 0; verticesXIndex < this.vertexCount; verticesXIndex++) {
                  localCameraX = this.verticesX[verticesXIndex];
                  verticesYEntry = this.verticesY[verticesXIndex];
                  localVertexDepthOffset = this.verticesZ[verticesXIndex];
                  if (sINEIndex != 0) {
                     int sourceLocalCameraX = localVertexDepthOffset * sINEEntry + localCameraX * cOSINEEntry >> 16;
                     localVertexDepthOffset = localVertexDepthOffset * cOSINEEntry - localCameraX * sINEEntry >> 16;
                     localCameraX = sourceLocalCameraX;
                  }

                  localCameraX += scalarArgument;
                  verticesYEntry += heightOffset;
                  int scalar4;
                  int localCameraY = (scalar4 = localVertexDepthOffset + scalarArgument2) * yawSin + localCameraX * yawCos >> 16;
                  localVertexDepthOffset = scalar4 * yawCos - localCameraX * yawSin >> 16;
                  localCameraX = localCameraY;
                  localCameraY = verticesYEntry * pitchCos - localVertexDepthOffset * pitchSin >> 16;
                  localVertexDepthOffset = verticesYEntry * pitchSin + localVertexDepthOffset * pitchCos >> 16;
                  vertexDepthOffset[verticesXIndex] = localVertexDepthOffset - scalar;
                  cameraDepth[verticesXIndex] = localVertexDepthOffset;
                  if (localVertexDepthOffset >= 50) {
                     projectedX[verticesXIndex] = projectedXOrDiagonal2DAboveOrigin + (localCameraX << Client.getProjectionScaleShift()) / localVertexDepthOffset;
                     projectedY[verticesXIndex] = localProjectedY + (localCameraY << Client.getProjectionScaleShift()) / localVertexDepthOffset;
                  } else {
                     projectedX[verticesXIndex] = -5000;
                     flag = true;
                  }

                  if (flag || this.texturedTriangleCount > 0) {
                     cameraX[verticesXIndex] = localCameraX;
                     cameraY[verticesXIndex] = localCameraY;
                     cameraZ[verticesXIndex] = localVertexDepthOffset;
                  }
               }

               try {
                  this.drawTriangles(flag, localFlag, pickedId);
                  return;
               } catch (Exception exception) {
               }
            }
         }
      }
   }
   private void drawTriangles(boolean flag, boolean newFlag, int pickedId) {
      for (int depthBucketCountIndex2 = 0; depthBucketCountIndex2 < this.diagonal3D; depthBucketCountIndex2++) {
         depthBucketCounts[depthBucketCountIndex2] = 0;
      }

      for (int triangleDrawTypeIndex = 0; triangleDrawTypeIndex < this.triangleCount; triangleDrawTypeIndex++) {
         if ((this.triangleDrawType == null || this.triangleDrawType[triangleDrawTypeIndex] != -1) && (!Client.hdModels || this.triangleAlphaValues == null || this.triangleAlphaValues[triangleDrawTypeIndex] < 255)) {
            int projectedXIndex;
            int projectedXIndex2;
            int projectedXIndex3;
            int localProjectedY;
            boolean[] localTriangleEdgeRestricted;
            int localTriangleEdgeRestrictedIndex;
            classifyTriangleClipping: {
               projectedXIndex = this.trianglePointsX[triangleDrawTypeIndex];
               projectedXIndex2 = this.trianglePointsY[triangleDrawTypeIndex];
               projectedXIndex3 = this.trianglePointsZ[triangleDrawTypeIndex];
               int projectedXEntry = projectedX[projectedXIndex];
               int projectedX2 = projectedX[projectedXIndex2];
               int projectedX3 = projectedX[projectedXIndex3];
               if (!flag || projectedXEntry != -5000 && projectedX2 != -5000 && projectedX3 != -5000) {
                  if (newFlag) {
                     localProjectedY = projectedY[projectedXIndex];
                     int projectedY2 = projectedY[projectedXIndex2];
                     int projectedY3 = projectedY[projectedXIndex3];
                     int scalar = projectedX3;
                     int scalar2 = projectedX2;
                     int scalar3 = projectedXEntry;
                     int scalar4 = projectedY3;
                     int scalar5 = projectedY2;
                     int sourceLocalProjectedY = localProjectedY;
                     int sourceMouseY = mouseY;
                     int sourceMouseX = mouseX;
                     if ((sourceMouseY >= sourceLocalProjectedY || sourceMouseY >= scalar5 || sourceMouseY >= scalar4)
                        && (sourceMouseY <= sourceLocalProjectedY || sourceMouseY <= scalar5 || sourceMouseY <= scalar4)
                        && (sourceMouseX >= scalar3 || sourceMouseX >= scalar2 || sourceMouseX >= scalar)
                        && (sourceMouseX <= scalar3 || sourceMouseX <= scalar2 || sourceMouseX <= scalar)) {
                        pickedIds[pickedCount++] = pickedId;
                        newFlag = false;
                     }
                  }

                  if ((projectedXEntry - projectedX2) * (projectedY[projectedXIndex3] - projectedY[projectedXIndex2]) - (projectedY[projectedXIndex] - projectedY[projectedXIndex2]) * (projectedX3 - projectedX2) <= 0) {
                     continue;
                  }

                  triangleNearClipped[triangleDrawTypeIndex] = false;
                  localTriangleEdgeRestricted = triangleEdgeRestricted;
                  localTriangleEdgeRestrictedIndex = triangleDrawTypeIndex;
                  if (projectedXEntry >= 0 && projectedX2 >= 0 && projectedX3 >= 0 && projectedXEntry <= Rasterizer2D.centerX && projectedX2 <= Rasterizer2D.centerX && projectedX3 <= Rasterizer2D.centerX) {
                     localProjectedY = 0;
                     break classifyTriangleClipping;
                  }
               } else {
                  localTriangleEdgeRestricted = triangleNearClipped;
                  localTriangleEdgeRestrictedIndex = triangleDrawTypeIndex;
               }

               localProjectedY = 1;
            }

            localTriangleEdgeRestricted[localTriangleEdgeRestrictedIndex] = localProjectedY != 0;
            int depthBucketIndex = (vertexDepthOffset[projectedXIndex] + vertexDepthOffset[projectedXIndex2] + vertexDepthOffset[projectedXIndex3]) / 3 + this.diagonal3DAboveOrigin;
            depthBuckets[depthBucketIndex][depthBucketCounts[depthBucketIndex]++] = triangleDrawTypeIndex;
         }
      }

      if (this.trianglePriorities == null) {
         for (int depthBucketCountIndex = this.diagonal3D - 1; depthBucketCountIndex >= 0; depthBucketCountIndex--) {
            int localDepthBucketCounts;
            if ((localDepthBucketCounts = depthBucketCounts[depthBucketCountIndex]) > 0) {
               int[] depthBucket = depthBuckets[depthBucketCountIndex];

               for (int depthBucketIndex2 = 0; depthBucketIndex2 < localDepthBucketCounts; depthBucketIndex2++) {
                  this.drawTriangle(depthBucket[depthBucketIndex2]);
               }
            }
         }
      } else {
         for (int priorityCountIndex = 0; priorityCountIndex < 12; priorityCountIndex++) {
            priorityCounts[priorityCountIndex] = 0;
            priorityDepthSums[priorityCountIndex] = 0;
         }

         for (int depthBucketCountIndex3 = this.diagonal3D - 1; depthBucketCountIndex3 >= 0; depthBucketCountIndex3--) {
            int depthBucketCounts2;
            if ((depthBucketCounts2 = depthBucketCounts[depthBucketCountIndex3]) > 0) {
               int[] depthBuckets2 = depthBuckets[depthBucketCountIndex3];

               for (int loopIndex = 0; loopIndex < depthBucketCounts2; loopIndex++) {
                  int depthBuckets2Entry = depthBuckets2[loopIndex];
                  int trianglePriority = this.trianglePriorities[depthBuckets2Entry];
                  int priority10DepthIndex = priorityCounts[trianglePriority]++;
                  priorityTriangles[trianglePriority][priority10DepthIndex] = depthBuckets2Entry;
                  if (trianglePriority < 10) {
                     priorityDepthSums[trianglePriority] = priorityDepthSums[trianglePriority] + depthBucketCountIndex3;
                  } else if (trianglePriority == 10) {
                     priority10Depths[priority10DepthIndex] = depthBucketCountIndex3;
                  } else {
                     priority11Depths[priority10DepthIndex] = depthBucketCountIndex3;
                  }
               }
            }
         }

         int scalar6 = 0;
         if (priorityCounts[1] > 0 || priorityCounts[2] > 0) {
            scalar6 = (priorityDepthSums[1] + priorityDepthSums[2]) / (priorityCounts[1] + priorityCounts[2]);
         }

         int scalar7 = 0;
         if (priorityCounts[3] > 0 || priorityCounts[4] > 0) {
            scalar7 = (priorityDepthSums[3] + priorityDepthSums[4]) / (priorityCounts[3] + priorityCounts[4]);
         }

         int scalar8 = 0;
         if (priorityCounts[6] > 0 || priorityCounts[8] > 0) {
            scalar8 = (priorityDepthSums[6] + priorityDepthSums[8]) / (priorityCounts[6] + priorityCounts[8]);
         }

         int position = 0;
         int priorityCount = priorityCounts[10];
         int[] priorityTriangle = priorityTriangles[10];
         int[] sourcePriority10Depths = priority10Depths;
         if (priorityCount == 0) {
            position = 0;
            priorityCount = priorityCounts[11];
            priorityTriangle = priorityTriangles[11];
            sourcePriority10Depths = priority11Depths;
         }

         int scalar9;
         if (priorityCount > 0) {
            scalar9 = sourcePriority10Depths[0];
         } else {
            scalar9 = -1000;
         }

         for (int priorityCountIndex2 = 0; priorityCountIndex2 < 10; priorityCountIndex2++) {
            while (priorityCountIndex2 == 0 && scalar9 > scalar6) {
               this.drawTriangle(priorityTriangle[position++]);
               if (position == priorityCount && priorityTriangle != priorityTriangles[11]) {
                  position = 0;
                  priorityCount = priorityCounts[11];
                  priorityTriangle = priorityTriangles[11];
                  sourcePriority10Depths = priority11Depths;
               }

               if (position < priorityCount) {
                  scalar9 = sourcePriority10Depths[position];
               } else {
                  scalar9 = -1000;
               }
            }

            while (priorityCountIndex2 == 3 && scalar9 > scalar7) {
               this.drawTriangle(priorityTriangle[position++]);
               if (position == priorityCount && priorityTriangle != priorityTriangles[11]) {
                  position = 0;
                  priorityCount = priorityCounts[11];
                  priorityTriangle = priorityTriangles[11];
                  sourcePriority10Depths = priority11Depths;
               }

               if (position < priorityCount) {
                  scalar9 = sourcePriority10Depths[position];
               } else {
                  scalar9 = -1000;
               }
            }

            while (priorityCountIndex2 == 5 && scalar9 > scalar8) {
               this.drawTriangle(priorityTriangle[position++]);
               if (position == priorityCount && priorityTriangle != priorityTriangles[11]) {
                  position = 0;
                  priorityCount = priorityCounts[11];
                  priorityTriangle = priorityTriangles[11];
                  sourcePriority10Depths = priority11Depths;
               }

               if (position < priorityCount) {
                  scalar9 = sourcePriority10Depths[position];
               } else {
                  scalar9 = -1000;
               }
            }

            int priorityCounts2 = priorityCounts[priorityCountIndex2];
            int[] priorityTriangles2 = priorityTriangles[priorityCountIndex2];

            for (int loopIndex2 = 0; loopIndex2 < priorityCounts2; loopIndex2++) {
               this.drawTriangle(priorityTriangles2[loopIndex2]);
            }
         }

         while (scalar9 != -1000) {
            this.drawTriangle(priorityTriangle[position++]);
            if (position == priorityCount && priorityTriangle != priorityTriangles[11]) {
               position = 0;
               priorityTriangle = priorityTriangles[11];
               priorityCount = priorityCounts[11];
               sourcePriority10Depths = priority11Depths;
            }

            if (position < priorityCount) {
               scalar9 = sourcePriority10Depths[position];
            } else {
               scalar9 = -1000;
            }
         }
      }
   }
   private void drawTriangle(int triangleNearClippedIndex) {
      if (triangleNearClipped[triangleNearClippedIndex]) {
         int trianglePointsXIndex = triangleNearClippedIndex;
         Model model = this;
         int clippedXOrViewportCenterX = Rasterizer3D.viewportCenterX;
         int clippedYOrViewportCenterY = Rasterizer3D.viewportCenterY;
         int clippedXIndex = 0;
         int trianglePointsXEntry = model.trianglePointsX[trianglePointsXIndex];
         int trianglePointsYEntry = model.trianglePointsY[trianglePointsXIndex];
         int trianglePointsZEntry = model.trianglePointsZ[trianglePointsXIndex];
         int cameraZEntry = cameraZ[trianglePointsXEntry];
         int cameraZEntry2 = cameraZ[trianglePointsYEntry];
         int cameraZEntry3 = cameraZ[trianglePointsZEntry];
         if (cameraZEntry >= 50) {
            clippedX[0] = projectedX[trianglePointsXEntry];
            clippedY[0] = projectedY[trianglePointsXEntry];
            clippedXIndex++;
            clippedShading[0] = model.triangleHSLA[trianglePointsXIndex];
         } else {
            int cameraXEntry = cameraX[trianglePointsXEntry];
            int cameraYEntry = cameraY[trianglePointsXEntry];
            int clippedShadingOrTriangleHSLA = model.triangleHSLA[trianglePointsXIndex];
            if (cameraZEntry3 >= 50) {
               int scalar = (50 - cameraZEntry) * RECIPROCAL_2048[cameraZEntry3 - cameraZEntry];
               clippedX[0] = clippedXOrViewportCenterX + (cameraXEntry + ((cameraX[trianglePointsZEntry] - cameraXEntry) * scalar >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedY[0] = clippedYOrViewportCenterY + (cameraYEntry + ((cameraY[trianglePointsZEntry] - cameraYEntry) * scalar >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedXIndex++;
               clippedShading[0] = clippedShadingOrTriangleHSLA + ((model.triangleHSLC[trianglePointsXIndex] - clippedShadingOrTriangleHSLA) * scalar >> 16);
            }

            if (cameraZEntry2 >= 50) {
               int scalar2 = (50 - cameraZEntry) * RECIPROCAL_2048[cameraZEntry2 - cameraZEntry];
               clippedX[clippedXIndex] = clippedXOrViewportCenterX + (cameraXEntry + ((cameraX[trianglePointsYEntry] - cameraXEntry) * scalar2 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedY[clippedXIndex] = clippedYOrViewportCenterY + (cameraYEntry + ((cameraY[trianglePointsYEntry] - cameraYEntry) * scalar2 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedShading[clippedXIndex++] = clippedShadingOrTriangleHSLA + ((model.triangleHSLB[trianglePointsXIndex] - clippedShadingOrTriangleHSLA) * scalar2 >> 16);
            }
         }

         if (cameraZEntry2 >= 50) {
            clippedX[clippedXIndex] = projectedX[trianglePointsYEntry];
            clippedY[clippedXIndex] = projectedY[trianglePointsYEntry];
            clippedShading[clippedXIndex++] = model.triangleHSLB[trianglePointsXIndex];
         } else {
            int cameraX2 = cameraX[trianglePointsYEntry];
            int cameraY2 = cameraY[trianglePointsYEntry];
            int clippedShadingOrTriangleHSLB = model.triangleHSLB[trianglePointsXIndex];
            if (cameraZEntry >= 50) {
               int scalar3 = (50 - cameraZEntry2) * RECIPROCAL_2048[cameraZEntry - cameraZEntry2];
               clippedX[clippedXIndex] = clippedXOrViewportCenterX + (cameraX2 + ((cameraX[trianglePointsXEntry] - cameraX2) * scalar3 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedY[clippedXIndex] = clippedYOrViewportCenterY + (cameraY2 + ((cameraY[trianglePointsXEntry] - cameraY2) * scalar3 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedShading[clippedXIndex++] = clippedShadingOrTriangleHSLB + ((model.triangleHSLA[trianglePointsXIndex] - clippedShadingOrTriangleHSLB) * scalar3 >> 16);
            }

            if (cameraZEntry3 >= 50) {
               int scalar4 = (50 - cameraZEntry2) * RECIPROCAL_2048[cameraZEntry3 - cameraZEntry2];
               clippedX[clippedXIndex] = clippedXOrViewportCenterX + (cameraX2 + ((cameraX[trianglePointsZEntry] - cameraX2) * scalar4 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedY[clippedXIndex] = clippedYOrViewportCenterY + (cameraY2 + ((cameraY[trianglePointsZEntry] - cameraY2) * scalar4 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedShading[clippedXIndex++] = clippedShadingOrTriangleHSLB + ((model.triangleHSLC[trianglePointsXIndex] - clippedShadingOrTriangleHSLB) * scalar4 >> 16);
            }
         }

         if (cameraZEntry3 >= 50) {
            clippedX[clippedXIndex] = projectedX[trianglePointsZEntry];
            clippedY[clippedXIndex] = projectedY[trianglePointsZEntry];
            clippedShading[clippedXIndex++] = model.triangleHSLC[trianglePointsXIndex];
         } else {
            int cameraX3 = cameraX[trianglePointsZEntry];
            int cameraY3 = cameraY[trianglePointsZEntry];
            int clippedShadingOrTriangleHSLC = model.triangleHSLC[trianglePointsXIndex];
            if (cameraZEntry2 >= 50) {
               int scalar5 = (50 - cameraZEntry3) * RECIPROCAL_2048[cameraZEntry2 - cameraZEntry3];
               clippedX[clippedXIndex] = clippedXOrViewportCenterX + (cameraX3 + ((cameraX[trianglePointsYEntry] - cameraX3) * scalar5 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedY[clippedXIndex] = clippedYOrViewportCenterY + (cameraY3 + ((cameraY[trianglePointsYEntry] - cameraY3) * scalar5 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedShading[clippedXIndex++] = clippedShadingOrTriangleHSLC + ((model.triangleHSLB[trianglePointsXIndex] - clippedShadingOrTriangleHSLC) * scalar5 >> 16);
            }

            if (cameraZEntry >= 50) {
               int scalar6 = (50 - cameraZEntry3) * RECIPROCAL_2048[cameraZEntry - cameraZEntry3];
               clippedX[clippedXIndex] = clippedXOrViewportCenterX + (cameraX3 + ((cameraX[trianglePointsXEntry] - cameraX3) * scalar6 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedY[clippedXIndex] = clippedYOrViewportCenterY + (cameraY3 + ((cameraY[trianglePointsXEntry] - cameraY3) * scalar6 >> 16) << Client.getProjectionScaleShift()) / 50;
               clippedShading[clippedXIndex++] = clippedShadingOrTriangleHSLC + ((model.triangleHSLA[trianglePointsXIndex] - clippedShadingOrTriangleHSLC) * scalar6 >> 16);
            }
         }

         int clippedXEntry = clippedX[0];
         int clippedX2 = clippedX[1];
         int clippedX3 = clippedX[2];
         int clippedYEntry = clippedY[0];
         clippedXOrViewportCenterX = clippedY[1];
         clippedYOrViewportCenterY = clippedY[2];
         if ((clippedXEntry - clippedX2) * (clippedYOrViewportCenterY - clippedXOrViewportCenterX) - (clippedYEntry - clippedXOrViewportCenterX) * (clippedX3 - clippedX2) <= 0) {
            return;
         }

         Rasterizer3D.restrictEdges = false;
         if (clippedXIndex == 3) {
            if (clippedXEntry < 0 || clippedX2 < 0 || clippedX3 < 0 || clippedXEntry > Rasterizer2D.centerX || clippedX2 > Rasterizer2D.centerX || clippedX3 > Rasterizer2D.centerX) {
               Rasterizer3D.restrictEdges = true;
            }

            if (model.triangleDrawType == null) {
               cameraZEntry = 0;
            } else {
               cameraZEntry = model.triangleDrawType[trianglePointsXIndex] & 3;
            }

            if (cameraZEntry == 0) {
               Rasterizer3D.drawShadedTriangle(true, clippedYEntry, clippedXOrViewportCenterX, clippedYOrViewportCenterY, clippedXEntry, clippedX2, clippedX3, clippedShading[0], clippedShading[1], clippedShading[2], -1.0F, -1.0F, -1.0F);
            } else if (cameraZEntry == 1) {
               Rasterizer3D.drawFlatTriangle(clippedYEntry, clippedXOrViewportCenterX, clippedYOrViewportCenterY, clippedXEntry, clippedX2, clippedX3, HSL_TO_RGB[model.triangleHSLA[trianglePointsXIndex]], -1.0F, -1.0F, -1.0F);
            } else if (cameraZEntry == 2) {
               cameraZEntry2 = model.triangleDrawType[trianglePointsXIndex] >> 2;
               cameraZEntry = model.texturedTrianglePointsX[cameraZEntry2];
               cameraZEntry3 = model.texturedTrianglePointsY[cameraZEntry2];
               cameraZEntry2 = model.texturedTrianglePointsZ[cameraZEntry2];
               Rasterizer3D.drawTexturedTriangle(
                  true,
                  clippedYEntry,
                  clippedXOrViewportCenterX,
                  clippedYOrViewportCenterY,
                  clippedXEntry,
                  clippedX2,
                  clippedX3,
                  clippedShading[0],
                  clippedShading[1],
                  clippedShading[2],
                  cameraX[cameraZEntry],
                  cameraX[cameraZEntry3],
                  cameraX[cameraZEntry2],
                  cameraY[cameraZEntry],
                  cameraY[cameraZEntry3],
                  cameraY[cameraZEntry2],
                  cameraZ[cameraZEntry],
                  cameraZ[cameraZEntry3],
                  cameraZ[cameraZEntry2],
                  model.triangleColorValues[trianglePointsXIndex],
                  cameraDepth[trianglePointsXEntry],
                  cameraDepth[trianglePointsYEntry],
                  cameraDepth[trianglePointsZEntry]
               );
            } else if (cameraZEntry == 3) {
               cameraZEntry2 = model.triangleDrawType[trianglePointsXIndex] >> 2;
               cameraZEntry = model.texturedTrianglePointsX[cameraZEntry2];
               cameraZEntry3 = model.texturedTrianglePointsY[cameraZEntry2];
               cameraZEntry2 = model.texturedTrianglePointsZ[cameraZEntry2];
               Rasterizer3D.drawTexturedTriangle(
                  true,
                  clippedYEntry,
                  clippedXOrViewportCenterX,
                  clippedYOrViewportCenterY,
                  clippedXEntry,
                  clippedX2,
                  clippedX3,
                  model.triangleHSLA[trianglePointsXIndex],
                  model.triangleHSLA[trianglePointsXIndex],
                  model.triangleHSLA[trianglePointsXIndex],
                  cameraX[cameraZEntry],
                  cameraX[cameraZEntry3],
                  cameraX[cameraZEntry2],
                  cameraY[cameraZEntry],
                  cameraY[cameraZEntry3],
                  cameraY[cameraZEntry2],
                  cameraZ[cameraZEntry],
                  cameraZ[cameraZEntry3],
                  cameraZ[cameraZEntry2],
                  model.triangleColorValues[trianglePointsXIndex],
                  cameraDepth[trianglePointsXEntry],
                  cameraDepth[trianglePointsYEntry],
                  cameraDepth[trianglePointsZEntry]
               );
            }
         }

         if (clippedXIndex != 4) {
            return;
         }

         if (clippedXEntry < 0
            || clippedX2 < 0
            || clippedX3 < 0
            || clippedXEntry > Rasterizer2D.centerX
            || clippedX2 > Rasterizer2D.centerX
            || clippedX3 > Rasterizer2D.centerX
            || clippedX[3] < 0
            || clippedX[3] > Rasterizer2D.centerX) {
            Rasterizer3D.restrictEdges = true;
         }

         if (model.triangleDrawType == null) {
            cameraZEntry = 0;
         } else {
            cameraZEntry = model.triangleDrawType[trianglePointsXIndex] & 3;
         }

         if (cameraZEntry == 0) {
            Rasterizer3D.drawShadedTriangle(true, clippedYEntry, clippedXOrViewportCenterX, clippedYOrViewportCenterY, clippedXEntry, clippedX2, clippedX3, clippedShading[0], clippedShading[1], clippedShading[2], -1.0F, -1.0F, -1.0F);
            Rasterizer3D.drawShadedTriangle(
               true,
               clippedYEntry,
               clippedYOrViewportCenterY,
               clippedY[3],
               clippedXEntry,
               clippedX3,
               clippedX[3],
               clippedShading[0],
               clippedShading[2],
               clippedShading[3],
               cameraDepth[trianglePointsXEntry],
               cameraDepth[trianglePointsYEntry],
               cameraDepth[trianglePointsZEntry]
            );
         } else if (cameraZEntry == 1) {
            cameraZEntry2 = HSL_TO_RGB[model.triangleHSLA[trianglePointsXIndex]];
            Rasterizer3D.drawFlatTriangle(clippedYEntry, clippedXOrViewportCenterX, clippedYOrViewportCenterY, clippedXEntry, clippedX2, clippedX3, cameraZEntry2, -1.0F, -1.0F, -1.0F);
            Rasterizer3D.drawFlatTriangle(clippedYEntry, clippedYOrViewportCenterY, clippedY[3], clippedXEntry, clippedX3, clippedX[3], cameraZEntry2, cameraDepth[trianglePointsXEntry], cameraDepth[trianglePointsYEntry], cameraDepth[trianglePointsZEntry]);
         } else {
            if (cameraZEntry != 2) {
               if (cameraZEntry == 3) {
                  cameraZEntry2 = model.triangleDrawType[trianglePointsXIndex] >> 2;
                  cameraZEntry = model.texturedTrianglePointsX[cameraZEntry2];
                  cameraZEntry3 = model.texturedTrianglePointsY[cameraZEntry2];
                  cameraZEntry2 = model.texturedTrianglePointsZ[cameraZEntry2];
                  Rasterizer3D.drawTexturedTriangle(
                     true,
                     clippedYEntry,
                     clippedXOrViewportCenterX,
                     clippedYOrViewportCenterY,
                     clippedXEntry,
                     clippedX2,
                     clippedX3,
                     model.triangleHSLA[trianglePointsXIndex],
                     model.triangleHSLA[trianglePointsXIndex],
                     model.triangleHSLA[trianglePointsXIndex],
                     cameraX[cameraZEntry],
                     cameraX[cameraZEntry3],
                     cameraX[cameraZEntry2],
                     cameraY[cameraZEntry],
                     cameraY[cameraZEntry3],
                     cameraY[cameraZEntry2],
                     cameraZ[cameraZEntry],
                     cameraZ[cameraZEntry3],
                     cameraZ[cameraZEntry2],
                     model.triangleColorValues[trianglePointsXIndex],
                     cameraDepth[trianglePointsXEntry],
                     cameraDepth[trianglePointsYEntry],
                     cameraDepth[trianglePointsZEntry]
                  );
                  Rasterizer3D.drawTexturedTriangle(
                     true,
                     clippedYEntry,
                     clippedYOrViewportCenterY,
                     clippedY[3],
                     clippedXEntry,
                     clippedX3,
                     clippedX[3],
                     model.triangleHSLA[trianglePointsXIndex],
                     model.triangleHSLA[trianglePointsXIndex],
                     model.triangleHSLA[trianglePointsXIndex],
                     cameraX[cameraZEntry],
                     cameraX[cameraZEntry3],
                     cameraX[cameraZEntry2],
                     cameraY[cameraZEntry],
                     cameraY[cameraZEntry3],
                     cameraY[cameraZEntry2],
                     cameraZ[cameraZEntry],
                     cameraZ[cameraZEntry3],
                     cameraZ[cameraZEntry2],
                     model.triangleColorValues[trianglePointsXIndex],
                     cameraDepth[trianglePointsXEntry],
                     cameraDepth[trianglePointsYEntry],
                     cameraDepth[trianglePointsZEntry]
                  );
               }

               return;
            }

            cameraZEntry2 = model.triangleDrawType[trianglePointsXIndex] >> 2;
            cameraZEntry = model.texturedTrianglePointsX[cameraZEntry2];
            cameraZEntry3 = model.texturedTrianglePointsY[cameraZEntry2];
            cameraZEntry2 = model.texturedTrianglePointsZ[cameraZEntry2];
            Rasterizer3D.drawTexturedTriangle(
               true,
               clippedYEntry,
               clippedXOrViewportCenterX,
               clippedYOrViewportCenterY,
               clippedXEntry,
               clippedX2,
               clippedX3,
               clippedShading[0],
               clippedShading[1],
               clippedShading[2],
               cameraX[cameraZEntry],
               cameraX[cameraZEntry3],
               cameraX[cameraZEntry2],
               cameraY[cameraZEntry],
               cameraY[cameraZEntry3],
               cameraY[cameraZEntry2],
               cameraZ[cameraZEntry],
               cameraZ[cameraZEntry3],
               cameraZ[cameraZEntry2],
               model.triangleColorValues[trianglePointsXIndex],
               cameraDepth[trianglePointsXEntry],
               cameraDepth[trianglePointsYEntry],
               cameraDepth[trianglePointsZEntry]
            );
            Rasterizer3D.drawTexturedTriangle(
               true,
               clippedYEntry,
               clippedYOrViewportCenterY,
               clippedY[3],
               clippedXEntry,
               clippedX3,
               clippedX[3],
               clippedShading[0],
               clippedShading[2],
               clippedShading[3],
               cameraX[cameraZEntry],
               cameraX[cameraZEntry3],
               cameraX[cameraZEntry2],
               cameraY[cameraZEntry],
               cameraY[cameraZEntry3],
               cameraY[cameraZEntry2],
               cameraZ[cameraZEntry],
               cameraZ[cameraZEntry3],
               cameraZ[cameraZEntry2],
               model.triangleColorValues[trianglePointsXIndex],
               cameraDepth[trianglePointsXEntry],
               cameraDepth[trianglePointsYEntry],
               cameraDepth[trianglePointsZEntry]
            );
         }
      } else {
         int trianglePointsXEntry2 = this.trianglePointsX[triangleNearClippedIndex];
         int trianglePointsYEntry2 = this.trianglePointsY[triangleNearClippedIndex];
         int trianglePointsZEntry2 = this.trianglePointsZ[triangleNearClippedIndex];
         Rasterizer3D.restrictEdges = triangleEdgeRestricted[triangleNearClippedIndex];
         if (this.triangleAlphaValues == null) {
            Rasterizer3D.alpha = 0;
         } else {
            Rasterizer3D.alpha = this.triangleAlphaValues[triangleNearClippedIndex];
         }

         int texturedTrianglePointsXIndex;
         if (this.triangleDrawType == null) {
            texturedTrianglePointsXIndex = 0;
         } else {
            texturedTrianglePointsXIndex = this.triangleDrawType[triangleNearClippedIndex] & 3;
         }

         if (texturedTrianglePointsXIndex == 0) {
            Rasterizer3D.drawShadedTriangle(
               true,
               projectedY[trianglePointsXEntry2],
               projectedY[trianglePointsYEntry2],
               projectedY[trianglePointsZEntry2],
               projectedX[trianglePointsXEntry2],
               projectedX[trianglePointsYEntry2],
               projectedX[trianglePointsZEntry2],
               this.triangleHSLA[triangleNearClippedIndex],
               this.triangleHSLB[triangleNearClippedIndex],
               this.triangleHSLC[triangleNearClippedIndex],
               cameraDepth[trianglePointsXEntry2],
               cameraDepth[trianglePointsYEntry2],
               cameraDepth[trianglePointsZEntry2]
            );
            return;
         }

         if (texturedTrianglePointsXIndex == 1) {
            Rasterizer3D.drawFlatTriangle(
               projectedY[trianglePointsXEntry2],
               projectedY[trianglePointsYEntry2],
               projectedY[trianglePointsZEntry2],
               projectedX[trianglePointsXEntry2],
               projectedX[trianglePointsYEntry2],
               projectedX[trianglePointsZEntry2],
               HSL_TO_RGB[this.triangleHSLA[triangleNearClippedIndex]],
               cameraDepth[trianglePointsXEntry2],
               cameraDepth[trianglePointsYEntry2],
               cameraDepth[trianglePointsZEntry2]
            );
            return;
         }

         if (texturedTrianglePointsXIndex == 2) {
            texturedTrianglePointsXIndex = this.triangleDrawType[triangleNearClippedIndex] >> 2;
            int texturedTrianglePointsXEntry = this.texturedTrianglePointsX[texturedTrianglePointsXIndex];
            int texturedTrianglePointsYEntry = this.texturedTrianglePointsY[texturedTrianglePointsXIndex];
            texturedTrianglePointsXIndex = this.texturedTrianglePointsZ[texturedTrianglePointsXIndex];
            Rasterizer3D.drawTexturedTriangle(
               true,
               projectedY[trianglePointsXEntry2],
               projectedY[trianglePointsYEntry2],
               projectedY[trianglePointsZEntry2],
               projectedX[trianglePointsXEntry2],
               projectedX[trianglePointsYEntry2],
               projectedX[trianglePointsZEntry2],
               this.triangleHSLA[triangleNearClippedIndex],
               this.triangleHSLB[triangleNearClippedIndex],
               this.triangleHSLC[triangleNearClippedIndex],
               cameraX[texturedTrianglePointsXEntry],
               cameraX[texturedTrianglePointsYEntry],
               cameraX[texturedTrianglePointsXIndex],
               cameraY[texturedTrianglePointsXEntry],
               cameraY[texturedTrianglePointsYEntry],
               cameraY[texturedTrianglePointsXIndex],
               cameraZ[texturedTrianglePointsXEntry],
               cameraZ[texturedTrianglePointsYEntry],
               cameraZ[texturedTrianglePointsXIndex],
               this.triangleColorValues[triangleNearClippedIndex],
               cameraDepth[trianglePointsXEntry2],
               cameraDepth[trianglePointsYEntry2],
               cameraDepth[trianglePointsZEntry2]
            );
            return;
         }

         if (texturedTrianglePointsXIndex == 3) {
            texturedTrianglePointsXIndex = this.triangleDrawType[triangleNearClippedIndex] >> 2;
            int texturedTrianglePointsXEntry2 = this.texturedTrianglePointsX[texturedTrianglePointsXIndex];
            int texturedTrianglePointsYEntry2 = this.texturedTrianglePointsY[texturedTrianglePointsXIndex];
            texturedTrianglePointsXIndex = this.texturedTrianglePointsZ[texturedTrianglePointsXIndex];
            Rasterizer3D.drawTexturedTriangle(
               true,
               projectedY[trianglePointsXEntry2],
               projectedY[trianglePointsYEntry2],
               projectedY[trianglePointsZEntry2],
               projectedX[trianglePointsXEntry2],
               projectedX[trianglePointsYEntry2],
               projectedX[trianglePointsZEntry2],
               this.triangleHSLA[triangleNearClippedIndex],
               this.triangleHSLA[triangleNearClippedIndex],
               this.triangleHSLA[triangleNearClippedIndex],
               cameraX[texturedTrianglePointsXEntry2],
               cameraX[texturedTrianglePointsYEntry2],
               cameraX[texturedTrianglePointsXIndex],
               cameraY[texturedTrianglePointsXEntry2],
               cameraY[texturedTrianglePointsYEntry2],
               cameraY[texturedTrianglePointsXIndex],
               cameraZ[texturedTrianglePointsXEntry2],
               cameraZ[texturedTrianglePointsYEntry2],
               cameraZ[texturedTrianglePointsXIndex],
               this.triangleColorValues[triangleNearClippedIndex],
               cameraDepth[trianglePointsXEntry2],
               cameraDepth[trianglePointsYEntry2],
               cameraDepth[trianglePointsZEntry2]
            );
         }
      }
   }
}
