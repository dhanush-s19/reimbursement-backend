package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing user operations in the Reimbursement Management System.
 * Provides functionality for user creation, authentication, profile management, and role-based queries.
 */
public interface UserService {

    /**
     * Creates a new user account with the provided registration details.
     *
     * @param request the user creation request containing registration information
     * @return AuthResponse containing authentication tokens and user details
     * @throws IllegalArgumentException if the request data is invalid
     * @throws RuntimeException if user creation fails due to database or validation errors
     */
    AuthResponse createUser(CreateUserRequest request);

    /**
     * Updates the password for an existing user.
     *
     * @param id the unique identifier of the user whose password should be updated
     * @param request the password update request containing current and new password
     * @throws IllegalArgumentException if the current password is incorrect or new password is invalid
     * @throws RuntimeException if password update fails due to database errors
     */
    void updatePassword(String id, UpdatePasswordRequestDTO request);

    /**
     * Updates the profile information of an existing user.
     *
     * @param id the unique identifier of the user to be updated
     * @param request the user update request containing new profile details
     * @return UserDTO with the updated user information
     * @throws IllegalArgumentException if the user ID is invalid or request data is malformed
     * @throws RuntimeException if user update fails due to database or validation errors
     */
    UserDTO updateUser(String id, UpdateUserRequest request);

    /**
     * Deletes a user account from the system.
     *
     * @param id the unique identifier of the user to be deleted
     * @throws IllegalArgumentException if the user ID is invalid or user doesn't exist
     * @throws RuntimeException if user deletion fails due to database constraints or errors
     */
    void deleteUser(String id);

    /**
     * Retrieves user information by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return UserDTO containing the user's profile information
     * @throws IllegalArgumentException if the user ID is invalid
     * @throws RuntimeException if user retrieval fails due to database errors
     */
    UserDTO getUserById(String id);

    /**
     * Retrieves a paginated list of users filtered by role and department.
     *
     * @param role the role to filter users by (null for all roles)
     * @param department the department to filter users by (null for all departments)
     * @param pageable pagination and sorting parameters
     * @return Page<UserDTO> containing the filtered users with pagination metadata
     * @throws IllegalArgumentException if pageable parameters are invalid
     * @throws RuntimeException if user retrieval fails due to database errors
     */
    Page<UserDTO> getUsers(Role role, Department department, Pageable pageable);

    /**
     * Searches for users by name or partial name match.
     *
     * @param name the name or partial name to search for
     * @return List<UserDTO> containing users matching the search criteria
     * @throws IllegalArgumentException if the search name is null or empty
     * @throws RuntimeException if user search fails due to database errors
     */
    List<UserDTO> searchUsers(String name);

    /**
     * Retrieves a paginated list of employees (users with EMPLOYEE role).
     *
     * @param page the page number to retrieve (0-based)
     * @return List<UserDTO> containing employees for the specified page
     * @throws IllegalArgumentException if page number is negative
     * @throws RuntimeException if employee retrieval fails due to database errors
     */
    List<UserDTO> getEmployees(int page);

    /**
     * Retrieves a paginated list of accountants (users with ACCOUNTANT role).
     *
     * @param page the page number to retrieve (0-based)
     * @return List<UserDTO> containing accountants for the specified page
     * @throws IllegalArgumentException if page number is negative
     * @throws RuntimeException if accountant retrieval fails due to database errors
     */
    List<UserDTO> getAccountants(int page);

    /**
     * Retrieves a paginated list of HR users (users with HR role).
     *
     * @param page the page number to retrieve (0-based)
     * @return List<UserDTO> containing HR users for the specified page
     * @throws IllegalArgumentException if page number is negative
     * @throws RuntimeException if HR user retrieval fails due to database errors
     */
    List<UserDTO> getHRUsers(int page);

    /**
     * Retrieves a paginated list of managers (users with MANAGER role).
     *
     * @param page the page number to retrieve (0-based)
     * @return List<UserDTO> containing managers for the specified page
     * @throws IllegalArgumentException if page number is negative
     * @throws RuntimeException if manager retrieval fails due to database errors
     */
    List<UserDTO> getManagers(int page);

}