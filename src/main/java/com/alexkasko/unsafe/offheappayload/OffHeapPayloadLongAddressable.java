package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapAddressable;

/**
 * Interface for off-heap header-long_payload collection.
 *
 * @author alexkasko
 * Date: 4/24/13
 */
public interface OffHeapPayloadLongAddressable extends OffHeapAddressable {
    /**
     * Returns payload for the given index
     *
     * @param index collection index to load payload from
     */
    long getPayload(long index);

    /**
     * Sets header and payload values on specified index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    void set(long index, long header, long payload);
}
