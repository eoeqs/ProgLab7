package me.lab7.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * The Worker class represents a worker in an organization. It contains information about the worker's ID, name,
 * coordinates, creation date, salary, start date, position, status, and organization.
 */
public class Worker implements Comparable<Worker>, Serializable {

    private long id; // >0, auto-generated, unique
    private String name; // not null, not empty
    private final Coordinates coordinates; // not null
    private final LocalDate creationDate; // not null, auto-generated
    private final int salary; // > 0
    private final LocalDate startDate; // not null
    private final Position position; // can be null
    private final Status status; // can be null
    private final Organization organization; // can be null
    private int creatorId;

    /**
     * Creates a new Worker object with the specified ID, name, coordinates, creation date, salary, start date, position,
     * status, and organization.
     *
     * @param id           the ID of the worker
     * @param name         the name of the worker
     * @param coordinates  the coordinates of the worker
     * @param creationDate the creation date of the worker
     * @param salary       the salary of the worker
     * @param startDate    the start date of the worker
     * @param position     the position of the worker
     * @param status       the status of the worker
     * @param organization the organization that the worker belongs to
     */
    public Worker(long id, String name, Coordinates coordinates, LocalDate creationDate, int salary,
                  LocalDate startDate, Position position, Status status, Organization organization) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.startDate = startDate;
        this.position = position;
        this.status = status;
        this.organization = organization;
    }

    public Worker(String name, Coordinates coordinates, LocalDate creationDate, int salary,
                  LocalDate startDate, Position position, Status status, Organization organization) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.startDate = startDate;
        this.position = position;
        this.status = status;
        this.organization = organization;
    }

    /**
     * Returns the ID of the worker.
     *
     * @return the ID of the worker
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the name of the worker.
     *
     * @return the name of the worker
     */
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Sets the name of the worker to the specified value.
     *
     * @param name the new name of the worker
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the coordinates of the worker.
     *
     * @return the coordinates of the worker
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the creation date of the worker.
     *
     * @return the creation date of the worker
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Returns the salary of the worker.
     *
     * @return the salary of the worker
     */
    public int getSalary() {
        return salary;
    }

    /**
     * Returns the start date of the worker.
     *
     * @return the start date of the worker
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the position of the worker.
     *
     * @return the position of the worker
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Returns the status of the worker.
     *
     * @return the status of the worker
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the organization of the worker.
     *
     * @return the organization of the worker
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Returns a String representation of the worker object, including the worker's name, ID, position, status, salary,
     * creation date, start date, and coordinates. If the worker is not currently a member of an organization, it will also
     * include a message stating so.
     *
     * @return a String representation of the worker object
     */
    @Override
    public String toString() {
        String one = name + " (ID: " + id + ", position: " + position + ", status: " + status + ", salary: " + salary +
                     ", creation date: " + creationDate + ", start date: " + startDate + ", coordinates: " + coordinates +
                     ")\n";
        String two;
        if (organization == null) {
            two = "Isn't a member of an organization right now.";
        } else {
            two = "Works at " + organization.getFullName() + " (annual turnover: " + organization.getAnnualTurnover() +
                  ", employee count: " + organization.getEmployeesCount() + ", postal address: " +
                  organization.getPostalAddress() + ")";
        }
        return one + two;
    }

    /**
     * Determines if the current worker object is equal to the object passed as a parameter. Equality is determined by comparing
     * the worker's ID, salary, name, coordinates, creation date, start date, position, status, and organization, if applicable.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return id == worker.id && salary == worker.salary && name.equals(worker.name) &&
               coordinates.equals(worker.coordinates) && creationDate.equals(worker.creationDate) &&
               startDate.equals(worker.startDate) && position == worker.position &&
               status == worker.status && Objects.equals(organization, worker.organization);
    }

    /**
     * Generates a hash code for the worker object, based on its ID, salary, name, coordinates, creation date, start date,
     * position, status, and organization, if applicable.
     *
     * @return a hash code for the worker object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, salary, startDate, position, status, organization);
    }

    /**
     * Compares the current worker object to the worker object passed as a parameter. Comparison is based on the worker's status,
     * position, salary, organization (if applicable), and name.
     *
     * @param worker the worker object to compare to
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     */
    @Override
    public int compareTo(@NotNull Worker worker) {
        if (this.status == Status.FIRED && worker.status != Status.FIRED) {
            return -1;
        }
        if (this.status != Status.FIRED && worker.status == Status.FIRED) {
            return 1;
        }
        if (this.position == Position.HEAD_OF_DEPARTMENT && worker.position != Position.HEAD_OF_DEPARTMENT) {
            return 1;
        }
        if (this.position != Position.HEAD_OF_DEPARTMENT && worker.position == Position.HEAD_OF_DEPARTMENT) {
            return -1;
        }
        if (this.status == Status.RECOMMENDED_FOR_PROMOTION && worker.status != Status.RECOMMENDED_FOR_PROMOTION) {
            return 1;
        }
        if (this.status != Status.RECOMMENDED_FOR_PROMOTION && worker.status == Status.RECOMMENDED_FOR_PROMOTION) {
            return -1;
        }
        if (this.salary > worker.salary) {
            return 1;
        } else if (this.salary < worker.salary) {
            return -1;
        }
        try {
            if (this.organization.compareTo(worker.organization) > 0) {
                return 1;
            } else if (this.organization.compareTo(worker.organization) < 0) {
                return -1;
            }
        } catch (NullPointerException e) {
            System.out.println("One (both) of the Workers' organizations is (are) null, thus organization field will not participate in the comparison.");
        }
        return Character.compare(this.name.charAt(0), worker.name.charAt(0));
    }
}
