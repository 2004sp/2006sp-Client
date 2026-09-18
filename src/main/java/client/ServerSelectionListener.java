package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
final class ServerSelectionListener implements ActionListener {
   private final ServerEntry server;

   ServerSelectionListener(ServerEntry serverEntry) {
      this.server = serverEntry;
   }

   @Override
   public final void actionPerformed(ActionEvent actionEvent) {
      try {
         SignLink.startpriv(InetAddress.getByName(Client.serverAddress = this.server.address));
      } catch (UnknownHostException exception) {
         exception.printStackTrace();
      }
   }
}
