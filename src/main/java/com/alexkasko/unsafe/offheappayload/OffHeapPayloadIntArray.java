package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapDisposable;
import com.alexkasko.unsafe.offheap.OffHeapMemory;

/**
 * Implementation of off-heap header-int_payload array. Memory block for each index
 * contains {@code long} header anb {@code int} payload.
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
public class OffHeapPayloadIntArray implements OffHeapPayloadIntAddressable, OffHeapDisposable {
    private static final int HEADER_LENGTH = 8;
    private static final int ELEMENT_LENGTH = 12;

    private final OffHeapMemory ohm;

    /**
     * Constructor
     *
     * @param size array size
     */
    public OffHeapPayloadIntArray(long size) {
        this.ohm = OffHeapMemory.allocateMemory(size * ELEMENT_LENGTH);
    }

    /**
     * Whether unsafe implementation of {@link com.alexkasko.unsafe.offheap.OffHeapMemory} is used
     *
     * @return whether unsafe implementation of {@link com.alexkasko.unsafe.offheap.OffHeapMemory} is used
     */
    public boolean isUnsafe() {
        return ohm.isUnsafe();
    }

    /**
     * Gets the header at position {@code index}
     *
     * @param index collection index
     * @return long value
     */
    @Override
    public long get(long index) {
        return ohm.getLong(index * ELEMENT_LENGTH);
    }

    /**
     * Returns payload for the given index
     *
     * @param index collection index to load payload from
     */
    @Override
    public int getPayload(long index) {
        return ohm.getInt(index * ELEMENT_LENGTH + HEADER_LENGTH);
    }

    /**
     * Sets header and payload values on specified index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    @Override
    public void set(long index, long header, int payload) {
        long addr = index * ELEMENT_LENGTH;
        ohm.putLong(addr, header);
        ohm.putInt(addr + HEADER_LENGTH, payload);
    }

    /**
     * Sets payload value on specified index
     *
     * @param index collection index to set payload
     * @param payload payload value
     */
    public void setPayload(long index, int payload) {
        long addr = index * ELEMENT_LENGTH;
        ohm.putInt(addr + HEADER_LENGTH, payload);
    }

    /**
     * Returns number of elements in array
     *
     * @return number of elements in array
     */
    @Override
    public long size() {
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
        sb.append("OffHeapPayloadIntArray");
        sb.append("{size=").append(size());
        sb.append(", unsafe=").append(isUnsafe());
        sb.append('}');
        return sb.toString();
    }
}
