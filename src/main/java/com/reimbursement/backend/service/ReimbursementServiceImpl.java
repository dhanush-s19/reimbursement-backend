package com.reimbursement.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.reimbursement.backend.dto.AccountantDashboardDTO;
import com.reimbursement.backend.dto.HrDashboardDTO;
import com.reimbursement.backend.dto.ReimbursementPageResponse;
import com.reimbursement.backend.dto.ReimbursementResponse;
import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
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
import java.util.*;

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
        validateFiles(type, noInvoice, invoiceNote, fileUrls);

        boolean requiresHR = (type == ReimbursementType.CERTIFICATE);
        Status initialStatus = requiresHR ? Status.FORWARDED_TO_HR : Status.SUBMITTED;

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
                .requiresHrApproval(requiresHR)
                .build();

        return repository.save(reimbursement);
    }

    @Override
    public Reimbursement submitTeamReimbursement(String title, Double amount, String description,
                                                 boolean noInvoice, String invoiceNote, String submittedById,
                                                 String name, List<String> teamMemberIds, ReimbursementType type,
                                                 List<MultipartFile> files) {

        Reimbursement reimbursement = Reimbursement.builder()
                .title(title)
                .amount(amount)
                .description(description)
                .employeeId(submittedById)
                .name(name)
                .teamMemberIds(teamMemberIds)
                .status(Status.FORWARDED_TO_HR)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(type)
                .requiresHrApproval(true)
                .fileUrls(new ArrayList<>())
                .invoiceNote(invoiceNote)
                .noInvoice(noInvoice)
                .build();
        return repository.save(reimbursement);
    }

    @Override
    public Reimbursement updateReimbursementStatus(String id, Status nextStatus, String reason, String processedById, BigDecimal approvedAmount) {

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

        if (reason != null) {
            r.setReason(reason.trim());
        }


        r.setStatus(nextStatus);
        r.setProcessedById(processedById);
        r.setUpdatedAt(LocalDateTime.now());

        return repository.save(r);
    }

    @Override
    public List<Status> getAllowedNextStatuses(Reimbursement r, String role) {
        Status current = r.getStatus();
        List<Status> allowed = new ArrayList<>();

        if ("HR".equalsIgnoreCase(role)) {
            if (current == Status.FORWARDED_TO_HR) {
                allowed.addAll(List.of(Status.HR_APPROVED, Status.HR_REJECTED));
            }
        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            switch (current) {
                case SUBMITTED -> {
                    allowed.addAll(List.of(Status.ACCOUNTANT_FINAL_APPROVED, Status.ACCOUNTANT_REJECTED, Status.FORWARDED_TO_HR));
                }
                case HR_APPROVED -> {
                    allowed.addAll(List.of(Status.ACCOUNTANT_FINAL_APPROVED, Status.ACCOUNTANT_REJECTED));
                }
                case ACCOUNTANT_FINAL_APPROVED -> {
                    allowed.add(Status.PAID);
                }
            }
        }
        return allowed;
    }

    @Override
    public ReimbursementResponse getById(String id, String role) {
        Reimbursement r = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        return mapToResponse(r, role);
    }

    private ReimbursementResponse mapToResponse(Reimbursement r, String role) {
        List<Status> nextStatuses = getAllowedNextStatuses(r, role);
        return ReimbursementResponse.builder()
                .reimbursement(r)
                .allowedNextStatuses(nextStatuses)
                // Show amount field only when the accountant is at the final approval stage
                .showApprovedAmountField(nextStatuses.contains(Status.ACCOUNTANT_FINAL_APPROVED))
                .showReasonField(nextStatuses.contains(Status.HR_REJECTED) || nextStatuses.contains(Status.ACCOUNTANT_REJECTED))
                .build();
    }

    private List<String> processFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return Collections.emptyList();
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

    private void validateFiles(ReimbursementType type, boolean noInvoice, String invoiceNote, List<String> fileUrls) {
        if (type == ReimbursementType.TEAM_EVENTS) {
            return;
        }
        if (type == ReimbursementType.CERTIFICATE) {
            if (fileUrls == null || fileUrls.isEmpty()) {
                throw new RuntimeException("A certificate file upload is mandatory for this type.");
            }
            return;
        }
        if (type == ReimbursementType.NORMAL) {
            if (!noInvoice && (fileUrls == null || fileUrls.isEmpty())) {
                throw new RuntimeException("Please upload an invoice or provide a reason for the missing receipt.");
            }
            if (noInvoice && (invoiceNote == null || invoiceNote.isBlank())) {
                throw new RuntimeException("An explanation note is required when no invoice is provided.");
            }
        }
    }

    public Page<Reimbursement> getAllReimbursements(Pageable p) {
        return repository.findAll(p);
    }

    public Page<Reimbursement> getReimbursementsByEmployeeId(String id, Pageable p) {
        return repository.findAllByEmployeeId(id, p);
    }

    public Page<Reimbursement> getByStatus(Status s, Pageable p) {
        return repository.findByStatus(s, p);
    }

    public Page<Reimbursement> getReimbursementsByType(ReimbursementType t, Pageable p) {
        return repository.findByType(t, p);
    }

    @Override
    public ReimbursementPageResponse getHRQueue(Pageable pageable) {
        Page<Reimbursement> page = repository.findByStatus(Status.FORWARDED_TO_HR, pageable);
        List<ReimbursementResponse> content = page.getContent().stream()
                .map(r -> mapToResponse(r, "HR")).toList();
        return ReimbursementPageResponse.builder()
                .content(content).totalPages(page.getTotalPages()).totalElements(page.getTotalElements()).number(page.getNumber()).build();
    }

    @Override
    public AccountantDashboardDTO getAccountantDashboardStats() {
        List<Reimbursement> all = repository.findAll();
        Double pendingPayout = all.stream()
                .filter(r -> r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED)
                .mapToDouble(r -> r.getApprovedAmount() != null ? r.getApprovedAmount().doubleValue() : 0.0)
                .sum();
        long pendingAction = all.stream()
                .filter(r -> r.getStatus() == Status.SUBMITTED || r.getStatus() == Status.HR_APPROVED)
                .count();

        Map<String, Double> spendByType = new HashMap<>();
        for (Reimbursement r : all) {
            if (r.getStatus() == Status.PAID || r.getStatus() == Status.ACCOUNTANT_FINAL_APPROVED) {
                String type = r.getType().name();
                Double amt = r.getApprovedAmount() != null ? r.getApprovedAmount().doubleValue() : r.getAmount();
                spendByType.put(type, spendByType.getOrDefault(type, 0.0) + amt);
            }
        }

        Map<String, Long> statusDist = new HashMap<>();
        for (Status s : Status.values()) {
            statusDist.put(s.name(), all.stream().filter(r -> r.getStatus() == s).count());
        }

        return AccountantDashboardDTO.builder()
                .totalPendingPayout(pendingPayout)
                .pendingApprovalCount(pendingAction)
                .spendByType(spendByType)
                .statusDistribution(statusDist)
                .build();
    }

    @Override
    public HrDashboardDTO getHrDashboardStats() {
        List<Reimbursement> allReimbursements = repository.findAll();
        long pendingHr = allReimbursements.stream()
                .filter(r -> r.getStatus() == Status.FORWARDED_TO_HR)
                .count();

        Map<String, Long> pendingByType = new HashMap<>();
        allReimbursements.stream()
                .filter(r -> r.getStatus() == Status.FORWARDED_TO_HR)
                .forEach(r -> {
                    String type = r.getType().name();
                    pendingByType.put(type, pendingByType.getOrDefault(type, 0L) + 1);
                });

        long totalEmployees = userRepository.count();
        Map<String, Long> employeesByDept = new HashMap<>();
        userRepository.findAll().forEach(user -> {
            if (user.getDepartment() != null) {
                String deptName = user.getDepartment().name();
                employeesByDept.put(deptName, employeesByDept.getOrDefault(deptName, 0L) + 1);
            }
        });

        return HrDashboardDTO.builder()
                .pendingHrVerificationCount(pendingHr)
                .pendingByType(pendingByType)
                .totalEmployees(totalEmployees)
                .employeesByDepartment(employeesByDept)
                .build();
    }
}