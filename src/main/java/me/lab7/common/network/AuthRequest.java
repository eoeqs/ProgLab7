package me.lab7.common.network;

import java.io.Serializable;

public record AuthRequest(boolean logInOrRegister, String username, String password) implements Serializable {

    @Override
    public String toString() {
        return (logInOrRegister ? "Log in request: " : "Register request: ") + username + " : " + password;
    }
}
