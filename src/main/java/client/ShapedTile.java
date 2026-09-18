package client;
final class ShapedTile {
   public int underlayCornerColor0;
   public int underlayCornerColor1;
   public int underlayCornerColor2;
   public int underlayCornerColor3;
   public int overlayCornerColor0;
   public int overlayCornerColor1;
   public int overlayCornerColor2;
   public int overlayCornerColor3;
   public boolean hasTexture;
   final int[] vertexX;
   final int[] vertexY;
   final int[] vertexZ;
   final int[] triangleColorA;
   final int[] triangleColorB;
   final int[] triangleColorC;
   final int[] triangleVertexA;
   final int[] triangleVertexB;
   final int[] triangleVertexC;
   int[] triangleTextureIds;
   final boolean flat;
   final int shape;
   final int rotation;
   final int underlayRgb;
   final int overlayRgb;
   static final int[] projectedDepth = new int[6];
   static final int[] projectedX = new int[6];
   static final int[] projectedY = new int[6];
   static final int[] cameraX = new int[6];
   static final int[] cameraY = new int[6];
   static final int[] cameraZ = new int[6];
   private static final int[][] shapeVertexCodes = new int[][]{
      {1, 3, 5, 7},
      {1, 3, 5, 7},
      {1, 3, 5, 7},
      {1, 3, 5, 7, 6},
      {1, 3, 5, 7, 6},
      {1, 3, 5, 7, 6},
      {1, 3, 5, 7, 6},
      {1, 3, 5, 7, 2, 6},
      {1, 3, 5, 7, 2, 8},
      {1, 3, 5, 7, 2, 8},
      {1, 3, 5, 7, 11, 12},
      {1, 3, 5, 7, 11, 12},
      {1, 3, 5, 7, 13, 14}
   };
   private static final int[][] shapeTriangleData = new int[][]{
      {0, 1, 2, 3, 0, 0, 1, 3},
      {1, 1, 2, 3, 1, 0, 1, 3},
      {0, 1, 2, 3, 1, 0, 1, 3},
      {0, 0, 1, 2, 0, 0, 2, 4, 1, 0, 4, 3},
      {0, 0, 1, 4, 0, 0, 4, 3, 1, 1, 2, 4},
      {0, 0, 4, 3, 1, 0, 1, 2, 1, 0, 2, 4},
      {0, 1, 2, 4, 1, 0, 1, 4, 1, 0, 4, 3},
      {0, 4, 1, 2, 0, 4, 2, 5, 1, 0, 4, 5, 1, 0, 5, 3},
      {0, 4, 1, 2, 0, 4, 2, 3, 0, 4, 3, 5, 1, 0, 4, 5},
      {0, 0, 4, 5, 1, 4, 1, 2, 1, 4, 2, 3, 1, 4, 3, 5},
      {0, 0, 1, 5, 0, 1, 4, 5, 0, 1, 2, 4, 1, 0, 5, 3, 1, 5, 4, 3, 1, 4, 2, 3},
      {1, 0, 1, 5, 1, 1, 4, 5, 1, 1, 2, 4, 0, 0, 5, 3, 0, 5, 4, 3, 0, 4, 2, 3},
      {1, 0, 5, 4, 1, 0, 1, 5, 0, 0, 4, 3, 0, 4, 5, 3, 0, 5, 2, 3, 0, 1, 2, 5}
   };

   public ShapedTile(
      int newTriangleVertexC,
      int newOverlayCornerColor0,
      int newUnderlayCornerColor3,
      int sourceLocalVertexY,
      int triangleTextureId,
      int newOverlayCornerColor2,
      int newRotation,
      int newUnderlayCornerColor0,
      int newUnderlayRgb,
      int newUnderlayCornerColor2,
      int sourceLocalVertexY2,
      int sourceLocalVertexY3,
      int newFlat,
      int newShape,
      int newOverlayCornerColor3,
      int newOverlayCornerColor1,
      int newUnderlayCornerColor1,
      int sourceLocalVertexX,
      int sourceOverlayRgb
   ) {
      this.underlayCornerColor0 = newUnderlayCornerColor0;
      this.underlayCornerColor1 = newUnderlayCornerColor1;
      this.underlayCornerColor2 = newUnderlayCornerColor2;
      this.underlayCornerColor3 = newUnderlayCornerColor3;
      this.overlayCornerColor0 = newOverlayCornerColor0;
      this.overlayCornerColor1 = newOverlayCornerColor1;
      this.overlayCornerColor2 = newOverlayCornerColor2;
      this.overlayCornerColor3 = newOverlayCornerColor3;
      this.flat = newFlat == sourceLocalVertexY3 && newFlat == sourceLocalVertexY && newFlat == sourceLocalVertexY2;
      this.shape = newShape;
      this.rotation = newRotation;
      this.underlayRgb = newUnderlayRgb;
      this.overlayRgb = sourceOverlayRgb;
      int[] localShapeVertexCodes;
      sourceOverlayRgb = (localShapeVertexCodes = shapeVertexCodes[newShape]).length;
      this.vertexX = new int[sourceOverlayRgb];
      this.vertexY = new int[sourceOverlayRgb];
      this.vertexZ = new int[sourceOverlayRgb];
      int[] localTriangleColorA = new int[sourceOverlayRgb];
      int[] triangleColorA2 = new int[sourceOverlayRgb];
      sourceLocalVertexX <<= 7;
      newTriangleVertexC <<= 7;

      for (int vertexXIndex = 0; vertexXIndex < sourceOverlayRgb; vertexXIndex++) {
         int localShapeVertexCode;
         if (((localShapeVertexCode = localShapeVertexCodes[vertexXIndex]) & 1) == 0 && localShapeVertexCode <= 8) {
            localShapeVertexCode = (localShapeVertexCode - newRotation - newRotation - 1 & 7) + 1;
         }

         if (localShapeVertexCode > 8 && localShapeVertexCode <= 12) {
            localShapeVertexCode = (localShapeVertexCode - 9 - newRotation & 3) + 9;
         }

         if (localShapeVertexCode > 12 && localShapeVertexCode <= 16) {
            localShapeVertexCode = (localShapeVertexCode - 13 - newRotation & 3) + 13;
         }

         int localVertexY;
         int scalar;
         int localVertexX;
         int localVertexZ;
         int scalar2;
         if (localShapeVertexCode == 1) {
            localVertexX = sourceLocalVertexX;
            localVertexZ = newTriangleVertexC;
            localVertexY = newFlat;
            scalar = newUnderlayCornerColor0;
            scalar2 = newOverlayCornerColor0;
         } else if (localShapeVertexCode == 2) {
            localVertexX = sourceLocalVertexX + 64;
            localVertexZ = newTriangleVertexC;
            localVertexY = newFlat + sourceLocalVertexY3 >> 1;
            scalar = newUnderlayCornerColor0 + newUnderlayCornerColor1 >> 1;
            scalar2 = newOverlayCornerColor0 + newOverlayCornerColor1 >> 1;
         } else if (localShapeVertexCode == 3) {
            localVertexX = sourceLocalVertexX + 128;
            localVertexZ = newTriangleVertexC;
            localVertexY = sourceLocalVertexY3;
            scalar = newUnderlayCornerColor1;
            scalar2 = newOverlayCornerColor1;
         } else if (localShapeVertexCode == 4) {
            localVertexX = sourceLocalVertexX + 128;
            localVertexZ = newTriangleVertexC + 64;
            localVertexY = sourceLocalVertexY3 + sourceLocalVertexY >> 1;
            scalar = newUnderlayCornerColor1 + newUnderlayCornerColor2 >> 1;
            scalar2 = newOverlayCornerColor1 + newOverlayCornerColor2 >> 1;
         } else if (localShapeVertexCode == 5) {
            localVertexX = sourceLocalVertexX + 128;
            localVertexZ = newTriangleVertexC + 128;
            localVertexY = sourceLocalVertexY;
            scalar = newUnderlayCornerColor2;
            scalar2 = newOverlayCornerColor2;
         } else if (localShapeVertexCode == 6) {
            localVertexX = sourceLocalVertexX + 64;
            localVertexZ = newTriangleVertexC + 128;
            localVertexY = sourceLocalVertexY + sourceLocalVertexY2 >> 1;
            scalar = newUnderlayCornerColor2 + newUnderlayCornerColor3 >> 1;
            scalar2 = newOverlayCornerColor2 + newOverlayCornerColor3 >> 1;
         } else if (localShapeVertexCode == 7) {
            localVertexX = sourceLocalVertexX;
            localVertexZ = newTriangleVertexC + 128;
            localVertexY = sourceLocalVertexY2;
            scalar = newUnderlayCornerColor3;
            scalar2 = newOverlayCornerColor3;
         } else if (localShapeVertexCode == 8) {
            localVertexX = sourceLocalVertexX;
            localVertexZ = newTriangleVertexC + 64;
            localVertexY = sourceLocalVertexY2 + newFlat >> 1;
            scalar = newUnderlayCornerColor3 + newUnderlayCornerColor0 >> 1;
            scalar2 = newOverlayCornerColor3 + newOverlayCornerColor0 >> 1;
         } else if (localShapeVertexCode == 9) {
            localVertexX = sourceLocalVertexX + 64;
            localVertexZ = newTriangleVertexC + 32;
            localVertexY = newFlat + sourceLocalVertexY3 >> 1;
            scalar = newUnderlayCornerColor0 + newUnderlayCornerColor1 >> 1;
            scalar2 = newOverlayCornerColor0 + newOverlayCornerColor1 >> 1;
         } else if (localShapeVertexCode == 10) {
            localVertexX = sourceLocalVertexX + 96;
            localVertexZ = newTriangleVertexC + 64;
            localVertexY = sourceLocalVertexY3 + sourceLocalVertexY >> 1;
            scalar = newUnderlayCornerColor1 + newUnderlayCornerColor2 >> 1;
            scalar2 = newOverlayCornerColor1 + newOverlayCornerColor2 >> 1;
         } else if (localShapeVertexCode == 11) {
            localVertexX = sourceLocalVertexX + 64;
            localVertexZ = newTriangleVertexC + 96;
            localVertexY = sourceLocalVertexY + sourceLocalVertexY2 >> 1;
            scalar = newUnderlayCornerColor2 + newUnderlayCornerColor3 >> 1;
            scalar2 = newOverlayCornerColor2 + newOverlayCornerColor3 >> 1;
         } else if (localShapeVertexCode == 12) {
            localVertexX = sourceLocalVertexX + 32;
            localVertexZ = newTriangleVertexC + 64;
            localVertexY = sourceLocalVertexY2 + newFlat >> 1;
            scalar = newUnderlayCornerColor3 + newUnderlayCornerColor0 >> 1;
            scalar2 = newOverlayCornerColor3 + newOverlayCornerColor0 >> 1;
         } else if (localShapeVertexCode == 13) {
            localVertexX = sourceLocalVertexX + 32;
            localVertexZ = newTriangleVertexC + 32;
            localVertexY = newFlat;
            scalar = newUnderlayCornerColor0;
            scalar2 = newOverlayCornerColor0;
         } else if (localShapeVertexCode == 14) {
            localVertexX = sourceLocalVertexX + 96;
            localVertexZ = newTriangleVertexC + 32;
            localVertexY = sourceLocalVertexY3;
            scalar = newUnderlayCornerColor1;
            scalar2 = newOverlayCornerColor1;
         } else if (localShapeVertexCode == 15) {
            localVertexX = sourceLocalVertexX + 96;
            localVertexZ = newTriangleVertexC + 96;
            localVertexY = sourceLocalVertexY;
            scalar = newUnderlayCornerColor2;
            scalar2 = newOverlayCornerColor2;
         } else {
            localVertexX = sourceLocalVertexX + 32;
            localVertexZ = newTriangleVertexC + 96;
            localVertexY = sourceLocalVertexY2;
            scalar = newUnderlayCornerColor3;
            scalar2 = newOverlayCornerColor3;
         }

         this.vertexX[vertexXIndex] = localVertexX;
         this.vertexY[vertexXIndex] = localVertexY;
         this.vertexZ[vertexXIndex] = localVertexZ;
         localTriangleColorA[vertexXIndex] = scalar;
         triangleColorA2[vertexXIndex] = scalar2;
      }

      int[] localShapeTriangleData;
      int loopIndex = (localShapeTriangleData = shapeTriangleData[newShape]).length / 4;
      this.triangleVertexA = new int[loopIndex];
      this.triangleVertexB = new int[loopIndex];
      this.triangleVertexC = new int[loopIndex];
      this.triangleColorA = new int[loopIndex];
      this.triangleColorB = new int[loopIndex];
      this.triangleColorC = new int[loopIndex];
      if (triangleTextureId != -1) {
         this.triangleTextureIds = new int[loopIndex];
         this.hasTexture = true;
      }

      byte localShapeTriangleDataIndex = 0;

      for (int triangleVertexAIndex = 0; triangleVertexAIndex < loopIndex; triangleVertexAIndex++) {
         int localShapeTriangleDataEntry = localShapeTriangleData[localShapeTriangleDataIndex];
         int localTriangleVertexA = localShapeTriangleData[localShapeTriangleDataIndex + 1];
         int localTriangleVertexB = localShapeTriangleData[localShapeTriangleDataIndex + 2];
         newTriangleVertexC = localShapeTriangleData[localShapeTriangleDataIndex + 3];
         localShapeTriangleDataIndex += 4;
         if (localTriangleVertexA < 4) {
            localTriangleVertexA = localTriangleVertexA - newRotation & 3;
         }

         if (localTriangleVertexB < 4) {
            localTriangleVertexB = localTriangleVertexB - newRotation & 3;
         }

         if (newTriangleVertexC < 4) {
            newTriangleVertexC = newTriangleVertexC - newRotation & 3;
         }

         this.triangleVertexA[triangleVertexAIndex] = localTriangleVertexA;
         this.triangleVertexB[triangleVertexAIndex] = localTriangleVertexB;
         this.triangleVertexC[triangleVertexAIndex] = newTriangleVertexC;
         if (localShapeTriangleDataEntry == 0) {
            this.triangleColorA[triangleVertexAIndex] = localTriangleColorA[localTriangleVertexA];
            this.triangleColorB[triangleVertexAIndex] = localTriangleColorA[localTriangleVertexB];
            this.triangleColorC[triangleVertexAIndex] = localTriangleColorA[newTriangleVertexC];
            if (this.triangleTextureIds != null) {
               this.triangleTextureIds[triangleVertexAIndex] = -1;
            }
         } else {
            this.triangleColorA[triangleVertexAIndex] = triangleColorA2[localTriangleVertexA];
            this.triangleColorB[triangleVertexAIndex] = triangleColorA2[localTriangleVertexB];
            this.triangleColorC[triangleVertexAIndex] = triangleColorA2[newTriangleVertexC];
            if (this.triangleTextureIds != null) {
               this.triangleTextureIds[triangleVertexAIndex] = triangleTextureId;
            }
         }
      }
   }
}
