package com.reimbursement.backend.service;

import com.reimbursement.backend.model.Certification;
import com.reimbursement.backend.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing certifications in the reimbursement system.
 * 
 * <p>This class provides business logic for CRUD operations on certifications,
 * including filtering by status and pagination support. It serves as the
 * implementation of the CertificationService interface.</p>
 * 
 * @author Reimbursement Management Team
 * @version 1.0
 * @since 1.0
 */
@Service
public class CertificationServiceImpl implements CertificationService {

    private CertificationRepository certificationRepository;


    /**
     * Constructs a new CertificationServiceImpl with the required repository.
     *
     * @param certificationRepository the repository for certification data access
     */
    public CertificationServiceImpl(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }


    /**
     * Adds a new certification to the system.
     *
     * @param certification the certification entity to be added
     * @return the saved certification entity with generated ID
     */
    @Override
    public Certification addCertification(Certification certification) {
        return certificationRepository.save(certification);
    }


    /**
     * Updates an existing certification with the provided details.
     *
     * @param id the ID of the certification to update
     * @param certification the certification entity with updated information
     * @return the updated certification entity, or null if certification not found
     */
    @Override
    public Certification updateCertification(String id, Certification certification) {
        Optional<Certification> existing = certificationRepository.findById(id);
        if (existing.isPresent()) {
            Certification updated = existing.get();
            updated.setCategory(certification.getCategory());
            updated.setCertification(certification.getCertification());
            updated.setRecommendedRoles(certification.getRecommendedRoles());
            updated.setStatus(certification.getStatus());
            return certificationRepository.save(updated);
        }
        return null;
    }

    /**
     * Deletes a certification from the system by its ID.
     *
     * @param id the ID of the certification to delete
     */
    @Override
    public void deleteCertification(String id) {
        certificationRepository.deleteById(id);
    }

    /**
     * Retrieves all certifications from the system.
     *
     * @return a list of all certification entities
     */
    @Override
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    /**
     * Retrieves certifications filtered by their status.
     *
     * @param status the status to filter certifications by (e.g., "ACTIVE", "INACTIVE")
     * @return a list of certifications with the specified status
     */
    @Override
    public List<Certification> getCertificationsByStatus(String status) {
        return certificationRepository.findByStatus(status);
    }

    /**
     * Retrieves a certification by its unique ID.
     *
     * @param id the ID of the certification to retrieve
     * @return the certification entity if found, null otherwise
     */
    @Override
    public Certification getCertificationById(String id) {
        return certificationRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves all certifications with pagination support.
     *
     * @param pageable the pagination and sorting information
     * @return a page of certification entities
     */
    @Override
    public Page<Certification> getAllCertifications(Pageable pageable) {
        return certificationRepository.findAll(pageable);
    }

    /**
     * Retrieves certifications filtered by status with pagination support.
     *
     * @param status the status to filter certifications by (e.g., "ACTIVE", "INACTIVE")
     * @param pageable the pagination and sorting information
     * @return a page of certifications with the specified status
     */
    @Override
    public Page<Certification> getCertificationsByStatus(String status, Pageable pageable) {
        return certificationRepository.findByStatus(status, pageable);
    }
}