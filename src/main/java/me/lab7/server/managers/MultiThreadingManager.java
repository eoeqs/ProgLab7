package me.lab7.server.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadingManager {
    private static final ExecutorService responseThreadPool = Executors.newCachedThreadPool();

    public static ExecutorService getResponseThreadPool() {
        return responseThreadPool;
    }
}
