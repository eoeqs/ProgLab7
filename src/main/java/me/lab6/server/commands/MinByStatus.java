package me.lab6.server.commands;


import me.lab6.common.network.Response;
import me.lab6.common.workerRelated.Status;
import me.lab6.common.workerRelated.Worker;
import me.lab6.server.managers.CollectionManager;

import java.util.List;

/**
 * A command to print out all elements with the lowest status value in the collection.
 * Implements the {@link Command} interface.
 */
public class MinByStatus implements Command {
    CollectionManager collectionManager;

    /**
     * Constructs a MinByStatus command object with a given collection manager.
     *
     * @param collectionManager the collection manager that contains the collection to be searched
     */
    public MinByStatus(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the MinByStatus command by printing out all elements with the lowest status value.
     *
     * @param arg a string argument that is not used in this command
     */
    @Override
    public Response execute(Object arg) {
        Status minStatus = Status.minStatus();
        List<Worker> filtered = collectionManager.workerMap().values()
                .stream().filter(w -> w.getStatus() == minStatus).toList();
        StringBuilder sb = new StringBuilder("The minimal Status value is ").append(minStatus).append("\n");
        if (filtered.size() == 0) {
            sb.append("The collection doesn't contain any elements with the minimal Status value.\n");
        } else {
            filtered.forEach(w -> sb.append(w).append("\n"));
        }
        return new Response(sb.toString());
    }

    /**
     * Returns the name of the MinByStatus command.
     *
     * @return the name of the command as a string
     */
    @Override
    public String name() {
        return "min_by_status";
    }

    /**
     * Returns the string representation of the command argument for help.
     *
     * @return an empty string since this command doesn't take any arguments
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * Returns the description of the MinByStatus command for help.
     *
     * @return the description of the command as a string
     */
    @Override
    public String desc() {
        return "print out all elements with the lowest status value";
    }

}
