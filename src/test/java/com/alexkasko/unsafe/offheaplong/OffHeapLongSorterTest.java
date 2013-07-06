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

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static org.junit.Assert.assertArrayEquals;

/**
* User: alexkasko
* Date: 2/20/13
*/
public class OffHeapLongSorterTest {
//    private static final int THRESHOLD = 1 << 20;
    private static final int THRESHOLD = 1 << 16;

    @Test
    public void test() throws Exception {
        OffHeapLongArray la = null;
        try {
            long[] heap = gendata();
            long[] unsafe = heap.clone();
            Arrays.sort(heap);

            la = new OffHeapLongArray(THRESHOLD);
            for (int i = 0; i < THRESHOLD; i++) {
                la.set(i, unsafe[i]);
            }
            OffHeapLongSorter.sort(la, 0, THRESHOLD);
            for (int i = 0; i < THRESHOLD; i++) {
                unsafe[i] = la.get(i);
            }
            assertArrayEquals(heap, unsafe);
            la.free();
        } finally {
            free(la);
        }
    }

    private static long[] gendata() throws Exception {
        Random random = new Random(42);
        long[] res = new long[THRESHOLD];
        for (int i = 0; i < THRESHOLD; i++) {
            res[i] = random.nextLong();
        }
        return res;
    }
}
