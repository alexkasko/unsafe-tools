package com.alexkasko.unsafe.offheaplong;

import com.alexkasko.unsafe.offheap.OffHeapAddressable;

/**
 * Interface for off-heap collections with long elements.
 *
 * @author alexkasko
 * Date: 3/3/13
 */
public interface OffHeapLongAddressable extends OffHeapAddressable {

    /**
     * Sets the element at position {@code index} to the given value
     *
     * @param index collection index
     * @param value long value
     */
    void set(long index, long value);
}
