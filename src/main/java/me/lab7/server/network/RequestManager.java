package me.lab7.server.network;

import ch.qos.logback.classic.Logger;
import com.google.common.primitives.Bytes;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.AuthResponse;
import me.lab7.common.network.Request;
import me.lab7.common.network.Response;
import me.lab7.common.utility.ChunkOrganizer;
import me.lab7.server.managers.CommandManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class RequestManager implements Runnable {

    private final DatagramSocket socket;
    private final int packageSize;
    private final int dataSize;
    private final CommandManager commandManager;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(RequestManager.class);

    public RequestManager(DatagramSocket socket, int packageSize, int dataSize, CommandManager commandManager) {
        this.socket = socket;
        this. packageSize = packageSize;
        this.dataSize = dataSize;
        this.commandManager = commandManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Pair<byte[], SocketAddress> dataPair = receiveData();
                byte[] data = dataPair.getLeft();
                SocketAddress address = dataPair.getRight();
                if (identifyRequestType(data)) {
                    Request request = SerializationUtils.deserialize(data);
                    Response response = handleCommandRequest(request, address);
                    if (response == null) {
                        logger.error("Failed to process the request on the server.");
                        response = new Response("There was an error while processing the request. Please, try again.");
                    }
                    byte[] responseData;
                    try {
                        responseData = SerializationUtils.serialize(response);
                    } catch (Exception e) {
                        logger.error("Failed to serialize response.");
                        responseData = SerializationUtils.serialize(new Response("The response for this request was impossible to serialize, thus it can't be displayed."));
                    }
                    try {
                        sendData(responseData, address);
                    } catch (Exception e) {
                        logger.error("Failed to send response.");
                    }
                } else {
                    AuthRequest request = SerializationUtils.deserialize(data);
                    AuthResponse response = handleAuthRequest(request, address);
                    if (response == null) {
                        logger.error("Failed to process the request on the server.");
                        response = new AuthResponse(false, "There was an error while processing the request. Please, try again.");
                    }
                    byte[] responseData = SerializationUtils.serialize(response);
                    try {
                        sendData(responseData, address);
                    } catch (Exception e) {
                        logger.error("Failed to send response.");
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to receive data: " + e);
            }
        }
    }

    private Pair<byte[], SocketAddress> receiveData() throws IOException {
        boolean received = false;
        byte[] result = new byte[0];
        SocketAddress address = null;
        while (!received) {
            byte[] data = new byte[packageSize];
            DatagramPacket packet = new DatagramPacket(data, packageSize);
            socket.receive(packet);
            address = packet.getSocketAddress();
            logger.info("Received " + new String(data) + " from " + packet.getAddress());
            logger.info("Last byte: " + data[data.length - 1]);
            if (data[data.length - 1] == 1) {
                received = true;
                logger.info("Receiving data from " + packet.getAddress() + " has just ended.");
            }
            result = Bytes.concat(result, Arrays.copyOf(data, data.length - 1));
        }
        return new ImmutablePair<>(result, address);
    }

    private boolean identifyRequestType(byte[] data) {
        return SerializationUtils.deserialize(data).getClass() == Request.class;
    }

    private Response handleCommandRequest(Request request, SocketAddress address) {
        logger.info("Processing " + request + " from " + address);
        Response response = null;
        try {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            response = forkJoinPool.invoke(new CommandTask(request, commandManager));
        } catch (Exception e) {
            logger.error("Failed to execute command: " + e.getMessage());
        }
        return response;
    }

    private AuthResponse handleAuthRequest(AuthRequest request, SocketAddress address) {
        logger.info("Processing " + request + " from " + address);
        AuthResponse response = null;
        try {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            response = forkJoinPool.invoke(new AuthTask(request));
        } catch (Exception e) {
            logger.error("Failed to authorize: " + e.getMessage());
        }
        return response;
    }

    private void sendData(byte[] data, SocketAddress address) {
        byte[][] chunks = ChunkOrganizer.divideIntoChunks(data, dataSize);
        logger.info("Sending " + chunks.length + " chunks...");
        for (int i = 0; i < chunks.length; i++) {
            byte[] chunk = chunks[i];
            byte[] numberedChunk = Bytes.concat(new byte[]{(byte) i}, chunk);
            if (i == chunks.length - 1) {
                numberedChunk = Bytes.concat(numberedChunk, new byte[]{1});
            }
            chunks[i] = numberedChunk;
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (byte[] chunk : chunks) {
            DatagramPacket packet = new DatagramPacket(chunk, chunk.length, address);
            executorService.execute(new Sender(socket, packet));
            logger.info("Chunk of size " + chunk.length + " has been sent to server.");
        }
        executorService.shutdown();
        logger.info("Finished sending chunks.");
    }
}
