package com.reimbursement.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a history of a reimbursement request's rejection.")
public class RejectionHistory {
    @Schema(description = "Previous status of the reimbursement request.")
    private String previousStatus;
    @Schema(description = "Reason for the rejection.")
    private String reason;
    @Schema(description = "Date and time the reimbursement request was rejected.")
    private LocalDateTime rejectedAt;
    @Schema(description = "ID of the user who rejected the reimbursement request.")
    private String rejectedBy;
}
