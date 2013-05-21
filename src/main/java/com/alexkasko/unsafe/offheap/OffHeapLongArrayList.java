package com.alexkasko.unsafe.offheap;

/**
 * Implementation of array-list of long using {@link OffHeapMemory}.
 * Memory area will be allocated another time and copied on elements adding. This class doesn't support elements removing.
 * {@link #get(long)} and {@link #set(long, long)} access operations indexes are checked using {@code assert} keyword
 * (indexes between size and capacity will be rejected).
 *
 * Default implementation uses {@code sun.misc.Unsafe}, with all operations guarded with {@code assert} keyword.
 * With assertions enabled in runtime ({@code -ea} java switch) {@link AssertionError}
 * will be thrown on illegal index access. Without assertions illegal index will crash JVM.
 *
 * Allocated memory may be freed manually using {@link #free()} (thread-safe
 * and may be called multiple times) or it will be freed after {@link OffHeapLongArray}
 * will be garbage collected.
 *
 * @author alexkasko
 *         Date: 3/1/13
 */
public class OffHeapLongArrayList implements OffHeapLongAddressable, OffHeapDisposable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final int ELEMENT_LENGTH = 8;

    private OffHeapMemory ohm;
    private long size;

    /**
     * Constructor, {@code 12} is used as initial capacity
     */
    public OffHeapLongArrayList() {
        this(MIN_CAPACITY_INCREMENT);
    }

    /**
     * Constructor
     *
     * @param capacity initial capacity
     */
    public OffHeapLongArrayList(long capacity) {
        this.ohm = OffHeapMemory.allocateMemory(capacity * ELEMENT_LENGTH);
    }

    /**
     * Adds element to the end of this list. Memory area will be allocated another time and copied
     * on capacity exceed.
     *
     * @param value value to add
     */
    public void add(long value) {
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
        set(s, value);
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
     * Gets the element at position {@code index} from {@code 0} to {@code size-1}
     *
     * @param index list index
     * @return long value
     */
    @Override
    public long get(long index) {
        assert index < size : index;
        return ohm.getLong(index * ELEMENT_LENGTH);
    }

    /**
     * Sets the element at position {@code index} (from {@code 0} to {@code size-1}) to the given value
     *
     * @param index list index
     * @param value long value
     */
    @Override
    public void set(long index, long value) {
        assert index < size : index;
        ohm.putLong(index * ELEMENT_LENGTH, value);
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
        sb.append("OffHeapLongArrayList");
        sb.append("{size=").append(size());
        sb.append(", unsafe=").append(isUnsafe());
        sb.append('}');
        return sb.toString();
    }
}