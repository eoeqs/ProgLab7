package me.lab7.common.models;

import java.io.Serializable;

/**
 * The Status enum class defines the possible statuses that a worker can hold. These statuses include Fired, Hired,
 * Recommended for Promotion, and Regular.
 */
public enum Status implements Serializable {
    /**
     * FIRED Status
     */
    FIRED,
    /**
     * HIRED Status
     */
    HIRED,
    /**
     * RECOMMENDED_FOR_PROMOTION Status
     */
    RECOMMENDED_FOR_PROMOTION,
    /**
     * REGULAR Status
     */
    REGULAR;
    /**
     * Returns the minimum status, which is FIRED.
     *
     * @return the minimum status
     */
    public static Status minStatus() {
        return FIRED;
    }
    /**
     * Returns a string that lists all possible statuses.
     *
     * @return a string listing all possible statuses
     */
    public static String allStatuses() {
        StringBuilder sb = new StringBuilder();
        for (Status p : Status.values()) {
            sb.append(p);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
