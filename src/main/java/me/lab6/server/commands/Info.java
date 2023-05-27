package me.lab6.server.commands;


import me.lab6.common.network.Response;
import me.lab6.common.workerRelated.Worker;
import me.lab6.server.managers.CollectionManager;

/**
 * The {@code Info} class represents a command to print out information about the collection.
 * It implements the {@code Command} interface.
 */
public class Info implements Command {
    CollectionManager collectionManager;

    /**
     * Constructs an {@code Info} object with the given collection manager.
     *
     * @param collectionManager the collection manager
     */
    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Prints out information about the collection.
     * Prints out the collection's initialization date, the number of elements and that the collection stores Workers.
     * The collection keys are the same as the Workers' id's.
     *
     * @param arg the command argument (not used)
     */
    @Override
    public Response execute(Object arg) {
        StringBuilder sb = new StringBuilder("This collection stores Worker elements in a HashMap.\n");
        sb.append("The workers' IDs match the map's keys.\n");
        sb.append(collectionManager.workerMap().isEmpty() ? "This collection is yet empty.\n" : "Currently there are " +
                collectionManager.workerMap().size() + " elements in this collection.\n");
        if (!collectionManager.workerMap().isEmpty()) {
            int salarySum = collectionManager.workerMap().values().stream().mapToInt(Worker::getSalary).sum();
            sb.append("The sum of all workers' salaries is ").append(salarySum).append("\n");
        }
        return new Response(sb.toString());
    }

    /**
     * Returns the command name.
     *
     * @return the command name
     */
    @Override
    public String name() {
        return "info";
    }

    /**
     * Returns the command argument format.
     * The {@code Info} command doesn't require an argument, so it returns an empty string.
     *
     * @return an empty string
     */
    @Override
    public String argDesc() {
        return null;
    }

    /**
     * Returns the command description for the help command.
     *
     * @return the command description
     */
    @Override
    public String desc() {
        return "print out the information about the collection";
    }
}
