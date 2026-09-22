package client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBSync;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GLSync;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

/**
 * GPU-backed triangle rasterizer for the legacy software client.
 *
 * Scene triangles are accumulated into a VBO-backed atlas batch and rasterized
 * by OpenGL. When the AWTGL presentation canvas is available, the game thread
 * renders into a Pbuffer that shares objects with the canvas context, copies the
 * completed scene into a shared texture, then lets the canvas composite the
 * legacy software UI and swap. While that canvas is starting, transitional
 * frames stay on the software rasterizer. A non-shared Pbuffer remains as the
 * GPU compatibility path when direct presentation is unavailable. Synchronous
 * readback is reserved for fallback and legacy bounded draws.
 */
final class GpuRasterizer3D {
   private static final float DEPTH_SCALE = 1048576.0F;
   private static final int GL_CLAMP_TO_EDGE = 33071;
   private static final int GL_BGRA = 32993;
   private static final int GL_DEPTH_COMPONENT24 = 33190;
   private static final int TEXTURE_COUNT = 51;
   private static final int TEXTURE_GRID_SIZE = 8;
   private static final int WHITE_TEXTURE_CELL = 63;
   private static final int BATCH_NONE = 0;
   private static final int BATCH_DEPTH_ONLY = 1;
   private static final int BATCH_OPAQUE = 2;
   private static final int BATCH_TRANSLUCENT = 3;
   private static final int BATCH_PARTICLE = 4;
   private static final int FLOATS_PER_VERTEX = 11;
   private static final int VERTEX_STRIDE_BYTES = FLOATS_PER_VERTEX * 4;
   private static final int MAX_BATCH_VERTICES = 98304;
   private static final int PARTICLE_SEGMENTS = 16;
   private static final int BUFFER_SHRINK_RATIO = 4;

   // Frame fallbacks preserve legacy software correctness. These reason codes
   // document the unsupported/failed cases that can force synchronous state
   // transfer from the GPU back into the Java framebuffer.
   private static final int FALLBACK_INVALID_NEGATIVE_DEPTH = 0;
   private static final int FALLBACK_INVALID_TEXTURE_ID = 1;
   private static final int FALLBACK_TEXTURE_UPLOAD_FAILURE = 2;
   private static final int FALLBACK_PROJECTED_COORDINATE_FAILURE = 3;
   private static final int FALLBACK_EXCEPTION = 4;
   private static final int FALLBACK_OTHER_UNSUPPORTED_PATH = 5;
   private static final int FALLBACK_CAUSE_COUNT = 6;
   private static final String[] FALLBACK_CAUSE_NAMES = {
      "invalid-negative-depth",
      "invalid-texture-id",
      "texture-upload-failure",
      "projected-coordinate-failure",
      "exception",
      "other-unsupported-path"
   };

   private static final float[] PARTICLE_UNIT_X = new float[PARTICLE_SEGMENTS + 1];
   private static final float[] PARTICLE_UNIT_Y = new float[PARTICLE_SEGMENTS + 1];
   static final int UI_TRANSPARENT_KEY = 0x00010203;

   private static volatile boolean requested = true;
   private static boolean unavailable;
   private static boolean failureLogged;

   private static final long[] fallbackCauseCounts = new long[FALLBACK_CAUSE_COUNT];
   private static long fallbackCount;
   private static long fallbackSyncReadbackCount;
   private static long fallbackSyncReadbackNanos;
   private static long fallbackSyncReadbackMaxNanos;
   private static long fallbackSyncReadbackPixels;
   private static volatile long lastSceneFrameNanos;

   private static boolean frameOpen;
   private static boolean frameActive;
   private static boolean frameSoftwareFallback;
   private static boolean frameDirectPresentation;
   private static boolean directFrameReady;
   private static boolean directModeLogged;
   private static final Object CONTEXT_LOCK = new Object();

   private static GpuPresentationCanvas presentationCanvas;
   private static boolean directPresentationExecution;
   private static boolean presentationTransitionSoftware;
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
   private static boolean pbufferSharesPresentationContext;
   private static int presentationSceneTexture;
   private static int presentationSceneTextureWidth;
   private static int presentationSceneTextureHeight;
   private static int presentationFramebuffer;
   private static int presentationDepthRenderbuffer;
   private static int presentationFramebufferWidth;
   private static int presentationFramebufferHeight;
   private static boolean presentationFramebufferActive;
   private static boolean presentationFramebufferUnavailable;
   private static int presentationPreviousDrawBuffer;
   private static int presentationPreviousReadBuffer;
   private static boolean presentationBufferSelectionSaved;
   private static volatile GLSync presentationSceneFence;
   private static int bufferWidth;
   private static int bufferHeight;
   private static int viewportWidth = -1;
   private static int viewportHeight = -1;
   private static int scissorX = Integer.MIN_VALUE;
   private static int scissorY = Integer.MIN_VALUE;
   private static int scissorWidth = Integer.MIN_VALUE;
   private static int scissorHeight = Integer.MIN_VALUE;
   private static int preparedClipMinX = Integer.MIN_VALUE;
   private static int preparedClipMinY = Integer.MIN_VALUE;
   private static int preparedClipMaxX = Integer.MIN_VALUE;
   private static int preparedClipMaxY = Integer.MIN_VALUE;

   private static final boolean[] textureDirty = new boolean[TEXTURE_COUNT];
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
   private static boolean batchPipelinePrepared;
   private static int configuredBatchMode = BATCH_NONE;
   private static boolean sceneShaderConfigured;

   private static final int[] colorPbos = new int[2];
   private static final boolean[] colorPboReady = new boolean[2];
   private static int colorPboWriteIndex;
   private static int colorPboBytes;
   private static boolean pboUnavailable;

   private static int[] frameColorTarget;
   private static float[] frameDepthTarget;
   private static int frameRasterWidth;
   private static int frameRasterHeight;
   private static boolean frameFogEnabled;
   private static float frameFogStart;
   private static float frameFogEnd;
   // Scene triangles follow the legacy rasterizer's painter order. The depth
   // buffer records the most recently drawn surface for later particle tests;
   // it must not reject scene faces, including coplanar floor overlays.

   static {
      Arrays.fill(textureDirty, true);
      for (int i = 0; i <= PARTICLE_SEGMENTS; i++) {
         double angle = Math.PI * 2.0D * i / PARTICLE_SEGMENTS;
         PARTICLE_UNIT_X[i] = (float)Math.cos(angle);
         PARTICLE_UNIT_Y[i] = (float)Math.sin(angle);
      }
   }

   private GpuRasterizer3D() {
   }

   static boolean isRequested() {
      return requested;
   }

   static long getLastSceneFrameNanos() {
      return lastSceneFrameNanos;
   }

   static long getLastPresentationNanos() {
      GpuPresentationCanvas canvas = presentationCanvas;
      return canvas != null ? canvas.getLastPresentationNanos() : 0L;
   }

   static long getLastPresentationEdtWaitNanos() {
      GpuPresentationCanvas canvas = presentationCanvas;
      return canvas != null ? canvas.getLastPresentationEdtWaitNanos() : 0L;
   }

   private static boolean finishTimedSceneFrame(long startedNanos, boolean completed) {
      lastSceneFrameNanos = Math.max(0L, System.nanoTime() - startedNanos);
      return completed;
   }

   static synchronized String getFallbackDiagnostics() {
      StringBuilder summary = new StringBuilder(256);
      summary.append("GPU fallback diagnostics: total=").append(fallbackCount);
      for (int i = 0; i < FALLBACK_CAUSE_COUNT; i++) {
         summary.append(", ").append(FALLBACK_CAUSE_NAMES[i]).append('=').append(fallbackCauseCounts[i]);
      }
      summary.append(", sync-readbacks=").append(fallbackSyncReadbackCount);
      summary.append(", sync-readback-pixels=").append(fallbackSyncReadbackPixels);
      summary.append(", sync-readback-total-ms=").append(fallbackSyncReadbackNanos / 1000000.0D);
      summary.append(", sync-readback-max-ms=").append(fallbackSyncReadbackMaxNanos / 1000000.0D);
      return summary.toString();
   }

   static synchronized void resetFallbackDiagnostics() {
      Arrays.fill(fallbackCauseCounts, 0L);
      fallbackCount = 0L;
      fallbackSyncReadbackCount = 0L;
      fallbackSyncReadbackNanos = 0L;
      fallbackSyncReadbackMaxNanos = 0L;
      fallbackSyncReadbackPixels = 0L;
   }

   static void setPresentationCanvas(GpuPresentationCanvas canvas) {
      presentationCanvas = canvas;
      Rasterizer2D.setEncodeGpuOverlayAlpha(isDirectUiOverlayPrepared());
   }

   static void setEnabled(boolean enabled) {
      synchronized (CONTEXT_LOCK) {
         requested = enabled;
         setDirectFrameReady(false);
         if (enabled) {
            unavailable = false;
         } else {
            if (presentationCanvas != null) {
               presentationCanvas.deactivate();
            }
            if (pbuffer != null) {
               destroyContext();
            } else {
               releaseStagingBuffers();
            }
         }
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

      long frameStartedNanos = System.nanoTime();

      if (requested
         && !unavailable
         && presentationCanvas != null
         && !presentationCanvas.hasFailed()
         && !presentationCanvas.isContextReady()) {
         // The direct AWT canvas is the destination we actually want. Do not
         // allocate or resize a temporary Pbuffer while its peer/context is
         // coming up; that context and all of its GL resources would be thrown
         // away as soon as direct presentation becomes ready. Render this
         // transitional frame with the existing software rasterizer instead.
         if (pbuffer != null) {
            destroyContext();
         }
         // Keep presenting the completed software frame while the AWT canvas
         // is not initialized. Client.drawFrameBufferToWindow() starts the
         // canvas only after a full loadingStage==2 frame has reached the
         // software surface, preventing a black CardLayout handoff.
         presentationTransitionSoftware = true;
         try {
            prepareSceneRasterBuffers();
            renderer.run();
         } finally {
            presentationTransitionSoftware = false;
         }
         return finishTimedSceneFrame(frameStartedNanos, false);
      }

      if (requested
         && !unavailable
         && presentationCanvas != null
         && presentationCanvas.isContextReady()) {
         synchronized (CONTEXT_LOCK) {
            // removeNotify() can invalidate the AWT peer while the game thread
            // is waiting to enter this section. Recheck before creating a
            // context that shares objects with the presentation canvas.
            if (requested
               && !unavailable
               && presentationCanvas != null
               && presentationCanvas.isContextReady()) {
               boolean rendered = false;
               boolean completed = false;
               directPresentationExecution = true;
               directUiWidth = Math.max(1, uiWidth);
               directUiHeight = Math.max(1, uiHeight);
               directTargetX = targetX;
               directTargetY = targetY;
               directTargetWidth = Math.max(1, targetWidth);
               directTargetHeight = Math.max(1, targetHeight);
               directSceneX = sceneX;
               directSceneY = sceneY;

               try {
                  beginFrame(fogEnabled, fogDistanceOffset);
                  prepareSceneRasterBuffers();
                  rendered = true;
                  renderer.run();
                  completed = endFrame();
               } finally {
                  if (frameOpen) {
                     endFrame();
                  }
                  releaseDirectPresentationContext();
                  directPresentationExecution = false;
               }

               if (rendered) {
                  return finishTimedSceneFrame(frameStartedNanos, completed);
               }
            }
         }
      }

      beginFrame(fogEnabled, fogDistanceOffset);
      prepareSceneRasterBuffers();
      renderer.run();
      return finishTimedSceneFrame(frameStartedNanos, endFrame());
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
      frameColorTarget = null;
      frameDepthTarget = null;
      frameRasterWidth = 0;
      frameRasterHeight = 0;
      setDirectFrameReady(false);
      frameFogEnabled = fogEnabled;
      frameFogStart = 1430.0F + fogDistanceOffset;
      frameFogEnd = 2100.0F + fogDistanceOffset;
      resetBatch();
      resetBatchPipelineTracking();
      preparedClipMinX = Integer.MIN_VALUE;
      preparedClipMinY = Integer.MIN_VALUE;
      preparedClipMaxX = Integer.MIN_VALUE;
      preparedClipMaxY = Integer.MIN_VALUE;

      if (!baseAvailable()) {
         frameSoftwareFallback = true;
         return false;
      }

      // Pin the legacy software scene target for the lifetime of this GPU
      // frame. Rasterizer2D is global mutable state and can be rebound while a
      // fallback readback is still in flight.
      frameColorTarget = Rasterizer2D.pixels;
      frameDepthTarget = Rasterizer2D.depthBuffer;
      frameRasterWidth = Rasterizer2D.width;
      frameRasterHeight = Rasterizer2D.height;

      try {
         if (directPresentationExecution) {
            ensureContext(directTargetWidth, directTargetHeight, true);
            makeCurrent();
            frameDirectPresentation = true;
         } else {
            ensureContext(Rasterizer2D.width, Rasterizer2D.height, false);
            makeCurrent();
            frameDirectPresentation = false;
         }

         if (frameDirectPresentation) {
            prepareDirectPresentationTarget(directTargetWidth, directTargetHeight);
         }
         configureViewport(frameRasterWidth, frameRasterHeight);
         ensureTextureAtlas();

         // The UI compositor also uses GL_SCISSOR_TEST in this same context.
         // Invalidate the rasterizer's cached rectangle before restoring the
         // scene scissor for a new frame.
         scissorX = Integer.MIN_VALUE;
         scissorY = Integer.MIN_VALUE;
         scissorWidth = Integer.MIN_VALUE;
         scissorHeight = Integer.MIN_VALUE;
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

   static void prepareSceneRasterBuffers() {
      if (isFrameActive() && frameDirectPresentation && canUseDirectPresentation() && frameColorTarget != null) {
         Arrays.fill(frameColorTarget, UI_TRANSPARENT_KEY);
         Rasterizer2D.markGpuOverlayCleared();
         return;
      }

      Rasterizer2D.clear();
   }

   static boolean endFrame() {
      if (!frameOpen) {
         return false;
      }

      boolean completed = false;
      try {
         if (frameActive) {
            flushBatch();
            finishBatchPipeline();
            if (frameDirectPresentation) {
               if (canUseDirectPresentation()) {
                  finishDirectPresentationFrame();
                  setDirectFrameReady(true);
               } else {
                  // The AWT peer can be recreated after this frame started.
                  // Direct frames render at the physical presentation size, so
                  // use the direct readback path to resample back into the
                  // logical software buffer instead of the ordinary async PBO
                  // path, which assumes 1:1 framebuffer dimensions.
                  readBackFrameSynchronous(false);
               }
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
         if (frameDirectPresentation) {
            finishDirectPresentationTarget();
         }
         frameOpen = false;
         frameActive = false;
         frameSoftwareFallback = false;
         frameDirectPresentation = false;
         frameColorTarget = null;
         frameDepthTarget = null;
         frameRasterWidth = 0;
         frameRasterHeight = 0;
         resetBatch();
      }
      return completed;
   }

   static boolean presentDirectFrame(
      int[] uiPixels,
      boolean softwareUiChanged,
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
         softwareUiChanged,
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
      setDirectFrameReady(false);
      if (!queued) {
         if (presentationCanvas.hasFailed()) {
            presentationCanvas.deactivate();
         }
      }
      return queued;
   }

   static boolean presentSoftwareFrame(
      int[] pixels,
      int frameWidth,
      int frameHeight,
      int targetX,
      int targetY,
      int targetWidth,
      int targetHeight
   ) {
      if (!requested
         || presentationCanvas == null
         || presentationCanvas.hasFailed()
         || !presentationCanvas.isContextReady()) {
         return false;
      }

      setDirectFrameReady(false);
      boolean presented = presentationCanvas.presentSoftwareFrame(
         pixels,
         frameWidth,
         frameHeight,
         targetX,
         targetY,
         targetWidth,
         targetHeight
      );
      return presented;
   }

   static boolean canRetainPresentedFrame() {
      return requested
         && presentationCanvas != null
         && !presentationCanvas.hasFailed()
         && presentationCanvas.hasPresentedFrame();
   }

   static boolean retainPresentedFrameForRegionTransition() {
      if (!requested
         || unavailable
         || presentationCanvas == null
         || presentationCanvas.hasFailed()) {
         return false;
      }

      // Reconstruct/capture the last successfully presented frame before
      // invalidating the pending direct-frame token.
      boolean retained = presentationCanvas.retainPresentedFrameForTransition();
      setDirectFrameReady(false);
      return retained;
   }

   static void requestPresentationInitialization() {
      if (requested
         && !unavailable
         && presentationCanvas != null
         && !presentationCanvas.hasFailed()
         && !presentationCanvas.isContextReady()) {
         presentationCanvas.requestInitialization();
      }
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

   static boolean isDirectUiOverlayPrepared() {
      return requested
         && directFrameReady
         && presentationCanvas != null
         && presentationCanvas.isContextReady();
   }

   private static void setDirectFrameReady(boolean ready) {
      directFrameReady = ready;
      Rasterizer2D.setEncodeGpuOverlayAlpha(
         ready
            && requested
            && presentationCanvas != null
            && presentationCanvas.isContextReady()
      );
   }

   private static void finishDirectPresentationFrame() {
      if (presentationCanvas == null || !pbufferSharesPresentationContext) {
         throw new IllegalStateException("Direct presentation Pbuffer is not shared with the AWT canvas");
      }

      // On drivers with EXT_framebuffer_object the scene is rendered directly
      // into presentationSceneTexture, avoiding a full-frame GPU copy. Keep the
      // old copy path as a compatibility fallback for older/broken drivers.
      if (!presentationFramebufferActive) {
         ensurePresentationSceneTexture(directTargetWidth, directTargetHeight);
         GL20.glUseProgram(0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, presentationSceneTexture);
         GL11.glCopyTexSubImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            0,
            0,
            0,
            0,
            directTargetWidth,
            directTargetHeight
         );
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      }

      publishPresentationScene();
      presentationCanvas.markSceneBackbufferPending();
      if (!directModeLogged) {
         directModeLogged = true;
         System.out.println(
            presentationFramebufferActive
               ? "GPU presentation mode: SHARED FBO (direct render-to-texture -> AWT composite)."
               : "GPU presentation mode: SHARED COPY (Pbuffer copy -> shared texture -> AWT composite)."
         );
      }
   }

   private static void prepareDirectPresentationTarget(int width, int height) {
      presentationFramebufferActive = false;
      if (presentationFramebufferUnavailable) {
         return;
      }

      try {
         if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            presentationFramebufferUnavailable = true;
            System.out.println("GPU direct render-to-texture unavailable; using framebuffer-copy presentation.");
            return;
         }

         int targetWidth = Math.max(1, width);
         int targetHeight = Math.max(1, height);
         ensurePresentationSceneTexture(targetWidth, targetHeight);

         presentationPreviousDrawBuffer = GL11.glGetInteger(GL11.GL_DRAW_BUFFER);
         presentationPreviousReadBuffer = GL11.glGetInteger(GL11.GL_READ_BUFFER);
         presentationBufferSelectionSaved = true;

         if (presentationFramebuffer == 0) {
            presentationFramebuffer = EXTFramebufferObject.glGenFramebuffersEXT();
         }
         if (presentationDepthRenderbuffer == 0) {
            presentationDepthRenderbuffer = EXTFramebufferObject.glGenRenderbuffersEXT();
         }

         EXTFramebufferObject.glBindFramebufferEXT(
            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
            presentationFramebuffer
         );
         EXTFramebufferObject.glFramebufferTexture2DEXT(
            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
            EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
            GL11.GL_TEXTURE_2D,
            presentationSceneTexture,
            0
         );
         GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
         GL11.glReadBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);

         EXTFramebufferObject.glBindRenderbufferEXT(
            EXTFramebufferObject.GL_RENDERBUFFER_EXT,
            presentationDepthRenderbuffer
         );
         if (presentationFramebufferWidth != targetWidth
            || presentationFramebufferHeight != targetHeight) {
            EXTFramebufferObject.glRenderbufferStorageEXT(
               EXTFramebufferObject.GL_RENDERBUFFER_EXT,
               GL_DEPTH_COMPONENT24,
               targetWidth,
               targetHeight
            );
            presentationFramebufferWidth = targetWidth;
            presentationFramebufferHeight = targetHeight;
         }
         EXTFramebufferObject.glFramebufferRenderbufferEXT(
            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
            EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
            EXTFramebufferObject.GL_RENDERBUFFER_EXT,
            presentationDepthRenderbuffer
         );
         EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);

         int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(
            EXTFramebufferObject.GL_FRAMEBUFFER_EXT
         );
         if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
            throw new IllegalStateException("Direct presentation framebuffer incomplete: 0x"
               + Integer.toHexString(status));
         }

         presentationFramebufferActive = true;
      } catch (Throwable failure) {
         try {
            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
         } catch (Throwable ignored) {
         }
         restorePresentationDefaultFramebuffer();
         presentationFramebufferActive = false;
         deletePresentationFramebufferResources();
         presentationFramebufferUnavailable = true;
         System.err.println(
            "GPU direct render-to-texture unavailable; using framebuffer-copy presentation: "
               + failure.getMessage()
         );
      }
   }

   private static void finishDirectPresentationTarget() {
      if (!presentationFramebufferActive) {
         return;
      }
      try {
         restorePresentationDefaultFramebuffer();
      } finally {
         presentationFramebufferActive = false;
      }
   }

   private static void restorePresentationDefaultFramebuffer() {
      try {
         EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
         if (presentationBufferSelectionSaved) {
            GL11.glDrawBuffer(presentationPreviousDrawBuffer);
            GL11.glReadBuffer(presentationPreviousReadBuffer);
         }
      } catch (Throwable ignored) {
      } finally {
         presentationBufferSelectionSaved = false;
      }
   }

   private static void publishPresentationScene() {
      deletePresentationSceneFence();

      try {
         if (GLContext.getCapabilities().GL_ARB_sync) {
            GLSync fence = ARBSync.glFenceSync(ARBSync.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
            if (fence != null) {
               presentationSceneFence = fence;
               // Flush the producer context so the consumer context's GPU-side
               // wait can observe the fence without blocking this Java thread.
               GL11.glFlush();
               return;
            }
         }
      } catch (Throwable ignored) {
         deletePresentationSceneFence();
      }

      // Very old drivers have no cross-context sync objects. Preserve the
      // previous correctness guarantee there, even though it stalls the CPU.
      GL11.glFinish();
   }

   static void waitForPresentationScene() {
      GLSync fence = presentationSceneFence;
      if (fence == null) {
         return;
      }

      presentationSceneFence = null;
      try {
         // Server-side wait: presentation commands queue behind the producer
         // context without forcing the EDT/game thread to spin on the CPU.
         ARBSync.glWaitSync(fence, 0, ARBSync.GL_TIMEOUT_IGNORED);
      } finally {
         try {
            ARBSync.glDeleteSync(fence);
         } catch (Throwable ignored) {
         }
      }
   }

   private static void deletePresentationSceneFence() {
      GLSync fence = presentationSceneFence;
      presentationSceneFence = null;
      if (fence != null) {
         try {
            ARBSync.glDeleteSync(fence);
         } catch (Throwable ignored) {
         }
      }
   }

   private static void deletePresentationFramebufferResources() {
      presentationFramebufferActive = false;
      restorePresentationDefaultFramebuffer();
      try {
         EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
      } catch (Throwable ignored) {
      }

      if (presentationDepthRenderbuffer != 0) {
         try {
            EXTFramebufferObject.glDeleteRenderbuffersEXT(presentationDepthRenderbuffer);
         } catch (Throwable ignored) {
         }
      }
      if (presentationFramebuffer != 0) {
         try {
            EXTFramebufferObject.glDeleteFramebuffersEXT(presentationFramebuffer);
         } catch (Throwable ignored) {
         }
      }
      presentationDepthRenderbuffer = 0;
      presentationFramebuffer = 0;
      presentationFramebufferWidth = 0;
      presentationFramebufferHeight = 0;
   }

   private static void ensurePresentationSceneTexture(int width, int height) {
      int targetWidth = Math.max(1, width);
      int targetHeight = Math.max(1, height);
      if (presentationSceneTexture != 0
         && presentationSceneTextureWidth == targetWidth
         && presentationSceneTextureHeight == targetHeight) {
         return;
      }

      if (presentationSceneTexture != 0) {
         GL11.glDeleteTextures(presentationSceneTexture);
      }

      presentationSceneTexture = GL11.glGenTextures();
      presentationSceneTextureWidth = targetWidth;
      presentationSceneTextureHeight = targetHeight;
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, presentationSceneTexture);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      GL11.glTexImage2D(
         GL11.GL_TEXTURE_2D,
         0,
         GL11.GL_RGBA8,
         targetWidth,
         targetHeight,
         0,
         GL_BGRA,
         GL11.GL_UNSIGNED_BYTE,
         (ByteBuffer)null
      );
   }

   static int getPresentationSceneTexture() {
      // The most recently completed scene texture remains valid after
      // directFrameReady is consumed. Region-transition retention uses it to
      // reconstruct the exact last presented frame without GL_FRONT readback.
      return pbufferSharesPresentationContext ? presentationSceneTexture : 0;
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
      boolean batched = frameActive;
      Bounds bounds = null;
      if (batched) {
         if (!triangleVisible(y0, x0, y1, x1, y2, x2)) {
            return true;
         }
      } else {
         bounds = Bounds.of(y0, x0, y1, x1, y2, x2);
         if (bounds == null) {
            return true;
         }
      }
      if (!prepare(bounds)) {
         return false;
      }
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

      boolean batched = frameActive;
      Bounds bounds = null;
      if (batched) {
         if (!triangleVisible(x0, y0, x1, y1, x2, y2)) {
            return true;
         }
      } else {
         bounds = Bounds.of(x0, y0, x1, y1, x2, y2);
         if (bounds == null) {
            return true;
         }
      }
      if (!prepare(bounds)) {
         return false;
      }
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
      int y0, int y1, int y2, int x0, int x1, int x2,
      int color0, int color1, int color2,
      float depth0, float depth1, float depth2
   ) {
      if (!canDraw(depth0, depth1, depth2)) {
         return false;
      }

      boolean batched = frameActive;
      Bounds bounds = null;
      if (batched) {
         if (!triangleVisible(x0, y0, x1, y1, x2, y2)) {
            return true;
         }
      } else {
         bounds = Bounds.of(x0, y0, x1, y1, x2, y2);
         if (bounds == null) {
            return true;
         }
      }
      if (!prepare(bounds)) {
         return false;
      }
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
      if (!canDraw(depth0, depth1, depth2)) {
         return false;
      }
      if (textureId < 0 || textureId >= TEXTURE_COUNT) {
         if (frameOpen && !frameSoftwareFallback) {
            fallbackCurrentFrame(FALLBACK_INVALID_TEXTURE_ID);
         }
         return false;
      }

      boolean batched = frameActive;
      Bounds bounds = null;
      if (batched) {
         if (!triangleVisible(x0, y0, x1, y1, x2, y2)) {
            return true;
         }
      } else {
         bounds = Bounds.of(x0, y0, x1, y1, x2, y2);
         if (bounds == null) {
            return true;
         }
      }
      if (!prepare(bounds)) {
         return false;
      }
      try {
         if (ensureTexture(textureId) == 0) {
            if (batched) {
               fallbackCurrentFrame(FALLBACK_TEXTURE_UPLOAD_FAILURE);
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

         double max = maxAbs9(pu0, pv0, pw0, pu1, pv1, pw1, pu2, pv2, pw2);
         if (!(max > 0.0D) || Double.isInfinite(max) || Double.isNaN(max)) {
            if (batched) {
               fallbackCurrentFrame(FALLBACK_PROJECTED_COORDINATE_FAILURE);
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
         && !presentationTransitionSoftware
         && Rasterizer2D.pixels != null
         && Rasterizer2D.depthBuffer != null
         && Rasterizer2D.width > 0
         && Rasterizer2D.height > 0;
   }

   private static boolean canDraw(float depth0, float depth1, float depth2) {
      // Auxiliary UI/model renders can run after a direct scene frame has
      // completed but before that shared frame is presented. Keep them on the
      // software rasterizer instead of replacing the shared Pbuffer, which
      // would destroy the pending presentation texture and thrash contexts.
      if (!frameOpen && (rendererUsesPresentationContext || pbufferSharesPresentationContext)) {
         return false;
      }
      if (frameOpen && frameSoftwareFallback) {
         return false;
      }

      if (!baseAvailable()) {
         if (frameOpen && !frameSoftwareFallback) {
            fallbackCurrentFrame(FALLBACK_OTHER_UNSUPPORTED_PATH);
         }
         return false;
      }
      if (depth0 < 0.0F || depth1 < 0.0F || depth2 < 0.0F) {
         if (frameOpen && !frameSoftwareFallback) {
            fallbackCurrentFrame(FALLBACK_INVALID_NEGATIVE_DEPTH);
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
            ensureContext(Rasterizer2D.width, Rasterizer2D.height, false);
            makeCurrent();
            configureViewport(Rasterizer2D.width, Rasterizer2D.height);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
         }

         if (frameActive) {
            int minX = Math.max(0, Rasterizer2D.topX);
            int minY = Math.max(0, Rasterizer2D.topY);
            int maxX = Math.min(viewportWidth, Rasterizer2D.bottomX);
            int maxY = Math.min(viewportHeight, Rasterizer2D.bottomY);
            if (minX != preparedClipMinX
               || minY != preparedClipMinY
               || maxX != preparedClipMaxX
               || maxY != preparedClipMaxY) {
               if (minX >= maxX || minY >= maxY) {
                  setScissor(0, 0, 0, 0);
               } else {
                  setLogicalScissor(minX, minY, maxX, maxY);
               }
               preparedClipMinX = minX;
               preparedClipMinY = minY;
               preparedClipMaxX = maxX;
               preparedClipMaxY = maxY;
            }
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

      int left = scaleFloor(uiLeft, directUiWidth, directTargetWidth);
      int right = scaleCeil(uiRight, directUiWidth, directTargetWidth);
      int top = scaleFloor(uiTop, directUiHeight, directTargetHeight);
      int bottom = scaleCeil(uiBottom, directUiHeight, directTargetHeight);

      left = Math.max(0, Math.min(directTargetWidth, left));
      right = Math.max(left, Math.min(directTargetWidth, right));
      top = Math.max(0, Math.min(directTargetHeight, top));
      bottom = Math.max(top, Math.min(directTargetHeight, bottom));

      setScissor(left, directTargetHeight - bottom, right - left, bottom - top);
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

   private static void fallbackCurrentFrame(int cause) {
      if (!frameOpen || frameSoftwareFallback) {
         return;
      }

      long sequence = recordFallbackCause(cause);
      boolean readbackAttempted = false;
      boolean readbackSucceeded = false;
      int readbackPixels = 0;
      long readbackNanos = 0L;
      if (frameActive) {
         try {
            flushBatch();
            finishBatchPipeline();
            readbackAttempted = true;
            long readbackStarted = System.nanoTime();
            readbackPixels = readBackFrameSynchronous(true);
            readbackNanos = System.nanoTime() - readbackStarted;
            readbackSucceeded = true;
            recordFallbackReadback(readbackPixels, readbackNanos);
         } catch (Throwable failure) {
            logFallback(sequence, cause, readbackAttempted, false, readbackPixels, readbackNanos);
            frameActive = false;
            frameSoftwareFallback = true;
            fail(failure);
            return;
         }
      }

      logFallback(sequence, cause, readbackAttempted, readbackSucceeded, readbackPixels, readbackNanos);
      frameActive = false;
      frameSoftwareFallback = true;
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
   }

   private static void failCurrentFrame(Throwable failure) {
      long sequence = 0L;
      if (frameOpen && !frameSoftwareFallback) {
         sequence = recordFallbackCause(FALLBACK_EXCEPTION);
      }

      boolean readbackAttempted = false;
      boolean readbackSucceeded = false;
      int readbackPixels = 0;
      long readbackNanos = 0L;
      if (frameOpen && frameActive) {
         try {
            flushBatch();
            finishBatchPipeline();
            readbackAttempted = true;
            long readbackStarted = System.nanoTime();
            readbackPixels = readBackFrameSynchronous(true);
            readbackNanos = System.nanoTime() - readbackStarted;
            readbackSucceeded = true;
            recordFallbackReadback(readbackPixels, readbackNanos);
         } catch (Throwable ignored) {
         }
      }

      if (sequence != 0L) {
         logFallback(
            sequence,
            FALLBACK_EXCEPTION,
            readbackAttempted,
            readbackSucceeded,
            readbackPixels,
            readbackNanos
         );
      }

      frameActive = false;
      if (frameOpen) {
         frameSoftwareFallback = true;
      }
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
      fail(failure);
   }

   private static synchronized long recordFallbackCause(int cause) {
      int normalizedCause = cause;
      if (normalizedCause < 0 || normalizedCause >= FALLBACK_CAUSE_COUNT) {
         normalizedCause = FALLBACK_OTHER_UNSUPPORTED_PATH;
      }
      fallbackCount++;
      fallbackCauseCounts[normalizedCause]++;
      return fallbackCount;
   }

   private static synchronized void recordFallbackReadback(int pixels, long nanos) {
      fallbackSyncReadbackCount++;
      fallbackSyncReadbackPixels += Math.max(0, pixels);
      fallbackSyncReadbackNanos += Math.max(0L, nanos);
      fallbackSyncReadbackMaxNanos = Math.max(fallbackSyncReadbackMaxNanos, nanos);
   }

   private static String fallbackCauseName(int cause) {
      if (cause < 0 || cause >= FALLBACK_CAUSE_COUNT) {
         return FALLBACK_CAUSE_NAMES[FALLBACK_OTHER_UNSUPPORTED_PATH];
      }
      return FALLBACK_CAUSE_NAMES[cause];
   }

   private static void logFallback(
      long sequence,
      int cause,
      boolean readbackAttempted,
      boolean readbackSucceeded,
      int readbackPixels,
      long readbackNanos
   ) {
      StringBuilder message = new StringBuilder(160);
      message.append("GPU frame fallback #").append(sequence)
         .append(": cause=").append(fallbackCauseName(cause));
      if (readbackAttempted) {
         message.append(", synchronous-readback=")
            .append(readbackSucceeded ? "ok" : "failed")
            .append(", pixels=").append(readbackPixels)
            .append(", ms=").append(readbackNanos / 1000000.0D);
      } else {
         message.append(", synchronous-readback=not-attempted");
      }
      System.err.println(message.toString());
   }

   private static void ensureContext(int width, int height, boolean shareWithPresentation) throws Exception {
      if (rendererUsesPresentationContext) {
         resetRendererResourceHandles();
      }

      boolean wantsSharedPresentation = shareWithPresentation
         && presentationCanvas != null
         && presentationCanvas.isContextReady();
      int targetWidth = Math.max(width, 765);
      int targetHeight = Math.max(height, 503);
      boolean recreate = pbuffer == null
         || pbuffer.isBufferLost()
         || pbufferSharesPresentationContext != wantsSharedPresentation
         || targetWidth > bufferWidth
         || targetHeight > bufferHeight
         || shouldShrink(
            (long)bufferWidth * (long)bufferHeight,
            (long)targetWidth * (long)targetHeight
         );
      if (!recreate) {
         return;
      }

      destroyContext();
      bufferWidth = targetWidth;
      bufferHeight = targetHeight;

      PixelFormat pixelFormat = new PixelFormat().withAlphaBits(8).withDepthBits(24);
      pbuffer = new Pbuffer(
         bufferWidth,
         bufferHeight,
         pixelFormat,
         wantsSharedPresentation ? presentationCanvas : null
      );
      pbufferSharesPresentationContext = wantsSharedPresentation;
      pbuffer.makeCurrent();
      initializeCurrentContextResources(false);

      System.out.println(
         "GPU renderer initialized: OpenGL Pbuffer "
            + bufferWidth + "x" + bufferHeight
            + (wantsSharedPresentation
               ? " [shared AWT presentation texture, VBO atlas batching, GPU fog/depth]"
               : " [VBO atlas batching, double-PBO color readback, GPU fog/depth]")
      );
   }

   private static void ensurePresentationContextResources() {
      if (rendererUsesPresentationContext && vertexBufferObject != 0) {
         return;
      }
      if (pbuffer != null) {
         throw new IllegalStateException("Pbuffer remained active while entering direct canvas rendering");
      }

      deleteRendererResources();
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
      resetBatchPipelineTracking();

      Arrays.fill(textureDirty, true);
      viewportWidth = -1;
      viewportHeight = -1;
      scissorX = Integer.MIN_VALUE;
      scissorY = Integer.MIN_VALUE;
      scissorWidth = Integer.MIN_VALUE;
      scissorHeight = Integer.MIN_VALUE;
      rendererUsesPresentationContext = presentationContext;
   }

   static void presentationContextLost(GpuPresentationCanvas canvas) {
      synchronized (CONTEXT_LOCK) {
         if (presentationCanvas != canvas) {
            return;
         }
         setDirectFrameReady(false);
         frameDirectPresentation = false;
         directModeLogged = false;
         if (pbufferSharesPresentationContext && pbuffer != null) {
            // The shared-object group belongs to this AWT context generation.
            // Recreate the Pbuffer against the replacement canvas context.
            destroyContext();
         } else if (pbuffer == null) {
            resetRendererResourceHandles();
         }
         releaseStagingBuffers();
      }
   }

   static void releasePresentationContextResources(GpuPresentationCanvas canvas) {
      synchronized (CONTEXT_LOCK) {
         if (presentationCanvas != canvas || pbuffer != null) {
            return;
         }
         deleteRendererResources();
      }
   }

   private static void deleteColorPbos() {
      try {
         GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      } catch (Throwable ignored) {
      }
      for (int pbo : colorPbos) {
         if (pbo != 0) {
            try {
               GL15.glDeleteBuffers(pbo);
            } catch (Throwable ignored) {
            }
         }
      }
      Arrays.fill(colorPbos, 0);
      Arrays.fill(colorPboReady, false);
      colorPboWriteIndex = 0;
      colorPboBytes = 0;
   }

   private static void deleteRendererResources() {
      deletePresentationSceneFence();
      deletePresentationFramebufferResources();

      try {
         GL20.glUseProgram(0);
      } catch (Throwable ignored) {
      }
      try {
         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
      } catch (Throwable ignored) {
      }
      try {
         GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      } catch (Throwable ignored) {
      }
      try {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      } catch (Throwable ignored) {
      }

      if (vertexBufferObject != 0) {
         try {
            GL15.glDeleteBuffers(vertexBufferObject);
         } catch (Throwable ignored) {
         }
      }
      if (shaderProgram != 0) {
         try {
            GL20.glDeleteProgram(shaderProgram);
         } catch (Throwable ignored) {
         }
      }
      if (atlasTexture != 0) {
         try {
            GL11.glDeleteTextures(atlasTexture);
         } catch (Throwable ignored) {
         }
      }
      if (presentationSceneTexture != 0) {
         try {
            GL11.glDeleteTextures(presentationSceneTexture);
         } catch (Throwable ignored) {
         }
      }
      deleteColorPbos();
      resetRendererResourceHandles();
   }

   private static void resetRendererResourceHandles() {
      rendererUsesPresentationContext = false;
      vertexBufferObject = 0;
      shaderProgram = 0;
      atlasTexture = 0;
      atlasTextureSize = 0;
      legacyTextureSize = 0;
      presentationSceneTexture = 0;
      presentationSceneTextureWidth = 0;
      presentationSceneTextureHeight = 0;
      presentationFramebuffer = 0;
      presentationDepthRenderbuffer = 0;
      presentationFramebufferWidth = 0;
      presentationFramebufferHeight = 0;
      presentationFramebufferActive = false;
      presentationFramebufferUnavailable = false;
      presentationPreviousDrawBuffer = 0;
      presentationPreviousReadBuffer = 0;
      presentationBufferSelectionSaved = false;
      presentationSceneFence = null;
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
      resetBatchPipelineTracking();
      Arrays.fill(textureDirty, true);
   }

   private static void makeCurrent() throws Exception {
      if (!pbuffer.isCurrent()) {
         pbuffer.makeCurrent();
      }
   }

   private static void releaseDirectPresentationContext() {
      if (pbuffer == null) {
         return;
      }

      try {
         if (pbuffer.isCurrent()) {
            pbuffer.releaseContext();
         }
      } catch (Throwable failure) {
         fail(failure);
      }
   }

   private static void configureViewport(int width, int height) {
      viewportWidth = width;
      viewportHeight = height;

      if (frameDirectPresentation) {
         GL11.glViewport(
            0,
            0,
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

      int vertexShader = 0;
      int fragmentShader = 0;
      int program = 0;
      boolean initialized = false;
      try {
         vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
         fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);
         program = GL20.glCreateProgram();
         GL20.glAttachShader(program, vertexShader);
         GL20.glAttachShader(program, fragmentShader);
         GL20.glLinkProgram(program);
         if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException("GPU shader link failed: " + GL20.glGetProgramInfoLog(program, 4096));
         }

         int newUniformAtlas = GL20.glGetUniformLocation(program, "uAtlas");
         int newUniformTextureSize = GL20.glGetUniformLocation(program, "uTextureSize");
         int newUniformFogEnabled = GL20.glGetUniformLocation(program, "uFogEnabled");
         int newUniformFogStart = GL20.glGetUniformLocation(program, "uFogStart");
         int newUniformFogEnd = GL20.glGetUniformLocation(program, "uFogEnd");
         int newUniformDepthScale = GL20.glGetUniformLocation(program, "uDepthScale");
         int newUniformFogColor = GL20.glGetUniformLocation(program, "uFogColor");

         GL20.glUseProgram(program);
         GL20.glUniform1i(newUniformAtlas, 0);
         GL20.glUniform1f(newUniformDepthScale, DEPTH_SCALE);
         GL20.glUniform3f(newUniformFogColor, 149.0F / 255.0F, 150.0F / 255.0F, 152.0F / 255.0F);
         GL20.glUseProgram(0);

         uniformAtlas = newUniformAtlas;
         uniformTextureSize = newUniformTextureSize;
         uniformFogEnabled = newUniformFogEnabled;
         uniformFogStart = newUniformFogStart;
         uniformFogEnd = newUniformFogEnd;
         uniformDepthScale = newUniformDepthScale;
         uniformFogColor = newUniformFogColor;
         shaderProgram = program;
         initialized = true;
      } finally {
         try {
            GL20.glUseProgram(0);
         } catch (Throwable ignored) {
         }
         if (vertexShader != 0) {
            try {
               GL20.glDeleteShader(vertexShader);
            } catch (Throwable ignored) {
            }
         }
         if (fragmentShader != 0) {
            try {
               GL20.glDeleteShader(fragmentShader);
            } catch (Throwable ignored) {
            }
         }
         if (!initialized && program != 0) {
            try {
               GL20.glDeleteProgram(program);
            } catch (Throwable ignored) {
            }
         }
      }
   }

   private static int compileShader(int type, String source) {
      int shader = GL20.glCreateShader(type);
      boolean compiled = false;
      try {
         GL20.glShaderSource(shader, source);
         GL20.glCompileShader(shader);
         if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException("GPU shader compile failed: " + GL20.glGetShaderInfoLog(shader, 4096));
         }
         compiled = true;
         return shader;
      } finally {
         if (!compiled && shader != 0) {
            try {
               GL20.glDeleteShader(shader);
            } catch (Throwable ignored) {
            }
         }
      }
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
      ensureReadbackCapacity(width * height, false, true);

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
         int[] colorTarget = getFrameColorTarget();
         if (colorTarget == null) {
            throw new IllegalStateException("GPU async readback lost its software frame target");
         }
         copyColorReadback(mapped, colorTarget, width, height);
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
         deleteColorPbos();
         pboUnavailable = true;
         System.err.println("GPU PBO readback unavailable; using synchronous color readback.");
         readBackFrameSynchronous(false);
      }
   }

   private static int[] getFrameColorTarget() {
      return frameColorTarget != null ? frameColorTarget : Rasterizer2D.pixels;
   }

   private static float[] getFrameDepthTarget() {
      return frameDepthTarget != null ? frameDepthTarget : Rasterizer2D.depthBuffer;
   }

   private static int getFrameRasterWidth() {
      return frameRasterWidth > 0 ? frameRasterWidth : Rasterizer2D.width;
   }

   private static int readBackFrameSynchronous(boolean copyDepth) {
      synchronized (CONTEXT_LOCK) {
         return readBackFrameSynchronousLocked(copyDepth);
      }
   }

   private static int readBackFrameSynchronousLocked(boolean copyDepth) {
      if (frameDirectPresentation) {
         return readBackDirectFrameSynchronous(copyDepth);
      }
      if (viewportWidth <= 0 || viewportHeight <= 0) {
         return 0;
      }

      int width = viewportWidth;
      int height = viewportHeight;
      int[] colorTarget = getFrameColorTarget();
      float[] depthTarget = copyDepth ? getFrameDepthTarget() : null;
      if (colorTarget == null || copyDepth && depthTarget == null) {
         throw new IllegalStateException("GPU fallback lost its software frame target");
      }
      int count = width * height;
      ensureReadbackCapacity(count, copyDepth, true);
      ByteBuffer colorBuffer = colorReadback;
      FloatBuffer depthBuffer = copyDepth ? depthReadback : null;
      if (colorBuffer == null || copyDepth && depthBuffer == null) {
         throw new IllegalStateException("GPU readback staging buffer was released during frame fallback");
      }

      GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      colorBuffer.clear();
      colorBuffer.limit(count * 4);
      GL11.glReadPixels(0, 0, width, height, GL_BGRA, GL11.GL_UNSIGNED_BYTE, colorBuffer);
      copyColorReadback(colorBuffer, colorTarget, width, height);

      if (copyDepth) {
         depthBuffer.clear();
         depthBuffer.limit(count);
         GL11.glReadPixels(0, 0, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthBuffer);
         for (int readRow = 0; readRow < height; readRow++) {
            int destination = (height - 1 - readRow) * width;
            int source = readRow * width;
            for (int x = 0; x < width; x++) {
               float gpuDepth = depthBuffer.get(source + x);
               if (gpuDepth < 0.9999999F) {
                  depthTarget[destination + x] = gpuDepth * DEPTH_SCALE;
               }
            }
         }
      }
      return count;
   
   }

   private static int readBackDirectFrameSynchronous(boolean copyDepth) {
      if (viewportWidth <= 0
         || viewportHeight <= 0
         || directUiWidth <= 0
         || directUiHeight <= 0
         || directTargetWidth <= 0
         || directTargetHeight <= 0) {
         return 0;
      }

      // A software fallback resumes drawing into the logical scene buffer, not
      // the entire scaled presentation target. Read back only the physical
      // rectangle that covers that logical scene. The floor/ceil pair retains
      // every physical sample selected by sampleScaledCoordinate().
      int readMinX = scaleFloor(directSceneX, directUiWidth, directTargetWidth);
      int readMinY = scaleFloor(directSceneY, directUiHeight, directTargetHeight);
      int readMaxX = scaleCeil(directSceneX + viewportWidth, directUiWidth, directTargetWidth);
      int readMaxY = scaleCeil(directSceneY + viewportHeight, directUiHeight, directTargetHeight);
      readMinX = Math.max(0, Math.min(directTargetWidth, readMinX));
      readMinY = Math.max(0, Math.min(directTargetHeight, readMinY));
      readMaxX = Math.max(readMinX, Math.min(directTargetWidth, readMaxX));
      readMaxY = Math.max(readMinY, Math.min(directTargetHeight, readMaxY));

      int readWidth = readMaxX - readMinX;
      int readHeight = readMaxY - readMinY;
      if (readWidth <= 0 || readHeight <= 0) {
         return 0;
      }

      long physicalCountLong = (long)readWidth * (long)readHeight;
      if (physicalCountLong > Integer.MAX_VALUE / 4L) {
         throw new IllegalStateException("Direct presentation scene is too large to read back");
      }
      int[] colorTarget = getFrameColorTarget();
      float[] depthTargetArray = copyDepth ? getFrameDepthTarget() : null;
      if (colorTarget == null || copyDepth && depthTargetArray == null) {
         throw new IllegalStateException("GPU direct fallback lost its software frame target");
      }

      int physicalCount = (int)physicalCountLong;
      ensureReadbackCapacity(physicalCount, copyDepth, true);
      ByteBuffer colorBuffer = colorReadback;
      FloatBuffer depthBuffer = copyDepth ? depthReadback : null;
      if (colorBuffer == null || copyDepth && depthBuffer == null) {
         throw new IllegalStateException("GPU direct readback staging buffer was released during frame fallback");
      }
      int readX = readMinX;
      int readY = directTargetHeight - readMaxY;

      GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      colorBuffer.clear();
      colorBuffer.limit(physicalCount * 4);
      GL11.glReadPixels(
         readX,
         readY,
         readWidth,
         readHeight,
         GL_BGRA,
         GL11.GL_UNSIGNED_BYTE,
         colorBuffer
      );

      if (copyDepth) {
         depthBuffer.clear();
         depthBuffer.limit(physicalCount);
         GL11.glReadPixels(
            readX,
            readY,
            readWidth,
            readHeight,
            GL11.GL_DEPTH_COMPONENT,
            GL11.GL_FLOAT,
            depthBuffer
         );
      }

      colorBuffer.rewind();
      IntBuffer packedColors = colorBuffer.order(ByteOrder.nativeOrder()).asIntBuffer();
      for (int y = 0; y < viewportHeight; y++) {
         int physicalTopY = sampleScaledCoordinate(
            directSceneY + y,
            directUiHeight,
            directTargetHeight
         );
         int sourceY = readMaxY - 1 - physicalTopY;
         int destination = y * viewportWidth;

         for (int x = 0; x < viewportWidth; x++) {
            int sourceX = sampleScaledCoordinate(
               directSceneX + x,
               directUiWidth,
               directTargetWidth
            ) - readMinX;
            int sourceIndex = sourceY * readWidth + sourceX;
            colorTarget[destination + x] = packedColors.get(sourceIndex) & 0x00FFFFFF;

            if (copyDepth) {
               float gpuDepth = depthBuffer.get(sourceIndex);
               // Direct frames do not pre-clear the CPU depth buffer. Mirror
               // the GL clear value so software fallback never sees stale depth.
               depthTargetArray[destination + x] = gpuDepth < 0.9999999F
                  ? gpuDepth * DEPTH_SCALE
                  : Float.MAX_VALUE;
            }
         }
      }
      return physicalCount;
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

   private static void copyColorReadback(ByteBuffer sourceBuffer, int[] destinationPixels, int width, int height) {
      if (sourceBuffer == null || destinationPixels == null) {
         throw new IllegalStateException("GPU color readback lost its staging or destination buffer");
      }
      sourceBuffer.rewind();
      IntBuffer packedColors = sourceBuffer.asIntBuffer();
      for (int readRow = 0; readRow < height; readRow++) {
         packedColors.position(readRow * width);
         packedColors.get(destinationPixels, (height - 1 - readRow) * width, width);
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
      ByteBuffer colorBuffer = colorReadback;
      FloatBuffer depthBuffer = copyDepth ? depthReadback : null;
      if (colorBuffer == null || copyDepth && depthBuffer == null) {
         throw new IllegalStateException("GPU bounded readback staging buffer was released");
      }

      colorBuffer.clear();
      colorBuffer.limit(count * 4);
      if (copyDepth) {
         depthBuffer.clear();
         depthBuffer.limit(count);
      }

      int readY = viewportHeight - bounds.maxY;
      GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
      GL11.glReadPixels(bounds.minX, readY, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorBuffer);
      if (copyDepth) {
         GL11.glReadPixels(bounds.minX, readY, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthBuffer);
      }

      int[] colorTarget = getFrameColorTarget();
      float[] depthTargetArray = copyDepth ? getFrameDepthTarget() : null;
      int rasterWidth = getFrameRasterWidth();
      if (colorTarget == null || copyDepth && depthTargetArray == null || rasterWidth <= 0) {
         throw new IllegalStateException("GPU bounded readback lost its software frame target");
      }

      int legacyAlpha = Rasterizer3D.alpha;
      for (int readRow = 0; readRow < height; readRow++) {
         int logicalY = bounds.maxY - 1 - readRow;
         int destination = logicalY * rasterWidth + bounds.minX;
         int sourceRow = readRow * width;

         for (int x = 0; x < width; x++) {
            int sourcePixel = sourceRow + x;
            int byteIndex = sourcePixel * 4;
            if (copyDepth) {
               float gpuDepth = depthBuffer.get(sourcePixel);
               if (gpuDepth >= 0.9999999F) {
                  continue;
               }
               depthTargetArray[destination + x] = gpuDepth * DEPTH_SCALE;
            } else {
               // Colour-only bounded readbacks are not used by the batched
               // frame path. Keep this branch conservative if one is added.
            }

            int destinationIndex = destination + x;
            if (copyColor) {
               int rgb = ((colorBuffer.get(byteIndex) & 255) << 16)
                  | ((colorBuffer.get(byteIndex + 1) & 255) << 8)
                  | (colorBuffer.get(byteIndex + 2) & 255);

               if (blendLegacyAlpha && legacyAlpha != 0) {
                  int sourceWeight = 256 - legacyAlpha;
                  int source = ((rgb & 16711935) * sourceWeight >> 8 & 16711935)
                     + ((rgb & 65280) * sourceWeight >> 8 & 65280);
                  int destinationRgb = colorTarget[destinationIndex];
                  int destinationColor = ((destinationRgb & 16711935) * legacyAlpha >> 8 & 16711935)
                     + ((destinationRgb & 65280) * legacyAlpha >> 8 & 65280);
                  colorTarget[destinationIndex] = source + destinationColor;
               } else {
                  colorTarget[destinationIndex] = rgb;
               }
            }
         }
      }
   }

   private static void ensureReadbackCapacity(int pixels, boolean copyDepth) {
      ensureReadbackCapacity(pixels, copyDepth, false);
   }

   private static void ensureReadbackCapacity(int pixels, boolean copyDepth, boolean allowShrink) {
      int colorBytes = pixels * 4;
      if (colorReadback == null
         || colorReadback.capacity() < colorBytes
         || allowShrink && shouldShrink(colorReadback.capacity(), colorBytes)) {
         colorReadback = BufferUtils.createByteBuffer(colorBytes);
      }
      if (copyDepth
         && (depthReadback == null
            || depthReadback.capacity() < pixels
            || allowShrink && shouldShrink(depthReadback.capacity(), pixels))) {
         depthReadback = BufferUtils.createFloatBuffer(pixels);
      } else if (!copyDepth
         && allowShrink
         && depthReadback != null
         && shouldShrink(depthReadback.capacity(), pixels)) {
         depthReadback = BufferUtils.createFloatBuffer(pixels);
      }
   }

   private static void ensureTextureUploadCapacity(int bytes) {
      if (textureUploadBuffer == null
         || textureUploadBuffer.capacity() < bytes
         || shouldShrink(textureUploadBuffer.capacity(), bytes)) {
         textureUploadBuffer = BufferUtils.createByteBuffer(bytes);
      }
   }

   private static boolean shouldShrink(long capacity, long required) {
      return required > 0L
         && capacity > required
         && capacity / BUFFER_SHRINK_RATIO >= required;
   }

   private static void releaseStagingBuffers() {
      colorReadback = null;
      depthReadback = null;
      textureUploadBuffer = null;
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
            float unitX0 = PARTICLE_UNIT_X[segment];
            float unitY0 = PARTICLE_UNIT_Y[segment];
            float unitX1 = PARTICLE_UNIT_X[segment + 1];
            float unitY1 = PARTICLE_UNIT_Y[segment + 1];
            putVertex(centerX, centerY, -testDepth, red, green, blue, clampedAlpha, 0.0F, 0.0F, 0.0F, 1.0F);
            putVertex(centerX + unitX0 * radius, centerY + unitY0 * radius, -testDepth, red, green, blue, clampedAlpha, 0.0F, 0.0F, 0.0F, 1.0F);
            putVertex(centerX + unitX1 * radius, centerY + unitY1 * radius, -testDepth, red, green, blue, clampedAlpha, 0.0F, 0.0F, 0.0F, 1.0F);
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
      ensureBatch(BATCH_DEPTH_ONLY, 0, 3);
      putVertex(x0, y0, -clampDepth(d0), 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
      putVertex(x1, y1, -clampDepth(d1), 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
      putVertex(x2, y2, -clampDepth(d2), 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
   }

   private static void queueColorTriangle(
      float x0, float y0, float d0, int rgb0,
      float x1, float y1, float d1, int rgb1,
      float x2, float y2, float d2, int rgb2,
      float alpha
   ) {
      int mode = alpha >= 0.99999F ? BATCH_OPAQUE : BATCH_TRANSLUCENT;
      ensureBatch(mode, atlasTexture, 3);
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
      ensureBatch(BATCH_OPAQUE, atlasTexture, 3);
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

      prepareBatchPipeline();
      configureBatchMode(batchMode);

      vertexBatch.flip();
      GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBatch, GL15.GL_STREAM_DRAW);
      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, batchVertexCount);

      // The scene renderer shares this GL context with the AWT presentation
      // compositor and texture upload path. Do not carry cached client-array,
      // shader, blend, texture, or depth state across batch boundaries: code
      // between flushes may legitimately mutate that shared state. A stale
      // scene shader is especially visible as intermittent fog-grey frames
      // while the camera is moving. Larger batches still keep this teardown
      // infrequent while making each submitted batch self-contained.
      finishBatchPipeline();
      resetBatch();
   }

   private static void prepareBatchPipeline() {
      if (batchPipelinePrepared) {
         return;
      }

      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
      GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
      GL11.glVertexPointer(3, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, 0L);
      GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
      GL11.glColorPointer(4, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, 12L);
      GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
      GL11.glTexCoordPointer(4, GL11.GL_FLOAT, VERTEX_STRIDE_BYTES, 28L);

      GL11.glColorMask(true, true, true, false);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glDisable(GL11.GL_FOG);
      GL11.glShadeModel(GL11.GL_SMOOTH);

      batchPipelinePrepared = true;
      configuredBatchMode = BATCH_NONE;
      sceneShaderConfigured = false;
   }

   private static void configureBatchMode(int mode) {
      if (configuredBatchMode == mode) {
         return;
      }

      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glDisable(GL11.GL_FOG);

      if (mode == BATCH_DEPTH_ONLY) {
         // Depth-only terrain must not pay for atlas sampling or scene shading.
         // The software rasterizer only records depth here; it never rejects a
         // later scene face against previously written depth.
         GL20.glUseProgram(0);
         sceneShaderConfigured = false;
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glDisable(GL11.GL_BLEND);
         GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
         GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         GL11.glColorMask(false, false, false, false);
         GL11.glDepthFunc(GL11.GL_ALWAYS);
         GL11.glDepthMask(true);
      } else if (mode == BATCH_OPAQUE || mode == BATCH_TRANSLUCENT) {
         GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
         GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         GL11.glColorMask(true, true, true, false);
         GL11.glDepthMask(true);

         if (mode == BATCH_OPAQUE) {
            GL11.glDisable(GL11.GL_BLEND);
            // Preserve the legacy painter order. Coplanar overlays such as
            // carpets/floor decorations otherwise z-fight as the camera moves.
            GL11.glDepthFunc(GL11.GL_ALWAYS);
         } else {
            // Legacy alpha faces are painter ordered and write depth too.
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
         }

         GL11.glEnable(GL11.GL_TEXTURE_2D);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasTexture);
         if (sceneShaderConfigured) {
            GL20.glUseProgram(shaderProgram);
         } else {
            useSceneShader(frameFogEnabled);
            sceneShaderConfigured = true;
         }
      } else if (mode == BATCH_PARTICLE) {
         GL20.glUseProgram(0);
         sceneShaderConfigured = false;
         GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
         GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
         GL11.glDepthFunc(GL11.GL_LEQUAL);
         GL11.glDepthMask(false);
      }

      configuredBatchMode = mode;
   }

   private static void finishBatchPipeline() {
      if (!batchPipelinePrepared) {
         return;
      }

      GL20.glUseProgram(0);
      GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
      GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
      GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glDepthFunc(GL11.GL_ALWAYS);
      GL11.glDepthMask(true);
      GL11.glColorMask(true, true, true, false);
      resetBatchPipelineTracking();
   }

   private static void resetBatchPipelineTracking() {
      batchPipelinePrepared = false;
      configuredBatchMode = BATCH_NONE;
      sceneShaderConfigured = false;
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

   private static double maxAbs9(
      double value0, double value1, double value2,
      double value3, double value4, double value5,
      double value6, double value7, double value8
   ) {
      double max = Math.abs(value0);
      max = Math.max(max, Math.abs(value1));
      max = Math.max(max, Math.abs(value2));
      max = Math.max(max, Math.abs(value3));
      max = Math.max(max, Math.abs(value4));
      max = Math.max(max, Math.abs(value5));
      max = Math.max(max, Math.abs(value6));
      max = Math.max(max, Math.abs(value7));
      max = Math.max(max, Math.abs(value8));
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
      synchronized (CONTEXT_LOCK) {
         if (pbuffer != null) {
            try {
               if (!pbuffer.isCurrent()) {
                  pbuffer.makeCurrent();
               }
               deleteRendererResources();
            } catch (Throwable ignored) {
            }
            try {
               pbuffer.destroy();
            } catch (Throwable ignored) {
            }
         }
         pbuffer = null;
         pbufferSharesPresentationContext = false;
         resetRendererResourceHandles();
         setDirectFrameReady(false);
         frameDirectPresentation = false;
         directModeLogged = false;
         bufferWidth = 0;
         bufferHeight = 0;
         releaseStagingBuffers();
      }
   }

   private static boolean triangleVisible(int x0, int y0, int x1, int y1, int x2, int y2) {
      int minX = Math.max(Rasterizer2D.topX, Math.min(x0, Math.min(x1, x2)));
      int minY = Math.max(Rasterizer2D.topY, Math.min(y0, Math.min(y1, y2)));
      int maxX = Math.min(Rasterizer2D.bottomX, Math.max(x0, Math.max(x1, x2)) + 1);
      int maxY = Math.min(Rasterizer2D.bottomY, Math.max(y0, Math.max(y1, y2)) + 1);
      return minX < maxX && minY < maxY;
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
