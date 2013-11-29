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

package com.alexkasko.unsafe.offheap;

import java.util.NoSuchElementException;

/**
 * Static utility methods for off-heap classes
 *
 * @author alexkasko
 * Date: 3/5/13
 */
public class OffHeapUtils {
    /**
     * Utility method for usage in {@code finally} clauses.
     * Calls {@link com.alexkasko.unsafe.offheap.OffHeapDisposable#free()}
     * on provided instance only if instance is not null.
     *
     * @param disposable off-heap disposable instance, may be null
     */
    public static void free(OffHeapDisposable disposable) {
        if(null == disposable) return;
        disposable.free();
    }

    /**
     * Utility method for usage in {@code finally} clauses.
     * Calls {@link com.alexkasko.unsafe.offheap.OffHeapDisposable#free()}
     * on all provided instances only if instance is not null.
     *
     * @param disposables one or more off-heap disposable
     */
    public static void freeAll(OffHeapDisposable... disposables) {
        for(OffHeapDisposable di : disposables) {
            free(di);
        }
    }

    /**
     * Returns empty disposable iterator
     *
     * @param <T> generic param
     * @return empty disposable iterator
     */
    public static <T> OffHeapDisposableIterator<T> emptyDisposableIterator() {
        return new EmptyDisposableIterator<T>();
    }

    private static class EmptyDisposableIterator<T> implements OffHeapDisposableIterator<T> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void free() {
            // NOOP
        }
    }
}
