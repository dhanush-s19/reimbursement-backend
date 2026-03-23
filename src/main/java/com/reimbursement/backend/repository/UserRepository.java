package com.reimbursement.backend.repository;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import com.reimbursement.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByDepartment(Department department, Pageable pageable);
    Page<User> findByRoleAndDepartment(Role role, Department department, Pageable pageable);
    List<User> findByRoleOrderByNameAsc(Role role, Pageable pageable);
    List<User> findByRole(Role role);
    List<User> findByRoleOrderByIdAsc(Role role, Pageable pageable);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);

}