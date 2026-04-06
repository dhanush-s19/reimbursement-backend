package com.reimbursement.backend.controller;

import com.reimbursement.backend.dto.AuthRequest;
import com.reimbursement.backend.dto.AuthResponse;
import com.reimbursement.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and token management")
public class AuthController {

    private final AuthService authService;

    /**
     *
     * @param request
     * @return login response
     */
    @Operation(summary = "User Login", description = "Authenticates user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}