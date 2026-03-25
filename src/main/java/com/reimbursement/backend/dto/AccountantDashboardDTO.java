package com.reimbursement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
public class AccountantDashboardDTO {
    private Double totalPendingPayout;
    private long pendingApprovalCount;
    private Double approvalRate;
    private Double totalDisbursedMonth;
    private Map<String, Double> spendByType;
    private Map<String, Long> statusDistribution;
}