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

package com.alexkasko.unsafe.offheaplong;

import java.util.Iterator;

/**
 * <p>Iterator implementation for unsafe long collections.
 * Underneath collection size will be remembered once on iterator creation.
 *
 * <p>Note: iterator will create new autoboxed Long object <b>on every</b> {@link #next()} call,
 * this behaviour is inevitable with iterators in java 6/7.
 *
 * @author alexkasko
 * Date: 7/3/13
 */
class OffHeapLongIterator implements Iterator<Long> {
    private final OffHeapLongAddressable data;
    private final long size;
    private long index = 0;

    /**
     * Constructor
     *
     * @param data offheap long collection
     */
    OffHeapLongIterator(OffHeapLongAddressable data) {
        this.data = data;
        this.size = data.size();
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
    public Long next() {
        if (index >= size) throw new IllegalStateException(
                "Current index: [" + index + "] is greater or equal then collection size: [" + size + "]");
        return data.get(index++);
    }

    /**
     * Remove operation is not supported
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
        sb.append("OffHeapLongIterator");
        sb.append("{data=").append(data);
        sb.append(", size=").append(size);
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
