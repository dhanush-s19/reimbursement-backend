package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.AccountantDashboardDTO;
import com.reimbursement.backend.dto.HrDashboardDTO;
import com.reimbursement.backend.dto.ReimbursementPageResponse;
import com.reimbursement.backend.dto.ReimbursementResponse;
import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ReimbursementService {

    Reimbursement submitReimbursement(
            String title,
            Double amount,
            String description,
            boolean noInvoice,
            String invoiceNote,
            List<MultipartFile> files,
            String submittedById,
            String name,
            ReimbursementType type
    ) throws RuntimeException;
    Reimbursement submitTeamReimbursement(
            String title,
            Double amount,
            String description,
            boolean noInvoice,
            String invoiceNote,
            String submittedById,
            String name,
            List<String> teamMemberIds,
            ReimbursementType type,
            List<MultipartFile> files
    ) throws RuntimeException;
    Reimbursement updateReimbursementStatus(
            String id,
            Status status,
            String reason,
            String processedById,
            BigDecimal approvedAmount
    ) throws RuntimeException;

    Reimbursement completeCertification(String id, List<MultipartFile> files, Double finalAmount) throws RuntimeException;
    ReimbursementResponse getById(String id, String role) throws RuntimeException;
    Page<Reimbursement> getAllReimbursements(Pageable pageable);
    Page<Reimbursement> getReimbursementsByEmployeeId(String employeeId, Pageable pageable);
    Page<Reimbursement> getByStatus(Status status, Pageable pageable);
    Page<Reimbursement> getReimbursementsByType(ReimbursementType type, Pageable pageable);
    ReimbursementPageResponse getHRQueue(Pageable pageable);
    List<Status> getAllowedNextStatuses(Reimbursement reimbursement, String role);
    AccountantDashboardDTO getAccountantDashboardStats();
    HrDashboardDTO getHrDashboardStats();
}