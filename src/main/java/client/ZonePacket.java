package client;

import java.io.IOException;

/** Decodes revision 443 zone events that share opcode 82's local 8x8 base. */
final class ZonePacket {
    static final int GROUND_AMOUNT_OPCODE = 79;
    static final int GROUND_REMOVE_OPCODE = 84;
    static final int GROUND_ADD_EXCEPT_OPCODE = 94;
    static final int PROJECTILE_OPCODE = 101;
    static final int AREA_SOUND_OPCODE = 109;
    static final int SPOT_ANIMATION_OPCODE = 115;
    static final int PLAYER_OBJECT_OPCODE = 129;
    static final int GROUND_ADD_OPCODE = 207;

    private ZonePacket() {
    }

    static boolean handles(int opcode) {
        return opcode == GROUND_AMOUNT_OPCODE || opcode == GROUND_REMOVE_OPCODE
                || opcode == GROUND_ADD_EXCEPT_OPCODE || opcode == PROJECTILE_OPCODE
                || opcode == AREA_SOUND_OPCODE || opcode == SPOT_ANIMATION_OPCODE
                || opcode == PLAYER_OBJECT_OPCODE || opcode == GROUND_ADD_OPCODE;
    }

    static Update decode(int opcode, byte[] payload) throws IOException {
        if (!handles(opcode)) {
            throw new IOException("Unsupported 443 zone packet opcode " + opcode);
        }
        int expectedLength = PacketFramer.lengthFor(opcode);
        if (payload == null || payload.length != expectedLength) {
            throw new IOException("443 zone packet " + opcode + " length "
                    + (payload == null ? -1 : payload.length) + " != " + expectedLength);
        }
        if (!ObjectPacket.isZoneBaseSet()) {
            throw new IOException("443 zone packet arrived before zone base");
        }

        int baseX = ObjectPacket.getZoneBaseX();
        int baseY = ObjectPacket.getZoneBaseY();

        if (opcode == GROUND_AMOUNT_OPCODE) {
            int packedPosition = payload[0] & 255;
            return Update.groundAmount(
                    baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7),
                    readUnsignedShort(payload, 1), readUnsignedShort(payload, 3),
                    readUnsignedShort(payload, 5));
        }
        if (opcode == GROUND_REMOVE_OPCODE) {
            int itemId = readUnsignedShortLittleEndian(payload, 0);
            int packedPosition = payload[2] & 255;
            return Update.groundRemove(
                    baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7), itemId);
        }
        if (opcode == GROUND_ADD_EXCEPT_OPCODE) {
            int sourcePlayerIndex = readUnsignedShortAdded(payload, 0);
            int amount = readUnsignedShortAdded(payload, 2);
            int packedPosition = -payload[4] & 255;
            int itemId = readUnsignedShortAdded(payload, 5);
            return Update.groundAddExcept(
                    baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7),
                    itemId, amount, sourcePlayerIndex);
        }
        if (opcode == PROJECTILE_OPCODE) {
            int packedPosition = payload[0] & 255;
            int startX = baseX + (packedPosition >> 4 & 7);
            int startY = baseY + (packedPosition & 7);
            int endX = startX + payload[1];
            int endY = startY + payload[2];
            return Update.projectile(startX, startY, endX, endY,
                    readUnsignedShort(payload, 5), readSignedShort(payload, 3),
                    (payload[7] & 255) * 4, (payload[8] & 255) * 4,
                    readUnsignedShort(payload, 9), readUnsignedShort(payload, 11),
                    payload[13] & 255, payload[14] & 255);
        }
        if (opcode == AREA_SOUND_OPCODE) {
            int packedPosition = payload[0] & 255;
            int packedSound = payload[3] & 255;
            return Update.areaSound(
                    baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7),
                    readUnsignedShort(payload, 1), packedSound >> 4 & 15,
                    packedSound & 7, payload[4] & 255);
        }
        if (opcode == SPOT_ANIMATION_OPCODE) {
            int packedPosition = payload[0] & 255;
            return Update.spotAnimation(
                    baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7),
                    readUnsignedShort(payload, 1), payload[3] & 255,
                    readUnsignedShort(payload, 4));
        }
        if (opcode == PLAYER_OBJECT_OPCODE) {
            int maxYOffset = (byte) -payload[0];
            int minXOffset = (byte) (128 - payload[1]);
            int objectId = readUnsignedShortAdded(payload, 2);
            int maxXOffset = (byte) (128 - payload[4]);
            int minYOffset = (byte) (payload[5] - 128);
            int startDelay = readUnsignedShortAdded(payload, 6);
            int packedType = -payload[8] & 255;
            int packedPosition = 128 - payload[9] & 255;
            return Update.playerObject(
                    baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7),
                    objectId, packedType >> 2, packedType & 3,
                    readUnsignedShortLittleEndianAdded(payload, 12),
                    startDelay, readUnsignedShort(payload, 10),
                    minXOffset, minYOffset, maxXOffset, maxYOffset);
        }

        int amount = readUnsignedShortAdded(payload, 0);
        int itemId = readUnsignedShortLittleEndian(payload, 2);
        int packedPosition = payload[4] & 255;
        return Update.groundAdd(
                baseX + (packedPosition >> 4 & 7), baseY + (packedPosition & 7),
                itemId, amount);
    }

    static void apply(Client client, int opcode, byte[] payload) throws IOException {
        Update update = decode(opcode, payload);
        if (opcode == GROUND_AMOUNT_OPCODE) {
            client.applyRevision443GroundItemAmount(update.x, update.y, update.id,
                    update.oldAmount, update.amount);
        } else if (opcode == GROUND_REMOVE_OPCODE) {
            client.applyRevision443GroundItemRemove(update.x, update.y, update.id);
        } else if (opcode == GROUND_ADD_EXCEPT_OPCODE) {
            client.applyRevision443GroundItemAddUnlessLocal(update.x, update.y, update.id,
                    update.amount, update.sourcePlayerIndex);
        } else if (opcode == PROJECTILE_OPCODE) {
            client.applyRevision443Projectile(update.x, update.y, update.endX, update.endY,
                    update.id, update.targetIndex, update.startHeight, update.endHeight,
                    update.startDelay, update.endDelay, update.slope, update.startDistance);
        } else if (opcode == AREA_SOUND_OPCODE) {
            client.applyRevision443AreaSound(update.x, update.y, update.id,
                    update.radius, update.loops, update.delay);
        } else if (opcode == SPOT_ANIMATION_OPCODE) {
            client.applyRevision443SpotAnimation(update.x, update.y, update.id,
                    update.startHeight, update.delay);
        } else if (opcode == PLAYER_OBJECT_OPCODE) {
            client.applyRevision443PlayerObjectAttachment(update.x, update.y, update.id,
                    update.type, update.orientation, update.sourcePlayerIndex,
                    update.startDelay, update.endDelay, update.minXOffset,
                    update.minYOffset, update.maxXOffset, update.maxYOffset);
        } else {
            client.applyRevision443GroundItemAdd(update.x, update.y, update.id, update.amount);
        }
    }

    private static int readUnsignedShort(byte[] data, int offset) {
        return (data[offset] & 255) << 8 | data[offset + 1] & 255;
    }

    private static int readSignedShort(byte[] data, int offset) {
        int value = readUnsignedShort(data, offset);
        return value > 32767 ? value - 65536 : value;
    }

    private static int readUnsignedShortLittleEndian(byte[] data, int offset) {
        return data[offset] & 255 | (data[offset + 1] & 255) << 8;
    }

    private static int readUnsignedShortAdded(byte[] data, int offset) {
        return (data[offset] & 255) << 8 | data[offset + 1] - 128 & 255;
    }

    private static int readUnsignedShortLittleEndianAdded(byte[] data, int offset) {
        return data[offset] - 128 & 255 | (data[offset + 1] & 255) << 8;
    }

    static final class Update {
        int x;
        int y;
        int endX;
        int endY;
        int id = -1;
        int amount;
        int oldAmount;
        int sourcePlayerIndex = -1;
        int targetIndex;
        int type;
        int orientation;
        int minXOffset;
        int minYOffset;
        int maxXOffset;
        int maxYOffset;
        int startHeight;
        int endHeight;
        int startDelay;
        int endDelay;
        int slope;
        int startDistance;
        int radius;
        int loops;
        int delay;

        static Update groundAmount(int x, int y, int id, int oldAmount, int amount) {
            Update update = groundAdd(x, y, id, amount);
            update.oldAmount = oldAmount;
            return update;
        }

        static Update groundRemove(int x, int y, int id) {
            Update update = new Update();
            update.x = x;
            update.y = y;
            update.id = id;
            return update;
        }

        static Update groundAdd(int x, int y, int id, int amount) {
            Update update = groundRemove(x, y, id);
            update.amount = amount;
            return update;
        }

        static Update groundAddExcept(int x, int y, int id, int amount,
                                      int sourcePlayerIndex) {
            Update update = groundAdd(x, y, id, amount);
            update.sourcePlayerIndex = sourcePlayerIndex;
            return update;
        }

        static Update playerObject(int x, int y, int id, int type, int orientation,
                                   int playerIndex, int startDelay, int endDelay,
                                   int minXOffset, int minYOffset,
                                   int maxXOffset, int maxYOffset) {
            Update update = groundRemove(x, y, id);
            update.type = type;
            update.orientation = orientation;
            update.sourcePlayerIndex = playerIndex;
            update.startDelay = startDelay;
            update.endDelay = endDelay;
            update.minXOffset = minXOffset;
            update.minYOffset = minYOffset;
            update.maxXOffset = maxXOffset;
            update.maxYOffset = maxYOffset;
            return update;
        }

        static Update projectile(int x, int y, int endX, int endY, int id,
                                 int targetIndex, int startHeight, int endHeight,
                                 int startDelay, int endDelay, int slope,
                                 int startDistance) {
            Update update = groundRemove(x, y, id);
            update.endX = endX;
            update.endY = endY;
            update.targetIndex = targetIndex;
            update.startHeight = startHeight;
            update.endHeight = endHeight;
            update.startDelay = startDelay;
            update.endDelay = endDelay;
            update.slope = slope;
            update.startDistance = startDistance;
            return update;
        }

        static Update areaSound(int x, int y, int id, int radius, int loops, int delay) {
            Update update = groundRemove(x, y, id);
            update.radius = radius;
            update.loops = loops;
            update.delay = delay;
            return update;
        }

        static Update spotAnimation(int x, int y, int id, int height, int delay) {
            Update update = groundRemove(x, y, id);
            update.startHeight = height;
            update.delay = delay;
            return update;
        }
    }
}
