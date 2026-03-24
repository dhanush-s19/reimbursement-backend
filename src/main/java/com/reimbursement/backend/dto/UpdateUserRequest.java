package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 1, message = "Employee ID cannot be empty")
    private String employeeId;
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    private Department department;
    @Email(message = "Invalid email format")
    private String email;
    private Role role;
}