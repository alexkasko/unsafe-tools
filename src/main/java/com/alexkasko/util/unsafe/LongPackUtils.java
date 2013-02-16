package com.alexkasko.util.unsafe;

/**
 * User: alexkasko
 * Date: 1/14/13
 */
public class LongPackUtils {
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

    public static long big(long pack, int bits) {
        assert bits > 32 && bits < 64;
        int ls = bits % 8;
        long res = (pack & (((1L << (bits - ls)) - 1) << (64 - bits + ls))) >>> (64 - bits);
        return res | pack & ((1 << ls) - 1);
    }

    public static int little(long pack, int bits) {
        assert bits > 32 && bits < 64;
        int ls = bits % 8;
        return (int) ((pack & (((1L << (64 - bits)) -1) << ls)) >> ls);
    }
}
