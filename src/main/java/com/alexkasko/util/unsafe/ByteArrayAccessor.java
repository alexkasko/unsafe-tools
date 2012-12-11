package com.alexkasko.util.unsafe;

/**
 * User: alexkasko
 * Date: 12/11/12
 */
public interface ByteArrayAccessor {
    boolean isUnsafe();

    byte readByte(byte[] data, int offset);

    void writeByte(byte[] data, int offset, byte value);

    short readUnsignedByte(byte[] data, int offset);

    void writeUnsignedByte(byte[] data, int offset, short value);

    short readShort(byte[] data, int offset);

    void writeShort(byte[] data, int offset, short value);

    int readUnsignedShort(byte[] data, int offset);

    void writeUnsignedShort(byte[] data, int offset, int value);

    int readInt(byte[] data, int offset);

    void writeInt(byte[] data, int offset, int value);

    long readUnsignedInt(byte[] data, int offset);

    void writeUnsignedInt(byte[] data, int offset, long value);

    long readLong(byte[] data, int offset);

    void writeLong(byte[] data, int offset, long value);

    void copy(byte[] input, int inputIndex, byte[] output, int outputIndex, int length);
}
