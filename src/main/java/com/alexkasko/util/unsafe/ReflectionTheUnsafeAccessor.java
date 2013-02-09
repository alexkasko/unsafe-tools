package com.alexkasko.util.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: alexkasko
 * Date: 1/16/13
 */
class ReflectionTheUnsafeAccessor extends TheUnsafeAccessor {
    private static final Object UNSAFE;

    private static final Method copyMemory2;
    private static final Method copyMemory;
    private static final Method allocateMemory;
    private static final Method reallocateMemory;
    private static final Method setMemory;
    private static final Method freeMemory;
    private static final Method arrayBaseOffset;
    private static final Method arrayIndexScale;
    private static final Method getInt2;
    private static final Method putInt2;
    private static final Method getByte2;
    private static final Method putByte2;
    private static final Method getShort2;
    private static final Method putShort2;
    private static final Method getLong2;
    private static final Method putLong2;
    private static final Method getByte;
    private static final Method putByte;
    private static final Method getInt;
    private static final Method putInt;
    private static final Method getLong;
    private static final Method putLong;
    private static final Method getIntVolatile;
    private static final Method putIntVolatile;
    private static final Method getByteVolatile;
    private static final Method putByteVolatile;
    private static final Method getShortVolatile;
    private static final Method putShortVolatile;
    private static final Method getLongVolatile;
    private static final Method putLongVolatile;
    private static final Method putOrderedInt;
    private static final Method putOrderedLong;

    static {
        try {
            Class<?> uc = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = uc.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = theUnsafe.get(null);
            copyMemory2 = uc.getMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
            copyMemory = uc.getMethod("copyMemory", long.class, long.class, long.class);
            allocateMemory = uc.getMethod("allocateMemory", long.class);
            reallocateMemory = uc.getMethod("reallocateMemory", long.class, long.class);
            setMemory = uc.getMethod("setMemory", long.class, long.class, byte.class);
            freeMemory = uc.getMethod("freeMemory", long.class);
            arrayBaseOffset = uc.getMethod("arrayBaseOffset", Class.class);
            arrayIndexScale = uc.getMethod("arrayIndexScale", Class.class);
            getInt2 = uc.getMethod("getInt", Object.class, long.class);
            putInt2 = uc.getMethod("putInt", Object.class, long.class, int.class);
            getByte2 = uc.getMethod("getByte", Object.class, long.class);
            putByte2 = uc.getMethod("putByte", Object.class, long.class, byte.class);
            getShort2 = uc.getMethod("getShort", Object.class, long.class);
            putShort2 = uc.getMethod("putShort", Object.class, long.class, short.class);
            getLong2 = uc.getMethod("getLong", Object.class, long.class);
            putLong2 = uc.getMethod("putLong", Object.class, long.class, long.class);
            getByte = uc.getMethod("getByte", long.class);
            putByte = uc.getMethod("putByte", long.class, byte.class);
            getInt = uc.getMethod("getInt", long.class);
            putInt = uc.getMethod("putInt", long.class, int.class);
            getLong = uc.getMethod("getLong", long.class);
            putLong = uc.getMethod("putLong", long.class, long.class);
            getIntVolatile = uc.getMethod("getIntVolatile", Object.class, long.class);
            putIntVolatile = uc.getMethod("putIntVolatile", Object.class, long.class, int.class);
            getByteVolatile = uc.getMethod("getByteVolatile", Object.class, long.class);
            putByteVolatile = uc.getMethod("putByteVolatile", Object.class, long.class, byte.class);
            getShortVolatile = uc.getMethod("getShortVolatile", Object.class, long.class);
            putShortVolatile = uc.getMethod("putByteVolatile", Object.class, long.class, short.class);
            getLongVolatile = uc.getMethod("getLongVolatile", Object.class, long.class);
            putLongVolatile = uc.getMethod("putByteVolatile", Object.class, long.class, short.class);
            putOrderedInt = uc.getMethod("putOrderedInt", Object.class, long.class, int.class);
            putOrderedLong = uc.getMethod("putOrderedLong", Object.class, long.class, long.class);

            int boo = (Integer) arrayBaseOffset.invoke(UNSAFE, byte[].class);
            // It seems not all Unsafe implementations implement the following method.
            copyMemory2.invoke(UNSAFE, new byte[1], boo, new byte[1], boo, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        try {
            copyMemory2.invoke(UNSAFE, srcBase, srcOffset, destBase, destOffset, bytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void copyMemory(long srcAddress, long destAddress, long bytes) {
        try {
            copyMemory.invoke(UNSAFE, srcAddress, destAddress, bytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long allocateMemory(long bytes) {
        try {
            return (Long) allocateMemory.invoke(UNSAFE, bytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long reallocateMemory(long address, long bytes) {
        try {
            return (Long) reallocateMemory.invoke(UNSAFE, address, bytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setMemory(long address, long bytes, byte value) {
        try {
            setMemory.invoke(UNSAFE, address, bytes, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void freeMemory(long address) {
        try {
            freeMemory.invoke(UNSAFE, address);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int arrayBaseOffset(Class arrayClass) {
        try {
            return (Integer) arrayBaseOffset.invoke(UNSAFE, arrayClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int arrayIndexScale(Class arrayClass) {
        try {
            return (Integer) arrayIndexScale.invoke(UNSAFE, arrayClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getInt(Object o, long offset) {
        try {
            return (Integer) getInt2.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putInt(Object o, long offset, int x) {
        try {
            putInt2.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getByte(Object o, long offset) {
        try {
            return (Byte) getByte2.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putByte(Object o, long offset, byte x) {
        try {
            putByte2.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    short getShort(Object o, long offset) {
        try {
            return (Short) getShort2.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putShort(Object o, long offset, short x) {
        try {
            putShort2.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getLong(Object o, long offset) {
        try {
            return (Long) getLong2.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putLong(Object o, long offset, long x) {
        try {
            putLong2.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getByte(long address) {
        try {
            return (Byte) getByte.invoke(UNSAFE, address);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putByte(long address, byte x) {
        try {
            putByte.invoke(UNSAFE, address, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getInt(long address) {
        try {
            return (Integer) getInt.invoke(UNSAFE, address);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putInt(long address, int x) {
        try {
            putInt.invoke(UNSAFE, address, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getLong(long address) {
        try {
            return (Long) getLong.invoke(UNSAFE, address);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putLong(long address, long x) {
        try {
            putLong.invoke(UNSAFE, address, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getIntVolatile(Object o, long offset) {
        try {
            return (Integer) getIntVolatile.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putIntVolatile(Object o, long offset, int x) {
        try {
            putIntVolatile.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getByteVolatile(Object o, long offset) {
        try {
            return (Byte) getByteVolatile.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putByteVolatile(Object o, long offset, byte x) {
        try {
            putByteVolatile.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    short getShortVolatile(Object o, long offset) {
        try {
            return (Short) getShortVolatile.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putShortVolatile(Object o, long offset, short x) {
        try {
            putShortVolatile.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getLongVolatile(Object o, long offset) {
        try {
            return (Long) getLongVolatile.invoke(UNSAFE, o, offset);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putLongVolatile(Object o, long offset, long x) {
        try {
            putLongVolatile.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putOrderedInt(Object o, long offset, int x) {
        try {
            putOrderedInt.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putOrderedLong(Object o, long offset, long x) {
        try {
            putOrderedLong.invoke(UNSAFE, o, offset, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}