package com.alexkasko.util.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * User: alexkasko
 * Date: 1/16/13
 */
class ExplicitTheUnsafeAccessor extends TheUnsafeAccessor {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            int boo = UNSAFE.arrayBaseOffset(byte[].class);
            // It seems not all Unsafe implementations implement the following method.
            UNSAFE.copyMemory(new byte[1], boo, new byte[1], boo, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        UNSAFE.copyMemory(srcBase, srcOffset, destBase, destOffset, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void copyMemory(long srcAddress, long destAddress, long bytes) {
        UNSAFE.copyMemory(srcAddress, destAddress, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long allocateMemory(long bytes) {
        return UNSAFE.allocateMemory(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long reallocateMemory(long address, long bytes) {
        return UNSAFE.reallocateMemory(address, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setMemory(long address, long bytes, byte value) {
        UNSAFE.setMemory(address, bytes, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int arrayBaseOffset(Class arrayClass) {
        return UNSAFE.arrayBaseOffset(arrayClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int arrayIndexScale(Class arrayClass) {
        return UNSAFE.arrayIndexScale(arrayClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getInt(Object o, long offset) {
        return UNSAFE.getInt(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putInt(Object o, long offset, int x) {
        UNSAFE.putInt(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getByte(Object o, long offset) {
        return UNSAFE.getByte(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putByte(Object o, long offset, byte x) {
        UNSAFE.putByte(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    short getShort(Object o, long offset) {
        return UNSAFE.getShort(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putShort(Object o, long offset, short x) {
        UNSAFE.putShort(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getLong(Object o, long offset) {
        return UNSAFE.getLong(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putLong(Object o, long offset, long x) {
        UNSAFE.putLong(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putByte(long address, byte x) {
        UNSAFE.putByte(address, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putInt(long address, int x) {
        UNSAFE.putInt(address, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putLong(long address, long x) {
        UNSAFE.putLong(address, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getIntVolatile(Object o, long offset) {
        return UNSAFE.getIntVolatile(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putIntVolatile(Object o, long offset, int x) {
        UNSAFE.putIntVolatile(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getByteVolatile(Object o, long offset) {
        return UNSAFE.getByteVolatile(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putByteVolatile(Object o, long offset, byte x) {
        UNSAFE.putByteVolatile(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    short getShortVolatile(Object o, long offset) {
        return UNSAFE.getShortVolatile(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putShortVolatile(Object o, long offset, short x) {
        UNSAFE.putShortVolatile(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long getLongVolatile(Object o, long offset) {
        return UNSAFE.getLongVolatile(o, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putLongVolatile(Object o, long offset, long x) {
        UNSAFE.putLongVolatile(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putOrderedInt(Object o, long offset, int x) {
        UNSAFE.putOrderedInt(o, offset, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void putOrderedLong(Object o, long offset, long x) {
        UNSAFE.putOrderedLong(o, offset, x);
    }
}
