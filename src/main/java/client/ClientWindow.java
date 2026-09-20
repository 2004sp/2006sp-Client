package client;

import worldmap.WorldMapViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
public final class ClientWindow extends Client implements ActionListener {
   public static JMenuBar menuBar;
   public JFrame frame;
   public static JMenu gameframeMenu;
   private static JMenu hiscoresMenu;
   private static JMenu lanServerMenu;
   public static JMenu fullscreenMenu;
   private static JPanel clientPanel;
   public static JButton windowedModeButton;
   public static ArrayList<ServerEntry> serverEntries = new ArrayList<ServerEntry>();
   private FullscreenManager fullscreenManager;
   public static boolean showTitleBar = true;
   private static ClientWindow instance;

   static {
      new ArrayList();
   }

   public ClientWindow() {
      try {
         SignLink.startpriv(InetAddress.getByName(Client.serverAddress));

         try {
            SpriteArchive.loadSprites();
            SpriteArchive.loadInterfaceSprites();
            Client.customSprites = SpriteArchive.sprites;
            Client.interfaceSprites = SpriteArchive.interfaceSprites;
         } catch (Exception exception) {
            System.out.println("Failed to load custom sprites.");
         }

         instance = this;
         ClientWindow clientWindow = this;

         try {
            boolean showTitlebar = Client.showTitlebar;
            showTitleBar = Client.showTitlebar;
            JFrame.setDefaultLookAndFeelDecorated(showTitlebar);
            UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            clientWindow.frame = new JFrame();
            clientWindow.frame.setLayout(new BorderLayout());
            clientWindow.setFocusTraversalKeysEnabled(false);
            clientWindow.frame.getContentPane().setBackground(Color.BLACK);
            resetServerEntries();
            clientWindow.frame.setDefaultCloseOperation(0);
            clientWindow.frame.addWindowListener(new ClientWindowListener(clientWindow));
            (clientPanel = new JPanel()).setLayout(new BorderLayout());
            clientPanel.add(clientWindow);
            clientPanel.setPreferredSize(new Dimension(765, 503));
            JButton jButton;
            (jButton = new JButton("World Map")).addActionListener(clientWindow);
            JButton jButton5;
            (jButton5 = new JButton("Screenshot")).addActionListener(clientWindow);
            JButton jButton2;
            (jButton2 = new JButton("Reload Userconfig")).addActionListener(clientWindow);
            JButton jButton3;
            (jButton3 = new JButton("Close")).addActionListener(clientWindow);
            (windowedModeButton = new JButton("Windowed mode")).addActionListener(clientWindow);
            JButton jButton4;
            (jButton4 = new JButton("Scan LAN Servers")).addActionListener(clientWindow);
            fullscreenMenu = new JMenu("Fullscreen");
            menuBar = new JMenuBar();
            (lanServerMenu = new JMenu("LAN Server List")).setEnabled(false);
            gameframeMenu = new JMenu("Gameframe");
            JMenuItem jMenuItem = new JMenuItem("317 Gameframe");
            JMenuItem jMenuItem7 = new JMenuItem("459 Gameframe");
            JMenuItem jMenuItem2 = new JMenuItem("474 Gameframe");
            JMenuItem jMenuItem3 = new JMenuItem("474 & Orbs Gameframe");
            JMenuItem jMenuItem4 = new JMenuItem("474 Resizable Gameframe");
            JMenuItem jMenuItem5 = new JMenuItem("474 & Orbs Resizable Gameframe");
            JMenuItem jMenuItem6 = new JMenuItem("OSRS Resizable Gameframe");
            jMenuItem.addActionListener(clientWindow);
            jMenuItem7.addActionListener(clientWindow);
            jMenuItem2.addActionListener(clientWindow);
            jMenuItem3.addActionListener(clientWindow);
            jMenuItem4.addActionListener(clientWindow);
            jMenuItem5.addActionListener(clientWindow);
            jMenuItem6.addActionListener(clientWindow);
            gameframeMenu.add(jMenuItem);
            gameframeMenu.add(jMenuItem7);
            gameframeMenu.add(jMenuItem2);
            gameframeMenu.add(jMenuItem3);
            gameframeMenu.add(jMenuItem4);
            gameframeMenu.add(jMenuItem5);
            gameframeMenu.add(jMenuItem6);
            hiscoresMenu = new JMenu("Hiscores");
            jMenuItem = new JMenuItem("Hiscores (normal)");
            jMenuItem7 = new JMenuItem("Hiscores (ironman)");
            jMenuItem2 = new JMenuItem("Hiscores (ultimate ironman)");
            jMenuItem3 = new JMenuItem("Hiscores (hardcore ironman)");
            jMenuItem.addActionListener(clientWindow);
            jMenuItem7.addActionListener(clientWindow);
            jMenuItem2.addActionListener(clientWindow);
            jMenuItem3.addActionListener(clientWindow);
            hiscoresMenu.add(jMenuItem);
            hiscoresMenu.add(jMenuItem7);
            hiscoresMenu.add(jMenuItem2);
            hiscoresMenu.add(jMenuItem3);
            clientWindow.addComponentListener(new ClientResizeListener(clientWindow));
            menuBar.add(jButton);
            menuBar.add(jButton5);
            menuBar.add(jButton2);
            menuBar.add(hiscoresMenu);
            menuBar.add(jButton4);
            menuBar.add(lanServerMenu);
            menuBar.add(gameframeMenu);
            if (!showTitlebar) {
               menuBar.add(jButton3);
            }

            clientWindow.frame.getContentPane().add(menuBar, "North");
            clientWindow.frame.getContentPane().add(clientPanel, "Center");
            if (!showTitlebar) {
               clientWindow.frame.setUndecorated(!showTitlebar);
            }

            clientWindow.frame.pack();
            clientWindow.frame.setLocationRelativeTo(null);
            clientWindow.frame.setVisible(true);
            clientWindow.frame.setResizable(false);
            clientWindow.init();
            clientWindow.frame.setTitle("Progressive 2006 singleplayer [v1.0]");
            clientWindow.initializeFullscreenMenu();
         } catch (Exception exception3) {
            exception3.printStackTrace();
            return;
         }
      } catch (Exception exception2) {
         exception2.printStackTrace();
      }
   }
   private void initializeFullscreenMenu() {
      this.fullscreenManager = new FullscreenManager();
      DisplayMode[] displayModes = this.fullscreenManager.getDisplayModes();

      for (int displayModeIndex = 0; displayModeIndex < displayModes.length; displayModeIndex++) {
         DisplayMode displayMode = displayModes[displayModeIndex];
         String text = displayMode.getWidth() + "x" + displayMode.getHeight() + " " + displayMode.getRefreshRate() + "Hz";
         if (displayMode.getWidth() > Client.minimumWindowWidth && displayMode.getHeight() > Client.minimumWindowHeight && (displayMode.getRefreshRate() == 60 || displayMode.getRefreshRate() == 30)) {
            if (Client.fullscreenDisplayMode == null) {
               Client.fullscreenDisplayMode = displayMode;
            }

            JMenuItem jMenuItem;
            (jMenuItem = new JMenuItem(text)).addActionListener(new DisplayModeListener(this, displayMode));
            fullscreenMenu.add(jMenuItem);
         }
      }
   }
   private static void resetServerEntries() {
      serverEntries.clear();
      serverEntries.add(new ServerEntry("Localhost", "127.0.0.1"));
   }
   public static void refreshLanServerMenu() {
      lanServerMenu.removeAll();
      if (serverEntries.size() <= 1) {
         lanServerMenu.setEnabled(false);

         try {
            SignLink.startpriv(InetAddress.getByName(((ServerEntry)serverEntries.get(0)).address));
         } catch (UnknownHostException exception) {
            exception.printStackTrace();
         }
      } else {
         for (ServerEntry serverEntry : serverEntries) {
            JMenuItem jMenuItem;
            (jMenuItem = new JMenuItem(serverEntry.name)).addActionListener(new ServerSelectionListener(serverEntry));
            lanServerMenu.add(jMenuItem);
         }

         lanServerMenu.setEnabled(true);
      }
   }
   public final void enterFullscreenMode() {
      try {
         this.fullscreenManager = new FullscreenManager();
         this.fullscreenManager.enterFullscreen(Client.fullscreenDisplayMode, this.frame);
         super.fullscreenActive = true;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
   public final void restoreWindowedMode(int scalarArgument, int scalarArgument2) {
      try {
         if (super.fullscreenActive) {
            if (this.fullscreenManager != null) {
               this.fullscreenManager.exitFullscreen();
            }
            super.fullscreenActive = false;
         }

         // Recreate a normal window peer only after exclusive fullscreen has
         // been released. This restores the normal maximize/minimize behavior.
         this.frame.dispose();
         this.frame.setTitle("Progressive 2006 singleplayer [v1.0]");
         this.frame.setExtendedState(JFrame.NORMAL);
         this.frame.setResizable(true);
         this.frame.setSize(808, 628);
         this.frame.setLocationRelativeTo(null);
         this.frame.setVisible(true);
         this.frame.setResizable(true);
         this.frame.toFront();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
   public static ClientWindow getInstance() {
      return instance;
   }

   @Override
   public final void actionPerformed(ActionEvent actionEvent) {
      String text = actionEvent.getActionCommand();

      try {
         if (text != null) {
            if (text.equalsIgnoreCase("Exit") || text.equalsIgnoreCase("Close")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before closing the client.");
               } else {
                  System.exit(0);
               }
            }

            if (text.equals("317 Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.getClient().setScreenMode(0);
                     Client.gameframeVersion = 317;
                     Client.orbsEnabled = false;
                     Client.applyGameframeVersion();
                  } catch (Exception exception) {
                     exception.printStackTrace();
                  }
               }
            }

            if (text.equals("459 Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.getClient().setScreenMode(0);
                     Client.gameframeVersion = 459;
                     Client.orbsEnabled = false;
                     Client.applyGameframeVersion();
                  } catch (Exception exception11) {
                     exception11.printStackTrace();
                  }
               }
            }

            if (text.equals("Windowed mode")) {
               Client.getClient().setScreenMode(1);
            }

            if (text.equals("474 Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.getClient().setScreenMode(0);
                     Client.gameframeVersion = 474;
                     Client.orbsEnabled = false;
                     Client.applyGameframeVersion();
                  } catch (Exception exception2) {
                     exception2.printStackTrace();
                  }
               }
            }

            if (text.equals("474 & Orbs Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.getClient().setScreenMode(0);
                     Client.gameframeVersion = 474;
                     Client.orbsEnabled = true;
                     Client.applyGameframeVersion();
                  } catch (Exception exception3) {
                     exception3.printStackTrace();
                  }
               }
            }

            if (text.equals("474 Resizable Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.gameframeVersion = 474;
                     Client.applyGameframeVersion();
                     Client.getClient().setScreenMode(1);
                     Client.orbsEnabled = false;
                     Client.osrsResizableFrame = false;
                  } catch (Exception exception4) {
                     exception4.printStackTrace();
                  }
               }
            }

            if (text.equals("474 & Orbs Resizable Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.gameframeVersion = 474;
                     Client.applyGameframeVersion();
                     Client.getClient().setScreenMode(1);
                     Client.orbsEnabled = true;
                     Client.osrsResizableFrame = false;
                  } catch (Exception exception5) {
                     exception5.printStackTrace();
                  }
               }
            }

            if (text.equals("OSRS Resizable Gameframe")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before changing the gameframe.");
               } else {
                  try {
                     Client.gameframeVersion = 474;
                     Client.applyGameframeVersion();
                     Client.getClient().setScreenMode(1);
                     Client.orbsEnabled = true;
                     Client.osrsResizableFrame = true;
                  } catch (Exception exception6) {
                     exception6.printStackTrace();
                  }
               }
            }

            if (text.equals("Scan LAN Servers")) {
               if (Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log out first before scanning for servers.");
               } else {
                  try {
                     resetServerEntries();
                     lanServerMenu.removeAll();
                     lanServerMenu.setEnabled(false);
                     LanServerDiscovery.discoverServers();
                  } catch (Exception exception7) {
                     exception7.printStackTrace();
                  }
               }
            }

            if (text.equalsIgnoreCase("World Map")) {
               WorldMapViewer.showWorldMap();
            }

            captureScreenshot:
            if (text.equalsIgnoreCase("Screenshot")) {
               boolean flag = true;
               ClientWindow clientWindow = this;
               String localText = "./Screenshots/";
               if (Client.username != null && Client.username != "") {
                  localText = localText + Client.username + "/";
               }

               BufferedImage bufferedImage;
               try {
                  Robot robot = new Robot();
                  Point point = clientPanel.getLocationOnScreen();
                  Rectangle rectangle = new Rectangle(point.x, point.y, clientPanel.getWidth(), clientPanel.getHeight());
                  bufferedImage = robot.createScreenCapture(rectangle);
               } catch (Throwable throwable) {
                  JOptionPane.showMessageDialog(clientWindow.frame, "An error occured while trying to create a screenshot!", "Screenshot Error", 0);
                  throwable.printStackTrace();
                  break captureScreenshot;
               }

               Object object = null;

               try {
                  object = getNextScreenshotFilename();
               } catch (Exception exception8) {
                  JOptionPane.showMessageDialog(
                     clientWindow.frame, "A screenshot directory does not exist, and could not be created!", "No Screenshot Directory", 0
                  );
                  break captureScreenshot;
               }

               if (object == null) {
                  JOptionPane.showMessageDialog(
                     clientWindow.frame,
                     "There are too many screenshots in the screenshot directory!\nDelete some screen\nshots and try again.",
                     "Screenshot Directory Full",
                     0
                  );
               } else {
                  try {
                     File file;
                     if (!(file = new File(localText)).exists()) {
                        file.mkdirs();
                     }

                     ImageIO.write(bufferedImage, "png", new File(localText + object));
                     System.out.println("You took a nice screenshot.");
                  } catch (IOException exception9) {
                     JOptionPane.showMessageDialog(
                        clientWindow.frame,
                        "An error occured while trying to save the screenshot!\nPlease make sure you have\n write access to the screenshot directory.",
                        "Screenshot Error",
                        0
                     );
                  }
               }
            }

            if (text.equalsIgnoreCase("Reload Userconfig")) {
               Client.loadUserConfig();
            }

            if (text.equalsIgnoreCase("Hiscores (normal)")) {
               if (!Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log in first to open hiscores.");
               } else {
                  this.outgoingBuffer.writeOpcode(103);
                  String text2 = "hiscore 0";
                  this.outgoingBuffer.writeByte(text2.length() + 1);
                  this.outgoingBuffer.writeString(text2);
               }
            }

            if (text.equalsIgnoreCase("Hiscores (ironman)")) {
               if (!Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log in first to open hiscores.");
               } else {
                  this.outgoingBuffer.writeOpcode(103);
                  String text3 = "hiscore 1";
                  this.outgoingBuffer.writeByte(text3.length() + 1);
                  this.outgoingBuffer.writeString(text3);
               }
            }

            if (text.equalsIgnoreCase("Hiscores (ultimate ironman)")) {
               if (!Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log in first to open hiscores.");
               } else {
                  this.outgoingBuffer.writeOpcode(103);
                  String text4 = "hiscore 2";
                  this.outgoingBuffer.writeByte(text4.length() + 1);
                  this.outgoingBuffer.writeString(text4);
               }
            }

            if (text.equalsIgnoreCase("Hiscores (hardcore ironman)")) {
               if (!Client.loggedIn) {
                  JOptionPane.showMessageDialog(this.frame, "Please log in first to open hiscores.");
                  return;
               }

               this.outgoingBuffer.writeOpcode(103);
               String text5 = "hiscore 3";
               this.outgoingBuffer.writeByte(text5.length() + 1);
               this.outgoingBuffer.writeString(text5);
               return;
            }
         }
      } catch (Exception exception10) {
      }
   }
   public static void saveFramebufferScreenshot() {
      BufferedImage bufferedImage = null;
      String text = "./Screenshots/";
      if (Client.username != null && Client.username != "") {
         text = text + Client.username + "/";
      }

      try {
         new Robot();
         Point point = clientPanel.getLocationOnScreen();
         new Rectangle(point.x, point.y, clientPanel.getWidth(), clientPanel.getHeight());
         bufferedImage = (BufferedImage)Client.getClient().frameBuffer.image;
      } catch (Throwable throwable) {
      }

      String localText = null;

      try {
         localText = getNextScreenshotFilename();
      } catch (Exception exception) {
      }

      try {
         File file;
         if (!(file = new File(text)).exists()) {
            file.mkdirs();
         }

         ImageIO.write(bufferedImage, "png", new File(text + localText));
         System.out.println("You took a nice screenshot.");
      } catch (IOException exception2) {
      }
   }
   private static String getNextScreenshotFilename() {
      String text = "./Screenshots";
      if (Client.username != null && Client.username != "") {
         text = text + "/" + Client.username;
      }

      File file = new File(text);
      int scalar = 0;

      do {
         String localText = "Pic .png";
         if (scalar < 10) {
            localText = localText.replaceFirst(" ", " 000" + scalar);
         } else if (scalar < 100) {
            localText = localText.replaceFirst(" ", " 00" + scalar);
         } else if (scalar < 1000) {
            localText = localText.replaceFirst(" ", " 0" + scalar);
         } else if (scalar < 10000) {
            localText = localText.replaceFirst(" ", " " + scalar);
         }

         if (!new File(file, localText).isFile()) {
            return localText;
         }
      } while (++scalar < 10000);

      return null;
   }
}
