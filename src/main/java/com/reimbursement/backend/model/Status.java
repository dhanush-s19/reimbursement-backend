package com.reimbursement.backend.model;

public enum Status {

    SUBMITTED,
    FORWARDED_TO_HR,
    HR_APPROVED,
    HR_REJECTED,
    BACK_TO_ACCOUNTANT,
    ACCOUNTANT_FINAL_APPROVED,
    ACCOUNTANT_REJECTED,
    PAID
}