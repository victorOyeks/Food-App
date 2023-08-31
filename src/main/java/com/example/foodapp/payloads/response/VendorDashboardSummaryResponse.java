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
public class VendorDashboardSummaryResponse {
    private Long totalOrder;
    private Long totalMenus;
    private BigDecimal totalSales;
}
