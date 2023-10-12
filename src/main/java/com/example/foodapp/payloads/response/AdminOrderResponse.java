package com.example.foodapp.payloads.response;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AdminOrderResponse {
    private String orderId;
    private List<FoodDataResponse> items;
    private BigDecimal totalAmount;
    private OrderType orderType;
    private String customerName;
    private String profilePic;
    private String phone;
    private String email;
    private String companyName;
    private Boolean customerStatus;
    private DeliveryStatus deliveryStatus;
    private LocalDateTime createdAt;
}
