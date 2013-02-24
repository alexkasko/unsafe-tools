Tools based on sun.misc.Unsafe
==============================

Some tools to work with off-heap memory using `sun.misc.Unsafe` class. Contains fallback
implementations for environments, where proper implementation of `sun.misc.Unsafe` is not available.

All memory access code has boundary checks using [assert](http://docs.oracle.com/javase/6/docs/technotes/guides/language/assert.html) keyword.
With assertions enabled in runtime (`-ea` java switch) illegal memory access will throw `AssertionError`.
Without assertions illegal memory access will crash JVM.

Library has no third-party dependencies and is available in [Maven cental](http://repo1.maven.org/maven2/com/alexkasko/):

    <dependency>
        <groupId>com.alexkasko.unsafe</groupId>
        <artifactId>unsafe-tools</artifactId>
        <version>1.0</version>
    </dependency>

Tools:
 - off-heap memory allocation and access ([javadoc](), [example]())
 - off-heap array of `long`'s with `long` indexing ([javadoc](), [example]())
 - quicksort implementation for off-heap arrays of `long`'s ([javadoc](), [example]())
 - byte arrays (on-heap) access using `sun.misc.Unsafe` ([javadoc](), [example]())
 - packing two limited values (one long and one int) into single long using bits shifting ([javadoc](), [example]())

License information
-------------------

This project is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Changelog
---------

**1.0** (2013-02-24)

 * initial version