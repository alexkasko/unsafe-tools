package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapDisposable;
import com.alexkasko.unsafe.offheap.OffHeapMemory;

/**
 * Implementation of off-heap header-payload array-list of long using {@link com.alexkasko.unsafe.offheap.OffHeapMemory}.
 * Memory block for each index contains {@code long} header anb {@code byte[]} payload.
 * Memory area will be allocated another time and copied on elements adding. This class doesn't support elements removing.
 * {@link #get(long)} and {@link #set(long, long, byte[])} access operations indexes are checked using {@code assert} keyword
 * (indexes between size and capacity will be rejected).
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
 * Date: 3/4/13
 */
public class OffHeapPayloadArrayList implements OffHeapPayloadAddressable, OffHeapDisposable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final int HEADER_LENGTH = 8;

    private final int payloadLength;
    private final int elementLength;
    private OffHeapMemory ohm;
    private long size;

    /**
     * Constructor, {@code 12} is used as initial capacity
     *
     * @param payloadLength length of payload data block in bytes
     */
    public OffHeapPayloadArrayList(int payloadLength) {
        this(MIN_CAPACITY_INCREMENT, payloadLength);
    }

    /**
     * Constructor
     *
     * @param capacity initial capacity
     * @param payloadLength length of payload data block in bytes
     */
    public OffHeapPayloadArrayList(long capacity, int payloadLength) {
        if(payloadLength <= 0) throw new IllegalArgumentException("Illegal payloadLength: [" + payloadLength + "]");
        this.payloadLength = payloadLength;
        this.elementLength = HEADER_LENGTH + payloadLength;
        this.ohm = OffHeapMemory.allocateMemory(capacity * elementLength);
    }

    /**
     * Whether unsafe implementation of {@link OffHeapMemory} is used
     *
     * @return whether unsafe implementation of {@link OffHeapMemory} is used
     */
    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    public void add(long header, byte[] payload) {
        add(header, payload, 0);
    }

    /**
     * Adds element to the end of this list. Memory area will be allocated another time and copied
     * on capacity exceed.
     *
     * @param header header to add
     * @param payload payload to add
     * @param payloadPos payload offset
     */
    public void add(long header, byte[] payload, int payloadPos) {
        OffHeapMemory oh = ohm;
        long s = size;
        if (s == capacity()) {
            long len = s + (s < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : s >> 1);
            OffHeapMemory newOhm = OffHeapMemory.allocateMemory(len * elementLength);
            // maybe it's better to use Unsafe#reallocateMemory here
            oh.copy(0, newOhm, 0, ohm.length());
            oh.free();
            ohm = newOhm;
        }
        size = s + 1;
        set(s, header, payload, payloadPos);
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
     * @param index list index
     * @return long header
     */
    @Override
    public long get(long index) {
        assert index < size : index;
        return ohm.getLong(index * elementLength);
    }

    /**
     * Loads payload into provided buffer
     *
     * @param index list index to load payload from
     * @param buffer buffer to load payload into
     */
    @Override
    public void getPayload(long index, byte[] buffer) {
        assert index < size : index;
        ohm.get(index * elementLength + HEADER_LENGTH, buffer);
    }

    /**
     * Loads payload into provided buffer
     *
     * @param index list index to load payload from
     * @param buffer buffer to load payload into
     * @param bufferPos buffer position
     */
    public void getPayload(long index, byte[] buffer, int bufferPos) {
        assert index < size : index;
        ohm.get(index * elementLength + HEADER_LENGTH, buffer, bufferPos, payloadLength);
    }

    /**
     * Sets header value and copies payload data on provided index
     *
     * @param index list index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    @Override
    public void set(long index, long header, byte[] payload) {
        assert index < size : index;
        long addr = index * elementLength;
        ohm.putLong(addr, header);
        ohm.put(addr + HEADER_LENGTH, payload);
    }

    /**
     * Sets header value and copies payload data on provided index
     *
     * @param index list index to set header and payload
     * @param header header value
     * @param payload payload value
     * @param payloadPos payload position
     */
    public void set(long index, long header, byte[] payload, int payloadPos) {
        assert index < size : index;
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
        assert index < size : index;
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
        assert index < size : index;
        long addr = index * elementLength;
        ohm.put(addr + HEADER_LENGTH, payload, payloadPos, payloadLength);
    }


    /**
     * Returns number of elements in list
     *
     * @return number of elements in list
     */
    @Override
    public long size() {
        return size;
    }

    /**
     * Returns number of elements list may contain without additional memory allocation
     *
     * @return number of elements list may contain without additional memory allocation
     */
    public long capacity() {
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
        sb.append("OffHeapPayloadArrayList");
        sb.append("{size=").append(size);
        sb.append(", unsafe=").append(isUnsafe());
        sb.append(", payloadLength=").append(payloadLength);
        sb.append('}');
        return sb.toString();
    }
}