package com.example.foodapp.service;

import com.example.foodapp.dto.request.VendorRegistrationRequest;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.OrderResponse;
import com.example.foodapp.dto.response.OrderSummary;

import java.io.IOException;
import java.util.List;

public interface VendorService {
    BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request) throws IOException;
    List<OrderResponse> viewAllOrdersToVendor();
    OrderSummary calculateOrderSummary(List<OrderResponse> orders);
}
