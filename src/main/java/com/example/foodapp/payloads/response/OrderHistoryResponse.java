package com.example.foodapp.payloads.response;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.OrderType;
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
public class OrderHistoryResponse {
    private String orderId;
    private LocalDateTime orderDate;
    private String customerName;
    private String profilePic;
    private OrderType orderType;
    private BigDecimal amount;
    private DeliveryStatus deliveryStatus;
    private String contactNumber;
    private String email;
}
