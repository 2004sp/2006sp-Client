package client;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;
import java.util.zip.GZIPOutputStream;

public final class ClientSmokeChecks {
    private ClientSmokeChecks() {
    }

    public static void main(String[] args) throws Exception {
        checkCacheStore();
        checkChatCodec();
        checkRevision443Js5Wire();
        checkRevision443CacheFiles();
        checkRevision443FontMask();
        checkRevision443BzipContainer();
        checkRevision443LoginPacket();
        checkRevision443PacketFraming();
        checkRevision443InterfaceProgress();
        checkRevision443RegionPacket();
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

    private static void checkRevision443FontMask() throws Exception {
        byte[] sprite = {
                0, 1, 2, 1, 2, 1, 2, 2, 2, 2,
                0, 0, 0, (byte) 255, (byte) 255, (byte) 255,
                0, 3, 0, 3, 2,
                0, 0, 0, 0, 0, 3, 0, 3,
                0, 1
        };
        byte[] mask = Sprites.decode(sprite)[0].toFontMask();
        require(Arrays.equals(mask, new byte[]{0, 1, 0, 1, 0, 1, 1, 1, 1}),
                "443 font background must remain transparent");
    }

    private static void checkChatCodec() {
        Buffer encoded = new Buffer(new byte[100]);
        ChatCodec.encode("hello world!", encoded);
        int length = encoded.currentPosition;
        encoded.currentPosition = 0;
        require("Hello world!".equals(ChatCodec.decode(length, encoded)), "chat round trip differed");
    }

    private static void checkRevision443LoginPacket() throws Exception {
        Buffer packet = new Buffer(new byte[512]);
        Buffer credentials = new Buffer(new byte[512]);
        int[] seed = {17, 23, 31, 47};
        int[] crcs = new int[14];
        for (int i = 0; i < crcs.length; i++) {
            crcs[i] = i * 1009 + 3;
        }
        LoginPacket.write(packet, credentials, "Test user", "pAss123",
                false, 123456, seed, crcs);
        byte[] wire = Arrays.copyOf(packet.buffer, packet.currentPosition);
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(wire));
        require(input.readUnsignedByte() == 16 && input.readUnsignedByte() == wire.length - 2,
                "443 login frame has the wrong type or length");
        require(input.readInt() == 443 && input.readUnsignedByte() == 0,
                "443 login revision or memory flag is wrong");
        for (int crc : crcs) {
            require(input.readInt() == crc, "443 login archive CRC is wrong");
        }
        int encryptedLength = input.readUnsignedByte();
        byte[] encrypted = new byte[encryptedLength];
        input.readFully(encrypted);
        require(input.available() == 0, "443 login has unexpected trailing bytes");

        BigInteger modulus = new BigInteger(
                "126281243334509621910786157961571648139029640444686961553514423188662257084412132099345405190869140255510782101811050242941632064063371431334829733868500675557132805905863594614007916107891529024023784039950030199813062171961096886168210646453313260607895180672957089197913358703168034604511968295223858703073");
        BigInteger privateExponent = new BigInteger(
                "30652639256685216982113709825788207861142309232862050206651318249008028834075838037084192223878820867367525545502695992564741792805897626221439361069016103707546910916876758973004891298232729104728327473982300624040057756427674392115989911877923828003372350341670381425233062849579939381994254110724498182865");
        byte[] plain = new BigInteger(1, encrypted).modPow(privateExponent, modulus).toByteArray();
        int offset = plain.length > 1 && plain[0] == 0 ? 1 : 0;
        DataInputStream decoded = new DataInputStream(new ByteArrayInputStream(
                plain, offset, plain.length - offset));
        require(decoded.readUnsignedByte() == 10, "443 RSA marker is wrong");
        for (int value : seed) {
            require(decoded.readInt() == value, "443 ISAAC seed is wrong");
        }
        require(decoded.readInt() == 123456, "443 UID is wrong");
        require(decoded.readLong() == NameUtils.encodeBase37("Test user"),
                "443 username hash is wrong");
        byte[] password = new byte[7];
        decoded.readFully(password);
        require(Arrays.equals(password, "pAss123".getBytes("ISO-8859-1"))
                        && decoded.readUnsignedByte() == 0 && decoded.available() == 0,
                "443 password is wrong");
    }

    private static void checkRevision443Js5Wire() throws Exception {
        final byte[] body = new byte[600];
        for (int i = 0; i < body.length; i++) {
            body[i] = (byte) (i * 17);
        }
        final AtomicReference<Throwable> serverFailure = new AtomicReference<Throwable>();
        try (final ServerSocket listener = new ServerSocket(0)) {
            Thread server = new Thread(new Runnable() {
                @Override
                public void run() {
                    try (Socket socket = listener.accept()) {
                        socket.setSoTimeout(5000);
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                        require(input.readUnsignedByte() == 15 && input.readInt() == 443,
                                "Wrong JS5 handshake");
                        output.writeByte(0);
                        output.flush();

                        checkJs5Request(input, 255, 255);
                        output.writeByte(255);
                        output.writeShort(255);
                        output.writeByte(0);
                        output.writeInt(56);
                        for (int archive = 0; archive < 14; archive++) {
                            output.writeInt(archive * 31 + 7);
                        }
                        output.flush();

                        checkJs5Request(input, 2, 10);
                        output.writeByte(2);
                        output.writeShort(10);
                        output.writeByte(0);
                        output.writeInt(body.length);
                        output.write(body, 0, 504);
                        output.writeByte(255);
                        output.write(body, 504, body.length - 504);
                        output.flush();
                    } catch (Throwable failure) {
                        serverFailure.set(failure);
                    }
                }
            }, "js5-smoke-server");
            server.start();
            try (Js5Client client =
                         new Js5Client(new Socket("127.0.0.1", listener.getLocalPort()))) {
                int[] crcs = client.readMasterCrcs();
                require(crcs.length == 14 && crcs[0] == 7 && crcs[13] == 410,
                        "443 master CRCs were decoded incorrectly");
                byte[] group = client.requestContainer(2, 10);
                require(group.length == body.length + 5 && group[0] == 0,
                        "443 group container length is wrong");
                require(Arrays.equals(body, Arrays.copyOfRange(group, 5, group.length)),
                        "443 JS5 continuation block was assembled incorrectly");
            }
            server.join(5000);
            require(!server.isAlive(), "JS5 smoke server did not finish");
            if (serverFailure.get() != null) {
                throw new AssertionError("JS5 smoke server failed", serverFailure.get());
            }
        }
    }

    private static void checkJs5Request(DataInputStream input, int archive, int group)
            throws Exception {
        require(input.readUnsignedByte() == 1 && input.readUnsignedByte() == archive
                        && input.readUnsignedShort() == group, "Wrong JS5 request");
    }

    private static void checkRevision443CacheFiles() throws Exception {
        ByteArrayOutputStream groupBytes = new ByteArrayOutputStream();
        DataOutputStream group = new DataOutputStream(groupBytes);
        group.writeBytes("ABWXYZ");
        group.writeInt(2); // first file contributes two bytes
        group.writeInt(2); // second file contributes four cumulative bytes
        group.writeByte(1); // one interleaved chunk
        final byte[] groupContainer = gzipContainer(groupBytes.toByteArray());

        ByteArrayOutputStream tableBytes = new ByteArrayOutputStream();
        DataOutputStream table = new DataOutputStream(tableBytes);
        table.writeByte(6); // reference table protocol
        table.writeInt(1); // version
        table.writeByte(1); // named groups and files
        table.writeShort(1); // group count
        table.writeShort(10); // first group ID
        table.writeInt(js5NameHash("example"));
        table.writeInt(crc(groupContainer));
        table.writeInt(1); // group version
        table.writeShort(2); // file count
        table.writeShort(1); // file ID 1
        table.writeShort(2); // file ID 3
        table.writeInt(js5NameHash("first"));
        table.writeInt(js5NameHash("second"));
        final byte[] tableContainer = plainContainer(tableBytes.toByteArray());

        ByteArrayOutputStream masterBytes = new ByteArrayOutputStream();
        DataOutputStream master = new DataOutputStream(masterBytes);
        for (int archive = 0; archive < 14; archive++) {
            master.writeInt(archive == 2 ? crc(tableContainer) : 0);
        }
        final byte[] masterContainer = plainContainer(masterBytes.toByteArray());

        final AtomicReference<Throwable> serverFailure = new AtomicReference<Throwable>();
        try (final ServerSocket listener = new ServerSocket(0)) {
            Thread server = new Thread(new Runnable() {
                @Override
                public void run() {
                    try (Socket socket = listener.accept()) {
                        socket.setSoTimeout(5000);
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                        require(input.readUnsignedByte() == 15 && input.readInt() == 443,
                                "Wrong cache JS5 handshake");
                        output.writeByte(0);
                        checkJs5Request(input, 255, 255);
                        sendJs5Response(output, 255, 255, masterContainer);
                        checkJs5Request(input, 255, 2);
                        sendJs5Response(output, 255, 2, tableContainer);
                        checkJs5Request(input, 2, 10);
                        sendJs5Response(output, 2, 10, groupContainer);
                    } catch (Throwable failure) {
                        serverFailure.set(failure);
                    }
                }
            }, "js5-cache-smoke-server");
            server.start();
            try (Js5Client transport =
                         new Js5Client(new Socket("127.0.0.1", listener.getLocalPort()))) {
                Cache cache = new Cache(transport);
                require(cache.readReferenceTable(2).getGroupIds()[0] == 10,
                        "443 reference group ID is wrong");
                require(cache.readReferenceTable(2).getGroupId("EXAMPLE") == 10
                                && cache.readReferenceTable(2).getFileId(10, "Second") == 3
                                && cache.readReferenceTable(2).getGroupId("missing") == -1,
                        "443 named JS5 lookups were decoded incorrectly");
                require(Arrays.equals(cache.readFile(2, 10, 3), "WXYZ".getBytes("US-ASCII")),
                        "443 multi-file GZIP group was decoded incorrectly");
                require(Arrays.equals(cache.readFile(2, "Example", "SECOND"),
                                "WXYZ".getBytes("US-ASCII")),
                        "443 named JS5 file lookup was decoded incorrectly");
            }
            server.join(5000);
            require(!server.isAlive(), "JS5 cache smoke server did not finish");
            if (serverFailure.get() != null) {
                throw new AssertionError("JS5 cache smoke server failed", serverFailure.get());
            }
        }
    }

    private static byte[] plainContainer(byte[] payload) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeByte(0);
        output.writeInt(payload.length);
        output.write(payload);
        return bytes.toByteArray();
    }

    private static int js5NameHash(String name) {
        int hash = 0;
        for (int i = 0; i < name.length(); i++) {
            hash = hash * 31 + Character.toLowerCase(name.charAt(i));
        }
        return hash;
    }

    private static byte[] gzipContainer(byte[] payload) throws Exception {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(compressed);
        gzip.write(payload);
        gzip.close();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeByte(2);
        output.writeInt(compressed.size());
        output.writeInt(payload.length);
        output.write(compressed.toByteArray());
        return bytes.toByteArray();
    }

    private static void checkRevision443BzipContainer() throws Exception {
        // BZip2 block for "JS5 BZIP2 TEST", with the standard BZh1 header removed.
        byte[] compressed = decodeHex(
                "3141592653590a4146dc0000039e004000120012304c102000220d01908069a68037a8a8295e2f1772453850900a4146dc");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream container = new DataOutputStream(bytes);
        container.writeByte(1);
        container.writeInt(compressed.length);
        container.writeInt(14);
        container.write(compressed);
        require(Arrays.equals(Cache.decodeContainer(bytes.toByteArray()),
                "JS5 BZIP2 TEST".getBytes("US-ASCII")), "443 BZip2 container was decoded incorrectly");
    }

    private static void checkRevision443PacketFraming() throws Exception {
        int tableHash = 1;
        for (int opcode = 0; opcode < 256; opcode++) {
            tableHash = 31 * tableHash + PacketFramer.lengthFor(opcode);
        }
        require(tableHash == 210505171, "Revision 443 packet length table changed");
        require(PacketFramer.lengthFor(3) == 6
                        && PacketFramer.lengthFor(18) == 3
                        && PacketFramer.lengthFor(25) == -1
                        && PacketFramer.lengthFor(29) == -2
                        && PacketFramer.lengthFor(73) == 8,
                "Revision 443 packet lengths differ from the paired protocol");
        int[] seed = {11, 22, 33, 44};
        IsaacCipher encoder = new IsaacCipher(seed.clone());
        IsaacCipher decoder = new IsaacCipher(seed.clone());
        try (ServerSocket listener = new ServerSocket(0);
             Socket clientSocket = new Socket("127.0.0.1", listener.getLocalPort());
             Socket serverSocket = listener.accept()) {
            BufferedConnection connection = new BufferedConnection(null, clientSocket);
            DataOutputStream output = new DataOutputStream(serverSocket.getOutputStream());
            PacketFramer framer = new PacketFramer();

            byte[] fixed = {1, 2, 3, 4, 5, 6};
            output.writeByte((3 + encoder.nextInt()) & 255);
            output.write(fixed);
            output.flush();
            PacketFramer.Packet packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 3 && Arrays.equals(packet.payload, fixed),
                    "Revision 443 fixed packet was framed incorrectly");

            byte[] progress = {74, 67, (byte) 250};
            output.writeByte((18 + encoder.nextInt()) & 255);
            output.write(progress);
            output.flush();
            packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 18 && Arrays.equals(packet.payload, progress),
                    "443 Grand Exchange progress packet was framed incorrectly");

            byte[] smallVarp = {(byte) 255, 0, 43};
            output.writeByte((62 + encoder.nextInt()) & 255);
            output.write(smallVarp);
            output.flush();
            packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 62 && Arrays.equals(packet.payload, smallVarp),
                    "443 small varp packet did not reach the client dispatcher");

            byte[] largeVarp = {0, 0, 0, 1, 1, 45};
            output.writeByte((74 + encoder.nextInt()) & 255);
            output.write(largeVarp);
            output.flush();
            packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 74 && Arrays.equals(packet.payload, largeVarp),
                    "443 large varp packet did not reach the client dispatcher");

            output.writeByte((25 + encoder.nextInt()) & 255);
            output.flush();
            awaitAvailable(connection, 1);
            require(framer.poll(connection, decoder) == null,
                    "Revision 443 packet should wait for its byte length");
            output.writeByte(3);
            output.write(new byte[]{7, 8, 9});
            output.flush();
            packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 25 && Arrays.equals(packet.payload, new byte[]{7, 8, 9}),
                    "Revision 443 byte-length packet was framed incorrectly");

            byte[] longPayload = new byte[300];
            for (int i = 0; i < longPayload.length; i++) longPayload[i] = (byte) i;
            output.writeByte((29 + encoder.nextInt()) & 255);
            output.writeShort(longPayload.length);
            output.write(longPayload);
            output.writeByte(encoder.nextInt() & 255); // opcode 0, empty packet
            output.flush();
            packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 29 && Arrays.equals(packet.payload, longPayload),
                    "Revision 443 short-length packet was framed incorrectly");
            packet = await443Packet(framer, connection, decoder);
            require(packet.opcode == 0 && packet.payload.length == 0,
                    "Revision 443 empty packet was framed incorrectly");
            connection.close();
        }
    }

    private static void checkRevision443InterfaceProgress() throws Exception {
        Widget widget = new Widget();
        widget.baseWidth = 80;
        Widget[] previous = Widget.widgets;
        try {
            Widget.widgets = new Widget[19095];
            Widget.widgets[19011] = widget;
            InterfaceProgress.apply(new byte[] {74, 67, 50});
            require(widget.width == 40 && widget.textColor == Client.packRgb(198, 139, 1),
                    "443 Grand Exchange progress did not apply partial state");
            InterfaceProgress.apply(new byte[] {74, 67, (byte) 250});
            require(widget.width == 80 && widget.textColor == Client.packRgb(138, 0, 16),
                    "443 Grand Exchange progress did not apply cancelled state");
            require(InterfaceProgress.isGrandExchangeBar(19094)
                            && !InterfaceProgress.isGrandExchangeBar(19093),
                    "443 Grand Exchange progress component set changed");
        } finally {
            Widget.widgets = previous;
        }
    }

    private static void checkRevision443RegionPacket() throws Exception {
        byte[] payload = new byte[73];
        payload[0] = (byte) 128;
        payload[1] = 1;
        payload[2] = 51;
        payload[4] = (byte) 178;
        payload[6] = (byte) 130;
        int[] knownKey = {1152832719, 1703327384, -664371384, 1662584632};
        int[][] keys = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                knownKey
        };
        for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
            for (int word = 0; word < 4; word++) {
                int value = keys[keyIndex][word];
                int offset = 7 + keyIndex * 16 + word * 4;
                payload[offset] = (byte) (value >>> 24);
                payload[offset + 1] = (byte) (value >>> 16);
                payload[offset + 2] = (byte) (value >>> 8);
                payload[offset + 3] = (byte) value;
            }
        }
        payload[71] = 1;
        RegionPacket region = RegionPacket.decode(payload);
        require(region.centerX == 384 && region.centerY == 384
                        && region.localX == 50 && region.localY == 51
                        && region.plane == 2 && region.xteaKeys.length == 4
                        && Arrays.equals(region.xteaKeys[3], knownKey),
                "Revision 443 region packet was decoded incorrectly");
        RegionAssets.Request[] requests = RegionAssets.requests(region);
        require(requests.length == 4
                        && requests[0].x == 47 && requests[0].y == 47
                        && requests[3].x == 48 && requests[3].y == 48
                        && Arrays.equals(requests[3].xteaKey, knownKey),
                "Revision 443 map-square/XTEA ordering is wrong");
        checkRevision443ConstructedRegionPacket();
    }

    private static void checkRevision443ConstructedRegionPacket() throws Exception {
        require(PacketFramer.lengthFor(193) == -2,
                "Revision 443 constructed rebuild must use a short length");
        int first = (2 << 24) | (384 << 14) | (385 << 3) | (1 << 1);
        int second = (1 << 24) | (392 << 14) | (400 << 3);
        int bitCount = 4 * 13 * 13 + 52;
        byte[] payload = new byte[(bitCount + 7) / 8 + 32 + 9];
        int bit = 0;
        for (int p = 0; p < 4; p++) {
            for (int x = 0; x < 13; x++) {
                for (int y = 0; y < 13; y++) {
                    int template = p == 0 && x == 0 && y == 0 ? first
                            : p == 3 && x == 12 && y == 12 ? second : -1;
                    if (template == -1) {
                        bit++;
                    } else {
                        payload[bit >> 3] |= 1 << (7 - (bit & 7));
                        bit++;
                        for (int i = 25; i >= 0; i--) {
                            if ((template & 1 << i) != 0) {
                                payload[bit >> 3] |= 1 << (7 - (bit & 7));
                            }
                            bit++;
                        }
                    }
                }
            }
        }
        int offset = (bit + 7) / 8;
        payload[offset + 15] = 1;
        payload[offset + 31] = 2;
        offset += 32;
        payload[offset] = (byte) (52 + 128);
        payload[offset + 2] = (byte) 128;
        payload[offset + 3] = 1;
        payload[offset + 4] = (byte) (50 + 128);
        payload[offset + 6] = (byte) 128;
        payload[offset + 7] = 1;
        payload[offset + 8] = (byte) -2;
        RegionPacket region = RegionPacket.decodeConstructed(payload);
        require(region.centerX == 384 && region.centerY == 384
                        && region.localX == 50 && region.localY == 52
                        && region.plane == 2
                        && region.templates[0][0][0] == first
                        && region.templates[3][12][12] == second
                        && region.templates[1][0][0] == -1,
                "Revision 443 constructed rebuild was decoded incorrectly");
        RegionAssets.Request[] requests = RegionAssets.requests(region);
        require(requests.length == 2 && requests[0].x == 48 && requests[0].y == 48
                        && requests[1].x == 49 && requests[1].y == 50
                        && requests[0].xteaKey[3] == 1
                        && requests[1].xteaKey[3] == 2,
                "Revision 443 constructed source-square/XTEA ordering is wrong");
    }
    private static PacketFramer.Packet await443Packet(
            PacketFramer framer, BufferedConnection connection, IsaacCipher cipher)
            throws Exception {
        long deadline = System.currentTimeMillis() + 5000;
        PacketFramer.Packet packet;
        while ((packet = framer.poll(connection, cipher)) == null) {
            require(System.currentTimeMillis() < deadline, "Timed out reading revision 443 packet");
            Thread.sleep(5);
        }
        return packet;
    }

    private static void awaitAvailable(BufferedConnection connection, int count)
            throws Exception {
        long deadline = System.currentTimeMillis() + 5000;
        while (connection.available() < count) {
            require(System.currentTimeMillis() < deadline, "Timed out waiting for packet bytes");
            Thread.sleep(5);
        }
    }

    private static byte[] decodeHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    private static int crc(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return (int) crc.getValue();
    }

    private static void sendJs5Response(DataOutputStream output, int archive, int group,
                                        byte[] container) throws Exception {
        output.writeByte(archive);
        output.writeShort(group);
        output.write(container, 0, 5);
        int offset = 5;
        int blockSpace = 504;
        while (offset < container.length) {
            int count = Math.min(blockSpace, container.length - offset);
            output.write(container, offset, count);
            offset += count;
            if (offset < container.length) {
                output.writeByte(255);
                blockSpace = 511;
            }
        }
        output.flush();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
