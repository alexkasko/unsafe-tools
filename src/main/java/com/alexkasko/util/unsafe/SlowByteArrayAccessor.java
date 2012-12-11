package com.alexkasko.util.unsafe;

import static java.lang.System.arraycopy;

/**
 * User: alexkasko
 * Date: 12/11/12
 */

// todo add checks
class SlowByteArrayAccessor implements ByteArrayAccessor {
    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public byte readByte(byte[] data, int offset) {
        return data[offset];
    }

    @Override
    public void writeByte(byte[] data, int offset, byte value) {
        data[offset] = value;
    }

    @Override
    public short readUnsignedByte(byte[] data, int offset) {
        return (short) (data[offset] & 0xff);
    }

    @Override
    public void writeUnsignedByte(byte[] data, int offset, short value) {
        data[offset] = (byte) value;
    }

    @Override
    public short readShort(byte[] data, int offset) {
        return (short) ((data[offset + 0] & 0xff) << 0 |
                        (data[offset + 1] & 0xff) << 8);
    }

    @Override
    public void writeShort(byte[] data, int offset, short value) {
        data[offset + 0] = (byte) (value >> 0);
        data[offset + 1] = (byte) (value >> 8);
    }

    @Override
    public int readUnsignedShort(byte[] data, int offset) {
        return ((data[offset + 0] & 0xff) << 0 |
                (data[offset + 1] & 0xff) << 8) & 0xffff;
    }

    @Override
    public void writeUnsignedShort(byte[] data, int offset, int value) {
        data[offset + 0] = (byte) (value >> 0);
        data[offset + 1] = (byte) (value >> 8);
    }

    @Override
    public int readInt(byte[] data, int offset) {
        return (data[offset + 0] & 0xff) << 0 |
               (data[offset + 1] & 0xff) << 8 |
               (data[offset + 2] & 0xff) << 16 |
               (data[offset + 3] & 0xff) << 24;
    }

    @Override
    public void writeInt(byte[] data, int offset, int value) {
        data[offset + 0] = (byte) (value >> 0);
        data[offset + 1] = (byte) (value >> 8);
        data[offset + 2] = (byte) (value >> 16);
        data[offset + 3] = (byte) (value >> 24);
    }

    @Override
    public long readUnsignedInt(byte[] data, int offset) {
        return ((data[offset + 0] & 0xff) << 0 |
                (data[offset + 1] & 0xff) << 8 |
                (data[offset + 2] & 0xff) << 16 |
                (data[offset + 3] & 0xff) << 24) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(byte[] data, int offset, long value) {
        data[offset + 0] = (byte) (value >> 0);
        data[offset + 1] = (byte) (value >> 8);
        data[offset + 2] = (byte) (value >> 16);
        data[offset + 3] = (byte) (value >> 24);
    }

    @Override
    public long readLong(byte[] data, int offset) {
        return (data[offset + 0] & 0xffL) << 0 |
               (data[offset + 1] & 0xffL) << 8 |
               (data[offset + 2] & 0xffL) << 16 |
               (data[offset + 3] & 0xffL) << 24 |
               (data[offset + 4] & 0xffL) << 32 |
               (data[offset + 5] & 0xffL) << 40 |
               (data[offset + 6] & 0xffL) << 48 |
               (data[offset + 7] & 0xffL) << 56;
    }

    @Override
    public void writeLong(byte[] data, int offset, long value) {
        data[offset + 0] = (byte) (value >> 0);
        data[offset + 1] = (byte) (value >> 8);
        data[offset + 2] = (byte) (value >> 16);
        data[offset + 3] = (byte) (value >> 24);
        data[offset + 4] = (byte) (value >> 32);
        data[offset + 5] = (byte) (value >> 40);
        data[offset + 6] = (byte) (value >> 48);
        data[offset + 7] = (byte) (value >> 56);
    }

    @Override
    public void copy(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
        arraycopy(input, inputIndex, output, outputIndex, length);
    }
}
