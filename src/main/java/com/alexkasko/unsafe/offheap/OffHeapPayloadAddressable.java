package com.alexkasko.unsafe.offheap;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public interface OffHeapPayloadAddressable extends OffHeapAddressable {

    int payloadLength();

    void getPayload(long index, byte[] buffer);

    void set(long index, long header, byte[] payload);
}
