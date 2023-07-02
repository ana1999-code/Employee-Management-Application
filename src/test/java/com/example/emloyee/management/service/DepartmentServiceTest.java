package com.example.emloyee.management.service;

import com.example.emloyee.management.exception.NoUpdateException;
import com.example.emloyee.management.exception.ResourceNotFoundException;
import com.example.emloyee.management.model.dto.DepartmentDto;
import com.example.emloyee.management.model.entity.Department;
import com.example.emloyee.management.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    public static final int ID = 10;
    public static final String NOT_FOUND = "Department with id = [%d] not found".formatted(ID);
    public static final String NO_UPDATES = "No updates for department with id = [%d]";
    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DepartmentService departmentService;

    @Captor
    private ArgumentCaptor<Department> departmentArgumentCaptor;

    private DepartmentDto departmentDto;

    private Department department;

    private DepartmentDto updateDepartmentRequest;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1)
                .name("IT")
                .location("Chisinau")
                .build();

        departmentDto = DepartmentDto.builder()
                .name("IT")
                .location("Chisinau")
                .build();
    }

    @Test
    void itShouldGetAllDepartments() {
        List<Department> departments = List.of(department);

        when(departmentRepository.findAll()).thenReturn(departments);
        when(modelMapper.map(department, DepartmentDto.class)).thenReturn(departmentDto);

        List<DepartmentDto> departmentDtos = departmentService.getAllDepartments();

        verify(departmentRepository).findAll();
        assertThat(departmentDtos.contains(departmentDto)).isTrue();
    }

    @Test
    void itShouldGetDepartmentById() {
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(modelMapper.map(department, DepartmentDto.class)).thenReturn(departmentDto);

        DepartmentDto departmentById = departmentService.getDepartmentById(department.getId());

        verify(departmentRepository).findById(department.getId());
        assertThat(departmentDto).isEqualTo(departmentById);
    }

    @Test
    void itShouldThrow_WhenGetDepartmentByUnknownId() {

        when(departmentRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(NOT_FOUND);

        verify(departmentRepository).findById(ID);
    }

    @Test
    void itShouldSaveDepartment() {
        when(modelMapper.map(departmentDto, Department.class)).thenReturn(department);

        DepartmentDto addedDepartment = departmentService.addDepartment(departmentDto);

        verify(departmentRepository).save(departmentArgumentCaptor.capture());

        assertThat(department).isEqualTo(departmentArgumentCaptor.getValue());
        assertThat(departmentDto).isEqualTo(addedDepartment);
    }

    @Test
    void itShouldDeleteDepartmentById() {
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));

        assertThatNoException().isThrownBy(() -> departmentService.deleteDepartmentById(department.getId()));
    }

    @Test
    void itShouldThrow_WhenDeletingDepartmentByUnknownId() {
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.deleteDepartmentById(ID))
                .hasMessageContaining(NOT_FOUND);

        verify(departmentRepository).findById(ID);
    }

    @ParameterizedTest
    @MethodSource("provideDataForUpdates")
    void itShouldUpdateDepartment(String departmentName, String departmentLocation) {
        final DepartmentDto departmentDtoRequest = DepartmentDto.builder()
                .name(departmentName)
                .location(departmentLocation)
                .build();

        final Department departmentRequest = Department.builder()
                .name(departmentDtoRequest.getName())
                .location(departmentDtoRequest.getLocation())
                .build();

        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(modelMapper.map(departmentDtoRequest, Department.class)).thenReturn(departmentRequest);

        departmentService.updateDepartment(departmentDtoRequest, department.getId());

        verify(departmentRepository).update(departmentArgumentCaptor.capture(), eq(department.getId()));
        assertThat(departmentName).isEqualTo(departmentArgumentCaptor.getValue().getName());
        assertThat(departmentLocation).isEqualTo(departmentArgumentCaptor.getValue().getLocation());
    }

    @Test
    void itShouldThrow_WhenUpdateDepartmentByUnknownId() {
        when(departmentRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.updateDepartment(any(DepartmentDto.class), ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(NOT_FOUND);
    }

    @Test
    void itShouldThrow_WhenNoUpdates() {
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> departmentService.updateDepartment(departmentDto, department.getId()))
                .isInstanceOf(NoUpdateException.class)
                .hasMessageContaining(NO_UPDATES.formatted(department.getId()));
    }

    private static Stream<Arguments> provideDataForUpdates() {
        final String departmentName = "Sales";
        final String departmentLocation = "Berlin";

        return Stream.of(
                Arguments.of(departmentName, null),
                Arguments.of(null, departmentLocation),
                Arguments.of(departmentName, departmentLocation)
        );
    }
}