package me.lab7.server;

import me.lab7.server.managers.CollectionManager;
import me.lab7.server.managers.CommandManager;
import me.lab7.server.managers.FileManager;
import me.lab7.server.managers.databaseManagers.ConnectionManager;
import me.lab7.server.managers.databaseManagers.WorkerDatabaseManager;
import me.lab7.server.network.UDPServer;
import me.lab7.server.sql.DDLManager;
import me.lab7.server.utility.Configuration;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class ServerMain {
    private final static int port = 5928;

    public static void main(String[] args) {
        try {
            getCredentials();
            startServer(initializeDB());
        } catch (Exception e) {
            System.out.println("Shutting down...");
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

    private static CollectionManager initializeDB() throws Exception {
        ConnectionManager connectionManager = new ConnectionManager(
                Configuration.getDbUrl(),
                Configuration.getDbLogin(),
                Configuration.getDbPass()
        );
        DDLManager ddlManager = new DDLManager(connectionManager);
        ddlManager.createTables();
        WorkerDatabaseManager workerDatabaseManager = new WorkerDatabaseManager(connectionManager);
        try {
            return new CollectionManager(workerDatabaseManager);
        } catch (SQLException e) {
            System.out.println("Failed to read collection from database.");
            throw new Exception(e);
        }
    }

    private static void startServer(CollectionManager collectionManager) {
        CommandManager commandManager = new CommandManager(collectionManager);
        try {
            UDPServer server = new UDPServer(InetAddress.getLocalHost(), port, commandManager);
            Runtime.getRuntime().addShutdownHook(new Thread(UDPServer::shutdown));
            server.start();
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Failed to launch the server using local host.\n");
        }
    }

}
