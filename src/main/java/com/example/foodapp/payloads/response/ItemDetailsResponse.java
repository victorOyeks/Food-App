package com.example.foodapp.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ItemDetailsResponse {
    private String itemId;
    private String itemName;
    private String itemCategory;
    private BigDecimal itemPrice;
    private boolean availableStatus;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long orderCount;

}
