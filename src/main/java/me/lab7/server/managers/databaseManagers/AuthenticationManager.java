package me.lab7.server.managers.databaseManagers;

import ch.qos.logback.classic.Logger;
import me.lab7.common.models.User;
import me.lab7.server.utility.Configuration;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AuthenticationManager {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationManager.class);
    private static final UserDatabaseManager databaseManager = new UserDatabaseManager(
            new ConnectionManager(Configuration.getDbUrl(), Configuration.getDbLogin(), Configuration.getDbPass()));

    public static int logIn(User user) {
        try {
            if (!databaseManager.userIsRegistered(user.name())) {
                logger.info("Failed to log in, not not yet registered.");
                return 1;
            } else {
                if (!databaseManager.checkPassword(user.name(), user.password())) {
                    logger.info("Failed to log in, wrong password.");
                    return 2;
                }
            }
            logger.info(user.name() + " logged in.");
            return 0;
        } catch (SQLException e) {
            logger.error("Failed to log in, SQL error: " + e);
            return 3;
        }
    }

    public static int register(User user) {
        try {
            if (databaseManager.userIsRegistered(user.name())) {
                logger.info("Failed to register, this user is already registered.");
                return 1;
            }
            databaseManager.insertUser(user);
            logger.info("Registered user: " + user);
            return 0;
        } catch (SQLException e) {
            logger.error("Failed to register, SQL error: " + e);
            return 2;
        }
    }

}
