package com.reimbursement.backend.repository;

import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementRepository extends MongoRepository<Reimbursement, String> {
    List<Reimbursement> findByProcessedById(String processedById);
    Page<Reimbursement> findAllByEmployeeId(String employeeId, Pageable pageable);
    Page<Reimbursement> findByStatus(Status status, Pageable pageable);
    Page<Reimbursement> findByType(ReimbursementType type, Pageable pageable);
    List<Reimbursement> findByStatus(Status status);
    @Aggregation(pipeline = {
            "{ $match: { status: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$approvedAmount' } } }"
    })
    Double sumApprovedAmountByStatus(Status status);
    long countByStatus(Status status);
    Page<Reimbursement> findByStatusAndManagerId(Status status, String managerId, Pageable pageable);
  ;

}