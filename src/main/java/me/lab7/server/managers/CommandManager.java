package me.lab7.server.managers;

import me.lab7.common.models.User;
import me.lab7.common.network.CommandRequest;
import me.lab7.common.network.CommandResponse;
import me.lab7.server.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commandMap;
    private final Map<String, ArrayList<String>> history;

    {
        history = new HashMap<>();
    }

    public CommandManager(CollectionManager collectionManager) {
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("info", new Info(collectionManager));
        commandMap.put("show", new Show(collectionManager));
        commandMap.put("insert", new Insert(collectionManager));
        commandMap.put("update", new Update(collectionManager));
        commandMap.put("remove_key", new RemoveKey(collectionManager));
        commandMap.put("clear", new Clear(collectionManager));
        commandMap.put("exit", new Exit(history));
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

    public CommandResponse handleRequest(CommandRequest commandRequest) {
        return executeCommand(commandRequest.command(), commandRequest.argument(), commandRequest.user());
    }

    public CommandResponse executeCommand(String command, Object arg, User user) {
        CommandResponse commandResponse = commandMap.get(command).execute(arg, user);
        List<String> userHistory = history.get(user.name());
        if (userHistory == null) {
            history.put(user.name(), new ArrayList<>());
            history.get(user.name()).add(command);
        } else {
            userHistory.add(command);
            if (userHistory.size() > 6) {
                userHistory.remove(0);
            }
        }
        return commandResponse;
    }

}
