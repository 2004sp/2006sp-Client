package client;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Revision 443 object/loc definitions from JS5 archive 2, group 6. */
public final class ObjectDefinitions {
    private static final int CONFIG_ARCHIVE = 2;
    private static final int OBJECT_GROUP = 6;

    private final Definition[] definitions;

    private ObjectDefinitions(Definition[] definitions) {
        this.definitions = definitions;
    }

    public static ObjectDefinitions load(Cache cache) throws IOException {
        Map<Integer, byte[]> files = cache.readFiles(CONFIG_ARCHIVE, OBJECT_GROUP);
        int maxId = -1;
        for (Integer id : files.keySet()) {
            if (id != null && id > maxId) maxId = id;
        }
        Definition[] definitions = new Definition[maxId + 1];
        for (Map.Entry<Integer, byte[]> entry : files.entrySet()) {
            int id = entry.getKey();
            definitions[id] = Definition.decode(id, entry.getValue());
        }
        return new ObjectDefinitions(definitions);
    }

    public int size() {
        return definitions.length;
    }

    public Definition get(int id) {
        return id >= 0 && id < definitions.length ? definitions[id] : null;
    }

    public int countModelReferences() {
        int count = 0;
        for (Definition definition : definitions) {
            if (definition != null && definition.modelIds != null) {
                count += definition.modelIds.length;
            }
        }
        return count;
    }

    public static final class Definition {
        public final int id;
        public int[] modelIds;
        public int[] modelTypes;
        public String name;
        public String description;
        public int sizeX = 1;
        public int sizeY = 1;
        public boolean solid = true;
        public boolean walkable = true;
        public boolean hasActions;
        public boolean adjustToTerrain;
        public boolean nonFlatShading;
        public boolean wall;
        public int animationId = -1;
        public int decorDisplacement = 16;
        public int ambient;
        public int contrast;
        public String[] actions;
        public int[] recolorFrom;
        public int[] recolorTo;
        public int[] retextureFrom;
        public int[] retextureTo;
        public int mapIconId = -1;
        public boolean rotated;
        public boolean castsShadow = true;
        public int modelSizeX = 128;
        public int modelSizeHeight = 128;
        public int modelSizeY = 128;
        public int mapSceneId = -1;
        public int blockingMask;
        public int offsetX;
        public int offsetHeight;
        public int offsetY;
        public boolean obstructsGround;
        public boolean hollow;
        public int supportsItems = -1;
        public int varbitId = -1;
        public int configId = -1;
        public int[] childIds;
        public boolean hasMorphFallback;
        public int morphFallbackId = -1;
        public int ambientSoundId = -1;
        public int soundRange;
        public int soundMinDelay;
        public int soundMaxDelay;
        public int[] soundEffectIds;
        public int terrainAdjustment = -1;
        public final Map<Integer, Object> params = new LinkedHashMap<Integer, Object>();

        private Definition(int id) {
            this.id = id;
        }

        private static Definition decode(int id, byte[] data) throws IOException {
            Definition definition = new Definition(id);
            Reader input = new Reader(data);
            int actionFlag = -1;
            while (true) {
                int opcode = input.readUnsignedByte();
                if (opcode == 0) break;
                if (opcode == 1) {
                    int count = input.readUnsignedByte();
                    definition.modelIds = new int[count];
                    definition.modelTypes = new int[count];
                    for (int i = 0; i < count; i++) {
                        definition.modelIds[i] = input.readUnsignedShort();
                        definition.modelTypes[i] = input.readUnsignedByte();
                    }
                } else if (opcode == 2) {
                    definition.name = input.readString();
                } else if (opcode == 3) {
                    definition.description = input.readString();
                } else if (opcode == 5) {
                    int count = input.readUnsignedByte();
                    definition.modelTypes = null;
                    definition.modelIds = new int[count];
                    for (int i = 0; i < count; i++) definition.modelIds[i] = input.readUnsignedShort();
                } else if (opcode == 14) {
                    definition.sizeX = input.readUnsignedByte();
                } else if (opcode == 15) {
                    definition.sizeY = input.readUnsignedByte();
                } else if (opcode == 17) {
                    definition.solid = false;
                } else if (opcode == 18) {
                    definition.walkable = false;
                } else if (opcode == 19) {
                    actionFlag = input.readUnsignedByte();
                    if (actionFlag == 1) definition.hasActions = true;
                } else if (opcode == 21) {
                    definition.adjustToTerrain = true;
                    definition.terrainAdjustment = 0;
                } else if (opcode == 22) {
                    definition.nonFlatShading = true;
                } else if (opcode == 23) {
                    definition.wall = true;
                } else if (opcode == 24) {
                    definition.animationId = readNullableUnsignedShort(input);
                } else if (opcode == 27) {
                    // 443 uses this as the alternate collision/interact mode.
                    definition.solid = true;
                } else if (opcode == 28) {
                    definition.decorDisplacement = input.readUnsignedByte();
                } else if (opcode == 29) {
                    definition.ambient = input.readByte();
                } else if (opcode == 39) {
                    definition.contrast = input.readByte();
                } else if (opcode >= 30 && opcode < 35) {
                    if (definition.actions == null) definition.actions = new String[5];
                    String action = input.readString();
                    definition.actions[opcode - 30] = "hidden".equalsIgnoreCase(action) ? null : action;
                } else if (opcode == 40) {
                    int count = input.readUnsignedByte();
                    definition.recolorFrom = new int[count];
                    definition.recolorTo = new int[count];
                    for (int i = 0; i < count; i++) {
                        definition.recolorFrom[i] = input.readUnsignedShort();
                        definition.recolorTo[i] = input.readUnsignedShort();
                    }
                } else if (opcode == 41) {
                    int count = input.readUnsignedByte();
                    definition.retextureFrom = new int[count];
                    definition.retextureTo = new int[count];
                    for (int i = 0; i < count; i++) {
                        definition.retextureFrom[i] = input.readUnsignedShort();
                        definition.retextureTo[i] = input.readUnsignedShort();
                    }
                } else if (opcode == 60 || opcode == 82) {
                    definition.mapIconId = input.readUnsignedShort();
                } else if (opcode == 62) {
                    definition.rotated = true;
                } else if (opcode == 64) {
                    definition.castsShadow = false;
                } else if (opcode == 65) {
                    definition.modelSizeX = input.readUnsignedShort();
                } else if (opcode == 66) {
                    definition.modelSizeHeight = input.readUnsignedShort();
                } else if (opcode == 67) {
                    definition.modelSizeY = input.readUnsignedShort();
                } else if (opcode == 68) {
                    definition.mapSceneId = input.readUnsignedShort();
                } else if (opcode == 69) {
                    definition.blockingMask = input.readUnsignedByte();
                } else if (opcode == 70) {
                    definition.offsetX = input.readShort();
                } else if (opcode == 71) {
                    definition.offsetHeight = input.readShort();
                } else if (opcode == 72) {
                    definition.offsetY = input.readShort();
                } else if (opcode == 73) {
                    definition.obstructsGround = true;
                } else if (opcode == 74) {
                    definition.hollow = true;
                } else if (opcode == 75) {
                    definition.supportsItems = input.readUnsignedByte();
                } else if (opcode == 77 || opcode == 92) {
                    definition.varbitId = readNullableUnsignedShort(input);
                    definition.configId = readNullableUnsignedShort(input);
                    int fallback = -1;
                    if (opcode == 92) {
                        definition.hasMorphFallback = true;
                        fallback = readNullableUnsignedShort(input);
                        definition.morphFallbackId = fallback;
                    }
                    int count = input.readUnsignedByte();
                    definition.childIds = new int[count + (opcode == 92 ? 2 : 1)];
                    for (int i = 0; i <= count; i++) {
                        definition.childIds[i] = readNullableUnsignedShort(input);
                    }
                    if (opcode == 92) definition.childIds[count + 1] = fallback;
                } else if (opcode == 78) {
                    definition.ambientSoundId = input.readUnsignedShort();
                    definition.soundRange = input.readUnsignedByte();
                } else if (opcode == 79) {
                    definition.soundMinDelay = input.readUnsignedShort();
                    definition.soundMaxDelay = input.readUnsignedShort();
                    definition.soundRange = input.readUnsignedByte();
                    int count = input.readUnsignedByte();
                    definition.soundEffectIds = new int[count];
                    for (int i = 0; i < count; i++) definition.soundEffectIds[i] = input.readUnsignedShort();
                } else if (opcode == 81) {
                    definition.terrainAdjustment = input.readUnsignedByte() * 256;
                    definition.adjustToTerrain = true;
                } else if (opcode == 249) {
                    int count = input.readUnsignedByte();
                    for (int i = 0; i < count; i++) {
                        boolean stringValue = input.readUnsignedByte() == 1;
                        int key = input.readUnsignedMedium();
                        definition.params.put(key, stringValue ? input.readString() : Integer.valueOf(input.readInt()));
                    }
                } else {
                    throw new IOException("Unsupported 443 object opcode " + opcode
                            + " for object " + id + " at byte " + (input.position() - 1));
                }
            }
            if (input.position() != data.length) {
                throw new IOException("Trailing bytes in 443 object " + id + ": "
                        + (data.length - input.position()));
            }
            if (actionFlag == -1) {
                definition.hasActions = definition.modelIds != null
                        && (definition.modelTypes == null || definition.modelTypes.length > 0
                        && definition.modelTypes[0] == 10);
                if (definition.actions != null) {
                    for (String action : definition.actions) {
                        if (action != null) {
                            definition.hasActions = true;
                            break;
                        }
                    }
                }
            }
            if (definition.hollow) {
                definition.solid = false;
                definition.walkable = false;
            }
            if (definition.supportsItems == -1) {
                definition.supportsItems = definition.solid ? 1 : 0;
            }
            return definition;
        }

        private static int readNullableUnsignedShort(Reader input) throws IOException {
            int value = input.readUnsignedShort();
            return value == 65535 ? -1 : value;
        }
    }

    /** Small reader for 443 cache config files. Strings are NUL-terminated. */
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

        int readByte() throws IOException {
            require(1);
            return data[position++];
        }

        int readUnsignedShort() throws IOException {
            require(2);
            int value = (data[position] & 255) << 8 | data[position + 1] & 255;
            position += 2;
            return value;
        }

        int readShort() throws IOException {
            int value = readUnsignedShort();
            return value > 32767 ? value - 65536 : value;
        }

        int readUnsignedMedium() throws IOException {
            require(3);
            int value = (data[position] & 255) << 16
                    | (data[position + 1] & 255) << 8
                    | data[position + 2] & 255;
            position += 3;
            return value;
        }

        int readInt() throws IOException {
            require(4);
            int value = (data[position] & 255) << 24
                    | (data[position + 1] & 255) << 16
                    | (data[position + 2] & 255) << 8
                    | data[position + 3] & 255;
            position += 4;
            return value;
        }

        String readString() throws IOException {
            int start = position;
            while (position < data.length && data[position] != 0) position++;
            if (position >= data.length) throw new EOFException("Unterminated 443 config string");
            String value = new String(data, start, position - start, StandardCharsets.ISO_8859_1);
            position++;
            return value;
        }

        private void require(int count) throws EOFException {
            if (position + count > data.length) throw new EOFException("Truncated 443 object config");
        }
    }
}
