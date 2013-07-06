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

package com.alexkasko.unsafe.offheappayload;

/**
 * alexkasko: borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java}
 * and adapted to {@link OffHeapPayloadAddressable}.
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
@Deprecated // use offheapstruct package
public class OffHeapPayloadSorter {

    /**
     * If the length of an collection to be sorted is less than this
     * constant, insertion sort is used in preference to Quicksort.
     */
    private static final int INSERTION_SORT_THRESHOLD = 32;

    /**
     * Sorts the specified off-heap header-payload collection into ascending order.
     *
     * @param a the off-heap header-payload collection to be sorted
     */
    public static void sort(OffHeapPayloadAddressable a) {
        sort(a, 0, a.size());
    }

    /**
     * Sorts the specified range of the off-heap header-payload collection into ascending order. The range
     * to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sort(OffHeapPayloadAddressable a, long fromIndex, long toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.payloadLength();
        doSort(a, fromIndex, toIndex - 1, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }

    /**
     * Sorts the specified range of the off-heap header-payload collection into ascending order. This
     * method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     */
    private static void doSort(OffHeapPayloadAddressable a, long left, long right, byte[] pi, byte[] pj,
                               byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {

        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                long ai = a.get(i);
                a.getPayload(i, pi);
                long j;
                for (j = i - 1; j >= left && ai < a.get(j); j--) {
                    a.getPayload(j, pj);
                    a.set(j + 1, a.get(j), pj);
                }
                a.set(j + 1, ai, pi);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksort(a, left, right, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap header-payload collection into ascending order by the
     * Dual-Pivot Quicksort algorithm.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     */
    private static void dualPivotQuicksort(OffHeapPayloadAddressable a, long left, long right, byte[] pi, byte[] pj,
                                           byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = a.get(e1), ae2 = a.get(e2), ae3 = a.get(e3), ae4 = a.get(e4), ae5 = a.get(e5);
        a.getPayload(e1, pe1); a.getPayload(e2, pe2); a.getPayload(e3, pe3); a.getPayload(e4, pe4); a.getPayload(e5, pe5);

        if (ae1 > ae2) { long t = ae1; byte[] pt = pe1; ae1 = ae2; pe1 = pe2; ae2 = t; pe2 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }
        if (ae1 > ae3) { long t = ae1; byte[] pt = pe1; ae1 = ae3; pe1 = pe3; ae3 = t; pe3 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae1 > ae4) { long t = ae1; byte[] pt = pe1; ae1 = ae4; pe1 = pe4; ae4 = t; pe4 = pt; }
        if (ae3 > ae4) { long t = ae3; byte[] pt = pe3; ae3 = ae4; pe3 = pe4; ae4 = t; pe4 = pt; }
        if (ae2 > ae5) { long t = ae2; byte[] pt = pe2; ae2 = ae5; pe2 = pe5; ae5 = t; pe5 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }

        a.set(e1, ae1, pe1); a.set(e3, ae3, pe3); a.set(e5, ae5, pe5);

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
        a.getPayload(left, pe1);
        long pivot1 = ae2; a.set(e2, a.get(left), pe1);
        a.getPayload(right, pe1);
        long pivot2 = ae4; a.set(e4, a.get(right), pe1);

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
                a.getPayload(k, pe1);
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.getPayload(less, pe3);
                        a.set(k, a.get(less), pe3);
                        a.set(less, ak, pe1);
                    }
                    less++;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.get(great) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.get(great) < pivot1) {
                        a.getPayload(less, pe3);
                        a.set(k, a.get(less), pe3);
                        a.getPayload(great, pe3);
                        a.set(less++, a.get(great), pe3);
                        a.set(great--, ak, pe1);
                    } else { // pivot1 <= a[great] <= pivot2
                        a.getPayload(great, pe3);
                        a.set(k, a.get(great), pe3);
                        a.set(great--, ak, pe1);
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
                a.getPayload(k, pe1);
                if (ak == pivot1) {
                    continue;
                }
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.getPayload(less, pe3);
                        a.set(k, a.get(less), pe3);
                        a.set(less, ak, pe1);
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
                        a.getPayload(less, pe3);
                        a.set(k, a.get(less), pe3);
                        a.getPayload(great, pe3);
                        a.set(less++, a.get(great), pe3);
                        a.set(great--, ak, pe1);
                    } else { // a[great] == pivot1
                        a.getPayload(great, pe3);
                        a.set(k, pivot1, pe3);
                        a.set(great--, ak, pe1);
                    }
                }
            }
        }

        // Swap pivots into their final positions
        a.getPayload(less - 1, pe1);
        a.set(left, a.get(less - 1), pe1); a.set(less - 1, pivot1, pe2);
        a.getPayload(great + 1, pe1);
        a.set(right, a.get(great + 1), pe1); a.set(great + 1, pivot2, pe4);

        // Sort left and right parts recursively, excluding known pivot values
        doSort(a, left,   less - 2, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSort(a, great + 2, right, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
                a.getPayload(k, pe1);
                if (ak == pivot2) { // Move a[k] to right part
                    while (a.get(great) == pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.get(great) == pivot1) {
                        a.getPayload(less, pe3);
                        a.set(k, a.get(less), pe3);
                        a.getPayload(great, pe3);
                        a.set(less++, pivot1, pe3);
                    } else { // pivot1 < a[great] < pivot2
                        a.getPayload(great, pe3);
                        a.set(k, a.get(great), pe3);
                    }
                    a.set(great--, ak, pe1);
                } else if (ak == pivot1) { // Move a[k] to left part
                    a.getPayload(less, pe3);
                    a.set(k, a.get(less), pe3);
                    a.set(less++, ak, pe1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSort(a, less, great, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }
}
