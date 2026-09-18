package client;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
public final class FullscreenManager {
   private GraphicsDevice graphicsDevice;

   public FullscreenManager() {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      this.graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
   }
   public final DisplayMode[] getDisplayModes() {
      return this.graphicsDevice.getDisplayModes();
   }
   public final DisplayMode findSupportedDisplayMode(DisplayMode[] values) {
      DisplayMode[] displayModes = this.graphicsDevice.getDisplayModes();

      for (int loopIndex = 0; loopIndex < values.length; loopIndex++) {
         for (int displayModeIndex = 0; displayModeIndex < displayModes.length; displayModeIndex++) {
            DisplayMode displayMode = values[loopIndex];
            DisplayMode displayMode3 = displayModes[displayModeIndex];
            DisplayMode displayMode2 = displayMode;
            if (displayMode.getWidth() != displayMode3.getWidth() || displayMode2.getHeight() != displayMode3.getHeight()
               ? false
               : (
                  displayMode2.getBitDepth() != -1 && displayMode3.getBitDepth() != -1 && displayMode2.getBitDepth() != displayMode3.getBitDepth()
                     ? false
                     : displayMode2.getRefreshRate() == 0 || displayMode3.getRefreshRate() == 0 || displayMode2.getRefreshRate() == displayMode3.getRefreshRate()
               )) {
               return values[loopIndex];
            }
         }
      }

      return null;
   }
   public final void enterFullscreen(DisplayMode displayMode, Frame frame) {
      if (this.graphicsDevice == null) {
         GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
         this.graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
      }

      frame.setUndecorated(true);
      frame.setResizable(false);
      this.graphicsDevice.setFullScreenWindow(frame);
      if (displayMode != null && this.graphicsDevice.isDisplayChangeSupported()) {
         try {
            this.graphicsDevice.setDisplayMode(displayMode);
            return;
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }
   public final void exitFullscreen() {
      Window window;
      if ((window = this.graphicsDevice.getFullScreenWindow()) != null) {
         window.dispose();
      }

      this.graphicsDevice.setFullScreenWindow(null);
   }
}
