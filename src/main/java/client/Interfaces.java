package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Native revision-443 archive-3 interface loader with a legacy-renderer slot adapter. */
final class Interfaces {
   private static Cache cache;
   private static Varbits varbits;
   private static RichTextFont[] fonts;
   private static final Map<Integer, RichTextFont> FONTS_BY_ARCHIVE_ID = new HashMap<Integer, RichTextFont>();
   private static Cache.ReferenceTable index;
   private static final Map<Integer, Integer> PACKED_TO_RENDER = new HashMap<Integer, Integer>();
   private static final Map<Integer, Integer> RENDER_TO_PACKED = new HashMap<Integer, Integer>();
   private static final Map<Integer, Integer> GROUP_ROOTS = new HashMap<Integer, Integer>();
   private static final java.util.Set<Integer> LOADED_GROUPS = new java.util.HashSet<Integer>();
   private static final java.util.Set<Integer> FAILED_GROUPS = new java.util.HashSet<Integer>();
   private static final Map<Integer, String> FAILURE_REASONS = new java.util.LinkedHashMap<Integer, String>();
   private static int nextRenderId;
   private static final java.util.Map<Integer, Integer> TYPE_COUNTS = new java.util.TreeMap<Integer, Integer>();
   private static final java.util.Map<Integer, Integer> OPTION_COUNTS = new java.util.TreeMap<Integer, Integer>();
   private static final java.util.Set<Integer> FONT_IDS = new java.util.TreeSet<Integer>();
   private static final java.util.Set<Integer> SPRITE_IDS = new java.util.TreeSet<Integer>();
   private static final java.util.Set<Integer> MISSING_SPRITE_IDS = new java.util.TreeSet<Integer>();
   private static int alternateModelReferences;

   private Interfaces() {}

   static void initialize(Cache newCache, RichTextFont[] newFonts) throws IOException {
      cache = newCache;
      varbits = Varbits.load(newCache);
      fonts = newFonts;
      index = cache.readReferenceTable(3);
      FONTS_BY_ARCHIVE_ID.clear();
      if (fonts != null) {
         if (fonts.length > 0 && fonts[0] != null) FONTS_BY_ARCHIVE_ID.put(494, fonts[0]);
         if (fonts.length > 1 && fonts[1] != null) FONTS_BY_ARCHIVE_ID.put(495, fonts[1]);
         if (fonts.length > 2 && fonts[2] != null) FONTS_BY_ARCHIVE_ID.put(496, fonts[2]);
         if (fonts.length > 3 && fonts[3] != null) FONTS_BY_ARCHIVE_ID.put(497, fonts[3]);
      }
      // The four additional IF1 fonts use unnamed archive-8 sprite groups and
      // archive-13 metrics groups. Their pairings are verified from glyph advances.
      FONTS_BY_ARCHIVE_ID.put(645, new RichTextFont(cache, 645, 0));
      FONTS_BY_ARCHIVE_ID.put(646, new RichTextFont(cache, 646, 1));
      FONTS_BY_ARCHIVE_ID.put(647, new RichTextFont(cache, 647, 2));
      FONTS_BY_ARCHIVE_ID.put(648, new RichTextFont(cache, 648, 3));
      PACKED_TO_RENDER.clear();
      RENDER_TO_PACKED.clear();
      GROUP_ROOTS.clear();
      LOADED_GROUPS.clear();
      FAILED_GROUPS.clear();
      FAILURE_REASONS.clear();
      TYPE_COUNTS.clear();
      OPTION_COUNTS.clear();
      FONT_IDS.clear();
      SPRITE_IDS.clear();
      MISSING_SPRITE_IDS.clear();
      alternateModelReferences = 0;
      nextRenderId = Widget.widgets == null ? 0 : Widget.widgets.length;
      configureSkillLevelUpInterface(newFonts);
      System.out.println("Revision 443 interfaces: archive 3 has " + index.getGroupIds().length + " groups (lazy native decode)");
   }

   private static void configureSkillLevelUpInterface(RichTextFont[] fonts) {
      final int rootId = 19580;
      final int backgroundId = 19581;
      final int headingId = 19582;
      final int levelId = 19583;
      final int continueId = 19584;

      if (fonts == null || fonts.length < 3
              || fonts[0] == null || fonts[1] == null || fonts[2] == null) {
         return;
      }

      if (Widget.widgets == null || Widget.widgets.length <= continueId) {
         Widget.widgets = Widget.widgets == null
                 ? new Widget[20000]
                 : Arrays.copyOf(Widget.widgets, continueId + 1);
      }

      Widget root = new Widget();
      root.id = rootId;
      root.parentId = -1;
      root.type = 0;
      root.width = 512;
      root.height = 114;
      root.scrollMax = 114;
      root.hoverId = -1;
      root.childIds = new int[] {backgroundId, headingId, levelId, continueId};
      root.childX = new int[] {12, 0, 0, 0};
      root.childY = new int[] {8, 17, 42, 78};
      Widget.widgets[rootId] = root;

      Widget background = new Widget();
      background.id = backgroundId;
      background.parentId = rootId;
      background.type = 3;
      background.width = 488;
      background.height = 96;
      background.filled = true;
      background.textColor = 0x3d3529;
      background.secondaryColor = 0x3d3529;
      background.defaultHoverColor = 0x3d3529;
      background.secondaryHoverColor = 0x3d3529;
      background.hoverId = -1;
      Widget.widgets[backgroundId] = background;

      Widget heading = levelUpText(headingId, rootId, fonts[2],
              "", 488, 20, 0xff981f);
      Widget.widgets[headingId] = heading;

      Widget level = levelUpText(levelId, rootId, fonts[1],
              "", 488, 18, 0xffffff);
      Widget.widgets[levelId] = level;

      Widget continueText = levelUpText(continueId, rootId, fonts[0],
              "Click here to continue", 488, 16, 0xff981f);
      continueText.optionType = 6;
      continueText.tooltip = "Continue";
      Widget.widgets[continueId] = continueText;
   }

   private static Widget levelUpText(int id, int parentId, RichTextFont font,
                                     String text, int width, int height, int color) {
      Widget widget = new Widget();
      widget.id = id;
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

   static boolean active() { return cache != null && index != null; }

   static int varbitValue(int id) {
      Varbits.Definition definition = varbits == null ? null : varbits.get(id);
      return definition == null ? 0 : varbits.getValue(id, Varps.get(definition.varpIndex));
   }

   static int packedForRenderId(int renderId) {
      Integer packed = RENDER_TO_PACKED.get(renderId);
      return packed == null ? -1 : packed.intValue();
   }

   static Widget widget(int packedId) {
      if (!active() || packedId < 0) return null;
      int group = packedId >>> 16;
      ensureGroup(group);
      Integer render = PACKED_TO_RENDER.get(packedId);
      return render == null || Widget.widgets == null || render.intValue() >= Widget.widgets.length
         ? null : Widget.widgets[render.intValue()];
   }

   static int rootRenderId(int groupId) {
      if (!active() || groupId < 0) return -1;
      ensureGroup(groupId);
      Integer root = GROUP_ROOTS.get(groupId);
      return root == null ? -1 : root.intValue();
   }

   private static void ensureGroup(int group) {
      if (!active() || LOADED_GROUPS.contains(group) || FAILED_GROUPS.contains(group)) return;
      try {
         decodeGroup(group);
         LOADED_GROUPS.add(group);
      } catch (Exception exception) {
         FAILED_GROUPS.add(group);
         FAILURE_REASONS.put(group, exception.getMessage());
         System.err.println("Failed to decode revision 443 interface group " + group + ": " + exception.getMessage());
      }
   }

   static void auditAllGroups() {
      if (!active()) return;
      int[] groups = index.getGroupIds();
      for (int group : groups) ensureGroup(group);
      int unresolvedRenderSlots = 0;
      for (Integer renderId : RENDER_TO_PACKED.keySet()) {
         int id = renderId.intValue();
         if (Widget.widgets == null || id < 0 || id >= Widget.widgets.length || Widget.widgets[id] == null) unresolvedRenderSlots++;
      }
      System.out.println("Revision 443 interface audit: " + (groups.length - FAILED_GROUPS.size())
         + "/" + groups.length + " groups decoded; failures=" + FAILURE_REASONS);
      System.out.println("  types=" + TYPE_COUNTS + " options=" + OPTION_COUNTS);
      java.util.Set<Integer> unresolvedFonts = new java.util.TreeSet<Integer>(FONT_IDS);
      unresolvedFonts.removeAll(FONTS_BY_ARCHIVE_ID.keySet());
      System.out.println("  fonts=" + FONT_IDS + " unresolvedFonts=" + unresolvedFonts
         + " sprites=" + SPRITE_IDS.size() + " missingSprites=" + MISSING_SPRITE_IDS);
      System.out.println("  alternateModels=" + alternateModelReferences
         + " unresolvedRenderSlots=" + unresolvedRenderSlots);
   }

   private static void decodeGroup(int group) throws IOException {
      int[] fileIds = index.getFileIds(group);
      if (fileIds == null || fileIds.length == 0) throw new IOException("missing archive-3 group");
      Map<Integer, byte[]> files = cache.readFiles(3, group);
      ArrayList<Node> nodes = new ArrayList<Node>(fileIds.length);
      for (int child : fileIds) {
         byte[] data = files.get(child);
         if (data == null || data.length == 0) continue;
         int packed = group << 16 | child;
         Node node = decodeIf1(packed, data);
         if (packed == (320 << 16 | 141)) {
            // Match the period skill-tab layout: label on the first line,
            // numeric total centered beneath it.
            node.widget.message = "Total Level:\n%1";
         }
         nodes.add(node);
         int renderId = allocateRenderId(packed);
         node.widget.id = renderId;
         Widget.widgets[renderId] = node.widget;
      }
      if (nodes.isEmpty()) throw new IOException("empty archive-3 group");

      ArrayList<Node> topLevel = new ArrayList<Node>();
      for (Node node : nodes) if (node.parentPacked == -1) topLevel.add(node);
      if (topLevel.isEmpty()) topLevel.add(nodes.get(0));

      // A JS5 interface group may contain many independent top-level IF1 components.
      // The 377 renderer expects one flat root, so give every group a runtime-only
      // synthetic root. Packed ids are retained only by real cache components.
      int groupRootId = allocateSyntheticRenderId();
      Widget groupRoot = new Widget();
      groupRoot.id = groupRootId;
      groupRoot.parentId = -1;
      groupRoot.type = 0;
      groupRoot.optionType = 0;
      groupRoot.width = 512;
      groupRoot.height = 334;
      groupRoot.scrollMax = 334;
      groupRoot.hoverId = -1;
      groupRoot.childIds = new int[topLevel.size()];
      groupRoot.childX = new int[topLevel.size()];
      groupRoot.childY = new int[topLevel.size()];
      for (int i = 0; i < topLevel.size(); i++) {
         Node node = topLevel.get(i);
         groupRoot.childIds[i] = PACKED_TO_RENDER.get(node.packed);
         groupRoot.childX[i] = node.x;
         groupRoot.childY[i] = node.y;
      }
      Widget.widgets[groupRootId] = groupRoot;
      GROUP_ROOTS.put(group, groupRootId);

      for (Node parent : nodes) {
         ArrayList<Node> children = new ArrayList<Node>();
         for (Node child : nodes) if (child.parentPacked == parent.packed) children.add(child);
         if (!children.isEmpty()) {
            parent.widget.childIds = new int[children.size()];
            parent.widget.childX = new int[children.size()];
            parent.widget.childY = new int[children.size()];
            for (int i = 0; i < children.size(); i++) {
               Node child = children.get(i);
               parent.widget.childIds[i] = PACKED_TO_RENDER.get(child.packed);
               parent.widget.childX[i] = child.x;
               parent.widget.childY[i] = child.y;
            }
         }
         parent.widget.parentId = parent.parentPacked == -1 ? groupRootId : renderId(parent.parentPacked);
      }
   }

   private static int allocateRenderId(int packed) {
      Integer old = PACKED_TO_RENDER.get(packed);
      if (old != null) return old.intValue();
      int id = nextRenderId++;
      ensureWidgetCapacity(id);
      PACKED_TO_RENDER.put(packed, id);
      RENDER_TO_PACKED.put(id, packed);
      return id;
   }

   private static int allocateSyntheticRenderId() {
      int id = nextRenderId++;
      ensureWidgetCapacity(id);
      return id;
   }

   private static void ensureWidgetCapacity(int id) {
      if (Widget.widgets == null) Widget.widgets = new Widget[Math.max(20000, id + 1)];
      else if (id >= Widget.widgets.length) Widget.widgets = Arrays.copyOf(Widget.widgets,
         Math.max(id + 1, Widget.widgets.length + 4096));
   }

   private static int renderId(int packed) {
      Integer id = PACKED_TO_RENDER.get(packed);
      return id == null ? allocateRenderId(packed) : id.intValue();
   }

   private static Node decodeIf1(int packed, byte[] data) throws IOException {
      Reader in = new Reader(data);
      int first = in.u8();
      if (first == 255) throw new IOException("IF3 component not yet supported: 0x" + Integer.toHexString(packed));
      Widget w = new Widget();
      w.type = first;
      w.optionType = in.u8();
      increment(TYPE_COUNTS, w.type);
      increment(OPTION_COUNTS, w.optionType);
      w.contentType = in.u16();
      int x = in.s16();
      int y = in.s16();
      w.width = in.u16();
      w.height = in.u16();
      if (w.type == 3) w.baseWidth = w.width;
      w.opacity = (byte)in.u8();
      int parent = in.u16();
      int parentPacked = parent == 65535 ? -1 : (packed & -65536) | parent;
      int hover = in.u16();
      w.hoverId = hover == 65535 ? -1 : renderId((packed & -65536) | hover);

      int comparisons = in.u8();
      if (comparisons > 0) {
         w.scriptCompareTypes = new int[comparisons];
         w.scriptCompareValues = new int[comparisons];
         for (int i = 0; i < comparisons; i++) { w.scriptCompareTypes[i] = in.u8(); w.scriptCompareValues[i] = in.u16(); }
      }
      int scripts = in.u8();
      if (scripts > 0) {
         w.valueIndexArray = new int[scripts][];
         for (int i = 0; i < scripts; i++) {
            int length = in.u16();
            w.valueIndexArray[i] = new int[length];
            for (int j = 0; j < length; j++) { int value = in.u16(); w.valueIndexArray[i][j] = value == 65535 ? -1 : value; }
         }
      }

      if (w.type == 0) { w.scrollMax = in.u16(); w.hoverOnly = in.u8() == 1; }
      if (w.type == 1) { in.u16(); in.u8(); }
      if (w.type == 2) decodeInventory(w, in);
      if (w.type == 3) w.filled = in.u8() == 1;
      if (w.type == 4 || w.type == 1) {
         w.textAlignment = in.u8();
         in.u8(); // vertical alignment
         in.u8(); // line height
         int font = in.u16();
         if (font != 65535) FONT_IDS.add(font);
         w.font = resolveFont(font);
         w.textShadow = in.u8() == 1;
      }
      if (w.type == 4) { w.message = in.string(); w.secondaryText = in.string(); }
      if (w.type == 1 || w.type == 3 || w.type == 4) w.textColor = in.i32();
      if (w.type == 3 || w.type == 4) { w.secondaryColor = in.i32(); w.defaultHoverColor = in.i32(); w.secondaryHoverColor = in.i32(); }
      if (w.type == 5) {
         int disabled = in.i32();
         int enabled = in.i32();
         w.disabledSprite = loadSprite(disabled);
         w.enabledSprite = loadSprite(enabled);
      }
      if (w.type == 6) decodeModel(w, in);
      if (w.type == 7) decodeTextInventory(w, in);
      if (w.type == 8) w.message = in.string();
      if (w.optionType == 2 || w.type == 2) { w.selectedActionName = in.string(); w.spellName = in.string(); w.spellUsableOn = in.u16(); }
      if (w.optionType == 1 || w.optionType == 4 || w.optionType == 5 || w.optionType == 6) {
         w.tooltip = in.string();
         if (w.tooltip.length() == 0) w.tooltip = w.optionType == 1 ? "Ok" : w.optionType == 6 ? "Continue" : "Select";
      }
      if (!in.done()) throw new IOException("trailing " + in.remaining() + " bytes in component 0x" + Integer.toHexString(packed));
      return new Node(packed, parentPacked, x, y, w);
   }

   private static void decodeInventory(Widget w, Reader in) throws IOException {
      int size = w.width * w.height;
      w.inventoryIds = new int[size]; w.inventoryAmounts = new int[size];
      w.swapItems = in.u8() == 1; w.inventoryInterface = in.u8() == 1; w.usableItems = in.u8() == 1; w.replaceItems = in.u8() == 1;
      w.spritePaddingX = in.u8(); w.spritePaddingY = in.u8();
      w.inventorySpriteX = new int[20]; w.inventorySpriteY = new int[20]; w.inventorySprites = new Sprite[20];
      for (int i = 0; i < 20; i++) if (in.u8() == 1) { w.inventorySpriteX[i] = in.s16(); w.inventorySpriteY[i] = in.s16(); w.inventorySprites[i] = loadSprite(in.i32()); }
      w.actions = new String[5]; for (int i = 0; i < 5; i++) { String s = in.string(); w.actions[i] = s.length() == 0 ? null : s; }
   }

   private static void decodeTextInventory(Widget w, Reader in) throws IOException {
      int size = w.width * w.height; w.inventoryIds = new int[size]; w.inventoryAmounts = new int[size];
      w.textAlignment = in.u8(); int font = in.u16();
      if (font != 65535) FONT_IDS.add(font);
      w.font = resolveFont(font);
      w.textShadow = in.u8() == 1; w.textColor = in.i32(); w.spritePaddingX = in.s16(); w.spritePaddingY = in.s16(); w.inventoryInterface = in.u8() == 1;
      w.actions = new String[5]; for (int i = 0; i < 5; i++) { String s = in.string(); w.actions[i] = s.length() == 0 ? null : s; }
   }

   private static void decodeModel(Widget w, Reader in) throws IOException {
      int value = in.u16();
      if (value != 65535) { w.defaultMediaType = 1; w.defaultMediaId = value; }
      int alternateModel = in.u16();
      if (alternateModel != 65535) alternateModelReferences++;
      value = in.u16(); w.defaultAnimationId = value == 65535 ? -1 : value;
      value = in.u16(); w.enabledAnimationId = value == 65535 ? -1 : value;
      w.modelZoom = in.u16(); w.modelRotation1 = in.u16(); w.modelRotation2 = in.u16();
   }

   private static Sprite loadSprite(int id) {
      if (id < 0 || cache == null) return null;
      SPRITE_IDS.add(id);
      try {
         Sprites.DecodedSprite[] sprites = Sprites.load(cache, id);
         if (sprites.length == 0) {
            MISSING_SPRITE_IDS.add(id);
            return null;
         }
         return sprites[0].toSprite();
      } catch (Exception ignored) {
         MISSING_SPRITE_IDS.add(id);
         return null;
      }
   }

   private static RichTextFont resolveFont(int id) {
      if (id == 65535) return null;
      return FONTS_BY_ARCHIVE_ID.get(id);
   }

   private static void increment(Map<Integer, Integer> counts, int key) {
      Integer old = counts.get(key);
      counts.put(key, old == null ? 1 : old.intValue() + 1);
   }

   private static final class Node {
      final int packed, parentPacked, x, y; final Widget widget;
      Node(int packed, int parentPacked, int x, int y, Widget widget) { this.packed = packed; this.parentPacked = parentPacked; this.x = x; this.y = y; this.widget = widget; }
   }

   private static final class Reader {
      final byte[] data; int p;
      Reader(byte[] data) { this.data = data; }
      int u8() throws IOException { need(1); return data[p++] & 255; }
      int u16() throws IOException { return u8() << 8 | u8(); }
      int s16() throws IOException { int v = u16(); return v > 32767 ? v - 65536 : v; }
      int i32() throws IOException { return u8() << 24 | u8() << 16 | u8() << 8 | u8(); }
      String string() throws IOException { int start = p; while (p < data.length && data[p] != 0) p++; if (p >= data.length) throw new IOException("unterminated string"); String s = new String(data, start, p - start, java.nio.charset.StandardCharsets.ISO_8859_1); p++; return s; }
      void need(int n) throws IOException { if (p + n > data.length) throw new IOException("truncated component"); }
      boolean done() { return p == data.length; } int remaining() { return data.length - p; }
   }
}
