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

import java.io.Serializable;

/**
 * Struct sort key class to use for sorting with multiple keys
 *
 * @author alexkasko
 * Date: 7/8/13
 */
public abstract class OffHeapStructSortKey implements Serializable {
    private static final long serialVersionUID = 6811864689863999749L;

    private final int offset;

    /**
     * Protected constructor for inheritors.
     *
     * @param offset key field offset in struct
     */
    protected OffHeapStructSortKey(int offset) {
        if(offset < 0) throw new IllegalArgumentException("Specified offset: [" + offset + "] must not be negative");
        this.offset = offset;
    }

    /**
     * Key type id for internal use
     *
     * @return key type id
     */
    abstract byte typeId();

    /**
     * Factory method for struct sort key implementation
     * that uses signed byte comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey byteSortKey(int offset) {
        return new ByteKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses unsigned byte comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey unsignedByteSortKey(int offset) {
        return new UnsignedByteKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses signed short comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey shortSortKey(int offset) {
        return new ShortKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses unsigned short comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey unsignedShortSortKey(int offset) {
        return new UnsignedShortKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses signed int comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey intSortKey(int offset) {
        return new IntKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses unsigned int comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey unsignedIntSortKey(int offset) {
        return new UnsignedIntKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses signed long comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey longSortKey(int offset) {
        return new LongKey(offset);
    }

    /**
     * Factory method for struct sort key implementation
     * that uses unsigned long comparison on specified struct offset
     *
     * @param offset struct key offset
     * @return sort key instance
     */
    public static OffHeapStructSortKey unsignedLongSortKey(int offset) {
        return new UnsignedLongKey(offset);
    }

    /**
     * Offset accessor
     *
     * @return key offset
     */
    public int offset() {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffHeapStructSortKey)) return false;
        OffHeapStructSortKey that = (OffHeapStructSortKey) o;
        if (offset != that.offset) return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("{offset=").append(offset);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Struct sort key implementation, uses signed byte comparison on specified struct offset
     */
    public static class ByteKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -5201378839882085844L;
        static final byte ID = 1;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public ByteKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses unsigned byte comparison on specified struct offset
     */
    public static class UnsignedByteKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 11;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public UnsignedByteKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses signed short comparison on specified struct offset
     */
    public static class ShortKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 2;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public ShortKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses unsigned short comparison on specified struct offset
     */
    public static class UnsignedShortKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 12;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public UnsignedShortKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses signed int comparison on specified struct offset
     */
    public static class IntKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 4;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public IntKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses unsigned int comparison on specified struct offset
     */
    public static class UnsignedIntKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 14;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public UnsignedIntKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses signed long comparison on specified struct offset
     */
    public static class LongKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 8;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public LongKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }

    /**
     * Struct sort key implementation, uses unsigned long comparison on specified struct offset
     */
    public static class UnsignedLongKey extends OffHeapStructSortKey {
        private static final long serialVersionUID = -4704362633057709836L;
        static final byte ID = 18;

        /**
         * Constructor
         *
         * @param offset key field offset in struct
         */
        public UnsignedLongKey(int offset) {
            super(offset);
        }

        /**
         * {@inheritDoc}
         */
        byte typeId() {
            return ID;
        }
    }
}
