package com.reimbursement.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReimbursementPageResponse {
    private List<ReimbursementResponse> content;
    private int totalPages;
    private long totalElements;
    private int number;
}
