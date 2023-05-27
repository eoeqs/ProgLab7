package me.lab6.server.commands;

import me.lab6.common.network.Response;
import me.lab6.server.managers.CollectionManager;

/**
 * A command that removes all elements from the collection with the key less than the specified one.
 * Implements the {@link Command} interface.
 */
public class RemoveLowerKey implements Command {
    CollectionManager collectionManager;

    /**
     * Constructs a new instance of the RemoveLowerKey command with the given Collection Manager.
     *
     * @param collectionManager the Collection Manager
     */
    public RemoveLowerKey(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the RemoveLowerKey command. Removes all elements from the collection with the key less than the specified one.
     *
     * @param arg the argument of the command (key of the element to be removed)
     */
    @Override
    public Response execute(Object arg) {
        if (collectionManager.workerMap().isEmpty()) {
            return new Response("This collection is empty.\n");
        }
        Long key = Long.parseLong((String) arg);
        int count = collectionManager.workerMap().size();
        collectionManager.workerMap().entrySet().removeIf(entry -> entry.getKey() < key);
        count -= collectionManager.workerMap().size();
        return new Response(count + " elements were removed from the collection.\n");

    }

    /**
     * Returns the name of the command.
     *
     * @return the name of the command
     */
    @Override
    public String name() {
        return "remove_lower_key";
    }

    /**
     * Returns the argument of the command needed for correct usage in the console.
     *
     * @return the argument of the command
     */
    @Override
    public String argDesc() {
        return "{key (long value)}";
    }

    /**
     * Returns the description of the command for displaying help information.
     *
     * @return the description of the command
     */
    @Override
    public String desc() {
        return "remove all elements with the key lower than given from the collection";
    }

}

