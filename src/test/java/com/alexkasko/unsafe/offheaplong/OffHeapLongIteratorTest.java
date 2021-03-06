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

import java.util.Iterator;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.free;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 7/3/13
 */
public class OffHeapLongIteratorTest {
    @Test
    public void testArray() {
        OffHeapLongArray arr = null;
        try {
            arr = new OffHeapLongArray(3);
            arr.set(0, 41);
            arr.set(1, 42);
            arr.set(2, 1L << 42);
            Iterator<Long> iter = arr.iterator();
            assertTrue("hasNext fail", iter.hasNext());
            assertEquals("Contents fail", 41, (long) iter.next());
            assertTrue("hasNext fail", iter.hasNext());
            assertEquals("Contents fail", 42, (long) iter.next());
            assertTrue("hasNext fail", iter.hasNext());
            assertEquals("Contents fail", 1L << 42, (long) iter.next());
            assertFalse("hasNext fail", iter.hasNext());
        } finally {
            free(arr);
        }
    }

    @Test
    public void testArrayList() {
        OffHeapLongArrayList arr = null;
        try {
            arr = new OffHeapLongArrayList();
            arr.add(41);
            arr.add(42);
            arr.add(1L << 42);
            Iterator<Long> iter = arr.iterator();
            // next values must be ignored by iterator
            arr.add(43);
            arr.add(44);
            arr.add(45);
            assertTrue("hasNext fail", iter.hasNext());
            assertEquals("Contents fail", 41, (long) iter.next());
            assertTrue("hasNext fail", iter.hasNext());
            assertEquals("Contents fail", 42, (long) iter.next());
            assertTrue("hasNext fail", iter.hasNext());
            assertEquals("Contents fail", 1L << 42, (long) iter.next());
            assertFalse("hasNext fail", iter.hasNext());
        } finally {
            free(arr);
        }
    }
}
