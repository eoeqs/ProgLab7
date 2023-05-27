package me.lab6.client.network;

import com.google.common.primitives.Bytes;
import me.lab6.client.exceptions.TooBigDataException;
import me.lab6.common.utility.ChunkOrganizer;
import me.lab6.common.network.Request;
import me.lab6.common.network.Response;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class UDPClient {

    private final static int packageSize = (int) Math.pow(2, 14);
    private final static int dataSize = (int) Math.pow(2, 14) - 1;
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
                return new Response("The received response is impossible to deserialize.\n");
            }
        } catch (SerializationException e) {
            return new Response("This request is impossible to serialize, thus it can't be sent to the server.\n");
        } catch (TooBigDataException e) {
            return new Response("The received response data is too big to deserialize, thus it can't be displayed.\n");
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
        boolean received = false;
        byte[] result = new byte[0];
        while (!received) {
            if (result.length > 65000) {
                throw new TooBigDataException();
            }
            byte[] data = receivePacket();
            if (data[data.length - 1] == 1) {
                received = true;
            }
            result = Bytes.concat(result, Arrays.copyOf(data, data.length - 1));
        }
        return result;
    }

    private byte[] sendAndReceiveData(byte[] data) throws IOException, TooBigDataException {
        sendData(data);
        return receiveData();
    }

}
