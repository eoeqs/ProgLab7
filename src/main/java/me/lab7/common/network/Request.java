package me.lab7.common.network;

import java.io.Serializable;

public record Request(String command, Object argument) implements Serializable {

    @Override
    public String toString() {
        if (argument != null) {
            return "Command: '" + command + "'; arg: " + argument();
        }
        return "Command: '" + command + "'";
    }

}
