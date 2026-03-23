package com.reimbursement.backend.service;

import com.reimbursement.backend.model.Certification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CertificationService {

    Certification addCertification(Certification certification);
    Certification updateCertification(String id, Certification certification);
    void deleteCertification(String id);
    List<Certification> getAllCertifications();
    List<Certification> getCertificationsByStatus(String status);
    Certification getCertificationById(String id);
    Page<Certification> getAllCertifications(Pageable pageable);
    Page<Certification> getCertificationsByStatus(String status, Pageable pageable);
}