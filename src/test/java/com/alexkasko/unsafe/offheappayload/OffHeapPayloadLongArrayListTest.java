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

package com.alexkasko.unsafe.offheappayload;

import org.junit.Test;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadLongArrayListTest {
    @Test
    public void test() {
        OffHeapPayloadLongArrayList list = null;
        try {
            list = new OffHeapPayloadLongArrayList(8);
            for (int i = 0; i < 42; i++) {
                list.add(42, i);
            }
            list.add(1L << 42, 1L << 42);

            assertEquals("Size fail", 43, list.size());
            assertTrue("Capacity fail", list.capacity() >= 43);
            assertEquals("Header fail", 42, list.get(0));
            assertEquals("Payload fail", 0, list.getPayload(0));
            assertEquals("Header fail", 42, list.get(41));
            assertEquals("Payload fail", 41, list.getPayload(41));
            assertEquals("Header fail", 1L << 42, list.get(42));
            assertEquals("Payload fail", 1L << 42, list.getPayload(42));
        } finally {
            free(list);
        }
    }
}
