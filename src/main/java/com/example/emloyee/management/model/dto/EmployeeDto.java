package com.example.emloyee.management.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {

    @NotNull(message = "First name must not be null")
    @NotEmpty(message = "First name must not be empty")
    @NotBlank(message = "First name must not be blank")
    @JsonProperty("first_name")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @NotEmpty(message = "Last name must not be empty")
    @NotBlank(message = "Last name must not be blank")
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("department_id")
    private Integer departmentId;

    @NotNull(message = "Department name must not be null")
    @NotEmpty(message = "Department name must not be empty")
    @NotBlank(message = "Department name must not be blank")
    @Email
    private String email;

    @NotNull(message = "Department name must not be null")
    @NotEmpty(message = "Department name must not be empty")
    @NotBlank(message = "Department name must not be blank")
    @JsonProperty("phone_number")
    @Pattern(regexp = "\\d{9}", message = "Invalid phone number")
    private String phoneNumber;

    @Min(value = 1, message = "Salary must be equal or greater than 1.0")
    private Double salary;
}
