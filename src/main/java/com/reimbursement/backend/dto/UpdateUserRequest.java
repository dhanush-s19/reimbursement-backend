package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for updating user details")
public class UpdateUserRequest {

    @Size(min = 1, message = "Employee ID cannot be empty")
    @Schema(description = "The organization-specific employee ID")
    private String employeeId;
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the user")
    private String name;
    @Schema(description = "Department of the user")
    private Department department;
    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the user")
    private String email;
    @Schema(description = "Role of the user")
    private Role role;
}