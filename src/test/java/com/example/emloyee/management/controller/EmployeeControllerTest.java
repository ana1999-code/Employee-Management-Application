package com.example.emloyee.management.controller;

import com.example.emloyee.management.exception.ApiExceptionHandler;
import com.example.emloyee.management.exception.ResourceNotFoundException;
import com.example.emloyee.management.model.dto.EmployeeDto;
import com.example.emloyee.management.service.EmployeeService;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {EmployeeController.class, EmployeeService.class, ApiExceptionHandler.class})
class EmployeeControllerTest {

    public static final String URI = "/employees";

    private static final Integer ID = 1;

    public static final String NOT_FOUND = "Employee with id = [%d] not found".formatted(ID);

    public static final String INVALID_ID = "invalid";

    public static final String TYPE_MISMATCH_EXCEPTION_MESSAGE = "Failed to convert value of type '%s' to required type 'java.lang.Integer'; For input string: \"%s\"".formatted(INVALID_ID.getClass().getName(), INVALID_ID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
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
    void itShouldGetAllEmployees() throws Exception {
        final List<EmployeeDto> employeeDtos = List.of(employeeDto);
        when(employeeService.getAllEmployees()).thenReturn(employeeDtos);

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].first_name").value(employeeDto.getFirstName()))
                .andExpect(jsonPath("$[0].last_name").value(employeeDto.getLastName()))
                .andExpect(jsonPath("$[0].email").value(employeeDto.getEmail()))
                .andExpect(jsonPath("$[0].phone_number").value(employeeDto.getPhoneNumber()))
                .andExpect(jsonPath("$[0].salary").value(employeeDto.getSalary()))
                .andExpect(jsonPath("$[0].department_id").value(employeeDto.getDepartmentId()));
    }

    @Test
    void itShouldGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(ID)).thenReturn(employeeDto);

        mockMvc.perform(get(URI + "/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("first_name").value(employeeDto.getFirstName()))
                .andExpect(jsonPath("last_name").value(employeeDto.getLastName()))
                .andExpect(jsonPath("email").value(employeeDto.getEmail()))
                .andExpect(jsonPath("phone_number").value(employeeDto.getPhoneNumber()))
                .andExpect(jsonPath("salary").value(employeeDto.getSalary()))
                .andExpect(jsonPath("department_id").value(employeeDto.getDepartmentId()));
    }

    @Test
    void itShouldThrow_WhenGetEmployeeByUnknownId() throws Exception {
        when(employeeService.getEmployeeById(ID)).thenThrow(new ResourceNotFoundException(NOT_FOUND));

        mockMvc.perform(get(URI + "/" + ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(NOT_FOUND));
    }

    @Test
    void itShouldThrow_WhenGetEmployeeByInvalidId() throws Exception {
        mockMvc.perform(get(URI + "/" + INVALID_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value(TYPE_MISMATCH_EXCEPTION_MESSAGE));
    }

    @Test
    void itShouldAddEmployee() throws Exception {
        when(employeeService.addEmployee(employeeDto)).thenReturn(employeeDto);

        mockMvc.perform(post(URI)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("first_name").value(employeeDto.getFirstName()))
                .andExpect(jsonPath("last_name").value(employeeDto.getLastName()))
                .andExpect(jsonPath("email").value(employeeDto.getEmail()))
                .andExpect(jsonPath("phone_number").value(employeeDto.getPhoneNumber()))
                .andExpect(jsonPath("salary").value(employeeDto.getSalary()))
                .andExpect(jsonPath("department_id").value(employeeDto.getDepartmentId()));
    }

    @Test
    void itShouldThrow_WhenAddEmployeeWithInvalidEmail() throws Exception {
        String invalidEmail = "invalid_email";
        employeeDto.setEmail(invalidEmail);

        mockMvc.perform(post(URI)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Field: email has invalid value: " + invalidEmail));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPhoneNumbers")
    void itShouldThrow_WhenAddEmployeeWithInvalidPhoneNumber(String phoneNumber) throws Exception {
        employeeDto.setPhoneNumber(phoneNumber);

        mockMvc.perform(post(URI)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Field: phoneNumber has invalid value: " + phoneNumber));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSalary")
    void itShouldThrow_WhenAddEmployeeWithInvalidSalary(Double salary) throws Exception {
        employeeDto.setSalary(salary);

        mockMvc.perform(post(URI)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Field: salary has invalid value: " + salary));
    }


    @ParameterizedTest
    @MethodSource("provideDataWithNullFields")
    void itShouldThrow_WhenAddEmployeeWithNullFields(String firstName,
                                                     String lastName,
                                                     String email,
                                                     String phoneNumber,
                                                     Integer departmentId,
                                                     Double salary,
                                                     String field) throws Exception {
        employeeDto.setFirstName(firstName);
        employeeDto.setLastName(lastName);
        employeeDto.setEmail(email);
        employeeDto.setSalary(salary);
        employeeDto.setDepartmentId(departmentId);
        employeeDto.setPhoneNumber(phoneNumber);

        mockMvc.perform(post(URI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", containsString("Field: %s has invalid value: null".formatted(field))));
    }

    @Test
    void itShouldUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(employeeDto, ID)).thenReturn(employeeDto);

        mockMvc.perform(put(URI + "/" + ID)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("first_name").value(employeeDto.getFirstName()))
                .andExpect(jsonPath("last_name").value(employeeDto.getLastName()))
                .andExpect(jsonPath("email").value(employeeDto.getEmail()))
                .andExpect(jsonPath("phone_number").value(employeeDto.getPhoneNumber()))
                .andExpect(jsonPath("salary").value(employeeDto.getSalary()))
                .andExpect(jsonPath("department_id").value(employeeDto.getDepartmentId()));
    }

    @Test
    void itShouldThrow_WhenUpdateEmployeeByUnknownId() throws Exception {
        when(employeeService.updateEmployee(any(EmployeeDto.class), eq(ID))).thenThrow(new ResourceNotFoundException(NOT_FOUND));

        mockMvc.perform(put(URI + "/" + ID)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(NOT_FOUND));
    }

    @Test
    void itShouldThrow_WhenUpdateEmployeeByInvalidId() throws Exception {
        mockMvc.perform(put(URI + "/" + INVALID_ID)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value(TYPE_MISMATCH_EXCEPTION_MESSAGE));
    }

    @Test
    void itShouldDeleteEmployeeById() throws Exception {
        doNothing().when(employeeService).deleteEmployeeById(ID);

        mockMvc.perform(delete(URI + "/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void itShouldThrow_WhenDeleteEmployeeByUnknownId() throws Exception {
        doThrow(new ResourceNotFoundException(NOT_FOUND)).when(employeeService).deleteEmployeeById(ID);

        mockMvc.perform(delete(URI + "/" + ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(NOT_FOUND));
    }

    @Test
    void itShouldThrow_WhenDeleteEmployeeByInvalidId() throws Exception {
        mockMvc.perform(delete(URI + "/" + INVALID_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value(TYPE_MISMATCH_EXCEPTION_MESSAGE));
    }

    private static Stream<Arguments> provideInvalidPhoneNumbers() {
        return Stream.of(
                Arguments.of("1234567i"),
                Arguments.of("1123456709"),
                Arguments.of("1234567")
        );
    }

    private static Stream<Arguments> provideInvalidSalary() {
        return Stream.of(
                Arguments.of(0.0),
                Arguments.of(-0.1)
        );
    }

    private static Stream<Arguments> provideDataWithNullFields() {
        final String firstName = "Maria";
        final String lastName = "Jonson";
        final String email = "maria@email.com";
        final String phoneNumber = "029384493";
        final Integer departmentId = 2;
        final Double salary = 2500.0;

        return Stream.of(
                Arguments.of(firstName, lastName, email, null, departmentId, salary, "phoneNumber"),
                Arguments.of(firstName, lastName, null, phoneNumber, departmentId, salary, "email"),
                Arguments.of(firstName, null, email, phoneNumber, departmentId, salary, "lastName"),
                Arguments.of(null, lastName, email, phoneNumber, departmentId, salary, "firstName")
        );
    }
}