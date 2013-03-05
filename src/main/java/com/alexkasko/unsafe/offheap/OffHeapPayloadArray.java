package com.alexkasko.unsafe.offheap;

/**
 * User: alexkasko
 * Date: 3/3/13
 */
public class OffHeapPayloadArray implements OffHeapPayloadAddressable {
    private static final int HEADER_LENGTH = 8;

    private final OffHeapMemory ohm;
    private final int payloadLength;
    private final int elementLength;

    public OffHeapPayloadArray(long size, int payloadLength) {
        if(payloadLength <= 0) throw new IllegalArgumentException("Illegal payloadLength: [" + payloadLength + "]");
        this.payloadLength = payloadLength;
        this.elementLength = HEADER_LENGTH + payloadLength;
        this.ohm = OffHeapMemory.allocateMemory(size * elementLength);
    }

    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    @Override
    public int payloadLength() {
        return payloadLength;
    }

    @Override
    public long get(long index) {
        return ohm.getLong(index * elementLength);
    }

    @Override
    public void getPayload(long index, byte[] buffer) {
        ohm.get(index * elementLength + HEADER_LENGTH, buffer);
    }

    public void getPayload(long index, byte[] buffer, int bufferPos) {
        ohm.get(index * elementLength + HEADER_LENGTH, buffer, bufferPos, payloadLength);
    }

    @Override
    public void set(long index, long header, byte[] payload) {
        long addr = index * elementLength;
        ohm.putLong(addr, header);
        ohm.put(addr + HEADER_LENGTH, payload);
    }

    public void set(long index, long header, byte[] payload, int payloadPos) {
        long addr = index * elementLength;
        ohm.putLong(addr, header);
        ohm.put(addr + HEADER_LENGTH, payload, payloadPos, payloadLength);
    }

    @Override
    public long size() {
        return ohm.length() / elementLength;
    }
}
