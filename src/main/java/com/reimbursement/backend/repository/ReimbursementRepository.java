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
/**
 * Repository interface for managing Reimbursement entities in the MongoDB database.
 *
 * <p>This repository provides methods for performing CRUD operations and custom queries
 * on reimbursement records, including filtering by status, type, employee, and manager.
 * It extends MongoRepository to inherit basic database operations.</p>
 *
 * @author Reimbursement Management System
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface ReimbursementRepository extends MongoRepository<Reimbursement, String> {
    /**
     * Retrieves all reimbursement requests processed by a specific user.
     *
     * @param processedById the ID of the user who processed the reimbursements
     * @return a list of reimbursement requests processed by the specified user
     */
    List<Reimbursement> findByProcessedById(String processedById);

    /**
     * Retrieves all reimbursement requests submitted by a specific employee with pagination.
     *
     * @param employeeId the ID of the employee who submitted the reimbursements
     * @param pageable pagination information
     * @return a page of reimbursement requests submitted by the specified employee
     */
    Page<Reimbursement> findAllByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Retrieves all reimbursement requests with a specific status with pagination.
     *
     * @param status the status to filter by (e.g., PENDING, APPROVED, REJECTED)
     * @param pageable pagination information
     * @return a page of reimbursement requests with the specified status
     */
    Page<Reimbursement> findByStatus(Status status, Pageable pageable);

    /**
     * Retrieves all reimbursement requests of a specific type with pagination.
     *
     * @param type the reimbursement type to filter by (e.g., TRAVEL, MEDICAL, TRAINING)
     * @param pageable pagination information
     * @return a page of reimbursement requests of the specified type
     */
    /**
     * * Retrieves all reimbursement requests of a specific type with pagination.
     * *
     * * @param type the reimbursement type to filter by (e.g., TRAVEL, MEDICAL, TRAINING)
     * * @param pageable pagination information
     * * @return a page of reimbursement requests of the specified type
     */
    Page<Reimbursement> findByType(ReimbursementType type, Pageable pageable);

    /**
     * Retrieves all reimbursement requests with a specific status.
     *
     * @param status the status to filter by (e.g., PENDING, APPROVED, REJECTED)
     * @return a list of all reimbursement requests with the specified status
     */
    List<Reimbursement> findByStatus(Status status);
    /**
     * Calculates the total approved amount for all reimbursements with a specific status.
     *
     * <p>This method uses MongoDB aggregation to sum the approvedAmount field
     * for all reimbursement records matching the given status.</p>
     *
     * @param status the status to filter by
     * @return the total approved amount for reimbursements with the specified status,
     *         or null if no matching records are found
     */
    @Aggregation(pipeline = {
            "{ $match: { status: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$approvedAmount' } } }"
    })

    /**
     * Counts the total number of reimbursement requests with a specific status.
     *
     * @param status the status to count by (e.g., PENDING, APPROVED, REJECTED)
     * @return the total count of reimbursement requests with the specified status
     */
    Double sumApprovedAmountByStatus(Status status);

    /**
     * Counts the total number of reimbursement requests with a specific status.
     *
     * @param status the status to count by (e.g., PENDING, APPROVED, REJECTED)
     * @return the total count of reimbursement requests with the specified status
     */
    long countByStatus(Status status);

    /**
     * Retrieves all reimbursement requests with a specific status assigned to a specific manager with pagination.
     *
     * @param status the status to filter by (e.g., PENDING, APPROVED, REJECTED)
     * @param managerId the ID of the manager assigned to the reimbursements
     * @param pageable pagination information
     * @return a page of reimbursement requests matching the status and manager criteria
     */
    Page<Reimbursement> findByStatusAndManagerId(Status status, String managerId, Pageable pageable);
  ;

}