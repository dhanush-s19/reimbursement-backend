package com.reimbursement.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "certifications")
public class Certification {

    @Id
    private String id;
    private String category;
    private String certification;
    private List<String> recommendedRoles;
    private String status;
}
