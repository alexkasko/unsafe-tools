package com.alexkasko.unsafe.offheap;

/**
 * Interface for off-heap header-payload collection.
 *
 * @author alexkasko
 * Date: 3/4/13
 */
public interface OffHeapPayloadAddressable extends OffHeapAddressable {

    /**
     * Returns length of the each payload block in bytes
     *
     * @return length of the each payload block in bytes
     */
    int payloadLength();

    /**
     * Loads payload into provided buffer
     *
     * @param index collection index to load payload from
     * @param buffer buffer to load payload into
     */
    void getPayload(long index, byte[] buffer);

    /**
     * Sets header value and copies payload data on provided index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    void set(long index, long header, byte[] payload);
}
