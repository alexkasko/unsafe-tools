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

package com.alexkasko.unsafe.offheap;

/**
 * Basic interface for off-heap long-indexed collections. Required for {@link com.alexkasko.unsafe.offheaplong.OffHeapLongBinarySearch}.
 *
 * @author alexkasko
 * Date: 3/5/13
 */
public interface OffHeapAddressable {
    /**
     * Gets the element at position {@code index}
     *
     * @param index collection index
     * @return long value
     */
    long get(long index);

    /**
     * Returns number of elements in collection
     *
     * @return number of elements in collection
     */
    long size();
}
