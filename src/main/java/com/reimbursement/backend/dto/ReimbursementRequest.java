package com.reimbursement.backend.dto;

import com.reimbursement.backend.model.ReimbursementType;
import com.reimbursement.backend.model.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReimbursementRequest {
    private String title;
    private String employeeId;
    private String name;
    private Double amount;
    private String description;
    private boolean noInvoice;
    private String invoiceNote;
    private List<MultipartFile> files;
    private String submittedBy;
    private ReimbursementType type;
}