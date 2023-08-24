package com.example.foodapp.payloads.response;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.OrderType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDetailsResponse {
    private String orderId;
    private LocalDateTime orderDate;
    private String customerName;
    private String profilePic;
    private OrderType orderType;
    private BigDecimal amount;
    private DeliveryStatus deliveryStatus;
}
