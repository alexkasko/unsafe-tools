package com.alexkasko.util.unsafe;

import static com.alexkasko.util.unsafe.MemoryArea.allocMemoryArea;

/**
 * User: alexkasko
 * Date: 2/22/13
 */
public class MemoryLongArray {
    private final MemoryArea ma;

    public boolean isUnsafe() {
        return ma.isUnsafe();
    }

    public MemoryLongArray(long length) {
        this.ma = allocMemoryArea(length * 8);
    }

    public long get(long index) {
        return ma.readLong(index * 8);
    }

    public void put(long index, long value) {
        ma.writeLong(index * 8, value);
    }

    public long length() {
        return ma.length() / 8;
    }

    public void free() {
        ma.free();
    }
}
