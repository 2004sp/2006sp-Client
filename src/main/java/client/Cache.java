package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

/** Reads revision 443 JS5 reference tables and individual files over an update connection. */
public final class Cache {
    private static final int MAX_DECOMPRESSED_LENGTH = 10_000_000;

    private final ContainerSource updateClient;
    private final int[] archiveCrcs;
    private final Map<Integer, ReferenceTable> referenceTables =
            new LinkedHashMap<Integer, ReferenceTable>();
    private final Map<Long, Map<Integer, byte[]>> decodedGroups =
            new LinkedHashMap<Long, Map<Integer, byte[]>>(32, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, Map<Integer, byte[]>> eldest) {
                    return size() > 32;
                }
            };

    public Cache(ContainerSource updateClient) throws IOException {
        this.updateClient = updateClient;
        this.archiveCrcs = updateClient.readMasterCrcs();
    }

    public int[] getArchiveCrcs() {
        return archiveCrcs.clone();
    }

    public ReferenceTable readReferenceTable(int archive) throws IOException {
        if (archive < 0 || archive >= archiveCrcs.length) {
            throw new IllegalArgumentException("Invalid JS5 archive " + archive);
        }
        ReferenceTable cached = referenceTables.get(archive);
        if (cached != null) {
            return cached;
        }
        byte[] container = updateClient.requestContainer(255, archive);
        verifyCrc(container, archiveCrcs[archive], "reference table " + archive);
        ReferenceTable table = ReferenceTable.decode(decodeContainer(container));
        referenceTables.put(archive, table);
        return table;
    }

    public Map<Integer, byte[]> readFiles(int archive, int group) throws IOException {
        return readFiles(archive, group, null);
    }

    /** Reads a group encrypted with XTEA, as used by revision 443 location maps. */
    public Map<Integer, byte[]> readFiles(int archive, int group, int[] xteaKey)
            throws IOException {
        if (xteaKey != null && xteaKey.length != 4) {
            throw new IllegalArgumentException("XTEA requires four key words");
        }
        long key = ((long) archive << 16) | group;
        Map<Integer, byte[]> cached = xteaKey == null ? decodedGroups.get(key) : null;
        if (cached != null) {
            return cached;
        }
        ReferenceTable table = readReferenceTable(archive);
        int[] fileIds = table.getFileIds(group);
        if (fileIds == null) {
            throw new IOException("Missing JS5 group " + archive + ":" + group);
        }
        byte[] container = updateClient.requestContainer(archive, group);
        verifyCrc(container, table.getGroupCrc(group), "group " + archive + ":" + group);
        if (xteaKey != null) {
            container = decryptXtea(container, xteaKey);
        }
        Map<Integer, byte[]> files = unpackGroup(decodeContainer(container), fileIds);
        if (xteaKey == null) {
            decodedGroups.put(key, files);
        }
        return files;
    }

    public byte[] readFile(int archive, int group, int file) throws IOException {
        return readFile(archive, group, file, null);
    }

    public byte[] readFile(int archive, int group, int file, int[] xteaKey)
            throws IOException {
        byte[] data = readFiles(archive, group, xteaKey).get(file);
        if (data == null) {
            throw new IOException("Missing JS5 file " + archive + ":" + group + ":" + file);
        }
        return data;
    }

    private static byte[] decryptXtea(byte[] container, int[] key) {
        byte[] decoded = container.clone();
        // JS5 leaves the compression type and compressed length unencrypted.
        for (int offset = 5; offset + 8 <= decoded.length; offset += 8) {
            int left = readInt(decoded, offset);
            int right = readInt(decoded, offset + 4);
            int sum = 0xC6EF3720;
            for (int round = 0; round < 32; round++) {
                right -= (((left << 4) ^ (left >>> 5)) + left)
                        ^ (sum + key[(sum >>> 11) & 3]);
                sum -= 0x9E3779B9;
                left -= (((right << 4) ^ (right >>> 5)) + right)
                        ^ (sum + key[sum & 3]);
            }
            writeInt(decoded, offset, left);
            writeInt(decoded, offset + 4, right);
        }
        return decoded;
    }

    private static int readInt(byte[] data, int offset) {
        return (data[offset] & 255) << 24 | (data[offset + 1] & 255) << 16
                | (data[offset + 2] & 255) << 8 | data[offset + 3] & 255;
    }

    private static void writeInt(byte[] data, int offset, int value) {
        data[offset] = (byte) (value >>> 24);
        data[offset + 1] = (byte) (value >>> 16);
        data[offset + 2] = (byte) (value >>> 8);
        data[offset + 3] = (byte) value;
    }

    public byte[] readFile(int archive, String groupName, String fileName)
            throws IOException {
        ReferenceTable table = readReferenceTable(archive);
        int group = table.getGroupId(groupName);
        if (group < 0) {
            throw new IOException("Missing JS5 group " + archive + ":" + groupName);
        }
        int file = table.getFileId(group, fileName);
        if (file < 0) {
            throw new IOException("Missing JS5 file " + archive + ":" + groupName
                    + ":" + fileName);
        }
        return readFile(archive, group, file);
    }

    private static void verifyCrc(byte[] data, int expected, String name) throws IOException {
        CRC32 crc = new CRC32();
        crc.update(data);
        if ((int) crc.getValue() != expected) {
            throw new IOException("JS5 CRC mismatch for " + name);
        }
    }

    static byte[] decodeContainer(byte[] container) throws IOException {
        if (container.length < 5) {
            throw new EOFException("Truncated JS5 container");
        }
        DataInputStream header = new DataInputStream(new ByteArrayInputStream(container));
        int compression = header.readUnsignedByte();
        int compressedLength = header.readInt();
        if (compressedLength < 0 || compressedLength > MAX_DECOMPRESSED_LENGTH) {
            throw new IOException("Invalid JS5 compressed length");
        }
        if (compression == 0) {
            if (container.length != 5 + compressedLength) {
                throw new IOException("Invalid uncompressed JS5 container length");
            }
            byte[] decoded = new byte[compressedLength];
            System.arraycopy(container, 5, decoded, 0, decoded.length);
            return decoded;
        }
        if (compression != 1 && compression != 2 || container.length < 9
                || container.length != 9 + compressedLength) {
            throw new IOException("Invalid compressed JS5 container");
        }
        int uncompressedLength = header.readInt();
        if (uncompressedLength < 0 || uncompressedLength > MAX_DECOMPRESSED_LENGTH) {
            throw new IOException("Invalid JS5 decompressed length");
        }
        byte[] decoded;
        if (compression == 1) {
            int offset = 9;
            int length = compressedLength;
            if (length >= 4 && container[offset] == 'B' && container[offset + 1] == 'Z'
                    && container[offset + 2] == 'h') {
                offset += 4;
                length -= 4;
            }
            decoded = new byte[uncompressedLength];
            int count = BZip2Decompressor.decompress(decoded, decoded.length, container,
                    length, offset);
            if (count != uncompressedLength) {
                throw new IOException("JS5 BZip2 length mismatch");
            }
        } else {
            GZIPInputStream gzip = new GZIPInputStream(
                    new ByteArrayInputStream(container, 9, compressedLength));
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream(uncompressedLength);
                byte[] buffer = new byte[8192];
                int count;
                while ((count = gzip.read(buffer)) != -1) {
                    if (output.size() + count > uncompressedLength) {
                        throw new IOException("JS5 GZIP length mismatch");
                    }
                    output.write(buffer, 0, count);
                }
                decoded = output.toByteArray();
            } finally {
                gzip.close();
            }
            if (decoded.length != uncompressedLength) {
                throw new IOException("JS5 GZIP length mismatch");
            }
        }
        return decoded;
    }

    private static Map<Integer, byte[]> unpackGroup(byte[] group, int[] fileIds)
            throws IOException {
        Map<Integer, byte[]> files = new LinkedHashMap<Integer, byte[]>();
        if (fileIds.length == 0) {
            return files;
        }
        if (fileIds.length == 1) {
            files.put(fileIds[0], group);
            return files;
        }
        if (group.length == 0) {
            throw new IOException("Empty multi-file JS5 group");
        }
        int chunks = group[group.length - 1] & 255;
        long tableLength = (long) chunks * fileIds.length * 4;
        long tableOffsetLong = group.length - 1L - tableLength;
        if (chunks == 0 || tableOffsetLong < 0) {
            throw new IOException("Invalid JS5 group chunk table");
        }
        int tableOffset = (int) tableOffsetLong;
        DataInputStream table = new DataInputStream(new ByteArrayInputStream(group,
                tableOffset, (int) tableLength));
        int[] sizes = new int[fileIds.length];
        for (int chunk = 0; chunk < chunks; chunk++) {
            int cumulative = 0;
            for (int file = 0; file < fileIds.length; file++) {
                long next = (long) cumulative + table.readInt();
                if (next < 0 || next > tableOffset
                        || (long) sizes[file] + next > tableOffset) {
                    throw new IOException("Invalid JS5 file size");
                }
                cumulative = (int) next;
                sizes[file] += cumulative;
            }
        }
        byte[][] contents = new byte[fileIds.length][];
        int[] offsets = new int[fileIds.length];
        for (int file = 0; file < fileIds.length; file++) {
            contents[file] = new byte[sizes[file]];
        }
        table = new DataInputStream(new ByteArrayInputStream(group, tableOffset,
                (int) tableLength));
        int sourceOffset = 0;
        for (int chunk = 0; chunk < chunks; chunk++) {
            int cumulative = 0;
            for (int file = 0; file < fileIds.length; file++) {
                long next = (long) cumulative + table.readInt();
                if (next < 0 || (long) sourceOffset + next > tableOffset
                        || (long) offsets[file] + next > contents[file].length) {
                    throw new IOException("Invalid JS5 group chunk size");
                }
                cumulative = (int) next;
                System.arraycopy(group, sourceOffset, contents[file], offsets[file], cumulative);
                sourceOffset += cumulative;
                offsets[file] += cumulative;
            }
        }
        if (sourceOffset != tableOffset) {
            throw new IOException("JS5 group data/table boundary mismatch");
        }
        for (int file = 0; file < fileIds.length; file++) {
            files.put(fileIds[file], contents[file]);
        }
        return files;
    }

    public static final class ReferenceTable {
        private final int[] groupIds;
        private final int[] groupCrcs;
        private final Map<Integer, int[]> fileIds;
        private final Map<Integer, Integer> namedGroups;
        private final Map<Integer, Map<Integer, Integer>> namedFiles;

        private ReferenceTable(int[] groupIds, int[] groupCrcs,
                               Map<Integer, int[]> fileIds,
                               Map<Integer, Integer> namedGroups,
                               Map<Integer, Map<Integer, Integer>> namedFiles) {
            this.groupIds = groupIds;
            this.groupCrcs = groupCrcs;
            this.fileIds = fileIds;
            this.namedGroups = namedGroups;
            this.namedFiles = namedFiles;
        }

        static ReferenceTable decode(byte[] data) throws IOException {
            try {
                DataInputStream input = new DataInputStream(new ByteArrayInputStream(data));
                int protocol = input.readUnsignedByte();
                if (protocol != 5 && protocol != 6) {
                    throw new IOException("Unsupported JS5 reference table protocol: " + protocol);
                }
                if (protocol == 6) {
                    input.readInt(); // table version
                }
                int flags = input.readUnsignedByte();
                if ((flags & ~1) != 0) {
                    throw new IOException("Unsupported JS5 reference table flags: " + flags);
                }
                boolean named = (flags & 1) != 0;
                int groupCount = input.readUnsignedShort();
                int[] groupIds = new int[groupCount];
                int[] groupCrcs = new int[groupCount];
                Map<Integer, Integer> namedGroups = new LinkedHashMap<Integer, Integer>();
                Map<Integer, Map<Integer, Integer>> namedFiles =
                        new LinkedHashMap<Integer, Map<Integer, Integer>>();
                int groupId = 0;
                for (int i = 0; i < groupCount; i++) {
                    groupId += input.readUnsignedShort();
                    groupIds[i] = groupId;
                }
                if (named) {
                    for (int i = 0; i < groupCount; i++) {
                        namedGroups.put(input.readInt(), groupIds[i]);
                    }
                }
                for (int i = 0; i < groupCount; i++) groupCrcs[i] = input.readInt();
                for (int i = 0; i < groupCount; i++) input.readInt(); // group versions
                int[] fileCounts = new int[groupCount];
                for (int i = 0; i < groupCount; i++) fileCounts[i] = input.readUnsignedShort();
                Map<Integer, int[]> fileIds = new LinkedHashMap<Integer, int[]>();
                for (int i = 0; i < groupCount; i++) {
                    int[] ids = new int[fileCounts[i]];
                    int fileId = 0;
                    for (int j = 0; j < ids.length; j++) {
                        fileId += input.readUnsignedShort();
                        ids[j] = fileId;
                    }
                    fileIds.put(groupIds[i], ids);
                }
                if (named) {
                    for (int i = 0; i < groupCount; i++) {
                        int[] ids = fileIds.get(groupIds[i]);
                        Map<Integer, Integer> names = new LinkedHashMap<Integer, Integer>();
                        for (int j = 0; j < ids.length; j++) {
                            names.put(input.readInt(), ids[j]);
                        }
                        namedFiles.put(groupIds[i], names);
                    }
                }
                if (input.available() != 0) {
                    throw new IOException("Trailing JS5 reference table data");
                }
                return new ReferenceTable(groupIds, groupCrcs, fileIds,
                        namedGroups, namedFiles);
            } catch (EOFException exception) {
                throw new IOException("Truncated JS5 reference table", exception);
            }
        }

        public int[] getGroupIds() {
            return groupIds.clone();
        }

        public int[] getFileIds(int group) {
            int[] ids = fileIds.get(group);
            return ids == null ? null : ids.clone();
        }

        public int getGroupId(String name) {
            Integer id = namedGroups.get(nameHash(name));
            return id == null ? -1 : id;
        }

        public int getFileId(int group, String name) {
            Map<Integer, Integer> names = namedFiles.get(group);
            Integer id = names == null ? null : names.get(nameHash(name));
            return id == null ? -1 : id;
        }

        private static int nameHash(String name) {
            int hash = 0;
            // Revision 443 class6.method48/method27 lowercases Latin-1 bytes
            // before applying the ordinary 31-based string hash.
            for (int i = 0; i < name.length(); i++) {
                int value = name.charAt(i);
                if (value > 255) {
                    throw new IllegalArgumentException("JS5 names must be Latin-1");
                }
                if (value >= 'A' && value <= 'Z'
                        || value >= 192 && value <= 222 && value != 215) {
                    value += 32;
                }
                hash = hash * 31 + value;
            }
            return hash;
        }

        int getGroupCrc(int group) throws IOException {
            for (int i = 0; i < groupIds.length; i++) {
                if (groupIds[i] == group) return groupCrcs[i];
            }
            throw new IOException("Missing JS5 group " + group);
        }
    }
}
