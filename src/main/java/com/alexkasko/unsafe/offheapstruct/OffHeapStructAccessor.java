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

/**
 * Interface for accessing fields of a single struct
 *
 * @author alexkasko
 * Date: 9/12/13
 */
public interface OffHeapStructAccessor {

    /**
     * Returns length of the single struct in bytes
     *
     * @return length of the single struct in bytes
     */
    int structLength();

    /**
     * Copies struct on specified index into specified buffer
     *
     * @param buffer buffer to copy struct into
     */
    void get(byte[] buffer);

    /**
     * Gets byte from struct on specified index with specified offset
     *
     * @param offset byte array index
     * @return byte
     */
    byte getByte(int offset);

    /**
     * Gets one byte (stored as one signed byte) from struct on specified index
     * with specified offset, converts it to unsigned and returns it as short
     *
     * @param offset byte array index
     * @return unsigned byte as short
     */
    short getUnsignedByte(int offset);

    /**
     * Gets two bytes as short from struct on specified index with specified offset
     *
     * @param offset byte array offset
     * @return short value
     */
    short getShort(int offset);

    /**
     * Gets unsigned short (stored as two bytes) from struct on specified index
     * with specified offset and returns it as int
     *
     * @param offset byte array offset
     * @return unsigned short as int
     */
    int getUnsignedShort(int offset);

    /**
     * Gets four bytes as int from struct on specified index with specified offset
     *
     * @param offset byte array offset
     * @return int value
     */
    int getInt(int offset);

    /**
     * Gets unsigned int (stored as 4 bytes) and returns it as long
     * from struct on specified index with specified offset
     *
     * @param offset byte array offset
     * @return unsigned int as long
     */
    long getUnsignedInt(int offset);

    /**
     * Gets long from struct on specified index with specified offset
     *
     * @param offset byte array offset
     * @return long value
     */
    long getLong(int offset);
}
