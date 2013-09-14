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

import java.util.Comparator;

/**
 * Binary search implementation borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/Arrays.java}
 * and adapted to {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructCollection} with comparators.
 * Search methods are not static in this class to prevent internal comparator wrapper object creation on each search call.
 * All operations on instances of this class are NOT thread-safe.
 *
 * @author alexkasko
 * Date: 9/13/13
 */
public class OffHeapStructBinarySearchWithComparator {
    private final OffHeapStructCollection col;
    private final OffHeapStructComparator comp;

    /**
     * Constructor
     *
     * @param collection collection to search over
     * @param comparator comparator to use for comparing provided structure while searching
     */
    public OffHeapStructBinarySearchWithComparator(OffHeapStructCollection collection, Comparator<OffHeapStructAccessor> comparator) {
        if(null == collection) throw new IllegalArgumentException("Provided collection is null");
        if(null == comparator) throw new IllegalArgumentException("Provided comparator is null");
        this.col = collection;
        this.comp = new OffHeapStructComparator(collection, comparator);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection
     * comparing provided struct using comparator (both collection and comparator are bound to searcher instance).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param struct the structure to find
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public long binarySearch(byte[] struct) {
        return binarySearch(0, col.size(), struct);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection
     * comparing provided struct using comparator (both collection and comparator are bound to searcher instance).
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param struct the structure to find
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     */
    public long binarySearch(long startIndex, long endIndex, byte[] struct) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > col.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + col.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        if(null == struct) throw new IllegalArgumentException("Provided struct is null");

        int compres;
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            compres = comp.compare(mid, struct);
            if (compres < 0) {
                lo = mid + 1;
            } else if (compres > 0) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection
     * comparing provided struct using comparator (both collection and comparator are bound to searcher instance).
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param struct the structure to find
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public void binarySearchRange(byte[] struct, OffHeapStructBinarySearch.IndexRange out) {
        binarySearchRange(0, col.size(), struct, out);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap struct collection
     * comparing provided struct using comparator (both collection and comparator are bound to searcher instance).
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param struct the structure to find
     * @param out range instance, will be set with start/end indices having given value or with empty value
     */
    public void binarySearchRange(long startIndex, long endIndex, byte[] struct, OffHeapStructBinarySearch.IndexRange out) {
        long ind = binarySearch(startIndex, endIndex, struct);
        if(ind < 0) {
            out.setEmpty(ind);
            return;
        }
        long from = ind;
        while (from >= startIndex && comp.eq(from, struct)) {
            from -= 1;
        }
        from += 1;
        long to = ind;
        while (to < endIndex && comp.eq(to, struct)) {
            to += 1;
        }
        to -= 1;
        out.set(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapStructBinarySearchWithComparator");
        sb.append("{col=").append(col);
        sb.append(", comp=").append(comp);
        sb.append('}');
        return sb.toString();
    }
}