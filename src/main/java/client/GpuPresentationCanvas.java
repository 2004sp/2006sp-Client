package client;

import java.awt.EventQueue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;

/**
 * AWT-hosted OpenGL presentation surface.
 *
 * The scene texture is shared with GpuRasterizer3D's Pbuffer, so the 3D frame
 * never has to cross back to Java memory in the normal GPU path. The existing
 * software framebuffer is uploaded as a UI texture and composited over it.
 *
 * The old client does not maintain per-pixel alpha for its software UI. During
 * direct presentation the untouched 3D viewport is filled with a dedicated
 * 0x010203 chroma key, which this canvas discards while preserving real black UI
 * pixels. Outside the viewport the software frame remains fully opaque.
 */
final class GpuPresentationCanvas extends AWTGLCanvas {
   private static final int GL_BGRA = 32993;

   private final ClientWindow owner;
   private final ByteBuffer[] uploadBytes = new ByteBuffer[2];
   private final IntBuffer[] uploadInts = new IntBuffer[2];

   private volatile boolean contextReady;
   private volatile boolean failed;
   private boolean contextInitializationScheduled;
   private int overlayTexture;
   private int overlayWidth;
   private int overlayHeight;
   private int overlayProgram;
   private int uniformOverlay;
   private int uniformSceneRect;
   private int uniformUseSceneKey;

   private FrameState pendingFrame;
   private int paintingBuffer = -1;

   GpuPresentationCanvas(ClientWindow owner) throws LWJGLException {
      super(new PixelFormat().withAlphaBits(8).withDepthBits(24));
      this.owner = owner;
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

   @Override
   public void addNotify() {
      super.addNotify();
      initializeContextAsync();
   }

   @Override
   public void removeNotify() {
      this.contextReady = false;
      this.contextInitializationScheduled = false;
      super.removeNotify();
   }

   void initializeContextAsync() {
      if (this.contextReady || this.failed || this.contextInitializationScheduled) {
         return;
      }
      this.contextInitializationScheduled = true;
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            contextInitializationScheduled = false;
            initializeContextOnEdt();
         }
      });
   }

   private void initializeContextOnEdt() {
      if (this.contextReady || this.failed) {
         return;
      }

      // AWT can report the Canvas as displayable slightly before AWTGLCanvas'
      // native peer is ready for makeCurrent(). Treat that as normal startup
      // ordering and retry instead of permanently disabling GPU presentation.
      if (!isDisplayable()) {
         scheduleContextInitializationRetry();
         return;
      }

      try {
         makeCurrent();
         initializeGlResources();
         setSwapInterval(0);
         this.contextReady = true;
         releaseContext();
         System.out.println("GPU direct presentation surface initialized.");
      } catch (IllegalStateException failure) {
         String message = failure.getMessage();
         if (!isDisplayable() || message != null && message.toLowerCase().contains("displayable")) {
            scheduleContextInitializationRetry();
            return;
         }
         markFailed(failure);
      } catch (Throwable failure) {
         markFailed(failure);
      }
   }

   private void scheduleContextInitializationRetry() {
      if (this.contextReady || this.failed || this.contextInitializationScheduled) {
         return;
      }
      this.contextInitializationScheduled = true;
      javax.swing.Timer retryTimer = new javax.swing.Timer(50, new java.awt.event.ActionListener() {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent event) {
            contextInitializationScheduled = false;
            initializeContextOnEdt();
         }
      });
      retryTimer.setRepeats(false);
      retryTimer.start();
   }

   boolean isContextReady() {
      return contextReady && !failed && isDisplayable();
   }

   void deactivate() {
      this.owner.setGpuPresentationSurface(false);
   }

   boolean queueFrame(
      int sceneTexture,
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
      if (!isContextReady() || sceneTexture == 0 || uiPixels == null || uiWidth <= 0 || uiHeight <= 0) {
         return false;
      }

      int count = uiWidth * uiHeight;
      int bufferIndex;
      synchronized (this) {
         if (this.pendingFrame != null && this.pendingFrame.bufferIndex != this.paintingBuffer) {
            bufferIndex = this.pendingFrame.bufferIndex;
         } else {
            bufferIndex = this.paintingBuffer == 0 ? 1 : 0;
         }

         ensureUploadCapacity(bufferIndex, count);
         IntBuffer destination = this.uploadInts[bufferIndex];
         destination.clear();
         destination.put(uiPixels, 0, Math.min(count, uiPixels.length));
         destination.flip();

         ByteBuffer byteBuffer = this.uploadBytes[bufferIndex];
         byteBuffer.position(0);
         byteBuffer.limit(count * 4);

         this.pendingFrame = new FrameState(
            bufferIndex,
            sceneTexture,
            uiWidth,
            uiHeight,
            targetX,
            targetY,
            Math.max(1, targetWidth),
            Math.max(1, targetHeight),
            sceneX,
            sceneY,
            Math.max(1, sceneWidth),
            Math.max(1, sceneHeight)
         );
      }

      this.owner.setGpuPresentationSurface(true);
      this.repaint();
      return true;
   }

   private void ensureUploadCapacity(int index, int pixels) {
      int bytes = pixels * 4;
      if (this.uploadBytes[index] == null || this.uploadBytes[index].capacity() < bytes) {
         ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes).order(ByteOrder.nativeOrder());
         this.uploadBytes[index] = byteBuffer;
         this.uploadInts[index] = byteBuffer.asIntBuffer();
      }
   }

   @Override
   protected void initGL() {
      try {
         initializeGlResources();
         setSwapInterval(0);
         this.contextReady = true;
      } catch (Throwable failure) {
         markFailed(failure);
      }
   }

   @Override
   protected void paintGL() {
      if (failed) {
         return;
      }

      FrameState frame;
      synchronized (this) {
         frame = this.pendingFrame;
         if (frame == null) {
            return;
         }
         this.pendingFrame = null;
         this.paintingBuffer = frame.bufferIndex;
      }

      try {
         initializeGlResources();
         uploadOverlay(frame);
         renderFrame(frame);
         swapBuffers();
      } catch (Throwable failure) {
         markFailed(failure);
      } finally {
         synchronized (this) {
            this.paintingBuffer = -1;
         }
      }
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

      ByteBuffer source;
      synchronized (this) {
         source = this.uploadBytes[frame.bufferIndex];
         source.position(0);
         source.limit(frame.uiWidth * frame.uiHeight * 4);
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.overlayTexture);
      GL11.glTexSubImage2D(
         GL11.GL_TEXTURE_2D,
         0,
         0,
         0,
         frame.uiWidth,
         frame.uiHeight,
         GL_BGRA,
         GL11.GL_UNSIGNED_BYTE,
         source
      );
   }

   private void renderFrame(FrameState frame) {
      int canvasWidth = Math.max(1, this.getWidth());
      int canvasHeight = Math.max(1, this.getHeight());

      GL20.glUseProgram(0);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glDisable(GL11.GL_SCISSOR_TEST);
      GL11.glViewport(0, 0, canvasWidth, canvasHeight);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

      int viewportY = canvasHeight - frame.targetY - frame.targetHeight;
      GL11.glViewport(frame.targetX, viewportY, frame.targetWidth, frame.targetHeight);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, frame.uiWidth, frame.uiHeight, 0.0D, -1.0D, 1.0D);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();

      // Scene texture is copied from an OpenGL framebuffer, so its vertical
      // orientation is opposite the Java top-down UI texture.
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.sceneTexture);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(frame.sceneX, frame.sceneY);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(frame.sceneX + frame.sceneWidth, frame.sceneY);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(frame.sceneX + frame.sceneWidth, frame.sceneY + frame.sceneHeight);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(frame.sceneX, frame.sceneY + frame.sceneHeight);
      GL11.glEnd();

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
            + "  if (uUseSceneKey > 0.5 && inScene"
            + " && all(lessThan(abs(ui.rgb - key), vec3(0.5 / 255.0)))) discard;\n"
            + "  gl_FragColor = vec4(ui.rgb, 1.0);\n"
            + "}\n";

      int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
      int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);
      int program = GL20.glCreateProgram();
      GL20.glAttachShader(program, vertexShader);
      GL20.glAttachShader(program, fragmentShader);
      GL20.glLinkProgram(program);
      if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
         throw new IllegalStateException("GPU presentation shader link failed: " + GL20.glGetProgramInfoLog(program, 4096));
      }
      GL20.glDeleteShader(vertexShader);
      GL20.glDeleteShader(fragmentShader);
      return program;
   }

   private static int compileShader(int type, String source) {
      int shader = GL20.glCreateShader(type);
      GL20.glShaderSource(shader, source);
      GL20.glCompileShader(shader);
      if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
         throw new IllegalStateException("GPU presentation shader compile failed: " + GL20.glGetShaderInfoLog(shader, 4096));
      }
      return shader;
   }

   private void markFailed(Throwable failure) {
      this.failed = true;
      this.contextReady = false;
      System.err.println("GPU direct presentation failed; returning to the Java framebuffer presentation path.");
      failure.printStackTrace();
      this.owner.setGpuPresentationSurface(false);
   }

   private static final class FrameState {
      final int bufferIndex;
      final int sceneTexture;
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

      FrameState(
         int bufferIndex,
         int sceneTexture,
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
         this.bufferIndex = bufferIndex;
         this.sceneTexture = sceneTexture;
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
      }
   }
}
