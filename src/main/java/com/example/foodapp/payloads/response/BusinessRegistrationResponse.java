package com.example.foodapp.payloads.response;

import com.example.foodapp.utils.geoLocation.GeoLocation;
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
    private GeoLocation coordinates;
}
