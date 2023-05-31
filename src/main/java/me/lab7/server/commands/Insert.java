package me.lab7.server.commands;

import me.lab7.common.models.User;
import me.lab7.common.network.Response;
import me.lab7.common.models.Worker;
import me.lab7.server.managers.CollectionManager;
import me.lab7.server.managers.databaseManagers.WorkerDatabaseManager;

import java.sql.SQLException;

public class Insert implements Command {
    private WorkerDatabaseManager workerDatabaseManager;
    private User user;
    private Worker worker;
    private CollectionManager collectionManager;
    public Insert() {
    }

    @Override
    public Response execute(Object arg) {
       try {
           Long id = workerDatabaseManager.addWorker(user, worker);
           worker.setId(id);
           worker.setCreatorId(user.getId());
           worker.getOrganization().setCreatorId(user.getId());
           return new Response(collectionManager.add(worker));
       } catch (SQLException e) {
           return new Response("There was an error inserting worker.");
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
