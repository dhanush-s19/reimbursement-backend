package com.reimbursement.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class AccountantDashboardDTO {
    private Double totalPendingPayout;
    private long pendingApprovalCount;
    private Double totalDisbursedMonth;
    private Map<String, Double> spendByType;
    private Map<String, Long> statusDistribution;
}