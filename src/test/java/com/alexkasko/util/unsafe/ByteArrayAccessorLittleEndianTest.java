package com.alexkasko.util.unsafe;

import org.junit.Test;

import static com.alexkasko.util.unsafe.ByteArrayUtils.BA_ACCESSOR;
import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
public class ByteArrayAccessorLittleEndianTest {

//    @Test
//    public void stress() {
//        byte[] b = new byte[128];
//        for(int i = 0; i < 100000000; i++) {
//            for(int j = 0; j < 128; j += 8) {
//                BA_ACCESSOR.writeLong(b, j, Long.MAX_VALUE);
//            }
//        }
//    }

    @Test
    public void testReadByte() {
        byte[] b = new byte[128];
        b[43] = (byte) 0x2a;
        assertEquals((byte) 0x2a, BA_ACCESSOR.readByte(b, 43));
        b[44] = (byte) 0xd6;
        assertEquals((byte) 0xd6, BA_ACCESSOR.readByte(b, 44));
    }

    @Test
    public void testWriteByte() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeByte(b, 43, (byte) 0x2a);
        assertEquals((byte) 0x2a, b[43]);
        BA_ACCESSOR.writeByte(b, 44, (byte) 0xd6);
        assertEquals((byte) 0xd6, b[44]);
    }

    @Test
    public void testReadUnsignedByte() {
        byte[] b = new byte[128];
        b[43] = (byte) 0xfe;
        assertEquals((short) 0xfe, BA_ACCESSOR.readUnsignedByte(b, 43));
        b[44] = (byte) 0x2a;
        assertEquals((short) 0x2a, BA_ACCESSOR.readUnsignedByte(b, 44));
    }

    @Test
    public void testWriteUnsignedByte() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeUnsignedByte(b, 43, (short) 0xfe);
        assertEquals((byte) 0xfe, b[43]);
        BA_ACCESSOR.writeByte(b, 44, (byte) 0x2a);
        assertEquals((byte) 0x2a, b[44]);
    }

    @Test
    public void testReadShort() {
        byte[] b = new byte[128];
        b[43] = (byte) 0x2a;
        b[44] = (byte) 0x7d;
        assertEquals((short) 0x7d2a, BA_ACCESSOR.readShort(b, 43));
        b[45] = (byte) 0xd6;
        b[46] = (byte) 0x82;
        assertEquals((short) 0x82d6, BA_ACCESSOR.readShort(b, 45));
    }

    @Test
    public void testWriteShort() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeShort(b, 43, (short) 0x7d2a);
        assertEquals((byte) 0x2a, b[43]);
        assertEquals((byte) 0x7d, b[44]);
        BA_ACCESSOR.writeShort(b, 45, (short) 0x82d6);
        assertEquals((byte) 0xd6, b[45]);
        assertEquals((byte) 0x82, b[46]);
    }

    @Test
    public void testReadUnsignedShort() {
        byte[] b = new byte[128];
        b[43] = (byte) 0xda;
        b[44] = (byte) 0x8e;
        assertEquals(0x8eda, BA_ACCESSOR.readUnsignedShort(b, 43));
        b[45] = (byte) 0x2a;
        b[46] = (byte) 0x00;
        assertEquals(0x2a, BA_ACCESSOR.readUnsignedShort(b, 45));
    }

    @Test
    public void testWriteUnsignedShort() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeUnsignedShort(b, 43, 0x8eda);
        assertEquals((byte) 0xda, b[43]);
        assertEquals((byte) 0x8e, b[44]);
        BA_ACCESSOR.writeUnsignedShort(b, 45, 0x2a);
        assertEquals((byte) 0x2a, b[45]);
        assertEquals((byte) 0x00, b[46]);
    }

    @Test
    public void testReadInt() {
        byte[] b = new byte[128];
        b[43] = (byte) 0xcd;
        b[44] = (byte) 0x86;
        b[45] = (byte) 0xf9;
        b[46] = (byte) 0x7f;
        assertEquals(0x7ff986cd, BA_ACCESSOR.readInt(b, 43));
        b[47] = (byte) 0x32;
        b[48] = (byte) 0x79;
        b[49] = (byte) 0x06;
        b[50] = (byte) 0x80;
        assertEquals(0x80067932, BA_ACCESSOR.readInt(b, 47));
    }

    @Test
    public void testWriteInt() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeInt(b, 43, 0x7ff986cd);
        assertEquals((byte) 0xcd, b[43]);
        assertEquals((byte) 0x86, b[44]);
        assertEquals((byte) 0xf9, b[45]);
        assertEquals((byte) 0x7f, b[46]);
        BA_ACCESSOR.writeInt(b, 47, 0x80067932);
        assertEquals((byte) 0x32, b[47]);
        assertEquals((byte) 0x79, b[48]);
        assertEquals((byte) 0x06, b[49]);
        assertEquals((byte) 0x80, b[50]);
    }

    @Test
    public void testReadUnsignedInt() {
        byte[] b = new byte[128];
        b[43] = (byte) 0xed;
        b[44] = (byte) 0xab;
        b[45] = (byte) 0xda;
        b[46] = (byte) 0xfe;
        assertEquals(0xfedaabedL, BA_ACCESSOR.readUnsignedInt(b, 43));
        b[47] = (byte) 0x2a;
        b[48] = (byte) 0x00;
        b[49] = (byte) 0x00;
        b[50] = (byte) 0x00;
        assertEquals(0x2aL, BA_ACCESSOR.readUnsignedInt(b, 47));
    }

    @Test
    public void testWriteUnsignedInt() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeUnsignedInt(b, 43, 0xfedaabedL);
        assertEquals((byte) 0xed, b[43]);
        assertEquals((byte) 0xab, b[44]);
        assertEquals((byte) 0xda, b[45]);
        assertEquals((byte) 0xfe, b[46]);
        BA_ACCESSOR.writeUnsignedInt(b, 47, 0x2aL);
        assertEquals((byte) 0x2a, b[47]);
        assertEquals((byte) 0x00, b[48]);
        assertEquals((byte) 0x00, b[49]);
        assertEquals((byte) 0x00, b[50]);
    }

    @Test
    public void testReadLong() {
        byte[] b = new byte[128];
        b[43] = (byte) 0x4d;
        b[44] = (byte) 0x36;
        b[45] = (byte) 0x0b;
        b[46] = (byte) 0xa2;
        b[47] = (byte) 0x89;
        b[48] = (byte) 0xed;
        b[49] = (byte) 0xf0;
        b[50] = (byte) 0x7f;
        assertEquals(0x7ff0ed89a20b364dL, BA_ACCESSOR.readLong(b, 43));
        b[51] = (byte) 0xb2;
        b[52] = (byte) 0xc9;
        b[53] = (byte) 0xf4;
        b[54] = (byte) 0x5d;
        b[55] = (byte) 0x76;
        b[56] = (byte) 0x12;
        b[57] = (byte) 0x0f;
        b[58] = (byte) 0x80;
        assertEquals(0x800f12765df4c9b2L, BA_ACCESSOR.readLong(b, 51));
    }

    @Test
    public void testWriteLong() {
        byte[] b = new byte[128];
        BA_ACCESSOR.writeLong(b, 43, 0x7ff0ed89a20b364dL);
        assertEquals((byte) 0x4d, b[43]);
        assertEquals((byte) 0x36, b[44]);
        assertEquals((byte) 0x0b, b[45]);
        assertEquals((byte) 0xa2, b[46]);
        assertEquals((byte) 0x89, b[47]);
        assertEquals((byte) 0xed, b[48]);
        assertEquals((byte) 0xf0, b[49]);
        assertEquals((byte) 0x7f, b[50]);
        BA_ACCESSOR.writeLong(b, 51, 0x800f12765df4c9b2L);
        assertEquals((byte) 0xb2, b[51]);
        assertEquals((byte) 0xc9, b[52]);
        assertEquals((byte) 0xf4, b[53]);
        assertEquals((byte) 0x5d, b[54]);
        assertEquals((byte) 0x76, b[55]);
        assertEquals((byte) 0x12, b[56]);
        assertEquals((byte) 0x0f, b[57]);
        assertEquals((byte) 0x80, b[58]);
    }

    @Test
    public void testCopy() {
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
        BA_ACCESSOR.copy(from, 43, to, 42, 8);
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
