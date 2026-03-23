package com.reimbursement.backend.repository;

import com.reimbursement.backend.model.Certification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CertificationRepository extends MongoRepository<Certification, String> {
    List<Certification> findByStatus(String status);
    Page<Certification> findAll(Pageable pageable);
    Page<Certification> findByStatus(String status, Pageable pageable);
}