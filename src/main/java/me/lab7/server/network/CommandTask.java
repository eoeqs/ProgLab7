package me.lab7.server.network;

import me.lab7.common.network.Request;
import me.lab7.common.network.Response;
import me.lab7.server.managers.CommandManager;

import java.util.concurrent.RecursiveTask;

public class CommandTask extends RecursiveTask<Response> {

    private final Request request;
    private final CommandManager commandManager;

    public CommandTask(Request request, CommandManager commandManager) {
        this.request = request;
        this.commandManager = commandManager;
    }

    @Override
    protected Response compute() {
        try {
            return commandManager.handleRequest(request);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
