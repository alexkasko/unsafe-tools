package com.alexkasko.unsafe;

/**
 * Implementation of array of long using {@link OffHeapMemory}.
 * Default implementation uses {@code sun.misc.Unsafe}, with all operations guarded with {@code assert} keyword.
 * With assertions enabled in runtime ({@code -ea} java switch) {@link AssertionError}
 * will be thrown on illegal index access. Without assertions illegal index will crash JVM.

 *
 * Array won't be zeroed after creation (will contain garbage by default).
 * Allocated memory may be freed manually using {@link #free()} (thread-safe
 * and may be called multiple times) or it will be freed after {@link OffHeapLongArray}
 * will be garbage collected.
 *
 * @author alexkasko
 * Date: 2/22/13
 */
public class OffHeapLongArray {
    private static final int ELEMENT_LENGTH = 8;

    private final OffHeapMemory ma;

    /**
     * Constructor
     *
     * @param size number of elements in array
     */
    public OffHeapLongArray(long size) {
        this.ma = OffHeapMemory.allocateMemory(size * ELEMENT_LENGTH);
    }

    /**
     * Whether unsafe implementation of {@link OffHeapMemory} is used
     *
     * @return whether unsafe implementation of {@link OffHeapMemory} is used
     */
    public boolean isUnsafe() {
        return ma.isUnsafe();
    }

    /**
     * Gets the element at position {@code index}
     *
     * @param index array index
     * @return long value
     */
    public long get(long index) {
        return ma.getLong(index * ELEMENT_LENGTH);
    }

    /**
     * Sets the element at position {@code index} to the given value
     *
     * @param index array index
     * @param value long value
     */
    public void set(long index, long value) {
        ma.putLong(index * ELEMENT_LENGTH, value);
    }

    /**
     * Returns number of elements in array
     *
     * @return number of elements in array
     */
    public long size() {
        return ma.length() / ELEMENT_LENGTH;
    }

    /**
     * Frees allocated memory
     */
    public void free() {
        ma.free();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapLongArray");
        sb.append("{size=").append(size());
        sb.append(", unsafe=").append(isUnsafe());
        sb.append('}');
        return sb.toString();
    }
}
