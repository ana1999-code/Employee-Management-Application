package com.example.emloyee.management.repository;

import com.example.emloyee.management.model.entity.Employee;
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
public class EmployeeRepositoryTest {

    private Employee employee;

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
    private EmployeeRepository employeeRepository;

    @Before
    public void setUp() throws Exception {
        employee = Employee.builder()
                .id(1)
                .firstName("John")
                .lastName("Smith")
                .email("johns@email.com")
                .salary(2000.0)
                .departmentId(1)
                .build();
    }

    @Test
    public void itShouldFindAllEmployees() throws SQLException {
        final String query = "SELECT * FROM employees";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(query)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("first_name")).thenReturn(employee.getFirstName());
        when(resultSet.getString("last_name")).thenReturn(employee.getLastName());
        when(resultSet.getInt("department_id")).thenReturn(employee.getDepartmentId());
        when(resultSet.getString("email")).thenReturn(employee.getEmail());
        when(resultSet.getDouble("salary")).thenReturn(employee.getSalary());
        when(resultSet.getInt("id")).thenReturn(employee.getId());

        List<Employee> employees = employeeRepository.findAll();

        assertThat(employees.get(0)).isEqualTo(employee);
        assertThat(employees.size()).isEqualTo(1);
    }

    @Test
    public void itShouldFindEmployeeById() throws SQLException {
        final String query = "SELECT * FROM employees WHERE id = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setInt(1, employee.getId());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("first_name")).thenReturn(employee.getFirstName());
        when(resultSet.getString("last_name")).thenReturn(employee.getLastName());
        when(resultSet.getInt("department_id")).thenReturn(employee.getDepartmentId());
        when(resultSet.getString("email")).thenReturn(employee.getEmail());
        when(resultSet.getDouble("salary")).thenReturn(employee.getSalary());
        when(resultSet.getInt("id")).thenReturn(employee.getId());

        Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getId());

        assertThat(optionalEmployee.get()).isEqualTo(employee);
    }

    @Test
    public void itShouldSaveEmployee() throws SQLException {
        final String query = "INSERT INTO employees(first_name, last_name, department_id, email, phone_number, salary)" +
                "VALUES(?, ?, ?, ?, ?, ?)";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(1, employee.getFirstName());
        doNothing().when(preparedStatement).setString(2, employee.getLastName());
        doNothing().when(preparedStatement).setInt(3, employee.getDepartmentId());
        doNothing().when(preparedStatement).setString(4, employee.getEmail());
        doNothing().when(preparedStatement).setString(5, employee.getPhoneNumber());
        doNothing().when(preparedStatement).setDouble(6, employee.getSalary());
        when(preparedStatement.execute()).thenReturn(true);

        assertThatNoException().isThrownBy(() -> employeeRepository.save(employee));
    }

    @Test
    public void itShouldDeleteEmployeeById() throws SQLException {
        final String query = "DELETE FROM employees WHERE id = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setInt(1, employee.getId());
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertThatNoException().isThrownBy(() -> employeeRepository.deleteById(employee.getId()));
    }

    @Test
    public void itShouldReturnTrue_WhenExistsByEmail() throws SQLException {
        final String query = "SELECT * FROM employees WHERE email = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(1, employee.getEmail());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean existsByEmail = employeeRepository.existsByEmail(employee.getEmail());

        assertThat(existsByEmail).isTrue();
    }

    @Test
    public void itShouldReturnFalse_WhenNotExistsByEmail() throws SQLException {
        final String query = "SELECT * FROM employees WHERE email = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(1, employee.getEmail());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean existsByEmail = employeeRepository.existsByEmail(employee.getEmail());

        assertThat(existsByEmail).isFalse();
    }

    @Test
    public void itShouldReturnTrue_WhenExistsByPhoneNumber() throws SQLException {
        final String query = "SELECT * FROM employees WHERE phone_number = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean existsByEmail = employeeRepository.existsByPhoneNumber(employee.getEmail());

        assertThat(existsByEmail).isTrue();
    }

    @Test
    public void itShouldReturnFalse_WhenNotExistsByPhoneNumber() throws SQLException {
        final String query = "SELECT * FROM employees WHERE phone_number = ?";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean existsByEmail = employeeRepository.existsByPhoneNumber(employee.getPhoneNumber());

        assertThat(existsByEmail).isFalse();
    }

    @Test
    public void itShouldDeleteAllDepartments() throws SQLException {
        final String query = "DELETE FROM employees";

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertThatNoException().isThrownBy(() -> employeeRepository.deleteAll());
    }
}