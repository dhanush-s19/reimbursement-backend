package com.reimbursement.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the Reimbursement Management System.
 * 
 * <p>This class configures Spring Security for the application, including:
 * <ul>
 *   <li>JWT-based authentication</li>
 *   <li>HTTP security rules and endpoint permissions</li>
 *   <li>Password encoding strategy</li>
 *   <li>CORS and CSRF configuration</li>
 * </ul>
 * 
 * <p>The configuration follows a stateless session management approach suitable for
 * REST APIs that use JWT tokens for authentication.
 * 
 * @author Reimbursement Management Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * <p>This method sets up the main security configuration including:
     * <ul>
     *   <li>Enables CORS with default configuration</li>
     *   <li>Disables CSRF protection (suitable for stateless JWT APIs)</li>
     *   <li>Configures endpoint authorization rules</li>
     *   <li>Sets stateless session management</li>
     *   <li>Adds JWT filter before the standard authentication filter</li>
     * </ul>
     *
     * <p>Authorization rules:
     * <ul>
     *   <li>Endpoints under "/auth/**" are publicly accessible (login, register)</li>
     *   <li>All other endpoints require authentication</li>
     * </ul>
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates and configures the AuthenticationManager bean.
     *
     * <p>This method exposes the AuthenticationManager as a Spring bean,
     * allowing it to be injected into other components like authentication
     * controllers or services.
     *
     * @param config the AuthenticationConfiguration containing the authentication manager
     * @return the configured AuthenticationManager instance
     * @throws Exception if an error occurs while retrieving the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates and configures the PasswordEncoder bean.
     *
     * <p>This method provides a BCryptPasswordEncoder for hashing passwords.
     * BCrypt is a strong, adaptive hash function that incorporates a salt
     * to protect against rainbow table attacks.
     *
     * <p>The default strength (10 rounds) provides a good balance between
     * security and performance for most applications.
     *
     * @return a BCryptPasswordEncoder instance for encoding passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}