package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FoodDataResponse {
    private String itemId;
    private String itemName;
    private BigDecimal price;
    private String imageUri;
    private String vendorName;
}
