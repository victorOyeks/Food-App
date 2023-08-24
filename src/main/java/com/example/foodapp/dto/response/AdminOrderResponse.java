package com.example.foodapp.dto.response;

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
    private DeliveryStatus deliveryStatus;
    private LocalDateTime createdAt;
}
