package client;

import java.util.Iterator;
public final class Widget {
   public Sprite disabledSprite;
   public Sprite alternateDisabledSprite;
   public Sprite alternateEnabledSprite;
   public int spriteXOffset = -1;
   public int spriteYOffset = -1;
   public boolean adjustSpriteX = false;
   public boolean adjustSpriteY = false;
   public int animationFrameCycle;
   public int baseWidth;
   public Sprite[] inventorySprites;
   public static Widget[] widgets;
   public int[] scriptCompareValues;
   public int contentType;
   public int[] inventorySpriteX;
   public int defaultHoverColor;
   public int optionType;
   public String spellName;
   public int secondaryColor;
   public int width;
   public String tooltip;
   public String selectedActionName;
   public int textAlignment;
   public int scrollPosition;
   public String[] actions;
   public int[][] valueIndexArray;
   public boolean filled;
   public String secondaryText;
   public int hoverId;
   public int spritePaddingX;
   public int textColor;
   public int defaultMediaType;
   public int defaultMediaId;
   public boolean replaceItems;
   public int parentId;
   public int spellUsableOn;
   private static LruCache spriteCache;
   private static int castleWarsCatapultMarkerWidgetId = -1;
   public int secondaryHoverColor;
   public int[] childIds;
   public int[] childX;
   public boolean usableItems;
   public RichTextFont font;
   public int spritePaddingY;
   public int[] scriptCompareTypes;
   public int animationFrame;
   public int[] inventorySpriteY;
   public String message;
   public boolean inventoryInterface;
   public int id;
   public int[] inventoryAmounts;
   public int[] inventoryIds;
   public byte opacity;
   private int enabledMediaType;
   private int enabledMediaId;
   public int defaultAnimationId;
   public int enabledAnimationId;
   public boolean swapItems;
   public Sprite enabledSprite;
   public int scrollMax;
   public int type;
   public int runtimeXOffset;
   private static final LruCache modelCache = new LruCache(30);
   public int runtimeYOffset;
   public boolean hoverOnly;
   public int height;
   public boolean textShadow;
   public int modelZoom;
   public int modelRotation1;
   public int modelRotation2;
   public int[] childY;
   public boolean newScroller = false;
   public int nextAnimationFrame;
   private static void overlaySpritePixels(int[][] values, Sprite sprite, int positionArgument, int newIndex, boolean flag) {
      int[][] localReshapeIntArray = Client.reshapeIntArray(sprite.pixels, sprite.spriteWidth);

      for (int loopIndex = newIndex; loopIndex < newIndex + sprite.spriteHeight; loopIndex++) {
         for (int loopIndex2 = positionArgument; loopIndex2 < positionArgument + sprite.spriteWidth; loopIndex2++) {
            if (localReshapeIntArray[loopIndex - newIndex][loopIndex2 - positionArgument] != 0) {
               values[loopIndex][loopIndex2] = localReshapeIntArray[loopIndex - newIndex][loopIndex2 - positionArgument];
            }
         }
      }
   }
   private static void tileSpritePixels(int[][] values, Sprite sprite, int positionArgument, int newIndex, int widgetIndex, int interfaceSpriteIndex) {
      int[][] localReshapeIntArray = Client.reshapeIntArray(sprite.pixels, sprite.spriteWidth);

      for (int loopIndex = newIndex; loopIndex < interfaceSpriteIndex; loopIndex++) {
         for (int loopIndex2 = positionArgument; loopIndex2 < widgetIndex; loopIndex2++) {
            values[loopIndex][loopIndex2] = localReshapeIntArray[(loopIndex - newIndex) % sprite.spriteHeight][(loopIndex2 - positionArgument) % sprite.spriteWidth];
         }
      }
   }
   public final void swapInventoryItems(int inventoryIdIndex, int newIndex) {
      int inventoryIdOrInventoryIds = this.inventoryIds[inventoryIdIndex];
      this.inventoryIds[inventoryIdIndex] = this.inventoryIds[newIndex];
      this.inventoryIds[newIndex] = inventoryIdOrInventoryIds;
      inventoryIdOrInventoryIds = this.inventoryAmounts[inventoryIdIndex];
      this.inventoryAmounts[inventoryIdIndex] = this.inventoryAmounts[newIndex];
      this.inventoryAmounts[newIndex] = inventoryIdOrInventoryIds;
   }
   public static void updateSkillLevelActions(int yCameraCurve) {
      int[] values = new int[]{8654, 8655, 8656, 8657, 8658, 8659, 8660, 8661, 8662, 8663, 8664, 8665, 8666, 8667, 8668, 8669, 8670, 8671, 8672, 12162, 13928};

      for (int loopIndex = 0; loopIndex < 21; loopIndex++) {
         int widgetIndex = values[loopIndex];
         Widget widget = getWidget(widgetIndex);
         if (widget == null) {
            continue;
         }
         if (yCameraCurve > 1) {
            widget.actions = new String[5];
            widget.actions[0] = "Set level";
         } else {
            widget.actions = null;
         }
      }
   }
   public static void enableSkillLevelActions(boolean flag) {
      int[] localWidgetId = new int[]{8654, 8655, 8656, 8657, 8658, 8659, 8660, 8661, 8662, 8663, 8664, 8665, 8666, 8667, 8668, 8669, 8670, 8671, 8672, 12162, 13928};

      for (int localWidgetIdIndex = 0; localWidgetIdIndex < 21; localWidgetIdIndex++) {
         int widgetId = localWidgetId[localWidgetIdIndex];
         Widget widget = getWidget(widgetId);
         if (widget == null) {
            continue;
         }
         widget.actions = new String[5];
         widget.actions[0] = "Set level";
      }
   }
   public static void load(Archive archive, RichTextFont[] newFont, Archive newArchive) {
      spriteCache = new LruCache(50000);
      Buffer buffer = new Buffer(archive.getFile("data"));
      int parentIdOrReadUnsignedShort = -1;
      widgets = new Widget[buffer.readUnsignedShort()];

      while (buffer.currentPosition < buffer.buffer.length) {
         int widgetIndex;
         if ((widgetIndex = buffer.readUnsignedShort()) == 65535) {
            parentIdOrReadUnsignedShort = buffer.readUnsignedShort();
            widgetIndex = buffer.readUnsignedShort();
         }

         Widget widget = widgets[widgetIndex] = new Widget();
         if (widgetIndex == 197) {
            widget.spriteXOffset = -820;
            widget.spriteYOffset = -245;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 201) {
            widget.spriteXOffset = -507;
            widget.spriteYOffset = -121;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 4535) {
            widget.spriteXOffset = -535;
            widget.spriteYOffset = -95;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 8680) {
            widget.spriteXOffset = 0;
            widget.spriteYOffset = -500;
            widget.adjustSpriteY = true;
         }

         if (widgetIndex == 15892) {
            widget.spriteXOffset = -775;
            widget.spriteYOffset = 10;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 15917) {
            widget.spriteXOffset = -775;
            widget.spriteYOffset = 10;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 15931) {
            widget.spriteXOffset = -775;
            widget.spriteYOffset = 10;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 15962) {
            widget.spriteXOffset = -775;
            widget.spriteYOffset = 10;
            widget.adjustSpriteX = true;
         }

         if (widgetIndex == 15922 || widgetIndex == 15923 || widgetIndex == 15924 || widgetIndex == 15925) {
            widget.spriteXOffset = -310;
            widget.spriteYOffset = -250;
            widget.adjustSpriteX = true;
            widget.adjustSpriteY = true;
         }

         if (widgetIndex == 15930) {
            widget.spriteXOffset = 0;
            widget.spriteYOffset = -550;
            widget.adjustSpriteY = true;
         }

         if (widgetIndex >= 15936 && widgetIndex <= 15943) {
            widget.spriteXOffset = 0;
            widget.spriteYOffset = -510;
            widget.adjustSpriteY = true;
         }

         if (widgetIndex >= 15967 && widgetIndex <= 15969) {
            widget.spriteXOffset = 0;
            widget.spriteYOffset = -530;
            widget.adjustSpriteY = true;
         }

         if (widgetIndex == 19556) {
            widget.spriteXOffset = -760;
            widget.spriteYOffset = 0;
            widget.adjustSpriteX = true;
         }

         widget.id = widgetIndex;
         widget.parentId = parentIdOrReadUnsignedShort;
         widget.type = buffer.readUnsignedByte();
         widget.optionType = buffer.readUnsignedByte();
         widget.contentType = buffer.readUnsignedShort();
         widgetIndex = buffer.readUnsignedShort();
         widget.width = widgetIndex;
         if (widget.type == 3) {
            widget.baseWidth = widgetIndex;
         }

         widget.height = buffer.readUnsignedShort();
         widget.opacity = (byte)buffer.readUnsignedByte();
         widget.hoverId = buffer.readUnsignedByte();
         if (widget.hoverId != 0) {
            widget.hoverId = (widget.hoverId - 1 << 8) + buffer.readUnsignedByte();
         } else {
            widget.hoverId = -1;
         }

         if ((widgetIndex = buffer.readUnsignedByte()) > 0) {
            widget.scriptCompareTypes = new int[widgetIndex];
            widget.scriptCompareValues = new int[widgetIndex];

            for (int scriptCompareTypeIndex = 0; scriptCompareTypeIndex < widgetIndex; scriptCompareTypeIndex++) {
               widget.scriptCompareTypes[scriptCompareTypeIndex] = buffer.readUnsignedByte();
               widget.scriptCompareValues[scriptCompareTypeIndex] = buffer.readUnsignedShort();
            }
         }

         int interfaceSpriteIndex;
         if ((interfaceSpriteIndex = buffer.readUnsignedByte()) > 0) {
            widget.valueIndexArray = new int[interfaceSpriteIndex][];

            for (int valueIndexArrayIndex = 0; valueIndexArrayIndex < interfaceSpriteIndex; valueIndexArrayIndex++) {
               int readUnsignedShortOrLength = buffer.readUnsignedShort();
               widget.valueIndexArray[valueIndexArrayIndex] = new int[readUnsignedShortOrLength];

               for (int loopIndex = 0; loopIndex < readUnsignedShortOrLength; loopIndex++) {
                  widget.valueIndexArray[valueIndexArrayIndex][loopIndex] = buffer.readUnsignedShort();
               }
            }
         }

         if (widget.type == 0) {
            widget.scrollMax = buffer.readUnsignedShort();
            widget.hoverOnly = buffer.readUnsignedByte() == 1;
            widgetIndex = buffer.readUnsignedShort();
            widget.childIds = new int[widgetIndex];
            widget.childX = new int[widgetIndex];
            widget.childY = new int[widgetIndex];

            for (int childIdIndex = 0; childIdIndex < widgetIndex; childIdIndex++) {
               widget.childIds[childIdIndex] = buffer.readUnsignedShort();
               widget.childX[childIdIndex] = buffer.readShort();
               widget.childY[childIdIndex] = buffer.readShort();
               configureCastleWarsWaitingInterface(newFont);

               if (Client.getClient().graphicsEnabled) {
                  boolean flag = false;
                  if (widget.parentId == 638) {
                     Iterator iterator = QuestEntry.entries.iterator();

                     while (iterator.hasNext()) {
                        QuestEntry questEntry;
                        if ((questEntry = (QuestEntry)iterator.next()).getId() == widget.childIds[childIdIndex]) {
                           questEntry.setY(widget.childY[childIdIndex]);
                           flag = true;
                           break;
                        }
                     }

                     if (!flag) {
                        new QuestEntry(widget.childIds[childIdIndex], widget.childY[childIdIndex]);
                     }
                  }
               }
            }
         }

         if (widget.type == 1) {
            buffer.readUnsignedShort();
            buffer.readUnsignedByte();
         }

         if (widget.type == 2) {
            widget.inventoryIds = new int[widget.width * widget.height];
            widget.inventoryAmounts = new int[widget.width * widget.height];
            widget.swapItems = buffer.readUnsignedByte() == 1;
            widget.inventoryInterface = buffer.readUnsignedByte() == 1;
            widget.usableItems = buffer.readUnsignedByte() == 1;
            widget.replaceItems = buffer.readUnsignedByte() == 1;
            widget.spritePaddingX = buffer.readUnsignedByte();
            widget.spritePaddingY = buffer.readUnsignedByte();
            widget.inventorySpriteX = new int[20];
            widget.inventorySpriteY = new int[20];
            widget.inventorySprites = new Sprite[20];

            for (int inventorySpriteXIndex = 0; inventorySpriteXIndex < 20; inventorySpriteXIndex++) {
               if (buffer.readUnsignedByte() == 1) {
                  widget.inventorySpriteX[inventorySpriteXIndex] = buffer.readShort();
                  widget.inventorySpriteY[inventorySpriteXIndex] = buffer.readShort();
                  String text = buffer.readString();
                  if (newArchive != null && text.length() > 0) {
                     interfaceSpriteIndex = text.lastIndexOf(",");
                     if (text.substring(0, interfaceSpriteIndex).toLowerCase().equals("cache")) {
                        int parsedNumber = Integer.parseInt(text.substring(interfaceSpriteIndex + 1));
                        widget.inventorySprites[inventorySpriteXIndex] = Client.interfaceSprites[parsedNumber];
                     } else {
                        widget.inventorySprites[inventorySpriteXIndex] = loadSprite(Integer.parseInt(text.substring(interfaceSpriteIndex + 1)), newArchive, text.substring(0, interfaceSpriteIndex));
                     }
                  }
               }
            }

            widget.actions = new String[5];

            for (int actionIndex = 0; actionIndex < 5; actionIndex++) {
               widget.actions[actionIndex] = buffer.readString();
               if (widget.actions[actionIndex].length() == 0) {
                  widget.actions[actionIndex] = null;
               }

               if (widget.parentId == 3824) {
                  widget.actions[4] = "Buy 100";
               }

               if (widget.parentId == 3822) {
                  widget.actions[4] = "Sell 100";
               }

               if (widget.parentId == 1644) {
                  widget.actions[2] = "Operate";
               }
            }
         }

         if (widget.type == 3) {
            widget.filled = buffer.readUnsignedByte() == 1;
         }

         if (widget.type == 4 || widget.type == 1) {
            widget.textAlignment = buffer.readUnsignedByte();
            widgetIndex = buffer.readUnsignedByte();
            widget.font = newFont[widgetIndex];
            widget.textShadow = buffer.readUnsignedByte() == 1;
         }

         if (widget.type == 4) {
            widget.message = buffer.readString()
               .replaceAll("RuneScape", "RuneScape")
               .replaceAll("Runescape", "RuneScape")
               .replaceAll("runescape", "RuneScape")
               .replaceAll("Jagex", "RuneScape")
               .replaceAll("jagex", "RuneScape");
            widget.secondaryText = buffer.readString();
            if (Client.getClient().graphicsEnabled) {
               boolean localFlag = false;
               if (widget.parentId == 638) {
                  Iterator iterator2 = QuestEntry.entries.iterator();

                  while (iterator2.hasNext()) {
                     QuestEntry questEntry4;
                     if ((questEntry4 = (QuestEntry)iterator2.next()).getId() == widget.id) {
                        if (widget.optionType == 1) {
                           questEntry4.setVisible(true);
                           questEntry4.setText(widget.message);
                        } else {
                           questEntry4.setVisible(false);
                           questEntry4.setText(widget.message);
                        }

                        localFlag = true;
                        break;
                     }
                  }

                  if (!localFlag) {
                     QuestEntry questEntry2 = new QuestEntry(widget.id);
                     if (widget.optionType == 1) {
                        questEntry2.setVisible(true);
                        questEntry2.setText(widget.message);
                     } else {
                        questEntry2.setVisible(false);
                        questEntry2.setText(widget.message);
                     }
                  }
               }
            }
         }

         if (widget.type == 1 || widget.type == 3 || widget.type == 4) {
            widget.textColor = buffer.readInt();
         }

         if (widget.type == 3 || widget.type == 4) {
            widget.secondaryColor = buffer.readInt();
            widget.defaultHoverColor = buffer.readInt();
            widget.secondaryHoverColor = buffer.readInt();
         }

         if (widget.type == 5 || widget.type == 17 || widget.type == 18 || widget.type == 19) {
            String localText = buffer.readString();
            if (newArchive != null && localText.length() > 0) {
               int localLastIndexOf = localText.lastIndexOf(",");
               if (localText.substring(0, localLastIndexOf).toLowerCase().equals("cache")) {
                  interfaceSpriteIndex = Integer.parseInt(localText.substring(localLastIndexOf + 1));
                  widget.disabledSprite = Client.interfaceSprites[interfaceSpriteIndex];
               } else {
                  widget.disabledSprite = loadSprite(Integer.parseInt(localText.substring(localLastIndexOf + 1)), newArchive, localText.substring(0, localLastIndexOf));
               }
            }

            localText = buffer.readString();
            if (newArchive != null && localText.length() > 0) {
               int lastIndexOf2 = localText.lastIndexOf(",");
               if (localText.substring(0, lastIndexOf2).toLowerCase().equals("cache")) {
                  interfaceSpriteIndex = Integer.parseInt(localText.substring(lastIndexOf2 + 1));
                  widget.enabledSprite = Client.interfaceSprites[interfaceSpriteIndex];
               } else {
                  widget.enabledSprite = loadSprite(Integer.parseInt(localText.substring(lastIndexOf2 + 1)), newArchive, localText.substring(0, lastIndexOf2));
               }
            }

            if (widget.type == 17) {
               localText = buffer.readString();
               if (newArchive != null && localText.length() > 0) {
                  int lastIndexOf3 = localText.lastIndexOf(",");
                  if (localText.substring(0, lastIndexOf3).toLowerCase().equals("cache")) {
                     interfaceSpriteIndex = Integer.parseInt(localText.substring(lastIndexOf3 + 1));
                     widget.alternateDisabledSprite = Client.interfaceSprites[interfaceSpriteIndex];
                  } else {
                     widget.alternateDisabledSprite = loadSprite(Integer.parseInt(localText.substring(lastIndexOf3 + 1)), newArchive, localText.substring(0, lastIndexOf3));
                  }
               }

               localText = buffer.readString();
               if (newArchive != null && localText.length() > 0) {
                  int lastIndexOf4 = localText.lastIndexOf(",");
                  if (localText.substring(0, lastIndexOf4).toLowerCase().equals("cache")) {
                     interfaceSpriteIndex = Integer.parseInt(localText.substring(lastIndexOf4 + 1));
                     widget.alternateEnabledSprite = Client.interfaceSprites[interfaceSpriteIndex];
                  } else {
                     widget.alternateEnabledSprite = loadSprite(Integer.parseInt(localText.substring(lastIndexOf4 + 1)), newArchive, localText.substring(0, lastIndexOf4));
                  }
               }
            }
         }

         if (widget.type == 18 || widget.type == 19) {
            boolean localType = widget.type == 18;
            interfaceSpriteIndex = widget.height;
            widgetIndex = widget.width;
            int[][] values;
            tileSpritePixels(values = new int[interfaceSpriteIndex][widgetIndex], Client.miscInterfaceSprites[0], 0, 0, widgetIndex, interfaceSpriteIndex);
            tileSpritePixels(values, Client.miscInterfaceSprites[5], 25, 0, widgetIndex - 25, 6);
            tileSpritePixels(values, Client.miscInterfaceSprites[7], 0, 30, 6, interfaceSpriteIndex - 30);
            tileSpritePixels(values, Client.miscInterfaceSprites[6], widgetIndex - 6, 30, widgetIndex, interfaceSpriteIndex - 30);
            tileSpritePixels(values, Client.miscInterfaceSprites[8], 25, interfaceSpriteIndex - 6, widgetIndex - 25, interfaceSpriteIndex);
            overlaySpritePixels(values, Client.miscInterfaceSprites[1], 0, 0, true);
            overlaySpritePixels(values, Client.miscInterfaceSprites[2], widgetIndex - 25, 0, true);
            overlaySpritePixels(values, Client.miscInterfaceSprites[3], 0, interfaceSpriteIndex - 30, true);
            overlaySpritePixels(values, Client.miscInterfaceSprites[4], widgetIndex - 25, interfaceSpriteIndex - 30, true);
            if (localType) {
               tileSpritePixels(values, Client.miscInterfaceSprites[5], 6, 29, widgetIndex - 6, 35);
            }

            int[] localFlattenIntGrid = Client.flattenIntGrid(values);
            widget.disabledSprite = widget.enabledSprite = new Sprite(widgetIndex, interfaceSpriteIndex, 0, 0, localFlattenIntGrid);
         }

         if (widget.type == 6) {
            if ((widgetIndex = buffer.readUnsignedByte()) != 0) {
               widget.defaultMediaType = 1;
               widget.defaultMediaId = (widgetIndex - 1 << 8) + buffer.readUnsignedByte();
            }

            if ((widgetIndex = buffer.readUnsignedByte()) != 0) {
               widget.enabledMediaType = 1;
               widget.enabledMediaId = (widgetIndex - 1 << 8) + buffer.readUnsignedByte();
            }

            if ((widgetIndex = buffer.readUnsignedByte()) != 0) {
               widget.defaultAnimationId = (widgetIndex - 1 << 8) + buffer.readUnsignedByte();
            } else {
               widget.defaultAnimationId = -1;
            }

            if ((widgetIndex = buffer.readUnsignedByte()) != 0) {
               widget.enabledAnimationId = (widgetIndex - 1 << 8) + buffer.readUnsignedByte();
            } else {
               widget.enabledAnimationId = -1;
            }

            widget.modelZoom = buffer.readUnsignedShort();
            widget.modelRotation1 = buffer.readUnsignedShort();
            widget.modelRotation2 = buffer.readUnsignedShort();
         }

         if (widget.type == 7) {
            widget.inventoryIds = new int[widget.width * widget.height];
            widget.inventoryAmounts = new int[widget.width * widget.height];
            widget.textAlignment = buffer.readUnsignedByte();
            widgetIndex = buffer.readUnsignedByte();
            widget.font = newFont[widgetIndex];
            widget.textShadow = buffer.readUnsignedByte() == 1;
            widget.textColor = buffer.readInt();
            widget.spritePaddingX = buffer.readShort();
            widget.spritePaddingY = buffer.readShort();
            widget.inventoryInterface = buffer.readUnsignedByte() == 1;
            widget.actions = new String[5];

            for (int actionIndex2 = 0; actionIndex2 < 5; actionIndex2++) {
               widget.actions[actionIndex2] = buffer.readString();
               if (widget.actions[actionIndex2].length() == 0) {
                  widget.actions[actionIndex2] = null;
               }
            }
         }

         if (widget.optionType == 2 || widget.type == 2) {
            widget.selectedActionName = buffer.readString();
            widget.spellName = buffer.readString();
            widget.spellUsableOn = buffer.readUnsignedShort();
         }

         if (widget.type == 8) {
            widget.message = buffer.readString();
         }

         if (widget.optionType == 1 || widget.optionType == 4 || widget.optionType == 5 || widget.optionType == 6) {
            widget.tooltip = buffer.readString();
            if (widget.tooltip.length() == 0) {
               if (widget.optionType == 1) {
                  widget.tooltip = "Ok";
               }

               if (widget.optionType == 4) {
                  widget.tooltip = "Select";
               }

               if (widget.optionType == 5) {
                  widget.tooltip = "Select";
               }

               if (widget.optionType == 6) {
                  widget.tooltip = "Continue";
               }
            }
         }
      }

      configureCastleWarsCatapultInterface();
      configureCastleWarsManualInterface();
      configureWorldTeleportInterface(newFont);

      if (Client.getClient().graphicsEnabled) {
         QuestEntry.categorizeQuests();
         QuestEntry.sortAndLayoutQuests();
         Widget widget3 = widgets[639];

         for (int childIdIndex2 = 0; childIdIndex2 < widget3.childIds.length; childIdIndex2++) {
            Widget widget2;
            if ((widget2 = widgets[widget3.childIds[childIdIndex2]]).message != null) {
               for (QuestEntry questEntry3 : QuestEntry.entries) {
                  if (widget2.id == questEntry3.getId()) {
                     widget3.childY[childIdIndex2] = questEntry3.getY();
                  }
               }
            }
         }

         widget3.scrollMax = QuestEntry.scrollHeight;
      }

      spriteCache = null;
   }
   private static void configureWorldTeleportInterface(RichTextFont[] fonts) {
      final int rootId = 19600;
      final int backgroundId = 19601;
      final int titleId = 19602;
      final int subtitleId = 19603;
      final int firstButtonId = 19610;
      final int destinationButtonCount = 9;
      final int pageButtonId = 19619;
      final int closeButtonId = 19620;
      final int backButtonId = 19621;
      final int firstLabelId = 19630;
      final int pageLabelId = 19639;
      final int backLabelId = 19604;
      final int lastWidgetId = pageLabelId;

      if (widgets == null || fonts == null || fonts.length < 3) {
         return;
      }
      if (widgets.length <= lastWidgetId) {
         widgets = java.util.Arrays.copyOf(widgets, lastWidgetId + 1);
      }

      Widget bankRoot = getWidget(5292);
      Widget bankBackground = null;
      int backgroundX = 18;
      int backgroundY = 18;
      int backgroundWidth = 476;
      int backgroundHeight = 286;
      int largestVisualArea = -1;

      if (bankRoot != null && bankRoot.childIds != null) {
         for (int childIndex = 0; childIndex < bankRoot.childIds.length; childIndex++) {
            Widget candidate = getWidget(bankRoot.childIds[childIndex]);
            if (!isVisualWidget(candidate)) {
               continue;
            }
            int area = Math.max(1, candidate.width) * Math.max(1, candidate.height);
            if (area > largestVisualArea) {
               largestVisualArea = area;
               bankBackground = candidate;
               backgroundX = bankRoot.childX[childIndex];
               backgroundY = bankRoot.childY[childIndex];
               backgroundWidth = candidate.width;
               backgroundHeight = candidate.height;
            }
         }
      }

      if (backgroundWidth < 260 || backgroundHeight < 150) {
         bankBackground = null;
         backgroundX = 18;
         backgroundY = 18;
         backgroundWidth = 476;
         backgroundHeight = 286;
      }

      Widget root = new Widget();
      root.id = rootId;
      root.parentId = -1;
      root.type = 0;
      root.optionType = 0;
      root.width = 512;
      root.height = 334;
      root.scrollMax = 334;
      root.hoverId = -1;
      widgets[rootId] = root;

      Widget background = createVisualClone(backgroundId, rootId, bankBackground,
            backgroundWidth, backgroundHeight);
      widgets[backgroundId] = background;

      Widget title = createTeleportTextWidget(titleId, rootId, fonts[2],
            "City Teleports", backgroundWidth, 20, 0xff981f);
      Widget subtitle = createTeleportTextWidget(subtitleId, rootId, fonts[1],
            "Select a destination", backgroundWidth, 18, 0xffffff);
      widgets[titleId] = title;
      widgets[subtitleId] = subtitle;

      Widget confirmButtonSource = findGrandExchangeConfirmButton();
      // The original GE confirm sprite is quite large. Scale it down so all
      // nine destinations fit as a clean 3x3 grid inside the bank frame.
      int buttonWidth = 86;
      int buttonHeight = 22;
      int horizontalGap = 5;
      int verticalGap = 5;
      int columns = 3;
      int rows = 3;
      int gridWidth = columns * buttonWidth + (columns - 1) * horizontalGap;
      int gridHeight = rows * buttonHeight + (rows - 1) * verticalGap;
      int gridX = backgroundX + (backgroundWidth - gridWidth) / 2;
      int gridY = backgroundY + 55;
      int navigationY = 0;

      root.childIds = new int[2 + destinationButtonCount * 2 + 4 + 1];
      root.childX = new int[root.childIds.length];
      root.childY = new int[root.childIds.length];
      int childIndex = 0;
      setChild(root, childIndex++, backgroundId, backgroundX, backgroundY);
      setChild(root, childIndex++, titleId, backgroundX, backgroundY + 9);

      for (int index = 0; index < destinationButtonCount; index++) {
         int buttonId = firstButtonId + index;
         int labelId = firstLabelId + index;
         int column = index % columns;
         int row = index / columns;
         int x = gridX + column * (buttonWidth + horizontalGap);
         int y = gridY + row * (buttonHeight + verticalGap);

         Widget button = createTeleportButton(buttonId, rootId, confirmButtonSource,
               buttonWidth, buttonHeight, "Teleport");
         Widget label = createTeleportTextWidget(labelId, rootId, fonts[0],
               "", buttonWidth, buttonHeight, 0xffffff);
         widgets[buttonId] = button;
         widgets[labelId] = label;
         setChild(root, childIndex++, buttonId, x, y);
         setChild(root, childIndex++, labelId, x, y + Math.max(0, (buttonHeight - 11) / 2));
      }

      int navigationButtonWidth = 112;
      int navigationButtonHeight = 24;
      int navigationMargin = 18;
      navigationY = backgroundY + backgroundHeight - navigationButtonHeight - 14;
      int backButtonX = backgroundX + navigationMargin;
      int pageButtonX = backgroundX + backgroundWidth - navigationMargin - navigationButtonWidth;

      Widget backButton = createTeleportButton(backButtonId, rootId, confirmButtonSource,
            navigationButtonWidth, navigationButtonHeight, "Previous page");
      Widget backLabel = createTeleportTextWidget(backLabelId, rootId, fonts[0],
            "", navigationButtonWidth, navigationButtonHeight, 0xffffff);
      widgets[backButtonId] = backButton;
      widgets[backLabelId] = backLabel;
      setChild(root, childIndex++, backButtonId, backButtonX, navigationY);
      setChild(root, childIndex++, backLabelId, backButtonX,
            navigationY + Math.max(0, (navigationButtonHeight - 11) / 2));

      Widget pageButton = createTeleportButton(pageButtonId, rootId, confirmButtonSource,
            navigationButtonWidth, navigationButtonHeight, "Next page");
      Widget pageLabel = createTeleportTextWidget(pageLabelId, rootId, fonts[0],
            "Next ->", navigationButtonWidth, navigationButtonHeight, 0xffffff);
      widgets[pageButtonId] = pageButton;
      widgets[pageLabelId] = pageLabel;
      setChild(root, childIndex++, pageButtonId, pageButtonX, navigationY);
      setChild(root, childIndex++, pageLabelId, pageButtonX,
            navigationY + Math.max(0, (navigationButtonHeight - 11) / 2));

      Widget closeSource = findBankCloseButton(bankRoot);
      Widget closeButton = createTeleportButton(closeButtonId, rootId, closeSource,
            closeSource == null ? 42 : closeSource.width,
            closeSource == null ? 18 : closeSource.height, "Close Window");
      if (closeSource == null) {
         closeButton.type = 4;
         closeButton.font = fonts[1];
         closeButton.message = "Close";
         closeButton.textAlignment = 1;
         closeButton.textShadow = true;
         closeButton.textColor = 0xff981f;
      }
      widgets[closeButtonId] = closeButton;

      int closeX = backgroundX + backgroundWidth - closeButton.width - 8;
      int closeY = backgroundY + 7;
      if (bankRoot != null && closeSource != null) {
         int sourceChildIndex = findChildIndex(bankRoot, closeSource.id);
         if (sourceChildIndex >= 0) {
            closeX = bankRoot.childX[sourceChildIndex];
            closeY = bankRoot.childY[sourceChildIndex];
         }
      }
      setChild(root, childIndex++, closeButtonId, closeX, closeY);

      // Subtitle is added last so it stays crisp above the bank-style frame.
      int[] oldIds = root.childIds;
      int[] oldX = root.childX;
      int[] oldY = root.childY;
      root.childIds = java.util.Arrays.copyOf(oldIds, oldIds.length + 1);
      root.childX = java.util.Arrays.copyOf(oldX, oldX.length + 1);
      root.childY = java.util.Arrays.copyOf(oldY, oldY.length + 1);
      setChild(root, root.childIds.length - 1, subtitleId, backgroundX, backgroundY + 31);
   }

   private static Widget getWidget(int widgetId) {
      return widgets != null && widgetId >= 0 && widgetId < widgets.length
            ? widgets[widgetId] : null;
   }

   private static boolean isVisualWidget(Widget widget) {
      if (widget == null || widget.width <= 0 || widget.height <= 0) {
         return false;
      }
      if (widget.type == 3) {
         return true;
      }
      return (widget.type == 5 || widget.type == 17 || widget.type == 18 || widget.type == 19)
            && (widget.disabledSprite != null || widget.enabledSprite != null);
   }

   private static Widget createVisualClone(int widgetId, int parentId, Widget source,
         int fallbackWidth, int fallbackHeight) {
      Widget widget = new Widget();
      widget.id = widgetId;
      widget.parentId = parentId;
      widget.optionType = 0;
      widget.hoverId = -1;
      widget.width = source != null && source.width > 0 ? source.width : fallbackWidth;
      widget.height = source != null && source.height > 0 ? source.height : fallbackHeight;

      if (source != null && source.type == 3) {
         widget.type = 3;
         widget.filled = source.filled;
         widget.opacity = source.opacity;
         widget.textColor = source.textColor;
         widget.secondaryColor = source.secondaryColor;
         widget.defaultHoverColor = source.defaultHoverColor;
         widget.secondaryHoverColor = source.secondaryHoverColor;
      } else if (source != null && (source.disabledSprite != null || source.enabledSprite != null)) {
         widget.type = 5;
         widget.disabledSprite = source.disabledSprite != null ? source.disabledSprite : source.enabledSprite;
         widget.enabledSprite = source.enabledSprite != null ? source.enabledSprite : source.disabledSprite;
      } else {
         widget.type = 3;
         widget.filled = true;
         widget.opacity = 0;
         widget.textColor = 0x3d3529;
         widget.secondaryColor = 0x3d3529;
         widget.defaultHoverColor = 0x3d3529;
         widget.secondaryHoverColor = 0x3d3529;
      }
      return widget;
   }

   private static Widget createTeleportButton(int widgetId, int parentId, Widget source,
         int width, int height, String tooltip) {
      Widget widget = createVisualClone(widgetId, parentId, source, width, height);
      widget.width = width;
      widget.height = height;
      widget.optionType = 1;
      widget.tooltip = tooltip;
      widget.hoverId = -1;

      // Type-5 widgets draw the sprite at its native dimensions, so changing
      // only widget.width/height would shrink the hitbox but not the artwork.
      // Scale the GE button sprites themselves to keep the visual grid tidy.
      if (source != null && widget.type == 5) {
         Sprite disabled = source.disabledSprite != null
               ? source.disabledSprite : source.enabledSprite;
         Sprite enabled = source.enabledSprite != null
               ? source.enabledSprite : disabled;
         widget.disabledSprite = scaleTeleportSprite(disabled, width, height);
         widget.enabledSprite = scaleTeleportSprite(enabled, width, height);
      }
      return widget;
   }

   private static Sprite scaleTeleportSprite(Sprite source, int width, int height) {
      if (source == null || source.pixels == null || source.spriteWidth <= 0
            || source.spriteHeight <= 0 || width <= 0 || height <= 0) {
         return source;
      }
      if (source.spriteWidth == width && source.spriteHeight == height) {
         return source;
      }

      int[] pixels = new int[width * height];
      for (int y = 0; y < height; y++) {
         int sourceY = y * source.spriteHeight / height;
         int sourceRow = sourceY * source.spriteWidth;
         int targetRow = y * width;
         for (int x = 0; x < width; x++) {
            int sourceX = x * source.spriteWidth / width;
            pixels[targetRow + x] = source.pixels[sourceRow + sourceX];
         }
      }

      Sprite scaled = new Sprite(width, height, 0, 0, pixels);
      scaled.canvasWidth = width;
      scaled.canvasHeight = height;
      return scaled;
   }

   private static Widget createTeleportTextWidget(int widgetId, int parentId,
         RichTextFont font, String text, int width, int height, int color) {
      Widget widget = new Widget();
      widget.id = widgetId;
      widget.parentId = parentId;
      widget.type = 4;
      widget.optionType = 0;
      widget.hoverId = -1;
      widget.width = width;
      widget.height = height;
      widget.font = font;
      widget.textAlignment = 1;
      widget.textShadow = true;
      widget.message = text;
      widget.secondaryText = "";
      widget.textColor = color;
      widget.secondaryColor = color;
      widget.defaultHoverColor = color;
      widget.secondaryHoverColor = color;
      return widget;
   }

   private static Widget findGrandExchangeConfirmButton() {
      int[] preferredIds = new int[]{18896, 18945};
      for (int widgetId : preferredIds) {
         Widget widget = getWidget(widgetId);
         if (isVisualWidget(widget)) {
            return widget;
         }
      }

      int upperBound = Math.min(widgets.length - 1, 19018);
      for (int widgetId = 18890; widgetId <= upperBound; widgetId++) {
         Widget widget = getWidget(widgetId);
         if (!isVisualWidget(widget) || widget.tooltip == null) {
            continue;
         }
         if (widget.tooltip.toLowerCase().contains("confirm")) {
            return widget;
         }
      }
      return null;
   }

   private static Widget findBankCloseButton(Widget bankRoot) {
      if (bankRoot == null || bankRoot.childIds == null) {
         return null;
      }
      for (int childId : bankRoot.childIds) {
         Widget child = getWidget(childId);
         if (child == null || child.tooltip == null || !isVisualWidget(child)) {
            continue;
         }
         if (child.tooltip.toLowerCase().contains("close")) {
            return child;
         }
      }
      return null;
   }

   private static int findChildIndex(Widget parent, int childId) {
      if (parent == null || parent.childIds == null) {
         return -1;
      }
      for (int childIndex = 0; childIndex < parent.childIds.length; childIndex++) {
         if (parent.childIds[childIndex] == childId) {
            return childIndex;
         }
      }
      return -1;
   }

   private static void setChild(Widget parent, int childIndex, int childId, int x, int y) {
      parent.childIds[childIndex] = childId;
      parent.childX[childIndex] = x;
      parent.childY[childIndex] = y;
   }

   private static void configureCastleWarsManualInterface() {
      // Cache 377's Castle Wars manual uses the standard book interface.
      // The arrow sprites are present, but this cache has them decoded without
      // an actionable option type, so clicking them never sends packet 185.
      makeInterfaceButton(840, "Previous Page");
      makeInterfaceButton(842, "Next Page");
   }

   private static void makeInterfaceButton(int widgetId, String tooltip) {
      if (widgets == null || widgetId < 0 || widgetId >= widgets.length
            || widgets[widgetId] == null) {
         return;
      }

      Widget widget = widgets[widgetId];
      widget.optionType = 1;
      widget.tooltip = tooltip;
   }

   private static void configureCastleWarsCatapultInterface() {
      final int rootId = 11169;
      final int closeId = 11259;
      final int upId = 11321;
      final int downId = 11322;
      final int rightId = 11323;
      final int leftId = 11324;
      final int fireId = 11329;

      if (widgets == null || rootId >= widgets.length || widgets[rootId] == null) {
         return;
      }

      makeCastleWarsCatapultButton(closeId, "Close Window");
      makeCastleWarsCatapultButton(upId, "Aim up");
      makeCastleWarsCatapultButton(downId, "Aim down");
      makeCastleWarsCatapultButton(leftId, "Aim left");
      makeCastleWarsCatapultButton(rightId, "Aim right");
      makeCastleWarsCatapultButton(fireId, "Fire Catapult");
      resolveCastleWarsCatapultMarker(rootId, closeId, upId, downId, leftId, rightId, fireId);

      java.util.ArrayList<Widget> controls = new java.util.ArrayList<Widget>();
      for (int id = 0; id < widgets.length; id++) {
         Widget candidate = widgets[id];
         if (candidate == null || candidate.id == closeId || candidate.id == fireId
               || candidate.id == upId || candidate.id == downId
               || candidate.id == leftId || candidate.id == rightId
               || !isCastleWarsCatapultDescendant(candidate, rootId)
               || !isCastleWarsCatapultArrowCandidate(candidate)) {
            continue;
         }
         controls.add(candidate);
      }

      if (controls.size() < 4) {
         return;
      }

      java.util.ArrayList<Widget> verticalCandidates = new java.util.ArrayList<Widget>();
      java.util.ArrayList<Widget> horizontalCandidates = new java.util.ArrayList<Widget>();

      for (Widget candidate : controls) {
         int x = castleWarsCatapultAbsoluteCoordinate(candidate, rootId, true);
         int y = castleWarsCatapultAbsoluteCoordinate(candidate, rootId, false);
         if (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE) {
            continue;
         }

         if (x >= 330 && y >= 15 && y <= 145) {
            verticalCandidates.add(candidate);
         }
         if (x >= 230 && y >= 145 && y <= 245) {
            horizontalCandidates.add(candidate);
         }
      }

      Widget[] vertical = selectCastleWarsCatapultPair(
            verticalCandidates.size() >= 2 ? verticalCandidates : controls,
            rootId, true);
      if (vertical == null) {
         return;
      }

      Widget up = castleWarsCatapultAbsoluteCoordinate(vertical[0], rootId, false)
            <= castleWarsCatapultAbsoluteCoordinate(vertical[1], rootId, false)
            ? vertical[0] : vertical[1];
      Widget down = up == vertical[0] ? vertical[1] : vertical[0];

      java.util.ArrayList<Widget> horizontalPool = horizontalCandidates.size() >= 2
            ? horizontalCandidates : controls;
      java.util.ArrayList<Widget> withoutVertical = new java.util.ArrayList<Widget>();
      for (Widget candidate : horizontalPool) {
         if (candidate != up && candidate != down) {
            withoutVertical.add(candidate);
         }
      }

      Widget[] horizontal = selectCastleWarsCatapultPair(withoutVertical, rootId, false);
      if (horizontal == null) {
         return;
      }

      Widget left = castleWarsCatapultAbsoluteCoordinate(horizontal[0], rootId, true)
            <= castleWarsCatapultAbsoluteCoordinate(horizontal[1], rootId, true)
            ? horizontal[0] : horizontal[1];
      Widget right = left == horizontal[0] ? horizontal[1] : horizontal[0];

      makeCastleWarsCatapultButton(up.id, "Aim up");
      makeCastleWarsCatapultButton(down.id, "Aim down");
      makeCastleWarsCatapultButton(left.id, "Aim left");
      makeCastleWarsCatapultButton(right.id, "Aim right");
   }

   private static void makeCastleWarsCatapultButton(int widgetId, String tooltip) {
      makeInterfaceButton(widgetId, tooltip);
   }

   private static void resolveCastleWarsCatapultMarker(int rootId, int... excludedIds) {
      castleWarsCatapultMarkerWidgetId = -1;
      int bestScore = -1;

      for (int id = 0; id < widgets.length; id++) {
         Widget candidate = widgets[id];
         if (candidate == null || candidate.type != 5
               || !isCastleWarsCatapultDescendant(candidate, rootId)
               || isCastleWarsCatapultExcluded(id, excludedIds)) {
            continue;
         }

         Sprite sprite = candidate.disabledSprite != null
               ? candidate.disabledSprite : candidate.enabledSprite;
         if (sprite == null || sprite.pixels == null
               || sprite.spriteWidth < 4 || sprite.spriteHeight < 4
               || sprite.spriteWidth > 32 || sprite.spriteHeight > 32) {
            continue;
         }

         int visiblePixels = 0;
         int redPixels = 0;
         for (int pixel : sprite.pixels) {
            if (pixel == 0) {
               continue;
            }
            visiblePixels++;
            int red = pixel >> 16 & 255;
            int green = pixel >> 8 & 255;
            int blue = pixel & 255;
            if (red >= 140 && red >= green + 45 && red >= blue + 45) {
               redPixels++;
            }
         }

         if (visiblePixels == 0 || redPixels < 4) {
            continue;
         }

         int score = redPixels * 1000 / visiblePixels;
         if (score > bestScore) {
            bestScore = score;
            castleWarsCatapultMarkerWidgetId = id;
         }
      }
   }

   private static boolean isCastleWarsCatapultExcluded(int widgetId, int[] excludedIds) {
      if (excludedIds == null) {
         return false;
      }
      for (int excludedId : excludedIds) {
         if (widgetId == excludedId) {
            return true;
         }
      }
      return false;
   }

   public static void updateCastleWarsCatapultAimMarker(int aimX, int aimY) {
      if (widgets == null) {
         return;
      }
      if (castleWarsCatapultMarkerWidgetId < 0
            || castleWarsCatapultMarkerWidgetId >= widgets.length
            || widgets[castleWarsCatapultMarkerWidgetId] == null) {
         resolveCastleWarsCatapultMarker(
               11169, 11259, 11321, 11322, 11323, 11324, 11329);
      }
      if (castleWarsCatapultMarkerWidgetId < 0
            || castleWarsCatapultMarkerWidgetId >= widgets.length) {
         return;
      }

      Widget marker = widgets[castleWarsCatapultMarkerWidgetId];
      int clampedX = Math.max(0, Math.min(30, aimX));
      int clampedY = Math.max(0, Math.min(30, aimY));
      marker.runtimeXOffset = (clampedY - 15) * 4;
      marker.runtimeYOffset = (15 - clampedX) * 4;
   }

   private static boolean isCastleWarsCatapultArrowCandidate(Widget widget) {
      if (widget == null
            || (widget.type != 5 && widget.type != 17
            && widget.type != 18 && widget.type != 19)) {
         return false;
      }
      return widget.width > 0 && widget.height > 0
            && widget.width <= 64 && widget.height <= 64;
   }

   private static boolean isCastleWarsCatapultDescendant(Widget widget, int rootId) {
      if (widget == null) {
         return false;
      }
      int currentId = widget.id;
      for (int depth = 0; depth < 16 && currentId != rootId; depth++) {
         Widget current = currentId >= 0 && currentId < widgets.length
               ? widgets[currentId] : null;
         if (current == null || current.parentId < 0 || current.parentId == currentId) {
            return false;
         }
         currentId = current.parentId;
      }
      return currentId == rootId;
   }

   private static Widget[] selectCastleWarsCatapultPair(
         java.util.ArrayList<Widget> controls, int rootId, boolean useX) {
      if (controls == null || controls.size() < 2) {
         return null;
      }

      Widget first = null;
      Widget second = null;
      int firstValue = Integer.MIN_VALUE;
      int secondValue = Integer.MIN_VALUE;

      for (Widget candidate : controls) {
         int value = castleWarsCatapultAbsoluteCoordinate(candidate, rootId, useX);
         if (value == Integer.MIN_VALUE) {
            continue;
         }
         if (first == null || value > firstValue) {
            second = first;
            secondValue = firstValue;
            first = candidate;
            firstValue = value;
         } else if (candidate != first && (second == null || value > secondValue)) {
            second = candidate;
            secondValue = value;
         }
      }

      return first == null || second == null ? null : new Widget[]{first, second};
   }

   private static int castleWarsCatapultAbsoluteCoordinate(
         Widget widget, int rootId, boolean xAxis) {
      if (widget == null) {
         return Integer.MIN_VALUE;
      }

      int coordinate = 0;
      int currentId = widget.id;
      for (int depth = 0; depth < 16 && currentId != rootId; depth++) {
         Widget current = currentId >= 0 && currentId < widgets.length
               ? widgets[currentId] : null;
         if (current == null || current.parentId < 0 || current.parentId >= widgets.length) {
            return Integer.MIN_VALUE;
         }

         Widget parent = widgets[current.parentId];
         if (parent == null || parent.childIds == null) {
            return Integer.MIN_VALUE;
         }

         int childCoordinate = Integer.MIN_VALUE;
         for (int childIndex = 0; childIndex < parent.childIds.length; childIndex++) {
            if (parent.childIds[childIndex] == currentId) {
               childCoordinate = xAxis
                     ? parent.childX[childIndex] : parent.childY[childIndex];
               break;
            }
         }
         if (childCoordinate == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
         }

         coordinate += childCoordinate;
         currentId = current.parentId;
      }

      return currentId == rootId ? coordinate : Integer.MIN_VALUE;
   }

   private static void configureCastleWarsWaitingInterface(RichTextFont[] fonts) {
      if (widgets == null || fonts == null || fonts.length < 3) {
         return;
      }

      final int parentId = 6673;
      final int timerId = 6570;
      final int zamorakCountId = 6572;
      final int saradominCountId = 6664;

      if (timerId < widgets.length && widgets[timerId] != null) {
         Widget timer = widgets[timerId];
         timer.font = fonts[2];
         timer.textAlignment = 1;
         timer.textShadow = true;
         timer.width = 512;
      }

      if (zamorakCountId < widgets.length && widgets[zamorakCountId] != null) {
         widgets[zamorakCountId].message = "";
      }
      if (saradominCountId < widgets.length && widgets[saradominCountId] != null) {
         widgets[saradominCountId].message = "";
      }

      if (parentId < widgets.length && widgets[parentId] != null) {
         Widget parent = widgets[parentId];
         if (parent.childIds != null) {
            for (int i = 0; i < parent.childIds.length; i++) {
               if (parent.childIds[i] == timerId) {
                  parent.childX[i] = 0;
                  parent.childY[i] = 24;
               } else if (parent.childIds[i] == zamorakCountId
                     || parent.childIds[i] == saradominCountId) {
                  parent.childX[i] = -1000;
                  parent.childY[i] = -1000;
               }
            }
         }
      }
   }

   private static Model getMediaModel(int scalarArgument, int modelHeaderIndex) {
      Model model;
      if ((model = (Model)modelCache.get((scalarArgument << 16) + modelHeaderIndex)) != null) {
         return model;
      }

      if (scalarArgument == 1) {
         model = Model.getModel(modelHeaderIndex);
      }

      if (scalarArgument == 2) {
         model = NpcDefinition.lookup(modelHeaderIndex).model();
      }

      if (scalarArgument == 3) {
         model = Client.localPlayer.getHeadModel();
      }

      if (scalarArgument == 4) {
         model = ItemDefinition.lookup(modelHeaderIndex).getUnshadedModel(50);
      }

      if (scalarArgument == 5) {
         model = null;
      }

      if (model != null) {
         modelCache.put(model, (scalarArgument << 16) + modelHeaderIndex);
      }

      return model;
   }
   private static Sprite loadSprite(int scalarArgument, Archive archive, String text) {
      long key = (NameUtils.hashUsername(text) << 8) + scalarArgument;
      Sprite sprite;
      if ((sprite = (Sprite)spriteCache.get(key)) != null) {
         return sprite;
      }

      try {
         sprite = new Sprite(archive, text, scalarArgument);
         spriteCache.put(sprite, key);
         return sprite;
      } catch (Exception exception) {
         return null;
      }
   }
   public static void setCachedMediaModel(boolean flag, Model model) {
      if (!flag) {
         modelCache.clear();
         modelCache.put(model, 327680L);
      }
   }
   public final Model getAnimatedModel(int frameIndex, int frameIndex2, boolean flag, int right, int frameLength, int animationFrameCycle) {
      Model model;
      if (flag) {
         model = getMediaModel(this.enabledMediaType, this.enabledMediaId);
      } else {
         model = getMediaModel(this.defaultMediaType, this.defaultMediaId);
      }

      if (model == null) {
         return null;
      }

      if (frameIndex2 == -1 && frameIndex == -1 && model.triangleColorValues == null) {
         return model;
      }

      Model model2 = new Model(true, AnimationFrame.isNullFrame(frameIndex2) & AnimationFrame.isNullFrame(frameIndex), false, model);
      if (frameIndex2 != -1 || frameIndex != -1) {
         model2.skin();
      }

      if (frameIndex2 != -1) {
         if (Client.smoothAnimations && right != -1) {
            model2.applyInterpolatedAnimation(frameIndex2, right, animationFrameCycle, frameLength);
         } else {
            model2.applyAnimationFrame(frameIndex2);
         }
      }

      if (frameIndex != -1) {
         if (Client.smoothAnimations && right != -1) {
            model2.applyInterpolatedAnimation(frameIndex, right, animationFrameCycle, frameLength);
         } else {
            model2.applyAnimationFrame(frameIndex);
         }
      }

      model2.light(64, 768, -50, -10, -50, true);
      return model2;
   }
}
