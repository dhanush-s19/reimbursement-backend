package com.reimbursement.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a reimbursement request in the system.
 * This entity stores comprehensive information about employee reimbursement requests,
 * including financial details, approval workflow status, supporting documents,
 * and audit trail information.
 *
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "reimbursements")
public class Reimbursement {

    @Id
    private String id;
    private String employeeId;
    private String managerId;
    private String managerName;
    private Integer teamSize;
    private String name;
    private String title;
    private Double amount;
    private String description;
    private boolean noInvoice;
    private String invoiceNote;
    private List<String> fileUrls;
    private BigDecimal approvedAmount;
    private String submittedBy;
    private String processedById;
    private Status status;
    private String reason;
    private ReimbursementType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean requiresHrApproval;
    private boolean resubmitted;
    @Builder.Default
    private int submissionCount = 1;
    private List<String> statusHistory;
    @Builder.Default
    private List<RejectionHistory> rejectionHistory = new ArrayList<>();
}