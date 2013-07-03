package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapUtils;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadIntArray;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadIntArrayTest {
    @Test
    public void test() {
        OffHeapPayloadIntArray arr = null;
        try {
            arr = new OffHeapPayloadIntArray(3);
            arr.set(0, 42, 421);
            arr.set(1, 43, 431);
            arr.set(2, 44, 441);

            assertEquals(42, arr.get(0));
            assertEquals(421, arr.getPayload(0));
            assertEquals(43, arr.get(1));
            assertEquals(431, arr.getPayload(1));
            assertEquals(44, arr.get(2));
            assertEquals(441, arr.getPayload(2));
        } finally {
            OffHeapUtils.free(arr);
        }
    }
}
