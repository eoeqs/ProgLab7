package me.lab7.server.commands;

import me.lab7.common.models.User;
import me.lab7.common.network.CommandResponse;
import me.lab7.common.models.Organization;
import me.lab7.common.models.Worker;
import me.lab7.server.managers.CollectionManager;

import java.util.List;

/**
 * Represents a command to print out elements from the worker collection with an organization value greater than a given organization.
 * Implements the {@link Command} interface.
 */
public class FilterGreaterThanOrganization implements Command {
    CollectionManager collectionManager;
    /**
     * Constructs a new {@code FilterGreaterThanOrganization} command with the specified collection manager.
     *
     * @param collectionManager the collection manager to be used for executing the command
     */
    public FilterGreaterThanOrganization(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    /**
     * Prints out elements from the worker collection with an organization value greater than a given organization.
     * If there are no elements that match the criteria, prints out a message indicating that the collection does not contain such elements.
     * Prompts the user to enter the details of the target organization.
     *
     * @param arg  the arguments for the command (not used in this command)
     * @param user
     */
    @Override
    public CommandResponse execute(Object arg, User user) {
        Organization organization = (Organization) arg;
        List<Worker> filtered = collectionManager.getWorkers()
                .stream().filter(w -> w.getOrganization().compareTo(organization) > 0).toList();
        if (filtered.size() == 0) {
            return new CommandResponse("The collection doesn't contain elements with organization value greater than given.\n");
        } else {
            StringBuilder sb = new StringBuilder();
            filtered.forEach(worker -> sb.append(worker).append("\n"));
            return new CommandResponse(sb.toString());
        }
    }

    /**
     * Returns the name of the command as a string ("filter_greater_than_organization").
     *
     * @return the name of the command as a string
     */
    @Override
    public String name() {
        return "filter_greater_than_organization";
    }
    /**
     * Returns the argument string for the command (empty string in this case).
     *
     * @return the argument string for the command
     */
    @Override
    public String argDesc() {
        return null;
    }
    /**
     * Returns a string description of the command ("print out elements with Organization value greater than given").
     *
     * @return a string description of the command
     */
    @Override
    public String desc() {
        return "print out elements with Organization value greater than given";
    }

}
