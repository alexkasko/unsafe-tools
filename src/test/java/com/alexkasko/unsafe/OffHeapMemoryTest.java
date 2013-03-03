package com.alexkasko.unsafe;

import org.junit.Test;

import static com.alexkasko.unsafe.OffHeapMemory.allocateMemoryDirect;
import static com.alexkasko.unsafe.OffHeapMemory.allocateMemoryUnsafe;
import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 2/16/13
 */
public class OffHeapMemoryTest {

    @Test
    public void test() throws Exception {
        testReadByte(allocateMemoryUnsafe(128));
        testWriteByte(allocateMemoryUnsafe(128));
        testReadUnsignedByte(allocateMemoryUnsafe(128));
        testWriteUnsignedByte(allocateMemoryUnsafe(128));
        testReadShort(allocateMemoryUnsafe(128));
        testWriteShort(allocateMemoryUnsafe(128));
        testReadUnsignedShort(allocateMemoryUnsafe(128));
        testWriteUnsignedShort(allocateMemoryUnsafe(128));
        testReadInt(allocateMemoryUnsafe(128));
        testWriteInt(allocateMemoryUnsafe(128));
        testReadUnsignedInt(allocateMemoryUnsafe(128));
        testWriteUnsignedInt(allocateMemoryUnsafe(128));
        testReadLong(allocateMemoryUnsafe(128));
        testWriteLong(allocateMemoryUnsafe(128));
        testCopy(allocateMemoryUnsafe(128), allocateMemoryUnsafe(128));

        testReadByte(allocateMemoryDirect(128));
        testWriteByte(allocateMemoryDirect(128));
        testReadUnsignedByte(allocateMemoryDirect(128));
        testWriteUnsignedByte(allocateMemoryDirect(128));
        testReadShort(allocateMemoryDirect(128));
        testWriteShort(allocateMemoryDirect(128));
        testReadUnsignedShort(allocateMemoryDirect(128));
        testWriteUnsignedShort(allocateMemoryDirect(128));
        testReadInt(allocateMemoryDirect(128));
        testWriteInt(allocateMemoryDirect(128));
        testReadUnsignedInt(allocateMemoryDirect(128));
        testWriteUnsignedInt(allocateMemoryDirect(128));
        testReadLong(allocateMemoryDirect(128));
        testWriteLong(allocateMemoryDirect(128));
        testCopy(allocateMemoryDirect(128), allocateMemoryDirect(128));
    }

    private static void testReadByte(OffHeapMemory ma) {
        byte[] b = new byte[2];
        b[0] = (byte) 0x2a;
        ma.put(43, b, 0, 1);
        assertEquals((byte) 0x2a, ma.getByte(43));
        b[1] = (byte) 0xd6;
        ma.put(44, b, 1, 1);
        assertEquals((byte) 0xd6, ma.getByte(44));
        ma.free();
    }

    private static void testWriteByte(OffHeapMemory ma) {
        byte[] b = new byte[2];
        ma.putByte(43, (byte) 0x2a);
        ma.get(43, b, 0, 1);
        assertEquals((byte) 0x2a, b[0]);
        ma.putByte(44, (byte) 0xd6);
        ma.get(44, b, 1, 1);
        assertEquals((byte) 0xd6, b[1]);
        ma.free();
    }

    private static void testReadUnsignedByte(OffHeapMemory ma) {
        byte[] b = new byte[2];
        b[0] = (byte) 0xfe;
        ma.put(43, b, 0, 1);
        assertEquals((short) 0xfe, ma.getUnsignedByte(43));
        b[1] = (byte) 0x2a;
        ma.put(44, b, 1, 1);
        assertEquals((short) 0x2a, ma.getUnsignedByte(44));
        ma.free();
    }

    private static void testWriteUnsignedByte(OffHeapMemory ma) {
        byte[] b = new byte[2];
        ma.putUnsignedByte(43, (short) 0xfe);
        ma.get(43, b, 0, 1);
        assertEquals((byte) 0xfe, b[0]);
        ma.putUnsignedByte(44, (short) 0x2a);
        ma.get(44, b, 1, 1);
        assertEquals((byte) 0x2a, b[1]);
        ma.free();
    }

    private static void testReadShort(OffHeapMemory ma) {
        byte[] b = new byte[4];
        b[0] = (byte) 0x2a;
        b[1] = (byte) 0x7d;
        ma.put(43, b, 0, 2);
        assertEquals((short) 0x7d2a, ma.getShort(43));
        b[2] = (byte) 0xd6;
        b[3] = (byte) 0x82;
        ma.put(45, b, 2, 2);
        assertEquals((short) 0x82d6, ma.getShort(45));
        ma.free();
    }

    private static void testWriteShort(OffHeapMemory ma) {
        byte[] b = new byte[4];
        ma.putShort(43, (short) 0x7d2a);
        ma.get(43, b, 0, 2);
        assertEquals((byte) 0x2a, b[0]);
        assertEquals((byte) 0x7d, b[1]);
        ma.putShort(45, (short) 0x82d6);
        ma.get(45, b, 2, 2);
        assertEquals((byte) 0xd6, b[2]);
        assertEquals((byte) 0x82, b[3]);
        ma.free();
    }

    private static void testReadUnsignedShort(OffHeapMemory ma) {
        byte[] b = new byte[4];
        b[0] = (byte) 0xda;
        b[1] = (byte) 0x8e;
        ma.put(43, b, 0, 2);
        assertEquals(0x8eda, ma.getUnsignedShort(43));
        b[2] = (byte) 0x2a;
        b[3] = (byte) 0x00;
        ma.put(45, b, 2, 2);
        assertEquals(0x2a, ma.getUnsignedShort(45));
        ma.free();
    }

    private static void testWriteUnsignedShort(OffHeapMemory ma) {
        byte[] b = new byte[4];
        ma.putUnsignedShort(43, 0x8eda);
        ma.get(43, b, 0, 2);
        assertEquals((byte) 0xda, b[0]);
        assertEquals((byte) 0x8e, b[1]);
        ma.putUnsignedShort(45, 0x2a);
        ma.get(45, b, 2, 2);
        assertEquals((byte) 0x2a, b[2]);
        assertEquals((byte) 0x00, b[3]);
        ma.free();
    }

    private static void testReadInt(OffHeapMemory ma) {
        byte[] b = new byte[8];
        b[0] = (byte) 0xcd;
        b[1] = (byte) 0x86;
        b[2] = (byte) 0xf9;
        b[3] = (byte) 0x7f;
        ma.put(43, b, 0, 4);
        assertEquals(0x7ff986cd, ma.getInt(43));
        b[4] = (byte) 0x32;
        b[5] = (byte) 0x79;
        b[6] = (byte) 0x06;
        b[7] = (byte) 0x80;
        ma.put(47, b, 4, 4);
        assertEquals(0x80067932, ma.getInt(47));
        ma.free();
    }

    private static void testWriteInt(OffHeapMemory ma) {
        byte[] b = new byte[8];
        ma.putInt(43, 0x7ff986cd);
        ma.get(43, b, 0, 4);
        assertEquals((byte) 0xcd, b[0]);
        assertEquals((byte) 0x86, b[1]);
        assertEquals((byte) 0xf9, b[2]);
        assertEquals((byte) 0x7f, b[3]);
        ma.putInt(47, 0x80067932);
        ma.get(47, b, 4, 4);
        assertEquals((byte) 0x32, b[4]);
        assertEquals((byte) 0x79, b[5]);
        assertEquals((byte) 0x06, b[6]);
        assertEquals((byte) 0x80, b[7]);
        ma.free();
    }

    private static void testReadUnsignedInt(OffHeapMemory ma) {
        byte[] b = new byte[8];
        b[0] = (byte) 0xed;
        b[1] = (byte) 0xab;
        b[2] = (byte) 0xda;
        b[3] = (byte) 0xfe;
        ma.put(43, b, 0, 4);
        assertEquals(0xfedaabedL, ma.getUnsignedInt(43));
        b[4] = (byte) 0x2a;
        b[5] = (byte) 0x00;
        b[6] = (byte) 0x00;
        b[7] = (byte) 0x00;
        ma.put(47, b, 4, 4);
        assertEquals(0x2aL, ma.getUnsignedInt(47));
        ma.free();
    }

    private static void testWriteUnsignedInt(OffHeapMemory ma) {
        byte[] b = new byte[8];
        ma.putUnsignedInt(43, 0xfedaabedL);
        ma.get(43, b, 0, 4);
        assertEquals((byte) 0xed, b[0]);
        assertEquals((byte) 0xab, b[1]);
        assertEquals((byte) 0xda, b[2]);
        assertEquals((byte) 0xfe, b[3]);
        ma.putUnsignedInt(47, 0x2aL);
        ma.get(47, b, 4, 4);
        assertEquals((byte) 0x2a, b[4]);
        assertEquals((byte) 0x00, b[5]);
        assertEquals((byte) 0x00, b[6]);
        assertEquals((byte) 0x00, b[7]);
        ma.free();
    }

    private static void testReadLong(OffHeapMemory ma) {
        byte[] b = new byte[16];
        b[0] = (byte) 0x4d;
        b[1] = (byte) 0x36;
        b[2] = (byte) 0x0b;
        b[3] = (byte) 0xa2;
        b[4] = (byte) 0x89;
        b[5] = (byte) 0xed;
        b[6] = (byte) 0xf0;
        b[7] = (byte) 0x7f;
        ma.put(43, b, 0, 8);
        assertEquals(0x7ff0ed89a20b364dL, ma.getLong(43));
        b[8] = (byte) 0xb2;
        b[9] = (byte) 0xc9;
        b[10] = (byte) 0xf4;
        b[11] = (byte) 0x5d;
        b[12] = (byte) 0x76;
        b[13] = (byte) 0x12;
        b[14] = (byte) 0x0f;
        b[15] = (byte) 0x80;
        ma.put(51, b, 8, 8);
        assertEquals(0x800f12765df4c9b2L, ma.getLong(51));
        ma.free();
    }

    private static void testWriteLong(OffHeapMemory ma) {
        byte[] b = new byte[16];
        ma.putLong(43, 0x7ff0ed89a20b364dL);
        ma.get(43, b, 0, 8);
        assertEquals((byte) 0x4d, b[0]);
        assertEquals((byte) 0x36, b[1]);
        assertEquals((byte) 0x0b, b[2]);
        assertEquals((byte) 0xa2, b[3]);
        assertEquals((byte) 0x89, b[4]);
        assertEquals((byte) 0xed, b[5]);
        assertEquals((byte) 0xf0, b[6]);
        assertEquals((byte) 0x7f, b[7]);
        ma.putLong(51, 0x800f12765df4c9b2L);
        ma.get(51, b, 8, 8);
        assertEquals((byte) 0xb2, b[8]);
        assertEquals((byte) 0xc9, b[9]);
        assertEquals((byte) 0xf4, b[10]);
        assertEquals((byte) 0x5d, b[11]);
        assertEquals((byte) 0x76, b[12]);
        assertEquals((byte) 0x12, b[13]);
        assertEquals((byte) 0x0f, b[14]);
        assertEquals((byte) 0x80, b[15]);
        ma.free();
    }

    private static void testCopy(OffHeapMemory ma1, OffHeapMemory ma2) {
        ma1.putByte(43, (byte) 0x7f);
        ma1.putByte(44, (byte) 0xf0);
        ma1.putByte(45, (byte) 0xed);
        ma1.putByte(46, (byte) 0x89);
        ma1.putByte(47, (byte) 0xa2);
        ma1.putByte(48, (byte) 0x0b);
        ma1.putByte(49, (byte) 0x36);
        ma1.putByte(50, (byte) 0x4d);
        ma1.copy(43, ma2, 42, 8);
        assertEquals((byte) 0x7f, ma2.getByte(42));
        assertEquals((byte) 0xf0, ma2.getByte(43));
        assertEquals((byte) 0xed, ma2.getByte(44));
        assertEquals((byte) 0x89, ma2.getByte(45));
        assertEquals((byte) 0xa2, ma2.getByte(46));
        assertEquals((byte) 0x0b, ma2.getByte(47));
        assertEquals((byte) 0x36, ma2.getByte(48));
        assertEquals((byte) 0x4d, ma2.getByte(49));
        ma1.free();
        ma2.free();
    }
}
