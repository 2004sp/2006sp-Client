package client;

import java.io.IOException;

/** Decodes revision 443 zone-base and runtime location packets. */
final class ObjectPacket {
    static final int REMOVE_OPCODE = 69;
    static final int ZONE_BASE_OPCODE = 82;
    static final int ADD_OPCODE = 122;
    static final int ANIMATE_OPCODE = 170;

    private static int zoneBaseX;
    private static int zoneBaseY;
    private static boolean zoneBaseSet;

    private ObjectPacket() {
    }

    static boolean handles(int opcode) {
        return opcode == REMOVE_OPCODE || opcode == ZONE_BASE_OPCODE
                || opcode == ADD_OPCODE || opcode == ANIMATE_OPCODE;
    }

    static void reset() {
        zoneBaseX = 0;
        zoneBaseY = 0;
        zoneBaseSet = false;
    }

    static int getZoneBaseX() {
        return zoneBaseX;
    }

    static int getZoneBaseY() {
        return zoneBaseY;
    }

    static boolean isZoneBaseSet() {
        return zoneBaseSet;
    }

    static Update decode(int opcode, byte[] payload) throws IOException {
        int expectedLength = PacketFramer.lengthFor(opcode);
        if (!handles(opcode) || expectedLength < 0) {
            throw new IOException("Unsupported 443 object packet opcode " + opcode);
        }
        if (payload == null || payload.length != expectedLength) {
            throw new IOException("443 object packet " + opcode + " length "
                    + (payload == null ? -1 : payload.length) + " != " + expectedLength);
        }

        if (opcode == ZONE_BASE_OPCODE) {
            int x = 128 - payload[0] & 255;
            int y = 128 - payload[1] & 255;
            return Update.zoneBase(x, y);
        }

        if (!zoneBaseSet) {
            throw new IOException("443 runtime object packet arrived before zone base");
        }

        if (opcode == REMOVE_OPCODE) {
            int packedType = payload[0] & 255;
            int packedPosition = payload[1] - 128 & 255;
            return Update.location(opcode, zoneBaseX + (packedPosition >> 4 & 7),
                    zoneBaseY + (packedPosition & 7), packedType >> 2,
                    packedType & 3, -1, -1);
        }

        if (opcode == ADD_OPCODE) {
            int packedPosition = -payload[0] & 255;
            int objectId = readUnsignedShort(payload, 1);
            int packedType = payload[3] - 128 & 255;
            return Update.location(opcode, zoneBaseX + (packedPosition >> 4 & 7),
                    zoneBaseY + (packedPosition & 7), packedType >> 2,
                    packedType & 3, objectId, -1);
        }

        int sequenceId = ((payload[1] & 255) << 8) + (payload[0] - 128 & 255);
        int packedType = payload[2] - 128 & 255;
        int packedPosition = 128 - payload[3] & 255;
        return Update.location(opcode, zoneBaseX + (packedPosition >> 4 & 7),
                zoneBaseY + (packedPosition & 7), packedType >> 2,
                packedType & 3, -1, sequenceId);
    }

    static void apply(int opcode, byte[] payload, int plane) throws IOException {
        Update update = decode(opcode, payload);
        if (update.zoneBase) {
            zoneBaseX = update.x;
            zoneBaseY = update.y;
            zoneBaseSet = true;
            return;
        }
        if (opcode == ANIMATE_OPCODE) {
            SceneObjects.animateRuntimeObject(plane, update.x, update.y,
                    update.type, update.orientation, update.sequenceId);
            return;
        }
        SceneObjects.updateRuntimeObject(plane, update.x, update.y,
                update.type, update.orientation, update.objectId);
    }

    private static int readUnsignedShort(byte[] data, int offset) {
        return (data[offset] & 255) << 8 | data[offset + 1] & 255;
    }

    static final class Update {
        final boolean zoneBase;
        final int x;
        final int y;
        final int type;
        final int orientation;
        final int objectId;
        final int sequenceId;

        private Update(boolean zoneBase, int x, int y, int type, int orientation,
                       int objectId, int sequenceId) {
            this.zoneBase = zoneBase;
            this.x = x;
            this.y = y;
            this.type = type;
            this.orientation = orientation;
            this.objectId = objectId;
            this.sequenceId = sequenceId;
        }

        static Update zoneBase(int x, int y) {
            return new Update(true, x, y, -1, -1, -1, -1);
        }

        static Update location(int opcode, int x, int y, int type, int orientation,
                               int objectId, int sequenceId) {
            return new Update(false, x, y, type, orientation, objectId, sequenceId);
        }
    }
}
