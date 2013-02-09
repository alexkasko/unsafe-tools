package com.alexkasko.util.unsafe;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
public abstract class ByteArrayAccessor {

    public static ByteArrayAccessor get() {
        try {
            return unsafe();
        } catch (Exception e) {
            return bitshift();
        }
    }

    public static ByteArrayAccessor unsafe() throws Exception {
        try {
            Class<? extends ByteArrayAccessor> unsafeBaaClass = ByteArrayAccessor.class
                    .getClassLoader()
                    .loadClass(ByteArrayAccessor.class.getPackage().getName() + ".UnsafeByteArrayAccessor")
                    .asSubclass(ByteArrayAccessor.class);
            return unsafeBaaClass.newInstance();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    public static ByteArrayAccessor bitshift() {
        return new BitShiftLittleEndianByteArrayAccessor();
    }

    public abstract boolean isUnsafe();

    public abstract byte readByte(byte[] data, int offset);

    public abstract void writeByte(byte[] data, int offset, byte value);

    public abstract short readUnsignedByte(byte[] data, int offset);

    public abstract void writeUnsignedByte(byte[] data, int offset, short value);

    public abstract short readShort(byte[] data, int offset);

    public abstract void writeShort(byte[] data, int offset, short value);

    public abstract int readUnsignedShort(byte[] data, int offset);

    public abstract void writeUnsignedShort(byte[] data, int offset, int value);

    public abstract int readInt(byte[] data, int offset);

    public abstract void writeInt(byte[] data, int offset, int value);

    public abstract long readUnsignedInt(byte[] data, int offset);

    public abstract void writeUnsignedInt(byte[] data, int offset, long value);

    public abstract long readLong(byte[] data, int offset);

    public abstract void writeLong(byte[] data, int offset, long value);

    public abstract void copy(byte[] input, int inputIndex, byte[] output, int outputIndex, int length);
}
