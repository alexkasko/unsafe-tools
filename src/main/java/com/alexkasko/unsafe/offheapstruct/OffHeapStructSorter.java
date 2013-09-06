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

import static com.alexkasko.unsafe.offheapstruct.OffHeapStructSortKey.*;

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
        sort(collection, keys, 0, collection.size());
    }

    /**
     * Sorts collection using multiple keys. Every subsequent keys is used on
     * collections interval where previous key has equal values
     *
     * @param collection off-heap struct collection
     * @param keys keys list
     * @param fromIndex start sort collection index
     * @param toIndex end sort collection index
     */
    public static void sort(OffHeapStructCollection collection, List<OffHeapStructSortKey> keys, long fromIndex, long toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "fromIndex: [" + fromIndex + "], toIndex: [" + toIndex + "]");
        }
        if(fromIndex == toIndex) return; // nothing to sort here
        multisortInternal(collection, fromIndex, toIndex, keys, 0);
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

    private static void sortByKey(OffHeapStructCollection col, long start, long end, OffHeapStructSortKey key) {
        switch (key.typeId()) {
            case LongKey.ID: sortByLongKey(col, start, end, key.offset()); break;
            case UnsignedLongKey.ID: sortByUnsignedLongKey(col, start, end, key.offset()); break;
            case IntKey.ID: sortByIntKey(col, start, end, key.offset()); break;
            case UnsignedIntKey.ID: sortByUnsignedIntKey(col, start, end, key.offset()); break;
            case ShortKey.ID: sortByShortKey(col, start, end, key.offset()); break;
            case UnsignedShortKey.ID: sortByUnsignedShortKey(col, start, end, key.offset()); break;
            case ByteKey.ID: sortByByteKey(col, start, end, key.offset()); break;
            case UnsignedByteKey.ID: sortByUnsignedByteKey(col, start, end, key.offset()); break;
            default: throw new IllegalArgumentException(key.toString());
        }
    }

    private static long getByKey(OffHeapStructCollection col, long index, OffHeapStructSortKey key) {
        switch (key.typeId()) {
            case LongKey.ID: return col.getLong(index, key.offset());
            case UnsignedLongKey.ID: return col.getLong(index, key.offset());
            case IntKey.ID: return col.getInt(index, key.offset());
            case UnsignedIntKey.ID: return col.getUnsignedInt(index, key.offset());
            case ShortKey.ID: return col.getShort(index, key.offset());
            case UnsignedShortKey.ID: return col.getUnsignedShort(index, key.offset());
            case ByteKey.ID: return col.getByte(index, key.offset());
            case UnsignedByteKey.ID: return col.getUnsignedByte(index, key.offset());
            default: throw new IllegalArgumentException(key.toString());
        }
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

    /**
     * Sorts the specified off-heap struct collection into ascending order using short struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset short key field offset within stuct bounds
     */
    public static void sortByShortKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterShort.sort(a, keyOffset);
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
        OffHeapStructSorterShort.sort(a, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using short struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset short key field offset within stuct bounds
     */
    public static void sortByUnsignedShortKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterUnsignedShort.sort(a, keyOffset);
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
    public static void sortByUnsignedShortKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        OffHeapStructSorterUnsignedShort.sort(a, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using byte struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset byte key field offset within stuct bounds
     */
    public static void sortByByteKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterByte.sort(a, keyOffset);
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
        OffHeapStructSorterByte.sort(a, fromIndex, toIndex, keyOffset);
    }

    /**
     * Sorts the specified off-heap struct collection into ascending order using byte struct key.
     *
     * @param a the off-heap struct collection to be sorted
     * @param keyOffset byte key field offset within stuct bounds
     */
    public static void sortByUnsignedByteKey(OffHeapStructCollection a, int keyOffset) {
        OffHeapStructSorterUnsignedByte.sort(a, keyOffset);
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
    public static void sortByUnsignedByteKey(OffHeapStructCollection a, long fromIndex, long toIndex, int keyOffset) {
        OffHeapStructSorterUnsignedByte.sort(a, fromIndex, toIndex, keyOffset);
    }
}
