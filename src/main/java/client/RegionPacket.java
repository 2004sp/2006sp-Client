package client;

import java.io.IOException;

/** Revision 443 static (121) and constructed (193) rebuild payloads. */
final class RegionPacket {
    final int centerX;
    final int centerY;
    final int localX;
    final int localY;
    final int plane;
    final int[][] xteaKeys;
    final int[][][] templates;

    private RegionPacket(int centerX, int centerY, int localX,
                                    int localY, int plane, int[][] xteaKeys,
                                    int[][][] templates) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.localX = localX;
        this.localY = localY;
        this.plane = plane;
        this.xteaKeys = xteaKeys;
        this.templates = templates;
    }

    static RegionPacket decode(byte[] payload) throws IOException {
        return decodeStatic(payload);
    }

    static RegionPacket decodeStatic(byte[] payload) throws IOException {
        if (payload.length < 9 || (payload.length - 9) % 16 != 0) {
            throw new IOException("Invalid revision 443 static region packet length");
        }
        int centerX = readLittleShort(payload, 0);
        int localY = readLittleShort(payload, 2);
        int localX = ((payload[5] & 255) << 8) | (payload[4] - 128 & 255);
        int plane = payload[6] - 128 & 255;
        int keyCount = (payload.length - 9) / 16;
        int[][] keys = new int[keyCount][4];
        int offset = 7;
        for (int i = 0; i < keyCount; i++) {
            for (int word = 0; word < 4; word++) {
                keys[i][word] = readInt(payload, offset);
                offset += 4;
            }
        }
        int centerY = ((payload[offset] & 255) << 8)
                | (payload[offset + 1] - 128 & 255);
        if (centerX <= 0 || centerY <= 0 || localX >= 104 || localY >= 104
                || plane > 3) {
            throw new IOException("Invalid revision 443 region coordinates");
        }
        int expectedKeys = expectedKeyCount(centerX, centerY);
        if (keyCount != expectedKeys) {
            throw new IOException("Invalid revision 443 region key count: expected "
                    + expectedKeys + ", got " + keyCount);
        }
        return new RegionPacket(centerX, centerY, localX, localY,
                plane, keys, null);
    }

    static RegionPacket decodeConstructed(byte[] payload) throws IOException {
        int[][][] templates = new int[4][13][13];
        int bit = 0;
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 13; x++) {
                for (int y = 0; y < 13; y++) {
                    if (bit + 1 > payload.length * 8) {
                        throw new IOException("Truncated revision 443 constructed templates");
                    }
                    int present = readBits(payload, bit, 1);
                    bit++;
                    if (present == 0) {
                        templates[plane][x][y] = -1;
                    } else {
                        if (bit + 26 > payload.length * 8) {
                            throw new IOException("Truncated revision 443 constructed template");
                        }
                        templates[plane][x][y] = readBits(payload, bit, 26);
                        bit += 26;
                    }
                }
            }
        }
        int offset = (bit + 7) / 8;
        if (payload.length - offset < 9 || (payload.length - offset - 9) % 16 != 0) {
            throw new IOException("Invalid revision 443 constructed region packet length");
        }
        int keyCount = (payload.length - offset - 9) / 16;
        int[][] keys = new int[keyCount][4];
        for (int i = 0; i < keyCount; i++) {
            for (int word = 0; word < 4; word++) {
                keys[i][word] = readInt(payload, offset);
                offset += 4;
            }
        }
        int localY = readLittleAddedShort(payload, offset);
        int centerX = readLittleShort(payload, offset + 2);
        int localX = readLittleAddedShort(payload, offset + 4);
        int centerY = readLittleShort(payload, offset + 6);
        int plane = -payload[offset + 8] & 255;
        if (centerX <= 0 || centerY <= 0 || localX >= 104 || localY >= 104
                || plane > 3) {
            throw new IOException("Invalid revision 443 constructed region coordinates");
        }
        return new RegionPacket(centerX, centerY, localX, localY,
                plane, keys, templates);
    }

    private static int readBits(byte[] data, int bitOffset, int count) {
        int value = 0;
        for (int i = 0; i < count; i++) {
            int bit = bitOffset + i;
            value = value << 1 | (data[bit >> 3] >> (7 - (bit & 7)) & 1);
        }
        return value;
    }

    private static int readLittleAddedShort(byte[] data, int offset) {
        return (data[offset] - 128 & 255) | ((data[offset + 1] & 255) << 8);
    }

    static int expectedKeyCount(int centerX, int centerY) {
        boolean special = isSpecialRegion(centerX, centerY);
        int count = 0;
        for (int x = (centerX - 6) / 8; x <= (centerX + 6) / 8; x++) {
            for (int y = (centerY - 6) / 8; y <= (centerY + 6) / 8; y++) {
                if (!special || !isSkippedSpecialSquare(x, y)) {
                    count++;
                }
            }
        }
        return count;
    }

    static boolean isSpecialRegion(int centerX, int centerY) {
        return ((centerX / 8 == 48 || centerX / 8 == 49) && centerY / 8 == 48)
                || (centerX / 8 == 48 && centerY / 8 == 148);
    }

    static boolean isSkippedSpecialSquare(int x, int y) {
        return y == 49 || y == 149 || y == 147 || x == 50
                || (x == 49 && y == 47);
    }

    private static int readLittleShort(byte[] data, int offset) {
        return (data[offset] & 255) | ((data[offset + 1] & 255) << 8);
    }

    private static int readInt(byte[] data, int offset) {
        return (data[offset] & 255) << 24 | (data[offset + 1] & 255) << 16
                | (data[offset + 2] & 255) << 8 | data[offset + 3] & 255;
    }
}
