package me.lab7.common.utility;

import java.util.Arrays;
import java.util.List;

public class ChunkOrganizer {

    public static byte[][] divideIntoChunks(byte[] data, int dataSize) {
        byte[][] chunks = new byte[(int) Math.ceil((double) data.length / dataSize)][dataSize];
        int start = 0;
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = Arrays.copyOfRange(data, start, start + dataSize);
            start += dataSize;
        }
        return chunks;
    }

    public static byte[] reassembleChunks(List<byte[]> chunks) {
        int totalSize = chunks.stream().mapToInt(b -> b.length).sum();
        byte[] data = new byte[totalSize];
        int position = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, data, position, chunk.length);
            position += chunk.length;
        }
        return data;
    }

}
