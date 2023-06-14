package me.lab7.server.commands;


import me.lab7.common.models.User;
import me.lab7.common.network.CommandResponse;
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
     * @param arg  the argument for the command
     * @param user
     */
    @Override
    public CommandResponse execute(Object arg, User user) {
        Worker newWorker = (Worker) arg;
        long id = newWorker.getId();
        switch (collectionManager.replace(id, newWorker)) {
            case 1 -> {
                return new CommandResponse("There is no element with id = " + id + " in the collection.");
            }
            case 2 -> {
                return new CommandResponse("You don't own this worker, thus you can't replace it.");
            }
            case 3 -> {
                return new CommandResponse("There was a SQL error on the server. Element wasn't replaced.");
            }
            default -> {
                return new CommandResponse("The element with id = " + id + " was replaced with a new one:\n" + newWorker +"\n");
            }
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
