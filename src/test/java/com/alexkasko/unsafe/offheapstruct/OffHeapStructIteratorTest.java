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

import java.util.Iterator;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 7/4/13
 */
public class OffHeapStructIteratorTest {

    @Test
    public void testArray() {
        OffHeapStructArray arr = null;
        try {
            ByteArrayTool bat = ByteArrayTool.get();
            arr = new OffHeapStructArray(3, 8);
            byte[] buf42 = new byte[8];
            bat.putInt(buf42, 0, 42);
            arr.set(0, buf42);
            byte[] buf43 = new byte[8];
            bat.putInt(buf43, 0, 43);
            arr.set(1, buf43);
            byte[] buf44 = new byte[8];
            bat.putInt(buf44, 0, 44);
            arr.set(2, buf44);

            Iterator<byte[]> iter = arr.iterator();
            assertTrue(iter.hasNext());
            assertArrayEquals("Contents fail", buf42, iter.next());
            assertTrue(iter.hasNext());
            assertArrayEquals("Contents fail", buf43, iter.next());
            assertTrue(iter.hasNext());
            assertArrayEquals("Contents fail", buf44, iter.next());
            assertFalse(iter.hasNext());
        } finally {
            OffHeapUtils.free(arr);
        }
    }

    @Test
    public void testArrayList() {
        OffHeapStructArrayList list = null;
        try {
            ByteArrayTool bat = ByteArrayTool.get();
            list = new OffHeapStructArrayList(8);
            byte[] buf = new byte[8];
            for (int i = 0; i < 42; i++) {
                bat.putLong(buf, 0, i);
                list.add(buf);
            }
            bat.putLong(buf, 0, 1L << 42);
            list.add(buf);

            Iterator<byte[]> iter = list.iterator();
            // next values must be ignored by iterator
            list.add(buf);
            list.add(buf);
            list.add(buf);
            list.get(0, buf);
            assertEquals("Payload fail", 0, bat.getLong(buf, 0));
            list.get(41, buf);
            assertEquals("Payload fail", 41, bat.getLong(buf, 0));
            list.get(42, buf);
            assertEquals("Payload fail", 1L << 42, bat.getLong(buf, 0));
        } finally {
            free(list);
        }
    }
}
