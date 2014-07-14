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
import com.alexkasko.unsafe.offheap.OffHeapDisposable;
import com.alexkasko.unsafe.offheap.OffHeapDisposableIterator;
import com.alexkasko.unsafe.offheap.OffHeapMemory;

/**
 * <p>Implementation of off-heap array of structs (memory areas of equal sizes).
 *
 * <p>Default implementation uses {@code sun.misc.Unsafe}, with all operations guarded with {@code assert} keyword.
 * With assertions enabled in runtime ({@code -ea} java switch) {@link AssertionError}
 * will be thrown on illegal index access. Without assertions illegal index will crash JVM.
 *
 * <p>Array won't be zeroed after creation (will contain garbage by default).
 * Allocated memory may be freed manually using {@link #free()} (thread-safe
 * and may be called multiple times) or it will be freed after {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructArray}
 * instance will be garbage collected.
 *
 * @author alexkasko
 * Date: 7/3/13
 */
public class OffHeapStructArray implements OffHeapStructCollection, OffHeapDisposable {

    private final OffHeapMemory ohm;
    private final int structLength;

    /**
      * Constructor
      *
      * @param size array size
      * @param structLength length of struct in bytes, must be >= {@code 8}
      */
    public OffHeapStructArray(long size, int structLength) {
        if(structLength < 8) throw new IllegalArgumentException(
                "structLength must be greater or equal to 8, but was: [" + structLength + "]");
        this.structLength = structLength;
        this.ohm = OffHeapMemory.allocateMemory(size * structLength);
    }

    /**
     * Constructor, uses {@link com.alexkasko.unsafe.offheap.OnHeapMemory} underneath
     * effectively making this instance an <b>OnHeap</b> collection
     *
     * @param bt byte array tool to manage on-heap memory of this collection
     * @param size array size
     * @param structLength length of struct in bytes, must be >= {@code 8}
     */
    public OffHeapStructArray(ByteArrayTool bt, int size, int structLength) {
        if(structLength < 8) throw new IllegalArgumentException(
                "structLength must be greater or equal to 8, but was: [" + structLength + "]");
        this.structLength = structLength;
        this.ohm = OffHeapMemory.allocateMemoryOnHeap(bt, size * structLength);
    }

    /**
     * Private constructor for {@link #clone()} support
     *
     * @param ohm cloned memory
     * @param structLength struct length
     */
    private OffHeapStructArray(OffHeapMemory ohm, int structLength) {
        this.ohm = ohm;
        this.structLength = structLength;
    }

    /**
     * Returns length of the single struct in bytes
     *
     * @return length of the single struct in bytes
     */
    @Override
    public int structLength() {
        return structLength;
    }

    /**
     * Returns number of elements in this array
     *
     * @return number of elements in this array
     */
    @Override
    public long size() {
        return ohm.length() / structLength;
    }

    /**
     * Frees allocated memory, may be called multiple times from any thread
     */
    @Override
    public void free() {
        ohm.free();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OffHeapDisposableIterator<byte[]> iterator() {
        return new OffHeapStructIterator(this);
    }

    /**
     * Whether unsafe implementation of {@link OffHeapMemory} is used
     *
     * @return whether unsafe implementation of {@link OffHeapMemory} is used
     */
    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    /**
     * Copies struct on specified index into specified buffer
     *
     * @param index array index
     * @param buffer buffer to copy struct into
     */
    @Override
    public void get(long index, byte[] buffer) {
        ohm.get(index * structLength, buffer);
    }

    /**
     * Copies struct on specified index into specified buffer
     *
     * @param index array index
     * @param buffer buffer to copy struct into
     * @param bufferPos start position in specified buffer
     */
    public void get(long index, byte[] buffer, int bufferPos) {
        ohm.get(index * structLength, buffer, bufferPos, structLength);
    }

    /**
     * Copies part of struct on specified index into specified buffer
     *
     * @param index array index
     * @param pos position in struct
     * @param buffer buffer to copy struct into
     * @param bufferPos start position in specified buffer
     * @param length number of bytes to copy
     */
    @Override
    public void get(long index, int pos, byte[] buffer, int bufferPos, int length) {
        ohm.get(index * structLength + pos, buffer, bufferPos, length);
    }

    /**
     * Copies specified struct contents onto specified index
     *
     * @param index array index
     * @param struct struct to copy into array
     */
    @Override
    public void set(long index, byte[] struct) {
        ohm.put(index * structLength, struct);
    }

    /**
     * Copies specified struct contents onto specified index
     *
     * @param index array index
     * @param struct struct to copy into array
     * @param structPos start position in specified struct
     */
    public void set(long index, byte[] struct, int structPos) {
        ohm.put(index * structLength, struct, structPos, structLength);
    }

    /**
     * Gets byte from struct on specified index with specified offset
     *
     * @param index  array index
     * @param offset struct offset
     * @return byte
     */
    @Override
    public byte getByte(long index, int offset) {
        assert offset <= structLength - 1 : offset;
        return ohm.getByte(index * structLength + offset);
    }

    /**
     * Puts byte into struct onto specified index with specified offset
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  value
     */
    @Override
    public void putByte(long index, int offset, byte value) {
        assert offset <= structLength - 1 : offset;
        ohm.putByte(index * structLength + offset, value);
    }

    /**
     * Gets one byte (stored as one signed byte) from struct on specified index
     * with specified offset, converts it to unsigned and returns it as short
     *
     * @param index  array index
     * @param offset struct offset
     * @return unsigned byte as short
     */
    @Override
    public short getUnsignedByte(long index, int offset) {
        assert offset <= structLength - 1 : offset;
        return ohm.getUnsignedByte(index * structLength + offset);
    }

    /**
     * Puts short with value from 0 to 255 inclusive into struct onto specified
     * index with specified offset as one signed byte
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  unsigned byte
     */
    @Override
    public void putUnsignedByte(long index, int offset, short value) {
        assert offset <= structLength - 1 : offset;
        ohm.putUnsignedByte(index * structLength + offset, value);
    }

    /**
     * Gets two bytes as short from struct on specified index with specified offset
     *
     * @param index  array index
     * @param offset struct offset
     * @return short value
     */
    @Override
    public short getShort(long index, int offset) {
        assert offset <= structLength - 2 : offset;
        return ohm.getShort(index * structLength + offset);
    }

    /**
     * Puts short into struct onto specified index with specified offset as two bytes
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  short value
     */
    @Override
    public void putShort(long index, int offset, short value) {
        assert offset <= structLength - 2 : offset;
        ohm.putShort(index * structLength + offset, value);
    }

    /**
     * Gets unsigned short (stored as two bytes) from struct on specified index
     * with specified offset and returns it as int
     *
     * @param index  array index
     * @param offset struct offset
     * @return unsigned short as int
     */
    @Override
    public int getUnsignedShort(long index, int offset) {
        assert offset <= structLength - 2 : offset;
        return ohm.getUnsignedShort(index * structLength + offset);
    }

    /**
     * Puts int with value from 0 to 65535 inclusive into struct onto specified
     * index with specified offset as two bytes
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  unsigned short as int
     */
    @Override
    public void putUnsignedShort(long index, int offset, int value) {
        assert offset <= structLength - 2 : offset;
        ohm.putUnsignedShort(index * structLength + offset, value);
    }

    /**
     * Gets four bytes as int from struct on specified index with specified offset
     *
     * @param index  array index
     * @param offset struct offset
     * @return int value
     */
    @Override
    public int getInt(long index, int offset) {
        assert offset <= structLength - 4 : offset;
        return ohm.getInt(index * structLength + offset);
    }

    /**
     * Puts int into struct onto specified index with specified offset as four bytes
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  int value
     */
    @Override
    public void putInt(long index, int offset, int value) {
        assert offset <= structLength - 4 : offset;
        ohm.putInt(index * structLength + offset, value);
    }

    /**
     * Gets unsigned int (stored as 4 bytes) and returns it as long
     * from struct on specified index with specified offset
     *
     * @param index  array index
     * @param offset struct offset
     * @return unsigned int as long
     */
    @Override
    public long getUnsignedInt(long index, int offset) {
        assert offset <= structLength - 4 : offset;
        return ohm.getUnsignedInt(index * structLength + offset);
    }

    /**
     * Puts long value from 0 to 4294967295 inclusive into struct onto specified index
     * with specified offset as four bytes
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  unsigned int as long
     */
    @Override
    public void putUnsignedInt(long index, int offset, long value) {
        assert offset <= structLength - 4 : offset;
        ohm.putUnsignedInt(index * structLength + offset, value);
    }

    /**
     * Gets long from struct on specified index with specified offset
     *
     * @param index  array index
     * @param offset struct offset
     * @return long value
     */
    @Override
    public long getLong(long index, int offset) {
        assert offset <= structLength - 8 : offset;
        return ohm.getLong(index * structLength + offset);
    }

    /**
     * Puts long into struct onto specified index with specified offset as eight bytes
     *
     * @param index  array index
     * @param offset struct offset
     * @param value  long value
     */
    @Override
    public void putLong(long index, int offset, long value) {
        assert offset <= structLength - 8 : offset;
        ohm.putLong(index * structLength + offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OffHeapStructArray clone() {
        OffHeapMemory cloned = ohm.clone();
        return new OffHeapStructArray(cloned, structLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapStructArray");
        sb.append("{ohm=").append(ohm);
        sb.append(", structLength=").append(structLength);
        sb.append('}');
        return sb.toString();
    }
}
