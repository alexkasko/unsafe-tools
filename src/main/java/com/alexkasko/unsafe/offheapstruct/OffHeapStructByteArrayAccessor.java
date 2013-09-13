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

/**
 * Struct's accessor implementation for byte array structs
 *
 * @author alexkasko
 * Date: 9/13/13
 */
class OffHeapStructByteArrayAccessor implements OffHeapStructAccessor {
    private final ByteArrayTool bt;
    private byte[] struct;

    OffHeapStructByteArrayAccessor(ByteArrayTool bt) {
        this.bt = bt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int structLength() {
        return struct.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(byte[] buffer) {
        bt.copy(struct, 0, buffer, 0, struct.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(int offset) {
        return bt.getByte(struct, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(int offset) {
        return bt.getUnsignedByte(struct, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(int offset) {
        return bt.getShort(struct, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(int offset) {
        return bt.getUnsignedShort(struct, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(int offset) {
        return bt.getInt(struct, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(int offset) {
        return bt.getUnsignedInt(struct, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(int offset) {
        return bt.getLong(struct, offset);
    }

    /**
     * Sets struct to access it
     *
     * @param struct struct
     */
    public void setStruct(byte[] struct) {
        this.struct = struct;
    }
}