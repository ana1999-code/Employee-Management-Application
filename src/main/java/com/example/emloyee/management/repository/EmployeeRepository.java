package com.example.emloyee.management.repository;

import com.example.emloyee.management.exception.NoUpdateException;
import com.example.emloyee.management.model.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository implements GenericRepository<Employee, Integer> {

    @Autowired
    private DataSource dataSource;

    private static void createQuery(Employee employee, StringBuilder query, List<Object> parameters) {
        if (employee.getFirstName() != null) {
            query.append("first_name = ?, ");
            parameters.add(employee.getFirstName());
        }

        if (employee.getLastName() != null) {
            query.append("last_name = ?, ");
            parameters.add(employee.getLastName());
        }

        if (employee.getDepartmentId() != null) {
            query.append("department_id = ?, ");
            parameters.add(employee.getDepartmentId());
        }

        if (employee.getEmail() != null) {
            query.append("email = ?, ");
            parameters.add(employee.getEmail());
        }

        if (employee.getPhoneNumber() != null) {
            query.append("phone_number = ?, ");
            parameters.add(employee.getPhoneNumber());
        }

        if (employee.getSalary() != null) {
            query.append("salary = ?, ");
            parameters.add(employee.getSalary());
        }

        query.delete(query.length() - 2, query.length());
        query.append(" WHERE id = ?");
    }

    @Override
    public List<Employee> findAll() {
        final String query = "SELECT * FROM employees";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next()) {
                employees.add(Employee.builder()
                        .id(resultSet.getInt("id"))
                        .firstName(resultSet.getString("first_name"))
                        .lastName(resultSet.getString("last_name"))
                        .departmentId(resultSet.getInt("department_id"))
                        .email(resultSet.getString("email"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .salary(resultSet.getDouble("salary"))
                        .build());
            }

            return employees;

        } catch (SQLException e) {
            return employees;
        }
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        final String query = "SELECT * FROM employees WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(Employee.builder()
                            .firstName(resultSet.getString("first_name"))
                            .lastName(resultSet.getString("last_name"))
                            .departmentId(resultSet.getInt("department_id"))
                            .email(resultSet.getString("email"))
                            .phoneNumber(resultSet.getString("phone_number"))
                            .salary(resultSet.getDouble("salary"))
                            .id(resultSet.getInt("id"))
                            .build());
                }
                return Optional.empty();
            }


        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Employee employee) {
        final String query = "INSERT INTO employees(first_name, last_name, department_id, email, phone_number, salary)" +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setInt(3, employee.getDepartmentId());
            preparedStatement.setString(4, employee.getEmail());
            preparedStatement.setString(5, employee.getPhoneNumber());
            preparedStatement.setDouble(6, employee.getSalary());

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        final String query = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Employee employee, Integer id) {
        StringBuilder query = new StringBuilder("UPDATE employees SET ");
        List<Object> parameters = new ArrayList<>();

        createQuery(employee, query, parameters);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            preparedStatement.setInt(parameters.size() + 1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new NoUpdateException("No update for employee with id = [%d]".formatted(id));
        }
    }

    public boolean existsByEmail(String email) {
        final String query = "SELECT * FROM employees WHERE email = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        final String query = "SELECT * FROM employees WHERE phone_number = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, phoneNumber);

            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void deleteAll() {
        final String query = "DELETE FROM employees";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
