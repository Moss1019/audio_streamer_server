package com.mossonthetree.audioserver.file;

import java.io.*;

public class FileOutStream implements Closeable {
    private FileOutputStream foStream;

    private boolean inError;

    private String error;

    public FileOutStream(String fileName) {
        try {
            foStream = new FileOutputStream(fileName);
            inError = false;
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }

    public boolean isInError() {
        return inError;
    }

    public String getError() {
        return error;
    }

    public int writeSome(byte[] buffer) {
        if(inError) {
            return -1;
        }
        try {
            foStream.write(buffer);
            return buffer.length;
        } catch (Exception ex) {
            inError = true;
            error = String.format("%s %s", ex.getClass().getName(), ex.getMessage());
            return -1;
        }
    }

    @Override
    public void close() {
        try {
            foStream.close();
        } catch (Exception ignored) {}
    }
}
