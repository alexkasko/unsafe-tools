package com.alexkasko.unsafe;

/**
 * User: alexkasko
 * Date: 3/1/13
 */
public class OffHeapLongArrayList extends OffHeapLongArray {

    private static final int MIN_CAPACITY_INCREMENT = 12;
    private long size;

    public OffHeapLongArrayList() {
        this(MIN_CAPACITY_INCREMENT);
    }

    public OffHeapLongArrayList(long capacity) {
        super(capacity);
    }

    public void add(long value) {
        OffHeapMemory oh = ohm;
        long s = size;
        if (s == super.size()) {
            long len = s + (s < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : s >> 1);
            OffHeapMemory newOhm = OffHeapMemory.allocateMemory(len * ELEMENT_LENGTH);
            oh.copy(0, newOhm, 0, ohm.length());
            oh.free();
            ohm = newOhm;
        }
        super.set(s, value);
        size = s + 1;
    }

    @Override
    public long size() {
        return size;
    }

    public long capacity() {
        return super.size();
    }
}
