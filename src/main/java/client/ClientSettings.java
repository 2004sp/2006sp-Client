package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public final class ClientSettings {
   private String name;
   private int parameterCount;
   private static ClientSettings[] cachedProfiles = new ClientSettings[]{
      new ClientSettings("XP_DROPS", 3),
      new ClientSettings("SMOOTH_RENDER", 1),
      new ClientSettings("SHOW_TITLEBAR", 1),
      new ClientSettings("UI_SCALE_PERCENT", 1),
      new ClientSettings("CAMERA_REFRESH_RATE", 1),
      new ClientSettings("WILDY_LVL_RANGE", 2),
      new ClientSettings("COMBAT_BOX", 1),
      new ClientSettings("SKILL_BOX", 1),
      new ClientSettings("FISH_ICONS", 1),
      new ClientSettings("SHOW_ALCH_VALUE_ON_EXAMINE", 1),
      new ClientSettings("DMG_TYPE", 1),
      new ClientSettings("AUTO_SCREENSHOTS", 1),
      new ClientSettings("GROUND_ITEM_OTHER", 7),
      new ClientSettings("GROUND_ITEM_RARE", 7),
      new ClientSettings("ATTACK_OPTION", 1),
      new ClientSettings("SMOOTH_ANIMATIONS", 1),
      new ClientSettings("USERNAME", 1),
      new ClientSettings("PASSWORD", 1),
      new ClientSettings("FOG", 1),
      new ClientSettings("LOGO", 1),
      new ClientSettings("AUTO_LOGIN", 1),
      new ClientSettings("CUSTOM_SOUNDFONT", 2),
      new ClientSettings("KEYBINDS", 13),
      new ClientSettings("HD_CHATBOX", 1),
      new ClientSettings("HD_MINIMAP", 1),
      new ClientSettings("HD_HPBAR", 1),
      new ClientSettings("HD_MODELS", 1),
      new ClientSettings("2007_MODELS", 1),
      new ClientSettings("HIDE_ROOF", 1)
   };

   private ClientSettings(String text, int newParameterCount) {
      this.name = text;
      this.parameterCount = newParameterCount;
   }
   public static void applySetting(String text, String[] newText) {
      int xpDropPositionOrParseInt = 0;
      ClientSettings[] sourceCachedProfiles = cachedProfiles;
      int cachedProfilesLengthOrLength = cachedProfiles.length;

      for (int parsedNumber = 0; parsedNumber < cachedProfilesLengthOrLength; parsedNumber++) {
         ClientSettings clientSettings;
         if ((clientSettings = sourceCachedProfiles[parsedNumber]).name.equals(text)) {
            if (newText.length == clientSettings.parameterCount) {
               String sourceText = text;
               if (text.equals("XP_DROPS")) {
                  if ((xpDropPositionOrParseInt = Integer.parseInt(newText[0])) < 0) {
                     xpDropPositionOrParseInt = 0;
                  }

                  if (xpDropPositionOrParseInt > 2) {
                     xpDropPositionOrParseInt = 2;
                  }

                  Client.xpDropPosition = xpDropPositionOrParseInt;
                  int xpDropSizeOrParseInt;
                  if ((xpDropSizeOrParseInt = Integer.parseInt(newText[1])) < 0) {
                     xpDropSizeOrParseInt = 0;
                  }

                  if (xpDropSizeOrParseInt > 2) {
                     xpDropSizeOrParseInt = 2;
                  }

                  Client.xpDropSize = xpDropSizeOrParseInt;
                  xpDropPositionOrParseInt = 16748608;
                  String[] text2;
                  if ((text2 = newText[2].split(",")).length != 3) {
                     System.out.println("Invalid color for xp drops!");
                  } else {
                     int localParseInt;
                     if ((localParseInt = Integer.parseInt(text2[0])) < 0) {
                        localParseInt = 0;
                     }

                     if (localParseInt > 255) {
                        localParseInt = 255;
                     }

                     if ((xpDropPositionOrParseInt = Integer.parseInt(text2[1])) < 0) {
                        xpDropPositionOrParseInt = 0;
                     }

                     if (xpDropPositionOrParseInt > 255) {
                        xpDropPositionOrParseInt = 255;
                     }

                     if ((parsedNumber = Integer.parseInt(text2[2])) < 0) {
                        parsedNumber = 0;
                     }

                     if (parsedNumber > 255) {
                        parsedNumber = 255;
                     }

                     xpDropPositionOrParseInt = Client.packRgb(localParseInt, xpDropPositionOrParseInt, parsedNumber);
                  }

                  Client.xpDropColor = xpDropPositionOrParseInt;
               } else if (sourceText.equals("AUTO_SCREENSHOTS")) {
                  Client.autoScreenshots = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("SHOW_TITLEBAR")) {
                  Client.showTitlebar = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("UI_SCALE_PERCENT")) {
                  Client.uiScalePercent = Client.clampUiScalePercent(Integer.parseInt(newText[0]));
               } else if (sourceText.equals("CAMERA_REFRESH_RATE")) {
                  Client.cameraRefreshRate = Client.clampCameraRefreshRate(Integer.parseInt(newText[0]));
               } else if (sourceText.equals("AUTO_LOGIN")) {
                  Client.autoLogin = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("LOGO")) {
                  Client.logoStyle = Integer.parseInt(newText[0]);
               } else if (sourceText.equals("COMBAT_BOX")) {
                  Client.combatBoxEnabled = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("HIDE_ROOF")) {
                  Client.hideRoofs = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("HD_CHATBOX")) {
                  Client.hdChatbox = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("HD_MINIMAP")) {
                  Client.hdMinimap = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("HD_HPBAR")) {
                  Client.hdHealthBar = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("HD_MODELS")) {
                  Client.hdModels = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("2007_MODELS")) {
                  Client.use2007Models = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("WILDY_LVL_RANGE")) {
                  Client.wildernessLevelRangeEnabled = Integer.parseInt(newText[0]) == 1;
                  xpDropPositionOrParseInt = 16777215;
                  String[] text3;
                  if ((text3 = newText[1].split(",")).length != 3) {
                     System.out.println("Invalid color for wildy lvl range!");
                  } else {
                     if ((xpDropPositionOrParseInt = Integer.parseInt(text3[0])) < 0) {
                        xpDropPositionOrParseInt = 0;
                     }

                     if (xpDropPositionOrParseInt > 255) {
                        xpDropPositionOrParseInt = 255;
                     }

                     if ((parsedNumber = Integer.parseInt(text3[1])) < 0) {
                        parsedNumber = 0;
                     }

                     if (parsedNumber > 255) {
                        parsedNumber = 255;
                     }

                     int parseInt2;
                     if ((parseInt2 = Integer.parseInt(text3[2])) < 0) {
                        parseInt2 = 0;
                     }

                     if (parseInt2 > 255) {
                        parseInt2 = 255;
                     }

                     xpDropPositionOrParseInt = Client.packRgb(xpDropPositionOrParseInt, parsedNumber, parseInt2);
                  }

                  Client.wildernessLevelRangeColor = xpDropPositionOrParseInt;
               } else if (sourceText.equals("SHOW_ALCH_VALUE_ON_EXAMINE")) {
                  Client.showAlchValueOnExamine = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("SKILL_BOX")) {
                  Client.skillBoxEnabled = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("FISH_ICONS")) {
                  Client.fishIconsEnabled = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("DMG_TYPE")) {
                  Client.showDamageType = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("SMOOTH_RENDER")) {
                  if ((xpDropPositionOrParseInt = Integer.parseInt(newText[0])) == 0) {
                     Client.smoothRendering = false;
                  } else {
                     Client.smoothRendering = true;
                     Rasterizer3D.smoothShading = false;
                     if (xpDropPositionOrParseInt == 2) {
                        Rasterizer3D.smoothShading = true;
                     }
                  }
               } else if (sourceText.equals("FOG")) {
                  Client.fogEnabled = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("ATTACK_OPTION")) {
                  Client.attackOption = Integer.parseInt(newText[0]);
               } else if (sourceText.equals("SMOOTH_ANIMATIONS")) {
                  Client.smoothAnimations = Integer.parseInt(newText[0]) == 1;
               } else if (sourceText.equals("CUSTOM_SOUNDFONT")) {
                  Client.customSoundfontEnabled = Integer.parseInt(newText[0]) == 1;
                  Client.customSoundfontName = newText[1];
               } else if (sourceText.equals("KEYBINDS")) {
                  for (int keybindIndex = 0; keybindIndex < newText.length; keybindIndex++) {
                     Client.keybinds[keybindIndex] = Integer.parseInt(newText[keybindIndex]);
                  }
               } else if (sourceText.equals("USERNAME")) {
                  Client.username = newText[0];
               } else if (sourceText.equals("PASSWORD")) {
                  Client.password = newText[0];
               } else if (sourceText.equals("GROUND_ITEM_OTHER")) {
                  Client.groundItemOtherNames.clear();
                  Client.groundItemOtherNamesEnabled = Integer.parseInt(newText[0]) == 1;
                  Client.groundItemOtherMenuColorEnabled = Integer.parseInt(newText[1]) == 1;
                  if ((xpDropPositionOrParseInt = Integer.parseInt(newText[2])) < 0) {
                     xpDropPositionOrParseInt = 0;
                  }

                  if (xpDropPositionOrParseInt > 2) {
                     xpDropPositionOrParseInt = 2;
                  }

                  Client.groundItemOtherTextSize = xpDropPositionOrParseInt;
                  int groundItemOtherTextColorOrPackRgb = 16777215;
                  String[] text4;
                  if ((text4 = newText[3].split(",")).length != 3) {
                     System.out.println("Invalid color for 3d game area!");
                  } else {
                     if ((parsedNumber = Integer.parseInt(text4[0])) < 0) {
                        parsedNumber = 0;
                     }

                     if (parsedNumber > 255) {
                        parsedNumber = 255;
                     }

                     int parseInt3;
                     if ((parseInt3 = Integer.parseInt(text4[1])) < 0) {
                        parseInt3 = 0;
                     }

                     if (parseInt3 > 255) {
                        parseInt3 = 255;
                     }

                     if ((xpDropPositionOrParseInt = Integer.parseInt(text4[2])) < 0) {
                        xpDropPositionOrParseInt = 0;
                     }

                     if (xpDropPositionOrParseInt > 255) {
                        xpDropPositionOrParseInt = 255;
                     }

                     groundItemOtherTextColorOrPackRgb = Client.packRgb(parsedNumber, parseInt3, xpDropPositionOrParseInt);
                  }

                  Client.groundItemOtherTextColor = groundItemOtherTextColorOrPackRgb;
                  parsedNumber = 16711935;
                  String[] text5;
                  if ((text5 = newText[4].split(",")).length != 3) {
                     System.out.println("Invalid color for right click menus!");
                  } else {
                     if ((xpDropPositionOrParseInt = Integer.parseInt(text5[0])) < 0) {
                        xpDropPositionOrParseInt = 0;
                     }

                     if (xpDropPositionOrParseInt > 255) {
                        xpDropPositionOrParseInt = 255;
                     }

                     if ((parsedNumber = Integer.parseInt(text5[1])) < 0) {
                        parsedNumber = 0;
                     }

                     if (parsedNumber > 255) {
                        parsedNumber = 255;
                     }

                     int parseInt4;
                     if ((parseInt4 = Integer.parseInt(text5[2])) < 0) {
                        parseInt4 = 0;
                     }

                     if (parseInt4 > 255) {
                        parseInt4 = 255;
                     }

                     parsedNumber = Client.packRgb(xpDropPositionOrParseInt, parsedNumber, parseInt4);
                  }

                  Client.groundItemOtherMenuColor = parsedNumber;
                  Client.groundItemOtherLootBeam = Integer.parseInt(newText[5]) == 1;
                  String[] text6;
                  int loopIndex = (text6 = newText[6].split(",")).length;

                  for (int loopIndex2 = 0; loopIndex2 < loopIndex; loopIndex2++) {
                     String text7;
                     if ((text7 = text6[loopIndex2]) != null && !text7.equals(" ") && !text7.equals("")) {
                        Client.groundItemOtherNames.add(text7);
                     }
                  }
               } else if (sourceText.equals("GROUND_ITEM_RARE")) {
                  Client.groundItemRareNames.clear();
                  Client.groundItemRareNamesEnabled = Integer.parseInt(newText[0]) == 1;
                  Client.groundItemRareMenuColorEnabled = Integer.parseInt(newText[1]) == 1;
                  if ((xpDropPositionOrParseInt = Integer.parseInt(newText[2])) < 0) {
                     xpDropPositionOrParseInt = 0;
                  }

                  if (xpDropPositionOrParseInt > 2) {
                     xpDropPositionOrParseInt = 2;
                  }

                  Client.groundItemRareTextSize = xpDropPositionOrParseInt;
                  int groundItemRareTextColorOrPackRgb = 16777215;
                  String[] text8;
                  if ((text8 = newText[3].split(",")).length != 3) {
                     System.out.println("Invalid color for 3d game area!");
                  } else {
                     if ((parsedNumber = Integer.parseInt(text8[0])) < 0) {
                        parsedNumber = 0;
                     }

                     if (parsedNumber > 255) {
                        parsedNumber = 255;
                     }

                     int parseInt5;
                     if ((parseInt5 = Integer.parseInt(text8[1])) < 0) {
                        parseInt5 = 0;
                     }

                     if (parseInt5 > 255) {
                        parseInt5 = 255;
                     }

                     if ((xpDropPositionOrParseInt = Integer.parseInt(text8[2])) < 0) {
                        xpDropPositionOrParseInt = 0;
                     }

                     if (xpDropPositionOrParseInt > 255) {
                        xpDropPositionOrParseInt = 255;
                     }

                     groundItemRareTextColorOrPackRgb = Client.packRgb(parsedNumber, parseInt5, xpDropPositionOrParseInt);
                  }

                  Client.groundItemRareTextColor = groundItemRareTextColorOrPackRgb;
                  parsedNumber = 16711935;
                  String[] text9;
                  if ((text9 = newText[4].split(",")).length != 3) {
                     System.out.println("Invalid color for right click menus!");
                  } else {
                     if ((xpDropPositionOrParseInt = Integer.parseInt(text9[0])) < 0) {
                        xpDropPositionOrParseInt = 0;
                     }

                     if (xpDropPositionOrParseInt > 255) {
                        xpDropPositionOrParseInt = 255;
                     }

                     if ((parsedNumber = Integer.parseInt(text9[1])) < 0) {
                        parsedNumber = 0;
                     }

                     if (parsedNumber > 255) {
                        parsedNumber = 255;
                     }

                     int parseInt6;
                     if ((parseInt6 = Integer.parseInt(text9[2])) < 0) {
                        parseInt6 = 0;
                     }

                     if (parseInt6 > 255) {
                        parseInt6 = 255;
                     }

                     parsedNumber = Client.packRgb(xpDropPositionOrParseInt, parsedNumber, parseInt6);
                  }

                  Client.groundItemRareMenuColor = parsedNumber;
                  Client.groundItemRareLootBeam = Integer.parseInt(newText[5]) == 1;
                  String[] text10;
                  int loopIndex3 = (text10 = newText[6].split(",")).length;

                  for (int loopIndex4 = 0; loopIndex4 < loopIndex3; loopIndex4++) {
                     String text11;
                     if ((text11 = text10[loopIndex4]) != null && !text11.equals(" ") && !text11.equals("")) {
                        Client.groundItemRareNames.add(text11);
                     }
                  }
               }

               xpDropPositionOrParseInt = 1;
            } else {
               System.out.println("Invalid params for config: " + text);
               xpDropPositionOrParseInt = 1;
            }
            break;
         }
      }

      if (xpDropPositionOrParseInt == 0) {
         System.out.println("Invalid config: " + text);
      }
   }
   private static void writeLine(BufferedWriter bufferedWriter, String text) throws IOException {
      bufferedWriter.write(text);
      bufferedWriter.newLine();
   }
   public static void ensureCameraRefreshRateSetting(File configFile) {
      if (configFile == null || !configFile.exists()) {
         return;
      }

      try {
         BufferedReader reader = new BufferedReader(new java.io.FileReader(configFile));
         try {
            String line;
            while ((line = reader.readLine()) != null) {
               if (line.trim().startsWith("[CAMERA_REFRESH_RATE]")) {
                  return;
               }
            }
         } finally {
            reader.close();
         }

         BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
         try {
            writer.newLine();
            writeLine(writer, "//CAMERA_REFRESH_RATE - Parameters for customization:");
            writeLine(writer, "//50-240 = camera/render refresh rate in frames per second.");
            writeLine(writer, "//Game simulation remains at 50 Hz; this only makes camera motion/redrawing smoother.");
            writeLine(writer, "");
            writeLine(writer, "[CAMERA_REFRESH_RATE];120");
            writer.flush();
         } finally {
            writer.close();
         }
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }

   public static void createDefaultConfig() {
      new File("./userConfig.cfg").delete();
      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./userConfig.cfg", true))) {
            writeLine(bufferedWriter, "//GROUND_ITEM_OTHER - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//ground item name drawing for 3d game area");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable //ground item name coloring for right click menus");
            writeLine(bufferedWriter, "//0-2 //ground item txt size for 3d game area");
            writeLine(bufferedWriter, "//rgbcolor (0-255,0-255,0-255) //ground item txt color for 3d game area");
            writeLine(bufferedWriter, "//rgbcolor (0-255,0-255,0-255) //ground item txt color for right click menus");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable //loot beam");
            writeLine(bufferedWriter, "//item names //list of items to use the following settings for");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[GROUND_ITEM_OTHER];1;0;0;255,255,255;255,0,255;0;coins");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//GROUND_ITEM_RARE - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//ground item name drawing for 3d game area");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable //ground item name coloring for right click menus");
            writeLine(bufferedWriter, "//0-2 //ground item txt size for 3d game area");
            writeLine(bufferedWriter, "//rgbcolor (0-255,0-255,0-255) //ground item txt color for 3d game area");
            writeLine(bufferedWriter, "//rgbcolor (0-255,0-255,0-255) //ground item txt color for right click menus");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable //loot beam");
            writeLine(bufferedWriter, "//item names //list of items to use the following settings for");
            writeLine(bufferedWriter, "");
            writeLine(
               bufferedWriter,
               "[GROUND_ITEM_RARE];0;1;0;255,255,255;255,0,255;1;clue scroll,abyssal whip,dragon chainbody,dragon platelegs,half of a key,membership scroll"
            );
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//XP_DROPS - Parameters for customization:");
            writeLine(bufferedWriter, "//0 = disabled, 1 = top right, 2 = overhead\t//xp drop position");
            writeLine(bufferedWriter, "//0-2 //size of the xp drops");
            writeLine(bufferedWriter, "//rgbcolor (0-255,0-255,0-255) //color of the xp drops");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[XP_DROPS];1;0;255,144,64");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//AUTO_SCREENSHOTS - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//automatically take screenshots from: lvlups, quest completions, clue rewards, duel victorys, death");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[AUTO_SCREENSHOTS];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//SHOW_TITLEBAR - Parameters for customization: [CHANGE ONLY WHEN CLIENT IS NOT RUNNING!]");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//shows/hides titlebar, recommended to hide for fullscreen");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[SHOW_TITLEBAR];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//UI_SCALE_PERCENT - Parameters for customization:");
            writeLine(bufferedWriter, "//50-200 = UI scale percentage, 100 = original size");
            writeLine(bufferedWriter, "//Applies to chatbox, sidebar/tabs and minimap in resizable/fullscreen mode.");
            writeLine(bufferedWriter, "//Large values may overlap on very small windows.");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[UI_SCALE_PERCENT];100");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//CAMERA_REFRESH_RATE - Parameters for customization:");
            writeLine(bufferedWriter, "//50-240 = camera/render refresh rate in frames per second.");
            writeLine(bufferedWriter, "//Game simulation remains at 50 Hz; this only makes camera motion/redrawing smoother.");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[CAMERA_REFRESH_RATE];120");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//FISH_ICONS - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//shows fish icons over fishing spots");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[FISH_ICONS];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//WILDY_LVL_RANGE - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//shows the combat lvl range in wildy below danger lvl");
            writeLine(bufferedWriter, "//rgbcolor (0-255,0-255,0-255) //color for the text");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[WILDY_LVL_RANGE];1;255,255,255");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//COMBAT_BOX - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//show hitpoint overlay in corner in combat");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[COMBAT_BOX];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//SHOW_ALCH_VALUE_ON_EXAMINE - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//show alch values on item examine");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[SHOW_ALCH_VALUE_ON_EXAMINE];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//SKILL_BOX - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//show affected skills overlay in corner");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[SKILL_BOX];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//DMG_TYPE - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//show damage type infront of hit");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[DMG_TYPE];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//SMOOTH_RENDER - Parameters for customization:");
            writeLine(bufferedWriter, "//0 = disable, 1 = enable, 2 = adds smoother shadows on ground/water etc.\t//smoother rendering");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[SMOOTH_RENDER];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//FOG - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//adds fog");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[FOG];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//ATTACK_OPTION - Parameters for customization:");
            writeLine(bufferedWriter, "//0 = default");
            writeLine(bufferedWriter, "//1 = always left click");
            writeLine(bufferedWriter, "//2 = always right click");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[ATTACK_OPTION];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//SMOOTH_ANIMATIONS - Parameters for customization: [CHANGE ONLY WHEN CLIENT IS NOT RUNNING!]");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//Smoother animations");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[SMOOTH_ANIMATIONS];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//USERNAME - Parameters for customization:");
            writeLine(bufferedWriter, "//just write ur username as parameter\t//remember username");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//[USERNAME];namehere + also remove the // infront of the line");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//PASSWORD - Parameters for customization:");
            writeLine(bufferedWriter, "//just write ur password as parameter\t//remember password");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//[PASSWORD];passwordhere + also remove the // infront of the line");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//AUTO_LOGIN - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//logs in automatically, if username and password are set above");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[AUTO_LOGIN];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//LOGO - Parameters for customization:");
            writeLine(bufferedWriter, "//0 = default rs logo, 1 = custom singleplayer logo");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[LOGO];1");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//CUSTOM_SOUNDFONT - Parameters for customization: [CHANGE ONLY WHEN CLIENT IS NOT RUNNING!]");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//enable disable loading of custom soundfont");
            writeLine(bufferedWriter, "//name\t//name of the custom soundfont file");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[CUSTOM_SOUNDFONT];0;8bitsf.sf2");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//KEYBINDS - Parameters for customization:");
            writeLine(bufferedWriter, "//KeyCodes in following order:");
            writeLine(bufferedWriter, "//1 - Combat tab, 2 - Skill tab, 3 - Quest tab, 4 - Inventory tab");
            writeLine(bufferedWriter, "//5 - Equipment tab, 6 - Prayer tab, 7 - Magic tab, 8 - Friend tab");
            writeLine(bufferedWriter, "//9 - Ignore tab, 10 - Logout tab, 11 - Settings tab, 12 - Emote tab");
            writeLine(bufferedWriter, "//13 - Music tab");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[KEYBINDS];112;113;114;27;115;116;117;119;120;-1;121;122;123");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//HD_CHATBOX - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//'hd' chatbox for resizable and fullscreen mode");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[HD_CHATBOX];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//HD_MINIMAP - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//'hd' minimap");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[HD_MINIMAP];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//HD_HPBAR - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//'hd' hpbar");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[HD_HPBAR];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//HD_MODELS (Beta) - Parameters for customization: [CHANGE ONLY WHEN CLIENT IS NOT RUNNING!]");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//'hd' models, hdmod folder with files required in cache");
            writeLine(bufferedWriter, "//NOTE: enabling this can currently cause crashing and other issues!");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[HD_MODELS];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//2007_MODELS (Beta) - Parameters for customization: [CHANGE ONLY WHEN CLIENT IS NOT RUNNING!]");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable");
            writeLine(bufferedWriter, "//NOTE: enabling this can currently cause issues, though might work better for 2007 content than default mode");
            writeLine(bufferedWriter, "//NOTE2: enabling HD_MODELS will over-ride this setting");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[2007_MODELS];0");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "//HIDE_ROOF - Parameters for customization:");
            writeLine(bufferedWriter, "//1 = enable, 0 = disable\t//hides roof");
            writeLine(bufferedWriter, "");
            writeLine(bufferedWriter, "[HIDE_ROOF];0");
            bufferedWriter.flush();
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }
}
