package me.lab7.common.models;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private Long id;
    private final double x;
    private final Double y;
    private int creatorId;

    public Coordinates(double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates coordinates = (Coordinates) o;
        return Double.compare(x, coordinates.x) == 0 && y.equals(coordinates.y);
    }

}
