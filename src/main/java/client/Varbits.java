package client;

import java.io.EOFException;
import java.io.IOException;
import java.util.Map;

/** Revision 443 varbit definitions from JS5 archive 2, group 14. */
public final class Varbits {
    private static final int CONFIG_ARCHIVE = 2;
    private static final int VARBIT_GROUP = 14;

    private final Definition[] definitions;

    private Varbits(Definition[] definitions) {
        this.definitions = definitions;
    }

    public static Varbits load(Cache cache) throws IOException {
        Map<Integer, byte[]> files = cache.readFiles(CONFIG_ARCHIVE, VARBIT_GROUP);
        int maxId = -1;
        for (Integer id : files.keySet()) {
            if (id != null && id > maxId) maxId = id;
        }
        Definition[] definitions = new Definition[maxId + 1];
        for (Map.Entry<Integer, byte[]> entry : files.entrySet()) {
            definitions[entry.getKey()] = Definition.decode(entry.getKey(), entry.getValue());
        }
        return new Varbits(definitions);
    }

    public int size() {
        return definitions.length;
    }

    public Definition get(int id) {
        return id >= 0 && id < definitions.length ? definitions[id] : null;
    }

    public int getRequiredVarpCount() {
        int maxVarp = -1;
        for (Definition definition : definitions) {
            if (definition != null && definition.varpIndex > maxVarp) {
                maxVarp = definition.varpIndex;
            }
        }
        return maxVarp + 1;
    }

    public int getValue(int id, int[] varps) {
        Definition definition = get(id);
        if (definition == null || varps == null
                || definition.varpIndex < 0 || definition.varpIndex >= varps.length) {
            return 0;
        }
        return extractValue(definition, varps[definition.varpIndex]);
    }

    public int getValue(int id, int varpValue) {
        Definition definition = get(id);
        return definition == null ? 0 : extractValue(definition, varpValue);
    }

    private static int extractValue(Definition definition, int varpValue) {
        int width = definition.mostSignificantBit - definition.leastSignificantBit + 1;
        int mask = width >= 32 ? -1 : (1 << width) - 1;
        return varpValue >>> definition.leastSignificantBit & mask;
    }

    public static final class Definition {
        public final int id;
        public int varpIndex = -1;
        public int leastSignificantBit;
        public int mostSignificantBit;

        private Definition(int id) {
            this.id = id;
        }

        private static Definition decode(int id, byte[] data) throws IOException {
            Definition definition = new Definition(id);
            Reader input = new Reader(data);
            while (true) {
                int opcode = input.readUnsignedByte();
                if (opcode == 0) break;
                if (opcode == 1) {
                    definition.varpIndex = input.readUnsignedShort();
                    definition.leastSignificantBit = input.readUnsignedByte();
                    definition.mostSignificantBit = input.readUnsignedByte();
                } else {
                    throw new IOException("Unsupported 443 varbit opcode " + opcode
                            + " for varbit " + id + " at byte " + (input.position() - 1));
                }
            }
            if (input.position() != data.length) {
                throw new IOException("Trailing bytes in 443 varbit " + id + ": "
                        + (data.length - input.position()));
            }
            if (definition.varpIndex < 0) {
                throw new IOException("443 varbit " + id + " has no base varp");
            }
            if (definition.leastSignificantBit < 0
                    || definition.mostSignificantBit < definition.leastSignificantBit
                    || definition.mostSignificantBit > 31) {
                throw new IOException("Invalid 443 varbit range " + id + ": "
                        + definition.leastSignificantBit + ".." + definition.mostSignificantBit);
            }
            return definition;
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

        int readUnsignedByte() throws IOException {
            require(1);
            return data[position++] & 255;
        }

        int readUnsignedShort() throws IOException {
            require(2);
            int value = (data[position] & 255) << 8 | data[position + 1] & 255;
            position += 2;
            return value;
        }

        private void require(int count) throws EOFException {
            if (position + count > data.length) {
                throw new EOFException("Truncated 443 varbit config");
            }
        }
    }
}
