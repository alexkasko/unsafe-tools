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
 * <h1>Byte array operations using sun.misc.Unsafe</h1>
 *
 * <p>This package contains one public frontend class {@link com.alexkasko.unsafe.bytearray.ByteArrayTool}
 *  with two implementations -
 * main one ({@link com.alexkasko.unsafe.bytearray.UnsafeByteArrayTool}) that uses {@link sun.misc.Unsafe}
 * and fallback one ({@link com.alexkasko.unsafe.bytearray.BitShiftLittleEndianByteArrayTool}) that uses bit shifting.
 * It contains operations for reading and writing primitives from/into byte arrays and additional
 * copy operation.
 *
 * <h2>Operations</h2>
 * <p>All operations with {@link sun.misc.Unsafe} are done using platform endianness (little endian on most platform),
 * bitshift backend always uses little endian byte order.
 *
 * <h2>Boundary checks</h2>
 * <p>With {@link sun.misc.Unsafe} backend all operations have boundary checks using
 * <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/language/assert.html">assert</a> keyword.
 * With assertions enabled in runtime ({@code -ea} java switch) illegal memory access will thow {@code AssertionError}.
 * Without assertions illegal memory access will crash JVM.
 *
 * <p>Usage example (in tests): <a href="https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/bytearray/ByteArrayToolTest.java">github link</a>
 */

package com.alexkasko.unsafe.bytearray;