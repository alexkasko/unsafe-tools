package com.alexkasko.unsafe.offheap;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 3/5/13
 */
public class OffHeapBinarySearchTest {
    @Test
    public void test() {
        Random random = new Random(42);
        long[] arr = new long[100000];
        OffHeapLongArray oha = new OffHeapLongArray(100000);
        long val4242 = -1;
        long val4243 = -1;
        for (int i = 0; i < 100000; i++) {
            long ra = random.nextLong();
            arr[i] = ra;
            oha.set(i, ra);
            if(4242 == i) val4242 = ra;
            if(4243 == i) val4243 = ra;
        }
        Arrays.sort(arr);
        OffHeapLongSorter.sort(oha);
        long ind4242e = Arrays.binarySearch(arr, val4242);
        long ind4242a = OffHeapBinarySearch.binarySearch(oha, val4242);
        long ind4243e = Arrays.binarySearch(arr, val4243);
        long ind4243a = OffHeapBinarySearch.binarySearch(oha, val4243);
        assertEquals(ind4242e, ind4242a);
        assertEquals(ind4243e, ind4243a);
    }
}
