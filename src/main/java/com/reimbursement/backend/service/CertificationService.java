package com.reimbursement.backend.service;

import com.reimbursement.backend.model.Certification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
/**
 * Service interface for managing certification operations in the reimbursement system.
 * Provides CRUD operations and status-based filtering for certification entities.
 */
public interface CertificationService {
    /**
     * Adds a new certification to the system.
     *
     * @param certification the certification entity to be added
     * @return the newly created certification with generated ID
     * @throws IllegalArgumentException if certification is null or invalid
     */
    Certification addCertification(Certification certification);

    /**
     * Updates an existing certification with the provided details.
     *
     * @param id the unique identifier of the certification to update
     * @param certification the updated certification details
     * @return the updated certification entity
     * @throws IllegalArgumentException if id is null or not found
     */
    Certification updateCertification(String id, Certification certification);

    /**
     * Deletes a certification from the system by its ID.
     *
     * @param id the unique identifier of the certification to delete
     * @throws IllegalArgumentException if id is null or not found
     */
    void deleteCertification(String id);

    /**
     * Retrieves all certifications from the system.
     *
     * @return a list of all certification entities
     */
    List<Certification> getAllCertifications();

    /**
     * Retrieves certifications filtered by their status.
     *
     * @param status the status to filter certifications by (e.g., "PENDING", "APPROVED", "REJECTED")
     * @return a list of certifications matching the specified status
     * @throws IllegalArgumentException if status is null or empty
     */
    List<Certification> getCertificationsByStatus(String status);

    /**
     * Retrieves a specific certification by its unique ID.
     *
     * @param id the unique identifier of the certification
     * @return the certification entity with the specified ID
     * @throws IllegalArgumentException if id is null or not found
     */
    Certification getCertificationById(String id);

    /**
     * Retrieves all certifications with pagination support.
     *
     * @param pageable the pagination information (page number, size, sort)
     * @return a page of certification entities
     */
    Page<Certification> getAllCertifications(Pageable pageable);

    /**
     * Retrieves certifications filtered by status with pagination support.
     *
     * @param status the status to filter certifications by (e.g., "PENDING", "APPROVED", "REJECTED")
     * @param pageable the pagination information (page number, size, sort)
     * @return a page of certifications matching the specified status
     * @throws IllegalArgumentException if status is null or empty
     */
    Page<Certification> getCertificationsByStatus(String status, Pageable pageable);
}