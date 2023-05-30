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

    protected UserDatabaseManager(String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    protected UserDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    protected int addUser(User user) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "insert into users(name, password_digest, salt)" +
                "values (?, ?, ?) returning id");

        statement.setString(1, user.getName());
        String salt = PasswordManager.getSalt();
        statement.setString(2, PasswordManager.getHash(user.getPassword(), salt));
        statement.setString(3, salt);
        ResultSet result = statement.executeQuery();
        connection.close();
        result.next();
        return result.getInt(1);
    }

    protected ServerUser getUser(int id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select * from users where id = ?");

        statement.setInt(1, id);

        ResultSet result = statement.executeQuery();

        connection.close();

        result.next();

        String name = result.getString("name");
        String password_digest = result.getString("password_digest");
        String salt = result.getString("salt");
        return new ServerUser(id, name, password_digest, salt);
    }

    protected ServerUser getUser(String name) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select * from users where name = ?");

        statement.setString(1, name);

        ResultSet result = statement.executeQuery();

        connection.close();

        result.next();

        int id = result.getInt("id");
        String password_digest = result.getString("password_digest");
        String salt = result.getString("salt");

        return new ServerUser(id, name, password_digest, salt);
    }

    protected ServerUser getUser(String name, String password) throws SQLException {
        ServerUser user = getUser(name);
        String salt = user.getSalt();
        String password_digest = PasswordManager.getHash(password, salt);
        if (password_digest.equals(user.getPasswordDigest())) {
            return user;
        }
        return null;
    }

    protected String getUserSalt(String name) throws SQLException {
        ServerUser user = getUser(name);

        return user.getSalt();
    }

    protected int getUserId(String name) throws SQLException {
        ServerUser user = getUser(name);
        return user.getId();
    }

    protected boolean checkUserName(String name) {
        try {
            return getUser(name) != null;
        } catch (SQLException e) {
            return false;
        }
    }

    protected boolean checkUserId(int id) {
        try {
            return getUser(id) != null;
        } catch (SQLException e) {
            return false;
        }
    }

    protected boolean checkUserPass(String name, String password) {
        try {
            return getUser(name, password) != null;
        } catch (SQLException e) {
            return false;
        }
    }
}
