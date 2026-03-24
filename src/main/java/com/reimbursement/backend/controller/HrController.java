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
@CrossOrigin(origins = "*")
public class HrController {

    private final UserService userService;
    private final ReimbursementService reimbursementService;

    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        AuthResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@Valid
            @PathVariable String id,
            @RequestBody UpdateUserRequest request
    ) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String name) {
        List<UserDTO> users = userService.searchUsers(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO>> getEmployees(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getEmployees(page));
    }

    @GetMapping("/accountants")
    public ResponseEntity<List<UserDTO>> getAccountants(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getAccountants(page));
    }

    @GetMapping("/hr")
    public ResponseEntity<List<UserDTO>> getHRUsers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getHRUsers(page));
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getManagers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getManagers(page));
    }

    @GetMapping("/stats")
    public ResponseEntity<HrDashboardDTO> getHrDashboard() {
        HrDashboardDTO stats = reimbursementService.getHrDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String id,
            @RequestBody UpdatePasswordRequestDTO request
    ) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }
}