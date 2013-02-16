package com.alexkasko.util.unsafe;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
class UnsafeByteArrayAccessor extends ByteArrayAccessor {

    private static final TheUnsafeAccessor UNSAFE = TheUnsafeAccessor.get();
    private static final int BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    @Override
    public boolean isUnsafe() {
        return true;
    }

    @Override
    public byte readByte(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 1;
        return UNSAFE.getByte(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void writeByte(byte[] data, long offset, byte value) {
        assert offset >= 0;
        assert offset <= data.length - 1;
        UNSAFE.putByte(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    @Override
    public short readUnsignedByte(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 1;
        return (short) (UNSAFE.getByte(data, BYTE_ARRAY_OFFSET + offset) & 0xff);
    }

    @Override
    public void writeUnsignedByte(byte[] data, long offset, short value) {
        assert offset >= 0;
        assert offset <= data.length - 1;
        UNSAFE.putByte(data, BYTE_ARRAY_OFFSET + offset, (byte) value);
    }

    @Override
    public short readShort(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 2;
        return UNSAFE.getShort(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void writeShort(byte[] data, long offset, short value) {
        assert offset >= 0;
        assert offset <= data.length - 2;
        UNSAFE.putShort(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    @Override
    public int readUnsignedShort(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 2;
        return UNSAFE.getShort(data, BYTE_ARRAY_OFFSET + offset) & 0xffff;

    }

    @Override
    public void writeUnsignedShort(byte[] data, long offset, int value) {
        assert offset >= 0;
        assert offset <= data.length - 2;
        UNSAFE.putShort(data, BYTE_ARRAY_OFFSET + offset, (short) value);
    }

    @Override
    public int readInt(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 4;
        return UNSAFE.getInt(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void writeInt(byte[] data, long offset, int value) {
        assert offset >= 0;
        assert offset <= data.length - 4;
        UNSAFE.putInt(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    @Override
    public long readUnsignedInt(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 4;
        return UNSAFE.getInt(data, BYTE_ARRAY_OFFSET + offset) & 0xffffffffL;
    }

    @Override
    public void writeUnsignedInt(byte[] data, long offset, long value) {
        assert offset >= 0;
        assert offset <= data.length - 4;
        UNSAFE.putInt(data, BYTE_ARRAY_OFFSET + offset, (int) value);
    }

    @Override
    public long readLong(byte[] data, long offset) {
        assert offset >= 0;
        assert offset <= data.length - 8;
        return UNSAFE.getLong(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void writeLong(byte[] data, long offset, long value) {
        assert offset >= 0;
        assert offset <= data.length - 8;
        UNSAFE.putLong(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    @Override
    public void copy(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
        assert inputIndex >= 0;
        assert inputIndex <= input.length - length;
        assert outputIndex >= 0;
        assert outputIndex <= output.length - length;
        UNSAFE.copyMemory(input, BYTE_ARRAY_OFFSET + inputIndex, output, BYTE_ARRAY_OFFSET + outputIndex, length);
    }
}
