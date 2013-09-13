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

import com.alexkasko.unsafe.bytearray.ByteArrayTool;

import java.util.Comparator;

/**
 * Internal wrapper for user-provided comparator
 *
 * @author alexkasko
 *         Date: 9/13/13
 */
class OffHeapStructComparator {
    private final OffHeapStructIndexAccessor ia1;
    private final OffHeapStructIndexAccessor ia2;
    private final OffHeapStructByteArrayAccessor baa1;
    private final OffHeapStructByteArrayAccessor baa2;
    private final Comparator<OffHeapStructAccessor> comp;

    /**
     * Constructor
     *
     * @param col collection for index access
     * @param comp comparator
     */
    OffHeapStructComparator(OffHeapStructCollection col, Comparator<OffHeapStructAccessor> comp) {
        ByteArrayTool bt = ByteArrayTool.get();
        this.ia1 = new OffHeapStructIndexAccessor(col);
        this.ia2 = new OffHeapStructIndexAccessor(col);
        this.baa1 = new OffHeapStructByteArrayAccessor(bt);
        this.baa2 = new OffHeapStructByteArrayAccessor(bt);
        this.comp = comp;
    }

    /**
     * Greater-then operation
     *
     * @param index1 collection index
     * @param index2 collection index
     * @return 'gt' operation result
     */
    boolean gt(long index1, long index2) {
        ia1.setIndex(index1);
        ia2.setIndex(index2);
        return comp.compare(ia1, ia2) > 0;
    }

    /**
     * Greater-then operation
     *
     * @param index1 collection index
     * @param struct2 structure
     * @return 'gt' operation result
     */
    boolean gt(long index1, byte[] struct2) {
        ia1.setIndex(index1);
        baa2.setStruct(struct2);
        return comp.compare(ia1, baa2) > 0;
    }

    /**
     * Lesser-then operation
     *
     * @param index1 collection index
     * @param struct2 structure
     * @return 'lt' operation result
     */
    boolean lt(long index1, byte[] struct2) {
        ia1.setIndex(index1);
        baa2.setStruct(struct2);
        return comp.compare(ia1, baa2) < 0;
    }

    /**
     * Lesser-then operation
     *
     * @param struct1 structure
     * @param index2 collection index
     * @return 'lt' operation result
     */
    boolean lt(byte[] struct1, long index2) {
        baa1.setStruct(struct1);
        ia2.setIndex(index2);
        return comp.compare(baa1, ia2) < 0;
    }

    /**
     * Equals operation
     *
     * @param index1 collection index
     * @param struct2 structure
     * @return 'eq' operation result
     */
    boolean eq(long index1, byte[] struct2) {
        ia1.setIndex(index1);
        baa2.setStruct(struct2);
        return 0 == comp.compare(ia1, baa2);
    }

    /**
     * Equals operation
     *
     * @param struct1 structure
     * @param struct2 structure
     * @return 'eq' operation result
     */
    boolean eq(byte[] struct1, byte[] struct2) {
        baa1.setStruct(struct1);
        baa2.setStruct(struct2);
        return 0 == comp.compare(baa1, baa2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapStructComparator");
        sb.append("{comp=").append(comp);
        sb.append('}');
        return sb.toString();
    }
}
