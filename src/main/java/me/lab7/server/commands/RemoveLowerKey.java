package me.lab7.server.commands;

import me.lab7.common.models.User;
import me.lab7.common.models.Worker;
import me.lab7.common.network.CommandResponse;
import me.lab7.server.managers.CollectionManager;

import java.util.List;

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
     * @param arg  the argument of the command (key of the element to be removed)
     * @param user
     */
    @Override
    public CommandResponse execute(Object arg, User user) {
        List<Worker> workers = collectionManager.getWorkers();
        if (workers.isEmpty()) {
            return new CommandResponse("This collection is empty.\n");
        }
        long key = Long.parseLong((String) arg);
        int previousCount = workers.size();
        if (collectionManager.removeLowerKey(key, user.name()) != 0) {
            return new CommandResponse("There was a SQL error on the server. Elements weren't removed.");
        }
        int currentCount = collectionManager.getWorkers().size();
        if (previousCount == currentCount) {
            return new CommandResponse("No elements were removed. Possibly, you don't own any elements with key lower than "
                    + key + " or there are no elements among yours with key lower than " + key + ".\n");
        }
        return new CommandResponse(previousCount - currentCount + " element(s) were removed.\n");
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

