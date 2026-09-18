package worldmap;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
public class WorldMapGameShell extends Applet implements FocusListener, KeyListener, MouseListener, MouseMotionListener, WindowListener, Runnable {
   private int shutdownCountdown = 0;
   private int delayTime = 20;
   private int minimumSleepTime = 1;
   private long[] timingSamples = new long[10];
   private int fps = 0;
   private boolean unusedFlag = false;
   public int canvasWidth;
   public int canvasHeight;
   public Graphics graphics;
   public WorldMapGraphicsBuffer graphicsBuffer;
   public WorldMapFrame frame;
   private boolean clearScreen = true;
   public int mouseButtonDown = 0;
   public int mouseX = 0;
   public int mouseY = 0;
   private int pendingMouseButton = 0;
   private int pendingClickX = 0;
   private int pendingClickY = 0;
   private long pendingClickTime = 0L;
   public int clickButton = 0;
   public int clickX = 0;
   public int clickY = 0;
   public int[] keyStatus = new int[128];
   public int[] keyQueue = new int[128];
   public int middleMouseX = 0;
   public int middleMouseY = 0;

   @Override
   public void stop() {
      if (this.shutdownCountdown >= 0) {
         this.shutdownCountdown = 4000 / this.delayTime;
      }
   }
   public void raiseWelcomeScreen() {
   }
   public final void drawLoadingText(int scalarArgument, String text) {
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

   @Override
   public void mouseReleased(MouseEvent mouseEvent) {
      this.mouseButtonDown = 0;
   }

   @Override
   public void keyPressed(KeyEvent keyEvent) {
      int keyCode = keyEvent.getKeyCode();
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
         this.keyQueue[this.middleMouseY] = keyQueueOrGetKeyChar;
         this.middleMouseY = this.middleMouseY + 1 & 127;
      }
   }
   public static void startRunnable(Runnable runnable, int scalarArgument) {
      Thread thread;
      (thread = new Thread(runnable)).start();
      thread.setPriority(1);
   }

   @Override
   public void windowClosing(WindowEvent windowEvent) {
      this.frame.setVisible(false);
   }

   private void exit() {
      this.shutdownCountdown = -2;
      this.cleanUpForQuit();
      if (this.frame != null) {
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

   @Override
   public void update(Graphics newGraphics) {
      if (this.graphics == null) {
         this.graphics = newGraphics;
      }

      this.clearScreen = true;
      this.raiseWelcomeScreen();
   }

   @Override
   public void mouseEntered(MouseEvent mouseEvent) {
   }

   @Override
   public void mouseExited(MouseEvent mouseEvent) {
      this.mouseX = -1;
      this.mouseY = -1;
   }

   @Override
   public void windowOpened(WindowEvent windowEvent) {
   }

   @Override
   public void windowDeiconified(WindowEvent windowEvent) {
   }

   @Override
   public void windowActivated(WindowEvent windowEvent) {
   }
   public void startUp() {
   }

   @Override
   public void start() {
      if (this.shutdownCountdown >= 0) {
         this.shutdownCountdown = 0;
      }
   }
   public void showErrorScreen() {
   }
   public final Component getGameComponent() {
      return this.frame != null ? this.frame : this;
   }

   @Override
   public void mouseClicked(MouseEvent mouseEvent) {
   }

   @Override
   public void mousePressed(MouseEvent mouseEvent) {
      int pendingClickXOrGetX = mouseEvent.getX();
      int pendingClickYOrGetY = mouseEvent.getY();
      if (this.frame != null) {
         pendingClickXOrGetX -= 4;
         pendingClickYOrGetY -= 22;
      }

      this.pendingClickX = pendingClickXOrGetX;
      this.pendingClickY = pendingClickYOrGetY;
      this.pendingClickTime = System.currentTimeMillis();
      if (mouseEvent.isMetaDown()) {
         this.pendingMouseButton = 2;
         this.mouseButtonDown = 2;
      } else {
         this.pendingMouseButton = 1;
         this.mouseButtonDown = 1;
      }
   }

   @Override
   public void mouseDragged(MouseEvent mouseEvent) {
      int mouseXOrGetX = mouseEvent.getX();
      int mouseYOrGetY = mouseEvent.getY();
      if (this.frame != null) {
         mouseXOrGetX -= 4;
         mouseYOrGetY -= 22;
      }

      this.mouseX = mouseXOrGetX;
      this.mouseY = mouseYOrGetY;
   }

   @Override
   public void mouseMoved(MouseEvent mouseEvent) {
      int mouseXOrGetX = mouseEvent.getX();
      int mouseYOrGetY = mouseEvent.getY();
      if (this.frame != null) {
         mouseXOrGetX -= 4;
         mouseYOrGetY -= 22;
      }

      this.mouseX = mouseXOrGetX;
      this.mouseY = mouseYOrGetY;
   }

   @Override
   public void keyTyped(KeyEvent keyEvent) {
   }

   @Override
   public void windowDeactivated(WindowEvent windowEvent) {
   }

   @Override
   public void paint(Graphics newGraphics) {
      if (this.graphics == null) {
         this.graphics = newGraphics;
      }

      this.clearScreen = true;
      this.raiseWelcomeScreen();
   }

   @Override
   public void destroy() {
      this.shutdownCountdown = -1;

      try {
         Thread.sleep(5000L);
      } catch (Exception exception) {
      }

      if (this.shutdownCountdown == -1) {
         this.exit();
      }
   }
   public void cleanUpForQuit() {
   }
   public void processGameLoop() {
   }

   @Override
   public void focusLost(FocusEvent focusEvent) {
      for (int keyStatuIndex = 0; keyStatuIndex < 128; keyStatuIndex++) {
         this.keyStatus[keyStatuIndex] = 0;
      }
   }

   @Override
   public void keyReleased(KeyEvent keyEvent) {
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
   }

   @Override
   public void windowClosed(WindowEvent windowEvent) {
   }
   @Override
   public void run() {
      this.getGameComponent().addMouseListener(this);
      this.getGameComponent().addMouseMotionListener(this);
      this.getGameComponent().addKeyListener(this);
      this.getGameComponent().addFocusListener(this);
      if (this.frame != null) {
         this.frame.addWindowListener(this);
      }

      this.drawLoadingText(0, "Loading...");
      this.startUp();
      int timingSampleIndex2 = 0;
      int scalar = 256;
      int scalar2 = 1;
      int scalar3 = 0;

      for (int timingSampleIndex = 0; timingSampleIndex < 10; timingSampleIndex++) {
         this.timingSamples[timingSampleIndex] = System.currentTimeMillis();
      }

      for (; this.shutdownCountdown >= 0; this.showErrorScreen()) {
         if (this.shutdownCountdown > 0) {
            this.shutdownCountdown--;
            if (this.shutdownCountdown == 0) {
               this.exit();
               return;
            }
         }

         int scalar4 = scalar;
         int scalar5 = scalar2;
         scalar = 300;
         scalar2 = 1;
         long timingSampleOrCurrentTimeMillis = System.currentTimeMillis();
         if (this.timingSamples[timingSampleIndex2] == 0L) {
            scalar = scalar4;
            scalar2 = scalar5;
         } else if (timingSampleOrCurrentTimeMillis > this.timingSamples[timingSampleIndex2]) {
            scalar = (int)(2560 * this.delayTime / (timingSampleOrCurrentTimeMillis - this.timingSamples[timingSampleIndex2]));
         }

         if (scalar < 25) {
            scalar = 25;
         }

         if (scalar > 256) {
            scalar = 256;
            scalar2 = (int)(this.delayTime - (timingSampleOrCurrentTimeMillis - this.timingSamples[timingSampleIndex2]) / 10L);
         }

         if (scalar2 > this.delayTime) {
            scalar2 = this.delayTime;
         }

         this.timingSamples[timingSampleIndex2] = timingSampleOrCurrentTimeMillis;
         timingSampleIndex2 = (timingSampleIndex2 + 1) % 10;
         if (scalar2 > 1) {
            for (int timingSampleIndex3 = 0; timingSampleIndex3 < 10; timingSampleIndex3++) {
               if (this.timingSamples[timingSampleIndex3] != 0L) {
                  this.timingSamples[timingSampleIndex3] = this.timingSamples[timingSampleIndex3] + scalar2;
               }
            }
         }

         if (scalar2 < this.minimumSleepTime) {
            scalar2 = this.minimumSleepTime;
         }

         try {
            Thread.sleep(scalar2);
         } catch (InterruptedException exception) {
         }

         while (scalar3 < 256) {
            this.clickButton = this.pendingMouseButton;
            this.clickX = this.pendingClickX;
            this.clickY = this.pendingClickY;
            this.pendingMouseButton = 0;
            this.processGameLoop();
            this.middleMouseX = this.middleMouseY;
            scalar3 += scalar;
         }

         scalar3 &= 255;
         if (this.delayTime > 0) {
            this.fps = scalar * 1000 / (this.delayTime << 8);
         }
      }

      if (this.shutdownCountdown == -1) {
         this.exit();
      }
   }

   @Override
   public void focusGained(FocusEvent focusEvent) {
      this.clearScreen = true;
      this.raiseWelcomeScreen();
   }

   @Override
   public void windowIconified(WindowEvent windowEvent) {
   }
}
