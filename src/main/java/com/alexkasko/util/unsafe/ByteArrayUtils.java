package com.alexkasko.util.unsafe;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
public class ByteArrayUtils {

    public static final ByteArrayAccessor BA_ACCESSOR;

    // borrowed from
    // https://github.com/dain/snappy/blob/602f4d7d71237f6a599389575edf5367da6cea37/src/main/java/org/iq80/snappy/SnappyInternalUtils.java#L28
    static {
        // Try to only load one implementation of Memory to assure the call sites are monomorphic (fast)
        ByteArrayAccessor baa = null;
        try {
            Class<? extends ByteArrayAccessor> unsafeBaaClass = ByteArrayUtils.class
                    .getClassLoader()
                    .loadClass("com.alexkasko.util.unsafe.UnsafeByteArrayAccessor")
                    .asSubclass(ByteArrayAccessor.class);
            ByteArrayAccessor unsafeBaa = unsafeBaaClass.newInstance();
            if(unsafeBaa.readInt(new byte[4], 0) == 0) {
                baa = unsafeBaa;
            }
        } catch(Throwable ignored) {
        }
        if(baa == null) {
            try {
                Class<? extends ByteArrayAccessor> slowBaaClass = ByteArrayUtils.class
                        .getClassLoader()
                        .loadClass("com.alexkasko.util.unsafe.SlowByteArrayAccessor")
                        .asSubclass(ByteArrayAccessor.class);
                ByteArrayAccessor slowBaa = slowBaaClass.newInstance();
                if(slowBaa.readInt(new byte[4], 0) == 0) {
                    baa = slowBaa;
                } else {
                    throw new AssertionError("SlowByteArrayAccessor class is broken!");
                }
            } catch(Throwable ignored) {
                throw new AssertionError("Could not find SlowByteArrayAccessor class");
            }
        }
        BA_ACCESSOR = baa;
    }

}
