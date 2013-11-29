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

import com.alexkasko.unsafe.offheap.OffHeapDisposableIterable;

/**
 * Base interface for off-heap struct (memory areas of equal sizes) collection.
 *
 * @author alexkasko
 * Date: 7/3/13
 */
public interface OffHeapStructCollection extends OffHeapDisposableIterable<byte[]> {

    /**
     * Returns length of the single struct in bytes
     *
     * @return length of the single struct in bytes
     */
    int structLength();

    /**
     * Returns number of elements in this array
     *
     * @return number of elements in this array
     */
    long size();

    /**
     * Copies struct on specified index into specified buffer
     *
     * @param index array index
     * @param buffer buffer to copy struct into
     */
    void get(long index, byte[] buffer);

    /**
     * Copies specified struct contents onto specified index
     *
     * @param index array index
     * @param struct struct to copy into array
     */
    void set(long index, byte[] struct);

    /**
     * Gets byte from struct on specified index with specified offset
     *
     * @param index array index
     * @param offset byte array index
     * @return byte
     */
    byte getByte(long index, int offset);

    /**
     * Puts byte into struct onto specified index with specified offset
     *
     * @param index array index
     * @param offset byte array index
     * @param value value
     */
    void putByte(long index, int offset, byte value);

    /**
     * Gets one byte (stored as one signed byte) from struct on specified index
     * with specified offset, converts it to unsigned and returns it as short
     *
     * @param index array index
     * @param offset byte array index
     * @return unsigned byte as short
     */
    short getUnsignedByte(long index, int offset);

    /**
     * Puts short with value from 0 to 255 inclusive into struct onto specified
     * index with specified offset as one signed byte
     *
     * @param index array index
     * @param offset byte array index
     * @param value unsigned byte
     */
    void putUnsignedByte(long index, int offset, short value);

    /**
     * Gets two bytes as short from struct on specified index with specified offset
     *
     * @param index array index
     * @param offset byte array offset
     * @return short value
     */
    short getShort(long index, int offset);

    /**
     * Puts short into struct onto specified index with specified offset as two bytes
     *
     * @param index array index
     * @param offset byte array offset
     * @param value short value
     */
    void putShort(long index, int offset, short value);

    /**
     * Gets unsigned short (stored as two bytes) from struct on specified index
     * with specified offset and returns it as int
     *
     * @param index array index
     * @param offset byte array offset
     * @return unsigned short as int
     */
    int getUnsignedShort(long index, int offset);

    /**
     * Puts int with value from 0 to 65535 inclusive into struct onto specified
     * index with specified offset as two bytes
     *
     * @param index array index
     * @param offset byte array offset
     * @param value unsigned short as int
     */
    void putUnsignedShort(long index, int offset, int value);

    /**
     * Gets four bytes as int from struct on specified index with specified offset
     *
     * @param index array index
     * @param offset byte array offset
     * @return int value
     */
    int getInt(long index, int offset);

    /**
     * Puts int into struct onto specified index with specified offset as four bytes
     *
     * @param index array index
     * @param offset byte array offset
     * @param value int value
     */
    void putInt(long index, int offset, int value);

    /**
     * Gets unsigned int (stored as 4 bytes) and returns it as long
     * from struct on specified index with specified offset
     *
     * @param index array index
     * @param offset byte array offset
     * @return unsigned int as long
     */
    long getUnsignedInt(long index, int offset);

    /**
     * Puts long value from 0 to 4294967295 inclusive into struct onto specified index
     * with specified offset as four bytes
     *
     * @param index array index
     * @param offset byte array offset
     * @param value unsigned int as long
     */
    void putUnsignedInt(long index, int offset, long value);

    /**
     * Gets long from struct on specified index with specified offset
     *
     * @param index array index
     * @param offset byte array offset
     * @return long value
     */
    long getLong(long index, int offset);

    /**
     * Puts long into struct onto specified index with specified offset as eight bytes
     *
     * @param index array index
     * @param offset byte array offset
     * @param value long value
     */
    void putLong(long index, int offset, long value);
}
