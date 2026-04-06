package com.reimbursement.backend.controller;

import com.reimbursement.backend.dto.*;
import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.Status;
import com.reimbursement.backend.service.ReimbursementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for managing reimbursement requests and operations.
 * Provides endpoints for submitting, updating, retrieving, and processing reimbursement requests.
 * Supports file uploads, role-based access, and various filtering options.
 *
 * @author Reimbursement Management System
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/reimbursements")
@RequiredArgsConstructor
@Tag(name = "Reimbursement Management", description = "Endpoints for Reimbursement Management")
public class ReimbursementController {

    private final ReimbursementService service;

    /**
     * Submits a new reimbursement request with optional file attachments.
     * Supports multiple file uploads and optional invoice information.
     *
     * @param title The title of the reimbursement request
     * @param amount The reimbursement amount being requested
     * @param submittedBy The ID of the employee submitting the request
     * @param name The name of the employee submitting the request
     * @param type The type of reimbursement (e.g., TRAVEL, MEDICAL, etc.)
     * @param description Optional description of the reimbursement purpose
     * @param noInvoice Flag indicating if no invoice is available (default: false)
     * @param invoiceNote Optional note explaining why no invoice is available
     * @param files Optional list of supporting documents/files
     * @return ResponseEntity containing the created Reimbursement object with HTTP status 201
     * @throws IllegalArgumentException if the reimbursement type is invalid
     */
    @Operation(summary = "Submit a new reimbursement request", description = "Submits a new reimbursement request with optional file attachments.")
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

    /**
     * Submits a team reimbursement request on behalf of another employee.
     * Used by managers or HR to submit reimbursements for team members.
     *
     * @param title The title of the reimbursement request
     * @param amount The reimbursement amount being requested
     * @param description Optional description of the reimbursement purpose
     * @param noInvoice Flag indicating if no invoice is available (default: false)
     * @param invoiceNote Optional note explaining why no invoice is available
     * @param submittedById The ID of the employee for whom the reimbursement is being submitted
     * @param name The name of the employee for whom the reimbursement is being submitted
     * @param type The type of reimbursement (e.g., TRAVEL, MEDICAL, etc.)
     * @param managerName The name of the manager submitting the request
     * @param userRole The role of the user submitting the request
     * @param managerId The ID of the manager submitting the request
     * @param files Optional list of supporting documents/files
     * @return ResponseEntity containing the created Reimbursement object with HTTP status 201,
     *         or HTTP 400 if the reimbursement type is invalid
     */
    @Operation(summary = "Submit a team reimbursement request", description = "Submits a team reimbursement request on behalf of another employee.")
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
            @RequestParam("managerName") String managerName,
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
                    managerName,
                    managerId
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(reimbursement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing reimbursement request with new details and optional file attachments.
     * Supports updating existing files while preserving previously uploaded documents.
     *
     * @param id The ID of the reimbursement to update
     * @param title The updated title of the reimbursement request
     * @param amount The updated reimbursement amount
     * @param description Optional updated description of the reimbursement purpose
     * @param noInvoice Updated flag indicating if no invoice is available
     * @param invoiceNote Optional updated note explaining invoice status
     * @param existingFileUrls Optional list of URLs for existing files to keep
     * @param newFiles Optional list of new files to upload
     * @return ResponseEntity containing the updated Reimbursement object
     */
    @Operation(summary = "Update an existing reimbursement request", description = "Updates an existing reimbursement request with new details and optional file attachments.")
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

    /**
     * Updates the status of a reimbursement request with optional reason and approved amount.
     * Used for approving, rejecting, or processing reimbursement requests.
     *
     * @param id The ID of the reimbursement to update
     * @param request The status update request containing new status, reason, processor ID, and approved amount
     * @return ResponseEntity containing the updated Reimbursement object
     */
    @Operation(summary = "Update the status of a reimbursement request", description = "Updates the status of a reimbursement request with optional reason and approved amount.")
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

    /**
     * Retrieves reimbursements filtered by type with pagination and sorting options.
     * Supports filtering by reimbursement categories like TRAVEL, MEDICAL, etc.
     *
     * @param type The reimbursement type to filter by (case-insensitive)
     * @param page The page number to retrieve (default: 0)
     * @param size The number of items per page (default: 10)
     * @param sortBy The field to sort by (default: "createdAt")
     * @param direction The sort direction - "asc" or "desc" (default: "desc")
     * @return ResponseEntity containing a Page of Reimbursement objects filtered by type,
     *         or HTTP 400 if the reimbursement type is invalid
     */
    @Operation(summary = "Get reimbursements by type", description = "Retrieves reimbursements filtered by type with pagination and sorting options.")
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

    /**
     * Completes the certification process for a reimbursement by uploading certification documents
     * and setting the final approved amount.
     *
     * @param id The ID of the reimbursement to complete certification for
     * @param files List of certification documents to upload
     * @param finalAmount The final approved amount after certification
     * @return ResponseEntity containing the updated Reimbursement object with completed certification
     */
    @Operation(summary = "Complete certification for a reimbursement", description = "Completes the certification process for a reimbursement by uploading certification documents and setting the final approved amount.")
    @PutMapping(value = "/{id}/complete-certification", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reimbursement> completeCertification(
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("finalAmount") Double finalAmount) {
        return ResponseEntity.ok(service.completeCertification(id, files, finalAmount));
    }

    /**
     * Retrieves a specific reimbursement by ID with role-based access control.
     * Returns different levels of detail based on the user's role.
     *
     * @param id The ID of the reimbursement to retrieve
     * @param role The role of the user making the request (affects data visibility)
     * @return ResponseEntity containing the ReimbursementResponse object with role-appropriate data
     */
    @Operation(summary = "Get a specific reimbursement by ID", description = "Retrieves a specific reimbursement by ID with role-based access control.")
    @GetMapping("/{id}")
    public ResponseEntity<ReimbursementResponse> getById(@PathVariable String id, @RequestParam String role) {
        return ResponseEntity.ok(service.getById(id, role));
    }

    /**
     * Retrieves reimbursements submitted by a specific employee with pagination and sorting.
     * Used by employees to view their own reimbursement history.
     *
     * @param employeeId The ID of the employee whose reimbursements to retrieve
     * @param page The page number to retrieve (default: 0)
     * @param size The number of items per page (default: 10)
     * @param sortBy The field to sort by (default: "createdAt")
     * @param direction The sort direction - "asc" or "desc" (default: "desc")
     * @return ResponseEntity containing a Page of Reimbursement objects for the specified employee
     */
    @Operation(summary = "Get reimbursements by employee", description = "Retrieves reimbursements submitted by a specific employee with pagination and sorting.")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<Reimbursement>> getByEmployee(@PathVariable String employeeId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getReimbursementsByEmployeeId(employeeId, createPageable(page, size, sortBy, direction)));
    }

    /**
     * Retrieves reimbursements filtered by their current status with pagination and sorting.
     * Useful for filtering requests by processing stages (PENDING, APPROVED, REJECTED, etc.).
     *
     * @param status The status to filter by (PENDING, APPROVED, REJECTED, etc.)
     * @param page The page number to retrieve (default: 0)
     * @param size The number of items per page (default: 10)
     * @param sortBy The field to sort by (default: "createdAt")
     * @param direction The sort direction - "asc" or "desc" (default: "desc")
     * @return ResponseEntity containing a Page of Reimbursement objects filtered by status
     */
    @Operation(summary = "Get reimbursements by status", description = "Retrieves reimbursements filtered by their current status with pagination and sorting.")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Reimbursement>> getByStatus(@PathVariable Status status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getByStatus(status, createPageable(page, size, sortBy, direction)));
    }

    /**
     * Retrieves the HR dashboard queue showing reimbursements requiring HR attention.
     * Returns requests that have been approved by managers and need HR processing.
     *
     * @param page The page number to retrieve (default: 0)
     * @param size The number of items per page (default: 10)
     * @param sortBy The field to sort by (default: "createdAt")
     * @param direction The sort direction - "asc" or "desc" (default: "desc")
     * @return ResponseEntity containing ReimbursementPageResponse with HR queue data
     */
    @Operation(summary = "Get HR queue", description = "Retrieves the HR dashboard queue showing reimbursements requiring HR attention.")
    @GetMapping("/queue/hr")
    public ResponseEntity<ReimbursementPageResponse> getHRQueue(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getHRQueue(createPageable(page, size, sortBy, direction)));
    }

    /**
     * Retrieves the manager queue showing reimbursements forwarded to a specific manager for approval.
     * Returns requests that require manager review and approval.
     *
     * @param managerId The ID of the manager whose queue to retrieve
     * @param page The page number to retrieve (default: 0)
     * @param size The number of items per page (default: 10)
     * @param sortBy The field to sort by (default: "createdAt")
     * @param direction The sort direction - "asc" or "desc" (default: "desc")
     * @return ResponseEntity containing ReimbursementPageResponse with manager queue data
     */
    @Operation(summary = "Get manager queue", description = "Retrieves the manager queue showing reimbursements forwarded to a specific manager for approval.")
    @GetMapping("/queue/manager/{managerId}")
    public ResponseEntity<ReimbursementPageResponse> getManagerQueue(
            @PathVariable String managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getManagerQueue(managerId, createPageable(page, size, sortBy, direction)));
    }

    /**
     * Retrieves all reimbursements in the system with pagination and sorting.
     * Typically used by administrators or for reporting purposes.
     *
     * @param page The page number to retrieve (default: 0)
     * @param size The number of items per page (default: 10)
     * @param sortBy The field to sort by (default: "createdAt")
     * @param direction The sort direction - "asc" or "desc" (default: "desc")
     * @return ResponseEntity containing a Page of all Reimbursement objects
     */
    @Operation(summary = "Get all reimbursements", description = "Retrieves all reimbursements in the system with pagination and sorting.")
    @GetMapping("/all")
    public ResponseEntity<Page<Reimbursement>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(service.getAllReimbursements(createPageable(page, size, sortBy, direction)));
    }

    /**
     * Retrieves dashboard statistics for the accountant role.
     * Returns summary data including pending certifications, completed requests, and financial totals.
     *
     * @return ResponseEntity containing AccountantDashboardDTO with accountant-specific statistics
     */
    @Operation(summary = "Get accountant statistics", description = "Retrieves dashboard statistics for the accountant role.")
    @GetMapping("/accountant/stats")
    public ResponseEntity<AccountantDashboardDTO> getAccountantStats() {
        return ResponseEntity.ok(service.getAccountantDashboardStats());
    }

    /**
     * Retrieves dashboard statistics for the HR role.
     * Returns summary data including pending approvals, processed requests, and workflow metrics.
     *
     * @return ResponseEntity containing HrDashboardDTO with HR-specific statistics
     */
    @Operation(summary = "Get HR statistics", description = "Retrieves dashboard statistics for the HR role.")
    @GetMapping("/hr/stats")
    public ResponseEntity<HrDashboardDTO> getHrStats() {
        return ResponseEntity.ok(service.getHrDashboardStats());
    }

    /**
     * Creates a Pageable object for pagination and sorting operations.
     * Helper method used across multiple endpoints to standardize pagination behavior.
     *
     * @param page The page number (0-based)
     * @param size The number of items per page
     * @param sortBy The field name to sort by
     * @param direction The sort direction ("asc" or "desc")
     * @return Pageable object configured with the specified pagination and sorting parameters
     */
    @Operation(summary = "Create pageable", description = "Creates a Pageable object for pagination and sorting operations.")
    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }
}