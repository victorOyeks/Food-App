package com.example.foodapp.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VendorRegistrationRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private String confirmPassword;
    private String businessName;
    private String domainName;
    private String businessAddress;
}