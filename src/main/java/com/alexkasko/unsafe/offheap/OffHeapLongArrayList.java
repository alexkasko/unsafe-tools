package com.alexkasko.unsafe.offheap;

/**
 * User: alexkasko
 * Date: 3/1/13
 */
public class OffHeapLongArrayList implements OffHeapLongAddressable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final int ELEMENT_LENGTH = 8;

    private OffHeapMemory ohm;
    private long size;

    public OffHeapLongArrayList() {
        this(MIN_CAPACITY_INCREMENT);
    }

    public OffHeapLongArrayList(long capacity) {
        this.ohm = OffHeapMemory.allocateMemory(capacity * ELEMENT_LENGTH);
    }

    public void add(long value) {
        OffHeapMemory oh = ohm;
        long s = size;
        if (s == capacity()) {
            long len = s + (s < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : s >> 1);
            OffHeapMemory newOhm = OffHeapMemory.allocateMemory(len * ELEMENT_LENGTH);
            oh.copy(0, newOhm, 0, ohm.length());
            oh.free();
            ohm = newOhm;
        }
        set(s, value);
        size = s + 1;
    }

    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    @Override
    public long get(long index) {
        return ohm.getLong(index * ELEMENT_LENGTH);
    }

    @Override
    public void set(long index, long value) {
        ohm.putLong(index * ELEMENT_LENGTH, value);
    }

    @Override
    public long size() {
        return size;
    }

    public long capacity() {
        return ohm.length() / ELEMENT_LENGTH;
    }
}
