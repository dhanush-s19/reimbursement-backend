package com.reimbursement.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a user entity in the reimbursement management system.
 * This class stores user information including employee details, credentials,
 * department assignment, and role-based access control information.
 */
@Data
@Document(collection = "users")
@Schema(description = "Represents a user entity in the reimbursement management system.")
public class User {

    @Id
    private String id;
    @Schema(description = "The organization-specific employee ID of the user.")
    private String employeeId;
    @Schema(description = "The full name of the user.")
    private String name;
    @Schema(description = "The email address of the user.")
    private String email;
    @Schema(description = "The password of the user.")
    private String password;
    @Schema(description = "The department of the user.")
    private Department department;
    @Schema(description = "The role of the user.")
    private Role role;

}
