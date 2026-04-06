package com.reimbursement.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Response object containing a list of reimbursement requests and pagination metadata.")
public class ReimbursementPageResponse {
    @Schema(description = "List of reimbursement requests")
    private List<ReimbursementResponse> content;
    @Schema(description = "Total number of pages")
    private int totalPages;
    @Schema(description = "Total number of reimbursement requests")
    private long totalElements;
    @Schema(description = "Current page number")
    private int number;
}
