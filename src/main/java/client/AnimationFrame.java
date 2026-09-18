package client;

import java.util.Hashtable;
public final class AnimationFrame {
   private static AnimationFrame[] frames;
   public int frameLength;
   public AnimationSkeleton skeleton;
   public int transformCount;
   public int[] transformIndices;
   public int[] transformX;
   public int[] transformY;
   public int[] transformZ;
   private static boolean[] noAlphaTransform;
   static Hashtable frameCache = new Hashtable();
   public static void initialize(int scalarArgument) {
      frames = new AnimationFrame[scalarArgument + 1];
      noAlphaTransform = new boolean[scalarArgument + 1];

      for (int noAlphaTransformIndex = 0; noAlphaTransformIndex < scalarArgument + 1; noAlphaTransformIndex++) {
         noAlphaTransform[noAlphaTransformIndex] = true;
      }
   }
   public static void load(byte[] byteBufferArgument, int id) {
      if (Client.hdModels) {

         try {
            Buffer buffer = new Buffer(byteBufferArgument);
            AnimationSkeleton animationSkeleton = new AnimationSkeleton(buffer, 530);
            int decodedUnsignedShort = buffer.readUnsignedShort();
            int[] transformIndice = new int[500];
            int[] localTransformX = new int[500];
            int[] values = new int[500];
            int[] localTransformZ = new int[500];

            for (int loopIndex = 0; loopIndex < decodedUnsignedShort; loopIndex++) {
               int readUnsignedShort2 = buffer.readUnsignedShort();
               AnimationFrame animationFrame = new AnimationFrame();
               frameCache.put(new Integer((id << 16) + readUnsignedShort2), animationFrame);
               animationFrame.skeleton = animationSkeleton;
               readUnsignedShort2 = buffer.readUnsignedByte();
               int transformIndiceIndex = 0;
               int sourceTransformTypeIndex = -1;

               for (int transformTypeIndex = 0; transformTypeIndex < readUnsignedShort2; transformTypeIndex++) {
                  int decodedUnsignedByte;
                  if ((decodedUnsignedByte = buffer.readUnsignedByte()) > 0) {
                     if (animationSkeleton.transformTypes[transformTypeIndex] != 0) {
                        for (int transformTypeIndex2 = transformTypeIndex - 1; transformTypeIndex2 > sourceTransformTypeIndex; transformTypeIndex2--) {
                           if (animationSkeleton.transformTypes[transformTypeIndex2] == 0) {
                              transformIndice[transformIndiceIndex] = transformTypeIndex2;
                              localTransformX[transformIndiceIndex] = 0;
                              localTransformZ[transformIndiceIndex] = values[transformIndiceIndex] = 0;
                              transformIndiceIndex++;
                              break;
                           }
                        }
                     }

                     transformIndice[transformIndiceIndex] = transformTypeIndex;
                     short shortCode = 0;
                     if (animationSkeleton.transformTypes[transformTypeIndex] == 3) {
                        shortCode = 128;
                     }

                     if ((decodedUnsignedByte & 1) != 0) {
                        localTransformX[transformIndiceIndex] = buffer.readAdjustedUnsignedShort();
                     } else {
                        localTransformX[transformIndiceIndex] = shortCode;
                     }

                     if ((decodedUnsignedByte & 2) != 0) {
                        values[transformIndiceIndex] = buffer.readAdjustedUnsignedShort();
                     } else {
                        values[transformIndiceIndex] = shortCode;
                     }

                     if ((decodedUnsignedByte & 4) != 0) {
                        localTransformZ[transformIndiceIndex] = buffer.readAdjustedUnsignedShort();
                     } else {
                        localTransformZ[transformIndiceIndex] = shortCode;
                     }

                     if (Client.smoothAnimations && animationSkeleton.transformTypes[transformTypeIndex] == 2) {
                        localTransformX[transformIndiceIndex] = ((localTransformX[transformIndiceIndex] & 0xFF) << 3) + (localTransformX[transformIndiceIndex] >> 8 & 7);
                        values[transformIndiceIndex] = ((values[transformIndiceIndex] & 0xFF) << 3) + (values[transformIndiceIndex] >> 8 & 7);
                        localTransformZ[transformIndiceIndex] = ((localTransformZ[transformIndiceIndex] & 0xFF) << 3) + (localTransformZ[transformIndiceIndex] >> 8 & 7);
                     }

                     sourceTransformTypeIndex = transformTypeIndex;
                     transformIndiceIndex++;
                  }
               }

               animationFrame.transformCount = transformIndiceIndex;
               animationFrame.transformIndices = new int[transformIndiceIndex];
               animationFrame.transformX = new int[transformIndiceIndex];
               animationFrame.transformY = new int[transformIndiceIndex];
               animationFrame.transformZ = new int[transformIndiceIndex];

               for (int transformIndex = 0; transformIndex < transformIndiceIndex; transformIndex++) {
                  animationFrame.transformIndices[transformIndex] = transformIndice[transformIndex];
                  animationFrame.transformX[transformIndex] = localTransformX[transformIndex];
                  animationFrame.transformY[transformIndex] = values[transformIndex];
                  animationFrame.transformZ[transformIndex] = localTransformZ[transformIndex];
               }
            }
         } catch (Exception exception) {
            return;
         }
      } else {
         decodeLegacy(byteBufferArgument, id);
      }
   }
   private static void decodeLegacy(byte[] newCurrentPosition, int id) {
      Buffer buffer;
      (buffer = new Buffer(newCurrentPosition)).currentPosition = newCurrentPosition.length - 12;
      int currentPositionOrReadInt = buffer.readInt();
      int decodedInt = buffer.readInt();
      int readInt2 = buffer.readInt();
      Buffer buffer5;
      (buffer5 = new Buffer(newCurrentPosition)).currentPosition = 0;
      currentPositionOrReadInt = 0 + currentPositionOrReadInt + 4;
      Buffer buffer2;
      (buffer2 = new Buffer(newCurrentPosition)).currentPosition = currentPositionOrReadInt;
      currentPositionOrReadInt += decodedInt;
      Buffer buffer3;
      (buffer3 = new Buffer(newCurrentPosition)).currentPosition = currentPositionOrReadInt;
      currentPositionOrReadInt += readInt2;
      Buffer buffer4;
      (buffer4 = new Buffer(newCurrentPosition)).currentPosition = currentPositionOrReadInt;
      AnimationSkeleton animationSkeleton = new AnimationSkeleton(buffer4, 474);
      int readInt3 = buffer5.readInt();
      int[] transformIndice = new int[500];
      int[] localTransformX = new int[500];
      int[] localTransformY = new int[500];
      int[] localTransformZ = new int[500];

      for (int loopIndex = 0; loopIndex < readInt3; loopIndex++) {
         int readInt4 = buffer5.readInt();
         AnimationFrame animationFrame = new AnimationFrame();
         frameCache.put(new Integer((id << 16) + readInt4), animationFrame);
         animationFrame.skeleton = animationSkeleton;
         readInt4 = buffer5.readUnsignedByte();
         int sourceTransformTypeIndex = -1;
         int transformIndiceIndex = 0;

         for (int transformTypeIndex = 0; transformTypeIndex < readInt4; transformTypeIndex++) {
            int decodedUnsignedByte;
            if ((decodedUnsignedByte = buffer2.readUnsignedByte()) > 0) {
               if (animationSkeleton.transformTypes[transformTypeIndex] != 0) {
                  for (int transformTypeIndex2 = transformTypeIndex - 1; transformTypeIndex2 > sourceTransformTypeIndex; transformTypeIndex2--) {
                     if (animationSkeleton.transformTypes[transformTypeIndex2] == 0) {
                        transformIndice[transformIndiceIndex] = transformTypeIndex2;
                        localTransformX[transformIndiceIndex] = 0;
                        localTransformY[transformIndiceIndex] = 0;
                        localTransformZ[transformIndiceIndex] = 0;
                        transformIndiceIndex++;
                        break;
                     }
                  }
               }

               transformIndice[transformIndiceIndex] = transformTypeIndex;
               short shortCode = 0;
               if (animationSkeleton.transformTypes[transformTypeIndex] == 3) {
                  shortCode = 128;
               }

               if ((decodedUnsignedByte & 1) != 0) {
                  localTransformX[transformIndiceIndex] = buffer3.readSignedSmart();
               } else {
                  localTransformX[transformIndiceIndex] = shortCode;
               }

               if ((decodedUnsignedByte & 2) != 0) {
                  localTransformY[transformIndiceIndex] = buffer3.readSignedSmart();
               } else {
                  localTransformY[transformIndiceIndex] = shortCode;
               }

               if ((decodedUnsignedByte & 4) != 0) {
                  localTransformZ[transformIndiceIndex] = buffer3.readSignedSmart();
               } else {
                  localTransformZ[transformIndiceIndex] = shortCode;
               }

               if (Client.smoothAnimations && animationSkeleton.transformTypes[transformTypeIndex] == 2) {
                  localTransformX[transformIndiceIndex] = ((localTransformX[transformIndiceIndex] & 0xFF) << 3) + (localTransformX[transformIndiceIndex] >> 8 & 7);
                  localTransformY[transformIndiceIndex] = ((localTransformY[transformIndiceIndex] & 0xFF) << 3) + (localTransformY[transformIndiceIndex] >> 8 & 7);
                  localTransformZ[transformIndiceIndex] = ((localTransformZ[transformIndiceIndex] & 0xFF) << 3) + (localTransformZ[transformIndiceIndex] >> 8 & 7);
               }

               sourceTransformTypeIndex = transformTypeIndex;
               transformIndiceIndex++;
            }
         }

         animationFrame.transformCount = transformIndiceIndex;
         animationFrame.transformIndices = new int[transformIndiceIndex];
         animationFrame.transformX = new int[transformIndiceIndex];
         animationFrame.transformY = new int[transformIndiceIndex];
         animationFrame.transformZ = new int[transformIndiceIndex];

         for (int transformIndex = 0; transformIndex < transformIndiceIndex; transformIndex++) {
            animationFrame.transformIndices[transformIndex] = transformIndice[transformIndex];
            animationFrame.transformX[transformIndex] = localTransformX[transformIndex];
            animationFrame.transformY[transformIndex] = localTransformY[transformIndex];
            animationFrame.transformZ[transformIndex] = localTransformZ[transformIndex];
         }
      }
   }
   public static void decodeClassic(byte[] newCurrentPosition) {
      Buffer buffer;
      (buffer = new Buffer(newCurrentPosition)).currentPosition = newCurrentPosition.length - 8;
      int sourceCurrentPosition = buffer.readUnsignedShort();
      int readUnsignedShort2 = buffer.readUnsignedShort();
      int readUnsignedShort3 = buffer.readUnsignedShort();
      int readUnsignedShort4 = buffer.readUnsignedShort();
      Buffer buffer6;
      (buffer6 = new Buffer(newCurrentPosition)).currentPosition = 0;
      sourceCurrentPosition = sourceCurrentPosition + 0 + 2;
      Buffer buffer2;
      (buffer2 = new Buffer(newCurrentPosition)).currentPosition = sourceCurrentPosition;
      sourceCurrentPosition += readUnsignedShort2;
      Buffer buffer3;
      (buffer3 = new Buffer(newCurrentPosition)).currentPosition = sourceCurrentPosition;
      sourceCurrentPosition += readUnsignedShort3;
      Buffer buffer4;
      (buffer4 = new Buffer(newCurrentPosition)).currentPosition = sourceCurrentPosition;
      sourceCurrentPosition += readUnsignedShort4;
      Buffer buffer5;
      (buffer5 = new Buffer(newCurrentPosition)).currentPosition = sourceCurrentPosition;
      AnimationSkeleton animationSkeleton = new AnimationSkeleton(buffer5);
      int readUnsignedShort5 = buffer6.readUnsignedShort();
      int[] transformIndice = new int[500];
      int[] localTransformX = new int[500];
      int[] localTransformY = new int[500];
      int[] localTransformZ = new int[500];

      for (int loopIndex = 0; loopIndex < readUnsignedShort5; loopIndex++) {
         int readUnsignedShort6 = buffer6.readUnsignedShort();
         AnimationFrame animationFrame;
         (animationFrame = frames[readUnsignedShort6] = new AnimationFrame()).frameLength = buffer4.readUnsignedByte();
         animationFrame.skeleton = animationSkeleton;
         int decodedUnsignedByte = buffer6.readUnsignedByte();
         int sourceTransformTypeIndex = -1;
         int transformIndiceIndex = 0;

         for (int transformTypeIndex = 0; transformTypeIndex < decodedUnsignedByte; transformTypeIndex++) {
            int readUnsignedByte2;
            if ((readUnsignedByte2 = buffer2.readUnsignedByte()) > 0) {
               if (animationSkeleton.transformTypes[transformTypeIndex] != 0) {
                  for (int transformTypeIndex2 = transformTypeIndex - 1; transformTypeIndex2 > sourceTransformTypeIndex; transformTypeIndex2--) {
                     if (animationSkeleton.transformTypes[transformTypeIndex2] == 0) {
                        transformIndice[transformIndiceIndex] = transformTypeIndex2;
                        localTransformX[transformIndiceIndex] = 0;
                        localTransformY[transformIndiceIndex] = 0;
                        localTransformZ[transformIndiceIndex] = 0;
                        transformIndiceIndex++;
                        break;
                     }
                  }
               }

               transformIndice[transformIndiceIndex] = transformTypeIndex;
               short shortCode = 0;
               if (animationSkeleton.transformTypes[transformTypeIndex] == 3) {
                  shortCode = 128;
               }

               if ((readUnsignedByte2 & 1) != 0) {
                  localTransformX[transformIndiceIndex] = buffer3.readSignedSmart();
               } else {
                  localTransformX[transformIndiceIndex] = shortCode;
               }

               if ((readUnsignedByte2 & 2) != 0) {
                  localTransformY[transformIndiceIndex] = buffer3.readSignedSmart();
               } else {
                  localTransformY[transformIndiceIndex] = shortCode;
               }

               if ((readUnsignedByte2 & 4) != 0) {
                  localTransformZ[transformIndiceIndex] = buffer3.readSignedSmart();
               } else {
                  localTransformZ[transformIndiceIndex] = shortCode;
               }

               if (Client.smoothAnimations && animationSkeleton.transformTypes[transformTypeIndex] == 2) {
                  localTransformX[transformIndiceIndex] = ((localTransformX[transformIndiceIndex] & 0xFF) << 3) + (localTransformX[transformIndiceIndex] >> 8 & 7);
                  localTransformY[transformIndiceIndex] = ((localTransformY[transformIndiceIndex] & 0xFF) << 3) + (localTransformY[transformIndiceIndex] >> 8 & 7);
                  localTransformZ[transformIndiceIndex] = ((localTransformZ[transformIndiceIndex] & 0xFF) << 3) + (localTransformZ[transformIndiceIndex] >> 8 & 7);
               }

               sourceTransformTypeIndex = transformTypeIndex;
               transformIndiceIndex++;
               if (animationSkeleton.transformTypes[transformTypeIndex] == 5) {
                  noAlphaTransform[readUnsignedShort6] = false;
               }
            }
         }

         animationFrame.transformCount = transformIndiceIndex;
         animationFrame.transformIndices = new int[transformIndiceIndex];
         animationFrame.transformX = new int[transformIndiceIndex];
         animationFrame.transformY = new int[transformIndiceIndex];
         animationFrame.transformZ = new int[transformIndiceIndex];

         for (int transformIndex = 0; transformIndex < transformIndiceIndex; transformIndex++) {
            animationFrame.transformIndices[transformIndex] = transformIndice[transformIndex];
            animationFrame.transformX[transformIndex] = localTransformX[transformIndex];
            animationFrame.transformY[transformIndex] = localTransformY[transformIndex];
            animationFrame.transformZ[transformIndex] = localTransformZ[transformIndex];
         }
      }
   }
   public static void clear() {
      frameCache = null;
      frames = null;
   }
   public static AnimationFrame get(int frameIndex) {
      if (Client.hdModels || frameIndex >= frames.length || Client.use2007Models) {
         return getCached(frameIndex);
      } else {
         return frames == null ? null : frames[frameIndex];
      }
   }
   private static AnimationFrame getCached(int frameIndex) {
      try {
         int scalar = frameIndex >> 16;
         AnimationFrame animationFrame;
         if ((animationFrame = (AnimationFrame)frameCache.get(new Integer(frameIndex))) == null) {
            Client.getClient().onDemandFetcher.provide(1, scalar);
            return null;
         } else {
            return animationFrame;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         return frames != null ? frames[frameIndex] : null;
      }
   }
   public static boolean isNullFrame(int frameIndex) {
      return frameIndex == -1;
   }

   private AnimationFrame() {
   }
}
