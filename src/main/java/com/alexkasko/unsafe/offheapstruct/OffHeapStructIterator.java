/*
 * Copyright 2013 Alex Kasko (alexkasko.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexkasko.unsafe.offheapstruct;

import com.alexkasko.unsafe.offheap.OffHeapDisposableIterator;
import com.alexkasko.unsafe.offheap.OffHeapUtils;

/**
 * Iterator implementation for unsafe long collections.
 * Underneath collection size will be remembered once on iterator creation.
 *
 * @author alexkasko
 * Date: 7/3/13
 */
class OffHeapStructIterator implements OffHeapDisposableIterator<byte[]> {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() {
        OffHeapUtils.free(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return data.size();
    }
}
