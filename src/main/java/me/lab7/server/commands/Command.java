package me.lab7.server.commands;


import me.lab7.common.models.User;
import me.lab7.common.network.CommandResponse;

/**
 * The Command interface represents a command that can be executed.
 */
public interface Command {
    /**
     * Executes the command with the given argument.
     *
     * @param arg  the argument to pass to the command
     * @param user
     */
    CommandResponse execute(Object arg, User user);

    /**
     * @return the name of the command
     */
    String name();

    /**
     * @return the argument string for the command
     */
    String argDesc();

    /**
     * @return the description of the command
     */
    String desc();

}
