package me.lab6.server.commands;


import me.lab6.common.network.Response;
import me.lab6.common.models.Worker;
import me.lab6.server.managers.CollectionManager;

import java.util.Collection;

/**
 * Command to print out all elements of the collection.
 * Implements the {@link Command} interface.
 */

public class Show implements Command {
    CollectionManager collectionManager;

    /**
     * Print out all elements of the collection.
     *
     * @param collectionManager the collection manager that contains the collection to be searched
     */
    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Get the name of the command.
     *
     * @param arg the argument of the command
     */
    @Override
    public Response execute(Object arg) {
        Collection<Worker> workers = collectionManager.workerMap().values();
        if (workers.size() == 0) {
            return new Response("This collection is empty.\n");
        }
        StringBuilder sb = new StringBuilder();
        workers.forEach(w -> sb.append(w).append("\n"));
        return new Response(sb.toString());
    }

    /**
     * Get the name of the command.
     *
     * @return the name of the command as a string.
     */
    @Override
    public String name() {
        return "show";
    }

    /**
     * Get the argument string for the command to display in the help command.
     *
     * @return the argument string for the command.
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * Get the description of the command to display in the help command.
     *
     * @return the description of the command as a string.
     */
    @Override
    public String desc() {
        return "print out all elements of the collection";
    }


}
