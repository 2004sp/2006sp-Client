package client;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

/** Converts the existing action writers at the network boundary. Packet boundaries
 * are recorded by Buffer.writeOpcode, so an unsupported legacy packet cannot
 * desynchronise the 443 stream. */
final class OutgoingPackets {
   private static final Set<Integer> REPORTED = new HashSet<Integer>();

   private OutgoingPackets() {
   }

   static byte[] translate(Buffer source, int selectedItemId, int selectedItemWidgetId) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream(source.currentPosition);
      for (int i = 0; i < source.outgoingPacketCount; i++) {
         int start = source.outgoingPacketStarts[i];
         int end = i + 1 < source.outgoingPacketCount
            ? source.outgoingPacketStarts[i + 1] : source.currentPosition;
         if (start < 0 || end <= start || end > source.currentPosition) continue;
         int oldOpcode = source.outgoingPacketOpcodes[i];
         Writer packet = new Writer();
         try {
            encode(oldOpcode, new Reader(source.buffer, start + 1, end), packet,
               selectedItemId, selectedItemWidgetId);
            if (packet.opcode < 0) continue;
            // Advance ISAAC only for packets actually sent to the 443 server.
            stream.write((packet.opcode + source.isaacCipher.nextInt()) & 255);
            byte[] payload = packet.bytes.toByteArray();
            stream.write(payload, 0, payload.length);
         } catch (RuntimeException exception) {
            if (REPORTED.add(oldOpcode)) {
               System.err.println("Skipping legacy packet " + oldOpcode
                  + " while encoding revision 443: " + exception.getMessage());
            }
         }
      }
      source.outgoingPacketCount = 0;
      return stream.toByteArray();
   }

   private static void encode(int opcode, Reader r, Writer w,
                              int selectedItemId, int selectedItemWidgetId) {
      switch (opcode) {
         case 164: case 248: case 98: movement(opcode, r, w); return;
         case 132: case 252: case 70: case 234: case 228: object(opcode, r, w); return;
         case 155: case 72: case 17: case 21: case 18: npc(opcode, r, w); return;
         case 128: case 153: case 73: case 139: case 39: player(opcode, r, w); return;
         case 156: case 23: case 236: case 253: case 79: groundItem(opcode, r, w); return;
         case 192: itemOnObject(r, w); return;
         case 57: itemOnNpc(r, w); return;
         case 14: itemOnPlayer(r, w, selectedItemId, selectedItemWidgetId); return;
         case 25: itemOnGround(r, w); return;
         case 103: command(r, w); return;
         case 4: publicChat(r, w); return;
         case 126: privateMessage(r, w); return;
         case 188: case 215: case 133: case 74: social(opcode, r, w); return;
         case 0: w.opcode = 86; r.expectEnd(); return;
         case 202: w.opcode = 192; r.expectEnd(); return;
         case 130: w.opcode = 70; r.expectEnd(); return;
         case 121: w.opcode = 21; r.expectEnd(); return;
         case 3: w.opcode = 207; copy(r, w, 1); return;
         case 241: w.opcode = 162; copy(r, w, 4); return;
         case 45: w.opcode = 133; variableCopy(r, w); return;
         case 86: camera(r, w); return;
         case 101: w.opcode = 118; copy(r, w, 13); return;
         case 218: w.opcode = 119; copy(r, w, 10); return;
         case 120: w.opcode = 65; copy(r, w, 1); return;
         case 152:
            if (r.remaining() == 1 && (r.bytes[r.at] & 255) != 88) {
               w.opcode = 65; copy(r, w, 1);
            }
            return;
         case 40: button(r, w); return;
         case 222: widgetAction(r, w); return;
         case 19: return;
         // 377 anti-cheat noise and client-only reports have no 443 equivalent.
         case 11: case 36: case 77: case 78: case 85: case 136: case 148:
         case 150: case 165: case 183: case 189: case 200: case 210:
         case 226: case 230: case 238: case 246: return;
         case 95: w.opcode = 76; w.bytes.write(r.u8()); w.bytes.write(r.u8()); w.bytes.write(r.u8()); r.expectEnd(); return;
         case 185: button(r, w); return;
         case 122: case 41: case 16: case 75: case 87:
            inventoryItem(opcode, r, w); return;
         case 145: case 117: case 43: case 129: case 135:
            widgetItem(opcode, r, w); return;
         case 53: itemOnItem(r, w); return;
         case 214: widgetDrag(r, w); return;
         case 208: w.opcode = 74; copy(r, w, 4); return;
         case 60: w.opcode = 22; copy(r, w, 8); return;
         case 35: spellOnObject(r, w); return;
         case 131: spellOnNpc(r, w); return;
         case 249: spellOnPlayer(r, w); return;
         case 181: spellOnGround(r, w); return;
         case 237: spellOnItem(r, w); return;
         default:
            throw new IllegalArgumentException("unknown legacy outgoing opcode " + opcode);
      }
   }

   private static void movement(int opcode, Reader r, Writer w) {
      int length = r.u8();
      int suffix = opcode == 248 ? 14 : 0;
      if (length != r.remaining() || length < 5 + suffix || ((length - 5 - suffix) & 1) != 0)
         throw new IllegalArgumentException("movement length " + length);
      w.opcode = opcode == 164 ? 99 : opcode == 248 ? 80 : 81;
      w.u8(length);
      int x = r.leAdd();
      int steps = (length - 5 - suffix) / 2;
      byte[] dx = new byte[steps], dy = new byte[steps];
      for (int i = 0; i < steps; i++) { dx[i] = (byte)r.u8(); dy[i] = (byte)r.u8(); }
      int y = r.le();
      int run = r.u8();
      w.leAdd(x); w.u8(run); w.le(y);
      for (int i = 0; i < steps; i++) { w.u8(dx[i]); w.u8(128 - dy[i]); }
      for (int i = 0; i < suffix; i++) w.u8(r.u8());
      r.expectEnd();
   }

   private static void object(int opcode, Reader r, Writer w) {
      int id, x, y;
      switch (opcode) {
         case 132: x = r.leAdd(); id = r.be(); y = r.beAdd(); w.opcode = 47; break;
         case 252: id = r.leAdd(); y = r.le(); x = r.beAdd(); w.opcode = 245; break;
         case 70: x = r.le(); y = r.be(); id = r.leAdd(); w.opcode = 69; break;
         case 234: x = r.leAdd(); id = r.beAdd(); y = r.leAdd(); w.opcode = 202; break;
         default: id = r.beAdd(); y = r.beAdd(); x = r.be(); w.opcode = 120; break;
      }
      r.expectEnd();
      switch (w.opcode) {
         case 47: w.beAdd(y); w.leAdd(id); w.beAdd(x); break;
         case 245: w.be(x); w.be(id); w.beAdd(y); break;
         case 69: w.le(id); w.be(y); w.be(x); break;
         case 202: w.be(id); w.beAdd(x); w.leAdd(y); break;
         default: w.be(y); w.be(x); w.be(id); break;
      }
   }

   private static void npc(int opcode, Reader r, Writer w) {
      int index;
      switch (opcode) {
         case 155: index = r.le(); w.opcode = 154; break;
         case 72: index = r.beAdd(); w.opcode = 224; break;
         case 17: index = r.leAdd(); w.opcode = 89; break;
         case 21: index = r.be(); w.opcode = 222; break;
         default: index = r.le(); w.opcode = 87; break;
      }
      r.expectEnd();
      if (w.opcode == 89 || w.opcode == 222) w.leAdd(index); else w.be(index);
   }

   private static void player(int opcode, Reader r, Writer w) {
      int index = opcode == 128 ? r.be() : r.le();
      r.expectEnd();
      switch (opcode) {
         case 128: w.opcode = 11; break;
         case 153: w.opcode = 169; break;
         case 73: w.opcode = 229; break;
         case 139: w.opcode = 101; break;
         default: w.opcode = 206; break;
      }
      if (w.opcode == 206) w.be(index);
      else if (w.opcode == 229) w.beAdd(index);
      else w.leAdd(index);
   }

   private static void groundItem(int opcode, Reader r, Writer w) {
      int id, x, y;
      switch (opcode) {
         case 156: x = r.beAdd(); y = r.le(); id = r.leAdd(); w.opcode = 149; break;
         case 23: y = r.le(); id = r.le(); x = r.le(); w.opcode = 252; break;
         case 236: y = r.le(); id = r.be(); x = r.le(); w.opcode = 85; break;
         case 253: x = r.le(); y = r.leAdd(); id = r.beAdd(); w.opcode = 38; break;
         default: y = r.le(); id = r.be(); x = r.beAdd(); w.opcode = 136; break;
      }
      r.expectEnd();
      switch (w.opcode) {
         case 149: w.leAdd(x); w.leAdd(y); w.be(id); break;
         case 252: w.leAdd(id); w.leAdd(x); w.leAdd(y); break;
         case 85: w.beAdd(y); w.leAdd(x); w.leAdd(id); break;
         case 38: w.le(id); w.beAdd(y); w.be(x); break;
         default: w.beAdd(x); w.le(id); w.be(y); break;
      }
   }

   private static void itemOnObject(Reader r, Writer w) {
      int widget = r.be(), id = r.le(), y = r.leAdd(), slot = r.le();
      int x = r.leAdd(), item = r.be(); r.expectEnd();
      w.opcode = 195;
      w.leAdd(y); w.le(id); w.beAdd(item); w.le(slot); w.leAdd(x);
      w.leInt(WidgetIds.packed(widget));
   }

   private static void itemOnNpc(Reader r, Writer w) {
      int item = r.beAdd(), npc = r.beAdd(), slot = r.le(), widget = r.beAdd();
      r.expectEnd(); w.opcode = 146;
      w.middleInt(WidgetIds.packed(widget));
      w.be(item); w.be(slot); w.leAdd(npc);
   }

   private static void itemOnPlayer(Reader r, Writer w, int selectedItemId, int selectedItemWidgetId) {
      int player = r.be(), slot = r.le(); r.expectEnd();
      w.opcode = 104; w.inverseMiddleInt(WidgetIds.packed(selectedItemWidgetId));
      w.leAdd(slot); w.le(player); w.leAdd(selectedItemId);
   }

   private static void itemOnGround(Reader r, Writer w) {
      int widget = r.le(), item = r.beAdd(), id = r.be(), y = r.beAdd();
      int slot = r.leAdd(), x = r.be(); r.expectEnd(); w.opcode = 114;
      w.le(item); w.beAdd(y); w.beInt(WidgetIds.packed(widget));
      w.beAdd(x); w.le(id); w.leAdd(slot);
   }

   private static void social(int opcode, Reader r, Writer w) {
      if (r.remaining() != 8) throw new IllegalArgumentException("social packet length");
      w.opcode = opcode == 188 ? 90 : opcode == 215 ? 159 : opcode == 133 ? 198 : 250;
      while (r.remaining() > 0) w.u8(r.u8());
   }

   private static void copy(Reader r, Writer w, int length) {
      if (r.remaining() != length) throw new IllegalArgumentException("packet length");
      while (r.remaining() > 0) w.u8(r.u8());
   }

   private static void variableCopy(Reader r, Writer w) {
      int length = r.u8();
      if (length != r.remaining()) throw new IllegalArgumentException("variable packet length");
      w.u8(length);
      while (r.remaining() > 0) w.u8(r.u8());
   }

   private static void widgetAction(Reader r, Writer w) {
      int widget = r.be();
      int operation = r.u8();
      r.expectEnd();
      int packed = WidgetIds.packed(widget);
      if (packed == -1) return;
      if (operation >= 0 && operation < 10) {
         int[] opcodes = { 13, 217, 161, 3, 40, 73, 57, 167, 28, 8 };
         w.opcode = opcodes[operation]; w.beInt(packed); w.be(0);
      } else {
         w.opcode = 54; w.beInt(packed);
      }
   }

   private static void camera(Reader r, Writer w) {
      int pitch = r.be(), yaw = r.beAdd(); r.expectEnd();
      w.opcode = 66; w.beAdd(pitch); w.be(yaw);
   }

   private static void widgetDrag(Reader r, Writer w) {
      int sourceWidget = r.leAdd(), mode = -r.u8() & 255;
      int sourceSlot = r.leAdd(), targetSlot = r.le(), targetWidget = r.leAdd();
      r.expectEnd();
      int sourcePacked = WidgetIds.packed(sourceWidget);
      int targetPacked = WidgetIds.packed(targetWidget);
      if (sourcePacked == -1 || targetPacked == -1) return;
      if (sourceWidget == targetWidget) {
         w.opcode = 190; w.be(sourceSlot); w.beInt(sourcePacked);
         w.u8(mode); w.be(targetSlot);
      } else {
         w.opcode = 46; w.leAdd(targetSlot); w.leAdd(sourceSlot);
         w.leInt(targetPacked); w.inverseMiddleInt(sourcePacked);
      }
   }

   private static void command(Reader r, Writer w) {
      int length = r.u8();
      if (length != r.remaining() || length < 1) throw new IllegalArgumentException("command length");
      w.opcode = 174;
      w.u8(length);
      while (r.remaining() > 1) w.u8(r.u8());
      r.u8(); w.u8(0);
   }

   private static void publicChat(Reader r, Writer w) {
      int length = r.u8();
      if (length != r.remaining() || length < 2) throw new IllegalArgumentException("chat length");
      int effect = 128 - r.u8() & 255;
      int color = 128 - r.u8() & 255;
      byte[] old = new byte[length - 2];
      for (int i = old.length - 1; i >= 0; i--) old[i] = (byte)(r.u8() - 128);
      byte[] packed = HuffmanChatCodec.get().encode(ChatCodec.decode(old.length, new Buffer(old)));
      if (packed.length + 2 > 255) throw new IllegalArgumentException("chat exceeds 443 byte length");
      w.opcode = 4; w.u8(packed.length + 2); w.u8(color); w.u8(effect);
      for (byte value : packed) w.u8(value);
   }

   private static void privateMessage(Reader r, Writer w) {
      int length = r.u8();
      if (length != r.remaining() || length < 8) throw new IllegalArgumentException("private length");
      byte[] recipient = new byte[8];
      for (int i = 0; i < 8; i++) recipient[i] = (byte)r.u8();
      byte[] old = new byte[length - 8];
      for (int i = 0; i < old.length; i++) old[i] = (byte)r.u8();
      byte[] packed = HuffmanChatCodec.get().encode(ChatCodec.decode(old.length, new Buffer(old)));
      if (packed.length + 8 > 255) throw new IllegalArgumentException("private message exceeds 443 byte length");
      w.opcode = 50; w.u8(packed.length + 8);
      for (byte value : recipient) w.u8(value);
      for (byte value : packed) w.u8(value);
   }

   private static void button(Reader r, Writer w) {
      int id = r.be(); r.expectEnd();
      int packed = WidgetIds.packed(id);
      if (packed == -1) return;
      w.opcode = 54; w.beInt(packed);
   }

   private static void inventoryItem(int opcode, Reader r, Writer w) {
      int widget, slot, item;
      switch (opcode) {
         case 122: widget = r.leAdd(); slot = r.beAdd(); item = r.le(); w.opcode = 0; break;
         case 41: item = r.be(); slot = r.beAdd(); widget = r.beAdd(); w.opcode = 29; break;
         case 16: item = r.beAdd(); slot = r.leAdd(); widget = r.leAdd(); w.opcode = 48; break;
         case 75: widget = r.leAdd(); slot = r.le(); item = r.beAdd(); w.opcode = 182; break;
         default: item = r.beAdd(); widget = r.be(); slot = r.beAdd(); w.opcode = 178; break;
      }
      r.expectEnd();
      int packed = WidgetIds.packed(widget);
      if (packed == -1) { w.opcode = -1; return; }
      switch (w.opcode) {
         case 0: w.le(slot); w.be(item); w.leInt(packed); break;
         case 29: w.middleInt(packed); w.be(item); w.beAdd(slot); break;
         case 48: w.beAdd(slot); w.middleInt(packed); w.le(item); break;
         case 182: w.le(item); w.be(slot); w.leInt(packed); break;
         default: w.leAdd(slot); w.beAdd(item); w.middleInt(packed); break;
      }
   }

   private static void widgetItem(int opcode, Reader r, Writer w) {
      int widget, slot, item;
      switch (opcode) {
         case 145: widget = r.beAdd(); slot = r.beAdd(); item = r.beAdd(); w.opcode = 144; break;
         case 117: widget = r.leAdd(); item = r.leAdd(); slot = r.le(); w.opcode = 113; break;
         case 43: widget = r.le(); item = r.beAdd(); slot = r.beAdd(); w.opcode = 188; break;
         case 129: slot = r.beAdd(); widget = r.be(); item = r.beAdd(); w.opcode = 221; break;
         default: slot = r.le(); widget = r.beAdd(); item = r.le(); w.opcode = 171; break;
      }
      r.expectEnd();
      int packed = WidgetIds.packed(widget);
      if (packed == -1) { w.opcode = -1; return; }
      switch (w.opcode) {
         case 144: w.leAdd(slot); w.beInt(packed); w.le(item); break;
         case 113: w.inverseMiddleInt(packed); w.le(item); w.leAdd(slot); break;
         case 188: w.leAdd(slot); w.leAdd(item); w.middleInt(packed); break;
         case 221: w.inverseMiddleInt(packed); w.leAdd(slot); w.be(item); break;
         default: w.beAdd(slot); w.beInt(packed); w.le(item); break;
      }
   }

   private static void itemOnItem(Reader r, Writer w) {
      int targetSlot = r.be(), selectedSlot = r.beAdd(), targetItem = r.leAdd();
      int selectedWidget = r.be(), selectedItem = r.le(), targetWidget = r.be();
      r.expectEnd();
      int targetPacked = WidgetIds.packed(targetWidget);
      int selectedPacked = WidgetIds.packed(selectedWidget);
      if (targetPacked == -1 || selectedPacked == -1) return;
      w.opcode = 147; w.beAdd(selectedSlot); w.le(selectedItem); w.le(targetItem);
      w.leInt(targetPacked); w.inverseMiddleInt(selectedPacked); w.be(targetSlot);
   }

   private static int spellWidget(int oldId) {
      int packed = WidgetIds.packed(oldId);
      if (packed == -1) throw new IllegalArgumentException("unmapped spell widget " + oldId);
      return packed;
   }

   private static void spellOnObject(Reader r, Writer w) {
      int x = r.le(), spell = r.beAdd(), y = r.beAdd(), object = r.le();
      r.expectEnd(); w.opcode = 78; w.inverseMiddleInt(spellWidget(spell));
      w.be(x); w.be(spell); w.le(y); w.beAdd(object);
   }

   private static void spellOnNpc(Reader r, Writer w) {
      int npc = r.leAdd(), spell = r.beAdd(); r.expectEnd();
      w.opcode = 200; w.le(spell); w.leInt(spellWidget(spell)); w.be(npc);
   }

   private static void spellOnPlayer(Reader r, Writer w) {
      int player = r.beAdd(), spell = r.le(); r.expectEnd();
      w.opcode = 236; w.beAdd(player); w.beAdd(spell); w.leInt(spellWidget(spell));
   }

   private static void spellOnGround(Reader r, Writer w) {
      int y = r.le(), item = r.be(), x = r.le(), spell = r.beAdd();
      r.expectEnd(); w.opcode = 64; w.leAdd(item);
      w.inverseMiddleInt(spellWidget(spell)); w.be(y); w.leAdd(x); w.leAdd(spell);
   }

   private static void spellOnItem(Reader r, Writer w) {
      int slot = r.be(), item = r.beAdd(), widget = r.be(), spell = r.beAdd();
      r.expectEnd(); w.opcode = 243;
      w.beInt(WidgetIds.packed(widget)); w.beAdd(slot);
      w.inverseMiddleInt(spellWidget(spell)); w.leAdd(item); w.le(spell);
   }

   private static final class Reader {
      final byte[] bytes;
      final int end;
      int at;
      Reader(byte[] bytes, int at, int end) { this.bytes = bytes; this.at = at; this.end = end; }
      int remaining() { return end - at; }
      int u8() { if (at >= end) throw new IllegalArgumentException("truncated payload"); return bytes[at++] & 255; }
      int be() { return u8() << 8 | u8(); }
      int le() { int a = u8(); return a | u8() << 8; }
      int beAdd() { return u8() << 8 | (u8() - 128 & 255); }
      int leAdd() { int a = u8() - 128 & 255; return a | u8() << 8; }
      void expectEnd() { if (at != end) throw new IllegalArgumentException("unexpected payload bytes"); }
   }

   private static final class Writer {
      int opcode = -1;
      final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      void u8(int value) { bytes.write(value & 255); }
      void be(int value) { u8(value >>> 8); u8(value); }
      void le(int value) { u8(value); u8(value >>> 8); }
      void beAdd(int value) { u8(value >>> 8); u8(value + 128); }
      void leAdd(int value) { u8(value + 128); u8(value >>> 8); }
      void beInt(int value) { u8(value >>> 24); u8(value >>> 16); u8(value >>> 8); u8(value); }
      void leInt(int value) { u8(value); u8(value >>> 8); u8(value >>> 16); u8(value >>> 24); }
      void middleInt(int value) { u8(value >>> 8); u8(value); u8(value >>> 24); u8(value >>> 16); }
      void inverseMiddleInt(int value) { u8(value >>> 16); u8(value >>> 24); u8(value); u8(value >>> 8); }
   }
}
