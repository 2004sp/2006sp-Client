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
         Widget widget = widgets[widgetIndex];
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
         Widget widget;
         (widget = widgets[widgetId]).actions = new String[5];
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
      final int fireId = 11328;

      if (widgets == null || rootId >= widgets.length || widgets[rootId] == null) {
         return;
      }

      makeCastleWarsCatapultButton(closeId, "Close Window");
      makeCastleWarsCatapultButton(fireId, "Fire Catapult");

      java.util.ArrayList<Widget> controls = new java.util.ArrayList<Widget>();
      for (int id = 0; id < widgets.length; id++) {
         Widget candidate = widgets[id];
         if (candidate == null || candidate.id == closeId || candidate.id == fireId
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
