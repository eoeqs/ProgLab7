package me.lab6.server.commands;


import me.lab6.common.network.Response;
import me.lab6.common.models.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Executes commands in the given script.
 */
public class ExecuteScript implements Command {

    private final Map<String, Command> commands;

    public ExecuteScript(Map<String, Command> commands) {
        this.commands = commands;
        this.commands.put("execute_script", this);
    }

    @Override
    public Response execute(Object arg) {
        String script = (String) arg;
        Scanner scanner = new Scanner(script);
        StringBuilder sb = new StringBuilder("Executing script.\n");
        while (scanner.hasNextLine()) {
            Response response = emulateExecution(scanner);
            if (response.message() != null) {
                sb.append(response).append("\n");
            }
        }
        return new Response(sb.append("Script finished execution.\n").toString());
    }

    private Response emulateExecution(Scanner scanner) {
        String currentString = scanner.nextLine();
        String[] words = currentString.split("\\s+", 2);
        if (words[0].isBlank()) {
            return new Response(null);
        }
        if (words[0].equalsIgnoreCase("insert") || words[0].equalsIgnoreCase("update")
                || words[0].equalsIgnoreCase("replace_if_lower")) {
            Worker worker = buildWorker(scanner, Long.parseLong(words[1]));
            return commands.get(words[0]).execute(worker);
        } else if (words[0].equalsIgnoreCase("filter_greater_than_organization")) {
            Organization organization = buildOrganization(scanner);
            return commands.get(words[0]).execute(organization);
        } else {
            if (words.length > 1) {
                return commands.get(words[0]).execute(words[1]);
            } else {
                return commands.get(words[0]).execute(null);
            }
        }
    }

    private Worker buildWorker(Scanner scanner, long key) {
        String name = scanner.nextLine();
        String xStr = scanner.nextLine();
        double x = xStr.isBlank() ? 0 : Double.parseDouble(xStr);
        String yStr = scanner.nextLine();
        Double y = yStr.isBlank() ? 0D : Double.parseDouble(yStr);
        int salary = Integer.parseInt(scanner.nextLine());
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        String posStr = scanner.nextLine();
        Position pos = posStr.isBlank() ? null : Position.valueOf(posStr.toUpperCase());
        String statusStr = scanner.nextLine();
        Status status = statusStr.isBlank() ? null : Status.valueOf(statusStr.toUpperCase());
        Organization org = null;
        if (!scanner.nextLine().isBlank()) {
            org = buildOrganization(scanner);
        }
        return new Worker(key, name, new Coordinates(x, y), LocalDate.now(), salary, startDate, pos, status, org);
    }

    private Organization buildOrganization(Scanner scanner) {
        String name = scanner.nextLine();
        int turnover = Integer.parseInt(scanner.nextLine());
        long empCount = Integer.parseInt(scanner.nextLine());
        String street = scanner.nextLine();
        street = street.isBlank() ? null : street;
        String zipCode = scanner.nextLine();
        return new Organization(name, turnover, empCount, new Address(street, zipCode));
    }

    @Override
    public String name() {
        return "execute_script";
    }

    @Override
    public String argDesc() {
        return "{file_path}";
    }

    @Override
    public String desc() {
        return "execute the sequence of commands from a file";
    }
}
