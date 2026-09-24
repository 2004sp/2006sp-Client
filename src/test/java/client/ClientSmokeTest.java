package client;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;

public final class ClientSmokeTest {
    private ClientSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        checkCacheStore();
        checkChatCodec();
        System.out.println("Client smoke checks passed.");
    }

    private static void checkCacheStore() throws Exception {
        File data = Files.createTempFile("client-cache-data-", ".dat").toFile();
        File index = Files.createTempFile("client-cache-index-", ".idx").toFile();
        try (RandomAccessFile dataFile = new RandomAccessFile(data, "rw");
             RandomAccessFile indexFile = new RandomAccessFile(index, "rw")) {
            CacheStore store = new CacheStore(dataFile, indexFile, 1);
            byte[] payload = new byte[1200];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i * 37);
            }
            require(store.write(payload.length, payload, 42), "multi-sector cache write failed");
            require(Arrays.equals(payload, store.read(42)), "multi-sector cache read differed");
            require(store.read(43) == null, "missing cache entry should return null");

            byte[] replacement = new byte[700];
            Arrays.fill(replacement, (byte) 0x5a);
            require(store.write(replacement.length, replacement, 42), "cache overwrite failed");
            require(Arrays.equals(replacement, store.read(42)), "cache overwrite read differed");
        } finally {
            data.delete();
            index.delete();
        }
    }

    private static void checkChatCodec() {
        Buffer encoded = new Buffer(new byte[100]);
        ChatCodec.encode("hello world!", encoded);
        int length = encoded.currentPosition;
        encoded.currentPosition = 0;
        require("Hello world!".equals(ChatCodec.decode(length, encoded)), "chat round trip differed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
