package client;

import java.awt.DisplayMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
final class DisplayModeListener implements ActionListener {
   private final DisplayMode displayMode;

   DisplayModeListener(ClientWindow clientWindow, DisplayMode newDisplayMode) {
      this.displayMode = newDisplayMode;
   }

   @Override
   public final void actionPerformed(ActionEvent actionEvent) {
      if (Client.fullscreenDisplayMode != this.displayMode || !Client.getClient().fullscreenActive) {
         Client.fullscreenDisplayMode = this.displayMode;
         new StringBuilder(String.valueOf(this.displayMode.getWidth()))
            .append("x")
            .append(this.displayMode.getHeight())
            .append(" ")
            .append(this.displayMode.getRefreshRate())
            .append("Hz");
         Client.getClient().setScreenMode(2);
      }
   }
}
