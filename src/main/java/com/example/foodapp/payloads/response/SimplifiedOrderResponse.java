package com.example.foodapp.payloads.response;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.SubmitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SimplifiedOrderResponse {
    private String orderId;
    private BigDecimal totalAmount;
    private DeliveryStatus deliveryStatus;
    private SubmitStatus submitStatus;
}