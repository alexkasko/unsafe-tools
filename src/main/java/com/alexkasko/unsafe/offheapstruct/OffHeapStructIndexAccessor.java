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
 * Struct's accessor implementation for off-heap stored structs
 *
 * @author alexkasko
 * Date: 9/13/13
 */
class OffHeapStructIndexAccessor implements OffHeapStructAccessor {
    private final OffHeapStructCollection col;
    private long index = -1;

    OffHeapStructIndexAccessor(OffHeapStructCollection col) {
        this.col = col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int structLength() {
        return col.structLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(byte[] buffer) {
        col.get(index, buffer);
    }

    @Override
    public byte getByte(int offset) {
        return col.getByte(index, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(int offset) {
        return col.getUnsignedByte(index, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(int offset) {
        return col.getShort(index, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(int offset) {
        return col.getUnsignedShort(index, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(int offset) {
        return col.getInt(index, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(int offset) {
        return col.getUnsignedInt(index, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(int offset) {
        return col.getLong(index, offset);
    }

    /**
     * Sets index value
     *
     * @param index index value
     */
    void setIndex(long index) {
        this.index = index;
    }
}
