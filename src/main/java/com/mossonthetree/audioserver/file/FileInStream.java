package com.mossonthetree.audioserver.file;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;

public class FileInStream implements Closeable {
    private FileInputStream fiStream;

    private boolean inError;

    private String error;

    private int size;

    private int offset;

    public FileInStream(String fileName) {
        try {
            fiStream = new FileInputStream(fileName);
            inError = false;
            size = fiStream.available();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            size = 0;
        }
        offset = 0;
    }

    public boolean isInError() {
        return inError;
    }

    public String getError() {
        return error;
    }

    public int getSize() {
        return size;
    }

    public int available() {
        return size - offset;
    }

    public int readSome(byte[] buffer) {
        if(inError) {
            return -1;
        }
        try {
            int length = buffer.length;
            int newLength = offset + length;
            if(newLength > size) {
                length = newLength - size;
            }
            int bytesRead = fiStream.read(buffer, 0, length);
            offset += bytesRead;
            return bytesRead;
        } catch (Exception ex) {
            inError = true;
            error = String.format("%s %s", ex.getClass().getName(), ex.getMessage());
            return -1;
        }
    }

    @Override
    public void close() {
        try {
            fiStream.close();
        } catch (Exception ignored) {}
    }
}
