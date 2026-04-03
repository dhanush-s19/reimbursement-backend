package com.reimbursement.backend.model;

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
public class User {

    @Id
    private String id;
    private String employeeId;
    private String name;
    private String email;
    private String password;
    private Department department;
    private Role role;

}
