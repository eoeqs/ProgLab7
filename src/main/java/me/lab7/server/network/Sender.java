package me.lab7.server.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Sender implements Runnable {

    private final DatagramSocket socket;
    private final DatagramPacket packet;

    public Sender(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run() {
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
