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

/**
 * <h1>Long sized off-heap collections of longs with sorting and searching support</h1>
 *
 * <p>This package contains implementations of fixed-sized array of longs ({@link com.alexkasko.unsafe.offheaplong.OffHeapLongArray})
 * and growing array list of longs ({@link com.alexkasko.unsafe.offheaplong.OffHeapLongArrayList}). Both classes are implemented on top of
 * {@link com.alexkasko.unsafe.offheap.OffHeapMemory}.
 *
 * <h2>Features</h2>
 * <ul>
 *  <li>long indexes (size is not bounded by {@code Integer.MAX_VALUE})</li>
 *  <li>GC doesn't know about allocated memory so you can allocate gigabytes of memory without additional load on GC</li>
 *  <li>memory may be freed to OS eagerly (it's not mandatory, {@link com.alexkasko.unsafe.offheap.OffHeapMemory}
 *  will free allocated memory when it's instance will be garbage collected)</li>
 * </ul>
 *
 * <h2>Iterators</h2>
 * <p>Both collections implements {@link java.util.Iterable}, but {@code foreach} must be used with caution, because
 * in java 6/7 iterators causes new autoboxed {@link java.lang.Long} object creation for each call to
 * {@link java.util.Iterator#next()}.
 *
 * <h2>Operations</h2>
 * <ul>
 *     <li>sorting using {@link com.alexkasko.unsafe.offheaplong.OffHeapLongSorter}: implementation of Dual-Pivot quicksort algorithm
 *      adapted to off-heap collections</li>
 *     <li>binary search over sorted collections using {@link com.alexkasko.unsafe.offheaplong.OffHeapLongBinarySearch}</li>
 *     <li>binary search returning ranges of equal values:
 *     {@link com.alexkasko.unsafe.offheaplong.OffHeapLongBinarySearch#binarySearchRange(com.alexkasko.unsafe.offheap.OffHeapAddressable, long, com.alexkasko.unsafe.offheaplong.OffHeapLongBinarySearch.IndexRange)}</li>
 * </ul>
 *
 * <h2>Long packing</h2>
 * <p>{@link com.alexkasko.unsafe.offheaplong.LongPacker} - utility class for storing one int primitive and one long with bounded value into one long
 *
 * <h2>Usage example in tests (github links)</h2>
 * <ul>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheaplong/OffHeapLongArrayTest.java">array</a></li>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheaplong/OffHeapLongArrayListTest.java">array list</a></li>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheaplong/OffHeapLongSorterTest.java">sorting</a></li>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheaplong/OffHeapLongBinarySearchTest.java">binary search</a></li>
 * </ul>
 *
 */

package com.alexkasko.unsafe.offheaplong;