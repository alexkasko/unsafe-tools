package com.alexkasko.unsafe.offheap;

import org.junit.Test;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 2/24/13
 */
public class OffHeapLongArrayTest {
    @Test
    public void test() {
        OffHeapLongArray arr = null;
        try {
            arr = new OffHeapLongArray(3);
            arr.set(0, 41);
            arr.set(1, 42);
            arr.set(2, 1L << 42);
            assertEquals("Length fail", 3, arr.size());
            assertEquals("Contents fail", 41, arr.get(0));
            assertEquals("Contents fail", 42, arr.get(1));
            assertEquals("Contents fail", 1L << 42, arr.get(2));
        } finally {
            free(arr);
        }
    }
}
