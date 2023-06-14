package me.lab7.server.commands;


import me.lab7.common.models.User;
import me.lab7.common.network.CommandResponse;

import java.util.*;

/**
 * The {@code History} class implements the {@code Command} interface
 * and represents the command for printing out the 6 last executed commands.
 */
public class History implements Command {

    private final Map<String, ArrayList<String>> history;

    /**
     * Constructs a new {@code History} object with the specified history of commands.
     *
     * @param history the history of commands to be used
     */
    public History(Map<String, ArrayList<String>> history) {
        this.history = history;
    }

    /**
     * Executes the command by printing out the last 6 executed commands.
     *
     * @param arg  the command argument (not used in this command)
     * @param user
     */
    @Override
    public CommandResponse execute(Object arg, User user) {
        List<String> userHistory = history.get(user.name());
        if (userHistory == null) {
            return new CommandResponse("History is yet empty.");
        }
        StringBuilder sb = new StringBuilder();
        userHistory.forEach(s -> sb.append(s).append("\n"));
        return new CommandResponse(sb.toString());
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


