package com.reimbursement.backend.security;

import com.reimbursement.backend.model.User;
import com.reimbursement.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * This service is responsible for loading user-specific data during authentication.
 * It retrieves user information from the database using the UserRepository and
 * converts it into a Spring Security UserDetails object.
 * 
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /** Repository for accessing user data from the database */
    private final UserRepository userRepository;


    /**
     * Loads the user details from the database based on the provided email address.
     * This method is called by Spring Security during authentication to retrieve
     * user information including credentials and authorities.
     * 
     * @param email The email address of the user to be loaded
     * @return UserDetails object containing the user's email, password, and authorities
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}