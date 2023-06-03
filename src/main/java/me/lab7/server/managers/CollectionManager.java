package me.lab7.server.managers;

import ch.qos.logback.classic.Logger;
import me.lab7.common.models.*;
import me.lab7.server.managers.databaseManagers.WorkerDatabaseManager;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class CollectionManager {

    private Map<Long, Worker> workerMap;
    private final WorkerDatabaseManager workerDatabaseManager;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(CollectionManager.class);

    public CollectionManager(WorkerDatabaseManager workerDatabaseManager) throws SQLException {
        this.workerDatabaseManager = workerDatabaseManager;
        workerMap = workerDatabaseManager.getAllWorkers();
    }

    synchronized public List<Worker> getWorkers() {
        return new ArrayList<>(workerMap.values());
    }

    private boolean hasKey(long key) {
        return workerMap.containsKey(key);
    }

    synchronized public boolean isEmpty() {
        return workerMap.isEmpty();
    }

    synchronized public int insert(long key, Worker worker) {
        if (hasKey(key)) {
            logger.info("Failed to insert a worker, key " + key + " is already occupied.");
            return 1;
        }
        long id;
        try {
            id = workerDatabaseManager.insertWorker(worker);
            logger.info("Successfully inserted a worker into database: " + worker);
        } catch (SQLException e) {
            logger.error("Failed to insert worker into database: " + e);
            return 2;
        }
        worker.setId(id);
        workerMap.put(key, worker);
        logger.info("Updated collection, new worker: " + worker);
        return 0;
    }

    private int allowedToUpdate(long id, Worker newWorker) {
        List<Worker> workers = getWorkers();
        Worker worker = null;
        for (Worker w : workers) {
            if (w.getId() == id) {
                worker = w;
                break;
            }
        }
        if (worker == null) {
            logger.info("Failed to update a worker, there is no worker with id = " + id);
            return 1;
        }
        if (!worker.getCreatorName().equals(newWorker.getCreatorName())) {
            logger.info("Failed to update a worker, user " + newWorker.getCreatorName() + " doesn't own this worker.");
            return 2;
        }
        return 0;
    }

    synchronized public int replace(long id, Worker newWorker) {
        int allowed = allowedToUpdate(id, newWorker);
        if (allowed != 0) {
            return allowed;
        }
        long key = 0;
        for (Map.Entry<Long, Worker> entry : workerMap.entrySet()) {
            if (entry.getValue().getId() == id) {
                key = entry.getKey();
                break;
            }
        }
        try {
            workerDatabaseManager.updateWorker(id, newWorker);
        } catch (SQLException e) {
            logger.error("Failed to update worker in the database: " + e);
            return 3;
        }
        workerMap.replace(key, newWorker);
        logger.info("Updated collection: new data for worker with id = " + id + ": " + newWorker);
        return 0;
    }

    synchronized public int remove(long key, String username) {
        if (!hasKey(key)) {
            logger.info("Failed to remove a worker, there is no worker with key = " + key);
            return 1;
        }
        if (!workerMap.get(key).getCreatorName().equals(username)) {
            logger.info("Failed to remove a worker, user " + username + " doesn't own this worker.");
            return 2;
        }
        try {
            workerDatabaseManager.deleteWorker(workerMap.get(key).getId());
        } catch (SQLException e) {
            logger.error("Failed to delete a worker from the database: " + e);
            return 3;
        }
        workerMap.remove(key);
        logger.info("Updated collection: removed worker with key = " + key);
        return 0;
    }

    synchronized public int removeLowerKey(long key, String username) {
        List<Long> keys = workerMap.keySet().stream().filter(k -> k < key).toList();
        List<Long> workers = new ArrayList<>();
        for (long k : keys) {
            Worker worker = workerMap.get(k);
            if (worker.getCreatorName().equals(username)) {
                workers.add(worker.getId());
            }
        }
        try {
            workerDatabaseManager.deleteWorker(workers);
        } catch (SQLException e) {
            logger.error("Failed to delete workers from the database: " + e);
            return 1;
        }
        workerMap.entrySet().removeIf(e -> e.getValue().getCreatorName().equals(username) && e.getKey() < key);
        logger.info("Updated collection: removed workers owned by " + username + " with key lower than " + key);
        return 0;
    }

    synchronized public int removeOwned(String username) {
        try {
            workerDatabaseManager.deleteOwnedWorkers(username);
        } catch (SQLException e) {
            logger.error("Failed to delete workers from database: " + e);
            return 1;
        }
        workerMap.entrySet().removeIf(e -> e.getValue().getCreatorName().equals(username));
        logger.info("Updated collection: removed workers owned by " + username);
        return 0;
    }

    synchronized public int replaceIfLower(long id, Worker newWorker) {
        int allowed = allowedToUpdate(id, newWorker);
        if (allowed != 0) {
            return allowed;
        }
        long key = 0;
        for (Map.Entry<Long, Worker> entry : workerMap.entrySet()) {
            if (entry.getValue().getId() == id) {
                key = entry.getKey();
                break;
            }
        }
        List<Worker> workers = getWorkers();
        for (Worker w : workers) {
            if (w.getId() == id && w.compareTo(newWorker) < 0) {
                try {
                    workerDatabaseManager.updateWorker(id, newWorker);
                } catch (SQLException e) {
                    logger.error("Failed to update worker in the database: " + e);
                    return 4;
                }
                workerMap.replace(key, newWorker);
                logger.info("Updated collection: new data for worker with id = " + id + ": " + newWorker);
                return 0;
            }
        }
        logger.info("Failed to update a worker, the new worker with id = " + id + " is greater or equal to the current one.");
        return 3;
    }
}
