package com.alexkasko.unsafe.offheap;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static org.junit.Assert.assertEquals;


/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadSorterTest {
    private static final int LENGTH = 100000;

    @Test
    public void test() throws UnsupportedEncodingException {
        OffHeapPayloadArray arr = null;
        try {
            ByteArrayTool bat = ByteArrayTool.get();
            Random random = new Random(42);
            long[] heap = new long[LENGTH];
            arr = new OffHeapPayloadArray(LENGTH, 8);
            byte[] buf = new byte[8];
            for (int i = 0; i < LENGTH; i++) {
                long header = random.nextInt();
                heap[i] = header;
                bat.putLong(buf, 0, header);
                arr.set(i, header, buf);
            }
//        System.out.println(Arrays.toString(heap));
            Arrays.sort(heap);
//        System.out.println(Arrays.toString(heap));
//        for (int i = 0; i < LENGTH; i++) {
//            long header = arr.getHeader(i);
//            arr.getPayload(i, buf);
//            long payload = bat.getLong(buf, 0);
//            System.out.println(i + " : " + header + " : " + payload);
//        }
            OffHeapPayloadSorter.sort(arr);
            for (int i = 0; i < LENGTH; i++) {
                long header = arr.get(i);
                assertEquals(header, heap[i]);
                arr.getPayload(i, buf);
                long payload = bat.getLong(buf, 0);
//            System.out.println(i + " : " + header + " : " + payload);
                assertEquals(payload, header);
            }
        } finally {
            free(arr);
        }
    }
}
