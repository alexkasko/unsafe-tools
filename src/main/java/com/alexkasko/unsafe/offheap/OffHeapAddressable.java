package com.alexkasko.unsafe.offheap;

/**
 * Basic interface for off-heap long-indexed collections. Required for {@link com.alexkasko.unsafe.offheaplong.OffHeapLongBinarySearch}.
 *
 * @author alexkasko
 * Date: 3/5/13
 */
public interface OffHeapAddressable {
    /**
     * Gets the element at position {@code index}
     *
     * @param index collection index
     * @return long value
     */
    long get(long index);

    /**
     * Returns number of elements in collection
     *
     * @return number of elements in collection
     */
    long size();
}
