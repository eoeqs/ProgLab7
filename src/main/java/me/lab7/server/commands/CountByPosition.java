package me.lab7.server.commands;

import me.lab7.common.models.User;
import me.lab7.common.network.Response;
import me.lab7.common.models.Position;
import me.lab7.common.models.Worker;
import me.lab7.server.managers.CollectionManager;

import java.util.List;

public class CountByPosition implements Command {
    CollectionManager collectionManager;

    /**
     * Constructs a new CountByPosition command with the given ColMan object.
     *
     * @param collectionManager the ColMan object to use for the command
     */
    public CountByPosition(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command with the given argument.
     *
     * @param arg  the Position value to count
     * @param user
     */
    @Override
    public Response execute(Object arg, User user) {
        String argStr = (String) arg;
        Position position = Position.valueOf(argStr.toUpperCase());
        List<Worker> workers = collectionManager.getWorkers();
        int count = workers.stream().filter(w -> w.getPosition() == position).toList().size();
        if (count == 0) {
            return new Response("The collection doesn't contain elements with such position value.\n");
        } else {
            return new Response("The collection contains " + count + " element(s) with position = " + position + ".\n");
        }
    }

    /**
     * Returns the name of the command.
     *
     * @return the name of the command
     */
    @Override
    public String name() {
        return "count_by_position";
    }

    /**
     * Returns the argument string for use in a help message.
     *
     * @return the argument string for the command
     */
    @Override
    public String argDesc() {
        return "{Position ( " + Position.allPositions() + ")}";
    }

    /**
     * Returns the description of the command for use in a help message.
     *
     * @return the description of the command
     */
    @Override
    public String desc() {
        return "print out the number of elements with Position field value equal to given";
    }


}
