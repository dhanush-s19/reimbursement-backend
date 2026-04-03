package com.reimbursement.backend.repository;

import com.reimbursement.backend.model.Certification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Repository interface for managing Certification entities in the MongoDB database.
 * Provides CRUD operations and custom query methods for certification data.
 * 
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
public interface CertificationRepository extends MongoRepository<Certification, String> {
    /**
     * Finds all certifications with the specified status.
     * 
     * @param status the status to search for (e.g., "PENDING", "APPROVED", "REJECTED")
     * @return a list of certifications matching the given status
     */
    List<Certification> findByStatus(String status);
    /**
     * Finds all certifications with pagination support.
     * 
     * @param pageable pagination and sorting information
     * @return a page containing all certifications
     */
    Page<Certification> findAll(Pageable pageable);
    /**
     * Finds certifications with the specified status with pagination support.
     * 
     * @param status the status to search for (e.g., "PENDING", "APPROVED", "REJECTED")
     * @param pageable pagination and sorting information
     * @return a page of certifications matching the given status
     */
    Page<Certification> findByStatus(String status, Pageable pageable);
}