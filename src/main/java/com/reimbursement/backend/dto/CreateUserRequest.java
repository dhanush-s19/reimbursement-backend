package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new user")
public class CreateUserRequest {

    private String id;
    @NotBlank(message = "Employee ID is required")
    @Schema(description = "The organization-specific employee ID")
    private String employeeId;
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the user")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the user")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "Password of the user")
    private String password;

    @NotNull(message = "Department is required")
    @Schema(description = "Department of the user")
    private Department department;

    @NotNull(message = "Role is required")
    @Schema(description = "Role of the user")
    private Role role;
}