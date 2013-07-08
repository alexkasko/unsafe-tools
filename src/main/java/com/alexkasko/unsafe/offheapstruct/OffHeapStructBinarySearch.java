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

import java.io.Serializable;

/**
 * Binary search implementation borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/Arrays.java}
 * and adapted to {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructCollection}
 *
 * @author alexkasko
 * Date: 7/4/13
 * @see com.alexkasko.unsafe.offheaplong.OffHeapLongSorter
 * @see com.alexkasko.unsafe.offheapstruct.OffHeapStructSorter
 */
public class OffHeapStructBinarySearch {


    // long key part


    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using long struct key.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset long key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearchByLongKey(OffHeapStructCollection collection, long value, int keyOffset) {
        return binarySearchByLongKey(collection, 0, collection.size(), value, keyOffset);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using long struct key.
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection      the sorted collection to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @param keyOffset  long key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     */
    public static long binarySearchByLongKey(OffHeapStructCollection collection, long startIndex, long endIndex, long value, int keyOffset) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = collection.getLong(mid, keyOffset);

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using long struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset  long key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByLongKey(OffHeapStructCollection collection, long value, int keyOffset, IndexRange out) {
        binarySearchRangeByLongKey(collection, 0, collection.size(), value, keyOffset, out);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using long struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value the element to find.
     * @param keyOffset  long key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByLongKey(OffHeapStructCollection collection, long startIndex, long endIndex,
                                               long value, int keyOffset, IndexRange out) {
        long ind = binarySearchByLongKey(collection, startIndex, endIndex, value, keyOffset);
        if(ind < 0) {
            out.setEmpty(ind);
            return;
        }
        long from = ind;
        while (from >= startIndex && value == collection.getLong(from, keyOffset)) from -= 1;
        from += 1;
        long to = ind;
        while (to < endIndex && value == collection.getLong(to, keyOffset)) to += 1;
        to -= 1;
        out.set(from, to);
    }


    // int key part


    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using int struct key.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset int key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearchByIntKey(OffHeapStructCollection collection, long value, int keyOffset) {
        return binarySearchByIntKey(collection, 0, collection.size(), value, keyOffset);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using int struct key.
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection      the sorted collection to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @param keyOffset  int key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     */
    public static long binarySearchByIntKey(OffHeapStructCollection collection, long startIndex, long endIndex, long value, int keyOffset) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = collection.getInt(mid, keyOffset);

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using int struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset int key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByIntKey(OffHeapStructCollection collection, long value, int keyOffset, IndexRange out) {
        binarySearchRangeByIntKey(collection, 0, collection.size(), value, keyOffset, out);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using int struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value the element to find.
     * @param keyOffset  int key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByIntKey(OffHeapStructCollection collection, long startIndex, long endIndex,
                                               long value, int keyOffset, IndexRange out) {
        long ind = binarySearchByIntKey(collection, startIndex, endIndex, value, keyOffset);
        if(ind < 0) {
            out.setEmpty(ind);
            return;
        }
        long from = ind;
        while (from >= startIndex && value == collection.getInt(from, keyOffset)) from -= 1;
        from += 1;
        long to = ind;
        while (to < endIndex && value == collection.getInt(to, keyOffset)) to += 1;
        to -= 1;
        out.set(from, to);
    }


    // short key part


    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using short struct key.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset short key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearchByShortKey(OffHeapStructCollection collection, short value, int keyOffset) {
        return binarySearchByShortKey(collection, 0, collection.size(), value, keyOffset);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using short struct key.
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection      the sorted collection to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @param keyOffset  short key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     */
    public static long binarySearchByShortKey(OffHeapStructCollection collection, long startIndex, long endIndex, short value, int keyOffset) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = collection.getShort(mid, keyOffset);

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using short struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset  short key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByShortKey(OffHeapStructCollection collection, short value, int keyOffset, IndexRange out) {
        binarySearchRangeByShortKey(collection, 0, collection.size(), value, keyOffset, out);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using short struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value the element to find.
     * @param keyOffset  short key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByShortKey(OffHeapStructCollection collection, long startIndex, long endIndex,
                                                   short value, int keyOffset, IndexRange out) {
        long ind = binarySearchByShortKey(collection, startIndex, endIndex, value, keyOffset);
        if(ind < 0) {
            out.setEmpty(ind);
            return;
        }
        long from = ind;
        while (from >= startIndex && value == collection.getShort(from, keyOffset)) from -= 1;
        from += 1;
        long to = ind;
        while (to < endIndex && value == collection.getShort(to, keyOffset)) to += 1;
        to -= 1;
        out.set(from, to);
    }


    // byte key part


    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using byte struct key.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset byte key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearchByByteKey(OffHeapStructCollection collection, byte value, int keyOffset) {
        return binarySearchByByteKey(collection, 0, collection.size(), value, keyOffset);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using byte struct key.
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection      the sorted collection to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @param keyOffset  byte key field offset within stuct bounds
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     */
    public static long binarySearchByByteKey(OffHeapStructCollection collection, long startIndex, long endIndex, byte value, int keyOffset) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = collection.getByte(mid, keyOffset);

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using byte struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @param keyOffset  byte key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByByteKey(OffHeapStructCollection collection, byte value, int keyOffset, IndexRange out) {
        binarySearchRangeByByteKey(collection, 0, collection.size(), value, keyOffset, out);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection using byte struct key.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value the element to find.
     * @param keyOffset  byte key field offset within stuct bounds
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public static void binarySearchRangeByByteKey(OffHeapStructCollection collection, long startIndex, long endIndex,
                                                  byte value, int keyOffset, IndexRange out) {
        long ind = binarySearchByByteKey(collection, startIndex, endIndex, value, keyOffset);
        if(ind < 0) {
            out.setEmpty(ind);
            return;
        }
        long from = ind;
        while (from >= startIndex && value == collection.getByte(from, keyOffset)) from -= 1;
        from += 1;
        long to = ind;
        while (to < endIndex && value == collection.getByte(to, keyOffset)) to += 1;
        to -= 1;
        out.set(from, to);
    }


    /**
     * {@link OffHeapStructCollection} index range representation.
     * Was made mutable to prevent new object instantiation for each search.
     * {@link #isEmpty()} method should be checked before accessing indices.
     * Empty range will contain negative equal indices which values are
     * {@code -index - 1} where the element would be inserted.
     * Range with size {@code 1} will contain equal indices.
     */
    public static class IndexRange implements Serializable {
        private static final long serialVersionUID = 879348143026038919L;
        private boolean empty;
        private long fromIndex;
        private long toIndex;

        public IndexRange() {
            this.empty = false;
            this.fromIndex = -1;
            this.toIndex = -1;
        }

        /**
         * Sets value for empty range
         *
         * @param value negative value for empty range returned by search
         */
        private void setEmpty(long value) {
            this.empty = true;
            this.fromIndex = value;
            this.toIndex = value;
        }

        /**
         * Sets boundaries for non-empty range
         *
         * @param from start index
         * @param to end index
         */
        private void set(long from, long to) {
            this.empty = false;
            this.fromIndex = from;
            this.toIndex = to;
        }

        /**
         * Whether this range is empty
         *
         * @return whether this range is empty
         */
        public boolean isEmpty() {
            return empty;
        }

        /**
         * Whether this range is not empty
         *
         * @return whether this range is not empty
         */
        public boolean isNotEmpty() {
            return !empty;
        }

        /**
         * Start index or {@code -index - 1}
         *      where the element would be inserted for empty range
         *
         * @return start index value or {@code -index - 1}
         *      where the element would be inserted for empty range
         */
        public long getFromIndex() {
            return fromIndex;
        }

        /**
         * End index or {@code -index - 1}
         *      where the element would be inserted for empty range
         *
         * @return end index value, {@code -index - 1}
         *      where the element would be inserted for empty range
         */
        public long getToIndex() {
            return toIndex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IndexRange that = (IndexRange) o;
            if (empty != that.empty) return false;
            if (fromIndex != that.fromIndex) return false;
            if (toIndex != that.toIndex) return false;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = (empty ? 1 : 0);
            result = 31 * result + (int) (fromIndex ^ (fromIndex >>> 32));
            result = 31 * result + (int) (toIndex ^ (toIndex >>> 32));
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("IndexRange");
            sb.append("{empty=").append(empty);
            sb.append(", fromIndex=").append(fromIndex);
            sb.append(", toIndex=").append(toIndex);
            sb.append('}');
            return sb.toString();
        }
    }
}