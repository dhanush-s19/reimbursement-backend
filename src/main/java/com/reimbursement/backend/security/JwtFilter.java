package com.reimbursement.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that processes incoming HTTP requests to validate JWT tokens.
 * 
 * <p>This filter extends {@link OncePerRequestFilter} to ensure that JWT token validation
 * is performed exactly once per request. It extracts JWT tokens from the Authorization header,
 * validates them, and sets up the Spring Security context if the token is valid.</p>
 * 
 * <p>The filter follows these steps:</p>
 * <ul>
 *   <li>Extracts the JWT token from the "Authorization" header (Bearer scheme)</li>
 *   <li>Validates the token and extracts the user email</li>
 *   <li>Loads user details from the UserDetailsService</li>
 *   <li>Sets up the Spring Security authentication context</li>
 * </ul>
 * 
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs a new JwtFilter with the required dependencies.
     *
     * @param jwtUtil the JWT utility for token validation and extraction
     * @param userDetailsService the service for loading user details during authentication
     */
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming HTTP requests to validate JWT tokens and set up authentication.
     *
     * <p>This method is called once per request and performs the following:</p>
     * <ul>
     *   <li>Extracts JWT token from Authorization header (Bearer scheme)</li>
     *   <li>Validates the token and extracts user email</li>
     *   <li>Loads user details if no existing authentication is found</li>
     *   <li>Sets up Spring Security context with valid authentication</li>
     * </ul>
     *
     * @param request the HTTP request being processed
     * @param response the HTTP response being generated
     * @param filterChain the filter chain for continuing request processing
     * @throws ServletException if an error occurs during filter processing
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            email = jwtUtil.extractEmail(token);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (email.equals(userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}