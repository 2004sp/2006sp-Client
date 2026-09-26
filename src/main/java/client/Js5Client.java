package client;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/** The revision 443 update connection. Each response is framed in 512-byte blocks. */
public final class Js5Client implements Closeable, ContainerSource {
    private static final int REVISION = 443;
    private static final int ARCHIVE_COUNT = 14;
    private static final int MAX_GROUP_LENGTH = 10_000_000;

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public Js5Client(Socket socket) throws IOException {
        this.socket = socket;
        try {
            socket.setSoTimeout(30_000);
            socket.setTcpNoDelay(true);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            output.writeByte(15);
            output.writeInt(REVISION);
            output.flush();
            int result = input.readUnsignedByte();
            if (result != 0) {
                throw new IOException("Revision 443 update handshake rejected: " + result);
            }
        } catch (IOException exception) {
            try {
                socket.close();
            } catch (IOException closeFailure) {
                exception.addSuppressed(closeFailure);
            }
            throw exception;
        }
    }

    public int[] readMasterCrcs() throws IOException {
        byte[] master = requestContainer(255, 255);
        if (master.length != 5 + ARCHIVE_COUNT * 4 || master[0] != 0
                || readInt(master, 1) != ARCHIVE_COUNT * 4) {
            throw new IOException("Invalid revision 443 master index");
        }
        int[] crcs = new int[ARCHIVE_COUNT];
        for (int archive = 0; archive < crcs.length; archive++) {
            crcs[archive] = readInt(master, 5 + archive * 4);
        }
        return crcs;
    }

    public byte[] requestContainer(int archive, int group) throws IOException {
        if (archive < 0 || archive > 255 || group < 0 || group > 65535) {
            throw new IllegalArgumentException("Invalid JS5 group " + archive + ":" + group);
        }
        output.writeByte(1); // urgent request
        output.writeByte(archive);
        output.writeShort(group);
        output.flush();

        int responseArchive = input.readUnsignedByte();
        int responseGroup = input.readUnsignedShort();
        if (responseArchive != archive || responseGroup != group) {
            throw new IOException("Unexpected JS5 response " + responseArchive + ":"
                    + responseGroup + " for " + archive + ":" + group);
        }
        int compression = input.readUnsignedByte();
        int compressedLength = input.readInt();
        if (compression > 2 || compressedLength < 0
                || compressedLength > MAX_GROUP_LENGTH - (compression == 0 ? 0 : 4)) {
            throw new IOException("Invalid JS5 container length or compression");
        }
        int bodyLength = compressedLength + (compression == 0 ? 0 : 4);
        ByteArrayOutputStream container = new ByteArrayOutputStream(5 + bodyLength);
        container.write(compression);
        container.write(compressedLength >>> 24);
        container.write(compressedLength >>> 16);
        container.write(compressedLength >>> 8);
        container.write(compressedLength);

        int remaining = bodyLength;
        int blockSize = 504; // first block has the eight-byte response header
        while (remaining > 0) {
            byte[] block = new byte[Math.min(remaining, blockSize)];
            input.readFully(block);
            container.write(block);
            remaining -= block.length;
            if (remaining > 0) {
                if (input.readUnsignedByte() != 255) {
                    throw new IOException("Missing JS5 continuation marker");
                }
                blockSize = 511;
            }
        }
        return container.toByteArray();
    }

    private static int readInt(byte[] bytes, int offset) {
        return (bytes[offset] & 255) << 24 | (bytes[offset + 1] & 255) << 16
                | (bytes[offset + 2] & 255) << 8 | bytes[offset + 3] & 255;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
