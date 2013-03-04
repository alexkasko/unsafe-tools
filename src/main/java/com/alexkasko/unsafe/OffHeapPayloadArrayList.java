package com.alexkasko.unsafe;

/**
 * User: alexkasko
 * Date: 3/4/13
 */
public class OffHeapPayloadArrayList implements OffHeapPayloadAddressable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final int HEADER_LENGTH = 8;

    private final int payloadLength;
    private final int elementLength;
    private OffHeapMemory ohm;
    private long size;

    public OffHeapPayloadArrayList(int payloadLength) {
        this(MIN_CAPACITY_INCREMENT, payloadLength);
    }

    public OffHeapPayloadArrayList(long capacity, int payloadLength) {
        if(payloadLength <= 0) throw new IllegalArgumentException("Illegal payloadLength: [" + payloadLength + "]");
        this.payloadLength = payloadLength;
        this.elementLength = HEADER_LENGTH + payloadLength;
        this.ohm = OffHeapMemory.allocateMemory(capacity * elementLength);
    }

    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    public void add(long header, byte[] payload) {
        OffHeapMemory oh = ohm;
        long s = size;
        if (s == capacity()) {
            long len = s + (s < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : s >> 1);
            OffHeapMemory newOhm = OffHeapMemory.allocateMemory(len * elementLength);
            oh.copy(0, newOhm, 0, ohm.length());
            oh.free();
            ohm = newOhm;
        }
        set(s, header, payload);
        size = s + 1;
    }

    @Override
    public int getPayloadLength() {
        return payloadLength;
    }

    @Override
    public long getHeader(long index) {
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
        return size;
    }

    public long capacity() {
        return ohm.length() / elementLength;
    }
}
