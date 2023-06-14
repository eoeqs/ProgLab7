package me.lab7.common.network;

import java.io.Serializable;

public record CommandResponse(String message) implements Serializable, Response {

    @Override
    public String toString() {
        return message;
    }

}
