package com.reimbursement.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
