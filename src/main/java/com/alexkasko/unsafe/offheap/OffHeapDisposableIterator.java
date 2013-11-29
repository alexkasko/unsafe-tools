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

import java.util.Iterator;

/**
 * User: alexkasko
 * Date: 11/29/13
 */
public interface OffHeapDisposableIterator<T> extends Iterator<T>, OffHeapDisposable {
    /**
     * Optional method, should return size of the underlying collections,
     * if such size is known on iterator creation or {@code -1} otherwise
     *
     * @return number of elements in iterator
     */
    long size();
}
