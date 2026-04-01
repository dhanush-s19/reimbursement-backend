package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ReimbursementService {

    Reimbursement submitReimbursement(String title, Double amount, String description, boolean noInvoice,
                                      String invoiceNote, List<MultipartFile> files, String submittedById,
                                      String name, ReimbursementType type);

    public Reimbursement submitTeamReimbursement(String title, Double amount, String description,
                                                 boolean noInvoice, String invoiceNote,
                                                 String submittedById, String name,
                                                 ReimbursementType type,
                                                 List<MultipartFile> files, String userRole,
                                                 String managerId);


    public Reimbursement updateReimbursement(String id, String title, Double amount, String description,
                                             boolean noInvoice, String invoiceNote,
                                             List<MultipartFile> newFiles, List<String> existingFileUrls);


    Reimbursement updateStatus(String id, Status nextStatus, String reason, String processedById, BigDecimal approvedAmount);


    Reimbursement completeCertification(String id, List<MultipartFile> files, Double finalAmount);


    ReimbursementResponse getById(String id, String role);


    Page<Reimbursement> getAllReimbursements(Pageable pageable);

    Page<Reimbursement> getReimbursementsByEmployeeId(String employeeId, Pageable pageable);

    Page<Reimbursement> getByStatus(Status status, Pageable pageable);

    Page<Reimbursement> getByType(ReimbursementType type, Pageable pageable);


    ReimbursementPageResponse getHRQueue(Pageable pageable);


    public ReimbursementPageResponse getManagerQueue(String managerId, Pageable pageable);

    AccountantDashboardDTO getAccountantDashboardStats();

    HrDashboardDTO getHrDashboardStats();
}