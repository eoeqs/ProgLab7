package me.lab7.server.managers.databaseManagers;

import me.lab7.common.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerDatabaseManager {

    private final ConnectionManager connectionManager;
    private final UserDatabaseManager

    public WorkerDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    private long insertCoordinates(Connection connection, Coordinates coordinates, long creatorId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                insert into coordinates (c_x, c_y, c_creator_id)
                values (?, ?, ?)
                returning c_id
                """);
        statement.setDouble(1, coordinates.getX());
        statement.setDouble(2, coordinates.getY());
        statement.setLong(3, creatorId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getLong(1);
    }

    private long insertAddress(Connection connection, Address address, long creatorId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                insert into addresses (a_street, a_zip_code, a_creator_id)
                values (?, ?, ?)
                returning a_id
                """);
        statement.setString(1, address.getStreet());
        statement.setString(2, address.getZipCode());
        statement.setLong(3, creatorId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getLong(1);
    }

    private long insertOrganization(Connection connection, Organization organization, long creatorId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                insert into organizations (o_full_name, o_annual_turnover, o_employees_count, o_address_id, o_creator_id)
                values (?, ?, ?, ?, ?)
                returning o_id
                """);
        Address address = organization.getPostalAddress();
        long addressId = insertAddress(connection, address, creatorId);
        statement.setString(1, organization.getFullName());
        statement.setInt(2, organization.getAnnualTurnover());
        statement.setLong(3, organization.getEmployeesCount());
        statement.setLong(4, addressId);
        statement.setLong(5, creatorId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getLong(1);
    }

    public void insertWorker(Worker worker, long creatorId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("""
                insert into workers (w_name, w_coordinates_id, w_creation_date, w_salary, w_start_date,
                w_pos, w_status, w_organization_id, w_creator_id)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """);
        Coordinates coordinates = worker.getCoordinates();
        long coordinatesId = insertCoordinates(connection, coordinates, creatorId);
        Organization organization = worker.getOrganization();
        long organizationId = insertOrganization(connection, organization, creatorId);
        statement.setString(1, worker.getName());
        statement.setLong(2, coordinatesId);
        statement.setDate(3, Date.valueOf(worker.getCreationDate()));
        statement.setInt(4, worker.getSalary());
        statement.setDate(5, Date.valueOf(worker.getStartDate()));
        String position = worker.getPosition() == null ? null : String.valueOf(worker.getPosition());
        statement.setString(6, position);
        String status = worker.getStatus() == null ? null : String.valueOf(worker.getStatus());
        statement.setString(7, status);
        statement.setLong(8, organizationId);
        statement.setLong(9, creatorId);
        statement.execute();
        connection.close();
    }

    public Map<Long, Worker> getAllWorkers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("""
                select * from workers w
                left join coordinates c on w.w_coordinates_id = c.c_id
                left join organizations o on w.w_organization_id = o.o_id
                left join addresses a on o.o_address_id = a.a_id;
                """);
        ResultSet resultSet = statement.executeQuery();
        connection.close();
        Map<Long, Worker> workerMap = new HashMap<>();
        while (resultSet.next()) {
            Worker worker = assembleWorker(resultSet);
            workerMap.put(worker.getId(), worker);
        }
        return workerMap;
    }

    private Worker assembleWorker(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("w_id");
        String name = resultSet.getString("w_name");
        double x = resultSet.getDouble("c_x");
        Double y = resultSet.getDouble("c_y");
        Coordinates coordinates = new Coordinates(x, y);
        LocalDate creationDate = resultSet.getDate("w_creation_date").toLocalDate();
        int salary = resultSet.getInt("w_salary");
        LocalDate startDate = resultSet.getDate("w_start_date").toLocalDate();
        Position position = resultSet.getString("w_pos") == null ? null
                : Position.valueOf(resultSet.getString("w_pos"));
        Status status = resultSet.getString("w_status") == null ? null
                : Status.valueOf(resultSet.getString("w_status"));
        String orgName = resultSet.getString("o_full_name");
        Organization organization;
        if (resultSet.getLong("w_organization_id") == 0) {
            int annualTurnover = resultSet.getInt("o_annual_turnover");
            long employeeCount = resultSet.getLong("o_employee_count");
            String street = resultSet.getString("a_street");
            String zipCode = resultSet.getString("a_zip_code");
            Address address = new Address(street, zipCode);
            organization = new Organization(orgName, annualTurnover, employeeCount, address);
        } else {
            organization = null;
        }
        long creatorId = resultSet.getLong("w_creator_id");

        return new Worker(id, name, coordinates, creationDate, salary, startDate, position, status, organization, creatorId);
    }

    private void deleteWorker(Connection connection, long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                select w_organization_id from workers
                where w_id = ?
                """);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long organizationId = resultSet.getLong(1);
        statement = connection.prepareStatement("""
                delete from coordinates
                where c_id in
                (select c_id from coordinates join workers
                on c_id = w_coordinates_id and w_id = ?)
                """);
        statement.setLong(1, id);
        statement.execute();
        statement = connection.prepareStatement("""
                delete from addresses
                where a_id in
                (select a_id from addresses join organizations
                on a_id = organizations.o_address_id and o_id = ?)
                """);
        statement.setLong(1, organizationId);
        statement.execute();
    }

    private void deleteWorker(Connection connection, List<Long> ids) throws SQLException {
        for (long l : ids) {
            deleteWorker(connection, l);
        }
    }

    public void deleteWorker(long id) throws SQLException {
        Connection connection = getConnection();
        deleteWorker(connection, id);
    }

    public void deleteLowerWorkers(long id, long creatorId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("""
                select w_id from workers
                where w_id < ? and w_creator_id = ?
                """);
        statement.setLong(1, id);
        statement.setLong(2, creatorId);
        ResultSet resultSet = statement.executeQuery();
        deleteWorker(connection, getIdList(resultSet));
        connection.close();
    }

    public void deleteOwnedWorkers(long creatorId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("""
                select w_id from workers
                where w_creator_id = ?
                """);
        statement.setLong(1, creatorId);
        ResultSet resultSet = statement.executeQuery();
        deleteWorker(connection, getIdList(resultSet));
        connection.close();
    }

    private List<Long> getIdList(ResultSet resultSet) throws SQLException {
        List<Long> ids = new ArrayList<>();
        while(resultSet.next()) {
            ids.add(resultSet.getLong(1));
        }
        return ids;
    }

    private void updateAddress(Connection connection, long addressId, Address address) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                update addresses
                set a_street = ?, a_zip_code = ?
                where a_id = ?
                """);
        statement.setString(1, address.getStreet());
        statement.setString(2, address.getZipCode());
        statement.setLong(3, addressId);
        statement.execute();
    }

    private void updateCoordinates(Connection connection, long coordinatesId, Coordinates coordinates) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                update coordinates
                set c_x = ?, c_y = ?
                where c_id = ?
                """);
        statement.setDouble(1, coordinates.getX());
        statement.setDouble(2, coordinates.getY());
        statement.setLong(3, coordinatesId);
        statement.execute();
    }

    private void updateOrganization(Connection connection, long organizationId, Organization organization) throws SQLException {
        Address address = organization.getPostalAddress();
        PreparedStatement statement = connection.prepareStatement("""
                select a_id from addresses join organizations
                on a_id = o_address_id and o_id = ?
                """);
        statement.setLong(1, organizationId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long addressId = resultSet.getLong(1);
        updateAddress(connection, addressId, address);

        statement = connection.prepareStatement("""
                update organizations
                set o_full_name = ?, o_annual_turnover = ?, o_employees_count = ?
                """);
        statement.setString(1, organization.getFullName());
        statement.setInt(2, organization.getAnnualTurnover());
        statement.setLong(3, organization.getEmployeesCount());
        statement.execute();
    }

    public void updateWorker(long id, Worker worker) throws SQLException {
        Connection connection = getConnection();
        Organization organization = worker.getOrganization();
        PreparedStatement statement = connection.prepareStatement("""
                select o_id from organizations join workers
                on o_id = workers.w_organization_id and w_id = ?
                """);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long orgId = resultSet.getLong(1);
        updateOrganization(connection, orgId, organization);

        Coordinates coordinates = worker.getCoordinates();
        statement = connection.prepareStatement("""
                select c_id from coordinates join workers
                on c_id = workers.w_coordinates_id and w_id = ?
                """);
        statement.setLong(1, id);
        resultSet = statement.executeQuery();
        resultSet.next();
        long coordinatesId = resultSet.getLong(1);
        updateCoordinates(connection, coordinatesId, coordinates);

        statement = connection.prepareStatement("""
                update workers
                set w_name = ?, w_salary = ?, w_start_date = ?, w_pos = ?, w_status = ?
                """);
        statement.setString(1, worker.getName());
        statement.setInt(2, worker.getSalary());
        statement.setDate(3, Date.valueOf(worker.getStartDate()));
        String pos = worker.getPosition() == null ? null : String.valueOf(worker.getPosition());
        statement.setString(4, pos);
        String status = worker.getStatus() == null ? null : String.valueOf(worker.getStatus());
        statement.setString(5, status);
        statement.execute();
        connection.close();
    }

}
