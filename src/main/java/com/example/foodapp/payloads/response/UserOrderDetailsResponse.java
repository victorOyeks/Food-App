package com.example.foodapp.payloads.response;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.SubmitStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserOrderDetailsResponse {

    private String orderId;
    private List<FoodDataResponse> itemMenu;
    private BigDecimal totalAmount;
    private SubmitStatus submitStatus;
    private DeliveryStatus deliveryStatus;

}
