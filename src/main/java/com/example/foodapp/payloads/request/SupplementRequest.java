package com.example.foodapp.payloads.request;

import com.example.foodapp.constant.SupplementCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SupplementRequest {
    private String supplementName;
    private BigDecimal supplementPrice;
    private SupplementCategory supplementCategory;
}
