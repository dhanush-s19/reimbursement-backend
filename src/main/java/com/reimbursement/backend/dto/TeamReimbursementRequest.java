package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.ReimbursementType;
import lombok.Data;

import java.util.List;

@Data
public class TeamReimbursementRequest {

    private List<String> teamMemberIds;
    private String title;
    private Double amount;
    private String description;
    private String submittedById;
    private String name;
    private ReimbursementType type;
    private boolean noInvoice = true;
    private String invoiceNote;
}