package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadArray;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadSorter;
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
public class OffHeapPayloadSorterTest {
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void test() throws UnsupportedEncodingException {
//        for(int j=0; j< 100000; j++) {
        OffHeapPayloadArray arr = null;
        try {
//            System.out.println(j);
            ByteArrayTool bat = ByteArrayTool.get();
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
            arr = new OffHeapPayloadArray(LENGTH, 8);
            byte[] buf = new byte[8];
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
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
                bat.putLong(buf, 0, payload);
                arr.set(i, header, buf);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapPayloadSorter.sort(arr);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                long head = arr.get(i);
                assertEquals(head, heapHeaders[i]);
                arr.getPayload(i, buf);
                long payl = bat.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//        }
    }
}
