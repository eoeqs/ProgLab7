package me.lab7.server.network.requestHandlers;

import ch.qos.logback.classic.Logger;
import com.google.common.primitives.Bytes;
import me.lab7.common.network.Response;
import me.lab7.common.utility.ChunkOrganizer;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class ResponseSender implements Runnable {

    private final DatagramSocket socket;
    private final Response response;
    private final SocketAddress address;
    private final int dataSize;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ResponseSender.class);

    public ResponseSender(DatagramSocket socket, Response response, SocketAddress address, int dataSize) {
        this.socket = socket;
        this.response = response;
        this.address = address;
        this.dataSize = dataSize;
    }

    @Override
    public void run() {
        byte[] sendData = SerializationUtils.serialize(response);
        byte[][] chunks = ChunkOrganizer.divideIntoChunks(sendData, dataSize);
        logger.info("Sending " + chunks.length + " chunks...");
        for (int i = 0; i < chunks.length; i++) {
            byte[] chunk = chunks[i];
            byte[] numberedChunk = Bytes.concat(new byte[]{(byte) i}, chunk);
            if (i == chunks.length - 1) {
                numberedChunk = Bytes.concat(numberedChunk, new byte[]{1});
            }
            chunks[i] = numberedChunk;
        }
        for (byte[] chunk : chunks) {
            DatagramPacket packet = new DatagramPacket(chunk, chunk.length, address);
            try {
                socket.send(packet);
            } catch (IOException e) {
                logger.error("Failed to send response to " + address);
                return;
            }
            logger.info("Chunk of size " + chunk.length + " has been sent to server.");
        }
        logger.info("Finished sending chunks.");
    }
}
