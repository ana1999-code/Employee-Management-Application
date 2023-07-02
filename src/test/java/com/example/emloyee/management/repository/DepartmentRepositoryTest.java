package com.example.emloyee.management.repository;

import com.example.emloyee.management.model.entity.Department;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentRepositoryTest {

    public static final int ID = 10;

    private Department department;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DepartmentRepository departmentRepository;


    @Before
    public void setUp() {
        department = Department.builder()
                .id(1)
                .name("IT")
                .location("Chisinau")
                .build();
    }

    @Test
    public void itShouldFindAllDepartments() throws SQLException {
        final String query = "SELECT * FROM departments";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(query)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(department.getId());
        when(resultSet.getString("name")).thenReturn(department.getName());
        when(resultSet.getString("location")).thenReturn(department.getLocation());

        List<Department> departments = departmentRepository.findAll();

        assertThat(departments.contains(department)).isTrue();
        assertThat(departments.size()).isEqualTo(1);
    }

    @Test
    public void itShouldFindById() throws SQLException {
        final String query = "SELECT * FROM departments WHERE id = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setInt(1, department.getId());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("name")).thenReturn(department.getName());
        when(resultSet.getString("location")).thenReturn(department.getLocation());

        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());

        assertThat(optionalDepartment.get()).isEqualTo(department);
    }

    @Test
    public void itShouldSaveDepartment() throws SQLException {
        final String query = "INSERT INTO departments(name, location) VALUES (?, ?)";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(1, department.getName());
        doNothing().when(preparedStatement).setString(2, department.getLocation());
        when(preparedStatement.execute()).thenReturn(true);

        assertThatNoException().isThrownBy(() -> departmentRepository.save(department));
    }

    @Test
    public void itShouldDeleteById() throws SQLException {
        final String query = "DELETE FROM departments WHERE id = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setInt(1, department.getId());
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertThatNoException().isThrownBy(() -> departmentRepository.deleteById(department.getId()));
    }

    @Test
    public void itShouldUpdateDepartment() throws SQLException {

        final String query = "UPDATE departments SET name = ?, location = ? WHERE id = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(1, department.getName());
        doNothing().when(preparedStatement).setString(2, department.getLocation());
        doNothing().when(preparedStatement).setInt(3, department.getId());
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertThatNoException().isThrownBy(() -> departmentRepository.update(department, department.getId()));
    }

    @Test
    public void itShouldDeleteAllDepartments() throws SQLException {
        final String query = "DELETE FROM departments";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertThatNoException().isThrownBy(() -> departmentRepository.deleteAll());
    }
}
