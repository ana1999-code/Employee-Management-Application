package com.example.emloyee.management.service;

import com.example.emloyee.management.exception.NoUpdateException;
import com.example.emloyee.management.exception.ResourceNotFoundException;
import com.example.emloyee.management.model.dto.DepartmentDto;
import com.example.emloyee.management.model.entity.Department;
import com.example.emloyee.management.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    public static final String NOT_FOUND = "Department with id = [%d] not found";
    public static final String NO_UPDATES = "No updates for department with id = [%d]";

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    private static void checkForDepartmentUpdates(DepartmentDto departmentDto, Integer id, Department department) {
        boolean isDifferent = !department.getName().equals(departmentDto.getName()) ||
                !department.getLocation().equals(departmentDto.getLocation());

        if (!isDifferent) {
            throw new NoUpdateException(NO_UPDATES.formatted(id));
        }
    }

    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(department -> modelMapper.map(department, DepartmentDto.class))
                .toList();
    }

    public DepartmentDto getDepartmentById(Integer id) {
        Department department = departmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND.formatted(id)));
        return modelMapper.map(department, DepartmentDto.class);
    }

    public DepartmentDto addDepartment(DepartmentDto departmentDto) {
        Department department = modelMapper.map(departmentDto, Department.class);
        departmentRepository.save(department);
        return departmentDto;
    }

    public void deleteDepartmentById(Integer id) {
        departmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND.formatted(id)));

        departmentRepository.deleteById(id);
    }

    public DepartmentDto updateDepartment(DepartmentDto departmentDto, Integer id) {
        Department department = departmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND.formatted(id)));

        checkForDepartmentUpdates(departmentDto, id, department);

        Department departmentToUpdate = modelMapper.map(departmentDto, Department.class);
        departmentRepository.update(departmentToUpdate, id);
        return modelMapper.map(getDepartmentById(id), DepartmentDto.class);
    }
}
