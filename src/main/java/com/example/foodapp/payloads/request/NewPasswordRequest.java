package com.example.foodapp.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewPasswordRequest {
    private String newPassword;
    private String confirmNewPassword;
}
