package com.example.foodapp.payloads.request;

import com.example.foodapp.constant.CompanySize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompanyRegistrationRequest {
    private String companyEmail;
    private String phoneNumber;
    private String companyName;
    private String password;
    private String confirmPassword;
    private String companyAddress;
    private CompanySize companySize;
}
