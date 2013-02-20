package com.alexkasko.util.unsafe;

import org.junit.Test;

import static com.alexkasko.util.unsafe.MemoryArea.allocDirectArea;
import static com.alexkasko.util.unsafe.MemoryArea.allocUnsafeArea;
import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 2/16/13
 */
// todo: complete me
public class MemoryAreaTest {

    @Test
    public void test() throws Exception {
        testReadByte(allocUnsafeArea(128));
        testWriteByte(allocUnsafeArea(128));
        testReadUnsignedByte(allocUnsafeArea(128));
        testWriteUnsignedByte(allocUnsafeArea(128));
        testReadShort(allocUnsafeArea(128));
        testWriteShort(allocUnsafeArea(128));
        testReadUnsignedShort(allocUnsafeArea(128));
        testWriteUnsignedShort(allocUnsafeArea(128));
        testReadInt(allocUnsafeArea(128));
        testWriteInt(allocUnsafeArea(128));
        testReadUnsignedInt(allocUnsafeArea(128));
        testWriteUnsignedInt(allocUnsafeArea(128));
        testReadLong(allocUnsafeArea(128));
        testWriteLong(allocUnsafeArea(128));

        testReadByte(allocDirectArea(128));
        testWriteByte(allocDirectArea(128));
        testReadUnsignedByte(allocDirectArea(128));
        testWriteUnsignedByte(allocDirectArea(128));
        testReadShort(allocDirectArea(128));
        testWriteShort(allocDirectArea(128));
        testReadUnsignedShort(allocDirectArea(128));
        testWriteUnsignedShort(allocDirectArea(128));
        testReadInt(allocDirectArea(128));
        testWriteInt(allocDirectArea(128));
        testReadUnsignedInt(allocDirectArea(128));
        testWriteUnsignedInt(allocDirectArea(128));
        testReadLong(allocDirectArea(128));
        testWriteLong(allocDirectArea(128));
    }

    private static void testReadByte(MemoryArea ma) {
        byte[] b = new byte[2];
        b[0] = (byte) 0x2a;
        ma.write(43, b, 0, 1);
        assertEquals((byte) 0x2a, ma.readByte(43));
        b[1] = (byte) 0xd6;
        ma.write(44, b, 1, 1);
        assertEquals((byte) 0xd6, ma.readByte(44));
        ma.free();
    }

    private static void testWriteByte(MemoryArea ma) {
        byte[] b = new byte[2];
        ma.writeByte(43, (byte) 0x2a);
        ma.read(43, b, 0, 1);
        assertEquals((byte) 0x2a, b[0]);
        ma.writeByte(44, (byte) 0xd6);
        ma.read(44, b, 1, 1);
        assertEquals((byte) 0xd6, b[1]);
        ma.free();
    }

    private static void testReadUnsignedByte(MemoryArea ma) {
        byte[] b = new byte[2];
        b[0] = (byte) 0xfe;
        ma.write(43, b, 0, 1);
        assertEquals((short) 0xfe, ma.readUnsignedByte(43));
        b[1] = (byte) 0x2a;
        ma.write(44, b, 1, 1);
        assertEquals((short) 0x2a, ma.readUnsignedByte(44));
        ma.free();
    }

    private static void testWriteUnsignedByte(MemoryArea ma) {
        byte[] b = new byte[2];
        ma.writeUnsignedByte(43, (short) 0xfe);
        ma.read(43, b, 0, 1);
        assertEquals((byte) 0xfe, b[0]);
        ma.writeUnsignedByte(44, (short) 0x2a);
        ma.read(44, b, 1, 1);
        assertEquals((byte) 0x2a, b[1]);
        ma.free();
    }

    private static void testReadShort(MemoryArea ma) {
        byte[] b = new byte[4];
        b[0] = (byte) 0x2a;
        b[1] = (byte) 0x7d;
        ma.write(43, b, 0, 2);
        assertEquals((short) 0x7d2a, ma.readShort(43));
        b[2] = (byte) 0xd6;
        b[3] = (byte) 0x82;
        ma.write(45, b, 2, 2);
        assertEquals((short) 0x82d6, ma.readShort(45));
        ma.free();
    }

    private static void testWriteShort(MemoryArea ma) {
        byte[] b = new byte[4];
        ma.writeShort(43, (short) 0x7d2a);
        ma.read(43, b, 0, 2);
        assertEquals((byte) 0x2a, b[0]);
        assertEquals((byte) 0x7d, b[1]);
        ma.writeShort(45, (short) 0x82d6);
        ma.read(45, b, 2, 2);
        assertEquals((byte) 0xd6, b[2]);
        assertEquals((byte) 0x82, b[3]);
        ma.free();
    }

    private static void testReadUnsignedShort(MemoryArea ma) {
        byte[] b = new byte[4];
        b[0] = (byte) 0xda;
        b[1] = (byte) 0x8e;
        ma.write(43, b, 0, 2);
        assertEquals(0x8eda, ma.readUnsignedShort(43));
        b[2] = (byte) 0x2a;
        b[3] = (byte) 0x00;
        ma.write(45, b, 2, 2);
        assertEquals(0x2a, ma.readUnsignedShort(45));
        ma.free();
    }

    private static void testWriteUnsignedShort(MemoryArea ma) {
        byte[] b = new byte[4];
        ma.writeUnsignedShort(43, 0x8eda);
        ma.read(43, b, 0, 2);
        assertEquals((byte) 0xda, b[0]);
        assertEquals((byte) 0x8e, b[1]);
        ma.writeUnsignedShort(45, 0x2a);
        ma.read(45, b, 2, 2);
        assertEquals((byte) 0x2a, b[2]);
        assertEquals((byte) 0x00, b[3]);
        ma.free();
    }

    private static void testReadInt(MemoryArea ma) {
        byte[] b = new byte[8];
        b[0] = (byte) 0xcd;
        b[1] = (byte) 0x86;
        b[2] = (byte) 0xf9;
        b[3] = (byte) 0x7f;
        ma.write(43, b, 0, 4);
        assertEquals(0x7ff986cd, ma.readInt(43));
        b[4] = (byte) 0x32;
        b[5] = (byte) 0x79;
        b[6] = (byte) 0x06;
        b[7] = (byte) 0x80;
        ma.write(47, b, 4, 4);
        assertEquals(0x80067932, ma.readInt(47));
        ma.free();
    }

    private static void testWriteInt(MemoryArea ma) {
        byte[] b = new byte[8];
        ma.writeInt(43, 0x7ff986cd);
        ma.read(43, b, 0, 4);
        assertEquals((byte) 0xcd, b[0]);
        assertEquals((byte) 0x86, b[1]);
        assertEquals((byte) 0xf9, b[2]);
        assertEquals((byte) 0x7f, b[3]);
        ma.writeInt(47, 0x80067932);
        ma.read(47, b, 4, 4);
        assertEquals((byte) 0x32, b[4]);
        assertEquals((byte) 0x79, b[5]);
        assertEquals((byte) 0x06, b[6]);
        assertEquals((byte) 0x80, b[7]);
        ma.free();
    }

    private static void testReadUnsignedInt(MemoryArea ma) {
        byte[] b = new byte[8];
        b[0] = (byte) 0xed;
        b[1] = (byte) 0xab;
        b[2] = (byte) 0xda;
        b[3] = (byte) 0xfe;
        ma.write(43, b, 0, 4);
        assertEquals(0xfedaabedL, ma.readUnsignedInt(43));
        b[4] = (byte) 0x2a;
        b[5] = (byte) 0x00;
        b[6] = (byte) 0x00;
        b[7] = (byte) 0x00;
        ma.write(47, b, 4, 4);
        assertEquals(0x2aL, ma.readUnsignedInt(47));
        ma.free();
    }

    private static void testWriteUnsignedInt(MemoryArea ma) {
        byte[] b = new byte[8];
        ma.writeUnsignedInt(43, 0xfedaabedL);
        ma.read(43, b, 0, 4);
        assertEquals((byte) 0xed, b[0]);
        assertEquals((byte) 0xab, b[1]);
        assertEquals((byte) 0xda, b[2]);
        assertEquals((byte) 0xfe, b[3]);
        ma.writeUnsignedInt(47, 0x2aL);
        ma.read(47, b, 4, 4);
        assertEquals((byte) 0x2a, b[4]);
        assertEquals((byte) 0x00, b[5]);
        assertEquals((byte) 0x00, b[6]);
        assertEquals((byte) 0x00, b[7]);
        ma.free();
    }

    private static void testReadLong(MemoryArea ma) {
        byte[] b = new byte[16];
        b[0] = (byte) 0x4d;
        b[1] = (byte) 0x36;
        b[2] = (byte) 0x0b;
        b[3] = (byte) 0xa2;
        b[4] = (byte) 0x89;
        b[5] = (byte) 0xed;
        b[6] = (byte) 0xf0;
        b[7] = (byte) 0x7f;
        ma.write(43, b, 0, 8);
        assertEquals(0x7ff0ed89a20b364dL, ma.readLong(43));
        b[8] = (byte) 0xb2;
        b[9] = (byte) 0xc9;
        b[10] = (byte) 0xf4;
        b[11] = (byte) 0x5d;
        b[12] = (byte) 0x76;
        b[13] = (byte) 0x12;
        b[14] = (byte) 0x0f;
        b[15] = (byte) 0x80;
        ma.write(51, b, 8, 8);
        assertEquals(0x800f12765df4c9b2L, ma.readLong(51));
        ma.free();
    }

    private static void testWriteLong(MemoryArea ma) {
        byte[] b = new byte[16];
        ma.writeLong(43, 0x7ff0ed89a20b364dL);
        ma.read(43, b, 0, 8);
        assertEquals((byte) 0x4d, b[0]);
        assertEquals((byte) 0x36, b[1]);
        assertEquals((byte) 0x0b, b[2]);
        assertEquals((byte) 0xa2, b[3]);
        assertEquals((byte) 0x89, b[4]);
        assertEquals((byte) 0xed, b[5]);
        assertEquals((byte) 0xf0, b[6]);
        assertEquals((byte) 0x7f, b[7]);
        ma.writeLong(51, 0x800f12765df4c9b2L);
        ma.read(51, b, 8, 8);
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
}
