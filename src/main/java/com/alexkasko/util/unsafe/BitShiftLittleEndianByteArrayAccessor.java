package com.alexkasko.util.unsafe;

import static java.lang.System.arraycopy;

/**
 * User: alexkasko
 * Date: 12/11/12
 */

class BitShiftLittleEndianByteArrayAccessor extends ByteArrayAccessor {
    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public byte readByte(byte[] data, long offset) {
        int off = (int) offset;
        return data[off];
    }

    @Override
    public void writeByte(byte[] data, long offset, byte value) {
        int off = (int) offset;
        data[off] = value;
    }

    @Override
    public short readUnsignedByte(byte[] data, long offset) {
        int off = (int) offset;
        return (short) (data[off] & 0xff);
    }

    @Override
    public void writeUnsignedByte(byte[] data, long offset, short value) {
        int off = (int) offset;
        data[off] = (byte) value;
    }

    @Override
    public short readShort(byte[] data, long offset) {
        int off = (int) offset;
        return (short) ((data[off + 0] & 0xff) << 0 |
                        (data[off + 1] & 0xff) << 8);
    }

    @Override
    public void writeShort(byte[] data, long offset, short value) {
        int off = (int) offset;
        data[off + 0] = (byte) (value >>> 0);
        data[off + 1] = (byte) (value >>> 8);
    }

    @Override
    public int readUnsignedShort(byte[] data, long offset) {
        int off = (int) offset;
        return ((data[off + 0] & 0xff) << 0 |
                (data[off + 1] & 0xff) << 8) & 0xffff;
    }

    @Override
    public void writeUnsignedShort(byte[] data, long offset, int value) {
        int off = (int) offset;
        data[off + 0] = (byte) (value >>> 0);
        data[off + 1] = (byte) (value >>> 8);
    }

    @Override
    public int readInt(byte[] data, long offset) {
        int off = (int) offset;
        return (data[off + 0] & 0xff) << 0 |
               (data[off + 1] & 0xff) << 8 |
               (data[off + 2] & 0xff) << 16 |
               (data[off + 3] & 0xff) << 24;
    }

    @Override
    public void writeInt(byte[] data, long offset, int value) {
        int off = (int) offset;
        data[off + 0] = (byte) (value >>> 0);
        data[off + 1] = (byte) (value >>> 8);
        data[off + 2] = (byte) (value >>> 16);
        data[off + 3] = (byte) (value >>> 24);
    }

    @Override
    public long readUnsignedInt(byte[] data, long offset) {
        int off = (int) offset;
        return ((data[off + 0] & 0xff) << 0 |
                (data[off + 1] & 0xff) << 8 |
                (data[off + 2] & 0xff) << 16 |
                (data[off + 3] & 0xff) << 24) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(byte[] data, long offset, long value) {
        int off = (int) offset;
        data[off + 0] = (byte) (value >>> 0);
        data[off + 1] = (byte) (value >>> 8);
        data[off + 2] = (byte) (value >>> 16);
        data[off + 3] = (byte) (value >>> 24);
    }

    @Override
    public long readLong(byte[] data, long offset) {
        int off = (int) offset;
        return (data[off + 0] & 0xffL) << 0 |
               (data[off + 1] & 0xffL) << 8 |
               (data[off + 2] & 0xffL) << 16 |
               (data[off + 3] & 0xffL) << 24 |
               (data[off + 4] & 0xffL) << 32 |
               (data[off + 5] & 0xffL) << 40 |
               (data[off + 6] & 0xffL) << 48 |
               (data[off + 7] & 0xffL) << 56;
    }

    @Override
    public void writeLong(byte[] data, long offset, long value) {
        int off = (int) offset;
        data[off + 0] = (byte) (value >>> 0);
        data[off + 1] = (byte) (value >>> 8);
        data[off + 2] = (byte) (value >>> 16);
        data[off + 3] = (byte) (value >>> 24);
        data[off + 4] = (byte) (value >>> 32);
        data[off + 5] = (byte) (value >>> 40);
        data[off + 6] = (byte) (value >>> 48);
        data[off + 7] = (byte) (value >>> 56);
    }

    @Override
    public void copy(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
        arraycopy(input, inputIndex, output, outputIndex, length);
    }
}
