package me.lab7.common.models;

public record User(String name, String password) {

    @Override
    public String toString() {
        return name;
    }
}
