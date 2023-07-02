package com.example.emloyee.management.controller;

import com.example.emloyee.management.exception.ApiExceptionHandler;
import com.example.emloyee.management.exception.ResourceNotFoundException;
import com.example.emloyee.management.model.dto.DepartmentDto;
import com.example.emloyee.management.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {DepartmentService.class, DepartmentController.class, ApiExceptionHandler.class})
public class DepartmentControllerTest {

    public static final String URI = "/departments";

    public static final int ID = 1;

    public static final String NOT_FOUND = "Department with id = [%d] not found".formatted(ID);

    public static final String INVALID_ID = "invalid";

    public static final String TYPE_MISMATCH = "Failed to convert value of type '%s' to required type 'java.lang.Integer'; For input string: \"%s\"".formatted(INVALID_ID.getClass().getName(), INVALID_ID);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    private DepartmentDto departmentDto;

    @BeforeEach
    void setUp() {
        departmentDto = DepartmentDto.builder()
                .name("IT")
                .location("Chisinau")
                .build();
    }

    @Test
    void itShouldGetAllDepartments() throws Exception {
        List<DepartmentDto> departments = List.of(departmentDto);

        when(departmentService.getAllDepartments()).thenReturn(departments);

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(departmentDto.getName()))
                .andExpect(jsonPath("$[0].location").value(departmentDto.getLocation()));
    }

    @Test
    void itShouldGetDepartmentById() throws Exception {
        when(departmentService.getDepartmentById(ID)).thenReturn(departmentDto);

        mockMvc.perform(get(URI + "/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(departmentDto.getName()))
                .andExpect(jsonPath("location").value(departmentDto.getLocation()));
    }

    @Test
    void itShouldThrow_WhenGetDepartmentByUnknownId() throws Exception {
        when(departmentService.getDepartmentById(ID)).thenThrow(new ResourceNotFoundException(NOT_FOUND));

        mockMvc.perform(get(URI + "/" + ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(NOT_FOUND));
    }

    @Test
    void itShouldThrow_WhenGetDepartmentByInvalidId() throws Exception {
        mockMvc.perform(get(URI + "/" + INVALID_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value(TYPE_MISMATCH));
    }

    @Test
    void itShouldAddDepartment() throws Exception {
        when(departmentService.addDepartment(departmentDto)).thenReturn(departmentDto);

        mockMvc.perform(post(URI)
                        .content(objectMapper.writeValueAsString(departmentDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(departmentDto.getName()))
                .andExpect(jsonPath("location").value(departmentDto.getLocation()));
    }

    @ParameterizedTest
    @MethodSource("provideNullDataForAddDepartment")
    void itShouldThrow_WhenAddDepartmentWithNullFields(String name, String location, String invalidField) throws Exception {
        final DepartmentDto departmentDtoWithInvalidFields = DepartmentDto.builder()
                .name(name)
                .location(location)
                .build();

        mockMvc.perform(post(URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDtoWithInvalidFields)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", containsString("Field: %s has invalid value: null".formatted(invalidField))));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDataForAddDepartment")
    void itShouldThrow_WhenAddDepartmentWithInvalidFields(String name, String location, String invalidField) throws Exception {
        final DepartmentDto departmentDtoWithInvalidFields = DepartmentDto.builder()
                .name(name)
                .location(location)
                .build();

        mockMvc.perform(post(URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDtoWithInvalidFields)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", containsString("Field: %s has invalid value: ".formatted(invalidField))));
    }

    @Test
    void itShouldDeleteDepartmentById() throws Exception {
        doNothing().when(departmentService).deleteDepartmentById(ID);

        mockMvc.perform(delete(URI + "/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void itShouldThrow_WhenDeleteDepartmentByUnknownId() throws Exception {
        doThrow(new ResourceNotFoundException(NOT_FOUND)).when(departmentService).deleteDepartmentById(ID);

        mockMvc.perform(delete(URI + "/" + ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(NOT_FOUND));
    }

    @Test
    void itShouldThrow_WhenDeleteDepartmentByInvalidId() throws Exception {
        mockMvc.perform(delete(URI + "/" + INVALID_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value(TYPE_MISMATCH));
    }

    @Test
    void itShouldUpdateDepartment() throws Exception {
        when(departmentService.updateDepartment(departmentDto, ID)).thenReturn(departmentDto);

        mockMvc.perform(put(URI + "/" + ID)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(departmentDto.getName()))
                .andExpect(jsonPath("location").value(departmentDto.getLocation()));
    }

    private static Stream<Arguments> provideNullDataForAddDepartment() {
        final String name = "IT";
        final String location = "Chisinau";

        return Stream.of(
                Arguments.of(null, location, "name"),
                Arguments.of(name, null, "location")
        );
    }

    private static Stream<Arguments> provideInvalidDataForAddDepartment() {
        final String name = "IT";
        final String location = "Chisinau";

        return Stream.of(
                Arguments.of("", location, "name"),
                Arguments.of(name, "", "location")
        );
    }
}