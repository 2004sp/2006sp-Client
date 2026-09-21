package client;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

/**
 * GPU-backed triangle rasterizer for the legacy software client.
 *
 * The main scene is rendered into an off-screen OpenGL Pbuffer and copied back
 * once at the end of the 3D pass. Triangle calls made outside that pass use a
 * small bounding-box readback. The legacy int[] color buffer and float[] depth
 * buffer therefore remain the source of truth for fog, UI and screenshots.
 */
final class GpuRasterizer3D {
   private static final float DEPTH_SCALE = 1048576.0F;
   private static final int GL_CLAMP_TO_EDGE = 33071;
   private static final int GL_BGRA = 32993;
   private static final int TEXTURE_COUNT = 51;

   private static volatile boolean requested = true;
   private static boolean unavailable;
   private static boolean failureLogged;

   private static boolean frameOpen;
   private static boolean frameActive;
   private static boolean frameSoftwareFallback;

   private static Pbuffer pbuffer;
   private static int bufferWidth;
   private static int bufferHeight;
   private static int viewportWidth = -1;
   private static int viewportHeight = -1;
   private static int scissorX = Integer.MIN_VALUE;
   private static int scissorY = Integer.MIN_VALUE;
   private static int scissorWidth = Integer.MIN_VALUE;
   private static int scissorHeight = Integer.MIN_VALUE;

   private static final int[] textureIds = new int[TEXTURE_COUNT];
   private static final boolean[] textureDirty = new boolean[TEXTURE_COUNT];
   private static boolean cachedLowMemory = Rasterizer3D.lowMemory;

   private static ByteBuffer colorReadback;
   private static FloatBuffer depthReadback;
   private static ByteBuffer textureUploadBuffer;

   static {
      Arrays.fill(textureDirty, true);
   }

   private GpuRasterizer3D() {
   }

   static boolean isRequested() {
      return requested;
   }

   static void setEnabled(boolean enabled) {
      requested = enabled;
      if (enabled) {
         unavailable = false;
      }
      System.out.println("Renderer: " + (enabled ? "GPU" : "software"));
   }

   static void beginFrame() {
      if (frameOpen) {
         endFrame();
      }

      frameOpen = true;
      frameActive = false;
      frameSoftwareFallback = false;

      if (!baseAvailable()) {
         frameSoftwareFallback = true;
         return;
      }

      try {
         ensureContext(Rasterizer2D.width, Rasterizer2D.height);
         makeCurrent();
         configureViewport(Rasterizer2D.width, Rasterizer2D.height);

         GL11.glEnable(GL11.GL_SCISSOR_TEST);
         GL11.glScissor(0, 0, viewportWidth, viewportHeight);
         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         GL11.glDisable(GL11.GL_BLEND);
         GL11.glEnable(GL11.GL_DEPTH_TEST);
         GL11.glDepthFunc(GL11.GL_ALWAYS);
         GL11.glDepthMask(true);
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
         // Keep alpha at the clear value (zero) so BGRA readback maps directly
         // to the legacy 0x00RRGGBB int framebuffer on little-endian Windows.
         GL11.glColorMask(true, true, true, false);
         frameActive = true;
      } catch (Throwable failure) {
         frameSoftwareFallback = true;
         fail(failure);
      }
   }

   static void endFrame() {
      endFrame(true);
   }

   static void endFrame(boolean copyDepth) {
      if (!frameOpen) {
         return;
      }

      try {
         if (frameActive) {
            readBackFrame(copyDepth);
         }
      } catch (Throwable failure) {
         frameActive = false;
         fail(failure);
      } finally {
         frameOpen = false;
         frameActive = false;
         frameSoftwareFallback = false;
      }
   }

   static void invalidateTexture(int textureId) {
      if (textureId >= 0 && textureId < textureDirty.length) {
         textureDirty[textureId] = true;
      }
   }

   static boolean drawDepthTriangle(
      int y0, int y1, int y2, int x0, int x1, int x2,
      float depth0, float depth1, float depth2
   ) {
      if (!canDraw(depth0, depth1, depth2)) {
         return false;
      }

      // The recovered depth-only walker uses x* as scanline Y and y* as X.
      Bounds bounds = Bounds.of(y0, x0, y1, x1, y2, x2);
      if (bounds == null) {
         return true;
      }
      if (!prepare(bounds)) {
         return false;
      }

      boolean batched = frameActive;
      try {
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         GL11.glDisable(GL11.GL_BLEND);
         GL11.glColorMask(false, false, false, false);
         GL11.glShadeModel(GL11.GL_FLAT);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glBegin(GL11.GL_TRIANGLES);
         GL11.glVertex3f(y0, x0, -clampDepth(depth0));
         GL11.glVertex3f(y1, x1, -clampDepth(depth1));
         GL11.glVertex3f(y2, x2, -clampDepth(depth2));
         GL11.glEnd();
         GL11.glColorMask(true, true, true, false);

         if (!batched) {
            readBack(bounds, false, false);
         }
         return true;
      } catch (Throwable failure) {
         GL11.glColorMask(true, true, true, true);
         failCurrentFrame(failure);
         return false;
      }
   }

   static boolean drawFlatTriangle(
      int y0, int y1, int y2, int x0, int x1, int x2, int rgb,
      float depth0, float depth1, float depth2
   ) {
      if (!canDraw(depth0, depth1, depth2)) {
         return false;
      }

      Bounds bounds = Bounds.of(x0, y0, x1, y1, x2, y2);
      if (bounds == null) {
         return true;
      }
      if (!prepare(bounds)) {
         return false;
      }

      boolean batched = frameActive;
      try {
         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         float sourceAlpha = configureLegacyBlend(batched);
         GL11.glShadeModel(GL11.GL_FLAT);
         setColor(rgb, 1.0F, sourceAlpha);

         GL11.glBegin(GL11.GL_TRIANGLES);
         GL11.glVertex3f(x0, y0, -clampDepth(depth0));
         GL11.glVertex3f(x1, y1, -clampDepth(depth1));
         GL11.glVertex3f(x2, y2, -clampDepth(depth2));
         GL11.glEnd();

         if (!batched) {
            readBack(bounds, true, true);
         }
         return true;
      } catch (Throwable failure) {
         failCurrentFrame(failure);
         return false;
      }
   }

   static boolean drawShadedTriangle(
      boolean useFallback,
      int y0, int y1, int y2, int x0, int x1, int x2,
      int color0, int color1, int color2,
      float depth0, float depth1, float depth2
   ) {
      if (!canDraw(depth0, depth1, depth2)) {
         return false;
      }

      Bounds bounds = Bounds.of(x0, y0, x1, y1, x2, y2);
      if (bounds == null) {
         return true;
      }
      if (!prepare(bounds)) {
         return false;
      }

      boolean batched = frameActive;
      try {
         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         float sourceAlpha = configureLegacyBlend(batched);
         GL11.glShadeModel(GL11.GL_SMOOTH);

         GL11.glBegin(GL11.GL_TRIANGLES);
         setColor(Rasterizer3D.HSL_TO_RGB[color0 & 65535], 1.0F, sourceAlpha);
         GL11.glVertex3f(x0, y0, -clampDepth(depth0));
         setColor(Rasterizer3D.HSL_TO_RGB[color1 & 65535], 1.0F, sourceAlpha);
         GL11.glVertex3f(x1, y1, -clampDepth(depth1));
         setColor(Rasterizer3D.HSL_TO_RGB[color2 & 65535], 1.0F, sourceAlpha);
         GL11.glVertex3f(x2, y2, -clampDepth(depth2));
         GL11.glEnd();

         if (!batched) {
            readBack(bounds, true, true);
         }
         return true;
      } catch (Throwable failure) {
         failCurrentFrame(failure);
         return false;
      }
   }

   static boolean drawTexturedTriangle(
      boolean useFallback,
      int y0, int y1, int y2, int x0, int x1, int x2,
      int shade0, int shade1, int shade2,
      int textureX0, int textureX1, int textureX2,
      int textureY0, int textureY1, int textureY2,
      int textureZ0, int textureZ1, int textureZ2,
      int textureId,
      float depth0, float depth1, float depth2
   ) {
      if (!canDraw(depth0, depth1, depth2) || textureId < 0 || textureId >= TEXTURE_COUNT) {
         if (frameOpen && !frameSoftwareFallback) {
            fallbackCurrentFrame();
         }
         return false;
      }

      Bounds bounds = Bounds.of(x0, y0, x1, y1, x2, y2);
      if (bounds == null) {
         return true;
      }
      if (!prepare(bounds)) {
         return false;
      }

      boolean batched = frameActive;
      try {
         int glTexture = ensureTexture(textureId);
         if (glTexture == 0) {
            if (batched) {
               fallbackCurrentFrame();
            }
            return false;
         }

         int deltaX1 = textureX0 - textureX1;
         int deltaY1 = textureY0 - textureY1;
         int deltaZ1 = textureZ0 - textureZ1;
         int deltaX2 = textureX2 - textureX0;
         int deltaY2 = textureY2 - textureY0;
         int deltaZ2 = textureZ2 - textureZ0;

         int projectionShift = Client.getProjectionScaleShift() + 5;
         if (!Rasterizer3D.renderModeFlag) {
            projectionShift = 14;
         }

         double uBase = Math.scalb((double)((long)deltaX2 * textureY0 - (long)deltaY2 * textureX0), projectionShift);
         double uSlopeX = Math.scalb((double)((long)deltaY2 * textureZ0 - (long)deltaZ2 * textureY0), 8);
         double uSlopeY = Math.scalb((double)((long)deltaZ2 * textureX0 - (long)deltaX2 * textureZ0), 5);
         double vBase = Math.scalb((double)((long)deltaX1 * textureY0 - (long)deltaY1 * textureX0), projectionShift);
         double vSlopeX = Math.scalb((double)((long)deltaY1 * textureZ0 - (long)deltaZ1 * textureY0), 8);
         double vSlopeY = Math.scalb((double)((long)deltaZ1 * textureX0 - (long)deltaX1 * textureZ0), 5);
         double wBase = Math.scalb((double)((long)deltaY1 * deltaX2 - (long)deltaX1 * deltaY2), projectionShift);
         double wSlopeX = Math.scalb((double)((long)deltaZ1 * deltaY2 - (long)deltaY1 * deltaZ2), 8);
         double wSlopeY = Math.scalb((double)((long)deltaX1 * deltaZ2 - (long)deltaZ1 * deltaX2), 5);

         double pu0 = projected(uBase, uSlopeX, uSlopeY, x0, y0);
         double pv0 = projected(vBase, vSlopeX, vSlopeY, x0, y0);
         double pw0 = projected(wBase, wSlopeX, wSlopeY, x0, y0);
         double pu1 = projected(uBase, uSlopeX, uSlopeY, x1, y1);
         double pv1 = projected(vBase, vSlopeX, vSlopeY, x1, y1);
         double pw1 = projected(wBase, wSlopeX, wSlopeY, x1, y1);
         double pu2 = projected(uBase, uSlopeX, uSlopeY, x2, y2);
         double pv2 = projected(vBase, vSlopeX, vSlopeY, x2, y2);
         double pw2 = projected(wBase, wSlopeX, wSlopeY, x2, y2);

         double max = maxAbs(pu0, pv0, pw0, pu1, pv1, pw1, pu2, pv2, pw2);
         if (!(max > 0.0D) || Double.isInfinite(max) || Double.isNaN(max)) {
            if (batched) {
               fallbackCurrentFrame();
            }
            return false;
         }
         float coordinateScale = (float)(1.0D / max);

         boolean smoothTextureLight = Client.smoothRendering
            && Rasterizer3D.smoothShading
            && Rasterizer3D.renderModeFlag
            && !useFallback;

         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(GL11.GL_BLEND);
         GL11.glEnable(GL11.GL_TEXTURE_2D);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
         GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
         GL11.glEnable(GL11.GL_ALPHA_TEST);
         GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
         GL11.glShadeModel(GL11.GL_SMOOTH);

         GL11.glBegin(GL11.GL_TRIANGLES);
         setTextureShade(shade0, smoothTextureLight);
         GL11.glTexCoord4f((float)(pu0 * coordinateScale), (float)(pv0 * coordinateScale), 0.0F, (float)(pw0 * coordinateScale));
         GL11.glVertex3f(x0, y0, -clampDepth(depth0));

         setTextureShade(shade1, smoothTextureLight);
         GL11.glTexCoord4f((float)(pu1 * coordinateScale), (float)(pv1 * coordinateScale), 0.0F, (float)(pw1 * coordinateScale));
         GL11.glVertex3f(x1, y1, -clampDepth(depth1));

         setTextureShade(shade2, smoothTextureLight);
         GL11.glTexCoord4f((float)(pu2 * coordinateScale), (float)(pv2 * coordinateScale), 0.0F, (float)(pw2 * coordinateScale));
         GL11.glVertex3f(x2, y2, -clampDepth(depth2));
         GL11.glEnd();

         if (!batched) {
            readBack(bounds, true, false);
         }
         return true;
      } catch (Throwable failure) {
         failCurrentFrame(failure);
         return false;
      }
   }

   private static boolean baseAvailable() {
      return requested
         && !unavailable
         && Rasterizer2D.pixels != null
         && Rasterizer2D.depthBuffer != null
         && Rasterizer2D.width > 0
         && Rasterizer2D.height > 0;
   }

   private static boolean canDraw(float depth0, float depth1, float depth2) {
      if (frameOpen && frameSoftwareFallback) {
         return false;
      }

      if (!baseAvailable() || depth0 < 0.0F || depth1 < 0.0F || depth2 < 0.0F) {
         if (frameOpen && !frameSoftwareFallback) {
            fallbackCurrentFrame();
         }
         return false;
      }
      return true;
   }

   private static boolean prepare(Bounds bounds) {
      try {
         // beginFrame() already owns the current context and configures the
         // viewport/depth state. Repeating those JNI calls for every triangle
         // is especially expensive in resizable mode.
         if (!frameActive) {
            ensureContext(Rasterizer2D.width, Rasterizer2D.height);
            makeCurrent();
            configureViewport(Rasterizer2D.width, Rasterizer2D.height);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
         }

         if (frameActive) {
            int minX = Math.max(0, Rasterizer2D.topX);
            int minY = Math.max(0, Rasterizer2D.topY);
            int maxX = Math.min(viewportWidth, Rasterizer2D.bottomX);
            int maxY = Math.min(viewportHeight, Rasterizer2D.bottomY);
            if (minX >= maxX || minY >= maxY) {
               setScissor(0, 0, 0, 0);
               return true;
            }
            setScissor(minX, viewportHeight - maxY, maxX - minX, maxY - minY);
         } else {
            setScissor(bounds.minX, viewportHeight - bounds.maxY, bounds.width(), bounds.height());
            GL11.glColorMask(true, true, true, true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            GL11.glDepthMask(true);
         }

         return true;
      } catch (Throwable failure) {
         failCurrentFrame(failure);
         return false;
      }
   }

   private static void setScissor(int x, int y, int width, int height) {
      if (x == scissorX && y == scissorY && width == scissorWidth && height == scissorHeight) {
         return;
      }
      GL11.glScissor(x, y, width, height);
      scissorX = x;
      scissorY = y;
      scissorWidth = width;
      scissorHeight = height;
   }

   private static float configureLegacyBlend(boolean batched) {
      if (!batched || Rasterizer3D.alpha == 0) {
         GL11.glDisable(GL11.GL_BLEND);
         return 1.0F;
      }

      int alpha = Rasterizer3D.alpha;
      if (alpha < 0) {
         alpha = 0;
      } else if (alpha > 256) {
         alpha = 256;
      }

      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      return (256 - alpha) / 256.0F;
   }

   private static void fallbackCurrentFrame() {
      if (!frameOpen || frameSoftwareFallback) {
         return;
      }

      if (frameActive) {
         try {
            readBackFrame();
         } catch (Throwable failure) {
            frameActive = false;
            frameSoftwareFallback = true;
            fail(failure);
            return;
         }
      }

      frameActive = false;
      frameSoftwareFallback = true;
   }

   private static void failCurrentFrame(Throwable failure) {
      if (frameOpen && frameActive) {
         try {
            readBackFrame();
         } catch (Throwable ignored) {
         }
      }

      frameActive = false;
      if (frameOpen) {
         frameSoftwareFallback = true;
      }
      fail(failure);
   }

   private static void ensureContext(int width, int height) throws Exception {
      boolean recreate = pbuffer == null || pbuffer.isBufferLost() || width > bufferWidth || height > bufferHeight;
      if (!recreate) {
         return;
      }

      destroyContext();
      bufferWidth = Math.max(width, 765);
      bufferHeight = Math.max(height, 503);

      PixelFormat pixelFormat = new PixelFormat().withAlphaBits(8).withDepthBits(24);
      pbuffer = new Pbuffer(bufferWidth, bufferHeight, pixelFormat, null);
      pbuffer.makeCurrent();

      GL11.glDisable(GL11.GL_DITHER);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_FOG);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDepthFunc(GL11.GL_ALWAYS);
      GL11.glDepthMask(true);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glClearDepth(1.0D);
      GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
      GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

      Arrays.fill(textureIds, 0);
      Arrays.fill(textureDirty, true);
      cachedLowMemory = Rasterizer3D.lowMemory;
      viewportWidth = -1;
      viewportHeight = -1;
      scissorX = Integer.MIN_VALUE;
      scissorY = Integer.MIN_VALUE;
      scissorWidth = Integer.MIN_VALUE;
      scissorHeight = Integer.MIN_VALUE;
      System.out.println("GPU renderer initialized: OpenGL Pbuffer " + bufferWidth + "x" + bufferHeight);
   }

   private static void makeCurrent() throws Exception {
      if (!pbuffer.isCurrent()) {
         pbuffer.makeCurrent();
      }
   }

   private static void configureViewport(int width, int height) {
      if (viewportWidth == width && viewportHeight == height) {
         return;
      }

      viewportWidth = width;
      viewportHeight = height;
      GL11.glViewport(0, 0, width, height);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, width, height, 0.0D, 0.0D, DEPTH_SCALE);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
   }

   private static int ensureTexture(int textureId) {
      if (cachedLowMemory != Rasterizer3D.lowMemory) {
         cachedLowMemory = Rasterizer3D.lowMemory;
         Arrays.fill(textureDirty, true);
      }

      if (textureIds[textureId] != 0 && !textureDirty[textureId]) {
         return textureIds[textureId];
      }

      if (textureIds[textureId] != 0) {
         GL11.glDeleteTextures(textureIds[textureId]);
         textureIds[textureId] = 0;
      }

      int[] pixels = Rasterizer3D.getGpuTexturePixels(textureId);
      if (pixels == null) {
         return 0;
      }

      int size = Rasterizer3D.lowMemory ? 64 : 128;
      int pixelCount = size * size;
      ensureTextureUploadCapacity(pixelCount * 4);
      textureUploadBuffer.clear();
      textureUploadBuffer.limit(pixelCount * 4);

      for (int i = 0; i < pixelCount; i++) {
         int rgb = pixels[i];
         textureUploadBuffer.put((byte)(rgb >> 16));
         textureUploadBuffer.put((byte)(rgb >> 8));
         textureUploadBuffer.put((byte)rgb);
         textureUploadBuffer.put((byte)(rgb == 0 ? 0 : 255));
      }
      textureUploadBuffer.flip();

      int glTexture = GL11.glGenTextures();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
      GL11.glTexImage2D(
         GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, size, size, 0,
         GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureUploadBuffer
      );

      textureIds[textureId] = glTexture;
      textureDirty[textureId] = false;
      return glTexture;
   }

   private static void readBackFrame() {
      readBackFrame(true);
   }

   private static void readBackFrame(boolean copyDepth) {
      if (viewportWidth <= 0 || viewportHeight <= 0) {
         return;
      }

      int width = viewportWidth;
      int height = viewportHeight;
      int count = width * height;
      ensureReadbackCapacity(count, copyDepth);

      // On little-endian desktop platforms, BGRA/UNSIGNED_BYTE viewed through
      // a native-order IntBuffer becomes 0xAARRGGBB. Alpha writes are disabled
      // for the scene, so the high byte stays zero and each row can be copied
      // straight into the legacy 0x00RRGGBB framebuffer.
      colorReadback.clear();
      colorReadback.limit(count * 4);
      GL11.glReadPixels(0, 0, width, height, GL_BGRA, GL11.GL_UNSIGNED_BYTE, colorReadback);
      IntBuffer packedColors = colorReadback.asIntBuffer();
      for (int readRow = 0; readRow < height; readRow++) {
         packedColors.position(readRow * width);
         packedColors.get(Rasterizer2D.pixels, (height - 1 - readRow) * width, width);
      }

      if (!copyDepth) {
         return;
      }

      depthReadback.clear();
      depthReadback.limit(count);
      GL11.glReadPixels(0, 0, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthReadback);
      for (int readRow = 0; readRow < height; readRow++) {
         int destination = (height - 1 - readRow) * width;
         int source = readRow * width;
         for (int x = 0; x < width; x++) {
            float gpuDepth = depthReadback.get(source + x);
            if (gpuDepth < 0.9999999F) {
               Rasterizer2D.depthBuffer[destination + x] = gpuDepth * DEPTH_SCALE;
            }
         }
      }
   }

   private static void readBack(Bounds bounds, boolean copyColor, boolean blendLegacyAlpha) {
      readBack(bounds, copyColor, blendLegacyAlpha, true);
   }

   private static void readBack(Bounds bounds, boolean copyColor, boolean blendLegacyAlpha, boolean copyDepth) {
      int width = bounds.width();
      int height = bounds.height();
      int count = width * height;
      ensureReadbackCapacity(count, copyDepth);

      colorReadback.clear();
      colorReadback.limit(count * 4);
      if (copyDepth) {
         depthReadback.clear();
         depthReadback.limit(count);
      }

      int readY = viewportHeight - bounds.maxY;
      GL11.glReadPixels(bounds.minX, readY, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorReadback);
      if (copyDepth) {
         GL11.glReadPixels(bounds.minX, readY, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthReadback);
      }

      int legacyAlpha = Rasterizer3D.alpha;
      for (int readRow = 0; readRow < height; readRow++) {
         int logicalY = bounds.maxY - 1 - readRow;
         int destination = logicalY * Rasterizer2D.width + bounds.minX;
         int sourceRow = readRow * width;

         for (int x = 0; x < width; x++) {
            int sourcePixel = sourceRow + x;
            int byteIndex = sourcePixel * 4;
            if (copyDepth) {
               float gpuDepth = depthReadback.get(sourcePixel);
               if (gpuDepth >= 0.9999999F) {
                  continue;
               }
               Rasterizer2D.depthBuffer[destination + x] = gpuDepth * DEPTH_SCALE;
            } else {
               // Colour-only bounded readbacks are not used by the batched
               // frame path. Keep this branch conservative if one is added.
            }

            int destinationIndex = destination + x;
            if (copyColor) {
               int rgb = ((colorReadback.get(byteIndex) & 255) << 16)
                  | ((colorReadback.get(byteIndex + 1) & 255) << 8)
                  | (colorReadback.get(byteIndex + 2) & 255);

               if (blendLegacyAlpha && legacyAlpha != 0) {
                  int sourceWeight = 256 - legacyAlpha;
                  int source = ((rgb & 16711935) * sourceWeight >> 8 & 16711935)
                     + ((rgb & 65280) * sourceWeight >> 8 & 65280);
                  int destinationRgb = Rasterizer2D.pixels[destinationIndex];
                  int destinationColor = ((destinationRgb & 16711935) * legacyAlpha >> 8 & 16711935)
                     + ((destinationRgb & 65280) * legacyAlpha >> 8 & 65280);
                  Rasterizer2D.pixels[destinationIndex] = source + destinationColor;
               } else {
                  Rasterizer2D.pixels[destinationIndex] = rgb;
               }
            }
         }
      }
   }

   private static void ensureReadbackCapacity(int pixels, boolean copyDepth) {
      int colorBytes = pixels * 4;
      if (colorReadback == null || colorReadback.capacity() < colorBytes) {
         colorReadback = BufferUtils.createByteBuffer(colorBytes);
      }
      if (copyDepth && (depthReadback == null || depthReadback.capacity() < pixels)) {
         depthReadback = BufferUtils.createFloatBuffer(pixels);
      }
   }

   private static void ensureTextureUploadCapacity(int bytes) {
      if (textureUploadBuffer == null || textureUploadBuffer.capacity() < bytes) {
         textureUploadBuffer = BufferUtils.createByteBuffer(bytes);
      }
   }

   private static void setColor(int rgb, float scale, float alpha) {
      float red = ((rgb >> 16) & 255) / 255.0F * scale;
      float green = ((rgb >> 8) & 255) / 255.0F * scale;
      float blue = (rgb & 255) / 255.0F * scale;
      GL11.glColor4f(red, green, blue, alpha);
   }

   private static void setTextureShade(int shade, boolean smooth) {
      float scale;
      if (smooth) {
         scale = (127 - shade) * 2 / 256.0F;
      } else {
         int normalized = shade;
         if (normalized < 0) {
            normalized = 0;
         } else if (normalized > 127) {
            normalized = 127;
         }

         int bank = normalized >> 4 & 3;
         int shift = normalized >> 6;
         float bankScale;
         if (bank == 0) {
            bankScale = 1.0F;
         } else if (bank == 1) {
            bankScale = 0.875F;
         } else if (bank == 2) {
            bankScale = 0.75F;
         } else {
            bankScale = 0.625F;
         }
         scale = bankScale / (1 << shift);
      }

      if (scale < 0.0F) {
         scale = 0.0F;
      } else if (scale > 1.0F) {
         scale = 1.0F;
      }
      GL11.glColor4f(scale, scale, scale, 1.0F);
   }

   private static double projected(double base, double slopeX, double slopeY, int x, int y) {
      double xOffset = x - Rasterizer3D.viewportCenterX;
      double yOffset = y - Rasterizer3D.viewportCenterY;
      return (base + slopeY * yOffset) * 8.0D + slopeX * xOffset;
   }

   private static double maxAbs(double... values) {
      double max = 0.0D;
      for (double value : values) {
         double absolute = Math.abs(value);
         if (absolute > max) {
            max = absolute;
         }
      }
      return max;
   }

   private static float clampDepth(float depth) {
      if (depth < 0.0F) {
         return 0.0F;
      }
      if (depth >= DEPTH_SCALE) {
         return DEPTH_SCALE - 1.0F;
      }
      return depth;
   }

   private static void fail(Throwable failure) {
      unavailable = true;
      if (!failureLogged) {
         failureLogged = true;
         System.err.println("GPU renderer unavailable; falling back to software Rasterizer3D.");
         failure.printStackTrace();
      }
      destroyContext();
   }

   private static void destroyContext() {
      if (pbuffer != null) {
         try {
            pbuffer.destroy();
         } catch (Throwable ignored) {
         }
      }
      pbuffer = null;
      bufferWidth = 0;
      bufferHeight = 0;
      viewportWidth = -1;
      viewportHeight = -1;
      Arrays.fill(textureIds, 0);
      Arrays.fill(textureDirty, true);
   }

   private static final class Bounds {
      final int minX;
      final int minY;
      final int maxX;
      final int maxY;

      private Bounds(int minX, int minY, int maxX, int maxY) {
         this.minX = minX;
         this.minY = minY;
         this.maxX = maxX;
         this.maxY = maxY;
      }

      int width() {
         return this.maxX - this.minX;
      }

      int height() {
         return this.maxY - this.minY;
      }

      static Bounds of(int x0, int y0, int x1, int y1, int x2, int y2) {
         int minX = Math.max(Rasterizer2D.topX, Math.min(x0, Math.min(x1, x2)));
         int minY = Math.max(Rasterizer2D.topY, Math.min(y0, Math.min(y1, y2)));
         int maxX = Math.min(Rasterizer2D.bottomX, Math.max(x0, Math.max(x1, x2)) + 1);
         int maxY = Math.min(Rasterizer2D.bottomY, Math.max(y0, Math.max(y1, y2)) + 1);
         if (minX >= maxX || minY >= maxY) {
            return null;
         }
         return new Bounds(minX, minY, maxX, maxY);
      }
   }
}
