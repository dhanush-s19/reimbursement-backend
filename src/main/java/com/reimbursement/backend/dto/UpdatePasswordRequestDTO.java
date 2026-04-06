package com.reimbursement.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for updating user password")
public class UpdatePasswordRequestDTO {
    @Schema(description = "Current password of the user")
    private String oldPassword;
    @Schema(description = "New password of the user")
    private String newPassword;
}
