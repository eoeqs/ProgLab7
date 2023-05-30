package me.lab7.common.utility;

import me.lab7.common.models.Position;
import me.lab7.common.models.Status;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Validator {

    private static final Map<String, Constraints> commandsWithoutArg = new HashMap<>();
    private static final Map<String, Constraints> commandsWithArg = new HashMap<>();

    static {
        commandsWithoutArg.put("exit", new Constraints());
        commandsWithoutArg.put("help", new Constraints());
        commandsWithoutArg.put("info", new Constraints());
        commandsWithoutArg.put("show", new Constraints());
        commandsWithoutArg.put("history", new Constraints());
        commandsWithoutArg.put("clear", new Constraints());
        commandsWithoutArg.put("min_by_status", new Constraints());
        commandsWithoutArg.put("filter_greater_than_organization", new Constraints());
        commandsWithArg.put("insert", new Constraints(
                DataType.LONG, Messages.wrongType("insert", "A proper long value"),
                Messages.noInput("insert", "a long value")));
        commandsWithArg.put("remove_key", new Constraints(
                DataType.LONG, Messages.wrongType("remove_key", "A proper long value"),
                Messages.noInput("remove_key", "a long value")));
        commandsWithArg.put("update", new Constraints(
                DataType.LONG, Messages.wrongType("update", "A proper long value"),
                Messages.noInput("update", "a long value")));
        commandsWithArg.put("remove_lower_key", new Constraints(
                DataType.LONG, Messages.wrongType("remove_lower_key",
                "A proper long value"),
                Messages.noInput("remove_lower_key", "a long value")));
        commandsWithArg.put("count_by_position", new Constraints(
                DataType.POSITION, Messages.wrongType("count_by_position",
                "A proper Position value (" + Position.allPositions() + ")"),
                Messages.noInput("count_by_position", Position.allPositions())));
        commandsWithArg.put("replace_if_lower", new Constraints(
                DataType.LONG, Messages.wrongType("replace_if_lower", "A proper long value"),
                Messages.noInput("replace_if_lower", "a long value")));
        commandsWithArg.put("execute_script", new Constraints(DataType.FILEPATH,
                Messages.wrongType("execute_script", "A path to an existing file"),
                Messages.noInput("execute_script", "a file path")));
    }

    public static String validateCommandAndArg(String[] input) {
        String commandStr = input[0].toLowerCase();
        int existence = validateCommand(commandStr);
        if (existence == 0) {
            return "Non-existent command. Please, use 'help' to see information about available commands.\n";
        }
        if (existence > 0) {
            Constraints constraints = commandsWithArg.get(commandStr);
            if (input.length == 1) {
                return constraints.getNoInputMessage();
            }
            switch (validateData(input[1], constraints)) {
                case 5 -> {
                    return constraints.getNoInputMessage();
                }
                case 10 -> {
                    return constraints.getWrongTypeMessage();
                }
            }
        }
        return null;
    }

    private static int validateCommand(String str) {
        if (commandsWithArg.containsKey(str)) {
            return 1;
        }
        if (commandsWithoutArg.containsKey(str)) {
            return -1;
        }
        return 0;
    }

    public static int validateData(String str, Constraints constraints) {
        if (str == null || str.isBlank()) {
            if (!nullCheck(str, constraints)) {
                return 5;
            }
            return 0;
        } else {
            if (!typeCheck(str, constraints) || !positiveCheck(str, constraints)) {
                return 10;
            }
        }
        return 0;
    }

    private static boolean typeCheck(String data, Constraints constraints) {
        switch (constraints.getDataType()) {
            case LONG -> {
                return checkLong(data);
            }
            case POSITION -> {
                return checkPosition(data);
            }
            case FILEPATH -> {
                return checkFilePath(data);
            }
            case INT -> {
                return checkInt(data);
            }
            case DATE -> {
                return checkDate(data);
            }
            case FLOAT -> {
                return checkFloat(data);
            }
            case DOUBLE -> {
                return checkDouble(data);
            }
            case STATUS -> {
                return checkStatus(data);
            }
            default -> {
                return true;
            }
        }
    }

    private static boolean positiveCheck(String number, Constraints constraints) {
        boolean positive = constraints.isPositive();
        if (!positive) {
            return true;
        }
        return Double.compare(Double.parseDouble(number), 0) > 0;
    }

    private static boolean nullCheck(String data, Constraints constraints) {
        if (constraints.getDataType() == null) {
            return true;
        }
        if (data == null || data.isBlank()) {
            return constraints.isNullable();
        }
        return true;
    }

    private static boolean checkLong(String data) {
        try {
            Long.parseLong(data);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean checkInt(String data) {
        try {
            Integer.parseInt(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkFloat(String data) {
        try {
            float f = Float.parseFloat(data);
            return !Float.isInfinite(f);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkDouble(String data) {
        try {
            Double d = Double.parseDouble(data);
            if (d.equals(Double.POSITIVE_INFINITY)) {
                return false;
            }
            return !d.equals(Double.NEGATIVE_INFINITY);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkPosition(String data) {
        try {
            Position.valueOf(data.toUpperCase());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean checkStatus(String data) {
        try {
            Status.valueOf(data.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkDate(String data) {
        try {
            LocalDate ld = LocalDate.parse(data);
            return ld.isAfter(LocalDate.parse("-0001-12-31"));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkFilePath(String data) {
        try {
            new Scanner(new File(data).getAbsoluteFile());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
