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
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 7/4/13
 */
public class OffHeapStructSorterTest {
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void testMultisort() {
        OffHeapStructArray arr = null;
        try {
            ByteArrayTool bt = ByteArrayTool.get();
            OffHeapStructSortKey byteKey = new OffHeapStructSortKey(0, byte.class);
            OffHeapStructSortKey shortKey = new OffHeapStructSortKey(1, short.class);
            OffHeapStructSortKey intKey = new OffHeapStructSortKey(3, int.class);
            OffHeapStructSortKey longKey = new OffHeapStructSortKey(7, long.class);
            // data
            byte[] buf0 = new byte[15];
            bt.putByte(buf0, byteKey.offset(), Byte.MAX_VALUE);
            bt.putShort(buf0, shortKey.offset(), Short.MAX_VALUE);
            bt.putInt(buf0, intKey.offset(), Integer.MAX_VALUE);
            bt.putLong(buf0, longKey.offset(), Long.MAX_VALUE);
            byte[] buf1 = new byte[15];
            bt.putByte(buf1, byteKey.offset(), Byte.MAX_VALUE);
            bt.putShort(buf1, shortKey.offset(), Short.MAX_VALUE);
            bt.putInt(buf1, intKey.offset(), Integer.MAX_VALUE - 1);
            bt.putLong(buf1, longKey.offset(), Long.MAX_VALUE);
            byte[] buf2 = new byte[15];
            bt.putByte(buf2, byteKey.offset(), Byte.MAX_VALUE);
            bt.putShort(buf2, shortKey.offset(), (short) (Short.MAX_VALUE - 1));
            bt.putInt(buf2, intKey.offset(), Integer.MAX_VALUE - 1);
            bt.putLong(buf2, longKey.offset(), Long.MAX_VALUE - 1);
            byte[] buf3 = new byte[15];
            bt.putByte(buf3, byteKey.offset(), (byte) (Byte.MAX_VALUE - 1));
            bt.putShort(buf3, shortKey.offset(), Short.MAX_VALUE);
            bt.putInt(buf3, intKey.offset(), Integer.MAX_VALUE);
            bt.putLong(buf3, longKey.offset(), Long.MAX_VALUE);
            byte[] buf4 = new byte[15];
            bt.putByte(buf4, byteKey.offset(), (byte) (Byte.MAX_VALUE - 1));
            bt.putShort(buf4, shortKey.offset(), Short.MAX_VALUE);
            bt.putInt(buf4, intKey.offset(), Integer.MAX_VALUE);
            bt.putLong(buf4, longKey.offset(), Long.MAX_VALUE - 1);
            byte[] buf5 = new byte[15];
            bt.putByte(buf5, byteKey.offset(), (byte) (Byte.MAX_VALUE - 1));
            bt.putShort(buf5, shortKey.offset(), Short.MAX_VALUE);
            bt.putInt(buf5, intKey.offset(), Integer.MAX_VALUE - 1);
            bt.putLong(buf5, longKey.offset(), Long.MAX_VALUE - 1);
            // fill
            arr = new OffHeapStructArray(6, 15);
            arr.set(0, buf0);
            arr.set(1, buf1);
            arr.set(2, buf2);
            arr.set(3, buf3);
            arr.set(4, buf4);
            arr.set(5, buf5);
            // sort
            OffHeapStructSorter.sort(arr, byteKey, shortKey, intKey, longKey);
            // check order reversed
            byte[] buf = new byte[15];
            arr.get(0, buf);
            assertArrayEquals(buf5, buf);
            arr.get(1, buf);
            assertArrayEquals(buf4, buf);
            arr.get(2, buf);
            assertArrayEquals(buf3, buf);
            arr.get(3, buf);
            assertArrayEquals(buf2, buf);
            arr.get(4, buf);
            assertArrayEquals(buf1, buf);
            arr.get(5, buf);
            assertArrayEquals(buf0, buf);
        } finally {
            free(arr);
        }
    }

    @Test
    public void testLongKey() {
//        for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//            System.out.println(j);
            ByteArrayTool bat = ByteArrayTool.get();
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
                bat.putLong(buf, 0, payload);
                bat.putLong(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapStructSorter.sortByLongKey(arr, 8);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                arr.get(i, buf);
                long head = bat.getLong(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bat.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//        }
    }

    @Test
    public void testIntKey() {
//          for(int j=0; j< 100000; j++) {
          OffHeapStructArray arr = null;
          try {
//              System.out.println(j);
              ByteArrayTool bat = ByteArrayTool.get();
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
                  bat.putLong(buf, 0, payload);
                  bat.putInt(buf, 8, header);
                  arr.set(i, buf);
              }
              // standard sort for heap array
              Arrays.sort(heapHeaders);
              // off-heap sort
              OffHeapStructSorter.sortByIntKey(arr, 8);
              // compare results
              for (int i = 0; i < LENGTH; i++) {
                  arr.get(i, buf);
                  int head = bat.getInt(buf, 8);
                  assertEquals(head, heapHeaders[i]);
                  long payl = bat.getLong(buf, 0);
                  assertTrue(heapPayloads.get(head).remove(payl));
              }
          } finally {
              free(arr);
          }
//          }
      }

    @Test
    public void testShortKey() {
//          for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//              System.out.println(j);
            ByteArrayTool bat = ByteArrayTool.get();
//              Random random = new Random(j);
            Random random = new Random(42);
            short[] heapHeaders = new short[LENGTH];
            Map<Short, List<Long>> heapPayloads = new HashMap<Short, List<Long>>();
            arr = new OffHeapStructArray(LENGTH, 12);
            byte[] buf = new byte[12];
            short header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
                if (0 == i % 5) {
                    header = (short) random.nextInt();
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
                bat.putInt(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapStructSorter.sortByShortKey(arr, 8);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                arr.get(i, buf);
                short head = bat.getShort(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bat.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//          }
    }

    @Test
    public void testByteKey() {
//          for(int j=0; j< 100000; j++) {
        OffHeapStructArray arr = null;
        try {
//              System.out.println(j);
            ByteArrayTool bat = ByteArrayTool.get();
//              Random random = new Random(j);
            Random random = new Random(42);
            byte[] heapHeaders = new byte[LENGTH];
            Map<Byte, List<Long>> heapPayloads = new HashMap<Byte, List<Long>>();
            arr = new OffHeapStructArray(LENGTH, 12);
            byte[] buf = new byte[12];
            byte header = 0;
            for (int i = 0; i < LENGTH; i++) {
                long payload = random.nextInt();
                if (0 == i % 5) {
                    header = (byte) random.nextInt();
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
                bat.putInt(buf, 8, header);
                arr.set(i, buf);
            }
            // standard sort for heap array
            Arrays.sort(heapHeaders);
            // off-heap sort
            OffHeapStructSorter.sortByByteKey(arr, 8);
            // compare results
            for (int i = 0; i < LENGTH; i++) {
                arr.get(i, buf);
                byte head = bat.getByte(buf, 8);
                assertEquals(head, heapHeaders[i]);
                long payl = bat.getLong(buf, 0);
                assertTrue(heapPayloads.get(head).remove(payl));
            }
        } finally {
            free(arr);
        }
//          }
    }

    @Test
    public void testMultisortEmpty() {
        OffHeapStructArray arr = null;
        try {
            arr = new OffHeapStructArray(0, 42);
            OffHeapStructSortKey key1 = new OffHeapStructSortKey(0, long.class);
            OffHeapStructSortKey key2 = new OffHeapStructSortKey(8, long.class);
            OffHeapStructSorter.sort(arr, key1, key2);
        } finally {
            free(arr);
        }
    }
}
