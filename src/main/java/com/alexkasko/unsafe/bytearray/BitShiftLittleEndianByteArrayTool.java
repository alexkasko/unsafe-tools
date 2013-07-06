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

package com.alexkasko.unsafe.bytearray;

import static java.lang.System.arraycopy;

/**
 * Implementation of {@link ByteArrayTool} using bits shifting and standard
 * byte array operations.
 *
 * @author alexkasko
 * Date: 12/11/12
 */
class BitShiftLittleEndianByteArrayTool extends ByteArrayTool {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnsafe() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(byte[] data, int offset) {
        return data[offset];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(byte[] data, int offset, byte value) {
        data[offset] = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(byte[] data, int offset) {
        return (short) (data[offset] & 0xff);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedByte(byte[] data, int offset, short value) {
        if(value < 0 || value >= 1<<8) throw new IllegalArgumentException(Short.toString(value));
        data[offset] = (byte) value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(byte[] data, int offset) {
        return (short) ((data[offset + 0] & 0xff) << 0 |
                        (data[offset + 1] & 0xff) << 8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(byte[] data, int offset, short value) {
        data[offset + 0] = (byte) (value >>> 0);
        data[offset + 1] = (byte) (value >>> 8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(byte[] data, int offset) {
        return ((data[offset + 0] & 0xff) << 0 |
                (data[offset + 1] & 0xff) << 8) & 0xffff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedShort(byte[] data, int offset, int value) {
        if(value < 0 || value >= 1<<16) throw new IllegalArgumentException(Integer.toString(value));
        data[offset + 0] = (byte) (value >>> 0);
        data[offset + 1] = (byte) (value >>> 8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(byte[] data, int offset) {
        return (data[offset + 0] & 0xff) << 0 |
               (data[offset + 1] & 0xff) << 8 |
               (data[offset + 2] & 0xff) << 16 |
               (data[offset + 3] & 0xff) << 24;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(byte[] data, int offset, int value) {
        data[offset + 0] = (byte) (value >>> 0);
        data[offset + 1] = (byte) (value >>> 8);
        data[offset + 2] = (byte) (value >>> 16);
        data[offset + 3] = (byte) (value >>> 24);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(byte[] data, int offset) {
        return ((data[offset + 0] & 0xff) << 0 |
                (data[offset + 1] & 0xff) << 8 |
                (data[offset + 2] & 0xff) << 16 |
                (data[offset + 3] & 0xff) << 24) & 0xffffffffL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedInt(byte[] data, int offset, long value) {
        if(value < 0 || value >= 1L<<32) throw new IllegalArgumentException(Long.toString(value));
        data[offset + 0] = (byte) (value >>> 0);
        data[offset + 1] = (byte) (value >>> 8);
        data[offset + 2] = (byte) (value >>> 16);
        data[offset + 3] = (byte) (value >>> 24);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(byte[] data, int offset) {
        return (data[offset + 0] & 0xffL) << 0 |
               (data[offset + 1] & 0xffL) << 8 |
               (data[offset + 2] & 0xffL) << 16 |
               (data[offset + 3] & 0xffL) << 24 |
               (data[offset + 4] & 0xffL) << 32 |
               (data[offset + 5] & 0xffL) << 40 |
               (data[offset + 6] & 0xffL) << 48 |
               (data[offset + 7] & 0xffL) << 56;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(byte[] data, int offset, long value) {
        data[offset + 0] = (byte) (value >>> 0);
        data[offset + 1] = (byte) (value >>> 8);
        data[offset + 2] = (byte) (value >>> 16);
        data[offset + 3] = (byte) (value >>> 24);
        data[offset + 4] = (byte) (value >>> 32);
        data[offset + 5] = (byte) (value >>> 40);
        data[offset + 6] = (byte) (value >>> 48);
        data[offset + 7] = (byte) (value >>> 56);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
        arraycopy(input, inputIndex, output, outputIndex, length);
    }
}
