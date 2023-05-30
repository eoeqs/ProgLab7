package me.lab7.server.managers.databaseManagers;

import java.sql.Connection;
import java.sql.SQLException;

public class WorkerDatabaseManager {
    private final ConnectionManager connectionManager;

    public WorkerDatabaseManager(String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public WorkerDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }
    //тут должна быть загрузка воркеров в бд,
    //а еще то как они добавляются, обновляются и убираются
}
