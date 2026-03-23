package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.AuthRequest;
import com.reimbursement.backend.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}