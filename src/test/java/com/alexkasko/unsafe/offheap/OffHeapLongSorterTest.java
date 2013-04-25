package com.alexkasko.unsafe.offheap;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static org.junit.Assert.assertArrayEquals;

/**
* User: alexkasko
* Date: 2/20/13
*/
public class OffHeapLongSorterTest {
//    private static final int THRESHOLD = 1 << 20;
    private static final int THRESHOLD = 1 << 16;

    @Test
    public void test() throws Exception {
        OffHeapLongArray la = null;
        try {
            long[] heap = gendata();
            long[] unsafe = heap.clone();
            Arrays.sort(heap);

            la = new OffHeapLongArray(THRESHOLD);
            for (int i = 0; i < THRESHOLD; i++) {
                la.set(i, unsafe[i]);
            }
            OffHeapLongSorter.sort(la, 0, THRESHOLD);
            for (int i = 0; i < THRESHOLD; i++) {
                unsafe[i] = la.get(i);
            }
            assertArrayEquals(heap, unsafe);
            la.free();
        } finally {
            free(la);
        }
    }

    private static long[] gendata() throws Exception {
        Random random = new Random(42);
        long[] res = new long[THRESHOLD];
        for (int i = 0; i < THRESHOLD; i++) {
            res[i] = random.nextLong();
        }
        return res;
    }
}
