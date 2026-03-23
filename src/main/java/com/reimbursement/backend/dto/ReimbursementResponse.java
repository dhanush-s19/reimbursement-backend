package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Reimbursement;
import com.reimbursement.backend.model.Status;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ReimbursementResponse {
    private Reimbursement reimbursement;
    private List<Status> allowedNextStatuses;
    private boolean showApprovedAmountField;
    private boolean showReasonField;
}
