package me.lab7.common.models;

import java.io.Serializable;

/**
 * The Position enum class defines the possible positions that a worker can hold. These positions include Head of
 * Department, Developer, and Manager of Cleaning.
 */
public enum Position implements Serializable {
    /**
     * HEAD_OF_DEPARTMENT Position
     */
    HEAD_OF_DEPARTMENT,
    /**
     * DEVELOPER Position
     */
    DEVELOPER,
    /**
     * MANAGER_OF_CLEANING Position
     */
    MANAGER_OF_CLEANING;

    /**
     * Returns a string that lists all possible positions.
     *
     * @return a string listing all possible positions
     */
    public static String allPositions() {
        StringBuilder sb = new StringBuilder();
        for (Position p : Position.values()) {
            sb.append(p);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
