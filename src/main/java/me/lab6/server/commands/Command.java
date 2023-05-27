package me.lab6.server.commands;


import me.lab6.common.network.Response;

/**
 * The Command interface represents a command that can be executed.
 */
public interface Command {
    /**
     * Executes the command with the given argument.
     *
     * @param arg the argument to pass to the command
     */
    Response execute(Object arg);

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
