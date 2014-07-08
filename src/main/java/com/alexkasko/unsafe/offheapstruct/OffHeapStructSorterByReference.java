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

import com.alexkasko.unsafe.offheap.OffHeapDisposableIterable;
import com.alexkasko.unsafe.offheap.OffHeapDisposableIterator;
import com.alexkasko.unsafe.offheaplong.OffHeapLongArray;
import com.alexkasko.unsafe.offheaplong.OffHeapLongComparator;
import com.alexkasko.unsafe.offheaplong.OffHeapLongSorter;

import java.util.Comparator;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.freeAll;

/**
 * <p>alexkasko: borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java}
 * and adapted to {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructCollection} to sort it by reference.
 * without changing element positions in array itself.
 *
 * <p>Temporary {@link com.alexkasko.unsafe.offheaplong.OffHeapLongArray} with the same size
 * as collection itself will be used to hold references.
 *
 * <p>This class implements the Dual-Pivot Quicksort algorithm by
 * Vladimir Yaroslavskiy, Jon Bentley, and Joshua Bloch. The algorithm
 * offers O(n log(n)) performance on many data sets that cause other
 * quicksorts to degrade to quadratic performance, and is typically
 * faster than traditional (one-pivot) Quicksort implementations.
 *
 * @author Vladimir Yaroslavskiy
 * @author Jon Bentley
 * @author Josh Bloch
 *
 * @version 2009.11.29 m765.827.12i
 */
public class OffHeapStructSorterByReference {

    /**
     * Sorts collection using additional {@link com.alexkasko.unsafe.offheaplong.OffHeapLongArray} with the same size
     * as collection itself as an array of references (indices) of the collection
     *
     * @param a the off-heap struct collection to be sorted
     * @param comparator structs comparator
     * @return sorted iterable over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    static OffHeapDisposableIterable<byte[]> sortedIterable(OffHeapStructCollection a, Comparator<OffHeapStructAccessor> comparator) {
        return sortedIterable(a, 0, a.size(), comparator);
    }

    /**
     * Sorts collection using additional {@link com.alexkasko.unsafe.offheaplong.OffHeapLongArray} with the same size
     * as collection itself as an array of references (indices) of the collection
     *
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param comparator structs comparator
     * @return sorted iterable over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    static OffHeapDisposableIterable<byte[]> sortedIterable(OffHeapStructCollection a, long fromIndex,
                                           long toIndex, Comparator<OffHeapStructAccessor> comparator) {
        if(null == comparator) throw new IllegalArgumentException("Provided comparator is null");
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        OffHeapLongArray indices = new OffHeapLongArray(toIndex - fromIndex);
        for (long i = fromIndex; i < toIndex; i++) {
            indices.set(i, i);
        }
        OffHeapLongComparator indexComp = new OffHeapReferenceComparator(a, comparator);
        OffHeapLongSorter.sort(indices, indexComp);
        return new ReferenceIterable(a, indices);
    }

    /**
     * Comparator wrapper to use with references, not thread-safe
     */
    private static class OffHeapReferenceComparator implements OffHeapLongComparator {
        private final Comparator<OffHeapStructAccessor> comp;
        private final OffHeapStructIndexAccessor ac1;
        private final OffHeapStructIndexAccessor ac2;

        /**
         * Constructor
         *
         * @param data data in sorting
         * @param comp user provided comparator
         */
        private OffHeapReferenceComparator(OffHeapStructCollection data, Comparator<OffHeapStructAccessor> comp) {
            this.comp = comp;
            this.ac1 = new OffHeapStructIndexAccessor(data);
            this.ac2 = new OffHeapStructIndexAccessor(data);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(long l1, long l2) {
            ac1.setIndex(l1);
            ac2.setIndex(l2);
            return comp.compare(ac1, ac2);
        }
    }

    /**
     * Struct's accessor implementation for off-heap stored structs
     *
     * @author alexkasko
     *         Date: 9/13/13
     */
    private static class OffHeapStructIndexAccessor implements OffHeapStructAccessor {
        private final OffHeapStructCollection col;
        private long index = -1;

        /**
         * Constructor
         *
         * @param col collection to access structs from
         */
        private OffHeapStructIndexAccessor(OffHeapStructCollection col) {
            this.col = col;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int structLength() {
            return col.structLength();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void get(byte[] buffer) {
            col.get(index, buffer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte getByte(int offset) {
            return col.getByte(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short getUnsignedByte(int offset) {
            return col.getUnsignedByte(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short getShort(int offset) {
            return col.getShort(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getUnsignedShort(int offset) {
            return col.getUnsignedShort(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getInt(int offset) {
            return col.getInt(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getUnsignedInt(int offset) {
            return col.getUnsignedInt(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getLong(int offset) {
            return col.getLong(index, offset);
        }

        /**
         * Sets index value
         *
         * @param index index value
         */
        private void setIndex(long index) {
            this.index = index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("OffHeapStructIndexAccessor");
            sb.append("{col=").append(col);
            sb.append(", index=").append(index);
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Iterable over the data collection using indices from specified array
     */
    private static class ReferenceIterable implements OffHeapDisposableIterable<byte[]> {
        private final OffHeapStructCollection data;
        private final OffHeapLongArray index;

        /**
         * Constructor
         *
         * @param data data to iterate over
         * @param index sorted index
         */
        private ReferenceIterable(OffHeapStructCollection data, OffHeapLongArray index) {
            this.data = data;
            this.index = index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OffHeapDisposableIterator<byte[]> iterator() {
            return new ReferenceIterator(data, index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void free() {
            freeAll(data, index);
        }
    }

    /**
     * Iterator returning data elements in the order specified by indices array
     */
    private static class ReferenceIterator implements OffHeapDisposableIterator<byte[]> {
        private final OffHeapStructCollection data;
        private final OffHeapLongArray index;
        private final byte[] buf;
        private final long size;
        private long ii = 0;

        /**
         * Constructor
         *
         * @param data data to iterate over
         * @param index sorted index
         */
        private ReferenceIterator(OffHeapStructCollection data, OffHeapLongArray index) {
            this.data = data;
            this.index = index;
            this.buf = new byte[data.structLength()];
            this.size = index.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long size() {
            return index.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return ii < size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] next() {
            long ind = index.get(ii);
            ii += 1;
            data.get(ind, buf);
            return buf;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void free() {
            freeAll(data, index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ReferenceIterator");
            sb.append("{data=").append(data);
            sb.append(", index=").append(index);
            sb.append(", ii=").append(ii);
            sb.append('}');
            return sb.toString();
        }
    }
}
