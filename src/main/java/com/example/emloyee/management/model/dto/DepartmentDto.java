package com.example.emloyee.management.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class DepartmentDto {

    @NotNull(message = "Department name must not be null")
    @NotEmpty(message = "Department name must not be empty")
    @NotBlank(message = "Department name must not be blank")
    private String name;

    @NotNull(message = "Location must not be null")
    @NotEmpty(message = "Location name must not be empty")
    @NotBlank(message = "Location name must not be blank")
    private String location;
}
