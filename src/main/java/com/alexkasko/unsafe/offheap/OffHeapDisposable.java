package com.alexkasko.unsafe.offheap;

/**
 * Basic interface for off-heap classes that support manual memory freeing.
 * Not freed memory will be released on {@link OffHeapMemory} (impl) instances garbage collection.
 *
 * @author alexkasko
 * Date: 3/5/13
 */
public interface OffHeapDisposable {
    /**
     * Frees allocated memory, may be called multiple times from any thread
     */
    void free();
}
