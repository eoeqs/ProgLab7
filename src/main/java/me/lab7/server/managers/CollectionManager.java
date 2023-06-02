package me.lab7.server.managers;

import me.lab7.common.models.*;
import me.lab7.server.managers.databaseManagers.WorkerDatabaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionManager {

    private final Map<Long, Worker> workerMap;
    private final WorkerDatabaseManager workerDatabaseManager;

    public CollectionManager(WorkerDatabaseManager workerDatabaseManager) throws SQLException {
        this.workerDatabaseManager = workerDatabaseManager;
        workerMap = workerDatabaseManager.getAllWorkers();
    }

    synchronized public List<Worker> getWorkers() {
        return new ArrayList<>(workerMap.values());
    }

    synchronized public boolean hasKey(long key) {
        return workerMap.containsKey(key);
    }

    synchronized public 

}
