package com.reimbursement.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for the Accountant Dashboard summary statistics")
public class AccountantDashboardDTO {

    @Schema(description = "The total monetary value of all reimbursement requests currently awaiting payout",
            example = "15420.50")
    private Double totalPendingPayout;

    @Schema(description = "The total count of reimbursement requests pending accountant approval",
            example = "42")
    private long pendingApprovalCount;

    @Schema(description = "The percentage of claims approved versus rejected",
            example = "94.5")
    private Double approvalRate;

    @Schema(description = "Total amount of funds disbursed in the current calendar month",
            example = "45000.00")
    private Double totalDisbursedMonth;

    @Schema(description = "A mapping of spending categories (e.g., Travel, Meals) to their respective total costs",
            example = "{ 'Travel': 5000.0, 'Office Supplies': 1200.50 }")
    private Map<String, Double> spendByType;

    @Schema(description = "A distribution count of requests indexed by their current status",
            example = "{ 'PENDING': 10, 'APPROVED': 50, 'REJECTED': 5 }")
    private Map<String, Long> statusDistribution;
}