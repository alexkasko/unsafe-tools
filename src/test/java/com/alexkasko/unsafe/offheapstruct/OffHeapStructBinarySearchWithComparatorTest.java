package com.alexkasko.unsafe.offheapstruct;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import com.alexkasko.unsafe.offheap.OffHeapUtils;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 9/13/13
 */
public class OffHeapStructBinarySearchWithComparatorTest {
//  private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void testLong() {
        ByteArrayTool bt = ByteArrayTool.get();
        byte[] buf = new byte[8];
        OffHeapStructArray oha = null;
        try {
            oha = new OffHeapStructArray(LENGTH, 8);
            Random random = new Random(42);
            long[] arr = new long[LENGTH];
            long val4242 = -1;
            long val4243 = -1;
            for (int i = 0; i < LENGTH; i++) {
                long ra = random.nextInt();
                arr[i] = ra;
                bt.putLong(buf, 0, ra);
                oha.set(i, buf);
                if (4242 == i) val4242 = ra;
                if (4243 == i) val4243 = ra;
            }
//            System.out.println(Arrays.toString(arr));
            Arrays.sort(arr);
//            System.out.println(Arrays.toString(arr));
            Comparator<OffHeapStructAccessor> comp = new LongComp();
//            System.out.println(toLongList(oha));
            OffHeapStructSorter.sort(oha, comp);
//            System.out.println(toLongList(oha));
            OffHeapStructBinarySearchWithComparator searcher = new OffHeapStructBinarySearchWithComparator(oha, comp);
            long ind4242e = Arrays.binarySearch(arr, val4242);
            bt.putLong(buf, 0, val4242);
            long ind4242a = searcher.binarySearch(buf);
            long ind4243e = Arrays.binarySearch(arr, val4243);
            bt.putLong(buf, 0, val4243);
            long ind4243a = searcher.binarySearch(buf);
            assertEquals(ind4242e, ind4242a);
            assertEquals(ind4243e, ind4243a);
        } finally {
            OffHeapUtils.free(oha);
        }
    }

    private static List<Long> toLongList(OffHeapStructArray arr) {
        List<Long> res = new ArrayList<Long>((int) arr.size());
        for (int i = 0; i < arr.size(); i++) {
            res.add(arr.getLong(i, 0));
        }
        return res;
    }

    private static class LongComp implements Comparator<OffHeapStructAccessor> {
        @Override
        public int compare(OffHeapStructAccessor o1, OffHeapStructAccessor o2) {
            long diff = o1.getLong(0) - o2.getLong(0);
            if (diff > 0) return 1;
            if (diff < 0) return -1;
            return 0;
        }
    }

}
