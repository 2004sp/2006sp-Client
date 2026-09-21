package client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
public class GameShell extends Applet implements FocusListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowListener, Runnable {
   private int shutdownCountdown;
   private int delayTime = 20;
   int minimumSleepTime = 1;
   private final long[] timingSamples = new long[10];
   int fps;
   private boolean unusedFlag = false;
   int canvasWidth;
   int canvasHeight;
   Graphics graphics;
   BufferedImageGraphicsBuffer graphicsBuffer;
   GameFrame gameFrame;
   ClientWindow clientWindow;
   private boolean clearScreen = true;
   boolean hasFocus = true;
   public boolean controlDown = false;
   public boolean shiftDown = false;
   int idleTime;
   int mouseButtonDown;
   public int mouseX;
   public int mouseY;
   int rawMouseX;
   int rawMouseY;
   private int pendingMouseButton;
   private int pendingClickX;
   private int pendingClickY;
   private int rawPendingClickX;
   private int rawPendingClickY;
   private long pendingClickTime;
   int clickButton;
   int clickX;
   int clickY;
   int rawClickX;
   int rawClickY;
   long clickTime;
   final int[] keyStatus = new int[128];
   private final int[] keyQueue = new int[128];
   private int keyQueueReadIndex;
   private int keyQueueWriteIndex;
   public boolean fullscreenActive = false;
   private ArrayList clickTimingSamples = new ArrayList();
   public boolean middleMouseDown;
   private int middleMouseX;
   private int middleMouseY;
   public final void restoreWindowedMode() {
      // Leave exclusive fullscreen before disposing/recreating the window peer.
      // Disposing the frame while GraphicsDevice still owns it as the fullscreen
      // window can leave the restored frame in a state where maximize no longer
      // changes the native window state.
      if (this.clientWindow != null) {
         this.clientWindow.restoreWindowedMode(800, 600);
         this.graphics = this.getGameComponent().getGraphics();
      } else {
         this.gameFrame.restoreWindowedMode(800, 600);
         this.graphics = this.getGameComponent().getGraphics();
      }
   }
   @Override
   public void run() {
      this.getGameComponent().addMouseListener(this);
      this.getGameComponent().addMouseMotionListener(this);
      this.getGameComponent().addKeyListener(this);
      this.getGameComponent().addFocusListener(this);
      this.getGameComponent().addMouseWheelListener(this);
      if (this.gameFrame != null) {
         this.gameFrame.addWindowListener(this);
      }

      this.drawLoadingText(0, "Loading...");
      this.startUp();

      // Preserve the original 50 Hz game simulation while allowing rendering
      // (and render-only camera rotation) to run at a higher cadence.
      final long gameTickNanos = 20000000L;
      long now = System.nanoTime();
      long nextGameTick = now;
      long nextRender = now;
      long lastCameraFrame = now;
      long fpsWindowStart = now;
      int renderedFrames = 0;

      while (this.shutdownCountdown >= 0) {
         now = System.nanoTime();

         int catchUpTicks = 0;
         while (now >= nextGameTick && catchUpTicks < 10) {
            if (this.shutdownCountdown > 0) {
               this.shutdownCountdown--;
               if (this.shutdownCountdown == 0) {
                  this.exit();
                  return;
               }
            }

            this.clickButton = this.pendingMouseButton;
            this.clickX = this.pendingClickX;
            this.clickY = this.pendingClickY;
            this.rawClickX = this.rawPendingClickX;
            this.rawClickY = this.rawPendingClickY;
            this.clickTime = this.pendingClickTime;
            this.pendingMouseButton = 0;
            this.processGameLoop();
            this.keyQueueReadIndex = this.keyQueueWriteIndex;

            nextGameTick += gameTickNanos;
            catchUpTicks++;
            now = System.nanoTime();
         }

         if (catchUpTicks == 10 && now >= nextGameTick) {
            nextGameTick = now + gameTickNanos;
         }

         int targetRenderFps = this.getRenderFpsLimit();
         if (targetRenderFps < 1) {
            targetRenderFps = 1;
         } else if (targetRenderFps > 240) {
            targetRenderFps = 240;
         }
         long renderIntervalNanos = 1000000000L / targetRenderFps;

         if (now >= nextRender) {
            double elapsedSeconds = (now - lastCameraFrame) / 1000000000.0;
            if (elapsedSeconds < 0.0) {
               elapsedSeconds = 0.0;
            } else if (elapsedSeconds > 0.1) {
               elapsedSeconds = 0.1;
            }

            this.processCameraFrame(elapsedSeconds);
            this.showErrorScreen();
            lastCameraFrame = now;

            renderedFrames++;
            long fpsWindowNanos = now - fpsWindowStart;
            if (fpsWindowNanos >= 1000000000L) {
               this.fps = (int)Math.round(renderedFrames * 1000000000.0 / fpsWindowNanos);
               renderedFrames = 0;
               fpsWindowStart = now;
            }

            nextRender += renderIntervalNanos;
            if (nextRender <= now) {
               nextRender = now + renderIntervalNanos;
            }
         }

         long wakeAt = Math.min(nextGameTick, nextRender);
         waitUntil(wakeAt);
      }

      if (this.shutdownCountdown == -1) {
         this.exit();
      }
   }

   private static void waitUntil(long deadlineNanos) {
      while (true) {
         long remaining = deadlineNanos - System.nanoTime();
         if (remaining <= 0L) {
            return;
         }

         if (remaining > 2000000L) {
            long coarseSleep = remaining - 1000000L;
            long sleepMillis = coarseSleep / 1000000L;
            int sleepExtraNanos = (int)(coarseSleep % 1000000L);
            try {
               Thread.sleep(sleepMillis, sleepExtraNanos);
            } catch (InterruptedException exception) {
               Thread.currentThread().interrupt();
               return;
            }
         } else if (remaining > 250000L) {
            Thread.yield();
         }
      }
   }

   private void exit() {
      this.shutdownCountdown = -2;
      this.cleanUpForQuit();
      if (this.gameFrame != null) {
         try {
            Thread.sleep(1000L);
         } catch (Exception exception) {
         }

         try {
            System.exit(0);
            return;
         } catch (Throwable throwable) {
         }
      }
   }
   final void setTargetFps(int scalarArgument) {
      this.delayTime = 1000;
   }

   int getRenderFpsLimit() {
      return 50;
   }

   void processCameraFrame(double elapsedSeconds) {
   }

   @Override
   public final void start() {
      if (this.shutdownCountdown >= 0) {
         this.shutdownCountdown = 0;
      }
   }

   @Override
   public final void stop() {
      if (this.shutdownCountdown >= 0) {
         this.shutdownCountdown = 4000 / this.delayTime;
      }
   }

   @Override
   public final void destroy() {
      this.shutdownCountdown = -1;

      try {
         Thread.sleep(5000L);
      } catch (Exception exception) {
      }

      if (this.shutdownCountdown == -1) {
         this.exit();
      }
   }

   @Override
   public final void update(Graphics newGraphics) {
      if (this.graphics == null) {
         this.graphics = newGraphics;
      }

      this.clearScreen = true;
      this.raiseWelcomeScreen();
   }

   @Override
   public final void paint(Graphics newGraphics) {
      if (this.graphics == null) {
         this.graphics = newGraphics;
      }

      this.clearScreen = true;
      this.raiseWelcomeScreen();
   }

   @Override
   public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
      int wheelRotation = mouseWheelEvent.getWheelRotation();
      if (Client.loggedIn) {
         if (mouseWheelEvent.isControlDown() && Client.getClient().openInterfaceId == -1) {
            Client.cameraZoom += wheelRotation * 35;
            if (Client.cameraZoom < 0) {
               Client.cameraZoom = 0;
            }

            if (Client.cameraZoom > 1200) {
               Client.cameraZoom = 1200;
               return;
            }
         } else {
            this.handleMouseWheel(mouseWheelEvent);
         }
      }
   }

   @Override
   public final void mousePressed(MouseEvent mouseEvent) {
      int pendingClickXOrGetX = mouseEvent.getX();
      int pendingClickYOrGetY = mouseEvent.getY();
      if (Client.screenMode != 0) {
         pendingClickXOrGetX += 4;
         pendingClickYOrGetY += 4;
      }

      if (this.gameFrame != null) {
         pendingClickXOrGetX -= 4;
         pendingClickYOrGetY -= 22;
      }

      int rawPendingClickXOrGetX = pendingClickXOrGetX;
      int rawPendingClickYOrGetY = pendingClickYOrGetY;
      long translatedUiPoint = Client.translateUiInputCoordinates(pendingClickXOrGetX, pendingClickYOrGetY);
      pendingClickXOrGetX = (int)(translatedUiPoint >> 32);
      pendingClickYOrGetY = (int)translatedUiPoint;

      if (pendingClickXOrGetX >= 0 && pendingClickYOrGetY >= 0 && pendingClickXOrGetX <= Client.clientWidth && pendingClickYOrGetY <= Client.clientHeight) {
         this.idleTime = 0;
         int pendingClickX = this.pendingClickX;
         int pendingClickY = this.pendingClickY;
         this.pendingClickX = pendingClickXOrGetX;
         this.pendingClickY = pendingClickYOrGetY;
         this.rawPendingClickX = rawPendingClickXOrGetX;
         this.rawPendingClickY = rawPendingClickYOrGetY;
         long pendingClickTime = this.pendingClickTime;
         this.pendingClickTime = System.currentTimeMillis();
         if (mouseEvent.getButton() == 2) {
            this.middleMouseDown = true;
            this.middleMouseX = pendingClickXOrGetX;
            this.middleMouseY = pendingClickYOrGetY;
         } else {
            if (pendingClickTime != 0L) {
               if (pendingClickX == this.pendingClickX && pendingClickY == this.pendingClickY) {
                  pendingClickXOrGetX = (int)(this.pendingClickTime - pendingClickTime);
                  MouseWheelEventRecord mouseWheelEventRecord = new MouseWheelEventRecord(this.pendingClickX, this.pendingClickY, pendingClickXOrGetX);
                  this.clickTimingSamples.add(mouseWheelEventRecord);
                  if (this.clickTimingSamples.size() >= 50) {
                     pendingClickXOrGetX = -1;
                     pendingClickYOrGetY = -1;
                     Iterator iterator = this.clickTimingSamples.iterator();

                     while (iterator.hasNext()) {
                        pendingClickX = ((MouseWheelEventRecord)iterator.next()).wheelRotation;
                        if (pendingClickXOrGetX < 0) {
                           pendingClickXOrGetX = pendingClickX;
                        }

                        if (pendingClickYOrGetY < 0) {
                           pendingClickYOrGetY = pendingClickX;
                        }

                        if (pendingClickX < pendingClickXOrGetX) {
                           pendingClickXOrGetX = pendingClickX;
                        }

                        if (pendingClickX > pendingClickYOrGetY) {
                           pendingClickYOrGetY = pendingClickX;
                        }
                     }

                     if ((pendingClickX = pendingClickYOrGetY - pendingClickXOrGetX) <= 16 && pendingClickX >= 0) {
                        this.sendClickTimingReport(1);
                     }

                     this.clickTimingSamples.clear();
                  }
               } else {
                  this.clickTimingSamples.clear();
               }
            }

            if (mouseEvent.isMetaDown()) {
               this.pendingMouseButton = 2;
               this.mouseButtonDown = 2;
            } else {
               this.pendingMouseButton = 1;
               this.mouseButtonDown = 1;
            }
         }
      }
   }

   @Override
   public final void mouseReleased(MouseEvent mouseEvent) {
      this.idleTime = 0;
      this.mouseButtonDown = 0;
      this.middleMouseDown = false;
   }

   @Override
   public final void mouseClicked(MouseEvent mouseEvent) {
   }

   @Override
   public final void mouseEntered(MouseEvent mouseEvent) {
   }

   @Override
   public final void mouseExited(MouseEvent mouseEvent) {
      this.idleTime = 0;
      this.mouseX = -1;
      this.mouseY = -1;
      this.rawMouseX = -1;
      this.rawMouseY = -1;
   }

   @Override
   public final void mouseDragged(MouseEvent mouseEvent) {
      int mouseXOrGetX = mouseEvent.getX();
      int mouseYOrGetY = mouseEvent.getY();
      if (Client.screenMode != 0) {
         mouseXOrGetX += 4;
         mouseYOrGetY += 4;
      }

      if (this.gameFrame != null) {
         mouseXOrGetX -= 4;
         mouseYOrGetY -= 22;
      }

      this.rawMouseX = mouseXOrGetX;
      this.rawMouseY = mouseYOrGetY;

      if (this.middleMouseDown) {
         mouseYOrGetY = this.middleMouseX - mouseEvent.getX();
         mouseXOrGetX = this.middleMouseY - mouseEvent.getY();
         this.handleMiddleMouseDrag(mouseYOrGetY, -mouseXOrGetX);
         this.middleMouseX = mouseEvent.getX();
         this.middleMouseY = mouseEvent.getY();
      } else {
         long translatedUiPoint = Client.translateUiInputCoordinates(mouseXOrGetX, mouseYOrGetY);
         mouseXOrGetX = (int)(translatedUiPoint >> 32);
         mouseYOrGetY = (int)translatedUiPoint;
         if (System.currentTimeMillis() - this.pendingClickTime >= 250L || Math.abs(this.clickX - mouseXOrGetX) > 5 || Math.abs(this.clickY - mouseYOrGetY) > 5) {
            this.idleTime = 0;
            this.mouseX = mouseXOrGetX;
            this.mouseY = mouseYOrGetY;
         }
      }
   }
   void handleMiddleMouseDrag(int scalarArgument, int scalarArgument2) {
   }

   @Override
   public final void mouseMoved(MouseEvent mouseEvent) {
      int mouseXOrGetX = mouseEvent.getX();
      int mouseYOrGetY = mouseEvent.getY();
      if (Client.screenMode != 0) {
         mouseXOrGetX += 4;
         mouseYOrGetY += 4;
      }

      if (this.gameFrame != null) {
         mouseXOrGetX -= 4;
         mouseYOrGetY -= 22;
      }

      this.rawMouseX = mouseXOrGetX;
      this.rawMouseY = mouseYOrGetY;
      long translatedUiPoint = Client.translateUiInputCoordinates(mouseXOrGetX, mouseYOrGetY);
      mouseXOrGetX = (int)(translatedUiPoint >> 32);
      mouseYOrGetY = (int)translatedUiPoint;

      if (System.currentTimeMillis() - this.pendingClickTime >= 250L || Math.abs(this.clickX - mouseXOrGetX) > 5 || Math.abs(this.clickY - mouseYOrGetY) > 5) {
         this.idleTime = 0;
         this.mouseX = mouseXOrGetX;
         this.mouseY = mouseYOrGetY;
      }
   }

   @Override
   public final void keyPressed(KeyEvent keyEvent) {
      this.idleTime = 0;
      int keyCode = keyEvent.getKeyCode();
      if (keyCode == KeyEvent.VK_F12 && this.fullscreenActive && this.clientWindow != null) {
         this.clientWindow.toggleFullscreenMenuBar();
         keyEvent.consume();
         return;
      }
      int keyQueueOrGetKeyChar;
      if ((keyQueueOrGetKeyChar = keyEvent.getKeyChar()) < 30) {
         keyQueueOrGetKeyChar = 0;
      }

      if (keyCode == 37) {
         keyQueueOrGetKeyChar = 1;
      }

      if (keyCode == 39) {
         keyQueueOrGetKeyChar = 2;
      }

      if (keyCode == 38) {
         keyQueueOrGetKeyChar = 3;
      }

      if (keyCode == 40) {
         keyQueueOrGetKeyChar = 4;
      }

      if (keyCode == 17) {
         keyQueueOrGetKeyChar = 5;
      }

      if (keyCode == 8) {
         keyQueueOrGetKeyChar = 8;
      }

      if (keyCode == 127) {
         keyQueueOrGetKeyChar = 8;
      }

      if (keyCode == 9) {
         keyQueueOrGetKeyChar = 9;
      }

      if (keyCode == 10) {
         keyQueueOrGetKeyChar = 10;
      }

      if (keyCode >= 112 && keyCode <= 123) {
         keyQueueOrGetKeyChar = keyCode + 1008 - 112;
      }

      if (keyCode == 36) {
         keyQueueOrGetKeyChar = 1000;
      }

      if (keyCode == 35) {
         keyQueueOrGetKeyChar = 1001;
      }

      if (keyCode == 33) {
         keyQueueOrGetKeyChar = 1002;
      }

      if (keyCode == 34) {
         keyQueueOrGetKeyChar = 1003;
      }

      if (keyQueueOrGetKeyChar > 0 && keyQueueOrGetKeyChar < 128) {
         this.keyStatus[keyQueueOrGetKeyChar] = 1;
      }

      if (keyQueueOrGetKeyChar > 4) {
         this.keyQueue[this.keyQueueWriteIndex] = keyQueueOrGetKeyChar;
         this.keyQueueWriteIndex = this.keyQueueWriteIndex + 1 & 127;
      }

      if (keyEvent.getKeyCode() == 16) {
         this.shiftDown = true;
      }

      if (keyEvent.getKeyCode() == 17) {
         this.controlDown = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[3]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 3;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[0]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 0;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[1]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 1;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[2]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 2;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[4]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 4;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[5]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 5;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[6]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 6;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[7]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 8;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[8]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 9;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[10]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 11;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[11]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 12;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[12]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 13;
         Client.getClient().tabAreaAltered = true;
      }

      if (keyEvent.getKeyCode() == Client.keybinds[9]) {
         Client.getClient().needDrawTabArea = true;
         Client.getClient().currentTab = 10;
         Client.getClient().tabAreaAltered = true;
      }

      keyEvent.consume();
   }

   @Override
   public final void keyReleased(KeyEvent keyEvent) {
      this.idleTime = 0;
      int keyCode = keyEvent.getKeyCode();
      char keyStatuIndex;
      if ((keyStatuIndex = keyEvent.getKeyChar()) < 30) {
         keyStatuIndex = 0;
      }

      if (keyCode == 37) {
         keyStatuIndex = 1;
      }

      if (keyCode == 39) {
         keyStatuIndex = 2;
      }

      if (keyCode == 38) {
         keyStatuIndex = 3;
      }

      if (keyCode == 40) {
         keyStatuIndex = 4;
      }

      if (keyCode == 17) {
         keyStatuIndex = 5;
      }

      if (keyCode == 8) {
         keyStatuIndex = '\b';
      }

      if (keyCode == 127) {
         keyStatuIndex = '\b';
      }

      if (keyCode == 9) {
         keyStatuIndex = '\t';
      }

      if (keyCode == 10) {
         keyStatuIndex = '\n';
      }

      if (keyStatuIndex > 0 && keyStatuIndex < 128) {
         this.keyStatus[keyStatuIndex] = 0;
      }

      if (keyEvent.getKeyCode() == 16) {
         this.shiftDown = false;
      }

      if (keyEvent.getKeyCode() == 17) {
         this.controlDown = false;
      }
   }

   @Override
   public final void keyTyped(KeyEvent keyEvent) {
   }
   final int readChar(int newKeyQueue) {
      newKeyQueue = -1;
      if (this.keyQueueWriteIndex != this.keyQueueReadIndex) {
         newKeyQueue = this.keyQueue[this.keyQueueReadIndex];
         this.keyQueueReadIndex = this.keyQueueReadIndex + 1 & 127;
      }

      return newKeyQueue;
   }

   @Override
   public final void focusGained(FocusEvent focusEvent) {
      this.hasFocus = true;
      this.clearScreen = true;
      this.raiseWelcomeScreen();
   }

   @Override
   public final void focusLost(FocusEvent focusEvent) {
      this.hasFocus = false;

      for (int keyStatuIndex = 0; keyStatuIndex < 128; keyStatuIndex++) {
         this.keyStatus[keyStatuIndex] = 0;
      }
   }

   @Override
   public final void windowActivated(WindowEvent windowEvent) {
   }

   @Override
   public final void windowClosed(WindowEvent windowEvent) {
   }

   @Override
   public final void windowClosing(WindowEvent windowEvent) {
      this.destroy();
   }

   @Override
   public final void windowDeactivated(WindowEvent windowEvent) {
   }

   @Override
   public final void windowDeiconified(WindowEvent windowEvent) {
   }

   @Override
   public final void windowIconified(WindowEvent windowEvent) {
   }

   @Override
   public final void windowOpened(WindowEvent windowEvent) {
   }
   void startUp() {
   }
   void processGameLoop() {
   }
   void cleanUpForQuit() {
   }
   void showErrorScreen() {
   }
   void raiseWelcomeScreen() {
   }
   void sendClickTimingReport(int scalarArgument) {
   }
   void handleMouseWheel(MouseWheelEvent mouseWheelEvent) {
   }
   Component getGameComponent() {
      return this.gameFrame != null ? this.gameFrame : this;
   }
   public void startRunnable(Runnable runnable, int scalarArgument) {
      Thread thread;
      (thread = new Thread(runnable)).start();
      thread.setPriority(scalarArgument);
   }
   void drawLoadingText(int scalarArgument, String text) {
      while (this.graphics == null) {
         this.graphics = this.getGameComponent().getGraphics();

         try {
            this.getGameComponent().repaint();
         } catch (Exception exception) {
         }

         try {
            Thread.sleep(1000L);
         } catch (Exception exception2) {
         }
      }

      Font font = new Font("Helvetica", 1, 13);
      FontMetrics fontMetrics = this.getGameComponent().getFontMetrics(font);
      Font font2 = new Font("Helvetica", 0, 13);
      this.getGameComponent().getFontMetrics(font2);
      if (this.clearScreen) {
         this.graphics.setColor(Color.black);
         this.graphics.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
         this.clearScreen = false;
      }

      Color color = new Color(140, 17, 17);
      int localCanvasHeight = this.canvasHeight / 2 - 18;
      this.graphics.setColor(color);
      this.graphics.drawRect(this.canvasWidth / 2 - 152, localCanvasHeight, 304, 34);
      this.graphics.fillRect(this.canvasWidth / 2 - 150, localCanvasHeight + 2, scalarArgument * 3, 30);
      this.graphics.setColor(Color.black);
      this.graphics.fillRect(this.canvasWidth / 2 - 150 + scalarArgument * 3, localCanvasHeight + 2, 300 - scalarArgument * 3, 30);
      this.graphics.setFont(font);
      this.graphics.setColor(Color.white);
      this.graphics.drawString(text, (this.canvasWidth - fontMetrics.stringWidth(text)) / 2, localCanvasHeight + 22);
   }
}
