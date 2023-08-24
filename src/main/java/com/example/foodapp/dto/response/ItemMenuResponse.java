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
public class ItemMenuResponse {
    private String itemName;
    private BigDecimal itemPrice;
    private String imageUrl;
    private String categoryName;
}
