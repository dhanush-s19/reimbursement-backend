package com.reimbursement.backend.service;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing reimbursement requests in the system.
 *
 * <p>This service provides comprehensive functionality for handling reimbursement lifecycle
 * including submission, approval, certification, and various administrative operations.
 * It supports both individual and team reimbursement requests with different user roles
 * and approval workflows.</p>
 *
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
public interface ReimbursementService {

    /**
     * Submits a new individual reimbursement request.
     *
     * @param title The title of the reimbursement request
     * @param amount The requested reimbursement amount
     * @param description Detailed description of the reimbursement
     * @param noInvoice Flag indicating if invoice is not available
     * @param invoiceNote Note about invoice status or absence
     * @param files List of supporting documents/files
     * @param submittedById ID of the employee submitting the request
     * @param name Name of the employee submitting the request
     * @param type Type of reimbursement (e.g., TRAVEL, MEDICAL, etc.)
     * @return The created Reimbursement entity
     */
    Reimbursement submitReimbursement(String title, Double amount, String description, boolean noInvoice,
                                      String invoiceNote, List<MultipartFile> files, String submittedById,
                                      String name, ReimbursementType type);


    /**
     * Submits a new team reimbursement request.
     *
     * @param title The title of the reimbursement request
     * @param amount The requested reimbursement amount
     * @param description Detailed description of the reimbursement
     * @param noInvoice Flag indicating if invoice is not available
     * @param invoiceNote Note about invoice status or absence
     * @param submittedById ID of the employee submitting the request
     * @param name Name of the employee submitting the request
     * @param type Type of reimbursement (e.g., TRAVEL, MEDICAL, etc.)
     * @param files List of supporting documents/files
     * @param userRole Role of the user submitting the request
     * @param managerName Name of the manager who will approve
     * @param managerId ID of the manager who will approve
     * @return The created Reimbursement entity
     */
    public Reimbursement submitTeamReimbursement(String title, Double amount, String description,
                                                 boolean noInvoice, String invoiceNote,
                                                 String submittedById, String name,
                                                 ReimbursementType type,
                                                 List<MultipartFile> files, String userRole,String managerName,
                                                 String managerId);

    /**
     * Updates an existing reimbursement request.
     *
     * @param id The ID of the reimbursement to update
     * @param title Updated title of the reimbursement
     * @param amount Updated reimbursement amount
     * @param description Updated description
     * @param noInvoice Updated flag indicating if invoice is not available
     * @param invoiceNote Updated note about invoice status
     * @param newFiles List of new files to add
     * @param existingFileUrls List of existing file URLs to keep
     * @return The updated Reimbursement entity
     */
    public Reimbursement updateReimbursement(String id, String title, Double amount, String description,
                                             boolean noInvoice, String invoiceNote,
                                             List<MultipartFile> newFiles, List<String> existingFileUrls);




    /**
     * Updates the status of a reimbursement request.
     *
     * @param id The ID of the reimbursement to update
     * @param nextStatus The new status to set
     * @param reason Reason for the status change
     * @param processedById ID of the user processing the status change
     * @param approvedAmount The approved amount (if applicable)
     * @return The updated Reimbursement entity
     */
    Reimbursement updateStatus(String id, Status nextStatus, String reason, String processedById, BigDecimal approvedAmount);


    /**
     * Completes the certification process for a reimbursement.
     *
     * @param id The ID of the reimbursement to certify
     * @param files List of certification documents
     * @param finalAmount The final certified amount
     * @return The updated Reimbursement entity
     */
    Reimbursement completeCertification(String id, List<MultipartFile> files, Double finalAmount);


    /**
     * Retrieves a reimbursement by its ID.
     *
     * @param id The ID of the reimbursement to retrieve
     * @param role The role of the user requesting the data
     * @return ReimbursementResponse containing the reimbursement details
     */
    ReimbursementResponse getById(String id, String role);


    /**
     * Retrieves all reimbursements with pagination.
     *
     * @param pageable Pagination information
     * @return Page of Reimbursement entities
     */
    Page<Reimbursement> getAllReimbursements(Pageable pageable);

    /**
     * Retrieves reimbursements submitted by a specific employee.
     *
     * @param employeeId The ID of the employee
     * @param pageable Pagination information
     * @return Page of Reimbursement entities for the employee
     */
    Page<Reimbursement> getReimbursementsByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Retrieves reimbursements filtered by status.
     *
     * @param status The status to filter by
     * @param pageable Pagination information
     * @return Page of Reimbursement entities with the specified status
     */
    Page<Reimbursement> getByStatus(Status status, Pageable pageable);

    /**
     * Retrieves reimbursements filtered by type.
     *
     * @param type The reimbursement type to filter by
     * @param pageable Pagination information
     * @return Page of Reimbursement entities with the specified type
     */
    Page<Reimbursement> getByType(ReimbursementType type, Pageable pageable);


    /**
     * Retrieves the HR queue of reimbursements pending HR review.
     *
     * @param pageable Pagination information
     * @return ReimbursementPageResponse containing HR queue data
     */
    ReimbursementPageResponse getHRQueue(Pageable pageable);


    /**
     * Retrieves the manager queue of reimbursements pending manager approval.
     *
     * @param managerId The ID of the manager
     * @param pageable Pagination information
     * @return ReimbursementPageResponse containing manager queue data
     */
    public ReimbursementPageResponse getManagerQueue(String managerId, Pageable pageable);

    /**
     * Retrieves dashboard statistics for the accountant role.
     *
     * @return AccountantDashboardDTO containing accountant-specific statistics
     */
    AccountantDashboardDTO getAccountantDashboardStats();

    /**
     * Retrieves dashboard statistics for the HR role.
     *
     * @return HrDashboardDTO containing HR-specific statistics
     */
    HrDashboardDTO getHrDashboardStats();
}