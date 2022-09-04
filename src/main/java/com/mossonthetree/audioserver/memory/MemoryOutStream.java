package com.mossonthetree.audioserver.memory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MemoryOutStream implements Closeable {
    private ByteArrayOutputStream bufaStream;

    private boolean inError;

    private String error;

    private int offset;

    public MemoryOutStream() {
        bufaStream = new ByteArrayOutputStream();
        inError = false;
    }

    public boolean isInError() {
        return inError;
    }

    public String getError() {
        return error;
    }

    public int size() {
        return bufaStream.size();
    }

    public byte[] getBuffer() {
        return bufaStream.toByteArray();
    }

    public int write(int data, boolean flipBytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(data);
        byte[] buffer = byteBuffer.array();
        if(flipBytes) {
            byte temp;
            for(int i = 0; i < buffer.length / 2; ++i) {
                temp = buffer[i];
                buffer[i] = buffer[buffer.length - i - 1];
                buffer[buffer.length - i - 1] = temp;
            }
        }
        return writeSome(byteBuffer.array());
    }

    public int write(long data, boolean flipBytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(data);
        byte[] buffer = byteBuffer.array();
        if(flipBytes) {
            byte temp;
            for(int i = 0; i < buffer.length / 2; ++i) {
                temp = buffer[i];
                buffer[i] = buffer[buffer.length - i - 1];
                buffer[buffer.length - i - 1] = temp;
            }
        }
        return writeSome(byteBuffer.array());
    }

    public int writeSome(byte[] buffer) {
        try {
            bufaStream.write(buffer);
        } catch (Exception ex) {
            inError = true;
            error = String.format("%s %s", ex.getClass().getName(), ex.getMessage());
            return -1;
        }
        return buffer.length;
    }

    @Override
    public void close() {
        try {
            bufaStream.close();
        } catch (Exception ignored) {}
    }
}
