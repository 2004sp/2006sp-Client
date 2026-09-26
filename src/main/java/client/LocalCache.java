package client;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/** Reads revision 443 containers directly from a local dat2/idx cache. */
public final class LocalCache implements Closeable, ContainerSource {
    private static final int ARCHIVE_COUNT = 14;
    private static final int MAX_GROUP_LENGTH = 10_000_000;
    private static final int SECTOR_SIZE = 520;

    private final File directory;
    private final RandomAccessFile data;
    private final Map<Integer, RandomAccessFile> indexes = new HashMap<Integer, RandomAccessFile>();

    public LocalCache(File directory) throws IOException {
        this.directory = directory;
        File dat2 = new File(directory, "main_file_cache.dat2");
        if (!dat2.isFile()) {
            throw new FileNotFoundException("Missing revision 443 cache data: " + dat2);
        }
        data = new RandomAccessFile(dat2, "r");
    }

    @Override
    public byte[] requestContainer(int archive, int group) throws IOException {
        if (archive == 255 && group == 255) {
            byte[] master = new byte[5 + ARCHIVE_COUNT * 4];
            master[4] = (byte) (ARCHIVE_COUNT * 4);
            int[] crcs = readMasterCrcs();
            for (int i = 0; i < crcs.length; i++) {
                int offset = 5 + i * 4;
                master[offset] = (byte) (crcs[i] >>> 24);
                master[offset + 1] = (byte) (crcs[i] >>> 16);
                master[offset + 2] = (byte) (crcs[i] >>> 8);
                master[offset + 3] = (byte) crcs[i];
            }
            return master;
        }
        if (archive < 0 || archive > 255 || group < 0 || group > 65535) {
            throw new IllegalArgumentException("Invalid JS5 group " + archive + ":" + group);
        }
        RandomAccessFile index = openIndex(archive);
        long entry = (long) group * 6;
        if (entry + 6 > index.length()) {
            throw new IOException("Missing JS5 group " + archive + ":" + group);
        }
        index.seek(entry);
        int length = readMedium(index);
        int sector = readMedium(index);
        if (length < 5 || length > MAX_GROUP_LENGTH || sector == 0) {
            throw new IOException("Invalid JS5 index entry " + archive + ":" + group);
        }

        ByteArrayOutputStream result = new ByteArrayOutputStream(length);
        int chunk = 0;
        while (result.size() < length) {
            if (sector <= 0 || (long) sector * SECTOR_SIZE + 8 > data.length()) {
                throw new IOException("Invalid JS5 sector " + archive + ":" + group);
            }
            data.seek((long) sector * SECTOR_SIZE);
            int storedGroup = data.readUnsignedShort();
            int storedChunk = data.readUnsignedShort();
            int nextSector = readMedium(data);
            int storedArchive = data.readUnsignedByte();
            if (storedGroup != group || storedChunk != chunk || storedArchive != archive) {
                throw new IOException("JS5 sector header mismatch " + archive + ":" + group);
            }
            int count = Math.min(512, length - result.size());
            byte[] block = new byte[count];
            data.readFully(block);
            result.write(block);
            sector = nextSector;
            chunk++;
        }
        byte[] raw = result.toByteArray();
        int compression = raw[0] & 255;
        int compressedLength = (raw[1] & 255) << 24 | (raw[2] & 255) << 16
                | (raw[3] & 255) << 8 | raw[4] & 255;
        long containerLength = (compression == 0 ? 5L : 9L) + compressedLength;
        if (compression > 2 || compressedLength < 0 || containerLength > raw.length) {
            throw new IOException("Invalid JS5 container " + archive + ":" + group);
        }
        byte[] container = new byte[(int) containerLength];
        System.arraycopy(raw, 0, container, 0, container.length);
        return container;
    }

    @Override
    public int[] readMasterCrcs() throws IOException {
        int[] crcs = new int[ARCHIVE_COUNT];
        for (int archive = 0; archive < ARCHIVE_COUNT; archive++) {
            byte[] container = requestContainer(255, archive);
            CRC32 crc = new CRC32();
            crc.update(container);
            crcs[archive] = (int) crc.getValue();
        }
        return crcs;
    }

    private RandomAccessFile openIndex(int archive) throws IOException {
        RandomAccessFile index = indexes.get(archive);
        if (index == null) {
            File file = new File(directory, "main_file_cache.idx" + archive);
            if (!file.isFile()) {
                throw new FileNotFoundException("Missing JS5 index: " + file);
            }
            index = new RandomAccessFile(file, "r");
            indexes.put(archive, index);
        }
        return index;
    }

    private static int readMedium(RandomAccessFile file) throws IOException {
        return file.readUnsignedByte() << 16 | file.readUnsignedByte() << 8
                | file.readUnsignedByte();
    }

    @Override
    public void close() throws IOException {
        IOException failure = null;
        for (RandomAccessFile index : indexes.values()) {
            try {
                index.close();
            } catch (IOException exception) {
                failure = exception;
            }
        }
        try {
            data.close();
        } catch (IOException exception) {
            failure = exception;
        }
        indexes.clear();
        if (failure != null) {
            throw failure;
        }
    }
}
