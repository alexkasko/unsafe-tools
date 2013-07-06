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
import com.alexkasko.unsafe.offheap.OffHeapUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/5/13
 */
public class OffHeapStructBinarySearchTest {
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void test() {
        ByteArrayTool bat = ByteArrayTool.get();
        byte[] buf = new byte[8];
        OffHeapStructArray oha = null;
        try {
            oha = new OffHeapStructArray(LENGTH, 8);
            Random random = new Random(42);
            long[] arr = new long[LENGTH];
            long val4242 = -1;
            long val4243 = -1;
            for (int i = 0; i < LENGTH; i++) {
                long ra = random.nextLong();
                arr[i] = ra;
                bat.putLong(buf, 0, ra);
                oha.set(i, buf);
                if (4242 == i) val4242 = ra;
                if (4243 == i) val4243 = ra;
            }
            Arrays.sort(arr);
            OffHeapStructSorter.sortByLongKey(oha, 0);
            long ind4242e = Arrays.binarySearch(arr, val4242);
            long ind4242a = OffHeapStructBinarySearch.binarySearchByLongKey(oha, val4242, 0);
            long ind4243e = Arrays.binarySearch(arr, val4243);
            long ind4243a = OffHeapStructBinarySearch.binarySearchByLongKey(oha, val4243, 0);
            assertEquals(ind4242e, ind4242a);
            assertEquals(ind4243e, ind4243a);
        } finally {
            OffHeapUtils.free(oha);
        }
    }

    @Test
    public void testRange() {
        ByteArrayTool bat = ByteArrayTool.get();
        byte[] buf = new byte[8];
        OffHeapStructArray oha = null;
        try {
            oha = new OffHeapStructArray(6, 8);
            bat.putLong(buf, 0, 41);
            oha.set(0, buf);
            bat.putLong(buf, 0, 41);
            oha.set(1, buf);
            bat.putLong(buf, 0, 42);
            oha.set(2, buf);
            bat.putLong(buf, 0, 42);
            oha.set(3, buf);
            bat.putLong(buf, 0, 42);
            oha.set(4, buf);
            bat.putLong(buf, 0, 43);
            oha.set(5, buf);
            OffHeapStructBinarySearch.IndexRange range41 = new OffHeapStructBinarySearch.IndexRange();
            OffHeapStructBinarySearch.binarySearchRangeByIntKey(oha, 41, 0, range41);
            assertTrue(range41.isNotEmpty());
            assertEquals("Start fail", 0, range41.getFromIndex());
            assertEquals("Start fail", 1, range41.getToIndex());
            OffHeapStructBinarySearch.IndexRange range42 = new OffHeapStructBinarySearch.IndexRange();
            OffHeapStructBinarySearch.binarySearchRangeByIntKey(oha, 42, 0, range42);
            assertTrue(range42.isNotEmpty());
            assertEquals("Middle fail", 2, range42.getFromIndex());
            assertEquals("Middle fail", 4, range42.getToIndex());
            OffHeapStructBinarySearch.IndexRange range43 = new OffHeapStructBinarySearch.IndexRange();
            OffHeapStructBinarySearch.binarySearchRangeByIntKey(oha, 43, 0, range43);
            assertTrue(range43.isNotEmpty());
            assertEquals("End fail", 5, range43.getFromIndex());
            assertEquals("End fail", 5, range43.getToIndex());
            OffHeapStructBinarySearch.IndexRange range44 = new OffHeapStructBinarySearch.IndexRange();
            OffHeapStructBinarySearch.binarySearchRangeByIntKey(oha, 44, 0, range44);
            assertTrue(range44.isEmpty());
            assertTrue("Empty fail", range44.getFromIndex() == range44.getToIndex());
            assertTrue("Empty fail", range44.getFromIndex() < 0);
            assertTrue("Empty fail", range44.getToIndex() < 0);
        } finally {
            OffHeapUtils.free(oha);
        }
    }
}
