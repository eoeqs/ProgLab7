package me.lab7.server;

import me.lab7.common.models.Worker;

import me.lab7.server.io.ServerConsole;
import me.lab7.server.managers.CollectionManager;
import me.lab7.server.managers.CommandManager;
import me.lab7.server.managers.FileManager;
import me.lab7.server.managers.databaseManagers.ConnectionManager;
import me.lab7.server.managers.databaseManagers.WorkerDatabaseManager;
import me.lab7.server.network.UDPServer;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class ServerMain {
    private final static int port = 49320;

    public static void main(String[] args) {
        getCredentials();
        prepareAndStart(new HashMap<>());
    }

    private static void startDB() {
        ConnectionManager connectionManager = new ConnectionManager(
                Configuration.getDbUrl(),
                Configuration.getDbLogin(),
                Configuration.getDbPass()
        );
        WorkerDatabaseManager databaseManager = new WorkerDatabaseManager(connectionManager);
        HashMap<Long, Worker> workerMap = null;
        try {
            workerMap = (HashMap<Long, Worker>) databaseManager.loadWorkers();
        } catch (SQLException e) {
            System.out.println("Failed to read collection from database.");
            System.exit(1);
        }
    }

    private static void getCredentials() {
        String fileName = System.getenv("credentials");
        String text = FileManager.getTextFromFile(fileName);
        String[] strings = text.split("\n");
        if (strings.length != 3) {
            System.out.println("File \"credentials.txt\" is not correct.");
            return;
        }
        Configuration.setDbUrl(strings[0].trim());
        Configuration.setDbLogin(strings[1].trim());
        Configuration.setDbPass(strings[2].trim());
    }

    private static void prepareAndStart(HashMap<Long, Worker> workerMap) {
        CollectionManager collectionManager = new CollectionManager(workerMap);
        CommandManager commandManager = new CommandManager(collectionManager);
        startDB();
        try {
            Scanner scanner = new Scanner(System.in);
            ServerConsole serverConsole = new ServerConsole(scanner, commandManager);
            // Runtime.getRuntime().addShutdownHook(new Thread(serverConsole::exit));
            UDPServer server = new UDPServer(InetAddress.getLocalHost(), port, commandManager);
            server.start();
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Failed to launch the server using local host.\n");
        }
    }
}
