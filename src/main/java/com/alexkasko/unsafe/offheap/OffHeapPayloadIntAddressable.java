package com.alexkasko.unsafe.offheap;

/**
 * Interface for off-heap header-int_payload collection.
 *
 * @author alexkasko
 * Date: 4/24/13
 */
public interface OffHeapPayloadIntAddressable extends OffHeapAddressable {
    /**
     * Returns payload for the given index
     *
     * @param index collection index to load payload from
     */
    int getPayload(long index);

    /**
     * Sets header and payload values on specified index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    void set(long index, long header, int payload);
}
