package com.example.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeOperationDTO {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private Long phone;
    private String operation; // "create", "update", "delete"

}