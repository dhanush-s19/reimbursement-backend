package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request object for updating reimbursement status")
public class UpdateStatusRequest {
    @Schema(description = "Status to update the reimbursement request to")
    private Status status;
    @Schema(description = "Reason for the status update")
    private String reason;
    @Schema(description = "ID of the user processing the status update")
    private String processedById;
    @Schema(description = "Approved amount for the reimbursement request")
    private BigDecimal approvedAmount;
}
