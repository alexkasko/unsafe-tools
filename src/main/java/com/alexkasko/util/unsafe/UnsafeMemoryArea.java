package com.alexkasko.util.unsafe;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: alexkasko
 * Date: 1/14/13
 */
class UnsafeMemoryArea extends MemoryArea {

    private static final TheUnsafeAccessor UNSAFE = TheUnsafeAccessor.get();
    private static final int BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    private final long address;
    private final long length;
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    // todo: test boundaries
    UnsafeMemoryArea(long bytes) {
        this.address = UNSAFE.allocateMemory(bytes);
        this.length = bytes;
    }

    @Override
    public boolean isUnsafe() {
        return true;
    }

    @Override
    public void free() {
        if(!disposed.compareAndSet(false, true)) return;
        UNSAFE.freeMemory(address);
    }

    @Override
    protected void finalize() throws Throwable {
        free();
    }

    @Override
    public void write(long offset, byte[] buffer, int bufferOffset, int bytes) {
        assert offset >= 0;
        assert offset < length;
        assert bufferOffset >= 0;
        assert bytes > 0;
        assert bytes <= length;
        assert bufferOffset <= buffer.length - bytes;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + bufferOffset, null, address + offset, bytes);
    }

    @Override
    public void write(long offset, byte[] buffer) {
        assert offset >= 0;
        assert offset < length;
        assert null != buffer;
        UNSAFE.copyMemory(buffer, 0, null, address + offset, buffer.length);
    }

    @Override
    public void read(long offset, byte[] buffer, int bufferOffset, int bytes) {
        assert offset >= 0;
        assert offset < length;
        assert bufferOffset >= 0;
        assert bytes > 0;
        assert bytes <= length;
        assert bufferOffset <= buffer.length - bytes;
        UNSAFE.copyMemory(null, address + offset, buffer, BYTE_ARRAY_OFFSET + bufferOffset, bytes);
    }

    @Override
    public void read(long offset, byte[] buffer) {
        assert offset >= 0;
        assert offset < length;
        assert null != buffer;
        UNSAFE.copyMemory(null, address + offset, buffer, 0, buffer.length);
    }

    @Override
    public byte readByte(long offset) {
        assert offset >= 0;
        assert offset <= length - 1;
        return UNSAFE.getByte(address + offset);
    }

    @Override
    public void writeByte(long offset, byte value) {
        assert offset >= 0;
        assert offset <= length - 1;
        UNSAFE.putByte(address + offset, value);
    }

    @Override
    public short readUnsignedByte(long offset) {
        assert offset >= 0;
        assert offset <= length - 1;
        return (short) (UNSAFE.getByte(address + offset) & 0xff);
    }

    @Override
    public void writeUnsignedByte(long offset, short value) {
        assert offset >= 0;
        assert offset <= length - 1;
        UNSAFE.putByte(address + offset, (byte) value);
    }

    @Override
    public short readShort(long offset) {
        assert offset >= 0;
        assert offset <= length - 2;
        return UNSAFE.getShort(address + offset);
    }

    @Override
    public void writeShort(long offset, short value) {
        assert offset >= 0;
        assert offset <= length - 2;
        UNSAFE.putShort(address + offset, value);
    }

    @Override
    public int readUnsignedShort(long offset) {
        assert offset >= 0;
        assert offset <= length - 2;
        return UNSAFE.getShort(address + offset) & 0xffff;
    }

    @Override
    public void writeUnsignedShort(long offset, int value) {
        assert offset >= 0;
        assert offset <= length - 2;
        UNSAFE.putShort(address + offset, (short) value);
    }

    @Override
    public int readInt(long offset) {
        assert offset >= 0;
        assert offset <= length - 4;
        return UNSAFE.getInt(address + offset);
    }

    @Override
    public void writeInt(long offset, int value) {
        assert offset >= 0;
        assert offset <= length - 4;
        UNSAFE.putInt(address + offset, value);
    }

    @Override
    public long readUnsignedInt(long offset) {
        assert offset >= 0;
        assert offset <= length - 4;
        return UNSAFE.getInt(address + offset) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(long offset, long value) {
        assert offset >= 0;
        assert offset <= length - 4;
        UNSAFE.putInt(address + offset, (int) value);
    }

    @Override
    public long readLong(long offset) {
        assert offset >= 0;
        assert offset <= length - 8;
        return UNSAFE.getLong(address + offset);
    }

    @Override
    public void writeLong(long offset, long value) {
        assert offset >= 0;
        assert offset <= length - 8;
        UNSAFE.putLong(address + offset, value);
    }
}