package com.reimbursement.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.reimbursement.backend.dto.AccountantDashboardDTO;
import com.reimbursement.backend.dto.HrDashboardDTO;
import com.reimbursement.backend.dto.ReimbursementPageResponse;
import com.reimbursement.backend.dto.ReimbursementResponse;
import com.reimbursement.backend.model.*;
import com.reimbursement.backend.repository.ReimbursementRepository;
import com.reimbursement.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Service implementation for managing reimbursement requests in the system.
 * 
 * <p>This class handles the core business logic for reimbursement operations including:
 * <ul>
 * <li>Submission of normal, certificate, and team event reimbursements</li>
 * <li>Status updates and approval workflows</li>
 * <li>File upload and processing via Cloudinary</li>
 * <li>Dashboard statistics for different user roles</li>
 * <li>Reimbursement editing and resubmission logic</li>
 * </ul>
 * 
 * <p>The service supports a multi-level approval workflow:
 * <ul>
 * <li>Normal reimbursements: Employee → Accountant/HR → Final Approval</li>
 * <li>Certificate reimbursements: Employee → HR → Accountant → Final Approval</li>
 * <li>Team events: Employee → Manager → HR → Accountant → Final Approval</li>
 * </ul>
 * 
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 * @see ReimbursementService
 * @see Reimbursement
 * @see Status
 */
@Service
@RequiredArgsConstructor
public class ReimbursementServiceImpl implements ReimbursementService {

    private final ReimbursementRepository repository;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;

    /**
     * Submits a new reimbursement request for normal reimbursements or certificates.
     *
     * <p>This method handles the initial submission of reimbursement requests. It processes
     * file uploads, validates the submission based on type and invoice requirements, and sets
     * the appropriate initial status based on the reimbursement type.
     *
     * <p>Status flow:
     * <ul>
     * <li>CERTIFICATE → FORWARDED_TO_HR</li>
     * <li>NORMAL → SUBMITTED</li>
     * </ul>
     *
     * @param title the title of the reimbursement request
     * @param amount the requested reimbursement amount
     * @param description detailed description of the reimbursement purpose
     * @param noInvoice flag indicating whether an invoice is available
     * @param invoiceNote explanation if no invoice is provided
     * @param files list of supporting documents (invoices, receipts, etc.)
     * @param submittedById the ID of the employee submitting the request
     * @param name the name of the employee submitting the request
     * @param type the type of reimbursement (NORMAL or CERTIFICATE)
     * @return the created and saved Reimbursement entity
     * @throws RuntimeException if validation fails (missing invoice for normal reimbursements)
     * @see ReimbursementType
     * @see Status
     */
    @Override
    public Reimbursement submitReimbursement(String title, Double amount, String description, boolean noInvoice,
                                             String invoiceNote, List<MultipartFile> files,
                                             String submittedById, String name, ReimbursementType type) {
        List<String> fileUrls = processFiles(files);
        validateInitialSubmission(type, noInvoice, invoiceNote, fileUrls);

        Status initialStatus = (type == ReimbursementType.CERTIFICATE) ? Status.FORWARDED_TO_HR : Status.SUBMITTED;

        Reimbursement reimbursement = Reimbursement.builder()
                .title(title)
                .amount(amount)
                .description(description)
                .noInvoice(noInvoice)
                .invoiceNote(invoiceNote)
                .fileUrls(fileUrls)
                .employeeId(submittedById)
                .status(initialStatus)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(name)
                .type(type)
                .requiresHrApproval(type == ReimbursementType.CERTIFICATE)
                .build();
        return repository.save(reimbursement);
    }

    /**
     * Submits a team event reimbursement request with manager approval workflow.
     *
     * <p>This method handles team event reimbursements that require manager approval.
     * The initial status is determined by the submitter's role:
     * <ul>
     * <li>Manager submission → FORWARDED_TO_HR</li>
     * <li>Employee submission → PENDING_MANAGER_APPROVAL</li>
     * </ul>
     *
     * @param title the title of the team event reimbursement
     * @param amount the requested reimbursement amount
     * @param description detailed description of the team event
     * @param noInvoice flag indicating whether an invoice is available
     * @param invoiceNote explanation if no invoice is provided
     * @param submittedById the ID of the employee submitting the request
     * @param name the name of the employee submitting the request
     * @param type the reimbursement type (should be TEAM_EVENTS)
     * @param files list of supporting documents
     * @param userRole the role of the submitter (MANAGER or other)
     * @param managerName the name of the manager who will approve
     * @param managerId the ID of the manager who will approve
     * @return the created and saved Reimbursement entity
     * @see ReimbursementType#TEAM_EVENTS
     * @see Status#PENDING_MANAGER_APPROVAL
     * @see Status#FORWARDED_TO_HR
     */
    @Override
    public Reimbursement submitTeamReimbursement(String title, Double amount, String description,
                                                 boolean noInvoice, String invoiceNote,
                                                 String submittedById, String name,
                                                 ReimbursementType type,
                                                 List<MultipartFile> files, String userRole,String managerName,
                                                 String managerId) {


        Status initialStatus = "MANAGER".equalsIgnoreCase(userRole)
                ? Status.FORWARDED_TO_HR
                : Status.PENDING_MANAGER_APPROVAL;

        Reimbursement reimbursement = Reimbursement.builder()
                .title(title)
                .amount(amount)
                .description(description)
                .employeeId(submittedById)
                .name(name)
                .managerId(managerId)
                .status(initialStatus)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(ReimbursementType.TEAM_EVENTS)
                .requiresHrApproval(true)
                .fileUrls(processFiles(files))
                .invoiceNote(invoiceNote)
                .noInvoice(noInvoice)
                .managerName(managerName)
                .build();

        return repository.save(reimbursement);
    }

    /**
     * Updates the status of a reimbursement request with appropriate validations.
     *
     * <p>This method handles status transitions throughout the approval workflow.
     * It includes special validations for certain status changes:
     * <ul>
     * <li>ACCOUNTANT_FINAL_APPROVED requires a valid approved amount</li>
     * <li>PAID status requires prior ACCOUNTANT_FINAL_APPROVED status</li>
     * </ul>
     *
     * @param id the ID of the reimbursement to update
     * @param nextStatus the new status to set
     * @param reason the reason for status change (required for rejections)
     * @param processedById the ID of the user processing the status change
     * @param approvedAmount the final approved amount (required for final approval)
     * @return the updated Reimbursement entity
     * @throws RuntimeException if reimbursement not found or validation fails
     * @see Status
     */
    @Override
    public Reimbursement updateStatus(String id, Status nextStatus, String reason, String processedById, BigDecimal approvedAmount) {
        Reimbursement r = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reimbursement not found"));

        if (nextStatus == Status.ACCOUNTANT_FINAL_APPROVED) {
            if (approvedAmount == null || approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Valid approved amount required.");
            }
            r.setApprovedAmount(approvedAmount);
        }

        if (nextStatus == Status.PAID && r.getStatus() != Status.ACCOUNTANT_FINAL_APPROVED) {
            throw new RuntimeException("Must be Final Approved before marking as Paid.");
        }

        if (reason != null) r.setReason(reason.trim());
        r.setStatus(nextStatus);
        r.setProcessedById(processedById);
        r.setUpdatedAt(LocalDateTime.now());
        return repository.save(r);
    }

    /**
     * Determines the allowed next statuses for a reimbursement based on current status and user role.
     *
     * <p>This private method enforces the approval workflow rules by returning only the
     * valid status transitions for each role:
     * <ul>
     * <li>MANAGER: Can approve or reject PENDING_MANAGER_APPROVED requests</li>
     * <li>HR: Can approve or reject FORWARDED_TO_HR requests</li>
     * <li>ACCOUNTANT: Can handle SUBMITTED, HR_APPROVED, and ACCOUNTANT_FINAL_APPROVED requests</li>
     * </ul>
     *
     * @param r the reimbursement entity to check
     * @param role the user role (MANAGER, HR, or ACCOUNTANT)
     * @return list of allowed next statuses for the given role and current status
     * @see Status
     */
    private List<Status> getAllowedNextStatuses(Reimbursement r, String role) {
        Status current = r.getStatus();
        List<Status> allowed = new ArrayList<>();
        if ("MANAGER".equalsIgnoreCase(role)) {
            if (current == Status.PENDING_MANAGER_APPROVAL) {
                allowed.addAll(List.of(Status.FORWARDED_TO_HR, Status.MANAGER_REJECTED));
            }
        } else if ("HR".equalsIgnoreCase(role)) {
            if (current == Status.FORWARDED_TO_HR) {
                allowed.addAll(List.of(Status.HR_APPROVED, Status.HR_REJECTED));
            }
        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            if (current == Status.SUBMITTED || current == Status.HR_APPROVED) {
                allowed.addAll(List.of(Status.ACCOUNTANT_FINAL_APPROVED, Status.ACCOUNTANT_REJECTED));
                if (current == Status.SUBMITTED) {
                    allowed.add(Status.FORWARDED_TO_HR);
                }
            } else if (current == Status.ACCOUNTANT_FINAL_APPROVED) {
                allowed.add(Status.PAID);
            }
        }
        return allowed;
    }

    /**
     * Retrieves a paginated list of reimbursement requests pending manager approval.
     *
     * <p>This method returns all reimbursements with status PENDING_MANAGER_APPROVAL
     * that are assigned to the specified manager. Each result includes the allowed
     * actions the manager can take.
     *
     * @param managerId the ID of the manager whose queue is being requested
     * @param pageable pagination information (page number, size, sorting)
     * @return paginated response containing reimbursement requests awaiting manager action
     * @see Status#PENDING_MANAGER_APPROVAL
     * @see ReimbursementPageResponse
     */
    @Override
    public ReimbursementPageResponse getManagerQueue(String managerId, Pageable pageable) {
        Page<Reimbursement> page = repository.findByStatusAndManagerId(Status.PENDING_MANAGER_APPROVAL, managerId, pageable);
        List<ReimbursementResponse> content = page.getContent().stream()
                .map(r -> getById(r.getId(), "MANAGER"))
                .toList();

        return ReimbursementPageResponse.builder()
                .content(content)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .number(page.getNumber())
                .build();
    }

    /**
     * Retrieves a paginated list of reimbursement requests pending HR approval.
     *
     * <p>This method returns all reimbursements with status FORWARDED_TO_HR
     * that require HR verification and approval. Each result includes the allowed
     * actions the HR user can take.
     *
     * @param pageable pagination information (page number, size, sorting)
     * @return paginated response containing reimbursement requests awaiting HR action
     * @see Status#FORWARDED_TO_HR
     * @see ReimbursementPageResponse
     */
    @Override
    public ReimbursementPageResponse getHRQueue(Pageable pageable) {
        Page<Reimbursement> page = repository.findByStatus(Status.FORWARDED_TO_HR, pageable);
        List<ReimbursementResponse> content = page.getContent().stream()
                .map(r -> getById(r.getId(), "HR"))
                .toList();
        return ReimbursementPageResponse.builder()
                .content(content)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .number(page.getNumber())
                .build();
    }

    /**
     * Processes and uploads multiple files to Cloudinary storage.
     *
     * <p>This private method handles the file upload process for reimbursement
     * supporting documents. Each file is uploaded to Cloudinary and the secure
     * URL is returned for storage in the reimbursement entity.
     *
     * @param files list of multipart files to upload
     * @return list of secure URLs from Cloudinary for the uploaded files
     * @throws RuntimeException if any file upload fails
     * @see Cloudinary
     */
    private List<String> processFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                urls.add(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException("Upload failed for: " + file.getOriginalFilename());
            }
        }
        return urls;
    }

    /**
     * Validates reimbursement submission requirements based on type and invoice availability.
     *
     * <p>This private method enforces business rules for initial submissions:
     * <ul>
     * <li>CERTIFICATE and TEAM_EVENTS: No validation required</li>
     * <li>NORMAL: Must have either invoice files or a valid invoice note</li>
     * </ul>
     *
     * @param type the reimbursement type being validated
     * @param noInvoice flag indicating whether an invoice is available
     * @param invoiceNote explanation if no invoice is provided
     * @param fileUrls list of uploaded file URLs
     * @throws RuntimeException if validation requirements are not met
     * @see ReimbursementType
     */
    private void validateInitialSubmission(ReimbursementType type, boolean noInvoice, String invoiceNote, List<String> fileUrls) {
        if (type == ReimbursementType.CERTIFICATE || type == ReimbursementType.TEAM_EVENTS) return;
        if (type == ReimbursementType.NORMAL) {
            if (!noInvoice && (fileUrls == null || fileUrls.isEmpty())) {
                throw new RuntimeException("Please upload an invoice or provide a reason for the missing receipt.");
            }
            if (noInvoice && (invoiceNote == null || invoiceNote.isBlank())) {
                throw new RuntimeException("An explanation note is required when no invoice is provided.");
            }
        }
    }

    /**
     * Retrieves a reimbursement by ID with role-specific action permissions.
     *
     * <p>This method returns the reimbursement details along with the allowed
     * next statuses based on the user's role. It also determines which UI fields
     * should be displayed (approved amount field, reason field, etc.).
     *
     * @param id the ID of the reimbursement to retrieve
     * @param role the user role (MANAGER, HR, ACCOUNTANT, or EMPLOYEE)
     * @return response containing reimbursement details and allowed actions
     * @throws RuntimeException if reimbursement not found
     * @see ReimbursementResponse
     * @see Status
     */
    @Override
    public ReimbursementResponse getById(String id, String role) {
        Reimbursement r = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        List<Status> nextStatuses = getAllowedNextStatuses(r, role);

        return ReimbursementResponse.builder()
                .reimbursement(r)
                .allowedNextStatuses(nextStatuses)
                .showApprovedAmountField(nextStatuses.contains(Status.ACCOUNTANT_FINAL_APPROVED))
                .showReasonField(nextStatuses.contains(Status.HR_REJECTED) ||
                        nextStatuses.contains(Status.ACCOUNTANT_REJECTED) ||
                        nextStatuses.contains(Status.MANAGER_REJECTED))
                .build();
    }

    /**
     * Generates dashboard statistics for the accountant role.
     *
     * <p>This method calculates key metrics for the accountant dashboard:
     * <ul>
     * <li>Total pending payout amount (final approved but not yet paid)</li>
     * <li>Count of reimbursements pending accountant action</li>
     * <li>Spending breakdown by reimbursement type</li>
     * </ul>
     *
     * @return dashboard statistics including pending amounts and spending analytics
     * @see AccountantDashboardDTO
     * @see Status#ACCOUNTANT_FINAL_APPROVED
     * @see Status#PAID
     */
    @Override
    public AccountantDashboardDTO getAccountantDashboardStats() {
        List<Reimbursement> all = repository.findAll();
        Double pendingPayout = all.stream()
                .filter(r -> r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED)
                .mapToDouble(r -> r.getApprovedAmount() != null ? r.getApprovedAmount().doubleValue() : 0.0)
                .sum();

        long pendingAction = all.stream()
                .filter(r -> r.getStatus() == Status.SUBMITTED ||
                        r.getStatus() == Status.HR_APPROVED)
                .count();

        Map<String, Double> spendByType = new HashMap<>();
        all.stream()
                .filter(r -> r.getStatus() == Status.PAID || r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED)
                .forEach(r -> {
                    String type = r.getType().name();
                    Double amt = r.getApprovedAmount() != null ? r.getApprovedAmount().doubleValue() : r.getAmount().doubleValue();
                    spendByType.put(type, spendByType.getOrDefault(type, 0.0) + amt);
                });

        Map<String, Long> statusDistribution = all.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStatus().name(),
                        Collectors.counting()
                ));
        long totalRequests = all.size();
        long approvedRequests = all.stream()
                .filter(r -> r.getStatus() == Status.PAID ||
                        r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED ||
                        r.getStatus() == Status.HR_APPROVED)
                .count();

        Double approvalRate = totalRequests > 0 ? ((double) approvedRequests / totalRequests) * 100 : 0.0;
        return AccountantDashboardDTO.builder()
                .totalPendingPayout(pendingPayout)
                .pendingApprovalCount(pendingAction)
                .spendByType(spendByType)
                .statusDistribution(statusDistribution)
                .approvalRate(approvalRate)
                .build();
    }

    /**
     * Generates dashboard statistics for the HR role.
     *
     * <p>This method calculates key metrics for the HR dashboard:
     * <ul>
     * <li>Count of reimbursements pending HR verification</li>
     * <li>Total number of employees in the system</li>
     * </ul>
     *
     * @return dashboard statistics including pending HR verification count
     * @see HrDashboardDTO
     * @see Status#FORWARDED_TO_HR
     */
    @Override
    public HrDashboardDTO getHrDashboardStats() {
        long pendingHr = repository.countByStatus(Status.FORWARDED_TO_HR);
        long totalEmployees = userRepository.count();
        Map<String, Long> pendingByType = repository.countPendingByType()
                .stream()
                .collect(Collectors.toMap(
                        TypeCountAggregation::_id,
                        TypeCountAggregation::count
                ));

        return HrDashboardDTO.builder()
                .pendingHrVerificationCount(pendingHr)
                .totalEmployees(totalEmployees)
                .pendingByType(pendingByType)
                .build();
    }

    /**
     * Updates an existing reimbursement request with new details and handles resubmission logic.
     *
     * <p>This method allows editing of reimbursements in specific statuses:
     * <ul>
     * <li>Initial submission statuses (SUBMITTED, PENDING_MANAGER_APPROVAL)</li>
     * <li>Rejected statuses (with resubmission limit of 1)</li>
     * </ul>
     *
     * <p>For rejected reimbursements, it:
     * <ul>
     * <li>Saves the rejection history</li>
     * <li>Resets the status based on reimbursement type</li>
     * <li>Increments the submission count</li>
     * </ul>
     *
     * @param id the ID of the reimbursement to update
     * @param title the updated title
     * @param amount the updated amount
     * @param description the updated description
     * @param noInvoice updated invoice availability flag
     * @param invoiceNote updated invoice note
     * @param newFiles new files to upload
     * @param existingFileUrls URLs of existing files to keep
     * @return the updated Reimbursement entity
     * @throws RuntimeException if editing not allowed or resubmission limit exceeded
     * @see RejectionHistory
     * @see Status
     */
    @Override
    public Reimbursement updateReimbursement(String id, String title, Double amount, String description,
                                             boolean noInvoice, String invoiceNote,
                                             List<MultipartFile> newFiles, List<String> existingFileUrls) {

        Reimbursement r = repository.findById(id).orElseThrow(() -> new RuntimeException("Reimbursement not found"));

        boolean wasRejected = r.getStatus().name().contains("REJECTED");
        if (wasRejected && r.getSubmissionCount() >= 1) {
            throw new RuntimeException("Maximum resubmission limit reached. This claim cannot be edited again.");
        }
        if (r.getStatus() != Status.SUBMITTED &&
                r.getStatus() != Status.PENDING_MANAGER_APPROVAL &&
                !wasRejected) {
            throw new RuntimeException("Editing only allowed in initial or rejected statuses.");
        }

        List<String> finalUrls = new ArrayList<>();
        if (existingFileUrls != null) finalUrls.addAll(existingFileUrls);
        if (newFiles != null && !newFiles.isEmpty()) finalUrls.addAll(processFiles(newFiles));
        r.setTitle(title);
        r.setAmount(amount);
        r.setDescription(description);
        r.setNoInvoice(noInvoice);
        r.setInvoiceNote(invoiceNote);
        r.setFileUrls(finalUrls);

        if (wasRejected) {
            RejectionHistory history = RejectionHistory.builder()
                    .previousStatus(r.getStatus().name())
                    .reason(r.getReason())
                    .rejectedAt(r.getUpdatedAt())
                    .rejectedBy(r.getProcessedById())
                    .build();

            if (r.getRejectionHistory() == null) {
                r.setRejectionHistory(new ArrayList<>());
            }
            r.getRejectionHistory().add(history);
            r.setResubmitted(true);
            r.setSubmissionCount(r.getSubmissionCount() + 1);
            r.setProcessedById(null);
            r.setReason(null);
            if (r.getType() == ReimbursementType.TEAM_EVENTS) {
                r.setStatus(Status.PENDING_MANAGER_APPROVAL);
            } else if (r.getType() == ReimbursementType.CERTIFICATE) {
                r.setStatus(Status.FORWARDED_TO_HR);
            } else {
                r.setStatus(Status.SUBMITTED);
            }
        }

        r.setUpdatedAt(LocalDateTime.now());
        return repository.save(r);
    }

    /**
     * Completes a certificate reimbursement by adding final documents and amount.
     *
     * <p>This method is used after HR approval to finalize certificate reimbursements.
     * It allows uploading the completion documents and setting the final amount,
     * then moves the reimbursement to SUBMITTED status for accountant processing.
     *
     * @param id the ID of the certificate reimbursement to complete
     * @param files list of completion documents (certificates, etc.)
     * @param finalAmount the final amount after completion
     * @return the updated Reimbursement entity
     * @throws RuntimeException if reimbursement not found or not HR approved
     * @see Status#HR_APPROVED
     * @see Status#SUBMITTED
     * @see ReimbursementType#CERTIFICATE
     */
    @Override
    public Reimbursement completeCertification(String id, List<MultipartFile> files, Double finalAmount) {
        Reimbursement r = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (r.getStatus() != Status.HR_APPROVED) throw new RuntimeException("HR Approval needed.");
        r.getFileUrls().addAll(processFiles(files));
        r.setAmount(finalAmount);
        r.setStatus(Status.SUBMITTED);
        return repository.save(r);
    }

    public Page<Reimbursement> getAllReimbursements(Pageable p) {
        return repository.findAll(p);
    }

    /**
     * Retrieves all reimbursements with pagination support.
     *
     * <p>This method provides access to all reimbursement records in the system
     * with pagination for efficient data retrieval. Typically used by admin users
     * or for system-wide reporting.
     *
     * @param p pagination parameters (page number, size, sorting)
     * @return paginated list of all reimbursements
     * @see Page
     * @see Pageable
     */
    public Page<Reimbursement> getReimbursementsByEmployeeId(String id, Pageable p) {
        return repository.findAllByEmployeeId(id, p);
    }


    /**
     * Retrieves all reimbursements submitted by a specific employee.
     *
     * <p>This method returns all reimbursement requests associated with a particular
     * employee ID, useful for employee dashboards and personal reimbursement history.
     *
     * @param id the employee ID whose reimbursements are being requested
     * @param p pagination parameters (page number, size, sorting)
     * @return paginated list of reimbursements for the specified employee
     * @see Page
     * @see Pageable
     */
    public Page<Reimbursement> getByStatus(Status s, Pageable p) {
        return repository.findByStatus(s, p);
    }


    /**
     * Retrieves all reimbursements with a specific status.
     *
     * <p>This method filters reimbursements by their current status, useful for
     * generating reports and monitoring workflow stages.
     *
     * @param s the status to filter by
     * @param p pagination parameters (page number, size, sorting)
     * @return paginated list of reimbursements with the specified status
     * @see Status
     * @see Page
     * @see Pageable
     */
    public Page<Reimbursement> getByType(ReimbursementType t, Pageable p) {
        return repository.findByType(t, p);
    }
}