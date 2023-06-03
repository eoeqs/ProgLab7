package me.lab7.common.network;

import me.lab7.common.models.User;

import java.io.Serializable;

public record AuthRequest(boolean logInOrRegister, User user) implements Serializable {

    @Override
    public String toString() {
        return (logInOrRegister ? "Log in request: " : "Register request: ") + user.name() + " : " + user.password();
    }
}
