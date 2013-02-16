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

        MemoryAccessor direct = MemoryAccessor.direct(1024);
        testReadByte(direct);
        testWriteByte(direct);
    }

    private static void testReadByte(MemoryAccessor ma) {
        int id = ma.alloc(128);
        byte[] b = new byte[2];
        b[0] = (byte) 0x2a;
        ma.write(id, 43, b, 0, 1);
        assertEquals((byte) 0x2a, ma.readByte(id,43));
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
    }

    private static void testReadUnsignedByte(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xfe;
        assertEquals((short) 0xfe, baa.readUnsignedByte(b, 43));
        b[44] = (byte) 0x2a;
        assertEquals((short) 0x2a, baa.readUnsignedByte(b, 44));
    }

    private static void testWriteUnsignedByte(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeUnsignedByte(b, 43, (short) 0xfe);
        assertEquals((byte) 0xfe, b[43]);
        baa.writeByte(b, 44, (byte) 0x2a);
        assertEquals((byte) 0x2a, b[44]);
    }

    private static void testReadShort(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0x2a;
        b[44] = (byte) 0x7d;
        assertEquals((short) 0x7d2a, baa.readShort(b, 43));
        b[45] = (byte) 0xd6;
        b[46] = (byte) 0x82;
        assertEquals((short) 0x82d6, baa.readShort(b, 45));
    }

    private static void testWriteShort(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeShort(b, 43, (short) 0x7d2a);
        assertEquals((byte) 0x2a, b[43]);
        assertEquals((byte) 0x7d, b[44]);
        baa.writeShort(b, 45, (short) 0x82d6);
        assertEquals((byte) 0xd6, b[45]);
        assertEquals((byte) 0x82, b[46]);
    }

    private static void testReadUnsignedShort(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xda;
        b[44] = (byte) 0x8e;
        assertEquals(0x8eda, baa.readUnsignedShort(b, 43));
        b[45] = (byte) 0x2a;
        b[46] = (byte) 0x00;
        assertEquals(0x2a, baa.readUnsignedShort(b, 45));
    }

    private static void testWriteUnsignedShort(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeUnsignedShort(b, 43, 0x8eda);
        assertEquals((byte) 0xda, b[43]);
        assertEquals((byte) 0x8e, b[44]);
        baa.writeUnsignedShort(b, 45, 0x2a);
        assertEquals((byte) 0x2a, b[45]);
        assertEquals((byte) 0x00, b[46]);
    }

    private static void testReadInt(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xcd;
        b[44] = (byte) 0x86;
        b[45] = (byte) 0xf9;
        b[46] = (byte) 0x7f;
        assertEquals(0x7ff986cd, baa.readInt(b, 43));
        b[47] = (byte) 0x32;
        b[48] = (byte) 0x79;
        b[49] = (byte) 0x06;
        b[50] = (byte) 0x80;
        assertEquals(0x80067932, baa.readInt(b, 47));
    }

    private static void testWriteInt(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeInt(b, 43, 0x7ff986cd);
        assertEquals((byte) 0xcd, b[43]);
        assertEquals((byte) 0x86, b[44]);
        assertEquals((byte) 0xf9, b[45]);
        assertEquals((byte) 0x7f, b[46]);
        baa.writeInt(b, 47, 0x80067932);
        assertEquals((byte) 0x32, b[47]);
        assertEquals((byte) 0x79, b[48]);
        assertEquals((byte) 0x06, b[49]);
        assertEquals((byte) 0x80, b[50]);
    }

    private static void testReadUnsignedInt(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xed;
        b[44] = (byte) 0xab;
        b[45] = (byte) 0xda;
        b[46] = (byte) 0xfe;
        assertEquals(0xfedaabedL, baa.readUnsignedInt(b, 43));
        b[47] = (byte) 0x2a;
        b[48] = (byte) 0x00;
        b[49] = (byte) 0x00;
        b[50] = (byte) 0x00;
        assertEquals(0x2aL, baa.readUnsignedInt(b, 47));
    }

    private static void testWriteUnsignedInt(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeUnsignedInt(b, 43, 0xfedaabedL);
        assertEquals((byte) 0xed, b[43]);
        assertEquals((byte) 0xab, b[44]);
        assertEquals((byte) 0xda, b[45]);
        assertEquals((byte) 0xfe, b[46]);
        baa.writeUnsignedInt(b, 47, 0x2aL);
        assertEquals((byte) 0x2a, b[47]);
        assertEquals((byte) 0x00, b[48]);
        assertEquals((byte) 0x00, b[49]);
        assertEquals((byte) 0x00, b[50]);
    }

    private static void testReadLong(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0x4d;
        b[44] = (byte) 0x36;
        b[45] = (byte) 0x0b;
        b[46] = (byte) 0xa2;
        b[47] = (byte) 0x89;
        b[48] = (byte) 0xed;
        b[49] = (byte) 0xf0;
        b[50] = (byte) 0x7f;
        assertEquals(0x7ff0ed89a20b364dL, baa.readLong(b, 43));
        b[51] = (byte) 0xb2;
        b[52] = (byte) 0xc9;
        b[53] = (byte) 0xf4;
        b[54] = (byte) 0x5d;
        b[55] = (byte) 0x76;
        b[56] = (byte) 0x12;
        b[57] = (byte) 0x0f;
        b[58] = (byte) 0x80;
        assertEquals(0x800f12765df4c9b2L, baa.readLong(b, 51));
    }

    private static void testWriteLong(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeLong(b, 43, 0x7ff0ed89a20b364dL);
        assertEquals((byte) 0x4d, b[43]);
        assertEquals((byte) 0x36, b[44]);
        assertEquals((byte) 0x0b, b[45]);
        assertEquals((byte) 0xa2, b[46]);
        assertEquals((byte) 0x89, b[47]);
        assertEquals((byte) 0xed, b[48]);
        assertEquals((byte) 0xf0, b[49]);
        assertEquals((byte) 0x7f, b[50]);
        baa.writeLong(b, 51, 0x800f12765df4c9b2L);
        assertEquals((byte) 0xb2, b[51]);
        assertEquals((byte) 0xc9, b[52]);
        assertEquals((byte) 0xf4, b[53]);
        assertEquals((byte) 0x5d, b[54]);
        assertEquals((byte) 0x76, b[55]);
        assertEquals((byte) 0x12, b[56]);
        assertEquals((byte) 0x0f, b[57]);
        assertEquals((byte) 0x80, b[58]);
    }
}
