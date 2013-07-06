package com.alexkasko.unsafe.offheapstruct;

import java.util.Iterator;

/**
 * Iterator implementation for unsafe long collections.
 * Underneath collection size will be remembered once on iterator creation.
 *
 * @author alexkasko
 * Date: 7/3/13
 */
public class OffHeapStructIterator implements Iterator<byte[]> {
    private final OffHeapStructCollection data;
    private final long size;
    private final byte[] buffer;
    private long index = 0;

    /**
     * Constructor
     *
     * @param data struct collection
     */
    OffHeapStructIterator(OffHeapStructCollection data) {
        this.data = data;
        this.size = data.size();
        this.buffer = new byte[data.structLength()];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return index < size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] next() {
        if (index >= size) throw new IllegalStateException(
                "Current index: [" + index + "] is greater or equal then collection size: [" + size + "]");
        data.get(index++, buffer);
        return buffer;
    }

    /**
     * Remove operation is not supported
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapStructIterator");
        sb.append("{data=").append(data);
        sb.append(", size=").append(size);
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
