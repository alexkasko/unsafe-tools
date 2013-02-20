//package com.alexkasko.util.unsafe;
//
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.Random;
//
//import static org.junit.Assert.assertArrayEquals;
//
///**
// * User: alexkasko
// * Date: 2/20/13
// */
//public class MemorySorterTest {
//    private static final int THRESHOLD = 1 << 3;
//
//    @Test
//    public void test() throws Exception {
//        long[] heap = gendata();
//        System.out.println(Arrays.toString(heap));
//        long[] unsafe = heap.clone();
//        System.out.println(Arrays.toString(unsafe));
//        Arrays.sort(heap);
//        System.out.println(Arrays.toString(heap));
//        MemoryArea ma = MemoryArea.unsafe(1);
//        int id = ma.alloc(THRESHOLD * 8);
//        for (int i = 0; i < THRESHOLD; i++) {
//            ma.writeLong(id, i*8, unsafe[i]);
//        }
//        MemorySorter.quicksort(ma, id, 0, THRESHOLD * 8);
//        for (int i = 0; i < THRESHOLD; i++) {
//            unsafe[i] = ma.readLong(id, i * 8);
//        }
//        System.out.println(Arrays.toString(unsafe));
//        assertArrayEquals(heap, unsafe);
//        ma.freeQuietly(id);
//    }
//
//    private static long[] gendata() throws Exception {
//        Random random = new Random(42);
//        long[] res = new long[THRESHOLD];
//        for (int i = 0; i < THRESHOLD; i++) {
//            res[i] = random.nextLong();
//        }
//        return res;
//    }
//}
