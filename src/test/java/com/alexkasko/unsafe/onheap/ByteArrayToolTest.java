package com.alexkasko.unsafe.onheap;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
public class ByteArrayToolTest {

    @Test
    public void test() throws Exception {
        // standard
        ByteArrayTool standard = ByteArrayTool.bitshift();
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
        ByteArrayTool unsafe = ByteArrayTool.unsafe();
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

    @Test(expected = AssertionError.class)
    public void testBreak() throws Exception {
        ByteArrayTool unsafe = ByteArrayTool.unsafe();
        byte[] buf = new byte[4];
        unsafe.getLong(buf, Integer.MAX_VALUE);
    }

    private static void testReadByte(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0x2a;
        assertEquals((byte) 0x2a, baa.getByte(b, 43));
        b[44] = (byte) 0xd6;
        assertEquals((byte) 0xd6, baa.getByte(b, 44));
    }


    private static void testWriteByte(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putByte(b, 43, (byte) 0x2a);
        assertEquals((byte) 0x2a, b[43]);
        baa.putByte(b, 44, (byte) 0xd6);
        assertEquals((byte) 0xd6, b[44]);
    }

    private static void testReadUnsignedByte(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xfe;
        assertEquals((short) 0xfe, baa.getUnsignedByte(b, 43));
        b[44] = (byte) 0x2a;
        assertEquals((short) 0x2a, baa.getUnsignedByte(b, 44));
    }

    private static void testWriteUnsignedByte(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putUnsignedByte(b, 43, (short) 0xfe);
        assertEquals((byte) 0xfe, b[43]);
        baa.putByte(b, 44, (byte) 0x2a);
        assertEquals((byte) 0x2a, b[44]);
    }

    private static void testReadShort(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0x2a;
        b[44] = (byte) 0x7d;
        assertEquals((short) 0x7d2a, baa.getShort(b, 43));
        b[45] = (byte) 0xd6;
        b[46] = (byte) 0x82;
        assertEquals((short) 0x82d6, baa.getShort(b, 45));
    }

    private static void testWriteShort(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putShort(b, 43, (short) 0x7d2a);
        assertEquals((byte) 0x2a, b[43]);
        assertEquals((byte) 0x7d, b[44]);
        baa.putShort(b, 45, (short) 0x82d6);
        assertEquals((byte) 0xd6, b[45]);
        assertEquals((byte) 0x82, b[46]);
    }

    private static void testReadUnsignedShort(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xda;
        b[44] = (byte) 0x8e;
        assertEquals(0x8eda, baa.getUnsignedShort(b, 43));
        b[45] = (byte) 0x2a;
        b[46] = (byte) 0x00;
        assertEquals(0x2a, baa.getUnsignedShort(b, 45));
    }

    private static void testWriteUnsignedShort(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putUnsignedShort(b, 43, 0x8eda);
        assertEquals((byte) 0xda, b[43]);
        assertEquals((byte) 0x8e, b[44]);
        baa.putUnsignedShort(b, 45, 0x2a);
        assertEquals((byte) 0x2a, b[45]);
        assertEquals((byte) 0x00, b[46]);
    }

    private static void testReadInt(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xcd;
        b[44] = (byte) 0x86;
        b[45] = (byte) 0xf9;
        b[46] = (byte) 0x7f;
        assertEquals(0x7ff986cd, baa.getInt(b, 43));
        b[47] = (byte) 0x32;
        b[48] = (byte) 0x79;
        b[49] = (byte) 0x06;
        b[50] = (byte) 0x80;
        assertEquals(0x80067932, baa.getInt(b, 47));
    }

    private static void testWriteInt(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putInt(b, 43, 0x7ff986cd);
        assertEquals((byte) 0xcd, b[43]);
        assertEquals((byte) 0x86, b[44]);
        assertEquals((byte) 0xf9, b[45]);
        assertEquals((byte) 0x7f, b[46]);
        baa.putInt(b, 47, 0x80067932);
        assertEquals((byte) 0x32, b[47]);
        assertEquals((byte) 0x79, b[48]);
        assertEquals((byte) 0x06, b[49]);
        assertEquals((byte) 0x80, b[50]);
    }

    private static void testReadUnsignedInt(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0xed;
        b[44] = (byte) 0xab;
        b[45] = (byte) 0xda;
        b[46] = (byte) 0xfe;
        assertEquals(0xfedaabedL, baa.getUnsignedInt(b, 43));
        b[47] = (byte) 0x2a;
        b[48] = (byte) 0x00;
        b[49] = (byte) 0x00;
        b[50] = (byte) 0x00;
        assertEquals(0x2aL, baa.getUnsignedInt(b, 47));
    }

    private static void testWriteUnsignedInt(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putUnsignedInt(b, 43, 0xfedaabedL);
        assertEquals((byte) 0xed, b[43]);
        assertEquals((byte) 0xab, b[44]);
        assertEquals((byte) 0xda, b[45]);
        assertEquals((byte) 0xfe, b[46]);
        baa.putUnsignedInt(b, 47, 0x2aL);
        assertEquals((byte) 0x2a, b[47]);
        assertEquals((byte) 0x00, b[48]);
        assertEquals((byte) 0x00, b[49]);
        assertEquals((byte) 0x00, b[50]);
    }

    private static void testReadLong(ByteArrayTool baa) {
        byte[] b = new byte[128];
        b[43] = (byte) 0x4d;
        b[44] = (byte) 0x36;
        b[45] = (byte) 0x0b;
        b[46] = (byte) 0xa2;
        b[47] = (byte) 0x89;
        b[48] = (byte) 0xed;
        b[49] = (byte) 0xf0;
        b[50] = (byte) 0x7f;
        assertEquals(0x7ff0ed89a20b364dL, baa.getLong(b, 43));
        b[51] = (byte) 0xb2;
        b[52] = (byte) 0xc9;
        b[53] = (byte) 0xf4;
        b[54] = (byte) 0x5d;
        b[55] = (byte) 0x76;
        b[56] = (byte) 0x12;
        b[57] = (byte) 0x0f;
        b[58] = (byte) 0x80;
        assertEquals(0x800f12765df4c9b2L, baa.getLong(b, 51));
    }

    private static void testWriteLong(ByteArrayTool baa) {
        byte[] b = new byte[128];
        baa.putLong(b, 43, 0x7ff0ed89a20b364dL);
        assertEquals((byte) 0x4d, b[43]);
        assertEquals((byte) 0x36, b[44]);
        assertEquals((byte) 0x0b, b[45]);
        assertEquals((byte) 0xa2, b[46]);
        assertEquals((byte) 0x89, b[47]);
        assertEquals((byte) 0xed, b[48]);
        assertEquals((byte) 0xf0, b[49]);
        assertEquals((byte) 0x7f, b[50]);
        baa.putLong(b, 51, 0x800f12765df4c9b2L);
        assertEquals((byte) 0xb2, b[51]);
        assertEquals((byte) 0xc9, b[52]);
        assertEquals((byte) 0xf4, b[53]);
        assertEquals((byte) 0x5d, b[54]);
        assertEquals((byte) 0x76, b[55]);
        assertEquals((byte) 0x12, b[56]);
        assertEquals((byte) 0x0f, b[57]);
        assertEquals((byte) 0x80, b[58]);
    }

    private static void testCopy(ByteArrayTool baa) {
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
