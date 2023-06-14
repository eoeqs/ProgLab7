package me.lab7.server.network.requestHandlers;

import ch.qos.logback.classic.Logger;
import com.google.common.primitives.Bytes;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.CommandRequest;
import me.lab7.server.managers.CommandManager;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class RequestReceiver implements Runnable {

    private final DatagramSocket socket;
    private final int packageSize;
    private final int dataSize;
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private static final Logger logger = (Logger) LoggerFactory.getLogger(RequestReceiver.class);
    private final CommandManager commandManager;

    public RequestReceiver(DatagramSocket socket, int packageSize, int dataSize, CommandManager commandManager) {
        this.socket = socket;
        this.packageSize = packageSize;
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
                    CommandRequest commandRequest = SerializationUtils.deserialize(data);
                    delegateRequestProcessing(commandRequest, address);
                } else {
                    AuthRequest authRequest = SerializationUtils.deserialize(data);
                    delegateRequestProcessing(authRequest, address);
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
        return SerializationUtils.deserialize(data).getClass() == CommandRequest.class;
    }

    private void delegateRequestProcessing(CommandRequest commandRequest, SocketAddress address) {
        logger.info("Processing " + commandRequest + " from " + address);
        forkJoinPool.invoke(new CommandTask(socket, commandRequest, commandManager, address, dataSize));
    }

    private void delegateRequestProcessing(AuthRequest request, SocketAddress address) {
        logger.info("Processing " + request + " from " + address);
        forkJoinPool.invoke(new AuthTask(socket, request, address, dataSize));
    }

}
