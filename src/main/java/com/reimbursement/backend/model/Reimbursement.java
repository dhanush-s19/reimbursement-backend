package com.reimbursement.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Represents a reimbursement request in the system.")
public class Reimbursement {

    @Schema(description = "Unique identifier for the reimbursement request in the database.")
    @Id
    private String id;
    @Schema(description = "The organization-specific employee ID of the user submitting the reimbursement request.")
    private String employeeId;
    @Schema(description = "The ID of the manager assigned to the reimbursement request.")
    private String managerId;
    @Schema(description = "The name of the manager assigned to the reimbursement request.")
    private String managerName;
    @Schema(description = "The team size of the employee submitting the reimbursement request.")
    private Integer teamSize;
    @Schema(description = "The name of the employee submitting the reimbursement request.")
    private String name;
    @Schema(description = "The title of the employee submitting the reimbursement request.")
    private String title;
    @Schema(description = "The amount of the reimbursement request.")
    private Double amount;
    @Schema(description = "The description of the reimbursement request.")
    private String description;
    @Schema(description = "Indicates if the reimbursement request does not require an invoice.")
    private boolean noInvoice;
    @Schema(description = "Note associated with the reimbursement request if no invoice is required.")
    private String invoiceNote;
    @Schema(description = "List of URLs for the files associated with the reimbursement request.")
    private List<String> fileUrls;
    @Schema(description = "The approved amount of the reimbursement request.")
    private BigDecimal approvedAmount;
    @Schema(description = "The ID of the user who submitted the reimbursement request.")
    private String submittedBy;
    @Schema(description = "Processed by ID")
    private String processedById;
    @Schema(description = "The status of the reimbursement request.")
    private Status status;
    @Schema(description = "The reason for the status change.")
    private String reason;
    @Schema(description = "The type of reimbursement request.")
    private ReimbursementType type;
    @Schema(description = "The date and time the reimbursement request was created.")
    private LocalDateTime createdAt;
    @Schema(description = "The date and time the reimbursement request was last updated.")
    private LocalDateTime updatedAt;
    @Schema(description = "Indicates if the reimbursement request requires HR approval.")
    private boolean requiresHrApproval;
    @Schema(description = "Indicates if the reimbursement request has been resubmitted.")
    private boolean resubmitted;
    @Schema(description = "The count of times the reimbursement request has been submitted.")
    @Builder.Default
    private int submissionCount = 0;
    @Schema(description = "List of status history for the reimbursement request.")
    private List<String> statusHistory;
    @Schema(description = "List of rejection history for the reimbursement request.")
    @Builder.Default
    private List<RejectionHistory> rejectionHistory = new ArrayList<>();
}