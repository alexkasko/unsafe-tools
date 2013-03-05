package com.alexkasko.unsafe.offheap;

/**
 * Contains static methods for packing two values with limited size (one long and one int)
 * into one long value. May be used to pack values before placing them into {@link OffHeapLongAddressable}.
 * Values are stored using bit shifting as follows:
 * <pre>
 * {@code
 *  0                             32        64-bits             64
 *  +-----------------------------+-----------+------------------+
 *  | first 32 bits of long value | int value | tail of long val |
 *  +-----------------------------+-----------+------------------+
 * }
 * </pre>
 * This class doesn't use {@code sun.misc.Unsafe}
 *
 * @author alexkasko
 * Date: 1/14/13
 */
public class LongPacker {
    /**
     * Packs one long value with limited size and one int value
     * into one long value.
     * Values limits are checking using {@code assert}
     * keyword, java must be run with {@code -ea} switch to enable them.
     *
     * @param big long value
     * @param little int value
     * @param bits max size of long value in bits, max size of int value is {@code 64 - bits}
     * @return packed long value
     */
    public static long pack(long big, int little, int bits) {
        assert bits > 32 && bits < 64;
        assert big < (1L << bits);
        assert little < (1 << (64 - bits));
        int ls = bits % 8;
        int bm = (1 << ls) - 1;
        long res = (big & ~bm) << (64 - bits);
        res |= (little & ((1L << (64 - bits)) - 1)) << ls;
        res |= big & bm;
        return res;
    }

    /**
     * Read long value from packed long.
     * Values limits are checking using {@code assert}
     * keyword, java must be run with {@code -ea} switch to enable them.
     *
     * @param pack packed long
     * @param bits max size of long value in bits, max size of int value is {@code 64 - bits}
     * @return source long value
     */
    public static long big(long pack, int bits) {
        assert bits > 32 && bits < 64;
        int ls = bits % 8;
        long res = (pack & (((1L << (bits - ls)) - 1) << (64 - bits + ls))) >>> (64 - bits);
        return res | pack & ((1 << ls) - 1);
    }

    /**
     *
     * Values limits are checking using {@code assert}
     * keyword, java must be run with {@code -ea} switch to enable them.
     *
     * @param pack packed long
     * @param bits max size of long value in bits, max size of int value is {@code 64 - bits}
     * @return source int value
     */
    public static int little(long pack, int bits) {
        assert bits > 32 && bits < 64;
        int ls = bits % 8;
        return (int) ((pack & (((1L << (64 - bits)) -1) << ls)) >> ls);
    }
}
