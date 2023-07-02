package com.example.emloyee.management.controller;

import com.example.emloyee.management.model.dto.DepartmentDto;
import com.example.emloyee.management.service.DepartmentService;
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
@RequestMapping("${app.endpoint.departments}")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentDto> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("{id}")
    public DepartmentDto getDepartmentById(@PathVariable("id") Integer id) {
        return departmentService.getDepartmentById(id);
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> addDepartment(@RequestBody @Valid DepartmentDto departmentDto) {
        DepartmentDto addedDepartment = departmentService.addDepartment(departmentDto);
        return ResponseEntity.ok(addedDepartment);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<DepartmentDto> deleteDepartmentById(@PathVariable("id") Integer id) {
        departmentService.deleteDepartmentById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@RequestBody DepartmentDto departmentDto, @PathVariable("id") Integer id) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(departmentDto, id);
        return ResponseEntity.ok(updatedDepartment);
    }
}
