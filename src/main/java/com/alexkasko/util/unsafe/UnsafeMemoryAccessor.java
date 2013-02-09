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

    private final int blockSize;
    private final AtomicLongArray pointers;
    private final AtomicInteger count = new AtomicInteger();

    UnsafeMemoryAccessor(int blockSize, int maxAllocated) {
        this.blockSize = blockSize;
        this.pointers = new AtomicLongArray(maxAllocated);
    }

    @Override
    public boolean isUnsafe() {
        return true;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    @Override
    public int getCount() {
        return count.get();
    }

    @Override
    public int alloc() {
        long po = UNSAFE.allocateMemory(blockSize);
        for (int i = 0; i < pointers.length(); i++) {
            boolean saved = pointers.compareAndSet(i, 0, po);
            if (saved) {
                count.incrementAndGet();
                return i;
            }
        }
        throw new IllegalStateException("Allocated blocks threshold: [" + pointers.length() + "] exceeded");
    }

    @Override
    public void write(int id, int targetLeftOffset, int targetRightOffset, byte[] buffer, int bufferOffset) {
        long po = pointers.get(id);
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + bufferOffset, null, po + targetLeftOffset, blockSize - targetLeftOffset - targetRightOffset);
    }

    @Override
    public void read(int id, int targetLeftOffset, int targetRightOffset, byte[] buffer, int bufferOffset) {
        long po = pointers.get(id);
        UNSAFE.copyMemory(null, po + targetLeftOffset, buffer, BYTE_ARRAY_OFFSET + bufferOffset, blockSize - targetLeftOffset - targetRightOffset);
    }

    @Override
    public void free(int id) {
        long po = pointers.get(id);
        UNSAFE.freeMemory(po);
        count.decrementAndGet();
    }
}
