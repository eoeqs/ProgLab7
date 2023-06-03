package me.lab7.server.commands;

import me.lab7.common.models.User;
import me.lab7.common.network.Response;
import me.lab7.common.utility.Messages;

/**
 * A command that stops the application without saving.
 * Implements the {@link Command} interface.
 */
public class Exit implements Command {

    @Override
    public Response execute(Object argument, User user) {
        return new Response(Messages.goodbye());
    }

    /**
     * Returns the name of this command.
     *
     * @return a string representing the name of the command ("exit")
     */
    @Override
    public String name() {
        return "exit";
    }

    /**
     * Returns a string representing the argument syntax of this command.
     *
     * @return an empty string (as this command does not require any arguments)
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * Returns a string representing the description of this command.
     *
     * @return a string describing the functionality of the command ("stop the application without saving")
     */
    @Override
    public String desc() {
        return "end this session and close the application";
    }

}
