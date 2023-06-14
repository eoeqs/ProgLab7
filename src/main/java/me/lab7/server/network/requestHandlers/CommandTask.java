package me.lab7.server.network.requestHandlers;

import me.lab7.common.network.CommandRequest;
import me.lab7.common.network.CommandResponse;
import me.lab7.server.managers.CommandManager;
import me.lab7.server.utility.ExecutorServiceFactory;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveTask;

public class CommandTask extends RecursiveTask<CommandResponse> {

    private final DatagramSocket socket;
    private final CommandRequest commandRequest;
    private final CommandManager commandManager;
    private final SocketAddress address;
    private final int dataSize;

    public CommandTask(DatagramSocket socket, CommandRequest commandRequest, CommandManager commandManager, SocketAddress address, int dataSize) {
        this.socket = socket;
        this.commandRequest = commandRequest;
        this.commandManager = commandManager;
        this.address = address;
        this.dataSize = dataSize;
    }

    @Override
    protected CommandResponse compute() {
        CommandResponse response = commandManager.handleRequest(commandRequest);
        delegateResponseSending(response);
        return null;
    }

    private void delegateResponseSending(CommandResponse commandResponse) {
        ExecutorService pool = ExecutorServiceFactory.getThreadPool();
        pool.execute(new ResponseSender(socket, commandResponse, address, dataSize));
    }
}
