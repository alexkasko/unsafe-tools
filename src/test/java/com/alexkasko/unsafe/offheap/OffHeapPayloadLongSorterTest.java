package com.alexkasko.unsafe.offheap;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadLongSorterTest {
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void test() throws UnsupportedEncodingException {
//        for(int j=0; j< 100000; j++) {
        OffHeapPayloadLongArray arr = null;
        try {
//            System.out.println(j);
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
            arr = new OffHeapPayloadLongArray(LENGTH);
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextLong();
                if(0 == i % 5) {
                    header = random.nextInt();
                }
                heapHeaders[i] = header;
                List<Long> existed = heapPayloads.get(header);
                if (null != existed) {
                    existed.add(payload);
                } else {
                    List<Long> li = new ArrayList<Long>();
                    li.add(payload);
                    heapPayloads.put(header, li);
                }
                arr.set(i, header, payload);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapPayloadLongSorter.sort(arr);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                long head = arr.get(i);
                assertEquals(head, heapHeaders[i]);
                long payl = arr.getPayload(i);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//        }
    }
}
