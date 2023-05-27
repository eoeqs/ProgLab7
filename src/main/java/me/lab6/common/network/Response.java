package me.lab6.common.network;

import java.io.Serializable;

public record Response(String message) implements Serializable {

    @Override
    public String toString() {
        return message;
    }

}
