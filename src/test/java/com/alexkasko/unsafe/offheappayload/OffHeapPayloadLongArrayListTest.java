package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheappayload.OffHeapPayloadLongArrayList;
import org.junit.Test;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadLongArrayListTest {
    @Test
    public void test() {
        OffHeapPayloadLongArrayList list = null;
        try {
            list = new OffHeapPayloadLongArrayList(8);
            for (int i = 0; i < 42; i++) {
                list.add(42, i);
            }
            list.add(1L << 42, 1L << 42);

            assertEquals("Size fail", 43, list.size());
            assertTrue("Capacity fail", list.capacity() >= 43);
            assertEquals("Header fail", 42, list.get(0));
            assertEquals("Payload fail", 0, list.getPayload(0));
            assertEquals("Header fail", 42, list.get(41));
            assertEquals("Payload fail", 41, list.getPayload(41));
            assertEquals("Header fail", 1L << 42, list.get(42));
            assertEquals("Payload fail", 1L << 42, list.getPayload(42));
        } finally {
            free(list);
        }
    }
}
