package com.example.foodapp.service;

import com.example.foodapp.dto.request.VendorRegistrationRequest;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.OrderResponse;
import com.example.foodapp.dto.response.OrderSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VendorService {
    BusinessRegistrationResponse vendorSignup(String email, String firstName, String lastName,
                                              String phone, String password, String confirmPassword,
                                              String businessName, String domainName, String businessAddress,
                                              MultipartFile file) throws IOException;
    List<OrderResponse> viewAllOrdersToVendor();
    OrderSummary calculateOrderSummary(List<OrderResponse> orders);
}
