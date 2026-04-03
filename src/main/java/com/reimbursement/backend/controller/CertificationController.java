package com.reimbursement.backend.controller;

import com.reimbursement.backend.model.Certification;
import com.reimbursement.backend.service.CertificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing certification-related operations.
 * This controller provides endpoints for CRUD operations on certifications,
 * including retrieval with pagination and filtering by status.
 */
@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * Constructs a CertificationController with the required CertificationService dependency.
     *
     * @param certificationService the service layer for certification operations
     */
    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    /**
     * Adds a new certification to the system.
     *
     * @param certification the certification object to be added
     * @return ResponseEntity containing the newly created certification
     */
    @PostMapping("/add")
    public ResponseEntity<Certification> addCertification(@RequestBody Certification certification) {
        return ResponseEntity.ok(certificationService.addCertification(certification));
    }

    /**
     * Updates an existing certification identified by its ID.
     *
     * @param id the ID of the certification to update
     * @param certification the updated certification object
     * @return ResponseEntity containing the updated certification, or 404 if not found
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Certification> updateCertification(@PathVariable String id,
                                                             @RequestBody Certification certification) {
        Certification updated = certificationService.updateCertification(id, certification);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a certification identified by its ID.
     *
     * @param id the ID of the certification to delete
     * @return ResponseEntity with status 200 if successful
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCertification(@PathVariable String id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all certifications from the system.
     *
     * @return ResponseEntity containing a list of all certifications
     */
    @GetMapping("/all")
    public ResponseEntity<List<Certification>> getAllCertifications() {
        return ResponseEntity.ok(certificationService.getAllCertifications());
    }

    /**
     * Retrieves a specific certification by its ID.
     *
     * @param id the ID of the certification to retrieve
     * @return ResponseEntity containing the certification, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Certification> getCertificationById(@PathVariable String id) {
        Certification certification = certificationService.getCertificationById(id);
        if (certification != null) {
            return ResponseEntity.ok(certification);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all certifications with pagination support.
     *
     * @param page the page number to retrieve (default: 0)
     * @param size the number of items per page (default: 10)
     * @return ResponseEntity containing a page of certifications
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Certification>> getAllCertificationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(certificationService.getAllCertifications(pageable));
    }

    /**
     * Retrieves certifications filtered by status with pagination support.
     *
     * @param status the status to filter certifications by
     * @param page the page number to retrieve (default: 0)
     * @param size the number of items per page (default: 10)
     * @return ResponseEntity containing a page of certifications with the specified status
     */
    @GetMapping("/status/page")
    public ResponseEntity<Page<Certification>> getCertificationsByStatusPaginated(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(certificationService.getCertificationsByStatus(status, pageable));
    }

    /**
     * Retrieves all certifications filtered by status.
     *
     * @param status the status to filter certifications by
     * @return ResponseEntity containing a list of certifications with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Certification>> getCertificationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(certificationService.getCertificationsByStatus(status));
    }
}