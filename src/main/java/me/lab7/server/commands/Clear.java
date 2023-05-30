package me.lab7.server.commands;


import me.lab7.common.network.Response;
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
    public Response execute(Object arg) {
        if (collectionManager.workerMap().isEmpty()) {
            return new Response("This collection is already empty.\n");
        }
        collectionManager.workerMap().clear();
        return new Response("Collection has been cleared.\n");
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