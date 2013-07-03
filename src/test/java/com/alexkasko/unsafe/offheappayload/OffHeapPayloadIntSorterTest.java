package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheappayload.OffHeapPayloadIntArray;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadIntSorter;
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
public class OffHeapPayloadIntSorterTest {
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void test() throws UnsupportedEncodingException {
//        for(int j=0; j< 100000; j++) {
        OffHeapPayloadIntArray arr = null;
        try {
//            System.out.println(j);
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Integer>> heapPayloads = new HashMap<Long, List<Integer>>();
            arr = new OffHeapPayloadIntArray(LENGTH);
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                int payload = random.nextInt();
                if(0 == i % 5) {
                    header = random.nextInt();
                }
                heapHeaders[i] = header;
                List<Integer> existed = heapPayloads.get(header);
                if (null != existed) {
                    existed.add(payload);
                } else {
                    List<Integer> li = new ArrayList<Integer>();
                    li.add(payload);
                    heapPayloads.put(header, li);
                }
                arr.set(i, header, payload);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapPayloadIntSorter.sort(arr);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                long head = arr.get(i);
                assertEquals(head, heapHeaders[i]);
                Integer payl = arr.getPayload(i);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//        }
    }
}
