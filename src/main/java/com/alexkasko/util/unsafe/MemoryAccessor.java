package com.alexkasko.util.unsafe;

/**
* User: alexkasko
* Date: 1/14/13
*/
public abstract class  MemoryAccessor {
    private static final int DEFAULT_MAX_ALLOCATED = 1 << 10;

    public static MemoryAccessor get() {
        return get(DEFAULT_MAX_ALLOCATED);
    }

    public static MemoryAccessor get(int maxAllocated) {
        try {
            return unsafe(maxAllocated);
        } catch (Exception e) {
            return direct(maxAllocated);
        }
    }

    public static MemoryAccessor unsafe(int maxAllocated) throws Exception {
        try {
            Class<? extends MemoryAccessor> unsafeMaClass = MemoryAccessor.class
                    .getClassLoader()
                    .loadClass(MemoryAccessor.class.getPackage().getName() + ".UnsafeMemoryAccessor")
                    .asSubclass(MemoryAccessor.class);
            return unsafeMaClass.getDeclaredConstructor(int.class).newInstance(maxAllocated);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    public static MemoryAccessor direct(int maxAllocated) {
        return new DirectMemoryAccessor(maxAllocated);
    }

    public abstract boolean isUnsafe();

    public abstract int getAllocationsCount();

    public abstract int alloc(long bytes);

    public abstract void free(int id);

    public abstract void freeQuetly(int id);

    public abstract void write(int id, long offset, byte[] buffer, int bufferOffset, int bytes);

    public abstract void write(int id, long offset, byte[] buffer);

    public abstract void read(int id, long offset, byte[] buffer, int bufferOffset, int bytes);

    public abstract void read(int id, long offset, byte[] buffer);
    
    public abstract byte readByte(int id, long offset);

    public abstract void writeByte(int id, long offset, byte value);

    public abstract short readUnsignedByte(int id, long offset);

    public abstract void writeUnsignedByte(int id, long offset, short value);

    public abstract short readShort(int id, long offset);

    public abstract void writeShort(int id, long offset, short value);

    public abstract int readUnsignedShort(int id, long offset);

    public abstract void writeUnsignedShort(int id, long offset, int value);

    public abstract int readInt(int id, long offset);

    public abstract void writeInt(int id, long offset, int value);

    public abstract long readUnsignedInt(int id, long offset);

    public abstract void writeUnsignedInt(int id, long offset, long value);

    public abstract long readLong(int id, long offset);

    public abstract void writeLong(int id, long offset, long value);
}
