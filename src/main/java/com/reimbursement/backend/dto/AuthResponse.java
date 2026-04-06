package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response object containing user details and the authentication token upon successful login.")
public class AuthResponse {

    @Schema(description = "Unique internal database identifier")
    private String id;
    @Schema(description = "The organization-specific employee ID")
    private String employeeId;
    @Schema(description = "Full name of the authenticated user")
    private String name;
    @Schema(description = "Registered email address")
    private String email;
    @Schema(description = "User access level within the system")
    private Role role;
    @Schema(description = "JWT access token used for authorizing subsequent requests")
    private String token;
}