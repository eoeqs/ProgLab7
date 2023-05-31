package me.lab7.server.managers.databaseManagers;

import me.lab7.common.models.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class WorkerDatabaseManager {
    private final ConnectionManager connectionManager;

    public WorkerDatabaseManager(String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public WorkerDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public Map<Long, Address> loadAddresses() throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("select * from addresses");
        ResultSet result = statement.executeQuery();

        Map<Long, Address> addresses = new HashMap<>();
        while (result.next()) {
            Long id = result.getLong("id");
            Address address = new Address(
                    result.getString("street"),
                    result.getString("zip_code")
            );
            address.setId(id);
            address.setCreatorId(result.getInt("creator_id"));

            addresses.put(id, address);
        }
        connection.close();
        return addresses;
    }

    public Map<Long, Organization> loadOrganizations() throws SQLException {
        Map<Long, Address> addresses = loadAddresses();
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("select * from organizations");
        ResultSet result = statement.executeQuery();

        Map<Long, Organization> organizations = new HashMap<>();
        while (result.next()) {
            Long id = result.getLong("id");
            long addressId = result.getLong("address_id");
            Address address = addresses.get(addressId);
            Organization organization = new Organization(
                    result.getString("full_name"),
                    result.getInt("annual_turnover"),
                    result.getLong("employees_count"),
                    address
            );
            organization.setId(id);
            organization.setCreatorId(result.getInt("creator_id"));
            organizations.put(id, organization);
        }
        connection.close();
        return organizations;
    }

    public Map<Long, Coordinates> loadCoordinates() throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("select * from coordinates");
        ResultSet result = statement.executeQuery();

        Map<Long, Coordinates> coordinates = new HashMap<>();
        while (result.next()) {
            Long id = result.getLong("id");
            Coordinates coordinate = new Coordinates(
                    result.getDouble("x"),
                    result.getDouble("y")
            );
            coordinate.setId(id);
            coordinate.setCreatorId(result.getInt("creator_id"));

            coordinates.put(id, coordinate);
        }
        connection.close();
        return coordinates;
    }

    public Map<Long, Worker> loadWorkers() throws SQLException {
        Map<Long, Organization> organizations = loadOrganizations();
        Map<Long, Coordinates> coordinates = loadCoordinates();

        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from workers");
        ResultSet result = statement.executeQuery();

        HashMap<Long, Worker> workers = new HashMap<>();
        while (result.next()) {
            int organizationId = result.getInt("organization_id");
            Organization organization = organizations.get(organizationId);
            int coordinateId = result.getInt("coordinates_id");
            Coordinates coordinate = coordinates.get(coordinateId);
            Integer salary = result.getInt("salary");
            if (result.wasNull()) salary = null;
            long id = result.getLong("id");
            Worker worker = new Worker(
                    result.getString("name"),
                    coordinate,
                    result.getTimestamp("creation_date").toLocalDateTime().toLocalDate(),
                    salary,
                    result.getTimestamp("start_date").toLocalDateTime().toLocalDate(),
                    result.getString("pos") == null ? null : Position.valueOf(result.getString("pos")),
                    result.getString("status") == null ? null : Status.valueOf(result.getString("status")),
                    organization
            );
            worker.setId(id);
            worker.setCreatorId(result.getInt("creator_id"));
            workers.put(id, worker);
        }
        connection.close();
        return workers;
    }

    public Long addAddress(User user, Address address) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "insert into addresses(street, zip_code, creator_id)" +
                "values (?,?, (select id from users where users.name=?))" +
                "returning id");
        if (address.getStreet() == null) statement.setNull(1, Types.VARCHAR);
        else statement.setString(1, address.getStreet());
        statement.setString(2, address.getZipCode());
        statement.setString(3, user.getName());
        ResultSet result = statement.executeQuery();
        connection.close();

        result.next();

        return result.getLong(1);
    }

    public Long addOrganization(User user, Organization organization) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "insert into organizations(full_name, annual_turnover, employees_count, address_id, creator_id) " +
                "values (?,?,?,?,(select id from users where users.name=?))" +
                "returning id"
        );
        statement.setString(1, organization.getFullName());
        statement.setInt(2, organization.getAnnualTurnover());
        statement.setLong(3, organization.getEmployeesCount());

        Long address_id = addAddress(user, organization.getPostalAddress());
        organization.getPostalAddress().setId(address_id);

        statement.setLong(4, address_id);
        statement.setString(5, user.getName());
        ResultSet result = statement.executeQuery();
        connection.close();

        result.next();

        return result.getLong(1);
    }

    public Long addCoordinates(User user, Coordinates coordinates) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "insert into coordinates(x, y, сreator_id)" +
                "values (?,?,(select id from users where users.name = ?))" +
                "returning id");
        if (coordinates.getX() == 0) statement.setNull(1, Types.DOUBLE);
        else statement.setDouble(1, coordinates.getX());
        statement.setDouble(2, coordinates.getY());
        statement.setString(3, user.getName());
        ResultSet result = statement.executeQuery();
        connection.close();

        result.next();

        return result.getLong(1);
    }

    public Long addWorker(User user, Worker worker) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "insert into workers(name, coordinates_id, salary, start_date, pos, status, organization_id, creator_id)" +
                "values (?,?,?,?,?,?,?,(select id from users where users.name = ?))" +
                "returning id");

        statement.setString(1, worker.getName());

        Long coordinates_id = addCoordinates(user, worker.getCoordinates());
        worker.getCoordinates().setId(coordinates_id);
        statement.setLong(2, coordinates_id);

        if (worker.getSalary() == 0) statement.setNull(3, Types.INTEGER);
        else statement.setInt(3, worker.getSalary());

        statement.setDate(4, Date.valueOf(worker.getStartDate()));

        if (worker.getPosition() == null) statement.setNull(5, Types.VARCHAR);
        else statement.setString(5, worker.getPosition().toString());

        if (worker.getStatus() == null) statement.setNull(6, Types.VARCHAR);
        else statement.setString(6, worker.getStatus().toString());

        Long organization_id = addOrganization(user, worker.getOrganization());
        worker.getOrganization().setId(organization_id);
        statement.setLong(7, organization_id);
        statement.setString(8, user.getName());

        ResultSet result = statement.executeQuery();
        connection.close();

        result.next();

        return result.getLong(1);
    }
}


