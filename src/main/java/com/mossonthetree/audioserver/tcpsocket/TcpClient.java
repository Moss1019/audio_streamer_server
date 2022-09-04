package com.mossonthetree.audioserver.tcpsocket;

import com.mossonthetree.audioserver.memory.MemoryInStream;
import com.mossonthetree.audioserver.memory.MemoryOutStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.Function;

public class TcpClient implements Closeable {
    private Socket socket;

    private OnReceiveSig<TcpClient, TcpMessage, Boolean> onReceive;

    private Thread worker;

    private boolean running;

    private boolean inError;

    private String error;

    public TcpClient(Socket socket, OnReceiveSig<TcpClient, TcpMessage, Boolean> onReceive) {
        this.socket = socket;
        this.onReceive = onReceive;
        inError = !socket.isConnected();
        if(inError) {
            error = "Socket not connected";
        }
    }

    public void start() {
        running = false;
        worker = new Thread(() -> {
            InputStream iStream;
            try {
                iStream = socket.getInputStream();
            } catch (Exception ex) {
                inError = true;
                error = "Could not get input stream";
                return;
            }
            running = true;
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            while(running) {
                int read = 0;
                MemoryOutStream moStream = new MemoryOutStream();
                do {
                    try {
                        read = iStream.read(buffer);
                        if(read == -1) {
                            inError = true;
                            running = false;
                            error = "Remote client closed connection";
                        } else {
                            moStream.writeSome(buffer);
                        }
                    } catch (Exception ex) {
                        inError = true;
                        running = false;
                        error = "Error while reading stream";
                        read = -1;
                    }
                } while(read >= bufferSize);
                TcpMessage message = new TcpMessage(moStream.getBuffer());
                if(!inError) {
                    onReceive.apply(this, message);
                }
                moStream.close();
            }
        });
        worker.start();
    }

    public void stop() {
        running = false;
    }

    public void send(byte[] data) {
        OutputStream oStream;
        try {
            oStream = socket.getOutputStream();
        } catch (Exception ex) {
            inError = true;
            error = "Could not get output stream";
            return;
        }
        try {
            oStream.write(data);
        } catch (Exception ex) {
            inError = true;
            error = "Could not write to output stream";
        }
    }

    @Override
    public void close() {
        running = false;
        try {
            worker.join();
        } catch (Exception ignored) {}
        try {
            socket.close();
        } catch (Exception ignored) {}
    }
}
