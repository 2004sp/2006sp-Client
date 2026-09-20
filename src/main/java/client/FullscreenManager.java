package client;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
public final class FullscreenManager {
   private GraphicsDevice graphicsDevice;
   private DisplayMode previousDisplayMode;

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

      this.previousDisplayMode = this.graphicsDevice.getDisplayMode();
      frame.setUndecorated(true);
      frame.setResizable(false);
      this.graphicsDevice.setFullScreenWindow(frame);
      if (displayMode != null && this.graphicsDevice.isDisplayChangeSupported()) {
         try {
            this.graphicsDevice.setDisplayMode(displayMode);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }
   public final void exitFullscreen() {
      if (this.graphicsDevice == null) {
         return;
      }

      if (this.previousDisplayMode != null && this.graphicsDevice.isDisplayChangeSupported()) {
         try {
            this.graphicsDevice.setDisplayMode(this.previousDisplayMode);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      // Release exclusive ownership before the caller disposes/recreates the
      // frame. Disposing the fullscreen window first can leave Windows/AWT with
      // a stale fullscreen peer and a non-functional maximize state.
      this.graphicsDevice.setFullScreenWindow(null);
      this.previousDisplayMode = null;
   }
}
