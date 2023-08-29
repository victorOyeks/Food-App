package com.example.foodapp.payloads.response;

import com.example.foodapp.entities.Supplement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
