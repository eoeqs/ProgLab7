package me.lab6.server.commands;


import me.lab6.common.network.Response;

import me.lab6.common.models.Worker;
import me.lab6.server.managers.CollectionManager;

public class ReplaceIfLower implements Command {
    CollectionManager collectionManager;

    public ReplaceIfLower(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Object arg) {
        Worker newWorker = (Worker) arg;
        long key = newWorker.getId();
        if (!collectionManager.workerMap().containsKey(key)) {
            return new Response("The collection doesn't contain an element with key = " + key + ".\n");
        }
        if (collectionManager.workerMap().get(key).compareTo(newWorker) > 0) {
            return new Response(collectionManager.replace(newWorker));
        } else {
            return new Response("Described element is equal to or greater than the current one.\n");
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
        return "replace an element with the given key if the newly described element is lower than the current";
    }


}
