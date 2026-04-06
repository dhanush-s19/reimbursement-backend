package com.reimbursement.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for the HR Dashboard summary statistics")
public class HrDashboardDTO {
    @Schema(description = "The total count of reimbursement requests pending HR verification")
    private long pendingHrVerificationCount;
    @Schema(description = "The total count of employees")
    private long totalEmployees;
    @Schema(description = "A mapping of department names to their respective employee counts")
    private Map<String, Long> employeesByDepartment;
    @Schema(description = "A mapping of reimbursement types to their respective pending counts")
    private Map<String, Long> pendingByType;
}