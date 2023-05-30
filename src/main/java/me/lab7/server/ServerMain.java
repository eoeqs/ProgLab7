package me.lab7.server;

import com.google.gson.JsonParseException;
import me.lab7.common.models.Worker;
import me.lab7.server.exceptions.IncorrectWorkerFieldException;
import me.lab7.server.exceptions.SameIDException;
import me.lab7.server.io.ServerConsole;
import me.lab7.server.managers.CollectionManager;
import me.lab7.server.managers.CommandManager;
import me.lab7.server.managers.FileManager;
import me.lab7.server.network.UDPServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class ServerMain {
    private final static int port = 49320;

    public static void main(String[] args) {
        String fileName = System.getenv("workers");
        if (fileName != null) {
            FileManager fileManager = new FileManager(fileName);
            try {
                if (!new File(fileName).createNewFile()) {
                    HashMap<Long, Worker> workerMap = fileManager.readWorkersFromFile();
                    prepareAndStart(fileManager, workerMap);
                } else {
                    prepareAndStart(fileManager, new HashMap<>());
                }
            } catch (IOException e) {
                System.out.println("Can't read the source file. Make sure that the environmental variable 'workers' stores a proper path to an existent file.\n");
            } catch (SameIDException e) {
                System.out.println("The source file contains two or more workers with the same ID. ID has to be unique.\n");
            } catch (JsonParseException e) {
                System.out.println("Failed to read the source file: content doesn't meet json standards.\n");
            } catch (IncorrectWorkerFieldException e) {
                System.out.println("The source file's worker representation is incorrect.\n");
            } catch (NullPointerException e) {
                prepareAndStart(fileManager, new HashMap<>());
            }
        } else {
            System.out.println("Can't read the source file. Environmental variable 'workers' is null.\n");
        }
    }

    private static void prepareAndStart(FileManager fileManager, HashMap<Long, Worker> workerMap) {
        CollectionManager collectionManager = new CollectionManager(workerMap);
        CommandManager commandManager = new CommandManager(collectionManager, fileManager);
        try {
            Scanner scanner = new Scanner(System.in);
            ServerConsole serverConsole = new ServerConsole(scanner, commandManager);
            Runtime.getRuntime().addShutdownHook(new Thread(serverConsole::exit));
            UDPServer server = new UDPServer(InetAddress.getLocalHost(), port, commandManager, serverConsole);
            server.run();
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Failed to launch the server using local host.\n");
        }
    }
}
