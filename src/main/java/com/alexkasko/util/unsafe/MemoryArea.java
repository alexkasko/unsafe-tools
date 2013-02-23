package com.alexkasko.util.unsafe;

/**
* User: alexkasko
* Date: 1/14/13
*/
public abstract class MemoryArea {
    public static MemoryArea allocMemoryArea(long bytes) {
        try {
            return allocUnsafeArea(bytes);
        } catch (Exception e) {
            return allocDirectArea(bytes);
        }
    }

    public static MemoryArea allocUnsafeArea(long bytes) throws Exception {
        try {
            Class<? extends MemoryArea> unsafeMaClass = MemoryArea.class
                    .getClassLoader()
                    .loadClass(MemoryArea.class.getPackage().getName() + ".UnsafeMemoryArea")
                    .asSubclass(MemoryArea.class);
            return unsafeMaClass.getDeclaredConstructor(long.class).newInstance(bytes);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    public static MemoryArea allocDirectArea(long bytes) {
        return new DirectMemoryArea(bytes);
    }

    public abstract boolean isUnsafe();

    public abstract long length();

    public abstract void free();

    public abstract void write(long offset, byte[] buffer, int bufferOffset, int bytes);

    public abstract void write(long offset, byte[] buffer);

    public abstract void read(long offset, byte[] buffer, int bufferOffset, int bytes);

    public abstract void read(long offset, byte[] buffer);
    
    public abstract byte readByte(long offset);

    public abstract void writeByte(long offset, byte value);

    public abstract short readUnsignedByte(long offset);

    public abstract void writeUnsignedByte(long offset, short value);

    public abstract short readShort(long offset);

    public abstract void writeShort(long offset, short value);

    public abstract int readUnsignedShort(long offset);

    public abstract void writeUnsignedShort(long offset, int value);

    public abstract int readInt(long offset);

    public abstract void writeInt(long offset, int value);

    public abstract long readUnsignedInt(long offset);

    public abstract void writeUnsignedInt(long offset, long value);

    public abstract long readLong(long offset);

    public abstract void writeLong(long offset, long value);
}