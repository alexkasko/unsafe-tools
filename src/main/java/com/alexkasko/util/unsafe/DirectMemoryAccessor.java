package com.alexkasko.util.unsafe;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
* User: alexkasko
* Date: 1/14/13
*/

// todo: get checks
class DirectMemoryAccessor extends MemoryAccessor {

    private final int blockSize;
    private final AtomicReferenceArray<ByteBuffer> buffers;
    private final AtomicInteger count = new AtomicInteger(0);

    DirectMemoryAccessor(int blockSize, int maxAllocated) {
        this.blockSize = blockSize;
        this.buffers = new AtomicReferenceArray<ByteBuffer>(maxAllocated);
    }

    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public boolean isDirect() {
        return true;
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
        ByteBuffer bb = ByteBuffer.allocateDirect(blockSize);
        for(int i=0; i< buffers.length(); i++) {
            boolean saved = buffers.compareAndSet(i, null, bb);
            if(saved) {
                count.incrementAndGet();
                return i;
            }
        }
        throw new IllegalStateException("Allocated blocks threshold: [" + buffers.length() + "] exceeded");
    }

    @Override
    public void write(int id, int targetLeftOffset, int targetRightOffset, byte[] buffer, int bufferOffset) {
        ByteBuffer bb = buffers.get(id);
        bb.clear();
        bb.position(targetLeftOffset);
        bb.put(buffer, bufferOffset, blockSize - targetRightOffset);
    }

    @Override
    public void read(int id, int targetLeftOffset, int targetRightOffset, byte[] buffer, int bufferOffset) {
        ByteBuffer bb = buffers.get(id);
        bb.clear();
        bb.position(targetLeftOffset);
        bb.get(buffer, bufferOffset, blockSize - targetRightOffset);
    }

    @Override
    public void free(int id) {
        buffers.set(id, null);
        count.decrementAndGet();
    }
}
