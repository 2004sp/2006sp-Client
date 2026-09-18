package client;

import java.net.DatagramPacket;
final class LanServerDiscoveryListener extends Thread {
   private final DatagramPacket requestPacket;

   LanServerDiscoveryListener(String text, DatagramPacket datagramPacket) {
      super(text);
      this.requestPacket = datagramPacket;
   }
   @Override
   public final void run() {
      try {
         LanServerDiscovery.running = true;
         LanServerDiscovery.getSocket().setSoTimeout(5000);
         LanServerDiscovery.getSocket().send(this.requestPacket);
         LanServerDiscovery.running = false;
         byte[] discoveryResponsePrefixLength = new byte[LanServerDiscovery.discoveryResponsePrefix.length()];
         DatagramPacket datagramPacket = new DatagramPacket(discoveryResponsePrefixLength, discoveryResponsePrefixLength.length);
         LanServerDiscovery.getSocket().receive(datagramPacket);
         if (new String(datagramPacket.getData()).trim().equals(LanServerDiscovery.discoveryResponsePrefix)) {
            byte[] serverAddressPrefixLength = new byte[LanServerDiscovery.serverAddressPrefix.length()];
            DatagramPacket datagramPacket2 = new DatagramPacket(serverAddressPrefixLength, serverAddressPrefixLength.length);
            LanServerDiscovery.getSocket().receive(datagramPacket2);
            String text = new String(datagramPacket2.getData()).trim();
            String substring = ("" + datagramPacket.getAddress()).substring(1);
            if (text.equals("invalid")) {
               text = substring;
            }

            ClientWindow.serverEntries.add(new ServerEntry(text, substring));
            ClientWindow.refreshLanServerMenu();
            return;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
