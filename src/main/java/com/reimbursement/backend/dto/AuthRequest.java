package com.reimbursement.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for user authentication")
public class AuthRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Registered email address of the user")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 6 characters long")
    @Schema(description = "User's account password",format = "password")
    private String password;
}