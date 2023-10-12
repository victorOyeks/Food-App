package com.example.foodapp.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompanyDetailsResponse {
    private Integer numberOfStaff;
    private Integer numberOfVendors;
    private BigDecimal totalAmountSpent;

}
