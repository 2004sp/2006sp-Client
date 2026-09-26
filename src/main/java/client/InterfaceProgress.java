package client;

import java.io.IOException;

/** Paired-server extension for the custom flat Grand Exchange progress bars. */
final class InterfaceProgress {
   private InterfaceProgress() {}

   static boolean isGrandExchangeBar(int id) {
      return id == 19011 || id >= 19049 && id <= 19094 && (id - 19049) % 9 == 0;
   }

   static void apply(byte[] payload) throws IOException {
      if (payload.length != 3) throw new IOException("Invalid 443 progress packet length");
      int id = (payload[0] & 255) << 8 | payload[1] & 255;
      if (!isGrandExchangeBar(id)) throw new IOException("Invalid 443 progress widget " + id);
      if (Widget.widgets == null || id >= Widget.widgets.length || Widget.widgets[id] == null)
         throw new IOException("Missing 443 progress widget " + id);
      apply(Widget.widgets[id], payload[2] & 255);
   }

   static void apply(Widget widget, int value) {
      int percent = value == 250 ? 100 : Math.min(value, 100);
      widget.textColor = value == 250 ? Client.packRgb(138, 0, 16)
         : value < 100 ? Client.packRgb(198, 139, 1) : Client.packRgb(0, 95, 0);
      widget.width = (int)(widget.baseWidth * (percent / 100.0));
   }
}
