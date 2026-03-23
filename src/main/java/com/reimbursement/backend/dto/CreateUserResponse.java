package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserResponse {

    private String employeeId;
    private String name;
    private String email;
    private Role role;
    private String token;

}