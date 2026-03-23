package com.reimbursement.backend.service;

import com.reimbursement.backend.model.Certification;
import com.reimbursement.backend.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificationServiceImpl implements CertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    @Override
    public Certification addCertification(Certification certification) {
        return certificationRepository.save(certification);
    }

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

    @Override
    public void deleteCertification(String id) {
        certificationRepository.deleteById(id);
    }

    @Override
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @Override
    public List<Certification> getCertificationsByStatus(String status) {
        return certificationRepository.findByStatus(status);
    }

    @Override
    public Certification getCertificationById(String id) {
        return certificationRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Certification> getAllCertifications(Pageable pageable) {
        return certificationRepository.findAll(pageable);
    }

    @Override
    public Page<Certification> getCertificationsByStatus(String status, Pageable pageable) {
        return certificationRepository.findByStatus(status, pageable);
    }
}