package me.lab6.server.network;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.primitives.Bytes;
import me.lab6.common.utility.ChunkOrganizer;
import me.lab6.common.network.Request;
import me.lab6.common.network.Response;
import me.lab6.server.io.ServerConsole;
import me.lab6.server.managers.CommandManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UDPServer {

    private final static int packageSize = (int) Math.pow(2, 14);
    private final static int dataSize = (int) Math.pow(2, 14) - 1;
    private final DatagramSocket socket;
    private final InetSocketAddress address;
    private final CommandManager commandManager;
    private final ServerConsole console;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UDPServer.class);



    public UDPServer(InetAddress address, int port, CommandManager commandManager, ServerConsole console) throws SocketException {
        logger.setLevel(Level.INFO);
        this.address = new InetSocketAddress(address, port);
        this.commandManager = commandManager;
        this.console = console;
        socket = new DatagramSocket(getAddress());
        socket.setReuseAddress(true);
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Pair<Byte[], SocketAddress> receiveData() throws IOException {
        boolean received = false;
        byte[] result = new byte[0];
        SocketAddress address = null;

        while (!received) {
            socket.setSoTimeout(300);
            console.handleServerInput();
            byte[] data = new byte[packageSize];

            DatagramPacket packet = new DatagramPacket(data, packageSize);

            socket.receive(packet);
            address = packet.getSocketAddress();
            logger.info("Received \"" + new String(data) + "\" from " + packet.getAddress());
            logger.info("Last byte: " + data[data.length - 1]);
            if (data[data.length - 1] == 1) {
                received = true;
                logger.info("Receiving data from " + packet.getAddress() + " has just ended.");
            }

            result = Bytes.concat(result, Arrays.copyOf(data, data.length - 1));
        }
        return new ImmutablePair<>(ArrayUtils.toObject(result), address);
    }

    public void sendData(byte[] data, SocketAddress address) throws IOException {
        byte[][] chunks = ChunkOrganizer.divideIntoChunks(data, dataSize);
        logger.info("Sending " + chunks.length + " chunks...");

        for (int i = 0; i < chunks.length; i++) {
            byte[] chunk = chunks[i];
            if (i == chunks.length - 1) {
                byte[] lastChunk = Bytes.concat(chunk, new byte[]{1});
                DatagramPacket packet = new DatagramPacket(lastChunk, packageSize, address);
                socket.send(packet);
                logger.info("Last chunk of size " + chunk.length + " has been sent to server.");

            } else {
                DatagramPacket packet = new DatagramPacket(
                        ByteBuffer.allocate(packageSize).put(chunk).array(), packageSize, address);
                socket.send(packet);
                logger.info("Chunk of size " + chunk.length + " has been sent to server.");

            }
        }
        logger.info("Finished sending data.");

    }

    public void connect(SocketAddress address) throws SocketException {
        socket.connect(address);
    }

    public void disconnect() {
        socket.disconnect();
    }

    public void close() {
        socket.close();
    }

    public void run() {
        System.out.println("Server started.");
        logger.info("Server started at " + address);
        while (true) {
            Pair<Byte[], SocketAddress> pair;
            try {
                pair = receiveData();
            } catch (Exception e) {
                disconnect();
                continue;
            }
            Byte[] clientData = pair.getKey();
            SocketAddress address = pair.getValue();
            try {
                connect(address);
                logger.info("Connected to " + address);
            } catch (Exception e) {
                logger.error("FAILED to connect: " + e.getMessage());
            }
            Request request;
            try {
                request = SerializationUtils.deserialize(ArrayUtils.toPrimitive(clientData));
                logger.info("Processing " + request + " from " + address);
            } catch (SerializationException e) {
                logger.error("Failed to deserialize received data.");
                Response response = new Response("The sent request was too big for the server to process. Please, try again.");
                byte[] data = SerializationUtils.serialize(response);
                try {
                    sendData(data, address);
                    logger.info("Response has been sent to client " + address);
                } catch (Exception t) {
                    logger.error("Failed to send response due to an IO error.");
                }
                disconnect();
                logger.info("Disconnecting from client " + address);
                continue;
            }
            Response response = null;
            try {
                response = commandManager.handleRequest(request);
            } catch (Exception e) {
                logger.error("Failed to execute command: " + e.getMessage());
            }
            byte[] data = SerializationUtils.serialize(response);
            logger.info("Response: " + response);

            try {
                sendData(data, address);
                logger.info("Response has been sent to client " + address);
            } catch (Exception e) {
                logger.error("Failed to send response due to an IO error.");
            }
            disconnect();
            logger.info("Disconnecting from client " + address);

            if (console.handleServerInput()) {
                break;
            }
        }
        close();
    }

}
