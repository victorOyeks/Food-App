package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderViewResponse {
    private List<OrderResponse> orderResponses;
    private OrderSummary orderSummary;
}

