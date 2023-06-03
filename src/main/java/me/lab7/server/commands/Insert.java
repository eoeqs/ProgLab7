package me.lab7.server.commands;

import me.lab7.common.models.User;
import me.lab7.common.network.Response;
import me.lab7.common.models.Worker;
import me.lab7.server.managers.CollectionManager;

public class Insert implements Command {
    private final CollectionManager collectionManager;

    public Insert(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Object arg, User user) {
        Worker worker = (Worker) arg;
        long key = worker.getId();
        switch (collectionManager.insert(key, worker)) {
            case 1 -> {
                return new Response("A worker with key = " + key + " already exists.");
            }
            case 2 -> {
                return new Response("There was a SQL error on the server. An element wasn't inserted.");
            }
            default -> {
                return new Response("The following element was inserted into the collection under the key = " + key +
                        ":\n" + worker + "\n");
            }
        }
    }

    @Override
    public String name() {
        return "insert";
    }

    @Override
    public String argDesc() {
        return "{key (long value)}";
    }

    @Override
    public String desc() {
        return "add a new element to the collection using the given key";
    }
}
