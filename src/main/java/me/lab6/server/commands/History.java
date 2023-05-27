package me.lab6.server.commands;


import me.lab6.common.network.Response;

import java.util.List;

/**
 * The {@code History} class implements the {@code Command} interface
 * and represents the command for printing out the 6 last executed commands.
 */
public class History implements Command {
    List<String> history;

    /**
     * Constructs a new {@code History} object with the specified history of commands.
     *
     * @param history the history of commands to be used
     */
    public History(List<String> history) {
        this.history = history;
    }

    /**
     * Executes the command by printing out the last 6 executed commands.
     *
     * @param arg the command argument (not used in this command)
     */
    @Override
    public Response execute(Object arg) {
        if (history.size() == 0) {
            return new Response("History is yet empty.\n");
        }
        StringBuilder sb = new StringBuilder();
        history.forEach(s -> sb.append(s).append("\n"));
        return new Response(sb.toString());
    }

    /**
     * Returns the name of the command.
     *
     * @return the name of the command
     */
    @Override
    public String name() {
        return "history";
    }

    /**
     * Returns the argument of the command for help information (not used in this command).
     *
     * @return an empty string
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * Returns the description of the command for help information.
     *
     * @return the description of the command for help information
     */
    @Override
    public String desc() {
        return "print out 6 last executed commands";
    }

}


