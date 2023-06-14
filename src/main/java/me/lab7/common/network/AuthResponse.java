package me.lab7.common.network;

import java.io.Serializable;

public record AuthResponse(boolean success, String message) implements Serializable, Response {

    @Override
    public String toString() {
        return "Authorization response: " + (success ? "successful" : "failed") + ", " + message;
    }

}
