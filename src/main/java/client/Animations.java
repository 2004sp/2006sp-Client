package client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Loads revision 443 sequences, frame sets, and skeletons from JS5 archives 2/0/1. */
final class Animations {
    private static Animations active;
    private static final int FRAME_ARCHIVE = 0;
    private static final int SKELETON_ARCHIVE = 1;
    private static final int CONFIG_ARCHIVE = 2;
    private static final int SEQUENCE_GROUP = 12;

    private final Cache cache;
    private final Map<Integer, AnimationSequence> sequences =
            new HashMap<Integer, AnimationSequence>();
    private final Map<Integer, AnimationSkeleton> skeletons =
            new HashMap<Integer, AnimationSkeleton>();

    Animations(Cache cache) {
        this.cache = cache;
    }

    static void loadRuntime(Cache cache) throws IOException {
        Map<Integer, byte[]> files = cache.readFiles(CONFIG_ARCHIVE, SEQUENCE_GROUP);
        int maxId = -1;
        for (Integer id : files.keySet()) maxId = Math.max(maxId, id.intValue());
        AnimationSequence[] loaded = new AnimationSequence[maxId + 1];
        for (Map.Entry<Integer, byte[]> entry : files.entrySet()) {
            loaded[entry.getKey().intValue()] = decodeSequence(entry.getValue());
        }
        AnimationFrame.prepareRevision443();
        AnimationSequence.sequences = loaded;
        active = new Animations(cache);
    }

    static boolean isActive() {
        return active != null;
    }

    static boolean ensureActiveFrame(int packedFrameId) throws IOException {
        if (active == null) return false;
        active.ensureFrame(packedFrameId);
        return true;
    }

    static void deactivate() {
        active = null;
    }

    AnimationSequence getLoadedSequence(int id) {
        return sequences.get(Integer.valueOf(id));
    }

    AnimationSequence getSequence(int id) throws IOException {
        if (id < 0) return null;
        AnimationSequence sequence = sequences.get(Integer.valueOf(id));
        if (sequence != null) return sequence;
        sequence = decodeSequence(cache.readFile(CONFIG_ARCHIVE, SEQUENCE_GROUP, id));
        for (int frameId : sequence.frameIds) {
            if (frameId != -1) ensureFrame(frameId);
        }
        sequences.put(Integer.valueOf(id), sequence);
        return sequence;
    }

    private void ensureFrame(int packedFrameId) throws IOException {
        if (AnimationFrame.frameCache != null
                && AnimationFrame.frameCache.get(new Integer(packedFrameId)) != null) {
            return;
        }
        int group = packedFrameId >>> 16;
        int file = packedFrameId & 65535;
        byte[] frameData = cache.readFile(FRAME_ARCHIVE, group, file);
        if (frameData.length < 3) {
            throw new IOException("Truncated 443 animation frame " + group + ":" + file);
        }
        int skeletonId = (frameData[0] & 255) << 8 | frameData[1] & 255;
        AnimationSkeleton skeleton = skeletons.get(Integer.valueOf(skeletonId));
        if (skeleton == null) {
            byte[] skeletonData = cache.readFile(SKELETON_ARCHIVE, skeletonId, 0);
            try {
                skeleton = new AnimationSkeleton(new Buffer(skeletonData), 474);
            } catch (RuntimeException exception) {
                throw new IOException("Invalid 443 animation skeleton " + skeletonId,
                        exception);
            }
            skeletons.put(Integer.valueOf(skeletonId), skeleton);
        }
        try {
            AnimationFrame.registerRevision443Frame(frameData, skeleton, packedFrameId);
        } catch (RuntimeException exception) {
            throw new IOException("Invalid 443 animation frame " + group + ":" + file,
                    exception);
        }
    }

    private static AnimationSequence decodeSequence(byte[] data) throws IOException {
        Buffer buffer = new Buffer(data);
        AnimationSequence sequence = new AnimationSequence();
        try {
            while (true) {
                int opcode = buffer.readUnsignedByte();
                if (opcode == 0) break;
                if (opcode == 1) {
                    int count = buffer.readUnsignedByte();
                    sequence.frameCount = count;
                    sequence.frameLengths = new int[count];
                    sequence.frameIds = new int[count];
                    sequence.secondaryFrameIds = new int[count];
                    for (int i = 0; i < count; i++) {
                        sequence.frameLengths[i] = buffer.readUnsignedShort();
                    }
                    for (int i = 0; i < count; i++) {
                        sequence.frameIds[i] = buffer.readUnsignedShort();
                        sequence.secondaryFrameIds[i] = -1;
                    }
                    for (int i = 0; i < count; i++) {
                        sequence.frameIds[i] += buffer.readUnsignedShort() << 16;
                    }
                } else if (opcode == 2) {
                    sequence.frameStep = buffer.readUnsignedShort();
                } else if (opcode == 3) {
                    int count = buffer.readUnsignedByte();
                    sequence.interleaveOrder = new int[count + 1];
                    for (int i = 0; i < count; i++) {
                        sequence.interleaveOrder[i] = buffer.readUnsignedByte();
                    }
                    sequence.interleaveOrder[count] = 9999999;
                } else if (opcode == 4) {
                    sequence.stretches = true;
                } else if (opcode == 5) {
                    sequence.forcedPriority = buffer.readUnsignedByte();
                } else if (opcode == 6) {
                    sequence.offHandModel = buffer.readUnsignedShort();
                } else if (opcode == 7) {
                    sequence.mainHandModel = buffer.readUnsignedShort();
                } else if (opcode == 8) {
                    sequence.maxLoops = buffer.readUnsignedByte();
                } else if (opcode == 9) {
                    sequence.precedenceAnimating = buffer.readUnsignedByte();
                } else if (opcode == 10) {
                    sequence.priority = buffer.readUnsignedByte();
                } else if (opcode == 11) {
                    sequence.replyMode = buffer.readUnsignedByte();
                } else if (opcode == 12) {
                    int count = buffer.readUnsignedByte();
                    for (int i = 0; i < count; i++) buffer.readUnsignedShort();
                    for (int i = 0; i < count; i++) buffer.readUnsignedShort();
                } else {
                    throw new IOException("Unsupported 443 sequence opcode " + opcode);
                }
            }
        } catch (RuntimeException exception) {
            throw new IOException("Truncated 443 sequence", exception);
        }
        if (sequence.frameCount == 0) {
            sequence.frameCount = 1;
            sequence.frameIds = new int[]{-1};
            sequence.secondaryFrameIds = new int[]{-1};
            sequence.frameLengths = new int[]{-1};
        }
        if (sequence.precedenceAnimating == -1) {
            sequence.precedenceAnimating = sequence.interleaveOrder == null ? 0 : 2;
        }
        if (sequence.priority == -1) {
            sequence.priority = sequence.interleaveOrder == null ? 0 : 2;
        }
        return sequence;
    }
}
