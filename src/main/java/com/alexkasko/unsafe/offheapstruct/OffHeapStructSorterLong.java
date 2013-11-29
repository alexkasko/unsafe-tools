package com.alexkasko.unsafe.offheapstruct;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.alexkasko.unsafe.offheapstruct.OffHeapStructSorter.INSERTION_SORT_THRESHOLD;

/**
 * <p>alexkasko: borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java}
 * and adapted to {@link OffHeapStructCollection} with signed long keys.
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
class OffHeapStructSorterLong {

    /**
     * Partially sorts collection and returns fully sorted iterator over it
     *
     * @param executor   executor service for parallel sorting
     * @param threads    number of worker threads to use
     * @param a          the off-heap struct collection to be sorted
     * @param keyOffset key offset
     * @return sorted iterator over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException         on worker thread error
     */
    static Iterator<byte[]> sortedIterator(ExecutorService executor, int threads, OffHeapStructCollection a, int keyOffset) {
        return sortedIterator(executor, threads, a, 0, a.size(), keyOffset);
    }

    /**
     * Partially sorts part of the collection and returns fully sorted iterator over it
     *
     * @param executor   executor service for parallel sorting
     * @param threads    number of worker threads to use
     * @param a          the off-heap struct collection to be sorted
     * @param fromIndex  the index of the first element, inclusive, to be sorted
     * @param toIndex    the index of the last element, exclusive, to be sorted
     * @param keyOffset key offset
     * @return sorted iterator over the collection part
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException         on worker thread error
     */
    static Iterator<byte[]> sortedIterator(ExecutorService executor, int threads, OffHeapStructCollection a, long fromIndex,
                                           long toIndex, int keyOffset) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        long step = (toIndex - 1 - fromIndex) / threads;
        if (0 == step) {
            doSort(a, fromIndex, toIndex - 1, keyOffset, new byte[len], new byte[len],
                    new byte[len], new byte[len], new byte[len], new byte[len], new byte[len]);
            return a.iterator();
        } else {
            List<Worker> workers = new ArrayList<Worker>();
            for (int start = 0; start < toIndex; start += step) {
                Worker worker = new Worker(a, start, Math.min(start + step - 1, toIndex - 1), keyOffset,
                        new byte[len], new byte[len], new byte[len], new byte[len], new byte[len], new byte[len], new byte[len]);
                workers.add(worker);
            }
            Worker.invokeAndWait(executor, workers);
            return new MergeIter(a, keyOffset, workers);
        }
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using signed long struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset long key field offset within stuct bounds
     */
    static void sort(OffHeapStructCollection a, int keyOffset) {
        sort(a, 0, a.size(), keyOffset);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using signed long struct key.
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
    static void sort(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + a.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        int len = a.structLength();
        doSort(a, fromIndex, toIndex - 1, keyOffset, new byte[len], new byte[len], new byte[len], new byte[len], new byte[len],
                new byte[len], new byte[len]);
    }


    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using signed long struct key.
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
    private static void doSort(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
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
            dualPivotQuicksort(a, left, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        }
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order by the
     * Dual-Pivot Quicksort algorithm using signed long struct key.
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
    private static void dualPivotQuicksort(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi, byte[] pj,
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
        doSort(a, left,   less - 2, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
        doSort(a, great + 2, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);

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
        doSort(a, less, great, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
    }

    private static class Worker implements Callable<Void> {
        private final OffHeapStructCollection a;
        final long left;
        final long right;
        private final int keyOffset;
        private final byte[] pi;
        private final byte[] pj;
        private final byte[] pe1;
        private final byte[] pe2;
        private final byte[] pe3;
        private final byte[] pe4;
        private final byte[] pe5;

        private Worker(OffHeapStructCollection a, long left, long right, int keyOffset, byte[] pi,
                       byte[] pj, byte[] pe1, byte[] pe2, byte[] pe3, byte[] pe4, byte[] pe5) {
            this.a = a;
            this.left = left;
            this.right = right;
            this.keyOffset = keyOffset;
            this.pi = pi;
            this.pj = pj;
            this.pe1 = pe1;
            this.pe2 = pe2;
            this.pe3 = pe3;
            this.pe4 = pe4;
            this.pe5 = pe5;
        }

        @Override
        public Void call() throws Exception {
            doSort(a, left, right, keyOffset, pi, pj, pe1, pe2, pe3, pe4, pe5);
            return null;
        }

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
    }

    private static class MergeIter implements Iterator<byte[]> {
        private final OffHeapStructCollection col;
        private final int keyOffset;
        private final List<Worker> bounds;
        private final long[] indices;
        private final byte[] buf;


        private MergeIter(OffHeapStructCollection col, int keyOffset, List<Worker> bounds) {
            this.col = col;
            this.keyOffset = keyOffset;
            this.bounds = bounds;
            this.buf = new byte[col.structLength()];
            this.indices = new long[bounds.size()];
            for (int i = 0; i < bounds.size(); i++) {
                indices[i] = bounds.get(i).left;
            }
        }

        @Override
        public boolean hasNext() {
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] <= bounds.get(i).right) return true;
            }
            return false;
        }

        @Override
        public byte[] next() {
            int minind = -1;
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] > bounds.get(i).right) continue;
                if (-1 == minind) minind = i;
                if (col.getLong(indices[minind], keyOffset) > col.getLong(indices[i], keyOffset)) minind = i;
            }
            if (-1 == minind) throw new NoSuchElementException();
            col.get(indices[minind], buf);
            indices[minind] += 1;
            return buf;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
