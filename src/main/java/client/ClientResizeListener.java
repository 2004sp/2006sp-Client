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
      Dimension dimension = this.clientWindow.frame.getSize();
      Dimension minimumSize = this.clientWindow.frame.getMinimumSize();
      if (dimension.width < minimumSize.width) {
         dimension.width = minimumSize.width;
      }

      if (dimension.height < minimumSize.height) {
         dimension.height = minimumSize.height;
      }

      this.clientWindow.frame.setSize(dimension);
   }
}
