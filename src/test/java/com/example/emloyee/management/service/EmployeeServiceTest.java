package com.example.emloyee.management.service;

import com.example.emloyee.management.exception.DuplicateResourceException;
import com.example.emloyee.management.exception.NoUpdateException;
import com.example.emloyee.management.exception.ResourceNotFoundException;
import com.example.emloyee.management.model.dto.EmployeeDto;
import com.example.emloyee.management.model.entity.Employee;
import com.example.emloyee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    public static final int ID = 10;

    public static final String NOT_FOUND = "Employee with id = [%d] not found".formatted(ID);

    public static final String DUPLICATE = "%s [%s] is already taken";

    public static final String NO_UPDATES = "No updates for department with id = [%d]";

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Captor
    private ArgumentCaptor<Employee> employeeArgumentCaptor;

    private Employee employee;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1)
                .firstName("John")
                .lastName("Smith")
                .email("john@mail.com")
                .phoneNumber("098765432")
                .departmentId(1)
                .salary(1500.0)
                .build();

        employeeDto = EmployeeDto.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john@mail.com")
                .phoneNumber("098765432")
                .departmentId(1)
                .salary(1500.0)
                .build();
    }

    @Test
    void itShouldGetAllEmployees() {
        final List<Employee> employees = List.of(employee);

        when(employeeRepository.findAll()).thenReturn(employees);
        when(modelMapper.map(employee, EmployeeDto.class)).thenReturn(employeeDto);

        List<EmployeeDto> employeeDtoList = employeeService.getAllEmployees();

        assertThat(employeeDtoList.contains(employeeDto)).isTrue();
        verify(employeeRepository).findAll();
    }

    @Test
    void itShouldGetEmployeeById() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDto.class)).thenReturn(employeeDto);

        EmployeeDto employeeById = employeeService.getEmployeeById(employee.getId());

        assertThat(employeeDto).isEqualTo(employeeById);

        verify(employeeRepository).findById(employee.getId());
    }

    @Test
    void itShouldThrow_WhenGetEmployeeByUnknownId() {
        when(employeeRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(NOT_FOUND);
    }

    @Test
    void itShouldAddEmployee() {
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);
        when(employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())).thenReturn(false);
        when(modelMapper.map(employeeDto, Employee.class)).thenReturn(employee);

        EmployeeDto addedEmployee = employeeService.addEmployee(employeeDto);

        assertThat(employeeDto).isEqualTo(addedEmployee);

        verify(employeeRepository).existsByEmail(employee.getEmail());
        verify(employeeRepository).existsByPhoneNumber(employee.getPhoneNumber());
        verify(employeeRepository).save(employee);
    }

    @Test
    void itShouldThrow_WhenAddEmployeeWithExistingEmail() {
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.addEmployee(employeeDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(DUPLICATE.formatted("Email", employee.getEmail()));

        verify(employeeRepository).existsByEmail(employee.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void itShouldThrow_WhenAddEmployeeWithExistingPhoneNumber() {
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);
        when(employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.addEmployee(employeeDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(DUPLICATE.formatted("Phone number", employee.getPhoneNumber()));

        verify(employeeRepository).existsByEmail(employee.getEmail());
        verify(employeeRepository).existsByPhoneNumber(employee.getPhoneNumber());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void itShouldUpdateEmployee() {

        final String firstName = "Maria";
        final String lastName = "Jonson";
        final String email = "maria@email.com";
        final String phoneNumber = "029384493";
        final Integer departmentId = 2;
        final Double salary = 2500.0;

        final EmployeeDto employeeDtoRequest = EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .departmentId(departmentId)
                .salary(salary)
                .build();

        final Employee employeeRequest = Employee.builder()
                .id(null)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .departmentId(departmentId)
                .salary(salary)
                .build();

        when(employeeRepository.findById(any())).thenReturn(Optional.of(employee));
        when(modelMapper.map(employeeDtoRequest, Employee.class)).thenReturn(employeeRequest);

        employeeService.updateEmployee(employeeDtoRequest, employee.getId());

        verify(employeeRepository).update(employeeArgumentCaptor.capture(), anyInt());

        assertAll(
                () -> assertThat(firstName).isEqualTo(employeeArgumentCaptor.getValue().getFirstName()),
                () -> assertThat(lastName).isEqualTo(employeeArgumentCaptor.getValue().getLastName()),
                () -> assertThat(email).isEqualTo(employeeArgumentCaptor.getValue().getEmail()),
                () -> assertThat(phoneNumber).isEqualTo(employeeArgumentCaptor.getValue().getPhoneNumber()),
                () -> assertThat(salary).isEqualTo(employeeArgumentCaptor.getValue().getSalary()),
                () -> assertThat(departmentId).isEqualTo(employeeArgumentCaptor.getValue().getDepartmentId())
        );
    }

    @Test
    void itShouldThrow_WhenUpdateEmployeeByUnknownId() {
        when(employeeRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(any(EmployeeDto.class), ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(NOT_FOUND);

        verify(employeeRepository).findById(ID);
        verify(employeeRepository, never()).update(any(Employee.class), anyInt());
    }

    @Test
    void itShouldThrow_WhenUpdateExistingEmail() {
        when(employeeRepository.findById(any())).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.updateEmployee(employeeDto, anyInt()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(DUPLICATE.formatted("Email", employee.getEmail()));

        verify(employeeRepository).existsByEmail(employee.getEmail());
        verify(employeeRepository, never()).update(any(Employee.class), anyInt());
    }

    @Test
    void itShouldThrow_WhenUpdateExistingPhoneNumber() {
        when(employeeRepository.findById(any())).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);
        when(employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.updateEmployee(employeeDto, anyInt()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(DUPLICATE.formatted("Phone number", employee.getPhoneNumber()));

        verify(employeeRepository).existsByEmail(employee.getEmail());
        verify(employeeRepository).existsByPhoneNumber(employee.getPhoneNumber());
        verify(employeeRepository, never()).update(any(Employee.class), anyInt());
    }

    @Test
    void itShouldThrow_WhenNoUpdates() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.updateEmployee(employeeDto, employee.getId()))
                .isInstanceOf(NoUpdateException.class)
                .hasMessageContaining(NO_UPDATES.formatted(employee.getId()));
    }

    @Test
    void itShouldDeleteEmployeeById() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatNoException().isThrownBy(() -> employeeService.deleteEmployeeById(employee.getId()));
    }

    @Test
    void itShouldThrow_WhenDeletingEmployeeByUnknownId() {
        when(employeeRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployeeById(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(NOT_FOUND);
    }
}