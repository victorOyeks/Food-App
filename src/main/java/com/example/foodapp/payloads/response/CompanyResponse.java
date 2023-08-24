package com.example.foodapp.payloads.response;

import com.example.foodapp.constant.CompanySize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompanyResponse {
    private String id;
    private String companyEmail;
    private String phoneNumber;
    private String companyName;
    private String companyAddress;
    private CompanySize companySize;
    private String imageUrl;
}
