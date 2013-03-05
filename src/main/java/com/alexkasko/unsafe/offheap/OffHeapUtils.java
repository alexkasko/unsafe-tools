package com.alexkasko.unsafe.offheap;

/**
 * Static utility methods for off-heap classes
 *
 * @author alexkasko
 * Date: 3/5/13
 */
public class OffHeapUtils {
    /**
     * Utility method for usage in {@code finally} clauses.
     * Calls {@link com.alexkasko.unsafe.offheap.OffHeapDisposable#free()}
     * on provided instance only if instance is not null.
     *
     * @param disposable off-heap disposable instance, may be null
     */
    public static void free(OffHeapDisposable disposable) {
        if(null == disposable) return;
        disposable.free();
    }
}
