package com.alexkasko.unsafe.bytearray;

/**
 * Front-end class for operations over byte arrays. Default implementation uses {@code sun.misc.Unsafe}
 * with {@code assert} boundary checks. To enable boundary check use {@code -ea} java switch,
 * without boundary checks invalid input will crash (segfault) JVM.
 * Fallback implementation uses standard byte array access and bit shifting.
 * Unsafe implementation uses native endianness (LE on most platforms). Fallback implementation uses Little Endian byte order.
 *
 * @author alexkasko
 * Date: 12/11/12
 */
public abstract class ByteArrayTool {

    /**
     * Instantiates unsafe tool, if proper {@code sun.misc.Unsafe} implementation is available.
     * Otherwise instantiates fallback (bitshift) implementation.
     * 
     * @return tool instance
     */
    public static ByteArrayTool get() {
        try {
            return unsafe();
        } catch (Exception e) {
            return bitshift();
        }
    }

    /**
     * Instantiates unsafe tool
     * 
     * @return unsafe tool instance
     * @throws Exception if proper {@code sun.misc.Unsafe} implementation is not available
     */
    public static ByteArrayTool unsafe() throws Exception {
        try {
            Class<? extends ByteArrayTool> unsafeBaaClass = ByteArrayTool.class
                    .getClassLoader()
                    .loadClass(ByteArrayTool.class.getPackage().getName() + ".UnsafeByteArrayTool")
                    .asSubclass(ByteArrayTool.class);
            return unsafeBaaClass.newInstance();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    /**
     * Instantiates bitshift tool
     * 
     * @return bitshift tool instance
     */
    public static ByteArrayTool bitshift() {
        return new BitShiftLittleEndianByteArrayTool();
    }

    /**
     * Whether current tool instance uses {@code sun.misc.Unsafe}
     * 
     * @return whether unsafe
     */
    public abstract boolean isUnsafe();

    /**
     * Gets byte
     * 
     * @param data byte array
     * @param offset byte array index
     * @return byte
     */
    public abstract byte getByte(byte[] data, int offset);

    /**
     * Puts byte
     *
     * @param data byte array
     * @param offset byte array index
     * @param value value
     */
    public abstract void putByte(byte[] data, int offset, byte value);

    /**
     * Gets one byte (stored as one signed byte), converts it to unsigned
     * and returns it as short
     *
     * @param data byte array
     * @param offset byte array index
     * @return unsigned byte as short
     */
    public abstract short getUnsignedByte(byte[] data, int offset);

    /**
     * Puts short with value from 0 to 255 inclusive into byte array as one
     * signed byte
     *
     * @param data byte array
     * @param offset byte array index
     * @param value unsigned byte
     */
    public abstract void putUnsignedByte(byte[] data, int offset, short value);

    /**
     * Gets two bytes as short
     *
     * @param data byte array
     * @param offset byte array offset
     * @return short value
     */
    public abstract short getShort(byte[] data, int offset);

    /**
     * Puts short as two bytes
     *
     * @param data byte array
     * @param offset byte array offset
     * @param value short value
     */
    public abstract void putShort(byte[] data, int offset, short value);

    /**
     * Gets unsigned short (stored as two bytes) and returns it as int
     *
     * @param data byte array
     * @param offset byte array offset
     * @return unsigned short as int
     */
    public abstract int getUnsignedShort(byte[] data, int offset);

    /**
     * Puts int with value from 0 to 65535 inclusive as two bytes
     *
     * @param data byte array
     * @param offset byte array offset
     * @param value unsigned short as int
     */
    public abstract void putUnsignedShort(byte[] data, int offset, int value);

    /**
     * Gets four bytes as int
     *
     * @param data byte array
     * @param offset byte array offset
     * @return int value
     */
    public abstract int getInt(byte[] data, int offset);

    /**
     * Puts int as four bytes
     *
     * @param data byte array
     * @param offset byte array offset
     * @param value int value
     */
    public abstract void putInt(byte[] data, int offset, int value);

    /**
     * Gets unsigned int (stored as 4 bytes) and returns it as long
     *
     * @param data byte array
     * @param offset byte array offset
     * @return unsigned int as long
     */
    public abstract long getUnsignedInt(byte[] data, int offset);

    /**
     * Puts long value from 0 to 4294967295 inclusive as four bytes
     *
     * @param data byte array
     * @param offset byte array offset
     * @param value unsigned int as long
     */
    public abstract void putUnsignedInt(byte[] data, int offset, long value);

    /**
     * Gets long
     *
     * @param data byte array
     * @param offset byte array offset
     * @return long value
     */
    public abstract long getLong(byte[] data, int offset);

    /**
     * Puts long as eight bytes
     *
     * @param data byte array
     * @param offset byte array offset
     * @param value long value
     */
    public abstract void putLong(byte[] data, int offset, long value);

    /**
     * Copies part of one array into another. Unsafe implementation has less
     * bounds checks (if assertions are disabled in runtime) then {@link System#arraycopy(Object, int, Object, int, int)}
     * that is used by fallback implementation uses
     *
     * @param src source array
     * @param srcPos source array position
     * @param dest destination array
     * @param destPos destination array position
     * @param length length to copy
     */
    public abstract void copy(byte[] src, int srcPos, byte[] dest, int destPos, int length);
}
