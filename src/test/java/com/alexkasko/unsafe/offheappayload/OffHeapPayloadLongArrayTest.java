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

import com.alexkasko.unsafe.offheap.OffHeapUtils;
import com.alexkasko.unsafe.offheappayload.OffHeapPayloadLongArray;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadLongArrayTest {
    @Test
    public void test() {
        OffHeapPayloadLongArray arr = null;
        try {
            arr = new OffHeapPayloadLongArray(3);
            arr.set(0, 42, 421);
            arr.set(1, 43, 431);
            arr.set(2, 44, 1L << 42);

            assertEquals(42, arr.get(0));
            assertEquals(421, arr.getPayload(0));
            assertEquals(43, arr.get(1));
            assertEquals(431, arr.getPayload(1));
            assertEquals(44, arr.get(2));
            assertEquals(1L << 42, arr.getPayload(2));
        } finally {
            OffHeapUtils.free(arr);
        }
    }
}
