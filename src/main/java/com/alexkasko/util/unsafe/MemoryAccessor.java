package com.alexkasko.util.unsafe;

/**
* User: alexkasko
* Date: 1/14/13
*/
// todo: still untested
public abstract class  MemoryAccessor {
    public static MemoryAccessor get(int blockSize, int maxAllocated) {
        try {
            return unsafe(blockSize, maxAllocated);
        } catch (Exception e) {
            return direct(blockSize, maxAllocated);
        }
    }

    public static MemoryAccessor unsafe(int blockSize, int maxAllocated) throws Exception {
        try {
            Class<? extends MemoryAccessor> unsafeMaClass = MemoryAccessor.class
                    .getClassLoader()
                    .loadClass(MemoryAccessor.class.getPackage().getName() + ".UnsafeMemoryAccessor")
                    .asSubclass(MemoryAccessor.class);
            return unsafeMaClass.getConstructor(int.class, int.class).newInstance(blockSize, maxAllocated);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    public static MemoryAccessor direct(int blockSize, int maxAllocated) {
        return new DirectMemoryAccessor(blockSize, maxAllocated);
    }

    public abstract boolean isUnsafe();

    public abstract boolean isDirect();

    public abstract int getBlockSize();

    public abstract int getCount();

    public abstract int alloc();

    public abstract void write(int id, int targetLeftOffset, int targetRightOffset, byte[] buffer, int bufferOffset);

    public abstract void read(int id, int targetLeftOffset, int targetRightOffset, byte[] buffer, int bufferOffset);

    public abstract void free(int id);
}
