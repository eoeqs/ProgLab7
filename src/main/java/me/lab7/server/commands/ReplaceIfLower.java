package me.lab7.server.commands;


import me.lab7.common.models.User;
import me.lab7.common.network.CommandResponse;

import me.lab7.common.models.Worker;
import me.lab7.server.managers.CollectionManager;

public class ReplaceIfLower implements Command {
    CollectionManager collectionManager;

    public ReplaceIfLower(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Object arg, User user) {
        Worker newWorker = (Worker) arg;
        long id = newWorker.getId();
        switch (collectionManager.replaceIfLower(id, newWorker)) {
            case 1 -> {
                return new CommandResponse("There is no element with id = " + id + " in the collection.");
            }
            case 2 -> {
                return new CommandResponse("You don't own this worker, thus you can't replace it.");
            }
            case 3 -> {
                return new CommandResponse("The described worker is greater or equal to the current one.");
            }
            case 4 -> {
                return new CommandResponse("There was a SQL error on the server. Element wasn't replaced.");
            }
            default -> {
                return new CommandResponse("The element with id = " + id + " was replaced with a new one:\n" + newWorker +"\n");
            }
        }
    }

    @Override
    public String name() {
        return "replace_if_lower";
    }

    @Override
    public String argDesc() {
        return "{id (long value)}";
    }

    @Override
    public String desc() {
        return "replace an element with the given id if the newly described element is lower than the current";
    }

}
