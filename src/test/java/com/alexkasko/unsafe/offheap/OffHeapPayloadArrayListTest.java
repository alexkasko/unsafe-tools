package com.alexkasko.unsafe.offheap;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadArrayListTest {
    @Test
    public void test() {
        ByteArrayTool bat = ByteArrayTool.get();
        OffHeapPayloadArrayList list = new OffHeapPayloadArrayList(8);
        byte[] buf = new byte[8];
        for (int i = 0; i < 42; i++) {
            bat.putLong(buf, 0, i);
            list.add(42, buf);
        }
        bat.putLong(buf, 0, 1L << 42);
        list.add(1L << 42, buf);
        assertEquals("Size fail", 43, list.size());
        assertTrue("Capacity fail", list.capacity() >= 43);
        assertEquals("Header fail", 42, list.get(0));
        list.getPayload(0, buf);
        assertEquals("Payload fail", 0, bat.getLong(buf, 0));
        assertEquals("Header fail", 42, list.get(41));
        list.getPayload(41, buf);
        assertEquals("Payload fail", 41, bat.getLong(buf, 0));
        assertEquals("Header fail", 1L << 42, list.get(42));
        list.getPayload(42, buf);
        assertEquals("Payload fail", 1L << 42, bat.getLong(buf, 0));
    }
}
