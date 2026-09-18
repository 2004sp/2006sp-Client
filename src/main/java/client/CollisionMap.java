package client;
final class CollisionMap {
   public final int[][] clippingData = new int[104][104];

   public CollisionMap() {
      this.reset();
   }
   public final void reset() {
      for (int clippingDataIndex = 0; clippingDataIndex < 104; clippingDataIndex++) {
         for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
            if (clippingDataIndex != 0 && loopIndex != 0 && clippingDataIndex != 103 && loopIndex != 103) {
               this.clippingData[clippingDataIndex][loopIndex] = 16777216;
            } else {
               this.clippingData[clippingDataIndex][loopIndex] = 16777215;
            }
         }
      }
   }
   public final void addWall(int scalarArgument, int wallFlagIndex, int clippingDataIndex, int objectType, boolean flag) {
      if (objectType == 0) {
         if (wallFlagIndex == 0) {
            this.addFlag(clippingDataIndex, scalarArgument, 128);
            this.addFlag(clippingDataIndex - 1, scalarArgument, 8);
         }

         if (wallFlagIndex == 1) {
            this.addFlag(clippingDataIndex, scalarArgument, 2);
            this.addFlag(clippingDataIndex, scalarArgument + 1, 32);
         }

         if (wallFlagIndex == 2) {
            this.addFlag(clippingDataIndex, scalarArgument, 8);
            this.addFlag(clippingDataIndex + 1, scalarArgument, 128);
         }

         if (wallFlagIndex == 3) {
            this.addFlag(clippingDataIndex, scalarArgument, 32);
            this.addFlag(clippingDataIndex, scalarArgument - 1, 2);
         }
      }

      if (objectType == 1 || objectType == 3) {
         if (wallFlagIndex == 0) {
            this.addFlag(clippingDataIndex, scalarArgument, 1);
            this.addFlag(clippingDataIndex - 1, scalarArgument + 1, 16);
         }

         if (wallFlagIndex == 1) {
            this.addFlag(clippingDataIndex, scalarArgument, 4);
            this.addFlag(clippingDataIndex + 1, scalarArgument + 1, 64);
         }

         if (wallFlagIndex == 2) {
            this.addFlag(clippingDataIndex, scalarArgument, 16);
            this.addFlag(clippingDataIndex + 1, scalarArgument - 1, 1);
         }

         if (wallFlagIndex == 3) {
            this.addFlag(clippingDataIndex, scalarArgument, 64);
            this.addFlag(clippingDataIndex - 1, scalarArgument - 1, 4);
         }
      }

      if (objectType == 2) {
         if (wallFlagIndex == 0) {
            this.addFlag(clippingDataIndex, scalarArgument, 130);
            this.addFlag(clippingDataIndex - 1, scalarArgument, 8);
            this.addFlag(clippingDataIndex, scalarArgument + 1, 32);
         }

         if (wallFlagIndex == 1) {
            this.addFlag(clippingDataIndex, scalarArgument, 10);
            this.addFlag(clippingDataIndex, scalarArgument + 1, 32);
            this.addFlag(clippingDataIndex + 1, scalarArgument, 128);
         }

         if (wallFlagIndex == 2) {
            this.addFlag(clippingDataIndex, scalarArgument, 40);
            this.addFlag(clippingDataIndex + 1, scalarArgument, 128);
            this.addFlag(clippingDataIndex, scalarArgument - 1, 2);
         }

         if (wallFlagIndex == 3) {
            this.addFlag(clippingDataIndex, scalarArgument, 160);
            this.addFlag(clippingDataIndex, scalarArgument - 1, 2);
            this.addFlag(clippingDataIndex - 1, scalarArgument, 8);
         }
      }

      if (flag) {
         if (objectType == 0) {
            if (wallFlagIndex == 0) {
               this.addFlag(clippingDataIndex, scalarArgument, 65536);
               this.addFlag(clippingDataIndex - 1, scalarArgument, 4096);
            }

            if (wallFlagIndex == 1) {
               this.addFlag(clippingDataIndex, scalarArgument, 1024);
               this.addFlag(clippingDataIndex, scalarArgument + 1, 16384);
            }

            if (wallFlagIndex == 2) {
               this.addFlag(clippingDataIndex, scalarArgument, 4096);
               this.addFlag(clippingDataIndex + 1, scalarArgument, 65536);
            }

            if (wallFlagIndex == 3) {
               this.addFlag(clippingDataIndex, scalarArgument, 16384);
               this.addFlag(clippingDataIndex, scalarArgument - 1, 1024);
            }
         }

         if (objectType == 1 || objectType == 3) {
            if (wallFlagIndex == 0) {
               this.addFlag(clippingDataIndex, scalarArgument, 512);
               this.addFlag(clippingDataIndex - 1, scalarArgument + 1, 8192);
            }

            if (wallFlagIndex == 1) {
               this.addFlag(clippingDataIndex, scalarArgument, 2048);
               this.addFlag(clippingDataIndex + 1, scalarArgument + 1, 32768);
            }

            if (wallFlagIndex == 2) {
               this.addFlag(clippingDataIndex, scalarArgument, 8192);
               this.addFlag(clippingDataIndex + 1, scalarArgument - 1, 512);
            }

            if (wallFlagIndex == 3) {
               this.addFlag(clippingDataIndex, scalarArgument, 32768);
               this.addFlag(clippingDataIndex - 1, scalarArgument - 1, 2048);
            }
         }

         if (objectType == 2) {
            if (wallFlagIndex == 0) {
               this.addFlag(clippingDataIndex, scalarArgument, 66560);
               this.addFlag(clippingDataIndex - 1, scalarArgument, 4096);
               this.addFlag(clippingDataIndex, scalarArgument + 1, 16384);
            }

            if (wallFlagIndex == 1) {
               this.addFlag(clippingDataIndex, scalarArgument, 5120);
               this.addFlag(clippingDataIndex, scalarArgument + 1, 16384);
               this.addFlag(clippingDataIndex + 1, scalarArgument, 65536);
            }

            if (wallFlagIndex == 2) {
               this.addFlag(clippingDataIndex, scalarArgument, 20480);
               this.addFlag(clippingDataIndex + 1, scalarArgument, 65536);
               this.addFlag(clippingDataIndex, scalarArgument - 1, 1024);
            }

            if (wallFlagIndex == 3) {
               this.addFlag(clippingDataIndex, scalarArgument, 81920);
               this.addFlag(clippingDataIndex, scalarArgument - 1, 1024);
               this.addFlag(clippingDataIndex - 1, scalarArgument, 4096);
            }
         }
      }
   }
   public final void addObject(boolean flag, int newTemp, int temp2, int clippingDataIndex2, int newIndex, int wallFlagIndex) {
      int scalar = 256;
      if (flag) {
         scalar = 131328;
      }

      if (wallFlagIndex == 1 || wallFlagIndex == 3) {
         int temp = newTemp;
         newTemp = temp2;
         temp2 = temp;
      }

      for (int clippingDataIndex = clippingDataIndex2; clippingDataIndex < clippingDataIndex2 + newTemp; clippingDataIndex++) {
         if (clippingDataIndex >= 0 && clippingDataIndex < 104) {
            for (int loopIndex = newIndex; loopIndex < newIndex + temp2; loopIndex++) {
               if (loopIndex >= 0 && loopIndex < 104) {
                  this.addFlag(clippingDataIndex, loopIndex, scalar);
               }
            }
         }
      }
   }
   public final void setBlocked(int scalarArgument, int clippingDataIndex) {
      this.clippingData[clippingDataIndex][scalarArgument] = this.clippingData[clippingDataIndex][scalarArgument] | 2097152;
   }
   private void addFlag(int clippingDataIndex, int scalarArgument, int scalarArgument2) {
      this.clippingData[clippingDataIndex][scalarArgument] = this.clippingData[clippingDataIndex][scalarArgument] | scalarArgument2;
   }
   public final void removeWall(int scalarArgument, int scalarArgument2, boolean flag, int clippingDataIndex, int tileY) {
      if (scalarArgument2 == 0) {
         if (scalarArgument == 0) {
            this.removeFlag(128, clippingDataIndex, tileY);
            this.removeFlag(8, clippingDataIndex - 1, tileY);
         }

         if (scalarArgument == 1) {
            this.removeFlag(2, clippingDataIndex, tileY);
            this.removeFlag(32, clippingDataIndex, tileY + 1);
         }

         if (scalarArgument == 2) {
            this.removeFlag(8, clippingDataIndex, tileY);
            this.removeFlag(128, clippingDataIndex + 1, tileY);
         }

         if (scalarArgument == 3) {
            this.removeFlag(32, clippingDataIndex, tileY);
            this.removeFlag(2, clippingDataIndex, tileY - 1);
         }
      }

      if (scalarArgument2 == 1 || scalarArgument2 == 3) {
         if (scalarArgument == 0) {
            this.removeFlag(1, clippingDataIndex, tileY);
            this.removeFlag(16, clippingDataIndex - 1, tileY + 1);
         }

         if (scalarArgument == 1) {
            this.removeFlag(4, clippingDataIndex, tileY);
            this.removeFlag(64, clippingDataIndex + 1, tileY + 1);
         }

         if (scalarArgument == 2) {
            this.removeFlag(16, clippingDataIndex, tileY);
            this.removeFlag(1, clippingDataIndex + 1, tileY - 1);
         }

         if (scalarArgument == 3) {
            this.removeFlag(64, clippingDataIndex, tileY);
            this.removeFlag(4, clippingDataIndex - 1, tileY - 1);
         }
      }

      if (scalarArgument2 == 2) {
         if (scalarArgument == 0) {
            this.removeFlag(130, clippingDataIndex, tileY);
            this.removeFlag(8, clippingDataIndex - 1, tileY);
            this.removeFlag(32, clippingDataIndex, tileY + 1);
         }

         if (scalarArgument == 1) {
            this.removeFlag(10, clippingDataIndex, tileY);
            this.removeFlag(32, clippingDataIndex, tileY + 1);
            this.removeFlag(128, clippingDataIndex + 1, tileY);
         }

         if (scalarArgument == 2) {
            this.removeFlag(40, clippingDataIndex, tileY);
            this.removeFlag(128, clippingDataIndex + 1, tileY);
            this.removeFlag(2, clippingDataIndex, tileY - 1);
         }

         if (scalarArgument == 3) {
            this.removeFlag(160, clippingDataIndex, tileY);
            this.removeFlag(2, clippingDataIndex, tileY - 1);
            this.removeFlag(8, clippingDataIndex - 1, tileY);
         }
      }

      if (flag) {
         if (scalarArgument2 == 0) {
            if (scalarArgument == 0) {
               this.removeFlag(65536, clippingDataIndex, tileY);
               this.removeFlag(4096, clippingDataIndex - 1, tileY);
            }

            if (scalarArgument == 1) {
               this.removeFlag(1024, clippingDataIndex, tileY);
               this.removeFlag(16384, clippingDataIndex, tileY + 1);
            }

            if (scalarArgument == 2) {
               this.removeFlag(4096, clippingDataIndex, tileY);
               this.removeFlag(65536, clippingDataIndex + 1, tileY);
            }

            if (scalarArgument == 3) {
               this.removeFlag(16384, clippingDataIndex, tileY);
               this.removeFlag(1024, clippingDataIndex, tileY - 1);
            }
         }

         if (scalarArgument2 == 1 || scalarArgument2 == 3) {
            if (scalarArgument == 0) {
               this.removeFlag(512, clippingDataIndex, tileY);
               this.removeFlag(8192, clippingDataIndex - 1, tileY + 1);
            }

            if (scalarArgument == 1) {
               this.removeFlag(2048, clippingDataIndex, tileY);
               this.removeFlag(32768, clippingDataIndex + 1, tileY + 1);
            }

            if (scalarArgument == 2) {
               this.removeFlag(8192, clippingDataIndex, tileY);
               this.removeFlag(512, clippingDataIndex + 1, tileY - 1);
            }

            if (scalarArgument == 3) {
               this.removeFlag(32768, clippingDataIndex, tileY);
               this.removeFlag(2048, clippingDataIndex - 1, tileY - 1);
            }
         }

         if (scalarArgument2 == 2) {
            if (scalarArgument == 0) {
               this.removeFlag(66560, clippingDataIndex, tileY);
               this.removeFlag(4096, clippingDataIndex - 1, tileY);
               this.removeFlag(16384, clippingDataIndex, tileY + 1);
            }

            if (scalarArgument == 1) {
               this.removeFlag(5120, clippingDataIndex, tileY);
               this.removeFlag(16384, clippingDataIndex, tileY + 1);
               this.removeFlag(65536, clippingDataIndex + 1, tileY);
            }

            if (scalarArgument == 2) {
               this.removeFlag(20480, clippingDataIndex, tileY);
               this.removeFlag(65536, clippingDataIndex + 1, tileY);
               this.removeFlag(1024, clippingDataIndex, tileY - 1);
            }

            if (scalarArgument == 3) {
               this.removeFlag(81920, clippingDataIndex, tileY);
               this.removeFlag(1024, clippingDataIndex, tileY - 1);
               this.removeFlag(4096, clippingDataIndex - 1, tileY);
            }
         }
      }
   }
   public final void removeObject(int scalarArgument, int sizeX, int clippingDataIndex2, int newIndex, int sizeY, boolean flag) {
      int scalar = 256;
      if (flag) {
         scalar = 131328;
      }

      if (scalarArgument == 1 || scalarArgument == 3) {
         scalarArgument = sizeX;
         sizeX = sizeY;
         sizeY = scalarArgument;
      }

      for (int clippingDataIndex = clippingDataIndex2; clippingDataIndex < clippingDataIndex2 + sizeX; clippingDataIndex++) {
         if (clippingDataIndex >= 0 && clippingDataIndex < 104) {
            for (int loopIndex = newIndex; loopIndex < newIndex + sizeY; loopIndex++) {
               if (loopIndex >= 0 && loopIndex < 104) {
                  this.removeFlag(scalar, clippingDataIndex, loopIndex);
               }
            }
         }
      }
   }
   private void removeFlag(int scalarArgument, int clippingDataIndex, int tileY) {
      this.clippingData[clippingDataIndex][tileY] = this.clippingData[clippingDataIndex][tileY] & 16777215 - scalarArgument;
   }
}
