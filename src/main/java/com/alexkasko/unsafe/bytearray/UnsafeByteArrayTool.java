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

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Implementation of {@link ByteArrayTool} using {@code sun.misc.Unsafe}.
 * All bound checks are done using {@code assert} keyword, they may be enabled
 * with java {@code ea} switch.
 *
 * @author alexkasko
 * Date: 12/11/12
 */
class UnsafeByteArrayTool extends ByteArrayTool {

    private static final Unsafe UNSAFE;
    private static final long BYTE_ARRAY_OFFSET;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            int boo = UNSAFE.arrayBaseOffset(byte[].class);
            // It seems not all Unsafe implementations implement the following method.
            UNSAFE.copyMemory(new byte[1], boo, new byte[1], boo, 1);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnsafe() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 1 : offset;
        return UNSAFE.getByte(data, BYTE_ARRAY_OFFSET + offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(byte[] data, int offset, byte value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 1 : offset;
        UNSAFE.putByte(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 1 : offset;
        return (short) (UNSAFE.getByte(data, BYTE_ARRAY_OFFSET + offset) & 0xff);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedByte(byte[] data, int offset, short value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 1 : offset;
        assert value >= 0 : value;
        assert value < 1<<8 : value;
        UNSAFE.putByte(data, BYTE_ARRAY_OFFSET + offset, (byte) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 2 : offset;
        return UNSAFE.getShort(data, BYTE_ARRAY_OFFSET + offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(byte[] data, int offset, short value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 2 : offset;
        UNSAFE.putShort(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 2 : offset;
        return UNSAFE.getShort(data, BYTE_ARRAY_OFFSET + offset) & 0xffff;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedShort(byte[] data, int offset, int value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 2 : offset;
        assert value >= 0 : value;
        assert value < 1<<16 : value;
        UNSAFE.putShort(data, BYTE_ARRAY_OFFSET + offset, (short) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 4 : offset;
        return UNSAFE.getInt(data, BYTE_ARRAY_OFFSET + offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(byte[] data, int offset, int value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 4 : offset;
        UNSAFE.putInt(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 4 : offset;
        return UNSAFE.getInt(data, BYTE_ARRAY_OFFSET + offset) & 0xffffffffL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedInt(byte[] data, int offset, long value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 4 : offset;
        assert value >= 0 : value;
        assert value < 1L<<32 : value;
        UNSAFE.putInt(data, BYTE_ARRAY_OFFSET + offset, (int) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(byte[] data, int offset) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 8 : offset;
        return UNSAFE.getLong(data, BYTE_ARRAY_OFFSET + offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(byte[] data, int offset, long value) {
        assert offset >= 0 : offset;
        assert offset <= data.length - 8 : offset;
        UNSAFE.putLong(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        assert length > 0 : length;
        assert srcPos >= 0 : srcPos;
        assert srcPos <= src.length - length : srcPos;
        assert destPos >= 0 : destPos;
        assert destPos <= dest.length - length : destPos;
        UNSAFE.copyMemory(src, BYTE_ARRAY_OFFSET + srcPos, dest, BYTE_ARRAY_OFFSET + destPos, length);
    }
}
