package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateStatusRequest {
    private Status status;
    private String reason;
    private String processedById;
    private BigDecimal approvedAmount;
}
