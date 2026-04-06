package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response object containing user details")
public class UserDTO {

    @Schema(description = "Unique internal database identifier")
    private String id;
    @Schema(description = "The organization-specific employee ID")
    private String employeeId;
    @Schema(description = "Full name of the user")
    private String name;
    @Schema(description = "Email address of the user")
    private String email;
    @Schema(description = "Password of the user")
    private String password;
    @Schema(description = "Department of the user")
    private Department department;
    @Schema(description = "Role of the user")
    private Role role;
}
