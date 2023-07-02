package com.example.emloyee.management.repository;

import com.example.emloyee.management.exception.NoUpdateException;
import com.example.emloyee.management.model.entity.Department;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DepartmentRepository implements GenericRepository<Department, Integer> {

    private final DataSource dataSource;

    private static void createQuery(Department department, StringBuilder query, List<String> parameters) {
        if (department.getName() != null) {
            query.append("name = ?, ");
            parameters.add(department.getName());
        }

        if (department.getLocation() != null) {
            query.append("location = ?, ");
            parameters.add(department.getLocation());
        }

        query.delete(query.length() - 2, query.length());
        query.append(" WHERE id = ?");
    }

    @Override
    public List<Department> findAll() {
        final String query = "SELECT * FROM departments";
        List<Department> departments = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                departments.add(Department.builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .location(resultSet.getString("location"))
                        .build());
            }

            return departments;

        } catch (SQLException e) {
            return departments;
        }
    }

    @Override
    public Optional<Department> findById(Integer id) {
        final String query = "SELECT * FROM departments WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(Department.builder()
                            .id(id)
                            .name(resultSet.getString("name"))
                            .location(resultSet.getString("location"))
                            .build());
                }
                return Optional.empty();

            }

        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Department department) {
        final String query = "INSERT INTO departments(name, location) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, department.getName());
            preparedStatement.setString(2, department.getLocation());

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        final String query = "DELETE FROM departments WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Department department, Integer id) {
        StringBuilder query = new StringBuilder("UPDATE departments SET ");
        List<String> parameters = new ArrayList<>();

        createQuery(department, query, parameters);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setString(i + 1, parameters.get(i));
            }

            preparedStatement.setInt(parameters.size() + 1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new NoUpdateException("No update for department with id = [%d]".formatted(id));
        }
    }

    @Override
    public void deleteAll() {
        final String query = "DELETE FROM departments";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
