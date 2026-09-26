package client;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Decodes the delta-compressed object placements stored in revision 443 lX_Y map files. */
public final class LocationMap {
    private LocationMap() {
    }

    public static Location[] decode(byte[] data) throws IOException {
        Reader input = new Reader(data);
        List<Location> locations = new ArrayList<Location>();
        int objectId = -1;
        while (true) {
            int objectDelta = input.readUnsignedSmart();
            if (objectDelta == 0) break;
            objectId += objectDelta;
            int packedPosition = 0;
            while (true) {
                int positionDelta = input.readUnsignedSmart();
                if (positionDelta == 0) break;
                packedPosition += positionDelta - 1;
                int localY = packedPosition & 63;
                int localX = packedPosition >> 6 & 63;
                int plane = packedPosition >> 12;
                int attributes = input.readUnsignedByte();
                int type = attributes >> 2;
                int orientation = attributes & 3;
                if (plane < 0 || plane > 3) {
                    throw new IOException("Invalid 443 location plane " + plane
                            + " for object " + objectId);
                }
                locations.add(new Location(objectId, plane, localX, localY, type, orientation));
            }
        }
        if (input.position() != data.length) {
            throw new IOException("Trailing bytes in 443 location map: "
                    + (data.length - input.position()));
        }
        return locations.toArray(new Location[locations.size()]);
    }

    public static final class Location {
        public final int objectId;
        public final int plane;
        public final int localX;
        public final int localY;
        public final int type;
        public final int orientation;

        private Location(int objectId, int plane, int localX, int localY,
                         int type, int orientation) {
            this.objectId = objectId;
            this.plane = plane;
            this.localX = localX;
            this.localY = localY;
            this.type = type;
            this.orientation = orientation;
        }
    }

    private static final class Reader {
        private final byte[] data;
        private int position;

        private Reader(byte[] data) {
            this.data = data;
        }

        int position() {
            return position;
        }

        int readUnsignedByte() throws EOFException {
            if (position >= data.length) throw new EOFException("Truncated 443 location map");
            return data[position++] & 255;
        }

        int readUnsignedShort() throws EOFException {
            int high = readUnsignedByte();
            int low = readUnsignedByte();
            return high << 8 | low;
        }

        int readUnsignedSmart() throws EOFException {
            if (position >= data.length) throw new EOFException("Truncated 443 location map");
            return (data[position] & 255) < 128
                    ? readUnsignedByte()
                    : readUnsignedShort() - 32768;
        }
    }
}
