package client;

final class CastleWarsOverlay {
   static final int CASTLE_WARS_WAITING_INTERFACE_ID = 6673;
   private static final int CASTLE_WARS_WAITING_TIMER_TEXT_ID = 6570;
   static final int CASTLE_WARS_GAME_INTERFACE_ID = 11344;
   private static final int CASTLE_WARS_ZAMORAK_SCORE_TEXT_ID = 11345;
   private static final int CASTLE_WARS_SARADOMIN_SCORE_TEXT_ID = 11346;
   private static final int CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID = 11349;
   private static final int CASTLE_WARS_SARADOMIN_FLAG_TEXT_ID = 11350;
   private static final int CASTLE_WARS_MAIN_DOOR_TEXT_ID = 11352;
   private static final int CASTLE_WARS_TIMER_TEXT_ID = 11353;
   private static final int CASTLE_WARS_SIDE_DOOR_TEXT_ID = 11356;
   private static final int CASTLE_WARS_TUNNEL_ONE_TEXT_ID = 11358;
   private static final int CASTLE_WARS_TUNNEL_TWO_TEXT_ID = 11360;
   private static final int CASTLE_WARS_CATAPULT_TEXT_ID = 11362;

   private int castleWarsCatapultAimX = 15;
   private int castleWarsCatapultAimY = 15;

   void drawCastleWarsWaitingOverlay(RichTextFont richBoldFont,
                                     int overlayWidth, int topRightReservedWidth) {
      String timer = this.getCastleWarsInterfaceText(CASTLE_WARS_WAITING_TIMER_TEXT_ID);
      if (timer.length() == 0) {
         return;
      }

      // All fixed gameframes render the 3D scene into the same 512x334 raster,
      // while every resizable frame renders into the full client raster. Draw
      // the waiting timer from those actual bounds instead of cache-authored
      // frame coordinates so 317, 459, 474, 474+orbs and OSRS resizable all
      // land in the same visible place.
      richBoldFont.drawCenteredString(timer,
            Math.max(160, overlayWidth - topRightReservedWidth) / 2,
            32, 16777215, 0);
   }

   private String getCastleWarsInterfaceText(int widgetId) {
      if (Widget.widgets == null || widgetId < 0 || widgetId >= Widget.widgets.length
            || Widget.widgets[widgetId] == null || Widget.widgets[widgetId].message == null) {
         return "";
      }
      return Widget.widgets[widgetId].message;
   }

   private int getCastleWarsStatusColor(String status) {
      if (status == null) {
         return 16777215;
      }

      String lower = status.toLowerCase();
      if (lower.contains("safe") || lower.contains("cleared")
            || lower.contains("operational") || lower.contains("locked")) {
         return 65280;
      }
      if (lower.contains("taken") || lower.contains("destroyed")
            || lower.contains("collapsed") || lower.equals("health 0%")) {
         return 16724787;
      }
      if (lower.contains("dropped") || lower.contains("unlocked")) {
         return 16776960;
      }
      if (lower.startsWith("health ")) {
         try {
            int percentIndex = lower.indexOf('%');
            int health = Integer.parseInt(lower.substring(7, percentIndex).trim());
            if (health <= 25) {
               return 16724787;
            }
            if (health <= 60) {
               return 16776960;
            }
            return 65280;
         } catch (Exception ignored) {
         }
      }
      return 16777215;
   }

   private int parseCastleWarsCatapultAim(String text, int fallback) {
      try {
         int value = Integer.parseInt(text == null ? "" : text.trim());
         return Math.max(0, Math.min(30, value));
      } catch (Exception ignored) {
         return fallback;
      }
   }

   void updateCatapultAimX(String text) {
      this.castleWarsCatapultAimX = this.parseCastleWarsCatapultAim(
            text, this.castleWarsCatapultAimX);
      Widget.updateCastleWarsCatapultAimMarker(
            this.castleWarsCatapultAimX, this.castleWarsCatapultAimY);
   }

   void updateCatapultAimY(String text) {
      this.castleWarsCatapultAimY = this.parseCastleWarsCatapultAim(
            text, this.castleWarsCatapultAimY);
      Widget.updateCastleWarsCatapultAimMarker(
            this.castleWarsCatapultAimX, this.castleWarsCatapultAimY);
   }

   void drawCastleWarsCatapultAimOverlay(BitmapFont boldFont) {
      if (Widget.widgets == null || 11169 >= Widget.widgets.length
            || Widget.widgets[11169] == null) {
         return;
      }

      Widget root = Widget.widgets[11169];
      int baseX = 0;
      int baseY = 0;
      if (Client.screenMode != 0 && Client.shouldCenterInterface(root)) {
         baseX = Client.clientWidth / 2 - 256;
         baseY = Client.clientHeight / 2 - 167;
      }

      this.drawCastleWarsCatapultCoordinate(
            boldFont, 11301, this.castleWarsCatapultAimX, baseX, baseY);
      this.drawCastleWarsCatapultCoordinate(
            boldFont, 11302, this.castleWarsCatapultAimY, baseX, baseY);
   }

   private void drawCastleWarsCatapultCoordinate(
         BitmapFont boldFont, int widgetId, int value, int baseX, int baseY) {
      if (widgetId < 0 || widgetId >= Widget.widgets.length
            || Widget.widgets[widgetId] == null) {
         return;
      }

      int[] position = this.findCastleWarsWidgetPosition(
            Widget.widgets[11169], widgetId, baseX, baseY, 0);
      if (position == null) {
         return;
      }

      Widget widget = Widget.widgets[widgetId];
      int width = widget.width > 0 ? widget.width : 86;
      int height = widget.height > 0 ? widget.height : 58;

      // The cache contains static sample digits (12 / 34). Cover only the
      // inside of their panel, keeping the native border, then draw the live
      // server-authoritative coordinate over it.
      int inset = 5;
      int drawWidth = Math.max(24, Math.min(92, width) - inset * 2);
      int drawHeight = Math.max(20, Math.min(62, height) - inset * 2);
      Rasterizer2D.fillRectangleAlternate(
            position[0] + inset, position[1] + inset,
            drawWidth, drawHeight, 0x8b7b59);

      String coordinate = value < 10 ? "0" + value : Integer.toString(value);
      int textX = position[0] + inset + drawWidth / 2;
      int textY = position[1] + inset + drawHeight / 2 + 5;
      boldFont.textCenter(0x302719, coordinate, textY, textX);
   }

   private int[] findCastleWarsWidgetPosition(int targetWidgetId) {
      if (Widget.widgets == null
            || CASTLE_WARS_GAME_INTERFACE_ID >= Widget.widgets.length) {
         return null;
      }
      return this.findCastleWarsWidgetPosition(
            Widget.widgets[CASTLE_WARS_GAME_INTERFACE_ID],
            targetWidgetId, 0, 0, 0);
   }

   private int[] findCastleWarsWidgetPosition(Widget parent, int targetWidgetId,
                                               int baseX, int baseY, int depth) {
      if (parent == null || parent.childIds == null
            || parent.childX == null || parent.childY == null || depth > 12) {
         return null;
      }

      for (int childIndex = 0; childIndex < parent.childIds.length; childIndex++) {
         int childId = parent.childIds[childIndex];
         if (childId < 0 || childId >= Widget.widgets.length) {
            continue;
         }

         Widget child = Widget.widgets[childId];
         if (child == null) {
            continue;
         }

         int childX = baseX + parent.childX[childIndex] + child.runtimeXOffset;
         int childY = baseY + parent.childY[childIndex]
               - parent.scrollPosition + child.runtimeYOffset;
         if (childId == targetWidgetId) {
            return new int[]{childX, childY};
         }

         if (child.type == 0) {
            int[] nested = this.findCastleWarsWidgetPosition(
                  child, targetWidgetId, childX, childY, depth + 1);
            if (nested != null) {
               return nested;
            }
         }
      }
      return null;
   }

   private void drawCastleWarsNativeIconLayer(Client client, int statusX, int statusY) {
      if (Widget.widgets == null
            || CASTLE_WARS_GAME_INTERFACE_ID >= Widget.widgets.length
            || CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID >= Widget.widgets.length) {
         return;
      }

      Widget root = Widget.widgets[CASTLE_WARS_GAME_INTERFACE_ID];
      Widget anchorText = Widget.widgets[CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID];
      int[] anchorPosition = this.findCastleWarsWidgetPosition(
            CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID);
      if (root == null || anchorText == null || anchorPosition == null) {
         return;
      }

      int lineHeight = anchorText.font == null ? 12 : anchorText.font.lineHeight;
      int desiredTextX = statusX + 32;
      int nativeTextBaselineY = anchorPosition[1] + lineHeight;
      int drawX = desiredTextX - anchorPosition[0];
      int drawY = statusY - nativeTextBaselineY;

      // Draw the original cache-authored Castle Wars graphics, but suppress
      // its text because the relocated overlay draws the status strings itself.
      // The native widgets still evaluate configs 377/378, so the cache chooses
      // the proper Safe/Taken/Dropped, door, tunnel and catapult artwork.
      int[] textWidgetIds = new int[]{
            CASTLE_WARS_ZAMORAK_SCORE_TEXT_ID,
            CASTLE_WARS_SARADOMIN_SCORE_TEXT_ID,
            CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID,
            CASTLE_WARS_SARADOMIN_FLAG_TEXT_ID,
            CASTLE_WARS_MAIN_DOOR_TEXT_ID,
            CASTLE_WARS_TIMER_TEXT_ID,
            CASTLE_WARS_SIDE_DOOR_TEXT_ID,
            CASTLE_WARS_TUNNEL_ONE_TEXT_ID,
            CASTLE_WARS_TUNNEL_TWO_TEXT_ID,
            CASTLE_WARS_CATAPULT_TEXT_ID,
            11363, 11364, 11365, 11366
      };
      String[] messages = new String[textWidgetIds.length];
      String[] secondaryTexts = new String[textWidgetIds.length];

      for (int i = 0; i < textWidgetIds.length; i++) {
         int widgetId = textWidgetIds[i];
         if (widgetId >= 0 && widgetId < Widget.widgets.length
               && Widget.widgets[widgetId] != null) {
            messages[i] = Widget.widgets[widgetId].message;
            secondaryTexts[i] = Widget.widgets[widgetId].secondaryText;
            Widget.widgets[widgetId].message = "";
            Widget.widgets[widgetId].secondaryText = "";
         }
      }

      try {
         client.drawCastleWarsWidgetLayer(root, drawX, drawY);
      } finally {
         for (int i = 0; i < textWidgetIds.length; i++) {
            int widgetId = textWidgetIds[i];
            if (widgetId >= 0 && widgetId < Widget.widgets.length
                  && Widget.widgets[widgetId] != null) {
               Widget.widgets[widgetId].message = messages[i];
               Widget.widgets[widgetId].secondaryText = secondaryTexts[i];
            }
         }
      }
   }

   private int getCastleWarsRelocatedTextBaselineY(int widgetId,
                                                   int anchorBaselineY) {
      if (Widget.widgets == null || widgetId < 0 || widgetId >= Widget.widgets.length
            || CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID >= Widget.widgets.length) {
         return anchorBaselineY;
      }

      Widget widget = Widget.widgets[widgetId];
      Widget anchor = Widget.widgets[CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID];
      int[] widgetPosition = this.findCastleWarsWidgetPosition(widgetId);
      int[] anchorPosition = this.findCastleWarsWidgetPosition(
            CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID);
      if (widget == null || anchor == null
            || widgetPosition == null || anchorPosition == null) {
         return anchorBaselineY;
      }

      int widgetLineHeight = widget.font == null ? 12 : widget.font.lineHeight;
      int anchorLineHeight = anchor.font == null ? 12 : anchor.font.lineHeight;
      int nativeWidgetBaselineY = widgetPosition[1] + widgetLineHeight;
      int nativeAnchorBaselineY = anchorPosition[1] + anchorLineHeight;
      return anchorBaselineY + nativeWidgetBaselineY - nativeAnchorBaselineY;
   }

   private void drawCastleWarsStatusLine(RichTextFont richPlainFont,
                                         RichTextFont richBoldFont,
                                         String label, int widgetId, int x, int y) {
      String status = this.getCastleWarsInterfaceText(widgetId);
      int textX = x + 32;
      richPlainFont.drawBasicString(label + ":", textX, y, 16777215, 0);
      int statusTextX = textX + richPlainFont.getTextWidth(label + ":") + 6;
      richBoldFont.drawBasicString(status, statusTextX, y,
            this.getCastleWarsStatusColor(status), 0);
   }

   void drawCastleWarsGameOverlay(Client client, RichTextFont richPlainFont,
                                  RichTextFont richBoldFont, int overlayWidth,
                                  int overlayHeight, int topRightReservedWidth,
                                  int bottomReservedHeight) {
      String zamorakScore = this.getCastleWarsInterfaceText(
            CASTLE_WARS_ZAMORAK_SCORE_TEXT_ID);
      String saradominScore = this.getCastleWarsInterfaceText(
            CASTLE_WARS_SARADOMIN_SCORE_TEXT_ID);
      String timer = this.getCastleWarsInterfaceText(CASTLE_WARS_TIMER_TEXT_ID);

      // Fixed 317/459/474 all use the same 512x334 scene raster. Resizable
      // 474/474+orbs/OSRS use the full client raster, so reserve the scaled
      // minimap area before centering top-of-screen Castle Wars information.
      int safeTopRight = Math.max(160, overlayWidth - topRightReservedWidth);
      int scoreX = safeTopRight / 2;
      richBoldFont.drawCenteredString(
            zamorakScore + "     " + saradominScore,
            scoreX, 32, 16777215, 0);

      // Keep the objective/status stack inside the visible scene even at high
      // UI scales. The chatbox can cover the bottom-left of resizable frames,
      // so clamp the cache-derived status block above that reserved region.
      int statusX = 10;
      int lastStatusOffset = this.getCastleWarsRelocatedTextBaselineY(
            CASTLE_WARS_CATAPULT_TEXT_ID, 0);
      int statusBlockHeight = Math.max(112, lastStatusOffset + 18);
      int safeBottom = overlayHeight - bottomReservedHeight - 8;
      int desiredStatusY = Math.max(120, overlayHeight / 2 - 100);
      int maxStatusY = Math.max(64, safeBottom - statusBlockHeight);
      int statusY = Math.min(desiredStatusY, maxStatusY);

      this.drawCastleWarsNativeIconLayer(client, statusX, statusY);
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Zamorak flag",
            CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_ZAMORAK_FLAG_TEXT_ID, statusY));
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Saradomin flag",
            CASTLE_WARS_SARADOMIN_FLAG_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_SARADOMIN_FLAG_TEXT_ID, statusY));
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Main door",
            CASTLE_WARS_MAIN_DOOR_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_MAIN_DOOR_TEXT_ID, statusY));
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Side door",
            CASTLE_WARS_SIDE_DOOR_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_SIDE_DOOR_TEXT_ID, statusY));
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Tunnel 1",
            CASTLE_WARS_TUNNEL_ONE_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_TUNNEL_ONE_TEXT_ID, statusY));
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Tunnel 2",
            CASTLE_WARS_TUNNEL_TWO_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_TUNNEL_TWO_TEXT_ID, statusY));
      this.drawCastleWarsStatusLine(richPlainFont, richBoldFont, "Catapult",
            CASTLE_WARS_CATAPULT_TEXT_ID, statusX,
            this.getCastleWarsRelocatedTextBaselineY(
                  CASTLE_WARS_CATAPULT_TEXT_ID, statusY));

      // The minimap is outside the scene raster in every fixed frame, but is
      // overlaid on the scene in every resizable frame. Anchoring the clock to
      // the safe top-right edge keeps it clear of 317-style, 474+orbs and OSRS
      // minimaps without maintaining a separate coordinate table per frame.
      int timerX = Math.max(80, safeTopRight - 42);
      richBoldFont.drawCenteredString(timer, timerX, 50, 16777215, 0);
   }

}
