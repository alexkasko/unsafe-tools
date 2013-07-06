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

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
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
    public void testLongKey() throws UnsupportedEncodingException {
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
    public void testIntKey() throws UnsupportedEncodingException {
//          for(int j=0; j< 100000; j++) {
          OffHeapStructArray arr = null;
          try {
//              System.out.println(j);
              ByteArrayTool bat = ByteArrayTool.get();
//              Random random = new Random(j);
              Random random = new Random(42);
              long[] heapHeaders = new long[LENGTH];
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
}
