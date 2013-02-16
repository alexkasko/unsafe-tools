package com.alexkasko.util.unsafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
* User: alexkasko
* Date: 1/14/13
*/

class DirectMemoryAccessor extends MemoryAccessor {

    private final AtomicReferenceArray<ByteBuffer> buffers;
    private final AtomicInteger count = new AtomicInteger(0);

    DirectMemoryAccessor(int maxAllocated) {
        this.buffers = new AtomicReferenceArray<ByteBuffer>(maxAllocated);
    }

    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public int getAllocationsCount() {
        return count.get();
    }

    @Override
    public int alloc(long bytes) {
        if(bytes <= 0) throw new IllegalArgumentException("Invalid bytes length provided: [" + bytes + "]");
        ByteBuffer bb = ByteBuffer.allocateDirect((int) bytes);
        for (int i = 0; i < buffers.length(); i++) {
            boolean saved = buffers.compareAndSet(i, null, bb);
            if (saved) {
                count.incrementAndGet();
                return i;
            }
        }
        throw new IllegalStateException("Allocated blocks threshold: [" + buffers.length() + "] exceeded");
    }

    @Override
    public void free(int id) {
        ByteBuffer bb = buffers.get(id);
        if (null == bb) throw new IllegalArgumentException("No buffer found for id: [" + id + "]");
        if (!bb.isDirect()) throw new IllegalStateException("Stored byte buffer is not direct");
        try { // http://stackoverflow.com/a/8191493/314015
            Method cleanerMethod = bb.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(bb);
            Method cleanMethod = cleaner.getClass().getMethod("clean");
            cleanMethod.setAccessible(true);
            cleanMethod.invoke(cleaner);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        buffers.set(id, null);
        count.decrementAndGet();
    }

    @Override
    public void write(int id, long offset, byte[] buffer, int bufferOffset, int bytes) {
        ByteBuffer bb = resolveBuffer(id, offset);
        bb.put(buffer, bufferOffset, bytes);
    }

    @Override
    public void write(int id, long offset, byte[] buffer) {
        ByteBuffer bb = resolveBuffer(id, offset);
        bb.put(buffer);
    }

    @Override
    public void read(int id, long offset, byte[] buffer, int bufferOffset, int bytes) {
        ByteBuffer bb = resolveBuffer(id, offset);
        bb.get(buffer, bufferOffset, bytes);
    }

    @Override
    public void read(int id, long offset, byte[] buffer) {
        ByteBuffer bb = resolveBuffer(id, offset);
        bb.get(buffer);
    }

    @Override
    public byte readByte(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return bb.get((int) offset);
    }

    @Override
    public void writeByte(int id, long offset, byte value) {
        ByteBuffer bb = buffers.get(id);
        bb.put((int) offset, value);
    }

    @Override
    public short readUnsignedByte(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return (short) (bb.get((int) offset) & 0xff);
    }

    @Override
    public void writeUnsignedByte(int id, long offset, short value) {
        ByteBuffer bb = buffers.get(id);
        bb.put((int) offset, (byte) value);
    }

    @Override
    public short readShort(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return bb.getShort((int) offset);
    }

    @Override
    public void writeShort(int id, long offset, short value) {
        ByteBuffer bb = buffers.get(id);
        bb.putShort((int) offset, value);
    }

    @Override
    public int readUnsignedShort(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return bb.getShort((int) offset) & 0xffff;
    }

    @Override
    public void writeUnsignedShort(int id, long offset, int value) {
        ByteBuffer bb = buffers.get(id);
        bb.putShort((int) offset, (short) value);
    }

    @Override
    public int readInt(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return bb.getInt((int) offset);
    }

    @Override
    public void writeInt(int id, long offset, int value) {
        ByteBuffer bb = buffers.get(id);
        bb.putInt((int) offset, value);
    }

    @Override
    public long readUnsignedInt(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return bb.getInt((int) offset) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(int id, long offset, long value) {
        ByteBuffer bb = buffers.get(id);
        bb.putInt((int) offset, (int) value);
    }

    @Override
    public long readLong(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        return bb.getLong((int) offset);
    }

    @Override
    public void writeLong(int id, long offset, long value) {
        ByteBuffer bb = buffers.get(id);
        bb.putLong((int) offset);
    }

    private ByteBuffer resolveBuffer(int id, long offset) {
        ByteBuffer bb = buffers.get(id);
        bb.clear();
        bb.position((int) offset);
        return bb;
    }
}
