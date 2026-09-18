package client;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
public final class SignLink implements Runnable {
   private Sequencer midiSequencer = null;
   private Sequence midiSequence = null;
   public static int uid;
   public static int storeId;
   public static RandomAccessFile cacheDataFile = null;
   public static final RandomAccessFile[] cacheIndexFiles = new RandomAccessFile[Client.cacheStoreCount];
   public static Applet applet = null;
   private static boolean active;
   private static int threadLiveId;
   private static InetAddress socketIp;
   private static int socketRequestPort;
   private static Socket socket = null;
   private static int threadRequestPriority = 1;
   private static Runnable threadRequest = null;
   private static String dnsRequest = null;
   public static String dns = null;
   private static String unusedString1 = null;
   private static String unusedString2 = null;
   private static byte[] unusedBytes = null;
   public static String unusedPublicString1 = null;
   public static int unusedPublicInt;
   public static boolean unusedPublicFlag = true;
   public static String unusedPublicString2;
   public static final void startpriv(InetAddress inetAddress) {
      threadLiveId = (int)(Math.random() * 9.9999999E7);
      if (active) {
         try {
            Thread.sleep(500L);
         } catch (Exception exception) {
         }

         active = false;
      }

      socketRequestPort = 0;
      threadRequest = null;
      dnsRequest = null;
      unusedString2 = null;
      unusedString1 = null;
      socketIp = inetAddress;
      Thread thread;
      (thread = new Thread(new SignLink())).setDaemon(true);
      thread.start();

      while (!active) {
         try {
            Thread.sleep(50L);
         } catch (Exception exception2) {
         }
      }
   }
   @Override
   public final void run() {
      active = true;
      String text;
      uid = loadUid(text = findcachedir());

      try {
         new File(text + "main_file_cache.dat");
         cacheDataFile = new RandomAccessFile(text + "main_file_cache.dat", "rw");

         for (int cacheIndexFileIndex = 0; cacheIndexFileIndex < Client.cacheStoreCount; cacheIndexFileIndex++) {
            cacheIndexFiles[cacheIndexFileIndex] = new RandomAccessFile(text + "main_file_cache.idx" + cacheIndexFileIndex, "rw");
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      int sourceThreadLiveId = threadLiveId;

      while (threadLiveId == sourceThreadLiveId) {
         if (socketRequestPort != 0) {
            try {
               socket = new Socket(socketIp, socketRequestPort);
            } catch (Exception exception4) {
               socket = null;
            }

            socketRequestPort = 0;
         } else if (threadRequest != null) {
            Thread thread;
            (thread = new Thread(threadRequest)).setDaemon(true);
            thread.start();
            thread.setPriority(threadRequestPriority);
            threadRequest = null;
         } else if (dnsRequest != null) {
            try {
               dns = InetAddress.getByName(dnsRequest).getHostName();
            } catch (Exception exception2) {
               dns = "unknown";
            }

            dnsRequest = null;
         }

         try {
            Thread.sleep(50L);
         } catch (Exception exception3) {
         }
      }
   }
   public static String findcachedir() {
      File file;
      if (!(file = new File("./cache/")).exists() && !file.mkdir()) {
         if (!(file = new File("c:/.Cache/")).exists()) {
            file.mkdir();
         }

         return file.toString();
      } else {
         return "./cache/";
      }
   }
   private static int loadUid(String text) {
      try {
         File file;
         if (!(file = new File(text + "uid.dat")).exists() || file.length() < 4L) {
            DataOutputStream dataOutputStream;
            (dataOutputStream = new DataOutputStream(new FileOutputStream(text + "uid.dat"))).writeInt((int)(Math.random() * 9.9999999E7));
            dataOutputStream.close();
         }
      } catch (Exception exception) {
      }

      try {
         DataInputStream dataInputStream;
         int decodedInt = (dataInputStream = new DataInputStream(new FileInputStream(text + "uid.dat"))).readInt();
         dataInputStream.close();
         return decodedInt + 1;
      } catch (Exception exception2) {
         return 0;
      }
   }
   public static synchronized void dnslookup(String text) {
      dns = text;
      dnsRequest = text;
   }
   public static void reporterror(String text) {
      System.out.println("Error: " + text);
   }
}
