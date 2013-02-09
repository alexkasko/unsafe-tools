package com.alexkasko.util.unsafe;

import static java.lang.Math.pow;

/**
 * User: alexkasko
 * Date: 1/14/13
 */
// todo: remove pow
public class LongPackUtils {
    public static long pack(long big, int little, int bits) {
        assert bits > 32 && bits < 64;
        assert big < pow(2, bits);
        assert little < pow(2, 64 - bits);
        int ls = bits % 8;
        int bm = ((int) pow(2, ls) - 1);
        long res = (big & ~bm) << (64 - bits);
        res |= (little & ((long) pow(2, 64 - bits) - 1)) << ls;
        res |= big & bm;
        return res;
    }

    public static long big(long pack, int bits) {
        assert bits > 32 && bits < 64;
        int ls = bits % 8;
        long res = (pack & (((long) pow(2, bits - ls) - 1) << (64 - bits + ls))) >>> (64 - bits);
        return res | pack & ((int) pow(2, ls) - 1);
    }

    public static int little(long pack, int bits) {
        assert bits > 32 && bits < 64;
        int ls = bits % 8;
        return (int) ((pack & (((long) pow(2, 64 - bits) -1) << ls)) >> ls);
    }
}
