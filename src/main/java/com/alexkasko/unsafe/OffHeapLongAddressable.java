package com.alexkasko.unsafe;

/**
 * User: alexkasko
 * Date: 3/3/13
 */
public interface OffHeapLongAddressable {

    long get(long index);

    void set(long index, long value);

    long size();
}
