package com.alexkasko.util.unsafe;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 2/16/13
 */
// todo: complete me
public class MemoryAccessorTest {

    @Test
    public void test() throws Exception {
        MemoryAccessor unsafe = MemoryAccessor.unsafe(1024);
        testReadByte(unsafe);
        testWriteByte(unsafe);
        testReadUnsignedByte(unsafe);
        testWriteUnsignedByte(unsafe);
        testReadShort(unsafe);
        testWriteShort(unsafe);
        testReadUnsignedShort(unsafe);
        testWriteUnsignedShort(unsafe);
        testReadInt(unsafe);
        testWriteInt(unsafe);
        testReadUnsignedInt(unsafe);
        testWriteUnsignedInt(unsafe);
        testReadLong(unsafe);
        testWriteLong(unsafe);

        MemoryAccessor direct = MemoryAccessor.direct(1024);
        testReadByte(direct);
        testWriteByte(direct);
        testReadUnsignedByte(direct);
        testWriteUnsignedByte(direct);
        testReadShort(direct);
        testWriteShort(direct);
        testReadUnsignedShort(direct);
        testWriteUnsignedShort(direct);
        testReadInt(direct);
        testWriteInt(direct);
        testReadUnsignedInt(direct);
        testWriteUnsignedInt(direct);
        testReadLong(direct);
        testWriteLong(direct);
    }

    private static void testReadByte(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[2];
        b[0] = (byte) 0x2a;
        ma.write(id, 43, b, 0, 1);
        assertEquals((byte) 0x2a, ma.readByte(id, 43));
        b[1] = (byte) 0xd6;
        ma.write(id, 44, b, 1, 1);
        assertEquals((byte) 0xd6, ma.readByte(id, 44));
        ma.free(id);
    }

    private static void testWriteByte(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[2];
        ma.writeByte(id, 43, (byte) 0x2a);
        ma.read(id, 43, b, 0, 1);
        assertEquals((byte) 0x2a, b[0]);
        ma.writeByte(id, 44, (byte) 0xd6);
        ma.read(id, 44, b, 1, 1);
        assertEquals((byte) 0xd6, b[1]);
        ma.free(id);
    }

    private static void testReadUnsignedByte(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[2];
        b[0] = (byte) 0xfe;
        ma.write(id, 43, b, 0, 1);
        assertEquals((short) 0xfe, ma.readUnsignedByte(id, 43));
        b[1] = (byte) 0x2a;
        ma.write(id, 44, b, 1, 1);
        assertEquals((short) 0x2a, ma.readUnsignedByte(id, 44));
        ma.free(id);
    }

    private static void testWriteUnsignedByte(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[2];
        ma.writeUnsignedByte(id, 43, (short) 0xfe);
        ma.read(id, 43, b, 0, 1);
        assertEquals((byte) 0xfe, b[0]);
        ma.writeUnsignedByte(id, 44, (short) 0x2a);
        ma.read(id, 44, b, 1, 1);
        assertEquals((byte) 0x2a, b[1]);
        ma.free(id);
    }

    private static void testReadShort(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[4];
        b[0] = (byte) 0x2a;
        b[1] = (byte) 0x7d;
        ma.write(id, 43, b, 0, 2);
        assertEquals((short) 0x7d2a, ma.readShort(id, 43));
        b[2] = (byte) 0xd6;
        b[3] = (byte) 0x82;
        ma.write(id, 45, b, 2, 2);
        assertEquals((short) 0x82d6, ma.readShort(id, 45));
        ma.free(id);
    }

    private static void testWriteShort(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[4];
        ma.writeShort(id, 43, (short) 0x7d2a);
        ma.read(id, 43, b, 0, 2);
        assertEquals((byte) 0x2a, b[0]);
        assertEquals((byte) 0x7d, b[1]);
        ma.writeShort(id, 45, (short) 0x82d6);
        ma.read(id, 45, b, 2, 2);
        assertEquals((byte) 0xd6, b[2]);
        assertEquals((byte) 0x82, b[3]);
        ma.free(id);
    }

    private static void testReadUnsignedShort(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[4];
        b[0] = (byte) 0xda;
        b[1] = (byte) 0x8e;
        ma.write(id, 43, b, 0, 2);
        assertEquals(0x8eda, ma.readUnsignedShort(id, 43));
        b[2] = (byte) 0x2a;
        b[3] = (byte) 0x00;
        ma.write(id, 45, b, 2, 2);
        assertEquals(0x2a, ma.readUnsignedShort(id, 45));
        ma.free(id);
    }

    private static void testWriteUnsignedShort(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[4];
        ma.writeUnsignedShort(id, 43, 0x8eda);
        ma.read(id, 43, b, 0, 2);
        assertEquals((byte) 0xda, b[0]);
        assertEquals((byte) 0x8e, b[1]);
        ma.writeUnsignedShort(id, 45, 0x2a);
        ma.read(id, 45, b, 2, 2);
        assertEquals((byte) 0x2a, b[2]);
        assertEquals((byte) 0x00, b[3]);
        ma.free(id);
    }

    private static void testReadInt(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[8];
        b[0] = (byte) 0xcd;
        b[1] = (byte) 0x86;
        b[2] = (byte) 0xf9;
        b[3] = (byte) 0x7f;
        ma.write(id, 43, b, 0, 4);
        assertEquals(0x7ff986cd, ma.readInt(id, 43));
        b[4] = (byte) 0x32;
        b[5] = (byte) 0x79;
        b[6] = (byte) 0x06;
        b[7] = (byte) 0x80;
        ma.write(id, 47, b, 4, 4);
        assertEquals(0x80067932, ma.readInt(id, 47));
        ma.free(id);
    }

    private static void testWriteInt(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[8];
        ma.writeInt(id, 43, 0x7ff986cd);
        ma.read(id, 43, b, 0, 4);
        assertEquals((byte) 0xcd, b[0]);
        assertEquals((byte) 0x86, b[1]);
        assertEquals((byte) 0xf9, b[2]);
        assertEquals((byte) 0x7f, b[3]);
        ma.writeInt(id, 47, 0x80067932);
        ma.read(id, 47, b, 4, 4);
        assertEquals((byte) 0x32, b[4]);
        assertEquals((byte) 0x79, b[5]);
        assertEquals((byte) 0x06, b[6]);
        assertEquals((byte) 0x80, b[7]);
        ma.free(id);
    }

    private static void testReadUnsignedInt(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[8];
        b[0] = (byte) 0xed;
        b[1] = (byte) 0xab;
        b[2] = (byte) 0xda;
        b[3] = (byte) 0xfe;
        ma.write(id, 43, b, 0, 4);
        assertEquals(0xfedaabedL, ma.readUnsignedInt(id, 43));
        b[4] = (byte) 0x2a;
        b[5] = (byte) 0x00;
        b[6] = (byte) 0x00;
        b[7] = (byte) 0x00;
        ma.write(id, 47, b, 4, 4);
        assertEquals(0x2aL, ma.readUnsignedInt(id, 47));
        ma.free(id);
    }

    private static void testWriteUnsignedInt(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[8];
        ma.writeUnsignedInt(id, 43, 0xfedaabedL);
        ma.read(id, 43, b, 0, 4);
        assertEquals((byte) 0xed, b[0]);
        assertEquals((byte) 0xab, b[1]);
        assertEquals((byte) 0xda, b[2]);
        assertEquals((byte) 0xfe, b[3]);
        ma.writeUnsignedInt(id, 47, 0x2aL);
        ma.read(id, 47, b, 4, 4);
        assertEquals((byte) 0x2a, b[4]);
        assertEquals((byte) 0x00, b[5]);
        assertEquals((byte) 0x00, b[6]);
        assertEquals((byte) 0x00, b[7]);
        ma.free(id);
    }

    private static void testReadLong(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[16];
        b[0] = (byte) 0x4d;
        b[1] = (byte) 0x36;
        b[2] = (byte) 0x0b;
        b[3] = (byte) 0xa2;
        b[4] = (byte) 0x89;
        b[5] = (byte) 0xed;
        b[6] = (byte) 0xf0;
        b[7] = (byte) 0x7f;
        ma.write(id, 43, b, 0, 8);
        assertEquals(0x7ff0ed89a20b364dL, ma.readLong(id, 43));
        b[8] = (byte) 0xb2;
        b[9] = (byte) 0xc9;
        b[10] = (byte) 0xf4;
        b[11] = (byte) 0x5d;
        b[12] = (byte) 0x76;
        b[13] = (byte) 0x12;
        b[14] = (byte) 0x0f;
        b[15] = (byte) 0x80;
        ma.write(id, 51, b, 8, 8);
        assertEquals(0x800f12765df4c9b2L, ma.readLong(id, 51));
        ma.free(id);
    }

    private static void testWriteLong(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[16];
        ma.writeLong(id, 43, 0x7ff0ed89a20b364dL);
        ma.read(id, 43, b, 0, 8);
        assertEquals((byte) 0x4d, b[0]);
        assertEquals((byte) 0x36, b[1]);
        assertEquals((byte) 0x0b, b[2]);
        assertEquals((byte) 0xa2, b[3]);
        assertEquals((byte) 0x89, b[4]);
        assertEquals((byte) 0xed, b[5]);
        assertEquals((byte) 0xf0, b[6]);
        assertEquals((byte) 0x7f, b[7]);
        ma.writeLong(id, 51, 0x800f12765df4c9b2L);
        ma.read(id, 51, b, 8, 8);
        assertEquals((byte) 0xb2, b[8]);
        assertEquals((byte) 0xc9, b[9]);
        assertEquals((byte) 0xf4, b[10]);
        assertEquals((byte) 0x5d, b[11]);
        assertEquals((byte) 0x76, b[12]);
        assertEquals((byte) 0x12, b[13]);
        assertEquals((byte) 0x0f, b[14]);
        assertEquals((byte) 0x80, b[15]);
        ma.free(id);
    }
}
