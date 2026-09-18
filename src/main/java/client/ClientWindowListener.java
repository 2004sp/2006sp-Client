package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
final class ClientWindowListener extends WindowAdapter {
   private final ClientWindow clientWindow;

   ClientWindowListener(ClientWindow newClientWindow) {
      this.clientWindow = newClientWindow;
   }

   @Override
   public final void windowClosing(WindowEvent windowEvent) {
      if (Client.loggedIn) {
         JOptionPane.showMessageDialog(this.clientWindow.frame, "Please log out first before closing the client.");
      } else {
         System.exit(0);
      }
   }
}
