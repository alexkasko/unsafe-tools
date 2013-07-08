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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>alexkasko: borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java}
 * and adapted to {@link OffHeapStructCollection}.
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
public class OffHeapStructSorter {

    /**
     * If the length of an collection to be sorted is less than this
     * constant, insertion sort is used in preference to Quicksort.
     */
    private static final int INSERTION_SORT_THRESHOLD = 32;


    // multiple conditions sort


    /**
     * Sorts collection using multiple keys. Every subsequent keys is used on
     * collections interval where previous key has equal values
     *
     * @param collection off-heap struct collection
     * @param keys vararg keys list
     */
    public static void sort(OffHeapStructCollection collection, OffHeapStructSortKey... keys) {
        List<OffHeapStructSortKey> keyList = new ArrayList<OffHeapStructSortKey>(keys.length);
        Collections.addAll(keyList, keys);
        sort(collection, keyList);
    }

    /**
     * Sorts collection using multiple keys. Every subsequent keys is used on
     * collections interval where previous key has equal values
     *
     * @param collection off-heap struct collection
     * @param keys keys list
     */
    public static void sort(OffHeapStructCollection collection, List<OffHeapStructSortKey> keys) {
        multisortInternal(collection, 0, collection.size(), keys, 0);
    }

    private static void multisortInternal(OffHeapStructCollection collection, long start, long end,
                                     List<OffHeapStructSortKey> keys, int keyIndex) {
        if(keyIndex >= keys.size()) return;
        OffHeapStructSortKey key = keys.get(keyIndex);
        // sort current key
        sortByKey(collection, start, end, key);
        // whether last key reached
        if(keyIndex >= keys.size() - 1) return;
        // sort children keys recursively
        int childKeyIndex = keyIndex + 1;
        long childStart = start;
        long cur = getByKey(collection, childStart, key);
        for(long i = start + 1; i < end; i++) {
            long el = getByKey(collection, i, key);
            if(el != cur) {
                multisortInternal(collection, childStart, i, keys, childKeyIndex);
                childStart = i;
                cur = getByKey(collection, childStart, key);
            }
        }
        // sort tail
        if(childStart < end - 1) multisortInternal(collection, childStart, end, keys, childKeyIndex);
    }

    private static void sortByKey(OffHeapStructCollection collection, long start, long end, OffHeapStructSortKey key) {
        switch (key.length()) {
            case 8: sortByLongKey(collection, start, end, key.offset()); break;
            case 4: sortByIntKey(collection, start, end, key.offset()); break;
            case 2: sortByShortKey(collection, start, end, key.offset()); break;
            case 1: sortByByteKey(collection, start, end, key.offset()); break;
            default: throw new IllegalArgumentException(key.toString());
        }
    }

    private static long getByKey(OffHeapStructCollection collection, long index, OffHeapStructSortKey key) {
        switch (key.length()) {
            case 8: return collection.getLong(index, key.offset());
            case 4: return collection.getInt(index, key.offset());
            case 2: return collection.getShort(index, key.offset());
            case 1: return collection.getByte(index, key.offset());
            default: throw new IllegalArgumentException(key.toString());
        }
    }


    // long key part


    /**
     * Sorts the specified off-heap struct collection into ascending order using long struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset long key field offset within stuct bounds
     */
    public static void sortByLongKey(OffHeapStructCollection a, int keyOffset) {
        sortByLongKey(a, 0, a.size(), keyOffset);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using long struct key.
     * The range to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param keyOffset long sort key field offset within stuct bounds
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sortByLongKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        doSortByLongKey(a, fromIndex, toIndex - 1, keyOffset, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }


    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using long struct key.
     * This method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted* @param keyOffset
     * @param keyOffset long sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void doSortByLongKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                               byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                long ai = a.getLong(i, keyOffset);
                a.get(i, pi);
                long j;
                for (j = i - 1; j >= left && ai < a.getLong(j, keyOffset); j--) {
                    a.get(j, pj);
                    a.set(j + 1, pj);
                }
                a.set(j + 1, pi);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksortByLongKey(a, left, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order by the
     * Dual-Pivot Quicksort algorithm using long struct key.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param keyOffset long sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void dualPivotQuicksortByLongKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                                           byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = a.getLong(e1, keyOffset), ae2 = a.getLong(e2, keyOffset), ae3 = a.getLong(e3, keyOffset),
                ae4 = a.getLong(e4, keyOffset), ae5 = a.getLong(e5, keyOffset);
        a.get(e1, pe1); a.get(e2, pe2); a.get(e3, pe3); a.get(e4, pe4); a.get(e5, pe5);

        if (ae1 > ae2) { long t = ae1; byte[] pt = pe1; ae1 = ae2; pe1 = pe2; ae2 = t; pe2 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }
        if (ae1 > ae3) { long t = ae1; byte[] pt = pe1; ae1 = ae3; pe1 = pe3; ae3 = t; pe3 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae1 > ae4) { long t = ae1; byte[] pt = pe1; ae1 = ae4; pe1 = pe4; ae4 = t; pe4 = pt; }
        if (ae3 > ae4) { long t = ae3; byte[] pt = pe3; ae3 = ae4; pe3 = pe4; ae4 = t; pe4 = pt; }
        if (ae2 > ae5) { long t = ae2; byte[] pt = pe2; ae2 = ae5; pe2 = pe5; ae5 = t; pe5 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }

        a.set(e1, pe1); a.set(e3, pe3); a.set(e5, pe5);

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
        a.get(left, pe1);
        long pivot1 = ae2; a.set(e2, pe1);
        a.get(right, pe1);
        long pivot2 = ae4; a.set(e4, pe1);

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
                long ak = a.getLong(k, keyOffset);
                a.get(k, pe1);
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.getLong(great, keyOffset) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getLong(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // pivot1 <= a[great] <= pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
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
                long ak = a.getLong(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot1) {
                    continue;
                }
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else { // (a[k] > pivot1) -  Move a[k] to right part
                    /*
                     * We know that pivot1 == a[e3] == pivot2. Thus, we know
                     * that great will still be >= k when the following loop
                     * terminates, even though we don't test for it explicitly.
                     * In other words, a[e3] acts as a sentinel for great.
                     */
                    while (a.getLong(great, keyOffset) > pivot1) {
                        great--;
                    }
                    if (a.getLong(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // a[great] == pivot1
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
                    }
                }
            }
        }

        // Swap pivots into their final positions
        a.get(less - 1, pe1);
        a.set(left, pe1); a.set(less - 1, pe2);
        a.get(great + 1, pe1);
        a.set(right, pe1); a.set(great + 1, pe4);

        // Sort left and right parts recursively, excluding known pivot values
        doSortByLongKey(a, left,   less - 2, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSortByLongKey(a, great + 2, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
            while (a.getLong(less, keyOffset) == pivot1) {
                less++;
            }
            while (a.getLong(great, keyOffset) == pivot2) {
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
                long ak = a.getLong(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot2) { // Move a[k] to right part
                    while (a.getLong(great, keyOffset) == pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getLong(great, keyOffset) == pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                    } else { // pivot1 < a[great] < pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                    }
                    a.set(great--, pe1);
                } else if (ak == pivot1) { // Move a[k] to left part
                    a.get(less, pe3);
                    a.set(k, pe3);
                    a.set(less++, pe1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSortByLongKey(a, less, great, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }


    // int key part


    /**
     * Sorts the specified off-heap struct collection into ascending order using int struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset int key field offset within stuct bounds
     */
    public static void sortByIntKey(OffHeapStructCollection a, int keyOffset) {
        sortByIntKey(a, 0, a.size(), keyOffset);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using int struct key. The range
     * to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param keyOffset int sort key field offset within stuct bounds
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sortByIntKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        doSortByIntKey(a, fromIndex, toIndex - 1, keyOffset, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }


    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using int struct key.
     * This method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted* @param keyOffset
     * @param keyOffset int sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void doSortByIntKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                               byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                long ai = a.getInt(i, keyOffset);
                a.get(i, pi);
                long j;
                for (j = i - 1; j >= left && ai < a.getInt(j, keyOffset); j--) {
                    a.get(j, pj);
                    a.set(j + 1, pj);
                }
                a.set(j + 1, pi);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksortByIntKey(a, left, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order by the
     * Dual-Pivot Quicksort algorithm using int struct key.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param keyOffset int sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void dualPivotQuicksortByIntKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                                           byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = a.getInt(e1, keyOffset), ae2 = a.getInt(e2, keyOffset), ae3 = a.getInt(e3, keyOffset),
                ae4 = a.getInt(e4, keyOffset), ae5 = a.getInt(e5, keyOffset);
        a.get(e1, pe1); a.get(e2, pe2); a.get(e3, pe3); a.get(e4, pe4); a.get(e5, pe5);

        if (ae1 > ae2) { long t = ae1; byte[] pt = pe1; ae1 = ae2; pe1 = pe2; ae2 = t; pe2 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }
        if (ae1 > ae3) { long t = ae1; byte[] pt = pe1; ae1 = ae3; pe1 = pe3; ae3 = t; pe3 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae1 > ae4) { long t = ae1; byte[] pt = pe1; ae1 = ae4; pe1 = pe4; ae4 = t; pe4 = pt; }
        if (ae3 > ae4) { long t = ae3; byte[] pt = pe3; ae3 = ae4; pe3 = pe4; ae4 = t; pe4 = pt; }
        if (ae2 > ae5) { long t = ae2; byte[] pt = pe2; ae2 = ae5; pe2 = pe5; ae5 = t; pe5 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }

        a.set(e1, pe1); a.set(e3, pe3); a.set(e5, pe5);

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
        a.get(left, pe1);
        long pivot1 = ae2; a.set(e2, pe1);
        a.get(right, pe1);
        long pivot2 = ae4; a.set(e4, pe1);

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
                long ak = a.getInt(k, keyOffset);
                a.get(k, pe1);
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.getInt(great, keyOffset) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getInt(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // pivot1 <= a[great] <= pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
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
                long ak = a.getInt(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot1) {
                    continue;
                }
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else { // (a[k] > pivot1) -  Move a[k] to right part
                    /*
                     * We know that pivot1 == a[e3] == pivot2. Thus, we know
                     * that great will still be >= k when the following loop
                     * terminates, even though we don't test for it explicitly.
                     * In other words, a[e3] acts as a sentinel for great.
                     */
                    while (a.getInt(great, keyOffset) > pivot1) {
                        great--;
                    }
                    if (a.getInt(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // a[great] == pivot1
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
                    }
                }
            }
        }

        // Swap pivots into their final positions
        a.get(less - 1, pe1);
        a.set(left, pe1); a.set(less - 1, pe2);
        a.get(great + 1, pe1);
        a.set(right, pe1); a.set(great + 1, pe4);

        // Sort left and right parts recursively, excluding known pivot values
        doSortByIntKey(a, left,   less - 2, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSortByIntKey(a, great + 2, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
            while (a.getInt(less, keyOffset) == pivot1) {
                less++;
            }
            while (a.getInt(great, keyOffset) == pivot2) {
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
                long ak = a.getInt(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot2) { // Move a[k] to right part
                    while (a.getInt(great, keyOffset) == pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getInt(great, keyOffset) == pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                    } else { // pivot1 < a[great] < pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                    }
                    a.set(great--, pe1);
                } else if (ak == pivot1) { // Move a[k] to left part
                    a.get(less, pe3);
                    a.set(k, pe3);
                    a.set(less++, pe1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSortByIntKey(a, less, great, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }


    // short key part


    /**
     * Sorts the specified off-heap struct collection into ascending order using short struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset short key field offset within stuct bounds
     */
    public static void sortByShortKey(OffHeapStructCollection a, int keyOffset) {
        sortByShortKey(a, 0, a.size(), keyOffset);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using short struct key.
     * The range to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param keyOffset long short key field offset within stuct bounds
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sortByShortKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        doSortByShortKey(a, fromIndex, toIndex - 1, keyOffset, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }


    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using short struct key.
     * This method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted* @param keyOffset
     * @param keyOffset short sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void doSortByShortKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                               byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                long ai = a.getShort(i, keyOffset);
                a.get(i, pi);
                long j;
                for (j = i - 1; j >= left && ai < a.getShort(j, keyOffset); j--) {
                    a.get(j, pj);
                    a.set(j + 1, pj);
                }
                a.set(j + 1, pi);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksortByShortKey(a, left, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order by the
     * Dual-Pivot Quicksort algorithm using short struct key.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param keyOffset short sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void dualPivotQuicksortByShortKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                                           byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = a.getShort(e1, keyOffset), ae2 = a.getShort(e2, keyOffset), ae3 = a.getShort(e3, keyOffset),
                ae4 = a.getShort(e4, keyOffset), ae5 = a.getShort(e5, keyOffset);
        a.get(e1, pe1); a.get(e2, pe2); a.get(e3, pe3); a.get(e4, pe4); a.get(e5, pe5);

        if (ae1 > ae2) { long t = ae1; byte[] pt = pe1; ae1 = ae2; pe1 = pe2; ae2 = t; pe2 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }
        if (ae1 > ae3) { long t = ae1; byte[] pt = pe1; ae1 = ae3; pe1 = pe3; ae3 = t; pe3 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae1 > ae4) { long t = ae1; byte[] pt = pe1; ae1 = ae4; pe1 = pe4; ae4 = t; pe4 = pt; }
        if (ae3 > ae4) { long t = ae3; byte[] pt = pe3; ae3 = ae4; pe3 = pe4; ae4 = t; pe4 = pt; }
        if (ae2 > ae5) { long t = ae2; byte[] pt = pe2; ae2 = ae5; pe2 = pe5; ae5 = t; pe5 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }

        a.set(e1, pe1); a.set(e3, pe3); a.set(e5, pe5);

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
        a.get(left, pe1);
        long pivot1 = ae2; a.set(e2, pe1);
        a.get(right, pe1);
        long pivot2 = ae4; a.set(e4, pe1);

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
                long ak = a.getShort(k, keyOffset);
                a.get(k, pe1);
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.getShort(great, keyOffset) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getShort(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // pivot1 <= a[great] <= pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
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
                long ak = a.getShort(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot1) {
                    continue;
                }
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else { // (a[k] > pivot1) -  Move a[k] to right part
                    /*
                     * We know that pivot1 == a[e3] == pivot2. Thus, we know
                     * that great will still be >= k when the following loop
                     * terminates, even though we don't test for it explicitly.
                     * In other words, a[e3] acts as a sentinel for great.
                     */
                    while (a.getShort(great, keyOffset) > pivot1) {
                        great--;
                    }
                    if (a.getShort(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // a[great] == pivot1
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
                    }
                }
            }
        }

        // Swap pivots into their final positions
        a.get(less - 1, pe1);
        a.set(left, pe1); a.set(less - 1, pe2);
        a.get(great + 1, pe1);
        a.set(right, pe1); a.set(great + 1, pe4);

        // Sort left and right parts recursively, excluding known pivot values
        doSortByShortKey(a, left,   less - 2, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSortByShortKey(a, great + 2, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
            while (a.getShort(less, keyOffset) == pivot1) {
                less++;
            }
            while (a.getShort(great, keyOffset) == pivot2) {
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
                long ak = a.getShort(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot2) { // Move a[k] to right part
                    while (a.getShort(great, keyOffset) == pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getShort(great, keyOffset) == pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                    } else { // pivot1 < a[great] < pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                    }
                    a.set(great--, pe1);
                } else if (ak == pivot1) { // Move a[k] to left part
                    a.get(less, pe3);
                    a.set(k, pe3);
                    a.set(less++, pe1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSortByShortKey(a, less, great, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }


    // byte key part


    /**
     * Sorts the specified off-heap struct collection into ascending order using byte struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset byte key field offset within stuct bounds
     */
    public static void sortByByteKey(OffHeapStructCollection a, int keyOffset) {
        sortByByteKey(a, 0, a.size(), keyOffset);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using byte struct key.
     * The range to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param keyOffset byte sort key field offset within stuct bounds
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sortByByteKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        doSortByByteKey(a, fromIndex, toIndex - 1, keyOffset, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }


    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using byte struct key.
     * This method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted* @param keyOffset
     * @param keyOffset byte sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void doSortByByteKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                               byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                long ai = a.getByte(i, keyOffset);
                a.get(i, pi);
                long j;
                for (j = i - 1; j >= left && ai < a.getByte(j, keyOffset); j--) {
                    a.get(j, pj);
                    a.set(j + 1, pj);
                }
                a.set(j + 1, pi);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksortByByteKey(a, left, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order by the
     * Dual-Pivot Quicksort algorithm using byte struct key.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param keyOffset byte sort key field offset within stuct bounds
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void dualPivotQuicksortByByteKey(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
                                           byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = a.getByte(e1, keyOffset), ae2 = a.getByte(e2, keyOffset), ae3 = a.getByte(e3, keyOffset),
                ae4 = a.getByte(e4, keyOffset), ae5 = a.getByte(e5, keyOffset);
        a.get(e1, pe1); a.get(e2, pe2); a.get(e3, pe3); a.get(e4, pe4); a.get(e5, pe5);

        if (ae1 > ae2) { long t = ae1; byte[] pt = pe1; ae1 = ae2; pe1 = pe2; ae2 = t; pe2 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }
        if (ae1 > ae3) { long t = ae1; byte[] pt = pe1; ae1 = ae3; pe1 = pe3; ae3 = t; pe3 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae1 > ae4) { long t = ae1; byte[] pt = pe1; ae1 = ae4; pe1 = pe4; ae4 = t; pe4 = pt; }
        if (ae3 > ae4) { long t = ae3; byte[] pt = pe3; ae3 = ae4; pe3 = pe4; ae4 = t; pe4 = pt; }
        if (ae2 > ae5) { long t = ae2; byte[] pt = pe2; ae2 = ae5; pe2 = pe5; ae5 = t; pe5 = pt; }
        if (ae2 > ae3) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (ae4 > ae5) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }

        a.set(e1, pe1); a.set(e3, pe3); a.set(e5, pe5);

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
        a.get(left, pe1);
        long pivot1 = ae2; a.set(e2, pe1);
        a.get(right, pe1);
        long pivot2 = ae4; a.set(e4, pe1);

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
                long ak = a.getByte(k, keyOffset);
                a.get(k, pe1);
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.getByte(great, keyOffset) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getByte(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // pivot1 <= a[great] <= pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
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
                long ak = a.getByte(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot1) {
                    continue;
                }
                if (ak < pivot1) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else { // (a[k] > pivot1) -  Move a[k] to right part
                    /*
                     * We know that pivot1 == a[e3] == pivot2. Thus, we know
                     * that great will still be >= k when the following loop
                     * terminates, even though we don't test for it explicitly.
                     * In other words, a[e3] acts as a sentinel for great.
                     */
                    while (a.getByte(great, keyOffset) > pivot1) {
                        great--;
                    }
                    if (a.getByte(great, keyOffset) < pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                        a.set(great--, pe1);
                    } else { // a[great] == pivot1
                        a.get(great, pe3);
                        a.set(k, pe3);
                        a.set(great--, pe1);
                    }
                }
            }
        }

        // Swap pivots into their final positions
        a.get(less - 1, pe1);
        a.set(left, pe1); a.set(less - 1, pe2);
        a.get(great + 1, pe1);
        a.set(right, pe1); a.set(great + 1, pe4);

        // Sort left and right parts recursively, excluding known pivot values
        doSortByByteKey(a, left,   less - 2, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSortByByteKey(a, great + 2, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
            while (a.getByte(less, keyOffset) == pivot1) {
                less++;
            }
            while (a.getByte(great, keyOffset) == pivot2) {
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
                long ak = a.getByte(k, keyOffset);
                a.get(k, pe1);
                if (ak == pivot2) { // Move a[k] to right part
                    while (a.getByte(great, keyOffset) == pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.getByte(great, keyOffset) == pivot1) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                    } else { // pivot1 < a[great] < pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                    }
                    a.set(great--, pe1);
                } else if (ak == pivot1) { // Move a[k] to left part
                    a.get(less, pe3);
                    a.set(k, pe3);
                    a.set(less++, pe1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSortByByteKey(a, less, great, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }
}
