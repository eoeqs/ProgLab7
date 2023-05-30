package me.lab7.common.utility;

import java.util.Arrays;

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

}
