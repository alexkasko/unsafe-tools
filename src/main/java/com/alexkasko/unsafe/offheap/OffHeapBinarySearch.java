package com.alexkasko.unsafe.offheap;

/**
 * Binary search implementation borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/Arrays.java}
 * and adapted to {@link OffHeapAddressable}
 *
 * @author alexkasko
 * Date: 3/5/13
 * @see OffHeapLongSorter
 * @see OffHeapPayloadSorter
 */
public class OffHeapBinarySearch {
    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap collection.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearch(OffHeapAddressable collection, long value) {
        return binarySearch(collection, 0, collection.size(), value);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap collection,
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection      the sorted collection to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     * @since 1.6
     */
    public static long binarySearch(OffHeapAddressable collection, long startIndex, long endIndex, long value) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = collection.get(mid);

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
}
