package client;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
final class ClientResizeListener extends ComponentAdapter {
   private final ClientWindow clientWindow;

   ClientResizeListener(ClientWindow newClientWindow) {
      this.clientWindow = newClientWindow;
   }

   @Override
   public final void componentResized(ComponentEvent componentEvent) {
      // Do not call setSize() while the JFrame is transitioning to
      // MAXIMIZED_BOTH; doing so can cancel the native maximize operation.
      // Fixed gameframes use the same resizable renderer once maximized, so
      // promote them before returning and keep the selected frame skin/orbs.
      if ((this.clientWindow.frame.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH) != 0) {
         if (Client.loggedIn && Client.screenMode == 0) {
            Client.getClient().setScreenMode(1);
         }
         return;
      }

      Dimension dimension = this.clientWindow.frame.getSize();
      Dimension minimumSize = this.clientWindow.frame.getMinimumSize();
      int width = Math.max(dimension.width, minimumSize.width);
      int height = Math.max(dimension.height, minimumSize.height);
      if (width != dimension.width || height != dimension.height) {
         this.clientWindow.frame.setSize(width, height);
      }
   }
}
