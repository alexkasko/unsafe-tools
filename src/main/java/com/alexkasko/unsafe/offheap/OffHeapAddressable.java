package com.alexkasko.unsafe.offheap;

/**
 * User: alexkasko
 * Date: 3/5/13
 */
public interface OffHeapAddressable {
    long get(long index);

    long size();
}
