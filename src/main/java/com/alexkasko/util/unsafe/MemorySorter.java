//package com.alexkasko.util.unsafe;
//
///**
// * User: alexkasko
// * Date: 2/20/13
// */
//public class MemorySorter {
//    public static void quicksort(MemoryArea ma, int id, long offBytes, long lenBytes) {
//        // todo:fixme
//        long off = offBytes / 8;
//        long len = lenBytes / 8;
//        // Choose a partition element, v
//        long m = off + (len >> 1);       // Small arrays, middle element
//        if (len > 7) {
//            long l = off;
//            long n = off + len - 1;
//            if (len > 40) {        // Big arrays, pseudomedian of 9
//                long s = len / 8;
//                l = med3(ma, id, l, l + s, l + 2 * s);
//                m = med3(ma, id, m - s, m, m + s);
//                n = med3(ma, id, n - 2 * s, n - s, n);
//            }
//            m = med3(ma, id, l, m, n); // Mid-size, med of 3
//        }
//        long v = ma.readLong(id, m*8);
//
//        // Establish Invariant: v* (<v)* (>v)* v*
//        long a = off, b = a, c = off + len - 1, d = c;
//        while (true) {
//            while (b <= c && ma.readLong(id, b*8) <= v) {
//                if (ma.readLong(id, b*8) == v)
//                    swap(ma, id, a++, b);
//                b++;
//            }
//            while (c >= b && ma.readLong(id, c*8) >= v) {
//                if (ma.readLong(id, c*8) == v)
//                    swap(ma, id, c, d--);
//                c--;
//            }
//            if (b > c)
//                break;
//            swap(ma, id, b++, c--);
//        }
//
//        // Swap partition elements back to middle
//        long s, n = off + len;
//        s = Math.min(a - off, b - a);
//        vecswap(ma, id, off, b - s, s);
//        s = Math.min(d - c, n - d - 1);
//        vecswap(ma, id, b, n - s, s);
//
//        // Recursively sort non-partition-elements
//        if ((s = b - a) > 1) quicksort(ma, id, off, s);
//        if ((s = d - c) > 1) quicksort(ma, id, n - s, s);
//    }
//
//    /**
//     * Swaps x[a] with x[b].
//     */
//    private static void swap(MemoryArea ma, int id, long a, long b) {
//        long aval = ma.readLong(id, a*8);
//        long bval = ma.readLong(id, b*8);
//        ma.writeLong(id, a*8, bval);
//        ma.writeLong(id, b*8, aval);
//    }
//
//    /**
//      * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
//      */
//     private static void vecswap(MemoryArea ma, int id, long a, long b, long n) {
//         for (int i=0; i<n; i++, a++, b++)
//             swap(ma, id, a, b);
//     }
//    /**
//     * Returns the index of the median of the three indexed longs.
//     */
//    private static long med3(MemoryArea ma, int id, long a, long b, long c) {
//        long aval = ma.readLong(id, a*8);
//        long bval = ma.readLong(id, b*8);
//        long cval = ma.readLong(id, c*8);
//        return (aval < bval ?
//                (bval < cval ? b : aval < cval ? c : a) :
//                (bval > cval ? b : aval > cval ? c : a));
//    }
//
//    private static class LongMemoryArray {
//        private final MemoryArea ma;
//        private final  int id;
//        private final long offset;
//
//        private LongMemoryArray(MemoryArea ma, int id, long offset) {
//            this.ma = ma;
//            this.id = id;
//            this.offset = offset;
//        }
//
//
//
//    }
//}
