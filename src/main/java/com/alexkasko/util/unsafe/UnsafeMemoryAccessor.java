package com.alexkasko.util.unsafe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * User: alexkasko
 * Date: 1/14/13
 */
// todo: get checks
class UnsafeMemoryAccessor extends MemoryAccessor {

    private static final TheUnsafeAccessor UNSAFE = TheUnsafeAccessor.get();
    private static final int BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    private final AtomicLongArray pointers;
    private final AtomicInteger count = new AtomicInteger();

    UnsafeMemoryAccessor(int maxAllocated) {
        this.pointers = new AtomicLongArray(maxAllocated * 2);
    }

    @Override
    public boolean isUnsafe() {
        return true;
    }

    @Override
    public int getAllocationsCount() {
        return count.get();
    }

    @Override
    public int alloc(long bytes) {
        if (bytes <= 0) throw new IllegalArgumentException("Invalid bytes length provided: [" + bytes + "]");
        long po = UNSAFE.allocateMemory(bytes);
        for (int i = 0; i < pointers.length(); i += 2) {
            boolean saved = pointers.compareAndSet(i, 0, po);
            if (saved) {
                boolean lenSaved = pointers.compareAndSet(i + 1, 0, bytes);
                if (!lenSaved) throw new IllegalStateException("Pointers list is corrupted on id: [" + i + "]");
                count.incrementAndGet();
                return i;
            }
        }
        throw new IllegalStateException("Allocated blocks threshold: [" + pointers.length() + "] exceeded");
    }

    @Override
    public void free(int id) {
        if (0 != (id & 1)) throw new IllegalArgumentException("Invalid odd id: [" + id + "]");
        long len = pointers.getAndSet(id + 1, 0);
        // was already called
        if (0 == len) return;
        long po = pointers.get(id);
        if (0 == po) throw new IllegalArgumentException("Pointers list is corrupted on id: [" + id + "], " +
                "address: [" + po + "], length: [" + len + "]");
        UNSAFE.freeMemory(po);
        pointers.set(id, 0);
        count.decrementAndGet();
    }

    @Override
    public void freeQuetly(int id) {
        if (0 == id) return;
        try {
            free(id);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        for (int i = 0; i < pointers.length(); i += 2) {
            freeQuetly(i);
        }
    }

    @Override
    public void write(int id, long offset, byte[] buffer, int bufferOffset, int bytes) {
        assert 0 == (id & 1);
        long po = pointers.get(id);
        assert 0 != po;
        long len = 0;
        assert 0 != (len = pointers.get(id + 1));
        assert offset >= 0;
        assert offset < len;
        assert bufferOffset >= 0;
        assert bytes > 0;
        assert bytes <= len;
        assert bufferOffset <= buffer.length - bytes;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + bufferOffset, null, po + offset, bytes);
    }

    @Override
    public void write(int id, long offset, byte[] buffer) {
        assert 0 == (id & 1);
        long po = pointers.get(id);
        assert 0 != po;
        long len = 0;
        assert 0 != (len = pointers.get(id + 1));
        assert offset >= 0;
        assert offset < len;
        assert null != buffer;
        UNSAFE.copyMemory(buffer, 0, null, po + offset, buffer.length);
    }

    @Override
    public void read(int id, long offset, byte[] buffer, int bufferOffset, int bytes) {
        assert 0 == (id & 1);
        long po = pointers.get(id);
        assert 0 != po;
        long len = 0;
        assert 0 != (len = pointers.get(id + 1));
        assert offset >= 0;
        assert offset < len;
        assert bufferOffset >= 0;
        assert bytes > 0;
        assert bytes <= len;
        assert bufferOffset <= buffer.length - bytes;
        UNSAFE.copyMemory(null, po + offset, buffer, BYTE_ARRAY_OFFSET + bufferOffset, bytes);
    }

    @Override
    public void read(int id, long offset, byte[] buffer) {
        assert 0 == (id & 1);
        long po = pointers.get(id);
        assert 0 != po;
        long len = 0;
        assert 0 != (len = pointers.get(id + 1));
        assert offset >= 0;
        assert offset < len;
        assert null != buffer;
        UNSAFE.copyMemory(null, po + offset, buffer, 0, buffer.length);
    }

    @Override
    public byte readByte(int id, long offset) {
        long address = resolveAddress(id, offset, 1);
        return UNSAFE.getByte(address);
    }

    @Override
    public void writeByte(int id, long offset, byte value) {
        long address = resolveAddress(id, offset, 1);
        UNSAFE.putByte(address, value);
    }

    @Override
    public short readUnsignedByte(int id, long offset) {
        long address = resolveAddress(id, offset, 1);
        return (short) (UNSAFE.getByte(address) & 0xff);
    }

    @Override
    public void writeUnsignedByte(int id, long offset, short value) {
        long address = resolveAddress(id, offset, 1);
        UNSAFE.putByte(address, (byte) value);
    }

    @Override
    public short readShort(int id, long offset) {
        long address = resolveAddress(id, offset, 2);
        return UNSAFE.getShort(address);
    }

    @Override
    public void writeShort(int id, long offset, short value) {
        long address = resolveAddress(id, offset, 2);
        UNSAFE.putShort(address, value);
    }

    @Override
    public int readUnsignedShort(int id, long offset) {
        long address = resolveAddress(id, offset, 2);
        return UNSAFE.getShort(address) & 0xffff;
    }

    @Override
    public void writeUnsignedShort(int id, long offset, int value) {
        long address = resolveAddress(id, offset, 2);
        UNSAFE.putShort(address, (short) value);
    }

    @Override
    public int readInt(int id, long offset) {
        long address = resolveAddress(id, offset, 4);
        return UNSAFE.getInt(address);
    }

    @Override
    public void writeInt(int id, long offset, int value) {
        long address = resolveAddress(id, offset, 4);
        UNSAFE.putInt(address, value);
    }

    @Override
    public long readUnsignedInt(int id, long offset) {
        long address = resolveAddress(id, offset, 4);
        return UNSAFE.getInt(address) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(int id, long offset, long value) {
        long address = resolveAddress(id, offset, 4);
        UNSAFE.putInt(address, (int) value);
    }

    @Override
    public long readLong(int id, long offset) {
        long address = resolveAddress(id, offset, 8);
        return UNSAFE.getLong(address);
    }

    @Override
    public void writeLong(int id, long offset, long value) {
        long address = resolveAddress(id, offset, 8);
        UNSAFE.putLong(address, value);
    }

    private long resolveAddress(int id, long offset, int elementSize) {
        assert 0 == (id & 1);
        long po = pointers.get(id);
        assert 0 != po;
        long len = 0;
        assert 0 != (len = pointers.get(id + 1));
        assert offset >= 0;
        assert offset <= len - elementSize;
        return po + offset;
    }
}