package client;
final class Rasterizer3D extends Rasterizer2D {
   public static boolean smoothShading = false;
   public static boolean lowMemory = true;
   static boolean restrictEdges;
   private static boolean textureOpaque;
   public static boolean renderModeFlag = true;
   public static int alpha;
   public static int viewportCenterX;
   public static int viewportCenterY;
   private static int[] reciprocal512 = new int[512];
   public static final int[] reciprocal2048 = new int[2048];
   public static int[] SINE = new int[2048];
   public static int[] COSINE = new int[2048];
   public static int[] scanOffsets;
   private static int textureCount;
   public static IndexedSprite[] textures = new IndexedSprite[51];
   private static boolean[] textureHasTransparency = new boolean[51];
   private static int[] averageTextureColors = new int[51];
   private static int texturePoolSize;
   private static int[][] texturePixelPool;
   private static int[][] texturePixels = new int[51][];
   public static int[] textureLastUsed = new int[51];
   public static int textureUsageCounter;
   public static int[] HSL_TO_RGB = new int[65536];
   private static int[][] texturePalettes = new int[51][];

   static {
      for (int divisor = 1; divisor < 512; divisor++) {
         reciprocal512[divisor] = 32768 / divisor;
      }

      for (int divisor = 1; divisor < 2048; divisor++) {
         reciprocal2048[divisor] = 65536 / divisor;
      }

      for (int angleIndex = 0; angleIndex < 2048; angleIndex++) {
         SINE[angleIndex] = (int)(65536.0 * Math.sin(angleIndex * 0.0030679615));
         COSINE[angleIndex] = (int)(65536.0 * Math.cos(angleIndex * 0.0030679615));
      }
   }
   private static int textureCoefficientNormalizationShift(
      long textureU,
      long textureUSlope,
      long textureUStep,
      long textureV,
      long textureVSlope,
      long textureVStep,
      long textureW,
      long textureWSlope,
      long textureWStep
   ) {
      // Horizontal texture projection is evaluated with long intermediates in
      // the scanline routines.  Only the coefficients that remain in the
      // legacy int triangle walker need viewport-span overflow protection.
      // Keeping the horizontal slopes out of the span calculation preserves
      // substantially more fixed-point precision in large viewports.
      long verticalSpan = Math.max(
         Math.abs((long)viewportCenterY),
         Math.abs((long)Rasterizer2D.bottomY - viewportCenterY)
      ) + 1L;

      long maxU = Math.abs(textureU) + Math.abs(textureUStep) * verticalSpan;
      long maxV = Math.abs(textureV) + Math.abs(textureVStep) * verticalSpan;
      long maxW = Math.abs(textureW) + Math.abs(textureWStep) * verticalSpan;
      long maxSlope = Math.max(
         Math.abs(textureUSlope),
         Math.max(Math.abs(textureVSlope), Math.abs(textureWSlope))
      );

      long max = Math.max(maxSlope, Math.max(maxU, Math.max(maxV, maxW)));
      long safeLimit = Integer.MAX_VALUE - (1L << 20);
      int shift = 0;
      while (max > safeLimit) {
         max = (max + 1L) >> 1;
         shift++;
      }

      return shift;
   }

   public static void drawDepthTriangle(int y0, int y1, int y2, int x0, int x1, int x2, float depth0, float depth1, float depth2) {
      if (GpuRasterizer3D.drawDepthTriangle(y0, y1, y2, x0, x1, x2, depth0, depth1, depth2)) {
         return;
      }

      int ySlope10 = 0;
      if (x1 != x0) {
         ySlope10 = (y1 - y0 << 16) / (x1 - x0);
      }

      int ySlope21 = 0;
      if (x2 != x1) {
         ySlope21 = (y2 - y1 << 16) / (x2 - x1);
      }

      int ySlope02 = 0;
      if (x2 != x0) {
         ySlope02 = (y0 - y2 << 16) / (x0 - x2);
      }

      float yDelta10 = y1 - y0;
      float xDelta10 = x1 - x0;
      float yDelta20 = y2 - y0;
      float xDelta20 = x2 - x0;
      float depthDelta10 = depth1 - depth0;
      float depthDelta20 = depth2 - depth0;
      float interpolant = yDelta10 * xDelta20 - yDelta20 * xDelta10;
      xDelta10 = (depthDelta10 * xDelta20 - depthDelta20 * xDelta10) / interpolant;
      yDelta10 = (depthDelta20 * yDelta10 - depthDelta10 * yDelta20) / interpolant;
      if (x0 <= x1 && x0 <= x2) {
         if (x0 < Rasterizer2D.bottomY) {
            if (x1 > Rasterizer2D.bottomY) {
               x1 = Rasterizer2D.bottomY;
            }

            if (x2 > Rasterizer2D.bottomY) {
               x2 = Rasterizer2D.bottomY;
            }

            depth0 = depth0 - xDelta10 * y0 + xDelta10;
            if (x1 < x2) {
               int scalar;
               y2 = scalar = y0 << 16;
               if (x0 < 0) {
                  y2 -= ySlope02 * x0;
                  scalar -= ySlope10 * x0;
                  depth0 -= yDelta10 * x0;
                  x0 = 0;
               }

               y1 <<= 16;
               if (x1 < 0) {
                  y1 -= ySlope21 * x1;
                  x1 = 0;
               }

               if ((x0 == x1 || ySlope02 >= ySlope10) && (x0 != x1 || ySlope02 <= ySlope21)) {
                  x2 -= x1;
                  x1 -= x0;

                  for (x0 = scanOffsets[x0]; --x1 >= 0; x0 += Rasterizer2D.width) {
                     drawDepthScanline(x0, scalar >> 16, y2 >> 16, depth0, xDelta10);
                     y2 += ySlope02;
                     scalar += ySlope10;
                     depth0 += yDelta10;
                  }

                  while (--x2 >= 0) {
                     drawDepthScanline(x0, y1 >> 16, y2 >> 16, depth0, xDelta10);
                     y2 += ySlope02;
                     y1 += ySlope21;
                     depth0 += yDelta10;
                     x0 += Rasterizer2D.width;
                  }

                  return;
               }

               x2 -= x1;
               x1 -= x0;

               for (x0 = scanOffsets[x0]; --x1 >= 0; x0 += Rasterizer2D.width) {
                  drawDepthScanline(x0, y2 >> 16, scalar >> 16, depth0, xDelta10);
                  y2 += ySlope02;
                  scalar += ySlope10;
                  depth0 += yDelta10;
               }

               while (--x2 >= 0) {
                  drawDepthScanline(x0, y2 >> 16, y1 >> 16, depth0, xDelta10);
                  y2 += ySlope02;
                  y1 += ySlope21;
                  depth0 += yDelta10;
                  x0 += Rasterizer2D.width;
               }

               return;
            }

            int scalar2;
            y1 = scalar2 = y0 << 16;
            if (x0 < 0) {
               y1 -= ySlope02 * x0;
               scalar2 -= ySlope10 * x0;
               depth0 -= yDelta10 * x0;
               x0 = 0;
            }

            y2 <<= 16;
            if (x2 < 0) {
               y2 -= ySlope21 * x2;
               x2 = 0;
            }

            if ((x0 == x2 || ySlope02 >= ySlope10) && (x0 != x2 || ySlope21 <= ySlope10)) {
               x1 -= x2;
               x2 -= x0;

               for (x0 = scanOffsets[x0]; --x2 >= 0; x0 += Rasterizer2D.width) {
                  drawDepthScanline(x0, scalar2 >> 16, y1 >> 16, depth0, xDelta10);
                  y1 += ySlope02;
                  scalar2 += ySlope10;
                  depth0 += yDelta10;
               }

               while (--x1 >= 0) {
                  drawDepthScanline(x0, scalar2 >> 16, y2 >> 16, depth0, xDelta10);
                  y2 += ySlope21;
                  scalar2 += ySlope10;
                  depth0 += yDelta10;
                  x0 += Rasterizer2D.width;
               }

               return;
            }

            x1 -= x2;
            x2 -= x0;

            for (x0 = scanOffsets[x0]; --x2 >= 0; x0 += Rasterizer2D.width) {
               drawDepthScanline(x0, y1 >> 16, scalar2 >> 16, depth0, xDelta10);
               y1 += ySlope02;
               scalar2 += ySlope10;
               depth0 += yDelta10;
            }

            while (--x1 >= 0) {
               drawDepthScanline(x0, y2 >> 16, scalar2 >> 16, depth0, xDelta10);
               y2 += ySlope21;
               scalar2 += ySlope10;
               depth0 += yDelta10;
               x0 += Rasterizer2D.width;
            }

            return;
         }
      } else if (x1 <= x2) {
         if (x1 < Rasterizer2D.bottomY) {
            if (x2 > Rasterizer2D.bottomY) {
               x2 = Rasterizer2D.bottomY;
            }

            if (x0 > Rasterizer2D.bottomY) {
               x0 = Rasterizer2D.bottomY;
            }

            depth1 = depth1 - xDelta10 * y1 + xDelta10;
            if (x2 < x0) {
               int scalar3;
               y0 = scalar3 = y1 << 16;
               if (x1 < 0) {
                  y0 -= ySlope10 * x1;
                  scalar3 -= ySlope21 * x1;
                  depth1 -= yDelta10 * x1;
                  x1 = 0;
               }

               y2 <<= 16;
               if (x2 < 0) {
                  y2 -= ySlope02 * x2;
                  x2 = 0;
               }

               if ((x1 == x2 || ySlope10 >= ySlope21) && (x1 != x2 || ySlope10 <= ySlope02)) {
                  x0 -= x2;
                  x2 -= x1;

                  for (x1 = scanOffsets[x1]; --x2 >= 0; x1 += Rasterizer2D.width) {
                     drawDepthScanline(x1, scalar3 >> 16, y0 >> 16, depth1, xDelta10);
                     y0 += ySlope10;
                     scalar3 += ySlope21;
                     depth1 += yDelta10;
                  }

                  while (--x0 >= 0) {
                     drawDepthScanline(x1, y2 >> 16, y0 >> 16, depth1, xDelta10);
                     y0 += ySlope10;
                     y2 += ySlope02;
                     depth1 += yDelta10;
                     x1 += Rasterizer2D.width;
                  }

                  return;
               }

               x0 -= x2;
               x2 -= x1;

               for (x1 = scanOffsets[x1]; --x2 >= 0; x1 += Rasterizer2D.width) {
                  drawDepthScanline(x1, y0 >> 16, scalar3 >> 16, depth1, xDelta10);
                  y0 += ySlope10;
                  scalar3 += ySlope21;
                  depth1 += yDelta10;
               }

               while (--x0 >= 0) {
                  drawDepthScanline(x1, y0 >> 16, y2 >> 16, depth1, xDelta10);
                  y0 += ySlope10;
                  y2 += ySlope02;
                  depth1 += yDelta10;
                  x1 += Rasterizer2D.width;
               }

               return;
            }

            int scalar4;
            y2 = scalar4 = y1 << 16;
            if (x1 < 0) {
               y2 -= ySlope10 * x1;
               scalar4 -= ySlope21 * x1;
               depth1 -= yDelta10 * x1;
               x1 = 0;
            }

            y0 <<= 16;
            if (x0 < 0) {
               y0 -= ySlope02 * x0;
               x0 = 0;
            }

            if (ySlope10 < ySlope21) {
               x2 -= x0;
               x0 -= x1;

               for (x1 = scanOffsets[x1]; --x0 >= 0; x1 += Rasterizer2D.width) {
                  drawDepthScanline(x1, y2 >> 16, scalar4 >> 16, depth1, xDelta10);
                  y2 += ySlope10;
                  scalar4 += ySlope21;
                  depth1 += yDelta10;
               }

               while (--x2 >= 0) {
                  drawDepthScanline(x1, y0 >> 16, scalar4 >> 16, depth1, xDelta10);
                  y0 += ySlope02;
                  scalar4 += ySlope21;
                  depth1 += yDelta10;
                  x1 += Rasterizer2D.width;
               }

               return;
            }

            x2 -= x0;
            x0 -= x1;

            for (x1 = scanOffsets[x1]; --x0 >= 0; x1 += Rasterizer2D.width) {
               drawDepthScanline(x1, scalar4 >> 16, y2 >> 16, depth1, xDelta10);
               y2 += ySlope10;
               scalar4 += ySlope21;
               depth1 += yDelta10;
            }

            while (--x2 >= 0) {
               drawDepthScanline(x1, scalar4 >> 16, y0 >> 16, depth1, xDelta10);
               y0 += ySlope02;
               scalar4 += ySlope21;
               depth1 += yDelta10;
               x1 += Rasterizer2D.width;
            }

            return;
         }
      } else if (x2 < Rasterizer2D.bottomY) {
         if (x0 > Rasterizer2D.bottomY) {
            x0 = Rasterizer2D.bottomY;
         }

         if (x1 > Rasterizer2D.bottomY) {
            x1 = Rasterizer2D.bottomY;
         }

         depth2 = depth2 - xDelta10 * y2 + xDelta10;
         if (x0 < x1) {
            int scalar5;
            y1 = scalar5 = y2 << 16;
            if (x2 < 0) {
               y1 -= ySlope21 * x2;
               scalar5 -= ySlope02 * x2;
               depth2 -= yDelta10 * x2;
               x2 = 0;
            }

            y0 <<= 16;
            if (x0 < 0) {
               y0 -= ySlope10 * x0;
               x0 = 0;
            }

            if (ySlope21 < ySlope02) {
               x1 -= x0;
               x0 -= x2;

               for (x2 = scanOffsets[x2]; --x0 >= 0; x2 += Rasterizer2D.width) {
                  drawDepthScanline(x2, y1 >> 16, scalar5 >> 16, depth2, xDelta10);
                  y1 += ySlope21;
                  scalar5 += ySlope02;
                  depth2 += yDelta10;
               }

               while (--x1 >= 0) {
                  drawDepthScanline(x2, y1 >> 16, y0 >> 16, depth2, xDelta10);
                  y1 += ySlope21;
                  y0 += ySlope10;
                  depth2 += yDelta10;
                  x2 += Rasterizer2D.width;
               }

               return;
            }

            x1 -= x0;
            x0 -= x2;

            for (x2 = scanOffsets[x2]; --x0 >= 0; x2 += Rasterizer2D.width) {
               drawDepthScanline(x2, scalar5 >> 16, y1 >> 16, depth2, xDelta10);
               y1 += ySlope21;
               scalar5 += ySlope02;
               depth2 += yDelta10;
            }

            while (--x1 >= 0) {
               drawDepthScanline(x2, y0 >> 16, y1 >> 16, depth2, xDelta10);
               y1 += ySlope21;
               y0 += ySlope10;
               depth2 += yDelta10;
               x2 += Rasterizer2D.width;
            }

            return;
         }

         int scalar6;
         y0 = scalar6 = y2 << 16;
         if (x2 < 0) {
            y0 -= ySlope21 * x2;
            scalar6 -= ySlope02 * x2;
            depth2 -= yDelta10 * x2;
            x2 = 0;
         }

         y1 <<= 16;
         if (x1 < 0) {
            y1 -= ySlope10 * x1;
            x1 = 0;
         }

         if (ySlope21 < ySlope02) {
            x0 -= x1;
            x1 -= x2;

            for (x2 = scanOffsets[x2]; --x1 >= 0; x2 += Rasterizer2D.width) {
               drawDepthScanline(x2, y0 >> 16, scalar6 >> 16, depth2, xDelta10);
               y0 += ySlope21;
               scalar6 += ySlope02;
               depth2 += yDelta10;
            }

            while (--x0 >= 0) {
               drawDepthScanline(x2, y1 >> 16, scalar6 >> 16, depth2, xDelta10);
               y1 += ySlope10;
               scalar6 += ySlope02;
               depth2 += yDelta10;
               x2 += Rasterizer2D.width;
            }

            return;
         }

         x0 -= x1;
         x1 -= x2;

         for (x2 = scanOffsets[x2]; --x1 >= 0; x2 += Rasterizer2D.width) {
            drawDepthScanline(x2, scalar6 >> 16, y0 >> 16, depth2, xDelta10);
            y0 += ySlope21;
            scalar6 += ySlope02;
            depth2 += yDelta10;
         }

         while (--x0 >= 0) {
            drawDepthScanline(x2, scalar6 >> 16, y1 >> 16, depth2, xDelta10);
            y1 += ySlope10;
            scalar6 += ySlope02;
            depth2 += yDelta10;
            x2 += Rasterizer2D.width;
         }
      }
   }
   private static void drawDepthScanline(int pixelOffset, int xStart, int xEnd, float depth, float depthSlope) {
      int depthBufferLengthOrDepthBuffer = Rasterizer2D.depthBuffer.length;
      if (restrictEdges) {
         if (xEnd > Rasterizer2D.width) {
            xEnd = Rasterizer2D.width;
         }

         if (xStart < 0) {
            xStart = 0;
         }
      }

      if (xStart < xEnd) {
         pixelOffset += xStart - 1;
         int scalar = xEnd - xStart >> 2;
         depth += depthSlope * xStart;
         if (alpha == 0) {
            while (--scalar >= 0) {
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
            }

            for (int position = xEnd - xStart & 3; --position >= 0; depth += depthSlope) {
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }
            }
         } else {
            while (--scalar >= 0) {
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }

               depth += depthSlope;
            }

            for (int position2 = xEnd - xStart & 3; --position2 >= 0; depth += depthSlope) {
               pixelOffset++;
               if (pixelOffset >= 0 && pixelOffset < depthBufferLengthOrDepthBuffer) {
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
               }
            }
         }
      }
   }
   private static void drawSmoothShadedTriangle(
      int y0, int y1, int y2, int x0, int x1, int x2, int color0, int color1, int color2, float depth0, float depth1, float depth2
   ) {
      if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
         color0 = HSL_TO_RGB[color0];
         color1 = HSL_TO_RGB[color1];
         color2 = HSL_TO_RGB[color2];
         int red0 = color0 >> 16 & 0xFF;
         int green0 = color0 >> 8 & 0xFF;
         color0 &= 255;
         int red1 = color1 >> 16 & 0xFF;
         int green1 = color1 >> 8 & 0xFF;
         color1 &= 255;
         int red2 = color2 >> 16 & 0xFF;
         int green2 = color2 >> 8 & 0xFF;
         color2 &= 255;
         int xSlope10 = 0;
         int redSlope10 = 0;
         int greenSlope10 = 0;
         int colorSlope10 = 0;
         if (y1 != y0) {
            xSlope10 = (x1 - x0 << 16) / (y1 - y0);
            redSlope10 = (red1 - red0 << 16) / (y1 - y0);
            greenSlope10 = (green1 - green0 << 16) / (y1 - y0);
            colorSlope10 = (color1 - color0 << 16) / (y1 - y0);
         }

         int xSlope21 = 0;
         int redSlope21 = 0;
         int greenSlope21 = 0;
         int colorSlope21 = 0;
         if (y2 != y1) {
            xSlope21 = (x2 - x1 << 16) / (y2 - y1);
            redSlope21 = (red2 - red1 << 16) / (y2 - y1);
            greenSlope21 = (green2 - green1 << 16) / (y2 - y1);
            colorSlope21 = (color2 - color1 << 16) / (y2 - y1);
         }

         int xSlope02 = 0;
         int redSlope02 = 0;
         int greenSlope02 = 0;
         int colorSlope02 = 0;
         if (y2 != y0) {
            xSlope02 = (x0 - x2 << 16) / (y0 - y2);
            redSlope02 = (red0 - red2 << 16) / (y0 - y2);
            greenSlope02 = (green0 - green2 << 16) / (y0 - y2);
            colorSlope02 = (color0 - color2 << 16) / (y0 - y2);
         }

         float xDelta10 = x1 - x0;
         float yDelta10 = y1 - y0;
         float xDelta20 = x2 - x0;
         float yDelta20 = y2 - y0;
         float depthDelta10 = depth1 - depth0;
         float depthDelta20 = depth2 - depth0;
         float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
         yDelta10 = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
         xDelta10 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
         if (y0 <= y1 && y0 <= y2) {
            if (y0 < Rasterizer2D.bottomY) {
               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta10 * x0 + yDelta10;
               if (y1 < y2) {
                  int scalar;
                  x2 = scalar = x0 << 16;
                  int scalar2;
                  red2 = scalar2 = red0 << 16;
                  int scalar3;
                  green2 = scalar3 = green0 << 16;
                  int scalar4;
                  color2 = scalar4 = color0 << 16;
                  if (y0 < 0) {
                     x2 -= xSlope02 * y0;
                     scalar -= xSlope10 * y0;
                     red2 = scalar2 - redSlope02 * y0;
                     green2 = scalar3 - greenSlope02 * y0;
                     color2 = scalar4 - colorSlope02 * y0;
                     scalar2 -= redSlope10 * y0;
                     scalar3 -= greenSlope10 * y0;
                     scalar4 -= colorSlope10 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  red1 <<= 16;
                  green1 <<= 16;
                  color1 <<= 16;
                  if (y1 < 0) {
                     x1 -= xSlope21 * y1;
                     red1 -= redSlope21 * y1;
                     green1 -= greenSlope21 * y1;
                     color1 -= colorSlope21 * y1;
                     y1 = 0;
                  }

                  if ((y0 == y1 || xSlope02 >= xSlope10) && (y0 != y1 || xSlope02 <= xSlope21)) {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, scalar >> 16, x2 >> 16, scalar2, scalar3, scalar4, red2, green2, color2, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar += xSlope10;
                        red2 += redSlope02;
                        green2 += greenSlope02;
                        color2 += colorSlope02;
                        scalar2 += redSlope10;
                        scalar3 += greenSlope10;
                        scalar4 += colorSlope10;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, x1 >> 16, x2 >> 16, red1, green1, color1, red2, green2, color2, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        red2 += redSlope02;
                        green2 += greenSlope02;
                        color2 += colorSlope02;
                        red1 += redSlope21;
                        green1 += greenSlope21;
                        color1 += colorSlope21;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  } else {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, x2 >> 16, scalar >> 16, red2, green2, color2, scalar2, scalar3, scalar4, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar += xSlope10;
                        red2 += redSlope02;
                        green2 += greenSlope02;
                        color2 += colorSlope02;
                        scalar2 += redSlope10;
                        scalar3 += greenSlope10;
                        scalar4 += colorSlope10;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, x2 >> 16, x1 >> 16, red2, green2, color2, red1, green1, color1, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        red2 += redSlope02;
                        green2 += greenSlope02;
                        color2 += colorSlope02;
                        red1 += redSlope21;
                        green1 += greenSlope21;
                        color1 += colorSlope21;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  }
               } else {
                  int scalar5;
                  x1 = scalar5 = x0 << 16;
                  int scalar6;
                  red1 = scalar6 = red0 << 16;
                  int scalar7;
                  green1 = scalar7 = green0 << 16;
                  int scalar8;
                  color1 = scalar8 = color0 << 16;
                  if (y0 < 0) {
                     x1 -= xSlope02 * y0;
                     scalar5 -= xSlope10 * y0;
                     red1 = scalar6 - redSlope02 * y0;
                     green1 = scalar7 - greenSlope02 * y0;
                     color1 = scalar8 - colorSlope02 * y0;
                     scalar6 -= redSlope10 * y0;
                     scalar7 -= greenSlope10 * y0;
                     scalar8 -= colorSlope10 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x2 <<= 16;
                  red2 <<= 16;
                  green2 <<= 16;
                  color2 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope21 * y2;
                     red2 -= redSlope21 * y2;
                     green2 -= greenSlope21 * y2;
                     color2 -= colorSlope21 * y2;
                     y2 = 0;
                  }

                  if ((y0 == y2 || xSlope02 >= xSlope10) && (y0 != y2 || xSlope21 <= xSlope10)) {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, scalar5 >> 16, x1 >> 16, scalar6, scalar7, scalar8, red1, green1, color1, depth0, yDelta10);
                        x1 += xSlope02;
                        scalar5 += xSlope10;
                        red1 += redSlope02;
                        green1 += greenSlope02;
                        color1 += colorSlope02;
                        scalar6 += redSlope10;
                        scalar7 += greenSlope10;
                        scalar8 += colorSlope10;
                        depth0 += xDelta10;
                     }

                     while (--y1 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, scalar5 >> 16, x2 >> 16, scalar6, scalar7, scalar8, red2, green2, color2, depth0, yDelta10);
                        x2 += xSlope21;
                        scalar5 += xSlope10;
                        red2 += redSlope21;
                        green2 += greenSlope21;
                        color2 += colorSlope21;
                        scalar6 += redSlope10;
                        scalar7 += greenSlope10;
                        scalar8 += colorSlope10;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  } else {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, x1 >> 16, scalar5 >> 16, red1, green1, color1, scalar6, scalar7, scalar8, depth0, yDelta10);
                        x1 += xSlope02;
                        scalar5 += xSlope10;
                        red1 += redSlope02;
                        green1 += greenSlope02;
                        color1 += colorSlope02;
                        scalar6 += redSlope10;
                        scalar7 += greenSlope10;
                        scalar8 += colorSlope10;
                        depth0 += xDelta10;
                     }

                     while (--y1 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y0, x2 >> 16, scalar5 >> 16, red2, green2, color2, scalar6, scalar7, scalar8, depth0, yDelta10);
                        x2 += xSlope21;
                        scalar5 += xSlope10;
                        red2 += redSlope21;
                        green2 += greenSlope21;
                        color2 += colorSlope21;
                        scalar6 += redSlope10;
                        scalar7 += greenSlope10;
                        scalar8 += colorSlope10;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  }
               }
            }
         } else if (y1 <= y2) {
            if (y1 < Rasterizer2D.bottomY) {
               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta10 * x1 + yDelta10;
               if (y2 < y0) {
                  int scalar9;
                  x0 = scalar9 = x1 << 16;
                  int scalar10;
                  red0 = scalar10 = red1 << 16;
                  int scalar11;
                  green0 = scalar11 = green1 << 16;
                  int scalar12;
                  color0 = scalar12 = color1 << 16;
                  if (y1 < 0) {
                     x0 -= xSlope10 * y1;
                     scalar9 -= xSlope21 * y1;
                     red0 -= redSlope10 * y1;
                     green0 -= greenSlope10 * y1;
                     color0 -= colorSlope10 * y1;
                     scalar10 -= redSlope21 * y1;
                     scalar11 -= greenSlope21 * y1;
                     scalar12 -= colorSlope21 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  red2 <<= 16;
                  green2 <<= 16;
                  color2 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope02 * y2;
                     red2 -= redSlope02 * y2;
                     green2 -= greenSlope02 * y2;
                     color2 -= colorSlope02 * y2;
                     y2 = 0;
                  }

                  if ((y1 == y2 || xSlope10 >= xSlope21) && (y1 != y2 || xSlope10 <= xSlope02)) {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, scalar9 >> 16, x0 >> 16, scalar10, scalar11, scalar12, red0, green0, color0, depth1, yDelta10);
                        x0 += xSlope10;
                        scalar9 += xSlope21;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        scalar10 += redSlope21;
                        scalar11 += greenSlope21;
                        scalar12 += colorSlope21;
                        depth1 += xDelta10;
                     }

                     while (--y0 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, x2 >> 16, x0 >> 16, red2, green2, color2, red0, green0, color0, depth1, yDelta10);
                        x0 += xSlope10;
                        x2 += xSlope02;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        red2 += redSlope02;
                        green2 += greenSlope02;
                        color2 += colorSlope02;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  } else {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, x0 >> 16, scalar9 >> 16, red0, green0, color0, scalar10, scalar11, scalar12, depth1, yDelta10);
                        x0 += xSlope10;
                        scalar9 += xSlope21;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        scalar10 += redSlope21;
                        scalar11 += greenSlope21;
                        scalar12 += colorSlope21;
                        depth1 += xDelta10;
                     }

                     while (--y0 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, x0 >> 16, x2 >> 16, red0, green0, color0, red2, green2, color2, depth1, yDelta10);
                        x0 += xSlope10;
                        x2 += xSlope02;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        red2 += redSlope02;
                        green2 += greenSlope02;
                        color2 += colorSlope02;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  }
               } else {
                  int scalar13;
                  x2 = scalar13 = x1 << 16;
                  int scalar14;
                  red2 = scalar14 = red1 << 16;
                  int scalar15;
                  green2 = scalar15 = green1 << 16;
                  int scalar16;
                  color2 = scalar16 = color1 << 16;
                  if (y1 < 0) {
                     x2 -= xSlope10 * y1;
                     scalar13 -= xSlope21 * y1;
                     red2 = scalar14 - redSlope10 * y1;
                     green2 = scalar15 - greenSlope10 * y1;
                     color2 = scalar16 - colorSlope10 * y1;
                     scalar14 -= redSlope21 * y1;
                     scalar15 -= greenSlope21 * y1;
                     scalar16 -= colorSlope21 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x0 <<= 16;
                  red0 <<= 16;
                  green0 <<= 16;
                  color0 <<= 16;
                  if (y0 < 0) {
                     x0 -= xSlope02 * y0;
                     red0 -= redSlope02 * y0;
                     green0 -= greenSlope02 * y0;
                     color0 -= colorSlope02 * y0;
                     y0 = 0;
                  }

                  if (xSlope10 < xSlope21) {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, x2 >> 16, scalar13 >> 16, red2, green2, color2, scalar14, scalar15, scalar16, depth1, yDelta10);
                        x2 += xSlope10;
                        scalar13 += xSlope21;
                        red2 += redSlope10;
                        green2 += greenSlope10;
                        color2 += colorSlope10;
                        scalar14 += redSlope21;
                        scalar15 += greenSlope21;
                        scalar16 += colorSlope21;
                        depth1 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, x0 >> 16, scalar13 >> 16, red0, green0, color0, scalar14, scalar15, scalar16, depth1, yDelta10);
                        x0 += xSlope02;
                        scalar13 += xSlope21;
                        red0 += redSlope02;
                        green0 += greenSlope02;
                        color0 += colorSlope02;
                        scalar14 += redSlope21;
                        scalar15 += greenSlope21;
                        scalar16 += colorSlope21;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  } else {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, scalar13 >> 16, x2 >> 16, scalar14, scalar15, scalar16, red2, green2, color2, depth1, yDelta10);
                        x2 += xSlope10;
                        scalar13 += xSlope21;
                        red2 += redSlope10;
                        green2 += greenSlope10;
                        color2 += colorSlope10;
                        scalar14 += redSlope21;
                        scalar15 += greenSlope21;
                        scalar16 += colorSlope21;
                        depth1 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawSmoothShadedScanline(Rasterizer2D.pixels, y1, scalar13 >> 16, x0 >> 16, scalar14, scalar15, scalar16, red0, green0, color0, depth1, yDelta10);
                        x0 += xSlope02;
                        scalar13 += xSlope21;
                        red0 += redSlope02;
                        green0 += greenSlope02;
                        color0 += colorSlope02;
                        scalar14 += redSlope21;
                        scalar15 += greenSlope21;
                        scalar16 += colorSlope21;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  }
               }
            }
         } else if (y2 < Rasterizer2D.bottomY) {
            if (y0 > Rasterizer2D.bottomY) {
               y0 = Rasterizer2D.bottomY;
            }

            if (y1 > Rasterizer2D.bottomY) {
               y1 = Rasterizer2D.bottomY;
            }

            depth2 = depth2 - yDelta10 * x2 + yDelta10;
            if (y0 < y1) {
               int scalar17;
               x1 = scalar17 = x2 << 16;
               int scalar18;
               red1 = scalar18 = red2 << 16;
               int scalar19;
               green1 = scalar19 = green2 << 16;
               int scalar20;
               color1 = scalar20 = color2 << 16;
               if (y2 < 0) {
                  x1 -= xSlope21 * y2;
                  scalar17 -= xSlope02 * y2;
                  red1 -= redSlope21 * y2;
                  green1 -= greenSlope21 * y2;
                  color1 -= colorSlope21 * y2;
                  scalar18 -= redSlope02 * y2;
                  scalar19 -= greenSlope02 * y2;
                  scalar20 -= colorSlope02 * y2;
                  depth2 -= xDelta10 * y2;
                  y2 = 0;
               }

               x0 <<= 16;
               red0 <<= 16;
               green0 <<= 16;
               color0 <<= 16;
               if (y0 < 0) {
                  x0 -= xSlope10 * y0;
                  red0 -= redSlope10 * y0;
                  green0 -= greenSlope10 * y0;
                  color0 -= colorSlope10 * y0;
                  y0 = 0;
               }

               if (xSlope21 < xSlope02) {
                  y1 -= y0;
                  y0 -= y2;

                  for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, scalar17 >> 16, red1, green1, color1, scalar18, scalar19, scalar20, depth2, yDelta10);
                     x1 += xSlope21;
                     scalar17 += xSlope02;
                     red1 += redSlope21;
                     green1 += greenSlope21;
                     color1 += colorSlope21;
                     scalar18 += redSlope02;
                     scalar19 += greenSlope02;
                     scalar20 += colorSlope02;
                     depth2 += xDelta10;
                  }

                  while (--y1 >= 0) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, x0 >> 16, red1, green1, color1, red0, green0, color0, depth2, yDelta10);
                     x1 += xSlope21;
                     x0 += xSlope10;
                     red1 += redSlope21;
                     green1 += greenSlope21;
                     color1 += colorSlope21;
                     red0 += redSlope10;
                     green0 += greenSlope10;
                     color0 += colorSlope10;
                     y2 += Rasterizer2D.width;
                     depth2 += xDelta10;
                  }
               } else {
                  y1 -= y0;
                  y0 -= y2;

                  for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, scalar17 >> 16, x1 >> 16, scalar18, scalar19, scalar20, red1, green1, color1, depth2, yDelta10);
                     x1 += xSlope21;
                     scalar17 += xSlope02;
                     red1 += redSlope21;
                     green1 += greenSlope21;
                     color1 += colorSlope21;
                     scalar18 += redSlope02;
                     scalar19 += greenSlope02;
                     scalar20 += colorSlope02;
                     depth2 += xDelta10;
                  }

                  while (--y1 >= 0) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, x0 >> 16, x1 >> 16, red0, green0, color0, red1, green1, color1, depth2, yDelta10);
                     x1 += xSlope21;
                     x0 += xSlope10;
                     red1 += redSlope21;
                     green1 += greenSlope21;
                     color1 += colorSlope21;
                     red0 += redSlope10;
                     green0 += greenSlope10;
                     color0 += colorSlope10;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                  }
               }
            } else {
               int scalar21;
               x0 = scalar21 = x2 << 16;
               int scalar22;
               red0 = scalar22 = red2 << 16;
               int scalar23;
               green0 = scalar23 = green2 << 16;
               int scalar24;
               color0 = scalar24 = color2 << 16;
               if (y2 < 0) {
                  x0 -= xSlope21 * y2;
                  scalar21 -= xSlope02 * y2;
                  red0 -= redSlope21 * y2;
                  green0 -= greenSlope21 * y2;
                  color0 -= colorSlope21 * y2;
                  scalar22 -= redSlope02 * y2;
                  scalar23 -= greenSlope02 * y2;
                  scalar24 -= colorSlope02 * y2;
                  depth2 -= xDelta10 * y2;
                  y2 = 0;
               }

               x1 <<= 16;
               red1 <<= 16;
               green1 <<= 16;
               color1 <<= 16;
               if (y1 < 0) {
                  x1 -= xSlope10 * y1;
                  red1 -= redSlope10 * y1;
                  green1 -= greenSlope10 * y1;
                  color1 -= colorSlope10 * y1;
                  y1 = 0;
               }

               if (xSlope21 < xSlope02) {
                  y0 -= y1;
                  y1 -= y2;

                  for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, x0 >> 16, scalar21 >> 16, red0, green0, color0, scalar22, scalar23, scalar24, depth2, yDelta10);
                     x0 += xSlope21;
                     scalar21 += xSlope02;
                     red0 += redSlope21;
                     green0 += greenSlope21;
                     color0 += colorSlope21;
                     scalar22 += redSlope02;
                     scalar23 += greenSlope02;
                     scalar24 += colorSlope02;
                     depth2 += xDelta10;
                  }

                  while (--y0 >= 0) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, scalar21 >> 16, red1, green1, color1, scalar22, scalar23, scalar24, depth2, yDelta10);
                     x1 += xSlope10;
                     scalar21 += xSlope02;
                     red1 += redSlope10;
                     green1 += greenSlope10;
                     color1 += colorSlope10;
                     scalar22 += redSlope02;
                     scalar23 += greenSlope02;
                     scalar24 += colorSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                  }
               } else {
                  y0 -= y1;
                  y1 -= y2;

                  for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, scalar21 >> 16, x0 >> 16, scalar22, scalar23, scalar24, red0, green0, color0, depth2, yDelta10);
                     x0 += xSlope21;
                     scalar21 += xSlope02;
                     red0 += redSlope21;
                     green0 += greenSlope21;
                     color0 += colorSlope21;
                     scalar22 += redSlope02;
                     scalar23 += greenSlope02;
                     scalar24 += colorSlope02;
                     depth2 += xDelta10;
                  }

                  while (--y0 >= 0) {
                     drawSmoothShadedScanline(Rasterizer2D.pixels, y2, scalar21 >> 16, x1 >> 16, scalar22, scalar23, scalar24, red1, green1, color1, depth2, yDelta10);
                     x1 += xSlope10;
                     scalar21 += xSlope02;
                     red1 += redSlope10;
                     green1 += greenSlope10;
                     color1 += colorSlope10;
                     scalar22 += redSlope02;
                     scalar23 += greenSlope02;
                     scalar24 += colorSlope02;
                     y2 += Rasterizer2D.width;
                     depth2 += xDelta10;
                  }
               }
            }
         }
      }
   }
   private static void drawSmoothShadedScanline(
      int[] pixels, int pixelOffset, int xStart, int xEnd, int red, int green, int blue, int redSlope, int greenSlope, int blueSlope, float depth, float depthSlope
   ) {
      int sourceXEnd;
      if ((sourceXEnd = xEnd - xStart) > 0) {
         redSlope = (redSlope - red) / sourceXEnd;
         greenSlope = (greenSlope - green) / sourceXEnd;
         blueSlope = (blueSlope - blue) / sourceXEnd;
         if (restrictEdges) {
            if (xEnd > Rasterizer2D.centerX) {
               sourceXEnd -= xEnd - Rasterizer2D.centerX;
               xEnd = Rasterizer2D.centerX;
            }

            if (xStart < 0) {
               sourceXEnd = xEnd;
               red -= xStart * redSlope;
               green -= xStart * greenSlope;
               blue -= xStart * blueSlope;
               xStart = 0;
            }
         }

         if (xStart < xEnd) {
            pixelOffset += xStart;
            depth += depthSlope * xStart;
            if (alpha == 0) {
               while (--sourceXEnd >= 0) {
                  pixels[pixelOffset] = red & 0xFF0000 | green >> 8 & 0xFF00 | blue >> 16 & 0xFF;
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
                  depth += depthSlope;
                  red += redSlope;
                  green += greenSlope;
                  blue += blueSlope;
                  pixelOffset++;
               }

               return;
            }

            xStart = alpha;
            xEnd = 256 - alpha;

            while (--sourceXEnd >= 0) {
               int scalar;
               scalar = (((scalar = red & 0xFF0000 | green >> 8 & 0xFF00 | blue >> 16 & 0xFF) & 16711935) * xEnd >> 8 & 16711935)
                  + ((scalar & 0xFF00) * xEnd >> 8 & 0xFF00);
               int pixel = pixels[pixelOffset];
               pixels[pixelOffset] = scalar + ((pixel & 16711935) * xStart >> 8 & 16711935) + ((pixel & 0xFF00) * xStart >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               red += redSlope;
               green += greenSlope;
               blue += blueSlope;
               pixelOffset++;
            }
         }
      }
   }
   private static void drawRgbShadedScanline(
      int[] pixels, int pixelOffset, int xStart, int xEnd, int red, int green, int blue, int redSlope, int greenSlope, int blueSlope, float depth, float depthSlope
   ) {
      int sourceXEnd;
      if ((sourceXEnd = xEnd - xStart) > 0) {
         redSlope = (redSlope - red) / sourceXEnd;
         greenSlope = (greenSlope - green) / sourceXEnd;
         blueSlope = (blueSlope - blue) / sourceXEnd;
         if (restrictEdges) {
            if (xEnd > Rasterizer2D.centerX) {
               sourceXEnd -= xEnd - Rasterizer2D.centerX;
               xEnd = Rasterizer2D.centerX;
            }

            if (xStart < 0) {
               sourceXEnd = xEnd;
               red -= xStart * redSlope;
               green -= xStart * greenSlope;
               blue -= xStart * blueSlope;
               xStart = 0;
            }
         }

         if (xStart < xEnd) {
            pixelOffset += xStart;
            depth += depthSlope * xStart;
            if (alpha == 0) {
               while (--sourceXEnd >= 0) {
                  pixels[pixelOffset] = red & 0xFF0000 | green >> 8 & 0xFF00 | blue >> 16 & 0xFF;
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
                  depth += depthSlope;
                  red += redSlope;
                  green += greenSlope;
                  blue += blueSlope;
                  pixelOffset++;
               }

               return;
            }

            xStart = alpha;
            xEnd = 256 - alpha;

            while (--sourceXEnd >= 0) {
               int scalar;
               scalar = (((scalar = red & 0xFF0000 | green >> 8 & 0xFF00 | blue >> 16 & 0xFF) & 16711935) * xEnd >> 8 & 16711935)
                  + ((scalar & 0xFF00) * xEnd >> 8 & 0xFF00);
               int pixel = pixels[pixelOffset];
               pixels[pixelOffset] = scalar + ((pixel & 16711935) * xStart >> 8 & 16711935) + ((pixel & 0xFF00) * xStart >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               red += redSlope;
               green += greenSlope;
               blue += blueSlope;
               pixelOffset++;
            }
         }
      }
   }
   private static void drawFlatScanlineSmooth(int[] pixels, int pixelOffset, int rgb, int xStart, int xEnd, float depth, float depthSlope) {
      if (restrictEdges) {
         if (xEnd > Rasterizer2D.centerX) {
            xEnd = Rasterizer2D.centerX;
         }

         if (xStart < 0) {
            xStart = 0;
         }
      }

      if (xStart < xEnd) {
         pixelOffset += xStart;
         int scalar = xEnd - xStart >> 2;
         depth += depthSlope * xStart;
         if (alpha == 0) {
            while (--scalar >= 0) {
               for (int loopIndex = 0; loopIndex < 4; loopIndex++) {
                  pixels[pixelOffset] = rgb;
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
                  pixelOffset++;
                  depth += depthSlope;
               }
            }

            for (int position = xEnd - xStart & 3; --position >= 0; depth += depthSlope) {
               pixels[pixelOffset] = rgb;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               pixelOffset++;
            }
         } else {
            int sourceAlpha = alpha;
            int scalar2 = 256 - alpha;
            rgb = ((rgb & 16711935) * scalar2 >> 8 & 16711935) + ((rgb & 0xFF00) * scalar2 >> 8 & 0xFF00);

            while (--scalar >= 0) {
               for (int loopIndex2 = 0; loopIndex2 < 4; loopIndex2++) {
                  pixels[pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
                  pixelOffset++;
                  depth += depthSlope;
               }
            }

            for (int position2 = xEnd - xStart & 3; --position2 >= 0; depth += depthSlope) {
               pixels[pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               pixelOffset++;
            }
         }
      }
   }
   private static void drawSmoothTexturedTriangle(
      int y0,
      int y1,
      int y2,
      int x0,
      int x1,
      int x2,
      int shade0,
      int shade1,
      int shade2,
      int textureX0,
      int textureX1,
      int textureX2,
      int textureY0,
      int textureY1,
      int textureY2,
      int textureZ0,
      int textureZ1,
      int textureZ2,
      int textureId,
      float depth0,
      float depth1,
      float depth2
   ) {
      if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
         shade0 = 127 - shade0 << 1;
         shade1 = 127 - shade1 << 1;
         shade2 = 127 - shade2 << 1;
         int[] texturePixelData = getTexturePixels(textureId);
         textureOpaque = !textureHasTransparency[textureId];
         textureX1 = textureX0 - textureX1;
         textureY1 = textureY0 - textureY1;
         textureZ1 = textureZ0 - textureZ1;
         textureX2 -= textureX0;
         textureY2 -= textureY0;
         textureZ2 -= textureZ0;
         textureId = Client.getProjectionScaleShift() + 5;
         if (!renderModeFlag) {
            textureId = 14;
         }

         long textureULong = ((long)textureX2 * textureY0 - (long)textureY2 * textureX0) << textureId;
         long textureUSlopeLong = ((long)textureY2 * textureZ0 - (long)textureZ2 * textureY0) << 8;
         long scalarLong = ((long)textureZ2 * textureX0 - (long)textureX2 * textureZ0) << 5;
         long textureVLong = ((long)textureX1 * textureY0 - (long)textureY1 * textureX0) << textureId;
         long textureVSlopeLong = ((long)textureY1 * textureZ0 - (long)textureZ1 * textureY0) << 8;
         long textureVStepLong = ((long)textureZ1 * textureX0 - (long)textureX1 * textureZ0) << 5;
         long textureWLong = ((long)textureY1 * textureX2 - (long)textureX1 * textureY2) << textureId;
         long textureWSlopeLong = ((long)textureZ1 * textureY2 - (long)textureY1 * textureZ2) << 8;
         long textureWStepLong = ((long)textureX1 * textureZ2 - (long)textureZ1 * textureX2) << 5;
         int coefficientShift = textureCoefficientNormalizationShift(
            textureULong,
            textureUSlopeLong,
            scalarLong,
            textureVLong,
            textureVSlopeLong,
            textureVStepLong,
            textureWLong,
            textureWSlopeLong,
            textureWStepLong
         );
         int textureU = (int)(textureULong >> coefficientShift);
         int textureUSlope = (int)(textureUSlopeLong >> coefficientShift);
         int scalar = (int)(scalarLong >> coefficientShift);
         int textureV = (int)(textureVLong >> coefficientShift);
         textureY0 = (int)(textureVSlopeLong >> coefficientShift);
         textureX0 = (int)(textureVStepLong >> coefficientShift);
         textureZ0 = (int)(textureWLong >> coefficientShift);
         textureY1 = (int)(textureWSlopeLong >> coefficientShift);
         textureX1 = (int)(textureWStepLong >> coefficientShift);
         textureX2 = 0;
         textureY2 = 0;
         if (y1 != y0) {
            textureX2 = (x1 - x0 << 16) / (y1 - y0);
            textureY2 = (shade1 - shade0 << 16) / (y1 - y0);
         }

         textureZ1 = 0;
         textureZ2 = 0;
         if (y2 != y1) {
            textureZ1 = (x2 - x1 << 16) / (y2 - y1);
            textureZ2 = (shade2 - shade1 << 16) / (y2 - y1);
         }

         textureId = 0;
         int shadeSlope02 = 0;
         if (y2 != y0) {
            textureId = (x0 - x2 << 16) / (y0 - y2);
            shadeSlope02 = (shade0 - shade2 << 16) / (y0 - y2);
         }

         float xDelta10 = x1 - x0;
         float yDelta10 = y1 - y0;
         float xDelta20 = x2 - x0;
         float yDelta20 = y2 - y0;
         float depthDelta10 = depth1 - depth0;
         float depthDelta20 = depth2 - depth0;
         float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
         yDelta10 = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
         xDelta10 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
         if (y0 <= y1 && y0 <= y2) {
            if (y0 < Rasterizer2D.bottomY) {
               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta10 * x0 + yDelta10;
               if (y1 < y2) {
                  int scalar2;
                  x2 = scalar2 = x0 << 16;
                  int scalar3;
                  shade2 = scalar3 = shade0 << 16;
                  if (y0 < 0) {
                     x2 -= textureId * y0;
                     scalar2 -= textureX2 * y0;
                     depth0 -= xDelta10 * y0;
                     shade2 -= shadeSlope02 * y0;
                     scalar3 -= textureY2 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  shade1 <<= 16;
                  if (y1 < 0) {
                     x1 -= textureZ1 * y1;
                     shade1 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  int scalar4 = y0 - viewportCenterY;
                  textureU += scalar * scalar4;
                  textureV += textureX0 * scalar4;
                  textureZ0 += textureX1 * scalar4;
                  if ((y0 == y1 || textureId >= textureX2) && (y0 != y1 || textureId <= textureZ1)) {
                     y2 -= y1;
                     y1 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y1 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, scalar2 >> 16, x2 >> 16, scalar3, shade2, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x2 += textureId;
                        scalar2 += textureX2;
                        depth0 += xDelta10;
                        shade2 += shadeSlope02;
                        scalar3 += textureY2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, x1 >> 16, x2 >> 16, shade1, shade2, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x2 += textureId;
                        x1 += textureZ1;
                        depth0 += xDelta10;
                        shade2 += shadeSlope02;
                        shade1 += textureZ2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y2 -= y1;
                     y1 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y1 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, x2 >> 16, scalar2 >> 16, shade2, scalar3, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x2 += textureId;
                        scalar2 += textureX2;
                        depth0 += xDelta10;
                        shade2 += shadeSlope02;
                        scalar3 += textureY2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, x2 >> 16, x1 >> 16, shade2, shade1, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x2 += textureId;
                        x1 += textureZ1;
                        depth0 += xDelta10;
                        shade2 += shadeSlope02;
                        shade1 += textureZ2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               } else {
                  int scalar5;
                  x1 = scalar5 = x0 << 16;
                  int scalar6;
                  shade1 = scalar6 = shade0 << 16;
                  if (y0 < 0) {
                     x1 -= textureId * y0;
                     scalar5 -= textureX2 * y0;
                     depth0 -= xDelta10 * y0;
                     shade1 -= shadeSlope02 * y0;
                     scalar6 -= textureY2 * y0;
                     y0 = 0;
                  }

                  x2 <<= 16;
                  shade2 <<= 16;
                  if (y2 < 0) {
                     x2 -= textureZ1 * y2;
                     shade2 -= textureZ2 * y2;
                     y2 = 0;
                  }

                  int scalar7 = y0 - viewportCenterY;
                  textureU += scalar * scalar7;
                  textureV += textureX0 * scalar7;
                  textureZ0 += textureX1 * scalar7;
                  if ((y0 == y2 || textureId >= textureX2) && (y0 != y2 || textureZ1 <= textureX2)) {
                     y1 -= y2;
                     y2 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, scalar5 >> 16, x1 >> 16, scalar6, shade1, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x1 += textureId;
                        scalar5 += textureX2;
                        shade1 += shadeSlope02;
                        scalar6 += textureY2;
                        depth0 += xDelta10;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y1 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, scalar5 >> 16, x2 >> 16, scalar6, shade2, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x2 += textureZ1;
                        scalar5 += textureX2;
                        shade2 += textureZ2;
                        scalar6 += textureY2;
                        depth0 += xDelta10;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y1 -= y2;
                     y2 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, x1 >> 16, scalar5 >> 16, shade1, scalar6, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x1 += textureId;
                        scalar5 += textureX2;
                        shade1 += shadeSlope02;
                        scalar6 += textureY2;
                        depth0 += xDelta10;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y1 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y0, x2 >> 16, scalar5 >> 16, shade2, scalar6, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                        );
                        x2 += textureZ1;
                        scalar5 += textureX2;
                        shade2 += textureZ2;
                        scalar6 += textureY2;
                        depth0 += xDelta10;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               }
            }
         } else if (y1 <= y2) {
            if (y1 < Rasterizer2D.bottomY) {
               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta10 * x1 + yDelta10;
               if (y2 < y0) {
                  int scalar8;
                  x0 = scalar8 = x1 << 16;
                  int scalar9;
                  shade0 = scalar9 = shade1 << 16;
                  if (y1 < 0) {
                     x0 -= textureX2 * y1;
                     scalar8 -= textureZ1 * y1;
                     depth1 -= xDelta10 * y1;
                     shade0 -= textureY2 * y1;
                     scalar9 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  shade2 <<= 16;
                  if (y2 < 0) {
                     x2 -= textureId * y2;
                     shade2 -= shadeSlope02 * y2;
                     y2 = 0;
                  }

                  int scalar10 = y1 - viewportCenterY;
                  textureU += scalar * scalar10;
                  textureV += textureX0 * scalar10;
                  textureZ0 += textureX1 * scalar10;
                  if ((y1 == y2 || textureX2 >= textureZ1) && (y1 != y2 || textureX2 <= textureId)) {
                     y0 -= y2;
                     y2 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, scalar8 >> 16, x0 >> 16, scalar9, shade0, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x0 += textureX2;
                        scalar8 += textureZ1;
                        shade0 += textureY2;
                        scalar9 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y0 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, x2 >> 16, x0 >> 16, shade2, shade0, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x0 += textureX2;
                        x2 += textureId;
                        shade0 += textureY2;
                        shade2 += shadeSlope02;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y0 -= y2;
                     y2 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, x0 >> 16, scalar8 >> 16, shade0, scalar9, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x0 += textureX2;
                        scalar8 += textureZ1;
                        shade0 += textureY2;
                        scalar9 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y0 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, x0 >> 16, x2 >> 16, shade0, shade2, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x0 += textureX2;
                        x2 += textureId;
                        shade0 += textureY2;
                        shade2 += shadeSlope02;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               } else {
                  int scalar11;
                  x2 = scalar11 = x1 << 16;
                  int scalar12;
                  shade2 = scalar12 = shade1 << 16;
                  if (y1 < 0) {
                     x2 -= textureX2 * y1;
                     scalar11 -= textureZ1 * y1;
                     depth1 -= xDelta10 * y1;
                     shade2 -= textureY2 * y1;
                     scalar12 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  x0 <<= 16;
                  shade0 <<= 16;
                  if (y0 < 0) {
                     x0 -= textureId * y0;
                     shade0 -= shadeSlope02 * y0;
                     y0 = 0;
                  }

                  int scalar13 = y1 - viewportCenterY;
                  textureU += scalar * scalar13;
                  textureV += textureX0 * scalar13;
                  textureZ0 += textureX1 * scalar13;
                  if (textureX2 < textureZ1) {
                     y2 -= y0;
                     y0 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y0 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, x2 >> 16, scalar11 >> 16, shade2, scalar12, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x2 += textureX2;
                        scalar11 += textureZ1;
                        shade2 += textureY2;
                        scalar12 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, x0 >> 16, scalar11 >> 16, shade0, scalar12, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x0 += textureId;
                        scalar11 += textureZ1;
                        shade0 += shadeSlope02;
                        scalar12 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y2 -= y0;
                     y0 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y0 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, scalar11 >> 16, x2 >> 16, scalar12, shade2, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x2 += textureX2;
                        scalar11 += textureZ1;
                        shade2 += textureY2;
                        scalar12 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawSmoothTexturedScanline(
                           Rasterizer2D.pixels, texturePixelData, y1, scalar11 >> 16, x0 >> 16, scalar12, shade0, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                        );
                        x0 += textureId;
                        scalar11 += textureZ1;
                        shade0 += shadeSlope02;
                        scalar12 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               }
            }
         } else if (y2 < Rasterizer2D.bottomY) {
            if (y0 > Rasterizer2D.bottomY) {
               y0 = Rasterizer2D.bottomY;
            }

            if (y1 > Rasterizer2D.bottomY) {
               y1 = Rasterizer2D.bottomY;
            }

            depth2 = depth2 - yDelta10 * x2 + yDelta10;
            if (y0 < y1) {
               int scalar14;
               x1 = scalar14 = x2 << 16;
               int scalar15;
               shade1 = scalar15 = shade2 << 16;
               if (y2 < 0) {
                  x1 -= textureZ1 * y2;
                  scalar14 -= textureId * y2;
                  depth2 -= xDelta10 * y2;
                  shade1 -= textureZ2 * y2;
                  scalar15 -= shadeSlope02 * y2;
                  y2 = 0;
               }

               x0 <<= 16;
               shade0 <<= 16;
               if (y0 < 0) {
                  x0 -= textureX2 * y0;
                  shade0 -= textureY2 * y0;
                  y0 = 0;
               }

               int scalar16 = y2 - viewportCenterY;
               textureU += scalar * scalar16;
               textureV += textureX0 * scalar16;
               textureZ0 += textureX1 * scalar16;
               if (textureZ1 < textureId) {
                  y1 -= y0;
                  y0 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y0 >= 0) {
                     drawSmoothTexturedScanline(
                        Rasterizer2D.pixels, texturePixelData, y2, x1 >> 16, scalar14 >> 16, shade1, scalar15, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x1 += textureZ1;
                     scalar14 += textureId;
                     shade1 += textureZ2;
                     scalar15 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y1 >= 0) {
                     drawSmoothTexturedScanline(Rasterizer2D.pixels, texturePixelData, y2, x1 >> 16, x0 >> 16, shade1, shade0, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10);
                     x1 += textureZ1;
                     x0 += textureX2;
                     shade1 += textureZ2;
                     shade0 += textureY2;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }
               } else {
                  y1 -= y0;
                  y0 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y0 >= 0) {
                     drawSmoothTexturedScanline(
                        Rasterizer2D.pixels, texturePixelData, y2, scalar14 >> 16, x1 >> 16, scalar15, shade1, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x1 += textureZ1;
                     scalar14 += textureId;
                     shade1 += textureZ2;
                     scalar15 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y1 >= 0) {
                     drawSmoothTexturedScanline(Rasterizer2D.pixels, texturePixelData, y2, x0 >> 16, x1 >> 16, shade0, shade1, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10);
                     x1 += textureZ1;
                     x0 += textureX2;
                     shade1 += textureZ2;
                     shade0 += textureY2;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }
               }
            } else {
               int scalar17;
               x0 = scalar17 = x2 << 16;
               int scalar18;
               shade0 = scalar18 = shade2 << 16;
               if (y2 < 0) {
                  x0 -= textureZ1 * y2;
                  scalar17 -= textureId * y2;
                  depth2 -= xDelta10 * y2;
                  shade0 -= textureZ2 * y2;
                  scalar18 -= shadeSlope02 * y2;
                  y2 = 0;
               }

               x1 <<= 16;
               shade1 <<= 16;
               if (y1 < 0) {
                  x1 -= textureX2 * y1;
                  shade1 -= textureY2 * y1;
                  y1 = 0;
               }

               int scalar19 = y2 - viewportCenterY;
               textureU += scalar * scalar19;
               textureV += textureX0 * scalar19;
               textureZ0 += textureX1 * scalar19;
               if (textureZ1 < textureId) {
                  y0 -= y1;
                  y1 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y1 >= 0) {
                     drawSmoothTexturedScanline(
                        Rasterizer2D.pixels, texturePixelData, y2, x0 >> 16, scalar17 >> 16, shade0, scalar18, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x0 += textureZ1;
                     scalar17 += textureId;
                     shade0 += textureZ2;
                     scalar18 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y0 >= 0) {
                     drawSmoothTexturedScanline(
                        Rasterizer2D.pixels, texturePixelData, y2, x1 >> 16, scalar17 >> 16, shade1, scalar18, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x1 += textureX2;
                     scalar17 += textureId;
                     shade1 += textureY2;
                     scalar18 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }
               } else {
                  y0 -= y1;
                  y1 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y1 >= 0) {
                     drawSmoothTexturedScanline(
                        Rasterizer2D.pixels, texturePixelData, y2, scalar17 >> 16, x0 >> 16, scalar18, shade0, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x0 += textureZ1;
                     scalar17 += textureId;
                     shade0 += textureZ2;
                     scalar18 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y0 >= 0) {
                     drawSmoothTexturedScanline(
                        Rasterizer2D.pixels, texturePixelData, y2, scalar17 >> 16, x1 >> 16, scalar18, shade1, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x1 += textureX2;
                     scalar17 += textureId;
                     shade1 += textureY2;
                     scalar18 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }
               }
            }
         }
      }
   }
   private static void drawSmoothTexturedScanline(
      int[] pixels,
      int[] texturePixels,
      int pixelOffset,
      int xStart,
      int xEnd,
      int brightness,
      int brightnessSlope,
      int textureU,
      int textureV,
      int textureW,
      int textureUSlope,
      int textureVSlope,
      int textureWSlope,
      float depth,
      float depthSlope
   ) {
      if (xStart >= xEnd) {
         return;
      }

      int brightnessStep = (brightnessSlope - brightness) / (xEnd - xStart);
      if (restrictEdges) {
         if (xEnd > Rasterizer2D.centerX) {
            xEnd = Rasterizer2D.centerX;
         }

         if (xStart < 0) {
            brightness -= xStart * brightnessStep;
            xStart = 0;
         }
      }

      if (xStart >= xEnd) {
         return;
      }

      pixelOffset += xStart;
      depth += depthSlope * xStart;

      int perspectiveShift = lowMemory ? 12 : 14;
      int coordinateMask = lowMemory ? 4032 : 16256;
      int coordinateShift = lowMemory ? 6 : 7;

      long xOffset = (long)xStart - viewportCenterX;
      // Preserve the three fractional slope bits that the legacy
      // (slope >> 3) * x calculation discarded. Perspective is evaluated at
      // four-pixel boundaries and interpolated inside each short block. This
      // removes the visible eight-pixel swimming without paying for two long
      // divisions for every textured pixel at fullscreen resolutions.
      long projectedU = ((long)textureU << 3) + (long)textureUSlope * xOffset;
      long projectedV = ((long)textureV << 3) + (long)textureVSlope * xOffset;
      long projectedW = ((long)textureW << 3) + (long)textureWSlope * xOffset;

      int u = 0;
      int v = 0;
      if (projectedW != 0L) {
         long projectedTextureU = (projectedU << perspectiveShift) / projectedW;
         if (projectedTextureU < 0L) {
            u = 0;
         } else if (projectedTextureU > coordinateMask) {
            u = coordinateMask;
         } else {
            u = (int)projectedTextureU;
         }

         v = (int)((projectedV << perspectiveShift) / projectedW);
      }

      int remaining = xEnd - xStart;
      while (remaining > 0) {
         int blockSize = remaining > 4 ? 4 : remaining;
         long nextProjectedU = projectedU + (long)textureUSlope * blockSize;
         long nextProjectedV = projectedV + (long)textureVSlope * blockSize;
         long nextProjectedW = projectedW + (long)textureWSlope * blockSize;

         int nextU = 0;
         int nextV = 0;
         if (nextProjectedW != 0L) {
            long projectedTextureU = (nextProjectedU << perspectiveShift) / nextProjectedW;
            if (projectedTextureU < 0L) {
               nextU = 0;
            } else if (projectedTextureU > coordinateMask) {
               nextU = coordinateMask;
            } else {
               nextU = (int)projectedTextureU;
            }

            nextV = (int)((nextProjectedV << perspectiveShift) / nextProjectedW);
         }

         int uStep = (nextU - u) / blockSize;
         int vStep = (nextV - v) / blockSize;

         for (int blockPixel = 0; blockPixel < blockSize; blockPixel++) {
            int texturePixel = texturePixels[(v & coordinateMask) + (u >> coordinateShift)];
            if (textureOpaque || texturePixel != 0) {
               int light = brightness >> 16;
               pixels[pixelOffset] = (
                  (texturePixel & 16711935) * light & -16711936
               ) + (
                  (texturePixel & 0xFF00) * light & 0xFF0000
               ) >> 8;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
            }

            pixelOffset++;
            depth += depthSlope;
            brightness += brightnessStep;
            u += uStep;
            v += vStep;
         }

         projectedU = nextProjectedU;
         projectedV = nextProjectedV;
         projectedW = nextProjectedW;
         u = nextU;
         v = nextV;
         remaining -= blockSize;
      }
   }
   private static void drawTexturedScanline(
      int[] pixels,
      int[] texturePixels,
      int pixelOffset,
      int xStart,
      int xEnd,
      int brightness,
      int brightnessSlope,
      int textureU,
      int textureV,
      int textureW,
      int textureUSlope,
      int textureVSlope,
      int textureWSlope,
      float depth,
      float depthSlope
   ) {
      if (xStart >= xEnd) {
         return;
      }

      int brightnessStep;
      if (restrictEdges) {
         int brightnessPerPixel = (brightnessSlope - brightness) / (xEnd - xStart);
         if (xEnd > Rasterizer2D.centerX) {
            xEnd = Rasterizer2D.centerX;
         }

         if (xStart < 0) {
            brightness -= xStart * brightnessPerPixel;
            xStart = 0;
         }

         if (xStart >= xEnd) {
            return;
         }

         brightnessStep = brightnessPerPixel << 12;
      } else if (xEnd - xStart > 7) {
         int blocks = xEnd - xStart >> 3;
         brightnessStep = (brightnessSlope - brightness) * reciprocal512[blocks] >> 6;
      } else {
         brightnessStep = 0;
      }

      int scaledBrightness = brightness << 9;
      pixelOffset += xStart;
      depth += depthSlope * xStart;

      int perspectiveShift = lowMemory ? 12 : 14;
      int coordinateMask = lowMemory ? 4032 : 16256;
      int coordinateShift = lowMemory ? 6 : 7;
      int bankShift = lowMemory ? 9 : 7;

      long xOffset = (long)xStart - viewportCenterX;
      long projectedU = ((long)textureU << 3) + (long)textureUSlope * xOffset;
      long projectedV = ((long)textureV << 3) + (long)textureVSlope * xOffset;
      long projectedW = ((long)textureW << 3) + (long)textureWSlope * xOffset;

      int u = 0;
      int v = 0;
      if (projectedW != 0L) {
         long projectedTextureU = (projectedU << perspectiveShift) / projectedW;
         if (projectedTextureU < 0L) {
            u = 0;
         } else if (projectedTextureU > coordinateMask) {
            u = coordinateMask;
         } else {
            u = (int)projectedTextureU;
         }

         v = (int)((projectedV << perspectiveShift) / projectedW);
      }

      int brightnessBlockPixel = 0;
      int remaining = xEnd - xStart;
      while (remaining > 0) {
         int blockSize = remaining > 4 ? 4 : remaining;
         long nextProjectedU = projectedU + (long)textureUSlope * blockSize;
         long nextProjectedV = projectedV + (long)textureVSlope * blockSize;
         long nextProjectedW = projectedW + (long)textureWSlope * blockSize;

         int nextU = 0;
         int nextV = 0;
         if (nextProjectedW != 0L) {
            long projectedTextureU = (nextProjectedU << perspectiveShift) / nextProjectedW;
            if (projectedTextureU < 0L) {
               nextU = 0;
            } else if (projectedTextureU > coordinateMask) {
               nextU = coordinateMask;
            } else {
               nextU = (int)projectedTextureU;
            }

            nextV = (int)((nextProjectedV << perspectiveShift) / nextProjectedW);
         }

         int uStep = (nextU - u) / blockSize;
         int vStep = (nextV - v) / blockSize;

         for (int blockPixel = 0; blockPixel < blockSize; blockPixel++) {
            int bankOffset = (scaledBrightness & 6291456) >> bankShift;
            int shadeShift = scaledBrightness >> 23;
            int texturePixel = texturePixels[bankOffset + (v & coordinateMask) + (u >> coordinateShift)] >>> shadeShift;
            if (textureOpaque || texturePixel != 0) {
               pixels[pixelOffset] = texturePixel;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
            }

            pixelOffset++;
            depth += depthSlope;
            u += uStep;
            v += vStep;

            if (++brightnessBlockPixel == 8) {
               brightnessBlockPixel = 0;
               scaledBrightness += brightnessStep;
            }
         }

         projectedU = nextProjectedU;
         projectedV = nextProjectedV;
         projectedW = nextProjectedW;
         u = nextU;
         v = nextV;
         remaining -= blockSize;
      }
   }
   public static void clear() {
      reciprocal512 = null;
      reciprocal512 = null;
      SINE = null;
      COSINE = null;
      scanOffsets = null;
      textures = null;
      textureHasTransparency = null;
      averageTextureColors = null;
      texturePixelPool = null;
      texturePixels = null;
      textureLastUsed = null;
      HSL_TO_RGB = null;
      texturePalettes = null;
   }
   public static void setViewportFromRaster() {
      scanOffsets = new int[Rasterizer2D.height];

      for (int scanOffsetIndex = 0; scanOffsetIndex < Rasterizer2D.height; scanOffsetIndex++) {
         scanOffsets[scanOffsetIndex] = Rasterizer2D.width * scanOffsetIndex;
      }

      viewportCenterX = Rasterizer2D.width / 2;
      viewportCenterY = Rasterizer2D.height / 2;
   }
   public static void setViewportFromClip() {
      int bottomY = Rasterizer2D.bottomY;
      int centerXOrBottomX = Rasterizer2D.bottomX;
      int topY = Rasterizer2D.topY;
      int scanOffsetOrTopX = Rasterizer2D.topX;
      Rasterizer2D.centerX = centerXOrBottomX - scanOffsetOrTopX;
      Rasterizer2D.bottomY = bottomY - topY;
      viewportCenterX = Rasterizer2D.centerX / 2;
      viewportCenterY = Rasterizer2D.bottomY / 2;
      Rasterizer2D.centerY = Rasterizer2D.centerX - viewportCenterX;
      Rasterizer2D.viewportCenterY = Rasterizer2D.bottomY - viewportCenterY;
      if (scanOffsets.length != Rasterizer2D.bottomY) {
         scanOffsets = new int[Rasterizer2D.bottomY];
      }

      scanOffsetOrTopX = topY * Rasterizer2D.width + scanOffsetOrTopX;

      for (int scanOffsetIndex = 0; scanOffsetIndex < Rasterizer2D.bottomY; scanOffsetIndex++) {
         scanOffsets[scanOffsetIndex] = scanOffsetOrTopX;
         scanOffsetOrTopX += Rasterizer2D.width;
      }
   }
   public static void setViewport(int scanOffsetOrViewportCenterX, int viewportCenterYOrLength) {
      scanOffsets = new int[viewportCenterYOrLength];

      for (int scanOffsetIndex = 0; scanOffsetIndex < viewportCenterYOrLength; scanOffsetIndex++) {
         scanOffsets[scanOffsetIndex] = scanOffsetOrViewportCenterX * scanOffsetIndex;
      }

      viewportCenterX = scanOffsetOrViewportCenterX / 2;
      viewportCenterY = viewportCenterYOrLength / 2;
   }
   public static void clearTextureCache() {
      texturePixelPool = null;

      for (int texturePixelIndex = 0; texturePixelIndex < 51; texturePixelIndex++) {
         texturePixels[texturePixelIndex] = null;
      }
   }
   public static void initializeTextureCache() {
      if (texturePixelPool == null) {
         texturePoolSize = 20;
         if (lowMemory) {
            texturePixelPool = new int[texturePoolSize][16384];
         } else {
            texturePixelPool = new int[texturePoolSize][65536];
         }

         for (int texturePixelIndex = 0; texturePixelIndex < 51; texturePixelIndex++) {
            texturePixels[texturePixelIndex] = null;
         }
      }
   }
   public static void loadTextures(Archive archive) {
      textureCount = 0;

      for (int textureIndex = 0; textureIndex < 51; textureIndex++) {
         try {
            textures[textureIndex] = new IndexedSprite(archive, String.valueOf(textureIndex), 0);
            if (lowMemory && textures[textureIndex].canvasWidth == 128) {
               textures[textureIndex].downscaleHalf();
            } else {
               textures[textureIndex].resize();
            }

            textureCount++;
         } catch (Exception exception) {
         }
      }
   }
   public static int getAverageTextureColor(int averageTextureColorIndex) {
      if (averageTextureColors[averageTextureColorIndex] != 0) {
         return averageTextureColors[averageTextureColorIndex];
      }

      int scalar = 0;
      int scalar2 = 0;
      int scalar3 = 0;
      int localTexturePalettes = texturePalettes[averageTextureColorIndex].length;

      for (int loopIndex = 0; loopIndex < localTexturePalettes; loopIndex++) {
         scalar += texturePalettes[averageTextureColorIndex][loopIndex] >> 16 & 0xFF;
         scalar2 += texturePalettes[averageTextureColorIndex][loopIndex] >> 8 & 0xFF;
         scalar3 += texturePalettes[averageTextureColorIndex][loopIndex] & 0xFF;
      }

      int adjustBrightnessResult;
      if ((adjustBrightnessResult = adjustBrightness((scalar / localTexturePalettes << 16) + (scalar2 / localTexturePalettes << 8) + scalar3 / localTexturePalettes, 1.4)) == 0) {
         adjustBrightnessResult = 1;
      }

      averageTextureColors[averageTextureColorIndex] = adjustBrightnessResult;
      return adjustBrightnessResult;
   }
   public static void releaseTexture(int texturePixelIndex) {
      GpuRasterizer3D.invalidateTexture(texturePixelIndex);
      if (texturePixels[texturePixelIndex] != null) {
         texturePixelPool[texturePoolSize++] = texturePixels[texturePixelIndex];
         texturePixels[texturePixelIndex] = null;
      }
   }
   private static int[] getTexturePixels(int textureLastUsedIndex) {
      textureLastUsed[textureLastUsedIndex] = textureUsageCounter++;
      if (texturePixels[textureLastUsedIndex] != null) {
         return texturePixels[textureLastUsedIndex];
      }

      int[] texturePixelOrTexturePixelPool;
      if (texturePoolSize > 0) {
         texturePixelOrTexturePixelPool = texturePixelPool[--texturePoolSize];
         texturePixelPool[texturePoolSize] = null;
      } else {
         int localTextureLastUsed = 0;
         int texturePixelIndex2 = -1;

         for (int texturePixelIndex = 0; texturePixelIndex < textureCount; texturePixelIndex++) {
            if (texturePixels[texturePixelIndex] != null && (textureLastUsed[texturePixelIndex] < localTextureLastUsed || texturePixelIndex2 == -1)) {
               localTextureLastUsed = textureLastUsed[texturePixelIndex];
               texturePixelIndex2 = texturePixelIndex;
            }
         }

         texturePixelOrTexturePixelPool = texturePixels[texturePixelIndex2];
         texturePixels[texturePixelIndex2] = null;
      }

      texturePixels[textureLastUsedIndex] = texturePixelOrTexturePixelPool;
      IndexedSprite indexedSprite = textures[textureLastUsedIndex];
      int[] texturePalette = texturePalettes[textureLastUsedIndex];
      if (lowMemory) {
         textureHasTransparency[textureLastUsedIndex] = false;

         for (int texturePixelOrTexturePixelPoolIndex = 0; texturePixelOrTexturePixelPoolIndex < 4096; texturePixelOrTexturePixelPoolIndex++) {
            int scalar;
            if ((scalar = texturePixelOrTexturePixelPool[texturePixelOrTexturePixelPoolIndex] = texturePalette[indexedSprite.pixelIndices[texturePixelOrTexturePixelPoolIndex]] & 16316671) == 0) {
               textureHasTransparency[textureLastUsedIndex] = true;
            }

            texturePixelOrTexturePixelPool[texturePixelOrTexturePixelPoolIndex + 4096] = scalar - (scalar >>> 3) & 16316671;
            texturePixelOrTexturePixelPool[texturePixelOrTexturePixelPoolIndex + 8192] = scalar - (scalar >>> 2) & 16316671;
            texturePixelOrTexturePixelPool[texturePixelOrTexturePixelPoolIndex + 12288] = scalar - (scalar >>> 2) - (scalar >>> 3) & 16316671;
         }
      } else {
         if (indexedSprite.width != 64) {
            for (int pixelIndex = 0; pixelIndex < 16384; pixelIndex++) {
               texturePixelOrTexturePixelPool[pixelIndex] = texturePalette[indexedSprite.pixelIndices[pixelIndex]];
            }
         } else {
            for (int loopIndex = 0; loopIndex < 128; loopIndex++) {
               for (int loopIndex2 = 0; loopIndex2 < 128; loopIndex2++) {
                  texturePixelOrTexturePixelPool[loopIndex2 + (loopIndex << 7)] = texturePalette[indexedSprite.pixelIndices[(loopIndex2 >> 1) + (loopIndex >> 1 << 6)]];
               }
            }
         }

         textureHasTransparency[textureLastUsedIndex] = false;

         for (int loopIndex3 = 0; loopIndex3 < 16384; loopIndex3++) {
            texturePixelOrTexturePixelPool[loopIndex3] &= 16316671;
            int scalar2;
            if ((scalar2 = texturePixelOrTexturePixelPool[loopIndex3]) == 0) {
               textureHasTransparency[textureLastUsedIndex] = true;
            }

            texturePixelOrTexturePixelPool[loopIndex3 + 16384] = scalar2 - (scalar2 >>> 3) & 16316671;
            texturePixelOrTexturePixelPool[loopIndex3 + 32768] = scalar2 - (scalar2 >>> 2) & 16316671;
            texturePixelOrTexturePixelPool[loopIndex3 + 49152] = scalar2 - (scalar2 >>> 2) - (scalar2 >>> 3) & 16316671;
         }
      }

      return texturePixelOrTexturePixelPool;
   }

   static int[] getGpuTexturePixels(int textureId) {
      return getTexturePixels(textureId);
   }

   public static void setBrightness(double calculationArgument) {
      calculationArgument += Math.random() * 0.03 - 0.015;
      int scalar = 0;

      for (int loopIndex = 0; loopIndex < 512; loopIndex++) {
         double calculation = loopIndex / 8 / 64.0 + 0.0078125;
         double calculation2 = (loopIndex & 7) / 8.0 + 0.0625;

         for (int loopIndex2 = 0; loopIndex2 < 128; loopIndex2++) {
            double calculation3;
            double calculation4 = calculation3 = loopIndex2 / 128.0;
            double calculation5 = calculation3;
            double calculation6 = calculation3;
            if (calculation2 != 0.0) {
               double calculation7;
               if (calculation3 < 0.5) {
                  calculation7 = calculation3 * (calculation2 + 1.0);
               } else {
                  calculation7 = calculation3 + calculation2 - calculation3 * calculation2;
               }

               double calculation8 = calculation3 * 2.0 - calculation7;
               double calculation9;
               if ((calculation9 = calculation + 0.3333333333333333) > 1.0) {
                  calculation9--;
               }

               double calculation10;
               if ((calculation10 = calculation - 0.3333333333333333) < 0.0) {
                  calculation10++;
               }

               if (calculation9 * 6.0 < 1.0) {
                  calculation4 = calculation8 + (calculation7 - calculation8) * 6.0 * calculation9;
               } else if (calculation9 * 2.0 < 1.0) {
                  calculation4 = calculation7;
               } else if (calculation9 * 3.0 < 2.0) {
                  calculation4 = calculation8 + (calculation7 - calculation8) * (0.6666666666666666 - calculation9) * 6.0;
               } else {
                  calculation4 = calculation8;
               }

               if (calculation * 6.0 < 1.0) {
                  calculation5 = calculation8 + (calculation7 - calculation8) * 6.0 * calculation;
               } else if (calculation * 2.0 < 1.0) {
                  calculation5 = calculation7;
               } else if (calculation * 3.0 < 2.0) {
                  calculation5 = calculation8 + (calculation7 - calculation8) * (0.6666666666666666 - calculation) * 6.0;
               } else {
                  calculation5 = calculation8;
               }

               if (calculation10 * 6.0 < 1.0) {
                  calculation6 = calculation8 + (calculation7 - calculation8) * 6.0 * calculation10;
               } else if (calculation10 * 2.0 < 1.0) {
                  calculation6 = calculation7;
               } else if (calculation10 * 3.0 < 2.0) {
                  calculation6 = calculation8 + (calculation7 - calculation8) * (0.6666666666666666 - calculation10) * 6.0;
               } else {
                  calculation6 = calculation8;
               }
            }

            int scalar2 = (int)(calculation4 * 256.0);
            int scalar3 = (int)(calculation5 * 256.0);
            int scalar4 = (int)(calculation6 * 256.0);
            int adjustBrightnessResult;
            if ((adjustBrightnessResult = adjustBrightness((scalar2 << 16) + (scalar3 << 8) + scalar4, calculationArgument)) == 0) {
               adjustBrightnessResult = 1;
            }

            HSL_TO_RGB[scalar++] = adjustBrightnessResult;
         }
      }

      for (int textureIndex = 0; textureIndex < 51; textureIndex++) {
         if (textures[textureIndex] != null) {
            int[] palette = textures[textureIndex].palette;
            texturePalettes[textureIndex] = new int[palette.length];

            for (int paletteIndex = 0; paletteIndex < palette.length; paletteIndex++) {
               texturePalettes[textureIndex][paletteIndex] = adjustBrightness(palette[paletteIndex], calculationArgument);
               if ((texturePalettes[textureIndex][paletteIndex] & 16316671) == 0 && paletteIndex != 0) {
                  texturePalettes[textureIndex][paletteIndex] = 1;
               }
            }
         }
      }

      for (int texturePixelIndex = 0; texturePixelIndex < 51; texturePixelIndex++) {
         releaseTexture(texturePixelIndex);
      }
   }
   private static int adjustBrightness(int paletteEntry, double calculationArgument) {
      double localPow = (paletteEntry >> 16) / 256.0;
      double pow2 = (paletteEntry >> 8 & 0xFF) / 256.0;
      double pow3 = (paletteEntry & 0xFF) / 256.0;
      localPow = Math.pow(localPow, calculationArgument);
      pow2 = Math.pow(pow2, calculationArgument);
      pow3 = Math.pow(pow3, calculationArgument);
      paletteEntry = (int)(localPow * 256.0);
      int scalar = (int)(pow2 * 256.0);
      int scalar2 = (int)(pow3 * 256.0);
      return (paletteEntry << 16) + (scalar << 8) + scalar2;
   }
   public static void drawShadedTriangle(
      boolean useFallback, int y0, int y1, int y2, int x0, int x1, int x2, int color0, int color1, int color2, float depth0, float depth1, float depth2
   ) {
      if (GpuRasterizer3D.drawShadedTriangle(useFallback, y0, y1, y2, x0, x1, x2, color0, color1, color2, depth0, depth1, depth2)) {
         return;
      }

      if (Client.smoothRendering) {
         if (smoothShading && !useFallback) {
            drawSmoothShadedTriangle(y0, y1, y2, x0, x1, x2, color0, color1, color2, depth0, depth1, depth2);
         } else if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
            int hSL_TO_RGBEntry = HSL_TO_RGB[color0];
            color0 = HSL_TO_RGB[color1];
            color1 = HSL_TO_RGB[color2];
            color2 = hSL_TO_RGBEntry >> 16 & 0xFF;
            int scalar = hSL_TO_RGBEntry >> 8 & 0xFF;
            hSL_TO_RGBEntry &= 255;
            int red0 = color0 >> 16 & 0xFF;
            int green0 = color0 >> 8 & 0xFF;
            color0 &= 255;
            int red1 = color1 >> 16 & 0xFF;
            int green1 = color1 >> 8 & 0xFF;
            color1 &= 255;
            int xSlope10 = 0;
            int scalar2 = 0;
            int scalar3 = 0;
            int scalar4 = 0;
            if (y1 != y0) {
               xSlope10 = (x1 - x0 << 16) / (y1 - y0);
               scalar2 = (red0 - color2 << 16) / (y1 - y0);
               scalar3 = (green0 - scalar << 16) / (y1 - y0);
               scalar4 = (color0 - hSL_TO_RGBEntry << 16) / (y1 - y0);
            }

            int xSlope21 = 0;
            int redSlope10 = 0;
            int greenSlope10 = 0;
            int colorSlope10 = 0;
            if (y2 != y1) {
               xSlope21 = (x2 - x1 << 16) / (y2 - y1);
               redSlope10 = (red1 - red0 << 16) / (y2 - y1);
               greenSlope10 = (green1 - green0 << 16) / (y2 - y1);
               colorSlope10 = (color1 - color0 << 16) / (y2 - y1);
            }

            int xSlope02 = 0;
            int scalar5 = 0;
            int scalar6 = 0;
            int scalar7 = 0;
            if (y2 != y0) {
               xSlope02 = (x0 - x2 << 16) / (y0 - y2);
               scalar5 = (color2 - red1 << 16) / (y0 - y2);
               scalar6 = (scalar - green1 << 16) / (y0 - y2);
               scalar7 = (hSL_TO_RGBEntry - color1 << 16) / (y0 - y2);
            }

            float xDelta10 = x1 - x0;
            float yDelta10 = y1 - y0;
            float xDelta20 = x2 - x0;
            float yDelta20 = y2 - y0;
            float depthDelta10 = depth1 - depth0;
            float depthDelta20 = depth2 - depth0;
            float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
            yDelta10 = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
            xDelta10 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
            if (y0 <= y1 && y0 <= y2) {
               if (y0 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta10 * x0 + yDelta10;
               if (y1 < y2) {
                  int scalar8;
                  x2 = scalar8 = x0 << 16;
                  int scalar9;
                  red1 = scalar9 = color2 << 16;
                  int scalar10;
                  green1 = scalar10 = scalar << 16;
                  int scalar11;
                  color1 = scalar11 = hSL_TO_RGBEntry << 16;
                  if (y0 < 0) {
                     x2 -= xSlope02 * y0;
                     scalar8 -= xSlope10 * y0;
                     red1 = scalar9 - scalar5 * y0;
                     green1 = scalar10 - scalar6 * y0;
                     color1 = scalar11 - scalar7 * y0;
                     scalar9 -= scalar2 * y0;
                     scalar10 -= scalar3 * y0;
                     scalar11 -= scalar4 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  red0 <<= 16;
                  green0 <<= 16;
                  color0 <<= 16;
                  if (y1 < 0) {
                     x1 -= xSlope21 * y1;
                     red0 -= redSlope10 * y1;
                     green0 -= greenSlope10 * y1;
                     color0 -= colorSlope10 * y1;
                     y1 = 0;
                  }

                  if (y0 != y1 && xSlope02 < xSlope10 || y0 == y1 && xSlope02 > xSlope21) {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, x2 >> 16, scalar8 >> 16, red1, green1, color1, scalar9, scalar10, scalar11, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar8 += xSlope10;
                        red1 += scalar5;
                        green1 += scalar6;
                        color1 += scalar7;
                        scalar9 += scalar2;
                        scalar10 += scalar3;
                        scalar11 += scalar4;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, x2 >> 16, x1 >> 16, red1, green1, color1, red0, green0, color0, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        red1 += scalar5;
                        green1 += scalar6;
                        color1 += scalar7;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  } else {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, scalar8 >> 16, x2 >> 16, scalar9, scalar10, scalar11, red1, green1, color1, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar8 += xSlope10;
                        red1 += scalar5;
                        green1 += scalar6;
                        color1 += scalar7;
                        scalar9 += scalar2;
                        scalar10 += scalar3;
                        scalar11 += scalar4;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, x1 >> 16, x2 >> 16, red0, green0, color0, red1, green1, color1, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        red1 += scalar5;
                        green1 += scalar6;
                        color1 += scalar7;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  }
               } else {
                  int scalar12;
                  x1 = scalar12 = x0 << 16;
                  int scalar13;
                  red0 = scalar13 = color2 << 16;
                  int scalar14;
                  green0 = scalar14 = scalar << 16;
                  int scalar15;
                  color0 = scalar15 = hSL_TO_RGBEntry << 16;
                  if (y0 < 0) {
                     x1 -= xSlope02 * y0;
                     scalar12 -= xSlope10 * y0;
                     red0 = scalar13 - scalar5 * y0;
                     green0 = scalar14 - scalar6 * y0;
                     color0 = scalar15 - scalar7 * y0;
                     scalar13 -= scalar2 * y0;
                     scalar14 -= scalar3 * y0;
                     scalar15 -= scalar4 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x2 <<= 16;
                  red1 <<= 16;
                  green1 <<= 16;
                  color1 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope21 * y2;
                     red1 -= redSlope10 * y2;
                     green1 -= greenSlope10 * y2;
                     color1 -= colorSlope10 * y2;
                     y2 = 0;
                  }

                  if (y0 != y2 && xSlope02 < xSlope10 || y0 == y2 && xSlope21 > xSlope10) {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, x1 >> 16, scalar12 >> 16, red0, green0, color0, scalar13, scalar14, scalar15, depth0, yDelta10);
                        x1 += xSlope02;
                        scalar12 += xSlope10;
                        red0 += scalar5;
                        green0 += scalar6;
                        color0 += scalar7;
                        scalar13 += scalar2;
                        scalar14 += scalar3;
                        scalar15 += scalar4;
                        depth0 += xDelta10;
                     }

                     while (--y1 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, x2 >> 16, scalar12 >> 16, red1, green1, color1, scalar13, scalar14, scalar15, depth0, yDelta10);
                        x2 += xSlope21;
                        scalar12 += xSlope10;
                        red1 += redSlope10;
                        green1 += greenSlope10;
                        color1 += colorSlope10;
                        scalar13 += scalar2;
                        scalar14 += scalar3;
                        scalar15 += scalar4;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  } else {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, scalar12 >> 16, x1 >> 16, scalar13, scalar14, scalar15, red0, green0, color0, depth0, yDelta10);
                        x1 += xSlope02;
                        scalar12 += xSlope10;
                        red0 += scalar5;
                        green0 += scalar6;
                        color0 += scalar7;
                        scalar13 += scalar2;
                        scalar14 += scalar3;
                        scalar15 += scalar4;
                        depth0 += xDelta10;
                     }

                     while (--y1 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y0, scalar12 >> 16, x2 >> 16, scalar13, scalar14, scalar15, red1, green1, color1, depth0, yDelta10);
                        x2 += xSlope21;
                        scalar12 += xSlope10;
                        red1 += redSlope10;
                        green1 += greenSlope10;
                        color1 += colorSlope10;
                        scalar13 += scalar2;
                        scalar14 += scalar3;
                        scalar15 += scalar4;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  }
               }
            } else if (y1 <= y2) {
               if (y1 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta10 * x1 + yDelta10;
               if (y2 < y0) {
                  int scalar16;
                  x0 = scalar16 = x1 << 16;
                  int scalar17;
                  color2 = scalar17 = red0 << 16;
                  int scalar18;
                  scalar = scalar18 = green0 << 16;
                  int scalar19;
                  hSL_TO_RGBEntry = scalar19 = color0 << 16;
                  if (y1 < 0) {
                     x0 -= xSlope10 * y1;
                     scalar16 -= xSlope21 * y1;
                     color2 -= scalar2 * y1;
                     scalar -= scalar3 * y1;
                     hSL_TO_RGBEntry -= scalar4 * y1;
                     scalar17 -= redSlope10 * y1;
                     scalar18 -= greenSlope10 * y1;
                     scalar19 -= colorSlope10 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  red1 <<= 16;
                  green1 <<= 16;
                  color1 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope02 * y2;
                     red1 -= scalar5 * y2;
                     green1 -= scalar6 * y2;
                     color1 -= scalar7 * y2;
                     y2 = 0;
                  }

                  if (y1 != y2 && xSlope10 < xSlope21 || y1 == y2 && xSlope10 > xSlope02) {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, x0 >> 16, scalar16 >> 16, color2, scalar, hSL_TO_RGBEntry, scalar17, scalar18, scalar19, depth1, yDelta10);
                        x0 += xSlope10;
                        scalar16 += xSlope21;
                        color2 += scalar2;
                        scalar += scalar3;
                        hSL_TO_RGBEntry += scalar4;
                        scalar17 += redSlope10;
                        scalar18 += greenSlope10;
                        scalar19 += colorSlope10;
                        depth1 += xDelta10;
                     }

                     while (--y0 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, x0 >> 16, x2 >> 16, color2, scalar, hSL_TO_RGBEntry, red1, green1, color1, depth1, yDelta10);
                        x0 += xSlope10;
                        x2 += xSlope02;
                        color2 += scalar2;
                        scalar += scalar3;
                        hSL_TO_RGBEntry += scalar4;
                        red1 += scalar5;
                        green1 += scalar6;
                        color1 += scalar7;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  } else {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, scalar16 >> 16, x0 >> 16, scalar17, scalar18, scalar19, color2, scalar, hSL_TO_RGBEntry, depth1, yDelta10);
                        x0 += xSlope10;
                        scalar16 += xSlope21;
                        color2 += scalar2;
                        scalar += scalar3;
                        hSL_TO_RGBEntry += scalar4;
                        scalar17 += redSlope10;
                        scalar18 += greenSlope10;
                        scalar19 += colorSlope10;
                        depth1 += xDelta10;
                     }

                     while (--y0 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, x2 >> 16, x0 >> 16, red1, green1, color1, color2, scalar, hSL_TO_RGBEntry, depth1, yDelta10);
                        x0 += xSlope10;
                        x2 += xSlope02;
                        color2 += scalar2;
                        scalar += scalar3;
                        hSL_TO_RGBEntry += scalar4;
                        red1 += scalar5;
                        green1 += scalar6;
                        color1 += scalar7;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  }
               } else {
                  int scalar20;
                  x2 = scalar20 = x1 << 16;
                  int scalar21;
                  red1 = scalar21 = red0 << 16;
                  int scalar22;
                  green1 = scalar22 = green0 << 16;
                  int scalar23;
                  color1 = scalar23 = color0 << 16;
                  if (y1 < 0) {
                     x2 -= xSlope10 * y1;
                     scalar20 -= xSlope21 * y1;
                     red1 = scalar21 - scalar2 * y1;
                     green1 = scalar22 - scalar3 * y1;
                     color1 = scalar23 - scalar4 * y1;
                     scalar21 -= redSlope10 * y1;
                     scalar22 -= greenSlope10 * y1;
                     scalar23 -= colorSlope10 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x0 <<= 16;
                  color2 <<= 16;
                  scalar <<= 16;
                  hSL_TO_RGBEntry <<= 16;
                  if (y0 < 0) {
                     x0 -= xSlope02 * y0;
                     color2 -= scalar5 * y0;
                     scalar -= scalar6 * y0;
                     hSL_TO_RGBEntry -= scalar7 * y0;
                     y0 = 0;
                  }

                  if (xSlope10 < xSlope21) {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, x2 >> 16, scalar20 >> 16, red1, green1, color1, scalar21, scalar22, scalar23, depth1, yDelta10);
                        x2 += xSlope10;
                        scalar20 += xSlope21;
                        red1 += scalar2;
                        green1 += scalar3;
                        color1 += scalar4;
                        scalar21 += redSlope10;
                        scalar22 += greenSlope10;
                        scalar23 += colorSlope10;
                        depth1 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, x0 >> 16, scalar20 >> 16, color2, scalar, hSL_TO_RGBEntry, scalar21, scalar22, scalar23, depth1, yDelta10);
                        x0 += xSlope02;
                        scalar20 += xSlope21;
                        color2 += scalar5;
                        scalar += scalar6;
                        hSL_TO_RGBEntry += scalar7;
                        scalar21 += redSlope10;
                        scalar22 += greenSlope10;
                        scalar23 += colorSlope10;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  } else {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, scalar20 >> 16, x2 >> 16, scalar21, scalar22, scalar23, red1, green1, color1, depth1, yDelta10);
                        x2 += xSlope10;
                        scalar20 += xSlope21;
                        red1 += scalar2;
                        green1 += scalar3;
                        color1 += scalar4;
                        scalar21 += redSlope10;
                        scalar22 += greenSlope10;
                        scalar23 += colorSlope10;
                        depth1 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y1, scalar20 >> 16, x0 >> 16, scalar21, scalar22, scalar23, color2, scalar, hSL_TO_RGBEntry, depth1, yDelta10);
                        x0 += xSlope02;
                        scalar20 += xSlope21;
                        color2 += scalar5;
                        scalar += scalar6;
                        hSL_TO_RGBEntry += scalar7;
                        scalar21 += redSlope10;
                        scalar22 += greenSlope10;
                        scalar23 += colorSlope10;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }
                  }
               }
            } else {
               if (y2 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               depth2 = depth2 - yDelta10 * x2 + yDelta10;
               if (y0 < y1) {
                  int scalar24;
                  x1 = scalar24 = x2 << 16;
                  int scalar25;
                  red0 = scalar25 = red1 << 16;
                  int scalar26;
                  green0 = scalar26 = green1 << 16;
                  int scalar27;
                  color0 = scalar27 = color1 << 16;
                  if (y2 < 0) {
                     x1 -= xSlope21 * y2;
                     scalar24 -= xSlope02 * y2;
                     red0 -= redSlope10 * y2;
                     green0 -= greenSlope10 * y2;
                     color0 -= colorSlope10 * y2;
                     scalar25 -= scalar5 * y2;
                     scalar26 -= scalar6 * y2;
                     scalar27 -= scalar7 * y2;
                     depth2 -= xDelta10 * y2;
                     y2 = 0;
                  }

                  x0 <<= 16;
                  color2 <<= 16;
                  scalar <<= 16;
                  hSL_TO_RGBEntry <<= 16;
                  if (y0 < 0) {
                     x0 -= xSlope10 * y0;
                     color2 -= scalar2 * y0;
                     scalar -= scalar3 * y0;
                     hSL_TO_RGBEntry -= scalar4 * y0;
                     y0 = 0;
                  }

                  if (xSlope21 < xSlope02) {
                     y1 -= y0;
                     y0 -= y2;

                     for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, scalar24 >> 16, red0, green0, color0, scalar25, scalar26, scalar27, depth2, yDelta10);
                        x1 += xSlope21;
                        scalar24 += xSlope02;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        scalar25 += scalar5;
                        scalar26 += scalar6;
                        scalar27 += scalar7;
                        depth2 += xDelta10;
                     }

                     while (--y1 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, x0 >> 16, red0, green0, color0, color2, scalar, hSL_TO_RGBEntry, depth2, yDelta10);
                        x1 += xSlope21;
                        x0 += xSlope10;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        color2 += scalar2;
                        scalar += scalar3;
                        hSL_TO_RGBEntry += scalar4;
                        y2 += Rasterizer2D.width;
                        depth2 += xDelta10;
                     }
                  } else {
                     y1 -= y0;
                     y0 -= y2;

                     for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y2, scalar24 >> 16, x1 >> 16, scalar25, scalar26, scalar27, red0, green0, color0, depth2, yDelta10);
                        x1 += xSlope21;
                        scalar24 += xSlope02;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        scalar25 += scalar5;
                        scalar26 += scalar6;
                        scalar27 += scalar7;
                        depth2 += xDelta10;
                     }

                     while (--y1 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y2, x0 >> 16, x1 >> 16, color2, scalar, hSL_TO_RGBEntry, red0, green0, color0, depth2, yDelta10);
                        x1 += xSlope21;
                        x0 += xSlope10;
                        red0 += redSlope10;
                        green0 += greenSlope10;
                        color0 += colorSlope10;
                        color2 += scalar2;
                        scalar += scalar3;
                        hSL_TO_RGBEntry += scalar4;
                        depth2 += xDelta10;
                        y2 += Rasterizer2D.width;
                     }
                  }
               } else {
                  int scalar28;
                  x0 = scalar28 = x2 << 16;
                  int scalar29;
                  color2 = scalar29 = red1 << 16;
                  int scalar30;
                  scalar = scalar30 = green1 << 16;
                  int scalar31;
                  hSL_TO_RGBEntry = scalar31 = color1 << 16;
                  if (y2 < 0) {
                     x0 -= xSlope21 * y2;
                     scalar28 -= xSlope02 * y2;
                     color2 -= redSlope10 * y2;
                     scalar -= greenSlope10 * y2;
                     hSL_TO_RGBEntry -= colorSlope10 * y2;
                     scalar29 -= scalar5 * y2;
                     scalar30 -= scalar6 * y2;
                     scalar31 -= scalar7 * y2;
                     depth2 -= xDelta10 * y2;
                     y2 = 0;
                  }

                  x1 <<= 16;
                  red0 <<= 16;
                  green0 <<= 16;
                  color0 <<= 16;
                  if (y1 < 0) {
                     x1 -= xSlope10 * y1;
                     red0 -= scalar2 * y1;
                     green0 -= scalar3 * y1;
                     color0 -= scalar4 * y1;
                     y1 = 0;
                  }

                  if (xSlope21 >= xSlope02) {
                     y0 -= y1;
                     y1 -= y2;

                     for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y2, scalar28 >> 16, x0 >> 16, scalar29, scalar30, scalar31, color2, scalar, hSL_TO_RGBEntry, depth2, yDelta10);
                        x0 += xSlope21;
                        scalar28 += xSlope02;
                        color2 += redSlope10;
                        scalar += greenSlope10;
                        hSL_TO_RGBEntry += colorSlope10;
                        scalar29 += scalar5;
                        scalar30 += scalar6;
                        scalar31 += scalar7;
                        depth2 += xDelta10;
                     }

                     while (--y0 >= 0) {
                        drawRgbShadedScanline(Rasterizer2D.pixels, y2, scalar28 >> 16, x1 >> 16, scalar29, scalar30, scalar31, red0, green0, color0, depth2, yDelta10);
                        x1 += xSlope10;
                        scalar28 += xSlope02;
                        red0 += scalar2;
                        green0 += scalar3;
                        color0 += scalar4;
                        scalar29 += scalar5;
                        scalar30 += scalar6;
                        scalar31 += scalar7;
                        y2 += Rasterizer2D.width;
                        depth2 += xDelta10;
                     }

                     return;
                  }

                  y0 -= y1;
                  y1 -= y2;

                  for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                     drawRgbShadedScanline(Rasterizer2D.pixels, y2, x0 >> 16, scalar28 >> 16, color2, scalar, hSL_TO_RGBEntry, scalar29, scalar30, scalar31, depth2, yDelta10);
                     x0 += xSlope21;
                     scalar28 += xSlope02;
                     color2 += redSlope10;
                     scalar += greenSlope10;
                     hSL_TO_RGBEntry += colorSlope10;
                     scalar29 += scalar5;
                     scalar30 += scalar6;
                     scalar31 += scalar7;
                     depth2 += xDelta10;
                  }

                  while (--y0 >= 0) {
                     drawRgbShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, scalar28 >> 16, red0, green0, color0, scalar29, scalar30, scalar31, depth2, yDelta10);
                     x1 += xSlope10;
                     scalar28 += xSlope02;
                     red0 += scalar2;
                     green0 += scalar3;
                     color0 += scalar4;
                     scalar29 += scalar5;
                     scalar30 += scalar6;
                     scalar31 += scalar7;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                  }
               }
            }
         }
      } else {
         drawShadedTriangleLegacy(y0, y1, y2, x0, x1, x2, color0, color1, color2, depth0, depth1, depth2);
      }
   }
   public static void drawFlatTriangle(int y0, int y1, int y2, int x0, int x1, int x2, int rgb, float depth0, float depth1, float depth2) {
      if (GpuRasterizer3D.drawFlatTriangle(y0, y1, y2, x0, x1, x2, rgb, depth0, depth1, depth2)) {
         return;
      }

      if (Client.smoothRendering) {
         if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
            int xSlope10 = 0;
            if (y1 != y0) {
               xSlope10 = (x1 - x0 << 16) / (y1 - y0);
            }

            int xSlope21 = 0;
            if (y2 != y1) {
               xSlope21 = (x2 - x1 << 16) / (y2 - y1);
            }

            int xSlope02 = 0;
            if (y2 != y0) {
               xSlope02 = (x0 - x2 << 16) / (y0 - y2);
            }

            float xDelta10 = x1 - x0;
            float yDelta10 = y1 - y0;
            float xDelta20 = x2 - x0;
            float yDelta20 = y2 - y0;
            float depthDelta10 = depth1 - depth0;
            float depthDelta20 = depth2 - depth0;
            float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
            yDelta10 = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
            xDelta10 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
            if (y0 <= y1 && y0 <= y2) {
               if (y0 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta10 * x0 + yDelta10;
               if (y1 < y2) {
                  int scalar;
                  x2 = scalar = x0 << 16;
                  if (y0 < 0) {
                     x2 -= xSlope02 * y0;
                     scalar -= xSlope10 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  if (y1 < 0) {
                     x1 -= xSlope21 * y1;
                     y1 = 0;
                  }

                  if (y0 != y1 && xSlope02 < xSlope10 || y0 == y1 && xSlope02 > xSlope21) {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, x2 >> 16, scalar >> 16, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar += xSlope10;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, x2 >> 16, x1 >> 16, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  } else {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, scalar >> 16, x2 >> 16, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar += xSlope10;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, x1 >> 16, x2 >> 16, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }
                  }
               } else {
                  int scalar2;
                  x1 = scalar2 = x0 << 16;
                  if (y0 < 0) {
                     x1 -= xSlope02 * y0;
                     scalar2 -= xSlope10 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x2 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope21 * y2;
                     y2 = 0;
                  }

                  if (y0 != y2 && xSlope02 < xSlope10 || y0 == y2 && xSlope21 > xSlope10) {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, x1 >> 16, scalar2 >> 16, depth0, yDelta10);
                        depth0 += xDelta10;
                        x1 += xSlope02;
                        scalar2 += xSlope10;
                     }

                     while (--y1 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, x2 >> 16, scalar2 >> 16, depth0, yDelta10);
                        depth0 += xDelta10;
                        x2 += xSlope21;
                        scalar2 += xSlope10;
                        y0 += Rasterizer2D.width;
                     }
                  } else {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, scalar2 >> 16, x1 >> 16, depth0, yDelta10);
                        depth0 += xDelta10;
                        x1 += xSlope02;
                        scalar2 += xSlope10;
                     }

                     while (--y1 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y0, rgb, scalar2 >> 16, x2 >> 16, depth0, yDelta10);
                        depth0 += xDelta10;
                        x2 += xSlope21;
                        scalar2 += xSlope10;
                        y0 += Rasterizer2D.width;
                     }
                  }
               }
            } else if (y1 <= y2) {
               if (y1 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta10 * x1 + yDelta10;
               if (y2 < y0) {
                  int scalar3;
                  x0 = scalar3 = x1 << 16;
                  if (y1 < 0) {
                     x0 -= xSlope10 * y1;
                     scalar3 -= xSlope21 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope02 * y2;
                     y2 = 0;
                  }

                  if (y1 != y2 && xSlope10 < xSlope21 || y1 == y2 && xSlope10 > xSlope02) {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, x0 >> 16, scalar3 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x0 += xSlope10;
                        scalar3 += xSlope21;
                     }

                     while (--y0 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, x0 >> 16, x2 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x0 += xSlope10;
                        x2 += xSlope02;
                        y1 += Rasterizer2D.width;
                     }
                  } else {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, scalar3 >> 16, x0 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x0 += xSlope10;
                        scalar3 += xSlope21;
                     }

                     while (--y0 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, x2 >> 16, x0 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x0 += xSlope10;
                        x2 += xSlope02;
                        y1 += Rasterizer2D.width;
                     }
                  }
               } else {
                  int scalar4;
                  x2 = scalar4 = x1 << 16;
                  if (y1 < 0) {
                     x2 -= xSlope10 * y1;
                     scalar4 -= xSlope21 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x0 <<= 16;
                  if (y0 < 0) {
                     x0 -= xSlope02 * y0;
                     y0 = 0;
                  }

                  if (xSlope10 < xSlope21) {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, x2 >> 16, scalar4 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x2 += xSlope10;
                        scalar4 += xSlope21;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, x0 >> 16, scalar4 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x0 += xSlope02;
                        scalar4 += xSlope21;
                        y1 += Rasterizer2D.width;
                     }
                  } else {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, scalar4 >> 16, x2 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x2 += xSlope10;
                        scalar4 += xSlope21;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y1, rgb, scalar4 >> 16, x0 >> 16, depth1, yDelta10);
                        depth1 += xDelta10;
                        x0 += xSlope02;
                        scalar4 += xSlope21;
                        y1 += Rasterizer2D.width;
                     }
                  }
               }
            } else {
               if (y2 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               depth2 = depth2 - yDelta10 * x2 + yDelta10;
               if (y0 < y1) {
                  int scalar5;
                  x1 = scalar5 = x2 << 16;
                  if (y2 < 0) {
                     x1 -= xSlope21 * y2;
                     scalar5 -= xSlope02 * y2;
                     depth2 -= xDelta10 * y2;
                     y2 = 0;
                  }

                  x0 <<= 16;
                  if (y0 < 0) {
                     x0 -= xSlope10 * y0;
                     y0 = 0;
                  }

                  if (xSlope21 < xSlope02) {
                     y1 -= y0;
                     y0 -= y2;

                     for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, x1 >> 16, scalar5 >> 16, depth2, yDelta10);
                        depth2 += xDelta10;
                        x1 += xSlope21;
                        scalar5 += xSlope02;
                     }

                     while (--y1 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x0 >> 16, depth2, yDelta10);
                        depth2 += xDelta10;
                        x1 += xSlope21;
                        x0 += xSlope10;
                        y2 += Rasterizer2D.width;
                     }
                  } else {
                     y1 -= y0;
                     y0 -= y2;

                     for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, scalar5 >> 16, x1 >> 16, depth2, yDelta10);
                        depth2 += xDelta10;
                        x1 += xSlope21;
                        scalar5 += xSlope02;
                     }

                     while (--y1 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, x0 >> 16, x1 >> 16, depth2, yDelta10);
                        depth2 += xDelta10;
                        x1 += xSlope21;
                        x0 += xSlope10;
                        y2 += Rasterizer2D.width;
                     }
                  }
               } else {
                  int scalar6;
                  x0 = scalar6 = x2 << 16;
                  if (y2 < 0) {
                     x0 -= xSlope21 * y2;
                     scalar6 -= xSlope02 * y2;
                     depth2 -= xDelta10 * y2;
                     y2 = 0;
                  }

                  x1 <<= 16;
                  if (y1 < 0) {
                     x1 -= xSlope10 * y1;
                     y1 = 0;
                  }

                  if (xSlope21 >= xSlope02) {
                     y0 -= y1;
                     y1 -= y2;

                     for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, scalar6 >> 16, x0 >> 16, depth2, yDelta10);
                        depth2 += xDelta10;
                        x0 += xSlope21;
                        scalar6 += xSlope02;
                     }

                     while (--y0 >= 0) {
                        drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, scalar6 >> 16, x1 >> 16, depth2, yDelta10);
                        depth2 += xDelta10;
                        x1 += xSlope10;
                        scalar6 += xSlope02;
                        y2 += Rasterizer2D.width;
                     }

                     return;
                  }

                  y0 -= y1;
                  y1 -= y2;

                  for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                     drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, x0 >> 16, scalar6 >> 16, depth2, yDelta10);
                     depth2 += xDelta10;
                     x0 += xSlope21;
                     scalar6 += xSlope02;
                  }

                  while (--y0 >= 0) {
                     drawFlatScanlineSmooth(Rasterizer2D.pixels, y2, rgb, x1 >> 16, scalar6 >> 16, depth2, yDelta10);
                     depth2 += xDelta10;
                     x1 += xSlope10;
                     scalar6 += xSlope02;
                     y2 += Rasterizer2D.width;
                  }
               }
            }
         }
      } else {
         if (depth0 < 0.0F || depth1 < 0.0F || depth2 < 0.0F) {
            return;
         }

         int xSlope102 = 0;
         if (y1 != y0) {
            xSlope102 = (x1 - x0 << 16) / (y1 - y0);
         }

         int xSlope212 = 0;
         if (y2 != y1) {
            xSlope212 = (x2 - x1 << 16) / (y2 - y1);
         }

         int xSlope022 = 0;
         if (y2 != y0) {
            xSlope022 = (x0 - x2 << 16) / (y0 - y2);
         }

         float xDelta102 = x1 - x0;
         float yDelta102 = y1 - y0;
         float xDelta202 = x2 - x0;
         float yDelta202 = y2 - y0;
         float depthDelta102 = depth1 - depth0;
         float depthDelta202 = depth2 - depth0;
         float interpolant2 = xDelta102 * yDelta202 - xDelta202 * yDelta102;
         yDelta102 = (depthDelta102 * yDelta202 - depthDelta202 * yDelta102) / interpolant2;
         xDelta102 = (depthDelta202 * xDelta102 - depthDelta102 * xDelta202) / interpolant2;
         if (y0 <= y1 && y0 <= y2) {
            if (y0 < Rasterizer2D.bottomY) {
               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta102 * x0 + yDelta102;
               if (y1 < y2) {
                  int scalar7;
                  x2 = scalar7 = x0 << 16;
                  if (y0 < 0) {
                     x2 -= xSlope022 * y0;
                     scalar7 -= xSlope102 * y0;
                     depth0 -= xDelta102 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  if (y1 < 0) {
                     x1 -= xSlope212 * y1;
                     y1 = 0;
                  }

                  if ((y0 == y1 || xSlope022 >= xSlope102) && (y0 != y1 || xSlope022 <= xSlope212)) {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, scalar7 >> 16, x2 >> 16, depth0, yDelta102);
                        x2 += xSlope022;
                        scalar7 += xSlope102;
                        depth0 += xDelta102;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, x1 >> 16, x2 >> 16, depth0, yDelta102);
                        x2 += xSlope022;
                        x1 += xSlope212;
                        depth0 += xDelta102;
                        y0 += Rasterizer2D.width;
                     }
                  } else {
                     y2 -= y1;
                     y1 -= y0;

                     for (y0 = scanOffsets[y0]; --y1 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, x2 >> 16, scalar7 >> 16, depth0, yDelta102);
                        x2 += xSlope022;
                        scalar7 += xSlope102;
                        depth0 += xDelta102;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, x2 >> 16, x1 >> 16, depth0, yDelta102);
                        x2 += xSlope022;
                        x1 += xSlope212;
                        depth0 += xDelta102;
                        y0 += Rasterizer2D.width;
                     }
                  }
               } else {
                  int scalar8;
                  x1 = scalar8 = x0 << 16;
                  if (y0 < 0) {
                     x1 -= xSlope022 * y0;
                     scalar8 -= xSlope102 * y0;
                     depth0 -= xDelta102 * y0;
                     y0 = 0;
                  }

                  x2 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope212 * y2;
                     y2 = 0;
                  }

                  if ((y0 == y2 || xSlope022 >= xSlope102) && (y0 != y2 || xSlope212 <= xSlope102)) {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, scalar8 >> 16, x1 >> 16, depth0, yDelta102);
                        depth0 += xDelta102;
                        x1 += xSlope022;
                        scalar8 += xSlope102;
                     }

                     while (--y1 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, scalar8 >> 16, x2 >> 16, depth0, yDelta102);
                        depth0 += xDelta102;
                        x2 += xSlope212;
                        scalar8 += xSlope102;
                        y0 += Rasterizer2D.width;
                     }
                  } else {
                     y1 -= y2;
                     y2 -= y0;

                     for (y0 = scanOffsets[y0]; --y2 >= 0; y0 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, x1 >> 16, scalar8 >> 16, depth0, yDelta102);
                        depth0 += xDelta102;
                        x1 += xSlope022;
                        scalar8 += xSlope102;
                     }

                     while (--y1 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y0, rgb, x2 >> 16, scalar8 >> 16, depth0, yDelta102);
                        depth0 += xDelta102;
                        x2 += xSlope212;
                        scalar8 += xSlope102;
                        y0 += Rasterizer2D.width;
                     }
                  }
               }
            }
         } else if (y1 <= y2) {
            if (y1 < Rasterizer2D.bottomY) {
               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta102 * x1 + yDelta102;
               if (y2 < y0) {
                  int scalar9;
                  x0 = scalar9 = x1 << 16;
                  if (y1 < 0) {
                     x0 -= xSlope102 * y1;
                     scalar9 -= xSlope212 * y1;
                     depth1 -= xDelta102 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  if (y2 < 0) {
                     x2 -= xSlope022 * y2;
                     y2 = 0;
                  }

                  if ((y1 == y2 || xSlope102 >= xSlope212) && (y1 != y2 || xSlope102 <= xSlope022)) {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, scalar9 >> 16, x0 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x0 += xSlope102;
                        scalar9 += xSlope212;
                     }

                     while (--y0 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, x2 >> 16, x0 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x0 += xSlope102;
                        x2 += xSlope022;
                        y1 += Rasterizer2D.width;
                     }
                  } else {
                     y0 -= y2;
                     y2 -= y1;

                     for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, x0 >> 16, scalar9 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x0 += xSlope102;
                        scalar9 += xSlope212;
                     }

                     while (--y0 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, x0 >> 16, x2 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x0 += xSlope102;
                        x2 += xSlope022;
                        y1 += Rasterizer2D.width;
                     }
                  }
               } else {
                  int scalar10;
                  x2 = scalar10 = x1 << 16;
                  if (y1 < 0) {
                     x2 -= xSlope102 * y1;
                     scalar10 -= xSlope212 * y1;
                     depth1 -= xDelta102 * y1;
                     y1 = 0;
                  }

                  x0 <<= 16;
                  if (y0 < 0) {
                     x0 -= xSlope022 * y0;
                     y0 = 0;
                  }

                  if (xSlope102 < xSlope212) {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, x2 >> 16, scalar10 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x2 += xSlope102;
                        scalar10 += xSlope212;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, x0 >> 16, scalar10 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x0 += xSlope022;
                        scalar10 += xSlope212;
                        y1 += Rasterizer2D.width;
                     }
                  } else {
                     y2 -= y0;
                     y0 -= y1;

                     for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, scalar10 >> 16, x2 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x2 += xSlope102;
                        scalar10 += xSlope212;
                     }

                     while (--y2 >= 0) {
                        drawFlatScanlineLegacy(Rasterizer2D.pixels, y1, rgb, scalar10 >> 16, x0 >> 16, depth1, yDelta102);
                        depth1 += xDelta102;
                        x0 += xSlope022;
                        scalar10 += xSlope212;
                        y1 += Rasterizer2D.width;
                     }
                  }
               }
            }
         } else if (y2 < Rasterizer2D.bottomY) {
            if (y0 > Rasterizer2D.bottomY) {
               y0 = Rasterizer2D.bottomY;
            }

            if (y1 > Rasterizer2D.bottomY) {
               y1 = Rasterizer2D.bottomY;
            }

            depth2 = depth2 - yDelta102 * x2 + yDelta102;
            if (y0 < y1) {
               int scalar11;
               x1 = scalar11 = x2 << 16;
               if (y2 < 0) {
                  x1 -= xSlope212 * y2;
                  scalar11 -= xSlope022 * y2;
                  depth2 -= xDelta102 * y2;
                  y2 = 0;
               }

               x0 <<= 16;
               if (y0 < 0) {
                  x0 -= xSlope102 * y0;
                  y0 = 0;
               }

               if (xSlope212 < xSlope022) {
                  y1 -= y0;
                  y0 -= y2;

                  for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, x1 >> 16, scalar11 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x1 += xSlope212;
                     scalar11 += xSlope022;
                  }

                  while (--y1 >= 0) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x0 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x1 += xSlope212;
                     x0 += xSlope102;
                     y2 += Rasterizer2D.width;
                  }
               } else {
                  y1 -= y0;
                  y0 -= y2;

                  for (y2 = scanOffsets[y2]; --y0 >= 0; y2 += Rasterizer2D.width) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, scalar11 >> 16, x1 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x1 += xSlope212;
                     scalar11 += xSlope022;
                  }

                  while (--y1 >= 0) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, x0 >> 16, x1 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x1 += xSlope212;
                     x0 += xSlope102;
                     y2 += Rasterizer2D.width;
                  }
               }
            } else {
               int scalar12;
               x0 = scalar12 = x2 << 16;
               if (y2 < 0) {
                  x0 -= xSlope212 * y2;
                  scalar12 -= xSlope022 * y2;
                  depth2 -= xDelta102 * y2;
                  y2 = 0;
               }

               x1 <<= 16;
               if (y1 < 0) {
                  x1 -= xSlope102 * y1;
                  y1 = 0;
               }

               if (xSlope212 < xSlope022) {
                  y0 -= y1;
                  y1 -= y2;

                  for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, x0 >> 16, scalar12 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x0 += xSlope212;
                     scalar12 += xSlope022;
                  }

                  while (--y0 >= 0) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, x1 >> 16, scalar12 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x1 += xSlope102;
                     scalar12 += xSlope022;
                     y2 += Rasterizer2D.width;
                  }
               } else {
                  y0 -= y1;
                  y1 -= y2;

                  for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, scalar12 >> 16, x0 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x0 += xSlope212;
                     scalar12 += xSlope022;
                  }

                  while (--y0 >= 0) {
                     drawFlatScanlineLegacy(Rasterizer2D.pixels, y2, rgb, scalar12 >> 16, x1 >> 16, depth2, yDelta102);
                     depth2 += xDelta102;
                     x1 += xSlope102;
                     scalar12 += xSlope022;
                     y2 += Rasterizer2D.width;
                  }
               }
            }
         }
      }
   }
   public static void drawTexturedTriangle(
      boolean useFallback,
      int y0,
      int y1,
      int y2,
      int x0,
      int x1,
      int x2,
      int shade0,
      int shade1,
      int shade2,
      int textureX0,
      int textureX1,
      int textureX2,
      int textureY0,
      int textureY1,
      int textureY2,
      int textureZ0,
      int textureZ1,
      int textureZ2,
      int textureId,
      float depth0,
      float depth1,
      float depth2
   ) {
      if (GpuRasterizer3D.drawTexturedTriangle(
         useFallback,
         y0,
         y1,
         y2,
         x0,
         x1,
         x2,
         shade0,
         shade1,
         shade2,
         textureX0,
         textureX1,
         textureX2,
         textureY0,
         textureY1,
         textureY2,
         textureZ0,
         textureZ1,
         textureZ2,
         textureId,
         depth0,
         depth1,
         depth2
      )) {
         return;
      }

      if (Client.smoothRendering) {
         if (smoothShading && renderModeFlag && !useFallback) {
            drawSmoothTexturedTriangle(
               y0, y1, y2, x0, x1, x2, shade0, shade1, shade2, textureX0, textureX1, textureX2, textureY0, textureY1, textureY2, textureZ0, textureZ1, textureZ2, textureId, depth0, depth1, depth2
            );
         } else if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
            int[] texturePixelData = getTexturePixels(textureId);
            textureOpaque = !textureHasTransparency[textureId];
            textureX1 = textureX0 - textureX1;
            textureY1 = textureY0 - textureY1;
            textureZ1 = textureZ0 - textureZ1;
            textureX2 -= textureX0;
            textureY2 -= textureY0;
            textureZ2 -= textureZ0;
            textureId = Client.getProjectionScaleShift() + 5;
            if (!renderModeFlag) {
               textureId = 14;
            }

            long textureULong = ((long)textureX2 * textureY0 - (long)textureY2 * textureX0) << textureId;
            long textureUSlopeLong = ((long)textureY2 * textureZ0 - (long)textureZ2 * textureY0) << 8;
            long scalarLong = ((long)textureZ2 * textureX0 - (long)textureX2 * textureZ0) << 5;
            long textureVLong = ((long)textureX1 * textureY0 - (long)textureY1 * textureX0) << textureId;
            long textureVSlopeLong = ((long)textureY1 * textureZ0 - (long)textureZ1 * textureY0) << 8;
            long textureVStepLong = ((long)textureZ1 * textureX0 - (long)textureX1 * textureZ0) << 5;
            long textureWLong = ((long)textureY1 * textureX2 - (long)textureX1 * textureY2) << textureId;
            long textureWSlopeLong = ((long)textureZ1 * textureY2 - (long)textureY1 * textureZ2) << 8;
            long textureWStepLong = ((long)textureX1 * textureZ2 - (long)textureZ1 * textureX2) << 5;
            int coefficientShift = textureCoefficientNormalizationShift(
               textureULong,
               textureUSlopeLong,
               scalarLong,
               textureVLong,
               textureVSlopeLong,
               textureVStepLong,
               textureWLong,
               textureWSlopeLong,
               textureWStepLong
            );
            int textureU = (int)(textureULong >> coefficientShift);
            int textureUSlope = (int)(textureUSlopeLong >> coefficientShift);
            int scalar = (int)(scalarLong >> coefficientShift);
            int textureV = (int)(textureVLong >> coefficientShift);
            textureY0 = (int)(textureVSlopeLong >> coefficientShift);
            textureX0 = (int)(textureVStepLong >> coefficientShift);
            textureZ0 = (int)(textureWLong >> coefficientShift);
            textureY1 = (int)(textureWSlopeLong >> coefficientShift);
            textureX1 = (int)(textureWStepLong >> coefficientShift);
            textureX2 = 0;
            textureY2 = 0;
            if (y1 != y0) {
               textureX2 = (x1 - x0 << 16) / (y1 - y0);
               textureY2 = (shade1 - shade0 << 16) / (y1 - y0);
            }

            textureZ1 = 0;
            textureZ2 = 0;
            if (y2 != y1) {
               textureZ1 = (x2 - x1 << 16) / (y2 - y1);
               textureZ2 = (shade2 - shade1 << 16) / (y2 - y1);
            }

            textureId = 0;
            int shadeSlope02 = 0;
            if (y2 != y0) {
               textureId = (x0 - x2 << 16) / (y0 - y2);
               shadeSlope02 = (shade0 - shade2 << 16) / (y0 - y2);
            }

            float xDelta10 = x1 - x0;
            float yDelta10 = y1 - y0;
            float xDelta20 = x2 - x0;
            float yDelta20 = y2 - y0;
            float depthDelta10 = depth1 - depth0;
            float depthDelta20 = depth2 - depth0;
            float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
            float depthSlope = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
            float interpolant2 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
            if (y0 <= y1 && y0 <= y2) {
               if (y0 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - depthSlope * x0 + depthSlope;
               if (y1 < y2) {
                  int scalar2;
                  x2 = scalar2 = x0 << 16;
                  int scalar3;
                  shade2 = scalar3 = shade0 << 16;
                  if (y0 < 0) {
                     x2 -= textureId * y0;
                     scalar2 -= textureX2 * y0;
                     depth0 -= interpolant2 * y0;
                     shade2 -= shadeSlope02 * y0;
                     scalar3 -= textureY2 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  shade1 <<= 16;
                  if (y1 < 0) {
                     x1 -= textureZ1 * y1;
                     shade1 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  int scalar4 = y0 - viewportCenterY;
                  textureU += scalar * scalar4;
                  textureV += textureX0 * scalar4;
                  textureZ0 += textureX1 * scalar4;
                  if (y0 != y1 && textureId < textureX2 || y0 == y1 && textureId > textureZ1) {
                     y2 -= y1;
                     y1 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           x2 >> 16,
                           scalar2 >> 16,
                           shade2 >> 8,
                           scalar3 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x2 += textureId;
                        scalar2 += textureX2;
                        depth0 += interpolant2;
                        shade2 += shadeSlope02;
                        scalar3 += textureY2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           x2 >> 16,
                           x1 >> 16,
                           shade2 >> 8,
                           shade1 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x2 += textureId;
                        x1 += textureZ1;
                        depth0 += interpolant2;
                        shade2 += shadeSlope02;
                        shade1 += textureZ2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y2 -= y1;
                     y1 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           scalar2 >> 16,
                           x2 >> 16,
                           scalar3 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x2 += textureId;
                        scalar2 += textureX2;
                        depth0 += interpolant2;
                        shade2 += shadeSlope02;
                        scalar3 += textureY2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           x1 >> 16,
                           x2 >> 16,
                           shade1 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x2 += textureId;
                        x1 += textureZ1;
                        depth0 += interpolant2;
                        shade2 += shadeSlope02;
                        shade1 += textureZ2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               } else {
                  int scalar5;
                  x1 = scalar5 = x0 << 16;
                  int scalar6;
                  shade1 = scalar6 = shade0 << 16;
                  if (y0 < 0) {
                     x1 -= textureId * y0;
                     scalar5 -= textureX2 * y0;
                     depth0 -= interpolant2 * y0;
                     shade1 -= shadeSlope02 * y0;
                     scalar6 -= textureY2 * y0;
                     y0 = 0;
                  }

                  x2 <<= 16;
                  shade2 <<= 16;
                  if (y2 < 0) {
                     x2 -= textureZ1 * y2;
                     shade2 -= textureZ2 * y2;
                     y2 = 0;
                  }

                  int scalar7 = y0 - viewportCenterY;
                  textureU += scalar * scalar7;
                  textureV += textureX0 * scalar7;
                  textureZ0 += textureX1 * scalar7;
                  if (y0 != y2 && textureId < textureX2 || y0 == y2 && textureZ1 > textureX2) {
                     y1 -= y2;
                     y2 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           x1 >> 16,
                           scalar5 >> 16,
                           shade1 >> 8,
                           scalar6 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x1 += textureId;
                        scalar5 += textureX2;
                        shade1 += shadeSlope02;
                        scalar6 += textureY2;
                        depth0 += interpolant2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           x2 >> 16,
                           scalar5 >> 16,
                           shade2 >> 8,
                           scalar6 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x2 += textureZ1;
                        scalar5 += textureX2;
                        shade2 += textureZ2;
                        scalar6 += textureY2;
                        depth0 += interpolant2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y1 -= y2;
                     y2 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           scalar5 >> 16,
                           x1 >> 16,
                           scalar6 >> 8,
                           shade1 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x1 += textureId;
                        scalar5 += textureX2;
                        shade1 += shadeSlope02;
                        scalar6 += textureY2;
                        depth0 += interpolant2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           scalar5 >> 16,
                           x2 >> 16,
                           scalar6 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           depthSlope
                        );
                        x2 += textureZ1;
                        scalar5 += textureX2;
                        shade2 += textureZ2;
                        scalar6 += textureY2;
                        depth0 += interpolant2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               }
            } else if (y1 <= y2) {
               if (y1 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - depthSlope * x1 + depthSlope;
               if (y2 < y0) {
                  int scalar8;
                  x0 = scalar8 = x1 << 16;
                  int scalar9;
                  shade0 = scalar9 = shade1 << 16;
                  if (y1 < 0) {
                     x0 -= textureX2 * y1;
                     scalar8 -= textureZ1 * y1;
                     depth1 -= interpolant2 * y1;
                     shade0 -= textureY2 * y1;
                     scalar9 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  shade2 <<= 16;
                  if (y2 < 0) {
                     x2 -= textureId * y2;
                     shade2 -= shadeSlope02 * y2;
                     y2 = 0;
                  }

                  int scalar10 = y1 - viewportCenterY;
                  textureU += scalar * scalar10;
                  textureV += textureX0 * scalar10;
                  textureZ0 += textureX1 * scalar10;
                  if (y1 != y2 && textureX2 < textureZ1 || y1 == y2 && textureX2 > textureId) {
                     y0 -= y2;
                     y2 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           x0 >> 16,
                           scalar8 >> 16,
                           shade0 >> 8,
                           scalar9 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x0 += textureX2;
                        scalar8 += textureZ1;
                        shade0 += textureY2;
                        scalar9 += textureZ2;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           x0 >> 16,
                           x2 >> 16,
                           shade0 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x0 += textureX2;
                        x2 += textureId;
                        shade0 += textureY2;
                        shade2 += shadeSlope02;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y0 -= y2;
                     y2 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           scalar8 >> 16,
                           x0 >> 16,
                           scalar9 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x0 += textureX2;
                        scalar8 += textureZ1;
                        shade0 += textureY2;
                        scalar9 += textureZ2;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           x2 >> 16,
                           x0 >> 16,
                           shade2 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x0 += textureX2;
                        x2 += textureId;
                        shade0 += textureY2;
                        shade2 += shadeSlope02;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               } else {
                  int scalar11;
                  x2 = scalar11 = x1 << 16;
                  int scalar12;
                  shade2 = scalar12 = shade1 << 16;
                  if (y1 < 0) {
                     x2 -= textureX2 * y1;
                     scalar11 -= textureZ1 * y1;
                     depth1 -= interpolant2 * y1;
                     shade2 -= textureY2 * y1;
                     scalar12 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  x0 <<= 16;
                  shade0 <<= 16;
                  if (y0 < 0) {
                     x0 -= textureId * y0;
                     shade0 -= shadeSlope02 * y0;
                     y0 = 0;
                  }

                  int scalar13 = y1 - viewportCenterY;
                  textureU += scalar * scalar13;
                  textureV += textureX0 * scalar13;
                  textureZ0 += textureX1 * scalar13;
                  if (textureX2 < textureZ1) {
                     y2 -= y0;
                     y0 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           x2 >> 16,
                           scalar11 >> 16,
                           shade2 >> 8,
                           scalar12 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x2 += textureX2;
                        scalar11 += textureZ1;
                        shade2 += textureY2;
                        scalar12 += textureZ2;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           x0 >> 16,
                           scalar11 >> 16,
                           shade0 >> 8,
                           scalar12 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x0 += textureId;
                        scalar11 += textureZ1;
                        shade0 += shadeSlope02;
                        scalar12 += textureZ2;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y2 -= y0;
                     y0 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           scalar11 >> 16,
                           x2 >> 16,
                           scalar12 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x2 += textureX2;
                        scalar11 += textureZ1;
                        shade2 += textureY2;
                        scalar12 += textureZ2;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           scalar11 >> 16,
                           x0 >> 16,
                           scalar12 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           depthSlope
                        );
                        x0 += textureId;
                        scalar11 += textureZ1;
                        shade0 += shadeSlope02;
                        scalar12 += textureZ2;
                        depth1 += interpolant2;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               }
            } else {
               if (y2 >= Rasterizer2D.bottomY) {
                  return;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               depth2 = depth2 - depthSlope * x2 + depthSlope;
               if (y0 < y1) {
                  int scalar14;
                  x1 = scalar14 = x2 << 16;
                  int scalar15;
                  shade1 = scalar15 = shade2 << 16;
                  if (y2 < 0) {
                     x1 -= textureZ1 * y2;
                     scalar14 -= textureId * y2;
                     depth2 -= interpolant2 * y2;
                     shade1 -= textureZ2 * y2;
                     scalar15 -= shadeSlope02 * y2;
                     y2 = 0;
                  }

                  x0 <<= 16;
                  shade0 <<= 16;
                  if (y0 < 0) {
                     x0 -= textureX2 * y0;
                     shade0 -= textureY2 * y0;
                     y0 = 0;
                  }

                  int scalar16 = y2 - viewportCenterY;
                  textureU += scalar * scalar16;
                  textureV += textureX0 * scalar16;
                  textureZ0 += textureX1 * scalar16;
                  if (textureZ1 < textureId) {
                     y1 -= y0;
                     y0 -= y2;
                     y2 = scanOffsets[y2];

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y2,
                           x1 >> 16,
                           scalar14 >> 16,
                           shade1 >> 8,
                           scalar15 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth2,
                           depthSlope
                        );
                        x1 += textureZ1;
                        scalar14 += textureId;
                        shade1 += textureZ2;
                        scalar15 += shadeSlope02;
                        depth2 += interpolant2;
                        y2 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y2,
                           x1 >> 16,
                           x0 >> 16,
                           shade1 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth2,
                           depthSlope
                        );
                        x1 += textureZ1;
                        x0 += textureX2;
                        shade1 += textureZ2;
                        shade0 += textureY2;
                        depth2 += interpolant2;
                        y2 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  } else {
                     y1 -= y0;
                     y0 -= y2;
                     y2 = scanOffsets[y2];

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y2,
                           scalar14 >> 16,
                           x1 >> 16,
                           scalar15 >> 8,
                           shade1 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth2,
                           depthSlope
                        );
                        x1 += textureZ1;
                        scalar14 += textureId;
                        shade1 += textureZ2;
                        scalar15 += shadeSlope02;
                        depth2 += interpolant2;
                        y2 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y2,
                           x0 >> 16,
                           x1 >> 16,
                           shade0 >> 8,
                           shade1 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth2,
                           depthSlope
                        );
                        x1 += textureZ1;
                        x0 += textureX2;
                        shade1 += textureZ2;
                        shade0 += textureY2;
                        depth2 += interpolant2;
                        y2 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }
                  }
               } else {
                  int scalar17;
                  x0 = scalar17 = x2 << 16;
                  int scalar18;
                  shade0 = scalar18 = shade2 << 16;
                  if (y2 < 0) {
                     x0 -= textureZ1 * y2;
                     scalar17 -= textureId * y2;
                     depth2 -= interpolant2 * y2;
                     shade0 -= textureZ2 * y2;
                     scalar18 -= shadeSlope02 * y2;
                     y2 = 0;
                  }

                  x1 <<= 16;
                  shade1 <<= 16;
                  if (y1 < 0) {
                     x1 -= textureX2 * y1;
                     shade1 -= textureY2 * y1;
                     y1 = 0;
                  }

                  int scalar19 = y2 - viewportCenterY;
                  textureU += scalar * scalar19;
                  textureV += textureX0 * scalar19;
                  textureZ0 += textureX1 * scalar19;
                  if (textureZ1 >= textureId) {
                     y0 -= y1;
                     y1 -= y2;
                     y2 = scanOffsets[y2];

                     while (--y1 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y2,
                           scalar17 >> 16,
                           x0 >> 16,
                           scalar18 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth2,
                           depthSlope
                        );
                        x0 += textureZ1;
                        scalar17 += textureId;
                        shade0 += textureZ2;
                        scalar18 += shadeSlope02;
                        depth2 += interpolant2;
                        y2 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y0 >= 0) {
                        drawTexturedScanline(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y2,
                           scalar17 >> 16,
                           x1 >> 16,
                           scalar18 >> 8,
                           shade1 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth2,
                           depthSlope
                        );
                        x1 += textureX2;
                        scalar17 += textureId;
                        shade1 += textureY2;
                        scalar18 += shadeSlope02;
                        depth2 += interpolant2;
                        y2 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     return;
                  }

                  y0 -= y1;
                  y1 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y1 >= 0) {
                     drawTexturedScanline(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y2,
                        x0 >> 16,
                        scalar17 >> 16,
                        shade0 >> 8,
                        scalar18 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth2,
                        depthSlope
                     );
                     x0 += textureZ1;
                     scalar17 += textureId;
                     shade0 += textureZ2;
                     scalar18 += shadeSlope02;
                     depth2 += interpolant2;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y0 >= 0) {
                     drawTexturedScanline(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y2,
                        x1 >> 16,
                        scalar17 >> 16,
                        shade1 >> 8,
                        scalar18 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth2,
                        depthSlope
                     );
                     x1 += textureX2;
                     scalar17 += textureId;
                     shade1 += textureY2;
                     scalar18 += shadeSlope02;
                     depth2 += interpolant2;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }
               }
            }
         }
      } else {
         drawTexturedTriangleLegacy(
            y0, y1, y2, x0, x1, x2, shade0, shade1, shade2, textureX0, textureX1, textureX2, textureY0, textureY1, textureY2, textureZ0, textureZ1, textureZ2, textureId, depth0, depth1, depth2
         );
      }
   }
   private static void drawShadedTriangleLegacy(
      int y0, int y1, int y2, int x0, int x1, int x2, int color0, int color1, int color2, float depth0, float depth1, float depth2
   ) {
      if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
         int xSlope10 = 0;
         int colorSlope10 = 0;
         if (y1 != y0) {
            xSlope10 = (x1 - x0 << 16) / (y1 - y0);
            colorSlope10 = (color1 - color0 << 15) / (y1 - y0);
         }

         int xSlope21 = 0;
         int colorSlope21 = 0;
         if (y2 != y1) {
            xSlope21 = (x2 - x1 << 16) / (y2 - y1);
            colorSlope21 = (color2 - color1 << 15) / (y2 - y1);
         }

         int xSlope02 = 0;
         int colorSlope02 = 0;
         if (y2 != y0) {
            xSlope02 = (x0 - x2 << 16) / (y0 - y2);
            colorSlope02 = (color0 - color2 << 15) / (y0 - y2);
         }

         float xDelta10 = x1 - x0;
         float yDelta10 = y1 - y0;
         float xDelta20 = x2 - x0;
         float yDelta20 = y2 - y0;
         float depthDelta10 = depth1 - depth0;
         float depthDelta20 = depth2 - depth0;
         float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
         yDelta10 = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
         xDelta10 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
         if (y0 <= y1 && y0 <= y2) {
            if (y0 < Rasterizer2D.bottomY) {
               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta10 * x0 + yDelta10;
               if (y1 < y2) {
                  int scalar;
                  x2 = scalar = x0 << 16;
                  int scalar2;
                  color2 = scalar2 = color0 << 15;
                  if (y0 < 0) {
                     x2 -= xSlope02 * y0;
                     scalar -= xSlope10 * y0;
                     color2 -= colorSlope02 * y0;
                     scalar2 -= colorSlope10 * y0;
                     depth0 -= xDelta10 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  color1 <<= 15;
                  if (y1 < 0) {
                     x1 -= xSlope21 * y1;
                     color1 -= colorSlope21 * y1;
                     y1 = 0;
                  }

                  if ((y0 == y1 || xSlope02 >= xSlope10) && (y0 != y1 || xSlope02 <= xSlope21)) {
                     y2 -= y1;
                     y1 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y1 >= 0) {
                        drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, scalar >> 16, x2 >> 16, scalar2 >> 7, color2 >> 7, depth0, yDelta10);
                        x2 += xSlope02;
                        scalar += xSlope10;
                        color2 += colorSlope02;
                        scalar2 += colorSlope10;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }

                     while (--y2 >= 0) {
                        drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, x1 >> 16, x2 >> 16, color1 >> 7, color2 >> 7, depth0, yDelta10);
                        x2 += xSlope02;
                        x1 += xSlope21;
                        color2 += colorSlope02;
                        color1 += colorSlope21;
                        y0 += Rasterizer2D.width;
                        depth0 += xDelta10;
                     }

                     return;
                  }

                  y2 -= y1;
                  y1 -= y0;
                  y0 = scanOffsets[y0];

                  while (--y1 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, x2 >> 16, scalar >> 16, color2 >> 7, scalar2 >> 7, depth0, yDelta10);
                     x2 += xSlope02;
                     scalar += xSlope10;
                     color2 += colorSlope02;
                     scalar2 += colorSlope10;
                     y0 += Rasterizer2D.width;
                     depth0 += xDelta10;
                  }

                  while (--y2 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, x2 >> 16, x1 >> 16, color2 >> 7, color1 >> 7, depth0, yDelta10);
                     x2 += xSlope02;
                     x1 += xSlope21;
                     color2 += colorSlope02;
                     color1 += colorSlope21;
                     y0 += Rasterizer2D.width;
                     depth0 += xDelta10;
                  }

                  return;
               }

               int scalar3;
               x1 = scalar3 = x0 << 16;
               int scalar4;
               color1 = scalar4 = color0 << 15;
               if (y0 < 0) {
                  x1 -= xSlope02 * y0;
                  scalar3 -= xSlope10 * y0;
                  color1 -= colorSlope02 * y0;
                  scalar4 -= colorSlope10 * y0;
                  depth0 -= xDelta10 * y0;
                  y0 = 0;
               }

               x2 <<= 16;
               color2 <<= 15;
               if (y2 < 0) {
                  x2 -= xSlope21 * y2;
                  color2 -= colorSlope21 * y2;
                  y2 = 0;
               }

               if ((y0 == y2 || xSlope02 >= xSlope10) && (y0 != y2 || xSlope21 <= xSlope10)) {
                  y1 -= y2;
                  y2 -= y0;
                  y0 = scanOffsets[y0];

                  while (--y2 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, scalar3 >> 16, x1 >> 16, scalar4 >> 7, color1 >> 7, depth0, yDelta10);
                     x1 += xSlope02;
                     scalar3 += xSlope10;
                     color1 += colorSlope02;
                     scalar4 += colorSlope10;
                     y0 += Rasterizer2D.width;
                     depth0 += xDelta10;
                  }

                  while (--y1 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, scalar3 >> 16, x2 >> 16, scalar4 >> 7, color2 >> 7, depth0, yDelta10);
                     x2 += xSlope21;
                     scalar3 += xSlope10;
                     color2 += colorSlope21;
                     scalar4 += colorSlope10;
                     y0 += Rasterizer2D.width;
                     depth0 += xDelta10;
                  }

                  return;
               }

               y1 -= y2;
               y2 -= y0;
               y0 = scanOffsets[y0];

               while (--y2 >= 0) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, x1 >> 16, scalar3 >> 16, color1 >> 7, scalar4 >> 7, depth0, yDelta10);
                  x1 += xSlope02;
                  scalar3 += xSlope10;
                  color1 += colorSlope02;
                  scalar4 += colorSlope10;
                  y0 += Rasterizer2D.width;
                  depth0 += xDelta10;
               }

               while (--y1 >= 0) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y0, x2 >> 16, scalar3 >> 16, color2 >> 7, scalar4 >> 7, depth0, yDelta10);
                  x2 += xSlope21;
                  scalar3 += xSlope10;
                  color2 += colorSlope21;
                  scalar4 += colorSlope10;
                  y0 += Rasterizer2D.width;
                  depth0 += xDelta10;
               }

               return;
            }
         } else if (y1 <= y2) {
            if (y1 < Rasterizer2D.bottomY) {
               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta10 * x1 + yDelta10;
               if (y2 < y0) {
                  int scalar5;
                  x0 = scalar5 = x1 << 16;
                  int scalar6;
                  color0 = scalar6 = color1 << 15;
                  if (y1 < 0) {
                     x0 -= xSlope10 * y1;
                     scalar5 -= xSlope21 * y1;
                     color0 -= colorSlope10 * y1;
                     scalar6 -= colorSlope21 * y1;
                     depth1 -= xDelta10 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  color2 <<= 15;
                  if (y2 < 0) {
                     x2 -= xSlope02 * y2;
                     color2 -= colorSlope02 * y2;
                     y2 = 0;
                  }

                  if ((y1 == y2 || xSlope10 >= xSlope21) && (y1 != y2 || xSlope10 <= xSlope02)) {
                     y0 -= y2;
                     y2 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y2 >= 0) {
                        drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, scalar5 >> 16, x0 >> 16, scalar6 >> 7, color0 >> 7, depth1, yDelta10);
                        x0 += xSlope10;
                        scalar5 += xSlope21;
                        color0 += colorSlope10;
                        scalar6 += colorSlope21;
                        y1 += Rasterizer2D.width;
                        depth1 += xDelta10;
                     }

                     while (--y0 >= 0) {
                        drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, x2 >> 16, x0 >> 16, color2 >> 7, color0 >> 7, depth1, yDelta10);
                        x0 += xSlope10;
                        x2 += xSlope02;
                        color0 += colorSlope10;
                        color2 += colorSlope02;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                     }

                     return;
                  }

                  y0 -= y2;
                  y2 -= y1;
                  y1 = scanOffsets[y1];

                  while (--y2 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, x0 >> 16, scalar5 >> 16, color0 >> 7, scalar6 >> 7, depth1, yDelta10);
                     x0 += xSlope10;
                     scalar5 += xSlope21;
                     color0 += colorSlope10;
                     scalar6 += colorSlope21;
                     y1 += Rasterizer2D.width;
                     depth1 += xDelta10;
                  }

                  while (--y0 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, x0 >> 16, x2 >> 16, color0 >> 7, color2 >> 7, depth1, yDelta10);
                     x0 += xSlope10;
                     x2 += xSlope02;
                     color0 += colorSlope10;
                     color2 += colorSlope02;
                     y1 += Rasterizer2D.width;
                     depth1 += xDelta10;
                  }

                  return;
               }

               int scalar7;
               x2 = scalar7 = x1 << 16;
               int scalar8;
               color2 = scalar8 = color1 << 15;
               if (y1 < 0) {
                  x2 -= xSlope10 * y1;
                  scalar7 -= xSlope21 * y1;
                  color2 -= colorSlope10 * y1;
                  scalar8 -= colorSlope21 * y1;
                  depth1 -= xDelta10 * y1;
                  y1 = 0;
               }

               x0 <<= 16;
               color0 <<= 15;
               if (y0 < 0) {
                  x0 -= xSlope02 * y0;
                  color0 -= colorSlope02 * y0;
                  y0 = 0;
               }

               if (xSlope10 < xSlope21) {
                  y2 -= y0;
                  y0 -= y1;

                  for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, x2 >> 16, scalar7 >> 16, color2 >> 7, scalar8 >> 7, depth1, yDelta10);
                     x2 += xSlope10;
                     scalar7 += xSlope21;
                     color2 += colorSlope10;
                     scalar8 += colorSlope21;
                     depth1 += xDelta10;
                  }

                  while (--y2 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, x0 >> 16, scalar7 >> 16, color0 >> 7, scalar8 >> 7, depth1, yDelta10);
                     x0 += xSlope02;
                     scalar7 += xSlope21;
                     color0 += colorSlope02;
                     scalar8 += colorSlope21;
                     depth1 += xDelta10;
                     y1 += Rasterizer2D.width;
                  }

                  return;
               }

               y2 -= y0;
               y0 -= y1;

               for (y1 = scanOffsets[y1]; --y0 >= 0; y1 += Rasterizer2D.width) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, scalar7 >> 16, x2 >> 16, scalar8 >> 7, color2 >> 7, depth1, yDelta10);
                  x2 += xSlope10;
                  scalar7 += xSlope21;
                  color2 += colorSlope10;
                  scalar8 += colorSlope21;
                  depth1 += xDelta10;
               }

               while (--y2 >= 0) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y1, scalar7 >> 16, x0 >> 16, scalar8 >> 7, color0 >> 7, depth1, yDelta10);
                  x0 += xSlope02;
                  scalar7 += xSlope21;
                  color0 += colorSlope02;
                  scalar8 += colorSlope21;
                  depth1 += xDelta10;
                  y1 += Rasterizer2D.width;
               }

               return;
            }
         } else if (y2 < Rasterizer2D.bottomY) {
            if (y0 > Rasterizer2D.bottomY) {
               y0 = Rasterizer2D.bottomY;
            }

            if (y1 > Rasterizer2D.bottomY) {
               y1 = Rasterizer2D.bottomY;
            }

            depth2 = depth2 - yDelta10 * x2 + yDelta10;
            if (y0 < y1) {
               int scalar9;
               x1 = scalar9 = x2 << 16;
               int scalar10;
               color1 = scalar10 = color2 << 15;
               if (y2 < 0) {
                  x1 -= xSlope21 * y2;
                  scalar9 -= xSlope02 * y2;
                  color1 -= colorSlope21 * y2;
                  scalar10 -= colorSlope02 * y2;
                  depth2 -= xDelta10 * y2;
                  y2 = 0;
               }

               x0 <<= 16;
               color0 <<= 15;
               if (y0 < 0) {
                  x0 -= xSlope10 * y0;
                  color0 -= colorSlope10 * y0;
                  y0 = 0;
               }

               if (xSlope21 < xSlope02) {
                  y1 -= y0;
                  y0 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y0 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, x1 >> 16, scalar9 >> 16, color1 >> 7, scalar10 >> 7, depth2, yDelta10);
                     x1 += xSlope21;
                     scalar9 += xSlope02;
                     color1 += colorSlope21;
                     scalar10 += colorSlope02;
                     y2 += Rasterizer2D.width;
                     depth2 += xDelta10;
                  }

                  while (--y1 >= 0) {
                     drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, x1 >> 16, x0 >> 16, color1 >> 7, color0 >> 7, depth2, yDelta10);
                     x1 += xSlope21;
                     x0 += xSlope10;
                     color1 += colorSlope21;
                     color0 += colorSlope10;
                     y2 += Rasterizer2D.width;
                     depth2 += xDelta10;
                  }

                  return;
               }

               y1 -= y0;
               y0 -= y2;
               y2 = scanOffsets[y2];

               while (--y0 >= 0) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, scalar9 >> 16, x1 >> 16, scalar10 >> 7, color1 >> 7, depth2, yDelta10);
                  x1 += xSlope21;
                  scalar9 += xSlope02;
                  color1 += colorSlope21;
                  scalar10 += colorSlope02;
                  y2 += Rasterizer2D.width;
                  depth2 += xDelta10;
               }

               while (--y1 >= 0) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, x0 >> 16, x1 >> 16, color0 >> 7, color1 >> 7, depth2, yDelta10);
                  x1 += xSlope21;
                  x0 += xSlope10;
                  color1 += colorSlope21;
                  color0 += colorSlope10;
                  y2 += Rasterizer2D.width;
                  depth2 += xDelta10;
               }

               return;
            }

            int scalar11;
            x0 = scalar11 = x2 << 16;
            int scalar12;
            color0 = scalar12 = color2 << 15;
            if (y2 < 0) {
               x0 -= xSlope21 * y2;
               scalar11 -= xSlope02 * y2;
               color0 -= colorSlope21 * y2;
               scalar12 -= colorSlope02 * y2;
               depth2 -= xDelta10 * y2;
               y2 = 0;
            }

            x1 <<= 16;
            color1 <<= 15;
            if (y1 < 0) {
               x1 -= xSlope10 * y1;
               color1 -= colorSlope10 * y1;
               y1 = 0;
            }

            if (xSlope21 < xSlope02) {
               y0 -= y1;
               y1 -= y2;

               for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, x0 >> 16, scalar11 >> 16, color0 >> 7, scalar12 >> 7, depth2, yDelta10);
                  x0 += xSlope21;
                  scalar11 += xSlope02;
                  color0 += colorSlope21;
                  scalar12 += colorSlope02;
                  depth2 += xDelta10;
               }

               while (--y0 >= 0) {
                  drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, x1 >> 16, scalar11 >> 16, color1 >> 7, scalar12 >> 7, depth2, yDelta10);
                  x1 += xSlope10;
                  scalar11 += xSlope02;
                  color1 += colorSlope10;
                  scalar12 += colorSlope02;
                  depth2 += xDelta10;
                  y2 += Rasterizer2D.width;
               }

               return;
            }

            y0 -= y1;
            y1 -= y2;

            for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
               drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, scalar11 >> 16, x0 >> 16, scalar12 >> 7, color0 >> 7, depth2, yDelta10);
               x0 += xSlope21;
               scalar11 += xSlope02;
               color0 += colorSlope21;
               scalar12 += colorSlope02;
               depth2 += xDelta10;
            }

            while (--y0 >= 0) {
               drawShadedScanlineLegacy(Rasterizer2D.pixels, y2, scalar11 >> 16, x1 >> 16, scalar12 >> 7, color1 >> 7, depth2, yDelta10);
               x1 += xSlope10;
               scalar11 += xSlope02;
               color1 += colorSlope10;
               scalar12 += colorSlope02;
               depth2 += xDelta10;
               y2 += Rasterizer2D.width;
            }

            return;
         }
      }
   }
   private static void drawShadedScanlineLegacy(int[] pixels, int pixelOffset, int xStart, int xEnd, int colorStart, int colorEnd, float depth, float depthSlope) {
      if (restrictEdges) {
         if (xEnd > Rasterizer2D.centerX) {
            xEnd = Rasterizer2D.centerX;
         }

         if (xStart < 0) {
            colorStart -= 0;
            xStart = 0;
         }
      }

      if (xStart < xEnd) {
         pixelOffset += xStart;
         depth += depthSlope * xStart;
         colorStart += 0 * xStart;
         if (renderModeFlag) {
            int scalar;
            int scalar2;
            if ((scalar = xEnd - xStart >> 2) > 0) {
               scalar2 = (colorEnd - colorStart) * reciprocal512[scalar] >> 15;
            } else {
               scalar2 = 0;
            }

            if (alpha == 0) {
               if (scalar > 0) {
                  do {
                     colorEnd = HSL_TO_RGB[colorStart >> 8];
                     colorStart += scalar2;
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     pixels[pixelOffset++] = colorEnd;
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     pixels[pixelOffset++] = colorEnd;
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     pixels[pixelOffset++] = colorEnd;
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     pixels[pixelOffset++] = colorEnd;
                  } while (--scalar > 0);
               }

               if ((scalar = xEnd - xStart & 3) > 0) {
                  colorEnd = HSL_TO_RGB[colorStart >> 8];

                  do {
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     pixels[pixelOffset++] = colorEnd;
                  } while (--scalar > 0);

                  return;
               }
            } else {
               int sourceAlpha = alpha;
               int scalar3 = 256 - alpha;
               if (scalar > 0) {
                  do {
                     colorEnd = HSL_TO_RGB[colorStart >> 8];
                     colorStart += scalar2;
                     colorEnd = ((colorEnd & 16711935) * scalar3 >> 8 & 16711935) + ((colorEnd & 0xFF00) * scalar3 >> 8 & 0xFF00);
                     int pixel = pixels[pixelOffset];
                     pixels[pixelOffset] = colorEnd + ((pixel & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixel & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     depth += depthSlope;
                     pixel = pixels[++pixelOffset];
                     pixels[pixelOffset] = colorEnd + ((pixel & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixel & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     depth += depthSlope;
                     pixel = pixels[++pixelOffset];
                     pixels[pixelOffset] = colorEnd + ((pixel & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixel & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     depth += depthSlope;
                     pixel = pixels[++pixelOffset];
                     pixels[pixelOffset] = colorEnd + ((pixel & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixel & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     depth += depthSlope;
                     pixelOffset++;
                  } while (--scalar > 0);
               }

               if ((scalar = xEnd - xStart & 3) > 0) {
                  colorEnd = (((colorEnd = HSL_TO_RGB[colorStart >> 8]) & 16711935) * scalar3 >> 8 & 16711935) + ((colorEnd & 0xFF00) * scalar3 >> 8 & 0xFF00);

                  do {
                     int pixel2 = pixels[pixelOffset];
                     pixels[pixelOffset] = colorEnd + ((pixel2 & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixel2 & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
                     Rasterizer2D.depthBuffer[pixelOffset] = depth;
                     depth += depthSlope;
                     pixelOffset++;
                  } while (--scalar > 0);

                  return;
               }
            }
         } else {
            int scalar4 = (colorEnd - colorStart) / (xEnd - xStart);
            int scalar5 = xEnd - xStart;
            if (alpha == 0) {
               do {
                  pixels[pixelOffset] = HSL_TO_RGB[colorStart >> 8];
                  Rasterizer2D.depthBuffer[pixelOffset] = depth;
                  depth += depthSlope;
                  pixelOffset++;
                  colorStart += scalar4;
               } while (--scalar5 > 0);

               return;
            }

            int alpha2 = alpha;
            int scalar6 = 256 - alpha;

            do {
               colorEnd = HSL_TO_RGB[colorStart >> 8];
               colorStart += scalar4;
               colorEnd = ((colorEnd & 16711935) * scalar6 >> 8 & 16711935) + ((colorEnd & 0xFF00) * scalar6 >> 8 & 0xFF00);
               xStart = pixels[pixelOffset];
               pixels[pixelOffset] = colorEnd + ((xStart & 16711935) * alpha2 >> 8 & 16711935) + ((xStart & 0xFF00) * alpha2 >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixelOffset++;
            } while (--scalar5 > 0);
         }
      }
   }
   private static void drawFlatScanlineLegacy(int[] pixels, int pixelOffset, int rgb, int xStart, int xEnd, float depth, float depthSlope) {
      if (restrictEdges) {
         if (xEnd > Rasterizer2D.centerX) {
            xEnd = Rasterizer2D.centerX;
         }

         if (xStart < 0) {
            xStart = 0;
         }
      }

      if (xStart < xEnd) {
         pixelOffset += xStart;
         int scalar = xEnd - xStart >> 2;
         depth += depthSlope * xStart;
         if (alpha == 0) {
            while (--scalar >= 0) {
               pixels[pixelOffset] = rgb;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixels[++pixelOffset] = rgb;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixels[++pixelOffset] = rgb;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixels[++pixelOffset] = rgb;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixelOffset++;
            }

            for (int position = xEnd - xStart & 3; --position >= 0; pixelOffset++) {
               pixels[pixelOffset] = rgb;
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
            }
         } else {
            int sourceAlpha = alpha;
            int scalar2 = 256 - alpha;
            rgb = ((rgb & 16711935) * scalar2 >> 8 & 16711935) + ((rgb & 0xFF00) * scalar2 >> 8 & 0xFF00);

            while (--scalar >= 0) {
               pixels[pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixels[++pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixels[++pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixels[++pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
               pixelOffset++;
            }

            for (int position2 = xEnd - xStart & 3; --position2 >= 0; pixelOffset++) {
               pixels[pixelOffset] = rgb + ((pixels[pixelOffset] & 16711935) * sourceAlpha >> 8 & 16711935) + ((pixels[pixelOffset] & 0xFF00) * sourceAlpha >> 8 & 0xFF00);
               Rasterizer2D.depthBuffer[pixelOffset] = depth;
               depth += depthSlope;
            }
         }
      }
   }
   private static void drawTexturedTriangleLegacy(
      int y0,
      int y1,
      int y2,
      int x0,
      int x1,
      int x2,
      int shade0,
      int shade1,
      int shade2,
      int textureX0,
      int textureX1,
      int textureX2,
      int textureY0,
      int textureY1,
      int textureY2,
      int textureZ0,
      int textureZ1,
      int textureZ2,
      int textureId,
      float depth0,
      float depth1,
      float depth2
   ) {
      if (!(depth0 < 0.0F) && !(depth1 < 0.0F) && !(depth2 < 0.0F)) {
         int[] texturePixelData = getTexturePixels(textureId);
         textureOpaque = !textureHasTransparency[textureId];
         textureX1 = textureX0 - textureX1;
         textureY1 = textureY0 - textureY1;
         textureZ1 = textureZ0 - textureZ1;
         textureX2 -= textureX0;
         textureY2 -= textureY0;
         textureZ2 -= textureZ0;
         textureId = Client.getProjectionScaleShift() + 5;
         if (!renderModeFlag) {
            textureId = 14;
         }

         long textureULong = ((long)textureX2 * textureY0 - (long)textureY2 * textureX0) << textureId;
         long textureUSlopeLong = ((long)textureY2 * textureZ0 - (long)textureZ2 * textureY0) << 8;
         long scalarLong = ((long)textureZ2 * textureX0 - (long)textureX2 * textureZ0) << 5;
         long textureVLong = ((long)textureX1 * textureY0 - (long)textureY1 * textureX0) << textureId;
         long textureVSlopeLong = ((long)textureY1 * textureZ0 - (long)textureZ1 * textureY0) << 8;
         long textureVStepLong = ((long)textureZ1 * textureX0 - (long)textureX1 * textureZ0) << 5;
         long textureWLong = ((long)textureY1 * textureX2 - (long)textureX1 * textureY2) << textureId;
         long textureWSlopeLong = ((long)textureZ1 * textureY2 - (long)textureY1 * textureZ2) << 8;
         long textureWStepLong = ((long)textureX1 * textureZ2 - (long)textureZ1 * textureX2) << 5;
         int coefficientShift = textureCoefficientNormalizationShift(
            textureULong,
            textureUSlopeLong,
            scalarLong,
            textureVLong,
            textureVSlopeLong,
            textureVStepLong,
            textureWLong,
            textureWSlopeLong,
            textureWStepLong
         );
         int textureU = (int)(textureULong >> coefficientShift);
         int textureUSlope = (int)(textureUSlopeLong >> coefficientShift);
         int scalar = (int)(scalarLong >> coefficientShift);
         int textureV = (int)(textureVLong >> coefficientShift);
         textureY0 = (int)(textureVSlopeLong >> coefficientShift);
         textureX0 = (int)(textureVStepLong >> coefficientShift);
         textureZ0 = (int)(textureWLong >> coefficientShift);
         textureY1 = (int)(textureWSlopeLong >> coefficientShift);
         textureX1 = (int)(textureWStepLong >> coefficientShift);
         textureX2 = 0;
         textureY2 = 0;
         if (y1 != y0) {
            textureX2 = (x1 - x0 << 16) / (y1 - y0);
            textureY2 = (shade1 - shade0 << 16) / (y1 - y0);
         }

         textureZ1 = 0;
         textureZ2 = 0;
         if (y2 != y1) {
            textureZ1 = (x2 - x1 << 16) / (y2 - y1);
            textureZ2 = (shade2 - shade1 << 16) / (y2 - y1);
         }

         textureId = 0;
         int shadeSlope02 = 0;
         if (y2 != y0) {
            textureId = (x0 - x2 << 16) / (y0 - y2);
            shadeSlope02 = (shade0 - shade2 << 16) / (y0 - y2);
         }

         float xDelta10 = x1 - x0;
         float yDelta10 = y1 - y0;
         float xDelta20 = x2 - x0;
         float yDelta20 = y2 - y0;
         float depthDelta10 = depth1 - depth0;
         float depthDelta20 = depth2 - depth0;
         float interpolant = xDelta10 * yDelta20 - xDelta20 * yDelta10;
         yDelta10 = (depthDelta10 * yDelta20 - depthDelta20 * yDelta10) / interpolant;
         xDelta10 = (depthDelta20 * xDelta10 - depthDelta10 * xDelta20) / interpolant;
         if (y0 <= y1 && y0 <= y2) {
            if (y0 < Rasterizer2D.bottomY) {
               if (y1 > Rasterizer2D.bottomY) {
                  y1 = Rasterizer2D.bottomY;
               }

               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               depth0 = depth0 - yDelta10 * x0 + yDelta10;
               if (y1 < y2) {
                  int scalar2;
                  x2 = scalar2 = x0 << 16;
                  int scalar3;
                  shade2 = scalar3 = shade0 << 16;
                  if (y0 < 0) {
                     x2 -= textureId * y0;
                     scalar2 -= textureX2 * y0;
                     depth0 -= xDelta10 * y0;
                     shade2 -= shadeSlope02 * y0;
                     scalar3 -= textureY2 * y0;
                     y0 = 0;
                  }

                  x1 <<= 16;
                  shade1 <<= 16;
                  if (y1 < 0) {
                     x1 -= textureZ1 * y1;
                     shade1 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  int scalar4 = y0 - viewportCenterY;
                  textureU += scalar * scalar4;
                  textureV += textureX0 * scalar4;
                  textureZ0 += textureX1 * scalar4;
                  if ((y0 == y1 || textureId >= textureX2) && (y0 != y1 || textureId <= textureZ1)) {
                     y2 -= y1;
                     y1 -= y0;
                     y0 = scanOffsets[y0];

                     while (--y1 >= 0) {
                        drawTexturedScanlineLegacy(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           scalar2 >> 16,
                           x2 >> 16,
                           scalar3 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           yDelta10
                        );
                        x2 += textureId;
                        scalar2 += textureX2;
                        depth0 += xDelta10;
                        shade2 += shadeSlope02;
                        scalar3 += textureY2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y2 >= 0) {
                        drawTexturedScanlineLegacy(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y0,
                           x1 >> 16,
                           x2 >> 16,
                           shade1 >> 8,
                           shade2 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth0,
                           yDelta10
                        );
                        x2 += textureId;
                        x1 += textureZ1;
                        depth0 += xDelta10;
                        shade2 += shadeSlope02;
                        shade1 += textureZ2;
                        y0 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     return;
                  }

                  y2 -= y1;
                  y1 -= y0;
                  y0 = scanOffsets[y0];

                  while (--y1 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y0,
                        x2 >> 16,
                        scalar2 >> 16,
                        shade2 >> 8,
                        scalar3 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth0,
                        yDelta10
                     );
                     x2 += textureId;
                     scalar2 += textureX2;
                     depth0 += xDelta10;
                     shade2 += shadeSlope02;
                     scalar3 += textureY2;
                     y0 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y2 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels, texturePixelData, y0, x2 >> 16, x1 >> 16, shade2 >> 8, shade1 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                     );
                     x2 += textureId;
                     x1 += textureZ1;
                     depth0 += xDelta10;
                     shade2 += shadeSlope02;
                     shade1 += textureZ2;
                     y0 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  return;
               }

               int scalar5;
               x1 = scalar5 = x0 << 16;
               int scalar6;
               shade1 = scalar6 = shade0 << 16;
               if (y0 < 0) {
                  x1 -= textureId * y0;
                  scalar5 -= textureX2 * y0;
                  depth0 -= xDelta10 * y0;
                  shade1 -= shadeSlope02 * y0;
                  scalar6 -= textureY2 * y0;
                  y0 = 0;
               }

               x2 <<= 16;
               shade2 <<= 16;
               if (y2 < 0) {
                  x2 -= textureZ1 * y2;
                  shade2 -= textureZ2 * y2;
                  y2 = 0;
               }

               int scalar7 = y0 - viewportCenterY;
               textureU += scalar * scalar7;
               textureV += textureX0 * scalar7;
               textureZ0 += textureX1 * scalar7;
               if ((y0 == y2 || textureId >= textureX2) && (y0 != y2 || textureZ1 <= textureX2)) {
                  y1 -= y2;
                  y2 -= y0;
                  y0 = scanOffsets[y0];

                  while (--y2 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y0,
                        scalar5 >> 16,
                        x1 >> 16,
                        scalar6 >> 8,
                        shade1 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth0,
                        yDelta10
                     );
                     x1 += textureId;
                     scalar5 += textureX2;
                     shade1 += shadeSlope02;
                     scalar6 += textureY2;
                     depth0 += xDelta10;
                     y0 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y1 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y0,
                        scalar5 >> 16,
                        x2 >> 16,
                        scalar6 >> 8,
                        shade2 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth0,
                        yDelta10
                     );
                     x2 += textureZ1;
                     scalar5 += textureX2;
                     shade2 += textureZ2;
                     scalar6 += textureY2;
                     depth0 += xDelta10;
                     y0 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  return;
               }

               y1 -= y2;
               y2 -= y0;
               y0 = scanOffsets[y0];

               while (--y2 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y0, x1 >> 16, scalar5 >> 16, shade1 >> 8, scalar6 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                  );
                  x1 += textureId;
                  scalar5 += textureX2;
                  shade1 += shadeSlope02;
                  scalar6 += textureY2;
                  depth0 += xDelta10;
                  y0 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               while (--y1 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y0, x2 >> 16, scalar5 >> 16, shade2 >> 8, scalar6 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth0, yDelta10
                  );
                  x2 += textureZ1;
                  scalar5 += textureX2;
                  shade2 += textureZ2;
                  scalar6 += textureY2;
                  depth0 += xDelta10;
                  y0 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               return;
            }
         } else if (y1 <= y2) {
            if (y1 < Rasterizer2D.bottomY) {
               if (y2 > Rasterizer2D.bottomY) {
                  y2 = Rasterizer2D.bottomY;
               }

               if (y0 > Rasterizer2D.bottomY) {
                  y0 = Rasterizer2D.bottomY;
               }

               depth1 = depth1 - yDelta10 * x1 + yDelta10;
               if (y2 < y0) {
                  int scalar8;
                  x0 = scalar8 = x1 << 16;
                  int scalar9;
                  shade0 = scalar9 = shade1 << 16;
                  if (y1 < 0) {
                     x0 -= textureX2 * y1;
                     scalar8 -= textureZ1 * y1;
                     depth1 -= xDelta10 * y1;
                     shade0 -= textureY2 * y1;
                     scalar9 -= textureZ2 * y1;
                     y1 = 0;
                  }

                  x2 <<= 16;
                  shade2 <<= 16;
                  if (y2 < 0) {
                     x2 -= textureId * y2;
                     shade2 -= shadeSlope02 * y2;
                     y2 = 0;
                  }

                  int scalar10 = y1 - viewportCenterY;
                  textureU += scalar * scalar10;
                  textureV += textureX0 * scalar10;
                  textureZ0 += textureX1 * scalar10;
                  if ((y1 == y2 || textureX2 >= textureZ1) && (y1 != y2 || textureX2 <= textureId)) {
                     y0 -= y2;
                     y2 -= y1;
                     y1 = scanOffsets[y1];

                     while (--y2 >= 0) {
                        drawTexturedScanlineLegacy(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           scalar8 >> 16,
                           x0 >> 16,
                           scalar9 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           yDelta10
                        );
                        x0 += textureX2;
                        scalar8 += textureZ1;
                        shade0 += textureY2;
                        scalar9 += textureZ2;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     while (--y0 >= 0) {
                        drawTexturedScanlineLegacy(
                           Rasterizer2D.pixels,
                           texturePixelData,
                           y1,
                           x2 >> 16,
                           x0 >> 16,
                           shade2 >> 8,
                           shade0 >> 8,
                           textureU,
                           textureV,
                           textureZ0,
                           textureUSlope,
                           textureY0,
                           textureY1,
                           depth1,
                           yDelta10
                        );
                        x0 += textureX2;
                        x2 += textureId;
                        shade0 += textureY2;
                        shade2 += shadeSlope02;
                        depth1 += xDelta10;
                        y1 += Rasterizer2D.width;
                        textureU += scalar;
                        textureV += textureX0;
                        textureZ0 += textureX1;
                     }

                     return;
                  }

                  y0 -= y2;
                  y2 -= y1;
                  y1 = scanOffsets[y1];

                  while (--y2 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y1,
                        x0 >> 16,
                        scalar8 >> 16,
                        shade0 >> 8,
                        scalar9 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth1,
                        yDelta10
                     );
                     x0 += textureX2;
                     scalar8 += textureZ1;
                     shade0 += textureY2;
                     scalar9 += textureZ2;
                     depth1 += xDelta10;
                     y1 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y0 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels, texturePixelData, y1, x0 >> 16, x2 >> 16, shade0 >> 8, shade2 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                     );
                     x0 += textureX2;
                     x2 += textureId;
                     shade0 += textureY2;
                     shade2 += shadeSlope02;
                     depth1 += xDelta10;
                     y1 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  return;
               }

               int scalar11;
               x2 = scalar11 = x1 << 16;
               int scalar12;
               shade2 = scalar12 = shade1 << 16;
               if (y1 < 0) {
                  x2 -= textureX2 * y1;
                  scalar11 -= textureZ1 * y1;
                  depth1 -= xDelta10 * y1;
                  shade2 -= textureY2 * y1;
                  scalar12 -= textureZ2 * y1;
                  y1 = 0;
               }

               x0 <<= 16;
               shade0 <<= 16;
               if (y0 < 0) {
                  x0 -= textureId * y0;
                  shade0 -= shadeSlope02 * y0;
                  y0 = 0;
               }

               int scalar13 = y1 - viewportCenterY;
               textureU += scalar * scalar13;
               textureV += textureX0 * scalar13;
               textureZ0 += textureX1 * scalar13;
               if (textureX2 < textureZ1) {
                  y2 -= y0;
                  y0 -= y1;
                  y1 = scanOffsets[y1];

                  while (--y0 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y1,
                        x2 >> 16,
                        scalar11 >> 16,
                        shade2 >> 8,
                        scalar12 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth1,
                        yDelta10
                     );
                     x2 += textureX2;
                     scalar11 += textureZ1;
                     shade2 += textureY2;
                     scalar12 += textureZ2;
                     depth1 += xDelta10;
                     y1 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y2 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y1,
                        x0 >> 16,
                        scalar11 >> 16,
                        shade0 >> 8,
                        scalar12 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth1,
                        yDelta10
                     );
                     x0 += textureId;
                     scalar11 += textureZ1;
                     shade0 += shadeSlope02;
                     scalar12 += textureZ2;
                     depth1 += xDelta10;
                     y1 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  return;
               }

               y2 -= y0;
               y0 -= y1;
               y1 = scanOffsets[y1];

               while (--y0 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y1, scalar11 >> 16, x2 >> 16, scalar12 >> 8, shade2 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                  );
                  x2 += textureX2;
                  scalar11 += textureZ1;
                  shade2 += textureY2;
                  scalar12 += textureZ2;
                  depth1 += xDelta10;
                  y1 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               while (--y2 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y1, scalar11 >> 16, x0 >> 16, scalar12 >> 8, shade0 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth1, yDelta10
                  );
                  x0 += textureId;
                  scalar11 += textureZ1;
                  shade0 += shadeSlope02;
                  scalar12 += textureZ2;
                  depth1 += xDelta10;
                  y1 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               return;
            }
         } else if (y2 < Rasterizer2D.bottomY) {
            if (y0 > Rasterizer2D.bottomY) {
               y0 = Rasterizer2D.bottomY;
            }

            if (y1 > Rasterizer2D.bottomY) {
               y1 = Rasterizer2D.bottomY;
            }

            depth2 = depth2 - yDelta10 * x2 + yDelta10;
            if (y0 < y1) {
               int scalar14;
               x1 = scalar14 = x2 << 16;
               int scalar15;
               shade1 = scalar15 = shade2 << 16;
               if (y2 < 0) {
                  x1 -= textureZ1 * y2;
                  scalar14 -= textureId * y2;
                  depth2 -= xDelta10 * y2;
                  shade1 -= textureZ2 * y2;
                  scalar15 -= shadeSlope02 * y2;
                  y2 = 0;
               }

               x0 <<= 16;
               shade0 <<= 16;
               if (y0 < 0) {
                  x0 -= textureX2 * y0;
                  shade0 -= textureY2 * y0;
                  y0 = 0;
               }

               int scalar16 = y2 - viewportCenterY;
               textureU += scalar * scalar16;
               textureV += textureX0 * scalar16;
               textureZ0 += textureX1 * scalar16;
               if (textureZ1 < textureId) {
                  y1 -= y0;
                  y0 -= y2;
                  y2 = scanOffsets[y2];

                  while (--y0 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels,
                        texturePixelData,
                        y2,
                        x1 >> 16,
                        scalar14 >> 16,
                        shade1 >> 8,
                        scalar15 >> 8,
                        textureU,
                        textureV,
                        textureZ0,
                        textureUSlope,
                        textureY0,
                        textureY1,
                        depth2,
                        yDelta10
                     );
                     x1 += textureZ1;
                     scalar14 += textureId;
                     shade1 += textureZ2;
                     scalar15 += shadeSlope02;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  while (--y1 >= 0) {
                     drawTexturedScanlineLegacy(
                        Rasterizer2D.pixels, texturePixelData, y2, x1 >> 16, x0 >> 16, shade1 >> 8, shade0 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                     );
                     x1 += textureZ1;
                     x0 += textureX2;
                     shade1 += textureZ2;
                     shade0 += textureY2;
                     depth2 += xDelta10;
                     y2 += Rasterizer2D.width;
                     textureU += scalar;
                     textureV += textureX0;
                     textureZ0 += textureX1;
                  }

                  return;
               }

               y1 -= y0;
               y0 -= y2;
               y2 = scanOffsets[y2];

               while (--y0 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y2, scalar14 >> 16, x1 >> 16, scalar15 >> 8, shade1 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                  );
                  x1 += textureZ1;
                  scalar14 += textureId;
                  shade1 += textureZ2;
                  scalar15 += shadeSlope02;
                  depth2 += xDelta10;
                  y2 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               while (--y1 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y2, x0 >> 16, x1 >> 16, shade0 >> 8, shade1 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                  );
                  x1 += textureZ1;
                  x0 += textureX2;
                  shade1 += textureZ2;
                  shade0 += textureY2;
                  depth2 += xDelta10;
                  y2 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               return;
            }

            int scalar17;
            x0 = scalar17 = x2 << 16;
            int scalar18;
            shade0 = scalar18 = shade2 << 16;
            if (y2 < 0) {
               x0 -= textureZ1 * y2;
               scalar17 -= textureId * y2;
               depth2 -= xDelta10 * y2;
               shade0 -= textureZ2 * y2;
               scalar18 -= shadeSlope02 * y2;
               y2 = 0;
            }

            x1 <<= 16;
            shade1 <<= 16;
            if (y1 < 0) {
               x1 -= textureX2 * y1;
               shade1 -= textureY2 * y1;
               y1 = 0;
            }

            int scalar19 = y2 - viewportCenterY;
            textureU += scalar * scalar19;
            textureV += textureX0 * scalar19;
            textureZ0 += textureX1 * scalar19;
            if (textureZ1 < textureId) {
               y0 -= y1;
               y1 -= y2;
               y2 = scanOffsets[y2];

               while (--y1 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y2, x0 >> 16, scalar17 >> 16, shade0 >> 8, scalar18 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                  );
                  x0 += textureZ1;
                  scalar17 += textureId;
                  shade0 += textureZ2;
                  scalar18 += shadeSlope02;
                  depth2 += xDelta10;
                  y2 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               while (--y0 >= 0) {
                  drawTexturedScanlineLegacy(
                     Rasterizer2D.pixels, texturePixelData, y2, x1 >> 16, scalar17 >> 16, shade1 >> 8, scalar18 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
                  );
                  x1 += textureX2;
                  scalar17 += textureId;
                  shade1 += textureY2;
                  scalar18 += shadeSlope02;
                  depth2 += xDelta10;
                  y2 += Rasterizer2D.width;
                  textureU += scalar;
                  textureV += textureX0;
                  textureZ0 += textureX1;
               }

               return;
            }

            y0 -= y1;
            y1 -= y2;
            y2 = scanOffsets[y2];

            while (--y1 >= 0) {
               drawTexturedScanlineLegacy(
                  Rasterizer2D.pixels, texturePixelData, y2, scalar17 >> 16, x0 >> 16, scalar18 >> 8, shade0 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
               );
               x0 += textureZ1;
               scalar17 += textureId;
               shade0 += textureZ2;
               scalar18 += shadeSlope02;
               depth2 += xDelta10;
               y2 += Rasterizer2D.width;
               textureU += scalar;
               textureV += textureX0;
               textureZ0 += textureX1;
            }

            while (--y0 >= 0) {
               drawTexturedScanlineLegacy(
                  Rasterizer2D.pixels, texturePixelData, y2, scalar17 >> 16, x1 >> 16, scalar18 >> 8, shade1 >> 8, textureU, textureV, textureZ0, textureUSlope, textureY0, textureY1, depth2, yDelta10
               );
               x1 += textureX2;
               scalar17 += textureId;
               shade1 += textureY2;
               scalar18 += shadeSlope02;
               depth2 += xDelta10;
               y2 += Rasterizer2D.width;
               textureU += scalar;
               textureV += textureX0;
               textureZ0 += textureX1;
            }

            return;
         }
      }
   }
   private static void drawTexturedScanlineLegacy(
      int[] pixels,
      int[] texturePixels,
      int pixelOffset,
      int xStart,
      int xEnd,
      int brightness,
      int brightnessSlope,
      int textureU,
      int textureV,
      int textureW,
      int textureUSlope,
      int textureVSlope,
      int textureWSlope,
      float depth,
      float depthSlope
   ) {
      drawTexturedScanline(
         pixels,
         texturePixels,
         pixelOffset,
         xStart,
         xEnd,
         brightness,
         brightnessSlope,
         textureU,
         textureV,
         textureW,
         textureUSlope,
         textureVSlope,
         textureWSlope,
         depth,
         depthSlope
      );
   }
}
