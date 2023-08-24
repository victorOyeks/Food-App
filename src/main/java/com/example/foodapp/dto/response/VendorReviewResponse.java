package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VendorReviewResponse {
    private String id;
    private String businessName;
    private String imageUrl;
    private Double averageRating;
}
