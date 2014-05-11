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

import com.alexkasko.unsafe.bytearray.ByteArrayTool;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link com.alexkasko.unsafe.offheap.OffHeapMemory}
 * using byte array effectively making it <b>OnHeap</b> memory
 *
 * @author alexkasko
 * Date: 1/14/13
*/
class OnHeapMemory extends OffHeapMemory {

    private final ByteArrayTool bt;
    private byte[] mem;
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    OnHeapMemory(ByteArrayTool bt, long bytes) {
        if(bytes > Integer.MAX_VALUE) throw new IllegalArgumentException(
                "Long-sized allocations are not supported by [" + getClass().getName() + "]");
        this.bt = bt;
        this.mem = new byte[(int) bytes];
    }

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
    public long length() {
        return mem.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        if(!disposed.compareAndSet(false, true)) return;
        this.mem = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(long offset, byte[] buffer, int bufferOffset, int bytes) {
        bt.copy(buffer, bufferOffset, mem, (int) offset, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(long offset, byte[] buffer) {
        bt.copy(buffer, 0, mem, (int) offset, buffer.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(long offset, byte[] buffer, int bufferOffset, int bytes) {
        bt.copy(mem, (int) offset, buffer, bufferOffset, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(long offset, byte[] buffer) {
        bt.copy(mem, (int) offset, buffer, 0, buffer.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(long offset) {
        return bt.getByte(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(long offset, byte value) {
        bt.putByte(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(long offset) {
        return bt.getUnsignedByte(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedByte(long offset, short value) {
        bt.putUnsignedByte(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(long offset) {
        return bt.getShort(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(long offset, short value) {
        bt.putShort(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(long offset) {
        return bt.getUnsignedShort(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedShort(long offset, int value) {
        bt.putUnsignedShort(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(long offset) {
        return bt.getInt(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(long offset, int value) {
        bt.putInt(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(long offset) {
        return bt.getUnsignedInt(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedInt(long offset, long value) {
        bt.putUnsignedInt(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(long offset) {
        return bt.getLong(mem, (int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(long offset, long value) {
        bt.putLong(mem, (int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(long offset, OffHeapMemory destination, long destOffset, long bytes) {
        OnHeapMemory dest = (OnHeapMemory) destination;
        bt.copy(mem, (int) offset, dest.mem, (int) destOffset, (int) bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OffHeapMemory clone() {
        OnHeapMemory res = new OnHeapMemory(bt, mem.length);
        this.copy(0, res, 0, mem.length);
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OnHeapMemory");
        sb.append("{bt=").append(bt);
        sb.append(", length=").append(!disposed.get() ? mem.length : -1);
        sb.append(", disposed=").append(disposed);
        sb.append('}');
        return sb.toString();
    }
}
