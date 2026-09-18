package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public final class LanServerDiscovery {
   private static final String discoveryRequest = "rs2006sp req";
   public static final String discoveryResponsePrefix = "rs2006sp resp";
   public static final String serverAddressPrefix = "this is a reserved text for server name";
   public static boolean debug = false;
   private static DatagramSocket socket;
   public static volatile boolean running = false;

   // Restored accessor used by the decompiled listener.
   static synchronized DatagramSocket getSocket() throws java.net.SocketException {
      if (socket == null || socket.isClosed()) {
         socket = new DatagramSocket();
         socket.setBroadcast(true);
      }

      return socket;
   }

   public static synchronized void discoverServers() {
      if (running) {
         return;
      }

      try {
         byte[] bytes = discoveryRequest.getBytes("UTF-8");
         DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("255.255.255.255"), 8002);
         running = true;
         new LanServerDiscoveryListener("Connection Listener", datagramPacket).start();
      } catch (Exception exception) {
         running = false;
         exception.printStackTrace();
      }
   }
}
