package me.lab7.client.network;

import com.google.common.primitives.Bytes;
import me.lab7.client.exceptions.TooBigDataException;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.AuthResponse;
import me.lab7.common.utility.ChunkOrganizer;
import me.lab7.common.network.Request;
import me.lab7.common.network.Response;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UDPClient {

    private final static int packageSize = (int) Math.pow(2, 12);
    private final static int dataSize = (int) Math.pow(2, 12) - 1;
    private final DatagramChannel client;
    private final InetSocketAddress addr;

    public UDPClient(InetAddress address, int port) throws IOException {
        this.addr = new InetSocketAddress(address, port);
        this.client = DatagramChannel.open().bind(null).connect(addr);
        this.client.configureBlocking(false);
    }

    public Response communicateWithServer(Request request) throws IOException {
        try {
            byte[] data = SerializationUtils.serialize(request);
            byte[] responseBytes = sendAndReceiveData(data);
            try {
                return SerializationUtils.deserialize(responseBytes);
            } catch (SerializationException e) {
                return new Response("The received response is impossible to deserialize. Please, try again.\n");
            }
        } catch (SerializationException e) {
            return new Response("This request is impossible to serialize, thus it can't be sent to the server.\n");
        } catch (TooBigDataException e) {
            return new Response("The received response data is too big to deserialize, thus it can't be displayed.\n");
        }
    }

    public AuthResponse authorize(AuthRequest request) throws IOException {
        try {
            byte[] data = SerializationUtils.serialize(request);
            byte[] responseBytes = sendAndReceiveData(data);
            try {
                return SerializationUtils.deserialize(responseBytes);
            } catch (SerializationException e) {
                return new AuthResponse(false, "The received response is impossible to deserialize. Please, try again.\n");
            }
        } catch (SerializationException e) {
            return new AuthResponse(false, "The authorization request is impossible to serialize. Please, use different username or password.\n");
        } catch (TooBigDataException e) {
            return new AuthResponse(false, "The received response data is too big to deserialize, thus it can't be displayed.\n");
        }
    }

    private void sendData(byte[] data) throws IOException {
        byte[][] chunks = ChunkOrganizer.divideIntoChunks(data, dataSize);
        for (int i = 0; i < chunks.length; i++) {
            byte[] chunk = chunks[i];
            if (i == chunks.length - 1) {
                byte[] lastChunk = Bytes.concat(chunk, new byte[]{1});
                client.send(ByteBuffer.wrap(lastChunk), addr);
            } else {
                byte[] nextChunk = Bytes.concat(chunk, new byte[]{0});
                client.send(ByteBuffer.wrap(nextChunk), addr);
            }
        }
    }

    private byte[] receivePacket() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(packageSize);
        SocketAddress address = null;
        while (address == null) {
            address = client.receive(buffer);
        }
        return buffer.array();
    }

    private byte[] receiveData() throws IOException, TooBigDataException {
        List<byte[]> chunks = new ArrayList<>();
        int totalChunks = Integer.MAX_VALUE;
        int chunkCount = 0;
        do {
            if (chunks.stream().mapToInt(b -> b.length).sum() > 131000) {
                throw new TooBigDataException();
            }
            byte[] chunk = receivePacket();
            chunkCount++;
            int number = chunk[0];
            if (chunk[chunk.length - 1] == 1) {
                totalChunks = number + 1;
            }
            chunks.add(number, Arrays.copyOfRange(chunk, 1, chunk.length - 1));
        } while (chunkCount != totalChunks);
        return ChunkOrganizer.reassembleChunks(chunks);
    }

    private byte[] sendAndReceiveData(byte[] data) throws IOException, TooBigDataException {
        sendData(data);
        return receiveData();
    }

}
