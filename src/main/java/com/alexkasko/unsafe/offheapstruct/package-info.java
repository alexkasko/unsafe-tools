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
 * <h1>Long sized off-heap collections of "structs" (memory areas with equal sizes)
 *  with sorting and searching support</h1>
 *
 * <p>This package contains implementations of fixed-sized array of "structs" ({@link com.alexkasko.unsafe.offheapstruct.OffHeapStructArray})
 * and growing array list of structs ({@link com.alexkasko.unsafe.offheapstruct.OffHeapStructArrayList}). Both classes are implemented on top of
 * {@link com.alexkasko.unsafe.offheap.OffHeapMemory}.
 *
 * <h2>Structs</h2>
 * <p>Struct here means a memory area (located in off-heap memory or loaded into byte array) of limited size
 * with predefined locations of primitive "fields" in it. Such memory areas may be used like C structs
 * (this library doesn't enforce memory alighment so they are more alike to packed C structs).
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
 * <p>Both collections implements {@link java.util.Iterable} returning elements copying it into one predefined byte array.
 *
 * <h2>Operations</h2>
 * <ul>
 *     <li>reading/writing primitives directly from/into off-heap memory without copying full structs into byte arrays</li>
 *     <li>sorting using {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructSorter}: implementation of Dual-Pivot quicksort algorithm
 *      adapted to off-heap collections. May use arbitrary fields from struct as sort keys with signed or unsigned comparison.</li>
 *     <li>binary search over sorted collections using {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructBinarySearch}.
 *      May use arbitrary field from struct to search on it.</li>
 *     <li>binary search returning ranges of equal values:
 *     {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructBinarySearch#binarySearchRangeByLongKey(com.alexkasko.unsafe.offheapstruct.OffHeapStructCollection, long, int, com.alexkasko.unsafe.offheapstruct.OffHeapStructBinarySearch.IndexRange)}</li>
 * </ul>
 *
 * <h2>Usage example in tests (github links)</h2>
 * <ul>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheapstruct/OffHeapStructArrayTest.java">array</a></li>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheapstruct/OffHeapStructArrayListTest.java">array list</a></li>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheapstruct/OffHeapStructSorterTest.java">sorting</a></li>
 *  <li><a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheapstruct/OffHeapStructBinarySearchTest.java">binary search</a></li>
 * </ul>
 *
 */

package com.alexkasko.unsafe.offheapstruct;