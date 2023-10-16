package com.example.foodapp.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartRequest {
    private List<CartItemWithSupplements> cartItemWithSupplements;
}
