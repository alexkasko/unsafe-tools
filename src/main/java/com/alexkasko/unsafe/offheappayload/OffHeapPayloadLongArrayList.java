package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapDisposable;
import com.alexkasko.unsafe.offheap.OffHeapMemory;

/**
 * Implementation of off-heap header-long_payload array-list of long using {@link com.alexkasko.unsafe.offheap.OffHeapMemory}.
 * Memory block for each index contains {@code long} header anb {@code long} payload.
 * Memory area will be allocated another time and copied on elements adding. This class doesn't support elements removing.
 * {@link #get(long)} and {@link #set(long, long, long)} access operations indexes are checked using {@code assert} keyword
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
 * Date: 4/24/13
 */
public class OffHeapPayloadLongArrayList implements OffHeapPayloadLongAddressable, OffHeapDisposable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final int HEADER_LENGTH = 8;
    private static final int ELEMENT_LENGTH = 16;

    private OffHeapMemory ohm;
    private long size;

    /**
     * Constructor, {@code 12} is used as initial capacity
     */
    public OffHeapPayloadLongArrayList() {
        this(MIN_CAPACITY_INCREMENT);
    }

    /**
     * Constructor
     *
     * @param capacity initial capacity
     */
    public OffHeapPayloadLongArrayList(long capacity) {
        this.ohm = OffHeapMemory.allocateMemory(capacity * ELEMENT_LENGTH);
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
     * Adds element to the end of this list. Memory area will be allocated another time and copied
     * on capacity exceed.
     *
     * @param header header to add
     * @param payload payload to add
     */
    public void add(long header, long payload) {
        OffHeapMemory oh = ohm;
        long s = size;
        if (s == capacity()) {
            long len = s + (s < (MIN_CAPACITY_INCREMENT / 2) ? MIN_CAPACITY_INCREMENT : s >> 1);
            OffHeapMemory newOhm = OffHeapMemory.allocateMemory(len * ELEMENT_LENGTH);
            // maybe it's better to use Unsafe#reallocateMemory here
            oh.copy(0, newOhm, 0, ohm.length());
            oh.free();
            ohm = newOhm;
        }
        size = s + 1;
        set(s, header, payload);
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
        return ohm.getLong(index * ELEMENT_LENGTH);
    }

    /**
     * Returns payload for the given index
     *
     * @param index list index to load payload from
     */
    @Override
    public long getPayload(long index) {
        assert index < size : index;
        return ohm.getLong(index * ELEMENT_LENGTH + HEADER_LENGTH);
    }

    /**
     * Sets header and payload values on specified index
     *
     * @param index list index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    @Override
    public void set(long index, long header, long payload) {
        assert index < size : index;
        long addr = index * ELEMENT_LENGTH;
        ohm.putLong(addr, header);
        ohm.putLong(addr + HEADER_LENGTH, payload);
    }

    /**
     * Sets payload value on specified index
     *
     * @param index collection index to set payload
     * @param payload payload value
     */
    public void setPayload(long index, long payload) {
        assert index < size : index;
        long addr = index * ELEMENT_LENGTH;
        ohm.putLong(addr + HEADER_LENGTH, payload);
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
        return ohm.length() / ELEMENT_LENGTH;
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
        sb.append("OffHeapPayloadLongArrayList");
        sb.append("{size=").append(size);
        sb.append(", unsafe=").append(isUnsafe());
        sb.append('}');
        return sb.toString();
    }
}