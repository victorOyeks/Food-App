package com.example.foodapp.controller;

import com.example.foodapp.dto.response.OrderResponse;
import com.example.foodapp.dto.response.OrderSummary;
import com.example.foodapp.dto.response.OrderViewResponse;
import com.example.foodapp.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors/")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping("orders")
    public ResponseEntity<OrderViewResponse> viewAllOrdersToVendor() {
        List<OrderResponse> orders = vendorService.viewAllOrdersToVendor();
        OrderSummary orderSummary = vendorService.calculateOrderSummary(orders);
        OrderViewResponse orderViewResponse = new OrderViewResponse(orders, orderSummary);
        return ResponseEntity.ok(orderViewResponse);
    }
}
