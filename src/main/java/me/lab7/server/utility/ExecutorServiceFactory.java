package me.lab7.server.utility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceFactory {

    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static ExecutorService getThreadPool() {
        return cachedThreadPool;
    }

}
