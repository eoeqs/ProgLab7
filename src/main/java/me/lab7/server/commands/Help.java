package me.lab7.server.commands;


import me.lab7.common.network.Response;

import java.util.ArrayList;

/**
 * Represents the Help command that prints out the list of available commands with their descriptions.
 * Implements the {@link Command} interface.
 */
public class Help implements Command {
    ArrayList<Command> commands;

    /**
     * Constructs a new Help command with the given list of commands.
     *
     * @param commands the list of commands to be displayed.
     */

    public Help(ArrayList<Command> commands) {
        this.commands = commands;
    }

    /**
     * Prints out the list of available commands with their descriptions.
     *
     * @param arg the argument for the command (not used in this case).
     */
    @Override
    public Response execute(Object arg) {

        StringBuilder sb = new StringBuilder(name()).append(" - ").append(desc()).append("\n");
        commands.forEach(c -> {
            sb.append(c.name());
            if (c.argDesc() != null) {
                sb.append(" ").append(c.argDesc());
            }
            sb.append(" - ").append(c.desc()).append("\n");
        });
        sb.append("execute_script {filepath} - execute the sequence of commands from a file\n");
        return new Response(sb.toString());
    }


    /**
     * Returns the name of the command.
     *
     * @return the name of the command.
     */
    @Override
    public String name() {
        return "help";
    }

    /**
     * Returns the argument description for the command.
     *
     * @return an empty string, as the command does not require an argument.
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    @Override
    public String desc() {
        return "print out the list of available commands";
    }
}
