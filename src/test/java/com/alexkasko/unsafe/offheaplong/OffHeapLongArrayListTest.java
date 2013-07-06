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

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/1/13
 */
public class OffHeapLongArrayListTest {
    @Test
    public void test() {
        OffHeapLongArrayList list = null;
        try {
            list = new OffHeapLongArrayList();
            for (int i = 0; i < 42; i++) {
                list.add(42);
            }
            list.add(1L << 42);
            assertEquals("Size fail", 43, list.size());
            assertTrue("Capacity fail", list.capacity() >= 43);
            assertEquals("Contents fail", 42, list.get(0));
            assertEquals("Contents fail", 42, list.get(41));
            assertEquals("Contents fail", 1L << 42, list.get(42));
        } finally {
            free(list);
        }
    }
}
