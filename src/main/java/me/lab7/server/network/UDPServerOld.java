package me.lab7.server.network;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.primitives.Bytes;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.AuthResponse;
import me.lab7.common.network.Request;
import me.lab7.common.network.Response;
import me.lab7.common.utility.ChunkOrganizer;
import me.lab7.server.io.ServerConsole;
import me.lab7.server.managers.CommandManager;
import me.lab7.server.managers.databaseManagers.AuthenticationManager;
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

public class UDPServerOld {

    private final static int packageSize = (int) Math.pow(2, 14);
    private final static int dataSize = (int) Math.pow(2, 14) - 1;
    private final DatagramSocket socket;
    private final InetSocketAddress address;
    private final CommandManager commandManager;
    private final ServerConsole console;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UDPServerOld.class);

    public UDPServerOld(InetAddress address, int port, CommandManager commandManager, ServerConsole console) throws SocketException {
        logger.setLevel(Level.INFO);
        this.address = new InetSocketAddress(address, port);
        this.commandManager = commandManager;
        this.console = console;
        socket = new DatagramSocket(getAddress());
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

    public InetSocketAddress getAddress() {
        return address;
    }

    private Pair<Byte[], SocketAddress> receiveData() throws IOException {
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

    private void sendData(byte[] data, SocketAddress address) throws IOException {
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

    private boolean identifyRequestType(byte[] clientData) throws SerializationException {
        return SerializationUtils.deserialize(clientData).getClass() == Request.class;
    }

    private Response handleRegularRequest(byte[] clientData) {
        Request request = SerializationUtils.deserialize(clientData);
        logger.info("Processing " + request + " from " + address);
        Response response = null;
        try {
            response = commandManager.handleRequest(request);
        } catch (Exception e) {
            logger.error("Failed to execute command: " + e.getMessage());
        }
        return response;
    }

    private AuthResponse handleAuthRequest(byte[] clientData) {
        AuthRequest request = SerializationUtils.deserialize(clientData);
        logger.info("Processing " + request + " from " + address);
        if (request.logInOrRegister()) {
            return handleLogInRequest(request);
        } else {
            return handleRegisterRequest(request);
        }
    }

    private AuthResponse handleLogInRequest(AuthRequest request) {
        if (AuthenticationManager.checkUserName(request.username())) {

        }
        return null;
    }

    private AuthResponse handleRegisterRequest(AuthRequest request) {
        return null;
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
            byte[] clientData = ArrayUtils.toPrimitive(pair.getKey());
            SocketAddress address = pair.getValue();
            try {
                connect(address);
                logger.info("Connected to " + address);
            } catch (Exception e) {
                logger.error("FAILED to connect: " + e.getMessage());
            }
            Response response;
            boolean requestType;
            try {
                requestType = identifyRequestType(clientData);
            } catch (SerializationException e) {
                logger.error("Failed to deserialize received data.");
                response = new Response("The sent request was too big for the server to process. Please, try again.");
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
            if (requestType) {
                try {
                    response = handleRegularRequest(clientData);
                    byte[] responseData = SerializationUtils.serialize(response);
                    logger.info("Response: " + response);
                    sendData(responseData, address);
                    logger.info("Response has been sent to client " + address);
                } catch (SerializationException e) {
                    logger.error("Failed to serialize the response data.");
                    response = new Response("The response data was impossible to serialize.\n");
                    byte[] responseData = SerializationUtils.serialize(response);
                    logger.info("Response: " + response);
                    try {
                        sendData(responseData, address);
                    } catch (IOException ex) {
                        logger.error("Failed to send response due to an IO error.");
                    }
                } catch (IOException e) {
                    logger.error("Failed to send response due to an IO error.");
                }
            } else {
                AuthResponse authResponse = handleAuthRequest(clientData);
                byte[] responseData = SerializationUtils.serialize(authResponse);
                logger.info("Auth response: " + authResponse);
                try {
                    sendData(responseData, address);
                    logger.info("Auth response has been sent to client " + address);
                } catch (IOException e) {
                    logger.error("Failed to send response due to an IO error.");
                }
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
