package me.lab6.client.utility;

import me.lab6.client.exceptions.InvalidScriptException;
import me.lab6.client.exceptions.ScriptRecursionException;
import me.lab6.common.utility.Constraints;
import me.lab6.common.utility.DataType;
import me.lab6.common.utility.Validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ScriptValidator {

    private final Scanner scanner;
    private static final List<String> allScriptsContent = new ArrayList<>();
    private static final Deque<File> scriptDeque = new ArrayDeque<>();

    public ScriptValidator(String scriptName) throws FileNotFoundException {
        try {
            File script = new File(scriptName).getAbsoluteFile();
            scanner = new Scanner(script);
            scriptDeque.addLast(script);
        } catch (SecurityException e) {
            throw new FileNotFoundException();
        }
    }

    private String next() {
        return scanner.nextLine().trim();
    }

    public String getFinalScript() throws FileNotFoundException, InvalidScriptException, ScriptRecursionException {
        try {
            if (validateScript()) {
                StringBuilder sb = new StringBuilder();
                allScriptsContent.forEach(s -> sb.append(s).append("\n"));
                allScriptsContent.clear();
                return sb.toString();
            } else {
                allScriptsContent.clear();
                scriptDeque.clear();
                throw new InvalidScriptException();
            }
        } catch (ScriptRecursionException e) {
            allScriptsContent.clear();
            scriptDeque.clear();
            throw new ScriptRecursionException();
        }
    }

    private boolean validateScript() throws FileNotFoundException, ScriptRecursionException {
        while (scanner.hasNextLine()) {
            String currentString = next();
            if (currentString.isBlank()) {
                continue;
            }
            String[] currentWords = currentString.split("\\s+", 2);
            if (Validator.validateCommandAndArg(currentWords) != null) {
                return false;
            }
            if (currentWords[0].equalsIgnoreCase("insert") || currentWords[0].equalsIgnoreCase("update")
                    || currentWords[0].equalsIgnoreCase("replace_if_lower")) {
                allScriptsContent.add(currentString);
                if (!validateWorkerFields()) {
                    return false;
                } else {
                    continue;
                }
            } else if (currentWords[0].equalsIgnoreCase("filter_greater_than_organization")) {
                allScriptsContent.add(currentString);
                if (!validateOrganizationFields()) {
                    return false;
                } else {
                    continue;
                }
            } else if (currentWords[0].equalsIgnoreCase("execute_script")) {
                if (!validateNestedScript(currentWords[1])) {
                    return false;
                }
            }
            if (!currentWords[0].equalsIgnoreCase("execute_script")) {
                allScriptsContent.add(currentString);
            }
        }
        return true;
    }

    private boolean checkRecursion(String scriptName) {
        File file = new File(scriptName).getAbsoluteFile();
        return !scriptDeque.contains(file);
    }

    private boolean validateWorkerFields() {
        try {
            String currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.STRING, false, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.DOUBLE, true, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.DOUBLE, true, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.INT, false, true)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.DATE, false, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.POSITION, true, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.STATUS, true, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            allScriptsContent.add(currentString);
            if (currentString.isBlank()) {
                return true;
            } else {
                return validateOrganizationFields();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            return false;
        }
    }

    private boolean validateOrganizationFields() {
        try {
            String currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.STRING, false, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.INT, false, true)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.LONG, false, true)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.STRING, true, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
            }
            currentString = next();
            if (Validator.validateData(currentString, new Constraints(DataType.STRING, false, false)) != 0) {
                return false;
            } else {
                allScriptsContent.add(currentString);
                return true;
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            return false;
        }
    }

    private boolean validateNestedScript(String scriptName) throws FileNotFoundException, ScriptRecursionException {
        if (checkRecursion(scriptName)) {
            ScriptValidator scriptValidator = new ScriptValidator(scriptName);
            boolean result = scriptValidator.validateScript();
            scriptDeque.removeLast();
            return result;
        } else {
            throw new ScriptRecursionException();
        }
    }
}
