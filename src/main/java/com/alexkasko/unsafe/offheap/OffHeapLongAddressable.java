package com.alexkasko.unsafe.offheap;

/**
 * User: alexkasko
 * Date: 3/3/13
 */
public interface OffHeapLongAddressable extends OffHeapAddressable {

    void set(long index, long value);
}
