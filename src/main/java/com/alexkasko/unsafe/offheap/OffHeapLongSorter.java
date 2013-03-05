package com.alexkasko.unsafe.offheap;

/**
 * alexkasko: borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java}
 * and adapted to {@link OffHeapLongAddressable}.
 *
 * This class implements the Dual-Pivot Quicksort algorithm by
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
public class OffHeapLongSorter {

    /**
     * If the length of an array to be sorted is less than this
     * constant, insertion sort is used in preference to Quicksort.
     */
    private static final int INSERTION_SORT_THRESHOLD = 32;

    /**
     * Sorts the specified off-heap collection into ascending order.
     *
     * @param a the off-heap collection to be sorted
     */
    public static void sort(OffHeapLongAddressable a) {
        sort(a, 0, a.size());
    }

    /**
     * Sorts the specified range of the off-heap collection into ascending order. The range
     * to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sort(OffHeapLongAddressable a, long fromIndex, long toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        doSort(a, fromIndex, toIndex - 1);
    }

    /**
     * Sorts the specified range of the off-heap collection into ascending order. This
     * method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     */
    private static void doSort(OffHeapLongAddressable a, long left, long right) {
        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                long ai = a.get(i);
                long j;
                for (j = i - 1; j >= left && ai < a.get(j); j--) {
                    a.set(j + 1, a.get(j));
                }
                a.set(j + 1, ai);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksort(a, left, right);
        }
    }

    /**
     * Sorts the specified range of the off-heap collection into ascending order by the
     * Dual-Pivot Quicksort algorithm.
     *
     * @param a the off-heap collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     */
    private static void dualPivotQuicksort(OffHeapLongAddressable a, long left, long right) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = a.get(e1), ae2 = a.get(e2), ae3 = a.get(e3), ae4 = a.get(e4), ae5 = a.get(e5);

        if (ae1 > ae2) { long t = ae1; ae1 = ae2; ae2 = t; }
        if (ae4 > ae5) { long t = ae4; ae4 = ae5; ae5 = t; }
        if (ae1 > ae3) { long t = ae1; ae1 = ae3; ae3 = t; }
        if (ae2 > ae3) { long t = ae2; ae2 = ae3; ae3 = t; }
        if (ae1 > ae4) { long t = ae1; ae1 = ae4; ae4 = t; }
        if (ae3 > ae4) { long t = ae3; ae3 = ae4; ae4 = t; }
        if (ae2 > ae5) { long t = ae2; ae2 = ae5; ae5 = t; }
        if (ae2 > ae3) { long t = ae2; ae2 = ae3; ae3 = t; }
        if (ae4 > ae5) { long t = ae4; ae4 = ae5; ae5 = t; }

        a.set(e1, ae1); a.set(e3, ae3); a.set(e5, ae5);

        /*
         * Use the second and fourth of the five sorted elements as pivots.
         * These values are inexpensive approximations of the first and
         * second terciles of the array. Note that pivot1 <= pivot2.
         *
         * The pivots are stored in local variables, and the first and
         * the last of the elements to be sorted are moved to the locations
         * formerly occupied by the pivots. When partitioning is complete,
         * the pivots are swapped back into their final positions, and
         * excluded from subsequent sorting.
         */
        long pivot1 = ae2; a.set(e2, a.get(left));
        long pivot2 = ae4; a.set(e4, a.get(right));

        // Pointers
        long less  = left  + 1; // The index of first element of center part
        long great = right - 1; // The index before first element of right part

        boolean pivotsDiffer = (pivot1 != pivot2);

        if (pivotsDiffer) {
            /*
             * Partitioning:
             *
             *   left part         center part                    right part
             * +------------------------------------------------------------+
             * | < pivot1  |  pivot1 <= && <= pivot2  |    ?    |  > pivot2 |
             * +------------------------------------------------------------+
             *              ^                          ^       ^
             *              |                          |       |
             *             less                        k     great
             *
             * Invariants:
             *
             *              all in (left, less)   < pivot1
             *    pivot1 <= all in [less, k)     <= pivot2
             *              all in (great, right) > pivot2
             *
             * Pointer k is the first index of ?-part
             */
            outer:
            for (long k = less; k <= great; k++) {
                long ak = a.get(k);
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.set(k, a.get(less));
                        a.set(less, ak);
                    }
                    less++;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.get(great) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.get(great) < pivot1) {
                        a.set(k, a.get(less));
                        a.set(less++, a.get(great));
                        a.set(great--, ak);
                    } else { // pivot1 <= a[great] <= pivot2
                        a.set(k, a.get(great));
                        a.set(great--, ak);
                    }
                }
            }
        } else { // Pivots are equal
            /*
             * Partition degenerates to the traditional 3-way,
             * or "Dutch National Flag", partition:
             *
             *   left part   center part            right part
             * +----------------------------------------------+
             * |  < pivot  |  == pivot  |    ?    |  > pivot  |
             * +----------------------------------------------+
             *              ^            ^       ^
             *              |            |       |
             *             less          k     great
             *
             * Invariants:
             *
             *   all in (left, less)   < pivot
             *   all in [less, k)     == pivot
             *   all in (great, right) > pivot
             *
             * Pointer k is the first index of ?-part
             */
            for (long k = less; k <= great; k++) {
                long ak = a.get(k);
                if (ak == pivot1) {
                    continue;
                }
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.set(k, a.get(less));
                        a.set(less, ak);
                    }
                    less++;
                } else { // (a[k] > pivot1) -  Move a[k] to right part
                    /*
                     * We know that pivot1 == a[e3] == pivot2. Thus, we know
                     * that great will still be >= k when the following loop
                     * terminates, even though we don't test for it explicitly.
                     * In other words, a[e3] acts as a sentinel for great.
                     */
                    while (a.get(great) > pivot1) {
                        great--;
                    }
                    if (a.get(great) < pivot1) {
                        a.set(k, a.get(less));
                        a.set(less++, a.get(great));
                        a.set(great--, ak);
                    } else { // a[great] == pivot1
                        a.set(k, pivot1);
                        a.set(great--, ak);
                    }
                }
            }
        }

        // Swap pivots into their final positions
        a.set(left, a.get(less - 1)); a.set(less - 1, pivot1);
        a.set(right, a.get(great + 1)); a.set(great + 1, pivot2);

        // Sort left and right parts recursively, excluding known pivot values
        doSort(a, left,   less - 2);
        doSort(a, great + 2, right);

        /*
         * If pivot1 == pivot2, all elements from center
         * part are equal and, therefore, already sorted
         */
        if (!pivotsDiffer) {
            return;
        }

        /*
         * If center part is too large (comprises > 2/3 of the array),
         * swap internal pivot values to ends
         */
        if (less < e1 && great > e5) {
            while (a.get(less) == pivot1) {
                less++;
            }
            while (a.get(great) == pivot2) {
                great--;
            }

            /*
             * Partitioning:
             *
             *   left part       center part                   right part
             * +----------------------------------------------------------+
             * | == pivot1 |  pivot1 < && < pivot2  |    ?    | == pivot2 |
             * +----------------------------------------------------------+
             *              ^                        ^       ^
             *              |                        |       |
             *             less                      k     great
             *
             * Invariants:
             *
             *              all in (*, less)  == pivot1
             *     pivot1 < all in [less, k)   < pivot2
             *              all in (great, *) == pivot2
             *
             * Pointer k is the first index of ?-part
             */
            outer:
            for (long k = less; k <= great; k++) {
                long ak = a.get(k);
                if (ak == pivot2) { // Move a[k] to right part
                    while (a.get(great) == pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.get(great) == pivot1) {
                        a.set(k, a.get(less));
                        a.set(less++, pivot1);
                    } else { // pivot1 < a[great] < pivot2
                        a.set(k, a.get(great));
                    }
                    a.set(great--, pivot2);
                } else if (ak == pivot1) { // Move a[k] to left part
                    a.set(k, a.get(less));
                    a.set(less++, pivot1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSort(a, less, great);
    }
}
