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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper container for struct sort key. Contains key offset and type:
 * {@code long.class}, {@code int.class}, {@code short.class} or {@code byte.class}
 *
 * @author alexkasko
 * Date: 7/8/13
 */
public class OffHeapStructSortKey implements Serializable {
    private static final long serialVersionUID = 6811864689863999749L;
    private static final Map<Class<? extends Number>, Integer> KEY_TYPES;

    static {
        KEY_TYPES = new LinkedHashMap<Class<? extends Number>, Integer>();
        KEY_TYPES.put(long.class, 8);
        KEY_TYPES.put(int.class, 4);
        KEY_TYPES.put(short.class, 2);
        KEY_TYPES.put(byte.class, 1);
    }

    private final int offset;
    private final int len;

    /**
     * Constructor. Allowed key types are:
     * {@code long.class}, {@code int.class}, {@code short.class} or {@code byte.class}
     *
     * @param offset key offset
     * @param type key type
     */
    public OffHeapStructSortKey(int offset, Class<? extends Number> type) {
        if(null == type) throw new IllegalArgumentException("Specified type is null");
        if(!KEY_TYPES.containsKey(type)) throw new IllegalArgumentException(
                "Unsupported type, supported types are: [" + KEY_TYPES.keySet() + "]");
        this.offset = offset;
        this.len = KEY_TYPES.get(type);
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
     * Type length accessor
     *
     * @return key type length in bytes
     */
    public int length() {
        return len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffHeapStructSortKey that = (OffHeapStructSortKey) o;
        if (len != that.len) return false;
        if (offset != that.offset) return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + len;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OffHeapStructSortCondition");
        sb.append("{offset=").append(offset);
        sb.append(", len=").append(len);
        sb.append('}');
        return sb.toString();
    }
}
