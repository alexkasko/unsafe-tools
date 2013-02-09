package com.alexkasko.util.unsafe;

import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
public class ByteArrayAccessorTest {
    private static final byte[] d1 = new byte[1048576];
    private static final byte[] d2 = new byte[1024*1024];
    private static final byte[] zeros = new byte[1024*1024];
    private static final byte[] buffer = new byte[1024*1024];
    private static final ByteArrayAccessor ba;
    static {
        Random ra = new Random();
        ra.nextBytes(d1);
        ra.nextBytes(d2);
        try {
            ba = ByteArrayAccessor.unsafe();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

//    14
//    @Test
//    public void foo() {
//        for(int i=0; i< 1000*50; i++) {
//            ba.copy(d1, 0, buffer, 0, 1048576);
//            ba.copy(d2, 0, buffer, 0, 1048576);
//            ba.copy(zeros, 0, buffer, 0, 1048576);
//            System.arraycopy(d1, 0, buffer, 0, 1048576);
//            System.arraycopy(d2, 0, buffer, 0, 1048576);
//            System.arraycopy(zeros, 0, buffer, 0, 1048576);
//        }
//    }

    @Test
    public void test() throws Exception {
        // standard
        ByteArrayAccessor standard = ByteArrayAccessor.bitshift();
        testReadByte(standard);
        testWriteByte(standard);
        testReadUnsignedByte(standard);
        testWriteUnsignedByte(standard);
        testReadShort(standard);
        testWriteShort(standard);
        testReadUnsignedShort(standard);
        testWriteUnsignedShort(standard);
        testReadInt(standard);
        testWriteInt(standard);
        testReadUnsignedInt(standard);
        testWriteUnsignedInt(standard);
        testReadLong(standard);
        testWriteLong(standard);
        testCopy(standard);
        // unsafe
        ByteArrayAccessor unsafe = ByteArrayAccessor.unsafe();
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
        testCopy(unsafe);
    }

    private static void testReadByte(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0x2a;
        assertEquals((byte) 0x2a, baa.readByte(b, 43));
        b[44] = (byte) 0xd6;
        assertEquals((byte) 0xd6, baa.readByte(b, 44));
    }


    private static void testWriteByte(ByteArrayAccessor baa) {
        byte[] b = new byte[128];
        baa.writeByte(b, 43, (byte) 0x2a);
        assertEquals((byte) 0x2a, b[43]);
        baa.writeByte(b, 44, (byte) 0xd6);
        assertEquals((byte) 0xd6, b[44]);
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

    private static void testCopy(ByteArrayAccessor baa) {
        byte[] from = new byte[128];
        byte[] to = new byte[128];
        from[43] = (byte) 0x7f;
        from[44] = (byte) 0xf0;
        from[45] = (byte) 0xed;
        from[46] = (byte) 0x89;
        from[47] = (byte) 0xa2;
        from[48] = (byte) 0x0b;
        from[49] = (byte) 0x36;
        from[50] = (byte) 0x4d;
        baa.copy(from, 43, to, 42, 8);
        assertEquals((byte) 0x7f, to[42]);
        assertEquals((byte) 0xf0, to[43]);
        assertEquals((byte) 0xed, to[44]);
        assertEquals((byte) 0x89, to[45]);
        assertEquals((byte) 0xa2, to[46]);
        assertEquals((byte) 0x0b, to[47]);
        assertEquals((byte) 0x36, to[48]);
        assertEquals((byte) 0x4d, to[49]);
    }
}
