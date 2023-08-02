package com.example.foodapp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private String customerId;
    private String customerName;
    private LocalDateTime dateJoined;
    private String customerType;
    private BigDecimal totalAmountSpent;
    private LocalDateTime lastOrderTime;
}
