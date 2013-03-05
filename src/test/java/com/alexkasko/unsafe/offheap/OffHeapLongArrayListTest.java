package com.alexkasko.unsafe.offheap;

import org.junit.Test;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/1/13
 */
public class OffHeapLongArrayListTest {
    @Test
    public void test() {
        OffHeapLongArrayList list = null;
        try {
            list = new OffHeapLongArrayList();
            for (int i = 0; i < 42; i++) {
                list.add(42);
            }
            list.add(1L << 42);
            assertEquals("Size fail", 43, list.size());
            assertTrue("Capacity fail", list.capacity() >= 43);
            assertEquals("Contents fail", 42, list.get(0));
            assertEquals("Contents fail", 42, list.get(41));
            assertEquals("Contents fail", 1L << 42, list.get(42));
        } finally {
            free(list);
        }
    }
}
