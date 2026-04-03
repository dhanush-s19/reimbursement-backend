package com.reimbursement.backend.controller;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import com.reimbursement.backend.service.ReimbursementService;
import com.reimbursement.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class HrController {

    private final UserService userService;
    private final ReimbursementService reimbursementService;

    /**
     * Creates a new user in the system.
     * @param request the user creation request containing user details
     * @return ResponseEntity containing the authentication response with user credentials
     */
    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        AuthResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing user's information.
     * @param id the ID of the user to update
     * @param request the update request containing new user details
     * @return ResponseEntity containing the updated user information
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@Valid
                                              @PathVariable String id,
                                              @RequestBody UpdateUserRequest request
    ) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user from the system by their ID.
     * @param id the ID of the user to delete
     * @return ResponseEntity with no content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a user by their unique ID.
     * @param id the ID of the user to retrieve
     * @return ResponseEntity containing the user information
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves a paginated list of users with optional filtering by role and department.
     * @param role optional filter by user role
     * @param department optional filter by department
     * @param page the page number to retrieve (default: 0)
     * @param size the number of users per page (default: 9)
     * @return ResponseEntity containing a page of users
     */
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(name = "role", required = false) Role role,
            @RequestParam(name = "department", required = false) Department department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.getUsers(role, department, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Searches for users by their name.
     * @param name the name or partial name to search for
     * @return ResponseEntity containing a list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String name) {
        List<UserDTO> users = userService.searchUsers(name);
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a paginated list of employees.
     * @param page the page number to retrieve (default: 0)
     * @return ResponseEntity containing a list of employees
     */
    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO>> getEmployees(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getEmployees(page));
    }

    /**
     * Retrieves a paginated list of accountants.
     * @param page the page number to retrieve (default: 0)
     * @return ResponseEntity containing a list of accountants
     */
    @GetMapping("/accountants")
    public ResponseEntity<List<UserDTO>> getAccountants(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getAccountants(page));
    }

    /**
     * Retrieves a paginated list of HR users.
     * @param page the page number to retrieve (default: 0)
     * @return ResponseEntity containing a list of HR users
     */
    @GetMapping("/hr")
    public ResponseEntity<List<UserDTO>> getHRUsers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getHRUsers(page));
    }

    /**
     * Retrieves a paginated list of managers.
     * @param page the page number to retrieve (default: 0)
     * @return ResponseEntity containing a list of managers
     */
    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getManagers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getManagers(page));
    }

    /**
     * Retrieves HR dashboard statistics including various metrics.
     * @return ResponseEntity containing HR dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<HrDashboardDTO> getHrDashboard() {
        HrDashboardDTO stats = reimbursementService.getHrDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Updates a user's password.
     * @param id the ID of the user whose password to update
     * @param request the password update request containing new password
     * @return ResponseEntity with success status
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String id,
            @RequestBody UpdatePasswordRequestDTO request
    ) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }
}