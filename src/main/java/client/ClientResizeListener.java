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
      // The fixed renderer is presented at the larger window size by Client,
      // so maximizing must not change screenMode or the selected gameframe.
      if ((this.clientWindow.frame.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH) != 0) {
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
