package me.lab6.server.io;

import me.lab6.common.utility.Messages;
import me.lab6.server.managers.CommandManager;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerConsole {
    private final Scanner scanner;
    private final CommandManager commandManager;

    public ServerConsole(Scanner scanner, CommandManager commandManager) {
        this.scanner = scanner;
        this.commandManager = commandManager;
    }

    public boolean handleServerInput() {
        try {
            if (System.in.available() > 0) {
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "save" -> {
                        try {
                            commandManager.save();
                            System.out.println(Messages.saved());
                            return false;
                        } catch (Exception e) {
                            System.out.println(Messages.failedToSave());
                            return false;
                        }
                    }
                    case "exit" -> {
                        if (confirmExit()) {
                            System.exit(0);
                        }
                    }
                    default -> {
                        System.out.println("You can only use 'save' and 'exit' in the Server console.\n");
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
        return false;
    }

    private boolean confirmExit() {
        if (commandManager.save()) {
            return true;
        } else {
            System.out.println(Messages.failedToSave());
            System.out.println("Exit anyway? (Enter anything to exit, press Enter without typing anything to cancel)\n");
            try {
                if (!scanner.nextLine().isEmpty()) {
                    return true;
                }
            } catch (NoSuchElementException n) {
                return true;
            }
        }
        return false;
    }

    public void exit() {
        if (commandManager.save()) {
            System.out.println(Messages.saved());
        } else {
            System.out.println(Messages.failedToSave());
        }
        System.out.println(Messages.serverGoodbye());
    }
}
