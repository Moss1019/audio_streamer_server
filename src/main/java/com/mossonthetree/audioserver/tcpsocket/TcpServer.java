package com.mossonthetree.audioserver.tcpsocket;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TcpServer implements Closeable {
    private ServerSocket socket;

    private Thread worker;

    private OnReceiveSig<TcpClient, TcpMessage, Boolean> onReceive;

    private boolean running;

    private boolean inError;

    private String error;

    private List<TcpClient> clients;

    public TcpServer(String ipAddress, int port, OnReceiveSig<TcpClient, TcpMessage, Boolean> onReceive) {
        InetSocketAddress endpoint = new InetSocketAddress(ipAddress, port);
        try {
            socket = new ServerSocket(port, 2, endpoint.getAddress());
            inError = false;
        } catch (Exception ex) {
            inError = true;
            error = String.format("%s %s", ex.getClass().getName(), ex.getMessage());
        }
        this.onReceive = onReceive;
        clients = new ArrayList<>();
    }

    public boolean isInError() {
        return inError;
    }

    public String getError() {
        return error;
    }

    public void start() {
        running = true;
        worker = new Thread(() -> {
            while(running) {
                Socket client;
                try {
                    client = socket.accept();
                } catch (Exception ignored) {
                    continue;
                }
                TcpClient newClient = new TcpClient(client, onReceive);
                clients.add(newClient);
                newClient.start();
            }
        });
        worker.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void close() {
        for(TcpClient client : clients) {
            client.close();
        }
        running = false;
        try {
            socket.close();
        } catch (Exception ignored) {}
        try {
            worker.join();
        } catch (Exception ignored) {}
    }
}
