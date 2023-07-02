package com.example.emloyee.management.service;

import com.example.emloyee.management.exception.DuplicateResourceException;
import com.example.emloyee.management.exception.NoUpdateException;
import com.example.emloyee.management.exception.ResourceNotFoundException;
import com.example.emloyee.management.model.dto.EmployeeDto;
import com.example.emloyee.management.model.entity.Employee;
import com.example.emloyee.management.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    public static final String NOT_FOUND = "Employee with id = [%d] not found";
    public static final String DUPLICATE_EMAIL = "Email [%s] is already taken";
    public static final String DUPLICATE_PHONE = "Phone number [%s] is already taken";
    public static final String NO_UPDATES = "No updates for department with id = [%d]";

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    private static void checkForEmployeeUpdates(EmployeeDto employeeDto, Integer id, Employee employee) {
        boolean isDifferent = !employee.getFirstName().equals(employeeDto.getFirstName()) ||
                !employee.getLastName().equals(employeeDto.getLastName()) ||
                !employee.getDepartmentId().equals(employeeDto.getDepartmentId()) ||
                !employee.getSalary().equals(employeeDto.getSalary()) ||
                !employee.getEmail().equals(employeeDto.getEmail()) ||
                !employee.getPhoneNumber().equals(employeeDto.getPhoneNumber());

        if (!isDifferent) {
            throw new NoUpdateException(NO_UPDATES.formatted(id));
        }
    }

    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDto.class))
                .toList();
    }

    public EmployeeDto getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND.formatted(id)));

        return modelMapper.map(employee, EmployeeDto.class);
    }

    public EmployeeDto addEmployee(EmployeeDto employeeDto) {
        checkForDuplications(employeeDto);

        Employee employee = modelMapper.map(employeeDto, Employee.class);
        employeeRepository.save(employee);

        return employeeDto;
    }

    public EmployeeDto updateEmployee(EmployeeDto employeeDto, Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND.formatted(id)));

        checkForDuplications(employeeDto);

        checkForEmployeeUpdates(employeeDto, id, employee);

        Employee employeeUpdateRequest = modelMapper.map(employeeDto, Employee.class);
        employeeRepository.update(employeeUpdateRequest, id);
        return modelMapper.map(getEmployeeById(id), EmployeeDto.class);
    }

    public void deleteEmployeeById(Integer id) {
        employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND.formatted(id)));

        employeeRepository.deleteById(id);
    }

    private void checkForDuplications(EmployeeDto employeeDto) {
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new DuplicateResourceException(DUPLICATE_EMAIL.formatted(employeeDto.getEmail()));
        }

        if (employeeRepository.existsByPhoneNumber(employeeDto.getPhoneNumber())) {
            throw new DuplicateResourceException(DUPLICATE_PHONE.formatted(employeeDto.getPhoneNumber()));
        }
    }
}
