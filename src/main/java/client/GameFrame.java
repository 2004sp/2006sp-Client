package client;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
final class GameFrame extends Frame {
   private final GameShell gameShell;
   private static DisplayMode[] displayModes = new DisplayMode[]{
      new DisplayMode(800, 600, 32, 0),
      new DisplayMode(800, 600, 24, 0),
      new DisplayMode(800, 600, 16, 0),
      new DisplayMode(640, 480, 32, 0),
      new DisplayMode(640, 480, 24, 0),
      new DisplayMode(640, 480, 16, 0)
   };
   private FullscreenManager fullscreenManager;

   public GameFrame(GameShell newGameShell, int scalarArgument, int scalarArgument2) {
      this.gameShell = newGameShell;
      this.setTitle("Jagex");
      this.setResizable(false);
      this.setVisible(true);
      this.toFront();
      this.setSize(scalarArgument + 8, scalarArgument2 + 28);
      this.setLocationRelativeTo(null);
   }

   @Override
   public final Graphics getGraphics() {
      Graphics graphics;
      (graphics = super.getGraphics()).translate(4, 24);
      return graphics;
   }
   public final void enterFullscreenMode() {
      try {
         this.fullscreenManager = new FullscreenManager();
         DisplayMode displayMode = this.fullscreenManager.findSupportedDisplayMode(displayModes);
         this.fullscreenManager.enterFullscreen(displayMode, this);
         this.gameShell.fullscreenActive = true;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
   public final void restoreWindowedMode(int scalarArgument, int scalarArgument2) {
      try {
         if (this.gameShell.fullscreenActive) {
            if (this.fullscreenManager != null) {
               this.fullscreenManager.exitFullscreen();
            }
            this.gameShell.fullscreenActive = false;
         }

         this.dispose();
         this.setUndecorated(false);
         this.setTitle("Jagex");
         this.setResizable(false);
         this.setSize(808, 628);
         this.setLocationRelativeTo(null);
         this.setVisible(true);
         this.toFront();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public final void update(Graphics graphics) {
      this.gameShell.update(graphics);
   }

   @Override
   public final void paint(Graphics graphics) {
      this.gameShell.paint(graphics);
   }
}
