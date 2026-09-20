package client;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
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
      // Use borderless windowed fullscreen instead of AWT exclusive fullscreen.
      // Exclusive mode changes the monitor display mode and Windows can blank or
      // minimize the client when focus moves to another monitor. A borderless
      // normal window keeps rendering while other monitors are being used.
      if (frame.getGraphicsConfiguration() != null) {
         this.graphicsDevice = frame.getGraphicsConfiguration().getDevice();
      } else if (this.graphicsDevice == null) {
         GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
         this.graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
      }

      Rectangle bounds = this.graphicsDevice.getDefaultConfiguration().getBounds();
      frame.setUndecorated(true);
      frame.setResizable(false);
      frame.setExtendedState(Frame.NORMAL);
      frame.setBounds(bounds);
      frame.setVisible(true);
      frame.toFront();
   }

   public final void exitFullscreen() {
      // Borderless fullscreen does not own the GraphicsDevice and does not
      // change its DisplayMode, so there is no exclusive state to release.
   }
}
