package me.lab7.server.commands;


import me.lab7.common.models.User;
import me.lab7.common.network.CommandResponse;
import me.lab7.server.managers.CollectionManager;


public class Clear implements Command {
    CollectionManager collectionManager;

    /**
     * Constructs a Clear object with a specified ColMan object.
     *
     * @param collectionManager the ColMan object used to manage the collection
     */
    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the clear command by clearing the WorkerMap collection.
     * If the collection is already empty, a message is printed to indicate so.
     */
    @Override
    public CommandResponse execute(Object arg, User user) {
        if (collectionManager.isEmpty()) {
            return new CommandResponse("The collection is already empty.");
        }
        int result = collectionManager.removeOwned(user.name());
        if (result == 0) {
            return new CommandResponse("Elements owned by you were successfully cleared.");
        } else {
            return new CommandResponse("There was a SQL error on the server. Elements weren't cleared.");
        }
    }

    /**
     * @return the name of this command
     */
    @Override
    public String name() {
        return "clear";
    }

    /**
     * @return the argument string for the command
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * @return a description of this command
     */
    @Override
    public String desc() {
        return "clear the collection";
    }


}