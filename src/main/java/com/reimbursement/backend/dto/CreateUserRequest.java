package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String id;
    private String employeeId;
    private String name;
    private String email;
    private String password;
    private Department department;
    private Role role;
}
