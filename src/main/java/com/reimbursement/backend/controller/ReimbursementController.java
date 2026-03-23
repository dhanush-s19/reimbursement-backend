package com.reimbursement.backend.controller;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.Status;
import com.reimbursement.backend.service.ReimbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reimbursements")
@RequiredArgsConstructor
public class ReimbursementController {

    private final ReimbursementService service;

    @PostMapping(value = "/submit", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reimbursement> submit(
            @RequestParam("title") String title,
            @RequestParam("amount") Double amount,
            @RequestParam("submittedBy") String submittedBy,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "noInvoice", required = false, defaultValue = "false") boolean noInvoice,
            @RequestParam(value = "invoiceNote", required = false) String invoiceNote,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        ReimbursementType reimbursementType = ReimbursementType.valueOf(type.toUpperCase());

        Reimbursement reimbursement = service.submitReimbursement(
                title,
                amount,
                description,
                noInvoice,
                invoiceNote,
                files,
                submittedBy,
                name,
                reimbursementType
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reimbursement);
    }

    @PostMapping("/team")
    public ResponseEntity<Reimbursement> submitTeamReimbursement(
            @RequestBody TeamReimbursementRequest request
    ) {
        Reimbursement reimbursement = service.submitTeamReimbursement(
                request.getTitle(),
                request.getAmount(),
                request.getDescription(),
                request.isNoInvoice(),
                request.getInvoiceNote(),
                request.getSubmittedById(),
                request.getName(),
                request.getTeamMemberIds(),
                request.getType(),
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reimbursement);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Reimbursement> updateStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request
    ) {
        Reimbursement updated = service.updateReimbursementStatus(
                id,
                request.getStatus(),
                request.getReason(),
                request.getProcessedById(),
                request.getApprovedAmount()
        );

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReimbursementResponse> getById(
            @PathVariable String id,
            @RequestParam String role
    ) {
        return ResponseEntity.ok(service.getById(id, role));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<Reimbursement>> getByEmployee(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(service.getReimbursementsByEmployeeId(employeeId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Reimbursement>> getByStatus(
            @PathVariable Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(service.getByStatus(status, pageable));
    }

    @GetMapping("/queue/hr")
    public ResponseEntity<ReimbursementPageResponse> getHRQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(service.getHRQueue(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Reimbursement>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(service.getAllReimbursements(pageable));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Reimbursement>> getByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        ReimbursementType reimbursementType;
        try {
            reimbursementType = ReimbursementType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // better than throwing RuntimeException
        }

        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(service.getReimbursementsByType(reimbursementType, pageable));
    }

    @GetMapping("/accountant/stats")
    public ResponseEntity<AccountantDashboardDTO> getAccountantStats() {
        return ResponseEntity.ok(service.getAccountantDashboardStats());
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }
}