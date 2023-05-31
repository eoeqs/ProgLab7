package me.lab7.server.managers;

import java.io.*;

public class FileManager {

    public FileManager() {
    }

    public static String getTextFromFile(String fileName) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(fileName));
            StringBuilder s = new StringBuilder();
            int curB = inputStreamReader.read();
            while (curB != -1) {
                s.append((char) curB);
                curB = inputStreamReader.read();
            }
            inputStreamReader.close();
            return s.toString();
        } catch (IOException e) {
            return "There was an error reading from file.";
        }
    }
}
