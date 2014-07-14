Off-heap memory tools based on sun.misc.Unsafe
==============================================

Some tools to work with off-heap memory using `sun.misc.Unsafe` class. Contains fallback
implementations for environments, where proper implementation of `sun.misc.Unsafe` is not available.

All memory access code has boundary checks using [assert](http://docs.oracle.com/javase/6/docs/technotes/guides/language/assert.html) keyword.
With assertions enabled in runtime (`-ea` java switch) illegal memory access will throw `AssertionError`.
Without assertions illegal memory access will crash JVM.

Library has no third-party dependencies and is available in [Maven central](http://repo1.maven.org/maven2/com/alexkasko/):

    <dependency>
        <groupId>com.alexkasko.unsafe</groupId>
        <artifactId>unsafe-tools</artifactId>
        <version>1.4.3</version>
    </dependency>

Off-heap memory, data structures, operations
--------------------------------------------

JVM allows to allocate and use memory outside of java heap. Such off-heap memory may be allocated/used/deallocated
without additional load on garbage collector.

###Off-heap memory management

Off-heap memory manager implemented as a thin wrapper over `sun.misc.Unsafe`. Other data structures implemented on top of it.

See `com.alexkasko.unsafe.offheap` [package description](http://alexkasko.github.io/unsafe-tools/com/alexkasko/unsafe/offheap/package-summary.html)
for details.

###Off-heap collections of longs

Maximum array length in Java is bounded by `Integer.MAX_VALUE`. This library provides long-sized fixed and growing arrays of longs.

See `com.alexkasko.unsafe.offheaplong` [package description](http://alexkasko.github.io/unsafe-tools/com/alexkasko/unsafe/offheaplong/package-summary.html)
for details.

###Off-heap collections of structs (memory areas)

Equal-sized memory areas may be stored in off-heap memory contiguously as collections. Such memory areas also may be used
like C structs (without compiler checks, though). Sorting and searching of these collections may be done using `long` or `int`
"fields" of such "structs" or using `Comparator`. Sorting also can be performed "by-references" using additional array to
 hold collections indices (without reordering elements in the collection itself).

See `com.alexkasko.unsafe.offheapstruct` [package description](http://alexkasko.github.io/unsafe-tools/com/alexkasko/unsafe/offheapstruct/package-summary.html)
for details.

###Operations

The following operations are implemented for all off-heap data structures:

 - Dual-Pivot Quicksort implementation, adapted from [here](https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/DualPivotQuicksort.java).
 - Binary Search implementation, adapted from [here](https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/Arrays.java).

Byte array tool
---------------

`sun.misc.Unsafe` may be used for writing/reading primitive values to/from byte arrays and to copying memory between byte arrays.
Byte array tool is implemented on top of it.

See `com.alexkasko.unsafe.bytearray` [package description](http://alexkasko.github.io/unsafe-tools/com/alexkasko/unsafe/bytearray/package-summary.html)
for details.

License information
-------------------

This project is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Changelog
---------

**1.4.3** (2014-14-08)

 * additional get method for accessors
 * struct reference sort added to facade

**1.4.2** (2014-07-08)

 * reference sorting for struct collections
 * missed javadoc fixes

**1.4.1** (2014-05-11)

 * accessors for struct parts in struct collections
 * clone support for arrays
 * remove zero length assertion for byte array copy
 * comparator sort for collections of longs

**1.4.0** (2013-12-27)

 * header-payload package removed
 * `short` and `byte` sort/search methods removed
 * multisort methods removed
 * javadocs cleanup

**1.3.9** (2013-11-29)

 * parallel sort for long keys

**1.3.8** (2013-11-05)

 * minor utility methods added

**1.3.7** (2013-10-31)

 * fix race condition in parallel quicksort
 * fix unsigned long sorter

**1.3.6** (2013-10-25)

 * naive parallel quicksort for struct collections

**1.3.5** (2013-10-14)

 * on-heap memory prototype

**1.3.4** (2013-09-16)

 * comparator sort for struct collections
 * struct binary search with comparator
 * fail-fast on OOM during allocation
 * faster multisort choosers

**1.3.3** (2013-08-13)

 * sorting struct collections using unsigned fields comparison

**1.3.2** (2013-08-08)

 * fix sort of empty struct collection using multiple keys

**1.3.1** (2013-07-08)

 * struct collections sorting using `short` and `byte` fields
 * struct collections sorting with multiple keys

**1.3.0** (2013-07-07)

 * packages changed
 * struct collectons added
 * payload collections deprecated

**1.2.4** (2013-05-21)

 * more strict checks for lists `set`
 * `setPayload` method added

**1.2.3** (2013-05-06)

 * improve `assert` checks for off-heap memory
 * binary search javadoc fixes

**1.2.2** (2013-04-28)

 * prevent result object instantiation in ranged search

**1.2.1** (2013-04-28)

 * ranges support for binary search

**1.2** (2013-04-25)

 * **bug fixed in** `OffHeapPayloadSorter` - in some circumstances in 1.1 sorting may overwrite payload values
 for equal headers. A lot of tests on random data have been run to check correctness of 1.2 version
 * header-int_payload array, array list and sorting
 * header-long_payload array, array list and sorting

**1.1** (2013-03-05)

 * long-array list
 * header-payload array and array list
 * binary search for all collections

**1.0** (2013-02-24)

 * initial version
