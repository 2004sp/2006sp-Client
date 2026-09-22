package client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.PixelFormat;

/**
 * AWT-hosted OpenGL presentation surface.
 *
 * The 3D scene is rendered directly into this canvas' backbuffer. The existing
 * software framebuffer is uploaded as a UI texture and composited over that
 * backbuffer before the canvas swaps.
 *
 * The old client does not maintain per-pixel alpha for its software UI. During
 * direct presentation the untouched 3D viewport is filled with a dedicated
 * 0x010203 chroma key, which this canvas discards while preserving real black UI
 * pixels. Outside the viewport the software frame remains fully opaque.
 */
final class GpuPresentationCanvas extends AWTGLCanvas {
   private static final int GL_BGRA = 32993;
   private static final int DIRTY_TILE_SIZE = 64;
   private static final int BUFFER_SHRINK_RATIO = 4;
   private static final DirtyRect[] NO_DIRTY_RECTS = new DirtyRect[0];

   private final ClientWindow owner;
   private final ByteBuffer[] uploadBytes = new ByteBuffer[2];
   private final IntBuffer[] uploadInts = new IntBuffer[2];
   private final int[] uploadPbos = new int[2];
   private int uploadPboCapacity;
   private int uploadPboWriteIndex;
   private boolean pboUnavailable;
   private int[] uiShadow;
   private int uiShadowWidth;
   private int uiShadowHeight;

   private volatile boolean contextReady;
   private volatile boolean failed;
   private int overlayTexture;
   private int overlayWidth;
   private int overlayHeight;
   private int overlayProgram;
   private int uniformOverlay;
   private int uniformSceneRect;
   private int uniformUseSceneKey;

   private int paintingBuffer = -1;
   private boolean frameUploadInProgress;
   private boolean releaseStagingWhenIdle;
   private volatile boolean sceneBackbufferPending;
   private volatile boolean hasPresentedFrame;
   private boolean everPresentedFrame;
   private boolean initialClearNeeded = true;

   private volatile boolean retainedFrameActive;
   private ByteBuffer retainedFrameBytes;
   private int retainedFrameWidth;
   private int retainedFrameHeight;
   private int retainedFrameTexture;
   private int retainedFrameTextureWidth;
   private int retainedFrameTextureHeight;
   private boolean retainedFrameTextureDirty;

   private int softwareFrameTexture;
   private int softwareFrameWidth;
   private int softwareFrameHeight;
   private ByteBuffer softwareFrameUploadBytes;
   private IntBuffer softwareFrameUploadInts;

   GpuPresentationCanvas(ClientWindow owner) throws LWJGLException {
      super(new PixelFormat().withAlphaBits(8).withDepthBits(24));
      this.owner = owner;
      this.setBackground(Color.BLACK);
      this.setIgnoreRepaint(false);
      this.setFocusable(true);

      // GameShell registered its listeners on the original Applet before this
      // canvas became visible. Mirror them here so renderer switching does not
      // change input semantics or coordinates.
      this.addMouseListener(owner);
      this.addMouseMotionListener(owner);
      this.addMouseWheelListener(owner);
      this.addKeyListener(owner);
      this.addFocusListener(owner);
   }

   /**
    * Canvas.update() normally erases the heavyweight peer to its background
    * color before paint(). During live resize/peer recreation that erase can
    * become visible for a compositor frame. Let AWTGLCanvas paint directly so
    * the previously swapped OpenGL front buffer remains on screen.
    */
   @Override
   public void update(Graphics graphics) {
      this.paint(graphics);
   }

   @Override
   public void removeNotify() {
      this.contextReady = false;
      this.sceneBackbufferPending = false;
      resetContextState();
      GpuRasterizer3D.presentationContextLost(this);
      super.removeNotify();
   }

   /**
    * AWTGLCanvas creates and makes its OpenGL context current from paint().
    * Showing the card and requesting a repaint is the safe bootstrap path;
    * calling makeCurrent() before the first paint races peer creation on Windows.
    */
   void requestInitialization() {
      if (this.contextReady || this.failed) {
         return;
      }
      this.owner.setGpuPresentationSurface(true);
      this.repaint();
   }

   boolean isContextReady() {
      return contextReady && !failed && isDisplayable();
   }

   boolean isInitializationPending() {
      return !contextReady && !failed;
   }

   boolean hasPresentedFrame() {
      return this.hasPresentedFrame && isContextReady();
   }

   boolean hasFailed() {
      return failed;
   }

   void deactivate() {
      this.retainedFrameActive = false;
      this.everPresentedFrame = false;
      synchronized (this) {
         this.releaseStagingWhenIdle = true;
         releaseStagingIfIdle();
      }
      this.owner.setGpuPresentationSurface(false);
   }

   boolean runInContext(final Runnable action) {
      if (action == null || !isContextReady()) {
         return false;
      }

      final Throwable[] failure = new Throwable[1];
      final boolean[] displayabilityLost = new boolean[1];
      Runnable contextAction = new Runnable() {
         @Override
         public void run() {
            // isContextReady() was checked on the render thread, but AWT can
            // recreate a heavyweight Canvas peer before this runnable reaches
            // the EDT. Recheck here, immediately before makeCurrent().
            if (!contextReady || failed || !isDisplayable()) {
               displayabilityLost[0] = true;
               return;
            }

            try {
               makeCurrent();
               try {
                  action.run();
               } catch (Throwable throwable) {
                  // The action failed with this presentation context current.
                  // Delete live GL resources before releasing the context and
                  // later clearing their Java handles.
                  if (!isDisplayable()
                     || throwable instanceof IllegalStateException
                        && "Canvas not yet displayable".equals(throwable.getMessage())) {
                     displayabilityLost[0] = true;
                  } else {
                     cleanupCurrentContextResources();
                     failure[0] = throwable;
                  }
               } finally {
                  releaseContext();
               }
            } catch (Throwable throwable) {
               // AWTGLCanvas throws this while a peer is being recreated.
               // Losing displayability is transient and must not permanently
               // disable GPU presentation.
               if (!isDisplayable()
                  || throwable instanceof IllegalStateException
                     && "Canvas not yet displayable".equals(throwable.getMessage())) {
                  displayabilityLost[0] = true;
               } else {
                  failure[0] = throwable;
               }
            }
         }
      };

      try {
         if (EventQueue.isDispatchThread()) {
            contextAction.run();
         } else {
            EventQueue.invokeAndWait(contextAction);
         }
      } catch (Throwable throwable) {
         failure[0] = throwable;
      }

      if (displayabilityLost[0]) {
         handleTransientDisplayabilityLoss();
         return false;
      }
      if (failure[0] != null) {
         markFailed(failure[0]);
         return false;
      }
      return true;
   }

   private void handleTransientDisplayabilityLoss() {
      this.contextReady = false;
      this.sceneBackbufferPending = false;
      resetContextState();
      GpuRasterizer3D.presentationContextLost(this);

      // Keep the requested GPU card alive and let the normal AWT paint
      // lifecycle recreate the context once the peer is displayable again.
      requestInitialization();
   }

   void markSceneBackbufferPending() {
      this.sceneBackbufferPending = true;
   }

   boolean retainPresentedFrameForTransition() {
      if (!hasPresentedFrame() || this.retainedFrameActive) {
         return this.retainedFrameActive;
      }

      final boolean[] captured = new boolean[1];
      boolean ran = runInContext(new Runnable() {
         @Override
         public void run() {
            int width = Math.max(1, GpuPresentationCanvas.this.getWidth());
            int height = Math.max(1, GpuPresentationCanvas.this.getHeight());
            long pixelCount = (long)width * (long)height;
            if (pixelCount > Integer.MAX_VALUE / 4L) {
               return;
            }

            ensureRetainedFrameCapacity((int)pixelCount);
            ByteBuffer destination = retainedFrameBytes;
            destination.clear();
            destination.limit((int)pixelCount * 4);

            GL11.glReadBuffer(GL11.GL_FRONT);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(
               0,
               0,
               width,
               height,
               GL_BGRA,
               GL11.GL_UNSIGNED_BYTE,
               destination
            );
            GL11.glReadBuffer(GL11.GL_BACK);

            destination.position(0);
            destination.limit((int)pixelCount * 4);
            retainedFrameWidth = width;
            retainedFrameHeight = height;
            retainedFrameTextureDirty = true;
            retainedFrameActive = true;
            captured[0] = true;
         }
      });
      return ran && captured[0];
   }

   private void ensureRetainedFrameCapacity(int pixels) {
      int bytes = pixels * 4;
      if (this.retainedFrameBytes == null
         || this.retainedFrameBytes.capacity() < bytes
         || shouldShrink(this.retainedFrameBytes.capacity(), bytes)) {
         this.retainedFrameBytes = BufferUtils.createByteBuffer(bytes).order(ByteOrder.nativeOrder());
      }
   }

   private void finishTransitionFrameRetention() {
      this.retainedFrameActive = false;
      this.retainedFrameBytes = null;
      this.retainedFrameWidth = 0;
      this.retainedFrameHeight = 0;
      this.retainedFrameTextureDirty = false;
      if (this.retainedFrameTexture != 0) {
         int texture = this.retainedFrameTexture;
         this.retainedFrameTexture = 0;
         try {
            GL11.glDeleteTextures(texture);
         } catch (Throwable ignored) {
         }
      }
      this.retainedFrameTextureWidth = 0;
      this.retainedFrameTextureHeight = 0;
   }

   boolean presentFrame(
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
      long pixelCount = (long)uiWidth * (long)uiHeight;
      if (!isContextReady()
         || uiPixels == null
         || uiWidth <= 0
         || uiHeight <= 0
         || pixelCount > Integer.MAX_VALUE / 4L
         || uiPixels.length < pixelCount) {
         return false;
      }

      final FrameState frame;
      synchronized (this) {
         int count = (int)pixelCount;
         trimIdleUploadBuffers(count);
         int bufferIndex = this.paintingBuffer == 0 ? 1 : 0;
         int tileColumns = (uiWidth + DIRTY_TILE_SIZE - 1) / DIRTY_TILE_SIZE;
         int tileRows = (uiHeight + DIRTY_TILE_SIZE - 1) / DIRTY_TILE_SIZE;
         boolean dimensionsChanged = this.uiShadow == null
            || this.uiShadowWidth != uiWidth
            || this.uiShadowHeight != uiHeight;
         DirtyRect[] dirtyRects;

         if (dimensionsChanged || softwareUiChanged) {
            boolean[] dirtyTiles = new boolean[tileColumns * tileRows];
            if (dimensionsChanged) {
               this.uiShadow = new int[count];
               this.uiShadowWidth = uiWidth;
               this.uiShadowHeight = uiHeight;
               System.arraycopy(uiPixels, 0, this.uiShadow, 0, count);
               Arrays.fill(dirtyTiles, true);
            } else {
               detectDirtyTiles(uiPixels, uiWidth, uiHeight, tileColumns, dirtyTiles);
            }

            dirtyRects = buildDirtyRectangles(
               dirtyTiles,
               tileColumns,
               tileRows,
               uiWidth,
               uiHeight
            );
         } else {
            // No software composition happened on this high-FPS scene frame,
            // so the retained overlay texture is already current. Avoid a
            // full framebuffer comparison just to rediscover that fact.
            dirtyRects = NO_DIRTY_RECTS;
         }

         int dirtyPixelCount = 0;
         for (DirtyRect rect : dirtyRects) {
            dirtyPixelCount += rect.width * rect.height;
         }

         int uploadByteCount = dirtyPixelCount * 4;
         if (dirtyPixelCount > 0) {
            ensureUploadCapacity(bufferIndex, dirtyPixelCount, count);
            IntBuffer destination = this.uploadInts[bufferIndex];
            destination.clear();

            int packedPixelOffset = 0;
            for (DirtyRect rect : dirtyRects) {
               rect.byteOffset = packedPixelOffset * 4;
               for (int y = rect.y; y < rect.y + rect.height; y++) {
                  destination.put(uiPixels, y * uiWidth + rect.x, rect.width);
               }
               packedPixelOffset += rect.width * rect.height;
            }
            destination.flip();

            ByteBuffer byteBuffer = this.uploadBytes[bufferIndex];
            byteBuffer.position(0);
            byteBuffer.limit(uploadByteCount);
         }

         frame = new FrameState(
            bufferIndex,
            uiWidth,
            uiHeight,
            targetX,
            targetY,
            Math.max(1, targetWidth),
            Math.max(1, targetHeight),
            sceneX,
            sceneY,
            Math.max(1, sceneWidth),
            Math.max(1, sceneHeight),
            dirtyRects,
            uploadByteCount
         );
         this.paintingBuffer = bufferIndex;
         this.frameUploadInProgress = true;
      }

      this.owner.setGpuPresentationSurface(true);
      boolean presented = runInContext(new Runnable() {
         @Override
         public void run() {
            initializeGlResources();
            uploadOverlay(frame);
            renderFrame(frame);
            try {
               swapBuffers();
               finishTransitionFrameRetention();
            } catch (LWJGLException failure) {
               throw new RuntimeException(failure);
            }
         }
      });

      synchronized (this) {
         this.paintingBuffer = -1;
         this.frameUploadInProgress = false;
         releaseStagingIfIdle();
      }
      if (presented) {
         this.sceneBackbufferPending = false;
         this.hasPresentedFrame = true;
         this.everPresentedFrame = true;
      }
      return presented;
   }

   boolean presentSoftwareFrame(
      int[] pixels,
      int frameWidth,
      int frameHeight,
      int targetX,
      int targetY,
      int targetWidth,
      int targetHeight
   ) {
      long pixelCount = (long)frameWidth * (long)frameHeight;
      if (!isContextReady()
         || pixels == null
         || frameWidth <= 0
         || frameHeight <= 0
         || pixelCount > Integer.MAX_VALUE / 4L
         || pixels.length < pixelCount) {
         return false;
      }

      final SoftwareFrameState frame;
      synchronized (this) {
         int count = (int)pixelCount;
         ensureSoftwareFrameUploadCapacity(count);
         this.softwareFrameUploadInts.clear();
         this.softwareFrameUploadInts.put(pixels, 0, count);
         this.softwareFrameUploadInts.flip();
         this.softwareFrameUploadBytes.position(0);
         this.softwareFrameUploadBytes.limit(count * 4);

         frame = new SoftwareFrameState(
            frameWidth,
            frameHeight,
            targetX,
            targetY,
            Math.max(1, targetWidth),
            Math.max(1, targetHeight)
         );
         this.frameUploadInProgress = true;
      }

      this.owner.setGpuPresentationSurface(true);
      boolean presented = runInContext(new Runnable() {
         @Override
         public void run() {
            initializeGlResources();
            uploadSoftwareFrame(frame, GpuPresentationCanvas.this.softwareFrameUploadBytes);
            renderSoftwareFrame(frame);
            try {
               swapBuffers();
               finishTransitionFrameRetention();
            } catch (LWJGLException failure) {
               throw new RuntimeException(failure);
            }
         }
      });

      synchronized (this) {
         this.frameUploadInProgress = false;
         releaseStagingIfIdle();
      }
      if (presented) {
         this.sceneBackbufferPending = false;
         this.hasPresentedFrame = true;
         this.everPresentedFrame = true;
      }
      return presented;
   }

   private void ensureSoftwareFrameUploadCapacity(int pixels) {
      int bytes = pixels * 4;
      if (this.softwareFrameUploadBytes == null
         || this.softwareFrameUploadBytes.capacity() < bytes
         || shouldShrink(this.softwareFrameUploadBytes.capacity(), bytes)) {
         this.softwareFrameUploadBytes = BufferUtils.createByteBuffer(bytes).order(ByteOrder.nativeOrder());
         this.softwareFrameUploadInts = this.softwareFrameUploadBytes.asIntBuffer();
      }
   }

   private void detectDirtyTiles(
      int[] uiPixels,
      int uiWidth,
      int uiHeight,
      int tileColumns,
      boolean[] dirtyTiles
   ) {
      for (int tileY = 0; tileY * DIRTY_TILE_SIZE < uiHeight; tileY++) {
         int minY = tileY * DIRTY_TILE_SIZE;
         int maxY = Math.min(uiHeight, minY + DIRTY_TILE_SIZE);

         for (int tileX = 0; tileX * DIRTY_TILE_SIZE < uiWidth; tileX++) {
            int minX = tileX * DIRTY_TILE_SIZE;
            int maxX = Math.min(uiWidth, minX + DIRTY_TILE_SIZE);
            boolean dirty = false;

            for (int y = minY; y < maxY && !dirty; y++) {
               int row = y * uiWidth;
               for (int x = minX; x < maxX; x++) {
                  int index = row + x;
                  if (uiPixels[index] != this.uiShadow[index]) {
                     dirty = true;
                     break;
                  }
               }
            }

            if (!dirty) {
               continue;
            }

            dirtyTiles[tileY * tileColumns + tileX] = true;
            int width = maxX - minX;
            for (int y = minY; y < maxY; y++) {
               int offset = y * uiWidth + minX;
               System.arraycopy(uiPixels, offset, this.uiShadow, offset, width);
            }
         }
      }
   }

   private static DirtyRect[] buildDirtyRectangles(
      boolean[] dirtyTiles,
      int tileColumns,
      int tileRows,
      int uiWidth,
      int uiHeight
   ) {
      boolean[] remaining = dirtyTiles.clone();
      ArrayList<DirtyRect> rectangles = new ArrayList<DirtyRect>();

      for (int tileY = 0; tileY < tileRows; tileY++) {
         int tileX = 0;
         while (tileX < tileColumns) {
            if (!remaining[tileY * tileColumns + tileX]) {
               tileX++;
               continue;
            }

            int endTileX = tileX + 1;
            while (endTileX < tileColumns
               && remaining[tileY * tileColumns + endTileX]) {
               endTileX++;
            }

            int endTileY = tileY + 1;
            while (endTileY < tileRows) {
               boolean completeRow = true;
               for (int x = tileX; x < endTileX; x++) {
                  if (!remaining[endTileY * tileColumns + x]) {
                     completeRow = false;
                     break;
                  }
               }
               if (!completeRow) {
                  break;
               }
               endTileY++;
            }

            for (int y = tileY; y < endTileY; y++) {
               Arrays.fill(
                  remaining,
                  y * tileColumns + tileX,
                  y * tileColumns + endTileX,
                  false
               );
            }

            int x = tileX * DIRTY_TILE_SIZE;
            int y = tileY * DIRTY_TILE_SIZE;
            int maxX = Math.min(uiWidth, endTileX * DIRTY_TILE_SIZE);
            int maxY = Math.min(uiHeight, endTileY * DIRTY_TILE_SIZE);
            rectangles.add(new DirtyRect(x, y, maxX - x, maxY - y));
            tileX = endTileX;
         }
      }

      return rectangles.toArray(new DirtyRect[rectangles.size()]);
   }

   private void ensureUploadCapacity(int index, int pixels, int framePixels) {
      int requiredBytes = pixels * 4;
      int frameBytes = framePixels * 4;
      ByteBuffer current = this.uploadBytes[index];
      if (current == null || current.capacity() < requiredBytes) {
         ByteBuffer byteBuffer = BufferUtils.createByteBuffer(requiredBytes).order(ByteOrder.nativeOrder());
         this.uploadBytes[index] = byteBuffer;
         this.uploadInts[index] = byteBuffer.asIntBuffer();
      } else if (shouldShrink(current.capacity(), frameBytes)) {
         ByteBuffer byteBuffer = BufferUtils.createByteBuffer(frameBytes).order(ByteOrder.nativeOrder());
         this.uploadBytes[index] = byteBuffer;
         this.uploadInts[index] = byteBuffer.asIntBuffer();
      }
   }

   private void trimIdleUploadBuffers(int framePixels) {
      int frameBytes = framePixels * 4;
      for (int i = 0; i < this.uploadBytes.length; i++) {
         if (i == this.paintingBuffer) {
            continue;
         }
         ByteBuffer current = this.uploadBytes[i];
         if (current != null && shouldShrink(current.capacity(), frameBytes)) {
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(frameBytes).order(ByteOrder.nativeOrder());
            this.uploadBytes[i] = byteBuffer;
            this.uploadInts[i] = byteBuffer.asIntBuffer();
         }
      }
   }

   @Override
   protected void initGL() {
      try {
         initializeGlResources();
         setVSyncEnabled(false);
         setSwapInterval(0);
         this.contextReady = true;
         System.out.println("GPU direct presentation surface initialized through AWT paint lifecycle.");
      } catch (Throwable failure) {
         markFailed(failure, true);
      }
   }

   @Override
   protected void paintGL() {
      if (failed || this.sceneBackbufferPending) {
         return;
      }

      synchronized (this) {
         this.frameUploadInProgress = true;
      }
      try {
         if (this.retainedFrameActive && renderRetainedFrame()) {
            swapBuffers();
            this.initialClearNeeded = false;
            this.hasPresentedFrame = true;
            return;
         }

         // On the very first handoff from the Java surface, seed the GL canvas
         // with the already-composed software framebuffer. The first visible
         // GPU paint therefore matches what was on screen immediately before
         // the CardLayout switch instead of exposing an empty canvas.
         if (!this.everPresentedFrame && renderInitialSoftwareFrame()) {
            swapBuffers();
            this.initialClearNeeded = false;
            this.hasPresentedFrame = true;
            this.everPresentedFrame = true;
            return;
         }

         if (!this.initialClearNeeded) {
            return;
         }

         this.initialClearNeeded = false;
         GL20.glUseProgram(0);
         GL11.glColorMask(true, true, true, true);
         GL11.glDisable(GL11.GL_SCISSOR_TEST);
         GL11.glViewport(0, 0, Math.max(1, this.getWidth()), Math.max(1, this.getHeight()));
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
         swapBuffers();
      } catch (LWJGLException failure) {
         markFailed(failure, true);
      } finally {
         synchronized (this) {
            this.frameUploadInProgress = false;
            releaseStagingIfIdle();
         }
      }
   }

   private boolean renderInitialSoftwareFrame() {
      BufferedImageGraphicsBuffer frameBuffer = this.owner.frameBuffer;
      if (frameBuffer == null || frameBuffer.pixels == null) {
         return false;
      }

      int frameWidth = frameBuffer.getWidth();
      int frameHeight = frameBuffer.getHeight();
      long pixelCount = (long)frameWidth * (long)frameHeight;
      if (frameWidth <= 0
         || frameHeight <= 0
         || pixelCount > Integer.MAX_VALUE / 4L
         || frameBuffer.pixels.length < pixelCount) {
         return false;
      }

      int count = (int)pixelCount;
      int byteCount = count * 4;

      // paintGL() runs on the AWT event thread while presentSoftwareFrame()
      // can prepare a fallback frame on the game thread. Do not share the
      // mutable position/limit state of softwareFrameUploadInts between them.
      // This seed upload only runs during presentation initialization, so a
      // short-lived staging buffer avoids the race without adding steady-state
      // allocation to software fallback frames.
      ByteBuffer uploadBytes = BufferUtils.createByteBuffer(byteCount).order(ByteOrder.nativeOrder());
      IntBuffer uploadInts = uploadBytes.asIntBuffer();
      uploadInts.put(frameBuffer.pixels, 0, count);
      uploadBytes.position(0);
      uploadBytes.limit(byteCount);

      SoftwareFrameState frame = new SoftwareFrameState(
         frameWidth,
         frameHeight,
         0,
         0,
         Math.max(1, this.getWidth()),
         Math.max(1, this.getHeight())
      );
      uploadSoftwareFrame(frame, uploadBytes);
      renderSoftwareFrame(frame);
      return true;
   }

   private boolean renderRetainedFrame() {
      if (!this.retainedFrameActive
         || this.retainedFrameBytes == null
         || this.retainedFrameWidth <= 0
         || this.retainedFrameHeight <= 0) {
         return false;
      }

      int texture = ensureRetainedFrameTexture(this.retainedFrameWidth, this.retainedFrameHeight);
      if (this.retainedFrameTextureDirty) {
         ByteBuffer upload = this.retainedFrameBytes.duplicate().order(ByteOrder.nativeOrder());
         upload.position(0);
         upload.limit(this.retainedFrameWidth * this.retainedFrameHeight * 4);
         GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
         GL11.glTexSubImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            0,
            0,
            this.retainedFrameWidth,
            this.retainedFrameHeight,
            GL_BGRA,
            GL11.GL_UNSIGNED_BYTE,
            upload
         );
         this.retainedFrameTextureDirty = false;
      }

      int canvasWidth = Math.max(1, this.getWidth());
      int canvasHeight = Math.max(1, this.getHeight());
      GL20.glUseProgram(0);
      GL11.glColorMask(true, true, true, true);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glDisable(GL11.GL_SCISSOR_TEST);
      GL11.glViewport(0, 0, canvasWidth, canvasHeight);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, canvasWidth, canvasHeight, 0.0D, -1.0D, 1.0D);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(canvasWidth, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(canvasWidth, canvasHeight);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, canvasHeight);
      GL11.glEnd();

      GL11.glDisable(GL11.GL_TEXTURE_2D);
      return true;
   }

   private int ensureRetainedFrameTexture(int width, int height) {
      if (this.retainedFrameTexture == 0) {
         this.retainedFrameTexture = GL11.glGenTextures();
         this.retainedFrameTextureWidth = 0;
         this.retainedFrameTextureHeight = 0;
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.retainedFrameTexture);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

      if (this.retainedFrameTextureWidth != width || this.retainedFrameTextureHeight != height) {
         this.retainedFrameTextureWidth = width;
         this.retainedFrameTextureHeight = height;
         GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA8,
            width,
            height,
            0,
            GL_BGRA,
            GL11.GL_UNSIGNED_BYTE,
            (ByteBuffer)null
         );
         this.retainedFrameTextureDirty = true;
      }
      return this.retainedFrameTexture;
   }

   private void initializeGlResources() {
      if (this.overlayProgram == 0) {
         this.overlayProgram = createOverlayProgram();
         this.uniformOverlay = GL20.glGetUniformLocation(this.overlayProgram, "uOverlay");
         this.uniformSceneRect = GL20.glGetUniformLocation(this.overlayProgram, "uSceneRect");
         this.uniformUseSceneKey = GL20.glGetUniformLocation(this.overlayProgram, "uUseSceneKey");
         GL20.glUseProgram(this.overlayProgram);
         GL20.glUniform1i(this.uniformOverlay, 0);
         GL20.glUseProgram(0);
      }

      if (this.overlayTexture == 0) {
         this.overlayTexture = GL11.glGenTextures();
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.overlayTexture);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
      }

      GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
   }

   private void uploadOverlay(FrameState frame) {
      if (this.overlayWidth != frame.uiWidth || this.overlayHeight != frame.uiHeight) {
         this.overlayWidth = frame.uiWidth;
         this.overlayHeight = frame.uiHeight;
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.overlayTexture);
         GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA8,
            this.overlayWidth,
            this.overlayHeight,
            0,
            GL_BGRA,
            GL11.GL_UNSIGNED_BYTE,
            (ByteBuffer)null
         );
      }

      if (frame.uploadByteCount == 0) {
         return;
      }

      if (!this.pboUnavailable) {
         try {
            uploadOverlayWithPbo(frame);
            return;
         } catch (Throwable failure) {
            try {
               GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
            } catch (Throwable ignored) {
            }
            deleteUploadPbos();
            this.pboUnavailable = true;
            System.err.println("GPU presentation PBO upload unavailable; using direct dirty-rectangle uploads.");
         }
      }

      uploadOverlayDirect(frame);
   }

   private void uploadOverlayWithPbo(FrameState frame) {
      ensureUploadPbos(frame.uploadByteCount, frame.uiWidth * frame.uiHeight * 4);

      int pbo = this.uploadPbos[this.uploadPboWriteIndex];
      GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pbo);

      // Orphan the selected PBO before mapping so the CPU never waits for the
      // previous texture upload that used this buffer's old storage.
      GL15.glBufferData(
         GL21.GL_PIXEL_UNPACK_BUFFER,
         (long)this.uploadPboCapacity,
         GL15.GL_STREAM_DRAW
      );

      ByteBuffer mapped = GL15.glMapBuffer(
         GL21.GL_PIXEL_UNPACK_BUFFER,
         GL15.GL_WRITE_ONLY,
         (long)frame.uploadByteCount,
         null
      );
      if (mapped == null) {
         throw new IllegalStateException("Unable to map UI upload PBO");
      }

      boolean mappedBuffer = true;
      try {
         ByteBuffer source = this.uploadBytes[frame.bufferIndex].duplicate();
         source.position(0);
         source.limit(frame.uploadByteCount);
         mapped.put(source);

         if (!GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER)) {
            mappedBuffer = false;
            throw new IllegalStateException("UI upload PBO contents became invalid while mapped");
         }
         mappedBuffer = false;

         GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.overlayTexture);
         for (DirtyRect rect : frame.dirtyRects) {
            GL11.glTexSubImage2D(
               GL11.GL_TEXTURE_2D,
               0,
               rect.x,
               rect.y,
               rect.width,
               rect.height,
               GL_BGRA,
               GL11.GL_UNSIGNED_BYTE,
               (long)rect.byteOffset
            );
         }
      } finally {
         if (mappedBuffer) {
            try {
               GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER);
            } catch (Throwable ignored) {
            }
         }
         GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
      }

      this.uploadPboWriteIndex ^= 1;
   }

   private void uploadOverlayDirect(FrameState frame) {
      GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.overlayTexture);

      for (DirtyRect rect : frame.dirtyRects) {
         int byteCount = rect.width * rect.height * 4;
         ByteBuffer region = this.uploadBytes[frame.bufferIndex].duplicate();
         region.position(rect.byteOffset);
         region.limit(rect.byteOffset + byteCount);
         region = region.slice().order(ByteOrder.nativeOrder());

         GL11.glTexSubImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            rect.x,
            rect.y,
            rect.width,
            rect.height,
            GL_BGRA,
            GL11.GL_UNSIGNED_BYTE,
            region
         );
      }
   }

   private void ensureUploadPbos(int requiredBytes, int frameBytes) {
      if (this.uploadPbos[0] == 0 || this.uploadPbos[1] == 0) {
         this.uploadPbos[0] = GL15.glGenBuffers();
         this.uploadPbos[1] = GL15.glGenBuffers();
         this.uploadPboCapacity = 0;
         this.uploadPboWriteIndex = 0;
      }

      int newCapacity = 0;
      if (this.uploadPboCapacity < requiredBytes) {
         newCapacity = requiredBytes;
      } else if (shouldShrink(this.uploadPboCapacity, frameBytes)) {
         newCapacity = frameBytes;
      }
      if (newCapacity == 0) {
         return;
      }

      for (int pbo : this.uploadPbos) {
         GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pbo);
         GL15.glBufferData(GL21.GL_PIXEL_UNPACK_BUFFER, (long)newCapacity, GL15.GL_STREAM_DRAW);
      }
      GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
      this.uploadPboCapacity = newCapacity;
      this.uploadPboWriteIndex = 0;
   }

   private void uploadSoftwareFrame(SoftwareFrameState frame, ByteBuffer uploadBytes) {
      GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, ensureSoftwareFrameTexture(frame.frameWidth, frame.frameHeight));

      ByteBuffer upload = uploadBytes.duplicate().order(ByteOrder.nativeOrder());
      upload.position(0);
      upload.limit(frame.frameWidth * frame.frameHeight * 4);
      GL11.glTexSubImage2D(
         GL11.GL_TEXTURE_2D,
         0,
         0,
         0,
         frame.frameWidth,
         frame.frameHeight,
         GL_BGRA,
         GL11.GL_UNSIGNED_BYTE,
         upload
      );
   }

   private int ensureSoftwareFrameTexture(int width, int height) {
      if (this.softwareFrameTexture == 0) {
         this.softwareFrameTexture = GL11.glGenTextures();
         this.softwareFrameWidth = 0;
         this.softwareFrameHeight = 0;
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.softwareFrameTexture);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

      if (this.softwareFrameWidth != width || this.softwareFrameHeight != height) {
         this.softwareFrameWidth = width;
         this.softwareFrameHeight = height;
         GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA8,
            width,
            height,
            0,
            GL_BGRA,
            GL11.GL_UNSIGNED_BYTE,
            (ByteBuffer)null
         );
      }

      return this.softwareFrameTexture;
   }

   private void renderSoftwareFrame(SoftwareFrameState frame) {
      int canvasWidth = Math.max(1, this.getWidth());
      int canvasHeight = Math.max(1, this.getHeight());

      GL20.glUseProgram(0);
      GL11.glColorMask(true, true, true, true);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      clearOutsideTarget(
         frame.targetX,
         frame.targetY,
         frame.targetWidth,
         frame.targetHeight,
         canvasWidth,
         canvasHeight
      );

      int viewportY = canvasHeight - frame.targetY - frame.targetHeight;
      GL11.glDisable(GL11.GL_SCISSOR_TEST);
      GL11.glViewport(frame.targetX, viewportY, frame.targetWidth, frame.targetHeight);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, frame.frameWidth, frame.frameHeight, 0.0D, -1.0D, 1.0D);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.softwareFrameTexture);

      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(frame.frameWidth, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(frame.frameWidth, frame.frameHeight);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, frame.frameHeight);
      GL11.glEnd();

      GL11.glDisable(GL11.GL_TEXTURE_2D);
   }

   private void renderFrame(FrameState frame) {
      int canvasWidth = Math.max(1, this.getWidth());
      int canvasHeight = Math.max(1, this.getHeight());

      GL20.glUseProgram(0);
      GL11.glColorMask(true, true, true, true);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      clearOutsideTarget(
         frame.targetX,
         frame.targetY,
         frame.targetWidth,
         frame.targetHeight,
         canvasWidth,
         canvasHeight
      );

      int viewportY = canvasHeight - frame.targetY - frame.targetHeight;
      GL11.glDisable(GL11.GL_SCISSOR_TEST);
      GL11.glViewport(frame.targetX, viewportY, frame.targetWidth, frame.targetHeight);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, frame.uiWidth, frame.uiHeight, 0.0D, -1.0D, 1.0D);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);

      GL20.glUseProgram(this.overlayProgram);
      GL20.glUniform4f(
         this.uniformSceneRect,
         frame.sceneX,
         frame.sceneY,
         frame.sceneX + frame.sceneWidth,
         frame.sceneY + frame.sceneHeight
      );
      GL20.glUniform1f(this.uniformUseSceneKey, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.overlayTexture);

      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(frame.uiWidth, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(frame.uiWidth, frame.uiHeight);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, frame.uiHeight);
      GL11.glEnd();

      GL20.glUseProgram(0);
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_BLEND);
   }

   private static void clearOutsideTarget(
      int targetX,
      int targetY,
      int targetWidth,
      int targetHeight,
      int canvasWidth,
      int canvasHeight
   ) {
      int left = Math.max(0, targetX);
      int bottom = Math.max(0, canvasHeight - targetY - targetHeight);
      int right = Math.min(canvasWidth, targetX + targetWidth);
      int top = Math.min(canvasHeight, canvasHeight - targetY);

      GL11.glEnable(GL11.GL_SCISSOR_TEST);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      clearRect(0, 0, left, canvasHeight);
      clearRect(right, 0, canvasWidth - right, canvasHeight);
      clearRect(left, 0, Math.max(0, right - left), bottom);
      clearRect(left, top, Math.max(0, right - left), canvasHeight - top);
      GL11.glDisable(GL11.GL_SCISSOR_TEST);
   }

   private static void clearRect(int x, int y, int width, int height) {
      if (width <= 0 || height <= 0) {
         return;
      }
      GL11.glScissor(x, y, width, height);
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
   }

   private static int createOverlayProgram() {
      String vertexSource =
         "#version 120\n"
            + "varying vec2 vLogical;\n"
            + "void main() {\n"
            + "  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n"
            + "  gl_TexCoord[0] = gl_MultiTexCoord0;\n"
            + "  vLogical = gl_Vertex.xy;\n"
            + "}\n";

      String fragmentSource =
         "#version 120\n"
            + "uniform sampler2D uOverlay;\n"
            + "uniform vec4 uSceneRect;\n"
            + "uniform float uUseSceneKey;\n"
            + "varying vec2 vLogical;\n"
            + "void main() {\n"
            + "  vec4 ui = texture2D(uOverlay, gl_TexCoord[0].st);\n"
            + "  bool inScene = vLogical.x >= uSceneRect.x && vLogical.x < uSceneRect.z"
            + " && vLogical.y >= uSceneRect.y && vLogical.y < uSceneRect.w;\n"
            + "  vec3 key = vec3(1.0 / 255.0, 2.0 / 255.0, 3.0 / 255.0);\n"
            + "  bool keyedTransparent = uUseSceneKey > 0.5 && inScene"
            + " && ui.a < (0.5 / 255.0)"
            + " && all(lessThan(abs(ui.rgb - key), vec3(0.5 / 255.0)));\n"
            + "  if (keyedTransparent) discard;\n"
            + "  float alpha = ui.a < (0.5 / 255.0) ? 1.0 : ui.a;\n"
            + "  gl_FragColor = vec4(ui.rgb, alpha);\n"
            + "}\n";

      int vertexShader = 0;
      int fragmentShader = 0;
      int program = 0;
      boolean linked = false;
      try {
         vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
         fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);
         program = GL20.glCreateProgram();
         GL20.glAttachShader(program, vertexShader);
         GL20.glAttachShader(program, fragmentShader);
         GL20.glLinkProgram(program);
         if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException("GPU presentation shader link failed: " + GL20.glGetProgramInfoLog(program, 4096));
         }
         linked = true;
         return program;
      } finally {
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
         if (!linked && program != 0) {
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
            throw new IllegalStateException("GPU presentation shader compile failed: " + GL20.glGetShaderInfoLog(shader, 4096));
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

   private void deleteUploadPbos() {
      for (int pbo : this.uploadPbos) {
         if (pbo != 0) {
            try {
               GL15.glDeleteBuffers(pbo);
            } catch (Throwable ignored) {
            }
         }
      }
      Arrays.fill(this.uploadPbos, 0);
      this.uploadPboCapacity = 0;
      this.uploadPboWriteIndex = 0;
   }

   private void cleanupCurrentContextResources() {
      try {
         GL20.glUseProgram(0);
      } catch (Throwable ignored) {
      }
      try {
         GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
      } catch (Throwable ignored) {
      }
      try {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      } catch (Throwable ignored) {
      }

      if (this.overlayTexture != 0) {
         try {
            GL11.glDeleteTextures(this.overlayTexture);
         } catch (Throwable ignored) {
         }
      }
      if (this.retainedFrameTexture != 0) {
         try {
            GL11.glDeleteTextures(this.retainedFrameTexture);
         } catch (Throwable ignored) {
         }
      }
      if (this.softwareFrameTexture != 0) {
         try {
            GL11.glDeleteTextures(this.softwareFrameTexture);
         } catch (Throwable ignored) {
         }
      }
      if (this.overlayProgram != 0) {
         try {
            GL20.glDeleteProgram(this.overlayProgram);
         } catch (Throwable ignored) {
         }
      }
      deleteUploadPbos();

      try {
         GpuRasterizer3D.releasePresentationContextResources(this);
      } catch (Throwable ignored) {
      }
   }

   private void markFailed(Throwable failure) {
      markFailed(failure, false);
   }

   private void markFailed(Throwable failure, boolean contextCurrent) {
      if (contextCurrent) {
         cleanupCurrentContextResources();
      }
      this.failed = true;
      this.contextReady = false;
      this.sceneBackbufferPending = false;
      resetContextState();
      GpuRasterizer3D.presentationContextLost(this);
      System.err.println("GPU direct presentation failed; returning to the Java framebuffer presentation path.");
      failure.printStackTrace();
      this.owner.setGpuPresentationSurface(false);
   }

   private void resetContextState() {
      this.overlayTexture = 0;
      this.overlayWidth = 0;
      this.overlayHeight = 0;
      this.overlayProgram = 0;
      this.uniformOverlay = 0;
      this.uniformSceneRect = 0;
      this.uniformUseSceneKey = 0;
      Arrays.fill(this.uploadPbos, 0);
      this.uploadPboCapacity = 0;
      this.uploadPboWriteIndex = 0;
      this.pboUnavailable = false;
      releaseStagingBuffers(this.retainedFrameActive);
      this.paintingBuffer = -1;
      this.initialClearNeeded = true;
      this.hasPresentedFrame = false;
      this.retainedFrameTexture = 0;
      this.retainedFrameTextureWidth = 0;
      this.retainedFrameTextureHeight = 0;
      this.retainedFrameTextureDirty = this.retainedFrameActive;
      this.softwareFrameTexture = 0;
      this.softwareFrameWidth = 0;
      this.softwareFrameHeight = 0;
   }

   private void releaseStagingIfIdle() {
      if (this.releaseStagingWhenIdle && !this.frameUploadInProgress) {
         releaseStagingBuffers(false);
      }
   }

   private void releaseStagingBuffers(boolean preserveRetainedFrame) {
      Arrays.fill(this.uploadBytes, null);
      Arrays.fill(this.uploadInts, null);
      this.uiShadow = null;
      this.uiShadowWidth = 0;
      this.uiShadowHeight = 0;
      this.softwareFrameUploadBytes = null;
      this.softwareFrameUploadInts = null;
      if (!preserveRetainedFrame) {
         this.retainedFrameBytes = null;
         this.retainedFrameWidth = 0;
         this.retainedFrameHeight = 0;
         this.retainedFrameTextureDirty = false;
      }
      this.releaseStagingWhenIdle = false;
   }

   private static boolean shouldShrink(int capacity, int required) {
      return required > 0
         && capacity > required
         && (long)capacity >= (long)required * BUFFER_SHRINK_RATIO;
   }

   private static final class SoftwareFrameState {
      final int frameWidth;
      final int frameHeight;
      final int targetX;
      final int targetY;
      final int targetWidth;
      final int targetHeight;

      SoftwareFrameState(
         int frameWidth,
         int frameHeight,
         int targetX,
         int targetY,
         int targetWidth,
         int targetHeight
      ) {
         this.frameWidth = frameWidth;
         this.frameHeight = frameHeight;
         this.targetX = targetX;
         this.targetY = targetY;
         this.targetWidth = targetWidth;
         this.targetHeight = targetHeight;
      }
   }

   private static final class DirtyRect {
      final int x;
      final int y;
      final int width;
      final int height;
      int byteOffset;

      DirtyRect(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }

   private static final class FrameState {
      final int bufferIndex;
      final int uiWidth;
      final int uiHeight;
      final int targetX;
      final int targetY;
      final int targetWidth;
      final int targetHeight;
      final int sceneX;
      final int sceneY;
      final int sceneWidth;
      final int sceneHeight;
      final DirtyRect[] dirtyRects;
      final int uploadByteCount;

      FrameState(
         int bufferIndex,
         int uiWidth,
         int uiHeight,
         int targetX,
         int targetY,
         int targetWidth,
         int targetHeight,
         int sceneX,
         int sceneY,
         int sceneWidth,
         int sceneHeight,
         DirtyRect[] dirtyRects,
         int uploadByteCount
      ) {
         this.bufferIndex = bufferIndex;
         this.uiWidth = uiWidth;
         this.uiHeight = uiHeight;
         this.targetX = targetX;
         this.targetY = targetY;
         this.targetWidth = targetWidth;
         this.targetHeight = targetHeight;
         this.sceneX = sceneX;
         this.sceneY = sceneY;
         this.sceneWidth = sceneWidth;
         this.sceneHeight = sceneHeight;
         this.dirtyRects = dirtyRects;
         this.uploadByteCount = uploadByteCount;
      }
   }

}
