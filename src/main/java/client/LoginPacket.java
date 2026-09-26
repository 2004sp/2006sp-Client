package client;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/** Builds the revision 443 login payload after the server seed is received. */
final class LoginPacket {
    private static final BigInteger RSA_MODULUS = new BigInteger(
            "126281243334509621910786157961571648139029640444686961553514423188662257084412132099345405190869140255510782101811050242941632064063371431334829733868500675557132805905863594614007916107891529024023784039950030199813062171961096886168210646453313260607895180672957089197913358703168034604511968295223858703073");
    private static final int ARCHIVE_COUNT = 14;

    private LoginPacket() {
    }

    static void write(Buffer packet, Buffer credentials, String username, String password,
                      boolean reconnecting, int uid, int[] seed, int[] archiveCrcs)
            throws UnsupportedEncodingException {
        if (seed == null || seed.length != 4 || archiveCrcs == null
                || archiveCrcs.length != ARCHIVE_COUNT) {
            throw new IllegalArgumentException("Revision 443 login needs four seeds and 14 CRCs");
        }
        SceneObjects.clearPending();
        Varps.reset();
        credentials.currentPosition = 0;
        credentials.writeByte(10);
        for (int value : seed) {
            credentials.writeInt(value);
        }
        credentials.writeInt(uid);
        credentials.writeLong(NameUtils.encodeBase37(username));
        byte[] passwordBytes = password.getBytes("ISO-8859-1");
        credentials.writeBytes(passwordBytes, passwordBytes.length, 0);
        credentials.writeByte(0);
        credentials.encryptRsa(RSA_MODULUS);

        int payloadLength = 4 + 1 + ARCHIVE_COUNT * 4 + credentials.currentPosition;
        if (payloadLength > 255) {
            throw new IllegalArgumentException("Revision 443 login payload is too long");
        }
        packet.currentPosition = 0;
        packet.writeByte(reconnecting ? 18 : 16);
        packet.writeByte(payloadLength);
        packet.writeInt(443);
        packet.writeByte(0); // normal memory mode
        for (int crc : archiveCrcs) {
            packet.writeInt(crc);
        }
        packet.writeBytes(credentials.buffer, credentials.currentPosition, 0);
    }
}
