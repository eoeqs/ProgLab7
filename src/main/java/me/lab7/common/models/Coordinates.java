package me.lab7.common.models;

import java.io.Serializable;

/**
 * Represents coordinates with an x and y value.
 */
public record Coordinates(double x, Double y) implements Serializable {
    /**
     * Returns a string representation of the coordinates in the format "(x, y)".
     *
     * @return a string representation of the coordinates
     */
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    /**
     * Compares this coordinates to another object for equality.
     *
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates coordinates = (Coordinates) o;
        return Double.compare(x, coordinates.x) == 0 && y.equals(coordinates.y);
    }

}
