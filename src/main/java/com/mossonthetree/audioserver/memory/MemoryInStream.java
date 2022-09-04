package com.mossonthetree.audioserver.memory;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MemoryInStream implements Closeable {
    private final ByteArrayInputStream biStream;

    private int length;

    private int offset;

    public MemoryInStream(byte[] buffer) {
        biStream = new ByteArrayInputStream(buffer);
        length = buffer.length;
        offset = 0;
    }

    public int available() {
        return length - offset;
    }

    public int readInt(boolean flipBytes) {
        byte[] buffer = new byte[Integer.BYTES];
        biStream.read(buffer, 0, buffer.length);
        if(flipBytes) {
            byte temp;
            for(int i = 0; i < buffer.length / 2; ++i) {
                temp = buffer[i];
                buffer[i] = buffer[buffer.length - i - 1];
                buffer[buffer.length - i - 1] = temp;
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.put(buffer);
        byteBuffer.rewind();
        offset += Integer.BYTES;
        return byteBuffer.getInt();
    }

    public long readLong(boolean flipBytes) {
        byte[] buffer = new byte[Long.BYTES];
        biStream.read(buffer, 0, buffer.length);
        if(flipBytes) {
            byte temp;
            for(int i = 0; i < buffer.length / 2; ++i) {
                temp = buffer[i];
                buffer[i] = buffer[buffer.length - i - 1];
                buffer[buffer.length - i - 1] = temp;
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.put(buffer);
        byteBuffer.rewind();
        offset += Long.BYTES;
        return byteBuffer.getLong();
    }

    public short readShort(boolean flipBytes) {
        byte[] buffer = new byte[Short.BYTES];
        biStream.read(buffer, 0, buffer.length);
        if(flipBytes) {
            byte temp;
            for(int i = 0; i < buffer.length / 2; ++i) {
                temp = buffer[i];
                buffer[i] = buffer[buffer.length - i - 1];
                buffer[buffer.length - i - 1] = temp;
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.put(buffer);
        byteBuffer.rewind();
        offset += Long.BYTES;
        return byteBuffer.getShort();
    }

    public int readSome(byte[] buffer, int length) {
        int read = biStream.read(buffer, 0, length);
        offset += read;
        return read;
    }

    @Override
    public void close() {
        try {
            biStream.close();
        } catch (Exception ignored) {}
    }
}
