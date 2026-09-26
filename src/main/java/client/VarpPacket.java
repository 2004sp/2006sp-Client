package client;

import java.io.IOException;

/** Decodes revision 443 varp updates. */
public final class VarpPacket {
    public static final int SMALL_OPCODE = 62;
    public static final int LARGE_OPCODE = 74;

    private VarpPacket() {
    }

    public static boolean handles(int opcode) {
        return payloadLength(opcode) >= 0;
    }

    static int payloadLength(int opcode) {
        if (opcode == SMALL_OPCODE) return 3;
        if (opcode == LARGE_OPCODE) return 6;
        return -1;
    }

    public static Update decode(int opcode, byte[] payload) throws IOException {
        int expectedLength = payloadLength(opcode);
        if (expectedLength < 0) {
            throw new IOException("Unsupported revision 443 varp opcode " + opcode);
        }
        if (payload == null || payload.length != expectedLength) {
            throw new IOException("Revision 443 varp opcode " + opcode + " must be "
                    + expectedLength + " bytes");
        }

        if (opcode == SMALL_OPCODE) {
            int value = (byte) -payload[0];
            int id = readUnsignedShort(payload, 1);
            return new Update(id, value);
        }
        int value = ((payload[1] & 255) << 24)
                | ((payload[0] & 255) << 16)
                | ((payload[3] & 255) << 8)
                | (payload[2] & 255);
        int id = readUnsignedShort(payload, 4);
        return new Update(id, value);
    }

    public static Update apply(int opcode, byte[] payload) throws IOException {
        Update update = decode(opcode, payload);
        Varps.set(update.id, update.value);
        return update;
    }

    private static int readUnsignedShort(byte[] payload, int offset) {
        return (payload[offset] & 255) << 8 | payload[offset + 1] & 255;
    }

    public static final class Update {
        public final int id;
        public final int value;

        Update(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }
}
