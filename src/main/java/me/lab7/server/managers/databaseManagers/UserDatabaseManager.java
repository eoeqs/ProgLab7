package me.lab7.server.managers.databaseManagers;

import me.lab7.common.models.User;
import me.lab7.server.ServerUser;
import me.lab7.server.managers.PasswordManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabaseManager {

    private final ConnectionManager connectionManager;

    public UserDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    private ServerUser getUser(String username) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from users where u_name = ?;");
        statement.setString(1, username);
        ResultSet queryResult = statement.executeQuery();
        connection.close();
        if (queryResult.next()) {
            int id = queryResult.getInt("u_id");
            String name = queryResult.getString("u_name");
            String passwordDigest = queryResult.getString("u_password_digest");
            String salt = queryResult.getString("u_salt");
            return new ServerUser(id, name, passwordDigest, salt);
        } else {
            return null;
        }
    }

    public void insertUser(User user) throws SQLException {
        Connection connection = getConnection();
        String name = user.name();
        String salt = PasswordManager.getSalt();
        String passwordDigest = PasswordManager.getHash(user.password(), salt);
        PreparedStatement statement = connection.prepareStatement(
                "insert into users(u_name, u_password_digest, u_salt) values (?, ?, ?);");
        statement.setString(1, name);
        statement.setString(2, passwordDigest);
        statement.setString(3, salt);
        statement.execute();
        connection.close();
    }

    public boolean userIsRegistered(String username) throws SQLException {
        return getUser(username) != null;
    }

    public boolean checkPassword(String username, String password) throws SQLException {
        ServerUser realUser = getUser(username);
        if (realUser == null) {
            throw new SQLException();
        }
        String realPassword = realUser.getPasswordDigest();
        String realSalt = realUser.getSalt();
        String probablePassword = PasswordManager.getHash(password, realSalt);
        return probablePassword.equals(realPassword);
    }

}
