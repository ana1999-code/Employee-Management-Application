package com.example.emloyee.management.controller;

import com.example.emloyee.management.model.dto.EmployeeDto;
import com.example.emloyee.management.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.endpoint.employees}")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("{id}")
    public EmployeeDto getEmployeeById(@PathVariable("id") Integer id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> addEmployee(@RequestBody @Valid EmployeeDto employeeDto) {
        EmployeeDto addedEmployee = employeeService.addEmployee(employeeDto);
        return ResponseEntity.ok(addedEmployee);
    }

    @PutMapping("{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto, @PathVariable("id") Integer id) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto, id);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("{id}")
    private ResponseEntity<EmployeeDto> deleteEmployeeById(@PathVariable("id") Integer id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }
}
