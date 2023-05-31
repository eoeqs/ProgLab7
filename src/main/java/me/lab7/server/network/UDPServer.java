package me.lab7.server.network;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import me.lab7.server.managers.CommandManager;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Scanner;

public class UDPServer {

    private final static int packageSize = (int) Math.pow(2, 14);
    private final static int dataSize = (int) Math.pow(2, 14) - 2;
    private final DatagramSocket socket;
    private final InetSocketAddress address;
    private final CommandManager commandManager;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UDPServerOld.class);

    public UDPServer(InetAddress address, int port, CommandManager commandManager) throws SocketException {
        logger.setLevel(Level.INFO);
        this.address = new InetSocketAddress(address, port);
        this.commandManager = commandManager;
        socket = new DatagramSocket(this.address);
        socket.setReuseAddress(true);
    }

    private void connect(SocketAddress address) throws SocketException {
        socket.connect(address);
    }

    private void disconnect() {
        socket.disconnect();
    }

    private void close() {
        socket.close();
    }

    public void start() {
        System.out.println("Server started.");
        logger.info("Server started at " + address);
        Thread requestThread = new Thread(new RequestManager(socket, packageSize, dataSize, commandManager));
        requestThread.start();
        while (true) {
            if (new Scanner(System.in).nextLine().trim().equalsIgnoreCase("exit")) {
                System.exit(0);
            } else {
                System.out.println("You can shut down the server by typing 'exit'.");
            }
        }
    }
}
