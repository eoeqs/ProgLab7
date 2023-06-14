package me.lab7.server.network.requestHandlers;

import me.lab7.common.models.User;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.AuthResponse;
import me.lab7.server.managers.databaseManagers.AuthenticationManager;
import me.lab7.server.utility.ExecutorServiceFactory;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveTask;

public class AuthTask extends RecursiveTask<AuthResponse> {

    private final DatagramSocket socket;
    private final AuthRequest request;
    private final SocketAddress address;
    private final int dataSize;

    public AuthTask(DatagramSocket socket, AuthRequest request, SocketAddress address, int dataSize) {
        this.request = request;
        this.socket = socket;
        this.address = address;
        this.dataSize = dataSize;
    }

    @Override
    protected AuthResponse compute() {
        User user = request.user();
        int result;
        if (request.logInOrRegister()) {
            result = logIn(user);
            AuthResponse response = switch (result) {
                case 1 -> new AuthResponse(false, "A user with such username is not yet registered.");
                case 2 -> new AuthResponse(false, "Wrong password.");
                case 3 -> new AuthResponse(false, "There was an authentication error on the server. Please, try again.");
                default -> new AuthResponse(true, "Successfully logged in. Welcome, " + user.name() + "!");
            };
            delegateResponseSending(response);
        } else {
            result = register(user);
            AuthResponse response = switch (result) {
                case 1 -> new AuthResponse(false, "A user with such username is already registered. Try logging in or use another username.");
                case 2 -> new AuthResponse(false, "There was an authentication error on the server. Please, try again.");
                default -> new AuthResponse(true, "Successfully registered. Welcome, " + user.name() + "!");
            };
            delegateResponseSending(response);
        }
        return null;
    }

    private void delegateResponseSending(AuthResponse response) {
        ExecutorService pool = ExecutorServiceFactory.getThreadPool();
        pool.execute(new ResponseSender(socket, response, address, dataSize));
    }

    private int logIn(User user) {
        return AuthenticationManager.logIn(user);
    }

    private int register(User user) {
        return AuthenticationManager.register(user);
    }
}
