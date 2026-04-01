package com.reimbursement.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectionHistory {
    private String previousStatus;
    private String reason;
    private LocalDateTime rejectedAt;
    private String rejectedBy;
}
