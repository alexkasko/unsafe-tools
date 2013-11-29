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

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.alexkasko.unsafe.offheapstruct.OffHeapStructSorterWithComparator.doSort;

/**
 * Naive parallel quicksort implementation on top of {@link OffHeapStructSorterWithComparator}.
 * Collections is divided in equal parts, each part being sorted separately using user-specified {@link ExecutorService}
 * and sorted {@link Iterator} is opened over the collection and returned to client.
 * Note: collection will be left in partially sorted state after iterator opening.
 */
class OffHeapStructParallelSorterWithComparator {

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

    private static void invokeAndWait(ExecutorService executor, List<Worker> workers) {
        try {
            List<Future<Void>> futures = executor.invokeAll(workers);
            for(Future<Void> fu : futures) {
                fu.get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

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

        @Override
        public Void call() throws Exception {
            doSort(a, left, right, comp, pi, pj, pe1, pe2, pe3, pe4, pe5);
            return null;
        }
    }

    private static class MergeIter implements OffHeapDisposableIterator<byte[]> {
        private final OffHeapStructCollection col;
        private final OffHeapStructComparator comp;
        private final List<Worker> bounds;
        private final long[] indices;
        private final byte[] buf;


        private MergeIter(OffHeapStructCollection col, OffHeapStructComparator comp, List<Worker> bounds) {
            this.col = col;
            this.comp = comp;
            this.bounds = bounds;
            this.buf = new byte[col.structLength()];
            this.indices = new long[bounds.size()];
            for(int i = 0; i < bounds.size(); i++) {
                indices[i] = bounds.get(i).left;
            }

        }

        @Override
        public boolean hasNext() {
            for (int i = 0; i < indices.length; i++) {
                if(indices[i] <= bounds.get(i).right) return true;
            }
            return false;
        }

        @Override
        public byte[] next() {
            int minind = -1;
            for (int i = 0; i < indices.length; i++) {
                if(indices[i] > bounds.get(i).right) continue;
                if(-1 == minind) minind = i;
                if(comp.gt(indices[minind], indices[i])) minind = i;
            }
            if(-1 == minind) throw new NoSuchElementException();
            col.get(indices[minind], buf);
            indices[minind] += 1;
            return buf;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void free() {
            OffHeapUtils.free(col);
        }
    }
}
