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

package com.alexkasko.unsafe.offheaplong;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static com.alexkasko.unsafe.offheaplong.OffHeapLongBinarySearch.binarySearchRange;
import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/5/13
 */
public class OffHeapLongBinarySearchTest {
//    private static final int LENGTH = 1000000;
    private static final int LENGTH = 10000;

    @Test
    public void test() {
        OffHeapLongArray oha = null;
        try {
            oha = new OffHeapLongArray(LENGTH);
            Random random = new Random(42);
            long[] arr = new long[LENGTH];
            long val4242 = -1;
            long val4243 = -1;
            for (int i = 0; i < LENGTH; i++) {
                long ra = random.nextLong();
                arr[i] = ra;
                oha.set(i, ra);
                if (4242 == i) val4242 = ra;
                if (4243 == i) val4243 = ra;
            }
            Arrays.sort(arr);
            OffHeapLongSorter.sort(oha);
            long ind4242e = Arrays.binarySearch(arr, val4242);
            long ind4242a = OffHeapLongBinarySearch.binarySearch(oha, val4242);
            long ind4243e = Arrays.binarySearch(arr, val4243);
            long ind4243a = OffHeapLongBinarySearch.binarySearch(oha, val4243);
            assertEquals(ind4242e, ind4242a);
            assertEquals(ind4243e, ind4243a);
        } finally {
            free(oha);
        }
    }

    @Test
    public void testRange() {
        OffHeapLongArray oha = null;
        try {
            oha = new OffHeapLongArray(6);
            oha.set(0, 41);
            oha.set(1, 41);
            oha.set(2, 42);
            oha.set(3, 42);
            oha.set(4, 42);
            oha.set(5, 43);
            OffHeapLongBinarySearch.IndexRange range41 = new OffHeapLongBinarySearch.IndexRange();
            binarySearchRange(oha, 41, range41);
            assertTrue(range41.isNotEmpty());
            assertEquals("Start fail", 0, range41.getFromIndex());
            assertEquals("Start fail", 1, range41.getToIndex());
            OffHeapLongBinarySearch.IndexRange range42 = new OffHeapLongBinarySearch.IndexRange();
            binarySearchRange(oha, 42, range42);
            assertTrue(range42.isNotEmpty());
            assertEquals("Middle fail", 2, range42.getFromIndex());
            assertEquals("Middle fail", 4, range42.getToIndex());
            OffHeapLongBinarySearch.IndexRange range43 = new OffHeapLongBinarySearch.IndexRange();
            binarySearchRange(oha, 43, range43);
            assertTrue(range43.isNotEmpty());
            assertEquals("End fail", 5, range43.getFromIndex());
            assertEquals("End fail", 5, range43.getToIndex());
            OffHeapLongBinarySearch.IndexRange range44 = new OffHeapLongBinarySearch.IndexRange();
            binarySearchRange(oha, 44, range44);
            assertTrue(range44.isEmpty());
            assertTrue("Empty fail", range44.getFromIndex() == range44.getToIndex());
            assertTrue("Empty fail", range44.getFromIndex() < 0);
            assertTrue("Empty fail", range44.getToIndex() < 0);
        } finally {
            free(oha);
        }
    }
}
