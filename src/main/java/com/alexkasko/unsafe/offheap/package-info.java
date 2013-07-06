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
 * <h1>Off-heap memory operations using sun.misc.Unsafe</h1>
 *
 * <p>This package contains {@link com.alexkasko.unsafe.offheap.OffHeapMemory}
 * frontend class for working with off-heap memory:
 * allocation, immediate deallocation, reading/writing primitives and byte arrays.
 *
 * <h2>Implementations</h2>
 * <p>Main implementation ({@link com.alexkasko.unsafe.offheap.UnsafeOffHeapMemory}) uses {@link sun.misc.Unsafe},
 * fallback implementation ({@link com.alexkasko.unsafe.offheap.DirectOffHeapMemory}) uses {@link java.nio.DirectByteBuffer}s.
 *
 * <h2>Boundary checks</h2>
 * <p>With {@link sun.misc.Unsafe} backend all operations have boundary checks using
 * <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/language/assert.html">assert</a> keyword.
 * With assertions enabled in runtime ({@code -ea} java switch) illegal memory access will thow {@code AssertionError}.
 * Without assertions illegal memory access will crash JVM.
 *
 * <h2>Fallback implementation</h2>
 * <p>Fallback implementation have some drawbacks comparing with unsafe one:
 * <ul>
 *     <li>mandatory boundary checks</li>
 *     <li>each memory allocation is limited by {@code Integer.MAX_VALUE}</li>
 *     <li>by default off-heap memory is freed only when {@link java.nio.DirectByteBuffer}
 *          is garbage collector; this library uses reflection hacks
 *          (different for OpenJDK and Android implementations) to free memory eagerly</li>
 * </ul>
 *
 * <h2>Data structures</h2>
 * <p>{@link com.alexkasko.unsafe.offheap.OffHeapMemory} is used as a base for off-heap data structures, see {@link com.alexkasko.unsafe.offheaplong}
 * and {@link com.alexkasko.unsafe.offheapstruct} packages.
 *
 * <h2>Additional utility classes</h2>
 * <ul>
 *     <li>{@link com.alexkasko.unsafe.offheap.OffHeapUtils}: allows freeing memory quietly checking null on provided instance</li>
 *     <li>{@link com.alexkasko.unsafe.offheap.OffHeapDisposable}: interface for freeable classes</li>
 * </ul>
 *
 * <p>Usage example (in tests): <a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheap/OffHeapMemoryTest.java">github link</a>
 *
 */

package com.alexkasko.unsafe.offheap;