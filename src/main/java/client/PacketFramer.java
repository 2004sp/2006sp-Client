package client;

import java.io.EOFException;
import java.io.IOException;

/** Reads complete revision 443 game packets without invoking revision 377 handlers. */
final class PacketFramer {
    // https://github.com/RuneWiki/rs-deob/blob/628b4e3c502a737ccbd700117209f66e25a7bbc4/src/main/java/client.java
    // client.field475; -1 and -2 are length prefixes. Opcode 18 is a
    // paired-server extension for the retained flat Grand Exchange bars.
    private static final int[] LENGTHS = {
        0, 0, 0, 6, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0,
        0, 0, 3, 0, 0, 0, 10, 0, 6, -1, 0, 0, 0, -2, 0, 4,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 6, 0, 0, 0, 3, 0,
        0, 0, 0, 0, 0, 2, 0, 0, 2, 8, 6, 0, 0, -1, 0, 7,
        0, 5, 2, 0, 3, 0, 0, 1, 6, 0, 3, 3, 0, 0, 7, 6,
        6, 2, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 5, 6, 6,
        0, 0, 0, 6, 0, 2, 0, 0, 0, -2, 4, 0, 0, 0, 0, 0,
        0, 14, -1, 0, 0, 2, 0, 0, 0, 10, 0, 0, 2, 0, 0, 0,
        0, 0, 4, 10, 1, 2, 0, 0, 0, 0, 4, 5, 4, -1, 0, -2,
        2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0,
        0, 0, 0, 10, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        -2, -2, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, -2, 2, 0, 5,
        0, 0, 0, 0, 0, -2, 0, 0, 0, 0, 0, 2, 0, 4, 0, 0,
        0, 0, 1, 6, -2, 0, 2, 0, 8, 0, 5, 0, 1, 0, -2, 0,
        0, 6, 0, 0, -2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private int opcode = -1;
    private int length;

    static int lengthFor(int opcode) {
        return LENGTHS[opcode];
    }

    Packet poll(BufferedConnection connection, IsaacCipher cipher) throws IOException {
        while (true) {
            int available = connection.available();
            if (opcode == -1) {
                if (available < 1) {
                    return null;
                }
                int encoded = connection.read();
                if (encoded < 0) {
                    throw new EOFException("EOF reading revision 443 opcode");
                }
                opcode = cipher == null ? encoded : (encoded - cipher.nextInt()) & 255;
                length = LENGTHS[opcode];
                available--;
            }
            if (length == -1) {
                if (available < 1) {
                    return null;
                }
                length = connection.read();
                if (length < 0) {
                    throw new EOFException("EOF reading revision 443 byte length");
                }
                available--;
            } else if (length == -2) {
                if (available < 2) {
                    return null;
                }
                int high = connection.read();
                int low = connection.read();
                if (high < 0 || low < 0) {
                    throw new EOFException("EOF reading revision 443 short length");
                }
                length = (high << 8) | low;
                available -= 2;
            }
            if (available < length) {
                return null;
            }
            byte[] payload = new byte[length];
            connection.flushInputStream(payload, length);
            int packetOpcode = opcode;
            opcode = -1;
            return new Packet(packetOpcode, payload);
        }
    }

    static final class Packet {
        final int opcode;
        final byte[] payload;

        Packet(int opcode, byte[] payload) {
            this.opcode = opcode;
            this.payload = payload;
        }
    }
}
