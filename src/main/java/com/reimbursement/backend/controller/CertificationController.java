package com.reimbursement.backend.controller;

import com.reimbursement.backend.model.Certification;
import com.reimbursement.backend.service.CertificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationService certificationService;
    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @PostMapping("/add")
    public ResponseEntity<Certification> addCertification(@RequestBody Certification certification) {
        return ResponseEntity.ok(certificationService.addCertification(certification));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Certification> updateCertification(@PathVariable String id,
                                                             @RequestBody Certification certification) {
        Certification updated = certificationService.updateCertification(id, certification);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCertification(@PathVariable String id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<Certification>> getAllCertifications() {
        return ResponseEntity.ok(certificationService.getAllCertifications());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Certification> getCertificationById(@PathVariable String id) {
        Certification certification = certificationService.getCertificationById(id);
        if (certification != null) {
            return ResponseEntity.ok(certification);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Certification>> getAllCertificationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(certificationService.getAllCertifications(pageable));
    }

    @GetMapping("/status/page")
    public ResponseEntity<Page<Certification>> getCertificationsByStatusPaginated(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(certificationService.getCertificationsByStatus(status, pageable));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<Certification>> getCertificationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(certificationService.getCertificationsByStatus(status));
    }
}