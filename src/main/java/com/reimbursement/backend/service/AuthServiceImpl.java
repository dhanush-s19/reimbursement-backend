package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.AuthRequest;
import com.reimbursement.backend.dto.AuthResponse;
import com.reimbursement.backend.model.User;
import com.reimbursement.backend.repository.UserRepository;
import com.reimbursement.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
/**
 * Implementation of the AuthService interface that provides authentication functionality.
 * This service handles user login by validating credentials and generating JWT tokens.
 *
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user based on their email and password credentials.
     *
     * * @param request The authentication request containing email and password
     * @return AuthResponse containing user details and JWT token upon successful authentication
     * @throws RuntimeException if email is not found or password doesn't match
     */
    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(
                user.getId(),
                user.getEmployeeId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}