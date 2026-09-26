package client;

import java.util.Arrays;

/** Revision-specific varp state used by 443 configs and morphed locs. */
public final class Varps {
    private static int[] values = new int[0];

    private Varps() {
    }

    public static synchronized void ensureCapacity(int count) {
        if (count < 0) throw new IllegalArgumentException("Negative 443 varp count");
        if (values.length < count) values = Arrays.copyOf(values, count);
    }

    public static synchronized int size() {
        return values.length;
    }

    public static synchronized int get(int index) {
        return index >= 0 && index < values.length ? values[index] : 0;
    }

    public static synchronized void set(int index, int value) {
        if (index < 0) throw new IllegalArgumentException("Negative 443 varp index");
        ensureCapacity(index + 1);
        values[index] = value;
    }

    public static synchronized int[] snapshot() {
        return values.clone();
    }

    public static synchronized void reset() {
        Arrays.fill(values, 0);
    }
}
