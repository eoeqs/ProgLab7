package me.lab7.server.commands;


import me.lab7.common.network.Response;
import me.lab7.common.models.Worker;
import me.lab7.server.managers.CollectionManager;

/**
 * The {@code Update} class represents the update command used to replace the worker with the given key in the collection
 * with a newly described worker.
 * Implements the {@link Command} interface.
 */
public class Update implements Command {

    CollectionManager collectionManager;

    /**
     * Constructs a new {@code Update} command with the given collection manager.
     *
     * @param collectionManager the collection manager to be used
     */

    public Update(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Replaces the worker with the given key in the collection with a newly described worker.
     *
     * @param arg the argument for the command
     */
    @Override
    public Response execute(Object arg) {
        Worker newWorker = (Worker) arg;
        long key = newWorker.getId();
        if (!collectionManager.workerMap().containsKey(key)) {
            return new Response("The collection doesn't contain an element with key = " + key + ".\n");
        } else {
            return new Response(collectionManager.replace(newWorker));
        }
    }

    /**
     * Returns the name of the command.
     *
     * @return the name of the command
     */
    @Override
    public String name() {
        return "update";
    }

    /**
     * Returns the argument format for the command.
     *
     * @return the argument format for the command
     */
    @Override
    public String argDesc() {
        return "{id (long value)}";
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command
     */
    @Override
    public String desc() {
        return "update an element with the given id field value";
    }

}
