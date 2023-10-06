package com.example.foodapp.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDashBoardResponse {
    private String id;
    private String vendorBusinessName;
    private Double vendorRating;
    private Long totalRatings;
    private String vendorImageUrl;
}
