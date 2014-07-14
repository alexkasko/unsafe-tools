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
 * Internal wrapper for user-provided comparator.
 * NOT thread-safe.
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
     * Calls {@code compare} on comparator and return it results
     *
     * @param index1 collection index
     * @param struct2 structure
     * @return compare result from comparator
     */
    int compare(long index1, byte[] struct2) {
        ia1.setIndex(index1);
        baa2.setStruct(struct2);
        return comp.compare(ia1, baa2);
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

    /**
     * Struct's accessor implementation for byte array structs
     *
     * @author alexkasko
     * Date: 9/13/13
     */
    private static class OffHeapStructByteArrayAccessor implements OffHeapStructAccessor {
        private final ByteArrayTool bt;
        private byte[] struct;

        /**
         * Constructor
         *
         * @param bt byte array tool to operate over held byte array
         */
        private OffHeapStructByteArrayAccessor(ByteArrayTool bt) {
            this.bt = bt;
        }

        /**
         * Constructor
         *
         * @param bt byte array tool to operate over held byte array
         * @param struct byte array to hold
         */
        private OffHeapStructByteArrayAccessor(ByteArrayTool bt, byte[] struct) {
            this.bt = bt;
            this.struct = struct;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int structLength() {
            return struct.length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void get(byte[] buffer) {
            bt.copy(struct, 0, buffer, 0, struct.length);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void get(int srcPos, byte[] dest, int destPos, int length) {
            bt.copy(struct, srcPos, dest, destPos, length);
        }

        /**
         * Copies specified buffer data into internal buffer
         *
         * @param buffer data to copy
         */
        public void set(byte[] buffer) {
            bt.copy(buffer, 0, struct, 0, struct.length);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte getByte(int offset) {
            return bt.getByte(struct, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short getUnsignedByte(int offset) {
            return bt.getUnsignedByte(struct, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short getShort(int offset) {
            return bt.getShort(struct, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getUnsignedShort(int offset) {
            return bt.getUnsignedShort(struct, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getInt(int offset) {
            return bt.getInt(struct, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getUnsignedInt(int offset) {
            return bt.getUnsignedInt(struct, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getLong(int offset) {
            return bt.getLong(struct, offset);
        }

        /**
         * Returns previously setted struct
         *
         * @return struct
         */
        private byte[] getStruct() {
            return struct;
        }

        /**
         * Sets struct to access it
         *
         * @param struct struct
         */
        private void setStruct(byte[] struct) {
            this.struct = struct;
        }

        /**
         * {@inheritDoc}
         *
         * @return
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("OffHeapStructByteArrayAccessor");
            sb.append("{bt=").append(bt);
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Struct's accessor implementation for off-heap stored structs
     *
     * @author alexkasko
     * Date: 9/13/13
     */
    private static class OffHeapStructIndexAccessor implements OffHeapStructAccessor {
        private final OffHeapStructCollection col;
        private long index = -1;

        /**
         * Constructor
         *
         * @param col collection to access structs from
         */
        private OffHeapStructIndexAccessor(OffHeapStructCollection col) {
            this.col = col;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int structLength() {
            return col.structLength();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void get(byte[] buffer) {
            col.get(index, buffer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void get(int srcPos, byte[] dest, int destPos, int length) {
            col.get(index, srcPos, dest, destPos, length);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte getByte(int offset) {
            return col.getByte(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short getUnsignedByte(int offset) {
            return col.getUnsignedByte(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short getShort(int offset) {
            return col.getShort(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getUnsignedShort(int offset) {
            return col.getUnsignedShort(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getInt(int offset) {
            return col.getInt(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getUnsignedInt(int offset) {
            return col.getUnsignedInt(index, offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getLong(int offset) {
            return col.getLong(index, offset);
        }

        /**
         * Sets index value
         *
         * @param index index value
         */
        private void setIndex(long index) {
            this.index = index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("OffHeapStructIndexAccessor");
            sb.append("{col=").append(col);
            sb.append(", index=").append(index);
            sb.append('}');
            return sb.toString();
        }
    }
}
