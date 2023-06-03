package me.lab7.server.network;

import me.lab7.common.models.User;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.AuthResponse;
import me.lab7.server.managers.databaseManagers.AuthenticationManager;

import java.util.concurrent.RecursiveTask;

public class AuthTask extends RecursiveTask<AuthResponse> {

    private final AuthRequest request;

    public AuthTask(AuthRequest request) {
        this.request = request;
    }

    @Override
    protected AuthResponse compute() {
        User user = request.user();
        if (request.logInOrRegister()) {
            int result = logIn(user);
            switch (result) {
                case 1 -> {
                    return new AuthResponse(false, "A user with such username is not yet registered.");
                }
                case 2 -> {
                    return new AuthResponse(false, "Wrong password.");
                }
                case 3 -> {
                    return new AuthResponse(false, "There was an authentication error on the server. Please, try again.");
                }
                default -> {
                    return new AuthResponse(true, "Successfully logged in. Welcome, " + user.name() + "!");
                }
            }
        } else {
            int result = register(user);
            switch (result) {
                case 1 -> {
                    return new AuthResponse(false, "A user with such username is already registered. Try logging in or use another username.");
                }
                case 2 -> {
                    return new AuthResponse(false, "There was an authentication error on the server. Please, try again.");
                }
                default -> {
                    return new AuthResponse(true, "Successfully registered. Welcome, " + user.name() + "!");
                }
            }
        }
    }

    private int logIn(User user) {
        return AuthenticationManager.logIn(user);
    }

    private int register(User user) {
        return AuthenticationManager.register(user);
    }
}
