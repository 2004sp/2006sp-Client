package client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

/**
 * GPU-backed triangle rasterizer for the legacy software client.
 *
 * Scene triangles are accumulated into a VBO-backed atlas batch and rasterized
 * by OpenGL. When the AWTGL presentation canvas is available, the main scene is
 * rendered straight into that canvas' backbuffer; only the legacy software UI is
 * uploaded before the same backbuffer is swapped. A Pbuffer remains as the GPU
 * compatibility path when the AWT canvas is unavailable. Synchronous readback is
 * reserved for mid-frame software fallback and legacy bounded draws.
 */
final class GpuRasterizer3D {
   private static final float DEPTH_SCALE = 1048576.0F;
   private static final int GL_CLAMP_TO_EDGE = 33071;
   private static final int GL_BGRA = 32993;
   private static final int TEXTURE_COUNT = 51;
   private static final int TEXTURE_GRID_SIZE = 8;
   private static final int WHITE_TEXTURE_CELL = 63;
   private static final int BATCH_NONE = 0;
   private static final int BATCH_TEXTURED = 1;
   private static final int BATCH_PARTICLE = 2;
   private static final int FLOATS_PER_VERTEX = 11;
   private static final int VERTEX_STRIDE_BYTES = FLOATS_PER_VERTEX * 4;
   private static final int MAX_BATCH_VERTICES = 24576;
   private static final int PARTICLE_SEGMENTS = 16;
   private static final int UI_TRANSPARENT_KEY = 0x00010203;

   private static volatile boolean requested = true;
   private static boolean unavailable;
   private static boolean failureLogged;

   private static boolean frameOpen;
   private static boolean frameActive;
   private static boolean frameSoftwareFallback;
   private static boolean frameDirectPresentation;
   private static boolean directFrameReady;
   private static boolean directModeLogged;

   private static GpuPresentationCanvas presentationCanvas;
   private static boolean directPresentationExecution;
   private static boolean rendererUsesPresentationContext;
   private static int directUiWidth;
   private static int directUiHeight;
   private static int directTargetX;
   private static int directTargetY;
   private static int directTargetWidth;
   private static int directTargetHeight;
   private static int directSceneX;
   private static int directSceneY;
   private static int directCanvasHeight;

   private static Pbuffer pbuffer;
   private static int bufferWidth;
   private static int bufferHeight;
   private static int viewportWidth = -1;
   private static int viewportHeight = -1;
   private static int scissorX = Integer.MIN_VALUE;
   private static int scissorY = Integer.MIN_VALUE;
   private static int scissorWidth = Integer.MIN_VALUE;
   private static int scissorHeight = Integer.MIN_VALUE;

   private static final boolean[] textureDirty = new boolean[TEXTURE_COUNT];
   private static boolean cachedLowMemory = Rasterizer3D.lowMemory;
   private static int atlasTexture;
   private static int atlasTextureSize;
   private static int legacyTextureSize;
   private static int shaderProgram;
   private static int uniformAtlas;
   private static int uniformTextureSize;
   private static int uniformFogEnabled;
   private static int uniformFogStart;
   private static int uniformFogEnd;
   private static int uniformDepthScale;
   private static int uniformFogColor;

   private static ByteBuffer colorReadback;
   private static FloatBuffer depthReadback;
   private static ByteBuffer textureUploadBuffer;

   private static FloatBuffer vertexBatch = BufferUtils.createFloatBuffer(MAX_BATCH_VERTICES * FLOATS_PER_VERTEX);
   private static int vertexBufferObject;
   private static int batchMode = BATCH_NONE;
   private static int batchTexture;
   private static int batchVertexCount;

   private static final int[] colorPbos = new int[2];
   private static final boolean[] colorPboReady = new boolean[2];
   private static int colorPboWriteIndex;
   private static int colorPboBytes;
   private static boolean pboUnavailable;

   private static boolean frameFogEnabled;
   private static float frameFogStart;
   private static float frameFogEnd;

   static {
      Arrays.fill(textureDirty, true);
   }

   private GpuRasterizer3D() {
   }

   static boolean isRequested() {
      return requested;
   }

   static void setPresentationCanvas(GpuPresentationCanvas canvas) {
      presentationCanvas = canvas;
   }

   static void setEnabled(boolean enabled) {
      requested = enabled;
      directFrameReady = false;
      if (enabled) {
         unavailable = false;
      } else if (presentationCanvas != null) {
         presentationCanvas.deactivate();
      }
      System.out.println("Renderer: " + (enabled ? "GPU" : "software"));
   }

   static boolean renderSceneFrame(
      final int uiWidth,
      final int uiHeight,
      final int targetX,
      final int targetY,
      final int targetWidth,
      final int targetHeight,
      final int sceneX,
      final int sceneY,
      final boolean fogEnabled,
      final float fogDistanceOffset,
      final Runnable renderer
   ) {
      if (renderer == null) {
         return false;
      }

      if (requested
         && !unavailable
         && presentationCanvas != null
         && presentationCanvas.isContextReady()) {
         if (pbuffer != null) {
            destroyContext();
         }

         final boolean[] rendered = new boolean[1];
         final boolean[] completed = new boolean[1];
         presentationCanvas.runInContext(new Runnable() {
            @Override
            public void run() {
               directPresentationExecution = true;
               directUiWidth = Math.max(1, uiWidth);
               directUiHeight = Math.max(1, uiHeight);
               directTargetX = targetX;
               directTargetY = targetY;
               directTargetWidth = Math.max(1, targetWidth);
               directTargetHeight = Math.max(1, targetHeight);
               directSceneX = sceneX;
               directSceneY = sceneY;
               directCanvasHeight = Math.max(1, presentationCanvas.getHeight());

               try {
                  beginFrame(fogEnabled, fogDistanceOffset);
                  prepareDirectUiOverlayBuffer();
                  rendered[0] = true;
                  renderer.run();
                  completed[0] = endFrame();
               } finally {
                  if (frameOpen) {
                     endFrame();
                  }
                  directPresentationExecution = false;
               }
            }
         });

         if (rendered[0]) {
            return completed[0];
         }
      }

      beginFrame(fogEnabled, fogDistanceOffset);
      prepareDirectUiOverlayBuffer();
      renderer.run();
      return endFrame();
   }

   static void beginFrame() {
      beginFrame(false, 0.0F);
   }

   static boolean beginFrame(boolean fogEnabled, float fogDistanceOffset) {
      if (frameOpen) {
         endFrame();
      }

      frameOpen = true;
      frameActive = false;
      frameSoftwareFallback = false;
      frameDirectPresentation = false;
      directFrameReady = false;
      frameFogEnabled = fogEnabled;
      frameFogStart = 1430.0F + fogDistanceOffset;
      frameFogEnd = 2100.0F + fogDistanceOffset;
      resetBatch();

      if (!baseAvailable()) {
         frameSoftwareFallback = true;
         return false;
      }

      try {
         if (directPresentationExecution) {
            ensurePresentationContextResources();
            frameDirectPresentation = true;
         } else {
            if (presentationCanvas != null
               && !presentationCanvas.isContextReady()
               && !presentationCanvas.hasFailed()) {
               presentationCanvas.requestInitialization();
            }

            ensureContext(Rasterizer2D.width, Rasterizer2D.height);
            makeCurrent();
            frameDirectPresentation = false;
         }

         configureViewport(Rasterizer2D.width, Rasterizer2D.height);
         ensureTextureAtlas();

         GL11.glEnable(GL11.GL_SCISSOR_TEST);
         setLogicalScissor(0, 0, viewportWidth, viewportHeight);
         GL11.glColorMask(true, true, true, true);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         GL11.glDisable(GL11.GL_BLEND);
         GL11.glDisable(GL11.GL_FOG);
         GL11.glEnable(GL11.GL_DEPTH_TEST);
         GL11.glDepthFunc(GL11.GL_ALWAYS);
         GL11.glDepthMask(true);
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
         GL11.glColorMask(true, true, true, false);
         frameActive = true;
         return true;
      } catch (Throwable failure) {
         frameSoftwareFallback = true;
         fail(failure);
         return false;
      }
   }

   static boolean isFrameActive() {
      return frameOpen && frameActive && !frameSoftwareFallback;
   }

   static void prepareDirectUiOverlayBuffer() {
      if (isFrameActive() && frameDirectPresentation && canUseDirectPresentation() && Rasterizer2D.pixels != null) {
         Arrays.fill(Rasterizer2D.pixels, UI_TRANSPARENT_KEY);
      }
   }

   static boolean endFrame() {
      return endFrame(false);
   }

   static boolean endFrame(boolean ignoredCopyDepth) {
      if (!frameOpen) {
         return false;
      }

      boolean completed = false;
      try {
         if (frameActive) {
            flushBatch();
            if (frameDirectPresentation && canUseDirectPresentation()) {
               finishDirectPresentationFrame();
               directFrameReady = true;
            } else {
               readBackFrameAsync();
               if (presentationCanvas != null && !isDirectPresentationTransitioning()) {
                  presentationCanvas.deactivate();
               }
            }
            completed = true;
         }
      } catch (Throwable failure) {
         frameActive = false;
         fail(failure);
      } finally {
         frameOpen = false;
         frameActive = false;
         frameSoftwareFallback = false;
         frameDirectPresentation = false;
         resetBatch();
      }
      return completed;
   }

   static boolean presentDirectFrame(
      int[] uiPixels,
      int uiWidth,
      int uiHeight,
      int targetX,
      int targetY,
      int targetWidth,
      int targetHeight,
      int sceneX,
      int sceneY,
      int sceneWidth,
      int sceneHeight
   ) {
      if (!requested
         || !directFrameReady
         || presentationCanvas == null
         || !presentationCanvas.isContextReady()) {
         return false;
      }

      boolean queued = presentationCanvas.presentFrame(
         uiPixels,
         uiWidth,
         uiHeight,
         targetX,
         targetY,
         targetWidth,
         targetHeight,
         sceneX,
         sceneY,
         sceneWidth,
         sceneHeight
      );
      directFrameReady = false;
      if (!queued) {
         presentationCanvas.deactivate();
      }
      return queued;
   }

   private static boolean canUseDirectPresentation() {
      return presentationCanvas != null && presentationCanvas.isContextReady();
   }

   static boolean isDirectPresentationTransitioning() {
      return requested
         && presentationCanvas != null
         && !presentationCanvas.hasFailed()
         && !presentationCanvas.isContextReady();
   }

   private static void finishDirectPresentationFrame() {
      if (presentationCanvas != null) {
         presentationCanvas.markSceneBackbufferPending();
      }
      if (!directModeLogged) {
         directModeLogged = true;
         System.out.println(
            "GPU presentation mode: DIRECT (scene rendered into AWTGLCanvas backbuffer)."
         );
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
      if (batched) {
         try {
            queueDepthTriangle(y0, x0, depth0, y1, x1, depth1, y2, x2, depth2);
            return true;
         } catch (Throwable failure) {
            failCurrentFrame(failure);
            return false;
         }
      }

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
      if (batched) {
         try {
            float sourceAlpha = legacySourceAlpha();
            queueColorTriangle(
               x0, y0, depth0, rgb,
               x1, y1, depth1, rgb,
               x2, y2, depth2, rgb,
               sourceAlpha
            );
            return true;
         } catch (Throwable failure) {
            failCurrentFrame(failure);
            return false;
         }
      }

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
      if (batched) {
         try {
            float sourceAlpha = legacySourceAlpha();
            queueColorTriangle(
               x0, y0, depth0, Rasterizer3D.HSL_TO_RGB[color0 & 65535],
               x1, y1, depth1, Rasterizer3D.HSL_TO_RGB[color1 & 65535],
               x2, y2, depth2, Rasterizer3D.HSL_TO_RGB[color2 & 65535],
               sourceAlpha
            );
            return true;
         } catch (Throwable failure) {
            failCurrentFrame(failure);
            return false;
         }
      }

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
         if (ensureTexture(textureId) == 0) {
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

         if (batched) {
            queueTexturedTriangle(
               textureId,
               x0, y0, depth0, textureShadeScale(shade0, smoothTextureLight), (float)(pu0 * coordinateScale), (float)(pv0 * coordinateScale), (float)(pw0 * coordinateScale),
               x1, y1, depth1, textureShadeScale(shade1, smoothTextureLight), (float)(pu1 * coordinateScale), (float)(pv1 * coordinateScale), (float)(pw1 * coordinateScale),
               x2, y2, depth2, textureShadeScale(shade2, smoothTextureLight), (float)(pu2 * coordinateScale), (float)(pv2 * coordinateScale), (float)(pw2 * coordinateScale)
            );
            return true;
         }

         // The atlas-backed textured path is frame-batched. Auxiliary
         // textured triangle calls outside a scene frame stay on the original
         // software rasterizer rather than sampling the wrong atlas cell.
         return false;
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
      if (!frameOpen && rendererUsesPresentationContext) {
         return false;
      }
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
            setLogicalScissor(minX, minY, maxX, maxY);
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

   private static void setLogicalScissor(int minX, int minY, int maxX, int maxY) {
      if (!frameDirectPresentation) {
         setScissor(minX, viewportHeight - maxY, maxX - minX, maxY - minY);
         return;
      }

      int uiLeft = directSceneX + minX;
      int uiTop = directSceneY + minY;
      int uiRight = directSceneX + maxX;
      int uiBottom = directSceneY + maxY;

      int left = directTargetX + scaleFloor(uiLeft, directUiWidth, directTargetWidth);
      int right = directTargetX + scaleCeil(uiRight, directUiWidth, directTargetWidth);
      int top = directTargetY + scaleFloor(uiTop, directUiHeight, directTargetHeight);
      int bottom = directTargetY + scaleCeil(uiBottom, directUiHeight, directTargetHeight);

      left = Math.max(directTargetX, Math.min(directTargetX + directTargetWidth, left));
      right = Math.max(left, Math.min(directTargetX + directTargetWidth, right));
      top = Math.max(directTargetY, Math.min(directTargetY + directTargetHeight, top));
      bottom = Math.max(top, Math.min(directTargetY + directTargetHeight, bottom));

      setScissor(left, directCanvasHeight - bottom, right - left, bottom - top);
   }

   private static void setScissor(int x, int y, int width, int height) {
      if (x == scissorX && y == scissorY && width == scissorWidth && height == scissorHeight) {
         return;
      }
      if (batchVertexCount > 0) {
         flushBatch();
      }
      GL11.glScissor(x, y, width, height);
      scissorX = x;
      scissorY = y;
      scissorWidth = width;
      scissorHeight = height;
   }

   private static float configureLegacyBlend(boolean batched) {
      float sourceAlpha = legacySourceAlpha();
      if (!batched || sourceAlpha >= 0.99999F) {
         GL11.glDisable(GL11.GL_BLEND);
         return sourceAlpha;
      }
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      return sourceAlpha;
   }

   private static float legacySourceAlpha() {
      int alpha = Rasterizer3D.alpha;
      if (alpha <= 0) {
         return 1.0F;
      }
      if (alpha >= 256) {
         return 0.0F;
      }
      return (256 - alpha) / 256.0F;
   }

   private static void fallbackCurrentFrame() {
      if (!frameOpen || frameSoftwareFallback) {
         return;
      }

      if (frameActive) {
         try {
            flushBatch();
            readBackFrameSynchronous(true);
         } catch (Throwable failure) {
            frameActive = false;
            frameSoftwareFallback = true;
            fail(failure);
            return;
         }
      }

      frameActive = false;
      frameSoftwareFallback = true;
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
   }

   private static void failCurrentFrame(Throwable failure) {
      if (frameOpen && frameActive) {
         try {
            flushBatch();
            readBackFrameSynchronous(true);
         } catch (Throwable ignored) {
         }
      }

      frameActive = false;
      if (frameOpen) {
         frameSoftwareFallback = true;
      }
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
      fail(failure);
   }

   private static void ensureContext(int width, int height) throws Exception {
      if (rendererUsesPresentationContext) {
         resetRendererResourceHandles();
      }

      boolean recreate = pbuffer == null
         || pbuffer.isBufferLost()
         || width > bufferWidth
         || height > bufferHeight;
      if (!recreate) {
         return;
      }

      destroyContext();
      bufferWidth = Math.max(width, 765);
      bufferHeight = Math.max(height, 503);

      PixelFormat pixelFormat = new PixelFormat().withAlphaBits(8).withDepthBits(24);
      pbuffer = new Pbuffer(
         bufferWidth,
         bufferHeight,
         pixelFormat,
         null
      );
      pbuffer.makeCurrent();
      initializeCurrentContextResources(false);

      System.out.println(
         "GPU renderer initialized: OpenGL Pbuffer "
            + bufferWidth + "x" + bufferHeight
            + " [VBO atlas batching, double-PBO color readback, GPU fog/depth]"
      );
   }

   private static void ensurePresentationContextResources() {
      if (rendererUsesPresentationContext && vertexBufferObject != 0) {
         return;
      }
      if (pbuffer != null) {
         throw new IllegalStateException("Pbuffer remained active while entering direct canvas rendering");
      }

      resetRendererResourceHandles();
      initializeCurrentContextResources(true);
      System.out.println(
         "GPU renderer initialized in AWTGLCanvas context "
            + "[VBO atlas batching, direct backbuffer presentation, GPU fog/depth]"
      );
   }

   private static void initializeCurrentContextResources(boolean presentationContext) {
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

      vertexBufferObject = GL15.glGenBuffers();
      shaderProgram = 0;
      atlasTexture = 0;
      atlasTextureSize = 0;
      legacyTextureSize = 0;
      initializeSceneShader();
      ensureTextureAtlas();
      Arrays.fill(colorPbos, 0);
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
      colorPboBytes = 0;
      pboUnavailable = false;
      resetBatch();

      Arrays.fill(textureDirty, true);
      cachedLowMemory = Rasterizer3D.lowMemory;
      viewportWidth = -1;
      viewportHeight = -1;
      scissorX = Integer.MIN_VALUE;
      scissorY = Integer.MIN_VALUE;
      scissorWidth = Integer.MIN_VALUE;
      scissorHeight = Integer.MIN_VALUE;
      rendererUsesPresentationContext = presentationContext;
   }

   static void presentationContextLost(GpuPresentationCanvas canvas) {
      if (presentationCanvas != canvas) {
         return;
      }
      directFrameReady = false;
      frameDirectPresentation = false;
      directModeLogged = false;
      if (rendererUsesPresentationContext) {
         resetRendererResourceHandles();
      }
   }

   private static void resetRendererResourceHandles() {
      rendererUsesPresentationContext = false;
      vertexBufferObject = 0;
      shaderProgram = 0;
      atlasTexture = 0;
      atlasTextureSize = 0;
      legacyTextureSize = 0;
      Arrays.fill(colorPbos, 0);
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
      colorPboBytes = 0;
      pboUnavailable = false;
      viewportWidth = -1;
      viewportHeight = -1;
      scissorX = Integer.MIN_VALUE;
      scissorY = Integer.MIN_VALUE;
      scissorWidth = Integer.MIN_VALUE;
      scissorHeight = Integer.MIN_VALUE;
      resetBatch();
      Arrays.fill(textureDirty, true);
   }

   private static void makeCurrent() throws Exception {
      if (!pbuffer.isCurrent()) {
         pbuffer.makeCurrent();
      }
   }

   private static void configureViewport(int width, int height) {
      viewportWidth = width;
      viewportHeight = height;

      if (frameDirectPresentation) {
         GL11.glViewport(
            directTargetX,
            directCanvasHeight - directTargetY - directTargetHeight,
            directTargetWidth,
            directTargetHeight
         );
         GL11.glMatrixMode(GL11.GL_PROJECTION);
         GL11.glLoadIdentity();
         GL11.glOrtho(
            -directSceneX,
            directUiWidth - directSceneX,
            directUiHeight - directSceneY,
            -directSceneY,
            0.0D,
            DEPTH_SCALE
         );
         GL11.glMatrixMode(GL11.GL_MODELVIEW);
         GL11.glLoadIdentity();
         return;
      }

      GL11.glViewport(0, 0, width, height);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, width, height, 0.0D, 0.0D, DEPTH_SCALE);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
   }

   private static int ensureTexture(int textureId) {
      ensureTextureAtlas();
      if (textureId < 0 || textureId >= TEXTURE_COUNT) {
         return 0;
      }
      if (textureDirty[textureId] && !uploadTextureCell(textureId)) {
         return 0;
      }
      return atlasTexture;
   }

   private static void ensureTextureAtlas() {
      int wantedTextureSize = Rasterizer3D.lowMemory ? 64 : 128;
      int wantedAtlasSize = wantedTextureSize * TEXTURE_GRID_SIZE;
      if (atlasTexture != 0 && legacyTextureSize == wantedTextureSize && atlasTextureSize == wantedAtlasSize) {
         return;
      }

      if (atlasTexture != 0) {
         GL11.glDeleteTextures(atlasTexture);
      }
      atlasTexture = GL11.glGenTextures();
      atlasTextureSize = wantedAtlasSize;
      legacyTextureSize = wantedTextureSize;
      cachedLowMemory = Rasterizer3D.lowMemory;

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasTexture);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      GL11.glTexImage2D(
         GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
         atlasTextureSize, atlasTextureSize, 0,
         GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null
      );

      Arrays.fill(textureDirty, true);
      uploadWhiteTextureCell();
   }

   private static void uploadWhiteTextureCell() {
      int pixelCount = legacyTextureSize * legacyTextureSize;
      ensureTextureUploadCapacity(pixelCount * 4);
      textureUploadBuffer.clear();
      for (int i = 0; i < pixelCount; i++) {
         textureUploadBuffer.put((byte)255);
         textureUploadBuffer.put((byte)255);
         textureUploadBuffer.put((byte)255);
         textureUploadBuffer.put((byte)255);
      }
      textureUploadBuffer.flip();

      int cellX = (WHITE_TEXTURE_CELL & 7) * legacyTextureSize;
      int cellY = (WHITE_TEXTURE_CELL >> 3) * legacyTextureSize;
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasTexture);
      GL11.glTexSubImage2D(
         GL11.GL_TEXTURE_2D, 0,
         cellX, cellY, legacyTextureSize, legacyTextureSize,
         GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureUploadBuffer
      );
   }

   private static boolean uploadTextureCell(int textureId) {
      int[] pixels = Rasterizer3D.getGpuTexturePixels(textureId);
      if (pixels == null) {
         return false;
      }

      int pixelCount = legacyTextureSize * legacyTextureSize;
      ensureTextureUploadCapacity(pixelCount * 4);
      textureUploadBuffer.clear();
      for (int i = 0; i < pixelCount; i++) {
         int rgb = pixels[i];
         textureUploadBuffer.put((byte)(rgb >> 16));
         textureUploadBuffer.put((byte)(rgb >> 8));
         textureUploadBuffer.put((byte)rgb);
         textureUploadBuffer.put((byte)(rgb == 0 ? 0 : 255));
      }
      textureUploadBuffer.flip();

      int cellX = (textureId & 7) * legacyTextureSize;
      int cellY = (textureId >> 3) * legacyTextureSize;
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasTexture);
      GL11.glTexSubImage2D(
         GL11.GL_TEXTURE_2D, 0,
         cellX, cellY, legacyTextureSize, legacyTextureSize,
         GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureUploadBuffer
      );
      textureDirty[textureId] = false;
      return true;
   }

   private static void initializeSceneShader() {
      if (shaderProgram != 0) {
         return;
      }

      String vertexSource =
         "#version 120\n"
            + "varying vec4 vLegacyTex;\n"
            + "varying vec4 vColor;\n"
            + "void main() {\n"
            + "  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n"
            + "  vLegacyTex = gl_MultiTexCoord0;\n"
            + "  vColor = gl_Color;\n"
            + "}\n";

      String fragmentSource =
         "#version 120\n"
            + "uniform sampler2D uAtlas;\n"
            + "uniform float uTextureSize;\n"
            + "uniform float uFogEnabled;\n"
            + "uniform float uFogStart;\n"
            + "uniform float uFogEnd;\n"
            + "uniform float uDepthScale;\n"
            + "uniform vec3 uFogColor;\n"
            + "varying vec4 vLegacyTex;\n"
            + "varying vec4 vColor;\n"
            + "void main() {\n"
            + "  float q = vLegacyTex.w;\n"
            + "  if (abs(q) < 0.0000001) discard;\n"
            + "  float inset = 0.5 / uTextureSize;\n"
            + "  float localS = clamp(vLegacyTex.x / q, inset, 1.0 - inset);\n"
            + "  float localT = fract(vLegacyTex.y / q);\n"
            + "  localT = clamp(localT, inset, 1.0 - inset);\n"
            + "  float cell = floor(vLegacyTex.z + 0.5);\n"
            + "  vec2 cellXY = vec2(mod(cell, 8.0), floor(cell / 8.0));\n"
            + "  vec2 uv = (cellXY + vec2(localS, localT)) / 8.0;\n"
            + "  vec4 texel = texture2D(uAtlas, uv);\n"
            + "  if (texel.a < 0.5) discard;\n"
            + "  vec4 color = vec4(texel.rgb * vColor.rgb, vColor.a);\n"
            + "  if (uFogEnabled > 0.5) {\n"
            + "    float sceneDepth = gl_FragCoord.z * uDepthScale;\n"
            + "    float fogAmount = 0.0;\n"
            + "    if (sceneDepth >= uFogEnd) {\n"
            + "      fogAmount = 1.0;\n"
            + "    } else if (sceneDepth >= uFogStart) {\n"
            + "      fogAmount = clamp((sceneDepth - uFogStart) / 768.0, 0.0, 1.0);\n"
            + "    }\n"
            + "    color.rgb = mix(color.rgb, uFogColor, fogAmount);\n"
            + "  }\n"
            + "  gl_FragColor = color;\n"
            + "}\n";

      int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
      int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);
      shaderProgram = GL20.glCreateProgram();
      GL20.glAttachShader(shaderProgram, vertexShader);
      GL20.glAttachShader(shaderProgram, fragmentShader);
      GL20.glLinkProgram(shaderProgram);
      if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
         throw new IllegalStateException("GPU shader link failed: " + GL20.glGetProgramInfoLog(shaderProgram, 4096));
      }
      GL20.glDeleteShader(vertexShader);
      GL20.glDeleteShader(fragmentShader);

      uniformAtlas = GL20.glGetUniformLocation(shaderProgram, "uAtlas");
      uniformTextureSize = GL20.glGetUniformLocation(shaderProgram, "uTextureSize");
      uniformFogEnabled = GL20.glGetUniformLocation(shaderProgram, "uFogEnabled");
      uniformFogStart = GL20.glGetUniformLocation(shaderProgram, "uFogStart");
      uniformFogEnd = GL20.glGetUniformLocation(shaderProgram, "uFogEnd");
      uniformDepthScale = GL20.glGetUniformLocation(shaderProgram, "uDepthScale");
      uniformFogColor = GL20.glGetUniformLocation(shaderProgram, "uFogColor");

      GL20.glUseProgram(shaderProgram);
      GL20.glUniform1i(uniformAtlas, 0);
      GL20.glUniform1f(uniformDepthScale, DEPTH_SCALE);
      GL20.glUniform3f(uniformFogColor, 149.0F / 255.0F, 150.0F / 255.0F, 152.0F / 255.0F);
      GL20.glUseProgram(0);
   }

   private static int compileShader(int type, String source) {
      int shader = GL20.glCreateShader(type);
      GL20.glShaderSource(shader, source);
      GL20.glCompileShader(shader);
      if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
         throw new IllegalStateException("GPU shader compile failed: " + GL20.glGetShaderInfoLog(shader, 4096));
      }
      return shader;
   }

   private static void useSceneShader(boolean fogEnabled) {
      initializeSceneShader();
      GL20.glUseProgram(shaderProgram);
      GL20.glUniform1f(uniformTextureSize, legacyTextureSize);
      GL20.glUniform1f(uniformFogEnabled, fogEnabled ? 1.0F : 0.0F);
      GL20.glUniform1f(uniformFogStart, frameFogStart);
      GL20.glUniform1f(uniformFogEnd, frameFogEnd);
   }

   private static void readBackFrameAsync() {
      if (viewportWidth <= 0 || viewportHeight <= 0) {
         return;
      }

      int width = viewportWidth;
      int height = viewportHeight;
      int bytes = width * height * 4;
      ensureReadbackCapacity(width * height, false);

      if (pboUnavailable) {
         readBackFrameSynchronous(false);
         return;
      }

      try {
         ensureColorPbos(bytes);
         int writeIndex = colorPboWriteIndex;
         int readIndex = writeIndex ^ 1;

         // Queue this frame's GPU->PBO transfer without providing a CPU pointer.
         // Orphaning the destination avoids waiting for an older transfer that
         // may still be using the same storage.
         GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, colorPbos[writeIndex]);
         GL15.glBufferData(GL21.GL_PIXEL_PACK_BUFFER, (long)bytes, GL15.GL_STREAM_READ);
         GL11.glReadPixels(0, 0, width, height, GL_BGRA, GL11.GL_UNSIGNED_BYTE, 0L);
         colorPboReady[writeIndex] = true;

         // Consume the other PBO. Its readback was issued a frame earlier, so
         // the transfer normally completes while Java renders the next frame.
         int consumeIndex = colorPboReady[readIndex] ? readIndex : writeIndex;
         GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, colorPbos[consumeIndex]);
         ByteBuffer mapped = GL15.glMapBuffer(GL21.GL_PIXEL_PACK_BUFFER, GL15.GL_READ_ONLY, (long)bytes, null);
         if (mapped == null) {
            throw new IllegalStateException("Unable to map completed GPU color PBO");
         }
         mapped.order(ByteOrder.nativeOrder());
         copyColorReadback(mapped, width, height);
         GL15.glUnmapBuffer(GL21.GL_PIXEL_PACK_BUFFER);

         GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
         colorPboWriteIndex = readIndex;
      } catch (Throwable failure) {
         // PBOs are an optimization, not a requirement. Keep GPU rendering
         // available on drivers that support the scene path but not PBOs.
         try {
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
         } catch (Throwable ignored) {
         }
         pboUnavailable = true;
         System.err.println("GPU PBO readback unavailable; using synchronous color readback.");
         readBackFrameSynchronous(false);
      }
   }

   private static void readBackFrameSynchronous(boolean copyDepth) {
      if (frameDirectPresentation) {
         readBackDirectFrameSynchronous(copyDepth);
         return;
      }
      if (viewportWidth <= 0 || viewportHeight <= 0) {
         return;
      }

      int width = viewportWidth;
      int height = viewportHeight;
      int count = width * height;
      ensureReadbackCapacity(count, copyDepth);

      GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      colorReadback.clear();
      colorReadback.limit(count * 4);
      GL11.glReadPixels(0, 0, width, height, GL_BGRA, GL11.GL_UNSIGNED_BYTE, colorReadback);
      copyColorReadback(colorReadback, width, height);

      if (copyDepth) {
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
   }

   private static void readBackDirectFrameSynchronous(boolean copyDepth) {
      if (viewportWidth <= 0
         || viewportHeight <= 0
         || directUiWidth <= 0
         || directUiHeight <= 0
         || directTargetWidth <= 0
         || directTargetHeight <= 0) {
         return;
      }

      long physicalCountLong = (long)directTargetWidth * (long)directTargetHeight;
      if (physicalCountLong > Integer.MAX_VALUE / 4L) {
         throw new IllegalStateException("Direct presentation target is too large to read back");
      }
      int physicalCount = (int)physicalCountLong;
      ensureReadbackCapacity(physicalCount, copyDepth);
      int readY = directCanvasHeight - directTargetY - directTargetHeight;

      GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      colorReadback.clear();
      colorReadback.limit(physicalCount * 4);
      GL11.glReadPixels(
         directTargetX,
         readY,
         directTargetWidth,
         directTargetHeight,
         GL_BGRA,
         GL11.GL_UNSIGNED_BYTE,
         colorReadback
      );

      if (copyDepth) {
         depthReadback.clear();
         depthReadback.limit(physicalCount);
         GL11.glReadPixels(
            directTargetX,
            readY,
            directTargetWidth,
            directTargetHeight,
            GL11.GL_DEPTH_COMPONENT,
            GL11.GL_FLOAT,
            depthReadback
         );
      }

      colorReadback.rewind();
      IntBuffer packedColors = colorReadback.order(ByteOrder.nativeOrder()).asIntBuffer();
      for (int y = 0; y < viewportHeight; y++) {
         int physicalTopY = sampleScaledCoordinate(
            directSceneY + y,
            directUiHeight,
            directTargetHeight
         );
         int sourceY = directTargetHeight - 1 - physicalTopY;
         int destination = y * viewportWidth;

         for (int x = 0; x < viewportWidth; x++) {
            int sourceX = sampleScaledCoordinate(
               directSceneX + x,
               directUiWidth,
               directTargetWidth
            );
            int sourceIndex = sourceY * directTargetWidth + sourceX;
            Rasterizer2D.pixels[destination + x] = packedColors.get(sourceIndex) & 0x00FFFFFF;

            if (copyDepth) {
               float gpuDepth = depthReadback.get(sourceIndex);
               if (gpuDepth < 0.9999999F) {
                  Rasterizer2D.depthBuffer[destination + x] = gpuDepth * DEPTH_SCALE;
               }
            }
         }
      }
   }

   private static int sampleScaledCoordinate(int logicalCoordinate, int logicalSize, int physicalSize) {
      long numerator = ((long)logicalCoordinate * 2L + 1L) * physicalSize;
      int coordinate = (int)(numerator / (2L * logicalSize));
      if (coordinate < 0) {
         return 0;
      }
      if (coordinate >= physicalSize) {
         return physicalSize - 1;
      }
      return coordinate;
   }

   private static int scaleFloor(int value, int sourceSize, int targetSize) {
      return (int)((long)value * targetSize / sourceSize);
   }

   private static int scaleCeil(int value, int sourceSize, int targetSize) {
      return (int)(((long)value * targetSize + sourceSize - 1L) / sourceSize);
   }

   private static void copyColorReadback(ByteBuffer sourceBuffer, int width, int height) {
      sourceBuffer.rewind();
      IntBuffer packedColors = sourceBuffer.asIntBuffer();
      for (int readRow = 0; readRow < height; readRow++) {
         packedColors.position(readRow * width);
         packedColors.get(Rasterizer2D.pixels, (height - 1 - readRow) * width, width);
      }
   }

   private static void ensureColorPbos(int bytes) {
      if (colorPbos[0] == 0 || colorPbos[1] == 0) {
         colorPbos[0] = GL15.glGenBuffers();
         colorPbos[1] = GL15.glGenBuffers();
         Arrays.fill(colorPboReady, false);
         colorPboBytes = 0;
      }
      if (colorPboBytes != bytes) {
         for (int i = 0; i < colorPbos.length; i++) {
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, colorPbos[i]);
            GL15.glBufferData(GL21.GL_PIXEL_PACK_BUFFER, (long)bytes, GL15.GL_STREAM_READ);
            colorPboReady[i] = false;
         }
         GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
         colorPboBytes = bytes;
         colorPboWriteIndex = 0;
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
      GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
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

   private static float textureShadeScale(int shade, boolean smooth) {
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
         return 0.0F;
      }
      if (scale > 1.0F) {
         return 1.0F;
      }
      return scale;
   }

   static boolean drawParticleCircle(int centerX, int centerY, float depth, float radius, int rgb, float alpha) {
      if (!isFrameActive()) {
         return false;
      }
      if (radius <= 0.0F || centerX + radius < Rasterizer2D.topX || centerX - radius >= Rasterizer2D.bottomX
         || centerY + radius < Rasterizer2D.topY || centerY - radius >= Rasterizer2D.bottomY) {
         return true;
      }

      try {
         float clampedAlpha = alpha;
         if (clampedAlpha < 0.0F) clampedAlpha = 0.0F;
         if (clampedAlpha > 1.0F) clampedAlpha = 1.0F;
         float testDepth = clampDepth(Math.max(0.0F, depth - radius * 0.25F - 15.0F));
         float red = (rgb >> 16 & 255) / 255.0F;
         float green = (rgb >> 8 & 255) / 255.0F;
         float blue = (rgb & 255) / 255.0F;

         ensureBatch(BATCH_PARTICLE, 0, PARTICLE_SEGMENTS * 3);
         for (int segment = 0; segment < PARTICLE_SEGMENTS; segment++) {
            double angle0 = Math.PI * 2.0D * segment / PARTICLE_SEGMENTS;
            double angle1 = Math.PI * 2.0D * (segment + 1) / PARTICLE_SEGMENTS;
            putVertex(centerX, centerY, -testDepth, red, green, blue, clampedAlpha, 0.0F, 0.0F, 0.0F, 1.0F);
            putVertex(centerX + (float)Math.cos(angle0) * radius, centerY + (float)Math.sin(angle0) * radius, -testDepth, red, green, blue, clampedAlpha, 0.0F, 0.0F, 0.0F, 1.0F);
            putVertex(centerX + (float)Math.cos(angle1) * radius, centerY + (float)Math.sin(angle1) * radius, -testDepth, red, green, blue, clampedAlpha, 0.0F, 0.0F, 0.0F, 1.0F);
         }
         return true;
      } catch (Throwable failure) {
         failCurrentFrame(failure);
         return false;
      }
   }

   private static void queueDepthTriangle(
      float x0, float y0, float d0, float x1, float y1, float d1, float x2, float y2, float d2
   ) {
      ensureBatch(BATCH_TEXTURED, atlasTexture, 3);
      putVertex(x0, y0, -clampDepth(d0), 1.0F, 1.0F, 1.0F, 0.0F, 0.5F, 0.5F, WHITE_TEXTURE_CELL, 1.0F);
      putVertex(x1, y1, -clampDepth(d1), 1.0F, 1.0F, 1.0F, 0.0F, 0.5F, 0.5F, WHITE_TEXTURE_CELL, 1.0F);
      putVertex(x2, y2, -clampDepth(d2), 1.0F, 1.0F, 1.0F, 0.0F, 0.5F, 0.5F, WHITE_TEXTURE_CELL, 1.0F);
   }

   private static void queueColorTriangle(
      float x0, float y0, float d0, int rgb0,
      float x1, float y1, float d1, int rgb1,
      float x2, float y2, float d2, int rgb2,
      float alpha
   ) {
      ensureBatch(BATCH_TEXTURED, atlasTexture, 3);
      putColorVertex(x0, y0, d0, rgb0, alpha);
      putColorVertex(x1, y1, d1, rgb1, alpha);
      putColorVertex(x2, y2, d2, rgb2, alpha);
   }

   private static void putColorVertex(float x, float y, float depth, int rgb, float alpha) {
      putVertex(
         x, y, -clampDepth(depth),
         (rgb >> 16 & 255) / 255.0F,
         (rgb >> 8 & 255) / 255.0F,
         (rgb & 255) / 255.0F,
         alpha,
         0.5F, 0.5F, WHITE_TEXTURE_CELL, 1.0F
      );
   }

   private static void queueTexturedTriangle(
      int textureCell,
      float x0, float y0, float d0, float shade0, float s0, float t0, float q0,
      float x1, float y1, float d1, float shade1, float s1, float t1, float q1,
      float x2, float y2, float d2, float shade2, float s2, float t2, float q2
   ) {
      ensureBatch(BATCH_TEXTURED, atlasTexture, 3);
      putVertex(x0, y0, -clampDepth(d0), shade0, shade0, shade0, 1.0F, s0, t0, textureCell, q0);
      putVertex(x1, y1, -clampDepth(d1), shade1, shade1, shade1, 1.0F, s1, t1, textureCell, q1);
      putVertex(x2, y2, -clampDepth(d2), shade2, shade2, shade2, 1.0F, s2, t2, textureCell, q2);
   }

   private static void ensureBatch(int mode, int texture, int verticesNeeded) {
      if (batchVertexCount > 0 && (batchMode != mode || batchTexture != texture || batchVertexCount + verticesNeeded > MAX_BATCH_VERTICES)) {
         flushBatch();
      }
      if (batchMode == BATCH_NONE || batchVertexCount == 0) {
         batchMode = mode;
         batchTexture = texture;
      }
   }

   private static void putVertex(
      float x, float y, float z,
      float red, float green, float blue, float alpha,
      float s, float t, float r, float q
   ) {
      vertexBatch.put(x).put(y).put(z);
      vertexBatch.put(red).put(green).put(blue).put(alpha);
      vertexBatch.put(s).put(t).put(r).put(q);
      batchVertexCount++;
   }

   private static void flushBatch() {
      if (batchVertexCount <= 0 || batchMode == BATCH_NONE) {
         return;
      }

      vertexBatch.flip();
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
      GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBatch, GL15.GL_STREAM_DRAW);

      GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
      GL11.glVertexPointer(3, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, 0L);
      GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
      GL11.glColorPointer(4, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, 12L);

      GL11.glDepthMask(true);
      GL11.glDepthFunc(GL11.GL_ALWAYS);
      GL11.glShadeModel(GL11.GL_SMOOTH);

      if (batchMode == BATCH_TEXTURED) {
         GL11.glColorMask(true, true, true, false);
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
         GL11.glEnable(GL11.GL_TEXTURE_2D);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasTexture);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         GL11.glDisable(GL11.GL_FOG);
         useSceneShader(frameFogEnabled);
         GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         GL11.glTexCoordPointer(4, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, 28L);
      } else if (batchMode == BATCH_PARTICLE) {
         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_ALPHA_TEST);
         GL11.glDisable(GL11.GL_FOG);
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
         GL11.glDepthFunc(GL11.GL_LEQUAL);
         GL11.glDepthMask(false);
      }

      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, batchVertexCount);

      if (batchMode == BATCH_TEXTURED) {
         GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         GL20.glUseProgram(0);
      }
      GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
      GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

      GL11.glDepthFunc(GL11.GL_ALWAYS);
      GL11.glDepthMask(true);
      GL11.glColorMask(true, true, true, false);
      resetBatch();
   }

   private static void resetBatch() {
      batchMode = BATCH_NONE;
      batchTexture = 0;
      batchVertexCount = 0;
      vertexBatch.clear();
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
            if (!pbuffer.isCurrent()) {
               pbuffer.makeCurrent();
            }
            if (vertexBufferObject != 0) {
               GL15.glDeleteBuffers(vertexBufferObject);
            }
            if (shaderProgram != 0) {
               GL20.glDeleteProgram(shaderProgram);
            }
            if (atlasTexture != 0) {
               GL11.glDeleteTextures(atlasTexture);
            }
            for (int pbo : colorPbos) {
               if (pbo != 0) {
                  GL15.glDeleteBuffers(pbo);
               }
            }
         } catch (Throwable ignored) {
         }
         try {
            pbuffer.destroy();
         } catch (Throwable ignored) {
         }
      }
      pbuffer = null;
      rendererUsesPresentationContext = false;
      directFrameReady = false;
      frameDirectPresentation = false;
      directModeLogged = false;
      bufferWidth = 0;
      bufferHeight = 0;
      viewportWidth = -1;
      viewportHeight = -1;
      vertexBufferObject = 0;
      shaderProgram = 0;
      atlasTexture = 0;
      atlasTextureSize = 0;
      legacyTextureSize = 0;
      Arrays.fill(colorPbos, 0);
      Arrays.fill(colorPboReady, false);
      colorPboBytes = 0;
      colorPboWriteIndex = 0;
      resetBatch();
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
