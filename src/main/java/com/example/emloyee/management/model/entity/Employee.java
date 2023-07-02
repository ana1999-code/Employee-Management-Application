package com.example.emloyee.management.model.entity;

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
public class Employee {

    private Integer id;

    private String firstName;

    private String lastName;

    private Integer departmentId;

    private String email;

    private String phoneNumber;

    private Double salary;
}
