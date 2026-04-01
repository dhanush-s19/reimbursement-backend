package com.reimbursement.backend.controller;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.Status;
import com.reimbursement.backend.service.ReimbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        ReimbursementType reimbursementType = ReimbursementType.valueOf(type.toUpperCase());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submitReimbursement(title, amount, description, noInvoice, invoiceNote, files, submittedBy, name, reimbursementType));
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reimbursement> update(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("amount") Double amount,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "noInvoice", required = false, defaultValue = "false") boolean noInvoice,
            @RequestParam(value = "invoiceNote", required = false) String invoiceNote,
            @RequestParam(value = "existingFileUrls", required = false) List<String> existingFileUrls,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles
    ) {
        Reimbursement updated = service.updateReimbursement(id, title, amount, description, noInvoice, invoiceNote, newFiles, existingFileUrls);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Reimbursement> updateStatus(@PathVariable String id, @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(service.updateStatus(
                id,
                request.getStatus(),
                request.getReason(),
                request.getProcessedById(),
                request.getApprovedAmount()
        ));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Reimbursement>> getByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            ReimbursementType reimbursementType = ReimbursementType.valueOf(type.toUpperCase());
            Pageable pageable = createPageable(page, size, sortBy, direction);
            return ResponseEntity.ok(service.getByType(reimbursementType, pageable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{id}/complete-certification", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reimbursement> completeCertification(
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("finalAmount") Double finalAmount) {
        return ResponseEntity.ok(service.completeCertification(id, files, finalAmount));
    }

    @PostMapping(value = "/team", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reimbursement> submitTeamReimbursement(
            @RequestParam("title") String title,
            @RequestParam("amount") Double amount,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "noInvoice", required = false, defaultValue = "false") boolean noInvoice,
            @RequestParam(value = "invoiceNote", required = false) String invoiceNote,
            @RequestParam("submittedById") String submittedById,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("userRole") String userRole,
            @RequestParam("managerId") String managerId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        try {
            ReimbursementType reimbursementType = ReimbursementType.valueOf(type.toUpperCase());
            Reimbursement reimbursement = service.submitTeamReimbursement(
                    title,
                    amount,
                    description,
                    noInvoice,
                    invoiceNote,
                    submittedById,
                    name,
                    reimbursementType,
                    files,
                    userRole,
                    managerId
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(reimbursement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReimbursementResponse> getById(@PathVariable String id, @RequestParam String role) {
        return ResponseEntity.ok(service.getById(id, role));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<Reimbursement>> getByEmployee(@PathVariable String employeeId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getReimbursementsByEmployeeId(employeeId, createPageable(page, size, sortBy, direction)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Reimbursement>> getByStatus(@PathVariable Status status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getByStatus(status, createPageable(page, size, sortBy, direction)));
    }

    @GetMapping("/queue/hr")
    public ResponseEntity<ReimbursementPageResponse> getHRQueue(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getHRQueue(createPageable(page, size, sortBy, direction)));
    }

    @GetMapping("/queue/manager/{managerId}")
    public ResponseEntity<ReimbursementPageResponse> getManagerQueue(
            @PathVariable String managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getManagerQueue(managerId, createPageable(page, size, sortBy, direction)));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Reimbursement>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getAllReimbursements(createPageable(page, size, sortBy, direction)));
    }

    @GetMapping("/accountant/stats")
    public ResponseEntity<AccountantDashboardDTO> getAccountantStats() {
        return ResponseEntity.ok(service.getAccountantDashboardStats());
    }

    @GetMapping("/hr/stats")
    public ResponseEntity<HrDashboardDTO> getHrStats() {
        return ResponseEntity.ok(service.getHrDashboardStats());
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }
}