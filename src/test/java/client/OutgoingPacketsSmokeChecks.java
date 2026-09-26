package client;

import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Wire checks for translation, packet boundaries and ISAAC alignment. */
public final class OutgoingPacketsSmokeChecks {
   public static void main(String[] args) {
      System.setProperty("prs.clientRevision", "443");
      byte[] table = new byte[256];
      Arrays.fill(table, (byte)8);
      HuffmanChatCodec.initialize(table);
      check(WidgetIds.packed(3214) == 149 << 16,
         "inventory widget bridge");
      check(WidgetIds.legacyComponent(149 << 16) == 3214,
         "inventory reverse widget bridge");
      check(WidgetIds.packed(5382) == (12 << 16 | 89), "bank container bridge");
      check(WidgetIds.packed(5383) == (12 << 16 | 90), "bank title bridge");
      check(WidgetIds.packed(5385) == (12 << 16 | 91), "bank scroll bridge");
      check(WidgetIds.packed(5064) == 15 << 16, "bank inventory bridge");
      check(WidgetIds.packed(3900) == (300 << 16 | 75), "shop stock bridge");
      check(WidgetIds.packed(3823) == 301 << 16, "shop inventory bridge");
      check(WidgetIds.packed(8144) == (275 << 16 | 2), "quest title bridge");
      check(WidgetIds.packed(8145) == (275 << 16 | 3), "quest first line bridge");
      check(WidgetIds.packed(8195) == (275 << 16 | 53), "quest first range end");
      check(WidgetIds.packed(12174) == (275 << 16 | 54), "quest second range start");
      check(WidgetIds.packed(12223) == (275 << 16 | 103), "quest second range end");
      check(WidgetIds.packed(3322) == 336 << 16, "trade inventory bridge");
      check(WidgetIds.packed(3417) == (335 << 16 | 93), "trade partner bridge");
      check(WidgetIds.packed(352) == (90 << 16 | 105), "staff spell label bridge");
      check(WidgetIds.packed(1830) == (319 << 16), "standard spell picker bridge");
      check(WidgetIds.packed(1845) == (319 << 16 | 15), "standard spell picker end");
      check(WidgetIds.packed(13178) == (388 << 16 | 82), "ancient spell picker bridge");
      check(WidgetIds.packed(1734) == (332 << 16 | 98), "enchanting bridge");
      check(WidgetIds.packed(2405) == (311 << 16 | 4), "smelting bridge");
      check(WidgetIds.packed(4226) == (446 << 16 | 64), "jewellery crafting bridge");
      check(WidgetIds.packed(8717) == (308 << 16 | 2), "skill guide scroll bridge");
      check(WidgetIds.packed(15948) == (197 << 16 | 3), "minigame reward inventory bridge");
      check(WidgetIds.packed(3431) == (335 << 16 | 107), "trade waiting text bridge");
      check(WidgetIds.packed(6571) == (106 << 16 | 133), "duel waiting text bridge");
      check(WidgetIds.legacyInterface(19018) == 19018,
         "custom Grand Exchange progress interface");
      check(WidgetIds.packed(18984) == 18984,
         "custom Grand Exchange widget passthrough");
      Widget.widgets = new Widget[4000];
      Widget.widgets[3213] = new Widget();
      Widget.widgets[3213].id = 3213;
      Widget.widgets[3213].parentId = -1;
      Widget.widgets[3214] = new Widget();
      Widget.widgets[3214].id = 3214;
      Widget.widgets[3214].parentId = 3213;
      check(WidgetIds.legacyInterface(149) == 3213,
         "inventory interface-group bridge");
      checkLegacyWriterCoverage();

      int[] seed = { 1, 2, 3, 4 };
      Buffer buffer = new Buffer(new byte[1024]);
      buffer.isaacCipher = new IsaacCipher(seed);
      // This legacy anti-noise packet has no 443 counterpart. It must not
      // advance the outbound ISAAC stream seen by the server.
      buffer.writeOpcode(136);
      buffer.writeOpcode(164);
      buffer.writeByte(5);
      buffer.writeShortLittleEndianAdded(3200);
      buffer.writeShortLittleEndian(3201);
      buffer.writeByteNegated(1);
      buffer.writeOpcode(132);
      buffer.writeShortLittleEndianAdded(3202);
      buffer.writeShort(127);
      buffer.writeShortAdded(3203);
      byte[] wire = OutgoingPackets.translate(buffer, 0, 3214);
      IsaacCipher serverCipher = new IsaacCipher(seed);
      int walkOpcode = (wire[0] - serverCipher.nextInt()) & 255;
      check(walkOpcode == 99, "walk opcode");
      check(wire[1] == 5, "walk length");
      check((wire[4] & 255) == 255, "run byte before Y");
      int objectOpcode = (wire[7] - serverCipher.nextInt()) & 255;
      check(objectOpcode == 47, "object option and ISAAC alignment");
      check(wire.length == 14, "wire length");

      buffer.currentPosition = 0;
      buffer.writeOpcode(103);
      buffer.writeByte(5);
      buffer.writeString("test");
      wire = OutgoingPackets.translate(buffer, 0, 3214);
      int commandOpcode = (wire[0] - serverCipher.nextInt()) & 255;
      check(commandOpcode == 174 && wire[1] == 5 && wire[6] == 0,
         "command framing and NUL terminator");

      buffer.currentPosition = 0;
      buffer.writeOpcode(155);
      buffer.writeShortLittleEndian(123);
      buffer.writeOpcode(4);
      buffer.writeByte(0);
      int lengthStart = buffer.currentPosition;
      buffer.writeByteSubtracted(2);
      buffer.writeByteSubtracted(3);
      Buffer chat = new Buffer(new byte[100]);
      ChatCodec.encode("Hi", chat);
      buffer.writeBytesReversedAdded(0, chat.buffer, chat.currentPosition);
      buffer.writeLengthByte(buffer.currentPosition - lengthStart);
      wire = OutgoingPackets.translate(buffer, 0, 3214);
      check(((wire[0] - serverCipher.nextInt()) & 255) == 154, "NPC option");
      check(wire[1] == 0 && (wire[2] & 255) == 123, "NPC index");
      check(((wire[3] - serverCipher.nextInt()) & 255) == 4, "public chat opcode");
      check((wire[5] & 255) == 3 && (wire[6] & 255) == 2, "chat color and effect");
      byte[] packed = Arrays.copyOfRange(wire, 7, wire.length);
      check(HuffmanChatCodec.get().decode(packed).trim().equals("Hi"), "chat wordpack");
      System.out.println("Revision 443 outgoing packet smoke checks passed.");
   }

   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   private static void checkLegacyWriterCoverage() {
      try {
         String client = new String(Files.readAllBytes(Paths.get("src/main/java/client/Client.java")),
            StandardCharsets.UTF_8);
         String translator = new String(Files.readAllBytes(
            Paths.get("src/main/java/client/OutgoingPackets.java")), StandardCharsets.UTF_8);
         Set<Integer> handled = new HashSet<Integer>();
         Matcher cases = Pattern.compile("case\\s+(\\d+)\\s*:").matcher(
            translator.substring(translator.indexOf("private static void encode("),
               translator.indexOf("private static void movement(")));
         while (cases.find()) handled.add(Integer.parseInt(cases.group(1)));
         Matcher writers = Pattern.compile("writeOpcode\\((\\d+)\\)").matcher(client);
         int count = 0;
         while (writers.find()) {
            int opcode = Integer.parseInt(writers.group(1));
            check(handled.contains(opcode), "untranslated legacy writer " + opcode);
            count++;
         }
         check(count > 80, "legacy writer audit did not inspect the client source");
      } catch (java.io.IOException exception) {
         throw new AssertionError("could not audit legacy writers", exception);
      }
   }
}
