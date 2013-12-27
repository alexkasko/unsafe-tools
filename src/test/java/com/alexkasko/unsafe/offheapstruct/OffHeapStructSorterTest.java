/*
 * Copyright 2013 Alex Kasko (alexkasko.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexkasko.unsafe.offheapstruct;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Executors;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static org.junit.Assert.*;

/**
 * User: alexkasko
 * Date: 7/4/13
 */
public class OffHeapStructSorterTest {
    private static final ByteArrayTool bt = ByteArrayTool.get();
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void testLongKey() {
//        for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//            System.out.println(j);
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
            arr = new OffHeapStructArray(LENGTH, 16);
            byte[] buf = new byte[16];
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
                if (0 == i % 5) {
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
                bt.putLong(buf, 0, payload);
                bt.putLong(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapStructSorter.sortByLongKey(arr, 8);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                arr.get(i, buf);
                long head = bt.getLong(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bt.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//        }
    }

    @Test
    public void testParallelLongKey() {
//        for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//            System.out.println(j);
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
            arr = new OffHeapStructArray(LENGTH, 16);
            byte[] buf = new byte[16];
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
                if (0 == i % 5) {
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
                bt.putLong(buf, 0, payload);
                bt.putLong(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
//            System.out.println(Arrays.toString(heapHeaders));
            Arrays.sort(heapHeaders);
//            System.out.println(Arrays.toString(heapHeaders));
            // off-heap sort
//            System.out.println(toStringList(arr));
            Iterator<byte[]> iter = OffHeapStructSorter.sortedIteratorByLongKey(Executors.newSingleThreadExecutor(), 4, arr, 8);
//            System.out.println(toStringList(arr));

//            ArrayList<byte[]> sorted = new ArrayList<byte[]>();
//            while (iter.hasNext()) {
//                sorted.add(iter.next().clone());
//            }
//            System.out.println(toStringList(sorted));
//            iter = sorted.iterator();

            // compare results
            for (int i = 0; i < LENGTH; i++) {
                assertTrue(iter.hasNext());
                buf = iter.next();
                long head = bt.getLong(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bt.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
            assertFalse(iter.hasNext());
        } finally {
            free(arr);
        }
//        }
    }

    @Test
    // proper test is not easy here
    public void testUnsignedLongKey() {
        OffHeapStructArray arr = null;
        try {
            arr = new OffHeapStructArray(3, 8);
            arr.putLong(0, 0, 0);
            arr.putLong(1, 0, -1);
            arr.putLong(2, 0, 1);
            OffHeapStructSorter.sortByUnsignedLongKey(arr, 0);
            assertEquals(0, arr.getLong(0, 0));
            assertEquals(1, arr.getLong(1, 0));
            assertEquals(-1, arr.getLong(2, 0));
        } finally {
            free(arr);
        }
    }

    @Test
    public void testIntKey() {
//          for(int j=0; j< 100000; j++) {
          OffHeapStructArray arr = null;
          try {
//              System.out.println(j);
//              Random random = new Random(j);
              Random random = new Random(42);
              int[] heapHeaders = new int[LENGTH];
              Map<Integer, List<Long>> heapPayloads = new HashMap<Integer, List<Long>>();
              arr = new OffHeapStructArray(LENGTH, 12);
              byte[] buf = new byte[12];
              int header = 0;
              for (int i = 0; i < LENGTH; i++) {
                  long payload = random.nextInt();
                  if (0 == i % 5) {
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
                  bt.putLong(buf, 0, payload);
                  bt.putInt(buf, 8, header);
                  arr.set(i, buf);
              }
              // standard sort for heap array
              Arrays.sort(heapHeaders);
              // off-heap sort
              OffHeapStructSorter.sortByIntKey(arr, 8);
              // compare results
              for (int i = 0; i < LENGTH; i++) {
                  arr.get(i, buf);
                  int head = bt.getInt(buf, 8);
                  assertEquals(head, heapHeaders[i]);
                  long payl = bt.getLong(buf, 0);
                  assertTrue(heapPayloads.get(head).remove(payl));
              }
          } finally {
              free(arr);
          }
//          }
      }

    @Test
    public void testIntKeyIterator() {
//          for(int j=0; j< 100000; j++) {
          OffHeapStructArray arr = null;
          try {
//              System.out.println(j);
//              Random random = new Random(j);
              Random random = new Random(42);
              int[] heapHeaders = new int[LENGTH];
              Map<Integer, List<Long>> heapPayloads = new HashMap<Integer, List<Long>>();
              arr = new OffHeapStructArray(LENGTH, 12);
              byte[] buf = new byte[12];
              int header = 0;
              for (int i = 0; i < LENGTH; i++) {
                  long payload = random.nextInt();
                  if (0 == i % 5) {
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
                  bt.putLong(buf, 0, payload);
                  bt.putInt(buf, 8, header);
                  arr.set(i, buf);
              }
              // standard sort for heap array
              Arrays.sort(heapHeaders);
              // off-heap sort
              Iterator<byte[]> iter = OffHeapStructSorter.sortedIteratorByIntKey(Executors.newSingleThreadExecutor(), 2, arr, 8);
              // compare results
              for (int i = 0; i < LENGTH; i++) {
                  assertTrue(iter.hasNext());
                  buf = iter.next();
                  int head = bt.getInt(buf, 8);
                  assertEquals(head, heapHeaders[i]);
                  long payl = bt.getLong(buf, 0);
                  assertTrue(heapPayloads.get(head).remove(payl));
              }
          } finally {
              free(arr);
          }
//          }
      }

    @Test
    public void testUnsignedIntKey() {
//          for(int j=0; j< 100000; j++) {
          OffHeapStructArray arr = null;
          try {
//              System.out.println(j);
//              Random random = new Random(j);
              Random random = new Random(42);
              long[] heapHeaders = new long[LENGTH];
              Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
              arr = new OffHeapStructArray(LENGTH, 12);
              byte[] buf = new byte[12];
              long header = 0;
              for (int i = 0; i < LENGTH; i++) {
                  long payload = random.nextInt();
                  if (0 == i % 5) {
                      header = random.nextInt() & 0xffffffffL;
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
                  bt.putLong(buf, 0, payload);
                  bt.putUnsignedInt(buf, 8, header);
                  arr.set(i, buf);
              }
              // standard sort for heap array
              Arrays.sort(heapHeaders);
              // off-heap sort
              OffHeapStructSorter.sortByUnsignedIntKey(arr, 8);
              // compare results
              for (int i = 0; i < LENGTH; i++) {
                  arr.get(i, buf);
                  long head = bt.getUnsignedInt(buf, 8);
                  assertEquals(head, heapHeaders[i]);
                  long payl = bt.getLong(buf, 0);
                  assertTrue(heapPayloads.get(head).remove(payl));
              }
          } finally {
              free(arr);
          }
//          }
      }

    @Test
    public void testComparator() {
//        for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//            System.out.println(j);
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
            arr = new OffHeapStructArray(LENGTH, 16);
            byte[] buf = new byte[16];
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
                if (0 == i % 5) {
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
                bt.putLong(buf, 0, payload);
                bt.putLong(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
//            System.out.println(Arrays.toString(heapHeaders));
            Arrays.sort(heapHeaders);
//            System.out.println(Arrays.toString(heapHeaders));
            // off-heap sort
//            System.out.println(toStringList(arr));
            OffHeapStructSorter.sort(arr, new LongComp());
//            System.out.println(toStringList(arr));
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                arr.get(i, buf);
                long head = bt.getLong(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bt.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//        }
    }

    @Test
    public void testParallelComparator() {
//        for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//            System.out.println(j);
//            Random random = new Random(j);
            Random random = new Random(42);
            long[] heapHeaders = new long[LENGTH];
            Map<Long, List<Long>> heapPayloads = new HashMap<Long, List<Long>>();
            arr = new OffHeapStructArray(LENGTH, 16);
            byte[] buf = new byte[16];
            long header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
                if (0 == i % 5) {
                    header = random.nextInt(99);
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
                bt.putLong(buf, 0, payload);
                bt.putLong(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
//            System.out.println(Arrays.toString(heapHeaders));
            Arrays.sort(heapHeaders);
//            System.out.println(Arrays.toString(heapHeaders));
            // off-heap sort
//            System.out.println(toStringList(arr));
            Iterator<byte[]> iter = OffHeapStructSorter.sortedIterator(Executors.newSingleThreadExecutor(), 4, arr, new LongComp());
//            System.out.println(toStringList(arr));

//            ArrayList<byte[]> sorted = new ArrayList<byte[]>();
//            while (iter.hasNext()) {
//                sorted.add(iter.next().clone());
//            }
//            System.out.println(toStringList(sorted));
//            iter = sorted.iterator();

            // compare results
            for (int i = 0; i < LENGTH; i++) {
                assertTrue(iter.hasNext());
                buf = iter.next();
                long head = bt.getLong(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bt.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
            assertFalse(iter.hasNext());
        } finally {
            free(arr);
        }
//        }
    }

//    @Test
//    public void testComparatorLong() {
//        OffHeapStructArray arr = null;
//        try {
//            arr = new OffHeapStructArray(LENGTH, 8);
//            int j = 0;
//            for (int i = LENGTH - 1; i >= 0; i--) {
//               arr.putLong(j++, 0, i);
//            }
//            OffHeapStructSorter.sort(arr, new LongComp());
//            for(int i = 0; i < LENGTH; i++) {
//                assertEquals(i, arr.getLong(i, 0));
//            }
//        } finally {
//            free(arr);
//        }
//    }

    private static List<String> toStringList(OffHeapStructArray arr) {
        List<String> res = new ArrayList<String>((int) arr.size());
        for (int i = 0; i < arr.size(); i++) {
//            res.add(arr.getLong(i, 0) + ":" + arr.getLong(i, 8));
            res.add(Long.toString(arr.getLong(i, 8)));
        }
        return res;
    }

    private static List<String> toStringList(List<byte[]> arr) {
        List<String> res = new ArrayList<String>((int) arr.size());
        for (int i = 0; i < arr.size(); i++) {
            byte[] buf = arr.get(i);
//            res.add(arr.getLong(i, 0) + ":" + arr.getLong(i, 8));
            res.add(Long.toString(bt.getLong(buf, 8)));
        }
        return res;
    }

    @Test
    public void twoLongKeysTest() {
        Random random = new Random(42);
        OffHeapStructArray arr1 = new OffHeapStructArray(LENGTH, 16);
        OffHeapStructArray arr2 = new OffHeapStructArray(LENGTH, 16);
        for (int i = 0; i < LENGTH; i++) {
            long l1 = random.nextLong();
            long l2 = random.nextLong();
            arr1.putLong(i, 0, l1);
            arr2.putLong(i, 0, l1);
            arr1.putLong(i, 8, l2);
            arr2.putLong(i, 8, l2);
        }
        OffHeapStructSorter.sort(arr1, new TwoLongComp());
        OffHeapStructSorter.sortByLongKeysParallel(Executors.newCachedThreadPool(), 4, arr2, 0, 8);
        for (int i = 0; i < LENGTH; i++) {
            assertEquals(arr1.getLong(i, 0), arr2.getLong(i, 0));
            assertEquals(arr1.getLong(i, 8), arr2.getLong(i, 8));
        }
    }

    private static class TwoLongComp implements Comparator<OffHeapStructAccessor> {
        @Override
        public int compare(OffHeapStructAccessor o1, OffHeapStructAccessor o2) {
            // first
            long l1 = o1.getLong(0);
            long l2 = o2.getLong(0);
            if (l1 > l2) return 1;
            if (l1 < l2) return -1;
            // second
            l1 = o1.getLong(8);
            l2 = o2.getLong(8);
            if (l1 > l2) return 1;
            if (l1 < l2) return -1;
            return 0;
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
            long l1 = o1.getLong(8);
            long l2 = o2.getLong(8);
            if(l1 > l2) return 1;
            if(l1 < l2) return -1;
            return 0;
        }
    }
}
