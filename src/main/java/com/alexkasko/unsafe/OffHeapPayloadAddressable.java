package com.alexkasko.unsafe;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public interface OffHeapPayloadAddressable {

    int getPayloadLength();

    long getHeader(long index);

    void getPayload(long index, byte[] buffer);

    void set(long index, long header, byte[] payload);

    long size();
}
