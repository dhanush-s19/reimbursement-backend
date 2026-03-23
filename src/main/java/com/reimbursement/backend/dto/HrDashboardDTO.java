package com.reimbursement.backend.dto;

import lombok.*;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HrDashboardDTO {
    private long pendingHrVerificationCount;
    private long totalEmployees;
    private Map<String, Long> employeesByDepartment;
    private Map<String, Long> pendingByType;
}