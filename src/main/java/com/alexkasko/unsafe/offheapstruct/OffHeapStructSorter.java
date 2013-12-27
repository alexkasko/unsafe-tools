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

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static com.alexkasko.unsafe.offheap.OffHeapUtils.emptyDisposableIterator;

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
    static final int INSERTION_SORT_THRESHOLD = 32;

    /**
     * Sorts collection using comparator.
     *
     * @param collection off-heap struct collection
     * @param comparator structs comparator
     */
    public static void sort(OffHeapStructCollection collection, Comparator<OffHeapStructAccessor> comparator) {
        OffHeapStructSorterWithComparator.sort(collection, comparator);
    }

    /**
     * Sorts collection using comparator.
     *
     * @param collection off-heap struct collection
     * @param comparator structs comparator
     * @param fromIndex start sort collection index
     * @param toIndex end sort collection index
     */
    public static void sort(OffHeapStructCollection collection, Comparator<OffHeapStructAccessor> comparator, long fromIndex, long toIndex) {
        if(fromIndex == toIndex) return; // nothing to sort here
        OffHeapStructSorterWithComparator.sort(collection, fromIndex, toIndex, comparator);
    }

    /**
     * Partially sorts collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threadsCount number of worker threads to use
     * @param collection the off-heap struct collection to be sorted
     * @param comparator structs comparator
     * @return sorted iterator over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    public static OffHeapDisposableIterator<byte[]> sortedIterator(ExecutorService executor, int threadsCount, OffHeapStructCollection collection,
                                                  Comparator<OffHeapStructAccessor> comparator) {
        return OffHeapStructSorterWithComparator.sortedIterator(executor, threadsCount, collection, comparator);
    }

    /**
     * Partially sorts part of the collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threadsCount number of worker threads to use
     * @param collection the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param comparator structs comparator
     * @return sorted iterator over the collection part
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    public static OffHeapDisposableIterator<byte[]> sortedIterator(ExecutorService executor, int threadsCount, OffHeapStructCollection collection,
                                                  Comparator<OffHeapStructAccessor> comparator, long fromIndex, long toIndex) {
        if(fromIndex == toIndex) return emptyDisposableIterator(); // nothing to sort here
        return OffHeapStructSorterWithComparator.sortedIterator(executor, threadsCount, collection, fromIndex, toIndex, comparator);
    }

    /**
     * Partially sorts collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threadsCount number of worker threads to use
     * @param collection the off-heap struct collection to be sorted
     * @param keyOffset key offset
     * @return sorted iterator over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    public static OffHeapDisposableIterator<byte[]> sortedIteratorByLongKey(ExecutorService executor, int threadsCount, OffHeapStructCollection collection,
                                                  int keyOffset) {
        return OffHeapStructSorterLong.sortedIterator(executor, threadsCount, collection, keyOffset);
    }

    /**
     * Partially sorts part of the collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threadsCount number of worker threads to use
     * @param collection the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param keyOffset key offset
     * @return sorted iterator over the collection part
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    public static OffHeapDisposableIterator<byte[]> sortedIteratorByLongKey(ExecutorService executor, int threadsCount, OffHeapStructCollection collection,
                                                           int keyOffset, long fromIndex, long toIndex) {
        if(fromIndex == toIndex) return emptyDisposableIterator(); // nothing to sort here
        return OffHeapStructSorterLong.sortedIterator(executor, threadsCount, collection, fromIndex, toIndex, keyOffset);
    }

    /**
     * Partially sorts collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threadsCount number of worker threads to use
     * @param collection the off-heap struct collection to be sorted
     * @param keyOffset key offset
     * @return sorted iterator over the collection
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    public static OffHeapDisposableIterator<byte[]> sortedIteratorByIntKey(ExecutorService executor, int threadsCount, OffHeapStructCollection collection,
                                                  int keyOffset) {
        return OffHeapStructSorterInt.sortedIterator(executor, threadsCount, collection, keyOffset);
    }

    /**
     * Partially sorts part of the collection and returns fully sorted iterator over it
     *
     * @param executor executor service for parallel sorting
     * @param threadsCount number of worker threads to use
     * @param collection the off-heap struct collection to be sorted
     * @param fromIndex the index of the first element, inclusive, to be sorted
     * @param toIndex the index of the last element, exclusive, to be sorted
     * @param keyOffset key offset
     * @return sorted iterator over the collection part
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     * @throws RuntimeException on worker thread error
     */
    public static OffHeapDisposableIterator<byte[]> sortedIteratorByIntKey(ExecutorService executor, int threadsCount, OffHeapStructCollection collection,
                                                           int keyOffset, long fromIndex, long toIndex) {
        if(fromIndex == toIndex) return emptyDisposableIterator(); // nothing to sort here
        return OffHeapStructSorterInt.sortedIterator(executor, threadsCount, collection, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using long struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset long key field offset within stuct bounds
     */
    public static void sortByLongKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterLong.sort(a, keyOffset);
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
        OffHeapStructSorterLong.sort(a, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts collection by two long keys. Second key sorting is done in parallel.
     *
     * @param executor   executor for parallel sorting
     * @param threads    number of worker threads to use
     * @param a          the off-heap struct collection to be sorted
     * @param key1Offset first key offset
     * @param key2Offset second key offset
     */
    public static void sortByLongKeysParallel(Executor executor, int threads, OffHeapStructCollection a, int key1Offset, int key2Offset) {
        OffHeapStructSorterLong.sortTwoKeysParallel(executor, threads, a, key1Offset, key2Offset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using unsigned long struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset long key field offset within stuct bounds
     */
    public static void sortByUnsignedLongKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterUnsignedLong.sort(a, keyOffset);
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
     * @param keyOffset long sort key field offset within stuct bounds
     * @throws IllegalArgumentException {@code if (fromIndex < 0 || fromIndex > toIndex || toIndex > a.size())}
     */
    public static void sortByUnsignedLongKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        OffHeapStructSorterUnsignedLong.sort(a, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using int struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset int key field offset within stuct bounds
     */
    public static void sortByIntKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterInt.sort(a, keyOffset);
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
        OffHeapStructSorterInt.sort(a, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using unsigned int struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset int key field offset within stuct bounds
     */
    public static void sortByUnsignedIntKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterUnsignedInt.sort(a, keyOffset);
    }

    /**
     * Sorts the specified range of the off-heap struct collection into ascending order using unsigned int struct key. The range
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
    public static void sortByUnsignedIntKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        OffHeapStructSorterUnsignedInt.sort(a, fromIndex, toIndex, keyOffset);
    }
}
