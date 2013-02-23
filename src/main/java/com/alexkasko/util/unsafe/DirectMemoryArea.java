package com.alexkasko.util.unsafe;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
* User: alexkasko
* Date: 1/14/13
*/

class DirectMemoryArea extends MemoryArea {

    private final ByteBuffer bb;
    private final long length;
    private Object cleaner;
    private Method clean;
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    DirectMemoryArea(long bytes) {
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


    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public void free() {
        if(!disposed.compareAndSet(false, true)) return;
        try {
            clean.invoke(cleaner);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(long offset, byte[] buffer, int bufferOffset, int bytes) {
        bb.clear().position((int) offset);
        bb.put(buffer, bufferOffset, bytes);
    }

    @Override
    public void write(long offset, byte[] buffer) {
        bb.clear().position((int) offset);
        bb.put(buffer);
    }

    @Override
    public void read(long offset, byte[] buffer, int bufferOffset, int bytes) {
        bb.clear().position((int) offset);
        bb.get(buffer, bufferOffset, bytes);
    }

    @Override
    public void read(long offset, byte[] buffer) {
        bb.clear().position((int) offset);
        bb.get(buffer);
    }

    @Override
    public byte readByte(long offset) {
        return bb.get((int) offset);
    }

    @Override
    public void writeByte(long offset, byte value) {
        bb.put((int) offset, value);
    }

    @Override
    public short readUnsignedByte(long offset) {
        return (short) (bb.get((int) offset) & 0xff);
    }

    @Override
    public void writeUnsignedByte(long offset, short value) {
        bb.put((int) offset, (byte) value);
    }

    @Override
    public short readShort(long offset) {
        return bb.getShort((int) offset);
    }

    @Override
    public void writeShort(long offset, short value) {
        bb.putShort((int) offset, value);
    }

    @Override
    public int readUnsignedShort(long offset) {
        return bb.getShort((int) offset) & 0xffff;
    }

    @Override
    public void writeUnsignedShort(long offset, int value) {
        bb.putShort((int) offset, (short) value);
    }

    @Override
    public int readInt(long offset) {
        return bb.getInt((int) offset);
    }

    @Override
    public void writeInt(long offset, int value) {
        bb.putInt((int) offset, value);
    }

    @Override
    public long readUnsignedInt(long offset) {
        return bb.getInt((int) offset) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(long offset, long value) {
        bb.putInt((int) offset, (int) value);
    }

    @Override
    public long readLong(long offset) {
        return bb.getLong((int) offset);
    }

    @Override
    public void writeLong(long offset, long value) {
        bb.putLong((int) offset, value);
    }
}
