package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapDisposable;
import com.alexkasko.unsafe.offheap.OffHeapMemory;

/**
 * Implementation of off-heap header-payload array. Memory block for each index
 * contains {@code long} header anb {@code byte[]} payload.
 *
 * Default implementation uses {@code sun.misc.Unsafe}, with all operations guarded with {@code assert} keyword.
 * With assertions enabled in runtime ({@code -ea} java switch) {@link AssertionError}
 * will be thrown on illegal index access. Without assertions illegal index will crash JVM.
 *
 * Array won't be zeroed after creation (will contain garbage by default).
 * Allocated memory may be freed manually using {@link #free()} (thread-safe
 * and may be called multiple times) or it will be freed after {@link com.alexkasko.unsafe.offheaplong.OffHeapLongArray}
 * will be garbage collected.
 *
 * @author alexkasko
 * Date: 3/3/13
 */
public class OffHeapPayloadArray implements OffHeapPayloadAddressable, OffHeapDisposable {
    private static final int HEADER_LENGTH = 8;

    private final OffHeapMemory ohm;
    private final int payloadLength;
    private final int elementLength;

    /**
     * Constructor
     *
     * @param size array size
     * @param payloadLength length of payload data block in bytes
     */
    public OffHeapPayloadArray(long size, int payloadLength) {
        if(payloadLength <= 0) throw new IllegalArgumentException("Illegal payloadLength: [" + payloadLength + "]");
        this.payloadLength = payloadLength;
        this.elementLength = HEADER_LENGTH + payloadLength;
        this.ohm = OffHeapMemory.allocateMemory(size * elementLength);
    }

    /**
     * Whether unsafe implementation of {@link OffHeapMemory} is used
     *
     * @return whether unsafe implementation of {@link OffHeapMemory} is used
     */
    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    /**
     * Returns length of the each payload block in bytes
     *
     * @return length of the each payload block in bytes
     */
    @Override
    public int payloadLength() {
        return payloadLength;
    }

    /**
     * Gets the header at position {@code index}
     *
     * @param index collection index
     * @return long value
     */
    @Override
    public long get(long index) {
        return ohm.getLong(index * elementLength);
    }

    /**
     * Loads payload into provided buffer
     *
     * @param index collection index to load payload from
     * @param buffer buffer to load payload into
     */
    @Override
    public void getPayload(long index, byte[] buffer) {
        ohm.get(index * elementLength + HEADER_LENGTH, buffer);
    }

    /**
     * Loads payload into provided buffer
     *
     * @param index collection index to load payload from
     * @param buffer buffer to load payload into
     * @param bufferPos buffer offset
     */
    public void getPayload(long index, byte[] buffer, int bufferPos) {
        ohm.get(index * elementLength + HEADER_LENGTH, buffer, bufferPos, payloadLength);
    }

    /**
     * Sets header value and copies payload data on provided index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    @Override
    public void set(long index, long header, byte[] payload) {
        long addr = index * elementLength;
        ohm.putLong(addr, header);
        ohm.put(addr + HEADER_LENGTH, payload);
    }

    /**
     * Sets header value and copies payload data on provided index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     * @param payloadPos payload offset
     */
    public void set(long index, long header, byte[] payload, int payloadPos) {
        long addr = index * elementLength;
        ohm.putLong(addr, header);
        ohm.put(addr + HEADER_LENGTH, payload, payloadPos, payloadLength);
    }

    /**
     * Copies payload data on provided index
     *
     * @param index collection index to set payload
     * @param payload payload value
     */
    public void setPayload(long index, byte[] payload) {
        long addr = index * elementLength;
        ohm.put(addr + HEADER_LENGTH, payload);
    }

    /**
     * Copies payload data on provided index
     *
     * @param index collection index to set payload
     * @param payload payload value
     * @param payloadPos payload offset
     */
    public void setPayload(long index, byte[] payload, int payloadPos) {
        long addr = index * elementLength;
        ohm.put(addr + HEADER_LENGTH, payload, payloadPos, payloadLength);
    }

    /**
     * Returns number of elements in array
     *
     * @return number of elements in array
     */
    @Override
    public long size() {
        return ohm.length() / elementLength;
    }

    /**
     * Frees allocated memory, may be called multiple times from any thread
     */
    @Override
    public void free() {
        ohm.free();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapPayloadArray");
        sb.append("{size=").append(size());
        sb.append(", unsafe=").append(isUnsafe());
        sb.append(", payloadLength=").append(payloadLength);
        sb.append('}');
        return sb.toString();
    }
}
