package me.lab7.client.io;


import me.lab7.client.network.UDPClient;
import me.lab7.client.utility.ScriptValidator;
import me.lab7.client.exceptions.InvalidScriptException;
import me.lab7.client.exceptions.ScriptRecursionException;
import me.lab7.common.models.User;
import me.lab7.common.network.AuthRequest;
import me.lab7.common.network.AuthResponse;
import me.lab7.common.network.Request;
import me.lab7.common.network.Response;
import me.lab7.common.utility.Messages;
import me.lab7.common.utility.Validator;
import me.lab7.common.models.Organization;
import me.lab7.common.models.Worker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Console {
    private final Scanner scanner = new Scanner(System.in);
    private final UDPClient client;
    private final EntityConstructor constructor;
    private static User user;

    public Console(UDPClient client) {
        this.client = client;
        constructor = new EntityConstructor(this);
    }

    public void initiate() {
        System.out.println(Messages.logo());
        boolean chosen = false;
        try {
            while (!chosen) {
                System.out.println("Enter 'L' to log in or 'R' to register.");
                String answer = getInput();
                if (!(answer.equalsIgnoreCase("L") || answer.equalsIgnoreCase("R"))) {
                    System.out.println(Messages.chooseLogInOrRegister());
                } else {
                    if (answer.equalsIgnoreCase("L")) {
                        chosen = logIn();
                    } else {
                        chosen = register();
                    }
                }
            }
            interact();
        } catch (NoSuchElementException e) {
            System.exit(0);
        }
    }

    private boolean logIn() {
        System.out.println("Enter your username and password.");
        try {
            while (true) {
                System.out.print("$ ");
                String[] input = getInput().split("\\s+");
                if (input.length != 2) {
                    System.out.println("Please, enter only your username and password as a single string.");
                } else {
                    user = new User(input[0], input[1]);
                    AuthResponse response = tryToAuthorize(true, user);
                    if (response == null) {
                        return false;
                    } else {
                        System.out.println(response.message());
                        return response.success();
                    }
                }
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean register() {
        System.out.println("Enter your username and password.");
        System.out.println("Username must not be longer than 64 characters.");
        System.out.println(Messages.passwordRequirements());
        try {
            while (true) {
                System.out.print("$ ");
                String[] input = getInput().split("\\s+");
                if (input.length != 2) {
                    System.out.println("Please, enter only your username and password as a single string.");
                } else {
                    if (input[0].length() > 64) {
                        System.out.println("Your username is too long. Please, use another.");
                    } else {
                        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[-_.])[a-zA-Z0-9-_.]{8,64}$";
                        String password = input[1];
                        if (!Pattern.matches(regex, password)) {
                            System.out.println("Your password doesn't meet the requirements. Please, use another.");
                        } else {
                            user = new User(input[0], input[1]);
                            AuthResponse response = tryToAuthorize(false, user);
                            if (response == null) {
                                return false;
                            } else {
                                System.out.println(response.message());
                                return response.success();
                            }
                        }
                    }
                }
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void interact() {
        System.out.println(Messages.hello());
        while (true) {
            System.out.print("$ ");
            try {
                handleInput();
            } catch (NoSuchElementException e) {
                System.exit(0);
            }
        }
    }

    public String getInput() throws NoSuchElementException {
        return scanner.nextLine().trim();
    }

    private void handleInput() throws NoSuchElementException {
        String inputStr = getInput();
        if (!inputStr.isBlank()) {
            String[] input = inputStr.split("\\s+", 2);
            String validationResult = Validator.validateCommandAndArg(input);
            if (validationResult == null) {
                Response response = null;
                if (input[0].equalsIgnoreCase("insert") || input[0].equalsIgnoreCase("replace_if_lower")
                        || input[0].equalsIgnoreCase("update")) {
                    try {
                        Worker worker = constructor.constructWorker(Long.parseLong(input[1]), user.name());
                        response = tryToProcess(input[0], worker);
                    } catch (NoSuchElementException e) {
                        System.out.println("Worker description process was canceled.\n");
                    }
                } else if (input[0].equalsIgnoreCase("filter_greater_than_organization")) {
                    try {
                        Organization organization = constructor.constructOrganization();
                        response = tryToProcess(input[0], organization);
                    } catch (NoSuchElementException e) {
                        System.out.println("Organization description process was canceled.");
                    }
                } else if (input[0].equalsIgnoreCase("execute_script")) {
                    try {
                        String scriptContent = new ScriptValidator(input[1]).getFinalScript();
                        response = tryToProcess(input[0], scriptContent);
                    } catch (InvalidScriptException e) {
                        System.out.println(Messages.invalidScript());
                    } catch (FileNotFoundException e) {
                        System.out.println(Messages.fileNotFound());
                    } catch (ScriptRecursionException e) {
                        System.out.println(Messages.scriptRecursion());
                    }
                } else {
                    if (input.length > 1) {
                        response = tryToProcess(input[0], input[1]);
                    } else {
                        response = tryToProcess(input[0], null);
                    }
                }
                if (response != null) {
                    if (response.message().equals(Messages.goodbye())) {
                        System.exit(0);
                    }
                    System.out.println(response);
                }
            } else {
                System.out.println(validationResult);
            }
        }
    }

    private Response tryToProcess(String input, Object arg) {
        try {
            return getResponseForRequest(input, arg);
        } catch (IOException e) {
            System.out.println(Messages.tryingAgain());
            try {
                Thread.sleep(1500);
                return getResponseForRequest(input, arg);
            } catch (IOException ex) {
                System.out.println(Messages.serverCommunicationError());
            } catch (InterruptedException ignored) {
            }
        }
        return null;
    }

    private Response getResponseForRequest(String command, Object argument) throws IOException {
        return client.communicateWithServer(new Request(command, argument, user));
    }

    private AuthResponse tryToAuthorize(boolean logInOrRegister, User user) {
        AuthRequest request = new AuthRequest(logInOrRegister, user);
        try {
            return getAuthResponse(request);
        } catch (IOException e) {
            System.out.println(Messages.tryingAgain());
            try {
                Thread.sleep(1500);
                return getAuthResponse(request);
            } catch (IOException ex) {
                System.out.println(Messages.serverCommunicationError());
            } catch (InterruptedException ignored) {
            }
        }
        return null;
    }

    private AuthResponse getAuthResponse(AuthRequest request) throws IOException {
        return client.authorize(request);
    }

}
