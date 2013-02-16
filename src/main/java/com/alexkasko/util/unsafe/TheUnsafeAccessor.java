package com.alexkasko.util.unsafe;

/**
 * User: alexkasko
 * Date: 1/16/13
 */
abstract class TheUnsafeAccessor {

    public static TheUnsafeAccessor get() throws RuntimeException {
        try {
            return explicit();
        } catch (RuntimeException e) {
            return reflective();
        }
    }

    public static TheUnsafeAccessor explicit() throws RuntimeException {
        return load("ExplicitTheUnsafeAccessor");
    }

    public static TheUnsafeAccessor reflective() throws RuntimeException {
        return load("ReflectionTheUnsafeAccessor");
    }

    static TheUnsafeAccessor load(String className) throws RuntimeException {
        try {
            Class<? extends TheUnsafeAccessor> tuaClass = TheUnsafeAccessor.class
                    .getClassLoader()
                    .loadClass(TheUnsafeAccessor.class.getPackage().getName() + "." + className)
                    .asSubclass(TheUnsafeAccessor.class);
            return tuaClass.newInstance();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    /**
     * Sets all bytes in a given block of memory to a copy of another
     * block.
     * <p/>
     * <p>This method determines each block's base address by means of two parameters,
     * and so it provides (in effect) a <em>double-register</em> addressing mode,
     * as discussed in {@link #getInt(Object, long)}. When the object reference is null,
     * the offset supplies an absolute base address.
     * <p/>
     * <p>The transfers are in coherent (atomic) units of a size determined
     * by the address and length parameters. If the effective addresses and
     * length are all even modulo 8, the transfer takes place in 'long' units.
     * If the effective addresses and length are (resp.) even modulo 4 or 2,
     * the transfer takes place in units of 'int' or 'short'.
     */
    abstract void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);


    /**
     * Sets all bytes in a given block of memory to a copy of another
     * block. This provides a <em>single-register</em> addressing mode,
     * as discussed in {@link #getInt(Object, long)}.
     * <p/>
     * Equivalent to <code>copyMemory(null, srcAddress, null, destAddress, bytes)</code>.
     */
    abstract void copyMemory(long srcAddress, long destAddress, long bytes);

    /**
     * Allocates a new block of abstract memory, of the given size in bytes.  The
     * contents of the memory are uninitialized; they will generally be
     * garbage.  The resulting abstract pointer will never be zero, and will be
     * aligned for all value types.  Dispose of this memory by calling {@link
     * #freeMemory}, or resize it with {@link #reallocateMemory}.
     *
     * @throws IllegalArgumentException if the size is negative or too large
     *                                  for the abstract size_t type
     * @throws OutOfMemoryError         if the allocation is refused by the system
     * @see #getByte(long)
     * @see #putByte(long, byte)
     */
    abstract long allocateMemory(long bytes);

    /**
     * Resizes a new block of abstract memory, to the given size in bytes.  The
     * contents of the new block past the size of the old block are
     * uninitialized; they will generally be garbage.  The resulting abstract
     * pointer will be zero if and only if the requested size is zero.  The
     * resulting abstract pointer will be aligned for all value types.  Dispose
     * of this memory by calling {@link #freeMemory}, or resize it with {@link
     * #reallocateMemory}.  The address passed to this method may be null, in
     * which case an allocation will be performed.
     *
     * @throws IllegalArgumentException if the size is negative or too large
     *                                  for the abstract size_t type
     * @throws OutOfMemoryError         if the allocation is refused by the system
     * @see #allocateMemory
     */
    abstract long reallocateMemory(long address, long bytes);

    /**
     * Sets all bytes in a given block of memory to a fixed value
     * (usually zero).
     */

    abstract void setMemory(long address, long bytes, byte value);

    /**
     * Disposes of a block of abstract memory, as obtained from {@link
     * #allocateMemory} or {@link #reallocateMemory}.  The address passed to
     * this method may be null, in which case no action is taken.
     *
     * @see #allocateMemory
     */
    abstract void freeMemory(long address);

    // offsets

    /**
     * Report the offset of the first element in the storage allocation of a
     * given array class.  If {@link #arrayIndexScale} returns a non-zero value
     * for the same class, you may use that scale factor, together with this
     * base offset, to form new offsets to access elements of arrays of the
     * given class.
     *
     * @see #getInt(Object, long)
     * @see #putInt(Object, long, int)
     */
    abstract int arrayBaseOffset(Class arrayClass);

    /**
     * Report the scale factor for addressing elements in the storage
     * allocation of a given array class.  However, arrays of "narrow" types
     * will generally not work properly with accessors like {@link
     * #getByte(Object, long)}, so the scale factor for such classes is reported
     * as zero.
     *
     * @see #arrayBaseOffset
     * @see #getInt(Object, long)
     * @see #putInt(Object, long, int)
     */
    abstract int arrayIndexScale(Class arrayClass);

    // These work on object fields in the Java heap.

    /**
     * Fetches a value from a given Java variable.
     * More specifically, fetches a field or array element within the given
     * object <code>o</code> at the given offset, or (if <code>o</code> is
     * null) from the memory address whose numerical value is the given
     * offset.
     * <p/>
     * The results are undefined unless one of the following cases is true:
     * <ul>
     * <li>The offset was obtained from {@link #objectFieldOffset} on
     * the {@link java.lang.reflect.Field} of some Java field and the object
     * referred to by <code>o</code> is of a class compatible with that
     * field's class.
     * <p/>
     * <li>The offset and object reference <code>o</code> (either null or
     * non-null) were both obtained via {@link #staticFieldOffset}
     * and {@link #staticFieldBase} (respectively) from the
     * reflective {@link java.lang.reflect.Field} representation of some Java field.
     * <p/>
     * <li>The object referred to by <code>o</code> is an array, and the offset
     * is an integer of the form <code>B+N*S</code>, where <code>N</code> is
     * a valid index into the array, and <code>B</code> and <code>S</code> are
     * the values obtained by {@link #arrayBaseOffset} and {@link
     * #arrayIndexScale} (respectively) from the array's class.  The value
     * referred to is the <code>N</code><em>th</em> element of the array.
     * <p/>
     * </ul>
     * <p/>
     * If one of the above cases is true, the call references a specific Java
     * variable (field or array element).  However, the results are undefined
     * if that variable is not in fact of the type returned by this method.
     * <p/>
     * This method refers to a variable by means of two parameters, and so
     * it provides (in effect) a <em>double-register</em> addressing mode
     * for Java variables.  When the object reference is null, this method
     * uses its offset as an absolute address.  This is similar in operation
     * to methods such as {@link #getInt(long)}, which provide (in effect) a
     * <em>single-register</em> addressing mode for non-Java variables.
     * However, because Java variables may have a different layout in memory
     * from non-Java variables, programmers should not assume that these
     * two addressing modes are ever equivalent.  Also, programmers should
     * remember that offsets from the double-register addressing mode cannot
     * be portably confused with longs used in the single-register addressing
     * mode.
     *
     * @param o      Java heap object in which the variable resides, if any, else
     *               null
     * @param offset indication of where the variable resides in a Java heap
     *               object, if any, else a memory address locating the variable
     *               statically
     * @return the value fetched from the indicated Java variable
     * @throws RuntimeException No defined exceptions are thrown, not even
     *                          {@link NullPointerException}
     */
    abstract int getInt(Object o, long offset);

    /**
     * Stores a value into a given Java variable.
     * <p/>
     * The first two parameters are interpreted exactly as with
     * {@link #getInt(Object, long)} to refer to a specific
     * Java variable (field or array element).  The given value
     * is stored into that variable.
     * <p/>
     * The variable must be of the same type as the method
     * parameter <code>x</code>.
     *
     * @param o      Java heap object in which the variable resides, if any, else
     *               null
     * @param offset indication of where the variable resides in a Java heap
     *               object, if any, else a memory address locating the variable
     *               statically
     * @param x      the value to store into the indicated Java variable
     * @throws RuntimeException No defined exceptions are thrown, not even
     *                          {@link NullPointerException}
     */
    abstract void putInt(Object o, long offset, int x);

    /**
     * @see #getInt(Object, long)
     */
    abstract byte getByte(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    abstract void putByte(Object o, long offset, byte x);

    /**
     * @see #getInt(Object, long)
     */
    abstract short getShort(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    abstract void putShort(Object o, long offset, short x);

    /**
     * @see #getInt(Object, long)
     */
    abstract long getLong(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    abstract void putLong(Object o, long offset, long x);

    // These work on values in the C heap.

    /**
     * Fetches a value from a given memory address.  If the address is zero, or
     * does not point into a block obtained from {@link #allocateMemory}, the
     * results are undefined.
     *
     * @see #allocateMemory
     */
    abstract byte getByte(long address);

    /**
     * Stores a value into a given memory address.  If the address is zero, or
     * does not point into a block obtained from {@link #allocateMemory}, the
     * results are undefined.
     *
     * @see #getByte(long)
     */
    abstract void putByte(long address, byte x);

    /**
     * @see #getInt(long)
     */
    abstract short getShort(long address);

    /**
     * @see #putInt(long, int)
     */
    abstract void putShort(long address, short x);

    /**
     * @see #getByte(long)
     */
    abstract int getInt(long address);

    /**
     * @see #putByte(long, byte)
     */
    abstract void putInt(long address, int x);

    /**
     * @see #getByte(long)
     */
    abstract long getLong(long address);

    /**
     * @see #putByte(long, byte)
     */
    abstract void putLong(long address, long x);

    // Volatile methods

    /**
     * Volatile version of {@link #getInt(Object, long)}
     */
    abstract int getIntVolatile(Object o, long offset);

    /**
     * Volatile version of {@link #putInt(Object, long, int)}
     */
    abstract void putIntVolatile(Object o, long offset, int x);

    /**
     * Volatile version of {@link #getByte(Object, long)}
     */
    abstract byte getByteVolatile(Object o, long offset);

    /**
     * Volatile version of {@link #putByte(Object, long, byte)}
     */
    abstract void putByteVolatile(Object o, long offset, byte x);

    /**
     * Volatile version of {@link #getShort(Object, long)}
     */
    abstract short getShortVolatile(Object o, long offset);

    /**
     * Volatile version of {@link #putShort(Object, long, short)}
     */
    abstract void putShortVolatile(Object o, long offset, short x);

    /**
     * Volatile version of {@link #getLong(Object, long)}
     */
    abstract long getLongVolatile(Object o, long offset);

    /**
     * Volatile version of {@link #putLong(Object, long, long)}
     */
    abstract void putLongVolatile(Object o, long offset, long x);

    // lazy

    /**
     * Ordered/Lazy version of {@link #putIntVolatile(Object, long, int)}
     */
    abstract void putOrderedInt(Object o, long offset, int x);

    /**
     * Ordered/Lazy version of {@link #putLongVolatile(Object, long, long)}
     */
    abstract void putOrderedLong(Object o, long offset, long x);
}
