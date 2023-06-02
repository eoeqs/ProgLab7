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
    private final static int port = 5928;

    public static void main(String[] args) {
        try {
            getCredentials();
            prepareAndStart(new HashMap<>());
        } catch (Exception e) {
            System.out.println("Shutting down...");
        }
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

    private static void getCredentials() throws Exception {
        String fileName = System.getenv("credentials");
        if (fileName == null) {
            System.out.println("The variable 'credentials' is null.");
            throw new Exception();
        }
        String text = FileManager.getTextFromFile(fileName);
        String[] strings = text.split("\n");
        if (strings.length != 3) {
            System.out.println("Credentials contents are incorrect.");
            throw new Exception();
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
            UDPServer server = new UDPServer(InetAddress.getLocalHost(), port, commandManager);
            Runtime.getRuntime().addShutdownHook(new Thread(UDPServer::shutdown));
            server.start();
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Failed to launch the server using local host.\n");
        }
    }
}
