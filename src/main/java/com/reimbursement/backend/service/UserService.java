package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    AuthResponse createUser(CreateUserRequest request);
    void updatePassword(String id, UpdatePasswordRequestDTO request);
    UserDTO updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
    UserDTO getUserById(String id);
    Page<UserDTO> getUsers(Role role, Department department, Pageable pageable);
    List<UserDTO> searchUsers(String name);
    List<UserDTO> getEmployees(int page);
    List<UserDTO> getAccountants(int page);
    List<UserDTO> getHRUsers(int page);
    List<UserDTO> getManagers(int page);

}