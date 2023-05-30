package me.lab7.server.managers.databaseManagers;

import ch.qos.logback.classic.Logger;
import me.lab7.common.models.User;
import me.lab7.server.Configuration;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AuthenticationManager {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationManager.class);
    private static final UserDatabaseManager databaseManager = new UserDatabaseManager(
            new ConnectionManager(Configuration.getDbUrl(), Configuration.getDbLogin(), Configuration.getDbPass()));

    public static boolean checkUserName(String name) {
        return databaseManager.checkUserName(name);
    }

    public static boolean checkUserPass(String name, String password) {
        return databaseManager.checkUserPass(name, password);
    }

    public static boolean register(User user) {
        try {
            if (!databaseManager.checkUserName(user.getName())) {
                // если имя не занято, то создаём пользователя
                int id = databaseManager.addUser(user);
                user.setId(id);
                logger.info("Registered successfully.");
                return true;
            }
        } catch (SQLException e) {
            logger.error("There was an error while registration.");
            return false;
        }
        return false;
    }

    public static boolean auth(User user) {
        try {
            if (databaseManager.checkUserPass(user.getName(), user.getPassword())) {
                //вход успешный
                int id = databaseManager.getUserId(user.getName());

                user.setId(id);

                logger.info("Authenticated successfully.");
                return true;
            }
        } catch (SQLException e) {
            logger.error("There was an error authenticating.");
            return false;
        }
        return false;
    }
}
