package com.alexkasko.unsafe.offheap;

/**
 * User: alexkasko
 * Date: 3/5/13
 */
public class OffHeapBinarySearch {
    /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array}.
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array the sorted array to search.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearch(OffHeapAddressable array, long value) {
        return binarySearch(array, 0, array.size(), value);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array},
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array      the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException       if {@code startIndex > endIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code startIndex < 0 || endIndex > array.length}
     * @since 1.6
     */
    public static long binarySearch(OffHeapAddressable array, long startIndex, long endIndex, long value) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > array.size()) {
            throw new IllegalArgumentException("Illegal input, array size: [" + array.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = array.get(mid);

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
