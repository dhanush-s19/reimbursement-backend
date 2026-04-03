package com.reimbursement.backend.repository;

import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import com.reimbursement.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities in the MongoDB database.
 * Extends MongoRepository to provide basic CRUD operations and custom query methods.
 * 
 * This repository handles all database operations related to user management,
 * including authentication, role-based queries, and department-based filtering.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found, otherwise empty
     */
    Optional<User> findByEmail(String email);
    /**
     * Finds users by their role with pagination support.
     * 
     * @param role the role to filter users by
     * @param pageable pagination information
     * @return a Page containing users with the specified role
     */
    Page<User> findByRole(Role role, Pageable pageable);
    /**
     * Finds users by their department with pagination support.
     * 
     * @param department the department to filter users by
     * @param pageable pagination information
     * @return a Page containing users from the specified department
     */
    Page<User> findByDepartment(Department department, Pageable pageable);
    /**
     * Finds users by both role and department with pagination support.
     * 
     * @param role the role to filter users by
     * @param department the department to filter users by
     * @param pageable pagination information
     * @return a Page containing users matching both criteria
     */
    Page<User> findByRoleAndDepartment(Role role, Department department, Pageable pageable);
    /**
     * Finds users by role, ordered by name in ascending order with pagination support.
     *
     * @param role the role to filter users by
     * @param pageable pagination information
     * @return a List of users with the specified role sorted by name ascending
     */
    List<User> findByRoleOrderByNameAsc(Role role, Pageable pageable);
    /**
     * Finds all users with the specified role.
     *
     * @param role the role to filter users by
     * @return a List of all users with the specified role
     */
    List<User> findByRole(Role role);
    /**
     * Finds users by role, ordered by ID in ascending order with pagination support.
     * 
     * @param role the role to filter users by
     * @param pageable pagination information
     * @return a List of users with the specified role sorted by ID ascending
     */
    List<User> findByRoleOrderByIdAsc(Role role, Pageable pageable);
    /**
     * Checks if a user exists with the specified employee ID.
     * 
     * @param employeeId the employee ID to check
     * @return true if a user exists with the employee ID, false otherwise
     */
    boolean existsByEmployeeId(String employeeId);
    /**
     * Checks if a user exists with the specified email address.
     * 
     * @param email the email address to check
     * @return true if a user exists with the email address, false otherwise
     */
    boolean existsByEmail(String email);

}