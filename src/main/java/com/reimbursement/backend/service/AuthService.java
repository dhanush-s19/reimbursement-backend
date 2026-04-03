package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.AuthRequest;
import com.reimbursement.backend.dto.AuthResponse;

/**
 * Service interface for handling authentication operations.
 * 
 * <p>This interface provides the contract for authentication-related business logic,
 * including user login and token generation. Implementations of this interface
 * should handle user credential validation and JWT token creation.</p>
 * 
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
public interface AuthService {
    /**
     * Authenticates a user based on the provided credentials and generates an authentication response.
     * 
     * <p>This method validates the user's credentials (typically username/email and password)
     * against the user database. If authentication is successful, it returns an AuthResponse
     * containing the JWT token and user information. If authentication fails, it should
     * throw an appropriate exception.</p>
     * 
     * @param request the authentication request containing user credentials
     *               (username/email and password)
     * @return AuthResponse containing the JWT token and authenticated user details
     * @throws IllegalArgumentException if the request is null or contains invalid credentials
     * @throws org.springframework.security.authentication.BadCredentialsException 
     *         if the authentication fails due to invalid credentials
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
     *         if the user is not found in the system
     * 
     * @see AuthRequest
     * @see AuthResponse
     */
    AuthResponse login(AuthRequest request);
}