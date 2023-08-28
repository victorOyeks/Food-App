package com.example.foodapp.payloads.response;

import com.example.foodapp.entities.ItemMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SupplementResponse {
    private String supplementId;
    private String supplementName;
    private BigDecimal supplementPrice;
    private String itemMenuName;
}
