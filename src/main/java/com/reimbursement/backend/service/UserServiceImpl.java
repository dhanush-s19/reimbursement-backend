package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Department;
import com.reimbursement.backend.model.Role;
import com.reimbursement.backend.model.User;
import com.reimbursement.backend.repository.UserRepository;
import com.reimbursement.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        User user = new User();
        user.setEmployeeId(request.getEmployeeId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setDepartment(request.getDepartment());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmployeeId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    @Override
    @Transactional
    public UserDTO updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().toLowerCase().trim();
            if (!user.getEmail().equalsIgnoreCase(newEmail)) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new RuntimeException("Email already in use");
                }
                user.setEmail(newEmail);
            }
        }

        if (request.getEmployeeId() != null) {
            String newEmployeeId = request.getEmployeeId().trim();
            if (!user.getEmployeeId().equals(newEmployeeId)) {
                if (userRepository.existsByEmployeeId(newEmployeeId)) {
                    throw new RuntimeException("Employee ID already in use");
                }
                user.setEmployeeId(newEmployeeId);
            }
        }


        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void updatePassword(String id, UpdatePasswordRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserById(String id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }


    @Override
    public Page<UserDTO> getUsers(Role role, Department department, Pageable pageable) {

        Page<User> usersPage;

        if (role != null && department != null) {
            usersPage = userRepository.findByRoleAndDepartment(role, department, pageable);

        } else if (role != null) {
            usersPage = userRepository.findByRole(role, pageable);

        } else if (department != null) {
            usersPage = userRepository.findByDepartment(department, pageable);

        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.map(this::mapToDTO);
    }

    @Override
    public List<UserDTO> searchUsers(String name) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getEmployees(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return userRepository.findByRoleOrderByIdAsc(Role.EMPLOYEE, pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getAccountants(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return userRepository.findByRoleOrderByNameAsc(Role.ACCOUNTANT, pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getHRUsers(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return userRepository.findByRoleOrderByNameAsc(Role.HR, pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getManagers(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return userRepository.findByRoleOrderByNameAsc(Role.MANAGER, pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private UserDTO mapToDTO(User user) {

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());

        return dto;
    }
}