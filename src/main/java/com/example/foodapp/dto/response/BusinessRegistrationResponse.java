package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BusinessRegistrationResponse {
    private String id;
    private String email;
    private String businessName;
    private String domainName;
    private String businessAddress;
    private String mapUri;
    private String imageUrl;
}
