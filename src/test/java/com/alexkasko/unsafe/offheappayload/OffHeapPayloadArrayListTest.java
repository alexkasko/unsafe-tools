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

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadArrayList;
import org.junit.Test;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadArrayListTest {
    @Test
    public void test() {
        OffHeapPayloadArrayList list = null;
        try {
            ByteArrayTool bat = ByteArrayTool.get();
            list = new OffHeapPayloadArrayList(8);
            byte[] buf = new byte[8];
            for (int i = 0; i < 42; i++) {
                bat.putLong(buf, 0, i);
                list.add(42, buf);
            }
            bat.putLong(buf, 0, 1L << 42);
            list.add(1L << 42, buf);
            assertEquals("Size fail", 43, list.size());
            assertTrue("Capacity fail", list.capacity() >= 43);
            assertEquals("Header fail", 42, list.get(0));
            list.getPayload(0, buf);
            assertEquals("Payload fail", 0, bat.getLong(buf, 0));
            assertEquals("Header fail", 42, list.get(41));
            list.getPayload(41, buf);
            assertEquals("Payload fail", 41, bat.getLong(buf, 0));
            assertEquals("Header fail", 1L << 42, list.get(42));
            list.getPayload(42, buf);
            assertEquals("Payload fail", 1L << 42, bat.getLong(buf, 0));
        } finally {
            free(list);
        }
    }
}
