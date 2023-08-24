package com.example.foodapp.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResetPasswordRequest {
    private String resetToken;
    private String newPassword;
    private String confirmPassword;
}