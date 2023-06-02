package me.lab7.common.network;

import me.lab7.common.models.User;

import java.io.Serializable;

public record Request(String command, Object argument, User user) implements Serializable {

    @Override
    public String toString() {
        if (argument != null) {
            return "Command: '" + command + "'; arg: " + argument() + "; from: " + user;
        }
        return "Command: '" + command + "'; from: " + user;
    }

}
