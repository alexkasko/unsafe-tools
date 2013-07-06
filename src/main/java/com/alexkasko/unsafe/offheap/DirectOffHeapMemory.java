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

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * Implementation of {@link OffHeapMemory} using {@link ByteBuffer#allocateDirect(int)}
 *
 * @author alexkasko
 * Date: 1/14/13
*/
class DirectOffHeapMemory extends OffHeapMemory {

    private final ByteBuffer bb;
    private final long length;
    private Object cleaner;
    private Method clean;
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    DirectOffHeapMemory(long bytes) {
        this.length = bytes;
        this.bb = ByteBuffer.allocateDirect((int) bytes).order(LITTLE_ENDIAN);
        // http://stackoverflow.com/a/8191493/314015
        try {
            setupOpenJdkCleaner();
        } catch (Exception e) {
            try {
                setupAndroidCleaner();
            } catch (Exception e1) {
                e.printStackTrace();
                e1.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private void setupOpenJdkCleaner() throws Exception {
        Method cleanerMethod = bb.getClass().getMethod("cleaner");
        cleanerMethod.setAccessible(true);
        this.cleaner = cleanerMethod.invoke(bb);
        this.clean = cleaner.getClass().getMethod("clean");
        this.clean.setAccessible(true);
    }

    private void setupAndroidCleaner() throws Exception {
        this.clean = bb.getClass().getMethod("free");
        this.clean.setAccessible(true);
        this.cleaner = bb;
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
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        if(!disposed.compareAndSet(false, true)) return;
        try {
            clean.invoke(cleaner);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(long offset, byte[] buffer, int bufferOffset, int bytes) {
        bb.clear().position((int) offset);
        bb.put(buffer, bufferOffset, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(long offset, byte[] buffer) {
        bb.clear().position((int) offset);
        bb.put(buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(long offset, byte[] buffer, int bufferOffset, int bytes) {
        bb.clear().position((int) offset);
        bb.get(buffer, bufferOffset, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void get(long offset, byte[] buffer) {
        bb.clear().position((int) offset);
        bb.get(buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(long offset) {
        return bb.get((int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putByte(long offset, byte value) {
        bb.put((int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getUnsignedByte(long offset) {
        return (short) (bb.get((int) offset) & 0xff);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedByte(long offset, short value) {
        bb.put((int) offset, (byte) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(long offset) {
        return bb.getShort((int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putShort(long offset, short value) {
        bb.putShort((int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnsignedShort(long offset) {
        return bb.getShort((int) offset) & 0xffff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedShort(long offset, int value) {
        bb.putShort((int) offset, (short) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(long offset) {
        return bb.getInt((int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putInt(long offset, int value) {
        bb.putInt((int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUnsignedInt(long offset) {
        return bb.getInt((int) offset) & 0xffffffffL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putUnsignedInt(long offset, long value) {
        bb.putInt((int) offset, (int) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(long offset) {
        return bb.getLong((int) offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putLong(long offset, long value) {
        bb.putLong((int) offset, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(long offset, OffHeapMemory destination, long destOffset, long bytes) {
        DirectOffHeapMemory dest = (DirectOffHeapMemory) destination;
        bb.clear().position((int) offset);
        dest.bb.clear().position((int) destOffset);
        dest.bb.put(bb);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DirectOffHeapMemory");
        sb.append("{bb=").append(bb);
        sb.append(", length=").append(length);
        sb.append(", cleaner=").append(cleaner);
        sb.append(", clean=").append(clean);
        sb.append(", disposed=").append(disposed);
        sb.append('}');
        return sb.toString();
    }
}
