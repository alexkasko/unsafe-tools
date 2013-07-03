package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import com.alexkasko.unsafe.offheap.OffHeapUtils;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadArray;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadArrayTest {
    @Test
    public void test() {
        OffHeapPayloadArray arr = null;
        try {
            ByteArrayTool bat = ByteArrayTool.get();
            arr = new OffHeapPayloadArray(3, 4);
            byte[] buf42 = new byte[4];
            bat.putInt(buf42, 0, 42);
            arr.set(0, 42, buf42);
            byte[] buf43 = new byte[4];
            bat.putInt(buf43, 0, 43);
            arr.set(1, 43, buf43);
            byte[] buf44 = new byte[4];
            bat.putInt(buf44, 0, 44);
            arr.set(2, 44, buf44);

            assertEquals(42, arr.get(0));
            byte[] buf = new byte[4];
            arr.getPayload(0, buf);
            assertArrayEquals(buf42, buf);
            assertEquals(43, arr.get(1));
            arr.getPayload(1, buf);
            assertArrayEquals(buf43, buf);
            assertEquals(44, arr.get(2));
            arr.getPayload(2, buf);
            assertArrayEquals(buf44, buf);
        } finally {
            OffHeapUtils.free(arr);
        }
    }
}
