package com.reimbursement.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.reimbursement.backend.dto.AccountantDashboardDTO;
import com.reimbursement.backend.dto.HrDashboardDTO;
import com.reimbursement.backend.dto.ReimbursementPageResponse;
import com.reimbursement.backend.dto.ReimbursementResponse;
import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.RejectionHistory;
import com.reimbursement.backend.model.Status;
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


@Service
@RequiredArgsConstructor
public class ReimbursementServiceImpl implements ReimbursementService {

    private final ReimbursementRepository repository;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;

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

    @Override
    public Reimbursement submitTeamReimbursement(String title, Double amount, String description,
                                                 boolean noInvoice, String invoiceNote,
                                                 String submittedById, String name,
                                                 ReimbursementType type,
                                                 List<MultipartFile> files, String userRole,
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
                .build();

        return repository.save(reimbursement);
    }

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

    @Override
    public AccountantDashboardDTO getAccountantDashboardStats() {
        List<Reimbursement> all = repository.findAll();
        Double pendingPayout = all.stream()
                .filter(r -> r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED)
                .mapToDouble(r -> r.getApprovedAmount() != null ? r.getApprovedAmount().doubleValue() : 0.0).sum();

        long pendingAction = all.stream()
                .filter(r -> r.getStatus() == Status.SUBMITTED || r.getStatus() == Status.HR_APPROVED).count();

        Map<String, Double> spendByType = new HashMap<>();
        all.stream()
                .filter(r -> r.getStatus() == Status.PAID || r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED)
                .forEach(r -> {
                    String type = r.getType().name();
                    Double amt = r.getApprovedAmount() != null ? r.getApprovedAmount().doubleValue() : r.getAmount();
                    spendByType.put(type, spendByType.getOrDefault(type, 0.0) + amt);
                });
        return AccountantDashboardDTO.builder()
                .totalPendingPayout(pendingPayout)
                .pendingApprovalCount(pendingAction)
                .spendByType(spendByType)
                .build();
    }

    @Override
    public HrDashboardDTO getHrDashboardStats() {
        long pendingHr = repository.findAll().stream().filter(r -> r.getStatus() == Status.FORWARDED_TO_HR).count();
        return HrDashboardDTO.builder().pendingHrVerificationCount(pendingHr).totalEmployees(userRepository.count()).build();
    }

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
     *
     * @param id
     * @param files
     * @param finalAmount
     * @return 
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
     *
     * @param id
     * @param p
     * @return gives all reimbursement by employeeID
     */
    public Page<Reimbursement> getReimbursementsByEmployeeId(String id, Pageable p) {
        return repository.findAllByEmployeeId(id, p);
    }


    /**
     *
     * @param s
     * @param p
     * @return gets reimbursement by status
     */
    public Page<Reimbursement> getByStatus(Status s, Pageable p) {
        return repository.findByStatus(s, p);
    }


    /**
     *
     * @param t
     * @param p
     * @return finds reimbursement by type (Certificate, Team, Normal)
     */
    public Page<Reimbursement> getByType(ReimbursementType t, Pageable p) {
        return repository.findByType(t, p);
    }
}