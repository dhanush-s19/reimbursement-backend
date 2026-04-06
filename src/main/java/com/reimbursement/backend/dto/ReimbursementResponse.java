package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
@Schema(description = "Response object containing a reimbursement request and its allowed next statuses.")
public class ReimbursementResponse {
    @Schema(description = "Reimbursement request details")
    private Reimbursement reimbursement;
    @Schema(description = "List of allowed next statuses for the reimbursement request")
    private List<Status> allowedNextStatuses;
    @Schema(description = "To show the approved amount field")
    private boolean showApprovedAmountField;
    @Schema(description = "To show the reason field")
    private boolean showReasonField;
}
