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
 * <p>Allocates an area of off-heap memory that is not a subject to GC.
 * Default implementation uses {@code sun.misc.Unsafe}, with all operations guarded with {@code assert}
 * boundary checks.
 * With assert enabled in runtime ({@code -ea} java switch) {@link AssertionError}
 * will be thrown on illegal input. Without assertions illegal input will crash JVM.
 *
 * <p>Fallback implementation uses {@link java.nio.ByteBuffer#allocateDirect(int)} that may (OpenJDK)
 * or may not (Dalvik) use {@code sun.misc.Unsafe} under the hood.
 * Note: only unsafe implementation ({@link #allocateMemoryUnsafe(long)}) may use memory areas longer than {@code Integer.MAX_VALUE}
 *
 * <p>Unsafe implementation uses native endianness (LE on most platforms). Fallback implementation uses Little Endian byte order.
 *
 * <p>Provides methods to access allocated memory. More complex data structures (off-heap quasi-objects) may be
 * constructed wrapping this class, see {@link com.alexkasko.unsafe.offheaplong.OffHeapLongArray}.
 *
 * <p>Memory may be freed manually calling {@link #free()} (thread-safe and may be called safely more than one time),
 * or it will be freed when {@link OffHeapMemory} instance itself will be garbage collected.
 *
 * @author alexkasko
 * Date: 1/14/13
 */
public abstract class OffHeapMemory {
    /**
     * Allocates memory using {@code sun.misc.Unsafe} if it's proper implementation is available.
     * Allocates using {@link java.nio.ByteBuffer#allocateDirect(int)} otherwise.
     *
     * @param bytes amount of memory to allocate
     * @return {@code OffHeapMemory} instance
     */
    public static OffHeapMemory allocateMemory(long bytes) {
        try {
            return allocateMemoryUnsafe(bytes);
        } catch (Exception e) {
            return allocateMemoryDirect(bytes);
        }
    }

    /**
     * Allocates memory using {@code sun.misc.Unsafe}
     *
     * @param bytes amount of memory to allocate
     * @return {@code OffHeapMemory} instance
     * @throws Exception if proper {@code sun.misc.Unsafe} not available
     */
    public static OffHeapMemory allocateMemoryUnsafe(long bytes) throws Exception {
        try {
            Class<? extends OffHeapMemory> unsafeMaClass = OffHeapMemory.class
                    .getClassLoader()
                    .loadClass(OffHeapMemory.class.getPackage().getName() + ".UnsafeOffHeapMemory")
                    .asSubclass(OffHeapMemory.class);
            return unsafeMaClass.getDeclaredConstructor(long.class).newInstance(bytes);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    /**
     * Allocates memory using {@link java.nio.ByteBuffer#allocateDirect(int)}
     *
     * @param bytes amount of memory to allocate
     * @return {@code OffHeapMemory} instance
     */
    public static OffHeapMemory allocateMemoryDirect(long bytes) {
        return new DirectOffHeapMemory(bytes);
    }

    /**
     * Whether current implementation uses {@code sun.misc.Unsafe}
     *
     * @return whether unsafe
     */
    public abstract boolean isUnsafe();

    /**
     * Returns length of allocated memory area
     *
     * @return length of allocated memory area
     */
    public abstract long length();

    /**
     * Frees allocated memory, may be called multiple times from any thread
     */
    public abstract void free();

    /**
     * Copies byte array contents into memory area
     *
     * @param offset memory area offset
     * @param buffer source byte array
     * @param bufferOffset position in byte array
     * @param bytes bytes length to copy
     */
    public abstract void put(long offset, byte[] buffer, int bufferOffset, int bytes);

    /**
     * Copies byte array contents into memory area
     *
     * @param offset memory area offset
     * @param buffer source byte array
     */
    public abstract void put(long offset, byte[] buffer);

    /**
     * Copies part of memory area into byte array
     *
     * @param offset memory area offset
     * @param buffer destination byte array
     * @param bufferOffset position in byte array
     * @param bytes bytes length to copy
     */
    public abstract void get(long offset, byte[] buffer, int bufferOffset, int bytes);

    /**
     * Copies part of memory area into byte array
     *
     * @param offset memory area offset
     * @param buffer destination byte array
     */
    public abstract void get(long offset, byte[] buffer);

    /**
     * Gets byte from memory area
     *
     * @param offset memory area offset
     * @return byte value
     */
    public abstract byte getByte(long offset);

    /**
     * Puts byte into memory area
     *
     * @param offset memory area offset
     * @param value byte value
     */
    public abstract void putByte(long offset, byte value);

    /**
     * Gets one byte (stored as one signed byte), converts it to unsigned
     * and returns it as short
     *
     * @param offset memory area offset
     * @return unsigned byte as short
     */
    public abstract short getUnsignedByte(long offset);

    /**
     * Puts short with value from 0 to 255 inclusive into memory area as one
     * signed byte
     *
     * @param offset memory area offset
     * @param value unsigned byte as short
     */
    public abstract void putUnsignedByte(long offset, short value);

    /**
     * Gets two bytes from memory area as short
     *
     * @param offset memory area offset
     * @return short value
     */
    public abstract short getShort(long offset);

    /**
     * Puts short into memory area as two bytes
     *
     * @param offset memory area offset
     * @param value short value
     */
    public abstract void putShort(long offset, short value);

    /**
     * Gets unsigned short from memory area (stored as two bytes) and returns it as int
     *
     * @param offset memory area offset
     * @return unsigned short as int
     */
    public abstract int getUnsignedShort(long offset);

    /**
     * Puts int with value from 0 to 65535 inclusive into memory are as two bytes
     *
     * @param offset memory area offset
     * @param value unsigned short as int
     */
    public abstract void putUnsignedShort(long offset, int value);

    /**
     * Gets four bytes from memory area as int
     *
     * @param offset memory area offset
     * @return int value
     */
    public abstract int getInt(long offset);

    /**
     * Puts int into memory area as four bytes
     *
     * @param offset memory area offset
     * @param value int value
     */
    public abstract void putInt(long offset, int value);

    /**
     * Gets unsigned int (stored as 4 bytes) and returns it as long
     *
     * @param offset memory area offset
     * @return unsigned int as long
     */
    public abstract long getUnsignedInt(long offset);

    /**
     * Puts long value from 0 to 4294967295 inclusive into memory area as four bytes
     *
     * @param offset memory area offset
     * @param value unsigned int as long
     */
    public abstract void putUnsignedInt(long offset, long value);

    /**
     * Gets eight bytes from memory area as long
     *
     * @param offset memory area offset
     * @return long value
     */
    public abstract long getLong(long offset);

    /**
     * Puts long into memory area as eight bytes
     *
     * @param offset memory area offset
     * @param value long value
     */
    public abstract void putLong(long offset, long value);

    /**
     * Copies memory from this instance's area into another instance's area
     *
     * @param offset this memory area offset
     * @param destination destination memory area
     * @param destOffset destination memory area offset
     * @param bytes memory length in bytes to copy
     */
    public abstract void copy(long offset, OffHeapMemory destination, long destOffset, long bytes);
}