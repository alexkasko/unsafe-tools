Off-heap memory tools based on sun.misc.Unsafe
==============================================

Some tools to work with off-heap memory using `sun.misc.Unsafe` class. Contains fallback
implementations for environments, where proper implementation of `sun.misc.Unsafe` is not available.

All memory access code has boundary checks using [assert](http://docs.oracle.com/javase/6/docs/technotes/guides/language/assert.html) keyword.
With assertions enabled in runtime (`-ea` java switch) illegal memory access will throw `AssertionError`.
Without assertions illegal memory access will crash JVM.

Library has no third-party dependencies and is available in [Maven cental](http://repo1.maven.org/maven2/com/alexkasko/):

    <dependency>
        <groupId>com.alexkasko.unsafe</groupId>
        <artifactId>unsafe-tools</artifactId>
        <version>1.1</version>
    </dependency>

Off-heap memory, data structures, operations
--------------------------------------------

JVM allows to allocate and use memory outside of java heap. Such off-heap memory is not managed by garbage collector and may be allocated using
public API: [ByteBuffer.allocateDirect](http://docs.oracle.com/javase/6/docs/api/java/nio/ByteBuffer.html#allocateDirect%28int%29).
In OpenJDK (and in Oracle java) Direct bytes buffers use `sun.misc.Unsafe` under the hood to work with off-heap memory.

####off-heap memory management

All off-heap data structures in this library are based on [OffHeapMemory](http://alexkasko.github.com/unsafe-tools/com/alexkasko/unsafe/offheap/OffHeapMemory.html).
It has two implementations:

 - main implementation: thin wrapper over `sun.misc.Unsafe` with boundary checks using `assert` keyword
 - fallback implementation: wrapper over `ByteBuffer.allocateDirect`

Both implementations have the same methods, but `DirectByteBuffer` (fallback) implementation has some drawbacks and
should be used only if proper implementation of `sun.misc.Unsafe` is not available in runtime (i.e. in Android's Dalvik VM):

 - mandatory boundary checks
 - each memory allocation is limited by `Integer.MAX_VALUE`
 - by default off-heap memory is freed only when `DirectByteBuffer` is garbage collector; this library uses
  reflection hacks (different for OpenJDK and Android implementations) to free memory eagerly

####off-heap data structures

Off-heap data structures have next distinctive features:

 - off-heap collections may use long indexes (not bounded by `Integer.MAX_VALUE`)
 - GC doesn't know about allocated memory so you can allocate gigabytes of memory without additional load on GC
 - memory may be freed to OS eagerly (it's not mandatory, `OffHeapMemory` will free memory when it's instance will be garbage collected)

This library implements next off-heap data structures:

 - long indexed fixed-size arrays of `long` ([javadoc](http://alexkasko.github.com/unsafe-tools/com/alexkasko/unsafe/offheap/OffHeapLongArray.html),
[usage](https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheap/OffHeapLongArrayTest.java))
 - long indexed growing array-lists of `long` ([javadoc](http://alexkasko.github.com/unsafe-tools/com/alexkasko/unsafe/offheap/OffHeapLongArrayList.html),
[usage](https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheap/OffHeapLongArrayListTest.java))
 - long indexed fixed-size header-payload (each element contain `long` header and `byte[]` payload) arrays ([javadoc](http://alexkasko.github.com/unsafe-tools/com/alexkasko/unsafe/offheap/OffHeapPayloadArray.html),
[usage](https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheap/OffHeapPayloadArrayTest.java))
 - long indexed growing array-lists ([javadoc](http://alexkasko.github.com/unsafe-tools/com/alexkasko/unsafe/offheap/OffHeapPayloadArrayList.html),
[usage](https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/offheap/OffHeapPayloadArrayListTest.java))

####operations

Next operations are implemented for all off-heap data structures:

 - Dual-Pivot Quicksort implementation, adapted from [here](https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java)
 - Binary Search implementation, adapted from [here](https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/Arrays.java)

Byte array tool
---------------

`sun.misc.Unsafe` may be used for writing/reading primitive values to/from byte arrays and to copying memory between byte arrays.
Such operations use platform byte order (little endian usually) and does not have boundary checks.

`ByteArrayTool` ([javadoc](http://alexkasko.github.com/unsafe-tools/com/alexkasko/unsafe/bytearray/ByteArrayTool.html),
[usage](https://github.com/alexkasko/unsafe-tools/blob/master/src/test/java/com/alexkasko/unsafe/bytearray/ByteArrayToolTest.java))
was created on top of such operations with `assert` boundary checks. It uses little endian bit-shifting as fallback implementation.

License information
-------------------

This project is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Changelog
---------

**1.1** (2013-03-05)

 * long-array list
 * header-payload array and array list
 * binary search for all collections

**1.0** (2013-02-24)

 * initial version
