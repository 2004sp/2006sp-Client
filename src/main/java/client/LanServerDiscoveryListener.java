package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

final class LanServerDiscoveryListener extends Thread {
   private static final int DISCOVERY_TIMEOUT_MS = 5000;
   private static final int RESPONSE_BUFFER_SIZE = 512;
   private final DatagramPacket requestPacket;

   LanServerDiscoveryListener(String text, DatagramPacket datagramPacket) {
      super(text);
      this.requestPacket = datagramPacket;
   }

   @Override
   public final void run() {
      DatagramSocket socket = null;

      try {
         socket = LanServerDiscovery.getSocket();
         sendDiscoveryRequests(socket);

         long deadline = System.currentTimeMillis() + DISCOVERY_TIMEOUT_MS;
         Set<String> waitingForServerName = new HashSet<String>();
         Set<String> discoveredAddresses = new HashSet<String>();

         while (true) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0L) {
               break;
            }

            socket.setSoTimeout((int)remaining);
            byte[] responseBytes = new byte[RESPONSE_BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);

            try {
               socket.receive(responsePacket);
            } catch (SocketTimeoutException timeout) {
               break;
            }

            String response = new String(
               responsePacket.getData(),
               responsePacket.getOffset(),
               responsePacket.getLength(),
               "UTF-8"
            ).trim();
            String address = responsePacket.getAddress().getHostAddress();

            if (LanServerDiscovery.discoveryResponsePrefix.equals(response)) {
               waitingForServerName.add(address);
               continue;
            }

            if (!waitingForServerName.remove(address) || !discoveredAddresses.add(address)) {
               continue;
            }

            String serverName = response;
            if (serverName.length() == 0 || "invalid".equals(serverName)) {
               serverName = address;
            }

            addServer(serverName, address);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      } finally {
         if (socket != null) {
            try {
               socket.setSoTimeout(0);
            } catch (Exception ignored) {
            }
         }

         LanServerDiscovery.running = false;
      }
   }

   private void sendDiscoveryRequests(DatagramSocket socket) throws Exception {
      Set<String> broadcastAddresses = new HashSet<String>();

      InetAddress globalBroadcast = this.requestPacket.getAddress();
      broadcastAddresses.add(globalBroadcast.getHostAddress());
      socket.send(this.requestPacket);

      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      if (interfaces == null) {
         return;
      }

      while (interfaces.hasMoreElements()) {
         NetworkInterface networkInterface = interfaces.nextElement();
         if (!networkInterface.isUp() || networkInterface.isLoopback()) {
            continue;
         }

         for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress broadcast = interfaceAddress.getBroadcast();
            if (broadcast == null || !broadcastAddresses.add(broadcast.getHostAddress())) {
               continue;
            }

            DatagramPacket directedBroadcast = new DatagramPacket(
               this.requestPacket.getData(),
               this.requestPacket.getLength(),
               broadcast,
               this.requestPacket.getPort()
            );

            try {
               socket.send(directedBroadcast);
            } catch (Exception exception) {
               if (LanServerDiscovery.debug) {
                  exception.printStackTrace();
               }
            }
         }
      }
   }

   private void addServer(String serverName, String address) {
      synchronized (ClientWindow.serverEntries) {
         for (ServerEntry serverEntry : ClientWindow.serverEntries) {
            if (address.equals(serverEntry.address)) {
               return;
            }
         }

         ClientWindow.serverEntries.add(new ServerEntry(serverName, address));
      }

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            ClientWindow.refreshLanServerMenu();
         }
      });
   }
}
