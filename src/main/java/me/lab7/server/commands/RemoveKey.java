package me.lab7.server.commands;


import me.lab7.common.models.User;
import me.lab7.common.network.Response;
import me.lab7.server.managers.CollectionManager;

/**
 * Command to remove an element with the given key from the collection.
 * Implements the {@link Command} interface.
 */
public class RemoveKey implements Command {
    private final CollectionManager collectionManager;

    /**
     * Constructs a RemoveKey command object with the given ColMan object.
     *
     * @param collectionManager the ColMan object to operate on
     */
    public RemoveKey(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the RemoveKey command by removing the element with the given key from the collection.
     *
     * @param arg  the key of the element to remove from the collection
     * @param user
     */
    @Override
    public Response execute(Object arg, User user) {
        long key = Long.parseLong((String) arg);
        switch (collectionManager.remove(key, user.name())) {
            case 1 -> {
                return new Response("The collection doesn't contain an element with key = " + key + ".\n");
            }
            case 2 -> {
                return new Response("You don't own this worker, thus you can't remove it.");
            }
            case 3 -> {
                return new Response("There was a SQL error on the server. Element wasn't removed.");
            }
            default -> {
                return new Response("Collection element with key " + key + " has been successfully deleted.\n");
            }
        }
    }

    /**
     * Returns the name of the RemoveKey command.
     *
     * @return the name of the command
     */
    @Override
    public String name() {
        return "remove_key";
    }

    /**
     * Returns the argument syntax of the RemoveKey command.
     *
     * @return the argument syntax of the command
     */
    @Override
    public String argDesc() {
        return "{key (long value)}";
    }

    /**
     * Returns the description of the RemoveKey command.
     *
     * @return the description of the command
     */
    @Override
    public String desc() {
        return "delete an element with the given key from the collection";
    }

}
