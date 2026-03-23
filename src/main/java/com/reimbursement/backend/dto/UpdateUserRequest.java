package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String employeeId;
    private String name;
    private Department department;
    private String email;
    private Role role;
}
