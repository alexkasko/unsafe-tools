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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.alexkasko.unsafe.offheapstruct.OffHeapStructSorter.INSERTION_SORT_THRESHOLD;

/**
 * <p>alexkasko: borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java}
 * and adapted to {@link com.alexkasko.unsafe.offheapstruct.OffHeapStructCollection} with comparator.
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
class OffHeapStructSorterWithComparator {

    /**
     * Partially sorts collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threads number of worker threads to use
     * @param a the off-heap struct collection to be sorted
     * @param comparator structs comparator
     * @return sorted iterator over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    static OffHeapDisposableIterator<byte[]> sortedIterator(ExecutorService executor, int threads, OffHeapStructCollection a, Comparator<OffHeapStructAccessor> comparator) {
        return sortedIterator(executor, threads, a, 0, a.size(), comparator);
    }

    /**
     * Partially sorts part of the collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threads number of worker threads to use
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param comparator structs comparator
     * @return sorted iterator over the collection part
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    static OffHeapDisposableIterator<byte[]> sortedIterator(ExecutorService executor, int threads, OffHeapStructCollection a, long fromIndex,
                                           long toIndex, Comparator<OffHeapStructAccessor> comparator) {
        if(null == comparator) throw new IllegalArgumentException("Provided comparator is null");
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        long step = (toIndex - 1 - fromIndex)/threads;
        if(0 == step) {
            doSort(a, fromIndex, toIndex - 1, new OffHeapStructComparator(a, comparator), new byte[len], new byte[len],
                    new byte[len], new byte[len], new byte[len], new byte[len], new byte[len]);
            return a.iterator();
        } else {
            List<Worker> workers = new ArrayList<Worker>();
            for(int start = 0; start < toIndex; start += step) {
                Worker worker = new Worker(a, start, Math.min(start + step - 1, toIndex - 1), new OffHeapStructComparator(a, comparator),
                        new byte[len], new byte[len], new byte[len], new byte[len], new byte[len], new byte[len], new byte[len]);
                workers.add(worker);
            }
            invokeAndWait(executor, workers);
            return new MergeIter(a, new OffHeapStructComparator(a, comparator), workers);
        }
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using unsigned long struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param comparator structs comparator
     */
    static void sort(OffHeapStructCollection a, Comparator<OffHeapStructAccessor> comparator) {
        sort(a, 0, a.size(), comparator);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using unsigned long struct key.
     * The range to be sorted extends from the index {@code fromIndex}, inclusive, to
     * the index {@code toIndex}, exclusive. If {@code fromIndex == toIndex},
     * the range to be sorted is empty (and the call is a no-op).
     *
     * @param a the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param comparator structs comparator
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    static void sort(OffHeapStructCollection a, long fromIndex, long toIndex, Comparator<OffHeapStructAccessor> comparator) {
        if(null == comparator) throw new IllegalArgumentException("Provided comparator is null");
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        OffHeapStructComparator comp = new OffHeapStructComparator(a, comparator);
        int len = a.structLength();
        doSort(a, fromIndex, toIndex - 1, comp, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using unsigned long struct key.
     * This method differs from the public {@code sort} method in that the
     * {@code right} index is inclusive, and it does no range checking on
     * {@code left} or {@code right}.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted* @param keyOffset
     * @param comp structs comparator
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    static void doSort(OffHeapStructCollection a, long left, long right, OffHeapStructComparator comp,
                               byte[] pi, byte[] pj, byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Use insertion sort on tiny arrays
        if (right - left + 1 < INSERTION_SORT_THRESHOLD) {
            for (long i = left + 1; i <= right; i++) {
                a.get(i, pi);
                long j;
                for (j = i - 1; j >= left && comp.lt(pi, j); j--) {
                    a.get(j, pj);
                    a.set(j + 1, pj);
                }
                a.set(j + 1, pi);
            }
        } else { // Use Dual-Pivot Quicksort on large arrays
            dualPivotQuicksort(a, left, right, comp, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order by the
     * Dual-Pivot Quicksort algorithm using unsigned long struct key.
     *
     * @param a the off-heap header-payload collection to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param comp structs comparator
     * @param pi temporary buffer for structs
     * @param pj temporary buffer for structs
     * @param pe1 temporary buffer for structs
     * @param pe2 temporary buffer for structs
     * @param pe3 temporary buffer for structs
     * @param pe4 temporary buffer for structs
     * @param pe5 temporary buffer for structs
     */
    private static void dualPivotQuicksort(OffHeapStructCollection a, long left, long right, OffHeapStructComparator comp, byte[] pi, byte[] pj,
                                           byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
        // Compute indices of five evenly spaced elements
        long sixth = (right - left + 1) / 6;
        long e1 = left  + sixth;
        long e5 = right - sixth;
        long e3 = (left + right) >>> 1; // The midpoint
        long e4 = e3 + sixth;
        long e2 = e3 - sixth;

        // Sort these elements using a 5-element sorting network
        long ae1 = e1, ae2 = e2, ae3 = e3, ae4 = e4, ae5 = e5;
        a.get(e1, pe1); a.get(e2, pe2); a.get(e3, pe3); a.get(e4, pe4); a.get(e5, pe5);

        if (comp.gt(ae1, ae2)) { long t = ae1; byte[] pt = pe1; ae1 = ae2; pe1 = pe2; ae2 = t; pe2 = pt; }
        if (comp.gt(ae4, ae5)) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }
        if (comp.gt(ae1, ae3)) { long t = ae1; byte[] pt = pe1; ae1 = ae3; pe1 = pe3; ae3 = t; pe3 = pt; }
        if (comp.gt(ae2, ae3)) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (comp.gt(ae1, ae4)) { long t = ae1; byte[] pt = pe1; ae1 = ae4; pe1 = pe4; ae4 = t; pe4 = pt; }
        if (comp.gt(ae3, ae4)) { long t = ae3; byte[] pt = pe3; ae3 = ae4; pe3 = pe4; ae4 = t; pe4 = pt; }
        if (comp.gt(ae2, ae5)) { long t = ae2; byte[] pt = pe2; ae2 = ae5; pe2 = pe5; ae5 = t; pe5 = pt; }
        if (comp.gt(ae2, ae3)) { long t = ae2; byte[] pt = pe2; ae2 = ae3; pe2 = pe3; ae3 = t; pe3 = pt; }
        if (comp.gt(ae4, ae5)) { long t = ae4; byte[] pt = pe4; ae4 = ae5; pe4 = pe5; ae5 = t; pe5 = pt; }

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
        a.set(e2, pe1);
        a.get(right, pe1);
        a.set(e4, pe1);

        // Pointers
        long less  = left  + 1; // The index of first element of center part
        long great = right - 1; // The index before first element of right part

        // comparable results holders
        int kpivot1, greatpivot1;

        boolean pivotsDiffer = !comp.eq(pe2, pe4);

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
                a.get(k, pe1);
                if (comp.lt(k, pe2)) { // Move a[k] to left part
                    if (k != less) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.set(less, pe1);
                    }
                    less++;
                } else if (comp.gt(k, pe4)) { // Move a[k] to right part
                    while (comp.gt(great, pe4)) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (comp.lt(great, pe2)) {
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
                a.get(k, pe1);
                kpivot1 = comp.compare(k, pe2);
                if (0 == kpivot1) {
                    continue;
                }
                if (kpivot1 < 0) { // Move a[k] to left part
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
                    while ((greatpivot1 = comp.compare(great, pe2)) > 0) {
                        great--;
                    }
                    if (greatpivot1 < 0) {
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
        doSort(a, left,   less - 2, comp, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSort(a, great + 2, right, comp, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
            while (comp.eq(less,pe2)) {
                less++;
            }
            while (comp.eq(great, pe4)) {
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
                a.get(k, pe1);
                if (comp.eq(k, pe4)) { // Move a[k] to right part
                    while (comp.eq(great, pe4)) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (comp.eq(great, pe2)) {
                        a.get(less, pe3);
                        a.set(k, pe3);
                        a.get(great, pe3);
                        a.set(less++, pe3);
                    } else { // pivot1 < a[great] < pivot2
                        a.get(great, pe3);
                        a.set(k, pe3);
                    }
                    a.set(great--, pe1);
                } else if (comp.eq(k, pe2)) { // Move a[k] to left part
                    a.get(less, pe3);
                    a.set(k, pe3);
                    a.set(less++, pe1);
                }
            }
        }

        // Sort center part recursively, excluding known pivot values
        doSort(a, less, great, comp, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }

    /**
     * Helper method to invoke and wait
     *
     * @param executor executor instance
     * @param workers  workers list
     */
    private static void invokeAndWait(ExecutorService executor, List<Worker> workers) {
        try {
            List<Future<Void>> futures = executor.invokeAll(workers);
            for (Future<Void> fu : futures) {
                fu.get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Worker, sorts part of the collection
     */
    private static class Worker implements Callable<Void> {
        private final OffHeapStructCollection a;
        final long left;
        final long right;
        private final OffHeapStructComparator comp;
        private final byte[] pi;
        private final byte[] pj;
        private final byte[] pe1;
        private final byte[] pe2;
        private final byte[] pe3;
        private final byte[] pe4;
        private final byte[] pe5;

        /**
         * Private constructor
         *
         * @param a     collection
         * @param left  start sort index
         * @param right end sort index
         * @param comp  comparator
         * @param pi    sort buffer
         * @param pj    sort buffer
         * @param pe1   sort buffer
         * @param pe2   sort buffer
         * @param pe3   sort buffer
         * @param pe4   sort buffer
         * @param pe5   sort buffer
         */
        private Worker(OffHeapStructCollection a, long left, long right, OffHeapStructComparator comp, byte[] pi,
                       byte[] pj, byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
            this.a = a;
            this.left = left;
            this.right = right;
            this.comp = comp;
            this.pi = pi;
            this.pj = pj;
            this.pe1 = pe1;
            this.pe2 = pe2;
            this.pe3 = pe3;
            this.pe4 = pe4;
            this.pe5 = pe5;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Void call() throws Exception {
            doSort(a, left, right, comp, pi, pj, pe1, pe2, pe3, pe4, pe5);
            return null;
        }
    }

    /**
     * Merge-sort iterator over partly sorted collection
     */
    private static class MergeIter implements OffHeapDisposableIterator<byte[]> {
        private final OffHeapStructCollection col;
        private final OffHeapStructComparator comp;
        private final List<Worker> bounds;
        private final long[] indices;
        private final byte[] buf;


        /**
         * Private constructor
         *
         * @param col    partly sorted collection
         * @param comp   comparator used to partially sort collection
         * @param bounds collection sorted parts bounds
         */
        private MergeIter(OffHeapStructCollection col, OffHeapStructComparator comp, List<Worker> bounds) {
            this.col = col;
            this.comp = comp;
            this.bounds = bounds;
            this.buf = new byte[col.structLength()];
            this.indices = new long[bounds.size()];
            for (int i = 0; i < bounds.size(); i++) {
                indices[i] = bounds.get(i).left;
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] <= bounds.get(i).right) return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] next() {
            int minind = -1;
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] > bounds.get(i).right) continue;
                if (-1 == minind) minind = i;
                if (comp.gt(indices[minind], indices[i])) minind = i;
            }
            if (-1 == minind) throw new NoSuchElementException();
            col.get(indices[minind], buf);
            indices[minind] += 1;
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
            OffHeapUtils.free(col);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long size() {
            return col.size();
        }
    }
}
