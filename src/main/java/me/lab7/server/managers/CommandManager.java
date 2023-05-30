package me.lab7.server.managers;



import me.lab7.common.network.Request;
import me.lab7.common.network.Response;
import me.lab7.server.commands.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ComMan class represents a command manager that handles the user input commands
 * and executes the corresponding actions.
 */
public class CommandManager {
    private final Map<String, Command> commandMap;
    private final List<String> history;
    private final FileManager fileManager;
    private final CollectionManager collectionManager;

    {
        history = new ArrayList<>();
    }

    /**
     * Constructs a ComMan object with the specified column and file managers, and interactive mode flag.
     *
     * @param collectionManager  The Column Manager object.
     * @param fileManager The File Manager object.
     */
    public CommandManager(CollectionManager collectionManager, FileManager fileManager) {
        this.fileManager = fileManager;
        this.collectionManager = collectionManager;
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("info", new Info(collectionManager));
        commandMap.put("show", new Show(collectionManager));
        commandMap.put("insert", new Insert(collectionManager));
        commandMap.put("update", new Update(collectionManager));
        commandMap.put("remove_key", new RemoveKey(collectionManager));
        commandMap.put("clear", new Clear(collectionManager));
        commandMap.put("exit", new Exit());
        commandMap.put("history", new History(history));
        commandMap.put("replace_if_lower", new ReplaceIfLower(collectionManager));
        commandMap.put("remove_lower_key", new RemoveLowerKey(collectionManager));
        commandMap.put("min_by_status", new MinByStatus(collectionManager));
        commandMap.put("count_by_position", new CountByPosition(collectionManager));
        commandMap.put("filter_greater_than_organization", new FilterGreaterThanOrganization(collectionManager));
        commandMap.put("help", new Help(new ArrayList<>(commandMap.values())));
        commandMap.put("execute_script", new ExecuteScript(commandMap));
        this.commandMap = commandMap;
    }

    public Response handleRequest(Request request) {
        return executeCommand(request.command(), request.argument());
    }

    public Response executeCommand(String command, Object arg) {
        Response response = commandMap.get(command).execute(arg);
        history.add(command);
        if (history.size() > 6) {
            history.remove(0);
        }
        return response;
    }

    public boolean save() {
        try {
            fileManager.writeWorkersToFile(collectionManager);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
